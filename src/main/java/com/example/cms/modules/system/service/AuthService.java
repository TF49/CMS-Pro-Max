package com.example.cms.modules.system.service;

import com.example.cms.modules.system.entity.User;

/**
 * 认证业务接口
 */
public interface AuthService {
    
    /**
     * 用户登录
     */
    AuthResult login(String username, String password);
    
    /**
     * 用户注册
     */
    boolean register(User user);
    
    /**
     * 验证Token
     */
    boolean validateToken(String token);
    
    /**
     * 获取当前用户信息
     */
    User getCurrentUser(String token);
    
    /**
     * 发送重置密码邮件
     */
    boolean sendResetPasswordEmail(String email);
    
    /**
     * 验证重置密码Token
     */
    boolean verifyResetToken(String token);
    
    /**
     * 重置密码
     */
    boolean resetPassword(String username, String idCard, String newPassword);
    
    /**
     * 基于令牌重置密码
     */
    boolean resetPassword(String token, String newPassword);
    
    /**
     * 验证用户身份（用户名+身份证号）
     */
    boolean verifyUserIdentity(String username, String idCard);
}