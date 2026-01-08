package com.example.cms.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限验证注解
 * 用于检查用户是否具有指定权限
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    /**
     * 需要的权限列表
     */
    String[] value();
    
    /**
     * 权限之间的逻辑关系，默认为OR
     */
    Logical logical() default Logical.OR;
}
