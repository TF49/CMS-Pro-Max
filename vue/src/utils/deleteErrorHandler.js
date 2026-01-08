import { ElMessageBox } from 'element-plus'

/**
 * 删除错误处理器
 * 统一处理删除操作中的关联数据错误
 */
class DeleteErrorHandler {
  /**
   * 显示删除错误对话框
   * @param {Object} options - 错误配置选项
   * @param {string} options.errorMessage - 错误消息
   * @param {string} options.solution - 解决方案
   * @param {Array} options.relatedData - 关联数据列表
   * @param {Function} options.onViewRelated - 查看关联数据的回调函数
   * @returns {Promise}
   */
  static async showDeleteError(options) {
    const {
      errorMessage,
      solution = '',
      relatedData = [],
      onViewRelated = null
    } = options

    // 构建错误消息内容
    let message = `<div style="text-align: center; padding: 20px 0;">
      <div style="margin-bottom: 16px;">
        <el-icon size="48" color="#F56C6C"><WarningFilled /></el-icon>
      </div>
      <div>
        <p style="font-size: 16px; font-weight: 600; color: #F56C6C; margin-bottom: 8px; line-height: 1.5;">
          ${errorMessage}
        </p>
        ${solution ? `<p style="font-size: 14px; color: #606266; line-height: 1.5; margin-top: 8px;">${solution}</p>` : ''}
      </div>
    </div>`

    // 如果有关联数据，添加关联数据展示
    if (relatedData.length > 0) {
      const relatedDataHtml = relatedData.map(item => 
        `<div style="display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 1px solid #F0F0F0;">
          <span style="font-weight: 600; color: #303133;">${item.label}:</span>
          <span style="color: #606266; max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">${item.value}</span>
        </div>`
      ).join('')
      
      message += `
      <div style="margin-top: 20px; border-top: 1px solid #EBEEF5; padding-top: 20px;">
        <div style="max-height: 200px; overflow-y: auto;">
          ${relatedDataHtml}
        </div>
      </div>`
    }

    const buttons = []
    
    // 如果有查看关联数据的回调函数，添加查看按钮
    if (onViewRelated) {
      buttons.push({
        text: '查看关联数据',
        type: 'primary',
        async handler() {
          await onViewRelated()
          return false // 不关闭对话框
        }
      })
    }
    
    buttons.push({
      text: '关闭',
      type: 'default',
      handler: () => true
    })

    return ElMessageBox({
      title: '删除操作失败',
      message: message,
      dangerouslyUseHTMLString: true,
      customClass: 'delete-error-message-box',
      showClose: false,
      closeOnClickModal: false,
      closeOnPressEscape: false,
      distinguishCancelAndClose: true,
      buttons: buttons
    })
  }

  /**
   * 检查是否为关联数据错误
   * @param {Error} error - 错误对象
   * @returns {boolean}
   */
  static isRelatedDataError(error) {
    const errorMessage = error.message || error.toString()
    return errorMessage.includes('关联') || 
           errorMessage.includes('关联数据') || 
           errorMessage.includes('无法删除') ||
           errorMessage.includes('存在关联')
  }

  /**
   * 处理删除操作错误
   * @param {Error} error - 错误对象
   * @param {Object} options - 错误处理选项
   * @returns {Promise}
   */
  static async handleDeleteError(error, options = {}) {
    const errorMessage = error.message || error.toString()
    
    // 如果是关联数据错误，显示详细错误对话框
    if (this.isRelatedDataError(error)) {
      return this.showDeleteError({
        errorMessage: errorMessage,
        solution: options.solution || '请先清除相关关联数据，然后再尝试删除操作。',
        relatedData: options.relatedData || [],
        onViewRelated: options.onViewRelated
      })
    } else {
      // 其他错误使用默认错误提示
      throw error
    }
  }

  /**
   * 创建关联数据错误对象
   * @param {string} moduleName - 模块名称
   * @param {Array} relatedRecords - 关联记录列表
   * @returns {Error}
   */
  static createRelatedDataError(moduleName, relatedRecords = []) {
    let errorMessage = `该记录在${moduleName}中存在关联数据，无法删除`
    
    if (relatedRecords.length > 0) {
      const recordCount = relatedRecords.length
      errorMessage += `，共发现 ${recordCount} 条关联记录`
    }
    
    const error = new Error(errorMessage)
    error.relatedRecords = relatedRecords
    error.isRelatedDataError = true
    
    return error
  }

  /**
   * 创建居民关联数据错误对象
   * @param {Array} relatedModules - 关联模块列表
   * @returns {Error}
   */
  static createResidentRelatedError(relatedModules) {
    if (relatedModules.length === 1) {
      return new Error(`该居民在${relatedModules[0]}中存在关联记录，无法删除，请先清除相关关联数据`)
    } else {
      const modulesText = relatedModules.join('、')
      return new Error(`该居民在${modulesText}中存在关联记录，无法删除，请先清除相关关联数据`)
    }
  }
}

// 导出单例实例
export default DeleteErrorHandler

// 全局安装方法
DeleteErrorHandler.install = (app) => {
  app.config.globalProperties.$deleteErrorHandler = DeleteErrorHandler
  app.provide('deleteErrorHandler', DeleteErrorHandler)
}