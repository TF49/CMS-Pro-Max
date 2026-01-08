import axios from 'axios'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.NODE_ENV === 'production' ? '/api' : '', // 生产环境使用/api前缀，开发环境使用代理
  timeout: 10000 // request timeout
})

// request拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    const token = localStorage.getItem('token')
    if (token) {
      // 让每个请求携带token
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    // 移除控制台日志输出
    return Promise.reject(error)
  }
)

// response拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码不是200，则判断为错误
    if (res.code !== 200) {
      // 50008: 非法token; 50012: 其他客户端登录了; 50014: Token 过期了
      if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
        // 重新登录
        localStorage.removeItem('token')
        // 跳转到登录页
        window.location.href = '/login'
      }
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  error => {
    // 处理网络错误
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未授权，跳转到登录页
          localStorage.removeItem('token')
          window.location.href = '/login'
          break
        case 403:
          // 拒绝访问 - 通常表示后端服务未启动或权限不足
          console.error('403 Forbidden: 拒绝访问')
          console.error('请检查：1. 后端服务是否启动(localhost:9090) 2. 跨域配置是否正确 3. 权限是否足够')
          console.error('响应状态:', error.response.status)
          console.error('响应数据:', error.response.data)
          break
        case 404:
          // 请求资源不存在
          console.error('404 Not Found: 请求的资源不存在', error.config.url)
          break
        case 500:
          // 服务器内部错误
          console.error('500 Server Error: 服务器内部错误')
          break
        default:
          console.error(`HTTP 错误: ${error.response.status}`)
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应 - 通常表示网络问题或后端未启动
      console.error('无响应错误：请检查后端服务是否启动', error.message)
    } else {
      // 断网等情况
      console.error('网络连接异常:', error.message)
    }
    return Promise.reject(error)
  }
)

export default service

// 封装常用的请求方法
export const get = (url, params) => {
  return service({
    url,
    method: 'get',
    params
  })
}

export const post = (url, data) => {
  return service({
    url,
    method: 'post',
    data
  })
}

export const put = (url, data) => {
  return service({
    url,
    method: 'put',
    data
  })
}

export const del = (url) => {
  return service({
    url,
    method: 'delete'
  })
}