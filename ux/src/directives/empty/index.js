import Vue from 'vue'
import Empty from './empty.vue'
import {
  addClass,
  removeClass,
  getStyle
} from 'element-ui/src/utils/dom'
const EmptyMask = Vue.extend(Empty)
/**
 * xs-empty-text
 * 在绑定了v-empty指令的元素上添加xs-empty-text属性，其值会被渲染为加载文案，并显示在加载图标的下方
 * xs-empty-icon
 * 定义了几个类型  none 就是不展示icon 其他或者无值 就是一种效果
 * xs-empty-background
 * 背景色
 * xs-empty-custom-class
 * 类选择器样式 多个以空格分开
 */
const emptyDirective = {}
emptyDirective.install = Vue => {
  if (Vue.prototype.$isServer) return
  const toggleEmpty = (el, binding) => {
    /** 如果是数组 判断数组长度  否则 判断是否存在 当做Boolean */
    if ((Object.prototype.toString.call(binding.value) === '[object Array]' && binding.value.length === 0) ||
      (Object.prototype.toString.call(binding.value) !== '[object Array]' && binding.value)) {
      Vue.nextTick(() => {
        el.wkEmptyOriginalPosition = getStyle(el, 'position')
        insertDom(el, el, binding)
      })
    } else { // 移除效果
      el.wkEmptyVisible = false
      removeClass(el, 'xs-empty-parent--relative')
      removeClass(el, 'xs-empty-parent--hidden')
      el.wkEmptyInstance.visible = false
    }
  }
  const insertDom = (parent, el, binding) => {
    if (!el.wkEmptyVisible && getStyle(el, 'display') !== 'none' && getStyle(el, 'visibility') !== 'hidden') {
      Object.keys(el.wkEmptyMaskStyle).forEach(property => {
        el.wkEmptyMask.style[property] = el.wkEmptyMaskStyle[property]
      })

      if (el.wkEmptyOriginalPosition !== 'absolute' && el.wkEmptyOriginalPosition !== 'fixed') {
        addClass(parent, 'xs-empty-parent--relative')
      }
      el.wkEmptyVisible = true

      parent.appendChild(el.wkEmptyMask)
      Vue.nextTick(() => {
        el.wkEmptyInstance.visible = true
      })
      el.wkEmptyInserted = true
    }
  }

  Vue.directive('empty', {
    bind: function(el, binding, vnode) {
      const textExr = el.getAttribute('xs-empty-text')
      const iconExr = el.getAttribute('xs-empty-icon')
      const backgroundExr = el.getAttribute('xs-empty-background')
      const customClassExr = el.getAttribute('xs-empty-custom-class')
      const vm = vnode.context
      const mask = new EmptyMask({
        el: document.createElement('div'),
        data: {
          text: vm && vm[textExr] || textExr,
          icon: vm && vm[iconExr] || iconExr,
          background: vm && vm[backgroundExr] || backgroundExr,
          customClass: vm && vm[customClassExr] || customClassExr
        }
      })
      el.wkEmptyInstance = mask
      el.wkEmptyMask = mask.$el
      el.wkEmptyMaskStyle = {}

      binding.value && toggleEmpty(el, binding)
    },

    update: function(el, binding) {
      el.wkEmptyInstance.setText(el.getAttribute('xs-empty-text'))
      el.wkEmptyInstance.setIcon(el.getAttribute('xs-empty-icon'))
      if (binding.oldValue !== binding.value) {
        toggleEmpty(el, binding)
      }
    },

    unbind: function(el, binding) {
      if (el.wkEmptyInserted) {
        el.wkEmptyMask &&
          el.wkEmptyMask.parentNode &&
          el.wkEmptyMask.parentNode.removeChild(el.wkEmptyMask)
        toggleEmpty(el, {
          value: false,
          modifiers: binding.modifiers
        })
      }
    }
  })
}

export default emptyDirective
