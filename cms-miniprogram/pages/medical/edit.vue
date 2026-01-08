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
          <text class="title-main">编辑医疗记录</text>
          <text class="title-sub">修改医疗相关信息</text>
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
          
          <!-- 选择居民 -->
          <view class="form-item">
            <text class="label required">选择居民</text>
            <view class="input-wrapper">
              <uni-icons type="person" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="搜索并选择居民"
                v-model="form.residentName"
                readonly
                @click="showResidentPicker = true"
              />
              <uni-icons type="arrow-down" size="24" color="#999" @click="showResidentPicker = true" style="margin-left: auto;"></uni-icons>
            </view>
            <text v-if="errors.residentId" class="error-text">{{ errors.residentId }}</text>
          </view>
        </view>
        
        <!-- 就诊信息 -->
        <view class="form-section animate-slide-up">
          <view class="section-header">
            <view class="section-icon bg-blue">
              <uni-icons type="medkit" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">就诊信息</text>
              <text class="section-subtitle">Visit Information</text>
            </view>
          </view>
          
          <!-- 医院名称 -->
          <view class="form-item">
            <text class="label required">医院名称</text>
            <view class="input-wrapper">
              <uni-icons type="office" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入医院名称"
                v-model="form.hospitalName"
                maxlength="100"
              />
            </view>
            <text v-if="errors.hospitalName" class="error-text">{{ errors.hospitalName }}</text>
          </view>
          
          <!-- 医生姓名和科室 -->
          <view class="form-row">
            <view class="form-item half">
              <text class="label">医生姓名</text>
              <view class="input-wrapper">
                <uni-icons type="person" size="20" color="#999" class="input-icon"></uni-icons>
                <input
                  class="input"
                  type="text"
                  placeholder="请输入医生姓名"
                  v-model="form.doctorName"
                  maxlength="50"
                />
              </view>
            </view>
            
            <view class="form-item half">
              <text class="label">科室</text>
              <view class="input-wrapper">
                <uni-icons type="list" size="20" color="#999" class="input-icon"></uni-icons>
                <input
                  class="input"
                  type="text"
                  placeholder="请输入科室"
                  v-model="form.department"
                  maxlength="50"
                />
              </view>
            </view>
          </view>
          
          <!-- 就诊日期和出院日期 -->
          <view class="form-row">
            <view class="form-item half">
              <text class="label required">就诊日期</text>
              <picker
                mode="date"
                :value="form.visitDate"
                :start="'1900-01-01'"
                :end="today"
                @change="handleVisitDateChange"
              >
                <view class="picker-wrapper">
                  <uni-icons type="calendar" size="20" color="#999" class="input-icon"></uni-icons>
                  <text class="picker-text">{{ form.visitDate || 'YYYY-MM-DD' }}</text>
                </view>
              </picker>
              <text v-if="errors.visitDate" class="error-text">{{ errors.visitDate }}</text>
            </view>
            
            <view class="form-item half" v-if="form.medicalType === 'HOSPITALIZATION'">
              <text class="label">出院日期</text>
              <picker
                mode="date"
                :value="form.dischargeDate"
                :start="form.visitDate || '1900-01-01'"
                :end="today"
                @change="handleDischargeDateChange"
              >
                <view class="picker-wrapper">
                  <uni-icons type="calendar" size="20" color="#999" class="input-icon"></uni-icons>
                  <text class="picker-text">{{ form.dischargeDate || 'YYYY-MM-DD' }}</text>
                </view>
              </picker>
            </view>
          </view>
        </view>
        
        <!-- 诊断信息 -->
        <view class="form-section animate-slide-up">
          <view class="section-header">
            <view class="section-icon bg-blue">
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
              <uni-icons type="sticker" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="text"
                placeholder="请输入诊断结果"
                v-model="form.diagnosis"
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
                maxlength="500"
                :auto-height="true"
              ></textarea>
            </view>
          </view>
          
          <!-- 治疗方案 -->
          <view class="form-item">
            <text class="label">治疗方案</text>
            <view class="textarea-wrapper">
              <uni-icons type="list" size="20" color="#999" class="textarea-icon"></uni-icons>
              <textarea
                class="textarea"
                placeholder="请输入治疗方案（可选）"
                v-model="form.treatmentPlan"
                maxlength="500"
                :auto-height="true"
              ></textarea>
            </view>
          </view>
        </view>
        
        <!-- 费用信息 -->
        <view class="form-section animate-slide-up">
          <view class="section-header">
            <view class="section-icon bg-blue">
              <uni-icons type="creditcard" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">费用信息</text>
              <text class="section-subtitle">Expense Information</text>
            </view>
          </view>
          
          <!-- 保险类型 -->
          <view class="form-item">
            <text class="label required">保险类型</text>
            <view class="radio-group-wrapper">
              <radio-group :value="form.insuranceType" @change="handleInsuranceTypeChange" class="radio-group">
                <label class="radio-item">
                  <radio value="城镇职工医保" color="#667eea" />
                  <text class="radio-text">城镇职工医保</text>
                </label>
                <label class="radio-item">
                  <radio value="城镇居民医保" color="#667eea" />
                  <text class="radio-text">城镇居民医保</text>
                </label>
              </radio-group>
            </view>
            <text v-if="errors.insuranceType" class="error-text">{{ errors.insuranceType }}</text>
          </view>
          
          <!-- 医疗费用 -->
          <view class="form-item">
            <text class="label">医疗费用</text>
            <view class="input-wrapper">
              <uni-icons type="ticket" size="20" color="#999" class="input-icon"></uni-icons>
              <input
                class="input"
                type="number"
                placeholder="请输入医疗费用"
                v-model="form.medicalExpense"
                maxlength="10"
                step="0.01"
              />
              <text class="unit" style="margin-left: 10rpx; color: #666; font-weight: bold;">元</text>
            </view>
          </view>
          
          <!-- 医保报销和自费金额 -->
          <view class="form-row">
            <view class="form-item half">
              <text class="label">医保报销</text>
              <view class="input-wrapper">
                <uni-icons type="checkmark" size="20" color="#999" class="input-icon"></uni-icons>
                <input
                  class="input"
                  type="number"
                  placeholder="请输入医保报销金额"
                  v-model="form.insuranceReimbursement"
                  maxlength="10"
                  step="0.01"
                />
                <text class="unit" style="margin-left: 10rpx; color: #666; font-weight: bold;">元</text>
              </view>
            </view>
            
            <view class="form-item half">
              <text class="label">自费金额</text>
              <view class="input-wrapper">
                <uni-icons type="cash" size="20" color="#999" class="input-icon"></uni-icons>
                <input
                  class="input"
                  type="number"
                  placeholder="请输入自费金额"
                  v-model="form.selfPayAmount"
                  maxlength="10"
                  step="0.01"
                />
                <text class="unit" style="margin-left: 10rpx; color: #666; font-weight: bold;">元</text>
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
    
    <!-- 居民选择器 -->
    <view class="picker-modal" v-if="showResidentPicker" @click="showResidentPicker = false">
      <view class="picker-content" @click.stop>
        <view class="picker-header">
          <button class="picker-cancel" @click="showResidentPicker = false">取消</button>
          <view class="picker-title">选择居民</view>
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
import { put, get } from '@/utils/request'

export default {
  data() {
    return {
      // 加载状态
      loading: false,
      // 医疗记录ID
      medicalId: '',
      // 居民列表
      residents: [],
      // 表单数据
      form: {
        id: '',
        residentId: '',
        residentName: '',
        medicalType: 'VISIT',
        hospitalName: '',
        doctorName: '',
        department: '',
        visitDate: '',
        dischargeDate: '',
        diagnosis: '',
        description: '',
        treatmentPlan: '',
        insuranceType: '',
        medicalExpense: '0.00',
        insuranceReimbursement: '0.00',
        selfPayAmount: '0.00',
        remark: ''
      },
      // 错误信息
      errors: {
        residentId: '',
        hospitalName: '',
        visitDate: '',
        diagnosis: '',
        insuranceType: ''
      },
      // 居民选择器
      showResidentPicker: false,
      pickerVisible: false,
      pickerValue: [0],
      // 今天日期
      today: '',
      statusBarHeight: 0
    }
  },
  onLoad(options) {
    this.getStatusBarHeight()
    // 获取医疗记录ID
    this.medicalId = options.id || ''
    // 获取今天日期
    this.getTodayDate()
    // 加载居民列表
    this.loadResidents()
    // 加载医疗记录详情
    if (this.medicalId) {
      this.loadMedicalDetail()
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
    
    // 加载医疗记录详情
    loadMedicalDetail() {
      this.loading = true
      get(`/medical/${this.medicalId}`)
        .then(res => {
          if (res.code === 200 && res.data) {
            const data = res.data
            // 填充表单数据，处理空值
            this.form = {
              id: data.id || '',
              residentId: data.residentId || '',
              residentName: data.residentName || '',
              medicalType: 'VISIT',
              hospitalName: data.hospital || '未填写',
              doctorName: data.doctor || '未填写',
              department: data.department || '未填写',
              visitDate: data.lastCheckupDate || '未填写',
              dischargeDate: data.dischargeDate || '未填写',
              diagnosis: data.diagnosis || '未填写',
              description: data.description || '未填写',
              treatmentPlan: data.treatmentPlan || '未填写',
              insuranceType: data.insuranceType || '未填写',
              medicalExpense: data.medicalExpense ? data.medicalExpense.toString() : '0.00',
              insuranceReimbursement: data.insuranceReimbursement ? data.insuranceReimbursement.toString() : '0.00',
              selfPayAmount: data.selfPayAmount ? data.selfPayAmount.toString() : '0.00',
              remark: data.notes || '未填写'
            }
            // 设置居民选择器初始值
            const residentIndex = this.residents.findIndex(r => r.id === data.residentId)
            if (residentIndex !== -1) {
              this.pickerValue = [residentIndex]
            }
          } else {
            uni.showToast({
              title: '获取详情失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('获取医疗记录详情失败', err)
          uni.showToast({
            title: '网络错误，请稍后重试',
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
    
    // 保险类型变化处理
    handleInsuranceTypeChange(e) {
      this.form.insuranceType = e.detail.value
    },
    
    // 就诊日期变化
    handleVisitDateChange(e) {
      this.form.visitDate = e.detail.value
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
        hospitalName: '',
        visitDate: '',
        diagnosis: '',
        insuranceType: ''
      }
      
      // 验证居民选择
      if (!validator.required(this.form.residentId)) {
        this.errors.residentId = '请选择居民'
        isValid = false
      }
      
      // 验证医院名称
      if (!validator.required(this.form.hospitalName)) {
        this.errors.hospitalName = '请输入医院名称'
        isValid = false
      }
      
      // 验证就诊日期
      if (!validator.required(this.form.visitDate)) {
        this.errors.visitDate = '请选择就诊日期'
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
      
      // 处理提交数据：创建副本避免修改原数据
      let submitData = JSON.parse(JSON.stringify(this.form))

      // 必须包含ID字段
      if (!submitData.id) {
        uni.showToast({
          title: '医疗记录ID缺失',
          icon: 'none'
        })
        this.loading = false
        return
      }

      // 转换字段名称，使其与后端一致
      submitData.hospital = submitData.hospitalName
      submitData.doctor = submitData.doctorName
      submitData.lastCheckupDate = submitData.visitDate
      submitData.notes = submitData.remark

      // 删除不需要的字段
      delete submitData.hospitalName
      delete submitData.doctorName
      delete submitData.visitDate
      delete submitData.dischargeDate
      delete submitData.description
      delete submitData.treatmentPlan
      delete submitData.medicalExpense
      delete submitData.insuranceReimbursement
      delete submitData.selfPayAmount
      delete submitData.remark
      delete submitData.medicalType
      
      // 处理特殊值：将空字符串转换为null，以便后端正确处理
      for (const key in submitData) {
        if (submitData[key] === '') {
          submitData[key] = null
        }
      }
      
      // 调用更新医疗记录API
      put(`/medical/update`, submitData)
        .then(res => {
          if (res.code === 200) {
            uni.showToast({
              title: '保存成功',
              icon: 'success'
            })
            // 返回上一页
            setTimeout(() => {
              this.handleBack()
            }, 1500)
          } else {
            uni.showToast({
              title: res.msg || res.message || '保存失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('更新医疗记录失败', err)
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

/* 居民选择器 */
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
  border-radius: 20rpx 20rpx 0 0;
  overflow: hidden;
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
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
  width: 100%;
}

.picker-item {
  line-height: 80rpx;
  text-align: center;
  font-size: 28rpx;
  color: #333;
}
</style>