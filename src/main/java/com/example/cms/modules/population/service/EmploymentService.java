package com.example.cms.modules.population.service;

import com.example.cms.modules.population.entity.Employment;
import java.util.List;

public interface EmploymentService {
    
    List<Employment> findAll();
    
    Employment findById(Long id);
    
    List<Employment> findByResidentId(Long residentId);
    
    int insert(Employment employment);
    
    int update(Employment employment);
    
    int deleteById(Long id);
    
    int count();
    
    /**
     * 搜索就业信息
     * @param residentName 居民姓名
     * @param companyName 工作单位
     * @param industry 行业
     * @param employmentStatus 就业状态
     * @return 就业信息列表
     */
    List<Employment> search(String residentName, String companyName, String industry, String employmentStatus);
    
    /**
     * 分页搜索就业信息
     * @param residentName 居民姓名
     * @param companyName 工作单位
     * @param industry 行业
     * @param employmentStatus 就业状态
     * @param page 页码
     * @param size 每页大小
     * @return 就业信息列表
     */
    List<Employment> searchWithPagination(String residentName, String companyName, String industry, String employmentStatus, int page, int size);
    
    /**
     * 统计搜索条件下的记录数
     * @param residentName 居民姓名
     * @param companyName 工作单位
     * @param industry 行业
     * @param employmentStatus 就业状态
     * @return 记录数
     */
    int countBySearch(String residentName, String companyName, String industry, String employmentStatus);
    
    /**
     * 根据创建者ID和搜索条件分页搜索就业信息
     * @param residentName 居民姓名
     * @param companyName 工作单位
     * @param industry 行业
     * @param employmentStatus 就业状态
     * @param createdBy 创建者ID
     * @param page 页码
     * @param size 每页大小
     * @return 就业信息列表
     */
    List<Employment> searchByCreatedBy(String residentName, String companyName, String industry, String employmentStatus, Long createdBy, int page, int size);
    
    /**
     * 根据创建者ID和搜索条件统计记录数
     * @param residentName 居民姓名
     * @param companyName 工作单位
     * @param industry 行业
     * @param employmentStatus 就业状态
     * @param createdBy 创建者ID
     * @return 记录数
     */
    int countByCreatedByAndSearch(String residentName, String companyName, String industry, String employmentStatus, Long createdBy);
    
    /**
     * 根据管理员和创建者ID及搜索条件分页搜索就业信息
     * @param residentName 居民姓名
     * @param companyName 工作单位
     * @param industry 行业
     * @param employmentStatus 就业状态
     * @param createdBy 创建者ID
     * @param page 页码
     * @param size 每页大小
     * @return 就业信息列表
     */
    List<Employment> searchByAdminOrCreatedBy(String residentName, String companyName, String industry, String employmentStatus, Long createdBy, int page, int size);
    
    /**
     * 根据管理员和创建者ID及搜索条件统计记录数
     * @param residentName 居民姓名
     * @param companyName 工作单位
     * @param industry 行业
     * @param employmentStatus 就业状态
     * @param createdBy 创建者ID
     * @return 记录数
     */
    int countByAdminOrCreatedByAndSearch(String residentName, String companyName, String industry, String employmentStatus, Long createdBy);
    
    /**
     * 根据居民ID列表和搜索条件分页查询就业信息
     * @param residentIds 居民ID列表
     * @param residentName 居民姓名
     * @param companyName 工作单位
     * @param industry 行业
     * @param employmentStatus 就业状态
     * @param page 页码
     * @param size 每页大小
     * @return 就业信息列表
     */
    List<Employment> searchByResidentIdsAndSearchConditions(List<Long> residentIds, String residentName, String companyName, String industry, String employmentStatus, int page, int size);
    
    /**
     * 根据居民ID列表和搜索条件统计就业信息数量
     * @param residentIds 居民ID列表
     * @param residentName 居民姓名
     * @param companyName 工作单位
     * @param industry 行业
     * @param employmentStatus 就业状态
     * @return 记录数
     */
    int countByResidentIdsAndSearchConditions(List<Long> residentIds, String residentName, String companyName, String industry, String employmentStatus);
}