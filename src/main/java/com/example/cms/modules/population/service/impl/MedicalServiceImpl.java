package com.example.cms.modules.population.service.impl;

import com.example.cms.modules.population.entity.Medical;
import com.example.cms.modules.population.entity.Resident;
import com.example.cms.modules.population.mapper.MedicalMapper;
import com.example.cms.modules.population.service.MedicalService;
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
public class MedicalServiceImpl implements MedicalService {

    @Autowired
    private MedicalMapper medicalMapper;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private WebSocketMessageService webSocketMessageService;

    @Autowired
    private org.springframework.cache.CacheManager cacheManager;
    
    @Override
    public List<Medical> findAll() {
        return medicalMapper.findAll();
    }
    
    @Override
    public Medical findById(Long id) {
        return medicalMapper.findById(id);
    }
    
    @Override
    public int insert(Medical medical) {
        // 验证居民信息
        validateResidentInfo(medical);
        
        // 设置时间信息
        LocalDateTime now = LocalDateTime.now();
        if (medical.getCreateTime() == null) {
            medical.setCreateTime(now);
        }
        medical.setUpdateTime(now);
        
        // 确保createdBy字段有值，默认使用admin用户的id（1）
        if (medical.getCreatedBy() == null) {
            medical.setCreatedBy(1L);
        }
        
        // 手动设置连续ID，全局唯一
        Long maxId = medicalMapper.getMaxId();
        medical.setId(maxId + 1);
        
        int result = medicalMapper.insert(medical);

        // 清除统计缓存，确保统计数据实时更新
        if (result > 0 && cacheManager.getCache("statistics") != null) {
            cacheManager.getCache("statistics").clear();
        }

        // 广播WebSocket通知
        if (result > 0) {
            DataChangeEvent event = new DataChangeEvent(
                "medical",
                "INSERT",
                medical.getId(),
                medical.getCreatedBy(),
                medical,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }
    
    /**
     * 验证居民信息
     * @param medical 医疗记录
     */
    private void validateResidentInfo(Medical medical) {
        // 如果提供了居民ID，检查居民ID是否存在
        if (medical.getResidentId() != null) {
            Resident resident = residentService.findById(medical.getResidentId());
            if (resident == null) {
                throw new IllegalArgumentException("居民ID不存在");
            }
            // 设置居民姓名和身份证号
            medical.setResidentName(resident.getName());
            medical.setIdCard(resident.getIdCard());
        } else {
            // 如果没有提供居民ID，检查身份证号
            if (medical.getIdCard() != null && !medical.getIdCard().isEmpty()) {
                List<Resident> residents = residentService.findByIdCard(medical.getIdCard());
                if (residents != null && !residents.isEmpty()) {
                    Resident resident = residents.get(0);
                    medical.setResidentId(resident.getId());
                    medical.setResidentName(resident.getName());
                } else {
                    throw new IllegalArgumentException("居民身份证号不存在");
                }
            } else {
                throw new IllegalArgumentException("居民ID或身份证号不能为空");
            }
        }
    }
    
    @Override
    public int update(Medical medical) {
        // 获取现有记录
        Medical existingMedical = medicalMapper.findById(medical.getId());
        if (existingMedical == null) {
            throw new IllegalArgumentException("医疗记录不存在");
        }
        
        // 只在居民ID或身份证号发生变化时验证居民信息
        if (medical.getResidentId() != null && !medical.getResidentId().equals(existingMedical.getResidentId())) {
            validateResidentInfo(medical);
        } else if (medical.getIdCard() != null && !medical.getIdCard().equals(existingMedical.getIdCard())) {
            validateResidentInfo(medical);
        } else {
            // 保留原有居民信息
            medical.setResidentId(existingMedical.getResidentId());
            medical.setResidentName(existingMedical.getResidentName());
            medical.setIdCard(existingMedical.getIdCard());
        }
        
        // 设置更新时间
        medical.setUpdateTime(LocalDateTime.now());
        // 保留原有创建信息
        medical.setCreatedBy(existingMedical.getCreatedBy());
        medical.setCreateTime(existingMedical.getCreateTime());
        
        int result = medicalMapper.update(medical);

        // 清除统计缓存，确保统计数据实时更新
        if (result > 0 && cacheManager.getCache("statistics") != null) {
            cacheManager.getCache("statistics").clear();
        }

        // 广播WebSocket通知
        if (result > 0) {
            DataChangeEvent event = new DataChangeEvent(
                "medical",
                "UPDATE",
                medical.getId(),
                medical.getCreatedBy(),
                medical,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }

    @Override
    public int deleteById(Long id) {
        // 检查记录是否存在
        Medical medical = medicalMapper.findById(id);
        if (medical == null) {
            throw new IllegalArgumentException("医疗记录不存在");
        }

        int result = medicalMapper.deleteById(id);

        // 删除成功后重新排序ID，全局唯一
        if (result > 0) {
            medicalMapper.reorderIds();
            // 清除统计缓存，确保统计数据实时更新
            if (cacheManager.getCache("statistics") != null) {
                cacheManager.getCache("statistics").clear();
            }

            // 广播WebSocket通知
            DataChangeEvent event = new DataChangeEvent(
                "medical",
                "DELETE",
                id,
                medical.getCreatedBy(),
                null,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }
    
    @Override
    public List<Medical> findByResidentId(Long residentId) {
        return medicalMapper.findByResidentId(residentId);
    }
    
    @Override
    public int count() {
        return medicalMapper.count();
    }
    
    @Override
    public List<Medical> findAllWithPagination(Integer page, Integer size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        
        int offset = (page - 1) * size;
        return medicalMapper.findAllWithPagination(offset, size);
    }
    
    @Override
    public List<Medical> search(String residentName, String hospitalName) {
        return medicalMapper.search(residentName, hospitalName);
    }
    
    @Override
    public List<Medical> findByCreatedBy(Long createdBy, Integer page, Integer size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        
        int offset = (page - 1) * size;
        return medicalMapper.findByCreatedBy(createdBy, offset, size);
    }
    
    @Override
    public int countByCreatedBy(Long createdBy) {
        return medicalMapper.countByCreatedBy(createdBy);
    }
    
    @Override
    public List<Medical> findByAdminOrCreatedBy(Long createdBy, Integer page, Integer size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        
        int offset = (page - 1) * size;
        return medicalMapper.findByAdminOrCreatedBy(createdBy, offset, size);
    }
    
    @Override
    public int countByAdminOrCreatedBy(Long createdBy) {
        return medicalMapper.countByAdminOrCreatedBy(createdBy);
    }

    @Override
    public List<Medical> findByResidentIds(List<Long> residentIds, Integer page, Integer size) {
        if (residentIds == null || residentIds.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        Integer offset = (page - 1) * size;
        return medicalMapper.findByResidentIds(residentIds, offset, size);
    }

    @Override
    public int countByResidentIds(List<Long> residentIds) {
        if (residentIds == null || residentIds.isEmpty()) {
            return 0;
        }
        return medicalMapper.countByResidentIds(residentIds);
    }
}