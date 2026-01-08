package com.example.cms.modules.population.controller;

import com.example.cms.common.Result;
import com.example.cms.common.annotation.RequiresRole;
import com.example.cms.modules.population.entity.Property;
import com.example.cms.modules.population.service.PropertyService;
import com.example.cms.modules.system.service.UserService;
import com.example.cms.modules.population.entity.Resident;
import com.example.cms.modules.population.service.ResidentService;
import com.example.cms.framework.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/property")
@Validated
public class PropertyController extends BaseController {
    
    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ResidentService residentService;
    
    /**
     * 查询房产信息列表（支持分页和搜索）
     */
    @GetMapping("/list")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Object> list(@RequestParam(required = false) String residentName,
                              @RequestParam(required = false) String propertyType,
                              @RequestParam(required = false) String usageType,
                              @RequestParam(required = false) String address,
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
            
            List<Property> list;
            int total;
            
            if (isAdmin) {
            // 管理员可以查询所有数据
            list = propertyService.searchWithPagination(residentName, propertyType, usageType, address, page, size);
            total = propertyService.countBySearch(residentName, propertyType, usageType, address);
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
                    
                    // 查询这些居民的符合搜索条件的房产数据
                    list = propertyService.searchByResidentIdsAndSearchConditions(residentIds, residentName, propertyType, usageType, address, page, size);
                    total = propertyService.countByResidentIdsAndSearchConditions(residentIds, residentName, propertyType, usageType, address);
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
            return error("查询房产信息列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取房产记录总数
     */
    @GetMapping("/count")
    @RequiresRole("ADMIN")
    public Result<java.util.Map<String, Object>> count() {
        try {
            long count = propertyService.count();
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("count", count);
            return success(data);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("获取房产记录总数失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID查询房产信息
     */
    @GetMapping("/{id}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Property> getById(@PathVariable Long id) {
        try {
            Property property = propertyService.findById(id);
            if (property == null) {
                return error("未找到ID为 " + id + " 的房产信息");
            }
            
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            // 普通用户只能查看自己关联户籍的数据
            if (!isAdmin && currentUserId != null) {
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                
                // 检查房产记录对应的居民是否属于用户关联的户籍
                Long residentId = property.getResidentId();
                Resident resident = residentService.findById(residentId);
                if (resident != null && resident.getHouseholdId().equals(userHouseholdId)) {
                    // 可以查看该房产信息
                } else {
                    return error("没有权限查看该房产信息");
                }
            }
            
            return success(property);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("查询房产信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据居民ID查询房产信息
     */
    @GetMapping("/resident/{residentId}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<List<Property>> getByResidentId(@PathVariable Long residentId) {
        try {
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            List<Property> list = propertyService.findByResidentId(residentId);
            
            // 如果是普通用户，只返回自己关联户籍的数据
            if (!isAdmin && currentUserId != null) {
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                
                // 检查居民是否属于用户关联的户籍
                Resident resident = residentService.findById(residentId);
                if (resident != null && resident.getHouseholdId().equals(userHouseholdId)) {
                    // 可以查看该居民的房产信息
                } else {
                    return error("没有权限查看该居民的房产信息");
                }
            }
            
            return success(list);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("查询居民房产信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 新增房产信息
     */
    @PostMapping("/add")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Void> add(@RequestBody Property property) {
        try {
            System.out.println("Received property: " + property);
            // 验证房产信息是否完整
            if (!property.hasValidResidentInfo()) {
                System.out.println("Invalid resident info: " + property);
                return error("居民信息不完整，请检查居民姓名和身份证号");
            }
            if (!property.hasValidPropertyInfo()) {
                System.out.println("Invalid property info: " + property);
                return error("房产信息不完整，请检查房产类型、地址、面积、估值和取得日期");
            }
            
            // 设置创建者ID为当前登录用户ID
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            if (currentUserId != null) {
                property.setCreatedBy(currentUserId);
            } else {
                // 如果无法获取当前用户ID，默认设置为1（管理员）
                property.setCreatedBy(1L);
            }
            
            int result = propertyService.insert(property);
            System.out.println("Insert result: " + result);
            return result > 0 ? success(null) : error("房产信息添加失败");
        } catch (IllegalArgumentException e) {
            System.out.println("Illegal argument: " + e.getMessage());
            return error(e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return error("服务器内部错误，请稍后重试");
        }
    }
    
    /**
     * 修改房产信息
     */
    @PutMapping("/update")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Void> update(@RequestBody Property property) {
        try {
            // 验证房产信息是否完整
            if (property.getId() == null) {
                return error("房产ID不能为空");
            }
            if (!property.hasValidResidentInfo()) {
                return error("居民信息不完整，请检查居民姓名和身份证号");
            }
            if (!property.hasValidPropertyInfo()) {
                return error("房产信息不完整，请检查房产类型、地址、面积、估值和取得日期");
            }
            
            // 检查数据所有权
            Property existingProperty = propertyService.findById(property.getId());
            if (existingProperty == null) {
                return error("未找到ID为 " + property.getId() + " 的房产信息");
            }
            
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            // 检查当前用户是否是管理员或数据属于用户关联的户籍
            if (!isAdmin && currentUserId != null) {
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                Long residentId = existingProperty.getResidentId();
                Resident resident = residentService.findById(residentId);
                if (resident == null || !resident.getHouseholdId().equals(userHouseholdId)) {
                    return error("没有权限修改该房产信息");
                }
            }
            
            // 打印调试信息
            System.out.println("Updating property record: " + property);
            
            int result = propertyService.update(property);
            System.out.println("Update result: " + result);
            return result > 0 ? success(null) : error("房产信息修改失败");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("修改房产信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 搜索房产信息
     */
    @GetMapping("/search")
    @RequiresRole("ADMIN")
    public Result<List<Property>> search(@RequestParam(required = false) String residentName,
                                       @RequestParam(required = false) String propertyType,
                                       @RequestParam(required = false) String usageType,
                                       @RequestParam(required = false) String address) {
        try {
            List<Property> list = propertyService.search(residentName, propertyType, usageType, address);
            return success(list);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("搜索房产信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除房产信息
     */
    @DeleteMapping("/delete/{id}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Void> delete(@PathVariable Long id) {
        // 检查数据所有权
        Property existingProperty = propertyService.findById(id);
        if (existingProperty == null) {
            return error("未找到ID为 " + id + " 的房产信息");
        }
        
        // 获取当前用户ID和角色
        Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
        boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
        
        // 检查当前用户是否是管理员或数据属于用户关联的户籍
        if (!isAdmin && currentUserId != null) {
            Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
            Long residentId = existingProperty.getResidentId();
            Resident resident = residentService.findById(residentId);
            if (resident == null || !resident.getHouseholdId().equals(userHouseholdId)) {
                return error("没有权限删除该房产信息");
            }
        }
        
        int result = propertyService.deleteById(id);
        return result > 0 ? success(null) : error("操作失败");
    }
    
    
}