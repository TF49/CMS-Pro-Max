package com.example.cms_android.AddActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.R;
import com.example.cms_android.model.Medical;
import com.example.cms_android.network.ApiService;
import com.example.cms_android.websocket.WebSocketManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.text.InputType;
import java.util.Calendar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AddMedicalActivity extends AppCompatActivity {
    private TextInputEditText etResidentName, etIdCard, etHospital, etDoctor, etDepartment,
            etLastCheckupDate, etDischargeDate, etDiagnosis, etDescription, etTreatmentPlan,
            etMedicalExpense, etInsuranceReimbursement, etSelfPayAmount, etNotes;
    private MaterialAutoCompleteTextView etInsuranceType;
    private RadioGroup rgMedicalType;
    private RadioButton rbVisit, rbHospitalization;
    private View layoutDischargeDate;
    private Button btnSave;
    private ProgressBar progressBar;

    private ApiService apiService;
    private ExecutorService executorService;
    private WebSocketManager webSocketManager;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        /**
         * 显示日期选择对话框
         */
        private void showDatePickerDialog(TextInputEditText targetEditText) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
    
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        targetEditText.setText(date);
                    },
                    year, month, day);
    
            datePickerDialog.show();
        }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medical);
        
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
                setupMedicalTypeListener();
    }

    private void initViews() {
        etResidentName = findViewById(R.id.et_resident_name);
        etIdCard = findViewById(R.id.et_id_card);
        etHospital = findViewById(R.id.et_hospital);
        etDoctor = findViewById(R.id.et_doctor);
        etDepartment = findViewById(R.id.et_department);
        etLastCheckupDate = findViewById(R.id.et_last_checkup_date);
        etDischargeDate = findViewById(R.id.et_discharge_date);
        etDiagnosis = findViewById(R.id.et_diagnosis);
        etDescription = findViewById(R.id.et_description);
        etTreatmentPlan = findViewById(R.id.et_treatment_plan);
        etInsuranceType = findViewById(R.id.et_insurance_type);

        etMedicalExpense = findViewById(R.id.et_medical_expense);
        etInsuranceReimbursement = findViewById(R.id.et_insurance_reimbursement);
        etSelfPayAmount = findViewById(R.id.et_self_pay_amount);
        etNotes = findViewById(R.id.et_notes);
        rgMedicalType = findViewById(R.id.rg_medical_type);
        rbVisit = findViewById(R.id.rb_visit);
        rbHospitalization = findViewById(R.id.rb_hospitalization);
        layoutDischargeDate = findViewById(R.id.layout_discharge_date);
        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void initServices() {
        apiService = new ApiService();
        executorService = Executors.newSingleThreadExecutor();
        webSocketManager = WebSocketManager.getInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveMedical());
        // 为日期输入框添加点击事件
        etLastCheckupDate.setOnClickListener(v -> showDatePickerDialog(etLastCheckupDate));
        etDischargeDate.setOnClickListener(v -> showDatePickerDialog(etDischargeDate));

        // 设置保险类型下拉选项
        String[] insuranceTypes = {"城镇职工医疗保险", "城镇居民医疗保险"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, insuranceTypes);
        etInsuranceType.setAdapter(adapter);
        // 允许点击显示下拉菜单
        etInsuranceType.setThreshold(Integer.MAX_VALUE);
        etInsuranceType.setOnClickListener(v -> etInsuranceType.showDropDown());
        etInsuranceType.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etInsuranceType.showDropDown();
            }
        });
    }
    
    private void setupMedicalTypeListener() {
        rgMedicalType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_hospitalization) {
                layoutDischargeDate.setVisibility(View.VISIBLE);
            } else {
                layoutDischargeDate.setVisibility(View.GONE);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveMedical() {
        String residentName = etResidentName.getText().toString().trim();
        String idCard = etIdCard.getText().toString().trim();
        String hospital = etHospital.getText().toString().trim();
        String doctor = etDoctor.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String lastCheckupDateStr = etLastCheckupDate.getText().toString().trim();
        String dischargeDateStr = etDischargeDate.getText().toString().trim();
        String diagnosis = etDiagnosis.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String treatmentPlan = etTreatmentPlan.getText().toString().trim();
        String insuranceType = etInsuranceType.getText().toString().trim();
        String healthInsuranceNumber = "";
        String healthStatus = "";
        String chronicDiseases = "";
        String allergies = "";
        String medicalExpenseStr = etMedicalExpense.getText().toString().trim();
        String insuranceReimbursementStr = etInsuranceReimbursement.getText().toString().trim();
        String selfPayAmountStr = etSelfPayAmount.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        
        String medicalType = rbVisit.isChecked() ? "VISIT" : "HOSPITALIZATION";

        if (residentName.isEmpty() || idCard.isEmpty() || hospital.isEmpty() || doctor.isEmpty() || 
            diagnosis.isEmpty() || insuranceType.isEmpty()) {
            Toast.makeText(this, "请填写必填信息", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(true);

        Medical medical = new Medical();
        medical.setResidentName(residentName);
        medical.setIdCard(idCard);
        medical.setHospital(hospital);
        medical.setDoctor(doctor);
        medical.setDepartment(department);
        medical.setDiagnosis(diagnosis);
        medical.setDescription(description);
        medical.setTreatmentPlan(treatmentPlan);
        medical.setInsuranceType(insuranceType);
        medical.setHealthInsuranceNumber(healthInsuranceNumber);
        medical.setHealthStatus(healthStatus);
        medical.setChronicDiseases(chronicDiseases);
        medical.setAllergies(allergies);
        medical.setNotes(notes);
        medical.setMedicalType(medicalType);
        
        // 处理日期字段
        if (!lastCheckupDateStr.isEmpty()) {
            try {
                LocalDate lastCheckupDate = LocalDate.parse(lastCheckupDateStr, dateFormatter);
                medical.setLastCheckupDate(lastCheckupDate);
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(this, "就诊日期格式错误，请使用 YYYY-MM-DD 格式", Toast.LENGTH_SHORT).show();
                });
                return;
            }
        }
        
        if (!dischargeDateStr.isEmpty() && rbHospitalization.isChecked()) {
            try {
                LocalDate dischargeDate = LocalDate.parse(dischargeDateStr, dateFormatter);
                medical.setDischargeDate(dischargeDate);
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(this, "出院日期格式错误，请使用 YYYY-MM-DD 格式", Toast.LENGTH_SHORT).show();
                });
                return;
            }
        }
        
        // 处理费用字段
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

        executorService.execute(() -> {
            try {
                boolean success = apiService.addMedical(medical);

                runOnUiThread(() -> {
                    showProgress(false);

                    if (success) {
                        Toast.makeText(AddMedicalActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        // 通过WebSocket发送医疗数据变更通知
                        if (webSocketManager.isConnected()) {
                            webSocketManager.sendMessage("{\"type\": \"data_change\", \"module\": \"medical\", \"action\": \"add\"}");
                        }
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AddMedicalActivity.this, "保存失败", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(AddMedicalActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}