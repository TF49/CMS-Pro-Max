package com.example.cms.modules.population.controller;

import com.example.cms.common.Result;
import com.example.cms.framework.web.BaseController;
import com.example.cms.common.JwtUtils;
import com.example.cms.modules.population.strategy.DataStrategy;
import com.example.cms.modules.population.strategy.DataStrategyFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class DataManagementController extends BaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(DataManagementController.class);
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private DataStrategyFactory dataStrategyFactory;
    
    /**
     * 根据类型获取数据列表
     */
    @GetMapping("/list")
    public Result<Object> list(@RequestParam String type,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestHeader("Authorization") String authHeader) {
        try {
            // 参数验证
            if (page < 1) {
                page = 1;
            }
            if (size < 1 || size > 100) {
                size = 10;
            }
            
            // 从JWT令牌中获取当前用户ID和角色
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
            Long currentUserId = jwtUtils.getUserIdFromToken(token);
            String role = jwtUtils.getRoleFromToken(token);
            boolean isAdmin = "ADMIN".equals(role);
            
            // 根据type参数获取对应的策略实现
            DataStrategy strategy = dataStrategyFactory.getStrategy(type);
            if (strategy == null) {
                // 如果没有找到对应的策略，返回空列表
                return success(createPaginationResult(new java.util.ArrayList<>(), 0, page, size));
            }
            
            // 使用策略处理数据请求
            List<?> dataList = strategy.findData(currentUserId, isAdmin, page, size);
            int total = strategy.countData(currentUserId, isAdmin);
            Object result = createPaginationResult(dataList, total, page, size);
            
            return success(result);
        } catch (Exception e) {
            logger.error("Error getting data list for type: {}", type, e);
            return error("获取数据失败，请稍后重试");
        }
    }
    
    /**
     * 创建分页结果
     */
    private Map<String, Object> createPaginationResult(List<?> list, int total, int page, int size) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("data", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size);
        return result;
    }
}