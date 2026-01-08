import request from '@/utils/request'

// 登录
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

// 注册
export function register(data) {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

// 忘记密码
export function forgotPassword(data) {
  return request({
    url: '/api/auth/forgot-password',
    method: 'post',
    data
  })
}

// 验证重置密码Token
export function verifyResetToken(data) {
  return request({
    url: '/api/auth/verify-reset-token',
    method: 'post',
    data
  })
}

// 重置密码
export function resetPassword(data) {
  return request({
    url: '/api/auth/reset-password',
    method: 'post',
    data
  })
}