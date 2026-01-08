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
          <text class="title-main">新增居民</text>
          <text class="title-sub">填写居民基本信息</text>
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
          <!-- 基本信息 -->
          <view class="form-section animate-slide-up">
            <view class="section-header">
              <view class="section-icon bg-blue">
                <uni-icons type="person-filled" size="20" color="#fff"></uni-icons>
              </view>
              <view class="section-info">
                <text class="section-title">基本信息</text>
                <text class="section-subtitle">Basic Information</text>
              </view>
            </view>
            
            <view class="form-item">
              <text class="label required">姓名</text>
              <view class="input-wrapper">
                <uni-icons type="person" size="20" color="#999" class="input-icon"></uni-icons>
                <input 
                  v-model="form.name" 
                  placeholder="请输入姓名" 
                  class="input" 
                  placeholder-class="placeholder"
                  maxlength="20"
                />
              </view>
            </view>
            
            <view class="form-item">
              <text class="label required">性别</text>
              <view class="gender-selector">
                <view 
                  class="gender-option" 
                  :class="{ 'active': form.gender === 'MALE', 'male': true }"
                  @click="form.gender = 'MALE'"
                >
                  <uni-icons type="person" size="24" :color="form.gender === 'MALE' ? '#fff' : '#2196f3'"></uni-icons>
                  <text>男</text>
                </view>
                <view 
                  class="gender-option" 
                  :class="{ 'active': form.gender === 'FEMALE', 'female': true }"
                  @click="form.gender = 'FEMALE'"
                >
                  <uni-icons type="person" size="24" :color="form.gender === 'FEMALE' ? '#fff' : '#e91e63'"></uni-icons>
                  <text>女</text>
                </view>
              </view>
            </view>
            
            <view class="form-row">
              <view class="form-item half">
                <text class="label required">年龄</text>
                <view class="input-wrapper">
                  <uni-icons type="calendar" size="20" color="#999" class="input-icon"></uni-icons>
                  <input 
                    v-model="form.age" 
                    placeholder="年龄" 
                    class="input" 
                    placeholder-class="placeholder"
                    type="number"
                    maxlength="3"
                  />
                </view>
              </view>
              
              <view class="form-item half">
                <text class="label">民族</text>
                <view class="input-wrapper">
                  <uni-icons type="flag" size="20" color="#999" class="input-icon"></uni-icons>
                  <input 
                    v-model="form.nationality" 
                    placeholder="民族" 
                    class="input" 
                    placeholder-class="placeholder"
                    maxlength="10"
                  />
                </view>
              </view>
            </view>
            
            <view class="form-item">
              <text class="label required">身份证号</text>
              <view class="input-wrapper">
                <uni-icons type="card" size="20" color="#999" class="input-icon"></uni-icons>
                <input 
                  v-model="form.idCard" 
                  placeholder="请输入18位身份证号" 
                  class="input" 
                  placeholder-class="placeholder"
                  maxlength="18"
                />
              </view>
            </view>
            
            <view class="form-item">
              <text class="label">婚姻状况</text>
              <picker 
                :value="maritalStatusIndex" 
                :range="maritalStatusOptions" 
                :range-key="'label'"
                class="picker"
                @change="handleMaritalStatusChange"
              >
                <view class="picker-content">
                  <uni-icons type="heart" size="20" color="#999"></uni-icons>
                  <text class="picker-text">{{ getMaritalStatusLabel(form.maritalStatus) }}</text>
                  <uni-icons type="arrowdown" size="20" color="#999"></uni-icons>
                </view>
              </picker>
            </view>
          </view>
          
          <!-- 联系方式 -->
          <view class="form-section animate-slide-up" style="animation-delay: 0.1s;">
            <view class="section-header">
              <view class="section-icon bg-green">
                <uni-icons type="phone-filled" size="20" color="#fff"></uni-icons>
              </view>
              <view class="section-info">
                <text class="section-title">联系方式</text>
                <text class="section-subtitle">Contact Information</text>
              </view>
            </view>
            
            <view class="form-item">
              <text class="label required">手机号码</text>
              <view class="input-wrapper">
                <uni-icons type="phone" size="20" color="#999" class="input-icon"></uni-icons>
                <input 
                  v-model="form.phone" 
                  placeholder="请输入手机号码" 
                  class="input" 
                  placeholder-class="placeholder"
                  type="number"
                  maxlength="11"
                />
              </view>
            </view>
            
            <view class="form-item">
              <text class="label">电子邮箱</text>
              <view class="input-wrapper">
                <uni-icons type="email" size="20" color="#999" class="input-icon"></uni-icons>
                <input 
                  v-model="form.email" 
                  placeholder="请输入电子邮箱" 
                  class="input" 
                  placeholder-class="placeholder"
                  type="email"
                />
              </view>
            </view>
            
            <view class="form-item">
              <text class="label required">家庭住址</text>
              <view class="textarea-wrapper">
                <uni-icons type="location" size="20" color="#999" class="textarea-icon"></uni-icons>
                <textarea 
                  v-model="form.address" 
                  placeholder="请输入家庭住址" 
                  class="textarea" 
                  placeholder-class="placeholder"
                  maxlength="200"
                  auto-height="true"
                ></textarea>
              </view>
            </view>
          </view>
          
          <!-- 其他信息 -->
          <view class="form-section animate-slide-up" style="animation-delay: 0.2s;">
            <view class="section-header">
              <view class="section-icon bg-purple">
                <uni-icons type="info-filled" size="20" color="#fff"></uni-icons>
              </view>
              <view class="section-info">
                <text class="section-title">其他信息</text>
                <text class="section-subtitle">Additional Information</text>
              </view>
            </view>
            
            <view class="form-item">
              <text class="label">教育程度</text>
              <picker 
                :value="educationIndex" 
                :range="educationOptions" 
                :range-key="'label'"
                class="picker"
                @change="handleEducationChange"
              >
                <view class="picker-content">
                  <uni-icons type="book" size="20" color="#999"></uni-icons>
                  <text class="picker-text">{{ getEducationLabel(form.education) }}</text>
                  <uni-icons type="arrowdown" size="20" color="#999"></uni-icons>
                </view>
              </picker>
            </view>
            
            <view class="form-row">
              <view class="form-item half">
                <text class="label">职业</text>
                <view class="input-wrapper">
                  <uni-icons type="briefcase" size="20" color="#999" class="input-icon"></uni-icons>
                  <input 
                    v-model="form.occupation" 
                    placeholder="职业" 
                    class="input" 
                    placeholder-class="placeholder"
                    maxlength="50"
                  />
                </view>
              </view>
              
              <view class="form-item half">
                <text class="label">月收入</text>
                <view class="input-wrapper">
                  <uni-icons type="wallet" size="20" color="#999" class="input-icon"></uni-icons>
                  <input 
                    v-model="form.income" 
                    placeholder="月收入" 
                    class="input" 
                    placeholder-class="placeholder"
                    type="number"
                  />
                </view>
              </view>
            </view>
            
            <view class="form-item">
              <text class="label">户籍类型</text>
              <view class="household-selector">
                <view 
                  class="household-option" 
                  :class="{ 'active': form.householdType === 'URBAN' }"
                  @click="form.householdType = 'URBAN'"
                >
                  <uni-icons type="home" size="24" :color="form.householdType === 'URBAN' ? '#fff' : '#667eea'"></uni-icons>
                  <text>城镇</text>
                </view>
                <view 
                  class="household-option" 
                  :class="{ 'active': form.householdType === 'RURAL' }"
                  @click="form.householdType = 'RURAL'"
                >
                  <uni-icons type="map-pin" size="24" :color="form.householdType === 'RURAL' ? '#fff' : '#4caf50'"
                ></uni-icons>
                  <text>农村</text>
                </view>
              </view>
            </view>
            
            <view class="form-item">
              <text class="label required">户籍ID</text>
              <view class="input-wrapper">
                <uni-icons type="location" size="20" color="#999" class="input-icon"></uni-icons>
                <input 
                  v-model="form.householdId" 
                  placeholder="请输入户籍ID" 
                  class="input" 
                  placeholder-class="placeholder"
                  type="number"
                />
              </view>
            </view>
            
            <view class="form-item">
              <text class="label">是否户主</text>
              <view class="radio-group">
                <radio-group value="{{ form.isHouseholder }}" @change="handleHouseholderChange">
                  <label class="radio-item">
                    <radio value="true" />是
                  </label>
                  <label class="radio-item">
                    <radio value="false" />否
                  </label>
                </radio-group>
              </view>
            </view>
            
            <view class="form-item" v-if="form.isHouseholder !== 'true'">
              <text class="label required">与户主关系</text>
              <picker 
                :value="relationshipIndex" 
                :range="relationshipOptions" 
                :range-key="'label'"
                class="picker"
                @change="handleRelationshipChange"
              >
                <view class="picker-content">
                  <uni-icons type="person" size="20" color="#999"></uni-icons>
                  <text class="picker-text">{{ getRelationshipLabel(form.relationship) }}</text>
                  <uni-icons type="arrowdown" size="20" color="#999"></uni-icons>
                </view>
              </picker>
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
import { post } from '@/utils/request'
import validator from '@/utils/validator'

export default {
  data() {
    return {
      loading: false,
      form: {
        name: '',
        gender: 'MALE',
        age: '',
        idCard: '',
        nationality: '汉族',
        maritalStatus: 'SINGLE',
        phone: '',
        email: '',
        address: '',
        education: 'PRIMARY',
        occupation: '',
        income: '',
        householdType: 'URBAN',
        householdId: '',
        isHouseholder: 'false',
        relationship: ''
      },
      statusBarHeight: 0,
      // 婚姻状况选项
      maritalStatusOptions: [
        { value: 'SINGLE', label: '未婚' },
        { value: 'MARRIED', label: '已婚' },
        { value: 'DIVORCED', label: '离婚' },
        { value: 'WIDOWED', label: '丧偶' }
      ],
      // 教育程度选项
      educationOptions: [
        { value: 'PRIMARY', label: '小学' },
        { value: 'JUNIOR', label: '初中' },
        { value: 'SENIOR', label: '高中' },
        { value: 'COLLEGE', label: '大专' },
        { value: 'BACHELOR', label: '本科' },
        { value: 'MASTER', label: '硕士' },
        { value: 'DOCTOR', label: '博士' }
      ],
      // 与户主关系选项
      relationshipOptions: [
        { value: '配偶', label: '配偶' },
        { value: '子女', label: '子女' },
        { value: '父母', label: '父母' },
        { value: '兄弟姐妹', label: '兄弟姐妹' },
        { value: '其他', label: '其他' }
      ]
    }
  },
  computed: {
    maritalStatusIndex() {
      return this.maritalStatusOptions.findIndex(item => item.value === this.form.maritalStatus)
    },
    educationIndex() {
      return this.educationOptions.findIndex(item => item.value === this.form.education)
    },
    relationshipIndex() {
      return this.relationshipOptions.findIndex(item => item.value === this.form.relationship)
    }
  },
  onLoad() {
    this.getStatusBarHeight()
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
    
    // 获取婚姻状况标签
    getMaritalStatusLabel(value) {
      const option = this.maritalStatusOptions.find(item => item.value === value)
      return option ? option.label : '请选择'
    },
    
    // 获取教育程度标签
    getEducationLabel(value) {
      const option = this.educationOptions.find(item => item.value === value)
      return option ? option.label : '请选择'
    },
    
    // 婚姻状况变化处理
    handleMaritalStatusChange(e) {
      const index = e.detail.value
      this.form.maritalStatus = this.maritalStatusOptions[index].value
    },
    
    // 教育程度变化处理
    handleEducationChange(e) {
      const index = e.detail.value
      this.form.education = this.educationOptions[index].value
    },
    
    // 是否户主变化处理
    handleHouseholderChange(e) {
      // 确保isHouseholder始终是字符串类型
      this.form.isHouseholder = e.detail.value === true ? 'true' : e.detail.value === false ? 'false' : e.detail.value
      // 当选择为户主时，清空与户主关系
      if (this.form.isHouseholder === 'true') {
        this.form.relationship = ''
      }
    },
    
    // 获取与户主关系标签
    getRelationshipLabel(value) {
      const option = this.relationshipOptions.find(item => item.value === value)
      return option ? option.label : '请选择'
    },
    
    // 与户主关系变化处理
    handleRelationshipChange(e) {
      const index = e.detail.value
      this.form.relationship = this.relationshipOptions[index].value
    },
    
    // 表单验证
    validateForm() {
      // 姓名验证
      if (!this.form.name) {
        uni.showToast({ title: '请输入姓名', icon: 'none' })
        return false
      }
      
      // 年龄验证
      if (!this.form.age) {
        uni.showToast({ title: '请输入年龄', icon: 'none' })
        return false
      }
      
      if (!validator.isInteger(this.form.age) || this.form.age < 0 || this.form.age > 150) {
        uni.showToast({ title: '请输入有效的年龄', icon: 'none' })
        return false
      }
      
      // 身份证号验证
      if (!this.form.idCard) {
        uni.showToast({ title: '请输入身份证号', icon: 'none' })
        return false
      }
      
      if (!validator.isIdCard(this.form.idCard)) {
        uni.showToast({ title: '请输入有效的身份证号', icon: 'none' })
        return false
      }
      
      // 手机号码验证
      if (!this.form.phone) {
        uni.showToast({ title: '请输入手机号码', icon: 'none' })
        return false
      }
      
      if (!validator.isMobile(this.form.phone)) {
        uni.showToast({ title: '请输入有效的手机号码', icon: 'none' })
        return false
      }
      
      // 邮箱验证 (如果有输入)
      if (this.form.email && !validator.isEmail(this.form.email)) {
        uni.showToast({ title: '请输入有效的电子邮箱', icon: 'none' })
        return false
      }
      
      // 家庭住址验证
      if (!this.form.address) {
        uni.showToast({ title: '请输入家庭住址', icon: 'none' })
        return false
      }
      
      // 户籍ID验证
      if (!this.form.householdId) {
        uni.showToast({ title: '请输入户籍ID', icon: 'none' })
        return false
      }
      
      // 与户主关系验证（非户主时必填）
      if (this.form.isHouseholder !== 'true' && !this.form.relationship) {
        uni.showToast({ title: '请选择与户主关系', icon: 'none' })
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
      
      // 从身份证号提取出生日期
      let birthDate = ''
      const idCard = this.form.idCard
      if (idCard.length === 18) {
        // 18位身份证：第7-14位是出生日期
        birthDate = `${idCard.substring(6, 10)}-${idCard.substring(10, 12)}-${idCard.substring(12, 14)}`
      } else if (idCard.length === 15) {
        // 15位身份证：第7-12位是出生日期，年份补19
        birthDate = `19${idCard.substring(6, 8)}-${idCard.substring(8, 10)}-${idCard.substring(10, 12)}`
      }
      
      // 处理表单数据
      const isHouseholder = this.form.isHouseholder === 'true' || this.form.isHouseholder === true
      const submitData = {
        name: this.form.name,
        gender: this.form.gender,
        idCard: this.form.idCard,
        birthDate: birthDate,
        householdId: parseInt(this.form.householdId),
        relationship: isHouseholder ? '户主' : this.form.relationship || '',
        ethnicGroup: this.form.nationality,
        educationLevel: this.form.education,
        occupation: this.form.occupation,
        maritalStatus: this.form.maritalStatus,
        phoneNumber: this.form.phone,
        email: this.form.email,
        isHouseholder: isHouseholder,
        address: this.form.address
      }
      
      // 处理可选字段
      if (this.form.income) {
        submitData.income = parseFloat(this.form.income)
      }
      
      post('/resident/add', submitData)
        .then(res => {
          if (res.code === 200) {
            uni.showToast({
              title: '新增成功',
              icon: 'success'
            })
            // 返回列表页
            setTimeout(() => {
              this.handleBack()
            }, 1500)
          } else {
            uni.showToast({
              title: res.msg || res.message || '新增失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('新增居民失败', err)
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

/* 性别选择器 */
.gender-selector {
  display: flex;
  gap: 16rpx;
}

.gender-option {
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

.gender-option.male {
  border-color: #e3f2fd;
}

.gender-option.female {
  border-color: #fce4ec;
}

.gender-option.active.male {
  background: linear-gradient(135deg, #2196f3 0%, #1976d2 100%);
  border-color: #2196f3;
  color: white;
  box-shadow: 0 4rpx 12rpx rgba(33, 150, 243, 0.3);
  transform: scale(1.02);
}

.gender-option.active.female {
  background: linear-gradient(135deg, #e91e63 0%, #c2185b 100%);
  border-color: #e91e63;
  color: white;
  box-shadow: 0 4rpx 12rpx rgba(233, 30, 99, 0.3);
  transform: scale(1.02);
}

/* 户籍选择器 */
.household-selector {
  display: flex;
  gap: 16rpx;
}

.household-option {
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

.household-option.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
  color: white;
  box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
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