package com.example.cms.modules.population.controller;

import com.example.cms.common.Result;
import com.example.cms.common.annotation.RequiresRole;
import com.example.cms.modules.population.entity.Vehicle;
import com.example.cms.modules.population.service.VehicleService;
import com.example.cms.modules.system.service.UserService;
import com.example.cms.modules.population.entity.Resident;
import com.example.cms.modules.population.service.ResidentService;
import com.example.cms.framework.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicle")
@Validated
public class VehicleController extends BaseController {
    
    @Autowired
    private VehicleService vehicleService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ResidentService residentService;
    
    /**
     * 查询车辆信息列表（支持分页和搜索）
     */
    @GetMapping("/list")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Object> list(@RequestParam(required = false) String residentName,
                              @RequestParam(required = false) String licensePlate,
                              @RequestParam(required = false) String vehicleType,
                              @RequestParam(required = false) String brand,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size) {
        try {
            // 参数验证
            if (page < 1) {
                page = 1;
            }
            if (size < 1 || size > 100) {
                size = 10;
            }
            
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            java.util.Set<String> userRoles = com.example.cms.common.util.UserContext.getCurrentUserRoles();
            
            System.out.println("[VehicleController.list] currentUserId: " + currentUserId + ", isAdmin: " + isAdmin + ", userRoles: " + userRoles);
            
            List<Vehicle> list;
            int total;
            
            if (isAdmin) {
            // 管理员可以查询所有数据
            list = vehicleService.searchWithPagination(residentName, licensePlate, vehicleType, brand, page, size);
            total = vehicleService.countBySearch(residentName, licensePlate, vehicleType, brand);
        } else {
            // 普通用户查看所有关联到自己户籍的居民的数据
            Long householdId = userService.getUserHouseholdId(currentUserId);
            if (householdId != null) {
                // 查询该户籍下的所有居民
                List<Resident> residents = residentService.findByHouseholdId(householdId);
                if (!residents.isEmpty()) {
                    // 获取居民ID列表
                    List<Long> residentIds = residents.stream()
                        .map(Resident::getId)
                        .collect(java.util.stream.Collectors.toList());
                    
                    // 查询这些居民的符合搜索条件的车辆数据
                    list = vehicleService.searchByResidentIdsAndSearchConditions(residentIds, residentName, licensePlate, vehicleType, brand, page, size);
                    total = vehicleService.countByResidentIdsAndSearchConditions(residentIds, residentName, licensePlate, vehicleType, brand);
                } else {
                    list = new java.util.ArrayList<>();
                    total = 0;
                }
            } else {
                list = new java.util.ArrayList<>();
                total = 0;
            }
        }            
            if (list == null) {
                list = new java.util.ArrayList<>();
            }
            
            // 构造分页响应
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("data", list);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            result.put("pages", (total + size - 1) / size);
            
            return success(result);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("查询车辆信息列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询所有车辆信息（兼容旧接口）
     */
    @GetMapping("/all")
    @RequiresRole("ADMIN")
    public Result<List<Vehicle>> getAll() {
        try {
            List<Vehicle> list = vehicleService.findAll();
            return success(list);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("查询所有车辆信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取车辆记录总数
     */
    @GetMapping("/count")
    @RequiresRole("ADMIN")
    public Result<java.util.Map<String, Object>> count() {
        try {
            long count = vehicleService.count();
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("count", count);
            return success(data);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("获取车辆记录总数失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID查询车辆信息
     */
    @GetMapping("/{id}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Vehicle> getById(@PathVariable Long id) {
        try {
            Vehicle vehicle = vehicleService.findById(id);
            if (vehicle == null) {
                return error("未找到ID为 " + id + " 的车辆信息");
            }
            
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            // 普通用户只能查看自己关联户籍的数据
            if (!isAdmin && currentUserId != null) {
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                
                // 检查车辆记录对应的居民是否属于用户关联的户籍
                Long residentId = vehicle.getResidentId();
                Resident resident = residentService.findById(residentId);
                if (resident != null && resident.getHouseholdId().equals(userHouseholdId)) {
                    // 可以查看该车辆信息
                } else {
                    return error("没有权限查看该车辆信息");
                }
            }
            
            return success(vehicle);
        } catch (Exception e) {
            return error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据居民ID查询车辆信息
     */
    @GetMapping("/resident/{residentId}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<List<Vehicle>> getByResidentId(@PathVariable Long residentId) {
        try {
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            List<Vehicle> list = vehicleService.findByResidentId(residentId);
            
            // 普通用户只能查看自己关联户籍的数据
            if (!isAdmin && currentUserId != null) {
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                
                // 检查居民是否属于用户关联的户籍
                Resident resident = residentService.findById(residentId);
                if (resident != null && resident.getHouseholdId().equals(userHouseholdId)) {
                    // 可以查看该居民的车辆信息
                } else {
                    return error("没有权限查看该居民的车辆信息");
                }
            }
            
            return success(list);
        } catch (Exception e) {
            return error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 新增车辆信息
     */
    @PostMapping("/add")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Void> add(@RequestBody Vehicle vehicle) {
        try {
            // 验证车辆信息是否完整
            if (!vehicle.hasValidResidentInfo()) {
                return error("居民信息不完整，请检查居民姓名和身份证号");
            }
            if (!vehicle.hasValidVehicleInfo()) {
                return error("车辆信息不完整，请检查车牌号、车辆类型、品牌、型号和购置日期");
            }
            
            // 设置创建者ID为当前登录用户ID
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            if (currentUserId != null) {
                vehicle.setCreatedBy(currentUserId);
            } else {
                // 如果无法获取当前用户ID，默认设置为1（管理员）
                vehicle.setCreatedBy(1L);
            }
            
            System.out.println("Received vehicle data: " + vehicle.toString());
            int result = vehicleService.insert(vehicle);
            System.out.println("Insert result: " + result);
            return result > 0 ? success(null) : error("车辆信息添加失败");
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " + e.getMessage());
            return error(e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return error("服务器内部错误，请稍后重试");
        }
    }
    
    /**
     * 修改车辆信息
     */
    @PutMapping("/update")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Void> update(@RequestBody Vehicle vehicle) {
        try {
            // 验证车辆信息是否完整
            if (vehicle.getId() == null) {
                return error("车辆ID不能为空");
            }
            if (!vehicle.hasValidResidentInfo()) {
                return error("居民信息不完整，请检查居民姓名和身份证号");
            }
            if (!vehicle.hasValidVehicleInfo()) {
                return error("车辆信息不完整，请检查车牌号、车辆类型、品牌、型号和购置日期");
            }
            
            // 检查数据所有权
            Vehicle existingVehicle = vehicleService.findById(vehicle.getId());
            if (existingVehicle == null) {
                return error("未找到ID为 " + vehicle.getId() + " 的车辆信息");
            }
            
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            // 检查当前用户是否是数据创建者或管理员
            if (currentUserId != null && !currentUserId.equals(existingVehicle.getCreatedBy()) && !isAdmin) {
                return error("没有权限修改该车辆信息");
            }
            
            // 打印调试信息
            System.out.println("Updating vehicle record: " + vehicle);
            
            int result = vehicleService.update(vehicle);
            System.out.println("Update result: " + result);
            return result > 0 ? success(null) : error("车辆信息修改失败");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("修改车辆信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除车辆信息
     */
    @DeleteMapping("/delete/{id}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Void> delete(@PathVariable Long id) {
        try {
            // 检查数据所有权
            Vehicle existingVehicle = vehicleService.findById(id);
            if (existingVehicle == null) {
                return error("未找到ID为 " + id + " 的车辆信息");
            }
            
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            // 检查当前用户是否是数据创建者或管理员
            if (currentUserId != null && !currentUserId.equals(existingVehicle.getCreatedBy()) && !isAdmin) {
                return error("没有权限删除该车辆信息");
            }
            
            int result = vehicleService.deleteById(id);
            return result > 0 ? success(null) : error("操作失败");
        } catch (Exception e) {
            return error("删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 搜索车辆信息
     */
    @GetMapping("/search")
    @RequiresRole("ADMIN")
    public Result<List<Vehicle>> search(@RequestParam(required = false) String residentName,
                                       @RequestParam(required = false) String licensePlate,
                                       @RequestParam(required = false) String vehicleType,
                                       @RequestParam(required = false) String brand) {
        try {
            List<Vehicle> list = vehicleService.search(residentName, licensePlate, vehicleType, brand);
            return success(list);
        } catch (Exception e) {
            return error("搜索失败：" + e.getMessage());
        }
    }
}