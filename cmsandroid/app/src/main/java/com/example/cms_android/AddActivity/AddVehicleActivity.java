package com.example.cms_android.AddActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.R;
import com.example.cms_android.model.Resident;
import com.example.cms_android.model.Vehicle;
import com.example.cms_android.network.ApiService;
import com.example.cms_android.websocket.WebSocketManager;
import com.google.android.material.textfield.TextInputEditText;

import android.app.DatePickerDialog;
import android.widget.AutoCompleteTextView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddVehicleActivity extends AppCompatActivity {
    private TextInputEditText etResidentName, etIdCard, etLicensePlate, etBrand, etModel, etColor, etPurchaseDate, etEngineNumber, etChassisNumber, etPurchasePrice, etInsuranceExpiryDate, etInspectionDate, etNote;
    private AutoCompleteTextView etVehicleType;
    private Button btnSave;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private ExecutorService executorService;
    private WebSocketManager webSocketManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);
        
        // 设置Toolbar为ActionBar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 启用返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        initViews();
        initServices();
        setupDropdowns();
        setupListeners();
    }
    
    private void initViews() {
        etResidentName = findViewById(R.id.et_resident_name);
        etIdCard = findViewById(R.id.et_id_card);
        etLicensePlate = findViewById(R.id.et_license_plate);
        etVehicleType = findViewById(R.id.et_vehicle_type);
        etBrand = findViewById(R.id.et_brand);
        etModel = findViewById(R.id.et_model);
        etColor = findViewById(R.id.et_color);
        etPurchaseDate = findViewById(R.id.et_purchase_date);
        etEngineNumber = findViewById(R.id.et_engine_number);
        etChassisNumber = findViewById(R.id.et_chassis_number);
        etPurchasePrice = findViewById(R.id.et_purchase_price);
        etInsuranceExpiryDate = findViewById(R.id.et_insurance_expiry_date);
        etInspectionDate = findViewById(R.id.et_inspection_date);
        etNote = findViewById(R.id.et_note);
        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar);
        
        setupDropdowns();
    }
    
    private void initServices() {
        apiService = new ApiService();
        executorService = Executors.newSingleThreadExecutor();
        webSocketManager = WebSocketManager.getInstance();
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveVehicle());
        
        // 设置日期选择器监听器
        etPurchaseDate.setOnClickListener(v -> showDatePickerDialog(etPurchaseDate));
        etPurchaseDate.setFocusable(false);
        etPurchaseDate.setClickable(true);
        
        etInsuranceExpiryDate.setOnClickListener(v -> showDatePickerDialog(etInsuranceExpiryDate));
        etInsuranceExpiryDate.setFocusable(false);
        etInsuranceExpiryDate.setClickable(true);
        
        etInspectionDate.setOnClickListener(v -> showDatePickerDialog(etInspectionDate));
        etInspectionDate.setFocusable(false);
        etInspectionDate.setClickable(true);
    }
    
    /**
     * 显示日期选择对话框
     */
    private void showDatePickerDialog(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        try {
            String dateStr = editText.getText().toString();
            if (!dateStr.isEmpty()) {
                calendar.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    editText.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    
    private void setupDropdowns() {
        String[] vehicleTypes = {"轿车", "SUV", "客车", "货车"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, vehicleTypes);
        etVehicleType.setAdapter(adapter);
        // 设置默认值为第一个选项
        etVehicleType.setText(vehicleTypes[0], false);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveVehicle() {
        String residentName = etResidentName.getText().toString().trim();
        String idCard = etIdCard.getText().toString().trim();
        String licensePlate = etLicensePlate.getText().toString().trim();
        String vehicleType = etVehicleType.getText().toString().trim();
        String brand = etBrand.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        String purchaseDateStr = etPurchaseDate.getText().toString().trim();
        String engineNumber = etEngineNumber.getText().toString().trim();
        String chassisNumber = etChassisNumber.getText().toString().trim();
        String purchasePriceStr = etPurchasePrice.getText().toString().trim();
        String insuranceExpiryDateStr = etInsuranceExpiryDate.getText().toString().trim();
        String inspectionDateStr = etInspectionDate.getText().toString().trim();
        String note = etNote.getText().toString().trim();
        
        if (residentName.isEmpty() || idCard.isEmpty() || licensePlate.isEmpty() || vehicleType.isEmpty() || brand.isEmpty() || model.isEmpty() || purchaseDateStr.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 显示进度条
        showProgress(true);
        
        // 在后台线程执行保存请求
        executorService.execute(() -> {
            try {
                // 创建车辆对象
                Vehicle vehicle = new Vehicle();
                vehicle.setResidentName(residentName);
                vehicle.setIdCard(idCard);
                vehicle.setLicensePlate(licensePlate);
                vehicle.setVehicleType(vehicleType);
                vehicle.setBrand(brand);
                vehicle.setModel(model);
                vehicle.setColor(color);
                vehicle.setEngineNumber(engineNumber);
                vehicle.setChassisNumber(chassisNumber);
                vehicle.setNote(note);
                
                // 设置购买日期
                try {
                    vehicle.setPurchaseDate(LocalDate.parse(purchaseDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(AddVehicleActivity.this, "购买日期格式错误，请使用yyyy-MM-dd格式", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }
                
                // 设置保险到期日期
                if (!insuranceExpiryDateStr.isEmpty()) {
                    try {
                        vehicle.setInsuranceExpiryDate(LocalDate.parse(insuranceExpiryDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            showProgress(false);
                            Toast.makeText(AddVehicleActivity.this, "保险到期日期格式错误，请使用yyyy-MM-dd格式", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }
                }
                
                // 设置年检日期
                if (!inspectionDateStr.isEmpty()) {
                    try {
                        vehicle.setAnnualInspectionDate(LocalDate.parse(inspectionDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            showProgress(false);
                            Toast.makeText(AddVehicleActivity.this, "年检日期格式错误，请使用yyyy-MM-dd格式", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }
                }
                
                // 设置购买价格
                if (!purchasePriceStr.isEmpty()) {
                    try {
                        vehicle.setPurchasePrice(new BigDecimal(purchasePriceStr));
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            showProgress(false);
                            Toast.makeText(AddVehicleActivity.this, "购买价格格式错误，请输入有效的数字", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }
                }
                
                boolean success = apiService.addVehicle(vehicle);
                
                // 切换到主线程更新UI
                runOnUiThread(() -> {
                    showProgress(false);
                    
                    if (success) {
                        Toast.makeText(AddVehicleActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        // 通过WebSocket发送车辆数据变更通知
                        if (webSocketManager.isConnected()) {
                            webSocketManager.sendMessage("{\"type\": \"data_change\", \"module\": \"vehicle\", \"action\": \"add\"}");
                        }
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AddVehicleActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(AddVehicleActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnSave.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSave.setEnabled(true);
        }
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
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}