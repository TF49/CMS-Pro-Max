package com.example.cms.modules.population.service.impl;

import com.example.cms.modules.population.entity.Employment;
import com.example.cms.modules.population.entity.Resident;
import com.example.cms.modules.population.mapper.EmploymentMapper;
import com.example.cms.modules.population.service.EmploymentService;
import com.example.cms.modules.population.service.ResidentService;
import com.example.cms.websocket.DataChangeEvent;
import com.example.cms.websocket.WebSocketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EmploymentServiceImpl implements EmploymentService {

    @Autowired
    private EmploymentMapper employmentMapper;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private WebSocketMessageService webSocketMessageService;

    @Autowired
    private org.springframework.cache.CacheManager cacheManager;
    
    @Override
    public List<Employment> findAll() {
        return employmentMapper.findAll();
    }
    
    @Override
    public Employment findById(Long id) {
        return employmentMapper.findById(id);
    }
    
    @Override
    public List<Employment> findByResidentId(Long residentId) {
        return employmentMapper.findByResidentId(residentId);
    }
    
    @Override
    public int insert(Employment employment) {
        // 验证居民信息
        validateResidentInfo(employment);
        
        // 验证就业信息是否完整
        if (!employment.hasValidEmploymentInfo()) {
            throw new IllegalArgumentException("就业信息不完整");
        }
        
        // 设置创建和更新时间
        LocalDateTime currentTime = LocalDateTime.now();
        if (employment.getCreateTime() == null) {
            employment.setCreateTime(currentTime);
        }
        employment.setUpdateTime(currentTime);
        
        // 确保createdBy字段有值，默认使用admin用户的id（1）
        if (employment.getCreatedBy() == null) {
            employment.setCreatedBy(1L);
        }
        
        // 手动设置连续ID，全局唯一
        Long maxId = employmentMapper.getMaxId();
        employment.setId(maxId + 1);
        
        // 插入就业信息
        int result = employmentMapper.insert(employment);

        // 清除统计缓存，确保统计数据实时更新
        if (result > 0 && cacheManager.getCache("statistics") != null) {
            cacheManager.getCache("statistics").clear();
        }

        // 广播WebSocket通知
        if (result > 0) {
            DataChangeEvent event = new DataChangeEvent(
                "employment",
                "INSERT",
                employment.getId(),
                employment.getCreatedBy(),
                employment,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }
    
    /**
     * 验证居民信息
     * @param employment 就业记录
     */
    private void validateResidentInfo(Employment employment) {
        // 如果提供了居民ID，检查居民ID是否存在
        if (employment.getResidentId() != null) {
            Resident resident = residentService.findById(employment.getResidentId());
            if (resident == null) {
                throw new IllegalArgumentException("居民ID不存在");
            }
            // 设置居民姓名和身份证号
            employment.setResidentName(resident.getName());
            employment.setIdCard(resident.getIdCard());
        } else {
            // 如果没有提供居民ID，检查身份证号
            if (employment.getIdCard() != null && !employment.getIdCard().isEmpty()) {
                List<Resident> residents = residentService.findByIdCard(employment.getIdCard());
                if (residents != null && !residents.isEmpty()) {
                    Resident resident = residents.get(0);
                    employment.setResidentId(resident.getId());
                    employment.setResidentName(resident.getName());
                } else {
                    throw new IllegalArgumentException("居民身份证号不存在");
                }
            } else {
                throw new IllegalArgumentException("居民ID或身份证号不能为空");
            }
        }
    }
    
    @Override
    public int update(Employment employment) {
        // 验证居民信息
        validateResidentInfo(employment);
        
        // 验证就业信息
        if (!employment.hasValidEmploymentInfo()) {
            throw new IllegalArgumentException("就业信息不完整");
        }
        
        // 设置更新时间
        employment.setUpdateTime(LocalDateTime.now());
        
        int result = employmentMapper.update(employment);

        // 清除统计缓存，确保统计数据实时更新
        if (result > 0 && cacheManager.getCache("statistics") != null) {
            cacheManager.getCache("statistics").clear();
        }

        // 广播WebSocket通知
        if (result > 0) {
            DataChangeEvent event = new DataChangeEvent(
                "employment",
                "UPDATE",
                employment.getId(),
                employment.getCreatedBy(),
                employment,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }

    @Override
    public int deleteById(Long id) {
        // 检查记录是否存在
        Employment employment = employmentMapper.findById(id);
        if (employment == null) {
            throw new IllegalArgumentException("就业记录不存在");
        }

        int result = employmentMapper.deleteById(id);

        // 删除成功后重新排序ID，全局唯一
        if (result > 0) {
            employmentMapper.reorderIds();
            // 清除统计缓存，确保统计数据实时更新
            if (cacheManager.getCache("statistics") != null) {
                cacheManager.getCache("statistics").clear();
            }

            // 广播WebSocket通知
            DataChangeEvent event = new DataChangeEvent(
                "employment",
                "DELETE",
                id,
                employment.getCreatedBy(),
                null,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }
    
    @Override
    public int count() {
        return employmentMapper.count();
    }
    
    @Override
    public List<Employment> search(String residentName, String companyName, String industry, String employmentStatus) {
        return employmentMapper.search(residentName, companyName, industry, employmentStatus);
    }
    
    @Override
    public List<Employment> searchWithPagination(String residentName, String companyName, String industry, String employmentStatus, int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;
        return employmentMapper.searchWithPagination(residentName, companyName, industry, employmentStatus, offset, size);
    }
    
    @Override
    public int countBySearch(String residentName, String companyName, String industry, String employmentStatus) {
        return employmentMapper.countBySearch(residentName, companyName, industry, employmentStatus);
    }
    
    @Override
    public List<Employment> searchByCreatedBy(String residentName, String companyName, String industry, String employmentStatus, Long createdBy, int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;
        return employmentMapper.searchByCreatedBy(residentName, companyName, industry, employmentStatus, createdBy, offset, size);
    }
    
    @Override
    public int countByCreatedByAndSearch(String residentName, String companyName, String industry, String employmentStatus, Long createdBy) {
        return employmentMapper.countByCreatedByAndSearch(residentName, companyName, industry, employmentStatus, createdBy);
    }
    
    @Override
    public List<Employment> searchByAdminOrCreatedBy(String residentName, String companyName, String industry, String employmentStatus, Long createdBy, int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;
        return employmentMapper.searchByAdminOrCreatedBy(residentName, companyName, industry, employmentStatus, createdBy, offset, size);
    }
    
    @Override
    public int countByAdminOrCreatedByAndSearch(String residentName, String companyName, String industry, String employmentStatus, Long createdBy) {
        return employmentMapper.countByAdminOrCreatedByAndSearch(residentName, companyName, industry, employmentStatus, createdBy);
    }

    @Override
    public List<Employment> searchByResidentIdsAndSearchConditions(List<Long> residentIds, String residentName, String companyName, String industry, String employmentStatus, int page, int size) {
        if (residentIds == null || residentIds.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        // 计算偏移量
        int offset = (page - 1) * size;
        return employmentMapper.searchByResidentIdsAndSearchConditions(residentIds, residentName, companyName, industry, employmentStatus, offset, size);
    }

    @Override
    public int countByResidentIdsAndSearchConditions(List<Long> residentIds, String residentName, String companyName, String industry, String employmentStatus) {
        if (residentIds == null || residentIds.isEmpty()) {
            return 0;
        }
        return employmentMapper.countByResidentIdsAndSearchConditions(residentIds, residentName, companyName, industry, employmentStatus);
    }
}