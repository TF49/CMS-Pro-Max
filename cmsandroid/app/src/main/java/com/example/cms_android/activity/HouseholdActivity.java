package com.example.cms_android.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.R;
import com.example.cms_android.model.Household;
import com.example.cms_android.network.ApiService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HouseholdActivity extends AppCompatActivity {
    private static final String EXTRA_HOUSEHOLD_ID = "household_id";

    private TextView tvHouseholdNumber, tvHouseholdHead, tvRegisterAddress, tvRegisterDate, tvResidentId,
            tvHouseholdStatus;
    private TextView tvPopulationCount, tvPhoneNumber, tvNotes;
    private View layoutNotes;
    private EditText etHouseholdNumberEdit, etHouseholdHeadEdit, etRegisterAddressEdit;
    private EditText etHouseholderIdCardEdit, etPhoneNumberEdit, etPopulationCountEdit, etRegisterDateEdit, etNotesEdit;
    private AutoCompleteTextView etHouseholdTypeEdit;
    private Button btnEdit, btnSave, btnCancel, btnDelete;
    private View viewDetails, viewEdit;

    private ApiService apiService;
    private ExecutorService executorService;
    private Long householdId;
    private Household household;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household);

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

        // 获取户籍ID
        householdId = getIntent().getLongExtra(EXTRA_HOUSEHOLD_ID, -1);
        if (householdId == -1) {
            Toast.makeText(this, "无效的户籍ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 加载户籍详情
        loadHouseholdDetails();
    }

    private void initViews() {
        // 详情视图
        viewDetails = findViewById(R.id.view_details);
        tvHouseholdNumber = findViewById(R.id.tv_household_number);
        tvHouseholdHead = findViewById(R.id.tv_household_head);
        tvRegisterAddress = findViewById(R.id.tv_register_address);
        tvRegisterDate = findViewById(R.id.tv_register_date);
        tvResidentId = findViewById(R.id.tv_resident_id);
        tvHouseholdStatus = findViewById(R.id.tv_household_status);
        tvPopulationCount = findViewById(R.id.tv_population_count);
        tvPhoneNumber = findViewById(R.id.tv_phone_number);
        tvNotes = findViewById(R.id.tv_notes);
        layoutNotes = findViewById(R.id.layout_notes);

        // 编辑视图
        viewEdit = findViewById(R.id.view_edit);
        etHouseholdNumberEdit = findViewById(R.id.et_household_number_edit);
        etHouseholdHeadEdit = findViewById(R.id.et_household_head_edit);
        etRegisterAddressEdit = findViewById(R.id.et_register_address_edit);
        etHouseholderIdCardEdit = findViewById(R.id.et_householder_id_card_edit);
        etPhoneNumberEdit = findViewById(R.id.et_phone_number_edit);
        etPopulationCountEdit = findViewById(R.id.et_population_count_edit);
        etRegisterDateEdit = findViewById(R.id.et_register_date_edit);
        etNotesEdit = findViewById(R.id.et_notes_edit);
        etHouseholdTypeEdit = findViewById(R.id.et_household_type_edit);

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
    }

    private void setupListeners() {
        btnEdit.setOnClickListener(v -> switchToEditMode());
        btnSave.setOnClickListener(v -> saveHouseholdChanges());
        btnCancel.setOnClickListener(v -> switchToDetailsMode());
        btnDelete.setOnClickListener(v -> deleteHousehold());

        // 设置日期选择器监听器
        etRegisterDateEdit.setOnClickListener(v -> showDatePickerDialog(etRegisterDateEdit));
        etRegisterDateEdit.setFocusable(false);
        etRegisterDateEdit.setClickable(true);

        setupDropdowns();
    }

    private void setupDropdowns() {
        String[] householdTypes = { "城镇", "农村" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                householdTypes);
        etHouseholdTypeEdit.setAdapter(adapter);
    }

    /**
     * 加载户籍详情
     */
    private void loadHouseholdDetails() {
        executorService.execute(() -> {
            try {
                // 这里需要根据实际的API调整，假设ApiService中有getHouseholdById方法
                household = apiService.getHouseholdById(householdId);

                runOnUiThread(() -> {
                    if (household != null) {
                        displayHouseholdDetails();
                    } else {
                        Toast.makeText(HouseholdActivity.this, "加载户籍详情失败", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(HouseholdActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * 显示户籍详情
     */
    private void displayHouseholdDetails() {
        // 户籍号码
        tvHouseholdNumber.setText(household.getHouseholdNumber() != null ? household.getHouseholdNumber() : "未填写");

        // 户主姓名
        tvHouseholdHead
                .setText(household.getHeadOfHouseholdName() != null ? household.getHeadOfHouseholdName() : "未填写");

        // 详细地址
        tvRegisterAddress.setText(household.getAddress() != null ? household.getAddress() : "未填写");

        // 人口数量
        Integer populationCount = household.getPopulationCount();
        tvPopulationCount.setText(populationCount != null ? populationCount + "人" : "0人");

        // 联系电话
        tvPhoneNumber.setText(household.getPhoneNumber() != null ? household.getPhoneNumber() : "未填写");

        // 登记日期
        Date registrationDate = household.getRegistrationDate();
        Date createTime = household.getCreateTime();
        Date displayDate = registrationDate != null ? registrationDate : createTime;
        String dateStr;
        if (displayDate != null) {
            dateStr = dateTimeFormat.format(displayDate);
        } else {
            dateStr = "未知";
        }
        tvRegisterDate.setText(dateStr);

        // 户主身份证号
        tvResidentId
                .setText(household.getHeadOfHouseholdIdCard() != null ? household.getHeadOfHouseholdIdCard() : "未填写");

        // 状态
        tvHouseholdStatus.setText(household.getStatus() != null ? household.getStatus() : "正常");

        // 备注
        String notes = household.getNotes();
        if (notes != null && !notes.isEmpty()) {
            tvNotes.setText(notes);
            layoutNotes.setVisibility(View.VISIBLE);
        } else {
            layoutNotes.setVisibility(View.GONE);
        }
    }

    /**
     * 切换到编辑模式
     */
    private void switchToEditMode() {
        // 填充编辑表单
        etHouseholdNumberEdit.setText(household.getHouseholdNumber());
        etHouseholdHeadEdit.setText(household.getHeadOfHouseholdName());
        etRegisterAddressEdit.setText(household.getAddress());
        etHouseholderIdCardEdit.setText(household.getHeadOfHouseholdIdCard());
        etPhoneNumberEdit.setText(household.getPhoneNumber());
        etPopulationCountEdit
                .setText(household.getPopulationCount() != null ? String.valueOf(household.getPopulationCount()) : "");
        etRegisterDateEdit.setText(
                household.getRegistrationDate() != null ? dateFormat.format(household.getRegistrationDate()) : "");
        etNotesEdit.setText(household.getNotes());
        etHouseholdTypeEdit.setText(
                household.getHouseholdType() != null ? ("URBAN".equals(household.getHouseholdType()) ? "城镇" : "农村")
                        : "城镇",
                false);

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
     * 保存户籍信息更改
     */
    private void saveHouseholdChanges() {
        String householdNumber = etHouseholdNumberEdit.getText().toString().trim();
        String householdHead = etHouseholdHeadEdit.getText().toString().trim();
        String registerAddress = etRegisterAddressEdit.getText().toString().trim();
        String householderIdCard = etHouseholderIdCardEdit.getText().toString().trim();
        String phoneNumber = etPhoneNumberEdit.getText().toString().trim();
        String populationCountStr = etPopulationCountEdit.getText().toString().trim();
        String registerDateStr = etRegisterDateEdit.getText().toString().trim();
        String notes = etNotesEdit.getText().toString().trim();
        String householdType = etHouseholdTypeEdit.getText().toString().trim();

        if (householdNumber.isEmpty() || householdHead.isEmpty() || registerAddress.isEmpty()) {
            Toast.makeText(this, "户籍编号、户主姓名和登记地址不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 更新户籍对象
        household.setHouseholdNumber(householdNumber);
        household.setHeadOfHouseholdName(householdHead);
        household.setAddress(registerAddress);
        household.setHeadOfHouseholdIdCard(householderIdCard);
        household.setPhoneNumber(phoneNumber);

        // 处理人口数量，转换为Integer类型
        if (!populationCountStr.isEmpty()) {
            try {
                Integer populationCount = Integer.parseInt(populationCountStr);
                household.setPopulationCount(populationCount);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "人口数量必须是数字", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            household.setPopulationCount(null);
        }

        // 处理建档日期
        if (!registerDateStr.isEmpty()) {
            try {
                household.setRegistrationDate(dateFormat.parse(registerDateStr));
            } catch (Exception e) {
                Toast.makeText(this, "建档日期格式错误", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            household.setRegistrationDate(null);
        }

        household.setNotes(notes);
        household.setHouseholdType("城镇".equals(householdType) ? "URBAN" : "RURAL");

        // 保存到服务器
        executorService.execute(() -> {
            try {
                // 这里需要根据实际的API调整，假设ApiService中有updateHousehold方法
                boolean success = apiService.updateHousehold(household);

                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(HouseholdActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        // 刷新详情显示
                        displayHouseholdDetails();
                        switchToDetailsMode();
                    } else {
                        Toast.makeText(HouseholdActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(HouseholdActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
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
    public void onBackPressed() {
        // 如果当前是编辑模式，提示用户是否保存更改
        if (viewEdit.getVisibility() == View.VISIBLE) {
            // 显示确认对话框
            new android.app.AlertDialog.Builder(this)
                    .setTitle("确认返回")
                    .setMessage("您正在编辑户籍信息，是否保存更改？")
                    .setPositiveButton("保存", (dialog, which) -> {
                        // 保存更改
                        saveHouseholdChanges();
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

    /**
     * 删除户籍信息
     */
    private void deleteHousehold() {
        // 显示确认对话框
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("确认删除")
                .setMessage("确定要删除该户籍信息吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    // 执行删除操作
                    executorService.execute(() -> {
                        try {
                            boolean success = apiService.deleteHousehold(householdId);
                            runOnUiThread(() -> {
                                if (success) {
                                    Toast.makeText(HouseholdActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    // 返回上一页
                                    finish();
                                } else {
                                    Toast.makeText(HouseholdActivity.this, "删除失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(HouseholdActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG)
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
                calendar.setTime(dateFormat.parse(dateStr));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    editText.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}