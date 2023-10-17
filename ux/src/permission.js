import router from './router'
import store from './store'
import NProgress from 'nprogress' // Progress 进度条
import 'nprogress/nprogress.css' // Progress 进度条样式
import { isArray } from '@/utils/types'

import {
  getAuth,
  removeAuth,
  redirectLogin
} from '@/utils/auth' // 验权

const pathWhiteList = ['/404', '/noAuth'] // 不重定向白名单
let tryCount = 3 // 如果0 清空授权
// 是否能继续出错
function canTryFun() {
  return tryCount > 0
}

// 出错
function executeTryFun() {
  return tryCount--
}

// 重置
function resetTryFun() {
  tryCount = 3
}

/**
 * 获取 url 中携带参数
 */
function getUrlSearch() {
  const result = {}
  window.location.search
    .slice(1)
    .split('&')
    .filter(o => !!o)
    .forEach(o => {
      const temp = o.split('=')
      result[temp[0]] = temp[1]
    })

  const hash = decodeURIComponent(window.location.hash)
  const index = hash.lastIndexOf('?')
  if (index !== -1) {
    hash
      .slice(index + 1)
      .split('&')
      .filter(o => !!o)
      .forEach(o => {
        const temp = o.split('=')
        result[temp[0]] = temp[1]
      })
  }
  console.log('get url search: ', result)
  return result
}

router.beforeEach(async(to, from, next) => {
  store.commit('CLEAR_CANCEL_TOKEN') // 取消请求

  let redirectedFrom = to.redirectedFrom
  if (from.name === 'login' && to.path === '/404' && redirectedFrom.includes('404')) {
    redirectedFrom = '/' // 可能因重定向导致404 校准一次
  }

  if (to.meta.disabled) {
    next(false)
    return
  }
  NProgress.start()
  // 允许进入登录页面  1005 需要完善信息 不能清除登录信息
  if (window.accessLogin) {
    next()
    NProgress.done()
    return
  }

  // 请求头包含授权信息 并且 页面必须授权 直接进入
  try {
    const params = getUrlSearch()
    if (params && params.authCode) {
      await store.dispatch('AuthorizationLogin', { code: params.authCode })
      const url = new URL(window.location.href)
      url.searchParams.delete('authCode')
      // eslint-disable-next-line require-atomic-updates
      window.location.href = url.toString()
    }
    await getAuth()
    if (to.name === 'login') {
      next({
        path: '/'
      })
    } else {
      if (store.getters.addRouters.length === 0) { // 判断当前用户是否获取权限
        // await store.dispatch('QueryModules')
        const auths = await store.dispatch('getAuth')
        await store.dispatch('GenerateRoutes', auths)
        router.addRoutes(store.getters.addRouters)
        resetTryFun()
        if (pathWhiteList.includes(to.path)) {
          if (isArray(store.getters.addRouters) && store.getters.addRouters.length === 0) {
            throw new Error('routeError')
          } else {
            next({
              path: redirectedFrom || '/',
              replace: true
            })
          }
        } else {
          next({
            ...to,
            replace: true
          })
        }
      } else {
        resetTryFun()
        next()
      }
      NProgress.done()
    }
  } catch (error) {
    executeTryFun()
    // 302 登录信息失效
    if (error && (error.code === 302) || !canTryFun()) {
      resetTryFun()
      await removeAuth()
      // 没权限
      redirectLogin()
      NProgress.done()
    } else {
      // 因为网络原因，但已登录，转404页面。 如果没有，跳转登录
      getAuth().then(res => {
        if (error) {
          to.name === 'noAuth' ? next() : next('/noAuth')
        } else {
          if (pathWhiteList.includes(to.path)) {
            next(redirectedFrom || '/')
          } else {
            next()
          }
        }
        NProgress.done()
      }).catch(() => {
        // 没权限，跳转id center登录
        redirectLogin()
        NProgress.done()
      })
    }
  }
})

router.afterEach(() => {
  NProgress.done() // 结束Progress
})

router.onError((error) => {
  const pattern = /Loading chunk (\d)+ failed/g
  const isChunkLoadFailed = error.message.match(pattern)
  const targetPath = router.history.pending.fullPath
  if (isChunkLoadFailed) {
    router.replace(targetPath)
  }
})
