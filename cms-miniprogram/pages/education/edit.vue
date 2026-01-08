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
          <text class="title-main">编辑教育信息</text>
          <text class="title-sub">修改教育相关信息</text>
        </view>
        <button class="save-btn" @click="handleSubmit" :disabled="loading">
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
        <!-- 居民基本信息 -->
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
          
          <view class="form-item">
            <text class="label required">居民姓名</text>
            <view class="input-wrapper">
              <uni-icons type="person" size="20" color="#999" class="input-icon"></uni-icons>
              <input 
                v-model="form.residentName" 
                placeholder="请输入居民姓名" 
                class="input"
                maxlength="20"
              />
            </view>
            <text v-if="errors.residentName" class="error-text">{{ errors.residentName }}</text>
          </view>
          
          <view class="form-item">
            <text class="label required">身份证号</text>
            <view class="input-wrapper">
              <uni-icons type="wallet" size="20" color="#999" class="input-icon"></uni-icons>
              <input 
                v-model="form.idCard" 
                placeholder="请输入居民身份证号" 
                class="input"
                maxlength="18"
              />
            </view>
            <text v-if="errors.idCard" class="error-text">{{ errors.idCard }}</text>
          </view>
        </view>
        
        <!-- 教育基本信息 -->
        <view class="form-section animate-slide-up">
          <view class="section-header">
            <view class="section-icon bg-blue">
              <uni-icons type="book" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">教育信息</text>
              <text class="section-subtitle">Education Information</text>
            </view>
          </view>
          
          <view class="form-item">
            <text class="label required">学校名称</text>
            <view class="input-wrapper">
              <uni-icons type="office" size="20" color="#999" class="input-icon"></uni-icons>
              <input 
                v-model="form.schoolName" 
                placeholder="请输入学校名称" 
                class="input"
                maxlength="50"
              />
            </view>
            <text v-if="errors.schoolName" class="error-text">{{ errors.schoolName }}</text>
          </view>
          
          <view class="form-item">
            <text class="label required">教育程度</text>
            <picker 
              :value="educationLevelOptions.findIndex(item => item.value === form.educationLevel)" 
              :range="educationLevelOptions" 
              :range-key="'label'"
              @change="handleEducationLevelChange"
            >
              <view class="picker-wrapper">
                <uni-icons type="trophy" size="20" color="#999" class="input-icon"></uni-icons>
                <text class="picker-text">{{ getEducationLevelLabel(form.educationLevel) }}</text>
              </view>
            </picker>
            <text v-if="errors.educationLevel" class="error-text">{{ errors.educationLevel }}</text>
          </view>
          
          <view class="form-item">
            <text class="label">专业名称</text>
            <view class="input-wrapper">
              <uni-icons type="briefcase" size="20" color="#999" class="input-icon"></uni-icons>
              <input 
                v-model="form.major" 
                placeholder="请输入专业名称" 
                class="input"
                maxlength="50"
              />
            </view>
            <text v-if="errors.major" class="error-text">{{ errors.major }}</text>
          </view>
          
          <view class="form-row">
            <view class="form-item half">
              <text class="label required">入学日期</text>
              <view class="input-wrapper">
                <uni-icons type="calendar" size="20" color="#999" class="input-icon"></uni-icons>
                <input 
                  v-model="form.enrollmentDate" 
                  placeholder="YYYY-MM-DD" 
                  class="input"
                  maxlength="10"
                />
              </view>
              <text v-if="errors.enrollmentDate" class="error-text">{{ errors.enrollmentDate }}</text>
            </view>
            
            <view class="form-item half">
              <text class="label">毕业日期</text>
              <view class="input-wrapper">
                <uni-icons type="calendar" size="20" color="#999" class="input-icon"></uni-icons>
                <input 
                  v-model="form.graduationDate" 
                  placeholder="YYYY-MM-DD，至今可留空" 
                  class="input"
                  maxlength="10"
                />
              </view>
            </view>
          </view>
          
          <view class="form-item">
            <text class="label required">学历状态</text>
            <view class="radio-group-wrapper">
              <radio-group :value="form.status" @change="handleStatusChange" class="radio-group">
                <label class="radio-item">
                  <radio value="STUDYING" color="#667eea" />
                  <text class="radio-text">在读</text>
                </label>
                <label class="radio-item">
                  <radio value="GRADUATED" color="#667eea" />
                  <text class="radio-text">毕业</text>
                </label>
                <label class="radio-item">
                  <radio value="SUSPENDED" color="#667eea" />
                  <text class="radio-text">休学</text>
                </label>
                <label class="radio-item">
                  <radio value="DROP_OUT" color="#667eea" />
                  <text class="radio-text">退学</text>
                </label>
              </radio-group>
            </view>
            <text v-if="errors.status" class="error-text">{{ errors.status }}</text>
          </view>
          
          <view class="form-item">
            <text class="label">备注</text>
            <view class="textarea-wrapper">
              <uni-icons type="chat" size="20" color="#999" class="textarea-icon"></uni-icons>
              <textarea 
                v-model="form.notes" 
                placeholder="请输入备注信息（可选）" 
                class="textarea"
                maxlength="200"
                auto-height="true"
              ></textarea>
            </view>
            <text v-if="errors.notes" class="error-text">{{ errors.notes }}</text>
          </view>
        </view>
        
        <!-- 底部提交按钮 -->
        <view class="submit-section">
          <button class="submit-btn" @click="handleSubmit" :disabled="loading">
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
import { get, put } from '@/utils/request'

export default {
  data() {
    return {
      educationId: null,
      loading: false,
      form: {
        // 居民信息
        residentId: '',
        residentName: '未填写',
        idCard: '未填写',
        
        // 教育信息
        schoolName: '未填写',
        educationLevel: 'PRIMARY',
        major: '未填写',
        
        // 日期字段
        enrollmentDate: '未填写',
        graduationDate: '未填写',
        
        // 状态
        status: 'STUDYING',
        
        // 其他信息
        notes: '未填写'
      },
      errors: {
        residentName: '',
        idCard: '',
        schoolName: '',
        educationLevel: '',
        enrollmentDate: '',
        status: ''
      },
      // 教育程度选项
      educationLevelOptions: [
        { value: 'PRIMARY', label: '小学' },
        { value: 'JUNIOR', label: '初中' },
        { value: 'SENIOR', label: '高中' },
        { value: 'COLLEGE', label: '大专' },
        { value: 'BACHELOR', label: '本科' },
        { value: 'MASTER', label: '硕士' },
        { value: 'DOCTOR', label: '博士' }
      ],
      statusBarHeight: 0
    }
  },
  onLoad(options) {
    this.getStatusBarHeight()
    // 获取传递的教育ID
    if (options.id) {
      this.educationId = options.id
      this.loadEducationDetail()
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
    
    // 加载教育详情
    loadEducationDetail() {
      this.loading = true
      get(`/education/${this.educationId}`)
        .then(res => {
          if (res.code === 200) {
            let data = res.data
            let resident = data.resident || {}
            
            // 如果教育详情中没有居民信息，尝试通过residentId获取
            if (!resident.name && data.residentId) {
              this.getResidentInfo(data.residentId)
            }
            
            // 处理教育程度转换
            let educationLevelValue = 'PRIMARY'
            if (data.educationLevel) {
              if (typeof data.educationLevel === 'string' && ['小学', '初中', '高中', '大专', '本科', '硕士', '博士'].includes(data.educationLevel)) {
                // 中文转换为英文
                const levelMap = {
                  '小学': 'PRIMARY',
                  '初中': 'JUNIOR',
                  '高中': 'SENIOR',
                  '大专': 'COLLEGE',
                  '本科': 'BACHELOR',
                  '硕士': 'MASTER',
                  '博士': 'DOCTOR'
                }
                educationLevelValue = levelMap[data.educationLevel] || 'PRIMARY'
              } else {
                educationLevelValue = data.educationLevel
              }
            }
            
            // 处理状态转换
            let statusValue = 'STUDYING'
            if (data.status) {
              if (typeof data.status === 'string' && ['在读', '毕业', '休学', '退学'].includes(data.status)) {
                // 中文转换为英文
                const statusMap = {
                  '在读': 'STUDYING',
                  '毕业': 'GRADUATED',
                  '休学': 'SUSPENDED',
                  '退学': 'DROP_OUT'
                }
                statusValue = statusMap[data.status] || 'STUDYING'
              } else {
                statusValue = data.status
              }
            }
            
            // 表单数据赋值，处理空值和字段映射
            this.form = {
              // 居民信息
              residentId: data.residentId || '',
              residentName: resident.name || (data.residentName || '未填写'),
              idCard: resident.idCard || (data.idCard || '未填写'),
              
              // 教育信息
              schoolName: data.schoolName || '未填写',
              educationLevel: educationLevelValue,
              major: data.major || '未填写',
              
              // 日期字段处理
              enrollmentDate: data.enrollmentDate ? data.enrollmentDate : '未填写',
              graduationDate: data.graduationDate ? data.graduationDate : '未填写',
              
              // 状态处理
              status: statusValue,
              
              // 其他信息
              notes: data.notes || '未填写'
            }
          } else {
            uni.showToast({
              title: '获取详情失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('获取教育详情失败', err)
          uni.showToast({
            title: '网络错误',
            icon: 'none'
          })
        })
        .finally(() => {
          this.loading = false
        })
    },
    
    // 根据居民ID获取居民详情
    getResidentInfo(residentId) {
      if (!residentId) return
      
      get(`/resident/${residentId}`)
        .then(res => {
          if (res.code === 200) {
            // 更新表单中的居民信息
            this.form.residentName = res.data.name || '未填写'
            this.form.idCard = res.data.idCard || '未填写'
          }
        })
        .catch(err => {
          console.error('获取居民详情失败', err)
        })
    },
    
    // 获取教育程度标签
    getEducationLevelLabel(value) {
      const option = this.educationLevelOptions.find(item => item.value === value)
      return option ? option.label : '请选择'
    },
    
    // 教育程度变化处理
    handleEducationLevelChange(e) {
      const index = e.detail.value
      this.form.educationLevel = this.educationLevelOptions[index].value
    },
    
    // 学历状态变化处理
    handleStatusChange(e) {
      this.form.status = e.detail.value
    },
    
    // 表单验证
    validateForm() {
      let isValid = true
      
      this.errors = {
        residentName: '',
        idCard: '',
        schoolName: '',
        educationLevel: '',
        enrollmentDate: '',
        status: '',
        major: '',
        notes: ''
      }
      
      // 辅助函数：检查值是否为有效内容（非空且非"未填写"）
      const isEmptyValue = (value) => {
        return value === '' || value === null || value === undefined || value === '未填写'
      }
      
      // 居民姓名验证
      if (isEmptyValue(this.form.residentName)) {
        this.errors.residentName = '请输入居民姓名'
        isValid = false
      } else if (this.form.residentName.length < 2 || this.form.residentName.length > 20) {
        this.errors.residentName = '姓名长度应在2-20个字符之间'
        isValid = false
      }
      
      // 身份证号验证
      if (isEmptyValue(this.form.idCard)) {
        this.errors.idCard = '请输入身份证号'
        isValid = false
      } else if (!validator.idCard(this.form.idCard)) {
        this.errors.idCard = '身份证号格式不正确'
        isValid = false
      }
      
      // 学校名称验证
      if (isEmptyValue(this.form.schoolName)) {
        this.errors.schoolName = '请输入学校名称'
        isValid = false
      } else if (this.form.schoolName.length < 2 || this.form.schoolName.length > 50) {
        this.errors.schoolName = '学校名称长度应在2-50个字符之间'
        isValid = false
      }
      
      // 教育程度验证
      if (isEmptyValue(this.form.educationLevel)) {
        this.errors.educationLevel = '请选择教育程度'
        isValid = false
      }
      
      // 入学日期验证
      if (isEmptyValue(this.form.enrollmentDate)) {
        this.errors.enrollmentDate = '请输入入学日期'
        isValid = false
      } else if (this.form.enrollmentDate !== '未填写' && !validator.date(this.form.enrollmentDate)) {
        this.errors.enrollmentDate = '入学日期格式不正确，应为YYYY-MM-DD'
        isValid = false
      }
      
      // 毕业日期验证（如果填写）
      if (this.form.graduationDate && this.form.graduationDate !== '未填写') {
        if (!validator.date(this.form.graduationDate)) {
          this.errors.enrollmentDate = '毕业日期格式不正确，应为YYYY-MM-DD'
          isValid = false
        } else if (this.form.enrollmentDate && this.form.enrollmentDate !== '未填写' && this.form.graduationDate < this.form.enrollmentDate) {
          // 毕业日期不能早于入学日期
          this.errors.enrollmentDate = '毕业日期不能早于入学日期'
          isValid = false
        }
      }
      
      // 专业名称长度验证（如果填写了实际内容，不是"未填写"）
      if (this.form.major && this.form.major !== '未填写') {
        if (this.form.major.length < 2 || this.form.major.length > 50) {
          this.errors.major = '专业名称长度应在2-50个字符之间'
          isValid = false
        }
      }
      
      // 备注长度验证（如果填写了实际内容，不是"未填写"）
      if (this.form.notes && this.form.notes !== '未填写') {
        if (this.form.notes.length > 200) {
          this.errors.notes = '备注信息不能超过200个字符'
          isValid = false
        }
      }
      
      // 学历状态验证
      if (isEmptyValue(this.form.status)) {
        this.errors.status = '请选择学历状态'
        isValid = false
      } else if (!['STUDYING', 'GRADUATED', 'SUSPENDED', 'DROP_OUT'].includes(this.form.status)) {
        // 确保状态值有效
        this.errors.status = '无效的状态值'
        isValid = false
      }
      
      return isValid
    },
    
    // 提交表单
    handleSubmit() {
      if (!this.validateForm()) {
        return
      }
      
      this.loading = true
      
      // 处理提交数据：创建副本避免修改原数据
      let submitData = JSON.parse(JSON.stringify(this.form))
      
      // 处理特殊值：将"未填写"转换为空字符串，以便后端正确处理
      for (const key in submitData) {
        if (submitData[key] === '未填写') {
          submitData[key] = ''
        }
      }
      
      // 确保日期格式正确，并且处理"未填写"转换后的空值
      if (submitData.enrollmentDate && !submitData.enrollmentDate.includes('-')) {
        // 如果日期格式不正确，尝试修复
        try {
          const date = new Date(submitData.enrollmentDate)
          submitData.enrollmentDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
        } catch (e) {
          console.error('入学日期格式错误', e)
        }
      }
      
      if (submitData.graduationDate && !submitData.graduationDate.includes('-')) {
        // 如果日期格式不正确，尝试修复
        try {
          const date = new Date(submitData.graduationDate)
          submitData.graduationDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
        } catch (e) {
          console.error('毕业日期格式错误', e)
        }
      }
      
      // 将PUT请求发送到正确的update路径，并确保数据包含id字段
      put('/education/update', { ...submitData, id: this.educationId })
        .then(res => {
          if (res.code === 200) {
            uni.showToast({
              title: '保存成功',
              icon: 'success'
            })
            // 返回列表页
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
          console.error('保存教育信息失败', err)
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
</style>