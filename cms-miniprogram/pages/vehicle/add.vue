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
          <text class="title-main">添加车辆记录</text>
          <text class="title-sub">填写车辆相关信息</text>
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
        <!-- 基本信息 -->
        <view class="form-section animate-slide-up">
          <view class="section-header">
            <view class="section-icon bg-blue">
              <uni-icons type="car-filled" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">基本信息</text>
              <text class="section-subtitle">Basic Information</text>
            </view>
          </view>
          
          <!-- 车牌号码 -->
          <view class="form-item">
            <text class="label required">车牌号码</text>
            <view class="input-wrapper">
              <uni-icons type="tag" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入车牌号码"
                v-model="form.licensePlate"
                maxlength="20"
              />
            </view>
            <text v-if="errors.licensePlate" class="error-text">{{ errors.licensePlate }}</text>
          </view>
          
          <!-- 车主姓名 -->
          <view class="form-item">
            <text class="label required">车主姓名</text>
            <view class="input-wrapper">
              <uni-icons type="person" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入车主姓名"
                v-model="form.residentName"
                maxlength="20"
              />
            </view>
            <text v-if="errors.residentName" class="error-text">{{ errors.residentName }}</text>
          </view>
          
          <!-- 身份证号 -->
          <view class="form-item">
            <text class="label required">身份证号</text>
            <view class="input-wrapper">
              <uni-icons type="wallet" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入身份证号"
                v-model="form.idCard"
                maxlength="18"
              />
            </view>
            <text v-if="errors.idCard" class="error-text">{{ errors.idCard }}</text>
          </view>
          
          <!-- 车辆类型 -->
          <view class="form-item">
            <text class="label required">车辆类型</text>
            <radio-group :value="form.vehicleType" @change="handleVehicleTypeChange" class="radio-group">
              <label class="radio-item">
                <radio value="轿车" color="#667eea" />
                <text class="radio-text">轿车</text>
              </label>
              <label class="radio-item">
                <radio value="SUV" color="#667eea" />
                <text class="radio-text">SUV</text>
              </label>
              <label class="radio-item">
                <radio value="货车" color="#667eea" />
                <text class="radio-text">货车</text>
              </label>
              <label class="radio-item">
                <radio value="客车" color="#667eea" />
                <text class="radio-text">客车</text>
              </label>
            </radio-group>
          </view>
          
          <view class="form-row">
            <!-- 品牌 -->
            <view class="form-item half">
              <text class="label">品牌</text>
              <view class="input-wrapper">
                <uni-icons type="grid" size="20" color="#999" class="input-icon"></uni-icons>
                <input
                  class="input"
                  type="text"
                  placeholder="请输入车辆品牌"
                  v-model="form.brand"
                  maxlength="20"
                />
              </view>
            </view>
            
            <!-- 型号 -->
            <view class="form-item half">
              <text class="label">型号</text>
              <view class="input-wrapper">
                <uni-icons type="apps" size="20" color="#999" class="input-icon"></uni-icons>
                <input
                  class="input"
                  type="text"
                  placeholder="请输入车辆型号"
                  v-model="form.model"
                  maxlength="20"
                />
              </view>
            </view>
          </view>
          
          <!-- 颜色 -->
          <view class="form-item">
            <text class="label">颜色</text>
            <view class="input-wrapper">
              <uni-icons type="brush" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入车辆颜色"
                v-model="form.color"
                maxlength="20"
              />
            </view>
          </view>
        </view>
        
        <!-- 车辆信息 -->
        <view class="form-section animate-slide-up">
          <view class="section-header">
            <view class="section-icon bg-orange">
              <uni-icons type="info-filled" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">车辆信息</text>
              <text class="section-subtitle">Vehicle Information</text>
            </view>
          </view>
          
          <!-- 发动机号 -->
          <view class="form-item">
            <text class="label">发动机号</text>
            <view class="input-wrapper">
              <uni-icons type="engine" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入发动机号"
                v-model="form.engineNumber"
                maxlength="50"
              />
            </view>
          </view>
          
          <!-- 车架号 -->
          <view class="form-item">
            <text class="label">车架号</text>
            <view class="input-wrapper">
              <uni-icons type="ticket" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入车架号"
                v-model="form.chassisNumber"
                maxlength="50"
              />
            </view>
          </view>
          
          <view class="form-row">
            <!-- 购买日期 -->
            <view class="form-item half">
              <text class="label">购买日期</text>
              <picker 
                mode="date" 
                :value="form.purchaseDate"
                :start="'1900-01-01'"
                :end="today"
                @change="handlePurchaseDateChange"
              >
                <view class="picker-wrapper">
                  <uni-icons type="calendar" size="20" color="#999" class="input-icon"></uni-icons>
                  <text class="picker-text">{{ form.purchaseDate || 'YYYY-MM-DD' }}</text>
                </view>
              </picker>
            </view>
            
            <!-- 购买价格 -->
            <view class="form-item half">
              <text class="label">购买价格</text>
              <view class="input-wrapper">
                <uni-icons type="creditcard" size="20" color="#999" class="input-icon"></uni-icons>
                <input
                  class="input"
                  type="number"
                  placeholder="请输入购买价格"
                  v-model="form.purchasePrice"
                  maxlength="20"
                />
              </view>
            </view>
          </view>
          
          <view class="form-row">
            <!-- 保险到期日 -->
            <view class="form-item half">
              <text class="label">保险到期日</text>
              <picker 
                mode="date" 
                :value="form.insuranceExpiryDate"
                :start="today"
                :end="'2100-12-31'"
                @change="handleInsuranceExpiryDateChange"
              >
                <view class="picker-wrapper">
                  <uni-icons type="shield" size="20" color="#999" class="input-icon"></uni-icons>
                  <text class="picker-text">{{ form.insuranceExpiryDate || 'YYYY-MM-DD' }}</text>
                </view>
              </picker>
            </view>
            
            <!-- 年检日期 -->
            <view class="form-item half">
              <text class="label">年检日期</text>
              <picker 
                mode="date" 
                :value="form.annualInspectionDate"
                :start="today"
                :end="'2100-12-31'"
                @change="handleAnnualInspectionDateChange"
              >
                <view class="picker-wrapper">
                  <uni-icons type="checkmarkcircle" size="20" color="#999" class="input-icon"></uni-icons>
                  <text class="picker-text">{{ form.annualInspectionDate || 'YYYY-MM-DD' }}</text>
                </view>
              </picker>
            </view>
          </view>
        </view>
        
        <!-- 备注信息 -->
        <view class="form-section animate-slide-up">
          <view class="section-header">
            <view class="section-icon bg-green">
              <uni-icons type="chat" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">备注信息</text>
              <text class="section-subtitle">Remarks</text>
            </view>
          </view>
          
          <!-- 备注 -->
          <view class="form-item">
            <text class="label">备注</text>
            <view class="textarea-wrapper">
              <uni-icons type="chat" size="20" color="#999" class="textarea-icon"></uni-icons>
              <textarea
                class="textarea"
                placeholder="请输入备注信息（可选）"
                v-model="form.notes"
                maxlength="500"
                :auto-height="true"
                show-word-limit
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
      // 加载状态
      loading: false,
      // 表单数据
    form: {
      licensePlate: '',
      residentName: '',
      idCard: '',
      vehicleType: '轿车',
      brand: '',
      model: '',
      color: '',
      engineNumber: '',
      chassisNumber: '',
      purchaseDate: '',
      purchasePrice: '',
      insuranceExpiryDate: '',
      annualInspectionDate: '',
      notes: ''
    },
    // 错误信息
    errors: {
      licensePlate: '',
      ownerName: '',
      idCard: ''
    },
      // 日期选择器
      showRegistrationDatePicker: false,
      showInsuranceExpiryDatePicker: false,
      showInspectionExpiryDatePicker: false,
      // 今天日期
      today: '',
      statusBarHeight: 0
    }
  },
  onLoad() {
    this.getStatusBarHeight()
    // 获取今天日期
    this.getTodayDate()
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
    
    // 获取今天日期
    getTodayDate() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      this.today = `${year}-${month}-${day}`
    },
    
    // 车辆类型变化处理
    handleVehicleTypeChange(e) {
      this.form.vehicleType = e.detail.value
    },
    

    
    // 购买日期选择
    handlePurchaseDateChange(e) {
      this.form.purchaseDate = e.detail.value
      this.showRegistrationDatePicker = false
    },
    
    // 保险到期日选择
    handleInsuranceExpiryDateChange(e) {
      this.form.insuranceExpiryDate = e.detail.value
      this.showInsuranceExpiryDatePicker = false
    },
    
    // 年检日期选择
    handleAnnualInspectionDateChange(e) {
      this.form.annualInspectionDate = e.detail.value
      this.showInspectionExpiryDatePicker = false
    },
    
    // 表单验证
    validateForm() {
      let isValid = true
      
      // 重置错误信息
      this.errors = {
        licensePlate: '',
        residentName: '',
        idCard: ''
      }
      
      // 验证车牌号码
      if (!validator.required(this.form.licensePlate)) {
        this.errors.licensePlate = '请输入车牌号码'
        isValid = false
      }
      
      // 验证车主姓名
      if (!validator.required(this.form.residentName)) {
        this.errors.residentName = '请输入车主姓名'
        isValid = false
      }
      
      // 验证身份证号
      if (!validator.required(this.form.idCard)) {
        this.errors.idCard = '请输入身份证号'
        isValid = false
      } else if (!validator.idCard(this.form.idCard)) {
        this.errors.idCard = '身份证号格式不正确'
        isValid = false
      }
      
      return isValid
    },
    
    // 保存车辆记录
    handleSave() {
      // 表单验证
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
      
      // 转换购买价格为数字类型
      if (submitData.purchasePrice) {
        submitData.purchasePrice = Number(submitData.purchasePrice)
      }
      
      post('/vehicle/add', submitData)
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
          console.error('添加车辆记录失败', err)
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

.bg-orange {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
}

.bg-green {
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
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
  outline: none;
}

.input::placeholder {
  color: #999;
}

.picker-wrapper {
  position: relative;
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

.textarea::placeholder {
  color: #999;
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

/* 单选组样式 */
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

.animate-slide-up {
  animation: slideUp 0.6s ease forwards;
}
</style>