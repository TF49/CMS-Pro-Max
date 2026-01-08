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
          <text class="title-main">添加医疗记录</text>
          <text class="title-sub">填写医疗相关信息</text>
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
              <uni-icons type="person-filled" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">居民信息</text>
              <text class="section-subtitle">Resident Information</text>
            </view>
          </view>
          
          <!-- 选择居民 -->
          <view class="form-item">
            <text class="label required">选择居民</text>
            <view class="input-wrapper" @click="showResidentPicker = true">
              <uni-icons type="person" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="搜索并选择居民"
                v-model="form.residentName"
                placeholder-class="placeholder"
                readonly
              />
              <uni-icons type="arrowdown" size="20" color="#999"></uni-icons>
            </view>
            <text v-if="errors.residentId" class="error-text">{{ errors.residentId }}</text>
          </view>
        </view>
        
        <!-- 就诊信息 -->
        <view class="form-section animate-slide-up" style="animation-delay: 0.1s;">
          <view class="section-header">
            <view class="section-icon bg-green">
              <uni-icons type="medkit" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">就诊信息</text>
              <text class="section-subtitle">Visit Information</text>
            </view>
          </view>
          
          <!-- 就诊类型 -->
          <view class="form-item">
            <text class="label required">就诊类型</text>
            <view class="status-selector">
              <view 
                class="status-option visit" 
                :class="{ 'active': form.medicalType === 'VISIT' }"
                @click="form.medicalType = 'VISIT'"
              >
                <uni-icons type="home" size="24" :color="form.medicalType === 'VISIT' ? '#fff' : '#2196f3'"></uni-icons>
                <text>门诊</text>
              </view>
              <view 
                class="status-option hospitalization" 
                :class="{ 'active': form.medicalType === 'HOSPITALIZATION' }"
                @click="form.medicalType = 'HOSPITALIZATION'"
              >
                <uni-icons type="location" size="24" :color="form.medicalType === 'HOSPITALIZATION' ? '#fff' : '#ff5722'"></uni-icons>
                <text>住院</text>
              </view>
            </view>
          </view>
          
          <!-- 医院名称 -->
          <view class="form-item">
            <text class="label required">医院名称</text>
            <view class="input-wrapper">
              <uni-icons type="building" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入医院名称"
                v-model="form.hospital"
                placeholder-class="placeholder"
                maxlength="100"
              />
            </view>
            <text v-if="errors.hospital" class="error-text">{{ errors.hospital }}</text>
          </view>
          
          <!-- 医生姓名 -->
          <view class="form-item">
            <text class="label">医生姓名</text>
            <view class="input-wrapper">
              <uni-icons type="contact" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入医生姓名"
                v-model="form.doctor"
                placeholder-class="placeholder"
                maxlength="50"
              />
            </view>
          </view>
          
          <!-- 科室 -->
          <view class="form-item">
            <text class="label">科室</text>
            <view class="input-wrapper">
              <uni-icons type="grid" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入科室"
                v-model="form.department"
                placeholder-class="placeholder"
                maxlength="50"
              />
            </view>
          </view>
          
          <view class="form-row">
            <!-- 就诊日期 -->
            <view class="form-item half">
              <text class="label required">就诊日期</text>
              <picker
                mode="date"
                :value="form.lastCheckupDate"
                :start="'1900-01-01'"
                :end="today"
                @change="handleVisitDateChange"
                class="picker"
              >
                <view class="picker-inner">
                  <uni-icons type="calendar" size="20" color="#999"></uni-icons>
                  <text class="picker-text">{{ form.lastCheckupDate || 'YYYY-MM-DD' }}</text>
                  <uni-icons type="arrowdown" size="20" color="#999"></uni-icons>
                </view>
              </picker>
              <text v-if="errors.lastCheckupDate" class="error-text">{{ errors.lastCheckupDate }}</text>
            </view>
            
            <!-- 出院日期 -->
            <view class="form-item half" v-if="form.medicalType === 'HOSPITALIZATION'">
              <text class="label">出院日期</text>
              <picker
                mode="date"
                :value="form.dischargeDate"
                :start="form.lastCheckupDate || '1900-01-01'"
                :end="today"
                @change="handleDischargeDateChange"
                class="picker"
              >
                <view class="picker-inner">
                  <uni-icons type="calendar" size="20" color="#999"></uni-icons>
                  <text class="picker-text">{{ form.dischargeDate || 'YYYY-MM-DD' }}</text>
                  <uni-icons type="arrowdown" size="20" color="#999"></uni-icons>
                </view>
              </picker>
            </view>
          </view>
        </view>
        
        <!-- 诊断信息 -->
        <view class="form-section animate-slide-up" style="animation-delay: 0.2s;">
          <view class="section-header">
            <view class="section-icon bg-purple">
              <uni-icons type="document" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">诊断信息</text>
              <text class="section-subtitle">Diagnosis Information</text>
            </view>
          </view>
          
          <!-- 诊断结果 -->
          <view class="form-item">
            <text class="label required">诊断结果</text>
            <view class="input-wrapper">
              <uni-icons type="chat" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入诊断结果"
                v-model="form.diagnosis"
                placeholder-class="placeholder"
                maxlength="200"
              />
            </view>
            <text v-if="errors.diagnosis" class="error-text">{{ errors.diagnosis }}</text>
          </view>
          
          <!-- 病情描述 -->
          <view class="form-item">
            <text class="label">病情描述</text>
            <view class="textarea-wrapper">
              <uni-icons type="chat" size="20" color="#999" class="textarea-icon"></uni-icons>
              <textarea
                class="textarea"
                placeholder="请输入病情描述（可选）"
                v-model="form.description"
                placeholder-class="placeholder"
                maxlength="500"
                auto-height="true"
                show-word-limit
              ></textarea>
            </view>
          </view>
          
          <!-- 治疗方案 -->
          <view class="form-item">
            <text class="label">治疗方案</text>
            <view class="textarea-wrapper">
              <uni-icons type="compose" size="20" color="#999" class="textarea-icon"></uni-icons>
              <textarea
                class="textarea"
                placeholder="请输入治疗方案（可选）"
                v-model="form.treatmentPlan"
                placeholder-class="placeholder"
                maxlength="500"
                auto-height="true"
                show-word-limit
              ></textarea>
            </view>
          </view>
        </view>
        
        <!-- 费用信息 -->
        <view class="form-section animate-slide-up" style="animation-delay: 0.3s;">
          <view class="section-header">
            <view class="section-icon bg-orange">
              <uni-icons type="wallet" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">费用信息</text>
              <text class="section-subtitle">Cost Information</text>
            </view>
          </view>
          
          <!-- 保险类型 -->
          <view class="form-item">
            <text class="label required">保险类型</text>
            <view class="status-selector">
              <view 
                class="status-option" 
                :class="{ 'active': form.insuranceType === '城镇职工医保' }"
                @click="form.insuranceType = '城镇职工医保'"
              >
                <uni-icons type="medkit" size="24" :color="form.insuranceType === '城镇职工医保' ? '#fff' : '#2196f3'"></uni-icons>
                <text>城镇职工医保</text>
              </view>
              <view 
                class="status-option" 
                :class="{ 'active': form.insuranceType === '城镇居民医保' }"
                @click="form.insuranceType = '城镇居民医保'"
              >
                <uni-icons type="medkit" size="24" :color="form.insuranceType === '城镇居民医保' ? '#fff' : '#4caf50'"></uni-icons>
                <text>城镇居民医保</text>
              </view>
            </view>
          </view>
          
          <view class="form-row">
            <!-- 医疗费用 -->
            <view class="form-item half">
              <text class="label">医疗费用</text>
              <view class="input-wrapper">
                <uni-icons type="money" size="20" color="#999" class="input-icon"></uni-icons>
                <input
                  class="input"
                  type="number"
                  placeholder="请输入医疗费用"
                  v-model="form.medicalExpense"
                  placeholder-class="placeholder"
                  maxlength="10"
                  step="0.01"
                />
                <text class="unit">元</text>
              </view>
            </view>
            
            <!-- 医保报销 -->
            <view class="form-item half">
              <text class="label">医保报销</text>
              <view class="input-wrapper">
                <uni-icons type="gift" size="20" color="#999" class="input-icon"></uni-icons>
                <input
                  class="input"
                  type="number"
                  placeholder="请输入医保报销金额"
                  v-model="form.insuranceReimbursement"
                  placeholder-class="placeholder"
                  maxlength="10"
                  step="0.01"
                />
                <text class="unit">元</text>
              </view>
            </view>
          </view>
          
          <!-- 自费金额 -->
          <view class="form-item">
            <text class="label">自费金额</text>
            <view class="input-wrapper">
              <uni-icons type="creditcard" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="number"
                placeholder="请输入自费金额"
                v-model="form.selfPayAmount"
                placeholder-class="placeholder"
                maxlength="10"
                step="0.01"
              />
              <text class="unit">元</text>
            </view>
          </view>
        </view>
        
        <!-- 备注信息 -->
        <view class="form-section animate-slide-up" style="animation-delay: 0.4s;">
          <view class="section-header">
            <view class="section-icon bg-gray">
              <uni-icons type="info" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">备注信息</text>
              <text class="section-subtitle">Remark Information</text>
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
                placeholder-class="placeholder"
                maxlength="500"
                auto-height="true"
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
    
    <!-- 居民选择器弹窗 -->
    <view class="picker-modal" v-if="showResidentPicker" @click="showResidentPicker = false">
      <view class="picker-content" @click.stop>
        <view class="picker-header">
          <button class="picker-cancel" @click="showResidentPicker = false">取消</button>
          <text class="picker-title">选择居民</text>
          <button class="picker-confirm" @click="handleResidentConfirm">确定</button>
        </view>
        <picker-view
          :value="pickerValue"
          @change="handlePickerChange"
          class="resident-picker"
        >
          <picker-view-column>
            <view class="picker-item" v-for="(resident, index) in residents" :key="index">
              {{ resident.name }} - {{ resident.idCard }}
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </view>
    

  </view>
</template>

<script>
import { post, get } from '@/utils/request'
import validator from '@/utils/validator'

export default {
  data() {
    return {
      // 加载状态
      loading: false,
      // 居民列表
      residents: [],
      // 表单数据
      form: {
        residentId: '',
        residentName: '',
        medicalType: 'VISIT',
        hospital: '',
        doctor: '',
        department: '',
        lastCheckupDate: '',
        dischargeDate: '',
        diagnosis: '',
        description: '',
        treatmentPlan: '',
        insuranceType: '',
        medicalExpense: '0.00',
        insuranceReimbursement: '0.00',
        selfPayAmount: '0.00',
        notes: ''
      },
      // 错误信息
      errors: {
        residentId: '',
        hospital: '',
        lastCheckupDate: '',
        diagnosis: '',
        insuranceType: ''
      },
      // 居民选择器
      showResidentPicker: false,
      pickerValue: [0],
      // 今天日期
      today: '',
      statusBarHeight: 0
    }
  },
  onLoad() {
    this.getStatusBarHeight()
    // 获取今天日期
    this.getTodayDate()
    // 加载居民列表
    this.loadResidents()
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
    
    // 获取今天日期
    getTodayDate() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      this.today = `${year}-${month}-${day}`
    },
    
    // 加载居民列表
    loadResidents() {
      this.loading = true
      // 调用居民列表API（简化版，实际应该分页或搜索）
      get('/resident/list', { page: 1, size: 100 })
        .then(res => {
          if (res.code === 200 && res.data) {
            // 根据后端返回的数据结构调整，有些接口返回data.records，有些返回data.data
            this.residents = res.data.records || res.data.data || []
            if (this.residents.length === 0) {
              uni.showToast({
                title: '获取居民列表为空',
                icon: 'none'
              })
            }
          } else {
            this.residents = []
            uni.showToast({
              title: '获取居民列表为空',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('获取居民列表失败', err)
          this.residents = []
          uni.showToast({
            title: '获取居民列表失败，请稍后重试',
            icon: 'none'
          })
        })
        .finally(() => {
          this.loading = false
        })
    },
    
    // 就诊类型变化处理
    handleTypeChange(e) {
      this.form.medicalType = e.detail.value
    },
    
    // 居民选择器变化
    handlePickerChange(e) {
      this.pickerValue = e.detail.value
    },
    
    // 居民选择确认
    handleResidentConfirm() {
      const index = this.pickerValue[0]
      if (this.residents[index]) {
        this.form.residentId = this.residents[index].id
        this.form.residentName = this.residents[index].name
      }
      this.showResidentPicker = false
    },
    
    // 就诊日期变化
    handleVisitDateChange(e) {
      this.form.lastCheckupDate = e.detail.value
    },
    
    // 出院日期变化
    handleDischargeDateChange(e) {
      this.form.dischargeDate = e.detail.value
    },
    
    // 表单验证
    validateForm() {
      let isValid = true
      
      // 重置错误信息
      this.errors = {
        residentId: '',
        hospital: '',
        lastCheckupDate: '',
        diagnosis: '',
        insuranceType: ''
      }
      
      // 验证居民选择
      if (!validator.required(this.form.residentId)) {
        this.errors.residentId = '请选择居民'
        isValid = false
      }
      
      // 验证医院名称
      if (!validator.required(this.form.hospital)) {
        this.errors.hospital = '请输入医院名称'
        isValid = false
      }
      
      // 验证就诊日期
      if (!validator.required(this.form.lastCheckupDate)) {
        this.errors.lastCheckupDate = '请选择就诊日期'
        isValid = false
      }
      
      // 验证诊断结果
      if (!validator.required(this.form.diagnosis)) {
        this.errors.diagnosis = '请输入诊断结果'
        isValid = false
      }
      
      // 验证保险类型
      if (!validator.required(this.form.insuranceType)) {
        this.errors.insuranceType = '请选择保险类型'
        isValid = false
      }
      
      return isValid
    },
    
    // 保存医疗记录
    handleSave() {
      // 表单验证
      if (!this.validateForm()) {
        return
      }
      
      this.loading = true
      
      // 调用添加医疗记录API
      post('/medical/add', this.form)
        .then(res => {
          if (res.code === 200) {
            uni.showToast({
              title: '添加成功',
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
              title: res.msg || res.message || '添加失败，请稍后重试',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('添加医疗记录失败', err)
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
}
</script>

<style scoped>
.add-container {
  background: #f5f7fa;
  min-height: 100vh;
  position: relative;
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
}

.title-sub {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.9);
  text-shadow: 0 1rpx 4rpx rgba(0, 0, 0, 0.2);
  text-transform: uppercase;
  letter-spacing: 1rpx;
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
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0.06);
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

/* 分组标题 */
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

.bg-green {
  background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
}

.bg-purple {
  background: linear-gradient(135deg, #9c27b0 0%, #7b1fa2 100%);
}

.bg-orange {
  background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
}

.bg-gray {
  background: linear-gradient(135deg, #9e9e9e 0%, #757575 100%);
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

/* 表单项 */
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

/* 标签 */
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

/* 输入框包装器 */
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

.unit {
  font-size: 28rpx;
  color: #666;
  margin-left: 10rpx;
}

/* 占位符样式 */
.placeholder {
  color: #adb5bd;
}

/* 文本域包装器 */
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

/* 状态选择器 */
.status-selector {
  display: flex;
  gap: 16rpx;
}

.status-option {
  flex: 1;
  height: 96rpx;
  border-radius: 16rpx;
  border: 2rpx solid #e9ecef;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  font-size: 26rpx;
  color: #666;
  transition: all 0.3s ease;
  background: #f8f9fa;
}

.status-option.visit {
  border-color: #e3f2fd;
}

.status-option.hospitalization {
  border-color: #ffebee;
}

.status-option.active.visit {
  background: linear-gradient(135deg, #2196f3 0%, #1976d2 100%);
  border-color: #2196f3;
  color: white;
  box-shadow: 0 4rpx 12rpx rgba(33, 150, 243, 0.3);
  transform: scale(1.02);
}

.status-option.active.hospitalization {
  background: linear-gradient(135deg, #ff5722 0%, #e64a19 100%);
  border-color: #ff5722;
  color: white;
  box-shadow: 0 4rpx 12rpx rgba(255, 87, 34, 0.3);
  transform: scale(1.02);
}

.status-option.active {
  background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
  border-color: #4caf50;
  color: white;
  box-shadow: 0 4rpx 12rpx rgba(76, 175, 80, 0.3);
  transform: scale(1.02);
}

/* 提交区域 */
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

/* 错误信息 */
.error-text {
  font-size: 22rpx;
  color: #ff4757;
  margin-top: 8rpx;
  display: block;
}

/* 日期选择器样式 */
.picker {
  background: #f8f9fa;
  border: 2rpx solid #e9ecef;
  border-radius: 16rpx;
  transition: all 0.3s ease;
}

.picker:active {
  background: white;
  border-color: #667eea;
  box-shadow: 0 0 0 6rpx rgba(102, 126, 234, 0.1);
}

/* 日期选择器内容样式 */
.picker .picker-inner {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 24rpx 20rpx;
}

.picker-text {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

/* 居民选择器弹窗 */
.picker-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: flex-end;
  z-index: 1000;
}

.picker-content {
  width: 100%;
  background: white;
  border-radius: 24rpx 24rpx 0 0;
  overflow: hidden;
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.picker-cancel, .picker-confirm {
  background: none;
  border: none;
  font-size: 28rpx;
  padding: 10rpx 20rpx;
  border-radius: 8rpx;
  transition: all 0.3s ease;
}

.picker-cancel {
  color: #999;
}

.picker-confirm {
  color: #667eea;
  font-weight: bold;
}

.picker-cancel:hover, .picker-confirm:hover {
  background: rgba(0, 0, 0, 0.05);
  transform: scale(1.05);
}

.picker-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.resident-picker {
  height: 400rpx;
}

.picker-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 80rpx;
  font-size: 28rpx;
  color: #333;
}

/* 日期选择器 */
.picker-wrapper {
  background: #f8f9fa;
  border: 2rpx solid #e9ecef;
  border-radius: 16rpx;
  transition: all 0.3s ease;
}

.picker-wrapper:active {
  background: white;
  border-color: #667eea;
  box-shadow: 0 0 0 6rpx rgba(102, 126, 234, 0.1);
}

.picker .picker-inner {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 24rpx 20rpx;
}

.picker-text {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}
</style>