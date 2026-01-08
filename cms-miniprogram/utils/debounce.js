// 防抖和节流工具

/**
 * 防抖函数
 * @param {Function} func 要执行的函数
 * @param {number} wait 等待时间（毫秒）
 * @param {boolean} immediate 是否立即执行
 * @returns {Function} 防抖后的函数
 */
export const debounce = (func, wait = 300, immediate = false) => {
  let timeout = null
  
  return function(...args) {
    const context = this
    
    const later = () => {
      timeout = null
      if (!immediate) {
        func.apply(context, args)
      }
    }
    
    const callNow = immediate && !timeout
    
    if (timeout) {
      clearTimeout(timeout)
    }
    
    timeout = setTimeout(later, wait)
    
    if (callNow) {
      func.apply(context, args)
    }
  }
}

/**
 * 节流函数
 * @param {Function} func 要执行的函数
 * @param {number} wait 等待时间（毫秒）
 * @param {boolean} leading 是否在开始时执行
 * @param {boolean} trailing 是否在结束时执行
 * @returns {Function} 节流后的函数
 */
export const throttle = (func, wait = 300, leading = true, trailing = true) => {
  let timeout = null
  let previous = 0
  
  return function(...args) {
    const context = this
    const now = Date.now()
    
    if (!previous && !leading) {
      previous = now
    }
    
    const remaining = wait - (now - previous)
    
    if (remaining <= 0 || remaining > wait) {
      if (timeout) {
        clearTimeout(timeout)
        timeout = null
      }
      previous = now
      func.apply(context, args)
    } else if (!timeout && trailing) {
      timeout = setTimeout(() => {
        previous = leading ? Date.now() : 0
        timeout = null
        func.apply(context, args)
      }, remaining)
    }
  }
}

