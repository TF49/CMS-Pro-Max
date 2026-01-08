package com.example.cms.framework.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Around("execution(* com.example.cms.controller..*(..))")
    public Object logControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        Object result = null;
        try {
            logger.info("开始执行方法: {}", methodName);
            result = joinPoint.proceed();
            return result;
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("方法 {} 执行完成，耗时: {} ms", methodName, endTime - startTime);
        }
    }
}