package com.example.cms.config;

import com.example.cms.websocket.JwtChannelInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtChannelInterceptor jwtChannelInterceptor;
    
    /**
     * 添加TaskScheduler配置，用于处理心跳定时任务
     * @return TaskScheduler实例
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("ws-heartbeat-");
        return taskScheduler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 配置消息代理，用于处理WebSocket消息
        config.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(new long[] {30000, 30000}) // 添加心跳配置，每30秒发送一次心跳
                .setTaskScheduler(this.taskScheduler()); // 显式设置TaskScheduler
        // 设置应用程序目标前缀
        config.setApplicationDestinationPrefixes("/app");
        // 设置用户目标前缀
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册WebSocket端点，用于客户端连接
        System.out.println("注册WebSocket端点: /ws");
        // 同时支持原生WebSocket和SockJS连接
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://172.18.215.2:8080", "http://localhost:8080", "http://127.0.0.1:8080")
                .setAllowedOrigins("*");
        // 添加SockJS支持，用于不支持原生WebSocket的客户端
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://172.18.215.2:8080", "http://localhost:8080", "http://127.0.0.1:8080")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration)
    {
        // 添加JWT拦截器，用于验证WebSocket连接
        registration.interceptors(jwtChannelInterceptor);
    }
}