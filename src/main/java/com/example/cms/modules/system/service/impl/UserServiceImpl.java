package com.example.cms.modules.system.service.impl;

import com.example.cms.modules.system.entity.User;
import com.example.cms.modules.system.mapper.UserMapper;
import com.example.cms.modules.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户业务实现类
 * 简化的密码处理：不使用加密，直接明文比较
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }
    
    @Override
    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }
    
    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }
    
    @Override
    public List<User> findByUsernameOrIdCard(String username, String idCard, Boolean isActive, int page, int size) {
        // 如果搜索条件都为空，直接返回所有用户
        if ((username == null || username.trim().isEmpty()) && (idCard == null || idCard.trim().isEmpty()) && isActive == null) {
            List<User> allUsers = userMapper.findAll();
            // 手动分页
            int offset = (page - 1) * size;
            int endIndex = Math.min(offset + size, allUsers.size());
            return allUsers.subList(offset, endIndex);
        }
        // 否则按照搜索条件过滤
        int offset = (page - 1) * size;
        return userMapper.findByUsernameOrIdCard(username, idCard, isActive, offset, size);
    }
    
    @Override
    public int countByUsernameOrIdCard(String username, String idCard, Boolean isActive) {
        // 如果搜索条件都为空，直接返回所有用户数量
        if ((username == null || username.trim().isEmpty()) && (idCard == null || idCard.trim().isEmpty()) && isActive == null) {
            return userMapper.count();
        }
        // 否则按照搜索条件统计
        return userMapper.countByUsernameOrIdCard(username, idCard, isActive);
    }
    
    @Override
    public int register(User user) {
        // 简化版本：直接保存密码（不加密）
        if (user.getCreateTime() == null) {
            user.setCreateTime(LocalDateTime.now());
        }
        // 确保密码不加密，直接存储
        if (user.getPassword() != null) {
            // 密码不加密，直接使用明文
        }
        
        // 设置默认角色为USER
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        
        // 设置默认状态为激活
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        
        // 手动设置连续ID
        Long maxId = userMapper.getMaxId();
        user.setId(maxId + 1);
        
        return userMapper.insert(user);
    }
    
    @Override
    public int update(User user) {
        // 先查询原有用户信息
        User existingUser = userMapper.findById(user.getId());
        if (existingUser != null) {
            // 确保角色字段不为空，使用原有角色
            if (user.getRole() == null) {
                user.setRole(existingUser.getRole());
            }
            // 确保激活状态不为空，使用原有激活状态
            if (user.getIsActive() == null) {
                user.setIsActive(existingUser.getIsActive());
            }
            // 确保密码不为空，使用原有密码
            if (user.getPassword() == null) {
                user.setPassword(existingUser.getPassword());
            }
        } else {
            // 如果用户不存在，设置默认值
            if (user.getRole() == null) {
                user.setRole("USER");
            }
            if (user.getIsActive() == null) {
                user.setIsActive(true);
            }
        }
        return userMapper.update(user);
    }
    
    @Override
    public int deleteById(Long id) {
        int result = userMapper.deleteById(id);
        // 删除成功后重新排序ID
        if (result > 0) {
            userMapper.reorderIds();
        }
        return result;
    }
    
    @Override
    public int updateLastLoginTime(Long id) {
        return userMapper.updateLastLoginTime(id, LocalDateTime.now());
    }
    
    @Override
    public int count() {
        return userMapper.count();
    }
    
    /**
     * 验证密码 - 简化版本：直接明文比较
     */
    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        // 直接比较明文密码，完全不使用加密
        return rawPassword != null && encodedPassword != null && rawPassword.equals(encodedPassword);
    }
    
    /**
     * 检查密码（根据用户名验证）
     */
    @Override
    public boolean checkPassword(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null) {
            return verifyPassword(password, user.getPassword());
        }
        return false;
    }
    
    /**
     * 根据用户ID获取关联的户籍ID
     */
    @Override
    public Long getUserHouseholdId(Long userId) {
        User user = userMapper.findById(userId);
        return user != null ? user.getHouseholdId() : null;
    }
}