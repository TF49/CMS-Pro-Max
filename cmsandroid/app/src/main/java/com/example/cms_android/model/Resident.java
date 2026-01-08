package com.example.cms_android.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Resident {
    @SerializedName("id")
    private Long id;
    @SerializedName("name")
    private String name;
    @SerializedName("idCard")
    private String idCard;
    @SerializedName("gender")
    private String gender;
    @SerializedName("birthDate")
    private Date birthDate;
    @SerializedName("age")
    private Integer age;
    @SerializedName("householdId")
    private Long householdId;
    @SerializedName("householdType")
    private String householdType;
    @SerializedName("relationship")
    private String relationship;
    @SerializedName("ethnicGroup")
    private String ethnicGroup;
    @SerializedName("nationality")
    private String nationality;
    @SerializedName("educationLevel")
    private String educationLevel;
    @SerializedName("education")
    private String education;
    @SerializedName("occupation")
    private String occupation;
    @SerializedName("income")
    private Double income;
    @SerializedName("maritalStatus")
    private String maritalStatus;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("email")
    private String email;
    @SerializedName("address")
    private String address;
    @SerializedName("isHouseholder")
    private Boolean isHouseholder;
    @SerializedName("notes")
    private String notes;
    @SerializedName("createTime")
    private Date createTime;
    @SerializedName("updateTime")
    private Date updateTime;
    @SerializedName("registerDate")
    private Date registerDate;
    @SerializedName("updateDate")
    private Date updateDate;
    @SerializedName("householdNumber")
    private String householdNumber;
    @SerializedName("createdBy")
    private Long createdBy;

    public Resident() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Long getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(Long householdId) {
        this.householdId = householdId;
    }

    public String getHouseholdType() {
        return householdType;
    }

    public void setHouseholdType(String householdType) {
        this.householdType = householdType;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getEthnicGroup() {
        return ethnicGroup;
    }

    public void setEthnicGroup(String ethnicGroup) {
        this.ethnicGroup = ethnicGroup;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getIsHouseholder() {
        return isHouseholder;
    }

    public void setIsHouseholder(Boolean isHouseholder) {
        this.isHouseholder = isHouseholder;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getHouseholdNumber() {
        return householdNumber;
    }

    public void setHouseholdNumber(String householdNumber) {
        this.householdNumber = householdNumber;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}