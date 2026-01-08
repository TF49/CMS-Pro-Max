package com.example.cms.common.config;

import com.example.cms.common.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置类
 * 用于配置权限控制规则和过滤器
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 启用CORS配置，使用Spring MVC的CORS配置
            .cors()
            .and()
            // 关闭CSRF保护
            .csrf().disable()
            // 不使用Session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // 配置权限控制规则
            .authorizeRequests()
                // 允许匿名访问的接口
                .antMatchers("/auth/**", "/api/auth/**").permitAll()
                // 允许WebSocket端点访问（握手阶段）
                .antMatchers("/ws/**").permitAll()
                // 允许直接访问的API接口（适配小程序请求）
                .antMatchers("/resident/**", "/household/**", "/education/**", "/medical/**", "/employment/**", "/social-security/**", "/property/**").authenticated()
                // 其他接口需要认证
                .anyRequest().authenticated()
            .and()
            // 添加JWT认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
