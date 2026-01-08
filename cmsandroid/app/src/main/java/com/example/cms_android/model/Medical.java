package com.example.cms_android.model;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.JsonAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Medical {
    
    public static class LocalDateAdapter extends com.google.gson.TypeAdapter<LocalDate> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        @Override
        public void write(com.google.gson.stream.JsonWriter out, LocalDate value) throws java.io.IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(formatter.format(value));
            }
        }

        @Override
        public LocalDate read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            String dateString = in.nextString();
            if (dateString == null || dateString.isEmpty()) {
                return null;
            }
            return LocalDate.parse(dateString, formatter);
        }
    }
    @SerializedName("id")
    private Long id;
    @SerializedName("residentId")
    private Long residentId;
    @SerializedName("residentName")
    private String residentName;
    @SerializedName("idCard")
    private String idCard;
    @SerializedName("medicalType")
    private String medicalType;
    @SerializedName("diagnosis")
    private String diagnosis;
    @SerializedName("description")
    private String description;
    @SerializedName("treatmentPlan")
    private String treatmentPlan;
    @SerializedName("healthStatus")
    private String healthStatus;
    @SerializedName("insuranceType")
    private String insuranceType;
    @SerializedName("healthInsuranceType")
    private String healthInsuranceType;
    @SerializedName("medicalCardNumber")
    private String healthInsuranceNumber;
    @SerializedName("chronicDiseases")
    private String chronicDiseases;
    @SerializedName("allergies")
    private String allergies;
    @SerializedName("lastPhysicalExamDate")
    @JsonAdapter(LocalDateAdapter.class)
    private LocalDate lastPhysicalExamDate;
    @SerializedName("bloodType")
    private String bloodType;
    @SerializedName("height")
    private String height;
    @SerializedName("weight")
    private String weight;
    @SerializedName("hospital")
    private String hospital;
    @SerializedName("department")
    private String department;
    @SerializedName("doctor")
    private String doctor;
    @SerializedName("checkDate")
    @JsonAdapter(LocalDateAdapter.class)
    private LocalDate checkDate;
    @SerializedName("lastCheckupDate")
    @JsonAdapter(LocalDateAdapter.class)
    private LocalDate lastCheckupDate;
    @SerializedName("dischargeDate")
    @JsonAdapter(LocalDateAdapter.class)
    private LocalDate dischargeDate;
    @SerializedName("medicalExpense")
    private Double medicalExpense;
    @SerializedName("insuranceReimbursement")
    private Double insuranceReimbursement;
    @SerializedName("selfPayAmount")
    private Double selfPayAmount;
    @SerializedName("remark")
    private String remark;
    @SerializedName("notes")
    private String notes;
    @SerializedName("createTime")
    private Date createTime;
    @SerializedName("updateTime")
    private Date updateTime;

    public Medical() {
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public String getResidentName() {
        return residentName;
    }

    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getHealthInsuranceType() {
        return healthInsuranceType;
    }

    public void setHealthInsuranceType(String healthInsuranceType) {
        this.healthInsuranceType = healthInsuranceType;
    }

    public String getHealthInsuranceNumber() {
        return healthInsuranceNumber;
    }

    public void setHealthInsuranceNumber(String healthInsuranceNumber) {
        this.healthInsuranceNumber = healthInsuranceNumber;
    }

    public String getChronicDiseases() {
        return chronicDiseases;
    }

    public void setChronicDiseases(String chronicDiseases) {
        this.chronicDiseases = chronicDiseases;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public LocalDate getLastPhysicalExamDate() {
        return lastPhysicalExamDate;
    }

    public void setLastPhysicalExamDate(LocalDate lastPhysicalExamDate) {
        this.lastPhysicalExamDate = lastPhysicalExamDate;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public LocalDate getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(LocalDate checkDate) {
        this.checkDate = checkDate;
    }

    public LocalDate getLastCheckupDate() {
        return lastCheckupDate;
    }

    public void setLastCheckupDate(LocalDate lastCheckupDate) {
        this.lastCheckupDate = lastCheckupDate;
    }

    public String getMedicalType() {
        return medicalType;
    }

    public void setMedicalType(String medicalType) {
        this.medicalType = medicalType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public LocalDate getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(LocalDate dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public Double getMedicalExpense() {
        return medicalExpense;
    }

    public void setMedicalExpense(Double medicalExpense) {
        this.medicalExpense = medicalExpense;
    }

    public Double getInsuranceReimbursement() {
        return insuranceReimbursement;
    }

    public void setInsuranceReimbursement(Double insuranceReimbursement) {
        this.insuranceReimbursement = insuranceReimbursement;
    }

    public Double getSelfPayAmount() {
        return selfPayAmount;
    }

    public void setSelfPayAmount(Double selfPayAmount) {
        this.selfPayAmount = selfPayAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}