import { createSSRApp } from 'vue'
import App from './App.vue'
import uniIcons from '@dcloudio/uni-ui/lib/uni-icons/uni-icons.vue'

// ========== 彻底禁用 uni-app 开发模式的 WebSocket 日志功能 ==========
// 原因：微信小程序最多只支持2个同时WebSocket连接
// uni-app的日志功能会占用1个连接，导致应用只能使用1个连接
// 必须在应用初始化时立即禁用，否则框架会自动建立日志连接

// 方法1：设置环境变量（在框架加载前）
if (typeof process !== 'undefined' && process.env) {
  process.env.UNI_CONSOLE_SOCKET_DISABLE = 'true'
  process.env.UNI_APP_CONSOLE_SOCKET_DISABLE = 'true'
  process.env.UNI_CLOUD_LOGSERVE_DISABLE = 'true'
  process.env.NODE_ENV = 'production'
}

// 方法2：直接设置全局变量禁用日志
if (typeof globalThis !== 'undefined') {
  globalThis.__UNI_LOG_DISABLE__ = true
  globalThis.__UNI_CONSOLE_LOG_DISABLE__ = true
}

// 方法3：禁用console功能（可选，会禁用所有console输出）
// console.log = function() {}
// console.error = function() {}
// console.warn = function() {}

// 创建应用实例
const app = createSSRApp(App)

// 全局注册 uni-icons 组件
app.component('uni-icons', uniIcons)

// 全局配置
app.config.globalProperties.$global = {
  // CMS后端API地址
  // 注意：开发环境使用本地地址，真机调试请将 127.0.0.1 换成电脑的局域网 IP (如 192.168.x.x)
  baseUrl: 'http://127.0.0.1:9090'
}

// 在应用初始化后再次尝试禁用日志（双保险）
app.config.globalProperties.$disableLogging = true

export function createApp() {
  return {
    app
  }
}