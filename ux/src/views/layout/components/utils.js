import path from 'path'
import { isExternal } from '@/utils/validate'

/**
 * 通过路由获取导航信息
 * path 用与标记
 * fullPath  用与跳转
 */
export function getNavMenus(routers) {
  const menus = []
  routers.forEach((item, index) => {
    const { result, onlyOneChild } = hasOneShowingChild(item.children, item)
    if (!item.hidden && result) {
      let fullPath = resolvePath(onlyOneChild.path, item.path)
      // 3级
      if (onlyOneChild.children?.length > 0) {
        fullPath = resolvePath(onlyOneChild.children[0].path, fullPath)
      }
      let path = fullPath
      if (fullPath.includes('/subs/')) {
        path = fullPath.split('/subs/')[0]
      }

      menus.push({
        label: onlyOneChild.meta.title,
        icon: onlyOneChild.meta.icon || (item.meta && item.meta.icon),
        num: onlyOneChild.meta.num,
        count: onlyOneChild.count,
        path: path,
        fullPath,
        index
      })
    }
  })

  return menus
}

function hasOneShowingChild(children = [], parent) {
  let onlyOneChild = null

  const showingChildren = children.filter(item => {
    if (item.hidden) {
      return false
    } else {
      // Temp set(will be used if only has one showing child)
      onlyOneChild = item
      return true
    }
  })

  // When there is only one child router, the child router is displayed by default
  if (showingChildren.length === 1) {
    if (onlyOneChild.children && onlyOneChild.children.length > 0) {
      return {
        result: true,
        onlyOneChild: {
          ...onlyOneChild,
          meta: parent.meta
        }
      }
    } else {
      return {
        result: true,
        onlyOneChild: onlyOneChild
      }
    }
  }

  // Show parent if there are no child router to display
  if (showingChildren.length === 0) {
    onlyOneChild = { ...parent, path: '', noShowingChildren: true }
    return {
      result: true,
      onlyOneChild: onlyOneChild
    }
  }

  if (showingChildren.length > 1) {
    return {
      result: true,
      onlyOneChild: {
        ...showingChildren[0],
        meta: parent.meta
      }
    }
  }

  return {
    result: false,
    onlyOneChild: null
  }
}

export function resolvePath(routePath, baseUrl) {
  if (isExternal(routePath)) {
    return routePath
  }
  if (isExternal(baseUrl)) {
    return baseUrl
  }
  return path.resolve(baseUrl, routePath)
}
