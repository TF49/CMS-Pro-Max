package com.example.cms.modules.population.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Vehicle {
    private Long id;
    private Long residentId; // 居民ID
    private String residentName; // 居民姓名
    private String idCard; // 居民身份证号
    private String licensePlate; // 车牌号
    private String vehicleType; // 车辆类型（轿车、SUV、货车等）
    private String brand; // 品牌
    private String model; // 型号
    private String color; // 颜色
    private String engineNumber; // 发动机号
    private String chassisNumber; // 车架号
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate; // 购买日期
    private BigDecimal purchasePrice; // 购买价格
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate insuranceExpiryDate; // 保险到期日期
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate annualInspectionDate; // 年检日期
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
    
    // 验证车辆信息是否完整
    public boolean hasValidVehicleInfo() {
        return licensePlate != null && !licensePlate.trim().isEmpty() &&
               vehicleType != null && !vehicleType.trim().isEmpty() &&
               brand != null && !brand.trim().isEmpty() &&
               model != null && !model.trim().isEmpty() &&
               purchaseDate != null;
    }
}