package com.example.cms_android.ListActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cms_android.AddActivity.AddMedicalActivity;
import com.example.cms_android.R;
import com.example.cms_android.adapter.MedicalAdapter;
import com.example.cms_android.model.Medical;
import com.example.cms_android.model.Resident;
import com.example.cms_android.network.ApiService;
import com.example.cms_android.response.base.MedicalResponse;
import com.example.cms_android.websocket.WebSocketManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MedicalListActivity extends AppCompatActivity {
    private static final int REQUEST_ADD_MEDICAL = 1005;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ExtendedFloatingActionButton fabAdd;

    private MedicalAdapter adapter;
    private List<Medical> medicalList;

    private ApiService apiService;
    private ExecutorService executorService;
    private WebSocketManager webSocketManager;

    private int currentPage = 1;
    private int pageSize = 20;
    private boolean hasMoreData = true;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_list);

        initViews();
        initServices();
        setupRecyclerView();
        setupListeners();
        loadMedicals();
        
        // 注册WebSocket消息监听器
        registerWebSocketListener();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        fabAdd = findViewById(R.id.fab_add);
        // 初始化Toolbar为ActionBar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 启用返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initServices() {
        apiService = new ApiService();
        executorService = Executors.newSingleThreadExecutor();
        webSocketManager = WebSocketManager.getInstance();
        medicalList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new MedicalAdapter(this, medicalList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // 设置医疗项点击事件
        adapter.setOnItemClickListener(medical -> {
            // 跳转到医疗详情页
            Intent intent = new Intent(MedicalListActivity.this,
                    com.example.cms_android.activity.MedicalActivity.class);
            intent.putExtra("medical_id", medical.getId());
            startActivity(intent);
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && hasMoreData
                            && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 10) {
                        loadMoreMedicals();
                    }
                }
            }
        });
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(this::loadMedicals);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MedicalListActivity.this, AddMedicalActivity.class);
            startActivityForResult(intent, REQUEST_ADD_MEDICAL);
        });
    }
    
    /**
     * 注册WebSocket消息监听器
     */
    private void registerWebSocketListener() {
        if (webSocketManager != null) {
            webSocketManager.setListener(new WebSocketManager.WebSocketListener() {
                @Override
                public void onConnected() {
                    // 连接建立时的处理
                }

                @Override
                public void onDisconnected() {
                    // 连接断开时的处理
                }

                @Override
                public void onMessage(String message) {
                    // 处理接收到的消息
                    if (message != null && message.contains("medical")) {
                        // 在主线程中处理消息
                        runOnUiThread(() -> {
                            // 重新加载医疗列表以反映最新的数据变更
                            loadMedicals();
                        });
                    }
                }

                @Override
                public void onError(Exception error) {
                    // 错误处理
                }
            });
        }
    }
    
    /**
     * 注销WebSocket消息监听器
     */
    private void unregisterWebSocketListener() {
        if (webSocketManager != null) {
            webSocketManager.setListener(null);
        }
    }

    private void loadMedicals() {
        currentPage = 1;
        hasMoreData = true;
        swipeRefreshLayout.setRefreshing(true);

        executorService.execute(() -> {
            try {
                MedicalResponse response = apiService.getMedicals(currentPage, pageSize);

                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);

                    if (response.isSuccess() && response.getData() != null && response.getData().getData() != null) {
                        medicalList.clear();
                        List<Medical> newMedicals = response.getData().getData();
                        // 为每个医疗对象补充居民信息
                        for (Medical medical : newMedicals) {
                            try {
                                // 获取居民信息
                                Resident resident = apiService.getResidentById(medical.getResidentId());
                                if (resident != null) {
                                    // 设置居民姓名和身份证号
                                    medical.setResidentName(resident.getName());
                                    medical.setIdCard(resident.getIdCard());
                                }
                                // 设置诊断结果（使用慢性病字段作为诊断结果）
                                medical.setDiagnosis(medical.getChronicDiseases());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        medicalList.addAll(newMedicals);
                        adapter.notifyDataSetChanged();
                        hasMoreData = medicalList.size() < response.getData().getTotal();
                    } else {
                        String message = response.getMessage() != null ? response.getMessage() : "没有医疗信息";
                        Toast.makeText(MedicalListActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(MedicalListActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void loadMoreMedicals() {
        if (isLoading || !hasMoreData) {
            return;
        }

        isLoading = true;
        currentPage++;

        executorService.execute(() -> {
            try {
                MedicalResponse response = apiService.getMedicals(currentPage, pageSize);

                runOnUiThread(() -> {
                    isLoading = false;

                    if (response.isSuccess() && response.getData() != null && response.getData().getData() != null) {
                        List<Medical> newMedicals = response.getData().getData();
                        if (newMedicals != null && !newMedicals.isEmpty()) {
                            // 为每个医疗对象补充居民信息
                            for (Medical medical : newMedicals) {
                                try {
                                    // 获取居民信息
                                    Resident resident = apiService.getResidentById(medical.getResidentId());
                                    if (resident != null) {
                                        // 设置居民姓名和身份证号
                                        medical.setResidentName(resident.getName());
                                        medical.setIdCard(resident.getIdCard());
                                    }
                                    // 设置诊断结果（使用慢性病字段作为诊断结果）
                                    medical.setDiagnosis(medical.getChronicDiseases());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            int startPosition = medicalList.size();
                            medicalList.addAll(newMedicals);
                            adapter.notifyItemRangeInserted(startPosition, newMedicals.size());
                            hasMoreData = medicalList.size() < response.getData().getTotal();
                        } else {
                            hasMoreData = false;
                        }
                    } else {
                        currentPage--;
                        String message = response.getMessage() != null ? response.getMessage() : "加载更多失败";
                        Toast.makeText(MedicalListActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    isLoading = false;
                    currentPage--;
                    Toast.makeText(MedicalListActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_MEDICAL && resultCode == RESULT_OK) {
            loadMedicals();
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
        
        // 注销WebSocket消息监听器
        unregisterWebSocketListener();
    }
}