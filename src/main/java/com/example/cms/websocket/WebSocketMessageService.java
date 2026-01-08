package com.example.cms.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * WebSocket消息服务
 * 用于向客户端推送数据变更通知
 */
@Service
public class WebSocketMessageService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 广播数据变更事件到所有连接的客户端
     * 客户端收到消息后根据角色进行过滤
     *
     * @param event 数据变更事件
     */
    public void broadcastDataChange(DataChangeEvent event) {
        try {
            // 发送到 /topic/data-changes 主题，所有订阅该主题的客户端都会收到
            messagingTemplate.convertAndSend("/topic/data-changes", event);
            System.out.println("WebSocket消息已广播: module=" + event.getModule() +
                             ", operation=" + event.getOperation() +
                             ", entityId=" + event.getEntityId());
        } catch (Exception e) {
            System.err.println("WebSocket消息广播失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 发送消息给特定用户
     *
     * @param userId 用户ID
     * @param event 数据变更事件
     */
    public void sendToUser(Long userId, DataChangeEvent event) {
        try {
            // 发送到特定用户的队列
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                event
            );
            System.out.println("WebSocket消息已发送给用户: userId=" + userId +
                             ", module=" + event.getModule());
        } catch (Exception e) {
            System.err.println("WebSocket消息发送失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
