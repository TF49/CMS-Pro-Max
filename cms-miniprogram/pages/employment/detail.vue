<template>
  <view class="detail-container">
    <!-- 头部导航 -->
    <view class="header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="header-left">
        <button class="back-btn" @click="handleBack">
          <uni-icons type="back" size="28" color="#333"></uni-icons>
        </button>
      </view>
      <view class="header-title">就业详情</view>
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
          <uni-icons type="briefcase" size="20" color="#667eea"></uni-icons>
          <text>基本信息</text>
        </view>
        <view class="info-list">
          <view class="info-item">
            <text class="label">姓名</text>
            <text class="value">{{ employmentInfo.residentName || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">就业状态</text>
            <text class="value">{{ employmentInfo.employmentStatus || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">单位名称</text>
            <text class="value">{{ employmentInfo.companyName || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">职位</text>
            <text class="value">{{ employmentInfo.position || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">入职时间</text>
            <text class="value">{{ formatDate(employmentInfo.startDate) }}</text>
          </view>
          <view class="info-item">
            <text class="label">离职时间</text>
            <text class="value">{{ employmentInfo.endDate ? formatDate(employmentInfo.endDate) : '无' }}</text>
          </view>
        </view>
      </view>
      
      <!-- 工作信息卡片 -->
      <view class="info-card">
        <view class="card-title">
          <uni-icons type="location" size="20" color="#667eea"></uni-icons>
          <text>工作信息</text>
        </view>
        <view class="info-list">
          <view class="info-item">
            <text class="label">行业类型</text>
            <text class="value">{{ employmentInfo.industryType || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">薪资水平</text>
            <text class="value">{{ employmentInfo.salaryRange || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">合同类型</text>
            <text class="value">{{ employmentInfo.contractType || '无' }}</text>
          </view>
        </view>
      </view>
      
      <!-- 备注信息卡片 -->
      <view class="info-card">
        <view class="card-title">
          <uni-icons type="compose" size="20" color="#667eea"></uni-icons>
          <text>备注信息</text>
        </view>
        <view class="info-list">
          <view class="info-item">
            <text class="label">备注</text>
            <text class="value">{{ employmentInfo.remark || '无' }}</text>
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
            <text class="value">{{ formatDateTime(employmentInfo.createTime) }}</text>
          </view>
          <view class="info-item">
            <text class="label">更新时间</text>
            <text class="value">{{ formatDateTime(employmentInfo.updateTime) }}</text>
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
      // 就业记录ID
      employmentId: '',
      // 就业记录详情
      employmentInfo: {
        id: '',
        residentId: '',
        residentName: '',
        employmentStatus: 'EMPLOYED',
        companyName: '',
        position: '',
        startDate: '',
        endDate: '',
        industryType: '',
        workLocation: '',
        contact: '',
        salaryRange: '',
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
      this.employmentId = options.id
      this.employmentId = options.id
      // 加载就业记录详情
      this.loadEmploymentDetail()
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
    
    // 编辑就业记录
    handleEdit() {
      uni.navigateTo({
        url: `/pages/employment/edit?id=${this.employmentId}`
      })
    },
    
    // 删除就业记录
    handleDelete() {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这条就业记录吗？',
        success: (res) => {
          if (res.confirm) {
            this.loading = true
            // 调用删除就业记录API
            del(`/employment/delete/${this.employmentId}`)
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
                console.error('删除就业记录失败', err)
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
    
    // 加载就业记录详情
    loadEmploymentDetail() {
      this.loading = true
      // 调用就业记录详情API
      get(`/employment/${this.employmentId}`)
        .then(res => {
          if (res.code === 200 && res.data) {
            const data = res.data
            // 映射后端字段到前端显示字段，处理可能的不同数据结构
            this.employmentInfo = {
              ...data,
              // 映射就业状态，处理不同字段名和默认值
              employmentStatus: data.employmentStatus || data.status || '无',
              // 映射单位名称，处理不同字段名和默认值
              companyName: data.company || data.companyName || '无',
              // 映射行业类型，处理不同字段名和默认值
              industryType: data.industry || data.industryType || '无',
              // 映射薪资水平，处理不同字段名和默认值
              salaryRange: data.salary || data.salaryRange || '无',
              // 映射备注，处理不同字段名和默认值
              remark: data.notes || data.remark || '无'
            }
          } else {
            uni.showToast({
              title: '获取详情失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('获取就业记录详情失败', err)
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
      if (!dateStr) return '无'
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    
    // 格式化日期时间
    formatDateTime(dateTimeStr) {
      if (!dateTimeStr) return '无'
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
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
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

.tag-employed {
  background: #e8f5e8;
  color: #4caf50;
}

.tag-unemployed {
  background: #ffebee;
  color: #f44336;
}

.tag-text {
  font-size: 20rpx;
  font-weight: bold;
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