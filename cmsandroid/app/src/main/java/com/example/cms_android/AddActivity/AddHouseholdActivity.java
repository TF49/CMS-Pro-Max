package com.example.cms_android.AddActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.R;
import com.example.cms_android.model.Household;
import com.example.cms_android.network.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddHouseholdActivity extends AppCompatActivity {
    private TextInputEditText etHouseholdNumber, etHouseholdHead, etHouseholderIdCard, etPhoneNumber, etPopulationCount, etRegisterAddress, etRegisterDate, etNotes;
    private Button btnSave;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_household);
        
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
        // 根据布局文件修正控件引用
        etHouseholdNumber = findViewById(R.id.et_household_number);
        etHouseholdHead = findViewById(R.id.et_household_head);
        etHouseholderIdCard = findViewById(R.id.et_householder_id_card);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etPopulationCount = findViewById(R.id.et_population_count);
        etRegisterAddress = findViewById(R.id.et_register_address);
        etRegisterDate = findViewById(R.id.et_register_date);
        etNotes = findViewById(R.id.et_notes);
        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void initServices() {
        apiService = new ApiService();
        executorService = Executors.newSingleThreadExecutor();
    }
    
    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveHousehold());
        
        // 为日期输入框设置点击事件，弹出日期选择器
        etRegisterDate.setOnClickListener(v -> showDatePickerDialog());
    }
    
    /**
     * 显示日期选择对话框
     */
    private void showDatePickerDialog() {
        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        
        // 创建日期选择器对话框
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // 格式化日期为 yyyy-MM-dd
                    String date = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    etRegisterDate.setText(date);
                },
                year, month, day);
        
        // 设置最大日期为今天
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        
        // 显示对话框
        datePickerDialog.show();
    }
    
    private void saveHousehold() {
        // 获取输入数据
        String householdNumber = etHouseholdNumber.getText().toString().trim();
        String householdHead = etHouseholdHead.getText().toString().trim();
        String householderIdCard = etHouseholderIdCard.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String populationCountStr = etPopulationCount.getText().toString().trim();
        String registerAddress = etRegisterAddress.getText().toString().trim();
        String registerDate = etRegisterDate.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        
        // 表单验证
        if (!validateForm(householdNumber, householdHead, householderIdCard, phoneNumber, registerAddress, registerDate)) {
            return;
        }
        
        showProgress(true);
        
        // 创建Household对象
        Household household = new Household();
        household.setHouseholdNumber(householdNumber);
        household.setHeadOfHouseholdName(householdHead);
        household.setHeadOfHouseholdIdCard(householderIdCard);
        household.setPhoneNumber(phoneNumber);
        household.setAddress(registerAddress);
        
        // 设置人口数量
        if (!populationCountStr.isEmpty()) {
            try {
                int populationCount = Integer.parseInt(populationCountStr);
                household.setPopulationCount(populationCount);
            } catch (NumberFormatException e) {
                // 如果转换失败，使用默认值1
                household.setPopulationCount(1);
            }
        } else {
            household.setPopulationCount(1);
        }
        
        // 设置登记日期
        if (!registerDate.isEmpty()) {
            try {
                // 解析日期字符串为Date对象
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = sdf.parse(registerDate);
                household.setRegistrationDate(date);
            } catch (ParseException e) {
                Toast.makeText(this, "日期格式不正确，请使用 yyyy-MM-dd 格式", Toast.LENGTH_SHORT).show();
                showProgress(false);
                etRegisterDate.requestFocus();
                return;
            }
        }
        
        // 设置备注
        if (!notes.isEmpty()) {
            household.setNotes(notes);
        }
        
        // 在后台线程执行保存请求
        executorService.execute(() -> {
            try {
                boolean success = apiService.addHousehold(household);
                
                // 切换到主线程更新UI
                runOnUiThread(() -> {
                    showProgress(false);
                    
                    if (success) {
                        Toast.makeText(AddHouseholdActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AddHouseholdActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(AddHouseholdActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
    
    /**
     * 表单验证
     */
    private boolean validateForm(String householdNumber, String householdHead, String householderIdCard, 
                               String phoneNumber, String registerAddress, String registerDate) {
        // 验证户籍编号
        if (householdNumber.isEmpty()) {
            Toast.makeText(this, "请输入户籍编号", Toast.LENGTH_SHORT).show();
            etHouseholdNumber.requestFocus();
            return false;
        }
        
        // 验证户主姓名
        if (householdHead.isEmpty()) {
            Toast.makeText(this, "请输入户主姓名", Toast.LENGTH_SHORT).show();
            etHouseholdHead.requestFocus();
            return false;
        } else if (householdHead.length() < 2 || householdHead.length() > 20) {
            Toast.makeText(this, "户主姓名长度应在2-20个字符之间", Toast.LENGTH_SHORT).show();
            etHouseholdHead.requestFocus();
            return false;
        }
        
        // 验证身份证号
        if (householderIdCard.isEmpty()) {
            Toast.makeText(this, "请输入身份证号", Toast.LENGTH_SHORT).show();
            etHouseholderIdCard.requestFocus();
            return false;
        } else if (!isValidIdCard(householderIdCard)) {
            Toast.makeText(this, "身份证号格式不正确", Toast.LENGTH_SHORT).show();
            etHouseholderIdCard.requestFocus();
            return false;
        }
        
        // 验证地址
        if (registerAddress.isEmpty()) {
            Toast.makeText(this, "请输入详细地址", Toast.LENGTH_SHORT).show();
            etRegisterAddress.requestFocus();
            return false;
        } else if (registerAddress.length() < 5 || registerAddress.length() > 100) {
            Toast.makeText(this, "地址长度应在5-100个字符之间", Toast.LENGTH_SHORT).show();
            etRegisterAddress.requestFocus();
            return false;
        }
        
        // 验证联系电话
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "请输入联系电话", Toast.LENGTH_SHORT).show();
            etPhoneNumber.requestFocus();
            return false;
        } else if (!isValidPhone(phoneNumber)) {
            Toast.makeText(this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
            etPhoneNumber.requestFocus();
            return false;
        }
        
        // 验证登记日期
        if (registerDate.isEmpty()) {
            Toast.makeText(this, "请选择登记日期", Toast.LENGTH_SHORT).show();
            etRegisterDate.requestFocus();
            return false;
        } else if (!isValidDate(registerDate)) {
            Toast.makeText(this, "日期格式不正确", Toast.LENGTH_SHORT).show();
            etRegisterDate.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * 验证手机号格式
     */
    private boolean isValidPhone(String phone) {
        return phone.matches("^1[3-9]\\d{9}$");
    }
    
    /**
     * 验证身份证号格式
     */
    private boolean isValidIdCard(String idCard) {
        return idCard.matches("(\\d{15}$)|(\\d{18}$)|(\\d{17}(\\d|X|x)$)");
    }
    
    /**
     * 验证日期格式
     */
    private boolean isValidDate(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
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