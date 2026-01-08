package com.example.cms_android.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.R;
import com.example.cms_android.model.Vehicle;
import com.example.cms_android.network.ApiService;
import com.example.cms_android.websocket.WebSocketManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class VehicleActivity extends AppCompatActivity {
    private static final String EXTRA_VEHICLE_ID = "vehicle_id";

    private TextView tvId, tvResidentId, tvResidentName, tvLicensePlate, tvVehicleType, tvBrand, tvModel, tvColor;
    private TextView tvEngineNumber, tvChassisNumber, tvPurchaseDate, tvPurchasePrice, tvInsuranceExpiryDate,
            tvInspectionDate;
    private TextView tvCreateTime, tvUpdateTime, tvNote;
    private EditText etLicensePlateEdit, etBrandEdit, etModelEdit, etColorEdit;
    private AutoCompleteTextView etVehicleTypeEdit;
    private EditText etEngineNumberEdit, etChassisNumberEdit, etPurchaseDateEdit, etPurchasePriceEdit;
    private EditText etInsuranceExpiryDateEdit, etInspectionDateEdit, etNoteEdit;
    private Button btnEdit, btnSave, btnCancel, btnDelete;
    private View viewDetails, viewEdit;

    private ApiService apiService;
    private ExecutorService executorService;
    private WebSocketManager webSocketManager;
    private Long vehicleId;
    private Vehicle vehicle;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        // 设置Toolbar为ActionBar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 启用返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();
        initServices();
        setupListeners();

        // 获取车辆ID
        vehicleId = getIntent().getLongExtra(EXTRA_VEHICLE_ID, -1);
        if (vehicleId == -1) {
            Toast.makeText(this, "无效的车辆ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 加载车辆详情
        loadVehicleDetails();
    }

    private void initViews() {
        // 详情视图
        viewDetails = findViewById(R.id.view_details);
        tvId = findViewById(R.id.tv_id);
        tvResidentId = findViewById(R.id.tv_resident_id);
        tvResidentName = findViewById(R.id.tv_resident_name);
        tvLicensePlate = findViewById(R.id.tv_license_plate);
        tvVehicleType = findViewById(R.id.tv_vehicle_type);
        tvBrand = findViewById(R.id.tv_brand);
        tvModel = findViewById(R.id.tv_model);
        tvColor = findViewById(R.id.tv_color);
        tvEngineNumber = findViewById(R.id.tv_engine_number);
        tvChassisNumber = findViewById(R.id.tv_chassis_number);
        tvPurchaseDate = findViewById(R.id.tv_purchase_date);
        tvPurchasePrice = findViewById(R.id.tv_purchase_price);
        tvInsuranceExpiryDate = findViewById(R.id.tv_insurance_expiry_date);
        tvInspectionDate = findViewById(R.id.tv_inspection_date);
        tvCreateTime = findViewById(R.id.tv_create_time);
        tvUpdateTime = findViewById(R.id.tv_update_time);
        tvNote = findViewById(R.id.tv_note);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);

        // 编辑视图
        viewEdit = findViewById(R.id.view_edit);
        etLicensePlateEdit = findViewById(R.id.et_license_plate_edit);
        etVehicleTypeEdit = findViewById(R.id.et_vehicle_type_edit);
        etBrandEdit = findViewById(R.id.et_brand_edit);
        etModelEdit = findViewById(R.id.et_model_edit);
        etColorEdit = findViewById(R.id.et_color_edit);
        etEngineNumberEdit = findViewById(R.id.et_engine_number_edit);
        etChassisNumberEdit = findViewById(R.id.et_chassis_number_edit);
        etPurchaseDateEdit = findViewById(R.id.et_purchase_date_edit);
        etPurchasePriceEdit = findViewById(R.id.et_purchase_price_edit);
        etInsuranceExpiryDateEdit = findViewById(R.id.et_insurance_expiry_date_edit);
        etInspectionDateEdit = findViewById(R.id.et_inspection_date_edit);
        etNoteEdit = findViewById(R.id.et_note_edit);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        // 默认显示详情视图，隐藏编辑视图
        viewDetails.setVisibility(View.VISIBLE);
        viewEdit.setVisibility(View.GONE);
    }

    private void initServices() {
        apiService = new ApiService();
        executorService = Executors.newSingleThreadExecutor();
        webSocketManager = WebSocketManager.getInstance();
    }

    private void setupListeners() {
        btnEdit.setOnClickListener(v -> switchToEditMode());
        btnSave.setOnClickListener(v -> saveVehicleChanges());
        btnCancel.setOnClickListener(v -> switchToDetailsMode());
        btnDelete.setOnClickListener(v -> deleteVehicle());

        // 设置日期选择器监听器
        etPurchaseDateEdit.setOnClickListener(v -> showDatePickerDialog(etPurchaseDateEdit));
        etPurchaseDateEdit.setFocusable(false);
        etPurchaseDateEdit.setClickable(true);

        etInsuranceExpiryDateEdit.setOnClickListener(v -> showDatePickerDialog(etInsuranceExpiryDateEdit));
        etInsuranceExpiryDateEdit.setFocusable(false);
        etInsuranceExpiryDateEdit.setClickable(true);

        etInspectionDateEdit.setOnClickListener(v -> showDatePickerDialog(etInspectionDateEdit));
        etInspectionDateEdit.setFocusable(false);
        etInspectionDateEdit.setClickable(true);

        setupDropdowns();
    }

    private void setupDropdowns() {
        String[] vehicleTypes = { "轿车", "SUV", "客车", "货车" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                vehicleTypes);
        etVehicleTypeEdit.setAdapter(adapter);
    }

    /**
     * 加载车辆详情
     */
    private void loadVehicleDetails() {
        executorService.execute(() -> {
            try {
                // 这里需要根据实际的API调整，假设ApiService中有getVehicleById方法
                vehicle = apiService.getVehicleById(vehicleId);

                runOnUiThread(() -> {
                    if (vehicle != null) {
                        displayVehicleDetails();
                    } else {
                        Toast.makeText(VehicleActivity.this, "加载车辆详情失败", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(VehicleActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * 显示车辆详情
     */
    private void displayVehicleDetails() {
        tvId.setText(String.valueOf(vehicle.getId() != null ? vehicle.getId() : ""));
        tvResidentId.setText(String.valueOf(vehicle.getResidentId() != null ? vehicle.getResidentId() : ""));
        tvResidentName.setText(vehicle.getResidentName() != null ? vehicle.getResidentName() : "");
        tvLicensePlate.setText(vehicle.getLicensePlate() != null ? vehicle.getLicensePlate() : "");
        tvVehicleType.setText(vehicle.getVehicleType() != null ? vehicle.getVehicleType() : "");
        tvBrand.setText(vehicle.getBrand() != null ? vehicle.getBrand() : "");
        tvModel.setText(vehicle.getModel() != null ? vehicle.getModel() : "");
        tvColor.setText(vehicle.getColor() != null ? vehicle.getColor() : "");
        tvEngineNumber.setText(vehicle.getEngineNumber() != null ? vehicle.getEngineNumber() : "");
        tvChassisNumber.setText(vehicle.getChassisNumber() != null ? vehicle.getChassisNumber() : "");
        tvPurchaseDate.setText(vehicle.getPurchaseDate() != null ? vehicle.getPurchaseDate().format(dateFormatter) : "");
        tvPurchasePrice.setText(vehicle.getPurchasePrice() != null ? vehicle.getPurchasePrice().toString() : "");
        tvInsuranceExpiryDate.setText(
                vehicle.getInsuranceExpiryDate() != null ? vehicle.getInsuranceExpiryDate().format(dateFormatter) : "");
        tvInspectionDate
                .setText(vehicle.getInspectionDate() != null ? vehicle.getInspectionDate().format(dateFormatter) : "");
        tvCreateTime.setText(vehicle.getCreateTime() != null ? vehicle.getCreateTime().format(dateTimeFormatter) : "");
        tvUpdateTime.setText(vehicle.getUpdateTime() != null ? vehicle.getUpdateTime().format(dateTimeFormatter) : "");
        tvNote.setText(vehicle.getNote() != null ? vehicle.getNote() : "");
    }

    /**
     * 切换到编辑模式
     */
    private void switchToEditMode() {
        // 填充编辑表单，添加空检查
        etLicensePlateEdit.setText(vehicle.getLicensePlate() != null ? vehicle.getLicensePlate() : "");
        // 关键修复：使用false参数关闭过滤，确保下拉时显示所有选项
        etVehicleTypeEdit.setText(vehicle.getVehicleType() != null ? vehicle.getVehicleType() : "", false);
        etBrandEdit.setText(vehicle.getBrand() != null ? vehicle.getBrand() : "");
        etModelEdit.setText(vehicle.getModel() != null ? vehicle.getModel() : "");
        etColorEdit.setText(vehicle.getColor() != null ? vehicle.getColor() : "");
        etEngineNumberEdit.setText(vehicle.getEngineNumber() != null ? vehicle.getEngineNumber() : "");
        etChassisNumberEdit.setText(vehicle.getChassisNumber() != null ? vehicle.getChassisNumber() : "");
        etPurchaseDateEdit
                .setText(vehicle.getPurchaseDate() != null ? vehicle.getPurchaseDate().format(dateFormatter) : "");
        etPurchasePriceEdit.setText(vehicle.getPurchasePrice() != null ? vehicle.getPurchasePrice().toString() : "");
        etInsuranceExpiryDateEdit.setText(
                vehicle.getInsuranceExpiryDate() != null ? vehicle.getInsuranceExpiryDate().format(dateFormatter) : "");
        etInspectionDateEdit
                .setText(vehicle.getInspectionDate() != null ? vehicle.getInspectionDate().format(dateFormatter) : "");
        etNoteEdit.setText(vehicle.getNote() != null ? vehicle.getNote() : "");

        // 切换视图
        viewDetails.setVisibility(View.GONE);
        viewEdit.setVisibility(View.VISIBLE);
    }

    /**
     * 切换到详情模式
     */
    private void switchToDetailsMode() {
        viewDetails.setVisibility(View.VISIBLE);
        viewEdit.setVisibility(View.GONE);
    }

    /**
     * 保存车辆信息更改
     */
    private void saveVehicleChanges() {
        String licensePlate = etLicensePlateEdit.getText().toString().trim();
        String vehicleType = etVehicleTypeEdit.getText().toString().trim();
        String brand = etBrandEdit.getText().toString().trim();
        String model = etModelEdit.getText().toString().trim();
        String color = etColorEdit.getText().toString().trim();
        String engineNumber = etEngineNumberEdit.getText().toString().trim();
        String chassisNumber = etChassisNumberEdit.getText().toString().trim();
        String purchaseDateStr = etPurchaseDateEdit.getText().toString().trim();
        String purchasePrice = etPurchasePriceEdit.getText().toString().trim();
        String insuranceExpiryDateStr = etInsuranceExpiryDateEdit.getText().toString().trim();
        String inspectionDateStr = etInspectionDateEdit.getText().toString().trim();
        String note = etNoteEdit.getText().toString().trim();

        if (licensePlate.isEmpty() || vehicleType.isEmpty() || brand.isEmpty() || model.isEmpty()) {
            Toast.makeText(this, "车牌号、车辆类型、品牌和型号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 更新车辆对象
        vehicle.setLicensePlate(licensePlate);
        vehicle.setVehicleType(vehicleType);
        vehicle.setBrand(brand);
        vehicle.setModel(model);
        vehicle.setColor(color);
        vehicle.setEngineNumber(engineNumber);
        vehicle.setChassisNumber(chassisNumber);
        vehicle.setPurchasePrice(new BigDecimal(purchasePrice));
        vehicle.setNote(note);
        
        // 保留原有的居民信息，避免后端验证失败
        // 注意：这里假设vehicle对象在加载时已经包含了这些信息

        // 转换日期字段
        try {
            if (!purchaseDateStr.isEmpty()) {
                vehicle.setPurchaseDate(LocalDate.parse(purchaseDateStr, dateFormatter));
            }
            if (!insuranceExpiryDateStr.isEmpty()) {
                vehicle.setInsuranceExpiryDate(LocalDate.parse(insuranceExpiryDateStr, dateFormatter));
            }
            if (!inspectionDateStr.isEmpty()) {
                vehicle.setInspectionDate(LocalDate.parse(inspectionDateStr, dateFormatter));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "日期格式错误，请使用yyyy-MM-dd格式", Toast.LENGTH_SHORT).show();
            return;
        }

        // 保存到服务器
        executorService.execute(() -> {
            try {
                // 这里需要根据实际的API调整，假设ApiService中有updateVehicle方法
                boolean success = apiService.updateVehicle(vehicle);

                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(VehicleActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        // 通过WebSocket发送车辆数据变更通知
                        if (webSocketManager.isConnected()) {
                            webSocketManager.sendMessage("{\"type\": \"data_change\", \"module\": \"vehicle\", \"action\": \"update\"}");
                        }
                        // 刷新详情显示
                        displayVehicleDetails();
                        switchToDetailsMode();
                    } else {
                        Toast.makeText(VehicleActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(VehicleActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * 删除车辆信息
     */
    private void deleteVehicle() {
        // 显示确认对话框
        new android.app.AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除该车辆档案吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    executorService.execute(() -> {
                        try {
                            boolean success = apiService.deleteVehicle(vehicleId);
                            runOnUiThread(() -> {
                                if (success) {
                                    Toast.makeText(VehicleActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    // 通过WebSocket发送车辆数据变更通知
                                    if (webSocketManager.isConnected()) {
                                        webSocketManager.sendMessage("{\"type\": \"data_change\", \"module\": \"vehicle\", \"action\": \"delete\"}");
                                    }
                                    finish();
                                } else {
                                    Toast.makeText(VehicleActivity.this, "删除失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(VehicleActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG)
                                        .show();
                            });
                        }
                    });
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 显示日期选择对话框
     */
    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        try {
            String dateStr = editText.getText().toString();
            if (!dateStr.isEmpty()) {
                LocalDate localDate = LocalDate.parse(dateStr, dateFormatter);
                calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                    editText.setText(selectedDate.format(dateFormatter));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // 如果当前是编辑模式，提示用户是否保存更改
        if (viewEdit.getVisibility() == View.VISIBLE) {
            // 显示确认对话框
            new android.app.AlertDialog.Builder(this)
                    .setTitle("确认返回")
                    .setMessage("您正在编辑车辆信息，是否保存更改？")
                    .setPositiveButton("保存", (dialog, which) -> {
                        // 保存更改
                        saveVehicleChanges();
                        // 保存成功后，返回上一页
                        super.onBackPressed();
                    })
                    .setNegativeButton("不保存", (dialog, which) -> {
                        // 不保存，直接返回上一页
                        super.onBackPressed();
                    })
                    .setNeutralButton("取消", null)
                    .show();
        } else {
            // 非编辑模式，直接返回上一页
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}