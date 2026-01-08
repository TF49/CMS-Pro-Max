<template>
  <view class="detail-container">
    <!-- 头部导航 -->
    <view class="header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="header-left">
        <button class="back-btn" @click="handleBack">
          <uni-icons type="back" size="28" color="#333"></uni-icons>
        </button>
      </view>
      <view class="header-title">车辆详情</view>
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
          <uni-icons type="car" size="20" color="#667eea"></uni-icons>
          <text>基本信息</text>
        </view>
        <view class="info-list">
          <view class="info-item">
            <text class="label">车牌号码</text>
            <text class="value">{{ vehicleInfo.licensePlate || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">车主姓名</text>
            <text class="value">{{ vehicleInfo.ownerName || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">车辆类型</text>
            <text class="value">
              <view 
                class="status-tag" 
                :class="getVehicleTypeClass(vehicleInfo.vehicleType)"
              >
                <text class="tag-text">{{ getVehicleTypeText(vehicleInfo.vehicleType) }}</text>
              </view>
            </text>
          </view>
          <view class="info-item">
            <text class="label">品牌</text>
            <text class="value">{{ vehicleInfo.brand || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">型号</text>
            <text class="value">{{ vehicleInfo.model || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">颜色</text>
            <text class="value">{{ vehicleInfo.color || '无' }}</text>
          </view>
        </view>
      </view>
      
      <!-- 车辆信息卡片 -->
      <view class="info-card">
        <view class="card-title">
          <uni-icons type="settings" size="20" color="#667eea"></uni-icons>
          <text>车辆信息</text>
        </view>
        <view class="info-list">
          <view class="info-item">
            <text class="label">发动机号</text>
            <text class="value">{{ vehicleInfo.engineNo || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">车架号</text>
            <text class="value">{{ vehicleInfo.frameNo || '无' }}</text>
          </view>
          <view class="info-item">
            <text class="label">注册日期</text>
            <text class="value">{{ formatDate(vehicleInfo.registrationDate) }}</text>
          </view>
          <view class="info-item">
            <text class="label">保险到期日</text>
            <text class="value">{{ formatDate(vehicleInfo.insuranceExpiryDate) }}</text>
          </view>
          <view class="info-item">
            <text class="label">年检到期日</text>
            <text class="value">{{ formatDate(vehicleInfo.inspectionExpiryDate) }}</text>
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
            <text class="value">{{ vehicleInfo.remark || '无' }}</text>
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
            <text class="value">{{ formatDateTime(vehicleInfo.createTime) }}</text>
          </view>
          <view class="info-item">
            <text class="label">更新时间</text>
            <text class="value">{{ formatDateTime(vehicleInfo.updateTime) }}</text>
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
      // 车辆记录ID
      vehicleId: '',
      // 车辆记录详情
      vehicleInfo: {
        id: '',
        licensePlate: '',
        ownerName: '',
        vehicleType: 'CAR',
        brand: '',
        model: '',
        color: '',
        engineNo: '',
        frameNo: '',
        registrationDate: '',
        insuranceExpiryDate: '',
        inspectionExpiryDate: '',
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
      this.vehicleId = options.id
      // 加载车辆记录详情
      this.loadVehicleDetail()
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
    
    // 编辑车辆记录
    handleEdit() {
      uni.navigateTo({
        url: `/pages/vehicle/edit?id=${this.vehicleId}`
      })
    },
    
    // 删除车辆记录
    handleDelete() {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这条车辆记录吗？',
        success: (res) => {
          if (res.confirm) {
            this.loading = true
            // 调用删除车辆记录API
            del(`/vehicle/delete/${this.vehicleId}`)
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
                console.error('删除车辆记录失败', err)
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
    
    // 加载车辆记录详情
    loadVehicleDetail() {
      this.loading = true
      // 调用车辆记录详情API
      get(`/vehicle/${this.vehicleId}`)
        .then(res => {
          if (res.code === 200 && res.data) {
            this.vehicleInfo = res.data
          }
        })
        .catch(err => {
          console.error('获取车辆记录详情失败', err)
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
    
    // 获取车辆类型文本
    getVehicleTypeText(type) {
      const typeMap = {
        'CAR': '小型汽车',
        'TRUCK': '货车',
        'MOTORCYCLE': '摩托车',
        'OTHER': '其他'
      }
      return typeMap[type] || '未知'
    },
    
    // 获取车辆类型样式类
    getVehicleTypeClass(type) {
      const classMap = {
        'CAR': 'tag-car',
        'TRUCK': 'tag-truck',
        'MOTORCYCLE': 'tag-motorcycle',
        'OTHER': 'tag-other'
      }
      return classMap[type] || 'tag-other'
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

.tag-car {
  background: #e8f5e8;
  color: #4caf50;
}

.tag-truck {
  background: #e3f2fd;
  color: #2196f3;
}

.tag-motorcycle {
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
</style>