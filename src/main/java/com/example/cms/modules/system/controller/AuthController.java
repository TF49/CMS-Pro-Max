package com.example.cms.modules.system.controller;

import com.example.cms.common.Result;
import com.example.cms.framework.web.BaseController;
import com.example.cms.modules.system.service.AuthResult;
import com.example.cms.modules.system.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证控制器
 * 处理用户登录、注册等认证相关请求
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController extends BaseController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private com.example.cms.modules.system.service.UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Object> login(@RequestBody LoginBody loginBody) {
        try {
            // 调用认证服务进行登录验证
            AuthResult result = authService.login(loginBody.getUsername(), loginBody.getPassword());
            
            if (result.isSuccess()) {
                return success(new LoginResult(result.getToken(), result.getMessage(), result.getUser()));
            } else {
                return error(result.getMessage());
            }
        } catch (Exception e) {
            return error("登录失败，请稍后重试");
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Object> register(@RequestBody RegisterBody registerBody) {
        try {
            // 检查密码确认
            if (!registerBody.getPassword().equals(registerBody.getConfirmPassword())) {
                return error("两次输入的密码不一致，请重新输入");
            }
            
            // 创建用户对象
            com.example.cms.modules.system.entity.User user = 
                new com.example.cms.modules.system.entity.User();
            user.setUsername(registerBody.getUsername());
            user.setPassword(registerBody.getPassword());
            user.setIdCard(registerBody.getIdCard());
            user.setRole(registerBody.getRole());
            user.setIsActive(true);
            
            // 先检查用户名是否已存在
            com.example.cms.modules.system.entity.User existingUser = userService.findByUsername(registerBody.getUsername());
            if (existingUser != null) {
                return error("该用户名已被注册，请选择其他用户名");
            }
            
            // 检查管理员唯一性
            if ("ADMIN".equals(registerBody.getRole())) {
                // 查找所有用户
                java.util.List<com.example.cms.modules.system.entity.User> allUsers = userService.findAll();
                // 检查是否已存在管理员
                for (com.example.cms.modules.system.entity.User u : allUsers) {
                    if ("ADMIN".equals(u.getRole())) {
                        return error("系统中已存在管理员账户，无法创建新的管理员");
                    }
                }
            }
            
            // 调用认证服务进行注册
            boolean success = authService.register(user);
            
            if (success) {
                return success("注册成功");
            } else {
                return error("注册失败，请稍后重试");
            }
        } catch (Exception e) {
            return error("注册失败，请稍后重试");
        }
    }

    /**
     * 验证用户名和身份证号，用于忘记密码
     */
    @PostMapping("/forgot-password")
    public Result<Object> forgotPassword(@RequestBody ForgotPasswordBody forgotPasswordBody) {
        try {
            // 调用认证服务验证用户名和身份证号
            boolean success = authService.verifyUserIdentity(forgotPasswordBody.getUsername(), forgotPasswordBody.getIdCard());
            
            if (success) {
                return success("身份验证成功");
            } else {
                return error("用户名或身份证号错误，请检查输入信息");
            }
        } catch (Exception e) {
            return error("身份验证失败，请稍后重试");
        }
    }

    /**
     * 验证重置密码Token
     */
    @PostMapping("/verify-reset-token")
    public Result<Object> verifyResetToken(@RequestBody VerifyResetTokenBody verifyResetTokenBody) {
        try {
            // 调用认证服务验证重置密码Token
            boolean isValid = authService.verifyResetToken(verifyResetTokenBody.getToken());
            
            if (isValid) {
                return success("Token有效");
            } else {
                return error("Token无效或已过期，请重新获取重置链接");
            }
        } catch (Exception e) {
            return error("验证Token失败，请稍后重试");
        }
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public Result<Object> resetPassword(@RequestBody ResetPasswordBody resetPasswordBody) {
        try {
            // 检查密码确认
            if (!resetPasswordBody.getNewPassword().equals(resetPasswordBody.getConfirmPassword())) {
                return error("两次输入的密码不一致，请重新输入");
            }
            
            boolean success;
            
            // 根据请求体中是否包含token选择不同的重置密码方式
            if (resetPasswordBody.getToken() != null && !resetPasswordBody.getToken().isEmpty()) {
                // 基于令牌重置密码
                success = authService.resetPassword(resetPasswordBody.getToken(), resetPasswordBody.getNewPassword());
                if (!success) {
                    return error("密码重置失败，Token无效或已过期，请重新获取重置链接");
                }
            } else {
                // 基于用户名和身份证号重置密码
                success = authService.resetPassword(resetPasswordBody.getUsername(), resetPasswordBody.getIdCard(), resetPasswordBody.getNewPassword());
                if (!success) {
                    return error("密码重置失败，用户名或身份证号错误，请检查输入信息");
                }
            }
            
            return success("密码重置成功");
        } catch (Exception e) {
            return error("重置密码失败，请稍后重试");
        }
    }

    /**
     * 验证Token
     */
    @PostMapping("/validate")
    public Result<Object> validate(@RequestBody ValidateBody validateBody) {
        try {
            boolean isValid = authService.validateToken(validateBody.getToken());
            if (isValid) {
                return success("Token有效");
            } else {
                return error("Token无效");
            }
        } catch (Exception e) {
            return error("验证失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result<Object> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            // 提取Token（去除"Bearer "前缀）
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
            
            com.example.cms.modules.system.entity.User user = authService.getCurrentUser(token);
            if (user != null) {
                // 清除敏感信息
                user.setPassword(null);
                return success(user);
            } else {
                return error("Token无效或已过期");
            }
        } catch (Exception e) {
            return error("获取用户信息失败：" + e.getMessage());
        }
    }
}

/**
 * 登录请求体
 */
class LoginBody {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

/**
 * 注册请求体
 */
class RegisterBody {
    private String username;
    private String password;
    private String confirmPassword;
    private String idCard;
    private String role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

/**
 * 忘记密码请求体
 */
class ForgotPasswordBody {
    private String username;
    private String idCard;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
}

/**
 * 验证重置密码Token请求体
 */
class VerifyResetTokenBody {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

/**
 * 重置密码请求体
 */
class ResetPasswordBody {
    private String username;
    private String idCard;
    private String token;
    private String newPassword;
    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}

/**
 * Token验证请求体
 */
class ValidateBody {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

/**
 * 登录结果返回体
 */
class LoginResult {
    private String token;
    private String message;
    private com.example.cms.modules.system.entity.User user;

    public LoginResult(String token, String message, com.example.cms.modules.system.entity.User user) {
        this.token = token;
        this.message = message;
        this.user = user;
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

    public com.example.cms.modules.system.entity.User getUser() {
        return user;
    }

    public void setUser(com.example.cms.modules.system.entity.User user) {
        this.user = user;
    }
}