package com.example.cms.modules.population.strategy;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 数据策略工厂类
 * 用于根据数据类型获取对应的策略实现
 */
@Component
public class DataStrategyFactory {

    private final Map<String, DataStrategy> strategyMap;

    /**
     * 构造函数，自动注入所有DataStrategy实现类
     * @param strategyMap 策略映射，key为策略名称，value为策略实现
     */
    public DataStrategyFactory(Map<String, DataStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    /**
     * 根据数据类型获取对应的策略实现
     * @param type 数据类型
     * @return 数据策略实现
     */
    public DataStrategy getStrategy(String type) {
        // 根据类型获取对应的策略名称
        String strategyName = type + "DataStrategy";
        return strategyMap.get(strategyName);
    }
}
