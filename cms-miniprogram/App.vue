<template>
  <!-- 微信小程序不需要router-view，直接使用pages.json配置的页面 -->
  <div>
    
  </div>
</template>

<script>
import wsHelper from '@/utils/websocketHelper'

export default {
  onLaunch: function() {
    console.log('App Launch')
    
    // 初始化WebSocket连接
    this.initWebSocket()
  },
  onShow: function() {
    console.log('App Show')
  },
  onHide: function() {
    console.log('App Hide')
    // 应用进入后台时可以选择断开WebSocket连接
    if (wsHelper.isReady()) {
      wsHelper.closeConnection()
    }
  },
  methods: {
    /**
     * 初始化WebSocket连接
     */
    initWebSocket() {
      const token = uni.getStorageSync('token')
      
      // 检查当前连接数
      console.log('当前WebSocket连接数:', wsHelper.getConnectionCount());

      // 只有在用户已登录时才连接WebSocket
      if (token) {
        const wsUrl = 'ws://127.0.0.1:9090/ws'
        wsHelper.connect(wsUrl, token)
          .then(() => {
            console.log('WebSocket连接成功，当前连接数:', wsHelper.getConnectionCount())
            // 注册全局消息处理器
            this.registerWebSocketHandlers()
          })
          .catch(error => {
            console.error('WebSocket连接失败:', error)
          })
      }
    },

    /**
     * 注册WebSocket消息处理器
     */
    registerWebSocketHandlers() {
      // 监听消息
      // 注意：由于uni.connectSocket的限制，我们需要通过全局事件来处理消息
      // 在实际使用中，可以通过uni.$on来监听特定事件
      
      // 处理数据变更通知
      uni.$on('dataChange', (message) => {
        console.log('收到数据变更通知:', message)
      })

      // 处理系统通知
      uni.$on('systemNotification', (message) => {
        console.log('收到系统通知:', message)
      })
    }
  }
}
</script>

<style>
/* 全局样式 */
@import '@/utils/responsive.css';

page {
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 全局响应式容器 */
.container {
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
}
</style>