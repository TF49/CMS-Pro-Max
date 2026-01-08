<template>
  <view class="social-security-list-container">
    <!-- 头部导航 -->
    <view class="header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="header-left">
        <button class="back-btn" @click="handleBack">
          <uni-icons type="back" size="28" color="#333"></uni-icons>
        </button>
      </view>
      <view class="header-title">社保管理</view>
      <view class="header-right">
        <button class="add-btn" @click="handleAdd">
          <uni-icons type="plus" size="24" color="#fff"></uni-icons>
        </button>
      </view>
    </view>
    
    <!-- 搜索区域 -->
    <view class="search-section">
      <view class="search-container">
        <uni-icons type="search" size="24" color="#999" class="search-icon"></uni-icons>
        <input
          class="search-input"
          placeholder="搜索居民姓名、身份证号"
          v-model="keyword"
          @confirm="handleSearch"
        />
        <button class="search-btn" @click="handleSearch" :disabled="loading">
          <text class="search-btn-text">搜索</text>
        </button>
        <button class="reset-btn" @click="handleReset" :disabled="loading || !keyword">
          <text class="reset-btn-text">重置</text>
        </button>
      </view>
    </view>
    
    <!-- 内容区域 -->
    <view class="content-container">
      <!-- 加载状态 -->
      <view v-if="loading" class="loading-container">
        <uni-icons type="spinner-cycle" size="48" color="#667eea" class="loading-icon"></uni-icons>
        <text class="loading-text">加载中...</text>
      </view>
      
      <!-- 空状态 -->
      <view v-else-if="socialSecurityList.length === 0" class="empty-container">
        <view class="empty-icon">
          <uni-icons type="medal" size="64" color="#ccc"></uni-icons>
        </view>
        <!-- 区分搜索结果为空和本来就没有数据的情况 -->
        <text v-if="keyword" class="empty-title">找不到该社保记录</text>
        <text v-else class="empty-title">暂无社保记录</text>
        
        <text v-if="keyword" class="empty-subtitle">没有找到匹配的社保记录，请尝试其他关键词</text>
        <text v-else class="empty-subtitle">您可以点击右上角添加社保记录</text>
        
        <!-- 只有在非搜索状态下才显示添加按钮 -->
        <button v-if="!keyword" class="add-first-btn" @click="handleAdd">
          <uni-icons type="plus" size="20" color="#667eea"></uni-icons>
          <text>添加第一条记录</text>
        </button>
      </view>
      
      <!-- 列表状态 -->
      <view v-else class="list-container">
        <uni-list>
          <uni-list-item 
            v-for="item in socialSecurityList" 
            :key="item.id"
            class="social-security-item"
          >
            <template #header>
              <view class="item-header" @click="handleDetail(item)">
                <text class="item-name">{{ item.residentName || '无' }}</text>
              </view>
            </template>
            <template #footer>
              <view class="item-footer">
                <view class="item-actions">
                  <button class="action-btn edit-btn" @click.stop="handleEdit(item)">
                    <uni-icons type="checkmarkempty" size="20" color="white"></uni-icons>
                    <text class="action-text">编辑</text>
                  </button>
                  <button class="action-btn delete-btn" @click.stop="handleDelete(item)">
                    <uni-icons type="trash" size="20" color="white"></uni-icons>
                    <text class="action-text">删除</text>
                  </button>
                </view>
              </view>
            </template>
          </uni-list-item>
        </uni-list>
        
        <!-- 分页组件 -->
        <view class="pagination-container">
          <uni-pagination
            :total="total"
            :current="currentPage"
            :page-size="pageSize"
            :show-page-size="false"
            @change="handlePageChange"
          ></uni-pagination>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { get, del } from '@/utils/request'

export default {
  data() {
    return {
      loading: false,
      keyword: '',
      socialSecurityList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      statusBarHeight: 0,
      dataChangeHandler: null
    }
  },
  onLoad() {
    this.getStatusBarHeight()
    this.loadSocialSecurityList()
    this.registerWebSocketListener()
  },
  onShow() {
    // 页面显示时重新加载数据，确保数据最新
    this.loadSocialSecurityList()
  },
  onUnload() {
    this.unregisterWebSocketListener()
  },
  methods: {
    getStatusBarHeight() {
      // #ifdef MP-WEIXIN
      const systemInfo = uni.getSystemInfoSync()
      this.statusBarHeight = systemInfo.statusBarHeight || 0
      // #endif
    },
    
    handleBack() {
      const pages = getCurrentPages()
      if (pages.length > 1) {
        uni.navigateBack()
      } else {
        uni.switchTab({
          url: '/pages/index/index'
        })
      }
    },
    
    loadSocialSecurityList() {
      this.loading = true
      
      // 注意：后端已经实现了权限控制，普通用户只能获取到自己关联的数据
      // 所以这里不传递keyword，只获取所有关联数据后在前端进行过滤
      const params = {
        page: this.currentPage,
        size: this.pageSize
        // 移除keyword参数，不在后端进行搜索
      }
      
      get('/social-security/list', params)
        .then(res => {
          let allSocialSecurityList = []
          let total = 0
          
          if (res.code === 200 && res.data) {
            if (res.data.data) {
              allSocialSecurityList = res.data.data || []
              total = res.data.total || 0
            } else if (res.data.records) {
              allSocialSecurityList = res.data.records || []
              total = res.data.total || 0
            } else {
              allSocialSecurityList = Array.isArray(res.data) ? res.data : []
              total = allSocialSecurityList.length
            }
          }
          
          // 在前端进行搜索过滤
          if (this.keyword) {
            const trimmedKeyword = this.keyword.trim()
            this.socialSecurityList = allSocialSecurityList.filter(item => {
              // 匹配姓名
              return item.residentName?.includes(trimmedKeyword)
            })
            this.total = this.socialSecurityList.length
          } else {
            this.socialSecurityList = allSocialSecurityList
            this.total = total
          }
        })
        .catch(err => {
          console.error('获取社保列表失败', err)
          uni.showToast({
            title: '获取社保列表失败',
            icon: 'none'
          })
        })
        .finally(() => {
          this.loading = false
        })
    },
    
    handleSearch() {
      this.currentPage = 1
      this.loadSocialSecurityList()
    },
    
    handleReset() {
      this.keyword = ''
      this.currentPage = 1
      this.loadSocialSecurityList()
    },
    
    handlePageChange(e) {
      this.currentPage = e.current
      this.pageSize = e.size || this.pageSize
      this.loadSocialSecurityList()
    },
    
    handleAdd() {
      uni.navigateTo({
        url: '/pages/social-security/add'
      })
    },
    
    // 修改社保记录
    handleEdit(item) {
      uni.navigateTo({
        url: `/pages/social-security/edit?id=${item.id}`
      })
    },
    
    // 删除社保记录
    handleDelete(item) {
      uni.showModal({
        title: '确认删除',
        content: `确定要删除社保记录 ${item.id} 吗？`,
        success: (res) => {
          if (res.confirm) {
            // 调用删除API
            del(`/social-security/delete/${item.id}`)
              .then(res => {
                if (res.code === 200) {
                  uni.showToast({
                    title: '删除成功',
                    icon: 'success'
                  })
                  // 重新加载列表
                  this.loadSocialSecurityList()
                } else {
                  uni.showToast({
                    title: res.message || '删除失败',
                    icon: 'none'
                  })
                }
              })
              .catch(err => {
                console.error('删除社保记录失败', err)
                uni.showToast({
                  title: '删除失败',
                  icon: 'none'
                })
              })
          }
        }
      })
    },
    
    handleDetail(item) {
      uni.navigateTo({
        url: `/pages/social-security/detail?id=${item.id}`
      })
    },
    
    getStatusClass(status) {
      const map = {
        '正常参保': 'status-normal',
        '暂停参保': 'status-paused',
        '停止参保': 'status-stopped'
      }
      return map[status] || 'status-unknown'
    },
    
    formatInsurance(item) {
      const types = []
      if (item.pensionInsurance) types.push('养老')
      if (item.medicalInsurance) types.push('医疗')
      if (item.unemploymentInsurance) types.push('失业')
      return types.length > 0 ? types.join('、') : '无保险'
    },

    /**
     * 注册WebSocket监听器
     */
    registerWebSocketListener() {
      this.dataChangeHandler = (message) => {
        console.log('社保列表页面收到数据变更通知:', message)

        if (message.entityType === 'SOCIAL_SECURITY') {
          const action = message.action

          switch (action) {
            case 'CREATE':
              this.loadSocialSecurityList()
              uni.showToast({
                title: '有新的社保记录',
                icon: 'none',
                duration: 2000
              })
              break
            case 'UPDATE':
              this.loadSocialSecurityList()
              uni.showToast({
                title: '社保信息已更新',
                icon: 'none',
                duration: 2000
              })
              break
            case 'DELETE':
              this.loadSocialSecurityList()
              uni.showToast({
                title: '社保记录已删除',
                icon: 'none',
                duration: 2000
              })
              break
          }
        }
      }

      uni.$on('dataChange', this.dataChangeHandler)
    },

    /**
     * 注销WebSocket监听器
     */
    unregisterWebSocketListener() {
      if (this.dataChangeHandler) {
        uni.$off('dataChange', this.dataChangeHandler)
        this.dataChangeHandler = null
      }
    }
  }
}
</script>

<style scoped>
.social-security-list-container {
  background: #f5f7fa;
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 88rpx;
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

.add-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 50%;
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.add-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.search-section {
  padding: 20rpx;
  background: white;
  position: sticky;
  top: 88rpx;
  z-index: 99;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.search-container {
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 0 20rpx;
  border: 2rpx solid transparent;
  transition: all 0.3s ease;
}

.search-container:focus-within {
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 8rpx rgba(102, 126, 234, 0.1);
}

.search-icon {
  margin-right: 15rpx;
  color: #999;
}

.search-input {
  flex: 1;
  height: 70rpx;
  font-size: 28rpx;
  color: #333;
  background: transparent;
  border: none;
  outline: none;
}

.search-input::placeholder {
  color: #999;
}

.search-btn, .reset-btn {
  background: none;
  border: none;
  padding: 10rpx 20rpx;
  font-size: 26rpx;
  border-radius: 8rpx;
  transition: all 0.3s ease;
  margin-left: 10rpx;
}

.search-btn {
  color: #667eea;
  font-weight: bold;
}

.search-btn:hover:not(:disabled) {
  background: rgba(102, 126, 234, 0.1);
  transform: scale(1.05);
}

.reset-btn {
  color: #999;
}

.reset-btn:hover:not(:disabled) {
  background: rgba(0, 0, 0, 0.05);
  transform: scale(1.05);
}

.search-btn:disabled, .reset-btn:disabled {
  opacity: 0.5;
  transform: none;
}

.content-container {
  padding: 20rpx;
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

.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 150rpx 0;
  background: white;
  border-radius: 16rpx;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.empty-icon {
  margin-bottom: 30rpx;
  opacity: 0.5;
}

.empty-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #666;
  margin-bottom: 15rpx;
}

.empty-subtitle {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 40rpx;
  text-align: center;
  padding: 0 40rpx;
}

.add-first-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 25rpx;
  padding: 15rpx 40rpx;
  font-size: 26rpx;
  display: flex;
  align-items: center;
  gap: 10rpx;
  transition: all 0.3s ease;
}

.add-first-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.3);
}

.list-container {
  background: white;
  border-radius: 16rpx;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.social-security-item {
  background: white;
  border-bottom: 1rpx solid #f0f0f0;
  transition: all 0.3s ease;
}

.social-security-item:last-child {
  border-bottom: none;
}

.social-security-item:hover {
  background: #fafafa;
  transform: translateY(-2rpx);
}

.item-header {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
  padding: 5rpx 0;
}

.item-basic-info {
  display: flex;
  align-items: center;
  gap: 15rpx;
}

.item-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.item-tag {
  padding: 5rpx 15rpx;
  border-radius: 15rpx;
  font-size: 20rpx;
  font-weight: bold;
}

.status-normal {
  background: #d4edda;
  color: #155724;
}

.status-paused {
  background: #fff3cd;
  color: #856404;
}

.status-stopped {
  background: #f8d7da;
  color: #721c24;
}

.status-unknown {
  background: #e2e3e5;
  color: #383d41;
}

.tag-text {
  font-size: 20rpx;
  font-weight: bold;
}

.item-insurance {
  font-size: 26rpx;
  color: #666;
}

.item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 5rpx;
}

.item-actions {
  display: flex;
  gap: 12rpx;
  margin: 0 10rpx;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6rpx;
  border: none;
  padding: 8rpx 16rpx;
  border-radius: 6rpx;
  font-size: 26rpx;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8rpx rgba(0, 0, 0, 0.1);
  min-width: 120rpx;
}

.action-btn:active {
  transform: scale(0.98);
}

.action-btn:hover {
  opacity: 0.9;
  box-shadow: 0 4px 12rpx rgba(0, 0, 0, 0.15);
}

.edit-btn {
  background: #667eea;
  color: white;
}

.delete-btn {
  background: #ff4d4f;
  color: white;
}

.action-text {
  font-size: 26rpx;
  font-weight: 500;
}

.item-date {
  font-size: 24rpx;
  color: #999;
}

.item-amount {
  font-size: 26rpx;
  color: #667eea;
  font-weight: bold;
}

.pagination-container {
  padding: 30rpx 20rpx;
  background: white;
  text-align: center;
  border-top: 1rpx solid #f0f0f0;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 375px) {
  .header-title {
    font-size: 28rpx;
  }
  
  .search-input {
    font-size: 26rpx;
  }
  
  .item-name {
    font-size: 28rpx;
  }
}
</style>
