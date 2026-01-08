<template>
  <view class="detail-container">
    <!-- 头部导航 -->
    <view class="header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="header-left">
        <button class="back-btn" @click="handleBack">
          <uni-icons type="back" size="28" color="#333"></uni-icons>
        </button>
      </view>
      <view class="header-title">医疗详情</view>
      <view class="header-right">
        <button class="edit-btn" @click="handleEdit">
          <uni-icons type="compose" size="28" color="#667eea"></uni-icons>
        </button>
        <button class="delete-btn" @click="handleDelete">
          <uni-icons type="trash" size="28" color="#ff4757"></uni-icons>
        </button>
      </view>
    </view>
    
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <uni-icons type="spinner-cycle" size="48" color="#667eea" class="loading-icon"></uni-icons>
      <text class="loading-text">加载中...</text>
    </view>
    
    <!-- 详情内容 -->
    <view v-else class="content">
      <!-- 基本信息卡片 -->
      <view class="info-card">
        <view class="card-title">
          <uni-icons type="medkit" size="20" color="#667eea"></uni-icons>
          <text>基本信息</text>
        </view>
        <view class="info-list">
          <view class="info-item">
            <text class="label">姓名</text>
            <text class="value">{{ medicalInfo.residentName || '未填写' }}</text>
          </view>

          <view class="info-item">
            <text class="label">医院名称</text>
            <text class="value">{{ medicalInfo.hospital || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">就诊日期</text>
            <text class="value">{{ formatDate(medicalInfo.lastCheckupDate) }}</text>
          </view>
          <view class="info-item" v-if="medicalInfo.medicalType === 'HOSPITALIZATION'">
            <text class="label">出院日期</text>
            <text class="value">{{ formatDate(medicalInfo.dischargeDate) }}</text>
          </view>
        </view>
      </view>
        
        <!-- 诊断信息卡片 -->
        <view class="info-card">
          <view class="card-title">
            <uni-icons type="document" size="20" color="#667eea"></uni-icons>
            <text>诊断信息</text>
          </view>
          <view class="info-list">
            <view class="info-item">
            <text class="label">诊断结果</text>
            <text class="value">{{ medicalInfo.diagnosis || '未填写' }}</text>
          </view>
            <view class="info-item">
            <text class="label">主治医生</text>
            <text class="value">{{ medicalInfo.doctor || '未填写' }}</text>
          </view>
            <view class="info-item">
            <text class="label">科室</text>
            <text class="value">{{ medicalInfo.department || '未填写' }}</text>
          </view>

          </view>
        </view>
        
        <!-- 备注信息卡片 -->
        <view class="info-card">
          <view class="card-title">
            <uni-icons type="info" size="20" color="#667eea"></uni-icons>
            <text>备注信息</text>
          </view>
          <view class="info-list">
            <view class="info-item">
            <text class="label">备注</text>
            <text class="value">{{ medicalInfo.notes || '未填写' }}</text>
          </view>
          </view>
        </view>
        
        <!-- 操作日志卡片 -->
        <view class="info-card">
          <view class="card-title">
            <uni-icons type="time" size="20" color="#667eea"></uni-icons>
            <text>操作日志</text>
          </view>
          <view class="info-list">
            <view class="info-item">
              <text class="label">创建时间</text>
              <text class="value">{{ formatDateTime(medicalInfo.createTime) }}</text>
            </view>
            <view class="info-item">
              <text class="label">更新时间</text>
              <text class="value">{{ formatDateTime(medicalInfo.updateTime) }}</text>
            </view>
          </view>
        </view>
    </view>
  </view>
</template>

<script>
import { get, del } from '@/utils/request'

export default {
  data() {
    return {
      // 加载状态
      loading: false,
      // 医疗记录ID
      medicalId: '',
      // 医疗记录详情
      medicalInfo: {
        id: '',
        residentId: '',
        residentName: '',
        medicalType: 'VISIT',
        hospitalName: '',
        doctorName: '',
        department: '',
        visitDate: '',
        dischargeDate: '',
        diagnosis: '',
        description: '',
        treatmentPlan: '',
        insuranceType: '',
        medicalExpense: '0.00',
        insuranceReimbursement: '0.00',
        selfPayAmount: '0.00',
        remark: '',
        createTime: '',
        updateTime: ''
      },
      statusBarHeight: 0
    }
  },
  onLoad(options) {
    this.getStatusBarHeight()
    if (options && options.id) {
      this.medicalId = options.id
      this.medicalId = options.id
      // 加载医疗记录详情
      this.loadMedicalDetail()
    }
  },
  methods: {
    // 返回上一页
    handleBack() {
      const pages = getCurrentPages()
      if (pages.length > 1) {
        uni.navigateBack()
      } else {
        // 如果是第一个页面，跳转到首页
        uni.switchTab({ url: '/pages/index/index' })
      }
    },
    
    // 获取状态栏高度
    getStatusBarHeight() {
      // #ifdef MP-WEIXIN
      const systemInfo = uni.getSystemInfoSync()
      this.statusBarHeight = systemInfo.statusBarHeight || 0
      // #endif
    },
    
    // 编辑医疗记录
    handleEdit() {
      uni.navigateTo({
        url: `/pages/medical/edit?id=${this.medicalId}`
      })
    },
    
    // 删除医疗记录
    handleDelete() {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这条医疗记录吗？',
        success: (res) => {
          if (res.confirm) {
            this.loading = true
            // 调用删除医疗记录API
            del(`/medical/delete/${this.medicalId}`)
              .then(res => {
                if (res.code === 200) {
                  uni.showToast({
                    title: '删除成功',
                    icon: 'success'
                  })
                  setTimeout(() => {
                    uni.navigateBack()
                  }, 1500)
                } else {
                  uni.showToast({
                    title: res.message || '删除失败',
                    icon: 'none'
                  })
                }
              })
              .catch(err => {
                console.error('删除医疗记录失败', err)
                uni.showToast({
                  title: '网络错误，请稍后重试',
                  icon: 'none'
                })
              })
              .finally(() => {
                this.loading = false
              })
          }
        }
      })
    },
    
    // 加载医疗记录详情
    loadMedicalDetail() {
      this.loading = true
      // 调用医疗记录详情API
      get(`/medical/${this.medicalId}`)
        .then(res => {
          if (res.code === 200 && res.data) {
            const data = res.data
            // 填充医疗信息，处理空值
            this.medicalInfo = {
              id: data.id || '',
              residentId: data.residentId || '',
              residentName: data.residentName || '',
              hospital: data.hospital || '',
              doctor: data.doctor || '',
              department: data.department || '',
              lastCheckupDate: data.lastCheckupDate || '',
              diagnosis: data.diagnosis || '',
              insuranceType: data.insuranceType || '',
              notes: data.notes || '',
              createTime: data.createTime || '',
              updateTime: data.updateTime || ''
            }
          } else {
            uni.showToast({
              title: '获取详情失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('获取医疗记录详情失败', err)
          uni.showToast({
            title: '网络错误，请稍后重试',
            icon: 'none'
          })
        })
        .finally(() => {
          this.loading = false
        })
    },
    
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return '未填写'
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    
    // 格式化日期时间
    formatDateTime(dateTimeStr) {
      if (!dateTimeStr) return '未填写'
      const date = new Date(dateTimeStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hour = String(date.getHours()).padStart(2, '0')
      const minute = String(date.getMinutes()).padStart(2, '0')
      const second = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}:${second}`
    }
  }
}
</script>

<style scoped>
.detail-container {
  background: #f5f7fa;
  min-height: 100vh;
}

/* 头部导航 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 88rpx;
  background: white;
  padding: 0 20rpx;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left, .header-right {
  display: flex;
  align-items: center;
}

.back-btn, .edit-btn, .delete-btn {
  background: none;
  border: none;
  padding: 10rpx;
  margin: 0 10rpx;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.back-btn:hover, .edit-btn:hover, .delete-btn:hover {
  background: rgba(0, 0, 0, 0.05);
  transform: scale(1.1);
}

.header-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.loading-icon {
  animation: rotate 1s linear infinite;
  margin-bottom: 20rpx;
}

.loading-text {
  font-size: 28rpx;
  color: #666;
}

/* 内容区域 */
.content {
  padding: 20rpx;
}

/* 信息卡片 */
.info-card {
  background: white;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.info-card:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
}

/* 卡片标题 */
.card-title {
  display: flex;
  align-items: center;
  gap: 10rpx;
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

/* 信息列表 */
.info-list {
  display: flex;
  flex-direction: column;
  gap: 25rpx;
}

/* 信息项 */
.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10rpx;
}

.label {
  font-size: 26rpx;
  color: #666;
  font-weight: 500;
}

.value {
  font-size: 26rpx;
  color: #333;
  font-weight: 600;
  text-align: right;
  flex: 1;
  margin-left: 30rpx;
  word-break: break-all;
}

/* 状态标签 */
.status-tag {
  padding: 5rpx 15rpx;
  border-radius: 15rpx;
  font-size: 20rpx;
  font-weight: bold;
  display: inline-block;
}

/* 就诊类型标签样式 */
.tag-hospitalization {
  background: #ffebee;
  color: #c62828;
}

.tag-visit {
  background: #e8f5e8;
  color: #2e7d32;
}

/* 动画 */
@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>