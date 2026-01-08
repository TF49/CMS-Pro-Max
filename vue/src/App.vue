<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'
import websocketService from '@/utils/websocket'

// 组件挂载时连接WebSocket
onMounted(() => {
  const token = localStorage.getItem('token')
  if (token) {
    websocketService.connect(token).catch(err => {
      console.error('WebSocket连接失败:', err)
    })
  }
})

// 组件卸载时断开WebSocket连接
onUnmounted(() => {
  websocketService.disconnect()
})
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  height: 100vh;
}

* {
  margin: 0;
  padding: 0;
}
</style>