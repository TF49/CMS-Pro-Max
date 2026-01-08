package com.example.cms_android.ListActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cms_android.AddActivity.AddEmploymentActivity;
import com.example.cms_android.R;
import com.example.cms_android.activity.EmploymentActivity;
import com.example.cms_android.adapter.EmploymentAdapter;
import com.example.cms_android.model.Employment;
import com.example.cms_android.network.ApiService;
import com.example.cms_android.response.base.EmploymentResponse;
import com.example.cms_android.websocket.DataChangeMessage;
import com.example.cms_android.websocket.WebSocketManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmploymentListActivity extends AppCompatActivity {
    private static final int REQUEST_ADD_EMPLOYMENT = 1004;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ExtendedFloatingActionButton fabAdd;

    private EmploymentAdapter adapter;
    private List<Employment> employmentList;

    private ApiService apiService;
    private ExecutorService executorService;
    private WebSocketManager webSocketManager;

    // 分页相关变量
    private int currentPage = 1;
    private int pageSize = 20;
    private boolean hasMoreData = true;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employment_list);

        initViews();
        initServices();
        setupRecyclerView();
        setupListeners();
        loadEmployments();
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
        employmentList = new ArrayList<>();
        webSocketManager = WebSocketManager.getInstance();
    }

    private void setupRecyclerView() {
        adapter = new EmploymentAdapter(this, employmentList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // 设置就业项点击事件
        adapter.setOnItemClickListener(employment -> {
            // 跳转到就业详情页
            Intent intent = new Intent(EmploymentListActivity.this, EmploymentActivity.class);
            intent.putExtra(EmploymentActivity.EXTRA_EMPLOYMENT_ID, employment.getId());
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
                        loadMoreEmployments();
                    }
                }
            }
        });
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(this::loadEmployments);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(EmploymentListActivity.this, AddEmploymentActivity.class);
            startActivityForResult(intent, REQUEST_ADD_EMPLOYMENT);
        });
        
        // 注册WebSocket监听器
        webSocketManager.setDataChangeListener(new WebSocketManager.DataChangeListener() {
            @Override
            public void onDataChange(DataChangeMessage message) {
                // 检查是否是就业相关的消息
                if (message != null && message.getEntityType() != null && 
                    message.getEntityType().equals("EMPLOYMENT")) {
                    // 在主线程中刷新数据
                    runOnUiThread(EmploymentListActivity.this::loadEmployments);
                    
                    // 显示提示信息
                    String action = message.getAction();
                    if ("CREATE".equals(action)) {
                        Toast.makeText(EmploymentListActivity.this, "有新的就业记录", Toast.LENGTH_SHORT).show();
                    } else if ("UPDATE".equals(action)) {
                        Toast.makeText(EmploymentListActivity.this, "就业信息已更新", Toast.LENGTH_SHORT).show();
                    } else if ("DELETE".equals(action)) {
                        Toast.makeText(EmploymentListActivity.this, "就业记录已删除", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void loadEmployments() {
        currentPage = 1;
        hasMoreData = true;
        swipeRefreshLayout.setRefreshing(true);

        executorService.execute(() -> {
            try {
                // 使用与居民模块一致的方法名getEmployments
                EmploymentResponse response = apiService.getEmployments(currentPage, pageSize);

                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);

                    if (response != null && response.isSuccess() && response.getData() != null
                            && response.getData().getData() != null) {
                        // 清空列表，添加新数据
                        employmentList.clear();
                        employmentList.addAll(response.getData().getData());
                        adapter.notifyDataSetChanged();

                        // 检查是否还有更多数据
                        hasMoreData = employmentList.size() < response.getData().getTotal();
                    } else {
                        String message = response != null && response.getMessage() != null ? response.getMessage()
                                : "加载失败";
                        Toast.makeText(EmploymentListActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(EmploymentListActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void loadMoreEmployments() {
        if (isLoading || !hasMoreData) {
            return;
        }

        isLoading = true;
        currentPage++;

        executorService.execute(() -> {
            try {
                // 使用与居民模块一致的方法名getEmployments
                EmploymentResponse response = apiService.getEmployments(currentPage, pageSize);

                runOnUiThread(() -> {
                    isLoading = false;

                    if (response != null && response.isSuccess() && response.getData() != null
                            && response.getData().getData() != null) {
                        List<Employment> newEmployments = response.getData().getData();
                        if (newEmployments != null && !newEmployments.isEmpty()) {
                            // 添加新数据到列表
                            int startPosition = employmentList.size();
                            employmentList.addAll(newEmployments);
                            adapter.notifyItemRangeInserted(startPosition, newEmployments.size());

                            // 检查是否还有更多数据
                            hasMoreData = employmentList.size() < response.getData().getTotal();
                        } else {
                            // 没有更多数据了
                            hasMoreData = false;
                        }
                    } else {
                        // 加载失败，恢复页码
                        currentPage--;
                        String message = response != null && response.getMessage() != null ? response.getMessage()
                                : "加载更多失败";
                        Toast.makeText(EmploymentListActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    isLoading = false;
                    // 加载失败，恢复页码
                    currentPage--;
                    Toast.makeText(EmploymentListActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_EMPLOYMENT && resultCode == RESULT_OK) {
            loadEmployments();
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
        // 注销WebSocket监听器
        if (webSocketManager != null) {
            webSocketManager.setListener(null);
        }
    }
}