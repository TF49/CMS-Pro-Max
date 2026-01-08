package com.example.cms.modules.population.service;

import com.example.cms.modules.population.entity.Household;
import java.util.List;
import java.util.Map;

public interface HouseholdService {
    
    List<Household> findAll();
    
    Household findById(Long id);
    
    Household findByHouseholdNumber(String householdNumber);
    
    int insert(Household household);
    
    int update(Household household);
    
    int deleteById(Long id);
    
    List<Household> findByHouseholderName(String name);
    
    int count();
    
    // 分页查询
    List<Household> findByPage(int page, int size);
    
    // 统计各地区人口数量
    List<Map<String, Object>> countPopulationByRegion();
    
    // 根据创建者ID查询户籍列表，支持分页
    List<Household> findByCreatedBy(Long createdBy, int page, int size);
    
    // 根据创建者ID统计户籍数量
    int countByCreatedBy(Long createdBy);
    
    // 查询管理员和当前用户创建的户籍列表，支持分页
    List<Household> findByAdminOrCreatedBy(Long createdBy, int page, int size);
    
    // 统计管理员和当前用户创建的户籍数量
    int countByAdminOrCreatedBy(Long createdBy);
}