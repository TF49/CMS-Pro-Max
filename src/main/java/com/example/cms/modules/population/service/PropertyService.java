package com.example.cms.modules.population.service;

import com.example.cms.modules.population.entity.Property;
import java.util.List;

public interface PropertyService {
    
    List<Property> findAll();
    
    Property findById(Long id);
    
    List<Property> findByResidentId(Long residentId);
    
    int insert(Property property);
    
    int update(Property property);
    
    int deleteById(Long id);
    
    int count();
    
    /**
     * 搜索房产信息
     * @param residentName 居民姓名
     * @param propertyType 房产类型
     * @param usageType 使用类型
     * @param address 房产地址
     * @return 房产信息列表
     */
    List<Property> search(String residentName, String propertyType, String usageType, String address);
    
    /**
     * 分页搜索房产信息
     * @param residentName 居民姓名
     * @param propertyType 房产类型
     * @param usageType 使用类型
     * @param address 房产地址
     * @param page 页码
     * @param size 每页大小
     * @return 房产信息列表
     */
    List<Property> searchWithPagination(String residentName, String propertyType, String usageType, String address, int page, int size);
    
    /**
     * 统计搜索条件下的记录数
     * @param residentName 居民姓名
     * @param propertyType 房产类型
     * @param usageType 使用类型
     * @param address 房产地址
     * @return 记录数
     */
    int countBySearch(String residentName, String propertyType, String usageType, String address);
    
    /**
     * 根据创建者ID和搜索条件分页搜索房产信息
     * @param residentName 居民姓名
     * @param propertyType 房产类型
     * @param usageType 使用类型
     * @param address 房产地址
     * @param createdBy 创建者ID
     * @param page 页码
     * @param size 每页大小
     * @return 房产信息列表
     */
    List<Property> searchByCreatedBy(String residentName, String propertyType, String usageType, String address, Long createdBy, int page, int size);
    
    /**
     * 根据创建者ID和搜索条件统计记录数
     * @param residentName 居民姓名
     * @param propertyType 房产类型
     * @param usageType 使用类型
     * @param address 房产地址
     * @param createdBy 创建者ID
     * @return 记录数
     */
    int countByCreatedByAndSearch(String residentName, String propertyType, String usageType, String address, Long createdBy);
    
    /**
     * 根据管理员和创建者ID及搜索条件分页搜索房产信息
     * @param residentName 居民姓名
     * @param propertyType 房产类型
     * @param usageType 使用类型
     * @param address 房产地址
     * @param createdBy 创建者ID
     * @param page 页码
     * @param size 每页大小
     * @return 房产信息列表
     */
    List<Property> searchByAdminOrCreatedBy(String residentName, String propertyType, String usageType, String address, Long createdBy, int page, int size);
    
    /**
     * 根据管理员和创建者ID及搜索条件统计记录数
     * @param residentName 居民姓名
     * @param propertyType 房产类型
     * @param usageType 使用类型
     * @param address 房产地址
     * @param createdBy 创建者ID
     * @return 记录数
     */
    int countByAdminOrCreatedByAndSearch(String residentName, String propertyType, String usageType, String address, Long createdBy);
    
    /**
     * 根据居民ID列表和搜索条件分页查询房产信息
     * @param residentIds 居民ID列表
     * @param residentName 居民姓名
     * @param propertyType 房产类型
     * @param usageType 使用类型
     * @param address 房产地址
     * @param page 页码
     * @param size 每页大小
     * @return 房产信息列表
     */
    List<Property> searchByResidentIdsAndSearchConditions(List<Long> residentIds, String residentName, String propertyType, String usageType, String address, int page, int size);
    
    /**
     * 根据居民ID列表和搜索条件统计房产信息数量
     * @param residentIds 居民ID列表
     * @param residentName 居民姓名
     * @param propertyType 房产类型
     * @param usageType 使用类型
     * @param address 房产地址
     * @return 记录数
     */
    int countByResidentIdsAndSearchConditions(List<Long> residentIds, String residentName, String propertyType, String usageType, String address);
}