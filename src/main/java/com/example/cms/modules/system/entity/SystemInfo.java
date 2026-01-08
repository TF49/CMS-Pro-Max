package com.example.cms.modules.system.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统信息实体类
 */
@Data
public class SystemInfo {
    private String version;
    private String appName;
    private LocalDateTime buildTime = LocalDateTime.now();
}