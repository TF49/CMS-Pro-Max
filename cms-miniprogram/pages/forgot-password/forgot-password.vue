<template>
  <view class="forgot-container">
    <!-- 背景装饰 -->
    <view class="bg-decoration">
      <view class="bg-circle bg-circle-1"></view>
      <view class="bg-circle bg-circle-2"></view>
      <view class="bg-circle bg-circle-3"></view>
    </view>
    
    <view class="forgot-box">
      <view class="logo-area">
        <view class="logo-circle">
          <text class="logo-icon">🔑</text>
        </view>
        <text class="logo-text">找回密码</text>
        <text class="logo-subtext">Reset Your Password</text>
      </view>
      
      <view class="form">
        <view class="form-item">
          <view class="input-wrapper">
            <uni-icons type="person" size="22" color="#0061ff" class="input-icon"></uni-icons>
            <input 
              v-model="form.username" 
              placeholder="请输入用户名" 
              class="input"
              :class="{ 'input-focus': isFocused.username }"
              @focus="isFocused.username = true"
              @blur="isFocused.username = false"
            />
          </view>
        </view>
        
        <view class="form-item">
          <view class="input-wrapper">
            <uni-icons type="paperclip" size="22" color="#0061ff" class="input-icon"></uni-icons>
            <input 
              v-model="form.idCard" 
              placeholder="请输入身份证号" 
              class="input"
              :class="{ 'input-focus': isFocused.idCard }"
              @focus="isFocused.idCard = true"
              @blur="isFocused.idCard = false"
            />
          </view>
        </view>
        
        <view class="form-item">
          <view class="input-wrapper">
            <uni-icons type="locked" size="22" color="#0061ff" class="input-icon"></uni-icons>
            <input 
              v-model="form.newPassword" 
              placeholder="请输入新密码" 
              class="input"
              :class="{ 'input-focus': isFocused.newPassword }"
              @focus="isFocused.newPassword = true"
              @blur="isFocused.newPassword = false"
              password
            />
          </view>
        </view>
        
        <view class="form-item">
          <view class="input-wrapper">
            <uni-icons type="locked" size="22" color="#0061ff" class="input-icon"></uni-icons>
            <input 
              v-model="form.confirmPassword" 
              placeholder="请再次输入新密码" 
              class="input"
              :class="{ 'input-focus': isFocused.confirmPassword }"
              @focus="isFocused.confirmPassword = true"
              @blur="isFocused.confirmPassword = false"
              password
            />
          </view>
        </view>
        
        <button 
          class="reset-btn"
          @click="handleResetPassword"
          :loading="loading"
        >
          <text v-if="!loading">重 置 密 码</text>
          <text v-else>重置中...</text>
        </button>
        
        <view class="links">
          <navigator url="/pages/login/login" open-type="redirect" class="link">
            <uni-icons type="arrow-left" size="16" class="link-icon"></uni-icons>
            返回登录
          </navigator>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { post } from '@/utils/request'
import validator from '@/utils/validator'

export default {
  data() {
    return {
      form: {
        username: '',
        idCard: '',
        newPassword: '',
        confirmPassword: ''
      },
      loading: false,
      isFocused: {
        username: false,
        idCard: false,
        newPassword: false,
        confirmPassword: false
      }
    }
  },
  methods: {
    handleResetPassword() {
      if (!this.form.username) {
        uni.showToast({ title: '请输入用户名', icon: 'none' })
        return
      }
      if (!this.form.idCard) {
        uni.showToast({ title: '请输入身份证号', icon: 'none' })
        return
      }
      if (!validator.isIdCard(this.form.idCard)) {
        uni.showToast({ title: '身份证号格式不正确', icon: 'none' })
        return
      }
      if (!this.form.newPassword) {
        uni.showToast({ title: '请输入新密码', icon: 'none' })
        return
      }
      if (this.form.newPassword.length < 6) {
        uni.showToast({ title: '密码长度不能少于6位', icon: 'none' })
        return
      }
      if (this.form.newPassword !== this.form.confirmPassword) {
        uni.showToast({ title: '两次输入的密码不一致', icon: 'none' })
        return
      }
      
      this.loading = true
      
      post('/api/auth/reset-password', this.form)
        .then(res => {
          if (res.code === 200) {
            uni.showToast({
              title: '密码重置成功',
              icon: 'success'
            })
            setTimeout(() => {
              uni.redirectTo({
                url: '/pages/login/login'
              })
            }, 1500)
          } else {
            uni.showToast({
              title: res.msg || res.message || '重置失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          uni.showToast({
            title: err.message || '重置失败，请稍后重试',
            icon: 'none'
          })
        })
        .finally(() => {
          this.loading = false
        })
    }
  }
}
</script>

<style scoped>
.forgot-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #005bea 0%, #00c6fb 100%);
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  animation: float 6s ease-in-out infinite;
}

.bg-circle-1 { width: 400rpx; height: 400rpx; top: 10%; left: 10%; animation-delay: 0s; }
.bg-circle-2 { width: 300rpx; height: 300rpx; bottom: 15%; right: 15%; animation-delay: 2s; }
.bg-circle-3 { width: 200rpx; height: 200rpx; top: 60%; left: 20%; animation-delay: 4s; }

@keyframes float {
  0%, 100% { transform: translateY(0rpx) rotate(0deg); }
  50% { transform: translateY(-40rpx) rotate(180deg); }
}

.forgot-box {
  width: 90%;
  max-width: 650rpx;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 48rpx;
  padding: 60rpx 60rpx;
  box-shadow: 0 40rpx 120rpx rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(20px);
  position: relative;
  overflow: hidden;
}

.forgot-box::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0; height: 8rpx;
  background: linear-gradient(90deg, #005bea, #00c6fb);
  border-radius: 48rpx 48rpx 0 0;
}

.logo-area {
  text-align: center;
  margin-bottom: 40rpx;
}

.logo-circle {
  width: 140rpx;
  height: 140rpx;
  background: linear-gradient(135deg, #005bea 0%, #00c6fb 100%);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto 20rpx;
  box-shadow: 0 15rpx 40rpx rgba(0, 91, 234, 0.3);
}

.logo-icon {
  font-size: 60rpx;
  color: white;
}

.logo-text {
  font-size: 40rpx;
  font-weight: 700;
  color: #2d3748;
  display: block;
}

.logo-subtext {
  font-size: 20rpx;
  color: #718096;
  text-transform: uppercase;
}

.form-item {
  margin-bottom: 30rpx;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input {
  width: 100%;
  height: 90rpx;
  border: 4rpx solid #e2e8f0;
  border-radius: 24rpx;
  padding: 0 40rpx 0 90rpx;
  font-size: 28rpx;
  background: #f7fafc;
  transition: all 0.3s ease;
}

.input-focus {
  border-color: #005bea;
  background: white;
  box-shadow: 0 0 0 6rpx rgba(0, 91, 234, 0.1);
}

.input-icon {
  position: absolute;
  left: 30rpx;
  z-index: 2;
}

.reset-btn {
  width: 100%;
  height: 100rpx;
  background: linear-gradient(135deg, #005bea 0%, #00c6fb 100%);
  color: #fff;
  border: none;
  border-radius: 24rpx;
  font-size: 32rpx;
  font-weight: 600;
  margin-top: 20rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 91, 234, 0.3);
}

.links {
  display: flex;
  justify-content: center;
  margin-top: 30rpx;
}

.link {
  font-size: 26rpx;
  color: #005bea;
  display: flex;
  align-items: center;
  gap: 8rpx;
}
</style>
