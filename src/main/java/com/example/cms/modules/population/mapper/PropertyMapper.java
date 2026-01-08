package com.example.cms.modules.population.mapper;

import com.example.cms.modules.population.entity.Property;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PropertyMapper {
    
    List<Property> findAll();
    
    Property findById(Long id);
    
    int insert(Property property);
    
    int update(Property property);
    
    int deleteById(Long id);
    
    List<Property> findByResidentId(Long residentId);
    
    int count();
    
    List<Property> search(@Param("residentName") String residentName, 
                         @Param("propertyType") String propertyType, 
                         @Param("usageType") String usageType,
                         @Param("address") String address);
    
    /**
     * 分页搜索房产信息
     * @param residentName 居民姓名
     * @param propertyType 房产类型
     * @param usageType 使用类型
     * @param address 房产地址
     * @param offset 偏移量
     * @param size 每页大小
     * @return 房产信息列表
     */
    List<Property> searchWithPagination(@Param("residentName") String residentName, 
                                      @Param("propertyType") String propertyType, 
                                      @Param("usageType") String usageType,
                                      @Param("address") String address,
                                      @Param("offset") int offset, 
                                      @Param("size") int size);
    
    /**
     * 统计搜索条件下的记录数
     * @param residentName 居民姓名
     * @param propertyType 房产类型
     * @param usageType 使用类型
     * @param address 房产地址
     * @return 记录数
     */
    int countBySearch(@Param("residentName") String residentName, 
                    @Param("propertyType") String propertyType, 
                    @Param("usageType") String usageType,
                    @Param("address") String address);
    
    /**
     * 根据创建者ID和搜索条件分页搜索房产信息
     * @param residentName 居民姓名
     * @param propertyType 房产类型
     * @param usageType 使用类型
     * @param address 房产地址
     * @param createdBy 创建者ID
     * @param offset 偏移量
     * @param size 每页大小
     * @return 房产信息列表
     */
    List<Property> searchByCreatedBy(@Param("residentName") String residentName, 
                                    @Param("propertyType") String propertyType, 
                                    @Param("usageType") String usageType,
                                    @Param("address") String address,
                                    @Param("createdBy") Long createdBy,
                                    @Param("offset") int offset, 
                                    @Param("size") int size);
    
    /**
     * 根据创建者ID和搜索条件统计记录数
     * @param residentName 居民姓名
     * @param propertyType 房产类型
     * @param usageType 使用类型
     * @param address 房产地址
     * @param createdBy 创建者ID
     * @return 记录数
     */
    int countByCreatedByAndSearch(@Param("residentName") String residentName, 
                                @Param("propertyType") String propertyType, 
                                @Param("usageType") String usageType,
                                @Param("address") String address,
                                @Param("createdBy") Long createdBy);
    
    /**
     * 根据管理员和创建者ID及搜索条件分页搜索房产信息
     * @param residentName 居民姓名
     * @param propertyType 房产类型
     * @param usageType 使用类型
     * @param address 房产地址
     * @param createdBy 创建者ID
     * @param offset 偏移量
     * @param size 每页大小
     * @return 房产信息列表
     */
    List<Property> searchByAdminOrCreatedBy(@Param("residentName") String residentName, 
                                    @Param("propertyType") String propertyType, 
                                    @Param("usageType") String usageType,
                                    @Param("address") String address,
                                    @Param("createdBy") Long createdBy,
                                    @Param("offset") int offset, 
                                    @Param("size") int size);
    
    /**
     * 根据管理员和创建者ID及搜索条件统计记录数
     * @param residentName 居民姓名
     * @param propertyType 房产类型
     * @param usageType 使用类型
     * @param address 房产地址
     * @param createdBy 创建者ID
     * @return 记录数
     */
    int countByAdminOrCreatedByAndSearch(@Param("residentName") String residentName, 
                                @Param("propertyType") String propertyType, 
                                @Param("usageType") String usageType,
                                @Param("address") String address,
                                @Param("createdBy") Long createdBy);
    
    /**
     * 获取房产表中的最大ID
     * @return 最大ID
     */
    Long getMaxId();
    
    /**
     * 重新排序房产记录ID，确保ID连续
     */
    void reorderIds();
    
    /**
     * 根据居民ID列表和搜索条件分页查询房产信息
     * @param residentIds 居民ID列表
     * @param residentName 居民姓名
     * @param propertyType 房产类型
     * @param usageType 使用类型
     * @param address 房产地址
     * @param offset 偏移量
     * @param size 每页大小
     * @return 房产信息列表
     */
    List<Property> searchByResidentIdsAndSearchConditions(@Param("residentIds") List<Long> residentIds,
                                                     @Param("residentName") String residentName,
                                                     @Param("propertyType") String propertyType,
                                                     @Param("usageType") String usageType,
                                                     @Param("address") String address,
                                                     @Param("offset") int offset,
                                                     @Param("size") int size);
    
    /**
     * 根据居民ID列表和搜索条件统计房产信息数量
     * @param residentIds 居民ID列表
     * @param residentName 居民姓名
     * @param propertyType 房产类型
     * @param usageType 使用类型
     * @param address 房产地址
     * @return 记录数
     */
    int countByResidentIdsAndSearchConditions(@Param("residentIds") List<Long> residentIds,
                                           @Param("residentName") String residentName,
                                           @Param("propertyType") String propertyType,
                                           @Param("usageType") String usageType,
                                           @Param("address") String address);
}