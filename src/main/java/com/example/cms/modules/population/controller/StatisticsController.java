package com.example.cms.modules.population.controller;

import com.example.cms.common.Result;
import com.example.cms.modules.population.entity.*;
import com.example.cms.modules.population.service.*;
import com.example.cms.framework.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController extends BaseController {
    
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
    
    /**
     * 获取系统统计摘要
     */
    @GetMapping("/summary")
    @Cacheable(value = "statistics", key = "'summary'")
    public Result<Map<String, Object>> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("residentCount", residentService.count());
        summary.put("householdCount", householdService.count());
        summary.put("educationCount", educationService.count());
        summary.put("medicalCount", medicalService.count());
        summary.put("employmentCount", employmentService.count());
        summary.put("socialSecurityCount", socialSecurityService.count());
        summary.put("propertyCount", propertyService.count());
        summary.put("vehicleCount", vehicleService.count());
        summary.put("updateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return success(summary);
    }
    
    /**
     * 获取各地区人口统计
     */
    @GetMapping("/population-by-region")
    @Cacheable(value = "statistics", key = "'populationByRegion'")
    public Result<Map<String, Object>> getPopulationByRegion() {
        // 使用优化后的SQL查询获取各地区人口统计
        List<Map<String, Object>> regionPopulationList = householdService.countPopulationByRegion();
        
        // 转换为前端需要的格式
        List<String> regions = new ArrayList<>();
        List<Integer> populations = new ArrayList<>();
        
        for (Map<String, Object> regionData : regionPopulationList) {
            regions.add((String) regionData.get("region"));
            populations.add(((Number) regionData.get("population")).intValue());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("regions", regions);
        result.put("populations", populations);
        return success(result);
    }
    
    /**
     * 获取人口增长趋势（近二十年）
     */
    @GetMapping("/population-trend")
    @Cacheable(value = "statistics", key = "'populationTrend'")
    public Result<Map<String, Object>> getPopulationTrend() {
        // 从数据库获取真实数据
        List<Resident> residents = residentService.findAll();
        
        // 统计各年份的出生人口
        Map<Integer, Map<String, Integer>> yearPopulationMap = new TreeMap<>();
        
        for (Resident resident : residents) {
            if (resident.getBirthDate() != null) {
                try {
                    // 将 Date 转换为 LocalDate
                    java.util.Date birthDate = resident.getBirthDate();
                    LocalDate localBirthDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    int birthYear = localBirthDate.getYear();
                    
                    // 初始化年份数据
                    yearPopulationMap.putIfAbsent(birthYear, new HashMap<>());
                    Map<String, Integer> yearData = yearPopulationMap.get(birthYear);
                    yearData.putIfAbsent("total", 0);
                    yearData.putIfAbsent("male", 0);
                    yearData.putIfAbsent("female", 0);
                    
                    // 统计人口
                    yearData.put("total", yearData.get("total") + 1);
                    if ("男".equals(resident.getGender())) {
                        yearData.put("male", yearData.get("male") + 1);
                    } else if ("女".equals(resident.getGender())) {
                        yearData.put("female", yearData.get("female") + 1);
                    }
                } catch (Exception e) {
                    // 跳过无效的出生日期
                    continue;
                }
            }
        }
        
        // 转换为前端需要的格式，只保留近二十年数据
        List<String> years = new ArrayList<>();
        List<Integer> totalData = new ArrayList<>();
        List<Integer> maleData = new ArrayList<>();
        List<Integer> femaleData = new ArrayList<>();
        
        // 获取当前年份和近二十年范围
        int currentYear = LocalDate.now().getYear();
        int startYear = currentYear - 19; // 近二十年：当前年份-19 到 当前年份
        
        // 遍历年份，只保留近二十年的数据
        for (Map.Entry<Integer, Map<String, Integer>> entry : yearPopulationMap.entrySet()) {
            int year = entry.getKey();
            if (year >= startYear && year <= currentYear) {
                years.add(String.valueOf(year));
                Map<String, Integer> yearData = entry.getValue();
                totalData.add(yearData.get("total"));
                maleData.add(yearData.get("male"));
                femaleData.add(yearData.get("female"));
            }
        }
        
        // 如果近二十年没有数据，返回空列表
        if (years.isEmpty()) {
            years = new ArrayList<>();
            totalData = new ArrayList<>();
            maleData = new ArrayList<>();
            femaleData = new ArrayList<>();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("years", years);
        result.put("totalData", totalData);
        result.put("maleData", maleData);
        result.put("femaleData", femaleData);
        return success(result);
    }
    
    /**
     * 获取年龄分布
     */
    @GetMapping("/age-distribution")
    @Cacheable(value = "statistics", key = "'ageDistribution'")
    public Result<List<Map<String, Object>>> getAgeDistribution() {
        // 从数据库获取真实数据
        List<Resident> residents = residentService.findAll();
        
        // 统计各年龄段人数
        int age0_18 = 0;
        int age19_35 = 0;
        int age36_60 = 0;
        int age60Plus = 0;
        
        LocalDate now = LocalDate.now();
        for (Resident resident : residents) {
            if (resident.getBirthDate() != null) {
                try {
                    // 将 Date 转换为 LocalDate
                    java.util.Date birthDate = resident.getBirthDate();
                    LocalDate localBirthDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    int age = Period.between(localBirthDate, now).getYears();
                    // 数据验证：确保年龄为正数
                    if (age >= 0) {
                        if (age <= 18) {
                            age0_18++;
                        } else if (age <= 35) {
                            age19_35++;
                        } else if (age <= 60) {
                            age36_60++;
                        } else {
                            age60Plus++;
                        }
                    }
                } catch (Exception e) {
                    // 跳过无效的出生日期
                    continue;
                }
            }
        }
        
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(createPieData("0-18岁", age0_18));
        data.add(createPieData("19-35岁", age19_35));
        data.add(createPieData("36-60岁", age36_60));
        data.add(createPieData("60岁以上", age60Plus));
        return success(data);
    }
    
    /**
     * 获取行业分布
     */
    @GetMapping("/industry-distribution")
    @Cacheable(value = "statistics", key = "'industryDistribution'")
    public Result<List<Map<String, Object>>> getIndustryDistribution() {
        // 从数据库获取真实数据
        List<Employment> employmentList = employmentService.findAll();
        
        // 统计各行业人数
        Map<String, Integer> industryMap = new HashMap<>();
        
        for (Employment employment : employmentList) {
            String industry = employment.getIndustry();
            if (industry != null) {
                industryMap.put(industry, industryMap.getOrDefault(industry, 0) + 1);
            }
        }
        
        // 转换为前端需要的格式
        List<Map<String, Object>> data = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : industryMap.entrySet()) {
            data.add(createPieData(entry.getKey(), entry.getValue()));
        }
        
        return success(data);
    }
    
    /**
     * 获取就业状态分布
     */
    @GetMapping("/employment-status-distribution")
    @Cacheable(value = "statistics", key = "'employmentStatusDistribution'")
    public Result<List<Map<String, Object>>> getEmploymentStatusDistribution() {
        // 从数据库获取真实数据
        List<Employment> employmentList = employmentService.findAll();
        
        // 初始化所有三种就业状态，确保即使没有数据也能显示
        Map<String, Integer> statusMap = new HashMap<>();
        statusMap.put("在职", 0);
        statusMap.put("失业", 0);
        statusMap.put("退休", 0);
        
        // 统计各就业状态人数
        for (Employment employment : employmentList) {
            String status = employment.getEmploymentStatus();
            if (status != null) {
                statusMap.put(status, statusMap.getOrDefault(status, 0) + 1);
            }
        }
        
        // 转换为前端需要的格式
        List<Map<String, Object>> data = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : statusMap.entrySet()) {
            data.add(createPieData(entry.getKey(), entry.getValue()));
        }
        
        return success(data);
    }
    
    /**
     * 获取社保参保情况
     */
    @GetMapping("/social-security-coverage")
    @Cacheable(value = "statistics", key = "'socialSecurityCoverage'")
    public Result<Map<String, Object>> getSocialSecurityCoverage() {
        // 从数据库获取真实数据
        List<SocialSecurity> socialSecurityList = socialSecurityService.findAll();
        int totalResidents = residentService.count();
        
        // 统计各社保类型的参保人数
        Map<String, Integer> insuranceTypeCountMap = new HashMap<>();
        insuranceTypeCountMap.put("养老保险", 0);
        insuranceTypeCountMap.put("医疗保险", 0);
        insuranceTypeCountMap.put("失业保险", 0);
        insuranceTypeCountMap.put("工伤保险", 0);
        insuranceTypeCountMap.put("生育保险", 0);
        
        for (SocialSecurity socialSecurity : socialSecurityList) {
            if ("正常参保".equals(socialSecurity.getInsuranceStatus())) {
                // 真实统计各社保类型的参保人数
                if (socialSecurity.getPensionInsurance() != null) {
                    insuranceTypeCountMap.put("养老保险", insuranceTypeCountMap.get("养老保险") + 1);
                }
                if (socialSecurity.getMedicalInsurance() != null) {
                    insuranceTypeCountMap.put("医疗保险", insuranceTypeCountMap.get("医疗保险") + 1);
                }
                if (socialSecurity.getUnemploymentInsurance() != null) {
                    insuranceTypeCountMap.put("失业保险", insuranceTypeCountMap.get("失业保险") + 1);
                }
                if (socialSecurity.getWorkInjuryInsurance() != null) {
                    insuranceTypeCountMap.put("工伤保险", insuranceTypeCountMap.get("工伤保险") + 1);
                }
                if (socialSecurity.getMaternityInsurance() != null) {
                    insuranceTypeCountMap.put("生育保险", insuranceTypeCountMap.get("生育保险") + 1);
                }
            }
        }
        
        // 计算参保率
        List<String> types = Arrays.asList("养老保险", "医疗保险", "失业保险", "工伤保险", "生育保险");
        List<Integer> coverageRates = new ArrayList<>();
        
        for (String type : types) {
            int count = insuranceTypeCountMap.get(type);
            int coverageRate = totalResidents > 0 ? (int) Math.round((double) count / totalResidents * 100) : 0;
            coverageRates.add(coverageRate);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("types", types);
        result.put("coverageRates", coverageRates);
        // 添加参保人数数据，用于更丰富的图表展示
        List<Integer> insuranceCounts = new ArrayList<>();
        for (String type : types) {
            insuranceCounts.add(insuranceTypeCountMap.get(type));
        }
        result.put("insuranceCounts", insuranceCounts);
        return success(result);
    }
    
    /**
     * 获取房产类型分布
     */
    @GetMapping("/property-type-distribution")
    @Cacheable(value = "statistics", key = "'propertyTypeDistribution'")
    public Result<List<Map<String, Object>>> getPropertyTypeDistribution() {
        // 从数据库获取真实数据
        List<Property> propertyList = propertyService.findAll();
        
        // 统计各房产类型的数量
        Map<String, Integer> propertyTypeMap = new HashMap<>();
        
        for (Property property : propertyList) {
            String propertyType = property.getPropertyType();
            if (propertyType != null) {
                propertyTypeMap.put(propertyType, propertyTypeMap.getOrDefault(propertyType, 0) + 1);
            }
        }
        
        // 转换为前端需要的格式
        List<Map<String, Object>> data = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : propertyTypeMap.entrySet()) {
            data.add(createPieData(entry.getKey(), entry.getValue()));
        }
        
        return success(data);
    }
    
    /**
     * 获取车辆品牌分布
     */
    @GetMapping("/vehicle-brand-distribution")
    @Cacheable(value = "statistics", key = "'vehicleBrandDistribution'")
    public Result<List<Map<String, Object>>> getVehicleBrandDistribution() {
        // 从数据库获取真实数据
        List<Vehicle> vehicleList = vehicleService.findAll();
        
        // 统计各车辆品牌的数量
        Map<String, Integer> vehicleBrandMap = new HashMap<>();
        
        for (Vehicle vehicle : vehicleList) {
            String brand = vehicle.getBrand();
            if (brand != null) {
                vehicleBrandMap.put(brand, vehicleBrandMap.getOrDefault(brand, 0) + 1);
            }
        }
        
        // 转换为前端需要的格式
        List<Map<String, Object>> data = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : vehicleBrandMap.entrySet()) {
            data.add(createPieData(entry.getKey(), entry.getValue()));
        }
        
        return success(data);
    }
    
    /**
     * 获取教育程度分布
     */
    @GetMapping("/education-level")
    @Cacheable(value = "statistics", key = "'educationLevel'")
    public Result<Map<String, Object>> getEducationLevel() {
        // 从数据库获取真实数据
        List<Education> educationList = educationService.findAll();
        List<Resident> residents = residentService.findAll();
        
        // 创建居民ID到性别的映射
        Map<Long, String> residentGenderMap = new HashMap<>();
        for (Resident resident : residents) {
            residentGenderMap.put(resident.getId(), resident.getGender());
        }
        
        // 统计各教育程度的男女人数
        Map<String, Integer> maleEducationMap = new HashMap<>();
        Map<String, Integer> femaleEducationMap = new HashMap<>();
        
        // 初始化教育程度列表
        List<String> levels = Arrays.asList("小学", "初中", "高中", "大专", "本科", "硕士", "博士");
        for (String level : levels) {
            maleEducationMap.put(level, 0);
            femaleEducationMap.put(level, 0);
        }
        
        for (Education education : educationList) {
            String gender = residentGenderMap.getOrDefault(education.getResidentId(), "");
            String educationLevel = education.getEducationLevel();
            
            if (educationLevel != null) {
                if ("男".equals(gender)) {
                    maleEducationMap.put(educationLevel, maleEducationMap.getOrDefault(educationLevel, 0) + 1);
                } else if ("女".equals(gender)) {
                    femaleEducationMap.put(educationLevel, femaleEducationMap.getOrDefault(educationLevel, 0) + 1);
                }
            }
        }
        
        // 转换为前端需要的格式
        List<Integer> maleData = new ArrayList<>();
        List<Integer> femaleData = new ArrayList<>();
        for (String level : levels) {
            maleData.add(maleEducationMap.getOrDefault(level, 0));
            femaleData.add(femaleEducationMap.getOrDefault(level, 0));
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("levels", levels);
        result.put("maleData", maleData);
        result.put("femaleData", femaleData);
        return success(result);
    }
    
    /**
     * 获取最新数据
     */
    @GetMapping("/latest-data")
    public Result<List<Map<String, Object>>> getLatestData() {
        // 模拟最新数据，实际项目中应该从数据库获取
        List<Map<String, Object>> latestData = new ArrayList<>();
        
        // 从各表获取所有数据，然后排序取最新的几条
        List<Resident> allResidents = residentService.findAll();
        List<Household> allHouseholds = householdService.findAll();
        List<Employment> allEmployments = employmentService.findAll();
        List<Education> allEducations = educationService.findAll();
        List<Medical> allMedicals = medicalService.findAll();
        List<SocialSecurity> allSocialSecurities = socialSecurityService.findAll();
        List<Property> allProperties = propertyService.findAll();
        List<Vehicle> allVehicles = vehicleService.findAll();
        
        // 转换为统一格式并添加到列表中
        for (Resident resident : allResidents) {
            Map<String, Object> data = new HashMap<>();
            if (resident.getCreateTime() != null) {
                data.put("date", resident.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                data.put("name", resident.getName());
                data.put("type", "新增居民");
                data.put("content", "新增居民信息：" + resident.getName() + "，身份证号：" + resident.getIdCard());
                data.put("status", "已完成");
                data.put("createTime", resident.getCreateTime()); // 添加完整的创建时间用于排序
                latestData.add(data);
            }
        }
        
        for (Household household : allHouseholds) {
            Map<String, Object> data = new HashMap<>();
            if (household.getCreateTime() != null) {
                data.put("date", household.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                data.put("name", household.getHouseholderName());
                data.put("type", "新增户籍");
                data.put("content", "新增户籍信息：" + household.getHouseholderName() + "，地址：" + household.getAddress());
                data.put("status", "已完成");
                data.put("createTime", household.getCreateTime()); // 添加完整的创建时间用于排序
                latestData.add(data);
            }
        }
        
        for (Employment employment : allEmployments) {
            Map<String, Object> data = new HashMap<>();
            if (employment.getCreateTime() != null) {
                data.put("date", employment.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                data.put("name", employment.getResidentName());
                data.put("type", "就业登记");
                data.put("content", "就业登记：" + employment.getResidentName() + "，单位：" + employment.getCompany());
                data.put("status", "已完成");
                data.put("createTime", employment.getCreateTime()); // 添加完整的创建时间用于排序
                latestData.add(data);
            }
        }
        
        for (Education education : allEducations) {
            Map<String, Object> data = new HashMap<>();
            if (education.getCreateTime() != null) {
                data.put("date", education.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                data.put("name", education.getResidentName());
                data.put("type", "教育信息");
                data.put("content", "教育信息：" + education.getResidentName() + "，学历：" + education.getEducationLevel());
                data.put("status", "已完成");
                data.put("createTime", education.getCreateTime()); // 添加完整的创建时间用于排序
                latestData.add(data);
            }
        }
        
        for (Medical medical : allMedicals) {
            Map<String, Object> data = new HashMap<>();
            if (medical.getCreateTime() != null) {
                data.put("date", medical.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                data.put("name", medical.getResidentName());
                data.put("type", "医疗信息");
                data.put("content", "医疗信息：" + medical.getResidentName() + "，医保类型：" + medical.getInsuranceType());
                data.put("status", "已完成");
                data.put("createTime", medical.getCreateTime()); // 添加完整的创建时间用于排序
                latestData.add(data);
            }
        }
        
        for (SocialSecurity socialSecurity : allSocialSecurities) {
            Map<String, Object> data = new HashMap<>();
            if (socialSecurity.getCreateTime() != null) {
                data.put("date", socialSecurity.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                data.put("name", socialSecurity.getResidentName());
                data.put("type", "社保变更");
                data.put("content", "社保变更：" + socialSecurity.getResidentName() + "，状态：" + socialSecurity.getInsuranceStatus());
                data.put("status", "已完成");
                data.put("createTime", socialSecurity.getCreateTime()); // 添加完整的创建时间用于排序
                latestData.add(data);
            }
        }
        
        for (Property property : allProperties) {
            Map<String, Object> data = new HashMap<>();
            if (property.getCreateTime() != null) {
                data.put("date", property.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                data.put("name", property.getResidentName());
                data.put("type", "房产信息");
                data.put("content", "房产信息：" + property.getResidentName() + "，类型：" + property.getPropertyType());
                data.put("status", "已完成");
                data.put("createTime", property.getCreateTime()); // 添加完整的创建时间用于排序
                latestData.add(data);
            }
        }
        
        for (Vehicle vehicle : allVehicles) {
            Map<String, Object> data = new HashMap<>();
            if (vehicle.getCreateTime() != null) {
                data.put("date", vehicle.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                data.put("name", vehicle.getResidentName());
                data.put("type", "车辆信息");
                data.put("content", "车辆信息：" + vehicle.getResidentName() + "，品牌：" + vehicle.getBrand());
                data.put("status", "已完成");
                data.put("createTime", vehicle.getCreateTime()); // 添加完整的创建时间用于排序
                latestData.add(data);
            }
        }
        
        // 按创建时间降序排序，确保最新的数据排在前面
        latestData.sort((a, b) -> {
            LocalDateTime timeA = (LocalDateTime) a.get("createTime");
            LocalDateTime timeB = (LocalDateTime) b.get("createTime");
            return timeB.compareTo(timeA); // 降序排序
        });
        
        // 限制返回数量
        if (latestData.size() > 10) {
            latestData = latestData.subList(0, 10);
        }
        
        return success(latestData);
    }
    
    /**
     * 获取医疗保险类型分布
     */
    @GetMapping("/medical-insurance")
    @Cacheable(value = "statistics", key = "'medicalInsurance'")
    public Result<List<Map<String, Object>>> getMedicalInsurance() {
        // 从社保表获取医疗保险类型分布，确保和社保管理中的医疗保险类型一致
        List<SocialSecurity> socialSecurityList = socialSecurityService.findAll();
        
        // 统计各医疗保险类型的人数，统一使用规范的医疗保险类型名称
        Map<String, Integer> insuranceTypeMap = new HashMap<>();
        
        for (SocialSecurity socialSecurity : socialSecurityList) {
            String medicalInsurance = socialSecurity.getMedicalInsurance();
            if (medicalInsurance != null) {
                // 统一医疗保险类型名称，将"城镇居民医疗保险"和"新农合医疗保险"合并为"城乡居民医疗保险"
                String standardizedType = medicalInsurance;
                if ("城镇居民医疗保险".equals(medicalInsurance) || "新农合医疗保险".equals(medicalInsurance)) {
                    standardizedType = "城乡居民医疗保险";
                }
                insuranceTypeMap.put(standardizedType, insuranceTypeMap.getOrDefault(standardizedType, 0) + 1);
            }
        }
        
        // 转换为前端需要的格式
        List<Map<String, Object>> data = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : insuranceTypeMap.entrySet()) {
            data.add(createPieData(entry.getKey(), entry.getValue()));
        }
        
        return success(data);
    }
    
    /**
     * 获取房产类型列表
     */
    @GetMapping("/property-types")
    @Cacheable(value = "statistics", key = "'propertyTypes'")
    public Result<List<String>> getPropertyTypes() {
        // 从房产表获取所有房产类型
        List<Property> propertyList = propertyService.findAll();
        Set<String> propertyTypes = new HashSet<>();
        
        for (Property property : propertyList) {
            if (property.getPropertyType() != null) {
                propertyTypes.add(property.getPropertyType());
            }
        }
        
        // 转换为列表并返回
        List<String> types = new ArrayList<>(propertyTypes);
        return success(types);
    }
    
    /**
     * 获取社保类型列表
     */
    @GetMapping("/social-security-types")
    @Cacheable(value = "statistics", key = "'socialSecurityTypes'")
    public Result<Map<String, List<String>>> getSocialSecurityTypes() {
        // 从社保表获取所有社保类型
        List<SocialSecurity> socialSecurityList = socialSecurityService.findAll();
        Set<String> pensionTypes = new HashSet<>();
        Set<String> medicalTypes = new HashSet<>();
        
        for (SocialSecurity socialSecurity : socialSecurityList) {
            if (socialSecurity.getPensionInsurance() != null) {
                pensionTypes.add(socialSecurity.getPensionInsurance());
            }
            if (socialSecurity.getMedicalInsurance() != null) {
                medicalTypes.add(socialSecurity.getMedicalInsurance());
            }
        }
        
        // 构建结果
        Map<String, List<String>> result = new HashMap<>();
        result.put("pensionTypes", new ArrayList<>(pensionTypes));
        result.put("medicalTypes", new ArrayList<>(medicalTypes));
        return success(result);
    }
    
    /**
     * 获取车辆类型列表
     */
    @GetMapping("/vehicle-types")
    @Cacheable(value = "statistics", key = "'vehicleTypes'")
    public Result<List<String>> getVehicleTypes() {
        // 从车辆表获取所有车辆类型
        List<Vehicle> vehicleList = vehicleService.findAll();
        Set<String> vehicleTypes = new HashSet<>();
        
        for (Vehicle vehicle : vehicleList) {
            if (vehicle.getVehicleType() != null) {
                vehicleTypes.add(vehicle.getVehicleType());
            }
        }
        
        // 转换为列表并返回
        List<String> types = new ArrayList<>(vehicleTypes);
        return success(types);
    }
    
    /**
     * 创建饼图数据
     */
    private Map<String, Object> createPieData(String name, int value) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("value", value);
        return data;
    }
}