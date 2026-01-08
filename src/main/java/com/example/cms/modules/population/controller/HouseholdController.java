package com.example.cms.modules.population.controller;

import com.example.cms.common.Result;
import com.example.cms.common.annotation.RequiresRole;
import com.example.cms.modules.population.entity.Household;
import com.example.cms.modules.population.service.HouseholdService;
import com.example.cms.framework.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 户籍管理Controller
 * 继承BaseController，提供标准化的响应结果
 */
@RestController
@RequestMapping("/household")
public class HouseholdController extends BaseController {
    
    @Autowired
    private HouseholdService householdService;
    
    @Autowired
    private com.example.cms.modules.system.service.UserService userService;
    
    /**
     * 分页查询户籍数据
     */
    @GetMapping("/list")
    @RequiresRole({"ADMIN", "USER"})
    public Result getHouseholdData(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        List<Household> householdList;
        int total;
        
        // 获取当前用户ID和角色
        Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
        boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
        
        if (isAdmin) {
            // 管理员查看所有数据
            householdList = householdService.findByPage(page, size);
            total = householdService.count();
        } else {
            // 普通用户只能查看自己关联的户籍
            Long householdId = userService.getUserHouseholdId(currentUserId);
            if (householdId != null) {
                // 查询自己关联的户籍
                Household household = householdService.findById(householdId);
                householdList = household != null ? java.util.Arrays.asList(household) : new java.util.ArrayList<>();
                total = household != null ? 1 : 0;
            } else {
                // 没有关联户籍，返回空列表
                householdList = new java.util.ArrayList<>();
                total = 0;
            }
        }
        
        if (householdList == null) {
            householdList = new java.util.ArrayList<>();
        }
        
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("data", householdList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        
        return success(result);
    }
    
    /**
     * 根据ID查询户籍信息
     */
    @GetMapping("/{id}")
    @RequiresRole({"ADMIN", "USER"})
    public Result getById(@PathVariable Long id) {
        Household household = householdService.findById(id);
        if (household == null) {
            return error("未找到ID为 " + id + " 的户籍信息");
        }
        
        // 获取当前用户ID和角色
        Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
        boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
        
        // 普通用户只能查看自己关联的户籍
        if (!isAdmin && currentUserId != null) {
            Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
            if (userHouseholdId == null || !userHouseholdId.equals(household.getId())) {
                return error("没有权限查看该户籍信息");
            }
        }
        
        return success(household);
    }
    
    /**
     * 根据户籍编号查询户籍信息
     */
    @GetMapping("/number/{householdNumber}")
    @RequiresRole({"ADMIN", "USER"})
    public Result getByNumber(@PathVariable String householdNumber) {
        Household household = householdService.findByHouseholdNumber(householdNumber);
        if (household == null) {
            return error("未找到户籍编号为 " + householdNumber + " 的户籍信息");
        }
        
        // 获取当前用户ID和角色
        Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
        boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
        
        // 普通用户只能查看自己关联的户籍
        if (!isAdmin && currentUserId != null) {
            Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
            if (userHouseholdId == null || !userHouseholdId.equals(household.getId())) {
                return error("没有权限查看该户籍信息");
            }
        }
        
        return success(household);
    }
    
    /**
     * 搜索户籍数据
     */
    @GetMapping("/search")
    @RequiresRole({"ADMIN", "USER"})
    public Result searchHouseholds(@RequestParam(required = false) String householdNumber,
                                   @RequestParam(required = false) String householderName) {
        List<Household> householdList;
        
        // 获取当前用户ID和角色
        Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
        boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
        
        if (isAdmin) {
            // 管理员可以搜索所有数据
            if (householdNumber != null && !householdNumber.trim().isEmpty()) {
                Household household = householdService.findByHouseholdNumber(householdNumber.trim());
                householdList = household != null ? java.util.Arrays.asList(household) : new java.util.ArrayList<>();
            } else if (householderName != null && !householderName.trim().isEmpty()) {
                householdList = householdService.findByHouseholderName(householderName.trim());
            } else {
                householdList = householdService.findAll();
            }
        } else {
            // 普通用户可以查看管理员和自己创建的数据
            householdList = householdService.findByAdminOrCreatedBy(currentUserId, 1, Integer.MAX_VALUE);
            // 对结果进行过滤
            if (householdNumber != null && !householdNumber.trim().isEmpty()) {
                householdList = householdList.stream()
                    .filter(household -> household.getHouseholdNumber().equals(householdNumber.trim()))
                    .collect(java.util.stream.Collectors.toList());
            } else if (householderName != null && !householderName.trim().isEmpty()) {
                householdList = householdList.stream()
                    .filter(household -> household.getHouseholderName().contains(householderName.trim()))
                    .collect(java.util.stream.Collectors.toList());
            }
        }
        
        if (householdList == null) {
            householdList = new java.util.ArrayList<>();
        }
        
        return success(householdList);
    }
    
    /**
     * 新增户籍信息
     */
    @PostMapping("/add")
    @RequiresRole({"ADMIN", "USER"})
    public Result addHousehold(@RequestBody Household household) {
        // 设置创建者ID
        Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
        if (currentUserId != null) {
            household.setCreatedBy(currentUserId);
        }
        
        int result = householdService.insert(household);
        return result > 0 ? success("户籍信息添加成功") : error("户籍信息添加失败");
    }
    
    /**
     * 修改户籍信息
     */
    @PutMapping("/update")
    @RequiresRole({"ADMIN", "USER"})
    public Result updateHousehold(@RequestBody Household household) {
        if (household.getId() == null) {
            return error("户籍ID不能为空");
        }
        
        // 检查数据所有权
        Household existingHousehold = householdService.findById(household.getId());
        if (existingHousehold == null) {
            return error("未找到ID为 " + household.getId() + " 的户籍信息");
        }
        
        // 获取当前用户ID
        Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
        
        // 检查当前用户是否是数据创建者或管理员或关联的用户
        boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
        
        // 普通用户可以修改自己关联的户籍信息
        boolean isAssociatedUser = false;
        if (currentUserId != null) {
            Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
            isAssociatedUser = household.getId().equals(userHouseholdId);
        }
        
        if (currentUserId != null && !currentUserId.equals(existingHousehold.getCreatedBy()) && !isAdmin && !isAssociatedUser) {
            return error("没有权限修改该户籍信息");
        }
        
        int result = householdService.update(household);
        return result > 0 ? success("户籍信息修改成功") : error("户籍信息修改失败");
    }
    
    /**
     * 删除户籍信息
     */
    @DeleteMapping("/delete/{id}")
    @RequiresRole({"ADMIN", "USER"})
    public Result deleteHousehold(@PathVariable Long id) {
        try {
            // 检查数据所有权
            Household existingHousehold = householdService.findById(id);
            if (existingHousehold == null) {
                return error("未找到ID为 " + id + " 的户籍信息");
            }
            
            // 获取当前用户ID
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            
            // 检查当前用户是否是数据创建者或管理员或关联的用户
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            // 普通用户可以删除自己关联的户籍信息
            boolean isAssociatedUser = false;
            if (currentUserId != null) {
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                isAssociatedUser = id.equals(userHouseholdId);
            }
            
            if (currentUserId != null && !currentUserId.equals(existingHousehold.getCreatedBy()) && !isAdmin && !isAssociatedUser) {
                return error("没有权限删除该户籍信息");
            }
            
            int result = householdService.deleteById(id);
            return result > 0 ? success("户籍信息删除成功") : error("户籍信息删除失败");
        } catch (IllegalStateException e) {
            // 处理前置校验异常，返回409状态码表示冲突
            return new Result<>(409, e.getMessage(), null);
        } catch (Exception e) {
            // 处理其他异常
            return error("删除户籍信息时发生错误：" + e.getMessage());
        }
    }
    
    /**
     * 获取户籍记录总数
     */
    @GetMapping("/count")
    @RequiresRole("ADMIN")
    public Result<Long> count() {
        long count = householdService.count();
        return success(count);
    }
}