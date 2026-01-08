<template>
  <view class="edit-password-container">
    <!-- 头部导航 -->
    <view class="header">
      <view class="header-left">
        <button class="back-btn" @click="handleBack">
          <uni-icons type="back" size="28" color="#333"></uni-icons>
        </button>
      </view>
      <view class="header-title">修改密码</view>
      <view class="header-right"></view>
    </view>
    
    <!-- 表单容器 -->
    <view class="form-container">
      <!-- 当前密码 -->
      <view class="form-item">
        <view class="form-label">
          <text class="label-text">当前密码</text>
          <text class="required">*</text>
        </view>
        <view class="form-control">
          <input
            class="input-field"
            type="password"
            placeholder="请输入当前密码"
            v-model="form.oldPassword"
            :password-visibility="showOldPassword"
            maxlength="20"
          />
          <button class="visibility-btn" @click="showOldPassword = !showOldPassword">
            <uni-icons
              :type="showOldPassword ? 'eye-filled' : 'eye-slash-filled'"
              size="24"
              color="#999"
            ></uni-icons>
          </button>
        </view>
        <view v-if="errors.oldPassword" class="error-message">{{ errors.oldPassword }}</view>
      </view>
      
      <!-- 新密码 -->
      <view class="form-item">
        <view class="form-label">
          <text class="label-text">新密码</text>
          <text class="required">*</text>
        </view>
        <view class="form-control">
          <input
            class="input-field"
            type="password"
            placeholder="请输入新密码（6-20个字符）"
            v-model="form.newPassword"
            :password-visibility="showNewPassword"
            maxlength="20"
          />
          <button class="visibility-btn" @click="showNewPassword = !showNewPassword">
            <uni-icons
              :type="showNewPassword ? 'eye-filled' : 'eye-slash-filled'"
              size="24"
              color="#999"
            ></uni-icons>
          </button>
        </view>
        <view v-if="errors.newPassword" class="error-message">{{ errors.newPassword }}</view>
      </view>
      
      <!-- 确认新密码 -->
      <view class="form-item">
        <view class="form-label">
          <text class="label-text">确认新密码</text>
          <text class="required">*</text>
        </view>
        <view class="form-control">
          <input
            class="input-field"
            type="password"
            placeholder="请再次输入新密码"
            v-model="form.confirmPassword"
            :password-visibility="showConfirmPassword"
            maxlength="20"
          />
          <button class="visibility-btn" @click="showConfirmPassword = !showConfirmPassword">
            <uni-icons
              :type="showConfirmPassword ? 'eye-filled' : 'eye-slash-filled'"
              size="24"
              color="#999"
            ></uni-icons>
          </button>
        </view>
        <view v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</view>
      </view>
      
      <!-- 密码强度提示 -->
      <view class="password-strength" v-if="form.newPassword">
        <text class="strength-label">密码强度：</text>
        <view class="strength-bars">
          <view
            class="strength-bar"
            :class="getStrengthClass(1)"
          ></view>
          <view
            class="strength-bar"
            :class="getStrengthClass(2)"
          ></view>
          <view
            class="strength-bar"
            :class="getStrengthClass(3)"
          ></view>
        </view>
        <text class="strength-text">{{ getStrengthText() }}</text>
      </view>
      
      <!-- 提交按钮 -->
      <button
        class="submit-btn"
        :disabled="loading"
        @click="handleSubmit"
      >
        <view v-if="loading" class="loading-spinner">
          <uni-icons type="spinner-cycle" size="24" color="#fff" class="loading-icon"></uni-icons>
        </view>
        <text v-else class="submit-text">确认修改</text>
      </button>
      
      <!-- 提示信息 -->
      <view class="tip-message">
        <text class="tip-text">密码建议：包含字母、数字和特殊字符，长度6-20位</text>
      </view>
    </view>
  </view>
</template>

<script>
import { put } from '@/utils/request'

export default {
  data() {
    return {
      // 加载状态
      loading: false,
      // 显示密码
      showOldPassword: false,
      showNewPassword: false,
      showConfirmPassword: false,
      // 表单数据
      form: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      // 错误信息
      errors: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
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
    
    // 表单验证
    validateForm() {
      let isValid = true
      
      // 重置错误信息
      this.errors = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      
      // 验证当前密码
      if (!this.form.oldPassword.trim()) {
        this.errors.oldPassword = '当前密码不能为空'
        isValid = false
      }
      
      // 验证新密码
      if (!this.form.newPassword.trim()) {
        this.errors.newPassword = '新密码不能为空'
        isValid = false
      } else if (this.form.newPassword.length < 6 || this.form.newPassword.length > 20) {
        this.errors.newPassword = '新密码长度必须在6-20个字符之间'
        isValid = false
      }
      
      // 验证确认密码
      if (!this.form.confirmPassword.trim()) {
        this.errors.confirmPassword = '确认密码不能为空'
        isValid = false
      } else if (this.form.newPassword !== this.form.confirmPassword) {
        this.errors.confirmPassword = '两次输入的密码不一致'
        isValid = false
      }
      
      return isValid
    },
    
    // 获取密码强度等级（1-3）
    getPasswordStrength() {
      const password = this.form.newPassword
      if (!password) return 0
      
      let strength = 0
      // 长度大于等于8
      if (password.length >= 8) strength++
      // 包含字母
      if (/[a-zA-Z]/.test(password)) strength++
      // 包含数字
      if (/\d/.test(password)) strength++
      // 包含特殊字符
      if (/[^a-zA-Z0-9]/.test(password)) strength++
      
      return Math.min(strength, 3)
    },
    
    // 获取密码强度样式类
    getStrengthClass(level) {
      const strength = this.getPasswordStrength()
      if (level === 1) {
        return strength >= 1 ? 'strength-weak' : 'strength-empty'
      } else if (level === 2) {
        return strength >= 2 ? 'strength-medium' : 'strength-empty'
      } else {
        return strength >= 3 ? 'strength-strong' : 'strength-empty'
      }
    },
    
    // 获取密码强度文本
    getStrengthText() {
      const strength = this.getPasswordStrength()
      switch (strength) {
        case 0:
          return '未输入'
        case 1:
          return '弱'
        case 2:
          return '中'
        case 3:
          return '强'
        default:
          return '未输入'
      }
    },
    
    // 提交表单
    handleSubmit() {
      // 表单验证
      if (!this.validateForm()) {
        return
      }
      
      // 显示加载状态
      this.loading = true
      
      // 调用修改密码API
      put('/api/user/changePassword', {
        oldPassword: this.form.oldPassword,
        newPassword: this.form.newPassword
      })
        .then(res => {
          if (res.code === 200) {
            // 修改成功
            uni.showToast({
              title: '密码修改成功',
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
            // 修改失败
            uni.showToast({
              title: res.msg || res.message || '密码修改失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('修改密码失败', err)
          uni.showToast({
            title: '网络错误，请稍后重试',
            icon: 'none'
          })
        })
        .finally(() => {
          // 隐藏加载状态
          this.loading = false
        })
    }
  }
}
</script>

<style scoped>
.edit-password-container {
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

/* 表单容器 */
.form-container {
  padding: 30rpx 20rpx;
  background: white;
  margin: 20rpx;
  border-radius: 16rpx;
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
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border-radius: 12rpx;
  transition: all 0.3s ease;
  border: 2rpx solid transparent;
}

.form-control:focus-within {
  background: white;
  border-color: #667eea;
  box-shadow: 0 0 0 8rpx rgba(102, 126, 234, 0.1);
}

.input-field {
  flex: 1;
  height: 80rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  color: #333;
  background: transparent;
  border: none;
  outline: none;
  border-radius: 12rpx;
}

.input-field::placeholder {
  color: #999;
}

.visibility-btn {
  background: none;
  border: none;
  padding: 10rpx 20rpx;
  border-radius: 0 12rpx 12rpx 0;
  transition: all 0.3s ease;
}

.visibility-btn:hover {
  background: rgba(0, 0, 0, 0.05);
}

/* 密码强度 */
.password-strength {
  margin: 20rpx 0 30rpx;
  padding: 20rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
  display: flex;
  flex-direction: column;
  gap: 15rpx;
}

.strength-label {
  font-size: 26rpx;
  color: #666;
  font-weight: bold;
}

.strength-bars {
  display: flex;
  gap: 10rpx;
  align-items: center;
  height: 10rpx;
}

.strength-bar {
  flex: 1;
  height: 8rpx;
  border-radius: 4rpx;
  background: #e0e0e0;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.strength-bar::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.5), transparent);
  animation: shimmer 2s infinite;
}

.strength-bar.strength-empty {
  background: #e0e0e0;
}

.strength-bar.strength-weak {
  background: #ff4757;
  box-shadow: 0 0 10rpx rgba(255, 71, 87, 0.3);
}

.strength-bar.strength-medium {
  background: #ffa502;
  box-shadow: 0 0 10rpx rgba(255, 165, 2, 0.3);
}

.strength-bar.strength-strong {
  background: #2ed573;
  box-shadow: 0 0 10rpx rgba(46, 213, 115, 0.3);
}

.strength-text {
  font-size: 24rpx;
  font-weight: bold;
  color: #666;
}

/* 提交按钮 */
.submit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12rpx;
  height: 90rpx;
  width: 100%;
  font-size: 32rpx;
  font-weight: bold;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.3s ease;
  margin-top: 20rpx;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2rpx);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
}

.submit-btn:disabled {
  opacity: 0.6;
  transform: none;
  box-shadow: none;
}

.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-icon {
  animation: rotate 1s linear infinite;
}

.submit-text {
  font-size: 32rpx;
  font-weight: bold;
}

/* 提示信息 */
.tip-message {
  margin-top: 25rpx;
  padding: 20rpx;
  background: #f0f4ff;
  border-radius: 12rpx;
  border-left: 8rpx solid #667eea;
}

.tip-text {
  font-size: 24rpx;
  color: #667eea;
  line-height: 1.5;
}

/* 错误信息 */
.error-message {
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #ff4757;
  padding: 0 5rpx;
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

@keyframes shimmer {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}
</style>