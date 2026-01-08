package com.example.cms.modules.system.service;

import com.example.cms.modules.system.entity.User;

import java.util.List;

/**
 * 用户业务接口
 */
public interface UserService {
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    User findByUsername(String username);
    
    /**
     * 根据用户ID查找用户
     * @param id 用户ID
     * @return 用户对象
     */
    User findById(Long id);
    
    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户对象
     */
    User findByEmail(String email);
    
    /**
     * 查找所有用户
     * @return 用户列表
     */
    List<User> findAll();
    
    /**
     * 根据用户名或身份证号搜索用户，支持分页
     * @param username 用户名
     * @param idCard 身份证号
     * @param isActive 状态
     * @param page 页码
     * @param size 每页数量
     * @return 用户列表
     */
    List<User> findByUsernameOrIdCard(String username, String idCard, Boolean isActive, int page, int size);
    
    /**
     * 根据用户名或身份证号统计用户数量
     * @param username 用户名
     * @param idCard 身份证号
     * @param isActive 状态
     * @return 用户数量
     */
    int countByUsernameOrIdCard(String username, String idCard, Boolean isActive);
    
    /**
     * 注册新用户
     * @param user 用户对象
     * @return 影响的行数
     */
    int register(User user);
    
    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 影响的行数
     */
    int update(User user);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 影响的行数
     */
    int deleteById(Long id);
    
    /**
     * 更新最后登录时间
     * @param id 用户ID
     * @return 影响的行数
     */
    int updateLastLoginTime(Long id);
    
    /**
     * 统计用户数量
     * @return 用户总数
     */
    int count();
    
    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean verifyPassword(String rawPassword, String encodedPassword);
    
    /**
     * 检查密码（根据用户名）
     * @param username 用户名
     * @param password 密码
     * @return 是否匹配
     */
    boolean checkPassword(String username, String password);
    
    /**
     * 根据用户ID获取关联的户籍ID
     * @param userId 用户ID
     * @return 户籍ID
     */
    Long getUserHouseholdId(Long userId);
}