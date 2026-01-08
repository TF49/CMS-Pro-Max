package com.example.cms.modules.population.controller;

import com.example.cms.common.Result;
import com.example.cms.common.annotation.RequiresRole;
import com.example.cms.modules.population.entity.Employment;
import com.example.cms.modules.population.service.EmploymentService;
import com.example.cms.modules.system.service.UserService;
import com.example.cms.modules.population.entity.Resident;
import com.example.cms.modules.population.service.ResidentService;
import com.example.cms.framework.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employment")
@Validated
public class EmploymentController extends BaseController {
    
    @Autowired
    private EmploymentService employmentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ResidentService residentService;
    
    /**
     * 查询就业信息列表（支持分页和搜索）
     */
    @GetMapping("/list")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Object> list(@RequestParam(required = false) String residentName,
                              @RequestParam(required = false) String companyName,
                              @RequestParam(required = false) String industry,
                              @RequestParam(required = false) String employmentStatus,
                              @RequestParam(required = false, defaultValue = "1") int page,
                              @RequestParam(required = false, defaultValue = "10") int size) {
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
            
            List<Employment> list;
            int total;
            
            if (isAdmin) {
            // 管理员可以查询所有数据
            list = employmentService.searchWithPagination(residentName, companyName, industry, employmentStatus, page, size);
            total = employmentService.countBySearch(residentName, companyName, industry, employmentStatus);
            // 添加日志，输出查询结果
            System.out.println("管理员查询结果：list.size() = " + (list == null ? 0 : list.size()) + ", total = " + total);
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
                    
                    // 查询这些居民的符合搜索条件的就业数据
                    list = employmentService.searchByResidentIdsAndSearchConditions(residentIds, residentName, companyName, industry, employmentStatus, page, size);
                    total = employmentService.countByResidentIdsAndSearchConditions(residentIds, residentName, companyName, industry, employmentStatus);
                } else {
                    list = new java.util.ArrayList<>();
                    total = 0;
                }
            } else {
                list = new java.util.ArrayList<>();
                total = 0;
            }
            // 添加日志，输出查询结果
            System.out.println("普通用户查询结果：list.size() = " + (list == null ? 0 : list.size()) + ", total = " + total);
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
            return error("查询就业信息列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID查询就业信息
     */
    @GetMapping("/{id}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Employment> getById(@PathVariable Long id) {
        try {
            Employment employment = employmentService.findById(id);
            
            // 检查数据所有权
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            // 检查当前用户是否是数据创建者、管理员或数据由管理员创建
            if (currentUserId != null && employment != null) {
                if (isAdmin) {
                    // 管理员可以查看所有数据
                } else {
                    // 普通用户只能查看自己关联户籍的数据
                    Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                    
                    // 检查就业记录对应的居民是否属于用户关联的户籍
                    Long residentId = employment.getResidentId();
                    Resident resident = residentService.findById(residentId);
                    if (resident != null && resident.getHouseholdId().equals(userHouseholdId)) {
                        // 可以查看该就业信息
                    } else {
                        return error("没有权限查看该就业信息");
                    }
                }
            }
            
            return success(employment);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("查询就业信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据居民ID查询就业信息
     */
    @GetMapping("/resident/{residentId}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<List<Employment>> getByResidentId(@PathVariable Long residentId) {
        try {
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            // 普通用户只能查看自己关联户籍的数据
            if (!isAdmin && currentUserId != null) {
                // 检查居民是否属于用户关联的户籍
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                Resident resident = residentService.findById(residentId);
                if (resident == null || !resident.getHouseholdId().equals(userHouseholdId)) {
                    return error("没有权限查看该居民的就业信息");
                }
            }
            
            List<Employment> list = employmentService.findByResidentId(residentId);
            
            // 普通用户已经通过居民ID检查，确保只能查看自己户籍下的居民的就业信息，不需要额外过滤
            // 移除基于创建者的过滤，保持与list方法逻辑一致
            
            return success(list);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("查询居民就业信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 新增就业信息
     */
    @PostMapping("/add")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Void> add(@RequestBody Employment employment) {
        try {
            // 设置创建者ID为当前登录用户ID
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            if (currentUserId != null) {
                employment.setCreatedBy(currentUserId);
            } else {
                // 如果无法获取当前用户ID，默认设置为1（管理员）
                employment.setCreatedBy(1L);
            }
            
            // 打印调试信息
            System.out.println("Adding employment record: " + employment);
            
            int result = employmentService.insert(employment);
            System.out.println("Add result: " + result);
            return result > 0 ? success(null) : error("就业信息添加失败");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("服务器内部错误，请稍后重试：" + e.getMessage());
        }
    }
    
    /**
     * 修改就业信息
     */
    @PutMapping("/update")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Void> update(@RequestBody Employment employment) {
        try {
            if (employment.getId() == null) {
                return error("就业信息ID不能为空");
            }
            
            // 检查数据所有权
            Employment existingEmployment = employmentService.findById(employment.getId());
            if (existingEmployment == null) {
                return error("未找到ID为 " + employment.getId() + " 的就业信息");
            }
            
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            // 检查当前用户是否是管理员或数据属于用户关联的户籍
            if (!isAdmin && currentUserId != null) {
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                Long residentId = existingEmployment.getResidentId();
                Resident resident = residentService.findById(residentId);
                if (resident == null || !resident.getHouseholdId().equals(userHouseholdId)) {
                    return error("没有权限修改该就业信息");
                }
            }
            
            int result = employmentService.update(employment);
            return result > 0 ? success(null) : error("操作失败");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("修改就业信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除就业信息
     */
    @DeleteMapping("/delete/{id}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Void> delete(@PathVariable Long id) {
        try {
            // 检查数据所有权
            Employment existingEmployment = employmentService.findById(id);
            if (existingEmployment == null) {
                return error("未找到ID为 " + id + " 的就业信息");
            }
            
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            // 检查当前用户是否是管理员或数据属于用户关联的户籍
            if (!isAdmin && currentUserId != null) {
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                Long residentId = existingEmployment.getResidentId();
                Resident resident = residentService.findById(residentId);
                if (resident == null || !resident.getHouseholdId().equals(userHouseholdId)) {
                    return error("没有权限删除该就业信息");
                }
            }
            
            int result = employmentService.deleteById(id);
            return result > 0 ? success(null) : error("操作失败");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("删除就业信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取就业记录总数
     */
    @GetMapping("/count")
    @RequiresRole("ADMIN")
    public Result<Long> count() {
        try {
            long count = employmentService.count();
            return success(count);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("获取就业记录总数失败：" + e.getMessage());
        }
    }
    
    /**
     * 搜索就业信息
     */
    @GetMapping("/search")
    @RequiresRole("ADMIN")
    public Result<List<Employment>> search(@RequestParam(required = false) String residentName,
                                          @RequestParam(required = false) String companyName,
                                          @RequestParam(required = false) String industry,
                                          @RequestParam(required = false) String employmentStatus) {
        try {
            List<Employment> list = employmentService.search(residentName, companyName, industry, employmentStatus);
            return success(list);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("搜索就业信息失败：" + e.getMessage());
        }
    }
}