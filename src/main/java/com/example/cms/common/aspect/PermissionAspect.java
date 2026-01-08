package com.example.cms.common.aspect;

import com.example.cms.common.annotation.RequiresPermission;
import com.example.cms.common.annotation.RequiresRole;
import com.example.cms.common.exception.PermissionDeniedException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 权限验证切面
 * 用于处理@RequiresRole和@RequiresPermission注解，检查用户角色和权限
 */
@Aspect
@Component
public class PermissionAspect {

    /**
     * 定义切点，拦截所有带有@RequiresRole或@RequiresPermission注解的方法
     */
    @Pointcut("@annotation(com.example.cms.common.annotation.RequiresRole) || @annotation(com.example.cms.common.annotation.RequiresPermission)")
    public void permissionPointcut() {
    }

    /**
     * 前置通知，在方法执行前检查权限
     */
    @Before("permissionPointcut()")
    public void checkPermission(JoinPoint joinPoint) {
        // 获取当前用户的认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new PermissionDeniedException("用户未登录");
        }

        // 获取当前方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 检查@RequiresRole注解
        if (method.isAnnotationPresent(RequiresRole.class)) {
            RequiresRole requiresRole = method.getAnnotation(RequiresRole.class);
            checkRole(authentication, requiresRole);
        }

        // 检查@RequiresPermission注解
        if (method.isAnnotationPresent(RequiresPermission.class)) {
            RequiresPermission requiresPermission = method.getAnnotation(RequiresPermission.class);
            checkPermission(authentication, requiresPermission);
        }
    }

    /**
     * 检查用户角色
     */
    private void checkRole(Authentication authentication, RequiresRole requiresRole) {
        // 获取用户拥有的角色
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Set<String> userRoles = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            userRoles.add(authority.getAuthority().replace("ROLE_", ""));
        }

        // 获取需要的角色
        String[] requiredRoles = requiresRole.value();
        com.example.cms.common.annotation.Logical logical = requiresRole.logical();

        // 检查角色是否匹配
        boolean hasRole = false;
        if (logical == com.example.cms.common.annotation.Logical.OR) {
            // OR逻辑：只要有一个角色匹配即可
            for (String role : requiredRoles) {
                if (userRoles.contains(role)) {
                    hasRole = true;
                    break;
                }
            }
        } else {
            // AND逻辑：必须所有角色都匹配
            hasRole = true;
            for (String role : requiredRoles) {
                if (!userRoles.contains(role)) {
                    hasRole = false;
                    break;
                }
            }
        }

        if (!hasRole) {
            throw new PermissionDeniedException("权限不足，需要角色：" + String.join(", ", requiredRoles));
        }
    }

    /**
     * 检查用户权限
     */
    private void checkPermission(Authentication authentication, RequiresPermission requiresPermission) {
        // 获取用户拥有的权限
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Set<String> userPermissions = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            userPermissions.add(authority.getAuthority());
        }

        // 获取需要的权限
        String[] requiredPermissions = requiresPermission.value();
        com.example.cms.common.annotation.Logical logical = requiresPermission.logical();

        // 检查权限是否匹配
        boolean hasPermission = false;
        if (logical == com.example.cms.common.annotation.Logical.OR) {
            // OR逻辑：只要有一个权限匹配即可
            for (String permission : requiredPermissions) {
                if (userPermissions.contains(permission)) {
                    hasPermission = true;
                    break;
                }
            }
        } else {
            // AND逻辑：必须所有权限都匹配
            hasPermission = true;
            for (String permission : requiredPermissions) {
                if (!userPermissions.contains(permission)) {
                    hasPermission = false;
                    break;
                }
            }
        }

        if (!hasPermission) {
            throw new PermissionDeniedException("权限不足，需要权限：" + String.join(", ", requiredPermissions));
        }
    }
}
