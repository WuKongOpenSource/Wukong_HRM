import UploadFileVue from './main.vue'

const UploadFile = {}

UploadFile.install = (Vue) => {
  const UploadFileConstructor = Vue.extend(UploadFileVue)
  const instance = new UploadFileConstructor({
    el: document.createElement('div')
  })
  document.body.appendChild(instance.$el)

  Vue.prototype.$wkUploadFile = instance
}

export default UploadFile
