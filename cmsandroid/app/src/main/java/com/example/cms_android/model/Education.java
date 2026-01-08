package com.example.cms_android.model;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.JsonAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Education {
    
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
    private Long id;
    @SerializedName("residentId")
    private Long residentId;
    @SerializedName("residentName")
    private String residentName;
    @SerializedName("idCard")
    private String idCard;
    @SerializedName("educationLevel")
    private String educationLevel;
    @SerializedName("schoolName")
    private String schoolName;
    @SerializedName("major")
    private String major;
    @SerializedName("enrollmentDate")
    @JsonAdapter(LocalDateAdapter.class)
    private LocalDate startDate;
    @SerializedName("graduationDate")
    @JsonAdapter(LocalDateAdapter.class)
    private LocalDate endDate;
    @SerializedName("diplomaType")
    private String diplomaType;
    @SerializedName("status")
    private String status;
    @SerializedName("notes")
    private String notes;
    @SerializedName("createTime")
    private Date createTime;
    @SerializedName("updateTime")
    private Date updateTime;

    public Education() {
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

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
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

    public String getDiplomaType() {
        return diplomaType;
    }

    public void setDiplomaType(String diplomaType) {
        this.diplomaType = diplomaType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getResidentName() {
        return residentName;
    }

    public void setResidentName(String residentName) {
        this.residentName = residentName;
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

    @Override
    public String toString() {
        return "Education{" +
                "id=" + id +
                ", residentId=" + residentId +
                ", residentName='" + residentName + '\'' +
                ", idCard='" + idCard + '\'' +
                ", educationLevel='" + educationLevel + '\'' +
                ", schoolName='" + schoolName + '\'' +
                ", major='" + major + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", diplomaType='" + diplomaType + '\'' +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}