package com.example.cms.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色验证注解
 * 用于检查用户是否具有指定角色
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRole {
    /**
     * 需要的角色列表
     */
    String[] value();
    
    /**
     * 角色之间的逻辑关系，默认为OR
     */
    Logical logical() default Logical.OR;
}
