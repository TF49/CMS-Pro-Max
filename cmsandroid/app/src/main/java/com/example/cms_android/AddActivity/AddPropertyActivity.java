package com.example.cms_android.AddActivity;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.R;
import com.example.cms_android.model.Property;
import com.example.cms_android.network.ApiService;
import com.example.cms_android.websocket.WebSocketManager;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AddPropertyActivity extends AppCompatActivity {
    private TextInputEditText etResidentName, etIdCard, etAddress, etArea,
            etPropertyCertificateNumber, etPrice, etPurchaseDate, etRooms, etRemark;
    private AutoCompleteTextView etPropertyType, etUseType;
    private Button btnSave;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private ExecutorService executorService;
    private WebSocketManager webSocketManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);
        
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
    }

    private void initViews() {
        etResidentName = findViewById(R.id.et_resident_name);
        etIdCard = findViewById(R.id.et_id_card);
        etPropertyType = findViewById(R.id.et_property_type);
        etAddress = findViewById(R.id.et_address);
        etArea = findViewById(R.id.et_area);
        etPropertyCertificateNumber = findViewById(R.id.et_property_certificate_number);
        etPrice = findViewById(R.id.et_price);
        etUseType = findViewById(R.id.et_use_type);
        etPurchaseDate = findViewById(R.id.et_purchase_date);
        etRooms = findViewById(R.id.et_rooms);
        etRemark = findViewById(R.id.et_remark);

        setupDropdowns();

        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void initServices() {
        apiService = new ApiService();
        executorService = Executors.newSingleThreadExecutor();
        webSocketManager = WebSocketManager.getInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveProperty());
        
        // 设置日期选择器监听器
        etPurchaseDate.setOnClickListener(v -> showDatePickerDialog(etPurchaseDate));
        etPurchaseDate.setFocusable(false);
        etPurchaseDate.setClickable(true);
    }

    private void setupDropdowns() {
        // 设置房产类别下拉选项
        String[] propertyTypes = {"商品房", "经济适用法", "农村自建房", "限价房", "公租房", "廉租房", "小产权房", "别墅"};
        ArrayAdapter<String> adapterProperty = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                propertyTypes);
        etPropertyType.setAdapter(adapterProperty);
        etPropertyType.setText(propertyTypes[0], false);

        // 设置使用类型下拉选项
        String[] useTypes = {"自住", "出租", "闲置"};
        ArrayAdapter<String> adapterUse = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                useTypes);
        etUseType.setAdapter(adapterUse);
        etUseType.setText(useTypes[0], false);
    }
    
    /**
     * 显示日期选择对话框
     */
    private void showDatePickerDialog(EditText editText) {
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

    private void saveProperty() {
        String residentName = etResidentName.getText().toString().trim();
        String idCard = etIdCard.getText().toString().trim();
        String propertyType = etPropertyType.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String area = etArea.getText().toString().trim();
        String propertyCertificateNumber = etPropertyCertificateNumber.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String useType = etUseType.getText().toString().trim();
        String purchaseDateStr = etPurchaseDate.getText().toString().trim();
        String roomsStr = etRooms.getText().toString().trim();
        String remark = etRemark.getText().toString().trim();
    
        if (propertyType.isEmpty() || address.isEmpty() || area.isEmpty()) {
            Toast.makeText(this, "房产类型、地址和面积不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
            
        // 验证面积必须大于0
        try {
            BigDecimal areaValue = new BigDecimal(area);
            if (areaValue.compareTo(BigDecimal.ZERO) <= 0) {
                Toast.makeText(this, "面积必须大于0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "面积格式错误，请输入有效的数字", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 验证估值必须大于等于0
        if (!priceStr.isEmpty()) {
            try {
                BigDecimal priceValue = new BigDecimal(priceStr);
                if (priceValue.compareTo(BigDecimal.ZERO) < 0) {
                    Toast.makeText(this, "估值必须大于等于0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "估值格式错误，请输入有效的数字", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        // 验证取得日期不能为空
        if (purchaseDateStr.isEmpty()) {
            Toast.makeText(this, "取得日期不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
            
        showProgress(true);
            
        Property property = new Property();
        property.setResidentName(residentName);
        property.setIdCard(idCard);
        property.setPropertyType(propertyType);
        property.setAddress(address);
        property.setArea(new BigDecimal(area));
        property.setPropertyCertificateNumber(propertyCertificateNumber);
        property.setUseType(useType);
    
        if (!priceStr.isEmpty()) {
            try {
                property.setPrice(new BigDecimal(priceStr));
            } catch (NumberFormatException e) {
                // Ignore or show warning
            }
        }
        
        // 设置取得日期
        if (!purchaseDateStr.isEmpty()) {
            try {
                property.setPurchaseDate(java.time.LocalDate.parse(purchaseDateStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } catch (Exception e) {
                Toast.makeText(this, "取得日期格式错误，请使用yyyy-MM-dd格式", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        // 设置房间数
        if (!roomsStr.isEmpty()) {
            try {
                property.setRooms(Integer.parseInt(roomsStr));
            } catch (Exception e) {
                Toast.makeText(this, "房间数格式错误，请输入有效的整数", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        // 设置备注
        property.setNotes(remark);

        executorService.execute(() -> {
            try {
                boolean success = apiService.addProperty(property);

                runOnUiThread(() -> {
                    showProgress(false);

                    if (success) {
                        Toast.makeText(AddPropertyActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        // 通过WebSocket发送房产数据变更通知
                        if (webSocketManager.isConnected()) {
                            webSocketManager.sendMessage("{\"type\": \"data_change\", \"module\": \"property\", \"action\": \"add\"}");
                        }
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AddPropertyActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(AddPropertyActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
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