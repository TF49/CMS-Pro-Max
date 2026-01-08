package com.example.cms.modules.population.service;

import com.example.cms.modules.population.entity.Resident;
import java.util.List;

public interface ResidentService {
    
    // 基础CRUD操作
    int insert(Resident resident);
    
    int update(Resident resident);
    
    int deleteById(Long id);
    
    // 查询所有居民信息
    List<Resident> findAll();
    
    // 根据姓名查询居民信息
    List<Resident> findByName(String name);
    
    // 根据身份证号查询居民信息
    List<Resident> findByIdCard(String idCard);
    
    // 根据ID查询单个居民信息（用于编辑和查看详情）
    Resident findById(Long id);
    
    // 查询所有居民信息，包含户籍信息
    List<Resident> findAllWithHouseholdInfo();
    
    // 根据ID查询居民信息，包含户籍信息
    Resident findByIdWithHouseholdInfo(Long id);
    
    /**
     * 带分页的居民搜索，只支持姓名和身份证号查询，包含户籍信息
     */
    List<Resident> searchWithHouseholdInfo(String name, String idCard, int page, int size);
    
    /**
     * 统计符合搜索条件的居民数量
     */
    int countBySearch(String name, String idCard);
    
    /**
     * 分页查询居民列表，包含户籍信息
     */
    List<Resident> findByPageWithHouseholdInfo(int page, int size);
    
    /**
     * 获取居民总数
     */
    int count();
    
    /**
     * 根据户籍ID获取居民列表
     */
    List<Resident> findByHouseholdId(Long householdId);
    
    /**
     * 根据户籍ID分页查询居民列表，包含户籍信息
     */
    List<Resident> findByHouseholdIdWithHouseholdInfo(Long householdId, int page, int size);
    
    /**
     * 根据户籍ID统计居民数量
     */
    int countByHouseholdId(Long householdId);
    
    /**
     * 根据创建者ID查询居民列表，支持分页
     */
    List<Resident> findByCreatedBy(Long createdBy, int page, int size);
    
    /**
     * 根据创建者ID统计居民数量
     */
    int countByCreatedBy(Long createdBy);
    
    /**
     * 查询管理员和当前用户创建的居民列表，支持分页，包含户籍信息
     */
    List<Resident> findByAdminOrCreatedBy(Long createdBy, int page, int size);
    
    /**
     * 统计管理员和当前用户创建的居民数量
     */
    int countByAdminOrCreatedBy(Long createdBy);
}