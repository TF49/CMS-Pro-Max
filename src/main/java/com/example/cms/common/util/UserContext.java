package com.example.cms.common.util;

import com.example.cms.common.JwtUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户上下文工具类
 * 用于获取当前登录用户的信息，包括用户ID和角色
 */
public class UserContext {

    // 静态JwtUtils实例，通过Spring的ApplicationContext获取
    private static JwtUtils jwtUtils;

    /**
     * 设置JwtUtils实例
     */
    public static void setJwtUtils(JwtUtils jwtUtils) {
        UserContext.jwtUtils = jwtUtils;
    }

    /**
     * 获取当前登录用户的ID
     * 从SecurityContextHolder中获取用户ID，如果获取失败，则尝试从请求头中的JWT令牌获取
     */
    public static Long getCurrentUserId() {
        // 首先尝试从SecurityContextHolder中获取
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            // 从UserDetails中获取用户ID（临时解决方案，用户ID存储在username字段中）
            Object principal = authentication.getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                String userIdStr = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
                try {
                    return Long.parseLong(userIdStr);
                } catch (NumberFormatException e) {
                    // 如果解析失败，返回null
                    return null;
                }
            }
        }
        
        // 从请求头中获取JWT令牌，适用于小程序等无状态请求
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return getUserIdFromToken(token);
        }
        
        return null;
    }

    /**
     * 获取当前登录用户的角色
     * 从SecurityContextHolder中获取用户角色，如果获取失败，则尝试从请求头中的JWT令牌获取
     */
    public static Set<String> getCurrentUserRoles() {
        // 首先尝试从SecurityContextHolder中获取
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = new HashSet<>();
        
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                String role = authority.getAuthority();
                // 移除ROLE_前缀
                if (role.startsWith("ROLE_")) {
                    role = role.substring(5);
                }
                roles.add(role);
            }
        }
        
        // 如果从SecurityContextHolder中未获取到角色，尝试从JWT令牌中获取
        if (roles.isEmpty()) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String roleFromToken = getRoleFromToken(token);
                if (roleFromToken != null && !roleFromToken.trim().isEmpty()) {
                    roles.add(roleFromToken);
                }
            }
        }
        
        return roles;
    }

    /**
     * 判断当前用户是否为管理员
     */
    public static boolean isAdmin() {
        Set<String> roles = getCurrentUserRoles();
        // 不区分大小写检查角色
        for (String role : roles) {
            if ("ADMIN".equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前用户是否为普通用户
     */
    public static boolean isUser() {
        Set<String> roles = getCurrentUserRoles();
        // 不区分大小写检查角色
        for (String role : roles) {
            if ("USER".equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从JWT令牌中获取用户ID
     */
    public static Long getUserIdFromToken(String token) {
        if (jwtUtils == null) {
            return null;
        }
        return jwtUtils.getUserIdFromToken(token);
    }

    /**
     * 从JWT令牌中获取用户角色
     */
    public static String getRoleFromToken(String token) {
        if (jwtUtils == null) {
            return null;
        }
        return jwtUtils.getRoleFromToken(token);
    }
}
