<template>
  <view class="login-container">
    <!-- 背景装饰 -->
    <view class="bg-decoration">
      <view class="bg-circle bg-circle-1"></view>
      <view class="bg-circle bg-circle-2"></view>
      <view class="bg-circle bg-circle-3"></view>
    </view>
    
    <view class="login-box">
      <view class="logo-area">
        <view class="logo-circle">
          <text class="logo-icon">🏙️</text>
        </view>
        <text class="logo-text">人口普查管理系统</text>
        <text class="logo-subtext">Population Census Management System</text>
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
            <uni-icons type="locked" size="22" color="#0061ff" class="input-icon"></uni-icons>
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
        
        <button 
          class="login-btn"
          @click="handleLogin"
          :loading="loading"
        >
          <text v-if="!loading">登 录</text>
          <text v-else>登录中...</text>
        </button>
        
        <view class="links">
          <navigator url="/pages/register/register" class="link">
            <uni-icons type="plus" size="16" class="link-icon"></uni-icons>
            注册新账号
          </navigator>
          <view class="link-divider"></view>
          <navigator url="/pages/forgot-password/forgot-password" class="link">
            <uni-icons type="help" size="16" class="link-icon"></uni-icons>
            忘记密码？
          </navigator>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { post } from '@/utils/request'

export default {
  data() {
    return {
      form: {
        username: '',
        password: ''
      },
      loading: false,
      isFocused: {
        username: false,
        password: false
      }
    }
  },
  methods: {
    handleLogin() {
      if (!this.form.username || !this.form.password) {
        uni.showToast({
          title: '请输入用户名和密码',
          icon: 'none'
        })
        return
      }
      
      this.loading = true
      
      post('/api/auth/login', this.form)
        .then(res => {
          if (res.code === 200) {
            // 检查用户角色 - 管理员不能登录小程序
            if (res.data.user && res.data.user.role === 'ADMIN') {
              uni.showToast({
                title: '管理员请使用网页端登录',
                icon: 'none',
                duration: 3000
              })
              return
            }
            
            // 保存token
            uni.setStorageSync('token', res.data.token)
            
            // 保存用户信息（包含userId和role）
            const userInfo = {
              id: res.data.user.id,
              userId: res.data.user.id,
              username: res.data.user.username,
              role: res.data.user.role,
              email: res.data.user.email,
              phone: res.data.user.phone,
              idCard: res.data.user.idCard
            }
            uni.setStorageSync('userInfo', userInfo)
            
            // 显示欢迎信息
            uni.showToast({
              title: '登录成功',
              icon: 'success',
              duration: 1500
            })
            
            // 延迟跳转到首页
            setTimeout(() => {
              uni.switchTab({
                url: '/pages/index/index'
              })
            }, 1500)
          } else {
            uni.showToast({
              title: res.msg || res.message || '登录失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          // 显示更详细的错误信息
          const errorMessage = err.message || '登录失败，请稍后重试'
          uni.showToast({
            title: errorMessage,
            icon: 'none',
            duration: 2000
          })
          console.error('登录错误:', err)
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
.login-container {
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
  backdrop-filter: blur(10px);
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

/* 登录盒子 */
.login-box {
  width: 90%;
  max-width: 650rpx;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 48rpx;
  padding: 80rpx 60rpx;
  box-shadow: 0 40rpx 120rpx rgba(0, 0, 0, 0.15),
              0 0 0 2rpx rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(20px);
  transform: translateY(0);
  animation: slideUp 0.8s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards;
  position: relative;
  overflow: hidden;
}

/* 渐变色边框效果 */
.login-box::before {
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
  margin-bottom: 60rpx;
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
  margin: 0 auto 30rpx;
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
  font-size: 24rpx;
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
  margin-bottom: 40rpx;
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

/* 登录按钮 */
.login-btn {
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
  align-items: center;
  justify-content: center;
}

/* 按钮悬停效果 */
.login-btn:hover {
  transform: translateY(-6rpx);
  box-shadow: 0 24rpx 70rpx rgba(0, 91, 234, 0.5);
}

/* 按钮点击效果 */
.login-btn:active {
  transform: translateY(0);
  box-shadow: 0 12rpx 40rpx rgba(0, 91, 234, 0.35);
}

/* 按钮渐变动画 */
.login-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.login-btn:hover::before {
  left: 100%;
}

/* 链接区域 */
.links {
  display: flex;
  justify-content: center;
  margin-top: 50rpx;
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

.link-divider {
  width: 2rpx;
  height: 30rpx;
  background-color: rgba(0, 91, 234, 0.2);
  margin: 0 20rpx;
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
  transform: scale(1.2) rotate(90deg);
}

/* 占位符样式 */
.input::placeholder {
  color: #a0aec0;
  font-weight: 400;
}

/* 响应式设计 - 小屏幕适配 */
@media (max-width: 320px) {
  .login-box {
    padding: 60rpx 40rpx;
    width: 95%;
  }
  
  .logo-circle {
    width: 140rpx;
    height: 140rpx;
  }
  
  .logo-icon {
    font-size: 60rpx;
  }
  
  .logo-text {
    font-size: 40rpx;
  }
  
  .input {
    height: 90rpx;
    font-size: 28rpx;
  }
  
  .login-btn {
    height: 100rpx;
    font-size: 30rpx;
  }
}
</style>
