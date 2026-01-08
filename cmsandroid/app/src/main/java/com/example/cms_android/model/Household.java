package com.example.cms_android.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Household {
    @SerializedName("id")
    private Long id;
    @SerializedName("householdNumber")
    private String householdNumber;
    @SerializedName("address")
    private String address;
    @SerializedName("householderName")
    private String headOfHouseholdName;
    @SerializedName("householderIdCard")
    private String headOfHouseholdIdCard;
    @SerializedName("createTime")
    private Date createTime;
    @SerializedName("updateTime")
    private Date updateTime;
    @SerializedName("populationCount")
    private Integer populationCount;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("registrationDate")
    private Date registrationDate;
    @SerializedName("notes")
    private String notes;
    @SerializedName("status")
    private String status;
    @SerializedName("householdType")
    private String householdType;

    public Household() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHouseholdNumber() {
        return householdNumber;
    }

    public void setHouseholdNumber(String householdNumber) {
        this.householdNumber = householdNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHeadOfHouseholdName() {
        return headOfHouseholdName;
    }

    public void setHeadOfHouseholdName(String headOfHouseholdName) {
        this.headOfHouseholdName = headOfHouseholdName;
    }

    public String getHeadOfHouseholdIdCard() {
        return headOfHouseholdIdCard;
    }

    public void setHeadOfHouseholdIdCard(String headOfHouseholdIdCard) {
        this.headOfHouseholdIdCard = headOfHouseholdIdCard;
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

    public Integer getPopulationCount() {
        return populationCount;
    }

    public void setPopulationCount(Integer populationCount) {
        this.populationCount = populationCount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHouseholdType() {
        return householdType;
    }

    public void setHouseholdType(String householdType) {
        this.householdType = householdType;
    }
}