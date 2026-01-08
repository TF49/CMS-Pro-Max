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
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.R;
import com.example.cms_android.model.Resident;
import com.example.cms_android.model.SocialSecurity;
import com.example.cms_android.network.ApiService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SocialSecurityActivity extends AppCompatActivity {
    private static final String EXTRA_SOCIAL_SECURITY_ID = "social_security_id";

    private TextView tvResidentName, tvMonthlyContribution, tvStartDate, tvEndDate, tvInsuranceStatus;
    private TextView tvPensionInsurance, tvMedicalInsurance, tvUnemploymentInsurance, tvWorkInjuryInsurance,
            tvMaternityInsurance;
    private AutoCompleteTextView etInsuranceStatusEdit;
    private EditText etMonthlyContributionEdit, etStartDateEdit, etEndDateEdit;
    private AutoCompleteTextView etPensionInsuranceEdit, etMedicalInsuranceEdit, etUnemploymentInsuranceEdit,
            etWorkInjuryInsuranceEdit, etMaternityInsuranceEdit;
    private Button btnEdit, btnSave, btnCancel, btnDelete;
    private View viewDetails, viewEdit;

    private ApiService apiService;
    private ExecutorService executorService;
    private Long socialSecurityId;
    private SocialSecurity socialSecurity;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socialsecurity);

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

        // 获取社保ID
        socialSecurityId = getIntent().getLongExtra(EXTRA_SOCIAL_SECURITY_ID, -1);
        if (socialSecurityId == -1) {
            Toast.makeText(this, "无效的社保ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 加载社保详情
        loadSocialSecurityDetails();
    }

    private void initViews() {
        // 详情视图
        viewDetails = findViewById(R.id.view_details);
        tvResidentName = findViewById(R.id.tv_resident_name);

        tvMonthlyContribution = findViewById(R.id.tv_monthly_contribution);
        tvStartDate = findViewById(R.id.tv_start_date);
        tvEndDate = findViewById(R.id.tv_end_date);
        tvInsuranceStatus = findViewById(R.id.tv_insurance_status);

        // 详细保险类型
        tvPensionInsurance = findViewById(R.id.tv_pension_insurance);
        tvMedicalInsurance = findViewById(R.id.tv_medical_insurance);
        tvUnemploymentInsurance = findViewById(R.id.tv_unemployment_insurance);
        tvWorkInjuryInsurance = findViewById(R.id.tv_work_injury_insurance);
        tvMaternityInsurance = findViewById(R.id.tv_maternity_insurance);

        btnEdit = findViewById(R.id.btn_edit);

        // 编辑视图
        viewEdit = findViewById(R.id.view_edit);

        etMonthlyContributionEdit = findViewById(R.id.et_monthly_contribution_edit);
        etStartDateEdit = findViewById(R.id.et_start_date_edit);
        etEndDateEdit = findViewById(R.id.et_end_date_edit);

        // 详细保险类型编辑字段
        etPensionInsuranceEdit = findViewById(R.id.et_pension_insurance_edit);
        etMedicalInsuranceEdit = findViewById(R.id.et_medical_insurance_edit);
        etUnemploymentInsuranceEdit = findViewById(R.id.et_unemployment_insurance_edit);
        etWorkInjuryInsuranceEdit = findViewById(R.id.et_work_injury_insurance_edit);
        etMaternityInsuranceEdit = findViewById(R.id.et_maternity_insurance_edit);

        etInsuranceStatusEdit = findViewById(R.id.et_insurance_status_edit);
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
        btnSave.setOnClickListener(v -> saveSocialSecurityChanges());
        btnCancel.setOnClickListener(v -> switchToDetailsMode());
        btnDelete.setOnClickListener(v -> deleteSocialSecurity());

        // 设置日期选择器监听器
        etStartDateEdit.setOnClickListener(v -> showDatePickerDialog(etStartDateEdit));
        etStartDateEdit.setFocusable(false);
        etStartDateEdit.setClickable(true);

        etEndDateEdit.setOnClickListener(v -> showDatePickerDialog(etEndDateEdit));
        etEndDateEdit.setFocusable(false);
        etEndDateEdit.setClickable(true);

        setupDropdowns();
    }

    private void setupDropdowns() {
        // 养老保险选项
        String[] pensionOptions = { "城镇职工养老保险", "城乡居民养老保险" };
        ArrayAdapter<String> pensionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, pensionOptions);
        etPensionInsuranceEdit.setAdapter(pensionAdapter);

        // 医疗保险选项
        String[] medicalOptions = { "城镇职工医疗保险", "城乡居民医疗保险" };
        ArrayAdapter<String> medicalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, medicalOptions);
        etMedicalInsuranceEdit.setAdapter(medicalAdapter);

        // 失业保险选项
        String[] unemploymentOptions = { "失业保险", "无失业保险" };
        ArrayAdapter<String> unemploymentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, unemploymentOptions);
        etUnemploymentInsuranceEdit.setAdapter(unemploymentAdapter);

        // 工伤保险选项
        String[] workInjuryOptions = { "工伤保险", "无工伤保险" };
        ArrayAdapter<String> workInjuryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, workInjuryOptions);
        etWorkInjuryInsuranceEdit.setAdapter(workInjuryAdapter);

        // 生育保险选项
        String[] maternityOptions = { "生育保险", "无生育保险" };
        ArrayAdapter<String> maternityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, maternityOptions);
        etMaternityInsuranceEdit.setAdapter(maternityAdapter);

        // 参保状态选项
        String[] statuses = { "正常参保", "暂停参保", "停止参保" };
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
        etInsuranceStatusEdit.setAdapter(statusAdapter);
    }

    /**
     * 加载社保详情
     */
    private void loadSocialSecurityDetails() {

        executorService.execute(() -> {
            try {
                // 加载社保详情
                socialSecurity = apiService.getSocialSecurityById(socialSecurityId);

                // 获取居民姓名
                if (socialSecurity != null && socialSecurity.getResidentId() != null) {
                    Resident resident = apiService.getResidentById(socialSecurity.getResidentId());
                    if (resident != null) {
                        socialSecurity.setResidentName(resident.getName());
                    }
                }

                runOnUiThread(() -> {
                    if (socialSecurity != null) {

                        displaySocialSecurityDetails();
                    } else {

                        Toast.makeText(SocialSecurityActivity.this, "加载社保详情失败", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {

                    Toast.makeText(SocialSecurityActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * 显示社保详情
     */
    private void displaySocialSecurityDetails() {
        // 添加调试日志，打印社保对象的关键信息

        tvResidentName.setText(socialSecurity.getResidentName() != null ? socialSecurity.getResidentName() : "");
        tvMonthlyContribution.setText(socialSecurity.getPaymentAmount() != null
                ? String.valueOf(socialSecurity.getPaymentAmount())
                : "");
        tvStartDate
                .setText(socialSecurity.getStartDate() != null ? socialSecurity.getStartDate().format(dateFormatter) : "");
        tvEndDate.setText(socialSecurity.getEndDate() != null ? socialSecurity.getEndDate().format(dateFormatter) : "");
        tvInsuranceStatus
                .setText(getInsuranceStatusDisplay(socialSecurity.getInsuranceStatus()));
        

        // 显示详细保险类型
        tvPensionInsurance
                .setText(socialSecurity.getPensionInsurance() != null ? socialSecurity.getPensionInsurance() : "");
        tvMedicalInsurance
                .setText(socialSecurity.getMedicalInsurance() != null ? socialSecurity.getMedicalInsurance() : "");
        tvUnemploymentInsurance.setText(
                socialSecurity.getUnemploymentInsurance() != null ? socialSecurity.getUnemploymentInsurance() : "");
        tvWorkInjuryInsurance.setText(
                socialSecurity.getWorkInjuryInsurance() != null ? socialSecurity.getWorkInjuryInsurance() : "");
        tvMaternityInsurance
                .setText(socialSecurity.getMaternityInsurance() != null ? socialSecurity.getMaternityInsurance() : "");
    }

    /**
     * 切换到编辑模式
     */
    private void switchToEditMode() {
        // 填充编辑表单，添加空检查
        etMonthlyContributionEdit.setText(socialSecurity.getPaymentAmount() != null
                ? String.valueOf(socialSecurity.getPaymentAmount())
                : "");
        etStartDateEdit
                .setText(socialSecurity.getStartDate() != null ? socialSecurity.getStartDate().format(dateFormatter) : "");
        etEndDateEdit
                .setText(socialSecurity.getEndDate() != null ? socialSecurity.getEndDate().format(dateFormatter) : "");

        // 填充详细保险类型
        etPensionInsuranceEdit.setText(
                socialSecurity.getPensionInsurance() != null ? socialSecurity.getPensionInsurance() : "", false);
        etMedicalInsuranceEdit.setText(
                socialSecurity.getMedicalInsurance() != null ? socialSecurity.getMedicalInsurance() : "", false);
        etUnemploymentInsuranceEdit.setText(
                socialSecurity.getUnemploymentInsurance() != null ? socialSecurity.getUnemploymentInsurance() : "", false);
        etWorkInjuryInsuranceEdit.setText(
                socialSecurity.getWorkInjuryInsurance() != null ? socialSecurity.getWorkInjuryInsurance() : "", false);
        etMaternityInsuranceEdit.setText(
                socialSecurity.getMaternityInsurance() != null ? socialSecurity.getMaternityInsurance() : "", false);

        etInsuranceStatusEdit
                .setText(getInsuranceStatusDisplay(socialSecurity.getInsuranceStatus()), false);

        // 切换视图
        viewDetails.setVisibility(View.GONE);
        viewEdit.setVisibility(View.VISIBLE);
    }

    private String getInsuranceStatusDisplay(String status) {
        Log.d("SocialSecurityActivity", "Insurance status raw value: " + status);
        if (status == null)
            return "参保";
        // 先转换为大写，再进行比较
        String upperStatus = status.toUpperCase();
        switch (upperStatus) {
            case "NORMAL":
                return "参保";
            case "PAUSED":
                return "暂停";
            case "TERMINATED":
                return "终止";
            default:
                return status;
        }
    }

    /**
     * 切换到详情模式
     */
    private void switchToDetailsMode() {
        viewDetails.setVisibility(View.VISIBLE);
        viewEdit.setVisibility(View.GONE);
    }

    /**
     * 保存社保信息更改
     */
    private void saveSocialSecurityChanges() {
        String pensionInsurance = etPensionInsuranceEdit.getText().toString().trim();
        String medicalInsurance = etMedicalInsuranceEdit.getText().toString().trim();
        String unemploymentInsurance = etUnemploymentInsuranceEdit.getText().toString().trim();
        String workInjuryInsurance = etWorkInjuryInsuranceEdit.getText().toString().trim();
        String maternityInsurance = etMaternityInsuranceEdit.getText().toString().trim();
        String monthlyContributionStr = etMonthlyContributionEdit.getText().toString().trim();
        String startDate = etStartDateEdit.getText().toString().trim();
        String endDate = etEndDateEdit.getText().toString().trim();
        String insuranceStatus = etInsuranceStatusEdit.getText().toString().trim();



        // 直接使用中文状态值
        String statusValue = "正常参保";
        switch (insuranceStatus) {
            case "参保":
            case "正常参保":
                statusValue = "正常参保";
                break;
            case "暂停":
            case "暂停参保":
                statusValue = "暂停参保";
                break;
            case "终止":
            case "停止参保":
                statusValue = "停止参保";
                break;
        }

        // 更新社保对象
        socialSecurity.setInsuranceStatus(statusValue);
        socialSecurity.setPensionInsurance(pensionInsurance);
        socialSecurity.setMedicalInsurance(medicalInsurance);
        socialSecurity.setUnemploymentInsurance(unemploymentInsurance);
        socialSecurity.setWorkInjuryInsurance(workInjuryInsurance);
        socialSecurity.setMaternityInsurance(maternityInsurance);

        // 解析月缴费金额
        if (!monthlyContributionStr.isEmpty()) {
            try {
                socialSecurity.setPaymentAmount(new BigDecimal(monthlyContributionStr));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "月缴费金额格式错误", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            socialSecurity.setPaymentAmount(null);
        }
        // 转换日期字段
        try {
            if (!startDate.isEmpty()) {
                socialSecurity.setStartDate(LocalDate.parse(startDate, dateFormatter));
            }
            if (!endDate.isEmpty()) {
                socialSecurity.setEndDate(LocalDate.parse(endDate, dateFormatter));
            }
        } catch (Exception e) {
            Toast.makeText(this, "日期格式错误", Toast.LENGTH_SHORT).show();
            return;
        }


        // 保存到服务器
        executorService.execute(() -> {
            try {
                // 这里需要根据实际的API调整，假设ApiService中有updateSocialSecurity方法
                boolean success = apiService.updateSocialSecurity(socialSecurity);

                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(SocialSecurityActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        // 刷新详情显示
                        displaySocialSecurityDetails();
                        switchToDetailsMode();
                    } else {
                        Toast.makeText(SocialSecurityActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(SocialSecurityActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                    .setMessage("您正在编辑社保信息，是否保存更改？")
                    .setPositiveButton("保存", (dialog, which) -> {
                        // 保存更改
                        saveSocialSecurityChanges();
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
     * 删除社保信息
     */
    private void deleteSocialSecurity() {
        // 显示确认对话框
        new android.app.AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除该社保档案吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    executorService.execute(() -> {
                        try {
                            boolean success = apiService.deleteSocialSecurity(socialSecurityId);
                            runOnUiThread(() -> {
                                if (success) {
                                    Toast.makeText(SocialSecurityActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(SocialSecurityActivity.this, "删除失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(SocialSecurityActivity.this, "网络错误: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}