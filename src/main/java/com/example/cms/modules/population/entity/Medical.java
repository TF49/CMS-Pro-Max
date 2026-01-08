package com.example.cms.modules.population.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Medical {
    private Long id; // 主键ID
    private Long residentId; // 居民ID
    private String residentName; // 居民姓名
    private String idCard; // 居民身份证号
    private String hospital; // 医院
    private String department; // 科室
    private String doctor; // 医生
    private String diagnosis; // 诊断
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastCheckupDate; // 检查日期
    private String insuranceType; // 保险类型
    private String notes; // 备注
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime; // 更新时间
    private Long createdBy; // 创建者ID
    
    // 验证居民信息是否完整
    public boolean hasValidResidentInfo() {
        return residentName != null && !residentName.trim().isEmpty() &&
               idCard != null && !idCard.trim().isEmpty();
    }
    
    // 验证医疗信息是否完整
    public boolean hasValidMedicalInfo() {
        return hospital != null && !hospital.trim().isEmpty() &&
               department != null && !department.trim().isEmpty() &&
               diagnosis != null && !diagnosis.trim().isEmpty() &&
               lastCheckupDate != null;
    }
}