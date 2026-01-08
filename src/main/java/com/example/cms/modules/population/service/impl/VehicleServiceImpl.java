package com.example.cms.modules.population.service.impl;

import com.example.cms.modules.population.entity.Resident;
import com.example.cms.modules.population.entity.Vehicle;
import com.example.cms.modules.population.mapper.VehicleMapper;
import com.example.cms.modules.population.service.ResidentService;
import com.example.cms.modules.population.service.VehicleService;
import com.example.cms.websocket.DataChangeEvent;
import com.example.cms.websocket.WebSocketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleMapper vehicleMapper;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private WebSocketMessageService webSocketMessageService;

    @Autowired
    private org.springframework.cache.CacheManager cacheManager;
    
    @Override
    public List<Vehicle> findAll() {
        return vehicleMapper.findAll();
    }
    
    @Override
    public Vehicle findById(Long id) {
        return vehicleMapper.findById(id);
    }
    
    @Override
    public List<Vehicle> findByResidentId(Long residentId) {
        return vehicleMapper.findByResidentId(residentId);
    }
    
    @Override
    public int insert(Vehicle vehicle) {
        LocalDateTime currentTime = LocalDateTime.now();
        
        if (vehicle.getCreateTime() == null) {
            vehicle.setCreateTime(currentTime);
        }
        vehicle.setUpdateTime(currentTime);
        
        // 确保createdBy字段有值，默认使用admin用户的id（1）
        if (vehicle.getCreatedBy() == null) {
            vehicle.setCreatedBy(1L);
        }
        
        // 根据身份证号查询居民ID，确保必须关联到一个有效的居民
        if (vehicle.getResidentId() == null && vehicle.getIdCard() != null) {
            List<Resident> residents = residentService.findByIdCard(vehicle.getIdCard());
            if (!residents.isEmpty()) {
                vehicle.setResidentId(residents.get(0).getId());
            } else {
                // 如果根据身份证号查询不到居民，抛出异常
                throw new IllegalArgumentException("未找到身份证号为 " + vehicle.getIdCard() + " 的居民信息");
            }
        }
        
        // 确保vehicle对象必须关联到一个有效的居民ID
        if (vehicle.getResidentId() == null) {
            throw new IllegalArgumentException("车辆信息必须关联到一个有效的居民");
        }
        
        // 手动设置连续ID，全局唯一
        Long maxId = vehicleMapper.getMaxId();
        // 如果maxId为null（表中没有数据），则从1开始
        vehicle.setId(maxId == null ? 1L : maxId + 1);
        
        int result = vehicleMapper.insert(vehicle);

        // 清除统计缓存，确保统计数据实时更新
        if (result > 0 && cacheManager.getCache("statistics") != null) {
            cacheManager.getCache("statistics").clear();
        }

        // 广播WebSocket通知
        if (result > 0) {
            DataChangeEvent event = new DataChangeEvent(
                "vehicle",
                "INSERT",
                vehicle.getId(),
                vehicle.getCreatedBy(),
                vehicle,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }
    
    @Override
    public int update(Vehicle vehicle) {
        vehicle.setUpdateTime(LocalDateTime.now());

        int result = vehicleMapper.update(vehicle);

        // 清除统计缓存，确保统计数据实时更新
        if (result > 0 && cacheManager.getCache("statistics") != null) {
            cacheManager.getCache("statistics").clear();
        }

        // 广播WebSocket通知
        if (result > 0) {
            DataChangeEvent event = new DataChangeEvent(
                "vehicle",
                "UPDATE",
                vehicle.getId(),
                vehicle.getCreatedBy(),
                vehicle,
                System.currentTimeMillis()
            );
            webSocketMessageService.broadcastDataChange(event);
        }

        return result;
    }

    @Override
    public int deleteById(Long id) {
        Vehicle vehicle = vehicleMapper.findById(id);
        int result = vehicleMapper.deleteById(id);

        // 删除成功后重新排序ID，全局唯一
        if (result > 0) {
            vehicleMapper.reorderIds();
            // 清除统计缓存，确保统计数据实时更新
            if (cacheManager.getCache("statistics") != null) {
                cacheManager.getCache("statistics").clear();
            }

            // 广播WebSocket通知
            if (vehicle != null) {
                DataChangeEvent event = new DataChangeEvent(
                    "vehicle",
                    "DELETE",
                    id,
                    vehicle.getCreatedBy(),
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
        return vehicleMapper.count();
    }
    
    @Override
    public List<Vehicle> searchWithPagination(String residentName, String licensePlate, String vehicleType, String brand, int page, int size) {
        int offset = (page - 1) * size;
        return vehicleMapper.searchWithPagination(residentName, licensePlate, vehicleType, brand, offset, size);
    }
    
    @Override
    public List<Vehicle> search(String residentName, String licensePlate, String vehicleType, String brand) {
        return vehicleMapper.search(residentName, licensePlate, vehicleType, brand);
    }
    
    @Override
    public int countBySearch(String residentName, String licensePlate, String vehicleType, String brand) {
        return vehicleMapper.countBySearch(residentName, licensePlate, vehicleType, brand);
    }
    
    @Override
    public List<Vehicle> searchByCreatedBy(String residentName, String licensePlate, String vehicleType, String brand, Long createdBy, int page, int size) {
        int offset = (page - 1) * size;
        return vehicleMapper.searchByCreatedBy(residentName, licensePlate, vehicleType, brand, createdBy, offset, size);
    }
    
    @Override
    public int countByCreatedByAndSearch(String residentName, String licensePlate, String vehicleType, String brand, Long createdBy) {
        return vehicleMapper.countByCreatedByAndSearch(residentName, licensePlate, vehicleType, brand, createdBy);
    }
    
    @Override
    public List<Vehicle> searchByAdminOrCreatedBy(String residentName, String licensePlate, String vehicleType, String brand, Long createdBy, int page, int size) {
        int offset = (page - 1) * size;
        return vehicleMapper.searchByAdminOrCreatedBy(residentName, licensePlate, vehicleType, brand, createdBy, offset, size);
    }
    
    @Override
    public int countByAdminOrCreatedByAndSearch(String residentName, String licensePlate, String vehicleType, String brand, Long createdBy) {
        return vehicleMapper.countByAdminOrCreatedByAndSearch(residentName, licensePlate, vehicleType, brand, createdBy);
    }

    @Override
    public List<Vehicle> searchByResidentIdsAndSearchConditions(List<Long> residentIds, String residentName, String licensePlate, String vehicleType, String brand, int page, int size) {
        if (residentIds == null || residentIds.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        // 计算偏移量
        int offset = (page - 1) * size;
        return vehicleMapper.searchByResidentIdsAndSearchConditions(residentIds, residentName, licensePlate, vehicleType, brand, offset, size);
    }

    @Override
    public int countByResidentIdsAndSearchConditions(List<Long> residentIds, String residentName, String licensePlate, String vehicleType, String brand) {
        if (residentIds == null || residentIds.isEmpty()) {
            return 0;
        }
        return vehicleMapper.countByResidentIdsAndSearchConditions(residentIds, residentName, licensePlate, vehicleType, brand);
    }
}