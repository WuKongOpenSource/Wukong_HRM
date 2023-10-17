/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-14 14:25:43
 * @LastEditTime: 2023-07-25 13:33:08
 * @LastEditors: yang
 */
import Vue from 'vue'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets

// 虚拟滚动
import VueVirtualScroller from 'vue-virtual-scroller'
Vue.use(VueVirtualScroller)
import 'vue-virtual-scroller/dist/vue-virtual-scroller.css'

// 配置信息
import config from '@/config'
window.WKConfig = config
Vue.prototype.WKConfig = config

import { getPermissionByKey } from './utils'
Vue.prototype.$auth = getPermissionByKey

// 金额单位设置
import { getMoneyUnit, getMoneyUnitValue } from './utils'
Vue.prototype.$unit = getMoneyUnit
Vue.prototype.$money = getMoneyUnitValue

import ElementUI from 'element-ui'
ElementUI.Tag.props.disableTransitions = {
  type: Boolean,
  default: true
}
Vue.use(ElementUI)
import { Message } from 'element-ui'
const message = function(data) {
  if (!data.hasOwnProperty('duration')) {
    data.duration = 1500
  }
  return Message(data)
}
const messageTyps = ['success', 'warning', 'info', 'error']
messageTyps.forEach(type => {
  const duration = 1500
  message[type] = (data) => {
    return Message({
      message: data,
      type,
      duration
    })
  }
})
Vue.prototype.$message = message

import 'element-ui/lib/theme-chalk/index.css'
import 'el-bigdata-table'

import Vue2OrgTree from 'vue2-org-tree'
Vue.use(Vue2OrgTree)

import '@/styles/index.scss' // global css

import App from './App'
import router from './router'
import store from './store'

import cache from '@/utils/cache'
cache.loadingCache()

import '@/permission' // permission control
import 'vue2-animate/dist/vue2-animate.min.css'

/** 事件传递 */
import VueBus from 'vue-bus'
Vue.use(VueBus)

/** 常用flex组件 */
import {
  Flexbox,
  FlexboxItem
} from '@/components/Flexbox'
Vue.component('Flexbox', Flexbox)
Vue.component('FlexboxItem', FlexboxItem)
import XrAvatar from '@/components/XrAvatar'
Vue.component('XrAvatar', XrAvatar)

import FileUpload from '@/components/FileUpload/index.js'
Vue.use(FileUpload)
import WkFileSelect from '@/components/NewCom/WkFile/Select/main.js'
Vue.use(WkFileSelect)
import WkPreviewFile from '@/components/WkPreviewFile/main.js'
Vue.use(WkPreviewFile)
import WkImport from '@/components/WkImport/main.js'
Vue.use(WkImport)
import WkExport from '@/components/WkExport/main.js'
Vue.use(WkExport)

/** 懒加载图片 */
import VueSrc from './directives/src'
Vue.directive('src', VueSrc)

import * as filters from './filters' // global filters
// 注册全局过滤器
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})
import vueNumeralFilterInstaller from './filters/vueNumeralFilter'
Vue.use(vueNumeralFilterInstaller, { locale: 'chs' })

// 处理时间的过滤器
Vue.use(require('vue-moment'))
import moment from 'moment'
moment.locale('zh_cn')

// 限制数据数值
import inputLimit from './directives/inputLimit'
Vue.use(inputLimit)
// 自定义全局点击空白处组件
import clickoutside from './directives/clickoutside'
Vue.directive('clickoutside', clickoutside)
import elClickoutside from './directives/elClickoutside'
Vue.directive('elclickoutside', elClickoutside)
import permission from './directives/permission'
Vue.directive('permission', permission)

import empty from './directives/empty'
Vue.use(empty)
import debounce from './directives/clickDebounce'
Vue.use(debounce)

Vue.config.productionTip = false

/**
 * 获取用户名
 * @param {object|string} user
 * @param {string} key
 * @return {string|null}
 */
Vue.prototype.$getUserName = (user, key = undefined) => {
  if (!user) return ''
  if (typeof user === 'string') return user
  if (key && user.hasOwnProperty(key)) return user[key]
  return user.nickname ||
    user.username ||
    user.realname ||
    ''
}

/**
 * 获取用户头像
 * @param {object|string} user
 * @param {string} key
 * @return {string|null}
 */
Vue.prototype.$getUserImg = (user, key = undefined) => {
  if (!user) return ''
  if (typeof user === 'string') return user
  if (key && user.hasOwnProperty(key)) return user[key]
  return user.userImg ||
    user.img ||
    ''
}

/**
 * 获取部门名称
 * @param {object|string} dept
 * @param {string} key
 * @return {string}
 */
Vue.prototype.$getDeptName = (dept, key = undefined) => {
  if (!dept) return ''
  if (typeof dept === 'string') return dept
  if (key && dept.hasOwnProperty(key)) return dept[key]
  return dept.deptName || dept.name || ''
}

window.app = new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
