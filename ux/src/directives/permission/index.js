import Vue from 'vue'
import { getRowValueByKey } from '@/utils'
import store from '@/store'

export default Vue.directive('permission', {
  inserted: function(el, binding) {
    const show = getRowValueByKey(store.state.user, binding.value)
    el.setAttribute('default-style', el.style.display)
    if (!show) {
      el.style.display = 'none'
    }
  },

  update: function(el, binding) {
    if (binding.value != binding.oldValue) {
      const show = getRowValueByKey(store.state.user, binding.value)
      el.style.display = show ? el.getAttribute('default-style') : 'none'
    }
  }
})
