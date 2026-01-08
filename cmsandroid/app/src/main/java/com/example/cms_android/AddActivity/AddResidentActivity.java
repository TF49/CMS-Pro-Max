package com.example.cms_android.AddActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.R;
import com.example.cms_android.auth.User;
import com.example.cms_android.model.Resident;
import com.example.cms_android.network.ApiService;
import com.example.cms_android.utils.TokenManager;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddResidentActivity extends AppCompatActivity {
    private TextInputEditText etName, etIdCard, etPhone, etAddress, etAge, etNationality, etEmail, etOccupation, etIncome, etHouseholdId, etBirthDate;
    private RadioButton rbMale, rbFemale, rbUrban, rbRural, rbHouseholderYes, rbHouseholderNo;
    private RadioGroup rgGender, rgHouseholdType, rgIsHouseholder;
    private Spinner spinnerMaritalStatus, spinnerEducation, spinnerRelationship;
    private Button btnSave;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private ExecutorService executorService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resident);
        
        // 设置Toolbar为ActionBar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 启用返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("新增居民");
        }
        
        initViews();
        initServices();
        setupListeners();
    }
    
    private void initViews() {
        etName = findViewById(R.id.et_name);
        etAge = findViewById(R.id.et_age);
        etNationality = findViewById(R.id.et_nationality);
        etIdCard = findViewById(R.id.et_id_card);
        etBirthDate = findViewById(R.id.et_birth_date);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        etAddress = findViewById(R.id.et_address);
        etOccupation = findViewById(R.id.et_occupation);
        etIncome = findViewById(R.id.et_income);
        etHouseholdId = findViewById(R.id.et_household_id);
        
        rbMale = findViewById(R.id.rb_male);
        rbFemale = findViewById(R.id.rb_female);
        rbUrban = findViewById(R.id.rb_urban);
        rbRural = findViewById(R.id.rb_rural);
        rbHouseholderYes = findViewById(R.id.rb_householder_yes);
        rbHouseholderNo = findViewById(R.id.rb_householder_no);
        
        rgGender = findViewById(R.id.rg_gender);
        rgHouseholdType = findViewById(R.id.rg_household_type);
        rgIsHouseholder = findViewById(R.id.rg_is_householder);
        
        spinnerMaritalStatus = findViewById(R.id.spinner_marital_status);
        spinnerEducation = findViewById(R.id.spinner_education);
        spinnerRelationship = findViewById(R.id.spinner_relationship);
        
        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar);
        
        // 初始化婚姻状况下拉框
        String[] maritalStatusOptions = {"未婚", "已婚", "离婚", "丧偶"};
        ArrayAdapter<String> maritalStatusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, maritalStatusOptions);
        maritalStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaritalStatus.setAdapter(maritalStatusAdapter);
        
        // 初始化教育程度下拉框
        String[] educationOptions = {"小学", "初中", "高中", "大专", "本科", "硕士", "博士"};
        ArrayAdapter<String> educationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, educationOptions);
        educationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEducation.setAdapter(educationAdapter);
        
        // 初始化与户主关系下拉框
        String[] relationshipOptions = {"配偶", "子女", "父母", "兄弟姐妹", "其他"};
        ArrayAdapter<String> relationshipAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, relationshipOptions);
        relationshipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRelationship.setAdapter(relationshipAdapter);
    }
    
    private void initServices() {
        apiService = new ApiService();
        executorService = Executors.newSingleThreadExecutor();
        tokenManager = new TokenManager(this);
    }
    
    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveResident());
    }
    
    private void saveResident() {
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String nationality = etNationality.getText().toString().trim();
        String idCard = etIdCard.getText().toString().trim();
        String birthDateStr = etBirthDate.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String occupation = etOccupation.getText().toString().trim();
        String incomeStr = etIncome.getText().toString().trim();
        String householdIdStr = etHouseholdId.getText().toString().trim();
        
        String gender = rbMale.isChecked() ? "MALE" : "FEMALE";
        String householdType = rbUrban.isChecked() ? "URBAN" : "RURAL";
        boolean isHouseholder = rbHouseholderYes.isChecked();
        
        String maritalStatus = getMaritalStatusValue(spinnerMaritalStatus.getSelectedItemPosition());
        String education = getEducationValue(spinnerEducation.getSelectedItemPosition());
        String relationship = getRelationshipValue(spinnerRelationship.getSelectedItemPosition());
        
        // 基本验证
        if (name.isEmpty() || idCard.isEmpty()) {
            Toast.makeText(this, "姓名和身份证号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!isValidIdCard(idCard)) {
            Toast.makeText(this, "身份证号格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!phone.isEmpty() && !isValidPhone(phone)) {
            Toast.makeText(this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!email.isEmpty() && !isValidEmail(email)) {
            Toast.makeText(this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 验证出生日期格式
        if (!birthDateStr.isEmpty() && !isValidDate(birthDateStr)) {
            Toast.makeText(this, "出生日期格式不正确，请使用 yyyy-MM-dd 格式", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 显示进度条
        showProgress(true);
        
        // 创建居民对象
        Resident resident = new Resident();
        resident.setName(name);
        resident.setIdCard(idCard);
        resident.setGender(gender);
        resident.setPhoneNumber(phone);
        resident.setEmail(email);
        resident.setAddress(address);
        resident.setOccupation(occupation);
        resident.setEthnicGroup(nationality.isEmpty() ? "汉族" : nationality);
        resident.setEducationLevel(education);
        resident.setMaritalStatus(maritalStatus);
        resident.setHouseholdType(householdType);
        resident.setIsHouseholder(isHouseholder);
        resident.setRelationship(isHouseholder ? "户主" : relationship);
        
        // 设置出生日期
        if (!birthDateStr.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date birthDate = sdf.parse(birthDateStr);
                resident.setBirthDate(birthDate);
            } catch (ParseException e) {
                Toast.makeText(this, "出生日期格式不正确，请使用 yyyy-MM-dd 格式", Toast.LENGTH_SHORT).show();
                showProgress(false);
                return;
            }
        }
        
        // 设置年龄
        if (!ageStr.isEmpty()) {
            try {
                resident.setAge(Integer.parseInt(ageStr));
            } catch (NumberFormatException e) {
                // 忽略无效年龄
            }
        }
        
        // 设置收入
        if (!incomeStr.isEmpty()) {
            try {
                resident.setIncome(Double.parseDouble(incomeStr));
            } catch (NumberFormatException e) {
                // 忽略无效收入
            }
        }
        
        // 设置户籍ID
        if (!householdIdStr.isEmpty()) {
            try {
                resident.setHouseholdId(Long.parseLong(householdIdStr));
            } catch (NumberFormatException e) {
                // 忽略无效户籍ID
            }
        } else {
            // 从当前登录用户获取户籍ID
            User user = tokenManager.getUserInfo();
            if (user != null && user.getHouseholdId() != null) {
                resident.setHouseholdId(user.getHouseholdId());
            }
        }
        
        // 在后台线程执行保存请求
        executorService.execute(() -> {
            try {
                boolean success = apiService.addResident(resident);
                
                // 切换到主线程更新UI
                runOnUiThread(() -> {
                    showProgress(false);
                    
                    if (success) {
                        Toast.makeText(AddResidentActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AddResidentActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(AddResidentActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
    
    /**
     * 获取婚姻状况值
     */
    private String getMaritalStatusValue(int position) {
        switch (position) {
            case 0: return "SINGLE"; // 未婚
            case 1: return "MARRIED"; // 已婚
            case 2: return "DIVORCED"; // 离婚
            case 3: return "WIDOWED"; // 丧偶
            default: return "SINGLE";
        }
    }
    
    /**
     * 获取教育程度值
     */
    private String getEducationValue(int position) {
        switch (position) {
            case 0: return "PRIMARY"; // 小学
            case 1: return "JUNIOR"; // 初中
            case 2: return "SENIOR"; // 高中
            case 3: return "COLLEGE"; // 大专
            case 4: return "BACHELOR"; // 本科
            case 5: return "MASTER"; // 硕士
            case 6: return "DOCTOR"; // 博士
            default: return "OTHER";
        }
    }
    
    /**
     * 获取与户主关系值
     */
    private String getRelationshipValue(int position) {
        switch (position) {
            case 0: return "配偶";
            case 1: return "子女";
            case 2: return "父母";
            case 3: return "兄弟姐妹";
            case 4: return "其他";
            default: return "其他";
        }
    }
    
    /**
     * 验证身份证号格式
     */
    private boolean isValidIdCard(String idCard) {
        return idCard.matches("(\\d{15}$)|(\\d{18}$)|(\\d{17}(\\d|X|x)$)");
    }
    
    /**
     * 验证手机号格式
     */
    private boolean isValidPhone(String phone) {
        return phone.matches("^1[3-9]\\d{9}$");
    }
    
    /**
     * 验证邮箱格式
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    /**
     * 验证日期格式
     */
    private boolean isValidDate(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}