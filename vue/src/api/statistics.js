import axios from 'axios'

// 创建专门的统计接口axios实例
const statisticsService = axios.create({
  baseURL: '/api/statistics', // 统计接口专用路径，统一使用/api前缀
  timeout: 10000
})

// API请求缓存
const cache = {
  data: {},
  // 缓存过期时间（毫秒）
  expireTime: 5 * 60 * 1000 // 5分钟
}

// 检查缓存是否有效
function isCacheValid(key) {
  if (!cache.data[key]) return false
  return Date.now() - cache.data[key].timestamp < cache.expireTime
}

// 获取缓存数据
function getCache(key) {
  if (isCacheValid(key)) {
    return Promise.resolve(cache.data[key].value)
  }
  return null
}

// 设置缓存数据
function setCache(key, value) {
  cache.data[key] = {
    value,
    timestamp: Date.now()
  }
}

// request拦截器
statisticsService.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// response拦截器
statisticsService.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          localStorage.removeItem('token')
          window.location.href = '/login'
          break
        case 403:
          console.error('拒绝访问')
          break
        case 404:
          console.error('请求资源不存在')
          break
        case 500:
          console.error('服务器内部错误')
          break
        default:
          console.error(`连接错误${error.response.status}`)
      }
    } else {
      console.error('网络连接异常')
    }
    return Promise.reject(error)
  }
)

// 带缓存的API请求包装函数
function requestWithCache(config) {
  const cacheKey = JSON.stringify(config)
  const cachedData = getCache(cacheKey)
  if (cachedData) {
    return cachedData
  }
  return statisticsService(config).then(response => {
    setCache(cacheKey, response)
    return response
  })
}

// 获取系统统计摘要
export function getSummary() {
  return requestWithCache({
    url: '/summary',
    method: 'get'
  })
}

// 获取各地区人口统计
export function getPopulationByRegion() {
  return requestWithCache({
    url: '/population-by-region',
    method: 'get'
  })
}

// 获取人口增长趋势
export function getPopulationTrend() {
  return requestWithCache({
    url: '/population-trend',
    method: 'get'
  })
}

// 获取年龄分布
export function getAgeDistribution() {
  return requestWithCache({
    url: '/age-distribution',
    method: 'get'
  })
}

// 获取行业分布
export function getIndustryDistribution() {
  return requestWithCache({
    url: '/industry-distribution',
    method: 'get'
  })
}

// 获取就业状态分布
export function getEmploymentStatusDistribution() {
  return requestWithCache({
    url: '/employment-status-distribution',
    method: 'get'
  })
}

// 获取社保参保率
export function getSocialSecurityCoverage() {
  return requestWithCache({
    url: '/social-security-coverage',
    method: 'get'
  })
}

// 获取房产类型分布
export function getPropertyTypeDistribution() {
  return requestWithCache({
    url: '/property-type-distribution',
    method: 'get'
  })
}

// 获取车辆品牌分布
export function getVehicleBrandDistribution() {
  return requestWithCache({
    url: '/vehicle-brand-distribution',
    method: 'get'
  })
}

// 获取教育程度分布
export function getEducationLevel() {
  return requestWithCache({
    url: '/education-level',
    method: 'get'
  })
}

// 获取医疗保险类型分布
export function getMedicalInsurance() {
  return requestWithCache({
    url: '/medical-insurance',
    method: 'get'
  })
}

// 获取最新数据 - 不使用缓存，确保数据实时性
export function getLatestData() {
  return statisticsService({
    url: '/latest-data',
    method: 'get'
  })
}

// 获取房产类型列表
export function getPropertyTypes() {
  return requestWithCache({
    url: '/property-types',
    method: 'get'
  })
}

// 获取社保类型列表
export function getSocialSecurityTypes() {
  return requestWithCache({
    url: '/social-security-types',
    method: 'get'
  })
}

// 获取车辆类型列表
export function getVehicleTypes() {
  return requestWithCache({
    url: '/vehicle-types',
    method: 'get'
  })
}