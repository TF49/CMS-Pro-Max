/**
 * 权限管理模块
 * 用于根据用户角色生成可访问的路由和菜单
 */

/**
 * 检查用户是否有访问该路由的权限
 * @param {Object} route - 路由对象
 * @param {Array} roles - 用户角色列表
 * @returns {boolean} - 是否有访问权限
 */
export function hasPermission(route, roles) {
  if (route.meta && route.meta.roles) {
    // 如果路由配置了roles，则检查用户角色是否在允许的列表中
    return roles.some(role => route.meta.roles.includes(role))
  } else {
    // 未配置roles的路由默认允许访问
    return true
  }
}

/**
 * 递归过滤异步路由表，返回符合用户角色权限的路由表
 * @param {Array} routes - 所有路由列表
 * @param {Array} roles - 用户角色列表
 * @returns {Array} - 过滤后的路由列表
 */
export function filterAsyncRoutes(routes, roles) {
  const res = []

  routes.forEach(route => {
    const tmp = { ...route }
    // 检查当前路由是否有权限访问
    if (hasPermission(tmp, roles)) {
      // 如果有子路由，递归过滤子路由
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, roles)
      }
      res.push(tmp)
    }
  })

  return res
}

/**
 * 生成可访问的路由列表
 * @param {Array} roles - 用户角色列表
 * @returns {Array} - 可访问的路由列表
 */
export function generateRoutes(roles) {
  // 导入所有路由
  const allRoutes = require('@/router/index.js').default.options.routes
  
  // 过滤出可访问的路由
  const accessedRoutes = filterAsyncRoutes(allRoutes, roles)
  
  return accessedRoutes
}

/**
 * 生成侧边栏菜单
 * @param {Array} routes - 可访问的路由列表
 * @returns {Array} - 侧边栏菜单列表
 */
export function generateMenus(routes) {
  const menus = []
  
  routes.forEach(route => {
    // 跳过隐藏的路由
    if (route.meta && route.meta.hidden) {
      return
    }
    
    const menu = {
      path: route.path,
      name: route.name,
      title: route.meta.title,
      icon: route.meta.icon,
      children: []
    }
    
    // 处理子路由
    if (route.children && route.children.length > 0) {
      // 过滤掉隐藏的子路由
      const visibleChildren = route.children.filter(child => !child.meta || !child.meta.hidden)
      if (visibleChildren.length > 0) {
        menu.children = generateMenus(visibleChildren)
      }
    }
    
    // 只添加有title的路由到菜单
    if (menu.title) {
      menus.push(menu)
    }
  })
  
  return menus
}
