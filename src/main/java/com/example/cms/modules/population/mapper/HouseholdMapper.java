package com.example.cms.modules.population.mapper;

import com.example.cms.modules.population.entity.Household;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface HouseholdMapper {
    
    List<Household> findAll();
    
    Household findById(Long id);
    
    Household findByHouseholdNumber(String householdNumber);
    
    int insert(Household household);
    
    int update(Household household);
    
    int deleteById(Long id);
    
    List<Household> findByHouseholderName(String name);
    
    int count();
    
    // 获取最大的户籍ID
    int getMaxId();
    
    // 分页查询
    List<Household> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    // 获取下一个可用的户籍编号
    String getNextHouseholdNumber(@Param("createdBy") Long createdBy);
    
    // 重新排序户籍ID，确保ID连续
    void reorderIds();
    
    // 统计各地区人口数量
    @Select("SELECT CASE WHEN address LIKE '%区%' THEN CONCAT(SUBSTRING_INDEX(address, '区', 1), '区') ELSE SUBSTRING_INDEX(address, '市', 1) END AS region, SUM(population_count) AS population FROM households GROUP BY region ORDER BY region ASC")
    List<Map<String, Object>> countPopulationByRegion();
    
    // 根据创建者ID查询户籍列表，支持分页
    List<Household> findByCreatedBy(@Param("createdBy") Long createdBy, @Param("offset") int offset, @Param("limit") int limit);
    
    // 根据创建者ID统计户籍数量
    int countByCreatedBy(@Param("createdBy") Long createdBy);
    
    // 查询管理员和当前用户创建的户籍列表，支持分页
    List<Household> findByAdminOrCreatedBy(@Param("createdBy") Long createdBy, @Param("offset") int offset, @Param("limit") int limit);
    
    // 统计管理员和当前用户创建的户籍数量
    int countByAdminOrCreatedBy(@Param("createdBy") Long createdBy);
}