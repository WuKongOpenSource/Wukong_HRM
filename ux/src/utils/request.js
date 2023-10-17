import axios from 'axios'
import {
  Message
} from 'element-ui'
import {
  redirectLogin,
  removeAuth
} from '@/utils/auth'
import qs from 'qs'
import { debounce } from 'throttle-debounce'
import store from '@/store'
import { isBlob } from '@/utils/types'
import { LOCAL_ADMIN_TOKEN } from '@/utils/constants.js'

//
// axios 0.18 支持不过滤掉自定义参数
// requestProp 额外的一些说明,与 data  method 同级
// disabledMessage 禁用消息弹框

const errorMessage = debounce(500, (message, type = 'error') => {
  Message({
    message: message,
    duration: 1500,
    type: type
  })
})

axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8'
// 创建axios实例
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // api 的 base_url
  timeout: 600000 // 请求超时时间
})

const cancelTokenWhiteList = [
  'admin',
  'financeauth',
  'queryallemployeelist',
  'querytreelist',
  'login',
  'receivingemail'
]

// request拦截器
service.interceptors.request.use(
  config => {
    const flag = config.headers['Content-Type'] && config.headers['Content-Type'].indexOf('application/json') !== -1
    if (!flag) {
      const mult = config.headers['Content-Type'] && config.headers['Content-Type'].indexOf('multipart/form-data') !== -1
      if (!mult) {
        config.data = qs.stringify(config.data)
      }
    } else {
      if (config.data === undefined || config.data === null) {
        // 不传参的情况下 json类型的提交数据，校准为 空对象
        config.data = {}
      }
    }

    const validUrl = config.url.toLowerCase()
    const isAdd = cancelTokenWhiteList.every(item => !validUrl.includes(item))
    if (isAdd) {
      config.cancelToken = new axios.CancelToken(function(cancel) {
        store.commit('Add_CANCEL_TOKEN', { cancel, url: config.url })
      })
    }
    const { customConfig } = config
    if (customConfig?.removeToken) {
      delete config.headers[LOCAL_ADMIN_TOKEN]
    }
    return config
  },
  error => {
    // Do something with request error
    return Promise.reject(error)
  }
)

// response 拦截器
service.interceptors.response.use(
  response => {
    const { url, baseURL } = response.config
    store.commit('Remove_CANCEL_TOKEN', { url: url.slice(baseURL.length) })

    const requestProp = response.config.requestProp || {} // 请求配置
    /**
     * code为非20000是抛错 可结合自己业务进行修改
     */
    const res = response.data
    if (response.status === 200 && response.config.responseType === 'blob') { // 文件类型特殊处理
      const contentType = response.headers['Content-Type'] || response.headers['content-type']
      if (contentType?.includes('json') || (response.data && response.data?.type.includes('json'))) {
        const resultBlob = new Blob([response.data], { type: 'application/json' })
        const fr = new FileReader()
        fr.onload = function() {
          if (this.result) {
            const result = JSON.parse(this.result)
            // 附件下载反馈的302 忽略
            if (result.msg && result.code !== 302) {
              errorMessage(result.msg, result.code == 1 ? 'success' : 'error')
            }
          }
        }
        fr.readAsText(resultBlob)
      } else if (isBlob(response.data)) {
        return response
      }
    } else if (res.code !== 0) {
      // 302	登录已失效
      if (res.code === 302) {
        removeAuth({ clearCookies: true })
        redirectLogin()
      } else {
        if (res.msg && !requestProp.disabledMessage) {
          errorMessage(res.msg)
        }
      }
      return Promise.reject(res)
    } else {
      return res
    }
  },
  error => {
    if (!axios.isCancel(error)) { // 取消请求的情况下，终端Promise调用链
      if (error.response) {
        const response = error.response
        if (response.status === 500) {
          errorMessage('网络错误，请检查您的网络')
        } else if (response.data && response.data.msg) {
          errorMessage(response.data.msg)
        }
      }
      return Promise.reject(error)
    }
  }
)

export default service
