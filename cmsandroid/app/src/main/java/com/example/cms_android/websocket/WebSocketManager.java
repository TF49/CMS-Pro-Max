package com.example.cms_android.websocket;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.cms_android.websocket.DataChangeMessage;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * WebSocket连接管理器
 * 负责管理Android客户端与后端WebSocket服务器之间的长连接
 *
 * 主要功能：
 * 1. 建立和维持WebSocket连接
 * 2. 支持JWT Token认证
 * 3. 自动重连机制（最多5次重连）
 * 4. 消息发送和接收处理
 * 5. 区分普通消息和数据变更消息
 *
 * 使用方式：
 * 1. 调用getInstance()获取单例实例
 * 2. 调用connect(token)建立连接
 * 3. 设置监听器接收连接状态和消息
 * 4. 不使用时调用disconnect()断开连接
 *
 * 注意事项：
 * - 建议在Application或全局单例中管理
 * - Activity销毁时应断开连接
 * - 避免重复连接
 */
public class WebSocketManager {

    /** 日志标签，用于区分日志输出 */
    private static final String TAG = "WebSocketManager";

    /** WebSocket服务器基础地址，Android模拟器访问本地服务器的地址 */
    private static final String BASE_URL = "ws://10.0.2.2:9090";

    /** WebSocket客户端实例，负责实际的网络通信 */
    private WebSocketClient webSocketClient;

    /** JWT认证令牌，用于身份验证 */
    private String token;

    /** 连接状态标记，表示是否已成功连接 */
    private boolean isConnected = false;

    /** 连接中状态标记，防止重复连接请求 */
    private boolean isConnecting = false;

    /** 普通WebSocket事件监听器 */
    private WebSocketListener listener;

    /** 数据变更专用监听器 */
    private DataChangeListener dataChangeListener;

    /** JSON解析器，用于解析消息内容 */
    private Gson gson = new Gson();

    /** 主线程Handler，用于在主线程执行重连任务 */
    private Handler reconnectHandler = new Handler(Looper.getMainLooper());

    /** 重连任务Runnable */
    private Runnable reconnectRunnable;

    /** 当前重连次数 */
    private int reconnectAttempts = 0;

    /** 最大重连次数限制 */
    private static final int MAX_RECONNECT_ATTEMPTS = 5;

    /** 重连间隔时间（毫秒），5秒 */
    private static final long RECONNECT_INTERVAL = 5000;

    /** 单例实例 */
    private static WebSocketManager instance;

    /**
     * 获取单例实例
     * 采用线程安全的懒汉式加载
     *
     * @return WebSocketManager单例实例
     */
    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    /**
     * 私有构造函数
     * 防止外部直接创建实例，确保单例模式
     */
    private WebSocketManager() {
    }

    /**
     * 连接到WebSocket服务器
     *
     * 连接流程：
     * 1. 检查是否已连接或正在连接，避免重复连接
     * 2. 创建WebSocketClient并设置连接地址
     * 3. 添加Authorization认证头
     * 4. 建立连接
     *
     * 认证说明：
     * 通过在HTTP头中添加Bearer Token进行JWT认证
     *
     * @param token JWT认证令牌，从登录接口获取
     */
    public void connect(String token) {
        this.token = token;

        // 检查连接状态，避免重复连接
        if (isConnected || isConnecting) {
            Log.d(TAG, "WebSocket已连接或正在连接");
            return;
        }

        isConnecting = true;

        try {
            // 构建WebSocket连接地址
            URI uri = new URI(BASE_URL + "/ws");
            webSocketClient = new WebSocketClient(uri) {

                /**
                 * 连接成功建立时的回调
                 * @param handshake 服务器握手信息
                 */
                @Override
                public void onOpen(ServerHandshake handshake) {
                    Log.d(TAG, "WebSocket连接已打开");
                    isConnected = true;
                    isConnecting = false;
                    reconnectAttempts = 0; // 重置重连次数
                    if (listener != null) {
                        listener.onConnected();
                    }
                }

                /**
                 * 收到消息时的回调
                 * 根据消息类型分发给不同的监听器处理
                 * @param message 收到的消息内容
                 */
                @Override
                public void onMessage(String message) {
                    Log.d(TAG, "收到WebSocket消息: " + message);

                    // 尝试解析为DataChangeMessage
                    try {
                        DataChangeMessage dataChangeMessage = gson.fromJson(message, DataChangeMessage.class);
                        if (dataChangeMessage != null && dataChangeMessage.getEntityType() != null) {
                            // 如果是数据变更消息，通知数据变更监听器
                            if (dataChangeListener != null) {
                                dataChangeListener.onDataChange(dataChangeMessage);
                            }
                        } else {
                            // 如果不是数据变更消息，通知普通消息监听器
                            if (listener != null) {
                                listener.onMessage(message);
                            }
                        }
                    } catch (Exception e) {
                        // 如果解析失败，当作普通消息处理
                        Log.d(TAG, "消息不是DataChangeMessage格式，当作普通消息处理");
                        if (listener != null) {
                            listener.onMessage(message);
                        }
                    }
                }

                /**
                 * 连接关闭时的回调
                 * @param code 关闭状态码
                 * @param reason 关闭原因
                 * @param remote 是否由远程端关闭
                 */
                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(TAG, "WebSocket连接已关闭: " + reason);
                    isConnected = false;
                    isConnecting = false;
                    if (listener != null) {
                        listener.onDisconnected();
                    }

                    // 如果不是主动断开连接（remote为false），尝试重连
                    if (!remote) {
                        attemptReconnect();
                    }
                }

                /**
                 * 连接发生错误时的回调
                 * @param ex 异常对象
                 */
                @Override
                public void onError(Exception ex) {
                    Log.e(TAG, "WebSocket错误: ", ex);
                    isConnecting = false;
                    if (listener != null) {
                        listener.onError(ex);
                    }

                    // 出错时尝试重连
                    attemptReconnect();
                }
            };

            // 添加认证头（Bearer Token方式）
            if (token != null && !token.isEmpty()) {
                webSocketClient.addHeader("Authorization", "Bearer " + token);
            }

            // 开始建立连接
            webSocketClient.connect();
        } catch (URISyntaxException e) {
            Log.e(TAG, "WebSocket URI语法错误", e);
            isConnecting = false;
        }
    }

    /**
     * 尝试重新建立连接
     *
     * 重连策略：
     * 1. 检查重连次数是否超过最大值
     * 2. 如果未超过最大值，延迟5秒后尝试重连
     * 3. 每次重连次数+1
     *
     * 触发条件：
     * - 连接意外断开（网络问题、服务器异常等）
     * - 连接发生错误
     */
    private void attemptReconnect() {
        if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
            reconnectAttempts++;
            Log.d(TAG, "尝试重连 WebSocket，第 " + reconnectAttempts + " 次");

            // 创建重连任务
            reconnectRunnable = new Runnable() {
                @Override
                public void run() {
                    // 使用保存的token重新连接
                    if (token != null && !token.isEmpty()) {
                        connect(token);
                    }
                }
            };

            // 延迟指定时间后执行重连任务
            reconnectHandler.postDelayed(reconnectRunnable, RECONNECT_INTERVAL);
        } else {
            Log.d(TAG, "达到最大重连次数，停止重连");
        }
    }

    /**
     * 断开WebSocket连接
     *
     * 断开操作：
     * 1. 关闭WebSocket客户端
     * 2. 重置连接状态标志
     * 3. 取消待执行的重连任务
     * 4. 重置重连计数
     *
     * 适用场景：
     * - 用户退出登录
     * - Activity/Fragment销毁
     * - 应用进入后台
     */
    public void disconnect() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
        isConnected = false;
        isConnecting = false;

        // 取消重连任务，避免内存泄漏
        if (reconnectRunnable != null) {
            reconnectHandler.removeCallbacks(reconnectRunnable);
        }

        // 重置重连次数
        reconnectAttempts = 0;
    }

    /**
     * 发送消息到服务器
     *
     * 发送条件：
     * - WebSocket已成功连接
     * - 客户端实例不为null
     *
     * @param message 要发送的消息内容（JSON格式）
     */
    public void sendMessage(String message) {
        if (webSocketClient != null && isConnected) {
            webSocketClient.send(message);
        } else {
            Log.w(TAG, "WebSocket未连接，无法发送消息");
        }
    }

    /**
     * 检查当前连接状态
     *
     * @return true表示已连接，false表示未连接
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 设置WebSocket事件监听器
     * 用于接收连接状态变化和普通消息
     *
     * 建议在调用connect()之前设置监听器
     *
     * @param listener WebSocket事件监听器实例
     */
    public void setListener(WebSocketListener listener) {
        this.listener = listener;
    }

    /**
     * WebSocket事件监听器接口
     * 用于接收连接状态变化和普通消息通知
     *
     * 实现此接口的类可以监听：
     * - 连接成功/断开
     * - 收到普通消息
     * - 发生错误
     */
    public interface WebSocketListener {

        /**
         * 连接成功建立时调用
         */
        void onConnected();

        /**
         * 连接断开时调用
         */
        void onDisconnected();

        /**
         * 收到普通消息时调用
         * @param message 消息内容
         */
        void onMessage(String message);

        /**
         * 发生错误时调用
         * @param error 异常对象
         */
        void onError(Exception error);
    }

    /**
     * 数据变更监听器接口
     * 专门用于接收数据变更消息
     *
     * 与WebSocketListener的区别：
     * - WebSocketListener：接收所有消息
     * - DataChangeListener：只接收DataChangeMessage类型的消息
     */
    public interface DataChangeListener {

        /**
         * 收到数据变更消息时调用
         * @param message 数据变更消息对象
         */
        void onDataChange(DataChangeMessage message);
    }

    /**
     * 设置数据变更监听器
     * 专门用于接收DataChangeMessage类型的数据变更消息
     *
     * 如果需要专门处理数据变更，建议使用此监听器
     * 而不是WebSocketListener
     *
     * @param listener 数据变更监听器实例
     */
    public void setDataChangeListener(DataChangeListener listener) {
        this.dataChangeListener = listener;
    }
}