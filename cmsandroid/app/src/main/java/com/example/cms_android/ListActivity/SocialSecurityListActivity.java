package com.example.cms_android.ListActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cms_android.AddActivity.AddSocialSecurityActivity;
import com.example.cms_android.R;
import com.example.cms_android.adapter.SocialSecurityAdapter;
import com.example.cms_android.model.Resident;
import com.example.cms_android.model.SocialSecurity;
import com.example.cms_android.network.ApiService;
import com.example.cms_android.response.base.SocialSecurityResponse;
import com.example.cms_android.websocket.WebSocketManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocialSecurityListActivity extends AppCompatActivity {
    private static final int REQUEST_ADD_SOCIAL_SECURITY = 1006;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ExtendedFloatingActionButton fabAdd;

    private SocialSecurityAdapter adapter;
    private List<SocialSecurity> socialSecurityList;

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
        setContentView(R.layout.activity_socialsecurity_list);

        initViews();
        initServices();
        setupRecyclerView();
        setupListeners();
        setupWebSocket();
        loadSocialSecurities();
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
        socialSecurityList = new ArrayList<>();
        webSocketManager = WebSocketManager.getInstance();
    }

    private void setupRecyclerView() {
        adapter = new SocialSecurityAdapter(this, socialSecurityList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // 设置点击事件监听器
        adapter.setOnItemClickListener(socialSecurity -> {
            Intent intent = new Intent(SocialSecurityListActivity.this,
                    com.example.cms_android.activity.SocialSecurityActivity.class);
            intent.putExtra("social_security_id", socialSecurity.getId());
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
                        loadMoreSocialSecurities();
                    }
                }
            }
        });
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(this::loadSocialSecurities);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(SocialSecurityListActivity.this, AddSocialSecurityActivity.class);
            startActivityForResult(intent, REQUEST_ADD_SOCIAL_SECURITY);
        });
    }

    private void loadSocialSecurities() {
        currentPage = 1;
        hasMoreData = true;
        swipeRefreshLayout.setRefreshing(true);

        executorService.execute(() -> {
            try {
                SocialSecurityResponse response = apiService.getSocialSecurities(currentPage, pageSize);

                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);

                    if (response != null && response.isSuccess() && response.getData() != null
                            && response.getData().getData() != null) {
                        List<SocialSecurity> socialSecurities = response.getData().getData();
                        if (socialSecurities != null && !socialSecurities.isEmpty()) {
                            // 获取每个社保对应的居民姓名
                            executorService.execute(() -> {
                                try {
                                    for (SocialSecurity socialSecurity : socialSecurities) {
                                        if (socialSecurity.getResidentId() != null) {
                                            Resident resident = apiService
                                                    .getResidentById(socialSecurity.getResidentId());
                                            if (resident != null) {
                                                socialSecurity.setResidentName(resident.getName());
                                            }
                                        }
                                    }

                                    runOnUiThread(() -> {
                                        socialSecurityList.clear();
                                        socialSecurityList.addAll(socialSecurities);
                                        adapter.notifyDataSetChanged();
                                        hasMoreData = socialSecurityList.size() < response.getData().getTotal();
                                    });
                                } catch (Exception e) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(SocialSecurityListActivity.this, "获取居民信息失败: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                        // 即使获取居民信息失败，也要显示社保列表
                                        socialSecurityList.clear();
                                        socialSecurityList.addAll(socialSecurities);
                                        adapter.notifyDataSetChanged();
                                        hasMoreData = socialSecurityList.size() < response.getData().getTotal();
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(SocialSecurityListActivity.this, "没有社保信息", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(SocialSecurityListActivity.this,
                                "获取社保信息失败: " + (response != null ? response.getMessage() : "未知错误"), Toast.LENGTH_LONG)
                                .show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(SocialSecurityListActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                });
            }
        });
    }

    private void loadMoreSocialSecurities() {
        if (isLoading || !hasMoreData) {
            return;
        }

        isLoading = true;
        currentPage++;

        executorService.execute(() -> {
            try {
                SocialSecurityResponse response = apiService.getSocialSecurities(currentPage, pageSize);

                runOnUiThread(() -> {
                    isLoading = false;

                    if (response != null && response.isSuccess() && response.getData() != null
                            && response.getData().getData() != null) {
                        List<SocialSecurity> socialSecurities = response.getData().getData();
                        if (socialSecurities != null && !socialSecurities.isEmpty()) {
                            // 获取每个社保对应的居民姓名
                            executorService.execute(() -> {
                                try {
                                    for (SocialSecurity socialSecurity : socialSecurities) {
                                        if (socialSecurity.getResidentId() != null) {
                                            Resident resident = apiService
                                                    .getResidentById(socialSecurity.getResidentId());
                                            if (resident != null) {
                                                socialSecurity.setResidentName(resident.getName());
                                            }
                                        }
                                    }

                                    runOnUiThread(() -> {
                                        int startPosition = socialSecurityList.size();
                                        socialSecurityList.addAll(socialSecurities);
                                        adapter.notifyItemRangeInserted(startPosition, socialSecurities.size());
                                        hasMoreData = socialSecurityList.size() < response.getData().getTotal();
                                    });
                                } catch (Exception e) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(SocialSecurityListActivity.this, "获取居民信息失败: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                        // 即使获取居民信息失败，也要显示社保列表
                                        int startPosition = socialSecurityList.size();
                                        socialSecurityList.addAll(socialSecurities);
                                        adapter.notifyItemRangeInserted(startPosition, socialSecurities.size());
                                        hasMoreData = socialSecurityList.size() < response.getData().getTotal();
                                    });
                                }
                            });
                        } else {
                            hasMoreData = false;
                        }
                    } else {
                        currentPage--;
                        Toast.makeText(SocialSecurityListActivity.this,
                                "获取更多社保信息失败: " + (response != null ? response.getMessage() : "未知错误"), Toast.LENGTH_LONG)
                                .show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    isLoading = false;
                    currentPage--;
                    Toast.makeText(SocialSecurityListActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_SOCIAL_SECURITY && resultCode == RESULT_OK) {
            loadSocialSecurities();
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
                    Toast.makeText(SocialSecurityListActivity.this, "已连接到服务器", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onDisconnected() {
                runOnUiThread(() -> {
                    Toast.makeText(SocialSecurityListActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onMessage(String message) {
                // 处理社保数据变更消息
                if (message.contains("socialsecurity") || message.contains("SOCIAL_SECURITY")) {
                    runOnUiThread(() -> {
                        Toast.makeText(SocialSecurityListActivity.this, "社保数据已更新，正在刷新列表", Toast.LENGTH_SHORT).show();
                        loadSocialSecurities(); // 重新加载数据
                    });
                }
            }

            @Override
            public void onError(Exception error) {
                runOnUiThread(() -> {
                    Toast.makeText(SocialSecurityListActivity.this, "WebSocket连接错误: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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