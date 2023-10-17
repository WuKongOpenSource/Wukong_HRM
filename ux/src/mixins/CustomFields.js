import { filedValidatesAPI } from '@/api/crm/common'

import { isArray, isEmpty, isObject } from '@/utils/types'
import { objDeepCopy, guid } from '@/utils'
import GenerateRulesMixin from '@/components/NewCom/WkForm/GenerateRules'
import merge from '@/utils/merge'

const LOGIC_OTHER = '其他'

export default {
  mixins: [GenerateRulesMixin],

  props: {},

  methods: {
    /**
     * 获取初始value值
     * detail 主要用于关联模块
     * type update 读取 value 判断 save 读取 defaultValue 判断
     * parentItem 明细表格子项的父元素
     */
    getItemValue(item, detail, type, parentItem) {
      detail = detail || {}
      if (item.formType == 'contacts' ||
          item.formType == 'customer' ||
          item.formType == 'contract' ||
          item.formType == 'receivables' ||
          item.formType == 'business'
      ) {
        // crm相关信息特殊处理
        if (type === 'update') {
          return item.value ? objDeepCopy(item.value) : []
        } else {
          const relativeData = detail[item.formType]
          return relativeData ? [relativeData] : []
        }
      } else if (item.formType == 'category') {
        // 产品类别
        return item.value ? item.value : []
      } else if (item.formType == 'product') {
        // 关联产品
        return item.value
      } else if (item.formType == 'map_address') {
        if (type == 'update') {
          if (item.value.address) {
            item.value.address = item.value.address.split(',')
          }
          return item.value
        } else {
          return {}
        }
      } else if (item.formType == 'address') {
        // 地址
        if (type == 'update') {
          if (item.value) {
            if (typeof item.value === 'string') {
              const citys = item.value.split(',')
              return {
                province: citys[0] || '',
                city: citys[1] || '',
                area: citys[2] || ''
              }
            }
            return item.value
          } else {
            return {}
          }
        } else {
          return {}
        }
      } else if (
        item.formType == 'single_user' ||
        (item.formType == 'user' && (!item.isMulti || item.radio))
      ) {
        if (type === 'update') {
          if (isObject(item.value) && item.value.userId) {
            return item.value.userId
          } else if (isArray(item.value) && item.value.length > 0) {
            return item.value[0].userId
          }
          return typeof item.value === 'string' ? item.value : ''
        } else {
          return isArray(item.defaultValue) && item.defaultValue.length > 0
            ? objDeepCopy(item.defaultValue[0]).userId
            : ''
        }
      } else if (item.formType == 'structure' && (!item.isMulti || item.radio)) {
        if (type === 'update') {
          if (isObject(item.value) && item.value.deptId) {
            return item.value.deptId || item.value.id
          } else if (isArray(item.value) && item.value.length > 0) {
            return item.value[0].deptId || item.value[0].id
          }
          return typeof item.value === 'string' ? item.value : ''
        } else {
          return isArray(item.defaultValue) && item.defaultValue.length > 0
            ? objDeepCopy(item.defaultValue[0]).deptId
            : ''
        }
      } else if (item.formType == 'user' ||
            item.formType == 'structure' ||
            item.formType == 'file') {
        if (type === 'update') {
          return item.value ? objDeepCopy(item.value) : []
        } else {
          return item.defaultValue
            ? objDeepCopy(item.defaultValue)
            : []
        }
      } else {
        if (item.formType == 'number' ||
        item.formType == 'floatnumber' ||
        item.formType == 'percent') {
          if (type == 'update') {
            return isEmpty(item.value) ? undefined : item.value
          } else {
            return isEmpty(item.defaultValue) ? undefined : item.defaultValue
          }
        } else if (item.formType == 'field_attention') {
          if (type == 'update') {
            return isEmpty(item.value) ? 0 : Number(item.value)
          } else {
            return isEmpty(item.defaultValue) ? 0 : Number(item.defaultValue)
          }
        } else if (item.formType == 'detail_table') {
          let baseFieldList = isEmpty(item.value) ? [item.fieldExtendList] : item.value
          if (typeof baseFieldList === 'string' && baseFieldList) {
            try {
              baseFieldList = JSON.parse(baseFieldList)
            } catch (error) { console.log('error--', error) }
          }

          // 二维数组
          const tableValue = []
          baseFieldList.forEach(bigItem => {
            const subForm = {}
            tableValue.push(subForm)
            // 明细表格 子块，在编辑是，需要携带上batchId
            const batchId = bigItem.length > 0 && bigItem[0].batchId ? bigItem[0].batchId : guid()
            bigItem.forEach(subItem => {
              // 未转换 取 fieldName 值
              if ([
                'select',
                'checkbox'
              ].includes(subItem.formType)) {
                subItem.optionsData = null
              }

              if (['structure', 'user'].includes(subItem.formType)) {
                const configField = item.fieldExtendList.find(extendField => extendField.fieldName === subItem.fieldName)
                if (configField) {
                  subItem.isMulti = configField.isMulti // 用字段内的配置填充
                }
              }

              subForm[subItem.fieldName] = this.getItemValue(subItem, null, type, item)
              if (batchId) {
                subForm.batchId = batchId
              }
            })
          })
          return tableValue
        } else if (item.formType == 'customer_relations') {
          if (type == 'update') {
            return isEmpty(item.value) ? [] : item.value
          } else {
            return [] // 该字段没有默认值逻辑
          }
        } else {
          if (type == 'update') {
            return item.value === null || item.value === undefined ? '' : item.value
          } else {
            return item.defaultValue === null || item.defaultValue === undefined ? '' : item.defaultValue
          }
        }
      }
    },

    /**
     * 验证唯一
     */
    getUniquePromise(field, value, detail, props) {
      const defaultProps = {
        paramFun: null,
        request: null
      }
      const config = merge({ ...defaultProps }, props || {})

      return new Promise((resolve, reject) => {
        var validatesParams = {}
        validatesParams.fieldId = field.fieldId
        const realValue = this.getRealParams(field, value)
        validatesParams.value = realValue && typeof realValue === 'object' ? JSON.stringify(realValue) : realValue
        if (detail.type == 'update') {
          validatesParams.batchId = detail.batchId
        }

        if (config.paramFun) {
          config.paramFun(validatesParams)
        }
        (config.request || filedValidatesAPI)(validatesParams).then(res => {
          // status 1 通过 0
          const resData = res.data || {}
          if (resData.status === 1) {
            resolve()
          } else {
            if (resData.ownerUserName) {
              reject(`${field.name}已存在，负责人为“${resData.ownerUserName}”`)
            } else if (resData.poolNames && resData.poolNames.length > 0) {
              reject(`${field.name}已存在，当前位于公海“${resData.poolNames.join('，')}”`)
            } else {
              reject(`${field.name}已存在`)
            }
          }
        }).catch(() => {
          reject()
        })
      })
    },

    /**
     * 获取字段默认内容
     */
    getFormItemDefaultProperty(item, isCopy = true) {
      const temp = isCopy ? objDeepCopy(item) : item
      temp.field = item.fieldName

      // 细化 precisions
      if (item.formType === 'date_interval') {
        // 1 日期  2 时间日期
        if (item.precisions === 2) {
          temp.dateType = 'datetimerange'
          temp.dateValueFormat = 'yyyy-MM-dd HH:mm:ss'
        } else {
          temp.dateType = 'daterange'
          temp.dateValueFormat = 'yyyy-MM-dd'
        }
      } else if (item.formType === 'position') {
        // 1 省/地区、市、区/县、详细地址 2 省/地区、市、区/县
        // 3 省/地区、市 4 省/地区
        temp.showDetail = item.precisions === 1
        temp.hideArea = item.precisions === 3 || item.precisions === 4
        temp.onlyProvince = item.precisions === 4
      } else if (item.formType === 'detail_table') {
        // 校准默认单元数据
        // authLevel 1 不能查看不能编辑 2可查看  3 可编辑可查看
        const fieldForm = {}
        temp.fieldExtendList.forEach(extendItem => {
          const copyExtendItem = objDeepCopy(extendItem)
          this.getFormItemDefaultProperty(extendItem, false)
          extendItem.show = extendItem.isHidden !== 1
          if (extendItem.show) {
            extendItem.rules = this.getRules(copyExtendItem)
          }
          this.getItemRadio(extendItem, extendItem)
          fieldForm[extendItem.fieldName] = this.getItemValue(extendItem)
        })
        temp.fieldForm = fieldForm // 用于追加新事项

        const fieldExtendList = objDeepCopy(item.fieldExtendList)
        let baseFieldList = isEmpty(item.value) ? [fieldExtendList] : item.value
        if (typeof baseFieldList === 'string' && baseFieldList) {
          try {
            baseFieldList = JSON.parse(baseFieldList)
          } catch (error) { console.log('error--', error) }
        }

        // 二维数组
        const tableFieldList = []
        baseFieldList.forEach(bigItem => {
          const subValue = []
          bigItem.forEach(subItem => {
            const copySubItem = objDeepCopy(subItem)
            const subTemp = this.getFormItemDefaultProperty(subItem, false)
            // 特殊字段允许多选
            this.getItemRadio(subItem, subTemp)
            subTemp.show = subTemp.isHidden !== 1
            if (subTemp.show) {
              subTemp.rules = this.getRules(copySubItem)
            }
            subValue.push(subTemp)
          })
          tableFieldList.push(subValue)
        })
        temp.fieldList = tableFieldList
      }
      return temp
    },

    /**
     * 获取二维数组字段
     * @param {*} array
     * @param {*} formType
     */
    getItemWithFromType(array, formType) {
      let item = null
      for (let index = 0; index < array.length; index++) {
        const children = array[index]
        for (let childIndex = 0; childIndex < children.length; childIndex++) {
          const element = children[childIndex]
          if (element.formType === formType) {
            item = element
            break
          }
        }

        if (item) {
          break
        }
      }

      return item
    },

    /**
     * 循环二维数组
     * @param {*} array
     * @param {*} result
     */
    itemsForEach(array, callback) {
      for (let index = 0; index < array.length; index++) {
        const children = array[index]
        if (isArray(children)) {
          for (let childIndex = 0; childIndex < children.length; childIndex++) {
            const element = children[childIndex]
            callback(element, index, childIndex, children)
          }
        } else {
          callback(children, index)
        }
      }
    },

    /**
     * 获取字段是否可编辑
     */
    getItemIsCanEdit(item, type) {
      if (isEmpty(item.authLevel)) return true
      // 如果是 自定义编号 自定义字段，编辑场景下不能编辑
      if (type === 'update' && item.formType === 'serial_number') return false
      // authLevel 1 不能查看不能编辑 2可查看  3 可编辑可查看
      return item.authLevel == 3
    },

    /**
     * user single_user structure
     */
    getItemRadio(field, data) {
      if (field.formType == 'user' || field.formType == 'structure') {
        data.radio = !field.isMulti
      } else if (field.formType == 'single_user') {
        data.radio = true
      }
    },

    /**
     * 获取error错误
     */
    getFormErrorMessage(crmForm) {
      // 提示第一个error
      if (crmForm.fields) {
        for (
          let index = 0;
          index < crmForm.fields.length;
          index++
        ) {
          const ruleField = crmForm.fields[index]
          if (ruleField.validateState == 'error') {
            this.$message.error(ruleField.validateMessage)
            break
          }
        }
      }
    },

    /**
     * 获取相关联item
     */
    getItemRelatveInfo(list, fromType) {
      let crmItem = null
      if (this.action.type == 'relative') {
        crmItem = this.action.data[fromType]
      } else {
        const childIsArray = list.length > 0 && isArray(list[0])
        const crmObj = childIsArray ? this.getItemWithFromType(list, fromType) : list.find(listItem => {
          return listItem.formType === fromType
        })

        if (crmObj && crmObj.value && crmObj.value.length > 0) {
          crmItem = crmObj.value[0]
        }
      }
      return crmItem
    },

    /**
     * 获取提交参数
     * otherKeys 该值也增加到对象里
     * isFullField 自定义字段上传完成对象
     * ignoreFormType 默认自定义字段新建编辑 忽略 描述文字的修改
     */
    getSubmiteParams(array, data, otherKeys, isFullField, ignoreFormType = ['desc_text']) {
      var params = { entity: {}, field: [] }
      for (let index = 0; index < array.length; index++) {
        const field = array[index]
        let dataValue = null
        // 校准值 不展示的 改为空值
        if (field.hasOwnProperty('show')) {
          dataValue = field.show ? data[field.fieldName] : null
        } else {
          dataValue = data[field.fieldName]
        }

        // 系统字段处理
        if (field.formType == 'product') {
          this.getProductParams(params, dataValue)
        } else if (field.formType == 'map_address') {
          this.getCustomerAddressParams(params.entity, dataValue)
        } else if (field.fieldType == 1) {
          const fieldValue = this.getRealParams(field, dataValue)
          params.entity[field.fieldName] = isEmpty(fieldValue) ? '' : fieldValue
        } else if (!ignoreFormType.includes(field.formType)) {
          const valueObj = {
            ...(isFullField ? field : {}),
            fieldName: field.fieldName,
            fieldType: field.fieldType,
            name: field.name,
            type: field.type,
            fieldId: field.fieldId,
            value: this.getRealParams(field, dataValue)
          }
          if (isArray(otherKeys)) {
            otherKeys.forEach(key => {
              valueObj[key] = field[key]
            })
          }
          params.field.push(valueObj)
        }
      }
      return params
    },

    /**
     * 获取产品提交参数
     */
    getProductParams(params, dataValue) {
      if (dataValue) {
        params['product'] = dataValue.product ? dataValue.product.map(item => {
          item.salesPrice = item.salesPrice == '' ? 0 : item.salesPrice
          item.num = item.num == '' ? 0 : item.num
          item.discount = item.discount == '' ? 0 : item.discount
          return item
        }) : []
        params.entity['totalPrice'] = dataValue.totalPrice
          ? dataValue.totalPrice
          : 0
        params.entity['discountRate'] = dataValue.discountRate
          ? dataValue.discountRate
          : 0
      } else {
        params['product'] = []
        params.entity['totalPrice'] = ''
        params.entity['discountRate'] = ''
      }
    },

    /**
     * 获取客户位置提交参数
     */
    getCustomerAddressParams(params, dataValue) {
      if (dataValue) {
        params['address'] = dataValue.address
          ? dataValue.address.join(',')
          : ''
        params['detailAddress'] = dataValue.detailAddress
        params['location'] = dataValue.location
        params['lng'] = dataValue.lng
        params['lat'] = dataValue.lat
      } else {
        params['address'] = ''
        params['detailAddress'] = ''
        params['location'] = ''
        params['lng'] = ''
        params['lat'] = ''
      }
    },

    /**
     * 关联客户 联系人等数据要特殊处理
     */
    getRealParams(field, dataValue) {
      const crmTypeArr = ['customer', 'contacts', 'business', 'leads', 'contract', 'receivables',
        'supplier_cause', 'purchase_cause', 'sale_cause', 'sale_return_cause', 'retreat_cause', 'warehouse_cause']
      if (crmTypeArr.includes(field.formType)) {
        if (isEmpty(dataValue)) return ''
        if (isArray(dataValue) && dataValue.length > 0 && isObject(dataValue[0])) {
          return dataValue[0][`${field.formType}Id`]
        } else if (isObject(dataValue)) {
          return dataValue.id
        }
        if (isArray(dataValue)) return dataValue[dataValue.length - 1]
        if (['string', 'number'].includes(typeof dataValue)) return dataValue
        return ''
      } else if (
        field.formType == 'user' ||
        field.formType == 'structure'
      ) {
        let newDataValue = dataValue || []
        if (isArray(dataValue) && dataValue.length > 0) {
          if (isObject(dataValue[0])) {
            newDataValue = dataValue.map(valueItem => field.formType == 'user'
              ? valueItem.userId
              : valueItem.id)
          }
        } else if (isObject(dataValue)) {
          newDataValue = field.formType == 'user'
            ? dataValue.userId
            : dataValue.id
        }
        return isArray(newDataValue) ? newDataValue.join(',') : newDataValue
      } else if (field.formType == 'file') {
        if (dataValue && dataValue.length > 0) {
          return dataValue[0].batchId
        }
        return ''
      } else if (field.formType == 'category') {
        if (isEmpty(dataValue)) return ''
        if (!isArray(dataValue)) return dataValue
        if (dataValue && dataValue.length > 0) {
          return dataValue[dataValue.length - 1]
        }
        return ''
      } else if (field.formType == 'checkbox') {
        if (isArray(dataValue)) {
          return dataValue.join(',')
        }
        return dataValue
      } else if (field.formType == 'select') {
        return dataValue
      } else if (field.formType == 'detail_table') {
        const fieldExtendList = field.fieldExtendList || []
        const values = []
        const tableValues = Array.isArray(dataValue) ? dataValue : []

        for (let tableValueIndex = 0; tableValueIndex < tableValues.length; tableValueIndex++) {
          const tableValue = tableValues[tableValueIndex]
          // 去除空值数据
          if (!this.getFormValueIsEmpty(fieldExtendList, tableValue)) {
            const batchId = tableValue.batchId || guid()
            const valuesItems = []
            fieldExtendList.forEach(tableField => {
              const copyTableField = objDeepCopy(tableField)
              delete copyTableField.companyId
              copyTableField.value = this.getRealParams(copyTableField, tableValue[copyTableField.fieldName])
              copyTableField.batchId = batchId
              valuesItems.push(copyTableField)
            })
            values.push(valuesItems)
          }
        }

        return values
      }

      return dataValue
    },

    /**
     * 判断对象值是否是空
     */
    getFormValueIsEmpty(fieldList, valueObj) {
      for (let index = 0; index < fieldList.length; index++) {
        const field = fieldList[index]
        const value = valueObj[field.fieldName]
        if (field.formType === 'location') {
          if (isObject(value) && (!isEmpty(value.lat) || !isEmpty(value.lng) || !isEmpty(value.address))) {
            return false
          }
        } else if (!isEmpty(value)) {
          return false
        }
      }
      return true
    },

    /**
     * 获取逻辑表单隐藏id
     */
    getFormAssistIds(list, valueObj) {
      let allIds = []
      // 如果valueObj 不存 在，根据默认值 处理
      const defaultValueObj = valueObj ? null : {}
      // 如果存在需要读取默认值
      if (valueObj) {
        this.itemsForEach(list, item => {
          if (!valueObj.hasOwnProperty(item.field)) {
            valueObj[item.field] = this.getItemValue(item)
          }
        })
      }
      this.itemsForEach(list, item => {
        if (defaultValueObj) {
          defaultValueObj[item.field] = this.getItemValue(item)
        }
        if ([
          'select',
          'checkbox'
        ].includes(item.formType) &&
        item.remark === 'options_type' &&
        item.optionsData) {
          // eslint-disable-next-line no-unused-vars
          for (const key in item.optionsData) {
            allIds = allIds.concat(item.optionsData[key] || [])
          }
        }
      })

      allIds = allIds.filter(o => Boolean(o))
      allIds = Array.from(new Set(allIds))

      const ignoreIds = []
      this.getFormAssistData(list, valueObj || defaultValueObj, allIds, ignoreIds)
      return allIds.filter(o => !ignoreIds.includes(o))
    },

    /**
     * 获取信息
     */
    getFormAssistData(list, valueObj, allIds, ignoreIds) {
      // let ignorIds = []
      const ignoreLength = ignoreIds.length
      this.itemsForEach(list, item => {
        if ([
          'select',
          'checkbox'
        ].includes(item.formType) &&
        item.remark === 'options_type' &&
        item.optionsData) {
          let value = valueObj ? valueObj[item.field || item.fieldName] : item.value
          if (!allIds.includes(item.formAssistId)) {
            if (item.formType === 'select') {
              if (isEmpty(value)) {
                value = []
              } else {
                const findRes = item.setting.find(o => {
                  return isObject(o) ? o.value === value : o === value
                })
                if (findRes) {
                  const _val = isObject(findRes) ? findRes.value : findRes
                  value = [_val]
                } else {
                  value = [LOGIC_OTHER]
                }
                // value = item.setting.includes(value) ? [value] : [LOGIC_OTHER]
              }
            } else if (item.formType === 'checkbox') {
              if (isArray(value)) {
                const copyValue = objDeepCopy(value)
                const otherItem = copyValue.filter((name) => !item.setting.includes(name))
                if (otherItem.length > 0) {
                  const newValue = copyValue.filter((name) => !otherItem.includes(name))
                  newValue.push(LOGIC_OTHER)
                  value = newValue
                }
              } else {
                value = []
              }
            }

            // eslint-disable-next-line no-unused-vars
            for (const key in item.optionsData) {
              // if (value && value.includes(key)) {
              if (value) {
                const findIndex = value.findIndex(o => o == key)
                if (findIndex === -1) continue

                const keyValue = item.optionsData[key] || []
                for (let index = 0; index < keyValue.length; index++) {
                  const id = keyValue[index]
                  if (!ignoreIds.includes(id)) {
                    ignoreIds.push(id)
                  }
                }
              }
            }
          }
        }
      })

      if (ignoreLength !== ignoreIds.length) {
        const newAllIds = allIds.filter(o => !ignoreIds.includes(o))
        this.getFormAssistData(list, valueObj, newAllIds, ignoreIds)
      }
    },

    /**
     * 获取表单展示内容
     * data 用于相关模块新建填充模块值
     * type 新建编辑
     * rules 默认权限
     */
    getFormContentByOptionsChange(fieldList, formObj, rules = {}, data = {}, type) {
      const allFieldRules = this.fieldRules || rules
      const actionData = this.action && this.action.data ? this.action.data : data
      const actionType = this.action && this.action.type ? this.action.type : type

      const fieldRules = {}
      const fieldForm = {}

      // 依据最新的值，获取隐藏的ids
      const assistIds = this.getFormAssistIds(fieldList, formObj)

      this.itemsForEach(fieldList, fieldItem => {
        fieldItem.show = !assistIds.includes(fieldItem.formAssistId)
        // 展示 并且 允许编辑，加入验证规 则
        if (fieldItem.show && !fieldItem.disabled) {
          if (allFieldRules[fieldItem.field]) {
            fieldRules[fieldItem.field] = allFieldRules[fieldItem.field]
          } else {
            fieldRules[fieldItem.field] = this.getRules(fieldItem)
          }
        }

        // 值获取
        if (fieldItem.show) {
          // 有属性，说明之前在页面内已经展示，读取当前值
          if (formObj.hasOwnProperty(fieldItem.field)) {
            fieldForm[fieldItem.field] = formObj[fieldItem.field]
          } else {
            fieldForm[fieldItem.field] = this.getItemValue(fieldItem, actionData, actionType)
          }
        }
      })

      return {
        fieldForm,
        fieldRules
      }
    }

  }
}
