package com.example.cms.modules.population.mapper;

import com.example.cms.modules.population.entity.Education;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 教育记录数据访问接口
 * 提供教育记录的数据库操作
 */
@Mapper
public interface EducationMapper {
    
    List<Education> findAll();
    
    List<Education> findAllWithPagination(@Param("offset") Integer offset, @Param("size") Integer size);
    
    Education findById(Long id);
    
    int insert(Education education);
    
    int update(Education education);
    
    int deleteById(Long id);
    
    List<Education> findByResidentId(Long residentId);
    
    List<Education> findByResidentIdCard(String idCard);
    
    List<Education> search(@Param("residentName") String residentName, @Param("schoolName") String schoolName, @Param("offset") Integer offset, @Param("size") Integer size);
    
    int count();
    
    List<Education> findByCreatedBy(@Param("createdBy") Long createdBy, @Param("offset") Integer offset, @Param("size") Integer size);
    
    int countByCreatedBy(@Param("createdBy") Long createdBy);
    
    List<Education> findByAdminOrCreatedBy(@Param("createdBy") Long createdBy, @Param("offset") Integer offset, @Param("size") Integer size);
    
    int countByAdminOrCreatedBy(@Param("createdBy") Long createdBy);
    
    List<Education> findByResidentIds(@Param("residentIds") List<Long> residentIds, @Param("offset") Integer offset, @Param("size") Integer size);
    
    int countByResidentIds(@Param("residentIds") List<Long> residentIds);
    
    /**
     * 获取教育记录的最大ID
     * @return 最大ID
     */
    Long getMaxId();
    
    /**
     * 重新排序教育记录ID，确保ID连续
     */
    void reorderIds();
}