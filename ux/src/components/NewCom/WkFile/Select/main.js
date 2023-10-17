import WkFileVue from './main.vue'

const WkFile = {}

WkFile.install = (Vue) => {
  const WkFileConstructor = Vue.extend(WkFileVue)
  const instance = new WkFileConstructor({
    el: document.createElement('div')
  })
  document.body.appendChild(instance.$el)

  Vue.prototype.$wkFile = instance
}

export default WkFile
