package com.example.cms.modules.system.controller;

import com.example.cms.common.Result;
import com.example.cms.framework.web.BaseController;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 缓存管理控制器
 * 用于管理系统缓存，特别是统计数据缓存的清除
 */
@RestController
@RequestMapping("/api/cache")
public class CacheController extends BaseController 
{
    @Resource
    private CacheManager cacheManager;

    /**
     * 清除所有统计数据缓存
     * 当数据库数据发生变化时，调用此接口清除统计缓存，确保统计数据实时更新
     */
    @PostMapping("/clear/statistics")
    public Result<Boolean> clearStatisticsCache() {
        try {
            // 清除所有统计相关的缓存
            if (cacheManager.getCache("statistics") != null) {
                cacheManager.getCache("statistics").clear();

            }
            return success(true);
        } catch (Exception e) {
            return error("统计缓存清除失败：" + e.getMessage());
        }
    }

    /**
     * 清除指定名称的缓存
     * @param cacheName 缓存名称
     */
    @PostMapping("/clear")
    public Result<Boolean> clearCache(String cacheName) {
        try {
            if (cacheName != null && !cacheName.isEmpty()) {
                cacheManager.getCache(cacheName).clear();
                return success(true);
            } else {
                return error("缓存名称不能为空");
            }
        } catch (Exception e) {
            return error("缓存清除失败：" + e.getMessage());
        }
    }
}