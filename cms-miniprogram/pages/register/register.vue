<template>
  <view class="register-container">
    <!-- 背景装饰 -->
    <view class="bg-decoration">
      <view class="bg-circle bg-circle-1"></view>
      <view class="bg-circle bg-circle-2"></view>
      <view class="bg-circle bg-circle-3"></view>
    </view>
    
    <view class="register-box">
      <view class="logo-area">
        <view class="logo-circle">
          <text class="logo-icon">📝</text>
        </view>
        <text class="logo-text">人口普查管理系统</text>
        <text class="logo-subtext">Population Census Management System</text>
      </view>
      
      <view class="form">
        <view class="form-item">
          <view class="input-wrapper">
            <uni-icons type="person" size="40" color="#005bea" class="input-icon"></uni-icons>
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
            <uni-icons type="person" size="40" color="#005bea" class="input-icon"></uni-icons>
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
            <uni-icons type="locked" size="40" color="#005bea" class="input-icon"></uni-icons>
            <input 
              v-model="form.password" 
              placeholder="请输入密码" 
              class="input"
              :class="{ 'input-focus': isFocused.password }"
              @focus="isFocused.password = true"
              @blur="isFocused.password = false"
              password
            />
          </view>
        </view>
        
        <view class="form-item">
          <view class="input-wrapper">
            <uni-icons type="locked" size="40" color="#005bea" class="input-icon"></uni-icons>
            <input 
              v-model="form.confirmPassword" 
              placeholder="请再次输入密码" 
              class="input"
              :class="{ 'input-focus': isFocused.confirmPassword }"
              @focus="isFocused.confirmPassword = true"
              @blur="isFocused.confirmPassword = false"
              password
            />
          </view>
        </view>
        
        <button 
          class="register-btn"
          @click="handleRegister"
          :loading="loading"
        >
          <text v-if="!loading">注 册</text>
          <text v-else>注册中...</text>
        </button>
        
        <view class="links">
          <navigator url="/pages/login/login" class="link">
            <uni-icons type="arrow-left" size="28" class="link-icon"></uni-icons>
            已有账号？登录
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
        password: '',
        confirmPassword: '',
        idCard: '',
        role: 'USER' // 默认角色
      },
      loading: false,
      isFocused: {
        username: false,
        password: false,
        confirmPassword: false,
        idCard: false
      }
    }
  },
  methods: {
    handleRegister() {
      
      // 表单验证
      if (!this.form.username) {
        uni.showToast({ title: '请输入用户名', icon: 'none' })
        return
      }
      
      if (!validator.isUsername(this.form.username)) {
        uni.showToast({ title: '用户名长度必须在3-20个字符之间', icon: 'none' })
        return
      }
      
      if (!this.form.password) {
        uni.showToast({ title: '请输入密码', icon: 'none' })
        return
      }
      
      if (!validator.isPassword(this.form.password)) {
        uni.showToast({ title: '密码长度必须在6-20个字符之间', icon: 'none' })
        return
      }
      
      if (this.form.password !== this.form.confirmPassword) {
        uni.showToast({ title: '两次输入的密码不一致', icon: 'none' })
        return
      }
      
      if (!this.form.idCard) {
        uni.showToast({ title: '请输入身份证号', icon: 'none' })
        return
      }
      
      // 身份证号格式验证
      if (!validator.isIdCard(this.form.idCard)) {
        uni.showToast({ title: '请输入有效的身份证号', icon: 'none' })
        return
      }
      
      this.loading = true
      
      post('/api/auth/register', this.form)
        .then(res => {
          if (res.code === 200) {
            uni.showToast({
              title: '注册成功',
              icon: 'success'
            })
            
            // 注册成功后跳转到登录页
            setTimeout(() => {
              uni.redirectTo({
                url: '/pages/login/login'
              })
            }, 1500)
          } else {
            uni.showToast({
              title: res.msg || res.message || '注册失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          uni.showToast({
            title: '注册失败，请稍后重试',
            icon: 'none'
          })
          console.error(err)
        })
        .finally(() => {
          this.loading = false
        })
    }
  }
}
</script>

<style scoped>
/* 现代化渐变背景 - 蓝色系 */
.register-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #005bea 0%, #00c6fb 100%);
  position: relative;
  overflow: hidden;
}

/* 背景装饰效果 */
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
  backdrop-filter: blur(20rpx);
  animation: float 6s ease-in-out infinite;
}

.bg-circle-1 {
  width: 400rpx;
  height: 400rpx;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.bg-circle-2 {
  width: 300rpx;
  height: 300rpx;
  bottom: 15%;
  right: 15%;
  animation-delay: 2s;
}

.bg-circle-3 {
  width: 200rpx;
  height: 200rpx;
  top: 60%;
  left: 20%;
  animation-delay: 4s;
}

/* 浮动动画 */
@keyframes float {
  0%, 100% {
    transform: translateY(0rpx) rotate(0deg);
  }
  50% {
    transform: translateY(-40rpx) rotate(180deg);
  }
}

/* 注册盒子 */
.register-box {
  width: 90%;
  max-width: 840rpx;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 48rpx;
  padding: 100rpx 70rpx;
  box-shadow: 0 40rpx 120rpx rgba(0, 0, 0, 0.15),
              0 0 0 2rpx rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(40rpx);
  transform: translateY(0);
  animation: slideUp 0.8s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards;
  position: relative;
  overflow: hidden;
}

/* 渐变色边框效果 */
.register-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 8rpx;
  background: linear-gradient(90deg, #005bea, #00c6fb);
  border-radius: 48rpx 48rpx 0 0;
}

/* 滑入动画 */
@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(60rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Logo区域 */
.logo-area {
  text-align: center;
  margin-bottom: 80rpx;
  animation: fadeIn 1s ease-out 0.3s both;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.logo-circle {
  width: 180rpx;
  height: 180rpx;
  background: linear-gradient(135deg, #005bea 0%, #00c6fb 100%);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto 40rpx;
  box-shadow: 0 20rpx 60rpx rgba(0, 91, 234, 0.3);
  animation: pulse 2s ease-in-out infinite;
}

/* 呼吸灯效果 */
@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 20rpx 60rpx rgba(0, 91, 234, 0.3);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 30rpx 80rpx rgba(0, 91, 234, 0.4);
  }
}

.logo-icon {
  font-size: 80rpx;
  color: white;
  animation: rotate 4s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.logo-text {
  font-size: 48rpx;
  font-weight: 700;
  color: #2d3748;
  display: block;
  margin-bottom: 16rpx;
  background: linear-gradient(135deg, #005bea 0%, #00c6fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-subtext {
  font-size: 26rpx;
  color: #718096;
  letter-spacing: 3rpx;
  text-transform: uppercase;
}

/* 表单区域 */
.form {
  width: 100%;
  animation: fadeIn 1s ease-out 0.6s both;
}

.form-item {
  margin-bottom: 56rpx;
}

/* 输入框容器 */
.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
}

/* 输入框样式 */
.input {
  width: 100%;
  height: 100rpx;
  border: 4rpx solid #e2e8f0;
  border-radius: 32rpx;
  padding: 0 48rpx 0 100rpx;
  font-size: 30rpx;
  box-sizing: border-box;
  background: #f7fafc;
  color: #2d3748;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 500;
}

/* 输入框聚焦效果 */
.input-focus {
  border-color: #005bea;
  background: white;
  box-shadow: 0 0 0 8rpx rgba(0, 91, 234, 0.1);
  transform: translateY(-4rpx);
}

.input:focus {
  outline: none;
}

/* 输入框图标 */
.input-icon {
  position: absolute;
  left: 40rpx;
  z-index: 2;
  transition: all 0.3s ease;
}

/* 聚焦时图标颜色变化 */
.input:focus + .input-icon,
.input-focus + .input-icon {
  color: #005bea;
  transform: scale(1.1);
}

/* 注册按钮 */
.register-btn {
  width: 100%;
  height: 110rpx;
  background: linear-gradient(135deg, #005bea 0%, #00c6fb 100%);
  color: #fff;
  border: none;
  border-radius: 32rpx;
  font-size: 34rpx;
  font-weight: 600;
  margin-top: 30rpx;
  box-shadow: 0 16rpx 50rpx rgba(0, 91, 234, 0.4);
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  letter-spacing: 1rpx;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 按钮悬停效果 */
.register-btn:hover {
  transform: translateY(-6rpx);
  box-shadow: 0 24rpx 70rpx rgba(0, 91, 234, 0.5);
}

/* 按钮点击效果 */
.register-btn:active {
  transform: translateY(0);
  box-shadow: 0 12rpx 40rpx rgba(0, 91, 234, 0.35);
}

/* 按钮渐变动画 */
.register-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.register-btn:hover::before {
  left: 100%;
}

/* 链接区域 */
.links {
  display: flex;
  justify-content: center;
  margin-top: 60rpx;
  animation: fadeIn 1s ease-out 0.9s both;
}

.link {
  font-size: 28rpx;
  color: #005bea;
  text-decoration: none;
  padding: 20rpx 40rpx;
  border-radius: 50rpx;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 12rpx;
  font-weight: 500;
  background: rgba(0, 91, 234, 0.05);
  border: 2rpx solid rgba(0, 91, 234, 0.1);
}

/* 链接悬停效果 */
.link:hover {
  background: rgba(0, 91, 234, 0.15);
  border-color: rgba(0, 91, 234, 0.3);
  transform: translateY(-4rpx);
  box-shadow: 0 8rpx 30rpx rgba(0, 91, 234, 0.2);
}

.link-icon {
  transition: transform 0.3s ease;
}

.link:hover .link-icon {
  transform: scale(1.2) rotate(-90deg);
}

/* 占位符样式 */
.input::placeholder {
  color: #a0aec0;
  font-weight: 400;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .register-box {
    padding: 80rpx 50rpx;
    margin: 0 40rpx;
  }
  
  .logo-circle {
    width: 160rpx;
    height: 160rpx;
  }
  
  .logo-icon {
    font-size: 80rpx;
  }
  
  .logo-text {
    font-size: 44rpx;
  }
  
  .input {
    height: 100rpx;
    font-size: 30rpx;
  }
  
  .register-btn {
    height: 108rpx;
    font-size: 32rpx;
  }
}
</style>