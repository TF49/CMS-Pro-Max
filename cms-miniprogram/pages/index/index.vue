<template>
  <view class="index-container">
    <!-- 顶部动态背景 -->
    <view class="top-gradient"></view>
    <view class="top-pattern"></view>
    
    <!-- 头部区域 -->
    <view class="header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <!-- 左侧Logo和标题 -->
      <view class="header-left">
        <!-- Logo -->
        <view class="app-logo animate-float">
          <uni-icons type="contact" size="36" color="#ffffff"></uni-icons>
        </view>
        
        <!-- 标题区域 -->
        <view class="header-content">
          <view class="main-title-row">
            <text class="app-title animate-fade-in">人口普查管理系统</text>
          </view>
          <view class="sub-title-row">
            <text class="welcome-text animate-fade-in-delay">欢迎回来</text>
            <text class="date-text animate-fade-in-delay-2">{{ currentDate }}</text>
          </view>
        </view>
      </view>
      
      <!-- 右侧功能按钮 -->
      <view class="header-right">

        
        <!-- 设置按钮 -->
        <navigator url="/pages/setting/setting" open-type="switchTab" class="header-btn btn-settings">
          <uni-icons type="gear" size="28" color="#ffffff"></uni-icons>
        </navigator>
        
        <!-- 用户头像 -->
        <view class="avatar animate-pulse" @click="handleAvatarClick">
          <uni-icons type="person" size="26" color="#ffffff"></uni-icons>
          <view class="avatar-badge"></view>
        </view>
      </view>
    </view>
    
    <!-- 轮播图区域 -->
    <view class="swiper-container animate-slide-up">
      <swiper 
        class="banner-swiper" 
        indicator-dots 
        autoplay 
        interval="3000" 
        duration="1000"
        circular
        indicator-active-color="#667eea"
      >
        <swiper-item>
          <view class="swiper-item">
            <view class="swiper-icon-container">
              <uni-icons type="chart" size="60" color="#667eea" class="swiper-icon"></uni-icons>
              <view class="icon-glow" style="background: rgba(102, 126, 234, 0.3);"></view>
            </view>
            <text class="swiper-title">数据概览</text>
            <text class="swiper-desc">全面了解人口数据</text>
          </view>
        </swiper-item>
        <swiper-item>
          <view class="swiper-item">
            <view class="swiper-icon-container">
              <uni-icons type="people" size="60" color="#764ba2" class="swiper-icon"></uni-icons>
              <view class="icon-glow" style="background: rgba(118, 75, 162, 0.3);"></view>
            </view>
            <text class="swiper-title">居民管理</text>
            <text class="swiper-desc">高效管理居民信息</text>
          </view>
        </swiper-item>
        <swiper-item>
          <view class="swiper-item">
            <view class="swiper-icon-container">
              <uni-icons type="stats-chart" size="60" color="#f093fb" class="swiper-icon"></uni-icons>
              <view class="icon-glow" style="background: rgba(240, 147, 251, 0.3);"></view>
            </view>
            <text class="swiper-title">统计分析</text>
            <text class="swiper-desc">深入分析数据趋势</text>
          </view>
        </swiper-item>
      </swiper>
    </view>
    
    <!-- 功能模块区域 -->
    <view class="module-section animate-slide-up-delay-3">
      <view class="section-title">
        <text class="title-text">功能模块</text>
        <view class="divider"></view>
      </view>
      
      <view class="module-grid">
        <navigator 
          :url="module.url" 
          open-type="navigate"
          class="module-item modern hover-lift" 
          v-for="(module, index) in modules" 
          :key="index">
          <view class="module-icon-container">
            <view 
              class="module-icon modern" 
              :class="module.bgClass"
            >
              <uni-icons :type="module.icon" size="32" color="#fff"></uni-icons>
              <view class="module-icon-glow" :style="{ background: module.glowColor }"></view>
            </view>
          </view>
          <text class="module-title">{{ module.title }}</text>
          <text class="module-desc">{{ module.desc }}</text>
          <view class="module-arrow">
            <uni-icons type="arrowright" size="16" color="#999"></uni-icons>
          </view>
        </navigator>
      </view>
    </view>
    
    <!-- 快速入口区域 -->
    <view class="quick-actions modern animate-slide-up-delay-4">
      <view class="action-item modern hover-lift" v-for="(action, index) in quickActions" :key="index" @click="handleQuickAction(action)">
        <view class="action-icon-container">
          <view 
            class="action-icon modern" 
            :class="action.bgClass"
          >
            <uni-icons :type="action.icon" size="24" color="#fff"></uni-icons>
          </view>
        </view>
        <text class="action-text">{{ action.text }}</text>
      </view>
    </view>
    
    <!-- 装饰元素 -->
    <view class="decor-element decor-1"></view>
    <view class="decor-element decor-2"></view>
    <view class="decor-element decor-3"></view>
  </view>
</template>

<script>
import { get } from '@/utils/request'

export default {
  data() {
    return {
      totalResidents: 0,
      totalHouseholds: 0,
      totalEmployed: 0,
      totalSocialSecurity: 0,
      currentDate: '',
      // 状态栏高度
      statusBarHeight: 0,
      // 导航栏高度
      navBarHeight: 0,
      // 轮播图数据
      swiperList: [
        { id: 1, title: '数据概览', desc: '全面了解社区人口数据', icon: 'chart', color: '#005bea' },
        { id: 2, title: '居民管理', desc: '高效管理居民信息', icon: 'people', color: '#00c6fb' },
        { id: 3, title: '统计分析', desc: '深入分析数据趋势', icon: 'stats-chart', color: '#00e5ff' }
      ],

      // 功能模块数据
      modules: [
        { title: '户籍管理', desc: '管理户籍相关信息', icon: 'document', bgClass: 'bg-blue', glowColor: 'rgba(0, 91, 234, 0.3)', url: '/pages/household/list' },
        { title: '居民管理', desc: '管理居民基本信息', icon: 'person', bgClass: 'bg-cyan', glowColor: 'rgba(0, 198, 251, 0.3)', url: '/pages/resident/list' },
        { title: '教育管理', desc: '管理教育相关信息', icon: 'book', bgClass: 'bg-teal', glowColor: 'rgba(0, 229, 255, 0.3)', url: '/pages/education/list' },
        { title: '医疗管理', desc: '管理医疗相关信息', icon: 'heart', bgClass: 'bg-indigo', glowColor: 'rgba(99, 102, 241, 0.3)', url: '/pages/medical/list' },
        { title: '就业管理', desc: '管理就业相关信息', icon: 'briefcase', bgClass: 'bg-blue-light', glowColor: 'rgba(79, 172, 254, 0.3)', url: '/pages/employment/list' },
        { title: '社保管理', desc: '管理社保相关信息', icon: 'medal', bgClass: 'bg-sky', glowColor: 'rgba(0, 242, 254, 0.3)', url: '/pages/social-security/list' },
        { title: '房产管理', desc: '管理房产相关信息', icon: 'home', bgClass: 'bg-azure', glowColor: 'rgba(0, 123, 255, 0.3)', url: '/pages/property/list' },
        { title: '车辆管理', desc: '管理车辆相关信息', icon: 'car', bgClass: 'bg-green', glowColor: 'rgba(0, 229, 255, 0.3)', url: '/pages/vehicle/list' }
      ],
      // 快速入口数据
      quickActions: [
        { text: '刷新数据', icon: 'refresh', bgClass: 'bg-teal', action: 'refresh' },
        { text: '设置', icon: 'gear', bgClass: 'bg-indigo', url: '/pages/setting/setting' }
      ],

    }
  },
  onLoad() {
    this.checkLoginStatus()
    this.loadStatistics()
    this.getCurrentDate()
    this.getStatusBarHeight()
  },
  computed: {
    // 格式化数字，添加千分位分隔符
    formattedResidents() {
      return this.formatNumber(this.totalResidents)
    },
    formattedHouseholds() {
      return this.formatNumber(this.totalHouseholds)
    },
    formattedEmployed() {
      return this.formatNumber(this.totalEmployed)
    },
    formattedSocialSecurity() {
      return this.formatNumber(this.totalSocialSecurity)
    }
  },
  onShow() {
    this.checkLoginStatus()
    // 页面显示时刷新数据
    this.loadStatistics()
  },
  methods: {
    // 检查登录状态
    checkLoginStatus() {
      const token = uni.getStorageSync('token')
      if (!token) {
        // 未登录，跳转到登录页面
        uni.redirectTo({
          url: '/pages/login/login'
        })
        return false
      }
      return true
    },
    // 获取状态栏高度
    getStatusBarHeight() {
      // #ifdef MP-WEIXIN
      const systemInfo = uni.getSystemInfoSync()
      this.statusBarHeight = systemInfo.statusBarHeight || 0
      this.navBarHeight = this.statusBarHeight + 44
      // #endif
    },
    // 格式化数字
    formatNumber(num) {
      if (num === undefined || num === null) return '0'
      return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    },
    


    loadStatistics() {
      // 获取统计数据
      const params = {}
      
      get('/api/statistics/summary', params)
        .then(res => {
          if (res.code === 200) {
            this.totalResidents = res.data.residentCount || 0
            this.totalHouseholds = res.data.householdCount || 0
            this.totalEmployed = res.data.employmentCount || 0
            this.totalSocialSecurity = res.data.socialSecurityCount || 0
          }
        })
        .catch(err => {
          console.error('获取统计数据失败', err)
        })
    },
    getCurrentDate() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      const dayOfWeek = days[now.getDay()]
      this.currentDate = `${year}年${month}月${day}日 ${dayOfWeek}`
    },
    // 刷新数据
    refreshData() {
      this.loadStatistics()
      uni.showToast({
        title: '数据已刷新',
        icon: 'success'
      })
    },
    
    // 快速入口点击
    handleQuickAction(action) {
      if (action.url) {
        // 判断是否是 tabbar 页面，如果是则使用 switchTab，否则使用 navigateTo
        if (action.url === '/pages/setting/setting') {
          uni.switchTab({
            url: action.url
          })
        } else {
          uni.navigateTo({
            url: action.url
          })
        }
      } else if (action.action === 'refresh') {
        this.refreshData()
      }
    },
    
    // 头像点击事件
    handleAvatarClick() {
      uni.navigateTo({
        url: '/pages/setting/edit-profile'
      })
    },
    

  }
}
</script>

<style>
/* 全局容器 */
.index-container {
  padding: 0;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4edf5 100%);
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
  overflow-y: auto;
}

/* 顶部动态背景 */
.top-gradient {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 400rpx;
  background: linear-gradient(135deg, #005bea 0%, #00c6fb 100%);
  z-index: -1;
  animation: gradientShift 15s ease infinite;
}

.top-pattern {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 400rpx;
  background-image: radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
                    radial-gradient(circle at 80% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
  z-index: -1;
}

/* 头部区域 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 30rpx;
  color: white;
  position: relative;
  z-index: 1;
  backdrop-filter: blur(20rpx);
  background: rgba(0, 91, 234, 0.1);
  min-height: 120rpx;
  border-bottom-left-radius: 30rpx;
  border-bottom-right-radius: 30rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 91, 234, 0.15);
  border-bottom: 2rpx solid rgba(255, 255, 255, 0.1);
}

/* 左侧Logo和标题 */
.header-left {
  display: flex;
  align-items: center;
  gap: 20rpx;
  flex: 1;
}

/* 应用Logo */
.app-logo {
  width: 80rpx;
  height: 80rpx;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
  backdrop-filter: blur(10px);
  border: 2rpx solid rgba(255, 255, 255, 0.3);
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.app-logo::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.15), transparent);
}

.app-logo:hover {
  transform: scale(1.05);
  box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
}

/* 标题区域 */
.header-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex: 1;
}

/* 主标题行 */
.main-title-row {
  margin-bottom: 8rpx;
}

/* 应用标题 */
.app-title {
  font-size: 32rpx;
  font-weight: bold;
  color: white;
  text-shadow: 0 3px 6px rgba(0, 0, 0, 0.3);
  display: block;
  letter-spacing: 1rpx;
  line-height: 1.2;
}

/* 副标题行 */
.sub-title-row {
  display: flex;
  align-items: center;
  gap: 15rpx;
  flex-wrap: wrap;
}

.welcome-text {
  font-size: 24rpx;
  font-weight: 600;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  letter-spacing: 0.5rpx;
}

.date-text {
  font-size: 22rpx;
  opacity: 1;
  background: rgba(255, 255, 255, 0.25);
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
  backdrop-filter: blur(10px);
  box-shadow: 0 3rpx 10rpx rgba(0, 0, 0, 0.2);
  color: white;
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  border: 1rpx solid rgba(255, 255, 255, 0.3);
}

/* 右侧功能按钮 */
.header-right {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

/* 头部按钮 */
.header-btn {
  position: relative;
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(10px);
  border: 1rpx solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.header-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-3rpx) scale(1.05);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.25);
}

/* 徽章样式 */
.badge {
  position: absolute;
  top: -6rpx;
  right: -6rpx;
  min-width: 26rpx;
  height: 26rpx;
  border-radius: 13rpx;
  background: linear-gradient(135deg, #ff4757, #ff3742);
  color: white;
  font-size: 16rpx;
  font-weight: bold;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0 10rpx;
  border: 2rpx solid white;
  box-shadow: 0 3rpx 10rpx rgba(0, 0, 0, 0.3);
  animation: bounce 1s ease-in-out infinite;
}

/* 用户头像 */
.avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  justify-content: center;
  align-items: center;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.avatar::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.avatar:hover::before {
  left: 100%;
}

.avatar:hover {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  background: rgba(255, 255, 255, 0.25);
}

/* 弹跳动画 */
@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5rpx);
  }
}

.avatar-badge {
  position: absolute;
  top: 5rpx;
  right: 5rpx;
  width: 20rpx;
  height: 20rpx;
  background: linear-gradient(135deg, #ff4757, #ff3742);
  border-radius: 50%;
  border: 2rpx solid white;
  animation: pulse 2s ease-in-out infinite;
}

/* 轮播图容器 */
.swiper-container {
  padding: 0 30rpx 30rpx;
  position: relative;
  z-index: 1;
  margin-top: -20rpx;
}

.banner-swiper {
  height: 200rpx;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.swiper-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(245, 247, 250, 0.9) 100%);
  border-radius: 20rpx;
  padding: 40rpx;
  text-align: center;
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
}

.swiper-icon-container {
  position: relative;
  margin-bottom: 20rpx;
}

.swiper-icon {
  position: relative;
  z-index: 1;
  animation: float 3s ease-in-out infinite;
}

.icon-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  filter: blur(20rpx);
  opacity: 0.5;
  animation: pulse 2s ease-in-out infinite;
}

.swiper-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #2d3748;
  margin-bottom: 10rpx;
  position: relative;
  z-index: 1;
}

.swiper-desc {
  font-size: 24rpx;
  color: #718096;
  position: relative;
  z-index: 1;
}

/* 统计卡片区域 */
.stats {
  padding: 0 30rpx 30rpx;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
  position: relative;
  z-index: 1;
}

.stat-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(245, 247, 250, 0.9) 100%);
  border-radius: 20rpx;
  padding: 30rpx 25rpx;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(102, 126, 234, 0.1);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4rpx;
  background: linear-gradient(90deg, #667eea, #764ba2, #f093fb);
}

.stat-card:hover {
  transform: translateY(-8rpx) scale(1.02);
  box-shadow: 0 15px 40px rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.3);
}

.stat-icon-top {
  width: 60rpx;
  height: 60rpx;
  border-radius: 16rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20rpx;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-icon-top::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.3), transparent);
}

.stat-card:hover .stat-icon-top {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.bg-blue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.bg-green {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.bg-purple {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

.bg-orange {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.bg-pink {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-label {
  font-size: 24rpx;
  color: #666;
  font-weight: 500;
}

.stat-value {
  font-size: 40rpx;
  font-weight: bold;
  color: #2d3748;
  display: block;
  margin-bottom: 15rpx;
  animation: countUp 2s ease-out;
}

.stat-change {
  font-size: 22rpx;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.stat-change.up {
  color: #4facfe;
}

.stat-change.down {
  color: #f5576c;
}

.change-icon {
  margin-right: 4rpx;
}

.change-text {
  font-weight: bold;
}

.change-time {
  font-size: 20rpx;
  color: #999;
  margin-left: 8rpx;
}

/* 可视化区域 */
.visualization-section {
  padding: 0 30rpx 30rpx;
  position: relative;
  z-index: 1;
}

.section-title {
  margin-bottom: 30rpx;
  position: relative;
}

.title-text {
  font-size: 34rpx;
  font-weight: 700;
  color: #2d3748;
  display: block;
  margin-bottom: 15rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.divider {
  height: 8rpx;
  background: linear-gradient(90deg, #667eea, #764ba2, #f093fb);
  border-radius: 4rpx;
  width: 100rpx;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.divider::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.5), transparent);
  animation: shimmer 2s infinite;
}

.section-title:hover .divider {
  width: 150rpx;
}

/* 图表卡片 */
.chart-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(245, 247, 250, 0.9) 100%);
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 25rpx;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(102, 126, 234, 0.1);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
}

.chart-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4rpx;
  background: linear-gradient(90deg, #667eea, #764ba2, #f093fb);
}

.chart-card:hover {
  box-shadow: 0 15px 40px rgba(102, 126, 234, 0.15);
  border-color: rgba(102, 126, 234, 0.3);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25rpx;
}

.chart-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #2d3748;
}

.chart-content {
  height: 250rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

/* 年龄分布图表 */
.age-distribution-modern {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  width: 100%;
  height: 100%;
  padding: 20rpx 0;
}

.age-group-modern {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  margin: 0 10rpx;
}

.age-bar-container-modern {
  width: 60rpx;
  height: 180rpx;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: center;
  position: relative;
}

.age-bar-modern {
  width: 50rpx;
  border-radius: 10rpx 10rpx 0 0;
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  animation: barGrow 1s ease-out forwards;
  transform-origin: bottom;
  transform: scaleY(0);
}

.age-bar-glow {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 10rpx 10rpx 0 0;
  opacity: 0.6;
  animation: glowPulse 2s ease-in-out infinite;
}

.age-label-modern {
  font-size: 22rpx;
  color: #666;
  margin-top: 15rpx;
  font-weight: 500;
}

.age-value-modern {
  font-size: 20rpx;
  color: #999;
  margin-top: 8rpx;
  font-weight: 600;
}

/* 趋势图表 */
.trend-chart-modern {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.trend-labels-modern {
  display: flex;
  justify-content: space-around;
  width: 100%;
  margin-top: 20rpx;
}

.trend-label-modern {
  font-size: 20rpx;
  color: #999;
  font-weight: 500;
}

.data-point {
  position: relative;
  transition: all 0.3s ease;
}

.data-point:hover {
  transform: scale(1.3);
  filter: brightness(1.2);
}

.data-point-glow {
  animation: glowExpand 2s ease-in-out infinite;
}

/* 功能模块区域 */
.module-section {
  padding: 0 30rpx 30rpx;
  position: relative;
  z-index: 1;
}

.module-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.module-item {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(245, 247, 250, 0.9) 100%);
  border-radius: 20rpx;
  padding: 35rpx 25rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(102, 126, 234, 0.1);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
}

.module-icon-container {
  position: relative;
  margin-bottom: 20rpx;
}

.module-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 20rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
  overflow: hidden;
}

.module-icon::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.3), transparent);
}

.module-icon-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  filter: blur(25rpx);
  opacity: 0.3;
  transition: all 0.3s ease;
}

.module-item:hover .module-icon-glow {
  opacity: 0.5;
}

.module-item:hover .module-icon {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

.module-title {
  font-size: 26rpx;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 10rpx;
  transition: color 0.3s ease;
}

.module-item:hover .module-title {
  color: #667eea;
}

.module-desc {
  font-size: 22rpx;
  color: #a0aec0;
  line-height: 1.4;
  margin-bottom: 15rpx;
}

.module-arrow {
  position: absolute;
  bottom: 20rpx;
  right: 20rpx;
  opacity: 0;
  transition: all 0.3s ease;
}

.module-item:hover .module-arrow {
  opacity: 1;
  transform: translateX(5rpx);
}

/* 快速入口区域 */
.quick-actions {
  padding: 0 30rpx 40rpx;
  display: flex;
  justify-content: space-around;
  align-items: center;
  position: relative;
  z-index: 1;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 15rpx;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(245, 247, 250, 0.9) 100%);
  min-width: 100rpx;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(102, 126, 234, 0.1);
  backdrop-filter: blur(10px);
}

.action-icon-container {
  position: relative;
  margin-bottom: 10rpx;
}

.action-icon {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.action-icon::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.3), transparent);
}

.action-item:hover .action-icon {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.action-text {
  font-size: 22rpx;
  color: #666;
  font-weight: 500;
  transition: color 0.3s ease;
}

.action-item:hover .action-text {
  color: #667eea;
}

/* 装饰元素 */
.decor-element {
  position: absolute;
  z-index: -1;
  opacity: 0.05;
  border-radius: 50%;
  animation: float 6s ease-in-out infinite;
}

.decor-1 {
  width: 400rpx;
  height: 400rpx;
  background: linear-gradient(135deg, #005bea, #00c6fb);
  top: 20%;
  left: -10%;
  animation-delay: 0s;
}

.decor-2 {
  width: 300rpx;
  height: 300rpx;
  background: linear-gradient(135deg, #00c6fb, #00e5ff);
  bottom: 30%;
  right: -5%;
  animation-delay: 2s;
}

.decor-3 {
  width: 200rpx;
  height: 200rpx;
  background: linear-gradient(135deg, #4facfe, #005bea);
  top: 60%;
  left: 20%;
  animation-delay: 4s;
}

/* 颜色工具类 - 蓝色系 */
.bg-blue {
  background: linear-gradient(135deg, #005bea 0%, #00c6fb 100%);
}

.bg-cyan {
  background: linear-gradient(135deg, #00c6fb 0%, #00e5ff 100%);
}

.bg-teal {
  background: linear-gradient(135deg, #00e5ff 0%, #4facfe 100%);
}

.bg-indigo {
  background: linear-gradient(135deg, #4facfe 0%, #005bea 100%);
}

.bg-blue-light {
  background: linear-gradient(135deg, #4facfe 0%, #00c6fb 100%);
}

.bg-sky {
  background: linear-gradient(135deg, #00c6fb 0%, #4facfe 100%);
}

.bg-azure {
  background: linear-gradient(135deg, #005bea 0%, #4facfe 100%);
}

.bg-green {
  background: linear-gradient(135deg, #00c6fb 0%, #00e5ff 100%);
}

.bg-purple {
  background: linear-gradient(135deg, #4facfe 0%, #005bea 100%);
}

.bg-orange {
  background: linear-gradient(135deg, #00e5ff 0%, #00c6fb 100%);
}

/* 动画类 */
.animate-fade-in {
  animation: fadeIn 0.6s ease-out;
}

.animate-fade-in-delay {
  animation: fadeIn 0.6s ease-out 0.2s both;
}

.animate-fade-in-delay-2 {
  animation: fadeIn 0.6s ease-out 0.4s both;
}

.animate-float {
  animation: float 3s ease-in-out infinite;
}

.animate-slide-up {
  animation: slideUp 0.6s ease-out;
}

.animate-slide-up-delay {
  animation: slideUp 0.6s ease-out 0.2s both;
}

.animate-slide-up-delay-2 {
  animation: slideUp 0.6s ease-out 0.4s both;
}

.animate-slide-up-delay-3 {
  animation: slideUp 0.6s ease-out 0.6s both;
}

.animate-slide-up-delay-4 {
  animation: slideUp 0.6s ease-out 0.8s both;
}

.animate-pulse {
  animation: pulse 2s ease-in-out infinite;
}

.animate-count-up {
  animation: countUp 2s ease-out;
}

.hover-lift {
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.hover-lift:hover {
  transform: translateY(-8rpx) scale(1.02);
  box-shadow: 0 15px 40px rgba(102, 126, 234, 0.15);
}

/* 动画定义 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20rpx) rotate(180deg);
  }
}

@keyframes gradientShift {
  0%, 100% {
    background: linear-gradient(135deg, #005bea 0%, #00c6fb 100%);
  }
  50% {
    background: linear-gradient(135deg, #00c6fb 0%, #00e5ff 100%);
  }
}

@keyframes barGrow {
  from {
    transform: scaleY(0);
  }
  to {
    transform: scaleY(1);
  }
}

@keyframes countUp {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes shimmer {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}

@keyframes glowExpand {
  0%, 100% {
    r: 15;
    opacity: 0.5;
  }
  50% {
    r: 25;
    opacity: 0.8;
  }
}

@keyframes glowPulse {
  0%, 100% {
    opacity: 0.6;
  }
  50% {
    opacity: 1;
  }
}

/* 响应式设计 */
/* 小屏设备 (<= 375px) */
@media (max-width: 375px) {
  .header {
    padding: 15rpx 20rpx;
  }
  
  .app-title {
    font-size: 28rpx;
  }
  
  .welcome-text {
    font-size: 22rpx;
  }
  
  .date-text {
    font-size: 20rpx;
  }
  
  .stats {
    grid-template-columns: 1fr;
    gap: 15rpx;
  }
  
  .stat-item {
    padding: 20rpx;
  }
  
  .stat-value {
    font-size: 36rpx;
  }
  
  .stat-label {
    font-size: 22rpx;
  }
  
  .module-grid {
    grid-template-columns: 1fr;
    gap: 15rpx;
  }
  
  .module-item {
    padding: 20rpx;
  }
  
  .module-title {
    font-size: 26rpx;
  }
  
  .module-desc {
    font-size: 22rpx;
  }
  
  .quick-actions {
    flex-wrap: wrap;
    gap: 15rpx;
    padding: 20rpx;
  }
  
  .action-item {
    flex: 1 1 calc(50% - 7.5rpx);
    min-width: 120rpx;
    padding: 15rpx;
  }
  
  .action-text {
    font-size: 22rpx;
  }
  
  .age-bar-modern {
    width: 40rpx;
  }
  
  .chart-content {
    height: 200rpx;
  }
  
  .swiper-container {
    margin: 20rpx;
    height: 300rpx;
  }
}

/* 中屏设备 (376px - 414px) */
@media (min-width: 376px) and (max-width: 414px) {
  .stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .module-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .action-item {
    flex: 1 1 calc(33.333% - 10rpx);
  }
}

/* 大屏设备 (> 414px) */
@media (min-width: 415px) {
  .index-container {
    max-width: 750rpx;
    margin: 0 auto;
  }
  
  .stats {
    grid-template-columns: repeat(4, 1fr);
  }
  
  .module-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .action-item {
    flex: 1 1 calc(25% - 15rpx);
  }
  
  .stat-value {
    font-size: 48rpx;
  }
  
  .module-title {
    font-size: 30rpx;
  }
}

/* 横屏适配 */
@media (orientation: landscape) {
  .header {
    padding: 10rpx 20rpx;
  }
  
  .swiper-container {
    height: 250rpx;
  }
  
  .stats {
    grid-template-columns: repeat(4, 1fr);
  }
  
  .module-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}
</style>