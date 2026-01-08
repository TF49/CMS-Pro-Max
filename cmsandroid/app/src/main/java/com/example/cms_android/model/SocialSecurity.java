package com.example.cms_android.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SocialSecurity {
    @RequiresApi(api = Build.VERSION_CODES.O)
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
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static class LocalDateTimeAdapter extends com.google.gson.TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void write(com.google.gson.stream.JsonWriter out, LocalDateTime value) throws java.io.IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(formatter.format(value));
            }
        }

        @Override
        public LocalDateTime read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
            String dateString = in.nextString();
            if (dateString == null || dateString.isEmpty()) {
                return null;
            }
            return LocalDateTime.parse(dateString, formatter);
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
    
    @SerializedName("pensionInsurance")
    private String pensionInsurance;
    
    @SerializedName("medicalInsurance")
    private String medicalInsurance;
    
    @SerializedName("unemploymentInsurance")
    private String unemploymentInsurance;
    
    @SerializedName("workInjuryInsurance")
    private String workInjuryInsurance;
    
    @SerializedName("maternityInsurance")
    private String maternityInsurance;
    
    @SerializedName("insuranceStatus")
    private String insuranceStatus;
    
    @SerializedName("paymentBase")
    private String paymentBase;
    
    @SerializedName("paymentAmount")
    private BigDecimal paymentAmount;
    
    @SerializedName("startDate")
    private LocalDate startDate;
    
    @SerializedName("endDate")
    private LocalDate endDate;
    
    @SerializedName("notes")
    private String notes;
    
    @SerializedName("createTime")
    private LocalDateTime createTime;
    
    @SerializedName("updateTime")
    private LocalDateTime updateTime;
    
    @SerializedName("createdBy")
    private Long createdBy;

    public SocialSecurity() {
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

    public String getInsuranceStatus() {
        return insuranceStatus;
    }

    public void setInsuranceStatus(String insuranceStatus) {
        this.insuranceStatus = insuranceStatus;
    }

    public java.math.BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(java.math.BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
    
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
    
    public String getPaymentBase() {
        return paymentBase;
    }

    public void setPaymentBase(String paymentBase) {
        this.paymentBase = paymentBase;
    }
    
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getPensionInsurance() {
        return pensionInsurance;
    }

    public void setPensionInsurance(String pensionInsurance) {
        this.pensionInsurance = pensionInsurance;
    }

    public String getMedicalInsurance() {
        return medicalInsurance;
    }

    public void setMedicalInsurance(String medicalInsurance) {
        this.medicalInsurance = medicalInsurance;
    }

    public String getUnemploymentInsurance() {
        return unemploymentInsurance;
    }

    public void setUnemploymentInsurance(String unemploymentInsurance) {
        this.unemploymentInsurance = unemploymentInsurance;
    }

    public String getWorkInjuryInsurance() {
        return workInjuryInsurance;
    }

    public void setWorkInjuryInsurance(String workInjuryInsurance) {
        this.workInjuryInsurance = workInjuryInsurance;
    }

    public String getMaternityInsurance() {
        return maternityInsurance;
    }

    public void setMaternityInsurance(String maternityInsurance) {
        this.maternityInsurance = maternityInsurance;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }


}