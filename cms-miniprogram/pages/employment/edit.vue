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
          <text class="title-main">编辑就业记录</text>
          <text class="title-sub">修改就业相关信息</text>
        </view>
        <button class="save-btn" @click="handleSave" :disabled="loading">
          <uni-icons v-if="!loading" type="checkmarkempty" size="24" color="#fff"></uni-icons>
          <uni-icons v-else type="spinner-cycle" size="24" color="#fff" class="rotating"></uni-icons>
        </button>
      </view>
    </view>
    
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <uni-icons type="spinner-cycle" size="48" color="#667eea" class="loading-icon"></uni-icons>
      <text class="loading-text">加载中...</text>
    </view>
    
    <!-- 表单内容 -->
    <scroll-view v-else class="form-scroll" scroll-y="true" show-scrollbar="false">
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
            <view class="input-wrapper disabled">
              <uni-icons type="person" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                v-model="form.residentName"
                readonly
                placeholder="居民姓名"
              />
            </view>
          </view>
        </view>
        
        <!-- 就业信息 -->
        <view class="form-section animate-slide-up">
          <view class="section-header">
            <view class="section-icon bg-blue">
              <uni-icons type="briefcase" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">就业信息</text>
              <text class="section-subtitle">Employment Information</text>
            </view>
          </view>
          
          <!-- 就业状态 -->
          <view class="form-item">
            <text class="label required">就业状态</text>
            <view class="radio-group-wrapper">
              <radio-group :value="form.employmentStatus" @change="handleStatusChange" class="radio-group">
                <label class="radio-item">
                  <radio value="EMPLOYED" color="#667eea" />
                  <text class="radio-text">已就业</text>
                </label>
                <label class="radio-item">
                  <radio value="UNEMPLOYED" color="#667eea" />
                  <text class="radio-text">未就业</text>
                </label>
              </radio-group>
            </view>
          </view>
          
          <!-- 单位名称 -->
          <view class="form-item" v-if="form.employmentStatus === 'EMPLOYED'">
            <text class="label required">单位名称</text>
            <view class="input-wrapper">
              <uni-icons type="office" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入单位名称"
                v-model="form.company"
                maxlength="100"
              />
            </view>
            <text v-if="errors.company" class="error-text">{{ errors.company }}</text>
          </view>
          
          <!-- 职位 -->
          <view class="form-item" v-if="form.employmentStatus === 'EMPLOYED'">
            <text class="label">职位</text>
            <view class="input-wrapper">
              <uni-icons type="trophy" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入职位"
                v-model="form.position"
                maxlength="50"
              />
            </view>
          </view>
          
          <!-- 入职时间和离职时间 -->
          <view class="form-row" v-if="form.employmentStatus === 'EMPLOYED'">
            <view class="form-item half">
              <text class="label required">入职时间</text>
              <picker
                mode="date"
                :value="form.startDate"
                :start="'1900-01-01'"
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
                :start="form.startDate || '1900-01-01'"
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
          
          <!-- 行业类型 -->
          <view class="form-item" v-if="form.employmentStatus === 'EMPLOYED'">
            <text class="label">行业类型</text>
            <view class="input-wrapper">
              <uni-icons type="grid" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入行业类型"
                v-model="form.industry"
                maxlength="50"
              />
            </view>
          </view>
          
          <!-- 合同类型 -->
          <view class="form-item" v-if="form.employmentStatus === 'EMPLOYED'">
            <text class="label">合同类型</text>
            <view class="radio-group-wrapper">
              <radio-group :value="form.contractType" @change="handleContractTypeChange" class="radio-group">
                <label class="radio-item">
                  <radio value="固定期限" color="#667eea" />
                  <text class="radio-text">固定期限</text>
                </label>
                <label class="radio-item">
                  <radio value="无固定期限" color="#667eea" />
                  <text class="radio-text">无固定期限</text>
                </label>
                <label class="radio-item">
                  <radio value="其他" color="#667eea" />
                  <text class="radio-text">其他</text>
                </label>
              </radio-group>
            </view>
          </view>
          

          
          <!-- 联系方式 -->
          <view class="form-row" v-if="form.employmentStatus === 'EMPLOYED'">
            <view class="form-item half">
              <text class="label">联系方式</text>
              <view class="input-wrapper">
                <uni-icons type="phone" size="20" color="#999" class="input-icon"></uni-icons>
                <input
                  class="input"
                  type="text"
                  placeholder="请输入联系方式"
                  v-model="form.contact"
                  maxlength="50"
                />
              </view>
            </view>
            
            <view class="form-item half">
              <text class="label">薪资水平</text>
              <view class="input-wrapper">
                <uni-icons type="creditcard" size="20" color="#999" class="input-icon"></uni-icons>
                <input
                  class="input"
                  type="number"
                  placeholder="请输入薪资水平"
                  v-model="form.salary"
                  maxlength="10"
                />
              </view>
            </view>
          </view>
        </view>
        
        <!-- 备注信息 -->
        <view class="form-section animate-slide-up">
          <view class="section-header">
            <view class="section-icon bg-blue">
              <uni-icons type="chat" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">备注信息</text>
              <text class="section-subtitle">Remarks</text>
            </view>
          </view>
          
          <view class="form-item">
            <text class="label">备注</text>
            <view class="textarea-wrapper">
              <uni-icons type="chat" size="20" color="#999" class="textarea-icon"></uni-icons>
              <textarea
                class="textarea"
                placeholder="请输入备注信息（可选）"
                v-model="form.remark"
                maxlength="500"
                :auto-height="true"
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
import validator from '@/utils/validator'
import { put, get } from '@/utils/request'

export default {
  data() {
    return {
      // 加载状态
      loading: false,
      // 就业记录ID
      employmentId: '',
      // 表单数据
      form: {
        id: '',
        residentId: '',
        residentName: '',
        idCard: '',
        employmentStatus: 'EMPLOYED',
        company: '',
        position: '',
        startDate: '',
        endDate: '',
        industry: '',
        contractType: '',
        workLocation: '',
        contact: '',
        salary: '',
        notes: '',
        remark: ''
      },
      // 错误信息
      errors: {
        company: '',
        startDate: ''
      },
      // 今天日期
      today: '',
      statusBarHeight: 0
    }
  },
  onLoad(options) {
    this.getStatusBarHeight()
    if (options && options.id) {
      this.employmentId = options.id
      // 获取今天日期
      this.getTodayDate()
      // 加载就业记录详情
      this.loadEmploymentDetail()
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
    
    // 加载就业记录详情
    loadEmploymentDetail() {
      this.loading = true
      // 调用就业记录详情API
      get(`/employment/${this.employmentId}`)
        .then(res => {
          if (res.code === 200 && res.data) {
            const data = res.data
            // 将后端返回的就业状态转换为前端枚举值
            const employmentStatus = data.employmentStatus === '在职' ? 'EMPLOYED' : 'UNEMPLOYED'
            
            // 填充表单数据，未接收到的数据显示为"未填写"
            this.form = {
              id: data.id || '',
              residentId: data.residentId || '',
              residentName: data.residentName || '未填写',
              idCard: data.idCard || '未填写',
              employmentStatus: employmentStatus,
              company: data.company || '未填写',
              position: data.position || '未填写',
              startDate: data.startDate || '',
              endDate: data.endDate || '',
              industry: data.industry || '未填写',
              contractType: data.contractType || '',
              workLocation: data.workLocation || '未填写',
              contact: data.contact || '未填写',
              salary: data.salary || '',
              notes: data.notes || '未填写',
              // 确保备注字段正确填充，未接收到的数据显示为"未填写"
              remark: data.remark || data.notes || '未填写'
            }
            
            // 根据residentId获取居民信息，填充联系方式
            if (this.form.residentId) {
              this.getResidentInfo(this.form.residentId)
            }
          }
        })
        .catch(err => {
          console.error('获取就业记录详情失败', err)
          uni.showToast({
            title: '获取详情失败',
            icon: 'none'
          })
        })
        .finally(() => {
          this.loading = false
        })
    },
    
    // 就业状态变化处理
    handleStatusChange(e) {
      this.form.employmentStatus = e.detail.value
    },
    
    // 合同类型变化处理
    handleContractTypeChange(e) {
      this.form.contractType = e.detail.value
    },
    
    // 入职时间变化
    handleStartDateChange(e) {
      this.form.startDate = e.detail.value
    },
    
    // 离职时间变化
    handleEndDateChange(e) {
      this.form.endDate = e.detail.value
    },
    
    // 根据居民ID获取居民信息，填充联系方式
    getResidentInfo(residentId) {
      get(`/resident/${residentId}`)
        .then(res => {
          if (res.code === 200 && res.data) {
            const residentData = res.data
            // 从居民信息中获取联系方式并填充
            if (residentData.phoneNumber) {
              this.form.contact = residentData.phoneNumber
            }
          }
        })
        .catch(err => {
          console.error('获取居民信息失败', err)
          // 不显示错误提示，避免影响用户体验
        })
    },
    
    // 表单验证
    validateForm() {
      let isValid = true
      
      // 重置错误信息
      this.errors = {
        company: '',
        startDate: ''
      }
      
      // 如果是已就业，验证相关字段
      if (this.form.employmentStatus === 'EMPLOYED') {
        if (!validator.required(this.form.company)) {
          this.errors.company = '请输入单位名称'
          isValid = false
        }
        
        if (!validator.required(this.form.startDate)) {
          this.errors.startDate = '请选择入职时间'
          isValid = false
        }
      }
      
      return isValid
    },
    
    // 保存就业记录
    handleSave() {
      // 表单验证
      if (!this.validateForm()) {
        return
      }
      
      this.loading = true
      
      // 准备提交数据，只包含后端实体类中存在的字段
      const submitData = {
        // 基本信息
        id: this.employmentId,
        residentId: this.form.residentId,
        residentName: this.form.residentName,
        idCard: this.form.idCard,
        // 就业信息
        employmentStatus: this.form.employmentStatus === 'EMPLOYED' ? '在职' : '失业',
        company: this.form.company,
        position: this.form.position,
        salary: this.form.salary ? Number(this.form.salary) : null,
        industry: this.form.industry,
        startDate: this.form.startDate,
        contractType: this.form.contractType,
        // 备注信息，使用notes字段与后端匹配
        notes: this.form.remark || this.form.notes
      }
      
      // 调用更新就业记录API
      put('/employment/update', submitData)
        .then(res => {
          if (res.code === 200) {
            uni.showToast({
              title: '更新成功',
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
              title: res.msg || res.message || '更新失败，请稍后重试',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('更新就业记录失败', err)
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
/* 复用户籍管理编辑页面样式 */
.edit-container {
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

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.loading-icon {
  animation: rotate 1s linear infinite;
  margin-bottom: 20rpx;
}

.loading-text {
  font-size: 28rpx;
  color: #666;
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

.input-wrapper.disabled {
  background: #f0f0f0;
  opacity: 0.7;
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

.hint-text {
  font-size: 22rpx;
  color: #999;
  margin-top: 8rpx;
  display: block;
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

/* 单选框样式 */
.radio-group-wrapper {
  background: #f8f9fa;
  border: 2rpx solid #e9ecef;
  border-radius: 16rpx;
  padding: 16rpx;
  transition: all 0.3s ease;
}

.radio-group {
  display: flex;
  gap: 40rpx;
}

.radio-item {
  display: flex;
  align-items: center;
  gap: 12rpx;
  font-size: 28rpx;
  color: #333;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 12rpx 20rpx;
  border-radius: 12rpx;
}

.radio-item:active {
  background: rgba(102, 126, 234, 0.1);
}

.radio-text {
  font-size: 28rpx;
  color: #333;
}
</style>