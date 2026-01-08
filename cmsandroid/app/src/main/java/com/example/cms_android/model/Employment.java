package com.example.cms_android.model;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Employment {
    @SerializedName("id")
    private Long id;
    @SerializedName("residentId")
    private Long residentId;
    @SerializedName("residentName")
    private String residentName;
    @SerializedName("idCard")
    private String idCard;
    @SerializedName("company")
    private String company;
    @SerializedName("position")
    private String position;
    @SerializedName("industry")
    private String industry;
    @SerializedName("employmentStatus")
    private String employmentStatus;
    @SerializedName("contractType")
    private String contractType;
    @SerializedName("startDate")
    private LocalDate startDate;
    @SerializedName("endDate")
    private LocalDate endDate;
    @SerializedName("salary")
    private java.math.BigDecimal salary;
    @SerializedName("notes")
    private String notes;
    @SerializedName("createTime")
    private LocalDateTime createTime;
    @SerializedName("updateTime")
    private LocalDateTime updateTime;
    @SerializedName("createdBy")
    private Long createdBy;
    @SerializedName("remark")
    private String remark;

    public Employment() {
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
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

    public java.math.BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(java.math.BigDecimal salary) {
        this.salary = salary;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}