import store from '@/store'
import Lockr from 'lockr'
import axios from '@/utils/request'
import Cookies from 'js-cookie'
import { getCookiesDomain } from '@/utils'
import {
  LOCAL_SYSTEM_LOGO,
  LOCAL_SYSTEM_NAME,
  LOCAL_ADMIN_TOKEN,
  COOKIE_ADMIN_TOKEN
} from '@/utils/constants.js'

const cache = {
  /**
   * 载入全部登陆信息
   */
  loadingCache: function() {
    // if (Lockr.get(LOCAL_ADMIN_TOKEN) && !axios.defaults.headers.common[LOCAL_ADMIN_TOKEN]) {
    //   /** 将用户信息放入缓存 */
    //   const userInfo = Lockr.get('loginUserInfo')
    //   if (userInfo) {
    //     store.commit('SET_USERINFO', userInfo)
    //   }
    // }
    store.commit('SET_APPNAME', Lockr.get(LOCAL_SYSTEM_NAME))
    store.commit('SET_APPLOGO', Lockr.get(LOCAL_SYSTEM_LOGO))
  },
  /**
   * 请求和更新登录缓存
   */
  updateAxiosCache: function(token) {
    axios.defaults.headers.common[LOCAL_ADMIN_TOKEN] = token || Lockr.get(LOCAL_ADMIN_TOKEN)
    if (token) {
      Lockr.set(LOCAL_ADMIN_TOKEN, token)
    }
  },
  updateAxiosHeaders: function(token) {
    const newToken = token || Lockr.get(LOCAL_ADMIN_TOKEN)

    if (token) {
      Lockr.set(LOCAL_ADMIN_TOKEN, token)
    }

    if (newToken && axios.defaults.headers.common[LOCAL_ADMIN_TOKEN] !== newToken) {
      axios.defaults.headers.common[LOCAL_ADMIN_TOKEN] = newToken
      return true // token 变动
    }
  },
  /**
   * 移除登录信息
   * @param {*}
   */
  rmAxiosCache: function() {
    Cookies.remove(COOKIE_ADMIN_TOKEN, { domain: getCookiesDomain() })
    Lockr.rm(LOCAL_ADMIN_TOKEN)
  }
}

export default cache
