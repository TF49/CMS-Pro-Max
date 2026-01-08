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
import android.util.Log;

import java.util.Calendar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.R;
import com.example.cms_android.model.Education;
import com.example.cms_android.network.ApiService;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EducationActivity extends AppCompatActivity
{
    private static final String EXTRA_EDUCATION_ID = "education_id";

    private TextView tvId, tvResidentId, tvResidentName, tvIdCard, tvEducationLevel, tvSchoolName, tvMajor, tvStartDate, tvGraduationDate, tvStatus, tvNotes, tvCreateTime, tvUpdateTime;
    private EditText etResidentNameEdit, etIdCardEdit, etSchoolNameEdit, etMajorEdit, etStartDateEdit, etGraduationDateEdit, etNotesEdit;
    private AutoCompleteTextView etEducationLevelEdit, etStatusEdit;
    private Button btnEdit, btnSave, btnCancel, btnDelete;
    private View viewDetails, viewEdit;

    private ApiService apiService;
    private ExecutorService executorService;
    private Long educationId;
    private Education education;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // 编辑模式标志
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);

        // 设置Toolbar为ActionBar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 启用返回按钮
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 初始化视图
        initViews();

        // 初始化服务
        initServices();

        // 设置监听器
        setupListeners();

        // 获取教育ID
        educationId = getIntent().getLongExtra(EXTRA_EDUCATION_ID, -1);
        if (educationId == -1)
        {
            Toast.makeText(this, "无效的教育ID", Toast.LENGTH_SHORT).show();

            finish();

            return;
        }

        // 检查是否是编辑模式
        isEditMode = getIntent().getBooleanExtra("is_edit_mode", false);

        // 加载教育详情
        loadEducationDetails();
        
        // 如果是编辑模式，直接切换到编辑视图
        if (isEditMode)
        {
            switchToEditMode();
        }
    }

    // 初始化视图
    private void initViews()
    {
        // 详情视图
        viewDetails = findViewById(R.id.view_details);
        tvId = findViewById(R.id.tv_id);
        tvResidentId = findViewById(R.id.tv_resident_id);
        tvResidentName = findViewById(R.id.tv_resident_name);
        tvIdCard = findViewById(R.id.tv_id_card);
        tvEducationLevel = findViewById(R.id.tv_education_level);
        tvSchoolName = findViewById(R.id.tv_school_name);
        tvMajor = findViewById(R.id.tv_major);
        tvStartDate = findViewById(R.id.tv_start_date);
        tvGraduationDate = findViewById(R.id.tv_graduation_date);
        tvStatus = findViewById(R.id.tv_status);
        tvNotes = findViewById(R.id.tv_notes);
        tvCreateTime = findViewById(R.id.tv_create_time);
        tvUpdateTime = findViewById(R.id.tv_update_time);

        // 编辑视图
        viewEdit = findViewById(R.id.view_edit);
        etResidentNameEdit = findViewById(R.id.et_resident_name_edit);
        etIdCardEdit = findViewById(R.id.et_id_card_edit);
        etEducationLevelEdit = findViewById(R.id.et_education_level_edit);
        etSchoolNameEdit = findViewById(R.id.et_school_name_edit);
        etMajorEdit = findViewById(R.id.et_major_edit);
        etStartDateEdit = findViewById(R.id.et_start_date_edit);
        etGraduationDateEdit = findViewById(R.id.et_graduation_date_edit);
        etStatusEdit = findViewById(R.id.et_status_edit);
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

    // 初始化服务
    private void initServices()
    {
        // 创建ApiService实例
        apiService = new ApiService();

        // 创建ExecutorService实例
        executorService = Executors.newSingleThreadExecutor();
    }

    // 设置监听器
    private void setupListeners()
    {
        // 按钮监听器
        btnEdit.setOnClickListener(v -> switchToEditMode());
        btnSave.setOnClickListener(v -> saveEducationChanges());
        btnCancel.setOnClickListener(v -> switchToDetailsMode());
        btnDelete.setOnClickListener(v -> deleteEducation());

        // 设置日期选择器监听器
        etStartDateEdit.setOnClickListener(v -> showDatePickerDialog(etStartDateEdit));
        etStartDateEdit.setFocusable(false);
        etStartDateEdit.setClickable(true);

        // 设置日期选择器监听器
        etGraduationDateEdit.setOnClickListener(v -> showDatePickerDialog(etGraduationDateEdit));
        etGraduationDateEdit.setFocusable(false);
        etGraduationDateEdit.setClickable(true);

        setupDropdowns();
    }

    // 设置下拉框
    private void setupDropdowns()
    {
        // 教育水平下拉框
        String[] educationLevels = { "小学", "初中", "高中", "中专", "大专", "本科", "硕士", "博士" };
        // 创建适配器
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, educationLevels);
        // 设置适配器
        etEducationLevelEdit.setAdapter(levelAdapter);

        // 状态下拉框
        String[] statuses = { "在读", "毕业", "辍学", "休学" };
        // 创建适配器
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
        // 设置适配器
        etStatusEdit.setAdapter(statusAdapter);
    }

    // 加载教育详情
    private void loadEducationDetails() 
    {
        // 使用线程池执行异步任务
        executorService.execute(() ->
         {
            try
            {
                // 这里需要根据实际的API调整，假设ApiService中有getEducationById方法
                education = apiService.getEducationById(educationId);

                runOnUiThread(() ->
                 {
                    if (education != null)
                    {
                        displayEducationDetails();
                    } 
                    else 
                    {
                        Toast.makeText(EducationActivity.this, "加载教育详情失败", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(EducationActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * 显示教育详情
     */
    private void displayEducationDetails() {
        // 添加调试日志，打印教育对象的完整信息

        tvId.setText(String.valueOf(education.getId()));
        tvResidentId.setText(String.valueOf(education.getResidentId()));
        tvResidentName.setText(education.getResidentName() != null ? education.getResidentName() : "未填写");
        tvIdCard.setText(education.getIdCard() != null ? education.getIdCard() : "未填写");

        // 学历转换：将英文转换为中文
        String educationLevelText = "未填写";
        if (education.getEducationLevel() != null) {
            switch (education.getEducationLevel()) {
                case "PRIMARY":
                case "小学":
                    educationLevelText = "小学";
                    break;
                case "JUNIOR":
                case "初中":
                    educationLevelText = "初中";
                    break;
                case "SENIOR":
                case "高中":
                    educationLevelText = "高中";
                    break;
                case "VOCATIONAL":
                case "中专":
                    educationLevelText = "中专";
                    break;
                case "COLLEGE":
                case "大专":
                    educationLevelText = "大专";
                    break;
                case "BACHELOR":
                case "本科":
                    educationLevelText = "本科";
                    break;
                case "MASTER":
                case "硕士":
                    educationLevelText = "硕士";
                    break;
                case "DOCTOR":
                case "博士":
                    educationLevelText = "博士";
                    break;
                default:
                    educationLevelText = education.getEducationLevel();
                    break;
            }
        }
        tvEducationLevel.setText(educationLevelText);

        tvSchoolName.setText(education.getSchoolName() != null ? education.getSchoolName() : "未填写");
        tvMajor.setText(education.getMajor() != null ? education.getMajor() : "未填写");

        // 格式化日期，添加时区设置，确保与服务器返回一致
        if (education.getStartDate() != null) {
            tvStartDate.setText(education.getStartDate().format(localDateFormatter));
        } else {
            tvStartDate.setText("未填写");
        }

        if (education.getEndDate() != null) {
            tvGraduationDate.setText(education.getEndDate().format(localDateFormatter));
        } else {
            tvGraduationDate.setText("未填写");
        }

        // 状态转换：将英文转换为中文
        String statusText = "未填写";
        if (education.getStatus() != null) {
            switch (education.getStatus()) {
                case "STUDYING":
                case "在读":
                    statusText = "在读";
                    break;
                case "GRADUATED":
                case "毕业":
                    statusText = "毕业";
                    break;
                case "DROP_OUT":
                case "辍学":
                    statusText = "辍学";
                    break;
                case "SUSPENDED":
                case "休学":
                    statusText = "休学";
                    break;
                default:
                    statusText = education.getStatus();
                    break;
            }
        }
        tvStatus.setText(statusText);

        tvNotes.setText(education.getNotes() != null ? education.getNotes() : "未填写");

        // 格式化日期时间，添加时区设置
        if (education.getCreateTime() != null) {
            dateTimeFormat.setTimeZone(java.util.TimeZone.getTimeZone("GMT+8"));
            tvCreateTime.setText(dateTimeFormat.format(education.getCreateTime()));
        } else {
            tvCreateTime.setText("未填写");
        }

        if (education.getUpdateTime() != null) {
            dateTimeFormat.setTimeZone(java.util.TimeZone.getTimeZone("GMT+8"));
            tvUpdateTime.setText(dateTimeFormat.format(education.getUpdateTime()));
        } else {
            tvUpdateTime.setText("未填写");
        }
        
        // 如果是编辑模式，切换到编辑视图
        if (isEditMode) {
            switchToEditMode();
        }
    }

    /**
     * 切换到编辑模式
     */
    private void switchToEditMode() {
        // 确保教育数据已加载
        if (education != null) {
            // 填充编辑表单
            etResidentNameEdit.setText(education.getResidentName() != null ? education.getResidentName() : "");
            etIdCardEdit.setText(education.getIdCard() != null ? education.getIdCard() : "");

            // 设置学历和状态（显示中文）
            etEducationLevelEdit.setText(getEducationLevelDisplay(education.getEducationLevel()), false);
            etStatusEdit.setText(getStatusDisplay(education.getStatus()), false);

            etSchoolNameEdit.setText(education.getSchoolName() != null ? education.getSchoolName() : "");
            etMajorEdit.setText(education.getMajor() != null ? education.getMajor() : "");
            etStartDateEdit.setText(education.getStartDate() != null ? education.getStartDate().format(localDateFormatter) : "");
            etGraduationDateEdit.setText(education.getEndDate() != null ? education.getEndDate().format(localDateFormatter) : "");
            etNotesEdit.setText(education.getNotes() != null ? education.getNotes() : "");

            // 切换视图
            viewDetails.setVisibility(View.GONE);
            viewEdit.setVisibility(View.VISIBLE);
        }
    }

    private String getEducationLevelDisplay(String level) {
        if (level == null)
            return "";
        switch (level) {
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
                return level;
        }
    }

    private String getStatusDisplay(String status) {
        if (status == null)
            return "";
        switch (status) {
            case "STUDYING":
                return "在读";
            case "GRADUATED":
                return "毕业";
            case "DROP_OUT":
                return "辍学";
            case "SUSPENDED":
                return "休学";
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
     * 保存教育信息更改
     */
    private void saveEducationChanges() {
        String residentName = etResidentNameEdit.getText().toString().trim();
        String idCard = etIdCardEdit.getText().toString().trim();
        String educationLevel = etEducationLevelEdit.getText().toString().trim();
        String schoolName = etSchoolNameEdit.getText().toString().trim();
        String major = etMajorEdit.getText().toString().trim();
        String startDate = etStartDateEdit.getText().toString().trim();
        String graduationDate = etGraduationDateEdit.getText().toString().trim();
        String status = etStatusEdit.getText().toString().trim();
        String notes = etNotesEdit.getText().toString().trim();

        if (residentName.isEmpty() || idCard.isEmpty() || educationLevel.isEmpty() || schoolName.isEmpty()
                || status.isEmpty()) {
            Toast.makeText(this, "居民姓名、身份证号、教育程度、学校名称和状态不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 转换回英文字符串（后端预期的格式）
        String levelEnum = "PRIMARY"; // 默认值
        switch (educationLevel) {
            case "小学":
                levelEnum = "PRIMARY";
                break;
            case "初中":
                levelEnum = "JUNIOR";
                break;
            case "高中":
                levelEnum = "SENIOR";
                break;
            case "中专":
                levelEnum = "VOCATIONAL";
                break;
            case "大专":
                levelEnum = "COLLEGE";
                break;
            case "本科":
                levelEnum = "BACHELOR";
                break;
            case "硕士":
                levelEnum = "MASTER";
                break;
            case "博士":
                levelEnum = "DOCTOR";
                break;
            default:
                // 如果输入的教育程度不在预定义列表中，显示错误信息
                Toast.makeText(this, "请选择有效的教育程度", Toast.LENGTH_SHORT).show();
                return;
        }

        String statusEnum = status;
        switch (status) {
            case "在读":
                statusEnum = "STUDYING";
                break;
            case "毕业":
                statusEnum = "GRADUATED";
                break;
            case "辍学":
                statusEnum = "DROP_OUT";
                break;
            case "休学":
                statusEnum = "SUSPENDED";
                break;
        }

        // 更新教育对象
        education.setResidentName(residentName);
        education.setIdCard(idCard);
        education.setEducationLevel(levelEnum);
        education.setSchoolName(schoolName);
        education.setMajor(major);
        education.setStatus(statusEnum);
        education.setNotes(notes);

        // 处理开始日期格式转换
        if (!startDate.isEmpty()) {
            try {
                LocalDate localStartDate = LocalDate.parse(startDate, localDateFormatter);
                education.setStartDate(localStartDate);
            } catch (Exception e) {
                Toast.makeText(this, "开始日期格式错误", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            education.setStartDate(null);
        }

        // 处理毕业日期格式转换
        if (!graduationDate.isEmpty()) {
            try {
                LocalDate localEndDate = LocalDate.parse(graduationDate, localDateFormatter);
                education.setEndDate(localEndDate);
            } catch (Exception e) {
                Toast.makeText(this, "毕业日期格式错误", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            education.setEndDate(null);
        }

        // 保存到服务器
        executorService.execute(() -> {
            try {
                // 这里需要根据实际的API调整，假设ApiService中有updateEducation方法
                boolean success = apiService.updateEducation(education);

                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(EducationActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        // 刷新详情显示
                        displayEducationDetails();
                        switchToDetailsMode();
                        
                        // 教育数据已更新，可以通过其他方式通知数据变更
                        // 例如：发送广播或使用EventBus等
                    } else {
                        Toast.makeText(EducationActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    String errorMessage = "保存失败: " + e.getMessage();
                    Toast.makeText(EducationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    
                    // 打印详细错误信息到日志，便于调试
                    Log.e("EducationActivity", "保存教育记录失败", e);
                });
            }
        });
    }

    /**
     * 删除教育信息
     */
    private void deleteEducation() {
        // 显示确认对话框
        new android.app.AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除该教育档案吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    executorService.execute(() -> {
                        try {
                            boolean success = apiService.deleteEducation(educationId);
                            runOnUiThread(() -> {
                                if (success) {
                                    Toast.makeText(EducationActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(EducationActivity.this, "删除失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(EducationActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG)
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
                    .setMessage("您正在编辑教育信息，是否保存更改？")
                    .setPositiveButton("保存", (dialog, which) -> {
                        // 保存更改
                        saveEducationChanges();
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