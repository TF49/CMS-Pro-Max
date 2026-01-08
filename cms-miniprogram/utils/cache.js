// 数据缓存工具
const CACHE_PREFIX = 'cms_cache_'
const CACHE_EXPIRE_PREFIX = 'cms_cache_expire_'
const DEFAULT_EXPIRE_TIME = 5 * 60 * 1000 // 默认5分钟过期

/**
 * 设置缓存
 * @param {string} key 缓存键
 * @param {any} value 缓存值
 * @param {number} expireTime 过期时间（毫秒），默认5分钟
 */
export const setCache = (key, value, expireTime = DEFAULT_EXPIRE_TIME) => {
  try {
    const cacheKey = CACHE_PREFIX + key
    const expireKey = CACHE_EXPIRE_PREFIX + key
    const expireAt = Date.now() + expireTime
    
    uni.setStorageSync(cacheKey, JSON.stringify(value))
    uni.setStorageSync(expireKey, expireAt.toString())
    return true
  } catch (error) {
    console.error('设置缓存失败', error)
    return false
  }
}

/**
 * 获取缓存
 * @param {string} key 缓存键
 * @returns {any|null} 缓存值，如果过期或不存在返回null
 */
export const getCache = (key) => {
  try {
    const cacheKey = CACHE_PREFIX + key
    const expireKey = CACHE_EXPIRE_PREFIX + key
    
    const expireAt = uni.getStorageSync(expireKey)
    if (!expireAt) {
      return null
    }
    
    // 检查是否过期
    if (Date.now() > parseInt(expireAt)) {
      // 清除过期缓存
      removeCache(key)
      return null
    }
    
    const value = uni.getStorageSync(cacheKey)
    if (!value) {
      return null
    }
    
    return JSON.parse(value)
  } catch (error) {
    console.error('获取缓存失败', error)
    return null
  }
}

/**
 * 移除缓存
 * @param {string} key 缓存键
 */
export const removeCache = (key) => {
  try {
    const cacheKey = CACHE_PREFIX + key
    const expireKey = CACHE_EXPIRE_PREFIX + key
    
    uni.removeStorageSync(cacheKey)
    uni.removeStorageSync(expireKey)
    return true
  } catch (error) {
    console.error('移除缓存失败', error)
    return false
  }
}

/**
 * 清除所有缓存
 */
export const clearAllCache = () => {
  try {
    const storage = uni.getStorageInfoSync()
    storage.keys.forEach(key => {
      if (key.startsWith(CACHE_PREFIX) || key.startsWith(CACHE_EXPIRE_PREFIX)) {
        uni.removeStorageSync(key)
      }
    })
    return true
  } catch (error) {
    console.error('清除缓存失败', error)
    return false
  }
}

/**
 * 检查缓存是否存在且未过期
 * @param {string} key 缓存键
 * @returns {boolean}
 */
export const hasCache = (key) => {
  return getCache(key) !== null
}

