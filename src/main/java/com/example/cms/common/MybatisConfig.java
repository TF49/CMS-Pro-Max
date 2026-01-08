package com.example.cms.common;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 */
@Configuration
@MapperScan(basePackages = {"com.example.cms.modules.system.mapper", "com.example.cms.modules.population.mapper"})
public class MybatisConfig {
}