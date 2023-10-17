import numeral from 'numeral'
import 'numeral/locales'

import {
  abbreviate,
  bytes,
  exponential,
  exposedNumeral,
  ordinal,
  percentage,
  separator,
  currency
} from './filters.js'

const vueNumeralFilterInstaller = {
  install(vue, { locale = 'en-gb' } = {}) {
    numeral.locale(locale)

    vue.filter('abbreviate', abbreviate)
    vue.filter('bytes', bytes)
    vue.filter('exponential', exponential)
    vue.filter('numeral', exposedNumeral)
    vue.filter('ordinal', ordinal)
    vue.filter('percentage', percentage)
    vue.filter('separator', separator)
    vue.filter('currency', currency)
  }
}

export default vueNumeralFilterInstaller

if (typeof window !== 'undefined' && window.Vue) {
  // eslint-disable-next-line no-undef
  Vue.use(vueNumeralFilterInstaller)
}
