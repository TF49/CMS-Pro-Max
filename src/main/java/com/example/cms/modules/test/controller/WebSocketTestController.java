package com.example.cms.modules.test.controller;

import com.example.cms.websocket.DataChangeEvent;
import com.example.cms.websocket.WebSocketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * WebSocket测试控制器
 * 用于测试WebSocket消息广播功能
 */
@RestController
@RequestMapping("/api/test/websocket")
public class WebSocketTestController {

    @Autowired
    private WebSocketMessageService webSocketMessageService;

    /**
     * 发送测试WebSocket消息
     * @param module 模块名称
     * @param operation 操作类型
     * @param entityId 实体ID
     * @return 响应消息
     */
    @GetMapping("/broadcast")
    public String broadcastTestMessage(
            @RequestParam(defaultValue = "test") String module,
            @RequestParam(defaultValue = "UPDATE") String operation,
            @RequestParam(defaultValue = "1") Long entityId) {
        
        // 创建测试数据变更事件
        DataChangeEvent event = new DataChangeEvent();
        event.setModule(module);
        event.setOperation(operation);
        event.setEntityId(entityId);
        event.setCreatedBy(1L);
        event.setTimestamp(System.currentTimeMillis());
        event.setData("这是一条测试消息");
        
        // 广播消息
        webSocketMessageService.broadcastDataChange(event);
        
        return "WebSocket测试消息已广播: module=" + module + ", operation=" + operation + ", entityId=" + entityId;
    }

    /**
     * 发送测试消息给特定用户
     * @param userId 用户ID
     * @param module 模块名称
     * @return 响应消息
     */
    @GetMapping("/send-to-user")
    public String sendToUser(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "test") String module) {
        
        // 创建测试数据变更事件
        DataChangeEvent event = new DataChangeEvent();
        event.setModule(module);
        event.setOperation("NOTIFICATION");
        event.setEntityId(1L);
        event.setCreatedBy(1L);
        event.setTimestamp(System.currentTimeMillis());
        event.setData("这是一条发送给特定用户的测试消息");
        
        // 发送消息给特定用户
        webSocketMessageService.sendToUser(userId, event);
        
        return "WebSocket测试消息已发送给用户: userId=" + userId + ", module=" + module;
    }
}