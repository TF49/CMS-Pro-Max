/**
 * 权限管理状态模块
 * 用于存储用户角色、可访问路由和菜单列表
 */
import { generateRoutes, generateMenus } from '@/utils/permission'

const state = {
  // 用户角色列表
  roles: [],
  // 可访问的路由列表
  routes: [],
  // 侧边栏菜单列表
  menus: [],
  // 是否已生成路由
  generated: false
}

const mutations = {
  // 设置用户角色
  SET_ROLES(state, roles) {
    state.roles = roles
  },
  // 设置可访问的路由
  SET_ROUTES(state, routes) {
    state.routes = routes
  },
  // 设置侧边栏菜单
  SET_MENUS(state, menus) {
    state.menus = menus
  },
  // 设置路由生成状态
  SET_GENERATED(state, generated) {
    state.generated = generated
  },
  // 重置权限状态
  RESET_PERMISSION(state) {
    state.roles = []
    state.routes = []
    state.menus = []
    state.generated = false
  }
}

const actions = {
  /**
   * 生成可访问的路由和菜单
   * @param {Object} commit - Vuex commit函数
   * @param {Array} roles - 用户角色列表
   * @returns {Promise} - Promise对象
   */
  generateRoutes({ commit }, roles) {
    return new Promise(resolve => {
      try {
        // 生成可访问的路由
        const accessedRoutes = generateRoutes(roles)
        
        // 生成侧边栏菜单
        const menus = generateMenus(accessedRoutes)
        
        // 更新状态
        commit('SET_ROLES', roles)
        commit('SET_ROUTES', accessedRoutes)
        commit('SET_MENUS', menus)
        commit('SET_GENERATED', true)
        
        resolve(accessedRoutes)
      } catch (error) {
        console.error('生成路由失败:', error)
        resolve([])
      }
    })
  },
  
  /**
   * 重置权限状态
   * @param {Object} commit - Vuex commit函数
   */
  resetPermission({ commit }) {
    commit('RESET_PERMISSION')
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
