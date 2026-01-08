package com.example.cms.modules.population.strategy;

import java.util.List;

/**
 * 数据策略接口
 * 定义处理不同类型数据请求的方法
 */
public interface DataStrategy {
    /**
     * 查询数据列表
     * @param currentUserId 当前用户ID
     * @param isAdmin 是否为管理员
     * @param page 页码
     * @param size 每页大小
     * @return 数据列表
     */
    List<?> findData(Long currentUserId, boolean isAdmin, int page, int size);
    
    /**
     * 统计数据数量
     * @param currentUserId 当前用户ID
     * @param isAdmin 是否为管理员
     * @return 数据数量
     */
    int countData(Long currentUserId, boolean isAdmin);
}
