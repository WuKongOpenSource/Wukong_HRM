/**
 * 自动化导入modules所有路由
 * ignore 忽略的路由
 * 文件名称 key 包含 sync 的默认加入的路由
 * admin路由实际用了manage关键词
 */
const syncRouters = []
const asyncRouterMap = []

function importAll(r) {
  let manageIndex = -1
  r.keys().forEach(key => {
    const fileName = key.slice(2, -3)
    const itemRouter = r(key).default
    if (fileName.includes('sync')) {
      syncRouters.push(...itemRouter)
    } else {
      const type = fileName === 'admin' ? 'manage' : fileName
      if (type === 'manage') {
        manageIndex = asyncRouterMap.length
      }
      asyncRouterMap.push({
        type,
        router: itemRouter
      })
    }
  })

  // 管理端放入尾部
  if (manageIndex !== -1) {
    asyncRouterMap.push(asyncRouterMap.splice(manageIndex, 1)[0])
  }

  // crm 放到头部 当做首页
  let crmIndex = -1
  for (let index = 0; index < asyncRouterMap.length; index++) {
    const element = asyncRouterMap[index]
    if (element.type === 'crm') {
      crmIndex = index
      break
    }
  }
  if (crmIndex !== -1) {
    asyncRouterMap.unshift(asyncRouterMap.splice(crmIndex, 1)[0])
  }

  // oa 放到头部
  let oaIndex = -1
  for (let index = 0; index < asyncRouterMap.length; index++) {
    const element = asyncRouterMap[index]
    if (element.type === 'oa') {
      oaIndex = index
      break
    }
  }
  if (oaIndex !== -1) {
    if (crmIndex !== -1) {
      asyncRouterMap.splice(1, 0, asyncRouterMap.splice(oaIndex, 1)[0])
    } else {
      asyncRouterMap.unshift(asyncRouterMap.splice(oaIndex, 1)[0])
    }
  }
}

importAll(require.context('./modules', false, /\.js$/))

export {
  syncRouters,
  asyncRouterMap
}
