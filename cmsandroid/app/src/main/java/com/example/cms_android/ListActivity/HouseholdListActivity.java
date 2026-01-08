package com.example.cms_android.ListActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cms_android.AddActivity.AddHouseholdActivity;
import com.example.cms_android.R;
import com.example.cms_android.adapter.HouseholdAdapter;
import com.example.cms_android.model.Household;
import com.example.cms_android.response.base.HouseholdResponse;
import com.example.cms_android.network.ApiService;
import com.example.cms_android.utils.TokenManager;
import com.example.cms_android.websocket.DataChangeHandler;
import com.example.cms_android.websocket.WebSocketManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HouseholdListActivity extends AppCompatActivity {
    private static final int REQUEST_ADD_HOUSEHOLD = 1002;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ExtendedFloatingActionButton fabAdd;

    private HouseholdAdapter adapter;
    private List<Household> householdList;

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
        setContentView(R.layout.activity_household_list);

        initViews();
        initServices();
        setupRecyclerView();
        setupListeners();
        initWebSocket();
        loadHouseholds();
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
        householdList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new HouseholdAdapter(this, householdList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // 设置户籍项点击事件
        adapter.setOnItemClickListener(household -> {
            // 跳转到户籍详情页
            Intent intent = new Intent(HouseholdListActivity.this,
                    com.example.cms_android.activity.HouseholdActivity.class);
            intent.putExtra("household_id", household.getId());
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
                        loadMoreHouseholds();
                    }
                }
            }
        });
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(this::loadHouseholds);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(HouseholdListActivity.this, AddHouseholdActivity.class);
            startActivityForResult(intent, REQUEST_ADD_HOUSEHOLD);
        });
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
                    Toast.makeText(HouseholdListActivity.this, "WebSocket已连接", Toast.LENGTH_SHORT).show();
                });
            }
            
            @Override
            public void onDisconnected() {
                runOnUiThread(() -> {
                    Toast.makeText(HouseholdListActivity.this, "WebSocket已断开", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(HouseholdListActivity.this, "WebSocket错误: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
        
        // 注册数据变更监听器
        dataChangeListener = (module, operation) -> {
            if ("household".equals(module)) {
                runOnUiThread(() -> {
                    // 户籍数据变更，刷新列表
                    loadHouseholds();
                    
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
                    
                    Toast.makeText(HouseholdListActivity.this, "户籍数据已" + operationName + "，列表已刷新", Toast.LENGTH_SHORT).show();
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

    private void loadHouseholds() {
        // 重置分页状态
        currentPage = 1;
        hasMoreData = true;
        swipeRefreshLayout.setRefreshing(true);

        executorService.execute(() -> {
            try {
                HouseholdResponse response = apiService.getHouseholds(currentPage, pageSize);

                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);

                    if (response.isSuccess() && response.getData() != null && response.getData().getData() != null) {
                        // 清空列表，添加新数据
                        householdList.clear();
                        householdList.addAll(response.getData().getData());
                        adapter.notifyDataSetChanged();

                        // 检查是否还有更多数据
                        hasMoreData = householdList.size() < response.getData().getTotal();
                    } else {
                        String message = response.getMessage() != null ? response.getMessage() : "加载失败";
                        Toast.makeText(HouseholdListActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(HouseholdListActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * 加载更多户籍数据
     */
    private void loadMoreHouseholds() {
        if (isLoading || !hasMoreData) {
            return;
        }

        isLoading = true;
        currentPage++;

        executorService.execute(() -> {
            try {
                HouseholdResponse response = apiService.getHouseholds(currentPage, pageSize);

                runOnUiThread(() -> {
                    isLoading = false;

                    if (response.isSuccess() && response.getData() != null && response.getData().getData() != null) {
                        List<Household> newHouseholds = response.getData().getData();
                        if (newHouseholds != null && !newHouseholds.isEmpty()) {
                            // 添加新数据到列表
                            int startPosition = householdList.size();
                            householdList.addAll(newHouseholds);
                            adapter.notifyItemRangeInserted(startPosition, newHouseholds.size());

                            // 检查是否还有更多数据
                            hasMoreData = householdList.size() < response.getData().getTotal();
                        } else {
                            // 没有更多数据了
                            hasMoreData = false;
                        }
                    } else {
                        // 加载失败，恢复页码
                        currentPage--;
                        String message = response.getMessage() != null ? response.getMessage() : "加载更多失败";
                        Toast.makeText(HouseholdListActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    isLoading = false;
                    // 加载失败，恢复页码
                    currentPage--;
                    Toast.makeText(HouseholdListActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_HOUSEHOLD && resultCode == RESULT_OK) {
            // 添加户籍成功，刷新列表
            loadHouseholds();
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
        
        // 断开WebSocket连接
        if (webSocketManager != null) {
            webSocketManager.disconnect();
        }
        
        // 注销数据变更监听器
        if (dataChangeHandler != null && dataChangeListener != null) {
            dataChangeHandler.unregisterListener(dataChangeListener);
        }
    }
}