import { isArray, isObject, isEmpty } from '@/utils/types'
import CheckStatusMixin from '@/mixins/CheckStatusMixin'
import CustomFieldsMixin from '@/mixins/CustomFields'
import { separator } from '@/filters/vueNumeralFilter/filters'
import { getWkDateTime } from '@/utils'
import store from '@/store'

/**
 * 获取自定义字段展示值
 * @param {*} formType
 * @param {*} value
 * @param {*} placeholder
 * @param {*} item 自定义字段模型
 * @returns 字符串
 */
export function getFormFieldShowName(formType, value, placeholder = '--', item) {
  if (formType === 'position') {
    if (isArray(value)) {
      return value.map(item => item?.name).join() || placeholder
    } else if (typeof value === 'string' && value) {
      try {
        const valueObj = JSON.parse(value)
        return isArray(valueObj) ? valueObj.map(item => item.name).join() : placeholder
      } catch (error) {
        return placeholder
      }
    }
    return placeholder
  // eslint-disable-next-line brace-style
  } else if (formType === 'floatnumber') {
    return isEmpty(value) ? '' : (isNaN(value) ? value : separator(value))
  } else if (formType === 'date') {
    return getWkDateTime(value)
  } else if (formType === 'location') {
    return isObject(value) ? value.address : placeholder
  } else if (formType === 'date_interval') {
    return isArray(value) ? value.join('-') : placeholder
  } else if (formType === 'percent') {
    return isEmpty(value) ? placeholder : `${value}%`
  } else if (formType === 'single_user') {
    if (isObject(value)) {
      return window.app.$getUserName(value) || placeholder
    }
    return value || placeholder
  } else if (formType === 'select') {
    const newValue = CustomFieldsMixin.methods.getRealParams({ formType }, value)
    if (isEmpty(newValue)) {
      return placeholder
    } else {
      return newValue
    }
  } else if (formType === 'checkbox') {
    const newValue = CustomFieldsMixin.methods.getRealParams({ formType }, value)
    if (isEmpty(newValue)) {
      return placeholder
    } else {
      return newValue
    }
  } else if (formType === 'structure') {
    if (isArray(value)) {
      return value.map(item => item.name || item.deptName).join() || placeholder
    } else if (typeof value === 'string' && value) {
      const userDeptMap = store.getters.userDeptMap
      if (userDeptMap[ `dept-${value}` ]) {
        return userDeptMap[ `dept-${value}`].name
      }
    }
    return value || placeholder
  } else if (formType === 'user') {
    if (isArray(value)) {
      return value.map(item => window.app.$getUserName(item) || item.employeeName).join() || placeholder
    } else if (isObject(value)) {
      return window.app.$getUserName(value) || value.employeeName || placeholder
    } else if (typeof value === 'string' && value) {
      const userDeptMap = store.getters.userDeptMap
      if (userDeptMap[ `user-${value}` ]) {
        return window.app.$getUserName(userDeptMap[ `user-${value}`])
      }
    }
    return value || placeholder
  } else if (formType === 'check_status') {
    return CheckStatusMixin.methods.getStatusName(value)
  }

  return isEmpty(value) ? placeholder : value
}
