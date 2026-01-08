package com.example.cms_android.ListActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cms_android.AddActivity.AddEducationActivity;
import com.example.cms_android.R;
import com.example.cms_android.adapter.EducationAdapter;
import com.example.cms_android.model.Education;
import com.example.cms_android.response.base.EducationResponse;
import com.example.cms_android.network.ApiService;
import com.example.cms_android.websocket.WebSocketManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EducationListActivity extends AppCompatActivity {
    private static final int REQUEST_ADD_EDUCATION = 1003;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ExtendedFloatingActionButton fabAdd;

    private EducationAdapter adapter;
    private List<Education> educationList;

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
        setContentView(R.layout.activity_education_list);

        initViews();
        initServices();
        setupRecyclerView();
        setupListeners();
        setupWebSocket();
        loadEducations();
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
        educationList = new ArrayList<>();
        webSocketManager = WebSocketManager.getInstance();
    }

    private void setupRecyclerView() {
        adapter = new EducationAdapter(this, educationList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // 设置教育项点击事件
        adapter.setOnItemClickListener(education -> {
            // 跳转到教育详情页
            Intent intent = new Intent(EducationListActivity.this,
                    com.example.cms_android.activity.EducationActivity.class);
            intent.putExtra("education_id", education.getId());
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
                        loadMoreEducations();
                    }
                }
            }
        });
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(this::loadEducations);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(EducationListActivity.this, AddEducationActivity.class);
            startActivityForResult(intent, REQUEST_ADD_EDUCATION);
        });
    }
    
    /**
     * 设置WebSocket连接
     */
    private void setupWebSocket() {
        webSocketManager.setListener(new WebSocketManager.WebSocketListener() {
            @Override
            public void onConnected() {
                runOnUiThread(() -> {
                    Toast.makeText(EducationListActivity.this, "已连接到服务器", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onDisconnected() {
                runOnUiThread(() -> {
                    Toast.makeText(EducationListActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onMessage(String message) {
                // 处理教育数据变更消息
                if (message.contains("education") || message.contains("EDUCATION")) {
                    runOnUiThread(() -> {
                        Toast.makeText(EducationListActivity.this, "教育数据已更新，正在刷新列表", Toast.LENGTH_SHORT).show();
                        loadEducations(); // 重新加载数据
                    });
                }
            }

            @Override
            public void onError(Exception error) {
                runOnUiThread(() -> {
                    Toast.makeText(EducationListActivity.this, "WebSocket连接错误: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * 加载教育信息列表
     */
    private void loadEducations() {
        // 重置分页状态
        currentPage = 1;
        hasMoreData = true;
        swipeRefreshLayout.setRefreshing(true);

        executorService.execute(() -> {
            try {
                EducationResponse response = apiService.getEducations(currentPage, pageSize);

                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);

                    if (response.isSuccess() && response.getData() != null && response.getData().getData() != null) {
                        // 清空列表，添加新数据
                        educationList.clear();
                        educationList.addAll(response.getData().getData());
                        adapter.notifyDataSetChanged();

                        // 检查是否还有更多数据
                        hasMoreData = educationList.size() < response.getData().getTotal();
                    } else {
                        String message = response.getMessage() != null ? response.getMessage() : "没有教育信息";
                        Toast.makeText(EducationListActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(EducationListActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * 加载更多教育信息
     */
    private void loadMoreEducations() {
        if (isLoading || !hasMoreData) {
            return;
        }

        isLoading = true;
        currentPage++;

        executorService.execute(() -> {
            try {
                EducationResponse response = apiService.getEducations(currentPage, pageSize);

                runOnUiThread(() -> {
                    isLoading = false;

                    if (response.isSuccess() && response.getData() != null && response.getData().getData() != null) {
                        List<Education> newEducations = response.getData().getData();
                        if (newEducations != null && !newEducations.isEmpty()) {
                            // 添加新数据到列表
                            int startPosition = educationList.size();
                            educationList.addAll(newEducations);
                            adapter.notifyItemRangeInserted(startPosition, newEducations.size());

                            // 检查是否还有更多数据
                            hasMoreData = educationList.size() < response.getData().getTotal();
                        } else {
                            // 没有更多数据了
                            hasMoreData = false;
                        }
                    } else {
                        // 加载失败，恢复页码
                        currentPage--;
                        String message = response.getMessage() != null ? response.getMessage() : "加载更多失败";
                        Toast.makeText(EducationListActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    isLoading = false;
                    // 加载失败，恢复页码
                    currentPage--;
                    Toast.makeText(EducationListActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_EDUCATION && resultCode == RESULT_OK) {
            loadEducations();
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
        // 移除WebSocket监听器
        if (webSocketManager != null) {
            webSocketManager.setListener(null);
        }
    }
}