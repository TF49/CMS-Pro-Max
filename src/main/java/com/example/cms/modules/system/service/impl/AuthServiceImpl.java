package com.example.cms.modules.system.service.impl;

import com.example.cms.common.JwtUtils;
import com.example.cms.modules.system.entity.User;
import com.example.cms.modules.system.service.AuthResult;
import com.example.cms.modules.system.service.AuthService;
import com.example.cms.modules.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 认证业务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Override
    public AuthResult login(String username, String password) {
        try {
            // 查找用户
            User user = userService.findByUsername(username);
            if (user == null) {
                return new AuthResult(false, null, "用户不存在，请先注册", null);
            }
            
            // 检查用户状态
            if (user.getIsActive() == null || !user.getIsActive()) {
                return new AuthResult(false, null, "用户已被禁用", null);
            }
            
            // 验证密码
            boolean passwordMatch = userService.verifyPassword(password, user.getPassword());
            
            if (!passwordMatch) {
                return new AuthResult(false, null, "密码或用户名错误", null);
            }
            
            // 生成JWT Token
            String token = jwtUtils.generateToken(username, user.getId(), user.getRole());
            
            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            userService.updateLastLoginTime(user.getId());
            
            // 隐藏密码字段
            user.setPassword(null);
            
            return new AuthResult(true, token, "登录成功", user);
            
        } catch (Exception e) {
            return new AuthResult(false, null, "登录失败，请稍后重试", null);
        }
    }
    
    @Override
    public boolean register(User user) {
        try {
            // 检查用户名是否已存在
            User existingUser = userService.findByUsername(user.getUsername());
            if (existingUser != null) {
                return false;
            }
            
            // 检查管理员唯一性
            if ("ADMIN".equals(user.getRole())) {
                // 查找所有用户
                List<User> allUsers = userService.findAll();
                // 检查是否已存在管理员
                for (User u : allUsers) {
                    if ("ADMIN".equals(u.getRole())) {
                        return false;
                    }
                }
            }
            
            // 注册新用户
            int result = userService.register(user);
            return result > 0;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean validateToken(String token) {
        return jwtUtils.validateToken(token);
    }
    
    @Override
    public User getCurrentUser(String token) {
        if (!validateToken(token)) {
            return null;
        }
        
        Long userId = jwtUtils.getUserIdFromToken(token);
        return userService.findById(userId);
    }
    
    @Override
    public boolean sendResetPasswordEmail(String email) {
        try {
            // 查找用户
            User user = userService.findByEmail(email);
            if (user == null) {
                return false;
            }
            
            // 生成重置密码Token
            String resetToken = jwtUtils.generateToken(user.getUsername(), user.getId(), "RESET_PASSWORD");
            
            // 简化实现：实际项目中应该发送包含重置链接的邮件
            // 这里只返回成功，不实际发送邮件
            System.out.println("重置密码Token：" + resetToken);
            System.out.println("重置密码链接：http://localhost:8080/reset-password?token=" + resetToken);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean verifyResetToken(String token) {
        try {
            // 验证Token
            return jwtUtils.validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean verifyUserIdentity(String username, String idCard) {
        try {
            // 根据用户名查找用户
            User user = userService.findByUsername(username);
            if (user == null) {
                return false;
            }
            
            // 检查身份证号是否匹配
            return user.getIdCard() != null && user.getIdCard().equals(idCard);
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean resetPassword(String username, String idCard, String newPassword) {
        try {
            // 验证用户身份
            boolean identityVerified = verifyUserIdentity(username, idCard);
            if (!identityVerified) {
                return false;
            }
            
            // 查找用户
            User user = userService.findByUsername(username);
            if (user == null) {
                return false;
            }
            
            // 更新密码
            user.setPassword(newPassword);
            int result = userService.update(user);
            
            return result > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean resetPassword(String token, String newPassword) {
        try {
            // 验证重置密码Token
            if (!verifyResetToken(token)) {
                return false;
            }
            
            // 从Token中获取用户ID
            Long userId = jwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                return false;
            }
            
            // 查找用户
            User user = userService.findById(userId);
            if (user == null) {
                return false;
            }
            
            // 更新密码
            user.setPassword(newPassword);
            int result = userService.update(user);
            
            return result > 0;
        } catch (Exception e) {
            return false;
        }
    }
}