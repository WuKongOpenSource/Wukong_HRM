/**
 * 功能：dom绑定事件
 * 参数：element(dom节点)
 *      event(事件名称)
 *      handler(回调)
 *返回：无
 * */
export const on = (function() {
  if (document.addEventListener) {
    return function(element, event, handler) {
      if (element && event && handler) {
        element.addEventListener(event, handler, false)
      }
    }
  } else {
    return function(element, event, handler) {
      if (element && event && handler) {
        element.attachEvent('on' + event, handler)
      }
    }
  }
})()

/**
 * 功能：移除dom绑定的事件
 * 参数：element(dom节点)
 *      event(事件名称)
 *      handler(回调函数)
 * 返回：无
 * */
export const off = (function() {
  if (document.removeEventListener) {
    return function(element, event, handler) {
      if (element && event) {
        element.removeEventListener(event, handler, false)
      }
    }
  } else {
    return function(element, event, handler) {
      if (element && event) {
        element.detachEvent('on' + event, handler)
      }
    }
  }
})()
