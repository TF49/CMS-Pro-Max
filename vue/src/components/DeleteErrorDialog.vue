<template>
  <el-dialog
    :title="errorTitle"
    v-model="dialogVisible"
    width="500px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    custom-class="delete-error-dialog"
  >
    <div class="error-content">
      <div class="error-icon">
        <el-icon :size="48" color="#F56C6C"><WarningFilled /></el-icon>
      </div>
      <div class="error-message">
        <p class="error-text">{{ errorMessage }}</p>
        <p class="error-solution" v-if="solution">{{ solution }}</p>
      </div>
      
      <!-- 关联数据展示区域 -->
      <div class="related-data" v-if="relatedData && relatedData.length > 0">
        <el-collapse v-model="activeCollapse">
          <el-collapse-item title="查看关联数据" name="related">
            <div class="data-list">
              <div 
                v-for="(item, index) in relatedData" 
                :key="index" 
                class="data-item"
              >
                <span class="data-label">{{ item.label }}:</span>
                <span class="data-value">{{ item.value }}</span>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button 
          v-if="showViewButton" 
          type="primary" 
          @click="handleViewRelated"
          class="view-button"
        >
          <el-icon><View /></el-icon>
          查看关联数据
        </el-button>
        <el-button @click="handleClose" class="close-button">
          <el-icon><Close /></el-icon>
          关闭
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import { WarningFilled, View, Close } from '@element-plus/icons-vue'

export default {
  name: 'DeleteErrorDialog',
  components: {
    WarningFilled,
    View,
    Close
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    errorTitle: {
      type: String,
      default: '删除操作失败'
    },
    errorMessage: {
      type: String,
      required: true
    },
    solution: {
      type: String,
      default: ''
    },
    relatedData: {
      type: Array,
      default: () => []
    },
    showViewButton: {
      type: Boolean,
      default: true
    }
  },
  emits: ['update:visible', 'view-related', 'close'],
  data() {
    return {
      dialogVisible: this.visible,
      activeCollapse: []
    }
  },
  watch: {
    visible(newVal) {
      this.dialogVisible = newVal
    },
    dialogVisible(newVal) {
      this.$emit('update:visible', newVal)
      if (!newVal) {
        this.$emit('close')
      }
    }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
    },
    handleViewRelated() {
      this.$emit('view-related')
    }
  }
}
</script>

<style scoped>
.delete-error-dialog {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.error-content {
  text-align: center;
  padding: 20px 0;
}

.error-icon {
  margin-bottom: 16px;
}

.error-message {
  margin-bottom: 20px;
}

.error-text {
  font-size: 16px;
  font-weight: 600;
  color: #F56C6C;
  margin-bottom: 8px;
  line-height: 1.5;
}

.error-solution {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  margin-top: 8px;
}

.related-data {
  margin-top: 20px;
  border-top: 1px solid #EBEEF5;
  padding-top: 20px;
}

.data-list {
  max-height: 200px;
  overflow-y: auto;
}

.data-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #F0F0F0;
}

.data-item:last-child {
  border-bottom: none;
}

.data-label {
  font-weight: 600;
  color: #303133;
}

.data-value {
  color: #606266;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
}

.view-button {
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  border: none;
  border-radius: 4px;
  padding: 10px 20px;
  font-weight: 600;
}

.view-button:hover {
  background: linear-gradient(135deg, #337ecc 0%, #2a6eb8 100%);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.close-button {
  border-radius: 4px;
  padding: 10px 20px;
  font-weight: 600;
}

.close-button:hover {
  background-color: #F5F7FA;
  border-color: #DCDFE6;
}

/* 自定义弹窗样式 */
:deep(.delete-error-dialog) {
  border-radius: 8px;
}

:deep(.delete-error-dialog .el-dialog__header) {
  border-bottom: 1px solid #EBEEF5;
  padding: 16px 20px;
  margin: 0;
}

:deep(.delete-error-dialog .el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #F56C6C;
}

:deep(.delete-error-dialog .el-dialog__body) {
  padding: 0 20px;
}

:deep(.delete-error-dialog .el-dialog__footer) {
  border-top: 1px solid #EBEEF5;
  padding: 16px 20px;
}

:deep(.delete-error-dialog .el-collapse-item__header) {
  font-weight: 600;
  color: #409EFF;
}

:deep(.delete-error-dialog .el-collapse-item__content) {
  padding-bottom: 0;
}
</style>