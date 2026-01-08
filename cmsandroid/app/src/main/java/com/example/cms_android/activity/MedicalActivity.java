package com.example.cms_android.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.text.InputType;

import java.util.Calendar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.R;
import com.example.cms_android.model.Medical;
import com.example.cms_android.model.Resident;
import com.example.cms_android.network.ApiService;
import com.example.cms_android.websocket.WebSocketManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MedicalActivity extends AppCompatActivity {
    private static final String EXTRA_MEDICAL_ID = "medical_id";

    private TextView tvResidentId, tvResidentName, tvIdCard, tvHospital, tvDepartment, tvDoctor,
            tvDiagnosis, tvMedicalInsuranceType, tvCheckDate, tvCreateTime, tvUpdateTime, tvRemark,
            tvMedicalType, tvLastCheckupDate, tvDischargeDate, tvDescription, tvTreatmentPlan,
            tvInsuranceType, tvMedicalExpense, tvInsuranceReimbursement, tvSelfPayAmount;
    private TextView tvResidentIdEdit;
    private EditText etResidentNameEdit, etIdCardEdit, etHospitalEdit, etDepartmentEdit, etDoctorEdit,
            etDiagnosisEdit, etCheckDateEdit, etRemarkEdit, etLastCheckupDateEdit, etDischargeDateEdit,
            etDescriptionEdit, etTreatmentPlanEdit, etMedicalExpenseEdit, etInsuranceReimbursementEdit,
            etSelfPayAmountEdit;
    private MaterialAutoCompleteTextView etMedicalInsuranceTypeEdit;
    private RadioGroup rgMedicalTypeEdit;
    private RadioButton rbVisitEdit, rbHospitalizationEdit;
    private View layoutDischargeDateEdit;
    private Button btnEdit, btnSave, btnCancel, btnDelete;
    private View viewDetails, viewEdit;

    private ApiService apiService;
    private ExecutorService executorService;
    private Long medicalId;
    private Medical medical;
    private WebSocketManager webSocketManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical);

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

        // 获取医疗ID
        medicalId = getIntent().getLongExtra(EXTRA_MEDICAL_ID, -1);
        if (medicalId == -1) {
            Toast.makeText(this, "无效的医疗ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 加载医疗详情
        loadMedicalDetails();
    }

    private void initViews() {
        // 详情视图
        viewDetails = findViewById(R.id.view_details);
        tvResidentId = findViewById(R.id.tv_resident_id);
        tvResidentName = findViewById(R.id.tv_resident_name);
        tvIdCard = findViewById(R.id.tv_id_card);
        tvHospital = findViewById(R.id.tv_hospital);
        tvDepartment = findViewById(R.id.tv_department);
        tvDoctor = findViewById(R.id.tv_doctor);
        tvDiagnosis = findViewById(R.id.tv_diagnosis);
        tvMedicalInsuranceType = findViewById(R.id.tv_medical_insurance_type);
        tvCheckDate = findViewById(R.id.tv_last_checkup_date);
        tvCreateTime = findViewById(R.id.tv_create_time);
        tvUpdateTime = findViewById(R.id.tv_update_time);
        tvRemark = findViewById(R.id.tv_remark);
        // 新增字段
        tvMedicalType = findViewById(R.id.tv_medical_type);
        tvLastCheckupDate = findViewById(R.id.tv_last_checkup_date);
        tvDischargeDate = findViewById(R.id.tv_discharge_date);
        tvDescription = findViewById(R.id.tv_description);
        tvTreatmentPlan = findViewById(R.id.tv_treatment_plan);
        tvInsuranceType = findViewById(R.id.tv_insurance_type);
        tvMedicalExpense = findViewById(R.id.tv_medical_expense);
        tvInsuranceReimbursement = findViewById(R.id.tv_insurance_reimbursement);
        tvSelfPayAmount = findViewById(R.id.tv_self_pay_amount);

        // 编辑视图
        viewEdit = findViewById(R.id.view_edit);
        tvResidentIdEdit = findViewById(R.id.tv_resident_id_edit);
        etResidentNameEdit = findViewById(R.id.et_resident_name_edit);
        etIdCardEdit = findViewById(R.id.et_id_card_edit);
        etHospitalEdit = findViewById(R.id.et_hospital_edit);
        etDepartmentEdit = findViewById(R.id.et_department_edit);
        etDoctorEdit = findViewById(R.id.et_doctor_edit);
        etDiagnosisEdit = findViewById(R.id.et_diagnosis_edit);
        etMedicalInsuranceTypeEdit = findViewById(R.id.et_insurance_type_edit);
        etCheckDateEdit = findViewById(R.id.et_last_checkup_date_edit);
        etRemarkEdit = findViewById(R.id.et_remark_edit);
        // 新增编辑字段
        etLastCheckupDateEdit = findViewById(R.id.et_last_checkup_date_edit);
        etDischargeDateEdit = findViewById(R.id.et_discharge_date_edit);
        etDescriptionEdit = findViewById(R.id.et_description_edit);
        etTreatmentPlanEdit = findViewById(R.id.et_treatment_plan_edit);
        etMedicalExpenseEdit = findViewById(R.id.et_medical_expense_edit);
        etInsuranceReimbursementEdit = findViewById(R.id.et_insurance_reimbursement_edit);
        etSelfPayAmountEdit = findViewById(R.id.et_self_pay_amount_edit);
        // 就诊类型相关控件
        rgMedicalTypeEdit = findViewById(R.id.rg_medical_type_edit);
        rbVisitEdit = findViewById(R.id.rb_visit_edit);
        rbHospitalizationEdit = findViewById(R.id.rb_hospitalization_edit);
        layoutDischargeDateEdit = findViewById(R.id.layout_discharge_date_edit);

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
        webSocketManager = WebSocketManager.getInstance();
    }

    private void setupListeners() {
        btnEdit.setOnClickListener(v -> switchToEditMode());
        btnSave.setOnClickListener(v -> saveMedicalChanges());
        btnCancel.setOnClickListener(v -> switchToDetailsMode());
        btnDelete.setOnClickListener(v -> deleteMedical());

        // 设置日期选择器监听器
        etCheckDateEdit.setOnClickListener(v -> showDatePickerDialog(etCheckDateEdit));
        etCheckDateEdit.setFocusable(false);
        etCheckDateEdit.setClickable(true);

        // 为新增的日期输入框添加点击事件
        etLastCheckupDateEdit.setOnClickListener(v -> showDatePickerDialog(etLastCheckupDateEdit));
        etLastCheckupDateEdit.setFocusable(false);
        etLastCheckupDateEdit.setClickable(true);

        etDischargeDateEdit.setOnClickListener(v -> showDatePickerDialog(etDischargeDateEdit));
        etDischargeDateEdit.setFocusable(false);
        etDischargeDateEdit.setClickable(true);

        // 设置就诊类型选择监听器
        setupMedicalTypeListener();

        // 设置保险类型下拉选项
        String[] insuranceTypes = {"城镇职工医疗保险", "城镇居民医疗保险"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, insuranceTypes);
        etMedicalInsuranceTypeEdit.setAdapter(adapter);
        // 允许点击显示下拉菜单
        etMedicalInsuranceTypeEdit.setThreshold(Integer.MAX_VALUE);
        etMedicalInsuranceTypeEdit.setOnClickListener(v -> etMedicalInsuranceTypeEdit.showDropDown());
        etMedicalInsuranceTypeEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etMedicalInsuranceTypeEdit.showDropDown();
            }
        });

    }

    /**
     * 加载医疗详情
     */
    private void loadMedicalDetails() {
        executorService.execute(() -> {
            try {
                medical = apiService.getMedicalById(medicalId);

                runOnUiThread(() -> {
                    if (medical != null) {
                        try {
                            // 获取居民信息，补充居民姓名和身份证号
                            Resident resident = apiService.getResidentById(medical.getResidentId());
                            if (resident != null) {
                                medical.setResidentName(resident.getName());
                                medical.setIdCard(resident.getIdCard());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // 调试信息：打印医疗对象的所有字段

                        displayMedicalDetails();
                    } else {
                        Toast.makeText(MedicalActivity.this, "加载医疗详情失败", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MedicalActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * 显示医疗详情
     */
    private void displayMedicalDetails() {
        // tvId.setText(String.valueOf(medical.getId())); // 移除ID显示
        tvResidentId.setText(String.valueOf(medical.getResidentId()));
        tvResidentName.setText(medical.getResidentName() != null ? medical.getResidentName() : "");
        tvIdCard.setText(medical.getIdCard() != null ? medical.getIdCard() : "");
        tvHospital.setText(medical.getHospital() != null ? medical.getHospital() : "");
        tvDepartment.setText(medical.getDepartment() != null ? medical.getDepartment() : "");
        tvDoctor.setText(medical.getDoctor() != null ? medical.getDoctor() : "");
        tvDiagnosis.setText(medical.getDiagnosis() != null ? medical.getDiagnosis() : "");

        // 确保保险类型和检查日期能够正确显示
        String insuranceType = medical.getHealthInsuranceType() != null ? medical.getHealthInsuranceType() : "";
        tvMedicalInsuranceType.setText(insuranceType);
        
        // 显示新增字段
        String medicalType = medical.getMedicalType() != null ? getMedicalTypeDisplay(medical.getMedicalType()) : "";
        tvMedicalType.setText(medicalType);
        
        String lastCheckupDateStr = medical.getLastCheckupDate() != null ? 
            medical.getLastCheckupDate().format(localDateFormatter) : "";
        tvLastCheckupDate.setText(lastCheckupDateStr);
        
        String dischargeDateStr = medical.getDischargeDate() != null ? 
            medical.getDischargeDate().format(localDateFormatter) : "";
        tvDischargeDate.setText(dischargeDateStr);
        
        tvDescription.setText(medical.getDescription() != null ? medical.getDescription() : "");
        tvTreatmentPlan.setText(medical.getTreatmentPlan() != null ? medical.getTreatmentPlan() : "");
        
        String insuranceTypeStr = medical.getInsuranceType() != null ? medical.getInsuranceType() : "";
        tvInsuranceType.setText(insuranceTypeStr);
        
        tvMedicalExpense.setText(medical.getMedicalExpense() != null ? 
            String.format("%.2f", medical.getMedicalExpense()) : "");
        tvInsuranceReimbursement.setText(medical.getInsuranceReimbursement() != null ? 
            String.format("%.2f", medical.getInsuranceReimbursement()) : "");
        tvSelfPayAmount.setText(medical.getSelfPayAmount() != null ? 
            String.format("%.2f", medical.getSelfPayAmount()) : "");

        // 调试信息：打印检查日期相关字段的原始值

        String checkDateStr = "";
        // 使用lastCheckupDate作为检查日期显示（根据服务器响应日志，这是正确的字段名）
        if (medical.getLastCheckupDate() != null) {
            checkDateStr = medical.getLastCheckupDate().format(localDateFormatter);

        } else if (medical.getCheckDate() != null) {
            checkDateStr = medical.getCheckDate().format(localDateFormatter);

        } else if (medical.getLastPhysicalExamDate() != null) {
            checkDateStr = medical.getLastPhysicalExamDate().format(localDateFormatter);
        }
        tvCheckDate.setText(checkDateStr);

        tvCreateTime.setText(medical.getCreateTime() != null
                ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(medical.getCreateTime())
                : "");
        tvUpdateTime.setText(medical.getUpdateTime() != null
                ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(medical.getUpdateTime())
                : "");
        tvRemark.setText(medical.getRemark() != null ? medical.getRemark() : "");
    }

    /**
     * 切换到编辑模式
     */
    private void switchToEditMode() {
        // 填充编辑表单，添加空检查
        // tvIdEdit.setText(String.valueOf(medical.getId())); // 移除ID显示
        tvResidentIdEdit.setText(String.valueOf(medical.getResidentId()));
        etResidentNameEdit.setText(medical.getResidentName() != null ? medical.getResidentName() : "");
        etIdCardEdit.setText(medical.getIdCard() != null ? medical.getIdCard() : "");
        etHospitalEdit.setText(medical.getHospital() != null ? medical.getHospital() : "");
        etDepartmentEdit.setText(medical.getDepartment() != null ? medical.getDepartment() : "");
        etDoctorEdit.setText(medical.getDoctor() != null ? medical.getDoctor() : "");
        etDiagnosisEdit.setText(medical.getDiagnosis() != null ? medical.getDiagnosis() : "");

        // 确保保险类型适配器被正确设置
        String[] insuranceTypes = {"城镇职工医疗保险", "城镇居民医疗保险"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, insuranceTypes);
        etMedicalInsuranceTypeEdit.setAdapter(adapter);
        // 允许点击显示下拉菜单
        etMedicalInsuranceTypeEdit.setThreshold(Integer.MAX_VALUE);
        etMedicalInsuranceTypeEdit.setOnClickListener(v -> etMedicalInsuranceTypeEdit.showDropDown());
        etMedicalInsuranceTypeEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etMedicalInsuranceTypeEdit.showDropDown();
            }
        });

        // 填充保险类型
        etMedicalInsuranceTypeEdit.setText(medical.getInsuranceType() != null ? getInsuranceTypeDisplay(medical.getInsuranceType()) : "");

        // 使用lastCheckupDate作为检查日期显示
        String checkDateStr = "";
        if (medical.getLastCheckupDate() != null) {
            checkDateStr = medical.getLastCheckupDate().format(localDateFormatter);
        }
        etCheckDateEdit.setText(checkDateStr);

        // 填充新增字段
        // 就诊类型
        if ("HOSPITALIZATION".equals(medical.getMedicalType())) {
            rbHospitalizationEdit.setChecked(true);
            layoutDischargeDateEdit.setVisibility(View.VISIBLE);
        } else {
            rbVisitEdit.setChecked(true);
            layoutDischargeDateEdit.setVisibility(View.GONE);
        }

        etLastCheckupDateEdit.setText(medical.getLastCheckupDate() != null ?
            medical.getLastCheckupDate().format(localDateFormatter) : "");
        etDischargeDateEdit.setText(medical.getDischargeDate() != null ?
            medical.getDischargeDate().format(localDateFormatter) : "");
        etDescriptionEdit.setText(medical.getDescription() != null ? medical.getDescription() : "");
        etTreatmentPlanEdit.setText(medical.getTreatmentPlan() != null ? medical.getTreatmentPlan() : "");
        etMedicalExpenseEdit.setText(medical.getMedicalExpense() != null ?
            String.valueOf(medical.getMedicalExpense()) : "");
        etInsuranceReimbursementEdit.setText(medical.getInsuranceReimbursement() != null ?
            String.valueOf(medical.getInsuranceReimbursement()) : "");
        etSelfPayAmountEdit.setText(medical.getSelfPayAmount() != null ?
            String.valueOf(medical.getSelfPayAmount()) : "");
        etRemarkEdit.setText(medical.getRemark() != null ? medical.getRemark() : "");

        // 切换视图
        viewDetails.setVisibility(View.GONE);
        viewEdit.setVisibility(View.VISIBLE);
    }

    private String getInsuranceTypeDisplay(String type) {
        if (type == null)
            return "无";
        switch (type) {
            case "URBAN_RELIANCE":
                return "城镇职工医疗保险";
            case "URBAN_RURAL_RESIDENT":
                return "城镇居民医疗保险";
            case "OTHER":
                return "其他";
            case "NONE":
                return "无";
            default:
                return type;
        }
    }
    
    /**
     * 获取就诊类型显示文本
     */
    private String getMedicalTypeDisplay(String type) {
        if (type == null)
            return "门诊";
        switch (type) {
            case "VISIT":
                return "门诊";
            case "HOSPITALIZATION":
                return "住院";
            default:
                return type;
        }
    }
    
    /**
     * 设置就诊类型选择监听器
     */
    private void setupMedicalTypeListener() {
        // 为编辑视图中的就诊类型设置监听器
        rgMedicalTypeEdit.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_hospitalization_edit) {
                layoutDischargeDateEdit.setVisibility(View.VISIBLE);
            } else {
                layoutDischargeDateEdit.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 切换到详情模式
     */
    private void switchToDetailsMode() {
        viewDetails.setVisibility(View.VISIBLE);
        viewEdit.setVisibility(View.GONE);
    }

    /**
     * 保存医疗信息更改
     */
    private void saveMedicalChanges() {
        String residentName = etResidentNameEdit.getText().toString().trim();
        String idCard = etIdCardEdit.getText().toString().trim();
        String hospital = etHospitalEdit.getText().toString().trim();
        String department = etDepartmentEdit.getText().toString().trim();
        String doctor = etDoctorEdit.getText().toString().trim();
        String diagnosis = etDiagnosisEdit.getText().toString().trim();
        String medicalInsuranceType = etMedicalInsuranceTypeEdit.getText().toString().trim();
        String checkDateStr = etCheckDateEdit.getText().toString().trim();
        String remark = etRemarkEdit.getText().toString().trim();
        
        // 获取新增字段的值
        String medicalType = rbVisitEdit.isChecked() ? "VISIT" : "HOSPITALIZATION";
        String lastCheckupDateStr = etLastCheckupDateEdit.getText().toString().trim();
        String dischargeDateStr = etDischargeDateEdit.getText().toString().trim();
        String description = etDescriptionEdit.getText().toString().trim();
        String treatmentPlan = etTreatmentPlanEdit.getText().toString().trim();
        String insuranceType = etMedicalInsuranceTypeEdit.getText().toString().trim();
        String medicalExpenseStr = etMedicalExpenseEdit.getText().toString().trim();
        String insuranceReimbursementStr = etInsuranceReimbursementEdit.getText().toString().trim();
        String selfPayAmountStr = etSelfPayAmountEdit.getText().toString().trim();

        if (residentName.isEmpty() || idCard.isEmpty() || medicalInsuranceType.isEmpty()) {
            Toast.makeText(this, "居民姓名、身份证号和保险类型不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 转换回枚举
        String typeEnum = "NONE";
        switch (medicalInsuranceType) {
            case "城镇职工基本医疗保险":
                typeEnum = "URBAN_RELIANCE";
                break;
            case "城镇居民基本医疗保险":
                typeEnum = "URBAN_RURAL_RESIDENT";
                break;
            default:
                typeEnum = medicalInsuranceType;
                break;
        }

        medical.setResidentName(residentName);
        medical.setIdCard(idCard);
        medical.setHospital(hospital);
        medical.setDepartment(department);
        medical.setDoctor(doctor);
        medical.setDiagnosis(diagnosis);
        medical.setInsuranceType(typeEnum);
        medical.setRemark(remark);
        
        // 设置新增字段
        medical.setMedicalType(medicalType);
        medical.setDescription(description);
        medical.setTreatmentPlan(treatmentPlan);
        medical.setInsuranceType(insuranceType);
        
        // 设置费用字段
        if (!medicalExpenseStr.isEmpty()) {
            try {
                medical.setMedicalExpense(Double.parseDouble(medicalExpenseStr));
            } catch (NumberFormatException e) {
                // 忽略无效数值
            }
        }
        
        if (!insuranceReimbursementStr.isEmpty()) {
            try {
                medical.setInsuranceReimbursement(Double.parseDouble(insuranceReimbursementStr));
            } catch (NumberFormatException e) {
                // 忽略无效数值
            }
        }
        
        if (!selfPayAmountStr.isEmpty()) {
            try {
                medical.setSelfPayAmount(Double.parseDouble(selfPayAmountStr));
            } catch (NumberFormatException e) {
                // 忽略无效数值
            }
        }

        // 设置检查日期
        if (!checkDateStr.isEmpty()) {
            try {
                LocalDate parsedDate = LocalDate.parse(checkDateStr, localDateFormatter);
                medical.setLastCheckupDate(parsedDate);
            } catch (Exception e) {
                Toast.makeText(this, "检查日期格式错误", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            medical.setLastCheckupDate(null);
        }
        
        // 设置就诊日期
        if (!lastCheckupDateStr.isEmpty()) {
            try {
                LocalDate lastCheckupDate = LocalDate.parse(lastCheckupDateStr, localDateFormatter);
                medical.setLastCheckupDate(lastCheckupDate);
            } catch (Exception e) {
                Toast.makeText(this, "就诊日期格式错误", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        // 设置出院日期
        if (!dischargeDateStr.isEmpty() && "HOSPITALIZATION".equals(medicalType)) {
            try {
                LocalDate dischargeDate = LocalDate.parse(dischargeDateStr, localDateFormatter);
                medical.setDischargeDate(dischargeDate);
            } catch (Exception e) {
                Toast.makeText(this, "出院日期格式错误", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // 保持remark和notes字段的一致性
        medical.setNotes(remark);

        // 保存到服务器
        executorService.execute(() -> {
            try {
                boolean success = apiService.updateMedical(medical);

                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(MedicalActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        // 通过WebSocket发送医疗数据变更通知
                        if (webSocketManager.isConnected()) {
                            webSocketManager.sendMessage("{\"type\": \"data_change\", \"module\": \"medical\", \"action\": \"update\"}");
                        }
                        // 刷新详情显示
                        displayMedicalDetails();
                        switchToDetailsMode();
                    } else {
                        Toast.makeText(MedicalActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MedicalActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * 删除医疗信息
     */
    private void deleteMedical() {
        // 显示确认对话框
        new android.app.AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除该医疗信息吗？此操作不可恢复。")
                .setPositiveButton("删除", (dialog, which) -> {
                    executorService.execute(() -> {
                        try {
                            boolean success = apiService.deleteMedical(medicalId);
                            runOnUiThread(() -> {
                                if (success) {
                                    Toast.makeText(MedicalActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    // 通过WebSocket发送医疗数据变更通知
                                    if (webSocketManager.isConnected()) {
                                        webSocketManager.sendMessage("{\"type\": \"data_change\", \"module\": \"medical\", \"action\": \"delete\"}");
                                    }
                                    finish();
                                } else {
                                    Toast.makeText(MedicalActivity.this, "删除失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(MedicalActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG)
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
                    .setMessage("您正在编辑医疗信息，是否保存更改？")
                    .setPositiveButton("保存", (dialog, which) -> {
                        // 保存更改
                        saveMedicalChanges();
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