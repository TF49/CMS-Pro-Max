package com.example.cms.modules.population.service;

import com.example.cms.modules.population.entity.Vehicle;
import java.util.List;

public interface VehicleService {
    
    List<Vehicle> findAll();
    
    Vehicle findById(Long id);
    
    List<Vehicle> findByResidentId(Long residentId);
    
    int insert(Vehicle vehicle);
    
    int update(Vehicle vehicle);
    
    int deleteById(Long id);
    
    int count();
    
    List<Vehicle> searchWithPagination(String residentName, String licensePlate, String vehicleType, String brand, int page, int size);
    
    List<Vehicle> search(String residentName, String licensePlate, String vehicleType, String brand);
    
    int countBySearch(String residentName, String licensePlate, String vehicleType, String brand);
    
    List<Vehicle> searchByCreatedBy(String residentName, String licensePlate, String vehicleType, String brand, Long createdBy, int page, int size);
    
    int countByCreatedByAndSearch(String residentName, String licensePlate, String vehicleType, String brand, Long createdBy);
    
    List<Vehicle> searchByAdminOrCreatedBy(String residentName, String licensePlate, String vehicleType, String brand, Long createdBy, int page, int size);
    
    int countByAdminOrCreatedByAndSearch(String residentName, String licensePlate, String vehicleType, String brand, Long createdBy);
    
    /**
     * 根据居民ID列表和搜索条件分页查询车辆信息
     * @param residentIds 居民ID列表
     * @param residentName 居民姓名
     * @param licensePlate 车牌号
     * @param vehicleType 车辆类型
     * @param brand 品牌
     * @param page 页码
     * @param size 每页大小
     * @return 车辆信息列表
     */
    List<Vehicle> searchByResidentIdsAndSearchConditions(List<Long> residentIds, String residentName, String licensePlate, String vehicleType, String brand, int page, int size);
    
    /**
     * 根据居民ID列表和搜索条件统计车辆信息数量
     * @param residentIds 居民ID列表
     * @param residentName 居民姓名
     * @param licensePlate 车牌号
     * @param vehicleType 车辆类型
     * @param brand 品牌
     * @return 记录数
     */
    int countByResidentIdsAndSearchConditions(List<Long> residentIds, String residentName, String licensePlate, String vehicleType, String brand);
}