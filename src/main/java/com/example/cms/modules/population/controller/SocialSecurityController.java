package com.example.cms.modules.population.controller;

import com.example.cms.common.Result;
import com.example.cms.common.annotation.RequiresRole;
import com.example.cms.modules.population.entity.SocialSecurity;
import com.example.cms.modules.population.service.SocialSecurityService;
import com.example.cms.modules.population.entity.Resident;
import com.example.cms.modules.population.service.ResidentService;
import com.example.cms.framework.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/social-security")
@Validated
public class SocialSecurityController extends BaseController {
    
    @Autowired
    private SocialSecurityService socialSecurityService;
    
    @Autowired
    private com.example.cms.modules.system.service.UserService userService;
    
    @Autowired
    private ResidentService residentService;
    
    /**
     * 查询社保信息列表（支持分页和搜索）
     */
    @GetMapping("/list")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Object> list(@RequestParam(required = false) String residentName,
                              @RequestParam(required = false) String idCard,
                              @RequestParam(required = false) String insuranceStatus,
                              @RequestParam(required = false) String pensionInsurance,
                              @RequestParam(required = false) String medicalInsurance,
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
            
            List<SocialSecurity> list;
            int total;
            
            if (isAdmin) {
            // 管理员可以查询所有数据
            list = socialSecurityService.searchWithPagination(residentName, idCard, insuranceStatus, pensionInsurance, medicalInsurance, page, size);
            total = socialSecurityService.countBySearch(residentName, idCard, insuranceStatus, pensionInsurance, medicalInsurance);
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
                    
                    // 查询这些居民的符合搜索条件的社保数据
                    list = socialSecurityService.searchByResidentIdsAndSearchConditions(residentIds, residentName, idCard, insuranceStatus, pensionInsurance, medicalInsurance, page, size);
                    total = socialSecurityService.countByResidentIdsAndSearchConditions(residentIds, residentName, idCard, insuranceStatus, pensionInsurance, medicalInsurance);
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
            return error("查询社保信息列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取社保记录总数
     */
    @GetMapping("/count")
    @RequiresRole("ADMIN")
    public Result<java.util.Map<String, Object>> count() {
        try {
            long count = socialSecurityService.count();
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("count", count);
            return success(data);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("获取社保记录总数失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID查询社保信息
     */
    @GetMapping("/{id}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<SocialSecurity> getById(@PathVariable Long id) {
        try {
            SocialSecurity socialSecurity = socialSecurityService.findById(id);
            if (socialSecurity == null) {
                return error("未找到ID为 " + id + " 的社保信息");
            }
            
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            // 普通用户只能查看自己关联户籍的数据
            if (!isAdmin && currentUserId != null) {
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                
                // 检查社保记录对应的居民是否属于用户关联的户籍
                Long residentId = socialSecurity.getResidentId();
                Resident resident = residentService.findById(residentId);
                if (resident == null || !resident.getHouseholdId().equals(userHouseholdId)) {
                    return error("没有权限查看该社保信息");
                }
            }
            
            return success(socialSecurity);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("查询社保信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据居民ID查询社保信息
     */
    @GetMapping("/resident/{residentId}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<List<SocialSecurity>> getByResidentId(@PathVariable Long residentId) {
        try {
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            List<SocialSecurity> list = socialSecurityService.findByResidentId(residentId);
            
            // 如果是普通用户，只返回自己关联户籍的数据
            if (!isAdmin && currentUserId != null) {
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                
                // 检查居民是否属于用户关联的户籍
                Resident resident = residentService.findById(residentId);
                if (resident != null && resident.getHouseholdId().equals(userHouseholdId)) {
                    // 可以查看该居民的社保信息
                } else {
                    return error("没有权限查看该居民的社保信息");
                }
            }
            
            return success(list);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("查询社保信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 新增社保信息
     */
    @PostMapping("/add")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Void> add(@RequestBody SocialSecurity socialSecurity) {
        try {
            // 验证社保信息是否完整
            if (!socialSecurity.hasValidResidentInfo()) {
                return error("居民信息不完整，请检查居民ID、姓名和身份证号");
            }
            if (!socialSecurity.hasValidSocialSecurityInfo()) {
                return error("社保信息不完整，请检查养老保险、医疗保险、参保状态、缴费金额和参保日期");
            }
            
            // 设置创建者ID为当前登录用户ID
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            if (currentUserId != null) {
                socialSecurity.setCreatedBy(currentUserId);
            } else {
                // 如果无法获取当前用户ID，默认设置为1（管理员）
                socialSecurity.setCreatedBy(1L);
            }
            
            // 打印调试信息
            System.out.println("Adding social security record: " + socialSecurity);
            
            int result = socialSecurityService.insert(socialSecurity);
            System.out.println("Add result: " + result);
            return result > 0 ? success(null) : error("社保信息添加失败");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("服务器内部错误，请稍后重试：" + e.getMessage());
        }
    }
    
    /**
     * 修改社保信息
     */
    @PutMapping("/update")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Void> update(@RequestBody SocialSecurity socialSecurity) {
        try {
            // 验证社保信息是否完整
            if (socialSecurity.getId() == null) {
                return error("社保ID不能为空");
            }
            if (!socialSecurity.hasValidResidentInfo()) {
                return error("居民信息不完整，请检查居民ID、姓名和身份证号");
            }
            if (!socialSecurity.hasValidSocialSecurityInfo()) {
                return error("社保信息不完整，请检查养老保险、医疗保险、参保状态、缴费金额和参保日期");
            }
            
            int result = socialSecurityService.update(socialSecurity);
            return result > 0 ? success(null) : error("社保信息修改失败");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            return error("服务器内部错误，请稍后重试");
        }
    }
    
    /**
     * 搜索社保信息
     */
    @GetMapping("/search")
    @RequiresRole("ADMIN")
    public Result<List<SocialSecurity>> search(@RequestParam(required = false) String residentName,
                                              @RequestParam(required = false) String idCard,
                                              @RequestParam(required = false) String insuranceStatus,
                                              @RequestParam(required = false) String pensionInsurance,
                                              @RequestParam(required = false) String medicalInsurance) {
        try {
            List<SocialSecurity> list = socialSecurityService.search(residentName, idCard, insuranceStatus, pensionInsurance, medicalInsurance);
            return success(list);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("搜索社保信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除社保信息
     */
    @DeleteMapping("/delete/{id}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Void> delete(@PathVariable Long id) {
        try {
            // 检查数据所有权
            SocialSecurity existingSocialSecurity = socialSecurityService.findById(id);
            if (existingSocialSecurity == null) {
                return error("未找到ID为 " + id + " 的社保信息");
            }
            
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            // 检查当前用户是否是数据创建者或管理员
            if (currentUserId != null && !currentUserId.equals(existingSocialSecurity.getCreatedBy()) && !isAdmin) {
                return error("没有权限删除该社保信息");
            }
            
            int result = socialSecurityService.deleteById(id);
            return result > 0 ? success(null) : error("操作失败");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("删除社保信息失败：" + e.getMessage());
        }
    }
}