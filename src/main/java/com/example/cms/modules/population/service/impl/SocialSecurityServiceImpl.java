package com.example.cms.modules.population.service.impl;

import com.example.cms.modules.population.entity.Resident;
import com.example.cms.modules.population.entity.SocialSecurity;
import com.example.cms.modules.population.mapper.SocialSecurityMapper;
import com.example.cms.modules.population.service.ResidentService;
import com.example.cms.modules.population.service.SocialSecurityService;
import com.example.cms.websocket.DataChangeEvent;
import com.example.cms.websocket.WebSocketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SocialSecurityServiceImpl implements SocialSecurityService {

    @Autowired
    private ResidentService residentService;

    @Autowired
    private SocialSecurityMapper socialSecurityMapper;

    @Autowired
    private WebSocketMessageService webSocketMessageService;

    @Autowired
    private org.springframework.cache.CacheManager cacheManager;
    
    @Override
    public List<SocialSecurity> findAll() {
        return socialSecurityMapper.findAll();
    }
    
    @Override
    public SocialSecurity findById(Long id) {
        return socialSecurityMapper.findById(id);
    }
    
    @Override
    public List<SocialSecurity> findByResidentId(Long residentId) {
        return socialSecurityMapper.findByResidentId(residentId);
    }
    
    @Override
    public int insert(SocialSecurity socialSecurity) {
        // 验证居民信息
        validateResidentInfo(socialSecurity);
        
        // 验证身份证号是否已存在社保记录
        List<SocialSecurity> existingRecords = socialSecurityMapper.findByIdCard(socialSecurity.getIdCard());
        if (existingRecords != null && !existingRecords.isEmpty()) {
            throw new IllegalArgumentException("该身份证号已存在社保记录");
        }
        
        LocalDateTime currentTime = LocalDateTime.now();
        
        if (socialSecurity.getCreateTime() == null) {
            socialSecurity.setCreateTime(currentTime);
        }
        socialSecurity.setUpdateTime(currentTime);
        
        // 确保createdBy字段有值，默认使用admin用户的id（1）
        if (socialSecurity.getCreatedBy() == null) {
            socialSecurity.setCreatedBy(1L);
        }
        
        // 手动设置连续ID，全局唯一
        Long maxId = socialSecurityMapper.getMaxId();
        socialSecurity.setId(maxId + 1);
        
        int result = socialSecurityMapper.insert(socialSecurity);

        // 清除统计缓存，确保统计数据实时更新
        if (result > 0 && cacheManager.getCache("statistics") != null) {
            cacheManager.getCache("statistics").clear();
        }

        // 广播WebSocket通知
        if (result > 0) {
            DataChangeEvent event = new DataChangeEvent(
                "socialSecurity",
                "INSERT",
                socialSecurity.getId(),
                socialSecurity.getCreatedBy(),
                socialSecurity,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }
    
    /**
     * 验证居民信息
     * @param socialSecurity 社保记录
     */
    private void validateResidentInfo(SocialSecurity socialSecurity) {
        // 如果提供了居民ID，检查居民ID是否存在
        if (socialSecurity.getResidentId() != null) {
            Resident resident = residentService.findById(socialSecurity.getResidentId());
            if (resident == null) {
                throw new IllegalArgumentException("居民ID不存在");
            }
            // 设置居民姓名和身份证号
            socialSecurity.setResidentName(resident.getName());
            socialSecurity.setIdCard(resident.getIdCard());
        } else {
            // 如果没有提供居民ID，检查身份证号
            if (socialSecurity.getIdCard() != null && !socialSecurity.getIdCard().isEmpty()) {
                List<Resident> residents = residentService.findByIdCard(socialSecurity.getIdCard());
                if (residents != null && !residents.isEmpty()) {
                    Resident resident = residents.get(0);
                    socialSecurity.setResidentId(resident.getId());
                    socialSecurity.setResidentName(resident.getName());
                } else {
                    throw new IllegalArgumentException("居民身份证号不存在");
                }
            } else {
                throw new IllegalArgumentException("居民ID或身份证号不能为空");
            }
        }
    }
    
    @Override
    public int update(SocialSecurity socialSecurity) {
        // 验证居民信息
        validateResidentInfo(socialSecurity);
        
        socialSecurity.setUpdateTime(LocalDateTime.now());
        
        int result = socialSecurityMapper.update(socialSecurity);

        // 清除统计缓存，确保统计数据实时更新
        if (result > 0 && cacheManager.getCache("statistics") != null) {
            cacheManager.getCache("statistics").clear();
        }

        // 广播WebSocket通知
        if (result > 0) {
            DataChangeEvent event = new DataChangeEvent(
                "socialSecurity",
                "UPDATE",
                socialSecurity.getId(),
                socialSecurity.getCreatedBy(),
                socialSecurity,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }

    @Override
    public int deleteById(Long id) {
        // 检查记录是否存在
        SocialSecurity socialSecurity = socialSecurityMapper.findById(id);
        if (socialSecurity == null) {
            throw new IllegalArgumentException("社保记录不存在");
        }

        int result = socialSecurityMapper.deleteById(id);

        // 删除成功后全局重新排序ID
        if (result > 0) {
            socialSecurityMapper.reorderIds();
            // 清除统计缓存，确保统计数据实时更新
            if (cacheManager.getCache("statistics") != null) {
                cacheManager.getCache("statistics").clear();
            }

            // 广播WebSocket通知
            if (socialSecurity != null) {
                DataChangeEvent event = new DataChangeEvent(
                    "socialSecurity",
                    "DELETE",
                    id,
                    socialSecurity.getCreatedBy(),
                    null,
                    System.currentTimeMillis()
                );
                webSocketMessageService.broadcastDataChange(event);
            }
        }

        return result;
    }
    
    @Override
    public int count() {
        return socialSecurityMapper.count();
    }

    @Override
    public List<SocialSecurity> search(String residentName, String idCard, String insuranceStatus, String pensionInsurance, String medicalInsurance) {
        return socialSecurityMapper.search(residentName, idCard, insuranceStatus, pensionInsurance, medicalInsurance);
    }

    @Override
    public List<SocialSecurity> searchWithPagination(String residentName, String idCard, String insuranceStatus, String pensionInsurance, String medicalInsurance, int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;
        return socialSecurityMapper.searchWithPagination(residentName, idCard, insuranceStatus, pensionInsurance, medicalInsurance, offset, size);
    }

    @Override
    public int countBySearch(String residentName, String idCard, String insuranceStatus, String pensionInsurance, String medicalInsurance) {
        return socialSecurityMapper.countBySearch(residentName, idCard, insuranceStatus, pensionInsurance, medicalInsurance);
    }
    
    @Override
    public List<SocialSecurity> searchByCreatedBy(String residentName, String idCard, String insuranceStatus, String pensionInsurance, String medicalInsurance, Long createdBy, int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;
        return socialSecurityMapper.searchByCreatedBy(residentName, idCard, insuranceStatus, pensionInsurance, medicalInsurance, createdBy, offset, size);
    }
    
    @Override
    public int countByCreatedByAndSearch(String residentName, String idCard, String insuranceStatus, String pensionInsurance, String medicalInsurance, Long createdBy) {
        return socialSecurityMapper.countByCreatedByAndSearch(residentName, idCard, insuranceStatus, pensionInsurance, medicalInsurance, createdBy);
    }
    
    @Override
    public List<SocialSecurity> searchByAdminOrCreatedBy(String residentName, String idCard, String insuranceStatus, String pensionInsurance, String medicalInsurance, Long createdBy, int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;
        return socialSecurityMapper.searchByAdminOrCreatedBy(residentName, idCard, insuranceStatus, pensionInsurance, medicalInsurance, createdBy, offset, size);
    }
    
    @Override
    public int countByAdminOrCreatedByAndSearch(String residentName, String idCard, String insuranceStatus, String pensionInsurance, String medicalInsurance, Long createdBy) {
        return socialSecurityMapper.countByAdminOrCreatedByAndSearch(residentName, idCard, insuranceStatus, pensionInsurance, medicalInsurance, createdBy);
    }

    @Override
    public List<SocialSecurity> searchByResidentIdsAndSearchConditions(List<Long> residentIds, String residentName, String idCard, String insuranceStatus, String pensionInsurance, String medicalInsurance, int page, int size) {
        if (residentIds == null || residentIds.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        // 计算偏移量
        int offset = (page - 1) * size;
        return socialSecurityMapper.searchByResidentIdsAndSearchConditions(residentIds, residentName, idCard, insuranceStatus, pensionInsurance, medicalInsurance, offset, size);
    }

    @Override
    public int countByResidentIdsAndSearchConditions(List<Long> residentIds, String residentName, String idCard, String insuranceStatus, String pensionInsurance, String medicalInsurance) {
        if (residentIds == null || residentIds.isEmpty()) {
            return 0;
        }
        return socialSecurityMapper.countByResidentIdsAndSearchConditions(residentIds, residentName, idCard, insuranceStatus, pensionInsurance, medicalInsurance);
    }
}