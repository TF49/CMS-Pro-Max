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
          <text class="title-main">编辑居民</text>
          <text class="title-sub">修改居民相关信息</text>
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
            <text class="label required">姓名</text>
            <view class="input-wrapper">
              <uni-icons type="person" size="20" color="#999" class="input-icon"></uni-icons>
              <input 
                v-model="form.name" 
                placeholder="请输入姓名" 
                class="input"
                maxlength="20"
              />
            </view>
            <text v-if="errors.name" class="error-text">{{ errors.name }}</text>
          </view>
          
          <view class="form-row">
            <view class="form-item half">
              <text class="label required">性别</text>
              <view class="radio-group-wrapper">
                <radio-group :value="form.gender" @change="handleGenderChange" class="radio-group">
                  <label class="radio-item">
                    <radio value="MALE" color="#667eea" />
                    <text class="radio-text">男</text>
                  </label>
                  <label class="radio-item">
                    <radio value="FEMALE" color="#667eea" />
                    <text class="radio-text">女</text>
                  </label>
                </radio-group>
              </view>
              <text v-if="errors.gender" class="error-text">{{ errors.gender }}</text>
            </view>
            
            <view class="form-item half">
              <text class="label required">年龄</text>
              <view class="input-wrapper">
                <uni-icons type="calendar" size="20" color="#999" class="input-icon"></uni-icons>
                <input 
                  v-model="form.age" 
                  placeholder="请输入年龄" 
                  class="input"
                  type="number"
                  maxlength="3"
                />
              </view>
              <text v-if="errors.age" class="error-text">{{ errors.age }}</text>
            </view>
          </view>
          
          <view class="form-item">
            <text class="label required">身份证号</text>
            <view class="input-wrapper">
              <uni-icons type="wallet" size="20" color="#999" class="input-icon"></uni-icons>
              <input 
                v-model="form.idCard" 
                placeholder="请输入18位身份证号" 
                class="input"
                maxlength="18"
              />
            </view>
            <text v-if="errors.idCard" class="error-text">{{ errors.idCard }}</text>
          </view>
          
          <view class="form-row">
            <view class="form-item half">
              <text class="label">民族</text>
              <view class="input-wrapper">
                <uni-icons type="flag" size="20" color="#999" class="input-icon"></uni-icons>
                <input 
                  v-model="form.ethnicGroup" 
                  placeholder="请输入民族" 
                  class="input"
                  maxlength="10"
                />
              </view>
            </view>
            
            <view class="form-item half">
              <text class="label">婚姻状况</text>
              <picker 
                :value="maritalStatusOptions.findIndex(item => item.value === form.maritalStatus)" 
                :range="maritalStatusOptions" 
                :range-key="'label'"
                @change="handleMaritalStatusChange"
              >
                <view class="picker-wrapper">
                  <uni-icons type="heart" size="20" color="#999" class="input-icon"></uni-icons>
                  <text class="picker-text">{{ getMaritalStatusLabel(form.maritalStatus) }}</text>
                </view>
              </picker>
            </view>
          </view>
        </view>
        
        <!-- 联系方式 -->
        <view class="form-section animate-slide-up">
          <view class="section-header">
            <view class="section-icon bg-blue">
              <uni-icons type="phone" size="20" color="#fff"></uni-icons>
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
                v-model="form.phoneNumber" 
                placeholder="请输入手机号码" 
                class="input"
                type="number"
                maxlength="11"
              />
            </view>
            <text v-if="errors.phoneNumber" class="error-text">{{ errors.phoneNumber }}</text>
          </view>
          
          <view class="form-item">
            <text class="label">电子邮箱</text>
            <view class="input-wrapper">
              <uni-icons type="email" size="20" color="#999" class="input-icon"></uni-icons>
              <input 
                v-model="form.email" 
                placeholder="请输入电子邮箱" 
                class="input"
                type="email"
              />
            </view>
            <text v-if="errors.email" class="error-text">{{ errors.email }}</text>
          </view>
          
          <view class="form-item">
            <text class="label required">家庭住址</text>
            <view class="textarea-wrapper">
              <uni-icons type="location" size="20" color="#999" class="textarea-icon"></uni-icons>
              <textarea 
                v-model="form.address" 
                placeholder="请输入家庭住址" 
                class="textarea"
                maxlength="200"
                auto-height="true"
              ></textarea>
            </view>
            <text v-if="errors.address" class="error-text">{{ errors.address }}</text>
          </view>
        </view>
        
        <!-- 其他信息 -->
        <view class="form-section animate-slide-up">
          <view class="section-header">
            <view class="section-icon bg-blue">
              <uni-icons type="info" size="20" color="#fff"></uni-icons>
            </view>
            <view class="section-info">
              <text class="section-title">其他信息</text>
              <text class="section-subtitle">Other Information</text>
            </view>
          </view>
          
          <view class="form-row">
            <view class="form-item half">
              <text class="label">教育程度</text>
              <picker 
                :value="educationOptions.findIndex(item => item.value === form.educationLevel)" 
                :range="educationOptions" 
                :range-key="'label'"
                @change="handleEducationChange"
              >
                <view class="picker-wrapper">
                  <uni-icons type="book" size="20" color="#999" class="input-icon"></uni-icons>
                  <text class="picker-text">{{ getEducationLabel(form.educationLevel) }}</text>
                </view>
              </picker>
            </view>
            
            <view class="form-item half">
              <text class="label">职业</text>
              <view class="input-wrapper">
                <uni-icons type="briefcase" size="20" color="#999" class="input-icon"></uni-icons>
                <input 
                  v-model="form.occupation" 
                  placeholder="请输入职业" 
                  class="input"
                  maxlength="50"
                />
              </view>
            </view>
          </view>
          
          <view class="form-row">
            <view class="form-item half">
              <text class="label">婚姻状况</text>
              <picker 
                :value="maritalStatusOptions.findIndex(item => item.value === form.maritalStatus)" 
                :range="maritalStatusOptions" 
                :range-key="'label'"
                @change="handleMaritalStatusChange"
              >
                <view class="picker-wrapper">
                  <uni-icons type="heart" size="20" color="#999" class="input-icon"></uni-icons>
                  <text class="picker-text">{{ getMaritalStatusLabel(form.maritalStatus) }}</text>
                </view>
              </picker>
            </view>
            
            <view class="form-item half">
              <text class="label">月收入</text>
              <view class="input-wrapper">
                <uni-icons type="cash" size="20" color="#999" class="input-icon"></uni-icons>
                <input 
                  v-model="form.income" 
                  placeholder="请输入月收入" 
                  class="input"
                  type="number"
                />
              </view>
            </view>
          </view>
          
          <view class="form-row">
            <view class="form-item half">
              <text class="label">民族</text>
              <view class="input-wrapper">
                <uni-icons type="flag" size="20" color="#999" class="input-icon"></uni-icons>
                <input 
                  v-model="form.ethnicGroup" 
                  placeholder="请输入民族" 
                  class="input"
                  maxlength="10"
                />
              </view>
            </view>
            
            <view class="form-item half">
              <text class="label">户籍类型</text>
              <view class="radio-group-wrapper">
                <radio-group :value="form.householdType" @change="handleHouseholdTypeChange" class="radio-group">
                  <label class="radio-item">
                    <radio value="URBAN" color="#667eea" />
                    <text class="radio-text">城镇</text>
                  </label>
                  <label class="radio-item">
                    <radio value="RURAL" color="#667eea" />
                    <text class="radio-text">农村</text>
                  </label>
                </radio-group>
              </view>
            </view>
          </view>
          
          <view class="form-row">
            <view class="form-item half">
              <text class="label">是否户主</text>
              <view class="radio-group-wrapper">
                <radio-group :value="form.isHouseholder" @change="handleHouseholderChange" class="radio-group">
                  <label class="radio-item">
                    <radio value="true" color="#667eea" />
                    <text class="radio-text">是</text>
                  </label>
                  <label class="radio-item">
                    <radio value="false" color="#667eea" />
                    <text class="radio-text">否</text>
                  </label>
                </radio-group>
              </view>
            </view>
            
            <view class="form-item half" v-if="form.isHouseholder !== 'true'">
              <text class="label required">与户主关系</text>
              <picker 
                :value="relationshipOptions.findIndex(item => item.value === form.relationship)" 
                :range="relationshipOptions" 
                :range-key="'label'"
                @change="handleRelationshipChange"
              >
                <view class="picker-wrapper">
                  <uni-icons type="users" size="20" color="#999" class="input-icon"></uni-icons>
                  <text class="picker-text">{{ getRelationshipLabel(form.relationship) }}</text>
                </view>
              </picker>
              <text v-if="errors.relationship" class="error-text">{{ errors.relationship }}</text>
            </view>
          </view>
          
          <!-- 备注 -->
          <view class="form-item">
            <text class="label">备注</text>
            <view class="textarea-wrapper">
              <uni-icons type="compose" size="20" color="#999" class="textarea-icon"></uni-icons>
              <textarea 
                v-model="form.notes" 
                placeholder="请输入备注信息" 
                class="textarea"
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
      residentId: null,
      loading: false,
      // 保存户籍ID，用于后续提交
      householdId: null,
      form: {
        name: '',
        gender: 'MALE',
        age: '',
        idCard: '',
        ethnicGroup: '汉族',
        maritalStatus: 'SINGLE',
        phoneNumber: '',
        email: '',
        address: '',
        educationLevel: 'PRIMARY',
        occupation: '',
        income: '',
        householdType: 'URBAN',
        isHouseholder: 'false',
        relationship: '',
        notes: ''
      },
      errors: {},
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
      ],
      statusBarHeight: 0
    }
  },
  onLoad(options) {
    this.getStatusBarHeight()
    // 获取传递的居民ID，并转换为数字类型
    if (options.id) {
      this.residentId = parseInt(options.id)
      this.loadResidentDetail()
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
    
    // 加载居民详情
    loadResidentDetail() {
      this.loading = true
      get(`/resident/${this.residentId}`)
        .then(res => {
          if (res.code === 200) {
            const resident = res.data
            // 转换数据格式
            // 处理性别转换：将中文性别转换为英文值
            let genderValue = resident.gender || 'MALE'
            if (genderValue === '男') {
              genderValue = 'MALE'
            } else if (genderValue === '女') {
              genderValue = 'FEMALE'
            }
            
            // 处理婚姻状况转换：将中文婚姻状况转换为英文值
            let maritalStatusValue = resident.maritalStatus || 'SINGLE'
            if (maritalStatusValue === '未婚') {
              maritalStatusValue = 'SINGLE'
            } else if (maritalStatusValue === '已婚') {
              maritalStatusValue = 'MARRIED'
            } else if (maritalStatusValue === '离婚') {
              maritalStatusValue = 'DIVORCED'
            } else if (maritalStatusValue === '丧偶') {
              maritalStatusValue = 'WIDOWED'
            }
            
            // 处理户籍类型转换：将中文户籍类型转换为英文值
            let householdTypeValue = resident.householdType || 'URBAN'
            if (householdTypeValue === '城镇') {
              householdTypeValue = 'URBAN'
            } else if (householdTypeValue === '农村') {
              householdTypeValue = 'RURAL'
            }
            
            // 处理教育程度转换：将中文教育程度转换为英文值
            let educationLevelValue = resident.educationLevel || 'PRIMARY'
            const educationMap = {
              '小学': 'PRIMARY',
              '初中': 'JUNIOR',
              '高中': 'SENIOR',
              '大专': 'COLLEGE',
              '本科': 'BACHELOR',
              '硕士': 'MASTER',
              '博士': 'DOCTOR'
            }
            if (educationMap[educationLevelValue]) {
              educationLevelValue = educationMap[educationLevelValue]
            }
            
            // 计算年龄：如果后端没有返回年龄，根据出生日期计算
            let ageValue = resident.age ? resident.age.toString() : ''
            if (!ageValue && resident.birthDate) {
              const birth = new Date(resident.birthDate)
              const today = new Date()
              let age = today.getFullYear() - birth.getFullYear()
              const monthDiff = today.getMonth() - birth.getMonth()
              if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
                age--
              }
              ageValue = age.toString()
            }
            
            // 统一处理空值：设置为"未填写"
            this.form = {
              name: resident.name || '未填写',
              gender: genderValue,
              age: ageValue || '未填写',
              idCard: resident.idCard || '未填写',
              ethnicGroup: resident.ethnicGroup || '汉族',
              maritalStatus: maritalStatusValue,
              phoneNumber: resident.phoneNumber || '未填写',
              email: resident.email || '未填写',
              address: resident.address || '未填写',
              educationLevel: educationLevelValue,
              occupation: resident.occupation || '未填写',
              income: resident.income ? resident.income.toString() : '未填写',
              householdType: householdTypeValue,
              isHouseholder: resident.isHouseholder ? 'true' : 'false',
              relationship: resident.relationship || '未填写',
              notes: resident.notes || '未填写'
            }
            
            // 保存户籍ID，用于后续提交
            this.householdId = resident.householdId || resident.household_id
            
            // 根据居民关联的户籍ID获取户籍地址
            if (this.householdId) {
              this.getHouseholdAddress(this.householdId)
            } else if (resident.householdNumber) {
              this.getHouseholdAddressByNumber(resident.householdNumber)
            }
          } else {
            uni.showToast({
              title: res.msg || res.message || '获取详情失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('获取居民详情失败', err)
          uni.showToast({
            title: '网络错误',
            icon: 'none'
          })
        })
        .finally(() => {
          this.loading = false
        })
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
    
    // 性别变化处理
    handleGenderChange(e) {
      this.form.gender = e.detail.value
    },
    
    // 婚姻状况变化处理
    handleMaritalStatusChange(e) {
      const index = e.detail.value
      this.form.maritalStatus = this.maritalStatusOptions[index].value
    },
    
    // 教育程度变化处理
    handleEducationChange(e) {
      const index = e.detail.value
      this.form.educationLevel = this.educationOptions[index].value
    },
    
    // 户籍类型变化处理
    handleHouseholdTypeChange(e) {
      this.form.householdType = e.detail.value
    },
    
    // 是否户主变化处理
    handleHouseholderChange(e) {
      this.form.isHouseholder = e.detail.value
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
    
    // 通过户籍ID获取户籍地址
    getHouseholdAddress(householdId) {
      get(`/household/${householdId}`)
        .then(res => {
          if (res.code === 200 && res.data) {
            this.form.address = res.data.address || this.form.address
          }
        })
        .catch(err => {
          console.error('获取户籍地址失败', err)
        })
    },
    
    // 通过户籍编号获取户籍地址
    getHouseholdAddressByNumber(householdNumber) {
      get(`/household/number/${householdNumber}`)
        .then(res => {
          if (res.code === 200 && res.data) {
            this.form.address = res.data.address || this.form.address
          }
        })
        .catch(err => {
          console.error('获取户籍地址失败', err)
        })
    },
    
    // 表单验证
    validateForm() {
      
      // 姓名验证
      if (!this.form.name || this.form.name === '未填写') {
        uni.showToast({ title: '请输入姓名', icon: 'none' })
        return false
      }
      
      // 年龄验证
      if (!this.form.age || this.form.age === '未填写') {
        uni.showToast({ title: '请输入年龄', icon: 'none' })
        return false
      }
      
      // 转换为数字进行验证
      const ageNum = parseInt(this.form.age)
      if (isNaN(ageNum) || ageNum < 0 || ageNum > 150) {
        uni.showToast({ title: '请输入有效的年龄', icon: 'none' })
        return false
      }
      
      // 身份证号验证
      if (!this.form.idCard || this.form.idCard === '未填写') {
        uni.showToast({ title: '请输入身份证号', icon: 'none' })
        return false
      }
      
      if (!validator.isIdCard(this.form.idCard)) {
        uni.showToast({ title: '请输入有效的身份证号', icon: 'none' })
        return false
      }
      
      // 手机号码验证
      if (!this.form.phoneNumber || this.form.phoneNumber === '未填写') {
        uni.showToast({ title: '请输入手机号码', icon: 'none' })
        return false
      }
      
      if (!validator.isMobile(this.form.phoneNumber)) {
        uni.showToast({ title: '请输入有效的手机号码', icon: 'none' })
        return false
      }
      
      // 邮箱验证
      if (this.form.email && this.form.email !== '未填写' && !validator.isEmail(this.form.email)) {
        uni.showToast({ title: '请输入有效的电子邮箱', icon: 'none' })
        return false
      }
      
      // 家庭住址验证
      if (!this.form.address || this.form.address === '未填写') {
        uni.showToast({ title: '请输入家庭住址', icon: 'none' })
        return false
      }
      
      // 与户主关系验证（非户主时必填）
      if (this.form.isHouseholder !== 'true' && (!this.form.relationship || this.form.relationship === '未填写')) {
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
      
      // 处理表单数据
      const submitData = { 
        ...this.form,
        // 添加居民ID到提交数据中
        id: this.residentId,
        // 添加户籍ID到提交数据中
        householdId: this.householdId,
        // 确保后端能识别household_id字段
        household_id: this.householdId
      }
      
      // 处理特殊值：将"未填写"转换为空字符串或合适的值
      for (const key in submitData) {
        if (submitData[key] === '未填写') {
          submitData[key] = ''
        }
      }
      
      // 从身份证号提取出生日期
      let birthDate = ''
      const idCard = submitData.idCard
      if (idCard && (idCard.length === 18 || idCard.length === 15)) {
        if (idCard.length === 18) {
          // 18位身份证：第7-14位是出生日期
          birthDate = `${idCard.substring(6, 10)}-${idCard.substring(10, 12)}-${idCard.substring(12, 14)}`
        } else if (idCard.length === 15) {
          // 15位身份证：第7-12位是出生日期，年份补19
          birthDate = `19${idCard.substring(6, 8)}-${idCard.substring(8, 10)}-${idCard.substring(10, 12)}`
        }
      }
      submitData.birthDate = birthDate
      submitData.birth_date = birthDate // 确保后端能识别birth_date字段
      
      // 将字符串类型的数字转换为真正的数字
      if (submitData.age) {
        submitData.age = parseInt(submitData.age)
      } else {
        delete submitData.age // 如果年龄为空，删除该字段
      }
      
      if (submitData.income) {
        submitData.income = parseFloat(submitData.income)
      } else {
        delete submitData.income // 如果收入为空，删除该字段
      }
      
      // 处理布尔值
      submitData.isHouseholder = submitData.isHouseholder === 'true' || submitData.isHouseholder === true
      
      // 确保householdId是数字类型
      if (submitData.householdId) {
        submitData.householdId = parseInt(submitData.householdId)
        submitData.household_id = parseInt(submitData.household_id)
      }
      
      put('/resident/update', submitData)
        .then(res => {
          if (res.code === 200) {
            uni.showToast({
              title: '编辑成功',
              icon: 'success'
            })
            // 返回详情页
            setTimeout(() => {
              this.handleBack()
            }, 1500)
          } else {
            uni.showToast({
              title: res.msg || res.message || '编辑失败',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('编辑居民失败', err)
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

/* 单选组 */
.radio-group-wrapper {
  background: #f8f9fa;
  border: 2rpx solid #e9ecef;
  border-radius: 16rpx;
  padding: 20rpx;
}

.radio-group {
  display: flex;
  gap: 40rpx;
  align-items: center;
}

.radio-item {
  display: flex;
  align-items: center;
  gap: 10rpx;
  font-size: 26rpx;
  color: #333;
  cursor: pointer;
  padding: 10rpx 0;
  transition: all 0.3s ease;
}

/* 错误文本 */
.error-text {
  font-size: 22rpx;
  color: #ff4757;
  margin-top: 8rpx;
  display: block;
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