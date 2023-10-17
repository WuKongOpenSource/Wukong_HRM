import {
  adminSystemIndexAPI
} from '@/api/admin/config'
import {
  crmSettingConfigDataAPI
} from '@/api/admin/crm'
import {
  adminConfigsetIndexAPI
} from '@/api/admin/application'

import Lockr from 'lockr'
import merge from '@/utils/merge'
import moment from 'moment'
import {
  LOCAL_LANG,
  LOCAL_SIDE_BAR_COLLAPSE,
  LOCAL_SYSTEM_LOGO,
  LOCAL_SYSTEM_NAME,
  LOCAL_TRIAL_TIPS,
  LOCAL_EXPIRATION_TIPS,
  LOCAL_UPDATE_TIPS
} from '@/utils/constants.js'

// 增加系统消息
const addSystemAlert = (state, data) => {
  const alertData = state.systemAlerts.find(item => item.sysType === data.sysType)
  if (!alertData) {
    state.systemAlerts.push(data)
  }

  // 不管展没展示，优先展示优先级最高的
  state.systemAlertProps = merge({ ...getDefaultSystemAlertProps() }, getPriorityAlertData(state) || {})
  state.systemAlertShow = true
}

/**
 * @description: 获取最有效消息
 * @param {*} state
 * @return {*}
 */
const getPriorityAlertData = (state) => {
  return state.systemAlerts.reduce((acc, cur) => {
    if (cur.priority < acc.priority) {
      return cur
    } else {
      return acc
    }
  }, { priority: Infinity })
}

export const getDefaultSystemAlertProps = () => {
  return {
    title: '',
    closable: true,
    center: false,
    closeText: '',
    showIcon: true,
    showButton: false,
    buttonLabel: '',
    buttonFun: null
  }
}

/** 记录 侧边索引 */
const app = {
  state: {
    logo: '',
    name: '',
    lang: Lockr.get(LOCAL_LANG) || 'cn',
    sidebar: {
      collapse: Lockr.get(LOCAL_SIDE_BAR_COLLAPSE) || false
    },
    // CRM配置信息
    CRMConfig: {},
    // 图片缓存
    imageCache: {},
    // 活动咨询是否开启
    marketingEnable: false,
    // 导航栏系统提示
    systemConfig: null,
    systemAlerts: [], // 所有系统消息  priority   1:高 2:中 3:低  sysType 1 升级提醒 2 试用到期 3 正式到期
    systemAlertShow: false,
    systemAlertProps: getDefaultSystemAlertProps(),
    // 模块权限
    moduleAuth: null,
    // 当前账套的月份
    nowMounth: '',
    pendingMap: new Map(), // 取消请求token数组

    moduleData: [] // 各模块状态 包含 时间 次数等信息
  },

  mutations: {
    SET_NOWMOUNTH: (state, num) => {
      state.nowMounth = num
    },
    SET_COLLAPSE: (state, collapse) => {
      state.sidebar.collapse = collapse
      Lockr.set(LOCAL_SIDE_BAR_COLLAPSE, collapse)
    },
    SET_APPLOGO: (state, logo) => {
      state.logo = logo
    },
    SET_APPNAME: (state, name) => {
      state.name = name
    },
    SET_LANG: (state, lang) => {
      state.lang = lang
      window.app.$i18n.locale = lang
      Lockr.set(LOCAL_LANG, lang)
      window.location.reload()
    },
    SET_CRMCONFIG: (state, config) => {
      state.CRMConfig = config
    },
    SET_IMAGECACHE: (state, value) => {
      state.imageCache = value
    },
    SET_MARKETINGENABLE: (state, value) => {
      state.marketingEnable = value
    },
    SET_SYSTEMALERTSHOW: (state, value) => {
      state.systemAlertShow = value
    },
    SET_SYSTEMALERTPROPS: (state, value) => {
      state.systemAlertProps = merge({ ...getDefaultSystemAlertProps() }, value || {})
    },
    SET_MODULEAUTH: (state, value) => {
      state.moduleAuth = value
    },
    // pendingMap
    Add_CANCEL_TOKEN({ pendingMap }, { url, cancel }) {
      if (!pendingMap.has(url)) {
        pendingMap.set(url, cancel)
      }
    },
    Remove_CANCEL_TOKEN({ pendingMap }, { url = '' }) {
      if (pendingMap.has(url)) {
        pendingMap.delete(url)
      }
    },
    CLEAR_CANCEL_TOKEN({ pendingMap }) {
      pendingMap.forEach(cancel => {
        cancel('路由跳转取消请求')
      })
      pendingMap.clear()
    },

    SET_MODULE_DATA(state, data) {
      state.moduleData = data
    },
    SET_MENU_COLOR_DATA(state, data) {
      state.menuColorData = data
    },

    // 设置消息
    SET_SYSTEM_ALERTS(state, data) {
      addSystemAlert(state, data)
    },

    SET_RM_SYSTEM_ALERTS(state, data) {
      state.systemAlerts = state.systemAlerts.filter(item => item.sysType !== data.sysType)
      if (state.systemAlerts.length > 0) {
        state.systemAlertProps = merge({ ...getDefaultSystemAlertProps() }, getPriorityAlertData(state) || {})
        state.systemAlertShow = true
      } else {
        state.systemAlertProps = getDefaultSystemAlertProps()
        state.systemAlertShow = false
      }
    },

    SET_SYSTEM_CONFIG(state, data) {
      const rootState = data.rootState
      delete data.rootState
      state.systemConfig = data

      const loginType = rootState.user.userInfo?.loginType
      const hideBuy = [3, 4, 6, 10].includes(Number(loginType))// 3 企业微信工作台应用 4 扫码登录 // 6飞书客户端  10外部浏览器

      if (data?.endTime) {
        const allDiff = moment(data.endTime).diff(moment(data.startTime), 'days')
        const isTrial = allDiff <= 90 // 时长小于90为试用

        let validDays = moment(data.endTime).diff(moment(), 'days') + 1 // 加1天为有效天数
        console.log('validDays---', validDays, allDiff)
        validDays = validDays < 0 ? 0 : validDays
        if (validDays <= 15) {
          // priority   1:高 2:中 3:低  sysType 1 升级提醒 2 试用到期 3 正式到期
          // wkUpdateTips 升级提醒
          // wkTrialTips 试用到期提醒
          // wkExpirationTips 到期提醒
          const localKey = isTrial ? LOCAL_TRIAL_TIPS : LOCAL_EXPIRATION_TIPS

          const preTipsDate = Lockr.get(localKey)
          moment().diff(moment(parseInt(preTipsDate)), 'days')

          if (!preTipsDate || !moment(parseInt(preTipsDate)).isValid() || moment().diff(moment(parseInt(preTipsDate)), 'days') > 0) {
            const defaultData = getDefaultSystemAlertProps()
            defaultData.sysType = isTrial ? 2 : 3 // 按照固定文案展示

            defaultData.showButton = !hideBuy
            defaultData.buttonLabel = isTrial ? '立即购买' : '立即续费→'
            defaultData.priority = 2
            defaultData.title = isTrial ? `正在免费试用悟空云服务，建议升级到付费版本。试用剩余天数<span style="color: #FF5630;">${validDays}</span>天。`
              : `您好，您的云服务将于${data.endTime}正式到期，截止目前仅剩${validDays}天，到期后将无法正常登录使用，为了保证正常使用，请及时续费！如果已启动付费流程，请忽略此消息。`
            defaultData.data = {
              isTrial,
              validDays,
              endTime: data.endTime
            }
            addSystemAlert(state, defaultData)
          }
        }
      }
    }
  },

  actions: {
    // 登录
    SystemLogoAndName({
      commit
    }) {
      return new Promise((resolve, reject) => {
        adminSystemIndexAPI().then(response => {
          const resData = response.data || {}
          commit('SET_APPNAME', resData.companyName)
          commit('SET_APPLOGO', resData.companyLogo)
          Lockr.set(LOCAL_SYSTEM_LOGO, resData.companyLogo)
          Lockr.set(LOCAL_SYSTEM_NAME, resData.companyName)

          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },

    /**
     * 获取客户管理配置
     */
    CRMSettingConfig({
      commit
    }) {
      return new Promise((resolve, reject) => {
        crmSettingConfigDataAPI().then(response => {
          commit('SET_CRMCONFIG', response.data)
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // // 查询活动配置
    // QueryMarketing({
    //   commit,
    //   state
    // }) {
    //   return new Promise((resolve, reject) => {
    //     configQueryMarketingAPI().then(res => {
    //       state.marketingEnable = res.data == 1
    //       resolve(res)
    //     }).catch(error => {
    //       reject(error)
    //     })
    //   })
    // },

    // 查询模块权限
    QueryModules({
      commit,
      state
    }) {
      return new Promise((resolve, reject) => {
        adminConfigsetIndexAPI().then(res => {
          const resData = res.data || []
          // status 状态 1:启用 0:停用 2:试用中 3:已过期,可用值:0,1,2,3
          const auth = {}
          resData.forEach(item => {
            auth[item.module] = item.status === 1 || item.status === 2
          })
          commit('SET_MODULEAUTH', auth)
          commit('SET_MODULE_DATA', resData)
          resolve(res)
        }).catch(error => {
          reject(error)
        })
      })
    }
  },

  getters: {
    lang: state => state.lang,
    app: state => state,
    logo: state => {
      if (state.logo) {
        return state.logo
      }
      return require('@/assets/img/logo.png')
    },
    name: state => {
      if (state.name) {
        return state.name
      }
      return window.WKConfig.companyName
    },
    nowMounth: state => state.nowMounth,
    moduleData: state => state.moduleData,

    collapse: state => state.sidebar.collapse,
    systemAlertShow: state => state.systemAlertShow,
    systemAlertProps: state => state.systemAlertProps,
    moduleAuth: state => state.moduleAuth,

    // 配置信息
    CRMConfig: state => state.CRMConfig,
    imageCache: state => state.imageCache
  }
}

export default app
