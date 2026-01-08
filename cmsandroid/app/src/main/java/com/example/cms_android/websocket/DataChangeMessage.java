package com.example.cms_android.websocket;

import com.google.gson.annotations.SerializedName;

/**
 * 数据变更消息实体类
 * 用于解析和存储WebSocket接收到的数据变更通知消息
 *
 * 该类对应后端推送的JSON消息格式，包含数据变更的完整信息
 * 示例JSON格式：
 * {
 *   "entityType": "resident",
 *   "action": "create",
 *   "id": 123,
 *   "userId": 1,
 *   "timestamp": 1699876543000
 * }
 */
public class DataChangeMessage {

    /**
     * 实体类型，表示发生数据变更的业务对象类型
     * 如：resident(居民)、household(住户)、building(楼栋)、fee(费用)等
     */
    @SerializedName("entityType")
    private String entityType;

    /**
     * 操作类型，表示具体的操作行为
     * 如：create(新增)、update(修改)、delete(删除)
     */
    @SerializedName("action")
    private String action;

    /**
     * 变更数据的唯一标识ID
     * 对应数据库中的主键值
     */
    @SerializedName("id")
    private Long id;

    /**
     * 触发数据变更的用户ID
     * 用于记录操作人信息
     */
    @SerializedName("userId")
    private Long userId;

    /**
     * 变更发生的时间戳
     * 格式：毫秒级Unix时间戳
     */
    @SerializedName("timestamp")
    private Long timestamp;

    /**
     * 默认无参构造函数
     * 用于GSON反序列化时创建对象实例
     */
    public DataChangeMessage() {
    }

    /**
     * 带参构造函数
     * 用于快速创建数据变更消息对象
     *
     * @param entityType 实体类型
     * @param action 操作类型
     * @param id 变更数据的ID
     * @param userId 操作用户ID
     * @param timestamp 变更时间戳
     */
    public DataChangeMessage(String entityType, String action, Long id, Long userId, Long timestamp) {
        this.entityType = entityType;
        this.action = action;
        this.id = id;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    /**
     * 获取实体类型
     * @return 实体类型字符串
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * 设置实体类型
     * @param entityType 实体类型字符串
     */
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    /**
     * 获取操作类型
     * @return 操作类型字符串
     */
    public String getAction() {
        return action;
    }

    /**
     * 设置操作类型
     * @param action 操作类型字符串
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * 获取变更数据的ID
     * @return 数据ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置变更数据的ID
     * @param id 数据ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取操作用户ID
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置操作用户ID
     * @param userId 用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取变更时间戳
     * @return 时间戳（毫秒）
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * 设置变更时间戳
     * @param timestamp 时间戳（毫秒）
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 获取对象的字符串表示
     * 用于调试和日志输出
     * @return 格式化的字符串描述
     */
    @Override
    public String toString() {
        return "DataChangeMessage{" +
                "entityType='" + entityType + '\'' +
                ", action='" + action + '\'' +
                ", id=" + id +
                ", userId=" + userId +
                ", timestamp=" + timestamp +
                '}';
    }
}