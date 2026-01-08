// 响应式设计工具

/**
 * 获取系统信息
 */
export const getSystemInfo = () => {
  try {
    return uni.getSystemInfoSync()
  } catch (error) {
    console.error('获取系统信息失败', error)
    return {
      windowWidth: 375,
      windowHeight: 667,
      pixelRatio: 2,
      screenWidth: 375,
      screenHeight: 667
    }
  }
}

/**
 * 判断是否为小屏设备（宽度 <= 375px）
 */
export const isSmallScreen = () => {
  const systemInfo = getSystemInfo()
  return systemInfo.windowWidth <= 375
}

/**
 * 判断是否为中屏设备（宽度 376px - 414px）
 */
export const isMediumScreen = () => {
  const systemInfo = getSystemInfo()
  return systemInfo.windowWidth > 375 && systemInfo.windowWidth <= 414
}

/**
 * 判断是否为大屏设备（宽度 > 414px）
 */
export const isLargeScreen = () => {
  const systemInfo = getSystemInfo()
  return systemInfo.windowWidth > 414
}

/**
 * 根据屏幕尺寸获取字体大小
 * @param {number} baseSize 基础字体大小（rpx）
 * @returns {number} 调整后的字体大小（rpx）
 */
export const getResponsiveFontSize = (baseSize) => {
  const systemInfo = getSystemInfo()
  const width = systemInfo.windowWidth
  
  if (width <= 320) {
    return baseSize * 0.85 // 超小屏
  } else if (width <= 375) {
    return baseSize * 0.95 // 小屏
  } else if (width <= 414) {
    return baseSize // 中屏
  } else {
    return baseSize * 1.05 // 大屏
  }
}

/**
 * 根据屏幕尺寸获取间距
 * @param {number} baseSpacing 基础间距（rpx）
 * @returns {number} 调整后的间距（rpx）
 */
export const getResponsiveSpacing = (baseSpacing) => {
  const systemInfo = getSystemInfo()
  const width = systemInfo.windowWidth
  
  if (width <= 320) {
    return baseSpacing * 0.8
  } else if (width <= 375) {
    return baseSpacing * 0.9
  } else if (width <= 414) {
    return baseSpacing
  } else {
    return baseSpacing * 1.1
  }
}

/**
 * 获取响应式列数（用于网格布局）
 * @param {number} defaultCols 默认列数
 * @returns {number} 调整后的列数
 */
export const getResponsiveCols = (defaultCols) => {
  const systemInfo = getSystemInfo()
  const width = systemInfo.windowWidth
  
  if (width <= 375) {
    return Math.max(1, defaultCols - 1) // 小屏减少一列
  } else if (width <= 414) {
    return defaultCols
  } else {
    return defaultCols + 1 // 大屏增加一列
  }
}

/**
 * 获取设备像素比
 */
export const getPixelRatio = () => {
  const systemInfo = getSystemInfo()
  return systemInfo.pixelRatio || 2
}

/**
 * 判断是否为横屏
 */
export const isLandscape = () => {
  const systemInfo = getSystemInfo()
  return systemInfo.windowWidth > systemInfo.windowHeight
}

/**
 * 判断是否为竖屏
 */
export const isPortrait = () => {
  return !isLandscape()
}

