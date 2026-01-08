package com.example.cms.modules.system.service;

import com.example.cms.modules.system.entity.User;

/**
 * 登录结果类
 */
public class AuthResult {
    private boolean success;
    private String token;
    private String message;
    private User user;
    
    public AuthResult(boolean success, String token, String message, User user) {
        this.success = success;
        this.token = token;
        this.message = message;
        this.user = user;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
}