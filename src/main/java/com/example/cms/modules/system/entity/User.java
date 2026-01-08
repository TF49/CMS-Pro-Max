package com.example.cms.modules.system.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 用于系统认证和用户管理
 */
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String role;
    private String phone;
    private String idCard;
    private Long householdId; // 关联的户籍ID
    private LocalDateTime createTime;
    private LocalDateTime lastLoginTime;
    private Boolean isActive;
}