import {
  hrmEmployeeQueryLoginEmployeeAPI,
  hrmEmployeeAllListAPI
} from '@/api/hrm/employee'
import {
  hrmDeptQueryTreeListAPI
} from '@/api/hrm/dept'

import { debounce } from 'throttle-debounce'

const hrm = {
  state: {
    hrmUserInfo: null, // 人资用户信息
    hrmShowType: 1, // 1 管理 2 员工
    hrmUserList: [], // 人力资源员工和部门
    hrmDeptList: []
  },

  mutations: {
    SET_HRMUSERINFO: (state, hrmUserInfo) => {
      state.hrmUserInfo = hrmUserInfo
    },
    SET_HRMSHOWTYPE: (state, hrmShowType) => {
      state.hrmShowType = hrmShowType
    },
    SET_HRMUSERLIST: (state, data) => {
      state.hrmUserList = data
    },
    SET_HRMDEPTLIST: (state, data) => {
      state.hrmDeptList = data
    }
  },

  actions: {
    // 获取人资用户信息
    GetHrmUserInfo({
      commit
    }) {
      return new Promise((resolve, reject) => {
        hrmEmployeeQueryLoginEmployeeAPI().then(res => {
          const resData = res.data || {}
          commit('SET_HRMUSERINFO', resData)
          resolve(res)
        }).catch(error => {
          reject(error)
        })
      })
    },

    debounceGetHrmUserList: debounce(3000, ({ dispatch }) => {
      dispatch('GetHrmUserList')
    }),

    // 管理后台员工列表
    GetHrmUserList({
      commit
    }) {
      return new Promise((resolve, reject) => {
        hrmEmployeeAllListAPI({
          pageType: 0
        }).then(res => {
          commit('SET_HRMUSERLIST', res.data || [])
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    debounceGetHrmDeptList: debounce(3000, ({ dispatch }) => {
      dispatch('GetHrmDeptList')
    }),

    // 管理后台部门列表
    GetHrmDeptList({
      commit
    }) {
      return new Promise((resolve, reject) => {
        hrmDeptQueryTreeListAPI({}).then(res => {
          commit('SET_HRMDEPTLIST', res.data || [])
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    }
  },

  getters: {
    hrmUserInfo: state => state.hrmUserInfo,
    hrmUserType: state => { // 1 管理员 2 上级
      if (state.hrmUserInfo && state.hrmUserInfo.role) {
        const role = state.hrmUserInfo.role
        if (role.label == 91) {
          return 1
        } else if (role.label == 92) {
          return 2
        }
      }
      return null
    },
    hrmShowType: state => state.hrmShowType,

    hrmUserList: state => state.hrmUserList,
    hrmDeptList: state => state.hrmDeptList
  }
}

export default hrm
