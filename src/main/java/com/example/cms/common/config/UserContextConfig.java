package com.example.cms.common.config;

import com.example.cms.common.JwtUtils;
import com.example.cms.common.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 用户上下文配置类
 * 用于初始化UserContext类的jwtUtils实例
 */
@Configuration
public class UserContextConfig {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 初始化UserContext类的jwtUtils实例
     */
    @PostConstruct
    public void init() {
        UserContext.setJwtUtils(jwtUtils);
    }
}
