package com.example.cms_android.ListActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cms_android.AddActivity.AddResidentActivity;
import com.example.cms_android.R;
import com.example.cms_android.adapter.ResidentAdapter;
import com.example.cms_android.model.Resident;
import com.example.cms_android.response.base.ResidentResponse;
import com.example.cms_android.network.ApiService;
import com.example.cms_android.utils.TokenManager;
import com.example.cms_android.websocket.DataChangeHandler;
import com.example.cms_android.websocket.WebSocketManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResidentListActivity extends AppCompatActivity {
    private static final int REQUEST_ADD_RESIDENT = 1001;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ExtendedFloatingActionButton fabAdd;

    private ResidentAdapter adapter;
    private List<Resident> residentList;

    private ApiService apiService;
    private ExecutorService executorService;

    // 分页相关变量
    private int currentPage = 1;
    private int pageSize = 20;
    private boolean hasMoreData = true;
    private boolean isLoading = false;
    
    // WebSocket相关
    private WebSocketManager webSocketManager;
    private DataChangeHandler dataChangeHandler;
    private DataChangeHandler.DataChangeListener dataChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_list);

        initViews();
        initServices();
        setupRecyclerView();
        setupListeners();
        initWebSocket();
        loadResidents();
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
        residentList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new ResidentAdapter(this, residentList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // 设置居民项点击事件
        adapter.setOnItemClickListener(resident -> {
            // 跳转到居民详情页
            Intent intent = new Intent(ResidentListActivity.this,
                    com.example.cms_android.activity.ResidentActivity.class);
            intent.putExtra("resident_id", resident.getId());
            startActivity(intent);
        });

        // 添加滚动监听器，实现加载更多功能
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 只有向下滚动时才触发加载更多
                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // 当滚动到倒数第10个item时，加载更多数据
                    if (!isLoading && hasMoreData
                            && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 10) {
                        loadMoreResidents();
                    }
                }
            }
        });
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(this::loadResidents);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ResidentListActivity.this, AddResidentActivity.class);
            startActivityForResult(intent, REQUEST_ADD_RESIDENT);
        });
    }

    private void loadResidents() {
        // 重置分页状态
        currentPage = 1;
        hasMoreData = true;
        swipeRefreshLayout.setRefreshing(true);

        executorService.execute(() -> {
            try {
                ResidentResponse response = apiService.getResidents(currentPage, pageSize);

                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);

                    if (response.isSuccess() && response.getData() != null && response.getData().getData() != null) {
                        // 清空列表，添加新数据
                        residentList.clear();
                        residentList.addAll(response.getData().getData());
                        adapter.notifyDataSetChanged();

                        // 检查是否还有更多数据
                        hasMoreData = residentList.size() < response.getData().getTotal();
                    } else {
                        String message = response.getMessage() != null ? response.getMessage() : "加载失败";
                        Toast.makeText(ResidentListActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(ResidentListActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * 加载更多居民数据
     */
    private void loadMoreResidents() {
        if (isLoading || !hasMoreData) {
            return;
        }

        isLoading = true;
        currentPage++;

        executorService.execute(() -> {
            try {
                ResidentResponse response = apiService.getResidents(currentPage, pageSize);

                runOnUiThread(() -> {
                    isLoading = false;

                    if (response.isSuccess() && response.getData() != null && response.getData().getData() != null) {
                        List<Resident> newResidents = response.getData().getData();
                        if (newResidents != null && !newResidents.isEmpty()) {
                            // 添加新数据到列表
                            int startPosition = residentList.size();
                            residentList.addAll(newResidents);
                            adapter.notifyItemRangeInserted(startPosition, newResidents.size());

                            // 检查是否还有更多数据
                            hasMoreData = residentList.size() < response.getData().getTotal();
                        } else {
                            // 没有更多数据了
                            hasMoreData = false;
                        }
                    } else {
                        // 加载失败，恢复页码
                        currentPage--;
                        String message = response.getMessage() != null ? response.getMessage() : "加载更多失败";
                        Toast.makeText(ResidentListActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    isLoading = false;
                    // 加载失败，恢复页码
                    currentPage--;
                    Toast.makeText(ResidentListActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_RESIDENT && resultCode == RESULT_OK) {
            // 添加居民成功，刷新列表
            loadResidents();
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

    /**
     * 初始化WebSocket连接
     */
    private void initWebSocket() {
        webSocketManager = WebSocketManager.getInstance();
        dataChangeHandler = DataChangeHandler.getInstance();
        
        // 设置WebSocket监听器
        webSocketManager.setListener(new WebSocketManager.WebSocketListener() {
            @Override
            public void onConnected() {
                runOnUiThread(() -> {
                    Toast.makeText(ResidentListActivity.this, "WebSocket已连接", Toast.LENGTH_SHORT).show();
                });
            }
            
            @Override
            public void onDisconnected() {
                runOnUiThread(() -> {
                    Toast.makeText(ResidentListActivity.this, "WebSocket已断开", Toast.LENGTH_SHORT).show();
                });
            }
            
            @Override
            public void onMessage(String message) {
                // 处理接收到的消息
                dataChangeHandler.handleMessage(message);
            }
            
            @Override
            public void onError(Exception error) {
                runOnUiThread(() -> {
                    Toast.makeText(ResidentListActivity.this, "WebSocket错误: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
        
        // 注册数据变更监听器
        dataChangeListener = (module, operation) -> {
            if ("resident".equals(module)) {
                runOnUiThread(() -> {
                    // 居民数据变更，刷新列表
                    loadResidents();
                    
                    String operationName = "";
                    switch (operation) {
                        case "INSERT":
                            operationName = "新增";
                            break;
                        case "UPDATE":
                            operationName = "更新";
                            break;
                        case "DELETE":
                            operationName = "删除";
                            break;
                    }
                    
                    Toast.makeText(ResidentListActivity.this, "居民数据已" + operationName + "，列表已刷新", Toast.LENGTH_SHORT).show();
                });
            }
        };
        dataChangeHandler.registerListener(dataChangeListener);
        
        // 连接WebSocket
        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();
        if (token != null && !token.isEmpty()) {
            webSocketManager.connect(token);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
        
        // 注销WebSocket监听器
        if (dataChangeHandler != null && dataChangeListener != null) {
            dataChangeHandler.unregisterListener(dataChangeListener);
        }
    }
}