<template>
  <view class="education-list-container">
    <!-- 头部导航 -->
    <view class="header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="header-left">
        <button class="back-btn" @click="handleBack">
          <uni-icons type="back" size="28" color="#333"></uni-icons>
        </button>
      </view>
      <view class="header-title">教育管理</view>
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
          placeholder="搜索姓名、学校名称"
          v-model="keyword"
          @confirm="handleSearch"
        />
        <button 
          class="search-btn" 
          @click="handleSearch"
          :disabled="loading"
        >
          <text class="search-btn-text">搜索</text>
        </button>
        <button 
          class="reset-btn" 
          @click="handleReset"
          :disabled="loading || !keyword"
        >
          <text class="reset-btn-text">重置</text>
        </button>
      </view>
    </view>
    
    <!-- 内容区域 -->
    <view class="content-container">
      <!-- 加载状态 -->
      <view v-if="loading" class="loading-container">
        <view class="loading-spinner">
          <view class="spinner-circle"></view>
          <view class="spinner-circle"></view>
          <view class="spinner-circle"></view>
        </view>
        <text class="loading-text">加载中...</text>
      </view>
      
      <!-- 空状态 -->
      <view v-else-if="educations.length === 0" class="empty-container">
        <view class="empty-icon">
          <uni-icons type="book" size="64" color="#ccc"></uni-icons>
        </view>
        <!-- 区分搜索结果为空和本来就没有数据的情况 -->
        <text v-if="keyword" class="empty-title">找不到该教育记录</text>
        <text v-else class="empty-title">暂无教育记录</text>
        
        <text v-if="keyword" class="empty-subtitle">没有找到匹配的教育记录，请尝试其他关键词</text>
        <text v-else class="empty-subtitle">您可以点击右上角添加教育记录</text>
        
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
            v-for="item in educations" 
            :key="item.id"
            class="education-item"
          >
            <template #header>
              <view class="education-header" @click="handleDetail(item)">
                <text class="education-name">{{ item.residentName }}</text>
              </view>
            </template>
            <template #footer>
              <view class="education-footer">
                <view class="education-actions">
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
            :page-size-options="['10', '20', '50']"
            @change="handlePageChange"
          ></uni-pagination>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { get, del } from '@/utils/request'
import { getCache, setCache } from '@/utils/cache'
import { debounce } from '@/utils/debounce'
import { showLoading, hideLoading, showError } from '@/utils/loading'

export default {
  data() {
    return {
      educations: [],
      keyword: '',
      currentPage: 1,
      pageSize: 10,
      total: 0,
      totalPages: 0,
      loading: false,
      statusBarHeight: 0,
      dataChangeHandler: null
    }
  },
  onLoad() {
    this.getStatusBarHeight()
    this.loadEducations()
    this.registerWebSocketListener()
  },
  onShow() {
    // 页面显示时重新加载数据，不使用缓存，确保数据最新
    this.loadEducations(false)
  },
  onUnload() {
    this.unregisterWebSocketListener()
  },
  methods: {
    // 返回上一页
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
    
    // 获取状态栏高度
    getStatusBarHeight() {
      // #ifdef MP-WEIXIN
      const systemInfo = uni.getSystemInfoSync()
      this.statusBarHeight = systemInfo.statusBarHeight || 0
      // #endif
    },
    
    // 获取教育水平中文名称
    getEducationLevelText(level) {
      switch (level) {
        case 'PRIMARY': return '小学'
        case 'JUNIOR': return '初中'
        case 'SENIOR': return '高中'
        case 'COLLEGE': return '大专'
        case 'BACHELOR': return '本科'
        case 'MASTER': return '硕士'
        default: return '博士'
      }
    },
    
    // 获取教育信息备注
    getEducationNote(item) {
      return `${this.getEducationLevelText(item.educationLevel)} - ${item.enrollmentDate} 至 ${item.graduationDate || '至今'}`
    },
    
    // 加载教育列表（带缓存）
    loadEducations(useCache = true) {
      // 注意：后端已经实现了权限控制，普通用户只能获取到自己关联的数据
      // 所以这里不传递keyword，只获取所有关联数据后在前端进行过滤
      
      // 生成缓存键（不包含keyword，因为我们在前端过滤）
      const cacheKey = `education_list_${this.currentPage}_${this.pageSize}`
      
      // 尝试从缓存获取
      if (useCache) {
        const cachedData = getCache(cacheKey)
        if (cachedData) {
          let allEducations = cachedData.data || []
          let total = cachedData.total || 0
          
          // 在前端进行搜索过滤
          if (this.keyword) {
            const trimmedKeyword = this.keyword.trim()
            this.educations = allEducations.filter(item => {
              // 匹配姓名或学校名称
              return item.residentName?.includes(trimmedKeyword) || 
                     item.schoolName?.includes(trimmedKeyword)
            })
            this.total = this.educations.length
          } else {
            this.educations = allEducations
            this.total = total
          }
          
          this.totalPages = Math.ceil(this.total / this.pageSize)
          return
        }
      }
      
      this.loading = true
      showLoading('加载中...')
      
      const params = {
        page: this.currentPage,
        size: this.pageSize
        // 移除keyword参数，不在后端进行搜索
      }
      
      get('/education/list', params)
        .then(res => {
          if (res.code === 200) {
            let allEducations = res.data.data || []
            const total = res.data.total || 0
            
            // 在前端进行搜索过滤
            if (this.keyword) {
              const trimmedKeyword = this.keyword.trim()
              this.educations = allEducations.filter(item => {
                // 匹配姓名或学校名称
                return item.residentName?.includes(trimmedKeyword) || 
                       item.schoolName?.includes(trimmedKeyword)
              })
              this.total = this.educations.length
            } else {
              this.educations = allEducations
              this.total = total
            }
            
            this.totalPages = Math.ceil(this.total / this.pageSize)
            
            // 缓存数据（5分钟过期）
            setCache(cacheKey, { data: allEducations, total }, 5 * 60 * 1000)
          } else {
            showError(res.msg || res.message || '获取数据失败')
          }
        })
        .catch(err => {
          console.error('获取教育列表失败', err)
          showError('网络错误，请稍后重试')
        })
        .finally(() => {
          this.loading = false
          hideLoading()
        })
    },
    
    // 搜索（防抖处理）
    handleSearch: debounce(function() {
      this.currentPage = 1
      this.loadEducations(false) // 搜索时不使用缓存
    }, 500),
    
    // 重置搜索
    handleReset() {
      this.keyword = ''
      this.currentPage = 1
      this.loadEducations(false) // 重置时不使用缓存
    },
    
    // 分页变化
    handlePageChange(e) {
      this.currentPage = e.current
      this.pageSize = e.size || this.pageSize
      this.loadEducations()
    },
    
    // 新增教育信息
    handleAdd() {
      uni.navigateTo({
        url: '/pages/education/add'
      })
    },
    
    // 修改教育信息
    handleEdit(item) {
      uni.navigateTo({
        url: `/pages/education/edit?id=${item.id}`
      })
    },
    
    // 删除教育信息
    handleDelete(item) {
      uni.showModal({
        title: '确认删除',
        content: `确定要删除教育记录 ${item.id} 吗？`,
        success: (res) => {
          if (res.confirm) {
            // 调用删除API
            del(`/education/delete/${item.id}`)
              .then(res => {
                if (res.code === 200) {
                  uni.showToast({
                    title: '删除成功',
                    icon: 'success'
                  })
                  // 重新加载列表
                  this.loadEducations(false)
                } else {
                  uni.showToast({
                    title: res.message || '删除失败',
                    icon: 'none'
                  })
                }
              })
              .catch(err => {
                console.error('删除教育记录失败', err)
                uni.showToast({
                  title: '删除失败',
                  icon: 'none'
                })
              })
          }
        }
      })
    },
    
    // 查看详情
    handleDetail(item) {
      uni.navigateTo({
        url: `/pages/education/detail?id=${item.id}`
      })
    },
    
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },

    /**
     * 注册WebSocket监听器
     */
    registerWebSocketListener() {
      this.dataChangeHandler = (message) => {
        console.log('教育列表页面收到数据变更通知:', message)

        if (message.entityType === 'EDUCATION') {
          const action = message.action

          switch (action) {
            case 'CREATE':
              this.loadEducations(false)
              uni.showToast({
                title: '有新的教育记录',
                icon: 'none',
                duration: 2000
              })
              break
            case 'UPDATE':
              this.loadEducations(false)
              uni.showToast({
                title: '教育信息已更新',
                icon: 'none',
                duration: 2000
              })
              break
            case 'DELETE':
              this.loadEducations(false)
              uni.showToast({
                title: '教育记录已删除',
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
.education-list-container {
  background: #f5f7fa;
  min-height: 100vh;
}

/* 头部导航 */
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

/* 搜索区域 */
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

/* 内容区域 */
.content-container {
  padding: 20rpx;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  margin-bottom: 20rpx;
}

.spinner-circle {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: #667eea;
  animation: bounce 1.4s ease-in-out infinite both;
}

.spinner-circle:nth-child(1) {
  animation-delay: -0.32s;
}

.spinner-circle:nth-child(2) {
  animation-delay: -0.16s;
}

.spinner-circle:nth-child(3) {
  animation-delay: 0s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.loading-text {
  font-size: 28rpx;
  color: #666;
}

/* 空状态 */
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

/* 列表容器 */
.list-container {
  background: white;
  border-radius: 16rpx;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

/* 教育记录项 */
.education-item {
  background: white;
  border-bottom: 1rpx solid #f0f0f0;
  transition: all 0.3s ease;
}

.education-item:last-child {
  border-bottom: none;
}

.education-item:hover {
  background: #fafafa;
  transform: translateY(-2rpx);
}

/* 教育记录头部 */
.education-header {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
  padding: 5rpx 0;
}

.education-basic-info {
  display: flex;
  align-items: center;
  gap: 15rpx;
}

.education-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.education-tag {
  padding: 5rpx 15rpx;
  border-radius: 15rpx;
  font-size: 20rpx;
  font-weight: bold;
}

.tag-studying {
  background: #e8f5e8;
  color: #4caf50;
}

.tag-graduated {
  background: #e3f2fd;
  color: #2196f3;
}

.tag-text {
  font-size: 20rpx;
  font-weight: bold;
}

.education-school {
  font-size: 26rpx;
  color: #666;
}

/* 教育记录底部 */
.education-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 5rpx;
}

.education-actions {
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

.education-level {
  font-size: 24rpx;
  color: #999;
}

.education-date {
  font-size: 22rpx;
  color: #aaa;
}

/* 分页容器 */
.pagination-container {
  padding: 30rpx 20rpx;
  background: white;
  text-align: center;
  border-top: 1rpx solid #f0f0f0;
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
/* 小屏设备 (<= 375px) */
@media (max-width: 375px) {
  .education-list-container {
    padding: 0;
  }
  
  .header {
    padding: 0 16rpx;
  }
  
  .header-title {
    font-size: 28rpx;
  }
  
  .search-section {
    padding: 16rpx;
  }
  
  .search-container {
    flex-direction: column;
    gap: 10rpx;
  }
  
  .search-input {
    font-size: 26rpx;
    height: 65rpx;
  }
  
  .search-btn, .reset-btn {
    width: 100%;
    margin-left: 0;
    margin-top: 10rpx;
  }
  
  .content-container {
    padding: 16rpx;
  }
  
  .education-name {
    font-size: 28rpx;
  }
  
  .education-school {
    font-size: 24rpx;
  }
  
  .education-level {
    font-size: 22rpx;
  }
  
  .education-date {
    font-size: 20rpx;
  }
  
  .pagination-container {
    padding: 20rpx 16rpx;
  }
}

/* 中屏设备 (376px - 414px) */
@media (min-width: 376px) and (max-width: 414px) {
  .education-name {
    font-size: 30rpx;
  }
  
  .education-school {
    font-size: 26rpx;
  }
}

/* 大屏设备 (> 414px) */
@media (min-width: 415px) {
  .education-list-container {
    max-width: 750rpx;
    margin: 0 auto;
  }
  
  .education-name {
    font-size: 32rpx;
  }
  
  .education-school {
    font-size: 28rpx;
  }
  
  .content-container {
    padding: 30rpx;
  }
}

/* 横屏适配 */
@media (orientation: landscape) {
  .search-container {
    flex-direction: row;
  }
  
  .search-btn, .reset-btn {
    width: auto;
    margin-top: 0;
  }
}
</style>