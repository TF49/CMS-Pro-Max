<template>
  <view class="edit-container">
    <!-- 顶部渐变背景 -->
    <view class="top-gradient"></view>
    
    <!-- 头部导航 -->
    <view class="header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="header-content">
        <button class="back-btn" @click="handleBack">
          <uni-icons type="back" size="24" color="#fff"></uni-icons>
        </button>
        <view class="header-title">
          <text class="title-main">编辑社保信息</text>
          <text class="title-sub">修改社保相关信息</text>
        </view>
        <button class="save-btn" @click="handleSave" :disabled="loading">
          <uni-icons v-if="!loading" type="checkmarkempty" size="24" color="#fff"></uni-icons>
          <uni-icons v-else type="spinner-cycle" size="24" color="#fff" class="rotating"></uni-icons>
        </button>
      </view>
    </view>
    
    <!-- 加载状态 -->
    <view v-if="initialLoading" class="loading-container">
      <uni-icons type="spinner-cycle" size="48" color="#667eea" class="loading-icon"></uni-icons>
      <text class="loading-text">加载中...</text>
    </view>
    
    <!-- 表单内容 -->
    <scroll-view v-else class="form-scroll" scroll-y="true" show-scrollbar="false">
      <view class="form-container">
        <!-- 基本信息 -->
        <view class="form-section animate-slide-up">
          <view class="section-header">
            <view class="section-icon bg-blue">
              <uni-icons type="person" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">基本信息</text>
              <text class="section-subtitle">Basic Information</text>
            </view>
          </view>
          
          <view class="form-item">
            <text class="label required">居民姓名</text>
            <view class="input-wrapper disabled">
              <uni-icons type="person" size="20" color="#999" class="input-icon"></uni-icons>
              <input 
                v-model="form.residentName" 
                placeholder="居民姓名不可修改" 
                class="input"
                disabled
              />
            </view>
            <text class="hint-text">居民信息不可修改</text>
          </view>
          
          <view class="form-item">
            <text class="label required">身份证号</text>
            <view class="input-wrapper disabled">
              <uni-icons type="wallet" size="20" color="#999" class="input-icon"></uni-icons>
              <input 
                v-model="form.idCard" 
                placeholder="身份证号不可修改" 
                class="input"
                disabled
              />
            </view>
          </view>
        </view>
        
        <!-- 保险信息 -->
        <view class="form-section animate-slide-up">
          <view class="section-header">
            <view class="section-icon bg-green">
              <uni-icons type="medal" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">保险信息</text>
              <text class="section-subtitle">Insurance Information</text>
            </view>
          </view>
          
          <view class="form-item">
            <text class="label required">养老保险</text>
            <picker 
              :value="pensionOptions.findIndex(item => item.value === form.pensionInsurance)" 
              :range="pensionOptions" 
              range-key="label"
              @change="handlePensionChange"
            >
              <view class="picker-wrapper">
                <uni-icons type="star" size="20" color="#999" class="input-icon"></uni-icons>
                <text class="picker-text">{{ getPensionLabel() }}</text>
              </view>
            </picker>
          </view>
          
          <view class="form-item">
            <text class="label required">医疗保险</text>
            <picker 
              :value="medicalOptions.findIndex(item => item.value === form.medicalInsurance)" 
              :range="medicalOptions" 
              range-key="label"
              @change="handleMedicalChange"
            >
              <view class="picker-wrapper">
                <uni-icons type="heart" size="20" color="#999" class="input-icon"></uni-icons>
                <text class="picker-text">{{ getMedicalLabel() }}</text>
              </view>
            </picker>
          </view>
          
          <view class="form-row">
            <view class="form-item half">
              <text class="label">失业保险</text>
              <picker 
                :value="unemploymentOptions.findIndex(item => item.value === form.unemploymentInsurance)" 
                :range="unemploymentOptions" 
                range-key="label"
                @change="handleUnemploymentChange"
              >
                <view class="picker-wrapper">
                  <text class="picker-text-small">{{ getUnemploymentLabel() }}</text>
                </view>
              </picker>
            </view>
            
            <view class="form-item half">
              <text class="label">工伤保险</text>
              <picker 
                :value="workInjuryOptions.findIndex(item => item.value === form.workInjuryInsurance)" 
                :range="workInjuryOptions" 
                range-key="label"
                @change="handleWorkInjuryChange"
              >
                <view class="picker-wrapper">
                  <text class="picker-text-small">{{ getWorkInjuryLabel() }}</text>
                </view>
              </picker>
            </view>
          </view>
          
          <view class="form-item">
            <text class="label">生育保险</text>
            <picker 
              :value="maternityOptions.findIndex(item => item.value === form.maternityInsurance)" 
              :range="maternityOptions" 
              range-key="label"
              @change="handleMaternityChange"
            >
              <view class="picker-wrapper">
                <uni-icons type="gift" size="20" color="#999" class="input-icon"></uni-icons>
                <text class="picker-text">{{ getMaternityLabel() }}</text>
              </view>
            </picker>
          </view>
        </view>
        
        <!-- 缴费信息 -->
        <view class="form-section animate-slide-up">
          <view class="section-header">
            <view class="section-icon bg-orange">
              <uni-icons type="wallet" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">缴费信息</text>
              <text class="section-subtitle">Payment Information</text>
            </view>
          </view>
          
          <view class="form-item">
            <text class="label required">参保状态</text>
            <picker 
              :value="statusOptions.findIndex(item => item.value === form.insuranceStatus)" 
              :range="statusOptions" 
              range-key="label"
              @change="handleStatusChange"
            >
              <view class="picker-wrapper">
                <uni-icons type="flag" size="20" color="#999" class="input-icon"></uni-icons>
                <text class="picker-text">{{ getStatusLabel() }}</text>
              </view>
            </picker>
          </view>
          
          <view class="form-item">
            <text class="label required">缴费金额</text>
            <view class="input-wrapper">
              <uni-icons type="wallet" size="20" color="#999" class="input-icon"></uni-icons>
              <input 
                v-model.number="form.paymentAmount" 
                placeholder="请输入缴费金额" 
                class="input"
                type="digit"
              />
              <text class="input-suffix">元/月</text>
            </view>
          </view>
          
          <view class="form-row">
            <view class="form-item half">
              <text class="label required">参保日期</text>
              <picker 
                mode="date" 
                :value="form.startDate"
                :end="today"
                @change="handleStartDateChange"
              >
                <view class="picker-wrapper">
                  <uni-icons type="calendar" size="20" color="#999" class="input-icon"></uni-icons>
                  <text class="picker-text-small">{{ form.startDate || 'YYYY-MM-DD' }}</text>
                </view>
              </picker>
            </view>
            
            <view class="form-item half">
              <text class="label">停保日期</text>
              <picker 
                mode="date" 
                :value="form.endDate"
                :start="form.startDate"
                @change="handleEndDateChange"
              >
                <view class="picker-wrapper">
                  <uni-icons type="calendar" size="20" color="#999" class="input-icon"></uni-icons>
                  <text class="picker-text-small">{{ form.endDate || '可选' }}</text>
                </view>
              </picker>
            </view>
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
            <text>{{ loading ? '保存中...' : '保存修改' }}</text>
          </button>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { get, put } from '@/utils/request'

export default {
  data() {
    return {
      socialSecurityId: null,
      initialLoading: false,
      loading: false,
      form: {
        residentName: '',
        idCard: '',
        pensionInsurance: '',
        medicalInsurance: '',
        unemploymentInsurance: '',
        workInjuryInsurance: '',
        maternityInsurance: '',
        insuranceStatus: '',
        paymentAmount: null,
        startDate: '',
        endDate: '',
        notes: ''
      },
      pensionOptions: [
        { value: '城镇职工养老保险', label: '城镇职工养老保险' },
        { value: '城乡居民养老保险', label: '城乡居民养老保险' }
      ],
      medicalOptions: [
        { value: '城镇职工医疗保险', label: '城镇职工医疗保险' },
        { value: '城乡居民医疗保险', label: '城乡居民医疗保险' }
      ],
      unemploymentOptions: [
        { value: '失业保险', label: '失业保险' },
        { value: '无失业保险', label: '无失业保险' }
      ],
      workInjuryOptions: [
        { value: '工伤保险', label: '工伤保险' },
        { value: '无工伤保险', label: '无工伤保险' }
      ],
      maternityOptions: [
        { value: '生育保险', label: '生育保险' },
        { value: '无生育保险', label: '无生育保险' }
      ],
      statusOptions: [
        { value: '正常参保', label: '正常参保' },
        { value: '暂停参保', label: '暂停参保' },
        { value: '停止参保', label: '停止参保' }
      ],
      today: '',
      statusBarHeight: 0
    }
  },
  onLoad(options) {
    this.getStatusBarHeight()
    this.getTodayDate()
    if (options.id) {
      this.socialSecurityId = options.id
      this.loadDetail()
    }
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
    
    loadDetail() {
      this.initialLoading = true
      get(`/social-security/${this.socialSecurityId}`)
        .then(res => {
          if (res.code === 200 && res.data) {
            this.form = {
              residentName: res.data.residentName || '',
              idCard: res.data.idCard || '',
              pensionInsurance: res.data.pensionInsurance || '',
              medicalInsurance: res.data.medicalInsurance || '',
              unemploymentInsurance: res.data.unemploymentInsurance || '',
              workInjuryInsurance: res.data.workInjuryInsurance || '',
              maternityInsurance: res.data.maternityInsurance || '',
              insuranceStatus: res.data.insuranceStatus || '',
              paymentAmount: res.data.paymentAmount || null,
              startDate: res.data.startDate || '',
              endDate: res.data.endDate || '',
              notes: res.data.notes || ''
            }
          } else {
            uni.showToast({
              title: '获取详情失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('获取社保详情失败', err)
          uni.showToast({
            title: '网络错误',
            icon: 'none'
          })
        })
        .finally(() => {
          this.initialLoading = false
        })
    },
    
    handlePensionChange(e) {
      this.form.pensionInsurance = this.pensionOptions[e.detail.value].value
    },
    
    handleMedicalChange(e) {
      this.form.medicalInsurance = this.medicalOptions[e.detail.value].value
    },
    
    handleUnemploymentChange(e) {
      this.form.unemploymentInsurance = this.unemploymentOptions[e.detail.value].value
    },
    
    handleWorkInjuryChange(e) {
      this.form.workInjuryInsurance = this.workInjuryOptions[e.detail.value].value
    },
    
    handleMaternityChange(e) {
      this.form.maternityInsurance = this.maternityOptions[e.detail.value].value
    },
    
    handleStatusChange(e) {
      this.form.insuranceStatus = this.statusOptions[e.detail.value].value
    },
    
    handleStartDateChange(e) {
      this.form.startDate = e.detail.value
    },
    
    handleEndDateChange(e) {
      this.form.endDate = e.detail.value
    },
    
    getPensionLabel() {
      return this.form.pensionInsurance || '请选择养老保险'
    },
    
    getMedicalLabel() {
      return this.form.medicalInsurance || '请选择医疗保险'
    },
    
    getUnemploymentLabel() {
      return this.form.unemploymentInsurance || '请选择'
    },
    
    getWorkInjuryLabel() {
      return this.form.workInjuryInsurance || '请选择'
    },
    
    getMaternityLabel() {
      return this.form.maternityInsurance || '请选择生育保险'
    },
    
    getStatusLabel() {
      return this.form.insuranceStatus || '请选择参保状态'
    },
    
    validateForm() {
      if (!this.form.pensionInsurance) {
        uni.showToast({ title: '请选择养老保险', icon: 'none' })
        return false
      }
      
      if (!this.form.medicalInsurance) {
        uni.showToast({ title: '请选择医疗保险', icon: 'none' })
        return false
      }
      
      if (!this.form.insuranceStatus) {
        uni.showToast({ title: '请选择参保状态', icon: 'none' })
        return false
      }
      
      if (!this.form.paymentAmount || this.form.paymentAmount <= 0) {
        uni.showToast({ title: '请输入有效的缴费金额', icon: 'none' })
        return false
      }
      
      if (!this.form.startDate) {
        uni.showToast({ title: '请选择参保日期', icon: 'none' })
        return false
      }
      
      return true
    },
    
    handleSave() {
      if (!this.validateForm()) {
        return
      }
      
      this.loading = true
      
      const submitData = {
        ...this.form,
        id: this.socialSecurityId
      }
      
      put('/social-security/update', submitData)
        .then(res => {
          if (res.code === 200) {
            uni.showToast({
              title: '修改成功',
              icon: 'success'
            })
            setTimeout(() => {
              this.handleBack()
            }, 1500)
          } else {
            uni.showToast({
              title: res.msg || res.message || '修改失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('修改社保信息失败', err)
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
/* 全局样式 */
.edit-container {
  background: #f5f7fa;
  min-height: 100vh;
  position: relative;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 顶部渐变背景 */
.top-gradient {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 400rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  z-index: 0;
}

/* 头部导航 */
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
  display: block;
  margin-bottom: 4rpx;
}

.title-sub {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.9);
  text-shadow: 0 1rpx 4rpx rgba(0, 0, 0, 0.2);
  font-weight: normal;
  display: block;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60vh;
  gap: 20rpx;
}

.loading-icon {
  animation: spin 1s linear infinite;
}

.loading-text {
  font-size: 28rpx;
  color: #666;
}

/* 旋转动画 */
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

/* 表单滚动区域 */
.form-scroll {
  height: calc(100vh - 120rpx);
  position: relative;
  z-index: 1;
}

/* 表单容器 */
.form-container {
  padding: 20rpx;
  padding-bottom: 40rpx;
}

/* 表单分组 */
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

/* 表单分组头部 */
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
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

.bg-green {
  background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
}

.bg-orange {
  background: linear-gradient(135deg, #ed8936 0%, #dd6b20 100%);
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
  margin-bottom: 4rpx;
  display: block;
}

.section-subtitle {
  font-size: 22rpx;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 1rpx;
  display: block;
}

/* 表单项 */
.form-item {
  margin-bottom: 30rpx;
  position: relative;
}

/* 表单行布局 */
.form-row {
  display: flex;
  gap: 20rpx;
  margin-bottom: 30rpx;
}

.form-item.half {
  flex: 1;
  margin-bottom: 0;
}

/* 标签 */
.label {
  display: block;
  font-size: 26rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 15rpx;
}

.required::after {
  content: '*';
  color: #ff4757;
  margin-left: 4rpx;
}

/* 输入框包装器 */
.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: #f7fafc;
  border: 2rpx solid #e2e8f0;
  border-radius: 12rpx;
  overflow: hidden;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.input-wrapper.disabled {
  background: #f0f0f0;
  opacity: 0.7;
}

.input-wrapper:focus-within {
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 4rpx rgba(102, 126, 234, 0.1);
}

/* 输入框图标 */
.input-icon {
  padding: 0 20rpx;
  color: #999;
}

/* 输入框 */
.input {
  flex: 1;
  border: none;
  outline: none;
  padding: 20rpx 20rpx 20rpx 0;
  font-size: 26rpx;
  background: transparent;
  color: #2d3748;
}

/* 输入框后缀 */
.input-suffix {
  padding-right: 20rpx;
  font-size: 24rpx;
  color: #999;
}

/* 文本域包装器 */
.textarea-wrapper {
  position: relative;
  display: flex;
  align-items: flex-start;
  background: #f7fafc;
  border: 2rpx solid #e2e8f0;
  border-radius: 12rpx;
  overflow: hidden;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.textarea-wrapper:focus-within {
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 4rpx rgba(102, 126, 234, 0.1);
}

/* 文本域图标 */
.textarea-icon {
  padding: 20rpx;
  color: #999;
  align-self: flex-start;
}

/* 文本域 */
.textarea {
  flex: 1;
  border: none;
  outline: none;
  padding: 20rpx 20rpx 20rpx 0;
  font-size: 26rpx;
  background: transparent;
  color: #2d3748;
  min-height: 120rpx;
  resize: none;
}

/* 选择器 */
.picker-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: #f7fafc;
  border: 2rpx solid #e2e8f0;
  border-radius: 12rpx;
  padding: 20rpx;
  cursor: pointer;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 26rpx;
  color: #2d3748;
}

.picker-wrapper:active {
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 4rpx rgba(102, 126, 234, 0.1);
}

.picker-text {
  flex: 1;
}

.picker-text-small {
  flex: 1;
}

/* 提示文本 */
.hint-text {
  display: block;
  font-size: 22rpx;
  color: #999;
  margin-top: 10rpx;
  padding-left: 20rpx;
}

/* 底部提交按钮 */
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

.submit-btn:disabled {
  opacity: 0.7;
}

.submit-btn:active:not(:disabled) {
  transform: scale(0.98);
  box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
}

/* 输入框占位符样式 */
.input::-webkit-input-placeholder,
.textarea::-webkit-input-placeholder {
  color: #a0aec0;
}

.input::-moz-placeholder,
.textarea::-moz-placeholder {
  color: #a0aec0;
}

.input:-ms-input-placeholder,
.textarea:-ms-input-placeholder {
  color: #a0aec0;
}

/* 动画延迟 */
.animate-slide-up:nth-child(1) {
  animation-delay: 0.1s;
}

.animate-slide-up:nth-child(2) {
  animation-delay: 0.2s;
}

.animate-slide-up:nth-child(3) {
  animation-delay: 0.3s;
}

.animate-slide-up:nth-child(4) {
  animation-delay: 0.4s;
}
</style>
