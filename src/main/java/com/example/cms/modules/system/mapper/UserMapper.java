package com.example.cms.modules.system.mapper;

import com.example.cms.modules.system.entity.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户Mapper接口
 * 用于数据库操作
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    /**
     * 根据用户ID查找用户
     * @param id 用户ID
     * @return 用户对象
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(@Param("id") Long id);
    
    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户对象
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    /**
     * 查找所有用户
     * @return 用户列表
     */
    @Select("SELECT * FROM users")
    List<User> findAll();
    
    /**
     * 根据用户名或身份证号搜索用户，支持分页
     * @param username 用户名
     * @param idCard 身份证号
     * @param isActive 状态
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 用户列表
     */
    @Select("SELECT * FROM users WHERE 1=1 " +
            "AND (#{username} IS NULL OR #{username} = '' OR username LIKE CONCAT('%', #{username}, '%')) " +
            "AND (#{idCard} IS NULL OR #{idCard} = '' OR id_card LIKE CONCAT('%', #{idCard}, '%')) " +
            "AND (#{isActive} IS NULL OR is_active = #{isActive}) " +
            "ORDER BY id LIMIT #{offset}, #{limit}")
    List<User> findByUsernameOrIdCard(@Param("username") String username, 
                                     @Param("idCard") String idCard, 
                                     @Param("isActive") Boolean isActive, 
                                     @Param("offset") int offset, 
                                     @Param("limit") int limit);
    
    /**
     * 根据用户名或身份证号统计用户数量
     * @param username 用户名
     * @param idCard 身份证号
     * @param isActive 状态
     * @return 用户数量
     */
    @Select("SELECT COUNT(*) FROM users WHERE 1=1 " +
            "AND (#{username} IS NULL OR #{username} = '' OR username LIKE CONCAT('%', #{username}, '%')) " +
            "AND (#{idCard} IS NULL OR #{idCard} = '' OR id_card LIKE CONCAT('%', #{idCard}, '%')) " +
            "AND (#{isActive} IS NULL OR is_active = #{isActive})")
    int countByUsernameOrIdCard(@Param("username") String username, 
                               @Param("idCard") String idCard, 
                               @Param("isActive") Boolean isActive);

    /**
     * 插入新用户
     * @param user 用户对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO users (id, username, password, email, role, phone, id_card, householdId, is_active) " +
            "VALUES (#{id}, #{username}, #{password}, #{email}, #{role}, #{phone}, #{idCard}, #{householdId}, #{isActive})")
    int insert(User user);

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 影响的行数
     */
    @Update("UPDATE users SET email = #{email}, role = #{role}, password = #{password}, " +
            "phone = #{phone}, id_card = #{idCard}, householdId = #{householdId}, is_active = #{isActive} WHERE id = #{id}")
    int update(User user);

    /**
     * 删除用户
     * @param id 用户ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 更新最后登录时间
     * @param id 用户ID
     * @param lastLoginTime 最后登录时间
     * @return 影响的行数
     */
    @Update("UPDATE users SET last_login_time = #{lastLoginTime} WHERE id = #{id}")
    int updateLastLoginTime(@Param("id") Long id, @Param("lastLoginTime") LocalDateTime lastLoginTime);

    /**
     * 统计用户数量
     * @return 用户总数
     */
    @Select("SELECT COUNT(*) FROM users")
    int count();
    
    /**
     * 获取用户表中的最大ID
     * @return 最大ID
     */
    @Select("SELECT COALESCE(MAX(id), 0) FROM users")
    Long getMaxId();
    
    /**
     * 重新排序用户ID，确保ID连续
     */
    @Update("UPDATE users u JOIN (SELECT id, @new_id := @new_id + 1 AS new_id FROM users, (SELECT @new_id := 0) AS tmp ORDER BY id ASC) u2 ON u.id = u2.id SET u.id = u2.new_id")
    void reorderIds();
}