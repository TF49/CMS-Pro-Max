package com.example.cms.modules.population.service.impl;

import com.example.cms.modules.population.entity.Property;
import com.example.cms.modules.population.entity.Resident;
import com.example.cms.modules.population.mapper.PropertyMapper;
import com.example.cms.modules.population.service.PropertyService;
import com.example.cms.modules.population.service.ResidentService;
import com.example.cms.websocket.DataChangeEvent;
import com.example.cms.websocket.WebSocketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {

    @Autowired
    private PropertyMapper propertyMapper;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private WebSocketMessageService webSocketMessageService;

    @Autowired
    private org.springframework.cache.CacheManager cacheManager;
    
    @Override
    public List<Property> findAll() {
        return propertyMapper.findAll();
    }
    
    @Override
    public Property findById(Long id) {
        return propertyMapper.findById(id);
    }
    
    @Override
    public List<Property> findByResidentId(Long residentId) {
        return propertyMapper.findByResidentId(residentId);
    }
    
    @Override
    public int insert(Property property) {
        LocalDateTime currentTime = LocalDateTime.now();
        
        if (property.getCreateTime() == null) {
            property.setCreateTime(currentTime);
        }
        property.setUpdateTime(currentTime);
        
        // 确保createdBy字段有值，默认使用admin用户的id（1）
        if (property.getCreatedBy() == null) {
            property.setCreatedBy(1L);
        }
        
        // 根据身份证号查询居民ID
        if (property.getResidentId() == null && property.getIdCard() != null) {
            List<Resident> residents = residentService.findByIdCard(property.getIdCard());
            if (residents != null && !residents.isEmpty()) {
                property.setResidentId(residents.get(0).getId());
            }
        }
        
        // 手动设置连续ID，全局唯一
        Long maxId = propertyMapper.getMaxId();
        property.setId(maxId + 1);
        
        int result = propertyMapper.insert(property);

        // 清除统计缓存，确保统计数据实时更新
        if (result > 0 && cacheManager.getCache("statistics") != null) {
            cacheManager.getCache("statistics").clear();
        }

        // 广播WebSocket通知
        if (result > 0) {
            DataChangeEvent event = new DataChangeEvent(
                "property",
                "INSERT",
                property.getId(),
                property.getCreatedBy(),
                property,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }
    
    @Override
    public int update(Property property) {
        property.setUpdateTime(LocalDateTime.now());

        int result = propertyMapper.update(property);

        // 清除统计缓存，确保统计数据实时更新
        if (result > 0 && cacheManager.getCache("statistics") != null) {
            cacheManager.getCache("statistics").clear();
        }

        // 广播WebSocket通知
        if (result > 0) {
            DataChangeEvent event = new DataChangeEvent(
                "property",
                "UPDATE",
                property.getId(),
                property.getCreatedBy(),
                property,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }

    @Override
    public int deleteById(Long id) {
        Property property = propertyMapper.findById(id);
        int result = propertyMapper.deleteById(id);

        // 删除成功后重新排序ID，全局唯一
        if (result > 0) {
            propertyMapper.reorderIds();
            // 清除统计缓存，确保统计数据实时更新
            if (cacheManager.getCache("statistics") != null) {
                cacheManager.getCache("statistics").clear();
            }

            // 广播WebSocket通知
            if (property != null) {
                DataChangeEvent event = new DataChangeEvent(
                    "property",
                    "DELETE",
                    id,
                    property.getCreatedBy(),
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
        return propertyMapper.count();
    }
    
    @Override
    public List<Property> search(String residentName, String propertyType, String usageType, String address) {
        return propertyMapper.search(residentName, propertyType, usageType, address);
    }
    
    @Override
    public List<Property> searchWithPagination(String residentName, String propertyType, String usageType, String address, int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;
        return propertyMapper.searchWithPagination(residentName, propertyType, usageType, address, offset, size);
    }
    
    @Override
    public int countBySearch(String residentName, String propertyType, String usageType, String address) {
        return propertyMapper.countBySearch(residentName, propertyType, usageType, address);
    }
    
    @Override
    public List<Property> searchByCreatedBy(String residentName, String propertyType, String usageType, String address, Long createdBy, int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;
        return propertyMapper.searchByCreatedBy(residentName, propertyType, usageType, address, createdBy, offset, size);
    }
    
    @Override
    public int countByCreatedByAndSearch(String residentName, String propertyType, String usageType, String address, Long createdBy) {
        return propertyMapper.countByCreatedByAndSearch(residentName, propertyType, usageType, address, createdBy);
    }
    
    @Override
    public List<Property> searchByAdminOrCreatedBy(String residentName, String propertyType, String usageType, String address, Long createdBy, int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;
        return propertyMapper.searchByAdminOrCreatedBy(residentName, propertyType, usageType, address, createdBy, offset, size);
    }
    
    @Override
    public int countByAdminOrCreatedByAndSearch(String residentName, String propertyType, String usageType, String address, Long createdBy) {
        return propertyMapper.countByAdminOrCreatedByAndSearch(residentName, propertyType, usageType, address, createdBy);
    }

    @Override
    public List<Property> searchByResidentIdsAndSearchConditions(List<Long> residentIds, String residentName, String propertyType, String usageType, String address, int page, int size) {
        if (residentIds == null || residentIds.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        // 计算偏移量
        int offset = (page - 1) * size;
        return propertyMapper.searchByResidentIdsAndSearchConditions(residentIds, residentName, propertyType, usageType, address, offset, size);
    }

    @Override
    public int countByResidentIdsAndSearchConditions(List<Long> residentIds, String residentName, String propertyType, String usageType, String address) {
        if (residentIds == null || residentIds.isEmpty()) {
            return 0;
        }
        return propertyMapper.countByResidentIdsAndSearchConditions(residentIds, residentName, propertyType, usageType, address);
    }
}