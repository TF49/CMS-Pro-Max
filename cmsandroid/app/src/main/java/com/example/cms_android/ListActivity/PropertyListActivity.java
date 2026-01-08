package com.example.cms_android.ListActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cms_android.AddActivity.AddPropertyActivity;
import com.example.cms_android.R;
import com.example.cms_android.adapter.PropertyAdapter;
import com.example.cms_android.model.Property;
import com.example.cms_android.model.Resident;
import com.example.cms_android.network.ApiService;
import com.example.cms_android.response.base.PropertyResponse;
import com.example.cms_android.websocket.WebSocketManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PropertyListActivity extends AppCompatActivity {
    private static final int REQUEST_ADD_PROPERTY = 1008;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ExtendedFloatingActionButton fabAdd;

    private PropertyAdapter adapter;
    private List<Property> propertyList;

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
        setContentView(R.layout.activity_property_list);

        initViews();
        initServices();
        setupRecyclerView();
        setupListeners();
        setupWebSocket();
        loadProperties();
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
        propertyList = new ArrayList<>();
        webSocketManager = WebSocketManager.getInstance();
    }

    private void setupRecyclerView() {
        adapter = new PropertyAdapter(this, propertyList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // 设置房产项点击事件
        adapter.setOnItemClickListener(property -> {
            // 跳转到房产详情页
            Intent intent = new Intent(PropertyListActivity.this,
                    com.example.cms_android.activity.PropertyActivity.class);
            intent.putExtra("property_id", property.getId());
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
                        loadMoreProperties();
                    }
                }
            }
        });
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(this::loadProperties);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(PropertyListActivity.this, AddPropertyActivity.class);
            startActivityForResult(intent, REQUEST_ADD_PROPERTY);
        });
    }

    private void loadProperties() {
        // 重置分页状态
        currentPage = 1;
        hasMoreData = true;
        swipeRefreshLayout.setRefreshing(true);

        executorService.execute(() -> {
            try {
                PropertyResponse response = apiService.getProperties(currentPage, pageSize);

                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);

                    if (response.isSuccess() && response.getData() != null && response.getData().getData() != null) {
                        // 清空列表，添加新数据
                        propertyList.clear();
                        List<Property> newProperties = response.getData().getData();
                        propertyList.addAll(newProperties);

                        // 为每个房产获取居民姓名
                        for (Property property : propertyList) {
                            if (property.getResidentId() != null) {
                                executorService.execute(() -> {
                                    try {
                                        Resident resident = apiService.getResidentById(property.getResidentId());
                                        if (resident != null) {
                                            property.setResidentName(resident.getName());
                                            runOnUiThread(() -> {
                                                adapter.notifyDataSetChanged();
                                            });
                                        }
                                    } catch (Exception e) {
                                        // 忽略异常，继续处理其他房产
                                    }
                                });
                            }
                        }

                        // 检查是否还有更多数据
                        hasMoreData = propertyList.size() < response.getData().getTotal();
                    } else {
                        String message = response.getMessage() != null ? response.getMessage() : "加载失败";
                        Toast.makeText(PropertyListActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(PropertyListActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * 加载更多房产数据
     */
    private void loadMoreProperties() {
        if (isLoading || !hasMoreData) {
            return;
        }

        isLoading = true;
        currentPage++;

        executorService.execute(() -> {
            try {
                PropertyResponse response = apiService.getProperties(currentPage, pageSize);

                runOnUiThread(() -> {
                    isLoading = false;

                    if (response.isSuccess() && response.getData() != null && response.getData().getData() != null) {
                        List<Property> newProperties = response.getData().getData();
                        if (newProperties != null && !newProperties.isEmpty()) {
                            // 添加新数据到列表
                            int startPosition = propertyList.size();
                            propertyList.addAll(newProperties);
                            adapter.notifyItemRangeInserted(startPosition, newProperties.size());

                            // 为每个新加载的房产获取居民姓名
                            for (Property property : newProperties) {
                                if (property.getResidentId() != null) {
                                    executorService.execute(() -> {
                                        try {
                                            Resident resident = apiService.getResidentById(property.getResidentId());
                                            if (resident != null) {
                                                property.setResidentName(resident.getName());
                                                runOnUiThread(() -> {
                                                    adapter.notifyDataSetChanged();
                                                });
                                            }
                                        } catch (Exception e) {
                                            // 忽略异常，继续处理其他房产
                                        }
                                    });
                                }
                            }

                            // 检查是否还有更多数据
                            hasMoreData = propertyList.size() < response.getData().getTotal();
                        } else {
                            // 没有更多数据了
                            hasMoreData = false;
                        }
                    } else {
                        // 加载失败，恢复页码
                        currentPage--;
                        String message = response.getMessage() != null ? response.getMessage() : "加载更多失败";
                        Toast.makeText(PropertyListActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    isLoading = false;
                    // 加载失败，恢复页码
                    currentPage--;
                    Toast.makeText(PropertyListActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_PROPERTY && resultCode == RESULT_OK) {
            loadProperties();
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
     * 设置WebSocket连接
     */
    private void setupWebSocket() {
        webSocketManager.setListener(new WebSocketManager.WebSocketListener() {
            @Override
            public void onConnected() {
                runOnUiThread(() -> {
                    Toast.makeText(PropertyListActivity.this, "已连接到服务器", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onDisconnected() {
                runOnUiThread(() -> {
                    Toast.makeText(PropertyListActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onMessage(String message) {
                // 处理房产数据变更消息
                if (message.contains("property") || message.contains("PROPERTY")) {
                    runOnUiThread(() -> {
                        Toast.makeText(PropertyListActivity.this, "房产数据已更新，正在刷新列表", Toast.LENGTH_SHORT).show();
                        loadProperties(); // 重新加载数据
                    });
                }
            }

            @Override
            public void onError(Exception error) {
                runOnUiThread(() -> {
                    Toast.makeText(PropertyListActivity.this, "WebSocket连接错误: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
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