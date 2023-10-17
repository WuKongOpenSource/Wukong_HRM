import {
  authorizationAPI,
  logoutAPI
} from '@/api/login'
import {
  userListAPI,
  depListAPI,
  adminUserQueryOrgInfoAPI,
  adminIndexAuthListAPI
} from '@/api/common'

import {
  resetRouter
} from '@/router'

import {
  adminUsersReadAPI
} from '@/api/user/personCenter'

import {
  addAuth,
  removeAuth
} from '@/utils/auth'
import Lockr from 'lockr'
import { debounce } from 'throttle-debounce'
import Cookies from 'js-cookie'

import { LOCAL_CLEAR_PAGE_TIME, LOCAL_FREE_TIME, LOCAL_ADMIN_TOKEN, COOKIE_ADMIN_TOKEN } from '@/utils/constants.js'
import store from '@/store'

function loopDeptMap(list, map, deptList) {
  list.forEach(item => {
    map[`dept-${item.deptId}`] = item
    deptList.push(item) // 添加部门搜索数据源

    if (item.children) {
      loopDeptMap(item.children, map, deptList)
    }
  })
}

const user = {
  state: {
    userInfo: null, // 用户信息
    // 权限信息
    allAuth: null, // 总权限信息 默认空 调整动态路由
    userDeptObj: {
      disableUserList: [],
      userMap: {},
      deptList: []
    }, // 查询完整组织架构信息
    userDeptMap: {}, // 根据user + id 和 dept + id 形成的对象
    searchUserDept: {
      userList: [],
      deptList: [],
      disableUserList: []
    }, // 用于搜索  一维数组
    userList: [], // 管理后台员工和部门
    deptList: [],
    crm: {}, // 客户管理
    bi: {}, // 商业智能
    manage: {}, // 管理后台
    oa: {}, // 办公
    call: false, // 呼叫中心是否开启
    hrm: {} // 人力资源
  },

  mutations: {
    SET_USERINFO: (state, userInfo) => {
      state.userInfo = userInfo
    },
    SET_ALLAUTH: (state, allAuth) => {
      state.allAuth = allAuth
    },
    SET_CRM: (state, crm) => {
      state.crm = crm
    },
    SET_BI: (state, bi) => {
      state.bi = bi
    },
    SET_MANAGE: (state, manage) => {
      state.manage = manage
    },
    SET_OA: (state, oa) => {
      state.oa = oa
    },
    SET_HRM: (state, hrm) => {
      state.hrm = hrm
    },
    SET_CALL: (state, call) => {
      state.call = call
    },
    SET_AUTH: (state, data) => {
      addAuth(data.token)
    },
    SET_USERLIST: (state, data) => {
      state.userList = data
    },
    SET_DEPTLIST: (state, data) => {
      state.deptList = data
    },
    SET_USERDEPTOBJ: (state, data) => {
      state.userDeptObj = data
    },
    SET_USERDEPTMAP: (state, data) => {
      const tempMap = {
        // 'dept-0': {
        //   deptId: 0,
        //   name: '全公司'
        // }
      }
      const userList = []
      const deptList = []
      loopDeptMap(data.deptList, tempMap, deptList)

      const disableUserList = []
      data.disableUserList.forEach(user => {
        tempMap[`user-${user.userId}`] = user
        disableUserList.push(user)
      })

      const userMap = data.userMap || {}
      let tempList = []
      Object.keys(userMap).forEach(key => {
        tempList = tempList.concat(userMap[key])
      })
      tempList.forEach(user => {
        tempMap[`user-${user.userId}`] = user
      })

      // 同时更新 搜索 和 map
      state.searchUserDept = {
        userList,
        deptList,
        disableUserList
      }
      state.userDeptMap = tempMap
    }
  },

  actions: {
    /**
     * 登录换取 token
     */
    AuthorizationLogin({ commit, dispatch }, data) {
      return new Promise((resolve, reject) => {
        const cacheCode = window.localStorage.getItem('code')
        const token = Cookies.get(COOKIE_ADMIN_TOKEN) || Lockr.get(LOCAL_ADMIN_TOKEN)

        // 如果已经验证过 code 后，不在进行验证
        if (token && cacheCode === data.code) return resolve()

        authorizationAPI(data)
          .then(res => {
            // code不等于0说明code验证失败，一律认为是 302 错误便于跳转到登录
            if (res.code !== 0) return reject({ ...res, code: 302 })

            commit('SET_AUTH', { token: res.data })
            window.localStorage.setItem('code', data.code)
            resolve()
          })
          .catch(error => {
            window.localStorage.removeItem('code')
            reject(error)
          })
      })
    },

    // 获取权限
    async getAuth({
      commit,
      dispatch,
      rootState
    }) {
      let data = {}
      try {
        const authData = await adminIndexAuthListAPI()
        data = authData.data
      } catch (error) {
        return Promise.reject(error)
      }

      if (data.crm && Object.keys(data.crm).length === 0) {
        delete data.crm
      }

      data.wkFirstModel = data.firstModel
      commit('SET_ALLAUTH', data)
      commit('SET_MANAGE', data.manage)
      commit('SET_HRM', data.hrm)
      // 获取 管理后台 员工和部门信息
      dispatch('GetUserList')
      dispatch('GetDeptList')
      dispatch('GetUserDepMap')

      if (data.hrm) {
        dispatch('GetHrmUserList')
        dispatch('GetHrmDeptList')
      }

      return Promise.resolve(data)
    },

    // 获取用户信息
    GetUserInfo({
      commit,
      dispatch
    }) {
      return new Promise((resolve, reject) => {
        adminUsersReadAPI().then(res => {
          const resData = res.data || {}
          // 邮件信息 走之前的逻辑
          commit('SET_USERINFO', resData)
          // dispatch('SystemLogoAndName')

          resolve(res)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 登出
    LogOut() {
      return new Promise((resolve, reject) => {
        logoutAPI().then(() => {
          removeAuth({
            clearCookies: true
          })
          resetRouter()
          Lockr.rm(LOCAL_CLEAR_PAGE_TIME)
          Lockr.rm(LOCAL_FREE_TIME)
          window.location.href = process.env.VUE_APP_ID_CENTER_LOGIN_URL
          resolve()
        }).catch(error => {
          removeAuth({
            clearCookies: true
          })
          resetRouter()
          Lockr.rm(LOCAL_CLEAR_PAGE_TIME)
          Lockr.rm(LOCAL_FREE_TIME)
          window.location.href = process.env.VUE_APP_ID_CENTER_LOGIN_URL
          reject(error)
        })
      })
    },

    debounceGetUserList: debounce(3000, ({ dispatch }) => {
      dispatch('GetUserList')
    }),

    // 管理后台员工列表
    GetUserList({
      commit
    }) {
      return new Promise((resolve, reject) => {
        userListAPI({
          pageType: 0
        }).then(res => {
          commit('SET_USERLIST', res.data.list || [])
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    GetUserDepMap({
      commit
    }) {
      return new Promise((resolve, reject) => {
        adminUserQueryOrgInfoAPI().then(res => {
          const resData = res.data || {}
          commit('SET_USERDEPTOBJ', resData)
          commit('SET_USERDEPTMAP', resData)
          resolve(resData)
        }).catch(error => {
          reject(error)
        })
      })
    },

    debounceGetDeptList: debounce(3000, ({ dispatch }) => {
      dispatch('GetDeptList')
    }),

    // 管理后台部门列表
    GetDeptList({
      commit
    }) {
      return new Promise((resolve, reject) => {
        depListAPI({ type: 'tree' }).then(res => {
          commit('SET_DEPTLIST', res.data || [])
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    }
  },
  getters: {
    userInfo: state => state.userInfo,
    userList: state => state.userList,
    userDeptObj: state => state.userDeptObj,
    userDeptMap: state => state.userDeptMap,
    searchUserDept: state => state.searchUserDept,
    deptList: state => state.deptList,
    // 权限
    allAuth: state => state.allAuth,
    crm: state => state.crm,
    bi: state => state.bi,
    manage: state => state.manage,
    oa: state => state.oa,
    hrm: state => state.hrm,
    call: state => state.call // 模块权限
  }
}

export default user
