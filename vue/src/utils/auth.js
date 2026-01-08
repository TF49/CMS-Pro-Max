/**
 * 用户权限相关工具函数
 */

/**
 * 获取token
 */
export function getToken() {
  return localStorage.getItem('token')
}

/**
 * 设置token
 * @param {string} token 
 */
export function setToken(token) {
  return localStorage.setItem('token', token)
}

/**
 * 移除token
 */
export function removeToken() {
  return localStorage.removeItem('token')
}

/**
 * 判断是否已登录
 */
export function isLoggedIn() {
  return !!localStorage.getItem('token')
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  const userInfo = localStorage.getItem('userInfo')
  return userInfo ? JSON.parse(userInfo) : null
}

/**
 * 设置用户信息
 * @param {Object} userInfo 
 */
export function setUserInfo(userInfo) {
  return localStorage.setItem('userInfo', JSON.stringify(userInfo))
}

/**
 * 移除用户信息
 */
export function removeUserInfo() {
  return localStorage.removeItem('userInfo')
}

/**
 * 退出登录
 */
export function logout() {
  removeToken()
  removeUserInfo()
  // 跳转到登录页
  window.location.href = '/login'
}