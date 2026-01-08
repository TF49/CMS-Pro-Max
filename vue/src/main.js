import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './assets/styles/responsive.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import DeleteErrorHandler from './utils/deleteErrorHandler'
import DeleteErrorDialog from './components/DeleteErrorDialog.vue'
import registerDirectives from './directive'

// 创建应用实例
const app = createApp(App)

// 创建全局事件总线
window.eventBus = app.config.globalProperties.$bus = {
  _events: {},
  $on(event, callback) {
    if (!this._events[event]) this._events[event] = []
    this._events[event].push(callback)
  },
  $off(event, callback) {
    if (!this._events[event]) return
    if (callback) {
      this._events[event] = this._events[event].filter(cb => cb !== callback)
    } else {
      this._events[event] = []
    }
  },
  $emit(event, ...args) {
    if (!this._events[event]) return
    this._events[event].forEach(callback => callback(...args))
  }
}

// 使用Element Plus
app.use(ElementPlus)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 注册全局组件
app.component('DeleteErrorDialog', DeleteErrorDialog)

// 注册全局错误处理器
app.use(DeleteErrorHandler)

// 使用路由
app.use(router)

// 注册自定义指令
registerDirectives(app)

// 挂载应用
app.mount('#app')