package com.example.cms_android.AddActivity;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.R;
import com.example.cms_android.model.Employment;
import com.example.cms_android.network.ApiService;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.AutoCompleteTextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AddEmploymentActivity extends AppCompatActivity {
    private TextInputEditText etResidentName, etIdCard, etCompanyName, etPosition, etSalary,
            etStartDate, etEndDate, etNotes;
    private AutoCompleteTextView etEmploymentStatus, etContractType, etIndustry;
    private Button btnSave;
    private ProgressBar progressBar;

    // SimpleDateFormat is not thread-safe, but here it's used in UI thread or
    // inside single method call
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ApiService apiService;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employment);

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
        etCompanyName = findViewById(R.id.et_company_name);
        etPosition = findViewById(R.id.et_position);
        etIndustry = findViewById(R.id.et_industry);
        etSalary = findViewById(R.id.et_salary);
        etEmploymentStatus = findViewById(R.id.et_employment_status);
        etContractType = findViewById(R.id.et_contract_type);
        etStartDate = findViewById(R.id.et_start_date);
        etEndDate = findViewById(R.id.et_end_date);
        etNotes = findViewById(R.id.et_notes);
        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void initServices() {
        apiService = new ApiService();
        executorService = Executors.newSingleThreadExecutor();
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveEmployment());

        // 设置行业类型下拉选项
        String[] industryOptions = {"IT/互联网", "金融", "制造业", "教育", "医疗", "服务业", "其他"};
        ArrayAdapter<String> industryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, industryOptions);
        etIndustry.setAdapter(industryAdapter);

        // 设置就业状态下拉选项
        String[] employmentStatusOptions = {"在职", "失业", "退休"};
        ArrayAdapter<String> employmentStatusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, employmentStatusOptions);
        etEmploymentStatus.setAdapter(employmentStatusAdapter);

        // 设置合同类型下拉选项
        String[] contractTypeOptions = {"固定期限", "无固定期限", "其他"};
        ArrayAdapter<String> contractTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, contractTypeOptions);
        etContractType.setAdapter(contractTypeAdapter);

        // 设置日期选择器监听器
        etStartDate.setOnClickListener(v -> showDatePickerDialog(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePickerDialog(etEndDate));

        // 设置输入类型为null以防止软键盘弹出
        etIndustry.setInputType(android.text.InputType.TYPE_NULL);
        etEmploymentStatus.setInputType(android.text.InputType.TYPE_NULL);
        etContractType.setInputType(android.text.InputType.TYPE_NULL);
    }

    private void saveEmployment() {
        String residentName = etResidentName.getText().toString().trim();
        String idCard = etIdCard.getText().toString().trim();
        String companyName = etCompanyName.getText().toString().trim();
        String position = etPosition.getText().toString().trim();
        String industry = etIndustry.getText().toString().trim();
        String salaryStr = etSalary.getText().toString().trim();
        String employmentStatus = etEmploymentStatus.getText().toString().trim();
        String contractType = etContractType.getText().toString().trim();
        String startDateStr = etStartDate.getText().toString().trim();
        String endDateStr = etEndDate.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        if (residentName.isEmpty() || idCard.isEmpty() || companyName.isEmpty() || position.isEmpty() || startDateStr.isEmpty()) {
            Toast.makeText(this, "请填写完整必填信息", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            java.math.BigDecimal salary = salaryStr.isEmpty() ? null : new java.math.BigDecimal(salaryStr);
            LocalDate startDate = LocalDate.parse(startDateStr, dateFormatter);
            LocalDate endDate = endDateStr.isEmpty() ? null : LocalDate.parse(endDateStr, dateFormatter);

            showProgress(true);

            Employment employment = new Employment();
            employment.setResidentName(residentName);
            employment.setIdCard(idCard);
            employment.setCompany(companyName);
            employment.setPosition(position);
            employment.setIndustry(industry);
            employment.setSalary(salary);
            employment.setEmploymentStatus(employmentStatus);
            employment.setContractType(contractType);
            employment.setStartDate(startDate);
            employment.setEndDate(endDate);
            employment.setNotes(notes);

            // Set default create/update time if needed, or let backend handle it

            executorService.execute(() -> {
                try {
                    boolean success = apiService.addEmployment(employment);

                    runOnUiThread(() -> {
                        showProgress(false);

                        if (success) {
                            Toast.makeText(AddEmploymentActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(AddEmploymentActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(AddEmploymentActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "薪资格式错误", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "日期格式错误，请使用yyyy-MM-dd", Toast.LENGTH_SHORT).show();
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
        // 获取当前日期
        java.util.Calendar calendar = java.util.Calendar.getInstance();

        // 如果编辑框中已有日期，则使用该日期作为默认值
        try {
            String dateStr = editText.getText().toString().trim();
            if (!dateStr.isEmpty()) {
                LocalDate localDate = LocalDate.parse(dateStr, dateFormatter);
                calendar.set(java.util.Calendar.YEAR, localDate.getYear());
                calendar.set(java.util.Calendar.MONTH, localDate.getMonthValue() - 1);
                calendar.set(java.util.Calendar.DAY_OF_MONTH, localDate.getDayOfMonth());
            }
        } catch (Exception e) {
            // 如果解析失败，使用当前日期
            e.printStackTrace();
        }

        // 创建日期选择器对话框
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // 格式化选择的日期并设置到编辑框
                    LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                    editText.setText(selectedDate.format(dateFormatter));
                },
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
        );

        // 显示日期选择器
        datePickerDialog.show();
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
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}