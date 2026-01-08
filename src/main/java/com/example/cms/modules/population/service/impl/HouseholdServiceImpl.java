package com.example.cms.modules.population.service.impl;

import com.example.cms.common.util.UserContext;
import com.example.cms.modules.population.entity.Household;
import com.example.cms.modules.population.entity.Resident;
import com.example.cms.modules.population.mapper.HouseholdMapper;
import com.example.cms.modules.population.service.HouseholdService;
import com.example.cms.modules.population.service.ResidentService;
import com.example.cms.websocket.DataChangeEvent;
import com.example.cms.websocket.WebSocketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class HouseholdServiceImpl implements HouseholdService {
    
    @Autowired
    private HouseholdMapper householdMapper;

    @Autowired
    private WebSocketMessageService webSocketMessageService;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private org.springframework.cache.CacheManager cacheManager;
    
    @Override
    public List<Household> findAll() {
        return householdMapper.findAll();
    }
    
    @Override
    public Household findById(Long id) {
        return householdMapper.findById(id);
    }
    
    @Override
    public Household findByHouseholdNumber(String householdNumber) {
        return householdMapper.findByHouseholdNumber(householdNumber);
    }
    
    @Override
    public int insert(Household household) {
        // 数据预处理和验证
        if (household == null) {
            throw new IllegalArgumentException("户籍信息不能为空");
        }
        
        // 处理人口数量默认值
        if (household.getPopulationCount() == null || household.getPopulationCount() <= 0) {
            household.setPopulationCount(1);
        }
        
        // 确保时间字段不为空
        LocalDateTime currentTime = LocalDateTime.now();
        
        if (household.getCreateTime() == null) {
            household.setCreateTime(currentTime);
        }
        household.setUpdateTime(currentTime);
        
        // Validate registration date
        if (household.getRegistrationDate() == null || household.getRegistrationDate().isEmpty()) {
            // Set to current date if not provided
            household.setRegistrationDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else {
            // 格式化日期，去除多余空格
            String registrationDate = household.getRegistrationDate().trim();
            // 去除日期中的空格
            registrationDate = registrationDate.replaceAll("\\s+", " ");
            household.setRegistrationDate(registrationDate);
        }
        
        // 设置创建者ID
        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId != null) {
            household.setCreatedBy(currentUserId);
        } else {
            // 如果无法获取当前用户ID，使用默认值
            household.setCreatedBy(1L);
        }

        // 生成连续的户籍编号和ID，按创建者分组
        String nextHouseholdNumber = householdMapper.getNextHouseholdNumber(household.getCreatedBy());
        household.setHouseholdNumber(nextHouseholdNumber);
        household.setId(Long.parseLong(nextHouseholdNumber));

        int result = householdMapper.insert(household);

        // 清除统计缓存，确保统计数据实时更新
        if (result > 0 && cacheManager.getCache("statistics") != null) {
            cacheManager.getCache("statistics").clear();
        }

        // 广播WebSocket通知
        if (result > 0) {
            DataChangeEvent event = new DataChangeEvent(
                "household",
                "INSERT",
                household.getId(),
                household.getCreatedBy(),
                household,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }
    
    @Override
    public int update(Household household) {
        // 验证户籍信息是否存在
        Household existingHousehold = householdMapper.findById(household.getId());
        if (existingHousehold == null) {
            throw new RuntimeException("未找到ID为 " + household.getId() + " 的户籍信息");
        }
        
        // 确保householdNumber字段不为null
        if (household.getHouseholdNumber() == null || household.getHouseholdNumber().trim().isEmpty()) {
            household.setHouseholdNumber(existingHousehold.getHouseholdNumber());
        }
        
        household.setUpdateTime(LocalDateTime.now());
        int result = householdMapper.update(household);

        // 清除统计缓存，确保统计数据实时更新
        if (result > 0 && cacheManager.getCache("statistics") != null) {
            cacheManager.getCache("statistics").clear();
        }

        // 广播WebSocket通知
        if (result > 0) {
            DataChangeEvent event = new DataChangeEvent(
                "household",
                "UPDATE",
                household.getId(),
                household.getCreatedBy() != null ? household.getCreatedBy() : existingHousehold.getCreatedBy(),
                household,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }
    
    @Override
    public int deleteById(Long id) {
        // 获取户籍信息（用于WebSocket通知）
        Household household = householdMapper.findById(id);

        // 前置校验：检查户籍下是否有关联的居民信息
        List<Resident> residents = residentService.findByHouseholdId(id);
        if (residents != null && !residents.isEmpty()) {
            throw new IllegalStateException("无法删除户籍，该户籍下存在关联的居民信息。请先删除或转移相关居民数据。");
        }

        int result = householdMapper.deleteById(id);
        // 删除成功后重新排序户籍ID，全局唯一
        if (result > 0 && household != null) {
            householdMapper.reorderIds();
            // 清除统计缓存，确保统计数据实时更新
            if (cacheManager.getCache("statistics") != null) {
                cacheManager.getCache("statistics").clear();
            }

            // 广播WebSocket通知
            DataChangeEvent event = new DataChangeEvent(
                "household",
                "DELETE",
                id,
                household.getCreatedBy(),
                null,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }
        return result;
    }
    
    @Override
    public List<Household> findByHouseholderName(String name) {
        return householdMapper.findByHouseholderName(name);
    }
    
    @Override
    public int count() {
        return householdMapper.count();
    }
    
    @Override
    public List<Household> findByPage(int page, int size) {
        int offset = (page - 1) * size;
        return householdMapper.findByPage(offset, size);
    }

    @Override
    public List<Map<String, Object>> countPopulationByRegion() {
        return householdMapper.countPopulationByRegion();
    }
    
    @Override
    public List<Household> findByCreatedBy(Long createdBy, int page, int size) {
        // 参数校验
        if (page < 1) {
            page = 1;
        }
        if (size < 1 || size > 100) {
            size = 10;
        }
        
        // 计算偏移量
        int offset = (page - 1) * size;
        return householdMapper.findByCreatedBy(createdBy, offset, size);
    }
    
    @Override
    public int countByCreatedBy(Long createdBy) {
        return householdMapper.countByCreatedBy(createdBy);
    }
    
    @Override
    public List<Household> findByAdminOrCreatedBy(Long createdBy, int page, int size) {
        // 参数校验
        if (page < 1) {
            page = 1;
        }
        if (size < 1 || size > 100) {
            size = 10;
        }
        
        // 计算偏移量
        int offset = (page - 1) * size;
        return householdMapper.findByAdminOrCreatedBy(createdBy, offset, size);
    }
    
    @Override
    public int countByAdminOrCreatedBy(Long createdBy) {
        return householdMapper.countByAdminOrCreatedBy(createdBy);
    }
}