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
import com.example.cms_android.model.Employment;
import com.example.cms_android.model.Resident;
import com.example.cms_android.network.ApiService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EmploymentActivity extends AppCompatActivity {
    public static final String EXTRA_EMPLOYMENT_ID = "employment_id";

    private TextView tvResidentId, tvResidentName, tvIdCard, tvCompanyName, tvJobTitle, tvSalary, tvIndustry,
            tvEmploymentStatus, tvEntryDate, tvContractType, tvCreateTime, tvUpdateTime, tvNotes;
    private EditText etCompanyNameEdit, etJobTitleEdit, etSalaryEdit, etIndustryEdit, etEntryDateEdit, etEndDateEdit, etNotesEdit;
    private AutoCompleteTextView etEmploymentStatusEdit, etContractTypeEdit;
    private Button btnEdit, btnSave, btnCancel, btnDelete;
    private View viewDetails, viewEdit;

    private ApiService apiService;
    private ExecutorService executorService;
    private Long employmentId;
    private Employment employment;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employment);

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

        // 获取就业ID
        employmentId = getIntent().getLongExtra(EXTRA_EMPLOYMENT_ID, -1);
        if (employmentId == -1) {
            Toast.makeText(this, "无效的就业ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 加载就业详情
        loadEmploymentDetails();
    }

    private void initViews() {
        // 详情视图
        viewDetails = findViewById(R.id.view_details);
        tvResidentId = findViewById(R.id.tv_resident_id);
        tvResidentName = findViewById(R.id.tv_resident_name);
        tvIdCard = findViewById(R.id.tv_id_card);
        tvCompanyName = findViewById(R.id.tv_company_name);
        tvJobTitle = findViewById(R.id.tv_job_title);
        tvSalary = findViewById(R.id.tv_salary);
        tvIndustry = findViewById(R.id.tv_industry);
        tvEmploymentStatus = findViewById(R.id.tv_employment_status);
        tvEntryDate = findViewById(R.id.tv_entry_date);
        tvContractType = findViewById(R.id.tv_contract_type);
        tvCreateTime = findViewById(R.id.tv_create_time);
        tvUpdateTime = findViewById(R.id.tv_update_time);
        tvNotes = findViewById(R.id.tv_notes);

        // 编辑视图
        viewEdit = findViewById(R.id.view_edit);
        etCompanyNameEdit = findViewById(R.id.et_company_name_edit);
        etJobTitleEdit = findViewById(R.id.et_job_title_edit);
        etSalaryEdit = findViewById(R.id.et_salary_edit);
        etIndustryEdit = findViewById(R.id.et_industry_edit);
        etEmploymentStatusEdit = findViewById(R.id.et_employment_status_edit);
        etEntryDateEdit = findViewById(R.id.et_entry_date_edit);
        etEndDateEdit = findViewById(R.id.et_end_date_edit);
        etContractTypeEdit = findViewById(R.id.et_contract_type_edit);
        etNotesEdit = findViewById(R.id.et_notes_edit);

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
        btnSave.setOnClickListener(v -> saveEmploymentChanges());
        btnCancel.setOnClickListener(v -> switchToDetailsMode());
        btnDelete.setOnClickListener(v -> deleteEmployment());

        // 设置日期选择器监听器
        etEntryDateEdit.setOnClickListener(v -> showDatePickerDialog(etEntryDateEdit));
        etEntryDateEdit.setFocusable(false);
        etEntryDateEdit.setClickable(true);

        setupDropdowns();
    }

    private void setupDropdowns() {
        // 设置就业状态下拉选项
        String[] employmentStatuses = { "在职", "失业", "退休" };
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                employmentStatuses);
        etEmploymentStatusEdit.setAdapter(statusAdapter);
        etEmploymentStatusEdit.setInputType(android.text.InputType.TYPE_NULL);

        // 设置合同类型下拉选项
        String[] contractTypes = { "固定期限", "无固定期限", "其他" };
        ArrayAdapter<String> contractAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                contractTypes);
        etContractTypeEdit.setAdapter(contractAdapter);
        etContractTypeEdit.setInputType(android.text.InputType.TYPE_NULL);
    }

    /**
     * 加载就业详情
     */
    private void loadEmploymentDetails() {
        executorService.execute(() -> {
            try {
                // 这里需要根据实际的API调整，假设ApiService中有getEmploymentById方法
                employment = apiService.getEmploymentById(employmentId);

                runOnUiThread(() -> {
                    if (employment != null) {
                        displayEmploymentDetails();
                    } else {
                        Toast.makeText(EmploymentActivity.this, "加载就业详情失败", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(EmploymentActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * 显示就业详情
     */
    private void displayEmploymentDetails() {
        // 基本信息
        tvResidentId.setText(String.valueOf(employment.getResidentId()));
        tvResidentName.setText(employment.getResidentName() != null ? employment.getResidentName() : "未填写");
        tvIdCard.setText(employment.getIdCard() != null ? employment.getIdCard() : "未填写");

        // 就业信息
        // 使用工作单位填充工作地点
        tvCompanyName.setText(employment.getCompany() != null ? employment.getCompany() : "未填写");
        tvJobTitle.setText(employment.getPosition() != null ? employment.getPosition() : "未填写");
        // 去掉部门字段，不再显示

        // 薪资处理
        java.math.BigDecimal salary = employment.getSalary();
        tvSalary.setText(salary != null ? salary.toString() : "未填写");

        tvIndustry.setText(employment.getIndustry() != null ? employment.getIndustry() : "未填写");

        // 就业状态
        String employmentStatus = employment.getEmploymentStatus() != null ? employment.getEmploymentStatus()
                : (employment.getEndDate() == null ? "在职" : "离职");
        tvEmploymentStatus.setText(employmentStatus);

        // 格式化日期显示
        String startDateStr = employment.getStartDate() != null ? employment.getStartDate().format(dateFormatter) : "未填写";
        tvEntryDate.setText(startDateStr);

        // 显示结束日期
        String endDateStr = employment.getEndDate() != null ? employment.getEndDate().format(dateFormatter) : "未填写";
        TextView tvEndDate = findViewById(R.id.tv_end_date);
        if (tvEndDate != null) {
            tvEndDate.setText(endDateStr);
        }

        // 合同类型
        tvContractType.setText(employment.getContractType() != null ? employment.getContractType() : "未填写");

        // 时间信息
        if (employment.getCreateTime() != null) {
            tvCreateTime.setText(employment.getCreateTime().format(dateTimeFormatter));
        } else {
            tvCreateTime.setText("未填写");
        }

        if (employment.getUpdateTime() != null) {
            tvUpdateTime.setText(employment.getUpdateTime().format(dateTimeFormatter));
        } else {
            tvUpdateTime.setText("未填写");
        }

        // 备注
        tvNotes.setText(employment.getNotes() != null ? employment.getNotes() : "未填写");
    }

    /**
     * 切换到编辑模式
     */
    private void switchToEditMode() {
        // 填充编辑表单，添加空检查
        // 使用工作单位填充工作地点
        etCompanyNameEdit.setText(employment.getCompany() != null ? employment.getCompany() : "");
        etJobTitleEdit.setText(employment.getPosition() != null ? employment.getPosition() : "");

        // 薪资处理
        java.math.BigDecimal salary = employment.getSalary();
        etSalaryEdit.setText(salary != null ? salary.toString() : "");

        etIndustryEdit.setText(employment.getIndustry() != null ? employment.getIndustry() : "");
        etEmploymentStatusEdit.setText(getEmploymentStatusDisplay(employment.getEmploymentStatus()), false);
        etContractTypeEdit.setText(getContractTypeDisplay(employment.getContractType()), false);
        etNotesEdit.setText(employment.getNotes() != null ? employment.getNotes() : "");

        // 格式化日期显示
        String startDateStr = employment.getStartDate() != null ? employment.getStartDate().format(dateFormatter) : "";
        etEntryDateEdit.setText(startDateStr);

        String endDateStr = employment.getEndDate() != null ? employment.getEndDate().format(dateFormatter) : "";
        etEndDateEdit.setText(endDateStr);

        // 切换视图
        viewDetails.setVisibility(View.GONE);
        viewEdit.setVisibility(View.VISIBLE);
    }

    private String getEmploymentStatusDisplay(String status) {
        if (status == null)
            return "在职";
        // 直接返回状态值，因为后端存储的是中文
        return status;
    }

    private String getContractTypeDisplay(String type) {
        if (type == null)
            return "";
        // 直接返回类型值，因为后端存储的是中文
        return type;
    }

    /**
     * 切换到详情模式
     */
    private void switchToDetailsMode() {
        viewDetails.setVisibility(View.VISIBLE);
        viewEdit.setVisibility(View.GONE);
    }

    /**
     * 保存就业信息更改
     */
    private void saveEmploymentChanges() {
        String workUnit = etCompanyNameEdit.getText().toString().trim();
        String jobTitle = etJobTitleEdit.getText().toString().trim();
        String salaryStr = etSalaryEdit.getText().toString().trim();
        String industry = etIndustryEdit.getText().toString().trim();
        String employmentStatus = etEmploymentStatusEdit.getText().toString().trim();
        String contractType = etContractTypeEdit.getText().toString().trim();
        String entryDate = etEntryDateEdit.getText().toString().trim();
        String endDate = etEndDateEdit.getText().toString().trim();
        String notes = etNotesEdit.getText().toString().trim();

        if (workUnit.isEmpty() || jobTitle.isEmpty()) {
            Toast.makeText(this, "工作单位和职位不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 更新就业对象
        employment.setCompany(workUnit);
        employment.setPosition(jobTitle);

        // 处理薪资
        if (!salaryStr.isEmpty()) {
            try {
                java.math.BigDecimal salary = new java.math.BigDecimal(salaryStr);
                employment.setSalary(salary);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "薪资格式错误", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // 就业状态和合同类型直接使用中文字符串（与后端保持一致）
        employment.setEmploymentStatus(employmentStatus);
        employment.setContractType(contractType);

        employment.setIndustry(industry);
        employment.setNotes(notes);

        // 处理入职日期
        if (!entryDate.isEmpty()) {
            try {
                employment.setStartDate(LocalDate.parse(entryDate, dateFormatter));
            } catch (Exception e) {
                Toast.makeText(this, "入职日期格式错误", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // 处理结束日期
        if (!endDate.isEmpty()) {
            try {
                employment.setEndDate(LocalDate.parse(endDate, dateFormatter));
            } catch (Exception e) {
                Toast.makeText(this, "结束日期格式错误", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            employment.setEndDate(null);
        }

        // 保存到服务器
        executorService.execute(() -> {
            try {
                // 这里需要根据实际的API调整，假设ApiService中有updateEmployment方法
                boolean success = apiService.updateEmployment(employment);

                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(EmploymentActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        // 刷新详情显示
                        displayEmploymentDetails();
                        switchToDetailsMode();
                    } else {
                        Toast.makeText(EmploymentActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(EmploymentActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                    .setMessage("您正在编辑就业信息，是否保存更改？")
                    .setPositiveButton("保存", (dialog, which) -> {
                        // 保存更改
                        saveEmploymentChanges();
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
     * 删除就业信息
     */
    private void deleteEmployment() {
        // 显示确认对话框
        new android.app.AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除该就业档案吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    executorService.execute(() -> {
                        try {
                            boolean success = apiService.deleteEmployment(employmentId);
                            runOnUiThread(() -> {
                                if (success) {
                                    Toast.makeText(EmploymentActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(EmploymentActivity.this, "删除失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(EmploymentActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG)
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
                calendar.set(Calendar.YEAR, localDate.getYear());
                calendar.set(Calendar.MONTH, localDate.getMonthValue() - 1);
                calendar.set(Calendar.DAY_OF_MONTH, localDate.getDayOfMonth());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                    editText.setText(selectedDate.format(dateFormatter));
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