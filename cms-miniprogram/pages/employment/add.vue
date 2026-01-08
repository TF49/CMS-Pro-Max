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
          <text class="title-main">添加就业记录</text>
          <text class="title-sub">填写就业相关信息</text>
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
          
          <view class="form-item">
            <text class="label required">选择居民</text>
            <view class="input-wrapper" @click="showResidentPicker = true">
              <uni-icons type="person" size="20" color="#999" class="input-icon"></uni-icons>
              <input 
                class="input" 
                placeholder="搜索并选择居民" 
                v-model="form.residentName"
                readonly
              />
              <uni-icons type="arrowdown" size="20" color="#999"></uni-icons>
            </view>
            <text v-if="errors.residentId" class="error-text">{{ errors.residentId }}</text>
          </view>
        </view>
        
        <!-- 就业信息 -->
        <view class="form-section animate-slide-up" style="animation-delay: 0.1s;">
          <view class="section-header">
            <view class="section-icon bg-green">
              <uni-icons type="briefcase-filled" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">就业信息</text>
              <text class="section-subtitle">Employment Information</text>
            </view>
          </view>
          
          <view class="form-item">
            <text class="label required">就业状态</text>
            <view class="status-selector">
              <view 
                class="status-option employed" 
                :class="{ 'active': form.employmentStatus === 'EMPLOYED' }"
                @click="form.employmentStatus = 'EMPLOYED'"
              >
                <uni-icons type="checkmarkempty" size="24" :color="form.employmentStatus === 'EMPLOYED' ? '#fff' : '#4caf50'"></uni-icons>
                <text>已就业</text>
              </view>
              <view 
                class="status-option unemployed" 
                :class="{ 'active': form.employmentStatus === 'UNEMPLOYED' }"
                @click="form.employmentStatus = 'UNEMPLOYED'"
              >
                <uni-icons type="closeempty" size="24" :color="form.employmentStatus === 'UNEMPLOYED' ? '#fff' : '#ff5722'"></uni-icons>
                <text>未就业</text>
              </view>
            </view>
          </view>
          
          <template v-if="form.employmentStatus === 'EMPLOYED'">
            <view class="form-item">
              <text class="label required">单位名称</text>
              <view class="input-wrapper">
                <uni-icons type="home" size="20" color="#999" class="input-icon"></uni-icons>
                <input 
                  v-model="form.company" 
                  placeholder="请输入单位名称" 
                  class="input"
                  maxlength="100"
                />
              </view>
              <text v-if="errors.company" class="error-text">{{ errors.company }}</text>
            </view>
            
            <view class="form-row">
              <view class="form-item half">
                <text class="label required">职位</text>
                <view class="input-wrapper">
                  <uni-icons type="star" size="20" color="#999" class="input-icon"></uni-icons>
                  <input 
                    v-model="form.position" 
                    placeholder="请输入职位" 
                    class="input"
                    maxlength="50"
                  />
                </view>
                <text v-if="errors.position" class="error-text">{{ errors.position }}</text>
              </view>
              
              <view class="form-item half">
                <text class="label">行业类型</text>
                <view class="input-wrapper">
                  <uni-icons type="gear" size="20" color="#999" class="input-icon"></uni-icons>
                  <input 
                    v-model="form.industry" 
                    placeholder="行业" 
                    class="input"
                    maxlength="50"
                  />
                </view>
              </view>
            </view>
            
            <view class="form-row">
              <view class="form-item half">
                <text class="label required">入职时间</text>
                <picker 
                  mode="date" 
                  :value="form.startDate"
                  :end="today"
                  @change="handleStartDateChange"
                >
                  <view class="picker-wrapper">
                    <uni-icons type="calendar" size="20" color="#999" class="input-icon"></uni-icons>
                    <text class="picker-text">{{ form.startDate || 'YYYY-MM-DD' }}</text>
                  </view>
                </picker>
                <text v-if="errors.startDate" class="error-text">{{ errors.startDate }}</text>
              </view>
              
              <view class="form-item half">
                <text class="label">离职时间</text>
                <picker 
                  mode="date" 
                  :value="form.endDate"
                  :start="form.startDate"
                  :end="today"
                  @change="handleEndDateChange"
                >
                  <view class="picker-wrapper">
                    <uni-icons type="calendar" size="20" color="#999" class="input-icon"></uni-icons>
                    <text class="picker-text">{{ form.endDate || 'YYYY-MM-DD' }}</text>
                  </view>
                </picker>
              </view>
            </view>
            
            <view class="form-item">
              <text class="label">合同类型</text>
              <view class="status-selector">
                <view 
                  class="status-option" 
                  :class="{ 'active': form.contractType === '固定期限' }"
                  @click="form.contractType = '固定期限'"
                >
                  <uni-icons type="checkmarkempty" size="24" :color="form.contractType === '固定期限' ? '#fff' : '#667eea'"></uni-icons>
                  <text>固定期限</text>
                </view>
                <view 
                  class="status-option" 
                  :class="{ 'active': form.contractType === '无固定期限' }"
                  @click="form.contractType = '无固定期限'"
                >
                  <uni-icons type="checkmarkempty" size="24" :color="form.contractType === '无固定期限' ? '#fff' : '#667eea'"></uni-icons>
                  <text>无固定期限</text>
                </view>
                <view 
                  class="status-option" 
                  :class="{ 'active': form.contractType === '其他' }"
                  @click="form.contractType = '其他'"
                >
                  <uni-icons type="checkmarkempty" size="24" :color="form.contractType === '其他' ? '#fff' : '#667eea'"></uni-icons>
                  <text>其他</text>
                </view>
              </view>
            </view>
            

            
            <view class="form-row">
              <view class="form-item half">
                <text class="label">联系方式</text>
                <view class="input-wrapper">
                  <uni-icons type="phone" size="20" color="#999" class="input-icon"></uni-icons>
                  <input 
                    v-model="form.contact" 
                    placeholder="联系方式" 
                    class="input"
                    maxlength="50"
                  />
                </view>
              </view>
              
              <view class="form-item half">
                <text class="label">薪资水平</text>
                <view class="input-wrapper">
                  <uni-icons type="wallet" size="20" color="#999" class="input-icon"></uni-icons>
                  <input 
                    v-model="form.salary" 
                    placeholder="薪资"
                    type="number"
                    class="input"
                    maxlength="10"
                  />
                </view>
              </view>
            </view>
          </template>
        </view>
        
        <!-- 备注信息 -->
        <view class="form-section animate-slide-up" style="animation-delay: 0.2s;">
          <view class="section-header">
            <view class="section-icon bg-purple">
              <uni-icons type="chat-filled" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">备注信息</text>
              <text class="section-subtitle">Remark Information</text>
            </view>
          </view>
          
          <view class="form-item">
            <text class="label">备注</text>
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
import validator from '@/utils/validator'
import { post, get } from '@/utils/request'

export default {
  data() {
    return {
      loading: false,
      residents: [],
      form: {
        residentId: '',
        residentName: '',
        idCard: '',
        employmentStatus: 'EMPLOYED',
        company: '',
        position: '',
        salary: '',
        startDate: '',
        endDate: '',
        contractType: '',
        industry: '',
        workLocation: '',
        contact: '',
        notes: ''
      },
      errors: {
        residentId: '',
        company: '',
        startDate: ''
      },
      showResidentPicker: false,
      pickerValue: [0],
      today: '',
      statusBarHeight: 0
    }
  },
  onLoad() {
    this.getStatusBarHeight()
    this.getTodayDate()
    this.loadResidents()
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
    
    loadResidents() {
      this.loading = true
      get('/resident/list', { page: 1, size: 100 })
        .then(res => {
          if (res.code === 200) {
            // 处理不同的数据结构，兼容res.data.records和res.data.data
            this.residents = res.data.records || res.data.data || []
          }
        })
        .catch(err => {
          console.error('获取居民列表失败', err)
          uni.showToast({
            title: '获取居民列表失败',
            icon: 'none'
          })
        })
        .finally(() => {
          this.loading = false
        })
    },
    
    handlePickerChange(e) {
      this.pickerValue = e.detail.value
    },
    
    handleResidentConfirm() {
      const index = this.pickerValue[0]
      if (this.residents[index]) {
        this.form.residentId = this.residents[index].id
        this.form.residentName = this.residents[index].name
        this.form.idCard = this.residents[index].idCard
        // 直接从居民列表中获取联系方式
        if (this.residents[index].phoneNumber) {
          this.form.contact = this.residents[index].phoneNumber
        }
      }
      this.showResidentPicker = false
    },
    
    handleStartDateChange(e) {
      this.form.startDate = e.detail.value
    },
    
    handleEndDateChange(e) {
      this.form.endDate = e.detail.value
    },
    
    validateForm() {
      let isValid = true
      
      this.errors = {
        residentId: '',
        company: '',
        position: '',
        startDate: ''
      }
      
      if (!validator.required(this.form.residentId)) {
        this.errors.residentId = '请选择居民'
        isValid = false
      }
      
      if (this.form.employmentStatus === 'EMPLOYED') {
        if (!validator.required(this.form.company)) {
          this.errors.company = '请输入单位名称'
          isValid = false
        }
        
        if (!validator.required(this.form.position)) {
          this.errors.position = '请输入职位'
          isValid = false
        }
        
        if (!validator.required(this.form.startDate)) {
          this.errors.startDate = '请选择入职时间'
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
      
      // 准备提交数据，与后端实体类字段匹配
      const submitData = {
        ...this.form,
        // 将枚举值转换为中文就业状态
        employmentStatus: this.form.employmentStatus === 'EMPLOYED' ? '在职' : '失业',
        // 确保薪资字段转换为数字类型
        salary: this.form.salary ? Number(this.form.salary) : null,
        // 移除后端实体类中不存在的字段
        contact: undefined,
        workLocation: undefined
      }
      
      post('/employment/add', submitData)
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
          console.error('添加就业记录失败', err)
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

.bg-green {
  background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
}

.bg-purple {
  background: linear-gradient(135deg, #9c27b0 0%, #7b1fa2 100%);
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

.placeholder {
  color: #adb5bd;
}

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

.status-option.employed {
  border-color: #e8f5e9;
}

.status-option.unemployed {
  border-color: #ffebee;
}

.status-option.active.employed {
  background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
  border-color: #4caf50;
  color: white;
  box-shadow: 0 4rpx 12rpx rgba(76, 175, 80, 0.3);
  transform: scale(1.02);
}

.status-option.active.unemployed {
  background: linear-gradient(135deg, #ff5722 0%, #e64a19 100%);
  border-color: #ff5722;
  color: white;
  box-shadow: 0 4rpx 12rpx rgba(255, 87, 34, 0.3);
  transform: scale(1.02);
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
</style>