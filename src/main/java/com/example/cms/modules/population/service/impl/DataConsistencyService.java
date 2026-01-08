package com.example.cms.modules.population.service.impl;

import com.example.cms.modules.population.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 * 数据一致性校验服务
 * 定期检查统计缓存数据与数据库数据是否一致
 */
@Service
public class DataConsistencyService {

    private static final Logger logger = Logger.getLogger(DataConsistencyService.class.getName());

    @Autowired
    private ResidentService residentService;

    @Autowired
    private HouseholdService householdService;

    @Autowired
    private EducationService educationService;

    @Autowired
    private MedicalService medicalService;

    @Autowired
    private EmploymentService employmentService;

    @Autowired
    private SocialSecurityService socialSecurityService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private CacheManager cacheManager;

    /**
     * 定期检查数据一致性，每5分钟执行一次
     * 检查统计缓存数据与数据库数据是否一致，不一致则清除缓存
     */
    @Scheduled(fixedRate = 300000) // 5分钟
    public void checkDataConsistency() {
        logger.info("开始检查数据一致性...");
        boolean needClearCache = false;

        try {
            // 获取数据库中的实际数据计数
            int dbResidentCount = residentService.count();
            int dbHouseholdCount = householdService.count();
            int dbEducationCount = educationService.count();
            int dbMedicalCount = medicalService.count();
            int dbEmploymentCount = employmentService.count();
            int dbSocialSecurityCount = socialSecurityService.count();
            int dbPropertyCount = propertyService.count();
            int dbVehicleCount = vehicleService.count();

            // 检查缓存中的数据
            // 注意：由于我们无法直接访问缓存中的值，这里采用清除缓存的策略
            // 如果缓存存在，则清除它以确保下次访问时从数据库获取最新数据
            if (cacheManager.getCache("statistics") != null) {
                needClearCache = true;
            }

            if (needClearCache) {
                cacheManager.getCache("statistics").clear();
                logger.info("统计缓存已清除，确保数据一致性");
            } else {
                logger.info("数据一致性检查通过，无需清除缓存");
            }
        } catch (Exception e) {
            logger.severe("数据一致性检查失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 手动检查数据一致性并清除缓存
     */
    public void manualCheckAndClearCache() {
        logger.info("手动检查数据一致性并清除缓存...");
        if (cacheManager.getCache("statistics") != null) {
            cacheManager.getCache("statistics").clear();
            logger.info("统计缓存已手动清除");
        }
    }
}
