package com.example.cms.modules.population.controller;

import com.example.cms.common.Result;
import com.example.cms.common.annotation.RequiresRole;
import com.example.cms.modules.population.entity.Resident;
import com.example.cms.modules.population.entity.Household;
import com.example.cms.modules.population.service.ResidentService;
import com.example.cms.modules.system.service.UserService;
import com.example.cms.modules.population.service.HouseholdService;
import com.example.cms.framework.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 居民管理Controller
 * 继承BaseController，提供标准化的响应结果
 */
@RestController
@RequestMapping("/resident")
public class ResidentController extends BaseController {
    
    @Autowired
    private ResidentService residentService;
    
    @Autowired
    private HouseholdService householdService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 分页查询居民数据 - 基础列表接口
     */
    @GetMapping("/list")
    @RequiresRole({"ADMIN", "USER"})
    public Result getResidentData(@RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        // 参数验证
        if (page < 1) {
            page = 1;
        }
        if (size < 1 || size > 100) {
            size = 10;
        }
        
        List<Resident> residentList;
        int total;
        
        // 获取当前用户ID和角色
        Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
        boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
        
        if (isAdmin) {
            // 管理员查看所有数据
            residentList = residentService.findByPageWithHouseholdInfo(page, size);
            total = residentService.count();
        } else {
            // 普通用户只能查看自己关联户籍的数据
            Long householdId = userService.getUserHouseholdId(currentUserId);
            residentList = householdId != null ? residentService.findByHouseholdIdWithHouseholdInfo(householdId, page, size) : new ArrayList<>();
            total = householdId != null ? residentService.countByHouseholdId(householdId) : 0;
        }
        
        if (residentList == null) {
            residentList = new ArrayList<>();
        }
        
        // 构造分页响应
        Map<String, Object> result = new HashMap<>();
        result.put("data", residentList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size);
        
        return success(result);
    }
    
    /**
     * 分页查询居民数据 - 统一搜索接口（只支持姓名和身份证号查询）
     */
    @GetMapping("/search")
    @RequiresRole({"ADMIN", "USER"})
    public Result searchResidents(@RequestParam(required = false) String idCard,
                                  @RequestParam(required = false) String name,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        // 参数验证
        if (page < 1) {
            page = 1;
        }
        if (size < 1 || size > 100) {
            size = 10;
        }
        
        List<Resident> residentList;
        int total;
        
        // 获取当前用户ID和角色
        Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
        boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
        
        if (isAdmin) {
            // 管理员可以搜索所有数据
            String trimmedName = name != null ? name.trim() : null;
            String trimmedIdCard = idCard != null ? idCard.trim() : null;
            
            // 检查搜索条件，如果都为空则返回所有数据
            boolean hasSearchCriteria = (trimmedName != null && !trimmedName.isEmpty()) || 
                                       (trimmedIdCard != null && !trimmedIdCard.isEmpty());
            
            if (hasSearchCriteria) {
                residentList = residentService.searchWithHouseholdInfo(
                    trimmedName,
                    trimmedIdCard,
                    page, size
                );
                total = residentService.countBySearch(
                    trimmedName,
                    trimmedIdCard
                );
            } else {
                // 如果没有搜索条件，返回所有数据
                residentList = residentService.findByPageWithHouseholdInfo(page, size);
                total = (int) residentService.count();
            }
        } else {
            // 普通用户只能搜索自己关联户籍的数据
            Long householdId = userService.getUserHouseholdId(currentUserId);
            if (householdId != null) {
                // 有搜索条件
                String trimmedName = name != null ? name.trim() : null;
                String trimmedIdCard = idCard != null ? idCard.trim() : null;
                residentList = residentService.findByHouseholdIdWithHouseholdInfo(householdId, page, size);
                total = residentService.countByHouseholdId(householdId);
                
                // 对结果进行过滤
                if (trimmedName != null && !trimmedName.isEmpty()) {
                    residentList = residentList.stream()
                        .filter(resident -> resident.getName().contains(trimmedName))
                        .collect(java.util.stream.Collectors.toList());
                    total = residentList.size();
                } else if (trimmedIdCard != null && !trimmedIdCard.isEmpty()) {
                    residentList = residentList.stream()
                        .filter(resident -> resident.getIdCard().contains(trimmedIdCard))
                        .collect(java.util.stream.Collectors.toList());
                    total = residentList.size();
                }
            } else {
                // 没有关联户籍，返回空列表
                residentList = new ArrayList<>();
                total = 0;
            }
        }
        
        if (residentList == null) {
            residentList = new ArrayList<>();
        }
        
        // 构造分页响应
        Map<String, Object> result = new HashMap<>();
        result.put("data", residentList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size);
        
        return success(result);
    }
    
    /**
     * 新增居民信息
     */
    @PostMapping("/add")
    @RequiresRole({"ADMIN", "USER"})
    public Result addResident(@RequestBody Resident resident) {
        // 数据验证
        if (resident == null) {
            return error("居民信息不能为空");
        }
        
        // 验证必要字段
        if (resident.getName() == null || resident.getName().trim().isEmpty()) {
            return error("居民姓名不能为空");
        }
        
        if (resident.getIdCard() == null || resident.getIdCard().trim().isEmpty()) {
            return error("身份证号不能为空");
        }
        
        // 验证身份证号格式（简单验证）
        String idCard = resident.getIdCard().trim();
        if (!isValidIdCard(idCard)) {
            return error("身份证号格式不正确");
        }
        
        // 检查姓名长度
        String name = resident.getName().trim();
        if (name.length() > 50) {
            return error("居民姓名长度不能超过50个字符");
        }
        
        // 清理数据
        resident.setName(name);
        resident.setIdCard(idCard.toUpperCase());
        
        // 检查是否已存在相同身份证号的居民
        List<Resident> existingResidents = residentService.findByIdCard(idCard);
        if (existingResidents != null && !existingResidents.isEmpty()) {
            return error("该身份证号已存在居民记录");
        }
        
        // 设置创建者ID
        Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
        if (currentUserId != null) {
            resident.setCreatedBy(currentUserId);
        }
        
        int result = residentService.insert(resident);
        return result > 0 ? success("居民信息添加成功") : error("居民信息添加失败");
    }
    
    /**
     * 验证身份证号格式
     */
    private boolean isValidIdCard(String idCard) {
        // 简单的身份证号格式验证
        if (idCard == null) {
            return false;
        }
        
        String trimmedIdCard = idCard.trim();
        
        // 18位或15位身份证号验证
        if (trimmedIdCard.length() == 18) {
            return trimmedIdCard.matches("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
        } else if (trimmedIdCard.length() == 15) {
            return trimmedIdCard.matches("^[1-9]\\d{5}\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}$");
        }
        
        return false;
    }
    
    /**
     * 修改居民信息
     */
    @PutMapping("/update")
    @RequiresRole({"ADMIN", "USER"})
    public Result updateResident(@RequestBody Resident resident) {
        if (resident.getId() == null) {
            return error("居民ID不能为空");
        }
        
        // 检查数据所有权
        Resident existingResident = residentService.findById(resident.getId());
        if (existingResident == null) {
            return error("未找到ID为 " + resident.getId() + " 的居民信息");
        }
        
        // 获取当前用户ID和角色
        Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
        boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
        
        // 普通用户只能修改自己关联户籍下的居民信息
        if (!isAdmin && currentUserId != null) {
            Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
            if (userHouseholdId == null || !userHouseholdId.equals(existingResident.getHouseholdId())) {
                return error("没有权限修改该居民信息");
            }
        }
        
        int result = residentService.update(resident);
        return result > 0 ? success("居民信息修改成功") : error("居民信息修改失败");
    }
    
    /**
     * 删除居民信息
     */
    @DeleteMapping("/delete/{id}")
    @RequiresRole({"ADMIN", "USER"})
    public Result deleteResident(@PathVariable Long id) {
        try {
            // 检查数据所有权
        Resident existingResident = residentService.findById(id);
        if (existingResident == null) {
            return error("未找到ID为 " + id + " 的居民信息");
        }
        
        // 获取当前用户ID和角色
        Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
        boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
        
        // 普通用户只能删除自己关联户籍下的居民信息
        if (!isAdmin && currentUserId != null) {
            Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
            if (userHouseholdId == null || !userHouseholdId.equals(existingResident.getHouseholdId())) {
                return error("没有权限删除该居民信息");
            }
        }
            
            int result = residentService.deleteById(id);
            return result > 0 ? success("居民信息删除成功") : error("居民信息删除失败");
        } catch (RuntimeException e) {
            // 捕获关联检查异常，返回409状态码和友好的错误信息
            return new Result<>(409, e.getMessage(), null);
        } catch (Exception e) {
            // 捕获其他异常
            return error("删除居民信息时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 获取居民记录总数
     */
    @GetMapping("/count")
    @RequiresRole("ADMIN")
    public Result<Map<String, Object>> count() {
        long count = residentService.count();
        Map<String, Object> data = new HashMap<>();
        data.put("count", count);
        return success(data);
    }
    
    /**
     * 根据户籍ID获取居民列表
     */
    @GetMapping("/household/{householdId}")
    @RequiresRole({"ADMIN", "USER"})
    public Result getByHouseholdId(@PathVariable Long householdId) {
        // 获取当前用户ID和角色
        Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
        boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
        
        // 先查询户籍信息，检查权限
        Household household = householdService.findById(householdId);
        if (household == null) {
            return error("未找到户籍ID为 " + householdId + " 的户籍信息");
        }
        
        // 普通用户只能查看自己关联户籍下的居民
        if (!isAdmin && currentUserId != null) {
            Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
            if (userHouseholdId == null || !userHouseholdId.equals(householdId)) {
                return error("没有权限查看该户籍下的居民信息");
            }
        }
        
        List<Resident> residentList = residentService.findByHouseholdId(householdId);
        if (residentList == null || residentList.isEmpty()) {
            return error("未找到户籍ID为 " + householdId + " 的居民信息");
        }
        return success(residentList);
    }
    
    /**
     * 根据身份证号获取居民信息
     */
    @GetMapping("/idCard/{idCard}")
    @RequiresRole({"ADMIN", "USER"})
    public Result getResidentByIdCard(@PathVariable String idCard) {
        if (idCard == null || idCard.trim().isEmpty()) {
            return error("身份证号不能为空");
        }
        
        List<Resident> residentList = residentService.findByIdCard(idCard.trim());
        if (residentList == null || residentList.isEmpty()) {
            return error("未找到身份证号为 " + idCard + " 的居民信息");
        }
        
        // 获取当前用户ID和角色
        Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
        boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
        
        // 普通用户只能查看自己关联户籍的数据
        if (!isAdmin && currentUserId != null) {
            Resident resident = residentList.get(0);
            Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
            if (userHouseholdId == null || !userHouseholdId.equals(resident.getHouseholdId())) {
                return error("没有权限查看该居民信息");
            }
        }
        
        // 返回第一个匹配的居民信息（身份证号应该是唯一的）
        return success(residentList.get(0));
    }
    
    /**
     * 根据ID查询居民详情，包含户籍信息
     */
    @GetMapping("/{id}")
    @RequiresRole({"ADMIN", "USER"})
    public Result getResidentById(@PathVariable Long id) {
        if (id == null) {
            return error("居民ID不能为空");
        }
        
        Resident resident = residentService.findByIdWithHouseholdInfo(id);
        if (resident == null) {
            return error("未找到ID为 " + id + " 的居民信息");
        }
        
        // 获取当前用户ID和角色
        Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
        boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
        
        // 普通用户只能查看自己关联户籍的数据
        if (!isAdmin && currentUserId != null) {
            Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
            // 处理居民householdId为null的情况
            if (userHouseholdId == null || (resident.getHouseholdId() == null || !userHouseholdId.equals(resident.getHouseholdId()))) {
                return error("没有权限查看该居民信息");
            }
        }
        
        return success(resident);
    }
}