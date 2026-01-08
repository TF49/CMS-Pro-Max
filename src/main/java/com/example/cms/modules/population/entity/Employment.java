package com.example.cms.modules.population.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
public class Employment {
    private Long id;
    private Long residentId; // 居民ID
    private String residentName; // 居民姓名
    private String idCard; // 身份证号码
    private String company; // 工作单位
    private String position; // 职位
    private java.math.BigDecimal salary; // 薪资
    private String employmentStatus; // 就业状态（在职、失业、退休等）
    private String industry; // 行业
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate; // 入职日期
    private String contractType; // 合同类型（固定期限、无固定期限等）
    private String notes; // 备注
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime; // 更新时间
    private Long createdBy; // 创建者ID
    
    // 验证居民信息是否完整
    public boolean hasValidResidentInfo() {
        return residentId != null && residentId > 0 &&
               residentName != null && !residentName.trim().isEmpty() &&
               idCard != null && !idCard.trim().isEmpty();
    }
    
    // 验证就业信息是否完整
    public boolean hasValidEmploymentInfo() {
        return company != null && !company.trim().isEmpty() &&
               position != null && !position.trim().isEmpty() &&
               employmentStatus != null && !employmentStatus.trim().isEmpty();
    }
}