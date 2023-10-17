export default {
  install(Vue, options = {}) {
    // 限制数据数值
    Vue.directive('WkNumber', {
      update(ele, { value }) {
        const el = ele.getElementsByTagName('input')[0]
        const copyValue = el.value

        /**
         * 正整数
         * input 类型必须是 text
         * onKeypress="return (/[\d]/.test(String.fromCharCode(event.keyCode)))"
         */
        if (value == 'positiveInt') {
          el.value = el.value.replace(/[^\d]/g, '')
          if (el.value.indexOf('.') < 0 && el.value != '') {
            console.log(el.value.indexOf('.'))
            // 首位不能为类似于 01、02的金额
            el.value = parseInt(el.value)
          }
        } else if (value == 'positive2Float') {
          el.value = el.value.replace(/[^\d.]/g, '')
          el.value = el.value.replace(/^(\d+)\.(\d\d).*$/, '$1.$2')
        } else if (value == 'positive3Float') {
          el.value = el.value.replace(/[^\d.]/g, '')
          el.value = el.value.replace(/^(\d+)\.(\d\d\d).*$/, '$1.$2')
        } else if (value == 'positive4Float') {
          el.value = el.value.replace(/[^\d.]/g, '')
          el.value = el.value.replace(/^(\d+)\.(\d\d\d\d).*$/, '$1.$2')
        } else if (value == 'positiveFloat') { // 负数以及不限制位数
          el.value = el.value.replace(/[^\d.-]/g, '')
          el.value = el.value.replace(/^(\d+)\.(\d+).*$/, '$1.$2')
          el.value = el.value.replace(/^\-{2,}$/, '-')
          el.value = el.value.replace(/^\-?(\d+)\-+$/, '$1')
          el.value = el.value.replace(/^(\-?\d+)\.\-+$/, '$1.')
          el.value = el.value.replace(/^(\-?\d+)\.(\d+)\-$/, '$1.$2')
        } else if (value == 'positiveNum') { // 数字 包含小数
          el.value = el.value.replace(/[^\d.]/g, '')
        } else {
          el.value = el.value.replace(/[^\-?\d.]/g, '')
          if (!value) { // 默认保留两位小数
            el.value = el.value.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3')
          }
        }

        if (el.value) {
          // 防止出现 10.1.1
          const arr = el.value.split('.')
          if (arr.length > 2) {
            el.value = `${arr[0]}.${arr[1]}`
          }
        }

        if (el.value != copyValue) {
          el.dispatchEvent(new Event('input'))
        }
      }
    })
  }
}

