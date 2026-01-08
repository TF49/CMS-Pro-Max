package com.example.cms.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据变更事件类
 * 用于封装数据变更信息，通过WebSocket推送给客户端
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataChangeEvent {

    /**
     * 模块名称
     * 例如: "resident", "household", "education", "employment",
     *      "medical", "socialSecurity", "property", "vehicle"
     */
    private String module;

    /**
     * 操作类型
     * INSERT - 新增
     * UPDATE - 更新
     * DELETE - 删除
     */
    private String operation;

    /**
     * 实体ID
     * 被操作的数据记录的ID
     */
    private Long entityId;

    /**
     * 创建者ID
     * 用于客户端进行角色过滤
     * 普通用户只能看到自己创建的数据变更
     */
    private Long createdBy;

    /**
     * 变更的数据对象（可选）
     * 可以包含完整的数据对象，方便客户端直接使用
     */
    private Object data;

    /**
     * 事件时间戳
     * 记录事件发生的时间
     */
    private Long timestamp;
}
