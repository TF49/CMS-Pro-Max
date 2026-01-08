<template>
  <div class="error-page">
    <div class="error-container">
      <el-icon class="error-icon"><WarningFilled /></el-icon>
      <h1 class="error-code">404</h1>
      <h2 class="error-message">页面不存在</h2>
      <p class="error-description">抱歉，您访问的页面不存在或已被删除</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script>
import { WarningFilled } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

export default {
  name: '404',
  components: {
    WarningFilled
  },
  setup() {
    const router = useRouter()
    
    const goHome = () => {
      router.push('/dashboard')
    }
    
    return {
      goHome
    }
  }
}
</script>

<style scoped>
.error-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-color: #f5f7fa;
}

.error-container {
  text-align: center;
  padding: 40px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.error-icon {
  font-size: 64px;
  color: #ff6b6b;
  margin-bottom: 20px;
}

.error-code {
  font-size: 72px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.error-message {
  font-size: 24px;
  color: #666;
  margin: 10px 0;
}

.error-description {
  font-size: 16px;
  color: #999;
  margin: 20px 0 30px;
}
</style>