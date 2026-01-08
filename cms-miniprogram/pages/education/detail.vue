<template>
  <view class="detail-container">
    <!-- 头部导航 -->
    <view class="header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="header-left">
        <button class="back-btn" @click="handleBack">
          <uni-icons type="back" size="28" color="#333"></uni-icons>
        </button>
      </view>
      <view class="header-title">教育详情</view>
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
      <!-- 居民信息卡片 -->
      <view class="info-card">
        <view class="card-title">
          <uni-icons type="person" size="20" color="#667eea"></uni-icons>
          <text>居民信息</text>
        </view>
        <view class="info-list">
          <view class="info-item">
            <text class="label">居民姓名</text>
            <text class="value">{{ residentInfo.name || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">身份证号</text>
            <text class="value">{{ residentInfo.idCard || '未填写' }}</text>
          </view>
          <view class="info-item">
            <text class="label">性别</text>
            <text class="value">{{ formatGender(residentInfo.gender) }}</text>
          </view>
          <view class="info-item">
            <text class="label">年龄</text>
            <text class="value">{{ calculatedAge || '未填写' }}</text>
          </view>
        </view>
      </view>
      
      <!-- 教育基本信息 -->
      <view class="info-card">
        <view class="card-title">
          <uni-icons type="book" size="20" color="#667eea"></uni-icons>
          <text>教育基本信息</text>
        </view>
        <view class="info-list">
          <view class="info-item">
            <text class="label">学校名称</text>
            <text class="value">{{ education.schoolName }}</text>
          </view>
          <view class="info-item">
            <text class="label">教育程度</text>
            <text class="value">{{ formatEducationLevel(education.educationLevel) }}</text>
          </view>
          <view class="info-item">
            <text class="label">专业名称</text>
            <text class="value">{{ education.major }}</text>
          </view>
          <view class="info-item">
            <text class="label">入学日期</text>
            <text class="value">{{ education.enrollmentDate }}</text>
          </view>
          <view class="info-item">
            <text class="label">毕业日期</text>
            <text class="value">{{ education.graduationDate }}</text>
          </view>
          <view class="info-item">
            <text class="label">学历状态</text>
            <text class="value">{{ formatStatus(education.status) }}</text>
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
            <text class="label">创建时间</text>
            <text class="value">{{ formatDate(education.createTime) }}</text>
          </view>
          <view class="info-item">
            <text class="label">更新时间</text>
            <text class="value">{{ formatDate(education.updateTime) }}</text>
          </view>
          <view class="info-item">
            <text class="label">备注</text>
            <text class="value">{{ education.notes }}</text>
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
      educationId: null,
      education: {},
      residentInfo: {},
      loading: false,
      statusBarHeight: 0
    }
  },
  computed: {
    // 计算年龄
    calculatedAge() {
      if (this.residentInfo.age && this.residentInfo.age > 0) {
        return this.residentInfo.age + '岁'
      }
      
      // 从身份证号计算年龄
      if (this.residentInfo.idCard) {
        const age = this.calculateAgeFromIdCard(this.residentInfo.idCard)
        return age > 0 ? age + '岁' : '未填写'
      }
      
      // 从出生日期计算年龄
      if (this.residentInfo.birthDate) {
        const age = this.calculateAgeFromBirthDate(this.residentInfo.birthDate)
        return age > 0 ? age + '岁' : '未填写'
      }
      
      return '未填写'
    }
  },
  onLoad(options) {
    this.getStatusBarHeight()
    // 获取传递的教育ID
    if (options.id) {
      this.educationId = options.id
      this.loadEducationDetail()
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
    
    // 加载教育详情
    loadEducationDetail() {
      this.loading = true
      get(`/education/${this.educationId}`)
        .then(res => {
          if (res.code === 200) {
            // 字段映射转换和空值处理
            this.education = {
              // 基本信息
              id: res.data.id || '',
              residentId: res.data.residentId || '',
              schoolName: res.data.schoolName || '未填写',
              educationLevel: res.data.educationLevel || '未知',
              major: res.data.major || '未填写',
              
              // 日期字段处理
              enrollmentDate: res.data.enrollmentDate || '未填写',
              graduationDate: res.data.graduationDate || '至今',
              
              // 状态处理
              status: res.data.status || '未填写',
              
              // 其他信息
              notes: res.data.notes || '未填写',
              createTime: res.data.createTime || '',
              updateTime: res.data.updateTime || ''
            }
            
            // 如果有居民ID，获取居民详情
            if (this.education.residentId) {
              this.getResidentInfo(this.education.residentId)
            } else if (res.data.idCard) {
              // 如果有身份证号，尝试通过身份证号获取居民详情
              this.getResidentByCardId(res.data.idCard)
            }
          } else {
            uni.showToast({
              title: '获取详情失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('获取教育详情失败', err)
          uni.showToast({
            title: '网络错误',
            icon: 'none'
          })
        })
        .finally(() => {
          this.loading = false
        })
    },
    
    // 根据居民ID获取居民详情
    getResidentInfo(residentId) {
      if (!residentId) return
      
      get(`/resident/${residentId}`)
        .then(res => {
          if (res.code === 200) {
            this.residentInfo = res.data
          }
        })
        .catch(err => {
          console.error('获取居民详情失败', err)
        })
    },
    
    // 根据身份证号获取居民详情
    getResidentByCardId(idCard) {
      if (!idCard) return
      
      get(`/resident/idcard/${idCard}`)
        .then(res => {
          if (res.code === 200 && res.data) {
            this.residentInfo = res.data
          }
        })
        .catch(err => {
          console.error('根据身份证号获取居民详情失败', err)
        })
    },
    
    // 编辑教育信息
    handleEdit() {
      uni.navigateTo({
        url: `/pages/education/edit?id=${this.educationId}`
      })
    },
    
    // 删除教育信息
    handleDelete() {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除该教育信息吗？',
        success: (res) => {
          if (res.confirm) {
            this.loading = true
            del(`/education/delete/${this.educationId}`)
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
                console.error('删除教育信息失败', err)
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
    
    // 格式化教育程度
    formatEducationLevel(level) {
      if (!level) return '未知'
      
      // 处理中文直接返回
      if (typeof level === 'string' && ['小学', '初中', '高中', '大专', '本科', '硕士', '博士'].includes(level)) {
        return level
      }
      
      // 英文转中文
      const map = {
        'PRIMARY': '小学',
        'JUNIOR': '初中',
        'SENIOR': '高中',
        'COLLEGE': '大专',
        'BACHELOR': '本科',
        'MASTER': '硕士',
        'DOCTOR': '博士'
      }
      return map[level] || '未知'
    },
    
    // 格式化性别
    formatGender(gender) {
      if (!gender) return '未填写'
      
      // 处理中文直接返回
      if (typeof gender === 'string' && ['男', '女'].includes(gender)) {
        return gender
      }
      
      // 英文转中文
      const map = {
        'MALE': '男',
        'FEMALE': '女'
      }
      return map[gender] || gender || '未填写'
    },
    
    // 从身份证号计算年龄
    calculateAgeFromIdCard(idCard) {
      if (!idCard || idCard.length !== 18) return 0
      
      try {
        // 身份证号第7-14位是出生日期，格式为YYYYMMDD
        const birthStr = idCard.substring(6, 14)
        const birthYear = parseInt(birthStr.substring(0, 4))
        const birthMonth = parseInt(birthStr.substring(4, 6))
        const birthDay = parseInt(birthStr.substring(6, 8))
        
        const today = new Date()
        const currentYear = today.getFullYear()
        const currentMonth = today.getMonth() + 1
        const currentDay = today.getDate()
        
        let age = currentYear - birthYear
        
        // 如果当前月份小于出生月份，或者当前月份等于出生月份但当前日期小于出生日期，年龄减1
        if (currentMonth < birthMonth || (currentMonth === birthMonth && currentDay < birthDay)) {
          age--
        }
        
        return age
      } catch (e) {
        console.error('从身份证号计算年龄失败', e)
        return 0
      }
    },
    
    // 从出生日期计算年龄
    calculateAgeFromBirthDate(birthDate) {
      if (!birthDate) return 0
      
      try {
        const birth = new Date(birthDate)
        const today = new Date()
        
        let age = today.getFullYear() - birth.getFullYear()
        const monthDiff = today.getMonth() - birth.getMonth()
        
        // 如果当前月份小于出生月份，或者当前月份等于出生月份但当前日期小于出生日期，年龄减1
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
          age--
        }
        
        return age
      } catch (e) {
        console.error('从出生日期计算年龄失败', e)
        return 0
      }
    },
    
    // 格式化状态
    formatStatus(status) {
      if (!status) return '未填写'
      
      // 处理中文直接返回
      if (typeof status === 'string' && ['在读', '毕业', '休学', '退学'].includes(status)) {
        return status
      }
      
      // 英文转中文
      const map = {
        'STUDYING': '在读',
        'GRADUATED': '毕业',
        'SUSPENDED': '休学',
        'DROP_OUT': '退学'
      }
      return map[status] || status
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