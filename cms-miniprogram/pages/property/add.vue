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
          <text class="title-main">添加房产记录</text>
          <text class="title-sub">填写房产相关信息</text>
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
        <!-- 居民信息 -->
        <view class="form-section animate-slide-up">
          <view class="section-header">
            <view class="section-icon bg-blue">
              <uni-icons type="person" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">居民信息</text>
              <text class="section-subtitle">Resident Information</text>
            </view>
          </view>
          
          <!-- 居民姓名 -->
          <view class="form-item">
            <text class="label required">居民姓名</text>
            <view class="input-wrapper">
              <uni-icons type="person" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入居民姓名"
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
              <uni-icons type="card" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入居民身份证号"
                v-model="form.idCard"
                maxlength="18"
              />
            </view>
            <text v-if="errors.idCard" class="error-text">{{ errors.idCard }}</text>
          </view>
        </view>
        
        <!-- 房产基本信息 -->
        <view class="form-section animate-slide-up">
          <view class="section-header">
            <view class="section-icon bg-blue">
              <uni-icons type="home-filled" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">房产基本信息</text>
              <text class="section-subtitle">Property Basic Information</text>
            </view>
          </view>
          
          <!-- 房产类型 -->
          <view class="form-item">
            <text class="label required">房产类型</text>
            <radio-group :value="form.propertyType" @change="handleTypeChange" class="radio-group">
              <label class="radio-item">
                <radio value="商品房" color="#667eea" />
                <text class="radio-text">商品房</text>
              </label>
              <label class="radio-item">
                <radio value="经济适用房" color="#667eea" />
                <text class="radio-text">经济适用房</text>
              </label>
              <label class="radio-item">
                <radio value="农村自建房" color="#667eea" />
                <text class="radio-text">农村自建房</text>
              </label>
              <label class="radio-item">
                <radio value="其他" color="#667eea" />
                <text class="radio-text">其他</text>
              </label>
            </radio-group>
          </view>
          
          <!-- 房产地址 -->
          <view class="form-item">
            <text class="label required">房产地址</text>
            <view class="input-wrapper">
              <uni-icons type="location" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入房产地址"
                v-model="form.address"
                maxlength="100"
              />
            </view>
            <text v-if="errors.address" class="error-text">{{ errors.address }}</text>
          </view>
          
          <view class="form-row">
            <!-- 建筑面积 -->
            <view class="form-item half">
              <text class="label required">建筑面积</text>
              <view class="input-wrapper">
                <uni-icons type="scan" size="20" color="#999" class="input-icon"></uni-icons>
                <input
                  class="input"
                  type="number"
                  placeholder="请输入建筑面积"
                  v-model="form.area"
                  maxlength="10"
                  step="0.01"
                />
                <text class="unit">㎡</text>
              </view>
              <text v-if="errors.area" class="error-text">{{ errors.area }}</text>
            </view>
            
            <!-- 估值 -->
            <view class="form-item half">
              <text class="label required">估值</text>
              <view class="input-wrapper">
                <uni-icons type="money" size="20" color="#999" class="input-icon"></uni-icons>
                <input
                  class="input"
                  type="number"
                  placeholder="请输入房产估值（万元）"
                  v-model="form.value"
                  maxlength="10"
                  step="0.01"
                />
                <text class="unit">万元</text>
              </view>
              <text v-if="errors.value" class="error-text">{{ errors.value }}</text>
            </view>
          </view>
          
          <!-- 取得日期 -->
          <view class="form-item">
            <text class="label required">取得日期</text>
            <picker 
              mode="date" 
              :value="form.acquisitionDate"
              :start="'1900-01-01'"
              :end="today"
              @change="handleAcquisitionDateChange"
            >
              <view class="picker-wrapper">
                <uni-icons type="calendar" size="20" color="#999" class="input-icon"></uni-icons>
                <text class="picker-text">{{ form.acquisitionDate || '请选择日期' }}</text>
              </view>
            </picker>
            <text v-if="errors.acquisitionDate" class="error-text">{{ errors.acquisitionDate }}</text>
          </view>
        </view>
        
        <!-- 详细信息 -->
        <view class="form-section animate-slide-up">
          <view class="section-header">
            <view class="section-icon bg-purple">
              <uni-icons type="document" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">详细信息</text>
              <text class="section-subtitle">Detailed Information</text>
            </view>
          </view>
          
          <!-- 产权证号 -->
          <view class="form-item">
            <text class="label">产权证号</text>
            <view class="input-wrapper">
              <uni-icons type="ticket" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入产权证号"
                v-model="form.propertyCertificate"
                maxlength="50"
              />
            </view>
          </view>
          
          <!-- 使用类型 -->
          <view class="form-item">
            <text class="label">使用类型</text>
            <radio-group :value="form.usageType" @change="handleUsageTypeChange" class="radio-group">
              <label class="radio-item">
                <radio value="自住" color="#667eea" />
                <text class="radio-text">自住</text>
              </label>
              <label class="radio-item">
                <radio value="出租" color="#667eea" />
                <text class="radio-text">出租</text>
              </label>
              <label class="radio-item">
                <radio value="闲置" color="#667eea" />
                <text class="radio-text">闲置</text>
              </label>
              <label class="radio-item">
                <radio value="其他" color="#667eea" />
                <text class="radio-text">其他</text>
              </label>
            </radio-group>
          </view>
          
          <!-- 房间数 -->
          <view class="form-item">
            <text class="label">房间数</text>
            <view class="input-wrapper">
              <uni-icons type="list" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="number"
                placeholder="请输入房间数"
                v-model="form.rooms"
                maxlength="2"
              />
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
import validator from '@/utils/validator'
import { post } from '@/utils/request'

export default {
  data() {
    return {
      // 加载状态
      loading: false,
      // 表单数据
      form: {
        residentName: '',
        idCard: '',
        propertyType: '商品房',
        address: '',
        area: '0',
        value: '0',
        acquisitionDate: '',
        propertyCertificate: '',
        usageType: '自住',
        rooms: '',
        notes: ''
      },
      // 错误信息
      errors: {
        residentName: '',
        idCard: '',
        propertyType: '',
        address: '',
        area: '',
        value: '',
        acquisitionDate: ''
      },
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
    
    // 房产类型变化处理
    handleTypeChange(e) {
      this.form.propertyType = e.detail.value
    },
    
    // 使用类型变化处理
    handleUsageTypeChange(e) {
      this.form.usageType = e.detail.value
    },
    
    // 取得日期选择
    handleAcquisitionDateChange(e) {
      this.form.acquisitionDate = e.detail.value
    },
    
    // 表单验证
    validateForm() {
      let isValid = true
      
      // 重置错误信息
      this.errors = {
        residentName: '',
        idCard: '',
        propertyType: '',
        address: '',
        area: '',
        value: '',
        acquisitionDate: ''
      }
      
      // 验证居民姓名
      if (!validator.required(this.form.residentName)) {
        this.errors.residentName = '请输入居民姓名'
        isValid = false
      }
      
      // 验证身份证号
      if (!validator.required(this.form.idCard)) {
        this.errors.idCard = '请输入居民身份证号'
        isValid = false
      } else if (!validator.idCard(this.form.idCard)) {
        this.errors.idCard = '请输入有效的身份证号'
        isValid = false
      }
      
      // 验证房产类型
      if (!validator.required(this.form.propertyType)) {
        this.errors.propertyType = '请选择房产类型'
        isValid = false
      }
      
      // 验证房产地址
      if (!validator.required(this.form.address)) {
        this.errors.address = '请输入房产地址'
        isValid = false
      }
      
      // 验证建筑面积
      if (!this.form.area || parseFloat(this.form.area) <= 0) {
        this.errors.area = '请输入有效的建筑面积'
        isValid = false
      }
      
      // 验证估值
      if (!this.form.value || parseFloat(this.form.value) < 0) {
        this.errors.value = '请输入有效的房产估值'
        isValid = false
      }
      
      // 验证取得日期
      if (!validator.required(this.form.acquisitionDate)) {
        this.errors.acquisitionDate = '请选择取得日期'
        isValid = false
      } else {
        // 验证日期格式是否为yyyy-MM-dd
        const datePattern = /^\d{4}-\d{2}-\d{2}$/
        if (!datePattern.test(this.form.acquisitionDate)) {
          this.errors.acquisitionDate = '请选择有效的日期格式（yyyy-MM-dd）'
          isValid = false
        }
      }
      
      return isValid
    },
    
    // 保存房产记录
    handleSave() {
      // 表单验证
      if (!this.validateForm()) {
        return
      }
      
      this.loading = true
      
      // 准备提交数据
      const submitData = {
        ...this.form,
        // 将字符串转换为数值类型，确保BigDecimal可以正确解析
        area: this.form.area ? parseFloat(this.form.area).toString() : '0',
        value: this.form.value ? parseFloat(this.form.value).toString() : '0',
        // 确保取得日期格式正确，兼容LocalDate类型
        acquisitionDate: this.form.acquisitionDate ? this.form.acquisitionDate : new Date().toISOString().split('T')[0],
        rooms: this.form.rooms ? parseInt(this.form.rooms) : null
      }
      
      post('/property/add', submitData)
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
          console.error('添加房产记录失败', err)
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

.bg-purple {
  background: linear-gradient(135deg, #a855f7 0%, #7e22ce 100%);
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

.unit {
  font-size: 28rpx;
  color: #666;
  margin-left: 10rpx;
  font-weight: bold;
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