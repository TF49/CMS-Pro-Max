package com.example.cms.modules.population.mapper;

import com.example.cms.modules.population.entity.SocialSecurity;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SocialSecurityMapper {
    
    List<SocialSecurity> findAll();
    
    SocialSecurity findById(Long id);
    
    int insert(SocialSecurity socialSecurity);
    
    int update(SocialSecurity socialSecurity);
    
    int deleteById(Long id);
    
    List<SocialSecurity> findByResidentId(Long residentId);
    
    int count();
    
    List<SocialSecurity> search(@Param("residentName") String residentName, 
                              @Param("idCard") String idCard,
                              @Param("insuranceStatus") String insuranceStatus, 
                              @Param("pensionInsurance") String pensionInsurance, 
                              @Param("medicalInsurance") String medicalInsurance);
    
    /**
     * 分页搜索社保信息
     * @param residentName 居民姓名
     * @param idCard 身份证号码
     * @param insuranceStatus 参保状态
     * @param pensionInsurance 养老保险
     * @param medicalInsurance 医疗保险
     * @param offset 偏移量
     * @param size 每页大小
     * @return 社保信息列表
     */
    List<SocialSecurity> searchWithPagination(@Param("residentName") String residentName, 
                                            @Param("idCard") String idCard,
                                            @Param("insuranceStatus") String insuranceStatus, 
                                            @Param("pensionInsurance") String pensionInsurance, 
                                            @Param("medicalInsurance") String medicalInsurance,
                                            @Param("offset") int offset, 
                                            @Param("size") int size);
    
    /**
     * 统计搜索条件下的记录数
     * @param residentName 居民姓名
     * @param idCard 身份证号码
     * @param insuranceStatus 参保状态
     * @param pensionInsurance 养老保险
     * @param medicalInsurance 医疗保险
     * @return 记录数
     */
    int countBySearch(@Param("residentName") String residentName, 
                    @Param("idCard") String idCard,
                    @Param("insuranceStatus") String insuranceStatus, 
                    @Param("pensionInsurance") String pensionInsurance, 
                    @Param("medicalInsurance") String medicalInsurance);
    
    /**
     * 根据创建者ID和搜索条件分页搜索社保信息
     * @param residentName 居民姓名
     * @param idCard 身份证号码
     * @param insuranceStatus 参保状态
     * @param pensionInsurance 养老保险
     * @param medicalInsurance 医疗保险
     * @param createdBy 创建者ID
     * @param offset 偏移量
     * @param size 每页大小
     * @return 社保信息列表
     */
    List<SocialSecurity> searchByCreatedBy(@Param("residentName") String residentName, 
                                         @Param("idCard") String idCard,
                                         @Param("insuranceStatus") String insuranceStatus, 
                                         @Param("pensionInsurance") String pensionInsurance, 
                                         @Param("medicalInsurance") String medicalInsurance,
                                         @Param("createdBy") Long createdBy,
                                         @Param("offset") int offset, 
                                         @Param("size") int size);
    
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
    int countByCreatedByAndSearch(@Param("residentName") String residentName, 
                               @Param("idCard") String idCard,
                               @Param("insuranceStatus") String insuranceStatus, 
                               @Param("pensionInsurance") String pensionInsurance, 
                               @Param("medicalInsurance") String medicalInsurance,
                               @Param("createdBy") Long createdBy);
    
    /**
     * 根据管理员和创建者ID及搜索条件分页搜索社保信息
     * @param residentName 居民姓名
     * @param idCard 身份证号码
     * @param insuranceStatus 参保状态
     * @param pensionInsurance 养老保险
     * @param medicalInsurance 医疗保险
     * @param createdBy 创建者ID
     * @param offset 偏移量
     * @param size 每页大小
     * @return 社保信息列表
     */
    List<SocialSecurity> searchByAdminOrCreatedBy(@Param("residentName") String residentName, 
                                             @Param("idCard") String idCard,
                                             @Param("insuranceStatus") String insuranceStatus, 
                                             @Param("pensionInsurance") String pensionInsurance, 
                                             @Param("medicalInsurance") String medicalInsurance,
                                             @Param("createdBy") Long createdBy,
                                             @Param("offset") int offset, 
                                             @Param("size") int size);
    
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
    int countByAdminOrCreatedByAndSearch(@Param("residentName") String residentName, 
                               @Param("idCard") String idCard,
                               @Param("insuranceStatus") String insuranceStatus, 
                               @Param("pensionInsurance") String pensionInsurance, 
                               @Param("medicalInsurance") String medicalInsurance,
                               @Param("createdBy") Long createdBy);
    
    /**
     * 获取社保表中的最大ID
     * @return 最大ID
     */
    Long getMaxId();
    
    /**
     * 重新排序社保记录ID，确保ID连续
     */
    void reorderIds();
    
    /**
     * 根据身份证号查询社保记录
     * @param idCard 身份证号码
     * @return 社保记录列表
     */
    List<SocialSecurity> findByIdCard(@Param("idCard") String idCard);
    
    /**
     * 根据居民ID列表和搜索条件分页查询社保信息
     * @param residentIds 居民ID列表
     * @param residentName 居民姓名
     * @param idCard 身份证号码
     * @param insuranceStatus 参保状态
     * @param pensionInsurance 养老保险
     * @param medicalInsurance 医疗保险
     * @param offset 偏移量
     * @param size 每页大小
     * @return 社保信息列表
     */
    List<SocialSecurity> searchByResidentIdsAndSearchConditions(@Param("residentIds") List<Long> residentIds,
                                                           @Param("residentName") String residentName,
                                                           @Param("idCard") String idCard,
                                                           @Param("insuranceStatus") String insuranceStatus,
                                                           @Param("pensionInsurance") String pensionInsurance,
                                                           @Param("medicalInsurance") String medicalInsurance,
                                                           @Param("offset") int offset,
                                                           @Param("size") int size);
    
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
    int countByResidentIdsAndSearchConditions(@Param("residentIds") List<Long> residentIds,
                                           @Param("residentName") String residentName,
                                           @Param("idCard") String idCard,
                                           @Param("insuranceStatus") String insuranceStatus,
                                           @Param("pensionInsurance") String pensionInsurance,
                                           @Param("medicalInsurance") String medicalInsurance);
}