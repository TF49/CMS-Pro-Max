// 网络请求封装
import { getGlobalClass } from '@dcloudio/uni-app'

// CMS后端API地址
// 注意：开发环境使用本地地址，真机调试请将 IP 换成电脑的局域网 IP
const baseUrl = 'http://127.0.0.1:9090'

const request = (options = {}) => {
  return new Promise((resolve, reject) => {
    // 获取token
    const token = uni.getStorageSync('token') || ''

    // 判断是否是登录/注册接口，这些接口不需要 token，也不应该自动跳转
    const isAuthRequest = options.url && (
      options.url.includes('/auth/login') ||
      options.url.includes('/auth/register') ||
      options.url.includes('/auth/reset-password') ||
      options.url.includes('/auth/forgot-password')
    )

    uni.request({
      url: baseUrl + (options.url || ''),
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      }
    }).then(response => {
      const { statusCode, data } = response

      // 对于登录/注册接口，直接返回响应，不进行自动跳转处理
      if (isAuthRequest) {
        resolve(data)
        return
      }

      // 处理 HTTP 状态码错误（非登录/注册接口）
      if (statusCode === 403) {
        console.error('API 请求被拒绝 (403)，可能是权限不足或 token 无效')
        // 尝试清除 token 并跳转登录
        uni.removeStorageSync('token')
        uni.showToast({
          title: '权限不足，请重新登录',
          icon: 'none'
        })
        setTimeout(() => {
          // 检查当前页面，避免重复跳转
          const pages = getCurrentPages()
          const currentPage = pages[pages.length - 1]
          if (currentPage && currentPage.route !== 'pages/login/login') {
            uni.redirectTo({
              url: '/pages/login/login'
            })
          }
        }, 1500)
        reject(new Error('权限不足'))
        return
      }

      if (statusCode === 401 || data.code === '401' || data.code === 401) {
        uni.removeStorageSync('token')
        uni.showToast({
          title: '登录已过期，请重新登录',
          icon: 'none'
        })
        setTimeout(() => {
          // 检查当前页面，避免重复跳转
          const pages = getCurrentPages()
          const currentPage = pages[pages.length - 1]
          if (currentPage && currentPage.route !== 'pages/login/login') {
            uni.redirectTo({
              url: '/pages/login/login'
            })
          }
        }, 1500)
        reject(new Error('未授权'))
        return
      }

      // 处理其他错误状态码
      if (statusCode >= 400) {
        console.error(`API 请求失败: ${statusCode}`, data)

        // 针对常见错误状态码提供更友好的错误信息
        let errorMessage = data.message || data.msg || `请求失败: ${statusCode}`

        if (statusCode === 502) {
          errorMessage = '服务器连接失败，请检查后端服务是否正常运行'
        } else if (statusCode === 503) {
          errorMessage = '服务暂时不可用，请稍后重试'
        } else if (statusCode === 504) {
          errorMessage = '请求超时，请检查网络连接'
        } else if (statusCode === 500) {
          // 500错误时保留后端返回的具体错误信息
          errorMessage = data.message || data.msg || '服务器内部错误，请联系管理员'
        } else if (statusCode === 404) {
          errorMessage = '请求的资源不存在'
        }

        reject(new Error(errorMessage))
        return
      }

      resolve(data)
    }).catch(error => {
      console.error('网络请求错误:', error)

      // 网络错误提示
      let errorMessage = '网络请求失败，请检查网络连接'

      if (error.errMsg) {
        if (error.errMsg.includes('502') || error.errMsg.includes('Bad Gateway')) {
          errorMessage = '服务器连接失败，请检查后端服务是否正常运行'
        } else if (error.errMsg.includes('timeout')) {
          errorMessage = '请求超时，请检查网络连接或服务器状态'
        } else if (error.errMsg.includes('fail')) {
          errorMessage = '网络请求失败，请检查网络连接和后端服务'
        }
      }

      // 对于登录/注册接口，不显示全局 toast，让页面自己处理
      if (!isAuthRequest) {
        uni.showToast({
          title: errorMessage,
          icon: 'none',
          duration: 2000
        })
      }

      reject(new Error(errorMessage))
    })
  })
}

// GET请求
export const get = (url, data = {}, options = {}) => {
  options.method = 'GET'
  options.data = data
  options.url = url
  return request(options)
}

// POST请求
export const post = (url, data = {}, options = {}) => {
  options.method = 'POST'
  options.data = data
  options.url = url
  return request(options)
}

// PUT请求
export const put = (url, data = {}, options = {}) => {
  options.method = 'PUT'
  options.data = data
  options.url = url
  return request(options)
}

// DELETE请求
export const del = (url, data = {}, options = {}) => {
  options.method = 'DELETE'
  options.data = data
  options.url = url
  return request(options)
}

export default {
  request,
  get,
  post,
  put,
  del
}