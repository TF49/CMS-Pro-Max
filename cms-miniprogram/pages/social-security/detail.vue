<template>
  <view class="detail-container">
    <!-- 头部导航 -->
    <view class="header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="header-left">
        <button class="back-btn" @click="handleBack">
          <uni-icons type="back" size="28" color="#333"></uni-icons>
        </button>
      </view>
      <view class="header-title">社保详情</view>
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
      <!-- 基本信息 -->
      <view class="info-card">
        <view class="card-title">
          <uni-icons type="person" size="20" color="#667eea"></uni-icons>
          <text>基本信息</text>
        </view>
        <view class="info-list">
          <view class="info-item">
            <text class="label">居民姓名</text>
            <text class="value">{{ socialSecurity.residentName || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">身份证号</text>
            <text class="value">{{ socialSecurity.idCard || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">参保状态</text>
            <view class="value">
              <view class="status-tag" :class="getStatusClass(socialSecurity.insuranceStatus)">
                {{ socialSecurity.insuranceStatus || '未知' }}
              </view>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 保险信息 -->
      <view class="info-card">
        <view class="card-title">
          <uni-icons type="medal" size="20" color="#667eea"></uni-icons>
          <text>保险信息</text>
        </view>
        <view class="info-list">
          <view class="info-item">
            <text class="label">养老保险</text>
            <text class="value">{{ socialSecurity.pensionInsurance || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">医疗保险</text>
            <text class="value">{{ socialSecurity.medicalInsurance || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">失业保险</text>
            <text class="value">{{ socialSecurity.unemploymentInsurance || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">工伤保险</text>
            <text class="value">{{ socialSecurity.workInjuryInsurance || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">生育保险</text>
            <text class="value">{{ socialSecurity.maternityInsurance || '无' }}</text>
          </view>
        </view>
      </view>
      
      <!-- 缴费信息 -->
      <view class="info-card">
        <view class="card-title">
          <uni-icons type="wallet" size="20" color="#667eea"></uni-icons>
          <text>缴费信息</text>
        </view>
        <view class="info-list">
          <view class="info-item">
            <text class="label">缴费金额</text>
            <text class="value amount">¥{{ socialSecurity.paymentAmount || 0 }}/月</text>
          </view>
          <view class="info-item">
            <text class="label">参保日期</text>
            <text class="value">{{ socialSecurity.startDate || '未填写' }}</text>
          </view>
          <view class="info-item" v-if="socialSecurity.endDate">
            <text class="label">停保日期</text>
            <text class="value">{{ socialSecurity.endDate }}</text>
          </view>
          <view class="info-item" v-if="socialSecurity.notes">
            <text class="label">备注</text>
            <text class="value">{{ socialSecurity.notes }}</text>
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
      socialSecurityId: null,
      socialSecurity: {},
      loading: false,
      statusBarHeight: 0
    }
  },
  onLoad(options) {
    this.getStatusBarHeight()
    if (options.id) {
      this.socialSecurityId = options.id
      this.loadDetail()
    }
  },
  methods: {
    handleBack() {
      const pages = getCurrentPages()
      if (pages.length > 1) {
        uni.navigateBack()
      } else {
        // 如果是第一个页面，跳转到首页
        uni.switchTab({ url: '/pages/index/index' })
      }
    },
    
    getStatusBarHeight() {
      // #ifdef MP-WEIXIN
      const systemInfo = uni.getSystemInfoSync()
      this.statusBarHeight = systemInfo.statusBarHeight || 0
      // #endif
    },
    
    loadDetail() {
      this.loading = true
      get(`/social-security/${this.socialSecurityId}`)
        .then(res => {
          if (res.code === 200) {
            this.socialSecurity = res.data
          } else {
            uni.showToast({
              title: '获取详情失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('获取社保详情失败', err)
          uni.showToast({
            title: '网络错误',
            icon: 'none'
          })
        })
        .finally(() => {
          this.loading = false
        })
    },
    
    handleEdit() {
      uni.navigateTo({
        url: `/pages/social-security/edit?id=${this.socialSecurityId}`
      })
    },
    
    handleDelete() {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除该社保信息吗？',
        success: (res) => {
          if (res.confirm) {
            this.loading = true
            del(`/social-security/delete/${this.socialSecurityId}`)
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
                    title: '删除失败',
                    icon: 'none'
                  })
                }
              })
              .catch(err => {
                console.error('删除社保失败', err)
                uni.showToast({
                  title: '网络错误',
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
    
    getStatusClass(status) {
      const map = {
        '正常参保': 'status-normal',
        '暂停参保': 'status-paused',
        '停止参保': 'status-stopped'
      }
      return map[status] || 'status-unknown'
    }
  }
}
</script>

<style scoped>
.detail-container {
  background: #f5f7fa;
  min-height: 100vh;
}

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

.content {
  padding: 20rpx;
}

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

.info-list {
  display: flex;
  flex-direction: column;
  gap: 25rpx;
}

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

.value.amount {
  color: #667eea;
  font-size: 28rpx;
}

.status-tag {
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  font-weight: bold;
  display: inline-block;
}

.status-normal {
  background: #d4edda;
  color: #155724;
}

.status-paused {
  background: #fff3cd;
  color: #856404;
}

.status-stopped {
  background: #f8d7da;
  color: #721c24;
}

.status-unknown {
  background: #e2e3e5;
  color: #383d41;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
