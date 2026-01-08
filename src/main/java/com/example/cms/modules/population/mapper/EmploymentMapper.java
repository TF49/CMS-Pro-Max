package com.example.cms.modules.population.mapper;

import com.example.cms.modules.population.entity.Employment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface EmploymentMapper {
    
    List<Employment> findAll();
    
    Employment findById(Long id);
    
    int insert(Employment employment);
    
    int update(Employment employment);
    
    int deleteById(Long id);
    
    List<Employment> findByResidentId(Long residentId);
    
    int count();
    
    /**
     * 搜索就业信息
     * @param residentName 居民姓名
     * @param companyName 工作单位
     * @param industry 行业
     * @param employmentStatus 就业状态
     * @return 就业信息列表
     */
    List<Employment> search(@Param("residentName") String residentName, @Param("companyName") String companyName, 
                           @Param("industry") String industry, @Param("employmentStatus") String employmentStatus);
    
    /**
     * 分页搜索就业信息
     * @param residentName 居民姓名
     * @param companyName 工作单位
     * @param industry 行业
     * @param employmentStatus 就业状态
     * @param offset 偏移量
     * @param size 每页大小
     * @return 就业信息列表
     */
    List<Employment> searchWithPagination(@Param("residentName") String residentName, @Param("companyName") String companyName, 
                                         @Param("industry") String industry, @Param("employmentStatus") String employmentStatus,
                                         @Param("offset") int offset, @Param("size") int size);
    
    /**
     * 统计搜索条件下的记录数
     * @param residentName 居民姓名
     * @param companyName 工作单位
     * @param industry 行业
     * @param employmentStatus 就业状态
     * @return 记录数
     */
    int countBySearch(@Param("residentName") String residentName, @Param("companyName") String companyName, 
                     @Param("industry") String industry, @Param("employmentStatus") String employmentStatus);
    
    /**
     * 根据创建者ID和搜索条件分页搜索就业信息
     * @param residentName 居民姓名
     * @param companyName 工作单位
     * @param industry 行业
     * @param employmentStatus 就业状态
     * @param createdBy 创建者ID
     * @param offset 偏移量
     * @param size 每页大小
     * @return 就业信息列表
     */
    List<Employment> searchByCreatedBy(@Param("residentName") String residentName, @Param("companyName") String companyName, 
                                      @Param("industry") String industry, @Param("employmentStatus") String employmentStatus,
                                      @Param("createdBy") Long createdBy, @Param("offset") int offset, @Param("size") int size);
    
    /**
     * 根据创建者ID和搜索条件统计记录数
     * @param residentName 居民姓名
     * @param companyName 工作单位
     * @param industry 行业
     * @param employmentStatus 就业状态
     * @param createdBy 创建者ID
     * @return 记录数
     */
    int countByCreatedByAndSearch(@Param("residentName") String residentName, @Param("companyName") String companyName, 
                                 @Param("industry") String industry, @Param("employmentStatus") String employmentStatus,
                                 @Param("createdBy") Long createdBy);
    
    /**
     * 根据管理员和创建者ID及搜索条件分页搜索就业信息
     * @param residentName 居民姓名
     * @param companyName 工作单位
     * @param industry 行业
     * @param employmentStatus 就业状态
     * @param createdBy 创建者ID
     * @param offset 偏移量
     * @param size 每页大小
     * @return 就业信息列表
     */
    List<Employment> searchByAdminOrCreatedBy(@Param("residentName") String residentName, @Param("companyName") String companyName, 
                                      @Param("industry") String industry, @Param("employmentStatus") String employmentStatus,
                                      @Param("createdBy") Long createdBy, @Param("offset") int offset, @Param("size") int size);
    
    /**
     * 根据管理员和创建者ID及搜索条件统计记录数
     * @param residentName 居民姓名
     * @param companyName 工作单位
     * @param industry 行业
     * @param employmentStatus 就业状态
     * @param createdBy 创建者ID
     * @return 记录数
     */
    int countByAdminOrCreatedByAndSearch(@Param("residentName") String residentName, @Param("companyName") String companyName, 
                                 @Param("industry") String industry, @Param("employmentStatus") String employmentStatus,
                                 @Param("createdBy") Long createdBy);
    
    /**
     * 获取就业表中的最大ID
     * @return 最大ID
     */
    Long getMaxId();
    
    /**
     * 重新排序就业记录ID，确保ID连续
     */
    void reorderIds();
    
    /**
     * 根据居民ID列表和搜索条件分页查询就业信息
     * @param residentIds 居民ID列表
     * @param residentName 居民姓名
     * @param companyName 工作单位
     * @param industry 行业
     * @param employmentStatus 就业状态
     * @param offset 偏移量
     * @param size 每页大小
     * @return 就业信息列表
     */
    List<Employment> searchByResidentIdsAndSearchConditions(@Param("residentIds") List<Long> residentIds,
                                                         @Param("residentName") String residentName,
                                                         @Param("companyName") String companyName,
                                                         @Param("industry") String industry,
                                                         @Param("employmentStatus") String employmentStatus,
                                                         @Param("offset") int offset,
                                                         @Param("size") int size);
    
    /**
     * 根据居民ID列表和搜索条件统计就业信息数量
     * @param residentIds 居民ID列表
     * @param residentName 居民姓名
     * @param companyName 工作单位
     * @param industry 行业
     * @param employmentStatus 就业状态
     * @return 记录数
     */
    int countByResidentIdsAndSearchConditions(@Param("residentIds") List<Long> residentIds,
                                           @Param("residentName") String residentName,
                                           @Param("companyName") String companyName,
                                           @Param("industry") String industry,
                                           @Param("employmentStatus") String employmentStatus);
}