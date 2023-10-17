import { debounce } from 'throttle-debounce'

const scope = 'FitText'

const changeFontSize = function() {
  const { el, option } = this[scope]

  let size = option.fontSize
  el.style.fontSize = size + 'px'
  let scrollWidth = el.scrollWidth
  while (el.scrollWidth > el.clientWidth) {
    scrollWidth = el.scrollWidth
    size = size - 0.2
    el.style.fontSize = size + 'px'
    if (scrollWidth <= el.clientWidth) {
      el.style.overflowX = 'visible'
      break
    } else if (size <= 12) {
      el.style.overflowX = 'hidden'
      el.style.overflowY = 'hidden'
      el.style.textOverflow = 'ellipsis'
      break
    }
  }
}

export default {
  name: 'FitText',
  bind: function(el, binding, vnode) {
    const option = binding.value || { fontSize: 14 }
    const debouncedResize = debounce(300, changeFontSize.bind(el))
    el[scope] = { el, option, debouncedResize }

    window.addEventListener('resize', debouncedResize)
  },
  inserted(el, binding, vnode) {
    const { debouncedResize } = el[scope]
    debouncedResize()
  },
  update(el, binding, vnode) {
    const { debouncedResize } = el[scope]
    debouncedResize()
  },
  unbind(el) {
    const { debouncedResize } = el[scope]
    window.removeEventListener('resize', debouncedResize)
  }
}
