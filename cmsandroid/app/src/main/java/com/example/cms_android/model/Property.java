package com.example.cms_android.model;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
public class Property {
    @SerializedName("id")
    private Long id;
    @SerializedName("residentId")
    private Long residentId;
    @SerializedName("residentName")
    private String residentName;
    @SerializedName("idCard")
    private String idCard;
    @SerializedName("propertyType")
    private String propertyType;
    @SerializedName("address")
    private String address;
    @SerializedName("area")
    private BigDecimal area;
    @SerializedName("propertyCertificate")
    private String propertyCertificateNumber;
    @SerializedName("usageType")
    private String useType;
    @SerializedName("value")
    private BigDecimal price;
    @SerializedName("acquisitionDate")
    private LocalDate purchaseDate;
    @SerializedName("createTime")
    private LocalDateTime createTime;
    @SerializedName("updateTime")
    private LocalDateTime updateTime;
    @SerializedName("rooms")
    private Integer rooms;
    @SerializedName("notes")
    private String notes;
    
    public Property() {
    }

    // Getters and Setters
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

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public java.math.BigDecimal getArea() {
        return area;
    }

    public void setArea(java.math.BigDecimal area) {
        this.area = area;
    }

    public String getPropertyCertificateNumber() {
        return propertyCertificateNumber;
    }

    public void setPropertyCertificateNumber(String propertyCertificateNumber) {
        this.propertyCertificateNumber = propertyCertificateNumber;
    }

    public String getUseType() {
        return useType;
    }

    public void setUseType(String useType) {
        this.useType = useType;
    }

    public java.math.BigDecimal getPrice() {
        return price;
    }

    public void setPrice(java.math.BigDecimal price) {
        this.price = price;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
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

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}