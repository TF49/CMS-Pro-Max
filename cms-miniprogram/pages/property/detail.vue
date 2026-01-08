<template>
  <view class="property-detail-container">
    <!-- 头部导航 -->
    <view class="header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="header-left">
        <button class="back-btn" @click="handleBack">
          <uni-icons type="back" size="28" color="#333"></uni-icons>
        </button>
      </view>
      <view class="header-title">房产详情</view>
      <view class="header-right">
        <button class="edit-btn" @click="handleEdit">
          <uni-icons type="compose" size="24" color="#fff"></uni-icons>
        </button>
      </view>
    </view>
    
    <!-- 内容区域 -->
    <view class="content-container">
      <!-- 加载状态 -->
      <view v-if="loading" class="loading-container">
        <uni-icons type="spinner-cycle" size="48" color="#667eea" class="loading-icon"></uni-icons>
        <text class="loading-text">加载中...</text>
      </view>
      
      <!-- 详情内容 -->
      <view v-else-if="propertyInfo.id" class="detail-content">
        <!-- 基本信息卡片 -->
        <view class="detail-card">
          <view class="card-header">
            <text class="card-title">基本信息</text>
          </view>
          <view class="card-body">
            <view class="detail-row">
              <view class="detail-label">房产地址</view>
              <view class="detail-value">{{ propertyInfo.address || '无' }}</view>
            </view>
            <view class="detail-row">
              <view class="detail-label">业主姓名</view>
              <view class="detail-value">{{ propertyInfo.ownerName || '无' }}</view>
            </view>
            <view class="detail-row">
              <view class="detail-label">房产类型</view>
              <view class="detail-value">
                <view 
                  class="status-tag" 
                  :class="getPropertyTypeClass(propertyInfo.propertyType)"
                >
                  <text class="tag-text">{{ getPropertyTypeText(propertyInfo.propertyType) }}</text>
                </view>
              </view>
            </view>
            <view class="detail-row">
              <view class="detail-label">建筑面积</view>
              <view class="detail-value">{{ propertyInfo.area || '0' }} ㎡</view>
            </view>
            <view class="detail-row">
              <view class="detail-label">户型</view>
              <view class="detail-value">{{ propertyInfo.houseType || '无' }}</view>
            </view>
            <view class="detail-row">
              <view class="detail-label">楼层</view>
              <view class="detail-value">{{ propertyInfo.floor || '0' }} 层</view>
            </view>
          </view>
        </view>
        
        <!-- 产权信息卡片 -->
        <view class="detail-card">
          <view class="card-header">
            <text class="card-title">产权信息</text>
          </view>
          <view class="card-body">
            <view class="detail-row">
              <view class="detail-label">产权人</view>
              <view class="detail-value">{{ propertyInfo.propertyOwner || '无' }}</view>
            </view>
            <view class="detail-row">
              <view class="detail-label">产权年限</view>
              <view class="detail-value">{{ propertyInfo.propertyYears || '0' }} 年</view>
            </view>
            <view class="detail-row">
              <view class="detail-label">产权证号</view>
              <view class="detail-value">{{ propertyInfo.propertyCertificateNo || '无' }}</view>
            </view>
            <view class="detail-row">
              <view class="detail-label">产权状态</view>
              <view class="detail-value">{{ propertyInfo.propertyStatus || '无' }}</view>
            </view>
          </view>
        </view>
        
        <!-- 备注信息卡片 -->
        <view class="detail-card">
          <view class="card-header">
            <text class="card-title">备注信息</text>
          </view>
          <view class="card-body">
            <view class="detail-row full-width">
              <view class="detail-label">备注</view>
              <view class="detail-value full-value">{{ propertyInfo.remark || '无' }}</view>
            </view>
          </view>
        </view>
        
        <!-- 操作日志卡片 -->
        <view class="detail-card">
          <view class="card-header">
            <text class="card-title">操作日志</text>
          </view>
          <view class="card-body">
            <view class="detail-row">
              <view class="detail-label">创建时间</view>
              <view class="detail-value">{{ formatDateTime(propertyInfo.createTime) }}</view>
            </view>
            <view class="detail-row">
              <view class="detail-label">更新时间</view>
              <view class="detail-value">{{ formatDateTime(propertyInfo.updateTime) }}</view>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 错误状态 -->
      <view v-else class="error-container">
        <view class="error-icon">
          <uni-icons type="close" size="64" color="#f5576c"></uni-icons>
        </view>
        <text class="error-title">加载失败</text>
        <text class="error-subtitle">无法加载房产详情</text>
        <button class="refresh-btn" @click="loadPropertyDetail">
          <uni-icons type="refresh" size="20" color="#667eea"></uni-icons>
          <text>重新加载</text>
        </button>
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
      // 房产记录ID
      propertyId: '',
      // 房产记录详情
      propertyInfo: {
        id: '',
        address: '',
        ownerName: '',
        propertyType: 'HOUSE',
        area: '0',
        houseType: '',
        floor: '0',
        propertyOwner: '',
        propertyYears: '70',
        propertyCertificateNo: '',
        propertyStatus: '',
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
      this.propertyId = options.id
      // 加载房产记录详情
      this.loadPropertyDetail()
    }
  },
  methods: {
    // 获取状态栏高度
    getStatusBarHeight() {
      // #ifdef MP-WEIXIN
      const systemInfo = uni.getSystemInfoSync()
      this.statusBarHeight = systemInfo.statusBarHeight || 0
      // #endif
    },

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
    
    // 编辑房产记录
    handleEdit() {
      uni.navigateTo({
        url: `/pages/property/edit?id=${this.propertyId}`
      })
    },
    
    // 删除房产记录
    handleDelete() {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这条房产记录吗？',
        success: (res) => {
          if (res.confirm) {
            this.loading = true
            // 调用删除房产记录API
            del(`/property/delete/${this.propertyId}`)
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
                console.error('删除房产记录失败', err)
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
    
    // 加载房产记录详情
    loadPropertyDetail() {
      this.loading = true
      // 调用房产记录详情API
      get(`/property/${this.propertyId}`)
        .then(res => {
          if (res.code === 200 && res.data) {
            this.propertyInfo = res.data
          }
        })
        .catch(err => {
          console.error('获取房产记录详情失败', err)
          uni.showToast({
            title: '获取详情失败',
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
    },
    
    // 获取房产类型文本
    getPropertyTypeText(type) {
      const typeMap = {
        'HOUSE': '住宅',
        'COMMERCIAL': '商业',
        'INDUSTRIAL': '工业',
        'OTHER': '其他'
      }
      return typeMap[type] || '未知'
    },
    
    // 获取房产类型样式类
    getPropertyTypeClass(type) {
      const classMap = {
        'HOUSE': 'tag-house',
        'COMMERCIAL': 'tag-commercial',
        'INDUSTRIAL': 'tag-industrial',
        'OTHER': 'tag-other'
      }
      return classMap[type] || 'tag-other'
    }
  }
}
</script>

<style scoped>
.property-detail-container {
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
  border-radius: 50%;
  transition: all 0.3s ease;
  margin-left: 10rpx;
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

/* 内容容器 */
.content-container {
  padding: 20rpx;
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

/* 详情内容 */
.detail-content {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

/* 卡片样式 */
.card {
  background: white;
  border-radius: 16rpx;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: all 0.3s ease;
}

.card:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  transform: translateY(-2rpx);
}

/* 卡片头部 */
.card-header {
  padding: 25rpx 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
  background: #fafafa;
}

.card-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

/* 卡片内容 */
.card-body {
  padding: 25rpx 20rpx;
}

/* 详情行 */
.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.detail-row:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.detail-row.full-width {
  flex-direction: column;
  align-items: flex-start;
}

/* 详情标签 */
.detail-label {
  font-size: 26rpx;
  color: #666;
  font-weight: bold;
  min-width: 120rpx;
}

/* 详情值 */
.detail-value {
  font-size: 26rpx;
  color: #333;
  flex: 1;
  text-align: right;
  word-break: break-all;
}

.detail-value.full-value {
  text-align: left;
  padding-top: 10rpx;
  line-height: 1.5;
}

/* 状态标签 */
.status-tag {
  padding: 5rpx 15rpx;
  border-radius: 15rpx;
  font-size: 20rpx;
  font-weight: bold;
  display: inline-block;
}

.tag-house {
  background: #e8f5e8;
  color: #4caf50;
}

.tag-commercial {
  background: #e3f2fd;
  color: #2196f3;
}

.tag-industrial {
  background: #fff3e0;
  color: #ff9800;
}

.tag-other {
  background: #f5f5f5;
  color: #9e9e9e;
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

/* 响应式设计 */
@media (max-width: 375px) {
  .header-title {
    font-size: 28rpx;
  }
  
  .detail-label {
    font-size: 24rpx;
    min-width: 100rpx;
  }
  
  .detail-value {
    font-size: 24rpx;
  }
}
</style>