package com.example.cms.modules.population.service;

import com.example.cms.modules.population.entity.SocialSecurity;
import java.util.List;

public interface SocialSecurityService {
    
    List<SocialSecurity> findAll();
    
    SocialSecurity findById(Long id);
    
    List<SocialSecurity> findByResidentId(Long residentId);
    
    int insert(SocialSecurity socialSecurity);
    
    int update(SocialSecurity socialSecurity);
    
    int deleteById(Long id);
    
    int count();
    
    List<SocialSecurity> search(String residentName, String idCard, String insuranceStatus, String pensionInsurance, String medicalInsurance);
    
    /**
     * 分页搜索社保信息
     * @param residentName 居民姓名
     * @param idCard 身份证号码
     * @param insuranceStatus 参保状态
     * @param pensionInsurance 养老保险
     * @param medicalInsurance 医疗保险
     * @param page 页码
     * @param size 每页大小
     * @return 社保信息列表
     */
    List<SocialSecurity> searchWithPagination(String residentName, String idCard, String insuranceStatus, String pensionInsurance, String medicalInsurance, int page, int size);
    
    /**
     * 统计搜索条件下的记录数
     * @param residentName 居民姓名
     * @param idCard 身份证号码
     * @param insuranceStatus 参保状态
     * @param pensionInsurance 养老保险
     * @param medicalInsurance 医疗保险
     * @return 记录数
     */
    int countBySearch(String residentName, String idCard, String insuranceStatus, String pensionInsurance, String medicalInsurance);
    
    /**
     * 根据创建者ID和搜索条件分页搜索社保信息
     * @param residentName 居民姓名
     * @param idCard 身份证号码
     * @param insuranceStatus 参保状态
     * @param pensionInsurance 养老保险
     * @param medicalInsurance 医疗保险
     * @param createdBy 创建者ID
     * @param page 页码
     * @param size 每页大小
     * @return 社保信息列表
     */
    List<SocialSecurity> searchByCreatedBy(String residentName, String idCard, String insuranceStatus, String pensionInsurance, String medicalInsurance, Long createdBy, int page, int size);
    
    /**
     * 根据创建者ID和搜索条件统计记录数
     * @param residentName 居民姓名
     * @param idCard 身份证号码
     * @param insuranceStatus 参保状态
     * @param pensionInsurance 养老保险
     * @param medicalInsurance 医疗保险
     * @param createdBy 创建者ID
     * @return 记录数
     */
    int countByCreatedByAndSearch(String residentName, String idCard, String insuranceStatus, String pensionInsurance, String medicalInsurance, Long createdBy);
    
    /**
     * 根据管理员和创建者ID及搜索条件分页搜索社保信息
     * @param residentName 居民姓名
     * @param idCard 身份证号码
     * @param insuranceStatus 参保状态
     * @param pensionInsurance 养老保险
     * @param medicalInsurance 医疗保险
     * @param createdBy 创建者ID
     * @param page 页码
     * @param size 每页大小
     * @return 社保信息列表
     */
    List<SocialSecurity> searchByAdminOrCreatedBy(String residentName, String idCard, String insuranceStatus, String pensionInsurance, String medicalInsurance, Long createdBy, int page, int size);
    
    /**
     * 根据管理员和创建者ID及搜索条件统计记录数
     * @param residentName 居民姓名
     * @param idCard 身份证号码
     * @param insuranceStatus 参保状态
     * @param pensionInsurance 养老保险
     * @param medicalInsurance 医疗保险
     * @param createdBy 创建者ID
     * @return 记录数
     */
    int countByAdminOrCreatedByAndSearch(String residentName, String idCard, String insuranceStatus, String pensionInsurance, String medicalInsurance, Long createdBy);
    
    /**
     * 根据居民ID列表和搜索条件分页查询社保信息
     * @param residentIds 居民ID列表
     * @param residentName 居民姓名
     * @param idCard 身份证号码
     * @param insuranceStatus 参保状态
     * @param pensionInsurance 养老保险
     * @param medicalInsurance 医疗保险
     * @param page 页码
     * @param size 每页大小
     * @return 社保信息列表
     */
    List<SocialSecurity> searchByResidentIdsAndSearchConditions(List<Long> residentIds, String residentName, String idCard, String insuranceStatus, String pensionInsurance, String medicalInsurance, int page, int size);
    
    /**
     * 根据居民ID列表和搜索条件统计社保信息数量
     * @param residentIds 居民ID列表
     * @param residentName 居民姓名
     * @param idCard 身份证号码
     * @param insuranceStatus 参保状态
     * @param pensionInsurance 养老保险
     * @param medicalInsurance 医疗保险
     * @return 记录数
     */
    int countByResidentIdsAndSearchConditions(List<Long> residentIds, String residentName, String idCard, String insuranceStatus, String pensionInsurance, String medicalInsurance);
}