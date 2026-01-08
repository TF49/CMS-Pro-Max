package com.example.cms.modules.population.mapper;

import com.example.cms.modules.population.entity.Resident;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ResidentMapper {
    
    List<Resident> findAll();
    
    Resident findById(Long id);
    
    int insert(Resident resident);
    
    int update(Resident resident);
    
    int deleteById(Long id);
    
    List<Resident> findByName(String name);
    
    List<Resident> findByIdCard(String idCard);
    
    int count();
    
    // 关联查询居民及其户籍信息
    List<Resident> findAllWithHouseholdInfo();
    
    // 根据ID关联查询居民及其户籍信息
    Resident findByIdWithHouseholdInfo(Long id);
    
    // 搜索居民及其户籍信息
    List<Resident> searchWithHouseholdInfo(@Param("name") String name, @Param("idCard") String idCard, @Param("offset") int offset, @Param("size") int size);
    
    // 分页查询居民及其户籍信息
    List<Resident> findByPageWithHouseholdInfo(@Param("offset") int offset, @Param("size") int size);
    
    // 统计按搜索条件的居民数量
    int countBySearch(@Param("name") String name, @Param("idCard") String idCard);
    
    // 根据户籍ID获取居民列表
    List<Resident> findByHouseholdId(@Param("householdId") Long householdId);
    
    // 根据户籍ID分页查询居民列表，包含户籍信息
    List<Resident> findByHouseholdIdWithHouseholdInfo(@Param("householdId") Long householdId, @Param("offset") int offset, @Param("size") int size);
    
    // 根据户籍ID统计居民数量
    int countByHouseholdId(@Param("householdId") Long householdId);
    
    // 获取居民表中的最大ID
    Long getMaxId();
    
    // 重新排序居民ID，确保ID连续
    void reorderIds();
    
    // 根据创建者ID查询居民列表，支持分页
    List<Resident> findByCreatedBy(@Param("createdBy") Long createdBy, @Param("offset") int offset, @Param("size") int size);
    
    // 根据创建者ID统计居民数量
    int countByCreatedBy(@Param("createdBy") Long createdBy);
    
    // 查询管理员和当前用户创建的居民列表，支持分页，包含户籍信息
    List<Resident> findByAdminOrCreatedBy(@Param("createdBy") Long createdBy, @Param("offset") int offset, @Param("size") int size);
    
    // 统计管理员和当前用户创建的居民数量
    int countByAdminOrCreatedBy(@Param("createdBy") Long createdBy);
}