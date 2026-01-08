package com.example.cms.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器
 * 用于验证请求中的JWT令牌，解析用户信息并存储到SecurityContextHolder中
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 日志记录器
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 排除不需要拦截的请求
     * @param request HTTP请求
     * @return 是否不拦截该请求
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 排除不需要拦截的请求
        String path = request.getRequestURI();
        // 输出日志，查看请求路径
        logger.info("请求路径: {}", path);
        return path.equals("/ws/info") || 
               path.startsWith("/ws/") && path.contains("/info") ||
               path.startsWith("/auth/") || 
               path.startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中的Authorization字段
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        String role = null;

        // 检查Authorization头是否存在且格式正确
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // 提取JWT令牌
            token = authorizationHeader.substring(7);
            try {
                // 从令牌中获取用户名和角色
                username = jwtUtils.getUsernameFromToken(token);
                role = jwtUtils.getRoleFromToken(token);
            } catch (Exception e) {
                // 令牌解析失败，记录日志
                logger.error("JWT令牌解析失败: {}", e.getMessage());
            }
        }

        // 如果令牌有效，无论SecurityContextHolder中是否已有认证信息，都更新认证信息
        if (username != null) {
            // 验证令牌有效性
            if (jwtUtils.validateToken(token)) {
                // 从令牌中获取用户ID
                Long userId = jwtUtils.getUserIdFromToken(token);
                
                // 添加日志，输出解析出的角色值
                logger.info("解析出的角色值: {}", role);
                logger.info("创建的权限: {}", "ROLE_" + role);
                
                // 创建UserDetails对象，将用户ID存储到username字段中（临时解决方案）
                // 后续可以创建自定义UserDetails类来存储更多用户信息
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(String.valueOf(userId))
                        .password("")
                        .authorities("ROLE_" + role)
                        .build();
                
                // 添加日志，输出创建的UserDetails对象的权限列表
                logger.info("UserDetails权限列表: {}", userDetails.getAuthorities());

                // 创建认证令牌
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 将认证令牌存储到SecurityContextHolder中
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}
