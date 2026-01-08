package com.example.cms.modules.population.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教育记录实体类
 * 对应数据库中的education表
 */
@Data
public class Education {
    private Long id; // 主键ID
    private Long residentId; // 居民ID
    private String residentName; // 居民姓名
    private String idCard; // 身份证号码
    private String schoolName; // 学校名称
    private String educationLevel; // 教育程度（小学、初中、高中、大专、本科、硕士、博士）
    private String major; // 专业
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate enrollmentDate; // 入学日期
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate graduationDate; // 毕业日期
    private String status; // 状态（在读、毕业、休学、退学）
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
    
    // 验证教育信息是否完整
    public boolean hasValidEducationInfo() {
        return schoolName != null && !schoolName.trim().isEmpty() &&
               educationLevel != null && !educationLevel.trim().isEmpty() &&
               enrollmentDate != null &&
               status != null && !status.trim().isEmpty();
    }
}