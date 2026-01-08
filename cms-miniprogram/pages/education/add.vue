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
          <text class="title-main">新增教育信息</text>
          <text class="title-sub">填写教育相关信息</text>
        </view>
        <button class="save-btn" @click="handleSubmit" :disabled="loading">
          <uni-icons v-if="!loading" type="checkmarkempty" size="24" color="#fff"></uni-icons>
          <uni-icons v-else type="spinner-cycle" size="24" color="#fff" class="rotating"></uni-icons>
        </button>
      </view>
    </view>
    
    <!-- 表单内容 -->
    <scroll-view class="form-scroll" scroll-y="true" show-scrollbar="false">
      <view class="form-container">
        <form @submit="handleSubmit">
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
            <text class="label required">居民姓名</text>
            <view class="input-wrapper">
              <uni-icons type="person" size="20" color="#999" class="input-icon"></uni-icons>
              <input 
                v-model="form.residentName" 
                placeholder="请输入居民姓名" 
                class="input" 
                placeholder-class="placeholder"
                maxlength="20"
              />
            </view>
          </view>
          
          <view class="form-item">
            <text class="label required">身份证号</text>
            <view class="input-wrapper">
              <uni-icons type="card" size="20" color="#999" class="input-icon"></uni-icons>
              <input 
                v-model="form.idCard" 
                placeholder="请输入居民身份证号" 
                class="input" 
                placeholder-class="placeholder"
                maxlength="18"
              />
            </view>
          </view>
        </view>
        
        <!-- 教育信息 -->
        <view class="form-section animate-slide-up" style="animation-delay: 0.1s;"> 
            <view class="section-header">
              <view class="section-icon bg-green">
                <uni-icons type="book-filled" size="20" color="#fff"></uni-icons>
              </view>
              <view class="section-info">
                <text class="section-title">教育信息</text>
                <text class="section-subtitle">Education Information</text>
              </view>
            </view>
            
            <view class="form-item">
              <text class="label required">学校名称</text>
              <view class="input-wrapper">
                <uni-icons type="home" size="20" color="#999" class="input-icon"></uni-icons>
                <input 
                  v-model="form.schoolName" 
                  placeholder="请输入学校名称" 
                  class="input" 
                  placeholder-class="placeholder"
                  maxlength="50"
                />
              </view>
            </view>
            
            <view class="form-item">
              <text class="label required">教育程度</text>
              <picker 
                :value="educationLevelIndex" 
                :range="educationLevelOptions" 
                :range-key="'label'"
                class="picker"
                @change="handleEducationLevelChange"
              >
                <view class="picker-content">
                  <uni-icons type="medal" size="20" color="#999"></uni-icons>
                  <text class="picker-text">{{ getEducationLevelLabel(form.educationLevel) }}</text>
                  <uni-icons type="arrowdown" size="20" color="#999"></uni-icons>
                </view>
              </picker>
            </view>
            
            <view class="form-item">
              <text class="label">专业名称</text>
              <view class="input-wrapper">
                <uni-icons type="star" size="20" color="#999" class="input-icon"></uni-icons>
                <input 
                  v-model="form.major" 
                  placeholder="请输入专业名称" 
                  class="input" 
                  placeholder-class="placeholder"
                  maxlength="50"
                />
              </view>
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
                    placeholder-class="placeholder"
                    maxlength="10"
                  />
                </view>
              </view>
              
              <view class="form-item half">
                <text class="label">毕业日期</text>
                <view class="input-wrapper">
                  <uni-icons type="calendar" size="20" color="#999" class="input-icon"></uni-icons>
                  <input 
                    v-model="form.graduationDate" 
                    placeholder="YYYY-MM-DD" 
                    class="input" 
                    placeholder-class="placeholder"
                    maxlength="10"
                  />
                </view>
              </view>
            </view>
            
            <view class="form-item">
              <text class="label required">学历状态</text>
              <view class="status-selector">
                <view 
                  class="status-option studying" 
                  :class="{ 'active': form.status === 'STUDYING' }"
                  @click="form.status = 'STUDYING'"
                >
                  <uni-icons type="compose" size="24" :color="form.status === 'STUDYING' ? '#fff' : '#4caf50'"></uni-icons>
                  <text>在读</text>
                </view>
                <view 
                  class="status-option graduated" 
                  :class="{ 'active': form.status === 'GRADUATED' }"
                  @click="form.status = 'GRADUATED'"
                >
                  <uni-icons type="checkmarkempty" size="24" :color="form.status === 'GRADUATED' ? '#fff' : '#2196f3'"></uni-icons>
                  <text>毕业</text>
                </view>
                <view 
                  class="status-option suspended" 
                  :class="{ 'active': form.status === 'SUSPENDED' }"
                  @click="form.status = 'SUSPENDED'"
                >
                  <uni-icons type="pause" size="24" :color="form.status === 'SUSPENDED' ? '#fff' : '#ff9800'"></uni-icons>
                  <text>休学</text>
                </view>
                <view 
                  class="status-option dropout" 
                  :class="{ 'active': form.status === 'DROP_OUT' }"
                  @click="form.status = 'DROP_OUT'"
                >
                  <uni-icons type="close" size="24" :color="form.status === 'DROP_OUT' ? '#fff' : '#f44336'"></uni-icons>
                  <text>退学</text>
                </view>
              </view>
            </view>
            
            <view class="form-item">
              <text class="label">备注</text>
              <view class="textarea-wrapper">
                <uni-icons type="chat" size="20" color="#999" class="textarea-icon"></uni-icons>
                <textarea 
                  v-model="form.notes" 
                  placeholder="请输入备注信息" 
                  class="textarea" 
                  placeholder-class="placeholder"
                  maxlength="200"
                  auto-height="true"
                ></textarea>
              </view>
            </view>
          </view>
          
          <!-- 底部提交按钮 -->
          <view class="submit-section">
            <button class="submit-btn" @click="handleSubmit" :disabled="loading">
              <uni-icons v-if="!loading" type="checkmarkempty" size="24" color="#fff"></uni-icons>
              <uni-icons v-else type="spinner-cycle" size="24" color="#fff" class="rotating"></uni-icons>
              <text>{{ loading ? '保存中...' : '保存信息' }}</text>
            </button>
          </view>
        </form>
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
      loading: false,
      form: {
        residentName: '未填写',
        idCard: '未填写',
        schoolName: '未填写',
        educationLevel: 'PRIMARY',
        major: '未填写',
        enrollmentDate: '未填写',
        graduationDate: '未填写',
        status: 'STUDYING',
        notes: '未填写'
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
  computed: {
    educationLevelIndex() {
      return this.educationLevelOptions.findIndex(item => item.value === this.form.educationLevel)
    }
  },
  onLoad(options) {
    this.getStatusBarHeight()
    // 接收从居民详情页传递过来的居民ID
    if (options.residentId) {
      this.form.residentId = options.residentId
      // 根据居民ID获取居民详情
      this.getResidentInfo(options.residentId)
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
    
    // 根据居民ID获取居民详情并填充到表单
    getResidentInfo(residentId) {
      if (!residentId) return
      
      get(`/resident/${residentId}`)
        .then(res => {
          if (res.code === 200) {
            // 将获取到的居民信息填充到表单
            this.form.residentName = res.data.name || '未填写'
            this.form.idCard = res.data.idCard || '未填写'
          }
        })
        .catch(err => {
          console.error('获取居民详情失败', err)
        })
    },
    
    // 获取状态栏高度
    getStatusBarHeight() {
      // #ifdef MP-WEIXIN
      const systemInfo = uni.getSystemInfoSync()
      this.statusBarHeight = systemInfo.statusBarHeight || 0
      // #endif
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
    
    // 表单验证
    validateForm() {
      // 辅助函数：检查值是否为有效内容（非空且非"未填写"）
      const isEmptyValue = (value) => {
        return value === '' || value === null || value === undefined || value === '未填写'
      }
      
      // 居民姓名验证
      if (isEmptyValue(this.form.residentName)) {
        uni.showToast({ title: '请输入居民姓名', icon: 'none' })
        return false
      } else if (this.form.residentName.length < 2 || this.form.residentName.length > 20) {
        uni.showToast({ title: '姓名长度应在2-20个字符之间', icon: 'none' })
        return false
      }
      
      // 身份证号验证
      if (isEmptyValue(this.form.idCard)) {
        uni.showToast({ title: '请输入身份证号', icon: 'none' })
        return false
      } else if (!validator.isIdCard(this.form.idCard)) {
        uni.showToast({ title: '请输入有效的身份证号', icon: 'none' })
        return false
      }
      
      // 学校名称验证
      if (isEmptyValue(this.form.schoolName)) {
        uni.showToast({ title: '请输入学校名称', icon: 'none' })
        return false
      } else if (this.form.schoolName.length < 2 || this.form.schoolName.length > 50) {
        uni.showToast({ title: '学校名称长度应在2-50个字符之间', icon: 'none' })
        return false
      }
      
      // 入学日期验证
      if (isEmptyValue(this.form.enrollmentDate)) {
        uni.showToast({ title: '请输入入学日期', icon: 'none' })
        return false
      } else if (this.form.enrollmentDate !== '未填写') {
        // 日期格式验证 (简单验证格式是否为 YYYY-MM-DD)
        const dateRegex = /^\d{4}-\d{2}-\d{2}$/
        if (!dateRegex.test(this.form.enrollmentDate)) {
          uni.showToast({ title: '入学日期格式错误，应为YYYY-MM-DD', icon: 'none' })
          return false
        }
      }
      
      // 毕业日期验证（如果填写）
      if (this.form.graduationDate && this.form.graduationDate !== '未填写') {
        const dateRegex = /^\d{4}-\d{2}-\d{2}$/
        if (!dateRegex.test(this.form.graduationDate)) {
          uni.showToast({ title: '毕业日期格式错误，应为YYYY-MM-DD', icon: 'none' })
          return false
        } else if (this.form.enrollmentDate && this.form.enrollmentDate !== '未填写' && this.form.graduationDate < this.form.enrollmentDate) {
          uni.showToast({ title: '毕业日期不能早于入学日期', icon: 'none' })
          return false
        }
      }
      
      // 专业名称长度验证（如果填写了实际内容，不是"未填写"）
      if (this.form.major && this.form.major !== '未填写') {
        if (this.form.major.length < 2 || this.form.major.length > 50) {
          uni.showToast({ title: '专业名称长度应在2-50个字符之间', icon: 'none' })
          return false
        }
      }
      
      // 备注长度验证（如果填写了实际内容，不是"未填写"）
      if (this.form.notes && this.form.notes !== '未填写') {
        if (this.form.notes.length > 200) {
          uni.showToast({ title: '备注信息不能超过200个字符', icon: 'none' })
          return false
        }
      }
      
      // 学历状态验证
      if (!this.form.status || !['STUDYING', 'GRADUATED', 'SUSPENDED', 'DROP_OUT'].includes(this.form.status)) {
        uni.showToast({ title: '请选择有效的学历状态', icon: 'none' })
        return false
      }
      
      return true
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
      
      post('/education/add', submitData)
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

/* 占位符样式 */
.placeholder {
  color: #adb5bd;
}

/* 学历状态选择器 */
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

.status-option.studying {
  border-color: #e8f5e9;
}

.status-option.graduated {
  border-color: #e3f2fd;
}

.status-option.suspended {
  border-color: #fff3cd;
}

.status-option.dropout {
  border-color: #f8d7da;
}

.status-option.active.studying {
  background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
  border-color: #4caf50;
  color: white;
  box-shadow: 0 4rpx 12rpx rgba(76, 175, 80, 0.3);
  transform: scale(1.02);
}

.status-option.active.graduated {
  background: linear-gradient(135deg, #2196f3 0%, #1976d2 100%);
  border-color: #2196f3;
  color: white;
  box-shadow: 0 4rpx 12rpx rgba(33, 150, 243, 0.3);
  transform: scale(1.02);
}

.status-option.active.suspended {
  background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
  border-color: #ff9800;
  color: white;
  box-shadow: 0 4rpx 12rpx rgba(255, 152, 0, 0.3);
  transform: scale(1.02);
}

.status-option.active.dropout {
  background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%);
  border-color: #f44336;
  color: white;
  box-shadow: 0 4rpx 12rpx rgba(244, 67, 54, 0.3);
  transform: scale(1.02);
}

/* 选择器 */
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

.picker-content {
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

/* 动画类 */
.animate-slide-up {
  animation: slideUp 0.6s ease forwards;
}
</style>