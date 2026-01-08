package com.example.cms_android.websocket;

import android.util.Log;

import com.example.cms_android.ListActivity.HouseholdListActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据变更事件处理器
 * 负责接收、解析和分发来自WebSocket的数据变更通知
 *
 * 该类采用发布-订阅模式，提供数据变更事件的统一处理机制
 * 当后端有数据变更时，通过WebSocket推送消息，前端接收后
 * 通过此处理器分发给各个关注数据变更的组件
 *
 * 工作流程：
 * 1. WebSocketManager收到消息后调用handleMessage()
 * 2. handleMessage()解析消息内容，提取module和operation
 * 3. 遍历所有注册的监听器，调用onDataChanged()方法
 * 4. 各监听器收到通知后执行相应的刷新逻辑
 *
 * 示例消息格式：
 * {
 *   "module": "resident",
 *   "operation": "create"
 * }
 */
public class DataChangeHandler {

    /** 日志标签，用于区分日志输出 */
    private static final String TAG = "DataChangeHandler";

    /** 单例实例，确保全局只有一个处理器 */
    private static DataChangeHandler instance;

    /** JSON解析器，用于解析WebSocket消息 */
    private Gson gson = new Gson();

    /** 数据变更监听器列表，支持多个组件同时监听 */
    private List<DataChangeListener> listeners = new ArrayList<>();

    /**
     * 获取单例实例
     * 采用懒汉式加载，第一次调用时创建实例
     *
     * @return DataChangeHandler单例实例
     */
    public static synchronized DataChangeHandler getInstance() {
        if (instance == null) {
            instance = new DataChangeHandler();
        }
        return instance;
    }

    /**
     * 私有构造函数
     * 防止外部直接创建实例，确保单例模式
     */
    private DataChangeHandler() {
    }

    /**
     * 处理WebSocket接收到的消息
     * 解析消息内容，判断是否为数据变更事件，并通知监听器
     *
     * 处理逻辑：
     * 1. 将消息字符串解析为JsonObject
     * 2. 检查是否包含module和operation字段
     * 3. 如果是数据变更事件，遍历所有监听器并通知
     * 4. 解析失败时记录错误日志
     *
     * @param message WebSocket接收到的原始消息字符串
     */
    public void handleMessage(String message) {
        try {
            // 将JSON字符串解析为JsonObject对象
            JsonObject jsonObject = gson.fromJson(message, JsonObject.class);

            // 检查是否是数据变更事件（必须包含module和operation字段）
            if (jsonObject.has("module") && jsonObject.has("operation")) {
                // 提取模块名称和操作类型
                String module = jsonObject.get("module").getAsString();
                String operation = jsonObject.get("operation").getAsString();

                // 记录日志，方便调试
                Log.d(TAG, "收到数据变更事件: module=" + module + ", operation=" + operation);

                // 遍历所有已注册的监听器，通知数据变更
                for (DataChangeListener listener : listeners) {
                    listener.onDataChanged(module, operation);
                }
            }
        } catch (JsonSyntaxException e) {
            // JSON解析异常，记录错误日志但不抛出异常
            Log.e(TAG, "解析WebSocket消息失败", e);
        }
    }

    /**
     * 注册数据变更监听器
     * 将监听器添加到监听列表中，以便接收数据变更通知
     *
     * 采用防止重复添加机制，同一个监听器只会注册一次
     *
     * @param listener 要注册的数据变更监听器
     */
    public void registerListener(DataChangeListener listener) {
        // 检查是否已存在，避免重复添加
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * 注销数据变更监听器
     * 将监听器从监听列表中移除，停止接收数据变更通知
     *
     * 建议在Activity/Fragment的onDestroy或onStop中调用
     * 防止内存泄漏和无效的通知
     *
     * @param listener 要注销的数据变更监听器
     */
    public void unregisterListener(DataChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * 数据变更监听器接口
     * 定义数据变更事件的回调方法
     *
     * 实现此接口的类可以接收数据变更通知
     * 典型实现场景     * -：
 列表页面刷新数据
     * - 详情页面更新显示
     * - 缓存失效处理
     */
    public interface DataChangeListener {

        /**
         * 数据变更回调方法
         * 当有数据发生变更时，此方法会被调用
         *
         * 实现者可以根据module和operation判断变更类型
         * 执行相应的业务逻辑，如刷新数据、显示提示等
         *
         * @param module 发生变更的模块名称（如resident、household等）
         * @param operation 操作类型（如create、update、delete）
         */
        void onDataChanged(String module, String operation);
    }
}