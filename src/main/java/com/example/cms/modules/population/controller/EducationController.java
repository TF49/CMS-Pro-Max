package com.example.cms.modules.population.controller;

import com.example.cms.common.Result;
import com.example.cms.common.annotation.RequiresRole;
import com.example.cms.modules.population.entity.Education;
import com.example.cms.modules.population.service.EducationService;
import com.example.cms.modules.system.service.UserService;
import com.example.cms.modules.population.entity.Resident;
import com.example.cms.modules.population.service.ResidentService;
import com.example.cms.framework.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教育管理Controller
 * 继承BaseController，提供标准化的响应结果
 * 统一接口规范，与医疗管理模块保持一致
 */
@RestController
@RequestMapping("/education")
@Validated
public class EducationController extends BaseController {
    
    @Autowired
    private EducationService educationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ResidentService residentService;
    
    /**
     * 查询教育记录（支持分页）
     */
    @GetMapping("/list")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Object> list(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer size) {
        try {
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            List<Education> list;
            int total;
            
            if (isAdmin) {
                // 管理员可以查询所有数据
                list = educationService.findAllWithPagination(page, size);
                total = educationService.count();
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
                        
                        // 查询这些居民的所有教育数据
                        list = educationService.findByResidentIds(residentIds, page, size);
                        total = educationService.countByResidentIds(residentIds);
                    } else {
                        list = new java.util.ArrayList<>();
                        total = 0;
                    }
                } else {
                    list = new java.util.ArrayList<>();
                    total = 0;
                }
            }
            
            // 构造分页响应
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("data", list);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            result.put("pages", (total + size - 1) / size);
            
            return success(result);
        } catch (Exception e) {
            return error("查询教育记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID查询教育记录
     */
    @GetMapping("/{id}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Education> getById(@PathVariable Long id) {
        try {
            Education education = educationService.findById(id);
            if (education == null) {
                return error("教育记录不存在");
            }
            
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            // 普通用户只能查看自己关联户籍的数据
            if (!isAdmin && currentUserId != null) {
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                
                // 检查教育记录对应的居民是否属于用户关联的户籍
                Long residentId = education.getResidentId();
                Resident resident = residentService.findById(residentId);
                if (resident != null && resident.getHouseholdId().equals(userHouseholdId)) {
                    // 可以查看该教育记录
                } else {
                    return error("没有权限查看该教育记录");
                }
            }
            
            return success(education);
        } catch (Exception e) {
            return error("查询教育记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据居民ID查询教育记录
     */
    @GetMapping("/resident/{residentId}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<List<Education>> getByResidentId(@PathVariable Long residentId) {
        try {
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            List<Education> list = educationService.findByResidentId(residentId);
            
            // 如果是普通用户，只返回自己关联户籍的数据
            if (!isAdmin && currentUserId != null) {
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                
                // 检查居民是否属于用户关联的户籍
                Resident resident = residentService.findById(residentId);
                if (resident != null && resident.getHouseholdId().equals(userHouseholdId)) {
                    // 可以查看该居民的教育记录
                } else {
                    return error("没有权限查看该居民的教育记录");
                }
            }
            
            return success(list);
        } catch (Exception e) {
            return error("查询居民教育记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据居民身份证号查询教育记录
     */
    @GetMapping("/resident/idcard/{idCard}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<List<Education>> getByResidentIdCard(@PathVariable String idCard) {
        try {
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            List<Education> list = educationService.findByResidentIdCard(idCard);
            
            // 如果是普通用户，只返回自己关联户籍的数据
            if (!isAdmin && currentUserId != null) {
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                
                // 检查居民是否属于用户关联的户籍
                List<Resident> residents = residentService.findByIdCard(idCard);
                if (!residents.isEmpty()) {
                    boolean hasAccess = residents.stream()
                        .anyMatch(resident -> resident.getHouseholdId().equals(userHouseholdId));
                    
                    if (hasAccess) {
                        // 可以查看该居民的教育记录
                    } else {
                        return error("没有权限查看该居民的教育记录");
                    }
                } else {
                    return error("未找到该居民的教育记录");
                }
            }
            
            return success(list);
        } catch (Exception e) {
            return error("查询居民教育记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 新增教育记录
     */
    @PostMapping("/add")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Void> add(@RequestBody Education education) {
        try {
            // 设置创建者ID为当前登录用户ID
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            if (currentUserId != null) {
                education.setCreatedBy(currentUserId);
            } else {
                // 如果无法获取当前用户ID，默认设置为1（管理员）
                education.setCreatedBy(1L);
            }
            
            // 打印调试信息
            System.out.println("Adding education record: " + education);
            
            int result = educationService.insert(education);
            System.out.println("Add result: " + result);
            return result > 0 ? success(null) : error("新增教育记录失败");
        } catch (RuntimeException e) {
            e.printStackTrace();
            String errorMsg = "RuntimeException: " + e.getMessage() + "，堆栈信息：" + java.util.Arrays.toString(e.getStackTrace());
            System.err.println(errorMsg);
            return error(errorMsg);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = "Exception: " + e.getMessage() + "，堆栈信息：" + java.util.Arrays.toString(e.getStackTrace());
            System.err.println(errorMsg);
            return error(errorMsg);
        }
    }
    
    /**
     * 修改教育记录
     */
    @PutMapping("/update")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Void> update(@RequestBody Education education) {
        try {
            if (education.getId() == null) {
                return error("教育记录ID不能为空");
            }
            
            // 检查数据所有权
            Education existingEducation = educationService.findById(education.getId());
            if (existingEducation == null) {
                return error("未找到ID为 " + education.getId() + " 的教育记录");
            }
            
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            // 检查权限
            if (!isAdmin && currentUserId != null) {
                // 普通用户只能修改自己关联户籍的数据
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                
                // 检查教育记录对应的居民是否属于用户关联的户籍
                Long residentId = existingEducation.getResidentId();
                Resident resident = residentService.findById(residentId);
                if (resident == null || !resident.getHouseholdId().equals(userHouseholdId)) {
                    return error("没有权限修改该教育记录");
                }
            }
            
            int result = educationService.update(education);
            return result > 0 ? success(null) : error("修改教育记录失败");
        } catch (RuntimeException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            return error("修改教育记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除教育记录
     */
    @DeleteMapping("/delete/{id}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Void> delete(@PathVariable Long id) {
        try {
            // 检查数据所有权
            Education existingEducation = educationService.findById(id);
            if (existingEducation == null) {
                return error("未找到ID为 " + id + " 的教育记录");
            }
            
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            // 检查权限
            if (!isAdmin && currentUserId != null) {
                // 普通用户只能删除自己关联户籍的数据
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                
                // 检查教育记录对应的居民是否属于用户关联的户籍
                Long residentId = existingEducation.getResidentId();
                Resident resident = residentService.findById(residentId);
                if (resident == null || !resident.getHouseholdId().equals(userHouseholdId)) {
                    return error("没有权限删除该教育记录");
                }
            }
            
            int result = educationService.deleteById(id);
            return result > 0 ? success(null) : error("删除教育记录失败");
        } catch (RuntimeException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            return error("删除教育记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 搜索教育记录
     */
    @GetMapping("/search")
    @RequiresRole("ADMIN")
    public Result<List<Education>> search(@RequestParam(required = false) String residentName,
                                         @RequestParam(required = false) String schoolName,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer size) {
        try {
            List<Education> list = educationService.search(residentName, schoolName, page, size);
            return success(list);
        } catch (Exception e) {
            return error("搜索教育记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取教育记录总数
     */
    @GetMapping("/count")
    @RequiresRole("ADMIN")
    public Result<Long> count() {
        try {
            long count = educationService.count();
            return success(count);
        } catch (Exception e) {
            return error("获取教育记录总数失败：" + e.getMessage());
        }
    }
}