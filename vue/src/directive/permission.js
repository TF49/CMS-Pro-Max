/**
 * 权限指令
 * 用于根据用户角色或权限动态显示/隐藏元素
 */

/**
 * 检查用户是否有指定的权限
 * @param {Array} roles - 用户角色列表
 * @param {Array} permissions - 所需的权限列表
 * @param {String} logical - 权限之间的逻辑关系（AND/OR）
 * @returns {boolean} - 是否有指定的权限
 */
function checkPermission(roles, permissions, logical = 'OR') {
  if (!permissions || permissions.length === 0) {
    // 没有指定权限要求，默认显示
    return true
  }
  
  if (!roles || roles.length === 0) {
    // 用户没有角色，默认隐藏
    return false
  }
  
  if (logical === 'AND') {
    // AND逻辑：用户必须拥有所有指定的权限
    return permissions.every(permission => roles.includes(permission))
  } else {
    // OR逻辑：用户只要拥有其中一个权限即可
    return permissions.some(permission => roles.includes(permission))
  }
}

/**
 * 权限指令定义
 */
export default {
  /**
   * 指令挂载时调用
   * @param {HTMLElement} el - 绑定的DOM元素
   * @param {Object} binding - 指令绑定信息
   */
  mounted(el, binding) {
    // 获取用户角色
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const roles = userInfo.role ? [userInfo.role] : []
    
    // 获取指令参数
    const { value, arg } = binding
    
    // 检查权限
    const hasPermission = checkPermission(roles, value, arg)
    
    if (!hasPermission) {
      // 没有权限，隐藏元素
      el.style.display = 'none'
    }
  },
  
  /**
   * 指令更新时调用
   * @param {HTMLElement} el - 绑定的DOM元素
   * @param {Object} binding - 指令绑定信息
   */
  updated(el, binding) {
    // 获取用户角色
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const roles = userInfo.role ? [userInfo.role] : []
    
    // 获取指令参数
    const { value, arg } = binding
    
    // 检查权限
    const hasPermission = checkPermission(roles, value, arg)
    
    if (hasPermission) {
      // 有权限，显示元素
      el.style.display = ''
    } else {
      // 没有权限，隐藏元素
      el.style.display = 'none'
    }
  }
}
