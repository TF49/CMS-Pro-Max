package com.example.cms_android.model;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Vehicle {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("residentId")
    private Long residentId;
    
    @SerializedName("residentName")
    private String residentName;
    
    @SerializedName("idCard")
    private String idCard;
    
    @SerializedName("licensePlate")
    private String licensePlate;
    
    @SerializedName("vehicleType")
    private String vehicleType;
    
    @SerializedName("brand")
    private String brand;
    
    @SerializedName("model")
    private String model;
    
    @SerializedName("color")
    private String color;
    
    @SerializedName("engineNumber")
    private String engineNumber;
    
    @SerializedName("chassisNumber")
    private String chassisNumber;
    
    @SerializedName("purchaseDate")
    private LocalDate purchaseDate;
    
    @SerializedName("purchasePrice")
    private BigDecimal purchasePrice;
    
    @SerializedName("insuranceExpiryDate")
    private LocalDate insuranceExpiryDate;
    
    @SerializedName("annualInspectionDate")
    private LocalDate annualInspectionDate;
    
    @SerializedName("inspectionDate")
    private LocalDate inspectionDate;
    
    @SerializedName("registrationDate")

    private LocalDate registrationDate;
    
    @SerializedName("createTime")
    private LocalDateTime createTime;
    
    @SerializedName("updateTime")
    private LocalDateTime updateTime;
    
    @SerializedName("note")
    private String note;

    public Vehicle() {
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

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public java.math.BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(java.math.BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public LocalDate getInsuranceExpiryDate() {
        return insuranceExpiryDate;
    }

    public void setInsuranceExpiryDate(LocalDate insuranceExpiryDate) {
        this.insuranceExpiryDate = insuranceExpiryDate;
    }

    public LocalDate getAnnualInspectionDate() {
        return annualInspectionDate;
    }

    public void setAnnualInspectionDate(LocalDate annualInspectionDate) {
        this.annualInspectionDate = annualInspectionDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDate getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(LocalDate inspectionDate) {
        this.inspectionDate = inspectionDate;
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