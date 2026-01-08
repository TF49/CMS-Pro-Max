package com.example.cms.modules.population.service;

import com.example.cms.modules.population.entity.Education;
import java.util.List;

/**
 * 教育记录服务接口
 * 提供教育记录的CRUD操作和业务逻辑处理
 */
public interface EducationService {
    List<Education> findAll();
    List<Education> findAllWithPagination(Integer page, Integer size);
    Education findById(Long id);
    int insert(Education education);
    int update(Education education);
    int deleteById(Long id);
    List<Education> findByResidentId(Long residentId);
    List<Education> findByResidentIdCard(String idCard);
    List<Education> search(String residentName, String schoolName, Integer page, Integer size);
    int count();
    List<Education> findByCreatedBy(Long createdBy, Integer page, Integer size);
    int countByCreatedBy(Long createdBy);
    List<Education> findByAdminOrCreatedBy(Long createdBy, Integer page, Integer size);
    int countByAdminOrCreatedBy(Long createdBy);
    List<Education> findByResidentIds(List<Long> residentIds, Integer page, Integer size);
    int countByResidentIds(List<Long> residentIds);
}