package com.example.cms.websocket;

import com.example.cms.common.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import java.util.ArrayList;
import java.util.List;

/**
 * JWT WebSocket通道拦截器
 * 用于在WebSocket连接时验证JWT Token
 */
@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 在消息发送前拦截，用于JWT认证
     * @param message 消息
     * @param channel 通道
     * @return 处理后的消息
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        System.out.println("=== WebSocket消息拦截器触发 ===");
        
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null) {
            System.out.println("STOMP命令: " + accessor.getCommand());
            System.out.println("原生头部: " + accessor.toNativeHeaderMap());
            
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                System.out.println("处理CONNECT命令");
                String token = null;
                
                // 1. 尝试从STOMP头部提取JWT token
                System.out.println("1. 尝试从STOMP头部提取JWT token");
                String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
                System.out.println("Authorization头部: " + authorizationHeader);
                
                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    token = authorizationHeader.substring(7);
                    System.out.println("从Authorization头获取到token: " + token);
                } 
                // 2. 如果没有Authorization头部，尝试从其他来源提取token
                else {
                    System.out.println("2. Authorization头不存在或格式不正确，尝试从其他来源获取token");
                    
                    // 打印所有可用的头部
                    System.out.println("所有可用头部: " + accessor.toNativeHeaderMap());
                    
                    // 3. 尝试从连接URL中提取token（适用于原生WebSocket和SockJS）
                    System.out.println("3. 尝试从连接URL提取token");
                    
                    // 尝试从SockJS URL获取
                    String connectUrl = accessor.getFirstNativeHeader("sockjs.url");
                    System.out.println("SockJS URL: " + connectUrl);
                    
                    // 如果SockJS URL不存在，尝试从session attributes获取连接URL
                    if (connectUrl == null) {
                        System.out.println("尝试从session attributes获取连接URL");
                        Object sessionUrlObj = accessor.getSessionAttributes().get("org.springframework.web.socket.handler.WebSocketSessionUtils.CONNECT_URL");
                        if (sessionUrlObj != null) {
                            connectUrl = sessionUrlObj.toString();
                            System.out.println("从session attributes获取到URL: " + connectUrl);
                        }
                    }
                    
                    // 4. 直接从token头部获取（兼容小程序原生WebSocket）
                    if (connectUrl == null) {
                        System.out.println("4. 尝试直接从token头部获取");
                        String tokenHeader = accessor.getFirstNativeHeader("token");
                        if (tokenHeader != null) {
                            token = tokenHeader;
                            System.out.println("从token头获取到token: " + token);
                        }
                    }
                    
                    // 解析URL中的token参数
                    if (connectUrl != null) {
                        // 解析URL获取token参数
                        try {
                            String[] urlParts = connectUrl.split("\\?");
                            System.out.println("URL分割结果: " + Arrays.toString(urlParts));
                            if (urlParts.length > 1) {
                                String[] params = urlParts[1].split("&");
                                System.out.println("URL参数: " + Arrays.toString(params));
                                for (String param : params) {
                                    String[] keyValue = param.split("=");
                                    System.out.println("参数键值对: " + Arrays.toString(keyValue));
                                    if (keyValue.length > 1 && "token".equals(keyValue[0])) {
                                        token = keyValue[1];
                                        System.out.println("从URL参数获取到token: " + token);
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("解析URL参数失败: " + e.getMessage());
                        }
                    }
                    
                    // 4. 尝试从message headers中提取token
                    System.out.println("4. 尝试从message headers中提取token");
                    Object tokenObj = accessor.getSessionAttributes().get("token");
                    if (tokenObj != null) {
                        token = tokenObj.toString();
                        System.out.println("从session attributes获取到token: " + token);
                    }
                }

                // 验证token
                if (token != null) {
                    // 移除可能的引号
                    token = token.replace("\"", "");
                    System.out.println("最终token: " + token);
                    
                    try {
                        if (jwtUtils.validateToken(token)) {
                            // 从token中提取用户信息
                            Long userId = jwtUtils.getUserIdFromToken(token);
                            String username = jwtUtils.getUsernameFromToken(token);
                            String role = jwtUtils.getRoleFromToken(token);
                            
                            System.out.println("Token验证成功");
                            System.out.println("用户ID: " + userId);
                            System.out.println("用户名: " + username);
                            System.out.println("角色: " + role);

                            // 创建权限列表
                            List<GrantedAuthority> authorities = new ArrayList<>();
                            if (role != null) {
                                authorities.add(new SimpleGrantedAuthority(role));
                                System.out.println("创建权限列表: " + authorities);
                            }

                            // 创建认证对象
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                            System.out.println("创建认证对象: " + authentication);

                            // 设置用户信息到STOMP会话中
                            accessor.setUser(authentication);
                            System.out.println("设置用户信息到STOMP会话");

                            System.out.println("=== WebSocket连接认证成功: userId=" + userId + ", username=" + username + ", role=" + role + " ===");
                        } else {
                            System.out.println("=== WebSocket连接认证失败: Token无效，token=" + token + " ===");
                        }
                    } catch (Exception e) {
                        System.out.println("=== WebSocket连接认证异常: " + e.getMessage() + " ===");
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("=== WebSocket连接未提供Token ===");
                }
            } else {
                System.out.println("忽略非CONNECT命令: " + accessor.getCommand());
            }
        } else {
            System.out.println("无法获取StompHeaderAccessor");
        }

        System.out.println("=== WebSocket消息拦截器处理完成 ===");
        return message;
    }
}
