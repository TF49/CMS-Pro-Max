const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  // 关闭开发模式下的WebSocket日志功能
  devServer: {
    client: {
      webSocketURL: false,
      progress: false,
      overlay: false
    },
    liveReload: false,
    hot: false
  },
  // 其他配置
  transpileDependencies: true
})
