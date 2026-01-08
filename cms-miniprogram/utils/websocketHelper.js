// WebSocket连接助手
// 提供更完善的WebSocket连接管理功能

// 使用单例模式确保全局只有一个WebSocket实例
let instance = null;

class WebSocketHelper {
  constructor() {
    // 单例模式实现
    if (instance) {
      return instance;
    }
    instance = this;
    
    this.connection = null;
    this.isConnected = false;
    this.isConnecting = false;
    this.reconnectAttempts = 0;
    this.maxReconnectAttempts = 5;
    this.reconnectDelay = 3000;
    this.heartbeatTimer = null;
    this.reconnectTimer = null;
    this.connectionCount = 0; // 连接计数器
  }

  /**
   * 建立WebSocket连接
   * @param {string} url - WebSocket服务器地址
   * @param {string} token - JWT令牌
   * @returns {Promise}
   */
  connect(url, token) {
    return new Promise((resolve, reject) => {
      // 检查连接数限制
      if (this.connectionCount >= 2) {
        console.warn('WebSocket连接数已达上限(2个)，无法创建新连接');
        reject(new Error('WebSocket连接数已达上限'));
        return;
      }
      
      // 如果已经连接或正在连接，则直接返回
      if (this.isConnected || this.isConnecting) {
        console.log('WebSocket已连接或正在连接中');
        resolve();
        return;
      }

      // 清除之前的重连定时器
      if (this.reconnectTimer) {
        clearTimeout(this.reconnectTimer);
        this.reconnectTimer = null;
      }

      this.isConnecting = true;
      // 保存当前连接的URL，用于重连
      this.currentUrl = url;
      const fullUrl = `${url}?token=${token}`;
      
      console.log('尝试连接WebSocket:', fullUrl);

      try {
        // 如果已有连接，先关闭
        if (this.connection) {
          this.closeConnection(1000, 'Reconnecting');
        }

        this.connection = uni.connectSocket({
          url: fullUrl,
          complete: () => {}
        });
        
        // 增加连接计数
        this.connectionCount++;

        this.connection.onOpen(() => {
          console.log('WebSocket连接已打开');
          this.isConnected = true;
          this.isConnecting = false;
          this.reconnectAttempts = 0;
          this.startHeartbeat();
          resolve();
        });

        this.connection.onClose((res) => {
          console.log('WebSocket连接已关闭:', res);
          this.isConnected = false;
          this.isConnecting = false;
          this.stopHeartbeat();
          
          // 减少连接计数
          if (this.connectionCount > 0) {
            this.connectionCount--;
          }
          
          // 清理连接对象
          this.connection = null;
          
          // 如果不是主动断开且重连次数未达上限，则尝试重连
          if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.scheduleReconnect();
          }
        });

        this.connection.onError((res) => {
          console.error('WebSocket连接错误:', res);
          this.isConnected = false;
          this.isConnecting = false;
          this.stopHeartbeat();
          
          // 减少连接计数
          if (this.connectionCount > 0) {
            this.connectionCount--;
          }
          
          // 清理连接对象
          this.connection = null;
          
          // 如果重连次数未达上限，则尝试重连
          if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.scheduleReconnect();
          }
          reject(new Error('WebSocket连接失败'));
        });
      } catch (error) {
        console.error('WebSocket连接异常:', error);
        this.isConnecting = false;
        // 清理连接对象
        this.connection = null;
        reject(error);
      }
    });
  }

  /**
   * 关闭WebSocket连接
   */
  closeConnection() {
    this.stopHeartbeat();
    this.reconnectAttempts = this.maxReconnectAttempts; // 防止自动重连

    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }

    if (this.connection) {
      try {
        // 先检查连接状态
        if (this.isConnected) {
          this.connection.close();
        }
      } catch (error) {
        console.error('关闭WebSocket连接失败:', error);
      } finally {
        // 无论关闭是否成功，都更新状态
        this.isConnected = false;
        // 减少连接计数
        if (this.connectionCount > 0) {
          this.connectionCount--;
        }
        // 清理连接对象
        this.connection = null;
        console.log('WebSocket已断开连接');
      }
    }
  }

  /**
   * 发送消息
   * @param {object} data - 消息数据
   */
  send(data) {
    if (!this.isConnected) {
      console.warn('WebSocket未连接，无法发送消息');
      return;
    }

    try {
      this.connection.send({
        data: JSON.stringify(data),
        complete: () => {}
      });
    } catch (error) {
      console.error('发送WebSocket消息失败:', error);
    }
  }

  /**
   * 启动心跳检测
   */
  startHeartbeat() {
    this.stopHeartbeat();
    this.heartbeatTimer = setInterval(() => {
      if (this.isConnected) {
        this.send({ type: 'PING' });
      }
    }, 30000); // 每30秒发送一次心跳
  }

  /**
   * 停止心跳检测
   */
  stopHeartbeat() {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer);
      this.heartbeatTimer = null;
    }
  }

  /**
   * 安排重连
   */
  scheduleReconnect() {
    this.reconnectAttempts++;
    const delay = this.reconnectDelay * this.reconnectAttempts;
    console.log(`将在${delay}ms后进行第${this.reconnectAttempts}次重连`);

    this.reconnectTimer = setTimeout(() => {
      const token = uni.getStorageSync('token');
      // 使用保存的当前URL进行重连，否则使用默认URL
      const url = this.currentUrl || 'ws://172.18.215.2:9090/ws';
      
      if (token) {
        this.connect(url, token).catch(error => {
          console.error('重连失败:', error);
        });
      }
    }, delay);
  }

  /**
   * 获取连接状态
   * @returns {boolean}
   */
  isReady() {
    return this.isConnected;
  }
  
  /**
   * 获取当前连接数
   * @returns {number}
   */
  getConnectionCount() {
    return this.connectionCount;
  }
  
  /**
   * 强制重置连接计数（仅用于调试）
   */
  resetConnectionCount() {
    this.connectionCount = 0;
  }
}

// 创建全局实例
const wsHelper = new WebSocketHelper();

export default wsHelper;