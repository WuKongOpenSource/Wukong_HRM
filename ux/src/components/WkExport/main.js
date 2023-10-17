import WkExportVue from './index.vue'

const WkExport = {}

WkExport.install = (Vue) => {
  const WkCRMExportConstructor = Vue.extend(WkExportVue)
  const instance = new WkCRMExportConstructor({
    el: document.createElement('div')
  })
  document.body.appendChild(instance.$el)

  Vue.prototype.$wkExport = instance
}

export default WkExport
