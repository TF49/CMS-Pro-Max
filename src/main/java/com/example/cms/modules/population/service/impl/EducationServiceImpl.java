package com.example.cms.modules.population.service.impl;

import com.example.cms.common.util.UserContext;
import com.example.cms.modules.population.entity.Education;
import com.example.cms.modules.population.entity.Resident;
import com.example.cms.modules.population.mapper.EducationMapper;
import com.example.cms.modules.population.service.EducationService;
import com.example.cms.modules.population.service.ResidentService;
import com.example.cms.websocket.DataChangeEvent;
import com.example.cms.websocket.WebSocketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 教育记录服务实现类
 * 提供教育记录的CRUD操作和业务逻辑处理
 */
@Service
@Transactional
public class EducationServiceImpl implements EducationService {
    
    @Autowired
    private EducationMapper educationMapper;

    @Autowired
    private WebSocketMessageService webSocketMessageService;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private org.springframework.cache.CacheManager cacheManager;
    
    @Override
    public List<Education> findAll() {
        return educationMapper.findAll();
    }
    
    @Override
    public List<Education> findAllWithPagination(Integer page, Integer size) {
        Integer offset = page != null ? (page - 1) * size : null;
        return educationMapper.findAllWithPagination(offset, size);
    }

    @Override
    public Education findById(Long id) {
        return educationMapper.findById(id);
    }

    @Override
    public int insert(Education education) {
        // 验证居民信息
        validateResidentInfo(education);
        
        // 设置时间信息
        LocalDateTime now = LocalDateTime.now();
        if (education.getCreateTime() == null) {
            education.setCreateTime(now);
        }
        education.setUpdateTime(now);

        // 设置创建者ID
        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId != null) {
            education.setCreatedBy(currentUserId);
        } else if (education.getCreatedBy() == null) {
            // 如果无法获取当前用户ID，使用默认值
            education.setCreatedBy(1L);
        }

        // 手动设置连续ID，全局唯一
        Long maxId = educationMapper.getMaxId();
        education.setId(maxId + 1);

        int result = educationMapper.insert(education);

        // 清除统计缓存，确保统计数据实时更新
        if (result > 0 && cacheManager.getCache("statistics") != null) {
            cacheManager.getCache("statistics").clear();
        }

        // 广播WebSocket通知
        if (result > 0) {
            DataChangeEvent event = new DataChangeEvent(
                "education",
                "INSERT",
                education.getId(),
                education.getCreatedBy(),
                education,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }
    
    /**
     * 验证居民信息
     * @param education 教育记录
     */
    private void validateResidentInfo(Education education) {
        // 如果提供了居民ID，检查居民ID是否存在
        if (education.getResidentId() != null) {
            Resident resident = residentService.findById(education.getResidentId());
            if (resident == null) {
                throw new IllegalArgumentException("居民ID不存在");
            }
            // 设置居民姓名和身份证号
            education.setResidentName(resident.getName());
            education.setIdCard(resident.getIdCard());
        } else {
            // 如果没有提供居民ID，检查身份证号
            if (education.getIdCard() != null && !education.getIdCard().isEmpty()) {
                List<Resident> residents = residentService.findByIdCard(education.getIdCard());
                if (residents != null && !residents.isEmpty()) {
                    Resident resident = residents.get(0);
                    education.setResidentId(resident.getId());
                    education.setResidentName(resident.getName());
                } else {
                    throw new IllegalArgumentException("居民身份证号不存在");
                }
            } else {
                throw new IllegalArgumentException("居民ID或身份证号不能为空");
            }
        }
    }

    @Override
    public int update(Education education) {
        // 验证居民信息
        validateResidentInfo(education);

        // 设置更新时间
        education.setUpdateTime(LocalDateTime.now());

        int result = educationMapper.update(education);

        // 清除统计缓存，确保统计数据实时更新
        if (result > 0 && cacheManager.getCache("statistics") != null) {
            cacheManager.getCache("statistics").clear();
        }

        // 广播WebSocket通知
        if (result > 0) {
            DataChangeEvent event = new DataChangeEvent(
                "education",
                "UPDATE",
                education.getId(),
                education.getCreatedBy(),
                education,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }
    
    @Override
    public int deleteById(Long id) {
        // 检查记录是否存在
        Education education = educationMapper.findById(id);
        if (education == null) {
            throw new IllegalArgumentException("教育记录不存在");
        }

        int result = educationMapper.deleteById(id);

        // 删除成功后重新排序ID，全局唯一
        if (result > 0) {
            educationMapper.reorderIds();
            // 清除统计缓存，确保统计数据实时更新
            if (cacheManager.getCache("statistics") != null) {
                cacheManager.getCache("statistics").clear();
            }

            // 广播WebSocket通知
            DataChangeEvent event = new DataChangeEvent(
                "education",
                "DELETE",
                id,
                education.getCreatedBy(),
                null,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }

    @Override
    public List<Education> findByResidentId(Long residentId) {
        return educationMapper.findByResidentId(residentId);
    }
    
    @Override
    public List<Education> search(String residentName, String schoolName, Integer page, Integer size) {
        Integer offset = page != null ? (page - 1) * size : null;
        return educationMapper.search(residentName, schoolName, offset, size);
    }
    
    @Override
    public int count() {
        return educationMapper.count();
    }
    
    /**
     * 根据居民身份证号获取教育记录
     * @param idCard 居民身份证号
     * @return 教育记录列表
     */
    @Override
    public List<Education> findByResidentIdCard(String idCard) {
        return educationMapper.findByResidentIdCard(idCard);
    }
    
    @Override
    public List<Education> findByCreatedBy(Long createdBy, Integer page, Integer size) {
        Integer offset = page != null ? (page - 1) * size : null;
        return educationMapper.findByCreatedBy(createdBy, offset, size);
    }
    
    @Override
    public int countByCreatedBy(Long createdBy) {
        return educationMapper.countByCreatedBy(createdBy);
    }
    
    @Override
    public List<Education> findByAdminOrCreatedBy(Long createdBy, Integer page, Integer size) {
        Integer offset = page != null ? (page - 1) * size : null;
        return educationMapper.findByAdminOrCreatedBy(createdBy, offset, size);
    }
    
    @Override
    public int countByAdminOrCreatedBy(Long createdBy) {
        return educationMapper.countByAdminOrCreatedBy(createdBy);
    }
    
    @Override
    public List<Education> findByResidentIds(List<Long> residentIds, Integer page, Integer size) {
        if (residentIds == null || residentIds.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        Integer offset = page != null ? (page - 1) * size : null;
        return educationMapper.findByResidentIds(residentIds, offset, size);
    }
    
    @Override
    public int countByResidentIds(List<Long> residentIds) {
        if (residentIds == null || residentIds.isEmpty()) {
            return 0;
        }
        return educationMapper.countByResidentIds(residentIds);
    }
}