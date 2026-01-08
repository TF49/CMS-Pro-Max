package com.example.cms.modules.population.service;

import com.example.cms.modules.population.entity.Medical;
import java.util.List;

public interface MedicalService {
    List<Medical> findAll();
    Medical findById(Long id);
    int insert(Medical medical);
    int update(Medical medical);
    int deleteById(Long id);
    List<Medical> findByResidentId(Long residentId);
    int count();
    
    /**
     * 分页查询医疗记录
     * @param page 页码
     * @param size 每页大小
     * @return 医疗记录列表
     */
    List<Medical> findAllWithPagination(Integer page, Integer size);
    
    /**
     * 搜索医疗记录
     * @param residentName 居民姓名
     * @param hospitalName 医院名称
     * @return 医疗记录列表
     */
    List<Medical> search(String residentName, String hospitalName);
    
    /**
     * 根据创建者ID查询医疗记录（支持分页）
     * @param createdBy 创建者ID
     * @param page 页码
     * @param size 每页大小
     * @return 医疗记录列表
     */
    List<Medical> findByCreatedBy(Long createdBy, Integer page, Integer size);
    
    /**
     * 根据创建者ID统计医疗记录数量
     * @param createdBy 创建者ID
     * @return 医疗记录数量
     */
    int countByCreatedBy(Long createdBy);
    
    /**
     * 根据管理员和创建者ID查询医疗记录（支持分页）
     * @param createdBy 创建者ID
     * @param page 页码
     * @param size 每页大小
     * @return 医疗记录列表
     */
    List<Medical> findByAdminOrCreatedBy(Long createdBy, Integer page, Integer size);
    
    /**
     * 根据管理员和创建者ID统计医疗记录数量
     * @param createdBy 创建者ID
     * @return 医疗记录数量
     */
    int countByAdminOrCreatedBy(Long createdBy);
    
    /**
     * 根据居民ID列表查询医疗记录（支持分页）
     * @param residentIds 居民ID列表
     * @param page 页码
     * @param size 每页大小
     * @return 医疗记录列表
     */
    List<Medical> findByResidentIds(List<Long> residentIds, Integer page, Integer size);
    
    /**
     * 根据居民ID列表统计医疗记录数量
     * @param residentIds 居民ID列表
     * @return 医疗记录数量
     */
    int countByResidentIds(List<Long> residentIds);
}