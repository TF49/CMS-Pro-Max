package com.example.cms.modules.system.controller;

import com.example.cms.common.Result;
import com.example.cms.framework.web.BaseController;
import com.example.cms.modules.system.entity.SystemInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/system")
public class SystemController extends BaseController {
    
    @GetMapping("/info")
    public Result<SystemInfo> getSystemInfo() {
        SystemInfo systemInfo = new SystemInfo();
        systemInfo.setVersion("v1.0");
        systemInfo.setAppName("人口普查管理系统");
        systemInfo.setBuildTime(LocalDateTime.now());
        return success(systemInfo);
    }
}