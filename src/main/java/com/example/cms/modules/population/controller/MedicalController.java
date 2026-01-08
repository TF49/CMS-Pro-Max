package com.example.cms.modules.population.controller;

import com.example.cms.common.Result;
import com.example.cms.common.annotation.RequiresRole;
import com.example.cms.modules.population.entity.Medical;
import com.example.cms.modules.population.service.MedicalService;
import com.example.cms.modules.system.service.UserService;
import com.example.cms.modules.population.entity.Resident;
import com.example.cms.modules.population.service.ResidentService;
import com.example.cms.framework.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医疗管理Controller
 * 继承BaseController，提供标准化的响应结果
 */
@RestController
@RequestMapping("/medical")
@Validated
public class MedicalController extends BaseController {
    
    @Autowired
    private MedicalService medicalService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ResidentService residentService;
    
    /**
     * 查询医疗记录（支持分页）
     */
    @GetMapping("/list")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Object> list(@RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer size) {
        try {
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            List<Medical> list;
            int total;
            
            if (isAdmin) {
            // 管理员可以查询所有数据
            list = medicalService.findAllWithPagination(page, size);
            total = medicalService.count();
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
                    
                    // 查询这些居民的所有医疗数据
                    list = medicalService.findByResidentIds(residentIds, page, size);
                    total = medicalService.countByResidentIds(residentIds);
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
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("查询医疗记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID查询医疗记录
     */
    @GetMapping("/{id}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Medical> getById(@PathVariable Long id) {
        try {
            Medical medical = medicalService.findById(id);
            if (medical == null) {
                return error("未找到ID为 " + id + " 的医疗记录");
            }
            
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            // 普通用户只能查看自己关联户籍的数据
            if (!isAdmin && currentUserId != null) {
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                
                // 检查医疗记录对应的居民是否属于用户关联的户籍
                Long residentId = medical.getResidentId();
                Resident resident = residentService.findById(residentId);
                if (resident != null && resident.getHouseholdId().equals(userHouseholdId)) {
                    // 可以查看该医疗记录
                } else {
                    return error("没有权限查看该医疗记录");
                }
            }
            
            return success(medical);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("查询医疗记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据居民ID查询医疗记录
     */
    @GetMapping("/resident/{residentId}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<List<Medical>> getByResidentId(@PathVariable Long residentId) {
        try {
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            List<Medical> list = medicalService.findByResidentId(residentId);
            
            // 如果是普通用户，只返回自己关联户籍的数据
            if (!isAdmin && currentUserId != null) {
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                
                // 检查居民是否属于用户关联的户籍
                Resident resident = residentService.findById(residentId);
                if (resident != null && resident.getHouseholdId().equals(userHouseholdId)) {
                    // 可以查看该居民的医疗记录
                } else {
                    return error("没有权限查看该居民的医疗记录");
                }
            }
            
            return success(list);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("查询居民医疗记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 新增医疗记录
     */
    @PostMapping("/add")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Void> add(@RequestBody Medical medical) {
        try {
            // 设置创建者ID为当前登录用户ID
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            if (currentUserId != null) {
                medical.setCreatedBy(currentUserId);
            } else {
                // 如果无法获取当前用户ID，默认设置为1（管理员）
                medical.setCreatedBy(1L);
            }
            
            // 打印调试信息
            System.out.println("Adding medical record: " + medical);
            
            int result = medicalService.insert(medical);
            System.out.println("Add result: " + result);
            return result > 0 ? success(null) : error("操作失败");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("服务器内部错误，请稍后重试：" + e.getMessage());
        }
    }
    
    /**
     * 修改医疗记录
     */
    @PutMapping("/update")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Void> update(@RequestBody Medical medical) {
        try {
            if (medical.getId() == null) {
                return error("医疗记录ID不能为空");
            }

            // 检查数据所有权
            Medical existingMedical = medicalService.findById(medical.getId());
            if (existingMedical == null) {
                return error("未找到ID为 " + medical.getId() + " 的医疗记录");
            }

            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();

            // 检查当前用户是否是管理员或数据属于用户关联的户籍
            if (!isAdmin && currentUserId != null) {
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                Long residentId = existingMedical.getResidentId();
                Resident resident = residentService.findById(residentId);
                if (resident == null || !resident.getHouseholdId().equals(userHouseholdId)) {
                    return error("没有权限修改该医疗记录");
                }
            }

            int result = medicalService.update(medical);
            return result > 0 ? success(null) : error("操作失败");
        } catch (Exception e) {
            return error("修改医疗记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除医疗记录
     */
    @DeleteMapping("/delete/{id}")
    @RequiresRole({"ADMIN", "USER"})
    public Result<Void> delete(@PathVariable Long id) {
        try {
            // 检查数据所有权
            Medical existingMedical = medicalService.findById(id);
            if (existingMedical == null) {
                return error("未找到ID为 " + id + " 的医疗记录");
            }
            
            // 获取当前用户ID和角色
            Long currentUserId = com.example.cms.common.util.UserContext.getCurrentUserId();
            boolean isAdmin = com.example.cms.common.util.UserContext.isAdmin();
            
            // 检查当前用户是否是管理员或数据属于用户关联的户籍
            if (!isAdmin && currentUserId != null) {
                Long userHouseholdId = userService.getUserHouseholdId(currentUserId);
                Long residentId = existingMedical.getResidentId();
                Resident resident = residentService.findById(residentId);
                if (resident == null || !resident.getHouseholdId().equals(userHouseholdId)) {
                    return error("没有权限删除该医疗记录");
                }
            }
            
            int result = medicalService.deleteById(id);
            return result > 0 ? success(null) : error("操作失败");
        } catch (Exception e) {
            return error("删除医疗记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取医疗记录总数
     */
    @GetMapping("/count")
    @RequiresRole("ADMIN")
    public Result<Long> count() {
        try {
            long count = medicalService.count();
            return success(count);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("获取医疗记录总数失败：" + e.getMessage());
        }
    }
    
    /**
     * 搜索医疗记录
     */
    @GetMapping("/search")
    @RequiresRole("ADMIN")
    public Result<List<Medical>> search(@RequestParam(required = false) String residentName,
                                      @RequestParam(required = false) String hospitalName) {
        try {
            List<Medical> list = medicalService.search(residentName, hospitalName);
            return success(list);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return error("搜索医疗记录失败：" + e.getMessage());
        }
    }
}