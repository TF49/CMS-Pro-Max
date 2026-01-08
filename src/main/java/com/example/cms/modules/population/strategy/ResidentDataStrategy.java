package com.example.cms.modules.population.strategy;

import com.example.cms.modules.population.service.ResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 居民数据策略实现类
 * 处理居民数据的查询和统计
 */
@Component("residentDataStrategy")
public class ResidentDataStrategy implements DataStrategy {

    @Autowired
    private ResidentService residentService;

    @Override
    public List<?> findData(Long currentUserId, boolean isAdmin, int page, int size) {
        if (isAdmin) {
            return residentService.findByPageWithHouseholdInfo(page, size);
        } else {
            return residentService.findByCreatedBy(currentUserId, page, size);
        }
    }

    @Override
    public int countData(Long currentUserId, boolean isAdmin) {
        if (isAdmin) {
            return residentService.count();
        } else {
            return residentService.countByCreatedBy(currentUserId);
        }
    }
}
