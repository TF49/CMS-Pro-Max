package com.example.cms_android.activity;

import android.app.DatePickerDialog;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.R;
import com.example.cms_android.model.Property;
import com.example.cms_android.network.ApiService;
import com.example.cms_android.websocket.WebSocketManager;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class PropertyActivity extends AppCompatActivity {
    private static final String EXTRA_PROPERTY_ID = "property_id";

    // Detail Views
    private TextView tvId, tvResidentId, tvResidentName, tvPropertyType, tvAddress, tvArea,
            tvPropertyCertificateNumber, tvPrice,
            tvUseType, tvPurchaseDate, tvRooms, tvCreateTime, tvUpdateTime, tvRemark;

    // Edit Views
    private TextInputEditText etResidentNameEdit, etIdCardEdit, etAddressEdit, etAreaEdit,
            etPropertyCertificateNumberEdit, etPriceEdit,
            etPurchaseDateEdit, etRoomsEdit, etRemarkEdit;
    private AutoCompleteTextView etPropertyTypeEdit, etUseTypeEdit;

    private Button btnEdit, btnSave, btnCancel, btnDelete;
    private View viewDetails, viewEdit;

    private ApiService apiService;
    private ExecutorService executorService;
    private WebSocketManager webSocketManager;
    private Long propertyId;
    private Property property;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property);

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

        // 获取房产ID
        propertyId = getIntent().getLongExtra(EXTRA_PROPERTY_ID, -1);
        if (propertyId == -1) {
            Toast.makeText(this, "无效的房产ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 加载房产详情
        loadPropertyDetails();
    }

    private void initViews() {
        // 详情视图
        viewDetails = findViewById(R.id.view_details);
        tvId = findViewById(R.id.tv_id);
        tvResidentId = findViewById(R.id.tv_resident_id);
        tvResidentName = findViewById(R.id.tv_resident_name);
        tvPropertyType = findViewById(R.id.tv_property_type);
        tvAddress = findViewById(R.id.tv_address);
        tvArea = findViewById(R.id.tv_area);
        tvPropertyCertificateNumber = findViewById(R.id.tv_property_certificate_number);
        tvPrice = findViewById(R.id.tv_price);
        tvUseType = findViewById(R.id.tv_use_type);
        tvPurchaseDate = findViewById(R.id.tv_purchase_date);
        tvRooms = findViewById(R.id.tv_rooms);
        tvCreateTime = findViewById(R.id.tv_create_time);
        tvUpdateTime = findViewById(R.id.tv_update_time);
        tvRemark = findViewById(R.id.tv_remark);

        // 编辑视图
        viewEdit = findViewById(R.id.view_edit);
        etResidentNameEdit = findViewById(R.id.et_resident_name_edit);
        etIdCardEdit = findViewById(R.id.et_id_card_edit);
        etPropertyTypeEdit = findViewById(R.id.et_property_type_edit);
        etAddressEdit = findViewById(R.id.et_address_edit);
        etAreaEdit = findViewById(R.id.et_area_edit);
        etPropertyCertificateNumberEdit = findViewById(R.id.et_property_certificate_number_edit);
        etPriceEdit = findViewById(R.id.et_price_edit);
        etUseTypeEdit = findViewById(R.id.et_use_type_edit);
        etPurchaseDateEdit = findViewById(R.id.et_purchase_date_edit);
        etRoomsEdit = findViewById(R.id.et_rooms_edit);
        etRemarkEdit = findViewById(R.id.et_remark_edit);

        // 按钮
        btnEdit = findViewById(R.id.btn_edit);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        btnDelete = findViewById(R.id.btn_delete);

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
        btnSave.setOnClickListener(v -> savePropertyChanges());
        btnCancel.setOnClickListener(v -> switchToDetailsMode());
        btnDelete.setOnClickListener(v -> deleteProperty());

        // 设置日期选择器监听器
        etPurchaseDateEdit.setOnClickListener(v -> showDatePickerDialog(etPurchaseDateEdit));
        etPurchaseDateEdit.setFocusable(false);
        etPurchaseDateEdit.setClickable(true);

        setupDropdowns();
    }

    private void setupDropdowns() {
        String[] propertyTypes = { "商品房", "经济适用法", "农村自建房", "限价房", "公租房", "廉租房", "小产权房", "别墅" };
        ArrayAdapter<String> adapterProperty = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                propertyTypes);
        etPropertyTypeEdit.setAdapter(adapterProperty);

        String[] useTypes = { "自住", "出租", "闲置" };
        ArrayAdapter<String> adapterUse = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                useTypes);
        etUseTypeEdit.setAdapter(adapterUse);
    }

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

    /**
     * 加载房产详情
     */
    private void loadPropertyDetails() {
        executorService.execute(() -> {
            try {
                property = apiService.getPropertyById(propertyId);

                runOnUiThread(() -> {
                    if (property != null) {
                        displayPropertyDetails();
                    } else {
                        Toast.makeText(PropertyActivity.this, "加载房产详情失败", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(PropertyActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * 显示房产详情
     */
    private void displayPropertyDetails() {
        tvId.setText(String.valueOf(property.getId()));
        tvResidentId.setText(String.valueOf(property.getResidentId()));
        tvResidentName.setText(property.getResidentName() != null ? property.getResidentName() : "");
        tvPropertyType.setText(property.getPropertyType() != null ? property.getPropertyType() : "");
        tvAddress.setText(property.getAddress() != null ? property.getAddress() : "");
        tvArea.setText(property.getArea() != null ? property.getArea().toString() : "");
        tvPropertyCertificateNumber.setText(
                property.getPropertyCertificateNumber() != null ? property.getPropertyCertificateNumber() : "");
        tvPrice.setText(property.getPrice() != null ? String.valueOf(property.getPrice()) : "");
        tvUseType.setText(property.getUseType() != null ? property.getUseType() : "");
        tvPurchaseDate.setText(property.getPurchaseDate() != null ? property.getPurchaseDate().format(dateFormatter) : "");
        tvRooms.setText(property.getRooms() != null ? String.valueOf(property.getRooms()) : "");
        tvCreateTime.setText(property.getCreateTime() != null ? property.getCreateTime().format(dateTimeFormatter) : "");
        tvUpdateTime.setText(property.getUpdateTime() != null ? property.getUpdateTime().format(dateTimeFormatter) : "");
        tvRemark.setText(property.getNotes() != null ? property.getNotes() : "");
    }

    /**
     * 切换到编辑模式
     */
    private void switchToEditMode() {
        // 填充编辑表单，添加空检查
        etResidentNameEdit.setText(property.getResidentName() != null ? property.getResidentName() : "");
        etIdCardEdit.setText(property.getIdCard() != null ? property.getIdCard() : "");
        etPropertyTypeEdit.setText(property.getPropertyType() != null ? property.getPropertyType() : "", false);
        etAddressEdit.setText(property.getAddress() != null ? property.getAddress() : "");
        etAreaEdit.setText(property.getArea() != null ? property.getArea().toString() : "");
        etPropertyCertificateNumberEdit.setText(
                property.getPropertyCertificateNumber() != null ? property.getPropertyCertificateNumber() : "");
        etPriceEdit.setText(property.getPrice() != null ? String.valueOf(property.getPrice()) : "");
        etUseTypeEdit.setText(property.getUseType() != null ? property.getUseType() : "", false);
        etPurchaseDateEdit
                .setText(property.getPurchaseDate() != null ? property.getPurchaseDate().format(dateFormatter) : "");
        etRoomsEdit.setText(property.getRooms() != null ? String.valueOf(property.getRooms()) : "");
        etRemarkEdit.setText(property.getNotes() != null ? property.getNotes() : "");

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
     * 保存房产信息更改
     */
    private void savePropertyChanges() {
        String residentName = etResidentNameEdit.getText().toString().trim();
        String propertyType = etPropertyTypeEdit.getText().toString().trim();
        String address = etAddressEdit.getText().toString().trim();
        String area = etAreaEdit.getText().toString().trim();
        String propertyCertificateNumber = etPropertyCertificateNumberEdit.getText().toString().trim();
        String priceStr = etPriceEdit.getText().toString().trim();
        String useType = etUseTypeEdit.getText().toString().trim();
        String purchaseDateStr = etPurchaseDateEdit.getText().toString().trim();
        String roomsStr = etRoomsEdit.getText().toString().trim();
        String remark = etRemarkEdit.getText().toString().trim();

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

        // 更新房产对象
        property.setResidentName(residentName);
        // 设置居民身份证号
        String idCard = etIdCardEdit.getText().toString().trim();
        property.setIdCard(idCard);
        property.setPropertyType(propertyType);
        property.setAddress(address);
        
        // 设置面积
        try {
            property.setArea(new BigDecimal(area));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "面积格式错误，请输入有效的数字", Toast.LENGTH_SHORT).show();
            return;
        }
        
        property.setPropertyCertificateNumber(propertyCertificateNumber);

        // 设置价格
        if (!priceStr.isEmpty()) {
            try {
                property.setPrice(new BigDecimal(priceStr));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "价格格式错误，请输入有效的数字", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            property.setPrice(null);
        }

        property.setUseType(useType);

        // 设置取得日期
        if (!purchaseDateStr.isEmpty()) {
            try {
                property.setPurchaseDate(java.time.LocalDate.parse(purchaseDateStr, dateFormatter));
            } catch (DateTimeParseException e) {
                Toast.makeText(this, "取得日期格式错误，请使用yyyy-MM-dd格式", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            property.setPurchaseDate(null);
        }

        // 设置房间数
        if (!roomsStr.isEmpty()) {
            property.setRooms(Integer.parseInt(roomsStr));
        } else {
            property.setRooms(null);
        }

        property.setNotes(remark);

        // 保存到服务器
        executorService.execute(() -> {
            try {
                boolean success = apiService.updateProperty(property);

                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(PropertyActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        // 通过WebSocket发送房产数据变更通知
                        if (webSocketManager.isConnected()) {
                            webSocketManager.sendMessage("{\"type\": \"data_change\", \"module\": \"property\", \"action\": \"update\"}");
                        }
                        // 刷新详情显示
                        displayPropertyDetails();
                        switchToDetailsMode();
                    } else {
                        Toast.makeText(PropertyActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(PropertyActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * 删除房产信息
     */
    private void deleteProperty() {
        // 显示确认对话框
        new android.app.AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除该房产信息吗？此操作不可恢复。")
                .setPositiveButton("删除", (dialog, which) -> {
                    executorService.execute(() -> {
                        try {
                            boolean success = apiService.deleteProperty(propertyId);
                            runOnUiThread(() -> {
                                if (success) {
                                    Toast.makeText(PropertyActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    // 通过WebSocket发送房产数据变更通知
                                    if (webSocketManager.isConnected()) {
                                        webSocketManager.sendMessage("{\"type\": \"data_change\", \"module\": \"property\", \"action\": \"delete\"}");
                                    }
                                    finish();
                                } else {
                                    Toast.makeText(PropertyActivity.this, "删除失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(PropertyActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG)
                                        .show();
                            });
                        }
                    });
                })
                .setNegativeButton("取消", null)
                .show();
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
                    .setMessage("您正在编辑房产信息，是否保存更改？")
                    .setPositiveButton("保存", (dialog, which) -> {
                        // 保存更改
                        savePropertyChanges();
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