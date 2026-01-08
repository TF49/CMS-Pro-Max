import { createRouter, createWebHashHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

// 登录页面
const Login = () => import('@/views/Login.vue')
// 仪表盘页面
const Dashboard = () => import('@/views/Dashboard.vue')
// 居民管理页面
const ResidentManagement = () => import('@/views/ResidentManagement.vue')
// 户籍管理页面
const HouseholdManagement = () => import('@/views/HouseholdManagement.vue')
// 教育管理页面
const EducationManagement = () => import('@/views/EducationManagement.vue')
// 医疗管理页面
const MedicalManagement = () => import('@/views/MedicalManagement.vue')
// 就业管理页面
const EmploymentManagement = () => import('@/views/EmploymentManagement.vue')
// 社保管理页面
const SocialSecurityManagement = () => import('@/views/SocialSecurityManagement.vue')
// 房产管理页面
const PropertyManagement = () => import('@/views/PropertyManagement.vue')
// 车辆管理页面
const VehicleManagement = () => import('@/views/VehicleManagement.vue')
// 数据详情页面
const DataDetail = () => import('@/views/DataManagement.vue')
// 用户管理页面
const UserManagement = () => import('@/views/UserManagement.vue')

const routes = [
  // 登录页面
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { hidden: true }
  },
  
  // 系统首页
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    meta: { title: '系统首页', icon: 'Odometer', roles: ['ADMIN', 'USER'] },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '系统首页', icon: 'Odometer', roles: ['ADMIN', 'USER'] }
      }
    ]
  },
  
  // 人口管理模块
  {
    path: '/population',
    component: Layout,
    redirect: '/population/resident',
    meta: { title: '人口管理', icon: 'User', roles: ['ADMIN', 'USER'] },
    children: [
      {
        path: 'household',
        name: 'PopulationHousehold',
        component: HouseholdManagement,
        meta: { title: '户籍管理', icon: 'HomeFilled', roles: ['ADMIN', 'USER'] }
      },
      {
        path: 'resident',
        name: 'PopulationResident',
        component: ResidentManagement,
        meta: { title: '居民管理', icon: 'UserFilled', roles: ['ADMIN', 'USER'] }
      }
    ]
  },
  
  // 社会管理模块
  {
    path: '/social',
    component: Layout,
    redirect: '/social/education',
    meta: { title: '社会管理', icon: 'OfficeBuilding', roles: ['ADMIN', 'USER'] },
    children: [
      {
        path: 'education',
        name: 'SocialEducation',
        component: EducationManagement,
        meta: { title: '教育管理', icon: 'Reading', roles: ['ADMIN', 'USER'] }
      },
      {
        path: 'medical',
        name: 'SocialMedical',
        component: MedicalManagement,
        meta: { title: '医疗管理', icon: 'FirstAidKit', roles: ['ADMIN', 'USER'] }
      }
    ]
  },
  
  // 就业社保模块
  {
    path: '/employment-security',
    component: Layout,
    redirect: '/employment-security/employment',
    meta: { title: '就业社保', icon: 'Suitcase', roles: ['ADMIN', 'USER'] },
    children: [
      {
        path: 'employment',
        name: 'Employment',
        component: EmploymentManagement,
        meta: { title: '就业管理', icon: 'Document', roles: ['ADMIN', 'USER'] }
      },
      {
        path: 'social-security',
        name: 'SocialSecurity',
        component: SocialSecurityManagement,
        meta: { title: '社保管理', icon: 'Document', roles: ['ADMIN', 'USER'] }
      }
    ]
  },
  
  // 财产管理模块
  {
    path: '/property',
    component: Layout,
    redirect: '/property/property-list',
    meta: { title: '财产管理', icon: 'House', roles: ['ADMIN', 'USER'] },
    children: [
      {
        path: 'property-list',
        name: 'Property',
        component: PropertyManagement,
        meta: { title: '房产管理', icon: 'Document', roles: ['ADMIN', 'USER'] }
      },
      {
        path: 'vehicle',
        name: 'Vehicle',
        component: VehicleManagement,
        meta: { title: '车辆管理', icon: 'Van', roles: ['ADMIN', 'USER'] }
      }
    ]
  },
  
  // 数据管理模块
  {
    path: '/data',
    component: Layout,
    redirect: '/data/detail',
    meta: { title: '数据管理', icon: 'DataAnalysis', roles: ['ADMIN'] },
    children: [
      {
        path: 'detail',
        name: 'DataDetail',
        component: DataDetail,
        meta: { title: '数据详情', icon: 'Data', roles: ['ADMIN'] }
      }
    ]
  },
  
  // 用户管理模块
  {
    path: '/system',
    component: Layout,
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'Setting', roles: ['ADMIN'] },
    children: [
      {
        path: 'user',
        name: 'UserManagement',
        component: UserManagement,
        meta: { title: '用户管理', icon: 'User', roles: ['ADMIN'] }
      }
    ]
  },
  
  // 直接访问子路由的重定向路由
  {
    path: '/employment',
    redirect: '/employment-security/employment',
    meta: { roles: ['ADMIN', 'USER'], hidden: true }
  },
  {
    path: '/household',
    redirect: '/population/household',
    meta: { roles: ['ADMIN', 'USER'], hidden: true }
  },
  {
    path: '/resident',
    redirect: '/population/resident',
    meta: { roles: ['ADMIN', 'USER'], hidden: true }
  },
  {
    path: '/education',
    redirect: '/social/education',
    meta: { roles: ['ADMIN', 'USER'], hidden: true }
  },
  {
    path: '/medical',
    redirect: '/social/medical',
    meta: { roles: ['ADMIN', 'USER'], hidden: true }
  },
  {
    path: '/social-security',
    redirect: '/employment-security/social-security',
    meta: { roles: ['ADMIN', 'USER'], hidden: true }
  },
  {
    path: '/property-list',
    redirect: '/property/property-list',
    meta: { roles: ['ADMIN', 'USER'], hidden: true }
  },
  {
    path: '/vehicle',
    redirect: '/property/vehicle',
    meta: { roles: ['ADMIN', 'USER'], hidden: true }
  },
  {
    path: '/detail',
    redirect: '/data/detail',
    meta: { roles: ['ADMIN'], hidden: true }
  },
  
  // 错误页面
  {
    path: '/403',
    name: '403',
    component: () => import('@/views/403.vue'),
    meta: { hidden: true }
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/404.vue'),
    meta: { hidden: true }
  },
  // 捕获所有未匹配的路由，重定向到404页面
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
    meta: { hidden: true }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  try {
    // 获取token
    const token = localStorage.getItem('token')
    
    // 如果要去登录页，直接放行
    if (to.path === '/login') {
      next()
      return
    }
    
    // 如果没有token，跳转到登录页
    if (!token) {
      next('/login')
      return
    }
    
    // 获取用户角色，处理各种异常情况
    let userInfo = {}
    try {
      const userInfoStr = localStorage.getItem('userInfo')
      if (userInfoStr && userInfoStr !== 'null') {
        userInfo = JSON.parse(userInfoStr)
      }
    } catch (e) {
      console.error('解析用户信息失败:', e)
      userInfo = {}
    }
    const roles = userInfo.role ? [userInfo.role] : []
    
    // 关闭可能存在的所有弹窗
    if (window.eventBus) {
      window.eventBus.$emit('closeAllModals')
    }
    
    // 检查路由是否需要权限
    if (to.meta && to.meta.roles) {
      // 检查用户是否有访问权限
      const hasPermission = roles.some(role => to.meta.roles.includes(role))
      if (hasPermission) {
        next()
      } else {
        // 没有权限，跳转到403页面
        next('/403')
      }
    } else {
      // 不需要权限的路由直接放行
      next()
    }
  } catch (error) {
    console.error('路由守卫错误:', error)
    // 如果发生错误，跳转到登录页
    next('/login')
  }
})

export default router