package com.example.cms.modules.system.controller;

import com.example.cms.common.Result;
import com.example.cms.common.annotation.RequiresRole;
import com.example.cms.modules.system.entity.User;
import com.example.cms.modules.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理Controller
 * 处理用户相关的请求
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    @RequiresRole({"ADMIN"})
    public Result<Object> updateUser(@RequestBody User user) {
        try {
            if (user.getId() == null) {
                return Result.error("用户ID不能为空");
            }
            
            // 检查用户是否存在
            User existingUser = userService.findById(user.getId());
            if (existingUser == null) {
                return Result.error("用户不存在");
            }
            
            // 更新用户信息
            int result = userService.update(user);
            
            if (result > 0) {
                return Result.success(null);
            } else {
                return Result.error("用户信息更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新用户信息时发生错误：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/info/{id}")
    @RequiresRole({"ADMIN"})
    public Result<User> getUserInfo(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            if (user != null) {
                user.setPassword(null); // 隐藏密码
                return Result.success(user);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            return Result.error("获取用户信息时发生错误：" + e.getMessage());
        }
    }
    
    /**
     * 获取所有用户信息，支持搜索和分页
     */
    @GetMapping("/list")
    @RequiresRole({"ADMIN"})
    public Result<Object> getAllUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String idCard,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // 验证分页参数
            if (page < 1) {
                page = 1;
            }
            if (size < 1 || size > 100) {
                size = 10;
            }
            
            // 将空字符串转换为null，便于后端查询
            if (username != null && username.trim().isEmpty()) {
                username = null;
            }
            if (idCard != null && idCard.trim().isEmpty()) {
                idCard = null;
            }
            
            // 调用搜索方法
            List<User> users = userService.findByUsernameOrIdCard(username, idCard, isActive, page, size);
            // 统计符合条件的用户总数
            int total = userService.countByUsernameOrIdCard(username, idCard, isActive);
            
            // 隐藏所有用户的密码
            users.forEach(user -> user.setPassword(null));
            
            // 构造分页响应
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("data", users);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            result.put("pages", (total + size - 1) / size);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取用户列表时发生错误：" + e.getMessage());
        }
    }
    
    /**
     * 添加用户
     */
    @PostMapping("/add")
    @RequiresRole({"ADMIN"})
    public Result<Object> addUser(@RequestBody User user) {
        try {
            if (user.getUsername() == null || user.getPassword() == null) {
                return Result.error("用户名和密码不能为空");
            }
            
            // 检查用户名是否已存在
            User existingUser = userService.findByUsername(user.getUsername());
            if (existingUser != null) {
                return Result.error("用户名已存在");
            }
            
            // 检查邮箱是否已存在
            if (user.getEmail() != null) {
                User existingEmailUser = userService.findByEmail(user.getEmail());
                if (existingEmailUser != null) {
                    return Result.error("邮箱已存在");
                }
            }
            
            // 添加用户
            int result = userService.register(user);
            
            if (result > 0) {
                return Result.success(null);
            } else {
                return Result.error("添加用户失败");
            }
        } catch (Exception e) {
            return Result.error("添加用户时发生错误：" + e.getMessage());
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @RequiresRole({"ADMIN"})
    public Result<Object> deleteUser(@PathVariable Long id) {
        try {
            // 检查用户是否存在
            User existingUser = userService.findById(id);
            if (existingUser == null) {
                return Result.error("用户不存在");
            }
            
            // 删除用户
            int result = userService.deleteById(id);
            
            if (result > 0) {
                return Result.success(null);
            } else {
                return Result.error("删除用户失败");
            }
        } catch (Exception e) {
            return Result.error("删除用户时发生错误：" + e.getMessage());
        }
    }
}