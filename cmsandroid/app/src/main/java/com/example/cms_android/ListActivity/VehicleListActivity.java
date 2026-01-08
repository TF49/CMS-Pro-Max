package com.example.cms_android.ListActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cms_android.AddActivity.AddVehicleActivity;
import com.example.cms_android.R;
import com.example.cms_android.adapter.VehicleAdapter;
import com.example.cms_android.model.Resident;
import com.example.cms_android.model.Vehicle;
import com.example.cms_android.network.ApiService;
import com.example.cms_android.response.base.VehicleResponse;
import com.example.cms_android.websocket.WebSocketManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VehicleListActivity extends AppCompatActivity {
    private static final int REQUEST_ADD_VEHICLE = 1007;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ExtendedFloatingActionButton fabAdd;

    private VehicleAdapter adapter;
    private List<Vehicle> vehicleList;

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
        setContentView(R.layout.activity_vehicle_list);

        initViews();
        initServices();
        setupRecyclerView();
        setupListeners();
        setupWebSocket();
        loadVehicles();
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
        vehicleList = new ArrayList<>();
        webSocketManager = WebSocketManager.getInstance();
    }

    private void setupRecyclerView() {
        adapter = new VehicleAdapter(this, vehicleList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // 设置车辆项点击事件
        adapter.setOnItemClickListener(vehicle -> {
            // 跳转到车辆详情页
            Intent intent = new Intent(VehicleListActivity.this,
                    com.example.cms_android.activity.VehicleActivity.class);
            intent.putExtra("vehicle_id", vehicle.getId());
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
                        loadMoreVehicles();
                    }
                }
            }
        });
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(this::loadVehicles);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(VehicleListActivity.this, AddVehicleActivity.class);
            startActivityForResult(intent, REQUEST_ADD_VEHICLE);
        });
    }

    /**
     * 加载车辆列表数据
     */
    private void loadVehicles() {
        // 重置分页状态
        currentPage = 1;
        hasMoreData = true;
        swipeRefreshLayout.setRefreshing(true);

        executorService.execute(() -> {
            try {
                VehicleResponse response = apiService.getVehicles(currentPage, pageSize);

                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);

                    if (response.isSuccess() && response.getData() != null && response.getData().getData() != null) {
                        // 清空列表，添加新数据
                        vehicleList.clear();
                        List<Vehicle> vehicles = response.getData().getData();

                        // 为每个车辆获取居民姓名
                        for (Vehicle vehicle : vehicles) {
                            if (vehicle.getResidentId() != null) {
                                try {
                                    // 在单独的线程中获取居民信息
                                    executorService.execute(() -> {
                                        try {
                                            Resident resident = apiService.getResidentById(vehicle.getResidentId());
                                            if (resident != null) {
                                                vehicle.setResidentName(resident.getName());
                                                runOnUiThread(() -> {
                                                    adapter.notifyDataSetChanged();
                                                });
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        vehicleList.addAll(vehicles);
                        adapter.notifyDataSetChanged();

                        // 检查是否还有更多数据
                        hasMoreData = vehicleList.size() < response.getData().getTotal();
                    } else {
                        String message = response.getMessage() != null ? response.getMessage() : "加载失败";
                        Toast.makeText(VehicleListActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(VehicleListActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * 加载更多车辆数据
     */
    private void loadMoreVehicles() {
        if (isLoading || !hasMoreData) {
            return;
        }

        isLoading = true;
        currentPage++;

        executorService.execute(() -> {
            try {
                VehicleResponse response = apiService.getVehicles(currentPage, pageSize);

                runOnUiThread(() -> {
                    isLoading = false;

                    if (response.isSuccess() && response.getData() != null && response.getData().getData() != null) {
                        List<Vehicle> newVehicles = response.getData().getData();
                        if (newVehicles != null && !newVehicles.isEmpty()) {
                            // 添加新数据到列表
                            int startPosition = vehicleList.size();
                            vehicleList.addAll(newVehicles);
                            adapter.notifyItemRangeInserted(startPosition, newVehicles.size());

                            // 为每个车辆获取居民姓名
                            for (Vehicle vehicle : newVehicles) {
                                if (vehicle.getResidentId() != null) {
                                    try {
                                        // 在单独的线程中获取居民信息
                                        executorService.execute(() -> {
                                            try {
                                                Resident resident = apiService.getResidentById(vehicle.getResidentId());
                                                if (resident != null) {
                                                    vehicle.setResidentName(resident.getName());
                                                    runOnUiThread(() -> {
                                                        adapter.notifyDataSetChanged();
                                                    });
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            // 检查是否还有更多数据
                            hasMoreData = vehicleList.size() < response.getData().getTotal();
                        } else {
                            // 没有更多数据了
                            hasMoreData = false;
                        }
                    } else {
                        // 加载失败，恢复页码
                        currentPage--;
                        String message = response.getMessage() != null ? response.getMessage() : "加载更多失败";
                        Toast.makeText(VehicleListActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    isLoading = false;
                    // 加载失败，恢复页码
                    currentPage--;
                    Toast.makeText(VehicleListActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_VEHICLE && resultCode == RESULT_OK) {
            // 添加车辆成功，刷新列表
            loadVehicles();
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
                    Toast.makeText(VehicleListActivity.this, "已连接到服务器", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onDisconnected() {
                runOnUiThread(() -> {
                    Toast.makeText(VehicleListActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onMessage(String message) {
                // 处理车辆数据变更消息
                if (message.contains("vehicle") || message.contains("VEHICLE")) {
                    runOnUiThread(() -> {
                        Toast.makeText(VehicleListActivity.this, "车辆数据已更新，正在刷新列表", Toast.LENGTH_SHORT).show();
                        loadVehicles(); // 重新加载数据
                    });
                }
            }

            @Override
            public void onError(Exception error) {
                runOnUiThread(() -> {
                    Toast.makeText(VehicleListActivity.this, "WebSocket连接错误: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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