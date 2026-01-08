import request from '@/utils/request'

// 获取系统信息
export function getSystemInfo() {
  return request({
    url: '/system/info',
    method: 'get'
  })
}

// 清除统计缓存
export function clearStatisticsCache() {
  return request({
    url: '/system/cache/clear',
    method: 'delete'
  })
}