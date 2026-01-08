<template>
  <div class="login-container">
    <!-- 人口普查主题背景动画 -->
    <div class="background-animation">
      <!-- 人口统计图表背景元素 -->
      <div class="census-charts">
        <div class="chart-element chart-1">
          <div class="bar bar-1"></div>
          <div class="bar bar-2"></div>
          <div class="bar bar-3"></div>
          <div class="bar bar-4"></div>
        </div>
        <div class="chart-element chart-2">
          <div class="pie-segment segment-1"></div>
          <div class="pie-segment segment-2"></div>
          <div class="pie-segment segment-3"></div>
        </div>
      </div>
      
      <!-- 浮动的人口和家庭图标 -->
      <div class="floating-icons">
        <div class="icon-group home-group">
          <svg class="census-icon home-icon" viewBox="0 0 24 24" fill="currentColor">
            <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/>
          </svg>
          <svg class="census-icon home-icon" viewBox="0 0 24 24" fill="currentColor">
            <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/>
          </svg>
        </div>
        <div class="icon-group people-group">
          <svg class="census-icon people-icon" viewBox="0 0 24 24" fill="currentColor">
            <path d="M16 4c0-1.11.89-2 2-2s2 .89 2 2-.89 2-2 2-2-.89-2-2zm4 18v-6h2.5l-2.54-7.63A1.5 1.5 0 0 0 18.5 8h-1c-.8 0-1.5.7-1.5 1.5v15.5h5zM12.5 11.5c.83 0 1.5-.67 1.5-1.5s-.67-1.5-1.5-1.5S11 9.17 11 10s.67 1.5 1.5 1.5zM5.5 6c1.11 0 2-.89 2-2s-.89-2-2-2-2 .89-2 2 .89 2 2 2zm2 16v-7H6c-.8 0-1.5.7-1.5 1.5v15.5h3zM12 2C8.13 2 5 5.13 5 9c0 2.38 1.19 4.47 3 5.74V17c0 .55.45 1 1 1h6c.55 0 1-.45 1-1v-2.26c1.81-1.27 3-3.36 3-5.74 0-3.87-3.13-7-7-7z"/>
          </svg>
          <svg class="census-icon people-icon" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
          </svg>
        </div>
      </div>
      
      <!-- 地理区域图案 -->
      <div class="geo-pattern">
        <div class="region region-1"></div>
        <div class="region region-2"></div>
        <div class="region region-3"></div>
      </div>
    </div>
    
    <div class="login-form-wrapper">
      <div class="login-form">
        <!-- 头部区域 - 人口普查主题设计 -->
        <div class="login-header">
          <div class="logo-container">
            <div class="census-logo">
              <!-- 自定义人口普查logo -->
              <div class="logo-main">
                <svg class="census-logo-svg" viewBox="0 0 100 100" fill="none">
                  <!-- 外圆环 -->
                  <circle cx="50" cy="50" r="45" stroke="currentColor" stroke-width="3" fill="none" opacity="0.3"/>
                  <!-- 人口统计柱状图 -->
                  <rect x="15" y="60" width="8" height="25" rx="2" fill="currentColor"/>
                  <rect x="30" y="50" width="8" height="35" rx="2" fill="currentColor"/>
                  <rect x="45" y="40" width="8" height="45" rx="2" fill="currentColor"/>
                  <rect x="60" y="45" width="8" height="40" rx="2" fill="currentColor"/>
                  <rect x="75" y="55" width="8" height="30" rx="2" fill="currentColor"/>
                  <!-- 中心人群图标 -->
                  <circle cx="50" cy="35" r="6" fill="currentColor"/>
                  <circle cx="42" cy="35" r="5" fill="currentColor" opacity="0.8"/>
                  <circle cx="58" cy="35" r="5" fill="currentColor" opacity="0.8"/>
                </svg>
              </div>
            </div>
          </div>
          <h2 class="title">
            <span class="title-cn">人口普查管理系统</span>
          </h2>
          <p class="subtitle">Population Census Management System</p>
          <div class="census-info">
            <div class="info-item">
              <span class="info-icon">👥</span>
              <span class="info-text">精准统计</span>
            </div>
            <div class="info-item">
              <span class="info-icon">📊</span>
              <span class="info-text">数据分析</span>
            </div>
            <div class="info-item">
              <span class="info-icon">🏘️</span>
              <span class="info-text">数据管理</span>
            </div>
          </div>
        </div>
        
        <!-- 表单切换标签 -->
        <div class="form-tabs" v-if="currentForm !== 'reset-password'">
          <div 
            class="form-tab" 
            :class="{ 'active': currentForm === 'login' }"
            @click="switchForm('login')"
          >
            登录
          </div>
          <div 
            class="form-tab" 
            :class="{ 'active': currentForm === 'register' }"
            @click="switchForm('register')"
          >
            注册
          </div>
        </div>
        
        <!-- 登录表单 -->
        <el-form 
          v-if="currentForm === 'login'"
          ref="loginForm" 
          :model="loginForm" 
          :rules="loginRules" 
          class="login-form-content"
          @keyup.enter.native="handleLogin"
        >
          <!-- 用户名输入框 -->
          <el-form-item prop="username" class="form-item-custom">
            <div class="uiverse-input-container">
              <div class="uiverse-input-wrapper" :class="{ 'uiverse-focused': inputFocused.username, 'uiverse-has-value': loginForm.username, 'uiverse-has-error': usernameError }">
                <div class="uiverse-input-icon">
                  <svg v-if="!usernameLoading && !usernameSuccess" class="uiverse-uiverse-input-icon-svg" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                  </svg>
                  <div v-else-if="usernameLoading" class="uiverse-input-loading-spinner"></div>
                  <svg v-else-if="usernameSuccess" class="uiverse-input-success-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                  </svg>
                </div>
                <input
                  v-model="loginForm.username"
                  type="text"
                  class="uiverse-input"
                  placeholder=" "
                  @focus="handleInputFocus('username')"
                  @blur="handleInputBlur('username')"
                  @input="validateUsername"
                />
                <label class="floating-label">用户名</label>
                <div class="input-progress"></div>
              </div>
              <div class="uiverse-input-feedback" v-if="usernameError">
                <span class="uiverse-error-text">{{ usernameError }}</span>
              </div>
            </div>
          </el-form-item>
          
          <!-- 密码输入框 -->
          <el-form-item prop="password" class="form-item-custom">
            <div class="uiverse-input-container">
              <div class="uiverse-input-wrapper" :class="{ 'uiverse-focused': inputFocused.password, 'uiverse-has-value': loginForm.password, 'uiverse-has-error': passwordError }">
                <div class="uiverse-input-icon">
                  <svg v-if="!passwordLoading && !passwordSuccess" class="uiverse-uiverse-input-icon-svg" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M18,8h-1V6c0-2.76-2.24-5-5-5S7,3.24,7,6v2H6c-1.1,0-2,0.9-2,2v10c0,1.1,0.9,2,2,2h12c1.1,0,2-0.9,2-2V10C20,8.9,19.1,8,18,8z M9,6c0-1.66,1.34-3,3-3s3,1.34,3,3v2H9V6z M18,20H6V10h12V20z M12,17c1.1,0,2-0.9,2-2s-0.9-2-2-2s-2,0.9-2,2S10.9,17,12,17z M12,14c1.1,0,2-0.9,2-2s-0.9-2-2-2s-2,0.9-2,2S10.9,14,12,14z M12,11c-1.1,0-2,0.9-2,2s0.9,2,2,2s2-0.9,2-2S13.1,11,12,11z M12,8c-1.1,0-2,0.9-2,2s0.9,2,2,2s2-0.9,2-2S13.1,8,12,8z"/>
                  </svg>
                  <div v-else-if="passwordLoading" class="uiverse-input-loading-spinner"></div>
                  <svg v-else-if="passwordSuccess" class="uiverse-input-success-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                  </svg>
                </div>
                <input
                  v-model="loginForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  class="uiverse-input"
                  placeholder=" "
                  @focus="handleInputFocus('password')"
                  @blur="handleInputBlur('password')"
                  @input="validatePassword"
                />
                <label class="floating-label">密码</label>
                <div class="input-progress"></div>
                <button 
                  type="button" 
                  class="uiverse-password-toggle"
                  @click="togglePasswordVisibility"
                >
                  <svg v-if="!showPassword" class="uiverse-toggle-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                  </svg>
                  <svg v-else class="uiverse-toggle-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zm2.76 9.54l2.17 2.17c-.43.51-.9.96-1.41 1.33-1.73 1.26-4.01 2.16-6.52 2.16-2.76 0-5-2.24-5-5 0-2.52.9-4.79 2.16-6.52.37-.51.82-.98 1.33-1.41l2.17 2.17C14.63 11.36 13.34 12 12 12c-1.66 0-3-1.34-3-3 0-.74.26-1.42.7-1.96L3 3l1.96-.7C5.58 1.26 8.3 1 12 1c3.89 0 7.29 1.26 10.04 3.34L20.33 6.43C17.58 4.26 14.96 3 12 3 6.48 3 2.39 6.92 1.59 12c.8 5.11 4.89 9 10.41 9 2.96 0 5.58-1.26 7.33-3.43l-3.17-3.17C14.42 16.74 13.14 17 12 17z"/>
                  </svg>
                </button>
              </div>

              <div class="uiverse-input-feedback" v-if="passwordError">
                <span class="uiverse-error-text">{{ passwordError }}</span>
              </div>
            </div>
          </el-form-item>
          
          <!-- 权限选择 -->
          <el-form-item prop="role" class="form-item-custom">
            <div class="permission-selector">
              <label class="permission-label">选择权限角色</label>
              <div class="permission-options">
                <div 
                  class="permission-option" 
                  :class="{ 'active': loginForm.role === 'ADMIN' }"
                  @click="loginForm.role = 'ADMIN'"
                >
                  <div class="permission-radio"></div>
                  <span class="permission-text">管理员</span>
                </div>
                <div 
                  class="permission-option" 
                  :class="{ 'active': loginForm.role === 'USER' }"
                  @click="loginForm.role = 'USER'"
                >
                  <div class="permission-radio"></div>
                  <span class="permission-text">普通用户</span>
                </div>
              </div>
            </div>
          </el-form-item>
          
          <el-form-item class="form-item-custom">
            <div class="options-row">
              <el-checkbox v-model="rememberMe" class="remember-checkbox">
                记住我
              </el-checkbox>
              <span class="forgot-password" @click="switchForm('forgot-password')">忘记密码？</span>
            </div>
          </el-form-item>
          
          <el-form-item class="form-item-custom">
            <button 
              type="button"
              class="uiverse-button" 
              :class="{ 'loading': loading }"
              :disabled="loading"
              @click="handleLogin"
            >
              <span v-if="!loading" class="uiverse-button-text">
                <svg class="uiverse-button-icon" viewBox="0 0 24 30" fill="currentColor">
                  <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                </svg>
                登录
              </span>
              <span v-else class="uiverse-button-loading">
                <span class="uiverse-loading-spinner"></span>
                登录中...
              </span>
            </button>
          </el-form-item>
        </el-form>
        
        <!-- 注册表单 -->
        <el-form 
          v-else-if="currentForm === 'register'"
          ref="registerForm" 
          :model="registerForm" 
          :rules="registerRules" 
          class="login-form-content"
          @keyup.enter.native="handleRegister"
        >
          <!-- 用户名输入框 -->
          <el-form-item prop="username" class="form-item-custom">
            <div class="uiverse-input-container">
              <div class="uiverse-input-wrapper" :class="{ 'uiverse-focused': inputFocused.registerUsername, 'uiverse-has-value': registerForm.username, 'uiverse-has-error': registerUsernameError }">
                <div class="uiverse-input-icon">
                  <svg v-if="!registerUsernameLoading && !registerUsernameSuccess" class="uiverse-uiverse-input-icon-svg" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                  </svg>
                  <div v-else-if="registerUsernameLoading" class="uiverse-input-loading-spinner"></div>
                  <svg v-else-if="registerUsernameSuccess" class="uiverse-input-success-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                  </svg>
                </div>
                <input
                  v-model="registerForm.username"
                  type="text"
                  class="uiverse-input"
                  placeholder=" "
                  @focus="handleInputFocus('registerUsername')"
                  @blur="handleInputBlur('registerUsername')"
                  @input="validateRegisterUsername"
                />
                <label class="floating-label">用户名</label>
                <div class="input-progress"></div>
              </div>
              <div class="uiverse-input-feedback" v-if="registerUsernameError">
                <span class="uiverse-error-text">{{ registerUsernameError }}</span>
              </div>
            </div>
          </el-form-item>
          
          <!-- 身份证号输入框 -->
          <el-form-item prop="idCard" class="form-item-custom">
            <div class="uiverse-input-container">
              <div class="uiverse-input-wrapper" :class="{ 'uiverse-focused': inputFocused.idCard, 'uiverse-has-value': registerForm.idCard, 'uiverse-has-error': idCardError }">
                <div class="uiverse-input-icon">
                  <svg v-if="!idCardLoading && !idCardSuccess" class="uiverse-uiverse-input-icon-svg" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V5h14v14zm-7-2h5v-2h-5v2zm0-4h5v-2h-5v2zm-3-4h8V7H9v2z"/>
                  </svg>
                  <div v-else-if="idCardLoading" class="uiverse-input-loading-spinner"></div>
                  <svg v-else-if="idCardSuccess" class="uiverse-input-success-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                  </svg>
                </div>
                <input
                  v-model="registerForm.idCard"
                  type="text"
                  class="uiverse-input"
                  placeholder=" "
                  @focus="handleInputFocus('idCard')"
                  @blur="handleInputBlur('idCard')"
                  @input="validateIdCard"
                />
                <label class="floating-label">身份证号</label>
                <div class="input-progress"></div>
              </div>
              <div class="uiverse-input-feedback" v-if="idCardError">
                <span class="uiverse-error-text">{{ idCardError }}</span>
              </div>
            </div>
          </el-form-item>
          
          <!-- 密码输入框 -->
          <el-form-item prop="password" class="form-item-custom">
            <div class="uiverse-input-container">
              <div class="uiverse-input-wrapper" :class="{ 'uiverse-focused': inputFocused.registerPassword, 'uiverse-has-value': registerForm.password, 'uiverse-has-error': registerPasswordError }">
                <div class="uiverse-input-icon">
                  <svg v-if="!registerPasswordLoading && !registerPasswordSuccess" class="uiverse-uiverse-input-icon-svg" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M18,8h-1V6c0-2.76-2.24-5-5-5S7,3.24,7,6v2H6c-1.1,0-2,0.9-2,2v10c0,1.1,0.9,2,2,2h12c1.1,0,2-0.9,2-2V10C20,8.9,19.1,8,18,8z M9,6c0-1.66,1.34-3,3-3s3,1.34,3,3v2H9V6z M18,20H6V10h12V20z M12,17c1.1,0,2-0.9,2-2s-0.9-2-2-2s-2,0.9-2,2S10.9,17,12,17z M12,14c1.1,0,2-0.9,2-2s-0.9-2-2-2s-2,0.9-2,2S10.9,14,12,14z M12,11c-1.1,0-2,0.9-2,2s0.9,2,2,2s2-0.9,2-2S13.1,11,12,11z M12,8c-1.1,0-2,0.9-2,2s0.9,2,2,2s2-0.9,2-2S13.1,8,12,8z"/>
                  </svg>
                  <div v-else-if="registerPasswordLoading" class="uiverse-input-loading-spinner"></div>
                  <svg v-else-if="registerPasswordSuccess" class="uiverse-input-success-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                  </svg>
                </div>
                <input
                  v-model="registerForm.password"
                  :type="showRegisterPassword ? 'text' : 'password'"
                  class="uiverse-input"
                  placeholder=" "
                  @focus="handleInputFocus('registerPassword')"
                  @blur="handleInputBlur('registerPassword')"
                  @input="validateRegisterPassword"
                />
                <label class="floating-label">密码</label>
                <div class="input-progress"></div>
                <button 
                  type="button" 
                  class="uiverse-password-toggle"
                  @click="toggleRegisterPasswordVisibility"
                >
                  <svg v-if="!showRegisterPassword" class="uiverse-toggle-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                  </svg>
                  <svg v-else class="uiverse-toggle-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zm2.76 9.54l2.17 2.17c-.43.51-.9.96-1.41 1.33-1.73 1.26-4.01 2.16-6.52 2.16-2.76 0-5-2.24-5-5 0-2.52.9-4.79 2.16-6.52.37-.51.82-.98 1.33-1.41l2.17 2.17C14.63 11.36 13.34 12 12 12c-1.66 0-3-1.34-3-3 0-.74.26-1.42.7-1.96L3 3l1.96-.7C5.58 1.26 8.3 1 12 1c3.89 0 7.29 1.26 10.04 3.34L20.33 6.43C17.58 4.26 14.96 3 12 3 6.48 3 2.39 6.92 1.59 12c.8 5.11 4.89 9 10.41 9 2.96 0 5.58-1.26 7.33-3.43l-3.17-3.17C14.42 16.74 13.14 17 12 17z"/>
                  </svg>
                </button>
              </div>
              <div class="uiverse-input-feedback" v-if="registerPasswordError">
                <span class="uiverse-error-text">{{ registerPasswordError }}</span>
              </div>
            </div>
          </el-form-item>
          
          <!-- 确认密码输入框 -->
          <el-form-item prop="confirmPassword" class="form-item-custom">
            <div class="uiverse-input-container">
              <div class="uiverse-input-wrapper" :class="{ 'uiverse-focused': inputFocused.confirmPassword, 'uiverse-has-value': registerForm.confirmPassword, 'uiverse-has-error': confirmPasswordError }">
                <div class="uiverse-input-icon">
                  <svg v-if="!confirmPasswordLoading && !confirmPasswordSuccess" class="uiverse-uiverse-input-icon-svg" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M18,8h-1V6c0-2.76-2.24-5-5-5S7,3.24,7,6v2H6c-1.1,0-2,0.9-2,2v10c0,1.1,0.9,2,2,2h12c1.1,0,2-0.9,2-2V10C20,8.9,19.1,8,18,8z M9,6c0-1.66,1.34-3,3-3s3,1.34,3,3v2H9V6z M18,20H6V10h12V20z M12,17c1.1,0,2-0.9,2-2s-0.9-2-2-2s-2,0.9-2,2S10.9,17,12,17z M12,14c1.1,0,2-0.9,2-2s-0.9-2-2-2s-2,0.9-2,2S10.9,14,12,14z M12,11c-1.1,0-2,0.9-2,2s0.9,2,2,2s2-0.9,2-2S13.1,11,12,11z M12,8c-1.1,0-2,0.9-2,2s0.9,2,2,2s2-0.9,2-2S13.1,8,12,8z"/>
                  </svg>
                  <div v-else-if="confirmPasswordLoading" class="uiverse-input-loading-spinner"></div>
                  <svg v-else-if="confirmPasswordSuccess" class="uiverse-input-success-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                  </svg>
                </div>
                <input
                  v-model="registerForm.confirmPassword"
                  :type="showRegisterPassword ? 'text' : 'password'"
                  class="uiverse-input"
                  placeholder=" "
                  @focus="handleInputFocus('confirmPassword')"
                  @blur="handleInputBlur('confirmPassword')"
                  @input="validateConfirmPassword"
                />
                <label class="floating-label">确认密码</label>
                <div class="input-progress"></div>
                <button 
                  type="button" 
                  class="uiverse-password-toggle"
                  @click="toggleRegisterPasswordVisibility"
                >
                  <svg v-if="!showRegisterPassword" class="uiverse-toggle-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                  </svg>
                  <svg v-else class="uiverse-toggle-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zm2.76 9.54l2.17 2.17c-.43.51-.9.96-1.41 1.33-1.73 1.26-4.01 2.16-6.52 2.16-2.76 0-5-2.24-5-5 0-2.52.9-4.79 2.16-6.52.37-.51.82-.98 1.33-1.41l2.17 2.17C14.63 11.36 13.34 12 12 12c-1.66 0-3-1.34-3-3 0-.74.26-1.42.7-1.96L3 3l1.96-.7C5.58 1.26 8.3 1 12 1c3.89 0 7.29 1.26 10.04 3.34L20.33 6.43C17.58 4.26 14.96 3 12 3 6.48 3 2.39 6.92 1.59 12c.8 5.11 4.89 9 10.41 9 2.96 0 5.58-1.26 7.33-3.43l-3.17-3.17C14.42 16.74 13.14 17 12 17z"/>
                  </svg>
                </button>
              </div>
              <div class="uiverse-input-feedback" v-if="confirmPasswordError">
                <span class="uiverse-error-text">{{ confirmPasswordError }}</span>
              </div>
            </div>
          </el-form-item>
          
          <!-- 权限选择 -->
          <el-form-item prop="role" class="form-item-custom">
            <div class="permission-selector">
              <label class="permission-label">选择权限角色</label>
              <div class="permission-options">
                <div 
                  class="permission-option" 
                  :class="{ 'active': registerForm.role === 'ADMIN' }"
                  @click="registerForm.role = 'ADMIN'"
                >
                  <div class="permission-radio"></div>
                  <span class="permission-text">管理员</span>
                </div>
                <div 
                  class="permission-option" 
                  :class="{ 'active': registerForm.role === 'USER' }"
                  @click="registerForm.role = 'USER'"
                >
                  <div class="permission-radio"></div>
                  <span class="permission-text">普通用户</span>
                </div>
              </div>
            </div>
          </el-form-item>
          
          <el-form-item class="form-item-custom">
            <button 
              type="button"
              class="uiverse-button" 
              :class="{ 'loading': loading }"
              :disabled="loading"
              @click="handleRegister"
            >
              <span v-if="!loading" class="uiverse-button-text">
                <svg class="uiverse-button-icon" viewBox="0 0 24 30" fill="currentColor">
                  <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                </svg>
                注册
              </span>
              <span v-else class="uiverse-button-loading">
                <span class="uiverse-loading-spinner"></span>
                注册中...
              </span>
            </button>
          </el-form-item>
        </el-form>
        
        <!-- 忘记密码表单 -->
        <el-form 
          v-else-if="currentForm === 'forgot-password'"
          ref="forgotPasswordForm" 
          :model="forgotPasswordForm" 
          :rules="forgotPasswordRules" 
          class="login-form-content"
          @keyup.enter.native="handleForgotPassword"
        >
          <!-- 用户名输入框 -->
          <el-form-item prop="username" class="form-item-custom">
            <div class="uiverse-input-container">
              <div class="uiverse-input-wrapper" :class="{ 'uiverse-focused': inputFocused.forgotUsername, 'uiverse-has-value': forgotPasswordForm.username, 'uiverse-has-error': forgotUsernameError }">
                <div class="uiverse-input-icon">
                  <svg v-if="!forgotUsernameLoading && !forgotUsernameSuccess" class="uiverse-uiverse-input-icon-svg" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                  </svg>
                  <div v-else-if="forgotUsernameLoading" class="uiverse-input-loading-spinner"></div>
                  <svg v-else-if="forgotUsernameSuccess" class="uiverse-input-success-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                  </svg>
                </div>
                <input
                  v-model="forgotPasswordForm.username"
                  type="text"
                  class="uiverse-input"
                  placeholder=" "
                  @focus="handleInputFocus('forgotUsername')"
                  @blur="handleInputBlur('forgotUsername')"
                  @input="validateForgotUsername"
                />
                <label class="floating-label">用户名</label>
                <div class="input-progress"></div>
              </div>
              <div class="uiverse-input-feedback" v-if="forgotUsernameError">
                <span class="uiverse-error-text">{{ forgotUsernameError }}</span>
              </div>
            </div>
          </el-form-item>
          
          <!-- 身份证号输入框 -->
          <el-form-item prop="idCard" class="form-item-custom">
            <div class="uiverse-input-container">
              <div class="uiverse-input-wrapper" :class="{ 'uiverse-focused': inputFocused.forgotIdCard, 'uiverse-has-value': forgotPasswordForm.idCard, 'uiverse-has-error': forgotIdCardError }">
                <div class="uiverse-input-icon">
                  <svg v-if="!forgotIdCardLoading && !forgotIdCardSuccess" class="uiverse-uiverse-input-icon-svg" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V5h14v14zm-7-2h5v-2h-5v2zm0-4h5v-2h-5v2zm-3-4h8V7H9v2z"/>
                  </svg>
                  <div v-else-if="forgotIdCardLoading" class="uiverse-input-loading-spinner"></div>
                  <svg v-else-if="forgotIdCardSuccess" class="uiverse-input-success-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                  </svg>
                </div>
                <input
                  v-model="forgotPasswordForm.idCard"
                  type="text"
                  class="uiverse-input"
                  placeholder=" "
                  @focus="handleInputFocus('forgotIdCard')"
                  @blur="handleInputBlur('forgotIdCard')"
                  @input="validateForgotIdCard"
                />
                <label class="floating-label">身份证号</label>
                <div class="input-progress"></div>
              </div>
              <div class="uiverse-input-feedback" v-if="forgotIdCardError">
                <span class="uiverse-error-text">{{ forgotIdCardError }}</span>
              </div>
            </div>
          </el-form-item>
          
          <el-form-item class="form-item-custom">
            <button 
              type="button"
              class="uiverse-button" 
              :class="{ 'loading': loading }"
              :disabled="loading"
              @click="handleForgotPassword"
            >
              <span v-if="!loading" class="uiverse-button-text">
                <svg class="uiverse-button-icon" viewBox="0 0 24 30" fill="currentColor">
                  <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                </svg>
                验证身份
              </span>
              <span v-else class="uiverse-button-loading">
                <span class="uiverse-loading-spinner"></span>
                验证中...
              </span>
            </button>
          </el-form-item>
        </el-form>
        
        <!-- 密码重置表单 -->
        <el-form 
          v-else-if="currentForm === 'reset-password'"
          ref="resetPasswordForm" 
          :model="resetPasswordForm" 
          :rules="resetPasswordRules" 
          class="login-form-content"
          @keyup.enter.native="handleResetPassword"
        >
          <!-- 新密码输入框 -->
          <el-form-item prop="newPassword" class="form-item-custom">
            <div class="uiverse-input-container">
              <div class="uiverse-input-wrapper" :class="{ 'uiverse-focused': inputFocused.newPassword, 'uiverse-has-value': resetPasswordForm.newPassword, 'uiverse-has-error': newPasswordError }">
                <div class="uiverse-input-icon">
                  <svg v-if="!newPasswordLoading && !newPasswordSuccess" class="uiverse-uiverse-input-icon-svg" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M18,8h-1V6c0-2.76-2.24-5-5-5S7,3.24,7,6v2H6c-1.1,0-2,0.9-2,2v10c0,1.1,0.9,2,2,2h12c1.1,0,2-0.9,2-2V10C20,8.9,19.1,8,18,8z M9,6c0-1.66,1.34-3,3-3s3,1.34,3,3v2H9V6z M18,20H6V10h12V20z M12,17c1.1,0,2-0.9,2-2s-0.9-2-2-2s-2,0.9-2,2S10.9,17,12,17z M12,14c1.1,0,2-0.9,2-2s-0.9-2-2-2s-2,0.9-2,2S10.9,14,12,14z M12,11c-1.1,0-2,0.9-2,2s0.9,2,2,2s2-0.9,2-2S13.1,11,12,11z M12,8c-1.1,0-2,0.9-2,2s0.9,2,2,2s2-0.9,2-2S13.1,8,12,8z"/>
                  </svg>
                  <div v-else-if="newPasswordLoading" class="uiverse-input-loading-spinner"></div>
                  <svg v-else-if="newPasswordSuccess" class="uiverse-input-success-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                  </svg>
                </div>
                <input
                  v-model="resetPasswordForm.newPassword"
                  :type="showResetPassword ? 'text' : 'password'"
                  class="uiverse-input"
                  placeholder=" "
                  @focus="handleInputFocus('newPassword')"
                  @blur="handleInputBlur('newPassword')"
                  @input="validateNewPassword"
                />
                <label class="floating-label">新密码</label>
                <div class="input-progress"></div>
                <button 
                  type="button" 
                  class="uiverse-password-toggle"
                  @click="toggleResetPasswordVisibility"
                >
                  <svg v-if="!showResetPassword" class="uiverse-toggle-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                  </svg>
                  <svg v-else class="uiverse-toggle-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zm2.76 9.54l2.17 2.17c-.43.51-.9.96-1.41 1.33-1.73 1.26-4.01 2.16-6.52 2.16-2.76 0-5-2.24-5-5 0-2.52.9-4.79 2.16-6.52.37-.51.82-.98 1.33-1.41l2.17 2.17C14.63 11.36 13.34 12 12 12c-1.66 0-3-1.34-3-3 0-.74.26-1.42.7-1.96L3 3l1.96-.7C5.58 1.26 8.3 1 12 1c3.89 0 7.29 1.26 10.04 3.34L20.33 6.43C17.58 4.26 14.96 3 12 3 6.48 3 2.39 6.92 1.59 12c.8 5.11 4.89 9 10.41 9 2.96 0 5.58-1.26 7.33-3.43l-3.17-3.17C14.42 16.74 13.14 17 12 17z"/>
                  </svg>
                </button>
              </div>
              <div class="uiverse-input-feedback" v-if="newPasswordError">
                <span class="uiverse-error-text">{{ newPasswordError }}</span>
              </div>
            </div>
          </el-form-item>
          
          <!-- 确认新密码输入框 -->
          <el-form-item prop="confirmPassword" class="form-item-custom">
            <div class="uiverse-input-container">
              <div class="uiverse-input-wrapper" :class="{ 'uiverse-focused': inputFocused.confirmNewPassword, 'uiverse-has-value': resetPasswordForm.confirmPassword, 'uiverse-has-error': confirmNewPasswordError }">
                <div class="uiverse-input-icon">
                  <svg v-if="!confirmNewPasswordLoading && !confirmNewPasswordSuccess" class="uiverse-uiverse-input-icon-svg" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M18,8h-1V6c0-2.76-2.24-5-5-5S7,3.24,7,6v2H6c-1.1,0-2,0.9-2,2v10c0,1.1,0.9,2,2,2h12c1.1,0,2-0.9,2-2V10C20,8.9,19.1,8,18,8z M9,6c0-1.66,1.34-3,3-3s3,1.34,3,3v2H9V6z M18,20H6V10h12V20z M12,17c1.1,0,2-0.9,2-2s-0.9-2-2-2s-2,0.9-2,2S10.9,17,12,17z M12,14c1.1,0,2-0.9,2-2s-0.9-2-2-2s-2,0.9-2,2S10.9,14,12,14z M12,11c-1.1,0-2,0.9-2,2s0.9,2,2,2s2-0.9,2-2S13.1,11,12,11z M12,8c-1.1,0-2,0.9-2,2s0.9,2,2,2s2-0.9,2-2S13.1,8,12,8z"/>
                  </svg>
                  <div v-else-if="confirmNewPasswordLoading" class="uiverse-input-loading-spinner"></div>
                  <svg v-else-if="confirmNewPasswordSuccess" class="uiverse-input-success-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                  </svg>
                </div>
                <input
                  v-model="resetPasswordForm.confirmPassword"
                  :type="showResetPassword ? 'text' : 'password'"
                  class="uiverse-input"
                  placeholder=" "
                  @focus="handleInputFocus('confirmNewPassword')"
                  @blur="handleInputBlur('confirmNewPassword')"
                  @input="validateConfirmNewPassword"
                />
                <label class="floating-label">确认新密码</label>
                <div class="input-progress"></div>
                <button 
                  type="button" 
                  class="uiverse-password-toggle"
                  @click="toggleResetPasswordVisibility"
                >
                  <svg v-if="!showResetPassword" class="uiverse-toggle-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                  </svg>
                  <svg v-else class="uiverse-toggle-icon" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zm2.76 9.54l2.17 2.17c-.43.51-.9.96-1.41 1.33-1.73 1.26-4.01 2.16-6.52 2.16-2.76 0-5-2.24-5-5 0-2.52.9-4.79 2.16-6.52.37-.51.82-.98 1.33-1.41l2.17 2.17C14.63 11.36 13.34 12 12 12c-1.66 0-3-1.34-3-3 0-.74.26-1.42.7-1.96L3 3l1.96-.7C5.58 1.26 8.3 1 12 1c3.89 0 7.29 1.26 10.04 3.34L20.33 6.43C17.58 4.26 14.96 3 12 3 6.48 3 2.39 6.92 1.59 12c.8 5.11 4.89 9 10.41 9 2.96 0 5.58-1.26 7.33-3.43l-3.17-3.17C14.42 16.74 13.14 17 12 17z"/>
                  </svg>
                </button>
              </div>
              <div class="uiverse-input-feedback" v-if="confirmNewPasswordError">
                <span class="uiverse-error-text">{{ confirmNewPasswordError }}</span>
              </div>
            </div>
          </el-form-item>
          
          <el-form-item class="form-item-custom">
            <button 
              type="button"
              class="uiverse-button" 
              :class="{ 'loading': loading }"
              :disabled="loading"
              @click="handleResetPassword"
            >
              <span v-if="!loading" class="uiverse-button-text">
                <svg class="uiverse-button-icon" viewBox="0 0 24 30" fill="currentColor">
                  <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                </svg>
                重置密码
              </span>
              <span v-else class="uiverse-button-loading">
                <span class="uiverse-loading-spinner"></span>
                重置中...
              </span>
            </button>
          </el-form-item>
        </el-form>
        
        <!-- 底部信息 -->
        <div class="login-footer">
          <div class="census-stats">
            <div class="stat-item">
              <span class="stat-number">14亿</span>
              <span class="stat-label">全国人口</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-number">31个</span>
              <span class="stat-label">省市自治区</span>
            </div>
          </div>
          
          <div class="divider">
            <span class="divider-text">或者</span>
          </div>
          
          <div class="quick-login">
            <span class="quick-text">快速登录：</span>
            <el-button 
              class="demo-account" 
              type="text" 
              @click="fillDemoAccount"
            >
              使用演示账号
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 权限选择组件样式 */
.permission-selector {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.permission-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 16px;
  text-align: center;
}

.permission-options {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
  justify-content: center;
  width: 100%;
}

.permission-option {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 14px 24px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background-color: #fff;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.permission-option:hover {
  border-color: #6c63ff;
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(108, 99, 255, 0.2);
}

.permission-option.active {
  border-color: #6c63ff;
  background: linear-gradient(135deg, #6c63ff 0%, #8a84ff 100%);
  box-shadow: 0 8px 24px rgba(108, 99, 255, 0.3);
  color: #fff;
}

.permission-option.active::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #6c63ff 0%, #8a84ff 100%);
  z-index: -1;
}

.permission-radio {
  width: 22px;
  height: 22px;
  border: 2px solid #e0e0e0;
  border-radius: 50%;
  margin-right: 12px;
  position: relative;
  transition: all 0.3s ease;
  background-color: #fff;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.08);
}

.permission-option:hover .permission-radio {
  border-color: #6c63ff;
  transform: scale(1.1);
}

.permission-option.active .permission-radio {
  border-color: #fff;
  background-color: #fff;
  box-shadow: 0 0 0 4px rgba(108, 99, 255, 0.2);
}

.permission-option.active .permission-radio::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 12px;
  height: 12px;
  background-color: #6c63ff;
  border-radius: 50%;
  animation: radioPulse 0.3s ease;
}

@keyframes radioPulse {
  0% {
    transform: translate(-50%, -50%) scale(0);
    opacity: 0;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.3);
    opacity: 1;
  }
  100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 1;
  }
}

.permission-text {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  transition: all 0.3s ease;
}

.permission-option.active .permission-text {
  color: #fff;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .permission-options {
    flex-direction: column;
    gap: 16px;
    align-items: center;
  }
  
  .permission-option {
    width: 100%;
    max-width: 300px;
    justify-content: center;
  }
}
</style>

<script>
import { login, register, forgotPassword, resetPassword } from '@/api/auth'

export default {
  name: 'Login',
  data() {
    return {
      // 当前显示的表单：login, register, forgot-password, reset-password
      currentForm: 'login',
      
      // 登录表单数据
      loginForm: {
        username: '',
        password: '',
        role: 'ADMIN'
      },
      
      // 注册表单数据
      registerForm: {
        username: '',
        idCard: '',
        password: '',
        confirmPassword: '',
        role: ''
      },
      
      // 忘记密码表单数据
      forgotPasswordForm: {
        username: '',
        idCard: ''
      },
      
      // 密码重置表单数据
      resetPasswordForm: {
        username: '',
        idCard: '',
        newPassword: '',
        confirmPassword: '',
        token: ''
      },
      
      // 登录表单验证规则
      loginRules: {
        username: [
          {required: true, message: '请输入用户名', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          {min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur'}
        ],
        role: [
          {required: true, message: '请选择权限角色', trigger: 'blur'}
        ]
      },
      
      // 注册表单验证规则
      registerRules: {
        username: [
          {required: true, message: '请输入用户名', trigger: 'blur'}
        ],
        idCard: [
          {required: true, message: '请输入身份证号', trigger: 'blur'},
          {pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入有效的身份证号', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          {min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur'}
        ],
        confirmPassword: [
          {required: true, message: '请确认密码', trigger: 'blur'},
          {validator: (rule, value, callback) => {
            if (value !== this.registerForm.password) {
              callback(new Error('两次输入的密码不一致'))
            } else {
              callback()
            }
          }, trigger: 'blur'}
        ],
        role: [
          {required: true, message: '请选择权限角色', trigger: 'blur'}
        ]
      },
      
      // 忘记密码表单验证规则
      forgotPasswordRules: {
        username: [
          {required: true, message: '请输入用户名', trigger: 'blur'}
        ],
        idCard: [
          {required: true, message: '请输入身份证号', trigger: 'blur'},
          {pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入有效的身份证号', trigger: 'blur'}
        ]
      },
      
      // 密码重置表单验证规则
      resetPasswordRules: {
        newPassword: [
          {required: true, message: '请输入新密码', trigger: 'blur'},
          {min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur'}
        ],
        confirmPassword: [
          {required: true, message: '请确认新密码', trigger: 'blur'},
          {validator: (rule, value, callback) => {
            if (value !== this.resetPasswordForm.newPassword) {
              callback(new Error('两次输入的密码不一致'))
            } else {
              callback()
            }
          }, trigger: 'blur'}
        ]
      },
      
      loading: false,
      rememberMe: false,
      
      // 输入框聚焦状态
      inputFocused: {
        username: false,
        password: false,
        registerUsername: false,
        idCard: false,
        registerPassword: false,
        confirmPassword: false,
        forgotUsername: false,
        forgotIdCard: false,
        newPassword: false,
        confirmNewPassword: false
      },
      
      // 密码显示状态
      showPassword: false,
      showRegisterPassword: false,
      showResetPassword: false,
      
      // 登录表单验证状态
      usernameLoading: false,
      passwordLoading: false,
      usernameSuccess: false,
      passwordSuccess: false,
      usernameError: '',
      passwordError: '',
      
      // 注册表单验证状态
      registerUsernameLoading: false,
      registerUsernameSuccess: false,
      registerUsernameError: '',
      idCardLoading: false,
      idCardSuccess: false,
      idCardError: '',
      registerPasswordLoading: false,
      registerPasswordSuccess: false,
      registerPasswordError: '',
      confirmPasswordLoading: false,
      confirmPasswordSuccess: false,
      confirmPasswordError: '',
      
      // 忘记密码表单验证状态
      forgotUsernameLoading: false,
      forgotUsernameSuccess: false,
      forgotUsernameError: '',
      forgotIdCardLoading: false,
      forgotIdCardSuccess: false,
      forgotIdCardError: '',
      
      // 密码重置表单验证状态
      newPasswordLoading: false,
      newPasswordSuccess: false,
      newPasswordError: '',
      confirmNewPasswordLoading: false,
      confirmNewPasswordSuccess: false,
      confirmNewPasswordError: ''
    }
  },

  mounted() {
    // 检查URL中是否有重置密码token
    const token = this.$route.query.token
    if (token) {
      this.currentForm = 'reset-password'
      this.resetPasswordForm.token = token
    }
  },
  methods: {
    // 切换表单
    switchForm(formName) {
      this.currentForm = formName
    },
    
    handleInputFocus(field) {
      this.inputFocused[field] = true
    },
    handleInputBlur(field) {
      this.inputFocused[field] = false

      // 失去焦点时进行验证
      if (field === 'username') {
        this.validateUsername()
      } else if (field === 'password') {
        this.validatePassword()
      } else if (field === 'registerUsername') {
        this.validateRegisterUsername()
      } else if (field === 'idCard') {
        this.validateIdCard()
      } else if (field === 'registerPassword') {
        this.validateRegisterPassword()
      } else if (field === 'confirmPassword') {
        this.validateConfirmPassword()
      } else if (field === 'forgotUsername') {
        this.validateForgotUsername()
      } else if (field === 'forgotIdCard') {
        this.validateForgotIdCard()
      } else if (field === 'newPassword') {
        this.validateNewPassword()
      } else if (field === 'confirmNewPassword') {
        this.validateConfirmNewPassword()
      }
    },
    handleButtonHover() {
      // 按钮悬停效果
    },
    handleButtonLeave() {
      // 按钮离开效果
    },
    // 切换密码显示状态
    togglePasswordVisibility() {
      this.showPassword = !this.showPassword
    },
    
    toggleRegisterPasswordVisibility() {
      this.showRegisterPassword = !this.showRegisterPassword
    },
    
    toggleResetPasswordVisibility() {
      this.showResetPassword = !this.showResetPassword
    },
    // 实时验证用户名
    validateUsername() {
      const username = this.loginForm.username.trim()

      if (!username) {
        this.usernameError = ''
        this.usernameLoading = false
        this.usernameSuccess = false
        return
      }

      this.usernameLoading = true
      this.usernameError = ''

      // 模拟异步验证
      setTimeout(() => {
        this.usernameError = ''
        this.usernameSuccess = true
        this.usernameLoading = false
      }, 500)
    },
    // 实时验证密码
    validatePassword() {
      const password = this.loginForm.password

      if (!password) {
        this.passwordError = ''
        this.passwordLoading = false
        this.passwordSuccess = false
        this.passwordStrength = 0
        return
      }

      this.passwordLoading = true
      this.passwordError = ''

      // 模拟异步验证
      setTimeout(() => {
        if (password.length < 6) {
          this.passwordError = '密码长度不能少于6个字符'
          this.passwordSuccess = false
        } else if (password.length > 20) {
          this.passwordError = '密码长度不能超过20个字符'
          this.passwordSuccess = false
        } else {
          this.passwordError = ''
          this.passwordSuccess = true
        }
        this.passwordLoading = false
      }, 500)
    },
    
    // 实时验证注册用户名
    validateRegisterUsername() {
      const username = this.registerForm.username.trim()

      if (!username) {
        this.registerUsernameError = ''
        this.registerUsernameLoading = false
        this.registerUsernameSuccess = false
        return
      }

      this.registerUsernameLoading = true
      this.registerUsernameError = ''

      // 模拟异步验证
      setTimeout(() => {
        this.registerUsernameError = ''
        this.registerUsernameSuccess = true
        this.registerUsernameLoading = false
      }, 500)
    },
    
    // 实时验证身份证号
    validateIdCard() {
      const idCard = this.registerForm.idCard.trim()

      if (!idCard) {
        this.idCardError = ''
        this.idCardLoading = false
        this.idCardSuccess = false
        return
      }

      this.idCardLoading = true
      this.idCardError = ''

      // 模拟异步验证
      setTimeout(() => {
        const idCardRegex = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
        if (!idCardRegex.test(idCard)) {
          this.idCardError = '请输入有效的身份证号'
          this.idCardSuccess = false
        } else {
          this.idCardError = ''
          this.idCardSuccess = true
        }
        this.idCardLoading = false
      }, 500)
    },
    
    // 实时验证注册密码
    validateRegisterPassword() {
      const password = this.registerForm.password

      if (!password) {
        this.registerPasswordError = ''
        this.registerPasswordLoading = false
        this.registerPasswordSuccess = false
        return
      }

      this.registerPasswordLoading = true
      this.registerPasswordError = ''

      // 模拟异步验证
      setTimeout(() => {
        if (password.length < 6) {
          this.registerPasswordError = '密码长度不能少于6个字符'
          this.registerPasswordSuccess = false
        } else if (password.length > 20) {
          this.registerPasswordError = '密码长度不能超过20个字符'
          this.registerPasswordSuccess = false
        } else {
          this.registerPasswordError = ''
          this.registerPasswordSuccess = true
        }
        this.registerPasswordLoading = false
      }, 500)
    },
    
    // 实时验证确认密码
    validateConfirmPassword() {
      const confirmPassword = this.registerForm.confirmPassword
      const password = this.registerForm.password

      if (!confirmPassword) {
        this.confirmPasswordError = ''
        this.confirmPasswordLoading = false
        this.confirmPasswordSuccess = false
        return
      }

      this.confirmPasswordLoading = true
      this.confirmPasswordError = ''

      // 模拟异步验证
      setTimeout(() => {
        if (confirmPassword !== password) {
          this.confirmPasswordError = '两次输入的密码不一致'
          this.confirmPasswordSuccess = false
        } else {
          this.confirmPasswordError = ''
          this.confirmPasswordSuccess = true
        }
        this.confirmPasswordLoading = false
      }, 500)
    },
    
    // 实时验证忘记密码邮箱
    validateForgotEmail() {
      const email = this.forgotPasswordForm.email.trim()

      if (!email) {
        this.forgotEmailError = ''
        this.forgotEmailLoading = false
        this.forgotEmailSuccess = false
        return
      }

      this.forgotEmailLoading = true
      this.forgotEmailError = ''

      // 模拟异步验证
      setTimeout(() => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        if (!emailRegex.test(email)) {
          this.forgotEmailError = '请输入有效的邮箱地址'
          this.forgotEmailSuccess = false
        } else {
          this.forgotEmailError = ''
          this.forgotEmailSuccess = true
        }
        this.forgotEmailLoading = false
      }, 500)
    },
    
    // 实时验证新密码
    validateNewPassword() {
      const password = this.resetPasswordForm.newPassword

      if (!password) {
        this.newPasswordError = ''
        this.newPasswordLoading = false
        this.newPasswordSuccess = false
        return
      }

      this.newPasswordLoading = true
      this.newPasswordError = ''

      // 模拟异步验证
      setTimeout(() => {
        if (password.length < 6) {
          this.newPasswordError = '密码长度不能少于6个字符'
          this.newPasswordSuccess = false
        } else if (password.length > 20) {
          this.newPasswordError = '密码长度不能超过20个字符'
          this.newPasswordSuccess = false
        } else {
          this.newPasswordError = ''
          this.newPasswordSuccess = true
        }
        this.newPasswordLoading = false
      }, 500)
    },
    
    // 实时验证确认新密码
    validateConfirmNewPassword() {
      const confirmPassword = this.resetPasswordForm.confirmPassword
      const password = this.resetPasswordForm.newPassword

      if (!confirmPassword) {
        this.confirmNewPasswordError = ''
        this.confirmNewPasswordLoading = false
        this.confirmNewPasswordSuccess = false
        return
      }

      this.confirmNewPasswordLoading = true
      this.confirmNewPasswordError = ''

      // 模拟异步验证
      setTimeout(() => {
        if (confirmPassword !== password) {
          this.confirmNewPasswordError = '两次输入的密码不一致'
          this.confirmNewPasswordSuccess = false
        } else {
          this.confirmNewPasswordError = ''
          this.confirmNewPasswordSuccess = true
        }
        this.confirmNewPasswordLoading = false
      }, 500)
    },
    
    // 实时验证忘记密码用户名
    validateForgotUsername() {
      const username = this.forgotPasswordForm.username.trim()

      if (!username) {
        this.forgotUsernameError = ''
        this.forgotUsernameLoading = false
        this.forgotUsernameSuccess = false
        return
      }

      this.forgotUsernameLoading = true
      this.forgotUsernameError = ''

      // 模拟异步验证
      setTimeout(() => {
        this.forgotUsernameError = ''
        this.forgotUsernameSuccess = true
        this.forgotUsernameLoading = false
      }, 500)
    },
    
    // 实时验证忘记密码身份证号
    validateForgotIdCard() {
      const idCard = this.forgotPasswordForm.idCard.trim()

      if (!idCard) {
        this.forgotIdCardError = ''
        this.forgotIdCardLoading = false
        this.forgotIdCardSuccess = false
        return
      }

      this.forgotIdCardLoading = true
      this.forgotIdCardError = ''

      // 模拟异步验证
      setTimeout(() => {
        const idCardRegex = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
        if (!idCardRegex.test(idCard)) {
          this.forgotIdCardError = '请输入有效的身份证号'
          this.forgotIdCardSuccess = false
        } else {
          this.forgotIdCardError = ''
          this.forgotIdCardSuccess = true
        }
        this.forgotIdCardLoading = false
      }, 500)
    },

    fillDemoAccount() {
      this.loginForm.username = 'admin'
      this.loginForm.password = '123456'
      // 触发验证
      this.validateUsername()
      this.validatePassword()
      this.$message.success('已填入演示账号信息')
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid && !this.usernameError && !this.passwordError) {
          this.loading = true

          // 记住用户名
          if (this.rememberMe) {
            localStorage.setItem('rememberedUsername', this.loginForm.username)
          } else {
            localStorage.removeItem('rememberedUsername')
          }

          login(this.loginForm)
              .then(response => {
                if (response.code === 200) {
                  // 保存token到localStorage
                  localStorage.setItem('token', response.data.token)
                  localStorage.setItem('userInfo', JSON.stringify(response.data.user))

                  // 显示成功消息
                  this.$message.success('登录成功，欢迎使用人口普查管理系统！')

                  // 延迟跳转到首页
                  setTimeout(() => {
                    this.$router.push('/')
                  }, 800)
                } else {
                  this.$message.error(response.message || '登录失败')
                }
              })
              .catch(error => {
                console.error('登录失败:', error)
                let errorMessage = '登录失败，请检查用户名和密码'

                // 根据错误类型提供更具体的错误信息
                if (error.response) {
                  if (error.response.status === 403) {
                    errorMessage = '后端服务不可用，请确保后端服务已启动 (localhost:9090)'
                  } else if (error.response.status === 404) {
                    errorMessage = '登录接口不存在，请检查后端路由配置'
                  } else if (error.response.status === 500) {
                    errorMessage = '后端服务错误，请查看服务器日志'
                  } else {
                    errorMessage = (error.response.data && error.response.data.message) || error.message || errorMessage
                  }
                } else if (error.request) {
                  errorMessage = '无法连接到服务器，请检查后端服务是否启动'
                } else {
                  errorMessage = error.message || errorMessage
                }

                this.$message.error(errorMessage)
              })
              .finally(() => {
                this.loading = false
              })
        } else {
          // 如果有错误，显示具体错误信息
          if (this.usernameError) {
            this.$message.warning(this.usernameError)
          } else if (this.passwordError) {
            this.$message.warning(this.passwordError)
          } else {
            this.$message.warning('请填写完整的登录信息，包括选择权限角色')
          }
          return false
        }
      })
    },
    
    // 处理注册
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid && !this.registerUsernameError && !this.idCardError && !this.registerPasswordError && !this.confirmPasswordError) {
          this.loading = true

          register(this.registerForm)
              .then(response => {
                if (response.code === 200) {
                  this.$message.success('注册成功，请登录')
                  // 切换到登录表单
                  this.currentForm = 'login'
                  // 清空注册表单
                  this.registerForm = {
                    username: '',
                    idCard: '',
                    password: '',
                    confirmPassword: ''
                  }
                } else {
                  this.$message.error(response.message || '注册失败')
                }
              })
              .catch(error => {
                console.error('注册失败:', error)
                // 从Error对象中提取原始错误信息
                const errorMessage = error.message || '注册失败，请稍后重试'
                this.$message.error(errorMessage)
              })
              .finally(() => {
                this.loading = false
              })
        } else {
          // 如果有错误，显示具体错误信息
          if (this.registerUsernameError) {
            this.$message.warning(this.registerUsernameError)
          } else if (this.idCardError) {
            this.$message.warning(this.idCardError)
          } else if (this.registerPasswordError) {
            this.$message.warning(this.registerPasswordError)
          } else if (this.confirmPasswordError) {
            this.$message.warning(this.confirmPasswordError)
          } else {
            this.$message.warning('请填写完整的注册信息')
          }
          return false
        }
      })
    },
    
    // 处理忘记密码
    handleForgotPassword() {
      this.$refs.forgotPasswordForm.validate(valid => {
        if (valid && !this.forgotUsernameError && !this.forgotIdCardError) {
          this.loading = true

          forgotPassword(this.forgotPasswordForm)
              .then(response => {
                if (response.code === 200) {
                  this.$message.success('身份验证成功，请设置新密码')
                  // 跳转到重置密码表单，并传递用户名和身份证号
                  this.currentForm = 'reset-password'
                  // 将用户名和身份证号保存到重置密码表单中
                  this.resetPasswordForm.username = this.forgotPasswordForm.username
                  this.resetPasswordForm.idCard = this.forgotPasswordForm.idCard
                } else {
                  this.$message.error(response.message || '身份验证失败')
                }
              })
              .catch(error => {
                console.error('身份验证失败:', error)
                // 从Error对象中提取原始错误信息
                const errorMessage = error.message || '身份验证失败，请稍后重试'
                this.$message.error(errorMessage)
              })
              .finally(() => {
                this.loading = false
              })
        } else {
          // 如果有错误，显示具体错误信息
          if (this.forgotUsernameError) {
            this.$message.warning(this.forgotUsernameError)
          } else if (this.forgotIdCardError) {
            this.$message.warning(this.forgotIdCardError)
          } else {
            this.$message.warning('请填写完整的身份信息')
          }
          return false
        }
      })
    },
    
    // 处理密码重置
    handleResetPassword() {
      this.$refs.resetPasswordForm.validate(valid => {
        if (valid && !this.newPasswordError && !this.confirmNewPasswordError) {
          this.loading = true

          resetPassword({
            username: this.resetPasswordForm.username,
            idCard: this.resetPasswordForm.idCard,
            token: this.resetPasswordForm.token,
            newPassword: this.resetPasswordForm.newPassword,
            confirmPassword: this.resetPasswordForm.confirmPassword
          })
              .then(response => {
                if (response.code === 200) {
                  this.$message.success('密码重置成功，请登录')
                  // 切换到登录表单
                  this.currentForm = 'login'
                  // 清空密码重置表单
                  this.resetPasswordForm = {
                    newPassword: '',
                    confirmPassword: '',
                    token: ''
                  }
                } else {
                  this.$message.error(response.message || '密码重置失败')
                }
              })
              .catch(error => {
                console.error('密码重置失败:', error)
                // 从Error对象中提取原始错误信息
                const errorMessage = error.message || '密码重置失败，请稍后重试'
                this.$message.error(errorMessage)
              })
              .finally(() => {
                this.loading = false
              })
        } else {
          // 如果有错误，显示具体错误信息
          if (this.newPasswordError) {
            this.$message.warning(this.newPasswordError)
          } else if (this.confirmNewPasswordError) {
            this.$message.warning(this.confirmNewPasswordError)
          } else {
            this.$message.warning('请填写完整的密码重置信息')
          }
          return false
        }
      })
    }
  }
}
</script>

<style scoped>
/* 基础动画 */
@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  33% { transform: translateY(-15px) rotate(120deg); }
  66% { transform: translateY(5px) rotate(240deg); }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

@keyframes chartBarGrow {
  from { height: 0; }
  to { height: var(--bar-height); }
}

@keyframes iconFloat {
  0%, 100% { transform: translateY(0px) scale(1); }
  50% { transform: translateY(-10px) scale(1.1); }
}

@keyframes dataFlow {
  0% { transform: translateX(-100%) translateY(0); opacity: 0; }
  50% { opacity: 1; }
  100% { transform: translateX(100vw) translateY(-50px); opacity: 0; }
}

/* 容器样式 */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 50%, #667eea 100%);
  position: relative;
  overflow: hidden;
  font-family: 'PingFang SC', 'Microsoft YaHei', 'Helvetica Neue', Arial, sans-serif;
}

/* 背景动画 - 人口普查主题 */
.background-animation {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

/* 统计图表背景元素 */
.census-charts {
  position: absolute;
  width: 100%;
  height: 100%;
}

.chart-element {
  position: absolute;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  padding: 10px;
}

.chart-1 {
  top: 15%;
  left: 10%;
  width: 80px;
  height: 100px;
}

.chart-2 {
  bottom: 20%;
  right: 15%;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
}

.bar {
  background: linear-gradient(to top, #4CAF50, #81C784);
  margin: 2px 0;
  border-radius: 2px;
  animation: chartBarGrow 2s ease-out infinite;
}

.bar-1 { width: 60%; --bar-height: 20px; animation-delay: 0.2s; }
.bar-2 { width: 80%; --bar-height: 35px; animation-delay: 0.4s; }
.bar-3 { width: 100%; --bar-height: 50px; animation-delay: 0.6s; }
.bar-4 { width: 75%; --bar-height: 40px; animation-delay: 0.8s; }

.pie-segment {
  position: absolute;
  border-radius: 50%;
  animation: chartBarGrow 2s ease-out infinite;
}

.segment-1 {
  width: 30px; height: 30px; background: #FF5722; 
  top: 5px; left: 5px; animation-delay: 0.3s;
}
.segment-2 {
  width: 25px; height: 25px; background: #2196F3; 
  top: 5px; right: 5px; animation-delay: 0.5s;
}
.segment-3 {
  width: 20px; height: 20px; background: #FF9800; 
  bottom: 5px; left: 15px; animation-delay: 0.7s;
}

/* 浮动图标 */
.floating-icons {
  position: absolute;
  width: 100%;
  height: 100%;
}

.icon-group {
  position: absolute;
  display: flex;
  gap: 20px;
}

.home-group {
  top: 25%;
  right: 20%;
  animation: iconFloat 4s ease-in-out infinite;
}

.people-group {
  bottom: 30%;
  left: 15%;
  animation: iconFloat 3s ease-in-out infinite 1s;
}

.census-icon {
  width: 30px;
  height: 30px;
  color: rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.home-group .census-icon:hover {
  color: rgba(255, 255, 255, 0.4);
  transform: scale(1.2);
}

.people-group .census-icon:hover {
  color: rgba(255, 255, 255, 0.4);
  transform: scale(1.2);
}

/* 地理图案 */
.geo-pattern {
  position: absolute;
  width: 100%;
  height: 100%;
}

.region {
  position: absolute;
  background: rgba(255, 255, 255, 0.03);
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  animation: float 8s ease-in-out infinite;
}

.region-1 {
  width: 120px;
  height: 80px;
  top: 10%;
  left: 60%;
  animation-delay: 0s;
}

/* UIverse组件样式 */

/* 按钮样式 */
.uiverse-button {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 12px 24px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.uiverse-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.uiverse-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 10px rgba(102, 126, 234, 0.3);
}

.uiverse-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.uiverse-button.loading {
  opacity: 0.8;
  cursor: wait;
}

.uiverse-button-text {
  display: flex;
  align-items: center;
  gap: 8px;
}

.uiverse-button-icon {
  width: 20px;
  height: 20px;
}

.uiverse-button-loading {
  display: flex;
  align-items: center;
  gap: 8px;
}

.uiverse-loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 输入框样式 */
.uiverse-input-container {
  margin-bottom: 20px;
}

.uiverse-uiverse-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  padding: 12px 16px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.uiverse-uiverse-input-wrapper.uiverse-uiverse-focused {
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
  transform: translateY(-1px);
}

.uiverse-uiverse-input-wrapper.uiverse-uiverse-has-error {
  box-shadow: 0 4px 16px rgba(255, 77, 79, 0.3);
}

.uiverse-uiverse-input-icon {
  margin-right: 12px;
  color: #666;
  transition: color 0.3s ease;
}

.uiverse-uiverse-input-wrapper.uiverse-uiverse-focused .uiverse-uiverse-input-icon {
  color: #667eea;
}

.uiverse-uiverse-uiverse-input-icon-svg {
  width: 20px;
  height: 20px;
}

.uiverse-uiverse-input-success-icon {
  width: 20px;
  height: 20px;
  color: #52c41a;
}

.uiverse-uiverse-input-loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(102, 126, 234, 0.3);
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.uiverse-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 16px;
  color: #333;
  background: transparent;
}

.uiverse-input::placeholder {
  color: #999;
  transition: color 0.3s ease;
}

.uiverse-uiverse-input-wrapper.uiverse-uiverse-focused .uiverse-input::placeholder {
  color: #667eea;
}

.uiverse-uiverse-password-toggle {
  background: none;
  border: none;
  outline: none;
  cursor: pointer;
  padding: 4px;
  color: #666;
  transition: color 0.3s ease;
}

.uiverse-uiverse-password-toggle:hover {
  color: #667eea;
}

.uiverse-uiverse-toggle-icon {
  width: 20px;
  height: 20px;
}

.uiverse-input-underline {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.uiverse-uiverse-input-wrapper.uiverse-uiverse-focused .uiverse-input-underline {
  transform: scaleX(1);
}

.uiverse-uiverse-input-feedback {
  margin-top: 8px;
  font-size: 14px;
  color: #ff4d4f;
}

.uiverse-uiverse-error-text {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 表单标签切换样式 */
.form-tabs {
  display: flex;
  margin-bottom: 24px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 4px;
}

.form-tab {
  flex: 1;
  padding: 12px 24px;
  text-align: center;
  font-size: 16px;
  font-weight: 500;
  color: #607D8B;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 6px;
  background: rgba(245, 247, 250, 0.8);
}

.form-tab:hover {
  color: white;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.form-tab.active {
  color: white;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.region-2 {
  width: 100px;
  height: 60px;
  bottom: 40%;
  right: 25%;
  animation-delay: 3s;
}

.region-3 {
  width: 80px;
  height: 50px;
  top: 60%;
  left: 25%;
  animation-delay: 6s;
}

/* 登录表单包装器 */
.login-form-wrapper {
  position: relative;
  z-index: 10;
  animation: fadeInUp 1s ease-out;
}

.login-form {
  width: 440px;
  padding: 50px 45px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.3);
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.3);
  animation: fadeInUp 1s ease-out;
  position: relative;
  overflow: hidden;
}

.login-form::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #FF5722, #2196F3, #4CAF50, #FF9800);
  background-size: 300% 100%;
  animation: dataFlow 3s linear infinite;
}

/* 头部样式 */
.login-header {
  margin-bottom: 40px;
}

.logo-container {
  margin-bottom: 25px;
}

.census-logo {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.logo-main {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #2196F3, #00BCD4);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 15px 30px rgba(33, 150, 243, 0.4);
  animation: pulse 2s ease-in-out infinite;
  position: relative;
}



.census-logo-svg {
  width: 60px;
  height: 60px;
  color: white;
}

.title {
  margin: 0 0 15px;
}

.title-cn {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #1e3c72, #2a5298, #2196F3);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: block;
}

.subtitle {
  margin: 0 0 20px;
  color: #607D8B;
  font-size: 14px;
  font-weight: 400;
  letter-spacing: 0.5px;
}

.census-info {
  display: flex;
  justify-content: space-around;
  margin: 20px 0;
  padding: 15px;
  background: rgba(33, 150, 243, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(33, 150, 243, 0.1);
}

.info-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  font-size: 12px;
}

.info-icon {
  font-size: 18px;
}

.info-text {
  color: #37474F;
  font-weight: 500;
}

/* 表单样式 */
.login-form-content {
  margin-top: 30px;
}

.form-item-custom {
  margin-bottom: 28px;
}

.input-container {
  position: relative;
}

.custom-input {
  width: 100%;
}

.custom-input :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.9);
  border: 2px solid #E0E6ED;
  border-radius: 16px;
  padding: 16px 24px;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.custom-input :deep(.el-input__wrapper:hover) {
  border-color: #90CAF9;
  box-shadow: 0 0 0 3px rgba(33, 150, 243, 0.1);
  background: rgba(255, 255, 255, 0.95);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  border-color: #2196F3;
  box-shadow: 0 0 0 4px rgba(33, 150, 243, 0.15);
  background: rgba(255, 255, 255, 0.98);
  transform: translateY(-1px);
}

.custom-input :deep(.el-input__inner) {
  font-size: 16px;
  color: #2C3E50;
  font-weight: 500;
}

.custom-input :deep(.el-input__inner::placeholder) {
  color: #90A4AE;
}

/* 增强输入框样式 */
.uiverse-input-container {
  position: relative;
  margin-bottom: 8px;
  width: 100%;
}

.uiverse-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.95);
  border: 2px solid #E0E6ED;
  border-radius: 16px;
  padding: 24px 28px;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  width: 100%;
  min-width: 100%;
  box-sizing: border-box;
  min-height: 64px;
  margin: 0 auto;
}

.uiverse-input-wrapper:hover {
  border-color: #90CAF9;
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 4px 20px rgba(33, 150, 243, 0.1);
}

.uiverse-input-wrapper.uiverse-focused {
  border-color: #2196F3;
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 0 0 4px rgba(33, 150, 243, 0.15), 0 8px 25px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.uiverse-input-wrapper.uiverse-has-error {
  border-color: #F44336;
  box-shadow: 0 0 0 4px rgba(244, 67, 54, 0.15);
}

.uiverse-input-wrapper.has-success {
  border-color: #4CAF50;
  box-shadow: 0 0 0 4px rgba(76, 175, 80, 0.15);
}

.uiverse-input-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  margin-right: 12px;
  color: #90A4AE;
  transition: color 0.3s ease;
}

.uiverse-input-wrapper.uiverse-focused .uiverse-input-icon {
  color: #2196F3;
}

.uiverse-input-wrapper.has-success .uiverse-input-icon {
  color: #4CAF50;
}

.uiverse-input-wrapper.uiverse-has-error .uiverse-input-icon {
  color: #F44336;
}

.uiverse-uiverse-input-icon-svg {
  width: 20px;
  height: 20px;
}

.uiverse-input-success-icon {
  width: 18px;
  height: 18px;
  color: #4CAF50;
}

.uiverse-input-loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(144, 164, 174, 0.3);
  border-top: 2px solid #2196F3;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.uiverse-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 16px;
  color: #2C3E50;
  font-weight: 500;
  padding: 8px 0;
  width: 100%;
}

.uiverse-input::placeholder {
  color: transparent;
}

.floating-label {
  position: absolute;
  left: 56px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 16px;
  color: #90A4AE;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  pointer-events: none;
  background: transparent;
  padding: 0 4px;
}

.uiverse-input-wrapper.uiverse-focused .floating-label,
.uiverse-input-wrapper.uiverse-has-value .floating-label {
  top: -8px;
  left: 12px;
  font-size: 12px;
  color: #2196F3;
  background: white;
  font-weight: 500;
}

.uiverse-input-wrapper.uiverse-has-error .floating-label {
  color: #F44336;
}

.uiverse-input-wrapper.has-success .floating-label {
  color: #4CAF50;
}

.input-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 3px;
  background: linear-gradient(90deg, #2196F3, #00BCD4);
  transition: width 0.3s ease;
  border-radius: 0 0 14px 14px;
}

.uiverse-input-wrapper.uiverse-focused .input-progress,
.uiverse-input-wrapper.uiverse-has-value .input-progress {
  width: 100%;
}

.uiverse-password-toggle {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  color: #90A4AE;
  transition: color 0.3s ease;
  border-radius: 4px;
}

.uiverse-password-toggle:hover {
  color: #2196F3;
  background: rgba(33, 150, 243, 0.1);
}

.uiverse-toggle-icon {
  width: 20px;
  height: 20px;
}

.uiverse-input-feedback {
  margin-top: 6px;
  padding: 0 4px;
}

.uiverse-error-text {
  font-size: 12px;
  color: #F44336;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.uiverse-error-text::before {
  content: '⚠';
  font-size: 10px;
}

/* 选项行 */
.options-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.remember-checkbox {
  color: #607D8B;
}

.forgot-password {
  color: #2196F3;
  font-size: 14px;
  cursor: pointer;
  transition: color 0.3s ease;
  font-weight: 500;
}

.forgot-password:hover {
  color: #1976D2;
  text-decoration: underline;
}

/* 登录按钮 */
.login-button {
  position: relative;
  width: 100%;
  height: 60px;
  background: transparent;
  border: none;
  border-radius: 16px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
  font-size: 16px;
  font-weight: 600;
  margin-top: 25px;
  box-shadow: 0 4px 15px rgba(33, 150, 243, 0.2);
}

.login-button:not(:disabled):hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(33, 150, 243, 0.4);
}

.login-button:disabled {
  cursor: not-allowed;
  opacity: 0.8;
}

.button-content {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  color: white;
  gap: 8px;
}

.button-text,
.button-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  white-space: nowrap;
}

.button-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #2196F3 0%, #00BCD4 50%, #2196F3 100%);
  background-size: 200% 200%;
  transition: all 0.3s ease;
}

.login-button:hover .button-bg {
  background-position: right center;
}

.login-button:active {
  transform: translateY(-1px);
}

/* 加载动画 */
.loading-spinner {
  width: 18px;
  height: 18px;
  border: 2px solid transparent;
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 底部样式 */
.login-footer {
  margin-top: 35px;
}

/* 人口统计信息 */
.census-stats {
  display: flex;
  justify-content: space-around;
  align-items: center;
  margin: 20px 0;
  padding: 15px;
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.1), rgba(33, 150, 243, 0.1));
  border-radius: 12px;
  border: 1px solid rgba(76, 175, 80, 0.2);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-number {
  font-size: 18px;
  font-weight: 700;
  color: #2E7D32;
}

.stat-label {
  font-size: 12px;
  color: #4CAF50;
  font-weight: 500;
}

.stat-divider {
  width: 1px;
  height: 30px;
  background: linear-gradient(to bottom, transparent, #4CAF50, transparent);
}

.divider {
  position: relative;
  margin: 25px 0;
  text-align: center;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(to right, transparent, #E0E6ED, transparent);
}

.divider-text {
  background: rgba(255, 255, 255, 0.98);
  padding: 0 20px;
  color: #bdc3c7;
  font-size: 13px;
}

.quick-login {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.quick-text {
  color: #95a5a6;
  font-size: 14px;
}

.demo-account {
  color: #2196F3 !important;
  font-size: 13px !important;
  padding: 6px 12px !important;
  border-radius: 8px !important;
  transition: all 0.3s ease;
  font-weight: 500;
}

.demo-account:hover {
  background: rgba(33, 150, 243, 0.1) !important;
  color: #1976D2 !important;
  transform: translateY(-1px);
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-form {
    width: 92%;
    max-width: 380px;
    padding: 40px 30px;
    margin: 20px;
  }
  
  .title-cn {
    font-size: 26px;
  }
  
  .census-charts,
  .floating-icons,
  .geo-pattern {
    display: none;
  }
  
  .census-info {
    flex-direction: column;
    gap: 10px;
  }
  
  .census-stats {
    flex-direction: column;
    gap: 10px;
  }
  
  .stat-divider {
    width: 30px;
    height: 1px;
    background: linear-gradient(to right, transparent, #4CAF50, transparent);
  }
}

/* 深色主题支持 */
@media (prefers-color-scheme: dark) {
  .login-form {
    background: rgba(33, 39, 50, 0.98);
    border: 1px solid rgba(255, 255, 255, 0.1);
  }
  
  .custom-input :deep(.el-input__wrapper) {
    background: rgba(255, 255, 255, 0.08);
    border-color: #455A64;
  }
  
  .custom-input :deep(.el-input__inner) {
    color: #ECEFF1;
  }
  
  .title-cn {
    color: #ECEFF1;
  }
  
  .subtitle,
  .info-text,
  .stat-label {
    color: #B0BEC5;
  }
}
</style>