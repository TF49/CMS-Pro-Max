package com.example.cms.modules.population.service.impl;

import com.example.cms.common.util.UserContext;
import com.example.cms.modules.population.entity.Resident;
import com.example.cms.modules.population.mapper.ResidentMapper;
import com.example.cms.modules.population.service.ResidentService;
import com.example.cms.modules.population.service.EducationService;
import com.example.cms.modules.population.service.MedicalService;
import com.example.cms.modules.population.service.EmploymentService;
import com.example.cms.modules.population.service.SocialSecurityService;
import com.example.cms.modules.population.service.PropertyService;
import com.example.cms.modules.population.service.VehicleService;
import com.example.cms.websocket.DataChangeEvent;
import com.example.cms.websocket.WebSocketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResidentServiceImpl implements ResidentService {
    
    @Autowired
    private ResidentMapper residentMapper;

    @Autowired
    private WebSocketMessageService webSocketMessageService;

    @Autowired
    private org.springframework.cache.CacheManager cacheManager;
    
    @Autowired
    private MedicalService medicalService;
    
    @Autowired
    private EmploymentService employmentService;

    @Autowired
    private EducationService educationService;
    
    @Autowired
    private SocialSecurityService socialSecurityService;
    
    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private VehicleService vehicleService;
    
    @Override
    public List<Resident> findAll() {
        return residentMapper.findAll();
    }
    
    @Override
    public Resident findById(Long id) {
        return residentMapper.findById(id);
    }
    
    @Override
    public int insert(Resident resident) {
        // 确保household_id不为空，如果为空则设置默认值0
        if (resident.getHouseholdId() == null) {
            resident.setHouseholdId(0L);
        }
        
        // 设置创建者ID
        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId != null) {
            resident.setCreatedBy(currentUserId);
        }
        
        // 手动设置连续id，全局唯一
        Long maxId = residentMapper.getMaxId();
        resident.setId(maxId + 1);
        
        int result = residentMapper.insert(resident);

        // 清除统计缓存，确保统计数据实时更新
        if (result > 0 && cacheManager.getCache("statistics") != null) {
            cacheManager.getCache("statistics").clear();
        }

        // 广播WebSocket通知
        if (result > 0) {
            DataChangeEvent event = new DataChangeEvent(
                "resident",
                "INSERT",
                resident.getId(),
                resident.getCreatedBy(),
                resident,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }
    
    @Override
    public int update(Resident resident) {
        int result = residentMapper.update(resident);

        // 清除统计缓存，确保统计数据实时更新
        if (result > 0 && cacheManager.getCache("statistics") != null) {
            cacheManager.getCache("statistics").clear();
        }

        // 广播WebSocket通知
        if (result > 0) {
            DataChangeEvent event = new DataChangeEvent(
                "resident",
                "UPDATE",
                resident.getId(),
                resident.getCreatedBy(),
                resident,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }
    
    @Override
    public int deleteById(Long id) {
        // 检查居民是否存在
        Resident resident = residentMapper.findById(id);
        if (resident == null) {
            throw new RuntimeException("居民信息不存在，ID: " + id);
        }
        
        // 检查是否存在关联的教育记录
        List<?> educationRecords = educationService.findByResidentId(id);
        if (educationRecords != null && !educationRecords.isEmpty()) {
            throw new RuntimeException("该居民存在关联的教育记录，无法删除。请先删除相关的教育信息。");
        }
        
        // 检查是否存在关联的医疗记录
        List<?> medicalRecords = medicalService.findByResidentId(id);
        if (medicalRecords != null && !medicalRecords.isEmpty()) {
            throw new RuntimeException("该居民存在关联的医疗记录，无法删除。请先删除相关的医疗信息。");
        }
        
        // 检查是否存在关联的就业记录
        List<?> employmentRecords = employmentService.findByResidentId(id);
        if (employmentRecords != null && !employmentRecords.isEmpty()) {
            throw new RuntimeException("该居民存在关联的就业记录，无法删除。请先删除相关的就业信息。");
        }
        
        // 检查是否存在关联的社保记录
        List<?> socialSecurityRecords = socialSecurityService.findByResidentId(id);
        if (socialSecurityRecords != null && !socialSecurityRecords.isEmpty()) {
            throw new RuntimeException("该居民存在关联的社保记录，无法删除。请先删除相关的社保信息。");
        }
        
        // 检查是否存在关联的房产记录
        List<?> propertyRecords = propertyService.findByResidentId(id);
        if (propertyRecords != null && !propertyRecords.isEmpty()) {
            throw new RuntimeException("该居民存在关联的房产记录，无法删除。请先删除相关的房产信息。");
        }
        
        // 检查是否存在关联的车辆记录
        List<?> vehicleRecords = vehicleService.findByResidentId(id);
        if (vehicleRecords != null && !vehicleRecords.isEmpty()) {
            throw new RuntimeException("该居民存在关联的车辆记录，无法删除。请先删除相关的车辆信息。");
        }
        
        // 执行删除操作
        int result = residentMapper.deleteById(id);

        // 调用重新排序方法，确保id连续
        if (result > 0) {
            residentMapper.reorderIds();
            // 清除统计缓存，确保统计数据实时更新
            if (cacheManager.getCache("statistics") != null) {
                cacheManager.getCache("statistics").clear();
            }

            // 广播WebSocket通知
            DataChangeEvent event = new DataChangeEvent(
                "resident",
                "DELETE",
                id,
                resident.getCreatedBy(),
                null,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }

    @Override
    public List<Resident> findByName(String name) {
        return residentMapper.findByName(name);
    }
    
    @Override
    public List<Resident> findByIdCard(String idCard) {
        return residentMapper.findByIdCard(idCard);
    }
    
    @Override
    public int count() {
        return residentMapper.count();
    }

    @Override
    public List<Resident> findAllWithHouseholdInfo() {
        return residentMapper.findAllWithHouseholdInfo();
    }

    @Override
    public Resident findByIdWithHouseholdInfo(Long id) {
        return residentMapper.findByIdWithHouseholdInfo(id);
    }

    @Override
    public List<Resident> searchWithHouseholdInfo(String name, String idCard, int page, int size) {
        // 参数校验
        if (page < 1) {
            page = 1;
        }
        if (size < 1 || size > 100) {
            size = 10;
        }
        
        // 计算偏移量
        int offset = (page - 1) * size;
        return residentMapper.searchWithHouseholdInfo(name, idCard, offset, size);
    }

    @Override
    public int countBySearch(String name, String idCard) {
        return residentMapper.countBySearch(name, idCard);
    }

    @Override
    public List<Resident> findByPageWithHouseholdInfo(int page, int size) {
        // 参数校验
        if (page < 1) {
            page = 1;
        }
        if (size < 1 || size > 100) {
            size = 10;
        }
        
        // 计算偏移量
        int offset = (page - 1) * size;
        return residentMapper.findByPageWithHouseholdInfo(offset, size);
    }
    
    @Override
    public List<Resident> findByHouseholdId(Long householdId) {
        return residentMapper.findByHouseholdId(householdId);
    }
    
    @Override
    public List<Resident> findByHouseholdIdWithHouseholdInfo(Long householdId, int page, int size) {
        // 参数校验
        if (page < 1) {
            page = 1;
        }
        if (size < 1 || size > 100) {
            size = 10;
        }
        
        // 计算偏移量
        int offset = (page - 1) * size;
        return residentMapper.findByHouseholdIdWithHouseholdInfo(householdId, offset, size);
    }
    
    @Override
    public int countByHouseholdId(Long householdId) {
        return residentMapper.countByHouseholdId(householdId);
    }
    
    @Override
    public List<Resident> findByCreatedBy(Long createdBy, int page, int size) {
        // 参数校验
        if (page < 1) {
            page = 1;
        }
        if (size < 1 || size > 100) {
            size = 10;
        }
        
        // 计算偏移量
        int offset = (page - 1) * size;
        return residentMapper.findByCreatedBy(createdBy, offset, size);
    }
    
    @Override
    public int countByCreatedBy(Long createdBy) {
        return residentMapper.countByCreatedBy(createdBy);
    }

    @Override
    public List<Resident> findByAdminOrCreatedBy(Long createdBy, int page, int size) {
        // 参数校验
        if (page < 1) {
            page = 1;
        }
        if (size < 1 || size > 100) {
            size = 10;
        }
        
        // 计算偏移量
        int offset = (page - 1) * size;
        return residentMapper.findByAdminOrCreatedBy(createdBy, offset, size);
    }
    
    @Override
    public int countByAdminOrCreatedBy(Long createdBy) {
        return residentMapper.countByAdminOrCreatedBy(createdBy);
    }
}