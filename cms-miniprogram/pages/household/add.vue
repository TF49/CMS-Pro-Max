<template>
  <view class="add-container">
    <!-- 顶部渐变背景 -->
    <view class="top-gradient"></view>
    
    <!-- 头部导航 -->
    <view class="header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="header-content">
        <button class="back-btn" @click="handleBack">
          <uni-icons type="back" size="24" color="#fff"></uni-icons>
        </button>
        <view class="header-title">
          <text class="title-main">添加户籍信息</text>
          <text class="title-sub">填写户籍相关信息</text>
        </view>
        <button class="save-btn" @click="handleSave" :disabled="loading">
          <uni-icons v-if="!loading" type="checkmarkempty" size="24" color="#fff"></uni-icons>
          <uni-icons v-else type="spinner-cycle" size="24" color="#fff" class="rotating"></uni-icons>
        </button>
      </view>
    </view>
    
    <!-- 表单内容 -->
    <scroll-view class="form-scroll" scroll-y="true" show-scrollbar="false">
      <view class="form-container">
        <!-- 户籍信息 -->
        <view class="form-section animate-slide-up">
          <view class="section-header">
            <view class="section-icon bg-blue">
              <uni-icons type="home-filled" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">户籍信息</text>
              <text class="section-subtitle">Household Information</text>
            </view>
          </view>
          
          <view class="form-item">
            <text class="label required">户籍编号</text>
            <view class="input-wrapper">
              <uni-icons type="bars" size="20" color="#999" class="input-icon"></uni-icons>
              <input 
                v-model="form.householdNumber" 
                placeholder="请输入户籍编号" 
                class="input"
                maxlength="50"
              />
            </view>
            <text v-if="errors.householdNumber" class="error-text">{{ errors.householdNumber }}</text>
          </view>
          
          <view class="form-item">
            <text class="label required">户主姓名</text>
            <view class="input-wrapper">
              <uni-icons type="person" size="20" color="#999" class="input-icon"></uni-icons>
              <input 
                v-model="form.householderName" 
                placeholder="请输入户主姓名" 
                class="input"
                maxlength="20"
              />
            </view>
            <text v-if="errors.householderName" class="error-text">{{ errors.householderName }}</text>
          </view>
          
          <view class="form-item">
            <text class="label required">身份证号</text>
            <view class="input-wrapper">
              <uni-icons type="wallet" size="20" color="#999" class="input-icon"></uni-icons>
              <input 
                v-model="form.householderIdCard" 
                placeholder="请输入身份证号" 
                class="input"
                maxlength="18"
              />
            </view>
            <text v-if="errors.householderIdCard" class="error-text">{{ errors.householderIdCard }}</text>
          </view>
          
          <view class="form-row">
            <view class="form-item half">
              <text class="label required">联系电话</text>
              <view class="input-wrapper">
                <uni-icons type="phone" size="20" color="#999" class="input-icon"></uni-icons>
                <input 
                  v-model="form.phoneNumber" 
                  placeholder="手机号" 
                  class="input"
                  type="number"
                  maxlength="11"
                />
              </view>
              <text v-if="errors.phoneNumber" class="error-text">{{ errors.phoneNumber }}</text>
            </view>
            
            <view class="form-item half">
              <text class="label required">人口数量</text>
              <view class="input-wrapper">
                <uni-icons type="person-filled" size="20" color="#999" class="input-icon"></uni-icons>
                <input 
                  v-model.number="form.populationCount" 
                  placeholder="人数" 
                  class="input"
                  type="number"
                />
              </view>
            </view>
          </view>
          
          <view class="form-item">
            <text class="label required">详细地址</text>
            <view class="textarea-wrapper">
              <uni-icons type="location" size="20" color="#999" class="textarea-icon"></uni-icons>
              <textarea 
                v-model="form.address" 
                placeholder="请输入详细地址" 
                class="textarea"
                maxlength="100"
                auto-height="true"
              ></textarea>
            </view>
            <text v-if="errors.address" class="error-text">{{ errors.address }}</text>
          </view>
          
          <view class="form-item">
            <text class="label required">登记日期</text>
            <picker 
              mode="date" 
              :value="form.registrationDate"
              :end="today"
              @change="handleDateChange"
            >
              <view class="picker-wrapper">
                <uni-icons type="calendar" size="20" color="#999" class="input-icon"></uni-icons>
                <text class="picker-text">{{ form.registrationDate || 'YYYY-MM-DD' }}</text>
              </view>
            </picker>
            <text v-if="errors.registrationDate" class="error-text">{{ errors.registrationDate }}</text>
          </view>
          
          <view class="form-item">
            <text class="label">备注信息</text>
            <view class="textarea-wrapper">
              <uni-icons type="chat" size="20" color="#999" class="textarea-icon"></uni-icons>
              <textarea 
                v-model="form.notes" 
                placeholder="请输入备注信息（可选）" 
                class="textarea"
                maxlength="500"
                auto-height="true"
              ></textarea>
            </view>
          </view>
        </view>
        
        <!-- 底部提交按钮 -->
        <view class="submit-section">
          <button class="submit-btn" @click="handleSave" :disabled="loading">
            <uni-icons v-if="!loading" type="checkmarkempty" size="24" color="#fff"></uni-icons>
            <uni-icons v-else type="spinner-cycle" size="24" color="#fff" class="rotating"></uni-icons>
            <text>{{ loading ? '保存中...' : '保存信息' }}</text>
          </button>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { post } from '@/utils/request'
import validator from '@/utils/validator'

export default {
  data() {
    return {
      loading: false,
      form: {
        householdNumber: '',
        householderName: '',
        householderIdCard: '',
        address: '',
        phoneNumber: '',
        registrationDate: '',
        populationCount: 1,
        notes: ''
      },
      errors: {
        householdNumber: '',
        householderName: '',
        householderIdCard: '',
        address: '',
        phoneNumber: '',
        registrationDate: ''
      },
      today: '',
      statusBarHeight: 0
    }
  },
  onLoad() {
    this.getStatusBarHeight()
    this.getTodayDate()
  },
  methods: {
    handleBack() {
      const pages = getCurrentPages()
      if (pages.length > 1) {
        uni.navigateBack()
      } else {
        // 如果是第一个页面，跳转到首页
        uni.switchTab({ url: '/pages/index/index' })
      }
    },
    
    getStatusBarHeight() {
      // #ifdef MP-WEIXIN
      const systemInfo = uni.getSystemInfoSync()
      this.statusBarHeight = systemInfo.statusBarHeight || 0
      // #endif
    },
    
    getTodayDate() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      this.today = `${year}-${month}-${day}`
    },
    
    handleDateChange(e) {
      this.form.registrationDate = e.detail.value
    },
    
    validateForm() {
      let isValid = true
      
      // 重置错误信息
      this.errors = {
        householdNumber: '',
        householderName: '',
        householderIdCard: '',
        address: '',
        phoneNumber: '',
        registrationDate: ''
      }
      
      // 验证户籍编号
      if (!validator.required(this.form.householdNumber)) {
        this.errors.householdNumber = '请输入户籍编号'
        isValid = false
      }
      
      // 验证户主姓名
      if (!validator.required(this.form.householderName)) {
        this.errors.householderName = '请输入户主姓名'
        isValid = false
      } else if (this.form.householderName.length < 2 || this.form.householderName.length > 20) {
        this.errors.householderName = '姓名长度应在2-20个字符之间'
        isValid = false
      }
      
      // 验证身份证号
      if (!validator.required(this.form.householderIdCard)) {
        this.errors.householderIdCard = '请输入身份证号'
        isValid = false
      } else if (!validator.idCard(this.form.householderIdCard)) {
        this.errors.householderIdCard = '身份证号格式不正确'
        isValid = false
      }
      
      // 验证地址
      if (!validator.required(this.form.address)) {
        this.errors.address = '请输入详细地址'
        isValid = false
      } else if (this.form.address.length < 5 || this.form.address.length > 100) {
        this.errors.address = '地址长度应在5-100个字符之间'
        isValid = false
      }
      
      // 验证联系电话
      if (!validator.required(this.form.phoneNumber)) {
        this.errors.phoneNumber = '请输入联系电话'
        isValid = false
      } else if (!validator.phone(this.form.phoneNumber)) {
        this.errors.phoneNumber = '手机号格式不正确'
        isValid = false
      }
      
      // 验证登记日期
      if (!validator.required(this.form.registrationDate)) {
        this.errors.registrationDate = '请选择登记日期'
        isValid = false
      } else if (!validator.date(this.form.registrationDate)) {
        this.errors.registrationDate = '日期格式不正确'
        isValid = false
      } else {
        // 检查日期不能晚于今天
        const selectedDate = new Date(this.form.registrationDate)
        const today = new Date(this.today)
        if (selectedDate > today) {
          this.errors.registrationDate = '登记日期不能晚于今天'
          isValid = false
        }
      }
      
      return isValid
    },
    
    handleSave() {
      if (!this.validateForm()) {
        return
      }
      
      this.loading = true
      
      // 获取当前登录用户信息
      const userInfo = uni.getStorageSync('userInfo')
      const userId = userInfo ? (userInfo.id || userInfo.userId) : null
      
      // 准备提交数据
      const submitData = {
        ...this.form,
        userId: userId  // 关联当前用户
      }
      
      post('/household/add', submitData)
        .then(res => {
          if (res.code === 200) {
            uni.showToast({
              title: '添加成功',
              icon: 'success'
            })
            setTimeout(() => {
              this.handleBack()
            }, 1500)
          } else {
            uni.showToast({
              title: res.msg || res.message || '添加失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('添加户籍信息失败', err)
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
}
</script>

<style scoped>
.add-container {
  background: #f5f7fa;
  min-height: 100vh;
  position: relative;
}

.top-gradient {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 400rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  z-index: 0;
}

.header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: transparent;
  padding: 20rpx;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.back-btn, .save-btn {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10rpx);
  border: 2rpx solid rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
}

.back-btn:active, .save-btn:active {
  transform: scale(0.95);
  background: rgba(255, 255, 255, 0.3);
}

.save-btn:disabled {
  opacity: 0.6;
}

.header-title {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
}

.title-main {
  font-size: 32rpx;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
}

.title-sub {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.9);
  text-shadow: 0 1rpx 4rpx rgba(0, 0, 0, 0.2);
}

.form-scroll {
  height: calc(100vh - 120rpx);
  position: relative;
  z-index: 1;
}

.form-container {
  padding: 20rpx;
  padding-bottom: 40rpx;
}

.form-section {
  background: white;
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.06);
  opacity: 0;
  animation: slideUp 0.6s ease forwards;
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

.section-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 32rpx;
  padding-bottom: 24rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.section-icon {
  width: 56rpx;
  height: 56rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
}

.bg-blue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.section-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.section-subtitle {
  font-size: 22rpx;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 1rpx;
}

.form-item {
  margin-bottom: 28rpx;
}

.form-item:last-child {
  margin-bottom: 0;
}

.form-row {
  display: flex;
  gap: 20rpx;
}

.form-item.half {
  flex: 1;
}

.label {
  display: block;
  font-size: 26rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 12rpx;
}

.required::after {
  content: '*';
  color: #ff4757;
  margin-left: 4rpx;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border: 2rpx solid #e9ecef;
  border-radius: 16rpx;
  padding: 0 20rpx;
  transition: all 0.3s ease;
}

.input-wrapper:focus-within {
  background: white;
  border-color: #667eea;
  box-shadow: 0 0 0 6rpx rgba(102, 126, 234, 0.1);
}

.input-icon {
  margin-right: 12rpx;
  flex-shrink: 0;
}

.input {
  flex: 1;
  height: 88rpx;
  font-size: 28rpx;
  color: #333;
  background: transparent;
  border: none;
}

.picker-wrapper {
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border: 2rpx solid #e9ecef;
  border-radius: 16rpx;
  padding: 0 20rpx;
  height: 88rpx;
  transition: all 0.3s ease;
}

.picker-wrapper:active {
  background: white;
  border-color: #667eea;
  box-shadow: 0 0 0 6rpx rgba(102, 126, 234, 0.1);
}

.picker-text {
  flex: 1;
  font-size: 28rpx;
  color: #333;
  margin-left: 12rpx;
}

.textarea-wrapper {
  position: relative;
  background: #f8f9fa;
  border: 2rpx solid #e9ecef;
  border-radius: 16rpx;
  padding: 20rpx;
  transition: all 0.3s ease;
}

.textarea-wrapper:focus-within {
  background: white;
  border-color: #667eea;
  box-shadow: 0 0 0 6rpx rgba(102, 126, 234, 0.1);
}

.textarea-icon {
  position: absolute;
  top: 24rpx;
  left: 20rpx;
}

.textarea {
  width: 100%;
  min-height: 120rpx;
  font-size: 28rpx;
  color: #333;
  background: transparent;
  border: none;
  padding-left: 48rpx;
  line-height: 1.6;
}

.error-text {
  font-size: 22rpx;
  color: #ff4757;
  margin-top: 8rpx;
  display: block;
}

.submit-section {
  margin-top: 32rpx;
  padding: 0 20rpx;
}

.submit-btn {
  width: 100%;
  height: 96rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 24rpx;
  border: none;
  color: white;
  font-size: 32rpx;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  box-shadow: 0 8rpx 24rpx rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.submit-btn:active:not(:disabled) {
  transform: scale(0.98);
  box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
}

.submit-btn:disabled {
  opacity: 0.7;
}

.rotating {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.animate-slide-up {
  animation: slideUp 0.6s ease forwards;
}
</style>
