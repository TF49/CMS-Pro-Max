package com.example.cms.modules.population.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SocialSecurity {
    private Long id;
    private Long residentId; // 居民ID
    private String residentName; // 居民姓名
    private String idCard; // 身份证号码
    private String pensionInsurance; // 养老保险
    private String medicalInsurance; // 医疗保险
    private String unemploymentInsurance; // 失业保险
    private String workInjuryInsurance; // 工伤保险
    private String maternityInsurance; // 生育保险
    private String insuranceStatus; // 参保状态
    private String paymentBase; // 缴费基数
    private BigDecimal paymentAmount; // 缴费金额
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate; // 参保日期
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate; // 停保日期
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
    
    // 验证社保信息是否完整
    public boolean hasValidSocialSecurityInfo() {
        return pensionInsurance != null && !pensionInsurance.trim().isEmpty() &&
               medicalInsurance != null && !medicalInsurance.trim().isEmpty() &&
               insuranceStatus != null && !insuranceStatus.trim().isEmpty() &&
               paymentAmount != null && paymentAmount.compareTo(BigDecimal.ZERO) >= 0 &&
               startDate != null;
    }
}