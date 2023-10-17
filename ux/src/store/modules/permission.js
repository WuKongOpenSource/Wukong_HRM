import {
  asyncRouterMap
} from '@/router'
import Vue from 'vue'
import { adminGroupsTypeListAPI } from '@/api/admin/role'

/**
 *
 * @param {*} router
 * @param {*} authInfo
 */
function checkAuth(router, authInfo) {
  // 判断当前路由在权限数组中是否存在
  if (router.meta) {
    const metaInfo = router.meta
    if (!metaInfo.requiresAuth) {
      return true
    } else {
      if (metaInfo.permissions) {
        authInfo = { ...authInfo }
        return forCheckPermission(metaInfo.permissions, authInfo)
      } else if (metaInfo.permissionList) { // 一个路由受多个权限判断
        for (let index = 0; index < metaInfo.permissionList.length; index++) {
          authInfo = { ...authInfo }
          const permission = forCheckPermission(metaInfo.permissionList[index], authInfo)
          if (permission) {
            return true
          }
        }
        return false
      } else if (metaInfo.authFun) { // 方法 主要验证人资 hrmTips
        return metaInfo.authFun()
      }
    }
  }
  return true
}

/**
 * 循环关键字检查权限
 * @param {*} permissions 权限关键数组
 * @param {*} authInfo
 */
const forCheckPermission = function(permissions, authInfo) {
  for (let index = 0; index < permissions.length; index++) {
    const key = permissions[index]
    authInfo = authInfo[key]
    if (!authInfo) {
      return false
    } else if (permissions.length - 1 === index) {
      return true
    }
  }
}

/**
 *
 * @param {*} routers
 * @param {*} authInfo
 */
const filterAsyncRouter = function(routers, authInfo) {
  const res = []
  routers.forEach(router => {
    const tmp = {
      ...router
    }
    if (checkAuth(tmp, authInfo)) {
      if (tmp.children) {
        tmp.children = filterAsyncRouter(tmp.children, authInfo)
      }
      res.push(tmp)
    }
  })
  return res
}

/**
 * 忽略用于菜单展示的传参路由
 * @param {*} routers
 * @param {*} authInfo
 */
const filterIgnoreRouter = function(routers) {
  const res = []
  routers.forEach(router => {
    const tmp = {
      ...router
    }
    if (!tmp.ignore) {
      if (tmp.children) {
        tmp.children = filterIgnoreRouter(tmp.children)
      }
      res.push(tmp)
    }
  })
  return res
}

/**
 * 路由重定向和角色路由完善
 */
const perfectRouter = function(authInfo, result) {
  getGroupData(authInfo, (groupData) => {
    const routerObj = {}
    let addRouter = []
    let redirect = ''
    let topRedirect = '' // 置顶的第一个路由
    for (let index = 0; index < asyncRouterMap.length; index++) {
      const mainRouter = asyncRouterMap[index]
      const accessedRouters = filterAsyncRouter(mainRouter.router, authInfo)
      for (let childIndex = 0; childIndex < accessedRouters.length; childIndex++) {
        const element = accessedRouters[childIndex]

        // 处理系统管理逻辑
        if (mainRouter.type == 'manage' && groupData.requiresAuth) {
          const authItem = getAuthItem(accessedRouters)
          const roleMenus = groupData.list.map(item => {
            return {
              name: 'role-auth',
              path: `role-auth/${item.roleType}/${encodeURI(item.name)}`,
              ignore: true, // 不加入路由 仅菜单展示
              meta: {
                title: item.name
              }
            }
          })

          if (roleMenus && roleMenus.length > 0) {
            const roleFirstChild = authItem.children[0]
            roleFirstChild.meta.redirect = roleMenus[0].path
            authItem.children = authItem.children.concat(roleMenus)
          }
        }

        if (element.children && element.children.length > 0) {
          const firstChild = element.children[0]
          const childPath = firstChild.meta ? firstChild.meta.redirect || firstChild.path : firstChild.path
          element.redirect = element.path + '/' + childPath
        }

        // 获取跳转
        if (element.redirect) {
          if (!redirect) {
            redirect = element.redirect
          }

          if (authInfo.wkFirstModel && !topRedirect) {
            const modelName = {
              crm: 'crm',
              taskExamine: 'taskExamine',
              log: 'workLog',
              book: 'addressBook',
              bi: 'bi',
              calendar: 'calendar',
              hrm: 'hrm',
              oa: 'oa'
            }[authInfo.wkFirstModel]
            if (modelName == mainRouter.type) {
              topRedirect = element.redirect
            }
          }

          // 为导航头 获取每个模块的 重定向 url
          accessedRouters.push({
            path: `/${mainRouter.type}`,
            name: mainRouter.type,
            redirect: element.redirect,
            hidden: true
          })

          break
        }
      }
      routerObj[mainRouter.type] = accessedRouters
      addRouter = addRouter.concat(filterIgnoreRouter(accessedRouters))
    }

    if (redirect || topRedirect) {
      addRouter.push({
        path: '/',
        redirect: topRedirect || redirect,
        hidden: true
      })
    }
    if (result) {
      result({ router: routerObj, addRouter })
    }
  })
}

/**
 *
 */
function getAuthItem(array) {
  return array.find(item => {
    return item.name == 'manage-role-auth'
  })
}

/**
 * 获取角色列表
 * @param {*} authInfo 授权信息
 * @param {*} result 回调
 */
function getGroupData(authInfo, result) {
  if (authInfo && authInfo.manage && authInfo.manage.permission) {
    adminGroupsTypeListAPI().then((response) => {
      if (result) {
        result({
          requiresAuth: true,
          list: response.data || []
        })
      }
    }).catch(() => {})
  } else {
    if (result) {
      result({
        requiresAuth: false
      })
    }
  }
}

const permission = {
  state: {
    addRouters: [],
    crmRouters: [],
    taskExamineRouters: [],
    workLogRouters: [],
    addressBookRouters: [],
    biRouters: [],
    manageRouters: [],
    hrmRouters: [],
    oaRouters: []
  },
  mutations: {
    SET_ROUTERS: (state, data) => {
      state.addRouters = data.addRouter
      state.crmRouters = data.router.crm || []
      state.workLogRouters = data.router.workLog || []
      state.taskExamineRouters = data.router.taskExamine || []
      state.addressBookRouters = data.router.addressBook || []
      state.biRouters = data.router.bi || []
      state.manageRouters = data.router.manage || []
      state.hrmRouters = data.router.hrm || []
      state.oaRouters = data.router.oa || []
    },

    /**
     * 客户管理待办消息数
     */
    SET_CRMROUTERSNUM: (state, num) => {
      const messageItem = state.crmRouters[1]
      messageItem.children[0].meta.num = num
      Vue.set(state.crmRouters, 1, messageItem)
    }
  },
  actions: {
    GenerateRoutes({
      commit,
      state
    }, data) {
      return new Promise(resolve => {
        // 路由完善
        perfectRouter(data, (routers) => {
          console.log('set routers', routers)
          commit('SET_ROUTERS', routers)
          resolve()
        })
      })
    }
  },

  getters: {
    // 路由
    addRouters(state, getters, rootState, rootGetters) {
      return state.addRouters
    },
    // addRouters: state => state.addRouters,
    crmRouters: state => state.crmRouters,
    taskExamineRouters: state => state.taskExamineRouters,
    workLogRouters: state => state.workLogRouters,
    addressBookRouters: state => state.addressBookRouters,
    biRouters: state => state.biRouters,
    manageRouters: state => state.manageRouters,
    hrmRouters: state => state.hrmRouters,
    oaRouters: state => state.oaRouters
  }
}

export default permission
