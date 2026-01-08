<template>
  <div class="app-wrapper">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar-container">
        <div class="sidebar-logo" v-if="!isCollapse">
          <el-icon :size="30" color="#fff"><House /></el-icon>
          <span class="logo-title">人口普查系统</span>
        </div>
        <div class="sidebar-logo collapse" v-else>
          <el-icon :size="30" color="#fff"><House /></el-icon>
        </div>
        
        <el-menu
          :default-active="$route.path"
          class="el-menu-vertical"
          :collapse="isCollapse"
          router
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#409EFF"
        >
          <!-- 动态生成菜单 -->
          <template v-for="route in permissionRoutes" :key="route.path">
            <!-- 处理根路由情况，根路由没有meta但有子路由 -->
            <template v-if="route.path === '/'">
              <!-- 直接渲染根路由的子路由 -->
              <template v-for="childRoute in route.children" :key="childRoute.path">
                <el-menu-item v-if="childRoute.meta && childRoute.meta.title" :index="`${route.path}${childRoute.path}`" :class="{'admin-menu-item': userRole === 'ADMIN', 'user-menu-item': userRole === 'USER'}">
                  <el-icon v-if="childRoute.meta.icon === 'Odometer'">
                    <Odometer />
                  </el-icon>
                  <el-icon v-else-if="childRoute.meta.icon === 'User'">
                    <User />
                  </el-icon>
                  <el-icon v-else-if="childRoute.meta.icon === 'HomeFilled'">
                    <HomeFilled />
                  </el-icon>
                  <el-icon v-else-if="childRoute.meta.icon === 'UserFilled'">
                    <UserFilled />
                  </el-icon>
                  <el-icon v-else-if="childRoute.meta.icon === 'OfficeBuilding'">
                    <OfficeBuilding />
                  </el-icon>
                  <el-icon v-else-if="childRoute.meta.icon === 'Reading'">
                    <Reading />
                  </el-icon>
                  <el-icon v-else-if="childRoute.meta.icon === 'FirstAidKit'">
                    <FirstAidKit />
                  </el-icon>
                  <el-icon v-else-if="childRoute.meta.icon === 'Suitcase'">
                    <Suitcase />
                  </el-icon>
                  <el-icon v-else-if="childRoute.meta.icon === 'Briefcase'">
                    <Briefcase />
                  </el-icon>
                  <el-icon v-else-if="childRoute.meta.icon === 'CreditCard'">
                    <CreditCard />
                  </el-icon>
                  <el-icon v-else-if="childRoute.meta.icon === 'House'">
                    <House />
                  </el-icon>
                  <el-icon v-else-if="childRoute.meta.icon === 'Van'">
                    <Van />
                  </el-icon>
                  <el-icon v-else-if="childRoute.meta.icon === 'DataAnalysis'">
                    <DataAnalysis />
                  </el-icon>
                  <el-icon v-else-if="childRoute.meta.icon === 'Data'">
                    <DataLine />
                  </el-icon>
                  <el-icon v-else>
                    <Document />
                  </el-icon>
                  <template #title>{{ childRoute.meta.title }}</template>
                </el-menu-item>
              </template>
            </template>
            <!-- 正常路由处理 -->
            <template v-else-if="route.meta && route.meta.title">
              <!-- 无子路由的菜单 -->
              <el-menu-item v-if="!route.children || route.children.length === 0" :index="route.path" :class="{'admin-menu-item': userRole === 'ADMIN', 'user-menu-item': userRole === 'USER'}">
                <!-- 管理员和普通用户显示不同的图标 -->
                <el-icon v-if="route.meta.icon === 'Odometer'">
                  <Odometer />
                </el-icon>
                <el-icon v-else-if="route.meta.icon === 'User'">
                  <User />
                </el-icon>
                <el-icon v-else-if="route.meta.icon === 'HomeFilled'">
                  <HomeFilled />
                </el-icon>
                <el-icon v-else-if="route.meta.icon === 'UserFilled'">
                  <UserFilled />
                </el-icon>
                <el-icon v-else-if="route.meta.icon === 'OfficeBuilding'">
                  <OfficeBuilding />
                </el-icon>
                <el-icon v-else-if="route.meta.icon === 'Reading'">
                  <Reading />
                </el-icon>
                <el-icon v-else-if="route.meta.icon === 'FirstAidKit'">
                  <FirstAidKit />
                </el-icon>
                <el-icon v-else-if="route.meta.icon === 'Suitcase'">
                  <Suitcase />
                </el-icon>
                <el-icon v-else-if="route.meta.icon === 'Briefcase'">
                  <Briefcase />
                </el-icon>
                <el-icon v-else-if="route.meta.icon === 'CreditCard'">
                  <CreditCard />
                </el-icon>
                <el-icon v-else-if="route.meta.icon === 'House'">
                  <House />
                </el-icon>
                <el-icon v-else-if="route.meta.icon === 'Van'">
                  <Van />
                </el-icon>
                <el-icon v-else-if="route.meta.icon === 'DataAnalysis'">
                  <DataAnalysis />
                </el-icon>
                <el-icon v-else-if="route.meta.icon === 'Data'">
                  <DataLine />
                </el-icon>
                <el-icon v-else>
                  <Document />
                </el-icon>
                <template #title>{{ route.meta.title }}</template>
              </el-menu-item>
              
              <!-- 有子路由的菜单 -->
              <el-sub-menu v-else :index="route.path" :class="{'admin-menu-item': userRole === 'ADMIN', 'user-menu-item': userRole === 'USER'}">
                <template #title>
                  <el-icon v-if="route.meta.icon === 'Odometer'">
                    <Odometer />
                  </el-icon>
                  <el-icon v-else-if="route.meta.icon === 'User'">
                    <User />
                  </el-icon>
                  <el-icon v-else-if="route.meta.icon === 'HomeFilled'">
                    <HomeFilled />
                  </el-icon>
                  <el-icon v-else-if="route.meta.icon === 'UserFilled'">
                    <UserFilled />
                  </el-icon>
                  <el-icon v-else-if="route.meta.icon === 'OfficeBuilding'">
                    <OfficeBuilding />
                  </el-icon>
                  <el-icon v-else-if="route.meta.icon === 'Reading'">
                    <Reading />
                  </el-icon>
                  <el-icon v-else-if="route.meta.icon === 'FirstAidKit'">
                    <FirstAidKit />
                  </el-icon>
                  <el-icon v-else-if="route.meta.icon === 'Suitcase'">
                    <Suitcase />
                  </el-icon>
                  <el-icon v-else-if="route.meta.icon === 'Briefcase'">
                    <Briefcase />
                  </el-icon>
                  <el-icon v-else-if="route.meta.icon === 'CreditCard'">
                    <CreditCard />
                  </el-icon>
                  <el-icon v-else-if="route.meta.icon === 'House'">
                    <House />
                  </el-icon>
                  <el-icon v-else-if="route.meta.icon === 'Van'">
                    <Van />
                  </el-icon>
                  <el-icon v-else-if="route.meta.icon === 'DataAnalysis'">
                    <DataAnalysis />
                  </el-icon>
                  <el-icon v-else-if="route.meta.icon === 'Data'">
                    <DataLine />
                  </el-icon>
                  <el-icon v-else>
                    <Document />
                  </el-icon>
                  <span>{{ route.meta.title }}</span>
                </template>
              <el-menu-item
                v-for="childRoute in route.children"
                :key="childRoute.path"
                :index="`${route.path}/${childRoute.path}`"
                :class="{'admin-menu-item': userRole === 'ADMIN', 'user-menu-item': userRole === 'USER'}"
              >
                <el-icon v-if="childRoute.meta.icon === 'Odometer'">
                  <Odometer />
                </el-icon>
                <el-icon v-else-if="childRoute.meta.icon === 'User'">
                  <User />
                </el-icon>
                <el-icon v-else-if="childRoute.meta.icon === 'HomeFilled'">
                  <HomeFilled />
                </el-icon>
                <el-icon v-else-if="childRoute.meta.icon === 'UserFilled'">
                  <UserFilled />
                </el-icon>
                <el-icon v-else-if="childRoute.meta.icon === 'OfficeBuilding'">
                  <OfficeBuilding />
                </el-icon>
                <el-icon v-else-if="childRoute.meta.icon === 'Reading'">
                  <Reading />
                </el-icon>
                <el-icon v-else-if="childRoute.meta.icon === 'FirstAidKit'">
                  <FirstAidKit />
                </el-icon>
                <el-icon v-else-if="childRoute.meta.icon === 'Suitcase'">
                  <Suitcase />
                </el-icon>
                <el-icon v-else-if="childRoute.meta.icon === 'Briefcase'">
                  <Briefcase />
                </el-icon>
                <el-icon v-else-if="childRoute.meta.icon === 'CreditCard'">
                  <CreditCard />
                </el-icon>
                <el-icon v-else-if="childRoute.meta.icon === 'House'">
                  <House />
                </el-icon>
                <el-icon v-else-if="childRoute.meta.icon === 'Van'">
                  <Van />
                </el-icon>
                <el-icon v-else-if="childRoute.meta.icon === 'DataAnalysis'">
                  <DataAnalysis />
                </el-icon>
                <el-icon v-else-if="childRoute.meta.icon === 'Data'">
                  <DataLine />
                </el-icon>
                <el-icon v-else>
                  <Document />
                </el-icon>
                <template #title>{{ childRoute.meta.title }}</template>
              </el-menu-item>
            </el-sub-menu>
            </template>
          </template>
        </el-menu>
      </el-aside>
      
      <!-- 主体区域 -->
      <el-container>
        <!-- 头部 -->
        <el-header class="header-container">
          <div class="header-left">
            <div class="menu-toggle" @click="toggleCollapse">
              <el-icon :size="20">
                <Expand v-if="isCollapse" />
                <Fold v-else />
              </el-icon>
            </div>
            <h2>人口普查管理系统</h2>
          </div>
          <div class="header-right">
            <div class="user-role-tag">
              <el-tag :type="userRole === 'ADMIN' ? 'primary' : 'success'" size="small">
                {{ userRole === 'ADMIN' ? '管理员' : '普通用户' }}
              </el-tag>
            </div>
            <el-dropdown>
              <div class="user-info">
                <el-avatar :size="30" icon="UserFilled" />
                <span class="username">{{ userInfo.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <!-- 内容区 -->
        <el-main class="main-container">
          <router-view v-slot="{ Component }">
            <transition name="fade-transform" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
        
        <!-- 底部 -->
        <el-footer class="footer-container">
          <div class="footer-content">
            <p>© 2025 人口普查管理系统 - 用心管理每一份数据</p>
          </div>
        </el-footer>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { logout } from '@/api/auth'
import { useRouter } from 'vue-router'
// 导入Element Plus图标组件
import { 
  Odometer, User, HomeFilled, UserFilled, OfficeBuilding, 
  Reading, FirstAidKit, Suitcase, Briefcase, CreditCard, 
  House, Van, Document, DataAnalysis, DataLine, 
  Expand, Fold, ArrowDown, SwitchButton 
} from '@element-plus/icons-vue'

export default {
  name: 'Layout',
  components: {
    Odometer, User, HomeFilled, UserFilled, OfficeBuilding, 
    Reading, FirstAidKit, Suitcase, Briefcase, CreditCard, 
    House, Van, Document, DataAnalysis, DataLine, 
    Expand, Fold, ArrowDown, SwitchButton
  },
  setup() {
    const router = useRouter()
    return { router }
  },
  data() {
    return {
      isCollapse: false,
      userInfo: { username: 'admin', role: 'ADMIN' },
      // 导入所有图标组件
      icons: {
        Odometer: 'Odometer',
        User: 'User',
        HomeFilled: 'HomeFilled',
        UserFilled: 'UserFilled',
        OfficeBuilding: 'OfficeBuilding',
        Reading: 'Reading',
        FirstAidKit: 'FirstAidKit',
        Suitcase: 'Suitcase',
        Briefcase: 'Briefcase',
        CreditCard: 'CreditCard',
        House: 'House',
        Van: 'Van',
        Document: 'Document',
        DataAnalysis: 'DataAnalysis',
        Data: 'Data'
      }
    }
  },
  created() {
    // 从localStorage获取用户信息
    this.getUserInfo()
    // 监听localStorage变化，以便在其他页面修改用户信息时及时更新
    window.addEventListener('storage', this.handleStorageChange)
  },
  beforeUnmount() {
    // 移除事件监听
    window.removeEventListener('storage', this.handleStorageChange)
  },
  computed: {
    // 获取当前用户角色
    userRole() {
      return this.userInfo.role || 'USER'
    },
    
    // 根据用户角色过滤路由
    permissionRoutes() {
      // 获取所有路由
      const allRoutes = this.$router.options.routes
      
      // 过滤出需要显示的路由
      const filteredRoutes = allRoutes.filter(route => {
        // 跳过登录页
        if (route.path === '/login') {
          return false
        }
        
        // 跳过隐藏的路由（检查route.meta是否存在）
        if (route.meta && route.meta.hidden) {
          return false
        }
        
        // 检查路由是否有角色限制
        if (route.meta && route.meta.roles) {
          // 检查用户是否有访问权限
          return route.meta.roles.includes(this.userRole)
        }
        
        // 默认允许访问
        return true
      })
      
      // 处理子路由
      return filteredRoutes.map(route => {
        if (route.children && route.children.length > 0) {
          return {
            ...route,
            children: route.children.filter(childRoute => {
              // 检查子路由是否有角色限制
              if (childRoute.meta && childRoute.meta.roles) {
                return childRoute.meta.roles.includes(this.userRole)
              }
              return true
            })
          }
        }
        return route
      })
    }
  },
  methods: {
    toggleCollapse() {
      this.isCollapse = !this.isCollapse
    },
    getUserInfo() {
      try {
        // 从localStorage获取用户信息
        const userInfoStr = localStorage.getItem('userInfo')
        if (userInfoStr && userInfoStr !== 'null') { // 确保不是null或'null'字符串
          this.userInfo = JSON.parse(userInfoStr)
        }
        // 如果userInfoStr为空或'null'，则使用默认值
      } catch (error) {
        console.error('解析用户信息失败:', error)
        this.userInfo = { username: 'user', role: 'USER' } // 默认使用普通用户角色
      }
    },
    handleStorageChange() {
      // 当localStorage发生变化时，重新获取用户信息
      this.getUserInfo()
    },
    logout() {
      // 调用退出登录接口
      logout().then(response => {
        // 清除登录信息
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        // 跳转到登录页
        this.$router.push('/login')
        this.$message.success('退出登录成功')
      }).catch(error => {
        console.error(error)
        // 即使接口调用失败，也清除本地信息并跳转到登录页
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        this.$router.push('/login')
        this.$message.success('退出登录成功')
      })
    }
  }
}
</script>

<style scoped>
.app-wrapper {
  height: 100vh;
}

.sidebar-container {
  background-color: #545c64;
  transition: width 0.28s;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 1001;
  border-right: 1px solid #e6e6e6;
}

.sidebar-logo {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  background-color: #434b54;
  color: #fff;
  transition: padding 0.28s;
}

.sidebar-logo.collapse {
  justify-content: center;
  padding: 15px 0;
}

.logo-title {
  margin-left: 10px;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
  transition: opacity 0.28s;
}

.el-menu-vertical {
  height: calc(100% - 60px);
  border: none;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 200px;
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  box-shadow: 0 1px 8px rgba(0, 21, 41, 0.1);
  color: #333;
  padding: 0 20px;
  z-index: 1000;
  border-bottom: 1px solid #f0f0f0;
}

.header-left {
  display: flex;
  align-items: center;
}

.menu-toggle {
  cursor: pointer;
  padding: 5px;
  margin-right: 20px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.menu-toggle:hover {
  background-color: #f5f5f5;
}

.header-left h2 {
  margin: 0;
  color: #409EFF;
  font-size: 20px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-role-tag {
  margin-right: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 20px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.username {
  margin: 0 10px;
  font-size: 14px;
  color: #333;
}

.main-container {
  background-color: #f0f2f5;
  padding: 20px;
  overflow: auto;
}

.main-container :deep(.el-card) {
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border: none;
  margin-bottom: 20px;
}

.footer-container {
  background-color: #fff;
  color: #666;
  font-size: 14px;
  padding: 0;
  box-shadow: 0 -1px 4px rgba(0, 21, 41, 0.08);
  border-top: 1px solid #f0f0f0;
}

.footer-content {
  text-align: center;
  padding: 20px;
}

/* 动画效果 */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

/* 管理员和普通用户菜单样式差异化 */
.admin-menu-item {
  /* 管理员菜单样式 */
  font-weight: 600;
}

.admin-menu-item .el-icon {
  color: #409EFF;
}

.user-menu-item {
  /* 普通用户菜单样式 */
  font-weight: 400;
}

.user-menu-item .el-icon {
  color: #67C23A;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar-container {
    position: fixed;
    height: 100vh;
    z-index: 1001;
  }
}
</style>