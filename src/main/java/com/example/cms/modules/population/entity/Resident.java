package com.example.cms.modules.population.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Resident {
    private Long id;
    private String name; // 姓名
    private String idCard; // 身份证号
    private String gender; // 性别
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate; // 出生日期
    private Long householdId; // 户籍ID
    private String relationship; // 与户主关系
    private String ethnicGroup; // 民族
    private String educationLevel; // 教育程度
    private String occupation; // 职业
    private String maritalStatus; // 婚姻状况
    private String phoneNumber; // 联系电话
    private Boolean isHouseholder; // 是否户主
    private String notes; // 备注
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime; // 更新时间
    private String householdNumber; // 户籍编号（用于关联查询）
    private Long createdBy; // 创建者ID
    
    // 验证居民基本信息是否完整
    public boolean hasValidBasicInfo() {
        return name != null && !name.trim().isEmpty() &&
               idCard != null && !idCard.trim().isEmpty() &&
               gender != null && !gender.trim().isEmpty() &&
               birthDate != null;
    }
}