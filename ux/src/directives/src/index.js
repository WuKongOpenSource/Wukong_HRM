// import { getImageData } from '@/utils'
import axios from '@/utils/request'
import { LOCAL_ADMIN_TOKEN } from '@/utils/constants.js'

window.dataCache = {}

const loadData = (el, binding) => {
  const loadingImg = require('@/assets/img/white.png')
  setSrc(el, binding, loadingImg)

  if (binding.value && (binding.value.indexOf('/file/downFile') == 0 ||
  binding.value.indexOf('/adminFile/down') == 0)) {
    let src = ''
    if (binding.value.indexOf('/adminFile/down') == 0) {
      const replaceVal = binding.value.replace('/adminFile/down', '/projectFile/download')
      src = `${process.env.VUE_APP_BASE_API}${replaceVal}`
    } else {
      src = `${process.env.VUE_APP_BASE_API}${binding.value}`
    }
    // const src = `${process.env.VUE_APP_BASE_API}${binding.value}`
    // setSrc(el, binding, src.replace('//', '/') + `?c=${axios.defaults.headers.common['Admin-Token']}`)
    setSrc(el, binding, src.replace('//', '/') + `?c=${axios.defaults.headers.common[LOCAL_ADMIN_TOKEN]}`)
    // if (window.dataCache[binding.value]) {
    //   setSrc(el, binding, window.dataCache[binding.value])
    // } else {
    //   getImageData(binding.value).then((data) => {
    //     setSrc(el, binding, data.src)
    //     window.dataCache[binding.value] = data.src
    //   }).catch(() => {})
    // }
  } else {
    setSrc(el, binding, binding.value)
  }
}

const setSrc = (el, binding, src) => {
  if (binding.arg) {
    el.style[binding.arg] = 'url("' + src + '")'
  } else {
    el.setAttribute('src', src)
  }
  // 如果不是img标签则手动触发 onload 事件，通知图片已经获取到
  if (el.tagName !== 'IMG') {
    const evt = document.createEvent('HTMLEvents')
    evt.initEvent('load', false, true)
    el.dispatchEvent(evt)
  }
}

export default {
  name: 'src',
  inserted(el, binding) {
    loadData(el, binding)
  },
  update: function(el, binding) {
    if (binding.oldValue != binding.value) {
      loadData(el, binding)
    }
  },

  unbind(el) {
  }
}

