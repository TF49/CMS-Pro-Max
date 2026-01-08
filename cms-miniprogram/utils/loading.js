// 加载动画工具

/**
 * 显示加载提示
 * @param {string} title 提示文字，默认为"加载中..."
 * @param {boolean} mask 是否显示透明蒙层
 */
export const showLoading = (title = '加载中...', mask = true) => {
  uni.showLoading({
    title: title,
    mask: mask
  })
}

/**
 * 隐藏加载提示
 */
export const hideLoading = () => {
  uni.hideLoading()
}

/**
 * 显示成功提示
 * @param {string} title 提示文字
 * @param {number} duration 显示时长（毫秒）
 */
export const showSuccess = (title, duration = 2000) => {
  uni.showToast({
    title: title,
    icon: 'success',
    duration: duration,
    mask: false
  })
}

/**
 * 显示错误提示
 * @param {string} title 提示文字
 * @param {number} duration 显示时长（毫秒）
 */
export const showError = (title, duration = 2000) => {
  uni.showToast({
    title: title,
    icon: 'none',
    duration: duration,
    mask: false
  })
}

/**
 * 显示普通提示
 * @param {string} title 提示文字
 * @param {number} duration 显示时长（毫秒）
 */
export const showMessage = (title, duration = 2000) => {
  uni.showToast({
    title: title,
    icon: 'none',
    duration: duration,
    mask: false
  })
}

