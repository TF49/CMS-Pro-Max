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
import com.example.cms_android.model.SocialSecurity;
import com.example.cms_android.network.ApiService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.DatePickerDialog;

public class AddSocialSecurityActivity extends AppCompatActivity {
    private TextInputEditText etPaymentAmount, etStartDate, etEndDate;
    private TextInputEditText etResidentName, etIdCard, etNotes;
    private MaterialAutoCompleteTextView etInsuranceStatus, etPensionInsurance, etMedicalInsurance;
    private MaterialAutoCompleteTextView etUnemploymentInsurance, etWorkInjuryInsurance, etMaternityInsurance;
    private Button btnSave;
    private ProgressBar progressBar;

    private ApiService apiService;
    private ExecutorService executorService;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_socialsecurity);
        
        initViews();
        initServices();
        setupListeners();
    }

    private void initViews() {
        etPaymentAmount = findViewById(R.id.et_payment_amount);
        etResidentName = findViewById(R.id.et_resident_name);
        etIdCard = findViewById(R.id.et_id_card);
        etStartDate = findViewById(R.id.et_start_date);
        etEndDate = findViewById(R.id.et_end_date);
        etNotes = findViewById(R.id.et_notes);
        etPensionInsurance = findViewById(R.id.et_pension_insurance);
        etMedicalInsurance = findViewById(R.id.et_medical_insurance);
        etUnemploymentInsurance = findViewById(R.id.et_unemployment_insurance);
        etWorkInjuryInsurance = findViewById(R.id.et_work_injury_insurance);
        etMaternityInsurance = findViewById(R.id.et_maternity_insurance);
        etInsuranceStatus = findViewById(R.id.et_insurance_status);
        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void initServices() {
        apiService = new ApiService();
        executorService = Executors.newSingleThreadExecutor();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveSocialSecurity());
        
        // 设置参保状态下拉选项
        String[] statuses = {"正常参保", "暂停参保", "停止参保"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, statuses);
        etInsuranceStatus.setAdapter(statusAdapter);
        // 允许点击显示下拉菜单
        etInsuranceStatus.setThreshold(Integer.MAX_VALUE);
        etInsuranceStatus.setOnClickListener(v -> etInsuranceStatus.showDropDown());
        etInsuranceStatus.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etInsuranceStatus.showDropDown();
            }
        });
        
        // 设置养老保险下拉选项
        String[] pensionOptions = {"城镇职工养老保险", "城乡居民养老保险"};
        ArrayAdapter<String> pensionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pensionOptions);
        etPensionInsurance.setAdapter(pensionAdapter);
        // 允许点击显示下拉菜单
        etPensionInsurance.setThreshold(Integer.MAX_VALUE);
        etPensionInsurance.setOnClickListener(v -> etPensionInsurance.showDropDown());
        etPensionInsurance.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etPensionInsurance.showDropDown();
            }
        });
        
        // 设置医疗保险下拉选项
        String[] medicalOptions = {"城镇职工医疗保险", "城乡居民医疗保险"};
        ArrayAdapter<String> medicalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, medicalOptions);
        etMedicalInsurance.setAdapter(medicalAdapter);
        // 允许点击显示下拉菜单
        etMedicalInsurance.setThreshold(Integer.MAX_VALUE);
        etMedicalInsurance.setOnClickListener(v -> etMedicalInsurance.showDropDown());
        etMedicalInsurance.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etMedicalInsurance.showDropDown();
            }
        });
        
        // 设置失业保险下拉选项
        String[] unemploymentOptions = {"失业保险", "无失业保险"};
        ArrayAdapter<String> unemploymentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, unemploymentOptions);
        etUnemploymentInsurance.setAdapter(unemploymentAdapter);
        // 允许点击显示下拉菜单
        etUnemploymentInsurance.setThreshold(Integer.MAX_VALUE);
        etUnemploymentInsurance.setOnClickListener(v -> etUnemploymentInsurance.showDropDown());
        etUnemploymentInsurance.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etUnemploymentInsurance.showDropDown();
            }
        });
        
        // 设置工伤保险下拉选项
        String[] workInjuryOptions = {"工伤保险", "无工伤保险"};
        ArrayAdapter<String> workInjuryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, workInjuryOptions);
        etWorkInjuryInsurance.setAdapter(workInjuryAdapter);
        // 允许点击显示下拉菜单
        etWorkInjuryInsurance.setThreshold(Integer.MAX_VALUE);
        etWorkInjuryInsurance.setOnClickListener(v -> etWorkInjuryInsurance.showDropDown());
        etWorkInjuryInsurance.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etWorkInjuryInsurance.showDropDown();
            }
        });
        
        // 设置生育保险下拉选项
        String[] maternityOptions = {"生育保险", "无生育保险"};
        ArrayAdapter<String> maternityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, maternityOptions);
        etMaternityInsurance.setAdapter(maternityAdapter);
        // 允许点击显示下拉菜单
        etMaternityInsurance.setThreshold(Integer.MAX_VALUE);
        etMaternityInsurance.setOnClickListener(v -> etMaternityInsurance.showDropDown());
        etMaternityInsurance.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etMaternityInsurance.showDropDown();
            }
        });
        
        // 设置日期选择器监听器
        etStartDate.setOnClickListener(v -> showDatePickerDialog(etStartDate));
        etStartDate.setFocusable(false);
        etStartDate.setClickable(true);
        
        etEndDate.setOnClickListener(v -> showDatePickerDialog(etEndDate));
        etEndDate.setFocusable(false);
        etEndDate.setClickable(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveSocialSecurity() {
        String residentName = etResidentName.getText().toString().trim();
        String idCard = etIdCard.getText().toString().trim();
        String paymentAmountStr = etPaymentAmount.getText().toString().trim();
        String startDateStr = etStartDate.getText().toString().trim();
        String endDateStr = etEndDate.getText().toString().trim();
        String insuranceStatus = etInsuranceStatus.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String pensionInsurance = etPensionInsurance.getText().toString().trim();
        String medicalInsurance = etMedicalInsurance.getText().toString().trim();
        String unemploymentInsurance = etUnemploymentInsurance.getText().toString().trim();
        String workInjuryInsurance = etWorkInjuryInsurance.getText().toString().trim();
        String maternityInsurance = etMaternityInsurance.getText().toString().trim();

        if (residentName.isEmpty() || idCard.isEmpty() || paymentAmountStr.isEmpty() || startDateStr.isEmpty() || 
            pensionInsurance.isEmpty() || medicalInsurance.isEmpty() || insuranceStatus.isEmpty()) {
            Toast.makeText(this, "请填写必填信息", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            java.math.BigDecimal paymentAmount = new java.math.BigDecimal(paymentAmountStr);

            showProgress(true);

            SocialSecurity socialSecurity = new SocialSecurity();
            socialSecurity.setResidentName(residentName);
            socialSecurity.setIdCard(idCard);
            socialSecurity.setPaymentAmount(paymentAmount);
            socialSecurity.setInsuranceStatus(insuranceStatus);
            socialSecurity.setNotes(notes);
            socialSecurity.setPensionInsurance(pensionInsurance);
            socialSecurity.setMedicalInsurance(medicalInsurance);
            socialSecurity.setUnemploymentInsurance(unemploymentInsurance);
            socialSecurity.setWorkInjuryInsurance(workInjuryInsurance);
            socialSecurity.setMaternityInsurance(maternityInsurance);

            // Format Date
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate startDate = LocalDate.parse(startDateStr, formatter);
                socialSecurity.setStartDate(startDate);
                
                if (!endDateStr.isEmpty()) {
                    LocalDate endDate = LocalDate.parse(endDateStr, formatter);
                    socialSecurity.setEndDate(endDate);
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(this, "日期格式错误，请使用 YYYY-MM-DD 格式", Toast.LENGTH_SHORT).show();
                });
                return;
            }

            executorService.execute(() -> {
                try {
                    // Assuming addSocialSecurity exists
                    boolean success = apiService.addSocialSecurity(socialSecurity);

                    runOnUiThread(() -> {
                        showProgress(false);

                        if (success) {
                            Toast.makeText(AddSocialSecurityActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(AddSocialSecurityActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(AddSocialSecurityActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG)
                                .show();
                    });
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "金额格式错误", Toast.LENGTH_SHORT).show();
        }
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
     * 显示日期选择对话框
     */
    private void showDatePickerDialog(TextInputEditText editText) {
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