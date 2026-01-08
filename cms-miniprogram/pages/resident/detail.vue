<template>
  <view class="detail-container">
    <!-- 头部导航 -->
    <view class="header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="header-left">
        <button class="back-btn" @click="handleBack">
          <uni-icons type="back" size="28" color="#333"></uni-icons>
        </button>
      </view>
      <view class="header-title">居民详情</view>
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
          <uni-icons type="person" size="20" color="#667eea"></uni-icons>
          <text>基本信息</text>
        </view>
        
        <view class="info-list">
          <view class="info-item">
            <text class="label">ID</text>
            <text class="value">{{ resident.id || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">姓名</text>
            <text class="value">{{ resident.name || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">性别</text>
            <text class="value">{{ resident.gender === 'MALE' ? '男' : (resident.gender === 'FEMALE' ? '女' : resident.gender || '未填写') }}</text>
          </view>
          <view class="info-item">
            <text class="label">年龄</text>
            <text class="value">{{ this.calculateAge(resident.birthDate) }}</text>
          </view>
          <view class="info-item">
            <text class="label">出生日期</text>
            <text class="value">{{ resident.birthDate || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">身份证号</text>
            <text class="value">{{ resident.idCard || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">民族</text>
            <text class="value">{{ resident.ethnicGroup || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">婚姻状况</text>
            <text class="value">{{ formatMaritalStatus(resident.maritalStatus) || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">是否户主</text>
            <text class="value">{{ resident.isHouseholder !== null ? (resident.isHouseholder ? '是' : '否') : '未填写' }}</text>
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
            <text class="label">手机号码</text>
            <text class="value">{{ resident.phoneNumber || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">电子邮箱</text>
            <text class="value">{{ resident.email || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">家庭住址</text>
            <text class="value">{{ resident.address || '未填写' }}</text>
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
            <text class="label">户籍ID</text>
            <text class="value">{{ resident.householdId || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">户籍编号</text>
            <text class="value">{{ resident.householdNumber || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">与户主关系</text>
            <text class="value">{{ resident.relationship || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">民族</text>
            <text class="value">{{ resident.ethnicGroup || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">教育程度</text>
            <text class="value">{{ formatEducation(resident.educationLevel) || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">职业</text>
            <text class="value">{{ resident.occupation || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">婚姻状况</text>
            <text class="value">{{ formatMaritalStatus(resident.maritalStatus) || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">月收入</text>
            <text class="value">{{ resident.income ? '¥' + resident.income : '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">是否户主</text>
            <text class="value">{{ resident.isHouseholder ? '是' : '否' }}</text>
          </view>
          <view class="info-item">
            <text class="label">登记日期</text>
            <text class="value">{{ formatDate(resident.createTime) || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">更新日期</text>
            <text class="value">{{ formatDate(resident.updateTime) || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">备注</text>
            <text class="value">{{ resident.notes || '未填写' }}</text>
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
      residentId: null,
      resident: {},
      educationInfo: {},
      loading: false,
      statusBarHeight: 0
    }
  },
  onLoad(options) {
    this.getStatusBarHeight()
    // 获取传递的居民ID
    if (options.id) {
      this.residentId = options.id
      this.loadResidentDetail()
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
    
    // 加载居民详情
    loadResidentDetail() {
      this.loading = true
      get(`/resident/${this.residentId}`)
        .then(res => {
          if (res.code === 200) {
            this.resident = res.data
            // 获取居民教育信息
            this.loadEducationInfo()
          } else {
            uni.showToast({
              title: '获取详情失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('获取居民详情失败', err)
          uni.showToast({
            title: '网络错误',
            icon: 'none'
          })
        })
        .finally(() => {
          this.loading = false
        })
    },
    
    // 加载居民教育信息
    loadEducationInfo() {
      get(`/education/resident/${this.residentId}`)
        .then(res => {
          if (res.code === 200 && res.data && res.data.length > 0) {
            // 取第一条教育信息作为主要教育信息
            this.educationInfo = res.data[0]
          }
        })
        .catch(err => {
          console.error('获取居民教育信息失败', err)
        })
    },
    
    // 编辑居民信息
    handleEdit() {
      uni.navigateTo({
        url: `/pages/resident/edit?id=${this.residentId}`
      })
    },
    
    // 删除居民信息
    handleDelete() {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除该居民信息吗？',
        success: (res) => {
          if (res.confirm) {
            this.loading = true
            del(`/resident/delete/${this.residentId}`)
              .then(res => {
                if (res.code === 200) {
                  uni.showToast({
                    title: '删除成功',
                    icon: 'success'
                  })
                  // 返回列表页
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
                console.error('删除居民失败', err)
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
    
    // 格式化婚姻状况
    formatMaritalStatus(status) {
      // 如果已经是中文，直接返回
      if (['未婚', '已婚', '离婚', '丧偶', '未知'].includes(status)) {
        return status
      }
      const map = {
        'SINGLE': '未婚',
        'MARRIED': '已婚',
        'DIVORCED': '离婚',
        'WIDOWED': '丧偶'
      }
      return map[status] || status || '未知'
    },
    
    // 格式化教育程度
    formatEducation(education) {
      // 如果已经是中文，直接返回
      if (['小学', '初中', '高中', '大专', '本科', '硕士', '博士', '其他'].includes(education)) {
        return education
      }
      const map = {
        'PRIMARY': '小学',
        'JUNIOR': '初中',
        'SENIOR': '高中',
        'COLLEGE': '大专',
        'BACHELOR': '本科',
        'MASTER': '硕士',
        'DOCTOR': '博士'
      }
      return map[education] || education || '未知'
    },
    
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    },
    
    // 根据出生日期计算年龄
    calculateAge(birthDate) {
      if (!birthDate) return '未填写'
      
      const birth = new Date(birthDate)
      const today = new Date()
      
      let age = today.getFullYear() - birth.getFullYear()
      const monthDiff = today.getMonth() - birth.getMonth()
      
      // 如果当前月份小于出生月份，或者月份相同但当前日期小于出生日期，则年龄减1
      if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
        age--
      }
      
      return age > 0 ? age + '岁' : '未填写'
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