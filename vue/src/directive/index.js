/**
 * 指令入口文件
 * 用于导出所有自定义指令
 */
import permission from './permission'

/**
 * 注册所有自定义指令
 * @param {Object} app - Vue应用实例
 */
export default function registerDirectives(app) {
  // 注册权限指令
  app.directive('permission', permission)
}
