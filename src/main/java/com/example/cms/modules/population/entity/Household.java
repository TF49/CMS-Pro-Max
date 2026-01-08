package com.example.cms.modules.population.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Household {
    private Long id;
    private String householdNumber; // 户籍编号
    private String address; // 户籍地址
    private String householderName; // 户主姓名
    private String householderIdCard; // 户主身份证号
    private String phoneNumber; // 联系电话
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String registrationDate; // 登记日期
    private Integer populationCount; // 人口数量
    private String notes; // 备注
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime; // 更新时间
    private Long createdBy; // 创建者ID
    
    // 验证户籍信息是否完整
    public boolean hasValidHouseholdInfo() {
        return householdNumber != null && !householdNumber.trim().isEmpty() &&
               address != null && !address.trim().isEmpty() &&
               householderName != null && !householderName.trim().isEmpty() &&
               householderIdCard != null && !householderIdCard.trim().isEmpty();
    }
}