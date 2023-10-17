import { isEmpty, isArray } from '@/utils/types'

// 5 为空 6 不为空
const isEmptyCondition = function(type) {
  return [5, 6].includes(type)
}

/**
 * 获取上传数据组
 */
export function getFilterPostData(list) {
  const obj = []
  list.forEach(o => {
    let dataValues = []
    if (o.formType == 'datetime' || o.formType == 'date') {
      if (o.type === 14) {
        if (!isEmpty(o.timeType)) {
          dataValues = [o.timeType]
        } else {
          dataValues = o.range
        }
      } else if (o.value) {
        dataValues = isArray(o.value) ? o.value : [o.value]
      }
      if (isEmptyCondition(o.type)) dataValues = []
      obj.push({
        formType: o.formType,
        name: o.fieldName,
        type: o.type,
        values: dataValues
      })
    } else if (
      o.formType == 'number' ||
          o.formType == 'floatnumber' ||
          o.formType == 'percent'
    ) {
      if (o.type === 14) {
        dataValues = [isEmpty(o.min) ? 0 : o.min, isEmpty(o.max) ? 0 : o.max]
      } else if (!isEmpty(o.value)) {
        dataValues = [o.value]
      }
      if (isEmptyCondition(o.type)) dataValues = []
      obj.push({
        formType: o.formType,
        name: o.fieldName,
        type: o.type,
        values: dataValues
      })
    } else if (o.formType == 'business_cause') {
      // 阶段组 和 阶段 依据name传值
      if (o.flowName) {
        obj.push({
          formType: o.formType,
          name: 'flowName',
          type: 1,
          values: [o.flowName]
        })
      }
      if (o.settingName) {
        obj.push({
          formType: o.formType,
          name: 'settingName',
          type: 1,
          values: [o.settingName]
        })
      }
    } else if (o.formType == 'user' || o.formType == 'single_user') {
      dataValues = o.value
      if (isEmptyCondition(o.type)) dataValues = []
      obj.push({
        type: o.type,
        values: dataValues,
        formType: o.formType,
        name: o.fieldName
      })
    } else if (o.formType == 'position') {
      dataValues = o.value.filter(item => !isEmpty(item.name)).map(item => JSON.stringify(item))
      if (isEmptyCondition(o.type)) dataValues = []
      obj.push({
        type: o.type,
        values: dataValues,
        formType: o.formType,
        name: o.fieldName
      })
    } else if (o.formType == 'structure') {
      dataValues = o.value
      if (isEmptyCondition(o.type)) dataValues = []
      obj.push({
        type: o.type,
        values: dataValues,
        formType: o.formType,
        name: o.fieldName
      })
    } else if (o.formType == 'checkbox') {
      // 回款状态 formType 为 checkbox
      if (o.fieldName === 'receivedStatus' && !isArray(o.value)) {
        dataValues = (o.value === '' || o.value === null || o.value === undefined) ? [] : [o.value]
      } else {
        dataValues = isArray(o.value) ? o.value : []
      }

      if (isEmptyCondition(o.type)) dataValues = []
      obj.push({
        type: o.type,
        values: dataValues,
        formType: o.formType,
        name: o.fieldName
      })
    } else if (o.formType == 'category') {
      dataValues = isArray(o.value) && o.value.length > 0 ? [o.value[o.value.length - 1]] : []
      if (isEmptyCondition(o.type)) dataValues = []
      obj.push({
        type: 1,
        values: dataValues,
        formType: o.formType,
        name: o.fieldName
      })
    } else if (o.formType === 'select') {
      dataValues = o.value || []
      if (
        getFilterSettingValueType(o.setting) != 'string' &&
        (
          o.value ||
          o.value === 0
        )
      ) {
        dataValues = [o.value]
      }
      if (isEmptyCondition(o.type)) dataValues = []
      obj.push({
        type: o.type,
        values: dataValues,
        formType: o.formType,
        name: o.fieldName
      })
    } else if (['field_tag', 'field_attention'].includes(o.formType)) {
      dataValues = o.value || []
      if (!isArray(dataValues)) {
        dataValues = [dataValues]
      }
      if (isEmptyCondition(o.type)) dataValues = []
      obj.push({
        type: o.type,
        values: dataValues,
        formType: o.formType,
        name: o.fieldName
      })
    } else if (o.formType == 'map_address') {
      const value = []
      for (var key in o.address) {
        const addValue = o.address[key]
        if (addValue) {
          value.push(addValue)
        }
      }
      obj.push({
        values: [value.join(',')],
        type: 3,
        formType: o.formType,
        name: o.fieldName
      })
    } else {
      let values = []
      if (typeof o.value === 'string') {
        const temp = o.value.replace(/；/g, ';')
        values = temp.split(';').filter(item => item !== '' && item !== null)
      } else {
        values = [o.value]
      }
      if (isEmptyCondition(o.type)) values = []
      obj.push({
        type: o.type,
        values: values,
        formType: o.formType,
        name: o.fieldName
      })
    }
  })

  return obj
}

/**
 * 获取setting数据类型
 */
function getFilterSettingValueType(setting) {
  if (setting && setting.length > 0) {
    const value = setting[0]
    return typeof value
  }
  return []
}
