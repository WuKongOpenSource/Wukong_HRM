// Figures out the event we are using with the bound element
// https://github.com/dhershman1/vue-debounce
// https://blog.csdn.net/sinat_36146776/article/details/84953131

import { debounce } from 'throttle-debounce'
import { on, off } from '@/utils/dom'

let fn = null
export default {
  name: 'debounce',
  install(Vue, { defaultTime = 300 } = {}) {
    Vue.directive('debounce', {
      bind(el, { value, arg, modifiers }) {
        fn = debounce(arg || defaultTime, value)
        on(el, 'click', fn)
      },

      unbind: function(el) {
        if (fn) {
          off(el, 'click', fn)
        }
      }
    })
  }
}
