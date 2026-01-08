<template>
  <view class="detail-container">
    <!-- 头部导航 -->
    <view class="header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="header-left">
        <button class="back-btn" @click="handleBack">
          <uni-icons type="back" size="28" color="#333"></uni-icons>
        </button>
      </view>
      <view class="header-title">户籍详情</view>
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
      <!-- 户籍基本信息卡片 -->
      <view class="info-card">
        <view class="card-title">
          <uni-icons type="home-filled" size="20" color="#667eea"></uni-icons>
          <text>户籍信息</text>
        </view>
        <view class="info-list">
          <view class="info-item">
            <text class="label">户籍编号</text>
            <text class="value">{{ household.householdNumber || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">户主姓名</text>
            <text class="value">{{ household.householderName || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">身份证号</text>
            <text class="value">{{ household.householderIdCard || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">人口数量</text>
            <text class="value">{{ household.populationCount || 0 }}人</text>
          </view>
        </view>
      </view>
      
      <!-- 联系方式卡片 -->
      <view class="info-card">
        <view class="card-title">
          <uni-icons type="phone" size="20" color="#667eea"></uni-icons>
          <text>联系方式</text>
        </view>
        <view class="info-list">
          <view class="info-item">
            <text class="label">联系电话</text>
            <text class="value">{{ household.phoneNumber || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">详细地址</text>
            <text class="value">{{ household.address || '未填写' }}</text>
          </view>
        </view>
      </view>
      
      <!-- 其他信息卡片 -->
      <view class="info-card">
        <view class="card-title">
          <uni-icons type="info" size="20" color="#667eea"></uni-icons>
          <text>其他信息</text>
        </view>
        <view class="info-list">
          <view class="info-item">
            <text class="label">登记日期</text>
            <text class="value">{{ household.registrationDate || '未填写' }}</text>
          </view>
          <view class="info-item" v-if="household.notes">
            <text class="label">备注</text>
            <text class="value">{{ household.notes }}</text>
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
      householdId: null,
      household: {},
      loading: false,
      statusBarHeight: 0
    }
  },
  onLoad(options) {
    this.getStatusBarHeight()
    if (options.id) {
      this.householdId = options.id
      this.loadHouseholdDetail()
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
    
    loadHouseholdDetail() {
      this.loading = true
      get(`/household/${this.householdId}`)
        .then(res => {
          if (res.code === 200) {
            this.household = res.data
          } else {
            uni.showToast({
              title: '获取详情失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('获取户籍详情失败', err)
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
        url: `/pages/household/edit?id=${this.householdId}`
      })
    },
    
    handleDelete() {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除该户籍信息吗？',
        success: (res) => {
          if (res.confirm) {
            this.loading = true
            del(`/household/delete/${this.householdId}`)
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
                console.error('删除户籍失败', err)
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

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
