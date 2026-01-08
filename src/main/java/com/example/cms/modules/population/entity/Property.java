package com.example.cms.modules.population.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Property {
    private Long id;
    private Long residentId; // 居民ID
    private String residentName; // 居民姓名
    private String idCard; // 居民身份证号
    private String propertyType; // 房产类型（商品房、经济适用房、农村自建房等）
    private String address; // 房产地址
    private BigDecimal area; // 面积（平方米）
    private BigDecimal value; // 估值
    private String propertyCertificate; // 产权证号
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate acquisitionDate; // 取得日期
    private String usageType; // 使用类型（自住、出租、闲置等）
    private Integer rooms; // 房间数
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
    
    // 验证房产信息是否完整
    public boolean hasValidPropertyInfo() {
        return propertyType != null && !propertyType.trim().isEmpty() &&
               address != null && !address.trim().isEmpty() &&
               area != null && area.compareTo(BigDecimal.ZERO) > 0 &&
               value != null && value.compareTo(BigDecimal.ZERO) >= 0 &&
               acquisitionDate != null;
    }
}