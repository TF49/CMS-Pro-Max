<template>
  <div class="permission-denied">
    <div class="permission-denied-container">
      <div class="permission-denied-icon">
        <svg xmlns="http://www.w3.org/2000/svg" width="120" height="120" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="15" y1="9" x2="9" y2="15"></line>
          <line x1="9" y1="9" x2="15" y2="15"></line>
        </svg>
      </div>
      <h1 class="permission-denied-title">403</h1>
      <p class="permission-denied-message">抱歉，您没有访问该页面的权限</p>
      <p class="permission-denied-tip">请联系管理员获取相应权限</p>
      <div class="permission-denied-actions">
        <el-button type="primary" @click="goHome">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PermissionDenied',
  methods: {
    goHome() {
      this.$router.push('/dashboard')
    }
  }
}
</script>

<style scoped>
.permission-denied {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.permission-denied-container {
  text-align: center;
  background-color: #fff;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  max-width: 400px;
  width: 100%;
}

.permission-denied-icon {
  color: #f56c6c;
  margin-bottom: 20px;
}

.permission-denied-title {
  font-size: 72px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 10px 0;
}

.permission-denied-message {
  font-size: 18px;
  color: #606266;
  margin: 0 0 10px 0;
}

.permission-denied-tip {
  font-size: 14px;
  color: #909399;
  margin: 0 0 30px 0;
}

.permission-denied-actions {
  display: flex;
  justify-content: center;
}
</style>
