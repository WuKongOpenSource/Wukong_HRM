const getLocationOrigin = () => {
  return window.location.protocol + '//' + window.location.hostname + (window.location.port ? ':' + window.location.port : '')
}
const macUrl = 'https://www.5kcrm.com/download/desktop/mac/%E6%82%9F%E7%A9%BACRM.dmg'
const winUrl = 'https://www.5kcrm.com/download/desktop/win/%E6%82%9F%E7%A9%BACRM.exe'
const iOSUrl = 'https://apps.apple.com/cn/app/%E6%82%9F%E7%A9%BAcrm-10-0/id1489143707'
const androidUrl = 'https://sj.qq.com/myapp/detail.htm?apkName=com.kakarote.crm10'

const contactUrl = process.env.VUE_APP_CONTACT_URL // 联系我们

const companyName = '悟空CRM'
const version = 'V12.3.13'
const baiduKey = 'BAIDU_KEY'

const build = 20230918

const updateContent = ``

// 默认表格样式
const tableStyle = {
  stripe: true, // 斑马纹
  class: [] // 'is-right-border-style', 'is-bottom-border-style'
}

export default {
  version,
  build,
  companyName,
  getLocationOrigin,
  baiduKey,
  macUrl,
  winUrl,
  iOSUrl,
  androidUrl,
  tableStyle,
  contactUrl,
  updateContent
}
