package com.example.cms.modules.population.mapper;

import com.example.cms.modules.population.entity.Medical;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface MedicalMapper {
    
    List<Medical> findAll();
    
    Medical findById(Long id);
    
    int insert(Medical medical);
    
    int update(Medical medical);
    
    int deleteById(Long id);
    
    List<Medical> findByResidentId(Long residentId);
    
    int count();
    
    /**
     * 分页查询医疗记录
     * @param offset 偏移量
     * @param size 每页大小
     * @return 医疗记录列表
     */
    List<Medical> findAllWithPagination(@Param("offset") Integer offset, @Param("size") Integer size);
    
    /**
     * 搜索医疗记录
     * @param residentName 居民姓名
     * @param hospitalName 医院名称
     * @return 医疗记录列表
     */
    List<Medical> search(@Param("residentName") String residentName, 
                        @Param("hospitalName") String hospitalName);
    
    /**
     * 根据创建者ID查询医疗记录（支持分页）
     * @param createdBy 创建者ID
     * @param offset 偏移量
     * @param size 每页大小
     * @return 医疗记录列表
     */
    List<Medical> findByCreatedBy(@Param("createdBy") Long createdBy, 
                                 @Param("offset") Integer offset, 
                                 @Param("size") Integer size);
    
    /**
     * 根据创建者ID统计医疗记录数量
     * @param createdBy 创建者ID
     * @return 医疗记录数量
     */
    int countByCreatedBy(@Param("createdBy") Long createdBy);
    
    /**
     * 根据管理员和创建者ID查询医疗记录（支持分页）
     * @param createdBy 创建者ID
     * @param offset 偏移量
     * @param size 每页大小
     * @return 医疗记录列表
     */
    List<Medical> findByAdminOrCreatedBy(@Param("createdBy") Long createdBy, 
                                 @Param("offset") Integer offset, 
                                 @Param("size") Integer size);
    
    /**
     * 根据管理员和创建者ID统计医疗记录数量
     * @param createdBy 创建者ID
     * @return 医疗记录数量
     */
    int countByAdminOrCreatedBy(@Param("createdBy") Long createdBy);
    
    /**
     * 获取医疗表中的最大ID
     * @return 最大ID值
     */
    Long getMaxId();
    
    /**
     * 重新排序医疗ID，确保ID连续
     */
    void reorderIds();
    
    /**
     * 根据居民ID列表查询医疗记录（支持分页）
     * @param residentIds 居民ID列表
     * @param offset 偏移量
     * @param size 每页大小
     * @return 医疗记录列表
     */
    List<Medical> findByResidentIds(@Param("residentIds") List<Long> residentIds,
                                  @Param("offset") Integer offset,
                                  @Param("size") Integer size);
    
    /**
     * 根据居民ID列表统计医疗记录数量
     * @param residentIds 居民ID列表
     * @return 医疗记录数量
     */
    int countByResidentIds(@Param("residentIds") List<Long> residentIds);
}