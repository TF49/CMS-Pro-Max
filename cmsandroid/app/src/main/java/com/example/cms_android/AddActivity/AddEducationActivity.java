package com.example.cms_android.AddActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.R;
import com.example.cms_android.model.Education;
import com.example.cms_android.network.ApiService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

public class AddEducationActivity extends AppCompatActivity {
    private TextInputEditText etResidentName, etIdCard, etSchoolName, etMajor, etStartDate, etGraduationDate, etNotes;
    private MaterialAutoCompleteTextView spEducationLevel;
    private String selectedEducationLevel;
    private RadioButton rbStudying, rbGraduated, rbSuspended, rbDropout;
    private RadioGroup rgStatus;
    private Button btnSave;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private ExecutorService executorService;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_education);
        
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
        spEducationLevel = findViewById(R.id.sp_education_level);
        etSchoolName = findViewById(R.id.et_school_name);
        etMajor = findViewById(R.id.et_major);
        etStartDate = findViewById(R.id.et_start_date);
        etGraduationDate = findViewById(R.id.et_graduation_date);
        etNotes = findViewById(R.id.et_notes);
        
        rbStudying = findViewById(R.id.rb_studying);
        rbGraduated = findViewById(R.id.rb_graduated);
        rbSuspended = findViewById(R.id.rb_suspended);
        rbDropout = findViewById(R.id.rb_dropout);
        rgStatus = findViewById(R.id.rg_status);
        
        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar);
        
        // 设置教育程度下拉选择器
        String[] educationLevels = {"请选择教育程度", "小学", "初中", "高中", "中专", "大专", "本科", "硕士", "博士"};
        ArrayAdapter<String> educationLevelAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, educationLevels);
        educationLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEducationLevel.setAdapter(educationLevelAdapter);
        
        // 设置AutoCompleteTextView选择监听器
        spEducationLevel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取选择的教育程度，排除默认选项
                if (position > 0) {
                    selectedEducationLevel = educationLevels[position];
                } else {
                    selectedEducationLevel = "";
                }
            }
        });
    }
    
    private void initServices() {
        apiService = new ApiService();
        executorService = Executors.newSingleThreadExecutor();
    }
    
    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveEducation());
        
        // 添加日期选择器点击事件
        etStartDate.setOnClickListener(v -> showDatePicker(etStartDate));
        etGraduationDate.setOnClickListener(v -> showDatePicker(etGraduationDate));
    }
    
    /**
     * 显示日期选择器
     */
    private void showDatePicker(TextInputEditText targetEditText) {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("选择日期");
        
        final MaterialDatePicker<Long> datePicker = builder.build();
        
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                // 将选择的日期转换为yyyy-MM-dd格式
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date selectedDate = new Date(selection);
                String formattedDate = sdf.format(selectedDate);
                targetEditText.setText(formattedDate);
            }
        });
        
        datePicker.show(getSupportFragmentManager(), datePicker.toString());
    }
    
    /**
     * 将中文教育程度转换为英文枚举值
     * @param educationLevel 中文教育程度
     * @return 英文枚举值，如果无效则返回null
     */
    private String getEducationLevelEnum(String educationLevel) {
        switch (educationLevel) {
            case "小学":
                return "PRIMARY";
            case "初中":
                return "JUNIOR";
            case "高中":
                return "SENIOR";
            case "中专":
                return "VOCATIONAL";
            case "大专":
                return "COLLEGE";
            case "本科":
                return "BACHELOR";
            case "硕士":
                return "MASTER";
            case "博士":
                return "DOCTOR";
            default:
                return null;
        }
    }
    
    /**
     * 保存教育信息
     */
    private void saveEducation() {
        String residentName = etResidentName.getText().toString().trim();
        String idCard = etIdCard.getText().toString().trim();
        String educationLevel = selectedEducationLevel;
        String schoolName = etSchoolName.getText().toString().trim();
        String major = etMajor.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String graduationDate = etGraduationDate.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        
        // 获取选中的状态
        String status = "STUDYING"; // 默认为在读
        if (rbGraduated.isChecked()) {
            status = "GRADUATED";
        } else if (rbSuspended.isChecked()) {
            status = "SUSPENDED";
        } else if (rbDropout.isChecked()) {
            status = "DROP_OUT";
        }
        
        if (educationLevel.isEmpty() || schoolName.isEmpty()) {
            Toast.makeText(this, "教育程度和学校名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 验证教育程度是否有效
        String levelEnum = getEducationLevelEnum(educationLevel);
        if (levelEnum == null) {
            Toast.makeText(this, "请选择有效的教育程度", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 验证身份证号格式（如果填写了的话）
        if (!idCard.isEmpty() && !isValidIdCard(idCard)) {
            Toast.makeText(this, "身份证号格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showProgress(true);
        
        Education education = new Education();
        education.setResidentName(residentName);
        education.setIdCard(idCard);
        education.setEducationLevel(levelEnum); // 使用转换后的枚举值
        education.setSchoolName(schoolName);
        education.setMajor(major);
        education.setStatus(status);
        education.setNotes(notes);
        
        // 处理开始日期格式转换
        if (!startDate.isEmpty()) {
            try {
                LocalDate localStartDate = LocalDate.parse(startDate, localDateFormatter);
                education.setStartDate(localStartDate);
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(AddEducationActivity.this, "开始日期格式错误", Toast.LENGTH_SHORT).show();
                });
                return;
            }
        }
        
        // 处理毕业日期格式转换
        if (!graduationDate.isEmpty()) {
            try {
                LocalDate localEndDate = LocalDate.parse(graduationDate, localDateFormatter);
                education.setEndDate(localEndDate);
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(AddEducationActivity.this, "毕业日期格式错误", Toast.LENGTH_SHORT).show();
                });
                return;
            }
        }
        
        executorService.execute(() -> {
            try {
                boolean success = apiService.addEducation(education);
                
                runOnUiThread(() -> {
                    showProgress(false);
                    
                    if (success) {
                        Toast.makeText(AddEducationActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        // 教育数据已添加，可以通过其他方式通知数据变更
                        // 例如：发送广播或使用EventBus等
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AddEducationActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(AddEducationActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
     * 验证身份证号格式
     */
    private boolean isValidIdCard(String idCard) {
        // 18位身份证号验证
        return idCard.matches("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}