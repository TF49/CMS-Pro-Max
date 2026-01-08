<template>
  <view class="setting-container">
    <!-- 头部导航 -->
    <view class="header">
      <view class="header-left">
        <button class="back-btn" @click="handleBack">
          <uni-icons type="back" size="28" color="#333"></uni-icons>
        </button>
      </view>
      <view class="header-title">设置</view>
      <view class="header-right"></view>
    </view>
    
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <uni-icons type="spinner-cycle" size="48" color="#667eea" class="loading-icon"></uni-icons>
      <text class="loading-text">加载中...</text>
    </view>
    
    <!-- 设置内容 -->
    <view v-else class="setting-content">
      <!-- 用户信息卡片 -->
      <view class="user-card">
        <view class="user-avatar">
          <uni-icons type="contact" size="64" color="#667eea"></uni-icons>
        </view>
        <view class="user-info">
          <text class="user-name">{{ userInfo.username || '未登录' }}</text>
          <text class="user-role">{{ userInfo.role === 'ADMIN' ? '管理员' : '普通用户' }}</text>
        </view>
      </view>
      
      <!-- 设置列表 -->
      <view class="setting-list">
        <!-- 账户设置 -->
        <view class="setting-section">
          <text class="section-title">账户设置</text>
          <view class="setting-item" @click="handleEditPassword">
            <view class="setting-item-left">
              <uni-icons type="locked" size="24" color="#667eea" class="setting-icon"></uni-icons>
              <text class="setting-item-text">修改密码</text>
            </view>
            <uni-icons type="arrowright" size="20" color="#999"></uni-icons>
          </view>
          <view class="setting-item" @click="handleEditProfile">
            <view class="setting-item-left">
              <uni-icons type="person" size="24" color="#667eea" class="setting-icon"></uni-icons>
              <text class="setting-item-text">编辑资料</text>
            </view>
            <uni-icons type="arrowright" size="20" color="#999"></uni-icons>
          </view>
        </view>
        
        <!-- 系统设置 -->
        <view class="setting-section">
          <text class="section-title">系统设置</text>
          <view class="setting-item" @click="handleAbout">
            <view class="setting-item-left">
              <uni-icons type="info" size="24" color="#667eea" class="setting-icon"></uni-icons>
              <text class="setting-item-text">关于我们</text>
            </view>
            <uni-icons type="arrowright" size="20" color="#999"></uni-icons>
          </view>
          <view class="setting-item" @click="handleFeedback">
            <view class="setting-item-left">
              <uni-icons type="chatbubble" size="24" color="#667eea" class="setting-icon"></uni-icons>
              <text class="setting-item-text">意见反馈</text>
            </view>
            <uni-icons type="arrowright" size="20" color="#999"></uni-icons>
          </view>
        </view>
        
        <!-- 退出登录 -->
        <button class="logout-btn" @click="handleLogout">
          <uni-icons type="exit" size="24" color="#ff4757"></uni-icons>
          <text>退出登录</text>
        </button>
        
        <!-- 版本信息 -->
        <view class="version-info">
          <text class="version-text">当前版本 v1.0.0</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { get } from '@/utils/request'

export default {
  data() {
    return {
      loading: false,
      userInfo: {
        username: '',
        role: 'USER'
      }
    }
  },
  onLoad() {
    this.loadUserInfo()
  },
  methods: {
    // 返回首页
    handleBack() {
      uni.switchTab({ url: '/pages/index/index' })
    },
    
    // 加载用户信息
    loadUserInfo() {
      this.loading = true
      // 从本地存储获取token
      const token = uni.getStorageSync('token')
      if (!token) {
        this.loading = false
        return
      }
      
      // 调用用户信息接口
      get('/api/auth/current')
        .then(res => {
          if (res.code === 200) {
            this.userInfo = res.data || { username: '', role: 'USER' }
          } else {
            uni.showToast({
              title: res.msg || res.message || '获取用户信息失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('获取用户信息失败', err)
          uni.showToast({
            title: '网络错误，请稍后重试',
            icon: 'none'
          })
        })
        .finally(() => {
          this.loading = false
        })
    },
    
    // 修改密码
    handleEditPassword() {
      uni.navigateTo({
        url: '/pages/setting/edit-password'
      })
    },
    
    // 编辑资料
    handleEditProfile() {
      uni.navigateTo({
        url: '/pages/setting/edit-profile'
      })
    },
    

    

    
    // 关于我们
    handleAbout() {
      uni.showModal({
        title: '关于我们',
        content: '人口普查管理系统 v1.0.0\n\n基于UniApp + Vue3开发\n\n© 2024 人口普查管理系统',
        showCancel: false,
        confirmText: '确定'
      })
    },
    
    // 意见反馈
    handleFeedback() {
      uni.navigateTo({
        url: '/pages/setting/feedback'
      })
    },
    
    // 退出登录
    handleLogout() {
      uni.showModal({
        title: '确认退出',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            // 清除本地存储
            uni.removeStorageSync('token')
            uni.removeStorageSync('userInfo')
            // 跳转到登录页
            uni.redirectTo({
              url: '/pages/login/login'
            })
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.setting-container {
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

.back-btn {
  background: none;
  border: none;
  padding: 10rpx;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.back-btn:hover {
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

/* 设置内容 */
.setting-content {
  padding: 20rpx;
}

/* 用户信息卡片 */
.user-card {
  background: white;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: all 0.3s ease;
}

.user-card:hover {
  transform: translateY(-2rpx);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.user-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.user-avatar:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.user-info {
  flex: 1;
  margin: 0 30rpx;
}

.user-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 10rpx;
}

.user-role {
  font-size: 24rpx;
  color: #666;
}

.edit-avatar-btn {
  background: none;
  border: none;
  padding: 10rpx;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.edit-avatar-btn:hover {
  background: rgba(0, 0, 0, 0.05);
  transform: scale(1.1);
}

/* 设置列表 */
.setting-list {
  background: white;
  border-radius: 16rpx;
  padding: 20rpx;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

/* 设置分组 */
.setting-section {
  margin-bottom: 30rpx;
}

.setting-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 24rpx;
  font-weight: bold;
  color: #999;
  margin-bottom: 20rpx;
  padding: 0 10rpx;
}

/* 设置项 */
.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 25rpx 10rpx;
  border-bottom: 1rpx solid #f0f0f0;
  transition: all 0.3s ease;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-item:hover {
  background: rgba(0, 0, 0, 0.02);
  transform: translateX(5rpx);
}

.setting-item-left {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.setting-icon {
  flex-shrink: 0;
}

.setting-item-text {
  font-size: 28rpx;
  color: #333;
}

/* 退出登录按钮 */
.logout-btn {
  background: white;
  color: #ff4757;
  border: 2rpx solid #ff4757;
  border-radius: 12rpx;
  padding: 20rpx;
  font-size: 28rpx;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  margin: 30rpx 0;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background: #ff4757;
  color: white;
  transform: scale(1.02);
  box-shadow: 0 5px 15px rgba(255, 71, 87, 0.3);
}

/* 版本信息 */
.version-info {
  text-align: center;
  padding: 20rpx 0;
}

.version-text {
  font-size: 24rpx;
  color: #999;
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

/* 响应式设计 */
/* 小屏设备 (<= 375px) */
@media (max-width: 375px) {
  .setting-content {
    padding: 16rpx;
  }
  
  .user-card {
    padding: 20rpx;
    flex-direction: column;
    text-align: center;
  }
  
  .user-info {
    margin: 20rpx 0;
  }
  
  .user-name {
    font-size: 28rpx;
  }
  
  .user-role {
    font-size: 22rpx;
  }
  
  .setting-item {
    padding: 20rpx 10rpx;
  }
  
  .setting-item-text {
    font-size: 26rpx;
  }
  
  .logout-btn {
    padding: 18rpx;
    font-size: 26rpx;
  }
}

/* 中屏设备 (376px - 414px) */
@media (min-width: 376px) and (max-width: 414px) {
  .user-name {
    font-size: 30rpx;
  }
  
  .setting-item-text {
    font-size: 28rpx;
  }
}

/* 大屏设备 (> 414px) */
@media (min-width: 415px) {
  .setting-container {
    max-width: 750rpx;
    margin: 0 auto;
  }
  
  .setting-content {
    padding: 30rpx;
  }
  
  .user-card {
    padding: 40rpx;
  }
  
  .user-name {
    font-size: 34rpx;
  }
  
  .setting-item-text {
    font-size: 30rpx;
  }
}

/* 横屏适配 */
@media (orientation: landscape) {
  .user-card {
    flex-direction: row;
    text-align: left;
  }
  
  .user-info {
    margin: 0 30rpx;
  }
}
</style>