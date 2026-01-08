package com.example.cms.modules.population.mapper;

import com.example.cms.modules.population.entity.Vehicle;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface VehicleMapper {
    
    List<Vehicle> findAll();
    
    Vehicle findById(Long id);
    
    int insert(Vehicle vehicle);
    
    int update(Vehicle vehicle);
    
    int deleteById(Long id);
    
    List<Vehicle> findByResidentId(Long residentId);
    
    int count();
    
    List<Vehicle> searchWithPagination(@Param("residentName") String residentName, @Param("licensePlate") String licensePlate, @Param("vehicleType") String vehicleType, @Param("brand") String brand, @Param("offset") int offset, @Param("limit") int limit);
    
    List<Vehicle> search(@Param("residentName") String residentName, @Param("licensePlate") String licensePlate, @Param("vehicleType") String vehicleType, @Param("brand") String brand);
    
    int countBySearch(@Param("residentName") String residentName, @Param("licensePlate") String licensePlate, @Param("vehicleType") String vehicleType, @Param("brand") String brand);
    
    List<Vehicle> searchByCreatedBy(@Param("residentName") String residentName, @Param("licensePlate") String licensePlate, @Param("vehicleType") String vehicleType, @Param("brand") String brand, @Param("createdBy") Long createdBy, @Param("offset") int offset, @Param("limit") int limit);
    
    int countByCreatedByAndSearch(@Param("residentName") String residentName, @Param("licensePlate") String licensePlate, @Param("vehicleType") String vehicleType, @Param("brand") String brand, @Param("createdBy") Long createdBy);
    
    List<Vehicle> searchByAdminOrCreatedBy(@Param("residentName") String residentName, @Param("licensePlate") String licensePlate, @Param("vehicleType") String vehicleType, @Param("brand") String brand, @Param("createdBy") Long createdBy, @Param("offset") int offset, @Param("limit") int limit);
    
    int countByAdminOrCreatedByAndSearch(@Param("residentName") String residentName, @Param("licensePlate") String licensePlate, @Param("vehicleType") String vehicleType, @Param("brand") String brand, @Param("createdBy") Long createdBy);
    
    /**
     * 获取车辆表中的最大ID
     * @return 最大ID
     */
    Long getMaxId();
    
    /**
     * 重新排序车辆记录ID，确保ID连续
     */
    void reorderIds();
    
    /**
     * 根据居民ID列表和搜索条件分页查询车辆信息
     * @param residentIds 居民ID列表
     * @param residentName 居民姓名
     * @param licensePlate 车牌号
     * @param vehicleType 车辆类型
     * @param brand 品牌
     * @param offset 偏移量
     * @param limit 每页大小
     * @return 车辆信息列表
     */
    List<Vehicle> searchByResidentIdsAndSearchConditions(@Param("residentIds") List<Long> residentIds,
                                                    @Param("residentName") String residentName,
                                                    @Param("licensePlate") String licensePlate,
                                                    @Param("vehicleType") String vehicleType,
                                                    @Param("brand") String brand,
                                                    @Param("offset") int offset,
                                                    @Param("limit") int limit);
    
    /**
     * 根据居民ID列表和搜索条件统计车辆信息数量
     * @param residentIds 居民ID列表
     * @param residentName 居民姓名
     * @param licensePlate 车牌号
     * @param vehicleType 车辆类型
     * @param brand 品牌
     * @return 记录数
     */
    int countByResidentIdsAndSearchConditions(@Param("residentIds") List<Long> residentIds,
                                          @Param("residentName") String residentName,
                                          @Param("licensePlate") String licensePlate,
                                          @Param("vehicleType") String vehicleType,
                                          @Param("brand") String brand);
}