<template>
  <view class="edit-profile-container">
    <!-- 头部导航 -->
    <view class="header">
      <view class="header-left">
        <button class="back-btn" @click="handleBack">
          <uni-icons type="back" size="28" color="#333"></uni-icons>
        </button>
      </view>
      <view class="header-title">编辑资料</view>
      <view class="header-right">
        <button class="save-btn" @click="handleSave" :disabled="loading">
          <text v-if="loading" class="save-text-loading">保存中...</text>
          <text v-else class="save-text">保存</text>
        </button>
      </view>
    </view>
    
    <!-- 内容容器 -->
    <view class="content-container">
  
      
      <!-- 表单区域 -->
      <view class="form-section">
        <!-- 用户名 -->
        <view class="form-item">
          <view class="form-label">
            <text class="label-text">用户名</text>
            <text class="required">*</text>
          </view>
          <view class="form-control">
            <input
              class="input-field"
              type="text"
              placeholder="请输入用户名"
              v-model="userInfo.username"
              maxlength="20"
            />
          </view>
          <view v-if="errors.username" class="error-message">{{ errors.username }}</view>
        </view>
        
        <!-- 真实姓名 -->
        <view class="form-item">
          <view class="form-label">
            <text class="label-text">真实姓名</text>
            <text class="required">*</text>
          </view>
          <view class="form-control">
            <input
              class="input-field"
              type="text"
              placeholder="请输入真实姓名"
              v-model="userInfo.realName"
              maxlength="10"
            />
          </view>
          <view v-if="errors.realName" class="error-message">{{ errors.realName }}</view>
        </view>
        
        <!-- 手机号 -->
        <view class="form-item">
          <view class="form-label">
            <text class="label-text">手机号</text>
          </view>
          <view class="form-control">
            <input
              class="input-field"
              type="number"
              placeholder="请输入手机号"
              v-model="userInfo.phone"
              maxlength="11"
            />
          </view>
          <view v-if="errors.phone" class="error-message">{{ errors.phone }}</view>
        </view>
        
        <!-- 邮箱 -->
        <view class="form-item">
          <view class="form-label">
            <text class="label-text">邮箱</text>
          </view>
          <view class="form-control">
            <input
              class="input-field"
              type="text"
              placeholder="请输入邮箱"
              v-model="userInfo.email"
              maxlength="50"
            />
          </view>
          <view v-if="errors.email" class="error-message">{{ errors.email }}</view>
        </view>
        
        <!-- 性别 -->
        <view class="form-item">
          <view class="form-label">
            <text class="label-text">性别</text>
          </view>
          <view class="form-control">
            <radio-group :value="userInfo.gender" @change="handleGenderChange" class="radio-group">
              <label class="radio-item">
                <radio value="MALE" color="#667eea" />
                <text class="radio-text">男</text>
              </label>
              <label class="radio-item">
                <radio value="FEMALE" color="#667eea" />
                <text class="radio-text">女</text>
              </label>
              <label class="radio-item">
                <radio value="UNKNOWN" color="#667eea" />
                <text class="radio-text">保密</text>
              </label>
            </radio-group>
          </view>
        </view>
        
        <!-- 出生日期 -->
        <view class="form-item">
          <view class="form-label">
            <text class="label-text">出生日期</text>
          </view>
          <view class="form-control date-control">
            <input
              class="input-field date-input"
              type="text"
              placeholder="请选择出生日期"
              v-model="userInfo.birthday"
              readonly
              @click="handleDatePick"
            />
            <button class="date-pick-btn" @click="handleDatePick">
              <uni-icons type="calendar" size="24" color="#667eea"></uni-icons>
            </button>
          </view>
        </view>
        
        <!-- 个人简介 -->
        <view class="form-item">
          <view class="form-label">
            <text class="label-text">个人简介</text>
          </view>
          <view class="form-control textarea-control">
            <textarea
              class="textarea-field"
              placeholder="请输入个人简介"
              v-model="userInfo.introduction"
              maxlength="100"
              :auto-height="true"
              show-word-limit
            ></textarea>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 日期选择器 -->
    <picker
      mode="date"
      :value="userInfo.birthday"
      :start="'1900-01-01'"
      :end="today"
      @change="handleDateChange"
      v-if="showDatePicker"
    >
      <view class="picker">请选择日期</view>
    </picker>
  </view>
</template>

<script>
import { defineComponent } from 'vue'
import { get, put } from '@/utils/request'

export default defineComponent({
  data() {
    return {
      // 加载状态
      loading: false,

      // 显示日期选择器
      showDatePicker: false,
      // 今天日期
      today: '',
      // 用户信息
      userInfo: {
        username: '',
        realName: '',
        phone: '',
        email: '',
        gender: 'UNKNOWN',
        birthday: '',
        introduction: ''
      },
      // 错误信息
      errors: {
        username: '',
        realName: '',
        phone: '',
        email: ''
      }
    }
  },
  onLoad() {
    // 获取今天日期
    this.getTodayDate()
    // 加载用户信息
    this.loadUserInfo()
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
    
    // 获取今天日期
    getTodayDate() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      this.today = `${year}-${month}-${day}`
    },
    
    // 加载用户信息
    loadUserInfo() {
      this.loading = true
      get('/api/auth/current')
        .then(res => {
          if (res.code === 200 && res.data) {
            // 从后端获取用户信息，但不包含头像URL，避免无效图片加载
            this.userInfo = {
              username: res.data.username || '',
              realName: res.data.realName || res.data.name || '',
              phone: res.data.phone || '',
              email: res.data.email || '',
              gender: res.data.gender || 'UNKNOWN',
              birthday: res.data.birthday || '',
              introduction: res.data.introduction || res.data.bio || ''
            }
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
    
    // 性别变化处理
    handleGenderChange(e) {
      this.userInfo.gender = e.detail.value
    },
    

    
    // 显示日期选择器
    handleDatePick() {
      this.showDatePicker = !this.showDatePicker
    },
    
    // 日期选择确认
    handleDateChange(e) {
      this.userInfo.birthday = e.detail.value
      this.showDatePicker = false
    },
    
    // 表单验证
    validateForm() {
      let isValid = true
      
      // 重置错误信息
      this.errors = {
        username: '',
        realName: '',
        phone: '',
        email: ''
      }
      
      // 验证用户名
      if (!this.userInfo.username.trim()) {
        this.errors.username = '用户名不能为空'
        isValid = false
      } else if (this.userInfo.username.length < 3 || this.userInfo.username.length > 20) {
        this.errors.username = '用户名长度必须在3-20个字符之间'
        isValid = false
      }
      
      // 验证真实姓名
      if (!this.userInfo.realName.trim()) {
        this.errors.realName = '真实姓名不能为空'
        isValid = false
      }
      
      // 验证手机号
      if (this.userInfo.phone && !/^1[3-9]\d{9}$/.test(this.userInfo.phone)) {
        this.errors.phone = '请输入正确的手机号'
        isValid = false
      }
      
      // 验证邮箱
      if (this.userInfo.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.userInfo.email)) {
        this.errors.email = '请输入正确的邮箱地址'
        isValid = false
      }
      
      return isValid
    },
    
    // 保存资料
    handleSave() {
      // 表单验证
      if (!this.validateForm()) {
        return
      }
      
      this.loading = true
      
      // 调用保存资料API
      put('/api/user/profile', this.userInfo)
        .then(res => {
          if (res.code === 200) {
            uni.showToast({
              title: '资料保存成功',
              icon: 'success',
              duration: 2000,
              success: () => {
                // 返回上一页
                setTimeout(() => {
                  this.handleBack()
                }, 2000)
              }
            })
          } else {
            uni.showToast({
              title: res.msg || res.message || '资料保存失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('保存资料失败', err)
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
</script>

<style scoped>
.edit-profile-container {
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

.save-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8rpx;
  padding: 12rpx 24rpx;
  font-size: 28rpx;
  font-weight: bold;
  transition: all 0.3s ease;
}

.save-btn:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.save-btn:disabled {
  opacity: 0.6;
  transform: none;
  box-shadow: none;
}

.save-text {
  font-size: 28rpx;
  font-weight: bold;
}

.save-text-loading {
  font-size: 28rpx;
  font-weight: bold;
}

/* 内容容器 */
.content-container {
  padding: 20rpx;
}


/* 表单区域 */
.form-section {
  background: white;
  border-radius: 16rpx;
  padding: 20rpx;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

/* 表单项 */
.form-item {
  margin-bottom: 30rpx;
}

.form-label {
  display: flex;
  align-items: center;
  margin-bottom: 15rpx;
}

.label-text {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.required {
  color: #ff4757;
  margin-left: 5rpx;
  font-size: 32rpx;
}

.form-control {
  position: relative;
  background: #f8f9fa;
  border-radius: 12rpx;
  transition: all 0.3s ease;
  border: 2rpx solid transparent;
  padding: 0 20rpx;
}

.form-control:focus-within {
  background: white;
  border-color: #667eea;
  box-shadow: 0 0 0 8rpx rgba(102, 126, 234, 0.1);
}

/* 输入框 */
.input-field {
  width: 100%;
  height: 80rpx;
  font-size: 28rpx;
  color: #333;
  background: transparent;
  border: none;
  outline: none;
}

.input-field::placeholder {
  color: #999;
}

/* 日期选择器 */
.date-control {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.date-input {
  flex: 1;
}

.date-pick-btn {
  background: none;
  border: none;
  padding: 10rpx;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.date-pick-btn:hover {
  background: rgba(102, 126, 234, 0.1);
  transform: scale(1.1);
}

/* 文本域 */
.textarea-control {
  padding: 15rpx 20rpx;
  min-height: 120rpx;
}

.textarea-field {
  width: 100%;
  font-size: 28rpx;
  color: #333;
  background: transparent;
  border: none;
  outline: none;
  line-height: 1.5;
}

.textarea-field::placeholder {
  color: #999;
}

/* 单选组 */
.radio-group {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  width: 100%;
}

.radio-item {
  display: flex;
  align-items: center;
  gap: 15rpx;
  padding: 20rpx;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 12rpx;
  transition: all 0.3s ease;
}

.radio-item:hover {
  background: rgba(102, 126, 234, 0.1);
  transform: translateX(5rpx);
}

.radio-text {
  font-size: 26rpx;
  color: #333;
  font-weight: 500;
}

/* 错误信息 */
.error-message {
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #ff4757;
  padding: 0 5rpx;
}


</style>