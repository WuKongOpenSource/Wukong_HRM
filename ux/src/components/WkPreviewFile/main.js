import WkPreviewFileVue from './index.vue'

const WkPreviewFile = {}

WkPreviewFile.install = (Vue) => {
  const WkPreviewFileConstructor = Vue.extend(WkPreviewFileVue)
  const instance = new WkPreviewFileConstructor({
    el: document.createElement('div')
  })
  document.body.appendChild(instance.$el)
  Vue.prototype.$wkPreviewFile = instance
}

export default WkPreviewFile
