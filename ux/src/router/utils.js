
/**
 * @description: 获取到有效展示路由
 * @param {*} routers
 * @return {*}
 */
export const filterShowRouters = (routers) => {
  const res = []
  routers.forEach(router => {
    const tmp = {
      ...router
    }
    // hidden 的路由不展示 仅插入路由
    if (!tmp.hidden) {
      if (tmp.children) {
        tmp.children = filterShowRouters(tmp.children)
        if (tmp.children.length > 0) {
          res.push(tmp)
        }
      } else {
        res.push(tmp)
      }
    }
  })
  return res
}

