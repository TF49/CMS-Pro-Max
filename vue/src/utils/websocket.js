import { ElNotification } from 'element-plus'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

/**
 * WebSocket服务类 - 使用SockJS和STOMP协议
 * 用于管理WebSocket连接、消息订阅和通知显示
 */
class WebSocketService {
  constructor() {
    this.socket = null
    this.stompClient = null
    this.connected = false
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = 5
    this.reconnectDelay = 3000
    this.messageHandlers = []
  }

  /**
   * 连接WebSocket服务器
   * @param {string} token - JWT认证token
   * @returns {Promise} 连接Promise
   */
  connect(token) {
    if (this.connected) {
      console.log('WebSocket已连接')
      return Promise.resolve()
    }

    return new Promise((resolve, reject) => {
      try {
        // 创建SockJS实例
        this.socket = new SockJS('http://localhost:9090/ws')
        
        // 获取STOMP客户端
        this.stompClient = Stomp.over(this.socket)
        
        // 设置调试模式
        this.stompClient.debug = (str) => {
          console.log('STOMP:', str)
        }
        
        // 连接成功回调
        const onConnected = () => {
          console.log('WebSocket连接成功')
          this.connected = true
          this.reconnectAttempts = 0
          
          // 订阅数据变更主题
          this.stompClient.subscribe('/topic/data-changes', (message) => {
            try {
              const data = JSON.parse(message.body)
              this.handleMessage(data)
            } catch (err) {
              console.error('解析WebSocket消息失败:', err)
            }
          })
          
          resolve()
        }
        
        // 连接错误回调
        const onError = (error) => {
          console.error('WebSocket连接错误:', error)
          this.connected = false
          reject(error)
        }
        
        // 连接关闭回调
        this.socket.onclose = () => {
          console.log('WebSocket连接关闭')
          this.connected = false
          this.handleReconnect(token)
        }
        
        // 启动连接，设置认证头
        this.stompClient.connect(
          { Authorization: `Bearer ${token}` },
          onConnected,
          onError
        )
      } catch (err) {
        console.error('WebSocket连接失败:', err)
        reject(err)
      }
    })
  }

  /**
   * 处理接收到的消息
   * @param {Object} message - 消息对象
   */
  handleMessage(message) {
    console.log('收到WebSocket消息:', message)

    // 检查是否是数据变更事件
    if (message.module && message.operation) {
      this.handleDataChangeEvent(message)
    }
  }

  /**
   * 处理数据变更事件
   * @param {Object} event - 数据变更事件对象
   */
  handleDataChangeEvent(event) {
    console.log('收到数据变更事件:', event)

    // 应用角色过滤
    const currentUserId = this.getCurrentUserId()
    const isAdmin = this.isAdmin()

    // 普通用户只能看到自己创建的数据变更
    if (!isAdmin && event.createdBy !== currentUserId) {
      return
    }

    // 显示通知
    this.showNotification(event)

    // 通知所有注册的处理器
    this.messageHandlers.forEach(handler => handler(event))
  }

  /**
   * 显示Element Plus通知
   * @param {Object} event - 数据变更事件对象
   */
  showNotification(event) {
    const moduleNames = {
      resident: '居民',
      household: '户籍',
      education: '教育',
      employment: '就业',
      medical: '医疗',
      socialSecurity: '社保',
      property: '房产',
      vehicle: '车辆'
    }

    const operationNames = {
      INSERT: '新增',
      UPDATE: '更新',
      DELETE: '删除'
    }

    const moduleName = moduleNames[event.module] || event.module
    const operationName = operationNames[event.operation] || event.operation

    ElNotification({
      title: '数据更新提醒',
      message: `${moduleName}数据已${operationName}，点击刷新查看最新数据`,
      type: 'info',
      duration: 5000,
      onClick: () => {
        // 触发刷新事件
        this.notifyRefresh(event.module)
      }
    })
  }

  /**
   * 注册消息处理器
   * @param {Function} handler - 消息处理函数
   */
  registerMessageHandler(handler) {
    this.messageHandlers.push(handler)
  }

  /**
   * 注销消息处理器
   * @param {Function} handler - 消息处理函数
   */
  unregisterMessageHandler(handler) {
    const index = this.messageHandlers.indexOf(handler)
    if (index > -1) {
      this.messageHandlers.splice(index, 1)
    }
  }

  /**
   * 触发模块刷新事件
   * @param {string} module - 模块名称
   */
  notifyRefresh(module) {
    // 发送自定义事件，供组件监听
    window.dispatchEvent(new CustomEvent('ws-refresh', { detail: { module } }))
  }

  /**
   * 处理重连逻辑
   * @param {string} token - JWT认证token
   */
  handleReconnect(token) {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++
      console.log(`尝试重连 (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`)
      setTimeout(() => {
        this.connect(token)
      }, this.reconnectDelay * this.reconnectAttempts)
    } else {
      console.error('达到最大重连次数')
      ElNotification({
        title: '连接断开',
        message: '实时数据同步已断开，请刷新页面重新连接',
        type: 'error',
        duration: 0
      })
    }
  }

  /**
   * 断开WebSocket连接
   */
  disconnect() {
    if (this.stompClient) {
      this.stompClient.disconnect()
      this.stompClient = null
    }
    if (this.socket) {
      this.socket.close()
      this.socket = null
    }
    this.connected = false
    console.log('WebSocket已断开')
  }

  /**
   * 获取当前用户ID
   * @returns {number|null} 用户ID
   */
  getCurrentUserId() {
    const userStr = localStorage.getItem('user')
    if (userStr) {
      try {
        const user = JSON.parse(userStr)
        return user.id
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }
    return null
  }

  /**
   * 判断当前用户是否为管理员
   * @returns {boolean} 是否为管理员
   */
  isAdmin() {
    const userStr = localStorage.getItem('user')
    if (userStr) {
      try {
        const user = JSON.parse(userStr)
        return user.role === 'ADMIN'
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }
    return false
  }
}

// 导出单例实例
export default new WebSocketService()
