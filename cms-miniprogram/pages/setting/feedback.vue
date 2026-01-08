<template>
  <view class="feedback-container">
    <!-- 头部导航 -->
    <view class="header">
      <view class="header-left">
        <button class="back-btn" @click="handleBack">
          <uni-icons type="back" size="28" color="#333"></uni-icons>
        </button>
      </view>
      <view class="header-title">意见反馈</view>
      <view class="header-right"></view>
    </view>
    
    <!-- 内容容器 -->
    <view class="content-container">
      <!-- 反馈表单 -->
      <view class="feedback-form">
        <!-- 反馈类型 -->
        <view class="form-item">
          <view class="form-label">
            <text class="label-text">反馈类型</text>
            <text class="required">*</text>
          </view>
          <view class="form-control">
            <radio-group :value="form.type" @change="handleTypeChange" class="radio-group">
              <label class="radio-item">
                <radio value="SUGGESTION" color="#667eea" />
                <text class="radio-text">功能建议</text>
              </label>
              <label class="radio-item">
                <radio value="BUG" color="#667eea" />
                <text class="radio-text">Bug反馈</text>
              </label>
              <label class="radio-item">
                <radio value="OTHER" color="#667eea" />
                <text class="radio-text">其他</text>
              </label>
            </radio-group>
          </view>
        </view>
        
        <!-- 反馈内容 -->
        <view class="form-item">
          <view class="form-label">
            <text class="label-text">反馈内容</text>
            <text class="required">*</text>
          </view>
          <view class="form-control textarea-control">
            <textarea
              class="textarea-field"
              placeholder="请详细描述您的问题或建议（至少10个字符）"
              v-model="form.content"
              maxlength="500"
              :auto-height="true"
              show-word-limit
              rows="5"
            ></textarea>
          </view>
          <view v-if="errors.content" class="error-message">{{ errors.content }}</view>
        </view>
        
        <!-- 联系方式 -->
        <view class="form-item">
          <view class="form-label">
            <text class="label-text">联系方式</text>
            <text class="optional">（可选）</text>
          </view>
          <view class="form-control">
            <input
              class="input-field"
              type="text"
              placeholder="请输入手机号或邮箱，方便我们联系您"
              v-model="form.contact"
              maxlength="50"
            />
          </view>
          <view v-if="errors.contact" class="error-message">{{ errors.contact }}</view>
        </view>
        
        <!-- 图片上传 -->
        <view class="form-item">
          <view class="form-label">
            <text class="label-text">图片上传</text>
            <text class="optional">（可选）</text>
          </view>
          <view class="image-upload-container">
            <!-- 已上传图片列表 -->
            <view class="uploaded-images">
              <view 
                class="image-item" 
                v-for="(image, index) in form.images" 
                :key="index"
              >
                <image class="uploaded-image" :src="image" mode="aspectFill" lazy-load></image>
                <button 
                  class="delete-image-btn" 
                  @click="handleDeleteImage(index)"
                >
                  <uni-icons type="close" size="20" color="#fff"></uni-icons>
                </button>
              </view>
            </view>
            
            <!-- 上传按钮 -->
            <button 
              class="upload-btn" 
              @click="handleImageUpload" 
              :disabled="form.images.length >= maxImages"
            >
              <view class="upload-btn-inner">
                <uni-icons type="plus" size="40" color="#999"></uni-icons>
                <text class="upload-text">添加图片</text>
                <text class="upload-subtext">{{ form.images.length }}/{{ maxImages }}</text>
              </view>
            </button>
            
            <!-- 图片上传提示 -->
            <view class="upload-tip">
              <text class="tip-text">支持JPG、PNG格式，单张不超过2MB，最多上传{{ maxImages }}张</text>
            </view>
          </view>
        </view>
        
        <!-- 提交按钮 -->
        <button
          class="submit-btn"
          :disabled="loading"
          @click="handleSubmit"
        >
          <view v-if="loading" class="loading-spinner">
            <uni-icons type="spinner-cycle" size="24" color="#fff" class="loading-icon"></uni-icons>
          </view>
          <text v-else class="submit-text">提交反馈</text>
        </button>
        
        <!-- 隐私政策提示 -->
        <view class="privacy-tip">
          <text class="privacy-text">提交即表示您同意我们的</text>
          <text class="privacy-link" @click="handlePrivacyPolicy">隐私政策</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { post } from '@/utils/request'

export default {
  data() {
    return {
      // 加载状态
      loading: false,
      // 最大图片数量
      maxImages: 3,
      // 表单数据
      form: {
        type: 'SUGGESTION',
        content: '',
        contact: '',
        images: []
      },
      // 错误信息
      errors: {
        content: '',
        contact: ''
      }
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
    
    // 反馈类型变化处理
    handleTypeChange(e) {
      this.form.type = e.detail.value
    },
    
    // 表单验证
    validateForm() {
      let isValid = true
      
      // 重置错误信息
      this.errors = {
        content: '',
        contact: ''
      }
      
      // 验证反馈内容
      if (!this.form.content.trim()) {
        this.errors.content = '反馈内容不能为空'
        isValid = false
      } else if (this.form.content.trim().length < 10) {
        this.errors.content = '反馈内容至少10个字符'
        isValid = false
      }
      
      // 验证联系方式
      if (this.form.contact) {
        const phoneRegex = /^1[3-9]\d{9}$/
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        if (!phoneRegex.test(this.form.contact) && !emailRegex.test(this.form.contact)) {
          this.errors.contact = '请输入正确的手机号或邮箱'
          isValid = false
        }
      }
      
      return isValid
    },
    
    // 图片上传
    handleImageUpload() {
      uni.chooseImage({
        count: this.maxImages - this.form.images.length,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          // 将选择的图片添加到表单中
          this.form.images = [...this.form.images, ...res.tempFilePaths]
        },
        fail: (err) => {
          console.error('选择图片失败', err)
        }
      })
    },
    
    // 删除图片
    handleDeleteImage(index) {
      this.form.images.splice(index, 1)
    },
    
    // 提交反馈
    handleSubmit() {
      // 表单验证
      if (!this.validateForm()) {
        return
      }
      
      this.loading = true
      
      // 调用提交反馈API
      post('/api/feedback', {
        type: this.form.type,
        content: this.form.content,
        contact: this.form.contact,
        images: this.form.images
      })
        .then(res => {
          if (res.code === 200) {
            uni.showToast({
              title: '反馈提交成功，感谢您的支持',
              icon: 'success',
              duration: 2500,
              success: () => {
                // 返回上一页
                setTimeout(() => {
                  this.handleBack()
                }, 2500)
              }
            })
          } else {
            uni.showToast({
              title: res.msg || res.message || '反馈提交失败，请稍后重试',
              icon: 'none'
            })
          }
        })
        .catch(err => {
          console.error('提交反馈失败', err)
          uni.showToast({
            title: '网络错误，请稍后重试',
            icon: 'none'
          })
        })
        .finally(() => {
          this.loading = false
        })
    },
    
    // 查看隐私政策
    handlePrivacyPolicy() {
      // 这里可以跳转到隐私政策页面
      uni.showToast({
        title: '隐私政策功能开发中',
        icon: 'none'
      })
    }
  }
}
</script>

<style scoped>
.feedback-container {
  background: #f5f7fa;
  min-height: 100vh;
}

/* 头部导航 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 88rpx;
  background: white;
  padding: 0 20rpx;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left, .header-right {
  display: flex;
  align-items: center;
}

.back-btn {
  background: none;
  border: none;
  padding: 10rpx;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: rgba(0, 0, 0, 0.05);
  transform: scale(1.1);
}

.header-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

/* 内容容器 */
.content-container {
  padding: 20rpx;
}

/* 反馈表单 */
.feedback-form {
  background: white;
  border-radius: 16rpx;
  padding: 20rpx;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

/* 表单项 */
.form-item {
  margin-bottom: 30rpx;
}

.form-label {
  display: flex;
  align-items: center;
  margin-bottom: 15rpx;
}

.label-text {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.required {
  color: #ff4757;
  margin-left: 5rpx;
  font-size: 32rpx;
}

.optional {
  color: #999;
  margin-left: 5rpx;
  font-size: 26rpx;
}

.form-control {
  position: relative;
  background: #f8f9fa;
  border-radius: 12rpx;
  transition: all 0.3s ease;
  border: 2rpx solid transparent;
}

.form-control:focus-within {
  background: white;
  border-color: #667eea;
  box-shadow: 0 0 0 8rpx rgba(102, 126, 234, 0.1);
}

/* 文本域 */
.textarea-control {
  padding: 15rpx 20rpx;
}

.textarea-field {
  width: 100%;
  font-size: 28rpx;
  color: #333;
  background: transparent;
  border: none;
  outline: none;
  line-height: 1.5;
  font-family: inherit;
}

.textarea-field::placeholder {
  color: #999;
}

/* 输入框 */
.input-field {
  width: 100%;
  height: 80rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  color: #333;
  background: transparent;
  border: none;
  outline: none;
}

.input-field::placeholder {
  color: #999;
}

/* 单选组 */
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

/* 图片上传 */
.image-upload-container {
  display: flex;
  flex-direction: column;
  gap: 15rpx;
}

.uploaded-images {
  display: flex;
  flex-wrap: wrap;
  gap: 15rpx;
  margin-bottom: 10rpx;
}

.image-item {
  position: relative;
  width: 160rpx;
  height: 160rpx;
  border-radius: 12rpx;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.uploaded-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.delete-image-btn {
  position: absolute;
  top: 5rpx;
  right: 5rpx;
  background: rgba(0, 0, 0, 0.6);
  border: none;
  border-radius: 50%;
  width: 36rpx;
  height: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  z-index: 10;
}

.delete-image-btn:hover {
  background: rgba(255, 71, 87, 0.8);
  transform: scale(1.1);
}

.upload-btn {
  width: 160rpx;
  height: 160rpx;
  background: #f8f9fa;
  border: 2rpx dashed #ddd;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  cursor: pointer;
}

.upload-btn:hover:not(:disabled) {
  background: #f0f4ff;
  border-color: #667eea;
  transform: scale(1.05);
}

.upload-btn:disabled {
  opacity: 0.6;
  transform: none;
  cursor: not-allowed;
}

.upload-btn-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5rpx;
}

.upload-text {
  font-size: 24rpx;
  color: #999;
  margin-top: 5rpx;
}

.upload-subtext {
  font-size: 20rpx;
  color: #bbb;
}

.upload-tip {
  margin-top: 10rpx;
}

.tip-text {
  font-size: 22rpx;
  color: #999;
  line-height: 1.4;
}

/* 提交按钮 */
.submit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12rpx;
  height: 90rpx;
  width: 100%;
  font-size: 32rpx;
  font-weight: bold;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.3s ease;
  margin-top: 20rpx;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2rpx);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
}

.submit-btn:disabled {
  opacity: 0.6;
  transform: none;
  box-shadow: none;
}

.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-icon {
  animation: rotate 1s linear infinite;
}

.submit-text {
  font-size: 32rpx;
  font-weight: bold;
}

/* 隐私政策提示 */
.privacy-tip {
  margin-top: 25rpx;
  text-align: center;
  padding: 20rpx;
}

.privacy-text {
  font-size: 24rpx;
  color: #999;
}

.privacy-link {
  font-size: 24rpx;
  color: #667eea;
  text-decoration: underline;
  margin-left: 5rpx;
  transition: all 0.3s ease;
}

.privacy-link:hover {
  color: #764ba2;
  transform: scale(1.05);
}

/* 错误信息 */
.error-message {
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #ff4757;
  padding: 0 5rpx;
}

/* 动画 */
@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 响应式设计 */
@media (max-width: 375px) {
  .image-item,
  .upload-btn {
    width: 140rpx;
    height: 140rpx;
  }
  
  .uploaded-images {
    gap: 10rpx;
  }
}
</style>