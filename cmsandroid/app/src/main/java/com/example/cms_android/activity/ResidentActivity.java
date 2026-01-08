package com.example.cms_android.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.TimeZone;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.R;
import com.example.cms_android.model.Resident;
import com.example.cms_android.network.ApiService;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResidentActivity extends AppCompatActivity {
    private static final String EXTRA_RESIDENT_ID = "resident_id";

    private TextView tvName, tvIdCard, tvGender, tvPhone, tvNotes;
    private TextView tvAge, tvBirthDate, tvNationality, tvMaritalStatus, tvIsHouseholder;
    private TextView tvEmail, tvAddress;
    private TextView tvHouseholdId, tvHouseholdType, tvRelationship, tvEducation, tvOccupation, tvIncome;
    private TextView tvRegisterDate, tvUpdateDate;
    private EditText etNameEdit, etIdCardEdit, etBirthDateEdit, etPhoneEdit, etNotesEdit;
    private EditText etEmailEdit, etAddressEdit;
    private AutoCompleteTextView etMaritalStatusEdit, etHouseholdTypeEdit, etEducationEdit, etRelationshipEdit;
    private RadioGroup rgGenderEdit;
    private RadioButton rbMaleEdit, rbFemaleEdit;
    private Button btnEdit, btnSave, btnCancel, btnDelete;
    private View viewDetails, viewEdit;

    private ApiService apiService;
    private ExecutorService executorService;
    private Long residentId;
    private Resident resident;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident);

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

        // 获取居民ID
        residentId = getIntent().getLongExtra(EXTRA_RESIDENT_ID, -1);
        if (residentId == -1) {
            Toast.makeText(this, "无效的居民ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 加载居民详情
        loadResidentDetails();
    }

    private void initViews() {
        // 详情视图
        viewDetails = findViewById(R.id.view_details);
        tvName = findViewById(R.id.tv_name);
        tvIdCard = findViewById(R.id.tv_id_card);
        tvGender = findViewById(R.id.tv_gender);
        tvPhone = findViewById(R.id.tv_phone);
        tvNotes = findViewById(R.id.tv_notes);

        // 新增基本信息字段
        tvAge = findViewById(R.id.tv_age);
        tvBirthDate = findViewById(R.id.tv_birth_date);
        tvNationality = findViewById(R.id.tv_nationality);
        tvMaritalStatus = findViewById(R.id.tv_marital_status);
        tvIsHouseholder = findViewById(R.id.tv_is_householder);

        // 新增联系方式字段
        tvEmail = findViewById(R.id.tv_email);
        tvAddress = findViewById(R.id.tv_address);

        // 新增其他信息字段
        tvHouseholdId = findViewById(R.id.tv_household_id);
        tvHouseholdType = findViewById(R.id.tv_household_type);
        tvRelationship = findViewById(R.id.tv_relationship);
        tvEducation = findViewById(R.id.tv_education);
        tvOccupation = findViewById(R.id.tv_occupation);
        tvIncome = findViewById(R.id.tv_income);
        tvRegisterDate = findViewById(R.id.tv_register_date);
        tvUpdateDate = findViewById(R.id.tv_update_date);

        // 编辑视图
        viewEdit = findViewById(R.id.view_edit);
        etNameEdit = findViewById(R.id.et_name_edit);
        etIdCardEdit = findViewById(R.id.et_id_card_edit);
        etBirthDateEdit = findViewById(R.id.et_birth_date_edit);
        etPhoneEdit = findViewById(R.id.et_phone_edit);
        etNotesEdit = findViewById(R.id.et_notes_edit);
        etEmailEdit = findViewById(R.id.et_email_edit);
        etAddressEdit = findViewById(R.id.et_address_edit);
        rgGenderEdit = findViewById(R.id.rg_gender_edit);
        rbMaleEdit = findViewById(R.id.rb_male_edit);
        rbFemaleEdit = findViewById(R.id.rb_female_edit);

        etMaritalStatusEdit = findViewById(R.id.et_marital_status_edit);
        etHouseholdTypeEdit = findViewById(R.id.et_household_type_edit);
        etEducationEdit = findViewById(R.id.et_education_edit);
        etRelationshipEdit = findViewById(R.id.et_relationship_edit);

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
        btnSave.setOnClickListener(v -> saveResidentChanges());
        btnCancel.setOnClickListener(v -> switchToDetailsMode());
        btnDelete.setOnClickListener(v -> deleteResident());

        // 设置日期选择器监听器
        etBirthDateEdit.setOnClickListener(v -> showDatePickerDialog(etBirthDateEdit));
        etBirthDateEdit.setFocusable(false);
        etBirthDateEdit.setClickable(true);

        setupDropdowns();
    }

    private void setupDropdowns() {
        String[] maritalStatuses = { "未婚", "已婚", "离婚", "丧偶" };
        ArrayAdapter<String> maritalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                maritalStatuses);
        etMaritalStatusEdit.setAdapter(maritalAdapter);

        String[] householdTypes = { "城镇", "农村" };
        ArrayAdapter<String> householdAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                householdTypes);
        etHouseholdTypeEdit.setAdapter(householdAdapter);

        String[] educationLevels = { "小学", "初中", "高中", "中专", "大专", "本科", "硕士", "博士" };
        ArrayAdapter<String> educationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                educationLevels);
        etEducationEdit.setAdapter(educationAdapter);

        String[] relationships = { "本人", "配偶", "子女", "父母", "孙子女", "其他" };
        ArrayAdapter<String> relationshipAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                relationships);
        etRelationshipEdit.setAdapter(relationshipAdapter);
    }

    /**
     * 加载居民详情
     */
    private void loadResidentDetails() {

        executorService.execute(() -> {
            try {
                resident = apiService.getResidentById(residentId);

                runOnUiThread(() -> {
                    if (resident != null) {

                        displayResidentDetails();
                    } else {

                        Toast.makeText(ResidentActivity.this, "加载居民详情失败: resident对象为null", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {

                    Toast.makeText(ResidentActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * 显示居民详情
     */
    private void displayResidentDetails() {
        // 添加调试日志，打印居民对象的关键信息

        tvName.setText(resident.getName() != null ? resident.getName() : "未填写");
        tvIdCard.setText(resident.getIdCard() != null ? resident.getIdCard() : "未填写");
        // 性别显示，统一使用gender字段，确保与网页端一致
        // 逻辑：显式检查各种情况，确保所有性别值都被正确处理
        tvGender.setText(
                resident.getGender() != null
                        ? ("MALE".equals(resident.getGender()) || "男".equals(resident.getGender()) ? "男"
                                : ("FEMALE".equals(resident.getGender()) || "女".equals(resident.getGender()) ? "女"
                                        : "未填写"))
                        : "未填写");
        tvPhone.setText(resident.getPhoneNumber() != null ? resident.getPhoneNumber() : "未填写");
        // 备注显示，与网页端保持一致，空值显示为"无"
        tvNotes.setText(resident.getNotes() != null ? resident.getNotes() : "无");

        // 显示新增基本信息字段
        // 计算年龄：优先使用resident.getAge()，如果为null则根据出生日期计算
        Integer age = resident.getAge();
        if (age == null && resident.getBirthDate() != null) {
            age = calculateAge(resident.getBirthDate());
        }
        tvAge.setText(age != null ? age + "岁" : "未填写");
        // 格式化出生日期，确保与网页端一致
        if (resident.getBirthDate() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+8"));
            tvBirthDate.setText(sdf.format(resident.getBirthDate()));
        } else {
            tvBirthDate.setText("未填写");
        }
        tvNationality.setText(resident.getEthnicGroup() != null ? resident.getEthnicGroup() : "未填写");
        // 婚姻状况显示，确保与网页端一致
        tvMaritalStatus.setText(
                resident.getMaritalStatus() != null ? formatMaritalStatus(resident.getMaritalStatus()) : "未填写");
        tvIsHouseholder
                .setText(resident.getIsHouseholder() != null ? (resident.getIsHouseholder() ? "是" : "否") : "未填写");

        // 显示新增联系方式字段
        tvEmail.setText(resident.getEmail() != null ? resident.getEmail() : "未填写");
        tvAddress.setText(resident.getAddress() != null ? resident.getAddress() : "未填写");

        // 显示新增其他信息字段
        tvHouseholdId.setText(resident.getHouseholdId() != null ? resident.getHouseholdId().toString() : "未填写");
        tvHouseholdType.setText(
                resident.getHouseholdType() != null ? ("URBAN".equals(resident.getHouseholdType()) ? "城镇" : "农村")
                        : "未填写");
        tvRelationship.setText(resident.getRelationship() != null ? resident.getRelationship() : "未填写");
        // 教育程度显示，使用educationLevel字段，确保与网页端一致
        tvEducation.setText(resident.getEducationLevel() != null ? formatEducation(resident.getEducationLevel())
                : (resident.getEducation() != null ? formatEducation(resident.getEducation()) : "未填写"));
        tvOccupation.setText(resident.getOccupation() != null ? resident.getOccupation() : "未填写");
        tvIncome.setText(resident.getIncome() != null ? "¥" + resident.getIncome() : "未填写");

        // 格式化登记日期，优先使用createTime字段，确保与网页端一致
        Date displayRegisterDate = resident.getRegisterDate() != null ? resident.getRegisterDate()
                : resident.getCreateTime();
        if (displayRegisterDate != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            tvRegisterDate.setText(sdf.format(displayRegisterDate));
        } else {
            tvRegisterDate.setText("未填写");
        }

        // 格式化更新日期，优先使用updateTime字段，确保与网页端一致
        Date displayUpdateDate = resident.getUpdateDate() != null ? resident.getUpdateDate() : resident.getUpdateTime();
        if (displayUpdateDate != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            tvUpdateDate.setText(sdf.format(displayUpdateDate));
        } else {
            tvUpdateDate.setText("未填写");
        }
    }

    /**
     * 切换到编辑模式
     */
    private void switchToEditMode() {
        // 填充编辑表单，添加空检查
        etNameEdit.setText(resident.getName() != null ? resident.getName() : "");
        etIdCardEdit.setText(resident.getIdCard() != null ? resident.getIdCard() : "");
        etBirthDateEdit.setText(resident.getBirthDate() != null ? dateFormat.format(resident.getBirthDate()) : "");
        etPhoneEdit.setText(resident.getPhoneNumber() != null ? resident.getPhoneNumber() : "");
        etNotesEdit.setText(resident.getNotes() != null ? resident.getNotes() : "");
        etEmailEdit.setText(resident.getEmail() != null ? resident.getEmail() : "");
        etAddressEdit.setText(resident.getAddress() != null ? resident.getAddress() : "");

        // 设置性别单选按钮
        if ("MALE".equals(resident.getGender()) || "男".equals(resident.getGender())) {
            rbMaleEdit.setChecked(true);
        } else if ("FEMALE".equals(resident.getGender()) || "女".equals(resident.getGender())) {
            rbFemaleEdit.setChecked(true);
        }

        // 设置下拉框初始值
        etMaritalStatusEdit.setText(formatMaritalStatus(resident.getMaritalStatus()), false);
        etHouseholdTypeEdit.setText(
                resident.getHouseholdType() != null ? ("URBAN".equals(resident.getHouseholdType()) ? "城镇" : "农村")
                        : "城镇",
                false);
        etEducationEdit.setText(
                resident.getEducationLevel() != null ? formatEducation(resident.getEducationLevel()) : "", false);
        etRelationshipEdit.setText(resident.getRelationship() != null ? resident.getRelationship() : "本人", false);

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
     * 保存居民信息更改
     */
    private void saveResidentChanges() {
        String name = etNameEdit.getText().toString().trim();
        String idCard = etIdCardEdit.getText().toString().trim();
        String birthDateStr = etBirthDateEdit.getText().toString().trim();
        String phone = etPhoneEdit.getText().toString().trim();
        String notes = etNotesEdit.getText().toString().trim();
        String email = etEmailEdit.getText().toString().trim();
        String address = etAddressEdit.getText().toString().trim();
        String gender = rbMaleEdit.isChecked() ? "MALE" : "FEMALE";

        if (name.isEmpty() || idCard.isEmpty()) {
            Toast.makeText(this, "姓名和身份证号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 更新居民对象
        resident.setName(name);
        resident.setIdCard(idCard);

        // 处理出生日期
        if (!birthDateStr.isEmpty()) {
            try {
                resident.setBirthDate(dateFormat.parse(birthDateStr));
            } catch (Exception e) {
                Toast.makeText(this, "出生日期格式错误", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            resident.setBirthDate(null);
        }

        resident.setPhoneNumber(phone);
        resident.setNotes(notes);
        resident.setGender(gender);
        resident.setEmail(email);
        resident.setAddress(address);

        // 获取下拉框的值并转换回枚举
        String maritalStatusStr = etMaritalStatusEdit.getText().toString();
        String maritalEnum = "SINGLE";
        switch (maritalStatusStr) {
            case "未婚":
                maritalEnum = "SINGLE";
                break;
            case "已婚":
                maritalEnum = "MARRIED";
                break;
            case "离婚":
                maritalEnum = "DIVORCED";
                break;
            case "丧偶":
                maritalEnum = "WIDOWED";
                break;
        }
        resident.setMaritalStatus(maritalEnum);

        String householdTypeStr = etHouseholdTypeEdit.getText().toString();
        resident.setHouseholdType("城镇".equals(householdTypeStr) ? "URBAN" : "RURAL");

        String educationStr = etEducationEdit.getText().toString();
        String educationEnum = educationStr;
        switch (educationStr) {
            case "小学":
                educationEnum = "PRIMARY";
                break;
            case "初中":
                educationEnum = "JUNIOR";
                break;
            case "高中":
                educationEnum = "SENIOR";
                break;
            case "中专":
                educationEnum = "VOCATIONAL";
                break;
            case "大专":
                educationEnum = "COLLEGE";
                break;
            case "本科":
                educationEnum = "BACHELOR";
                break;
            case "硕士":
                educationEnum = "MASTER";
                break;
            case "博士":
                educationEnum = "DOCTOR";
                break;
        }
        resident.setEducationLevel(educationEnum);

        String relationshipStr = etRelationshipEdit.getText().toString();
        resident.setRelationship(relationshipStr);

        // 保存到服务器
        executorService.execute(() -> {
            try {
                boolean success = apiService.updateResident(resident);

                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(ResidentActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        // 刷新详情显示
                        displayResidentDetails();
                        switchToDetailsMode();
                    } else {
                        Toast.makeText(ResidentActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(ResidentActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
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
                    .setMessage("您正在编辑居民信息，是否保存更改？")
                    .setPositiveButton("保存", (dialog, which) -> {
                        // 保存更改
                        saveResidentChanges();
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
     * 删除居民信息
     */
    private void deleteResident() {
        // 显示确认对话框
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("确认删除")
                .setMessage("确定要删除该居民信息吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    // 执行删除操作
                    executorService.execute(() -> {
                        try {
                            boolean success = apiService.deleteResident(residentId);
                            runOnUiThread(() -> {
                                if (success) {
                                    Toast.makeText(ResidentActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    // 返回上一页
                                    finish();
                                } else {
                                    Toast.makeText(ResidentActivity.this, "删除失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(ResidentActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG)
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

    /**
     * 格式化婚姻状况
     */
    private String formatMaritalStatus(String maritalStatus) {
        if (maritalStatus == null)
            return "未填写";
        switch (maritalStatus) {
            case "SINGLE":
                return "未婚";
            case "MARRIED":
                return "已婚";
            case "DIVORCED":
                return "离婚";
            case "WIDOWED":
                return "丧偶";
            default:
                return maritalStatus;
        }
    }

    /**
     * 格式化教育程度
     */
    private String formatEducation(String education) {
        if (education == null)
            return "未填写";
        switch (education) {
            case "PRIMARY":
                return "小学";
            case "JUNIOR":
                return "初中";
            case "SENIOR":
                return "高中";
            case "VOCATIONAL":
                return "中专";
            case "COLLEGE":
                return "大专";
            case "BACHELOR":
                return "本科";
            case "MASTER":
                return "硕士";
            case "DOCTOR":
                return "博士";
            default:
                return education;
        }
    }

    /**
     * 根据出生日期计算年龄
     * 
     * @param birthDate 出生日期
     * @return 年龄
     */
    private Integer calculateAge(Date birthDate) {
        if (birthDate == null) {
            return null;
        }

        Calendar currentDate = Calendar.getInstance();
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthDate);

        int age = currentDate.get(Calendar.YEAR) - birth.get(Calendar.YEAR);

        // 如果当前月份小于出生月份，或者月份相同但当前日期小于出生日期，年龄减1
        if (currentDate.get(Calendar.MONTH) < birth.get(Calendar.MONTH) ||
                (currentDate.get(Calendar.MONTH) == birth.get(Calendar.MONTH) &&
                        currentDate.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        // 年龄不能为负数
        return Math.max(0, age);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}