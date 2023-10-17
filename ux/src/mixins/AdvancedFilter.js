import { isArray, isObject } from '@/utils/types'
import Vue from 'vue'

export default {
  methods: {
    /**
     * @description: 通过高级筛选type获取条件名称
     * @param {*} type
     * @return {*}
     */
    getAdvancedFilterOptionName(type, formType) {
      const isDate = formType === 'date' || formType === 'datetime'
      return {
        1: '等于',
        2: '不等于',
        3: '包含',
        4: '不包含',
        5: '为空',
        6: '不为空',
        7: isDate ? '晚于' : '大于',
        8: isDate ? '不早于' : '大于等于',
        9: isDate ? '早于' : '小于',
        10: isDate ? '不晚于' : '小于等于',
        12: '开始于',
        13: '结束于',
        14: isDate ? '等于（时间段）' : '等于（范围）'
      }[type] || ''
    },

    /**
     * 根据类型获取条件
     * @param {String} formType
     * @param {String} fieldName // 后期改为仅以formType 作为依据，外层进行formType校准
     */
    getAdvancedFilterOptions(formType, fieldName) {
      // 单行文本、多行文本、网址、手机、邮箱
      // 等于、不等于、包含、不包含、开始于、结束于、为空、不为空
      if (formType == 'text' ||
      formType == 'textarea' ||
      formType == 'website' ||
      formType == 'mobile' ||
      formType == 'email' ||
      formType == 'module' ||
      formType == 'serial_number') {
        return [
          { value: 'is', label: '等于', disabled: false, type: 1 },
          { value: 'isNot', label: '不等于', disabled: false, type: 2 },
          { value: 'contains', label: '包含', disabled: false, type: 3 },
          { value: 'notContains', label: '不包含', disabled: false, type: 4 },
          { value: 'startWith', label: '开始于', disabled: false, type: 12 },
          { value: 'endWith', label: '结束于', disabled: false, type: 13 },
          { value: 'isNull', label: '为空', disabled: false, type: 5 },
          { value: 'isNotNull', label: '不为空', disabled: false, type: 6 }
        ]
      } else if (
        formType === 'invoiceStatus' ||
        formType == 'checkStatus' ||
        formType == 'dealStatus' ||
        formType == 'receivedStatus' ||
        formType == 'status' // this.crmType === 'product' && fieldName === 'status' && formType === 'select' 通过conditionTypeFun 在外层校准为 status
      ) {
        return [
          { value: 'is', label: '等于', disabled: false, type: 1 },
          { value: 'isNot', label: '不等于', disabled: false, type: 2 }
        ]
      } else if (
        // 下拉框 等于、不等于、为空、不为空
        formType == 'select' ||
        formType == 'field_attention'
      ) {
        return [
          { value: 'is', label: '等于', disabled: false, type: 1 },
          { value: 'isNot', label: '不等于', disabled: false, type: 2 },
          { value: 'isNull', label: '为空', disabled: false, type: 5 },
          { value: 'isNotNull', label: '不为空', disabled: false, type: 6 }
        ]
      } else if (
        // 布尔值 等于、不等于
        formType == 'boolean_value'
      ) {
        return [
          { value: 'is', label: '等于', disabled: false, type: 1 },
          { value: 'isNot', label: '不等于', disabled: false, type: 2 }
        ]
      } else if (
        formType == 'checkbox' ||
        formType == 'location'
      ) {
        return [
          { value: 'is', label: '等于', disabled: false, type: 1 },
          { value: 'isNot', label: '不等于', disabled: false, type: 2 },
          { value: 'contains', label: '包含', disabled: false, type: 3 },
          { value: 'notContains', label: '不包含', disabled: false, type: 4 },
          { value: 'isNull', label: '为空', disabled: false, type: 5 },
          { value: 'isNotNull', label: '不为空', disabled: false, type: 6 }
        ]
        // 数字、货币、百分数
        // 等于、不等于、大于、大于等于、小于、小于等于、区间（范围）、为空、不为空
      } else if (
        formType == 'number' ||
        formType == 'floatnumber' ||
        formType == 'percent'
      ) {
        return [
          { value: 'is', label: '等于', disabled: false, type: 1 },
          { value: 'isNot', label: '不等于', disabled: false, type: 2 },
          { value: 'gt', label: '大于', disabled: false, type: 7 },
          { value: 'egt', label: '大于等于', disabled: false, type: 8 },
          { value: 'lt', label: '小于', disabled: false, type: 9 },
          { value: 'elt', label: '小于等于', disabled: false, type: 10 },
          { value: 'range', label: '等于（范围）', disabled: false, type: 14 },
          { value: 'isNull', label: '为空', disabled: false, type: 5 },
          { value: 'isNotNull', label: '不为空', disabled: false, type: 6 }
        ]
        // 日期、日期时间
        // 等于、不等于、早于(大于)、晚于（小于）、不早于（小于等于）、不晚于（大于等于）、等于（时间段）、为空、不为空
      } else if (
        formType == 'date' ||
        formType == 'datetime'
      ) {
        return [
          { value: 'is', label: '等于', disabled: false, type: 1 },
          { value: 'isNot', label: '不等于', disabled: false, type: 2 },
          { value: 'lt', label: '早于', disabled: false, type: 9 },
          { value: 'gt', label: '晚于', disabled: false, type: 7 },
          { value: 'egt', label: '不早于', disabled: false, type: 8 },
          { value: 'elt', label: '不晚于', disabled: false, type: 10 },
          { value: 'range', label: '等于（时间段）', disabled: false, type: 14 },
          { value: 'isNull', label: '为空', disabled: false, type: 5 },
          { value: 'isNotNull', label: '不为空', disabled: false, type: 6 }
        ]
        // 日期区间 地址 无判断符
      } else if (
        formType == 'date_interval' ||
        formType == 'position'
      ) {
        return []
        // 人员、部门 包含、不包含、为空、不为空
      } else if (
        formType == 'user' ||
        formType == 'structure' ||
        formType == 'single_user' ||
        formType == 'field_tag'
      ) {
        return [
          { value: 'contains', label: '包含', disabled: false, type: 3 },
          { value: 'notContains', label: '不包含', disabled: false, type: 4 },
          { value: 'isNull', label: '为空', disabled: false, type: 5 },
          { value: 'isNotNull', label: '不为空', disabled: false, type: 6 }
        ]
      } else {
        return []
      }
    },

    /**
     * 根据类型确定不同的默认值属性
     * item 校准后对象
     * obj 字段对象
     * formTypeFun 校准formType 部分页面（产品下状态）formType 需要校准
     */
    getAdvancedFilterDefaultItemByFormType(item, obj, formTypeFun) {
      if (!obj) {
        obj = item
      }

      if (item.formType == 'business_cause') {
        item.typeOption = obj.setting
        item.settingList = []
        Vue.set(item, 'flowName', '')
        Vue.set(item, 'settingName', '')
        item.value = ''
      } else if (
        item.formType == 'checkStatus' ||
        item.formType == 'dealStatus' ||
        item.formType == 'invoiceStatus' ||
        item.formType == 'receivedStatus' ||
        item.formType == 'status'
      ) {
        item.setting = obj.setting || []
        item.value = ''
      } else if (item.formType == 'map_address') {
        item.address = {
          state: '',
          city: '',
          area: ''
        }
      } else if (
        item.formType === 'user' ||
        item.formType === 'single_user' ||
        item.formType === 'structure' ||
        item.formType === 'category' ||
        item.formType === 'position'
      ) {
        item.value = []
      } else if (
        item.formType === 'date' ||
        item.formType === 'datetime'
      ) {
        item.value = '' // 时间点
        Vue.set(item, 'timeType', '')
        Vue.set(item, 'timeTypeName', '')
        Vue.set(item, 'range', [])
      } else if (
        item.formType == 'select' ||
        item.formType === 'checkbox' ||
        item.formType === 'field_attention'
      ) {
        item.setting = obj.setting || []
        item.value = []
      } else if (
        item.formType == 'number' ||
        item.formType == 'floatnumber' ||
        item.formType == 'percent'
      ) {
        item.min = ''
        item.max = ''
        item.value = ''
      } else if (item.formType === 'field_tag') {
        item.setting = obj.setting || []
        item.value = ''
      } else {
        item.value = ''
      }

      const conditions = this.getAdvancedFilterOptions(formTypeFun ? formTypeFun(item) : item.formType, item.fieldName)
      if (conditions.length > 0) {
        const conditionObj = conditions[0]
        item.condition = conditionObj.value
        item.type = conditionObj.type
      } else {
        item.condition = 'is'
        item.type = 1
      }
      return item
    },

    /**
     * 重置高级筛选指
     */
    resetAdvancedFilterFieldsValue(list) {
      list.forEach(item => {
        if (
          item.formType == 'number' ||
          item.formType == 'floatnumber' ||
          item.formType == 'percent'
        ) {
          item.min = ''
          item.max = ''
          item.value = ''
        } else if (item.formType == 'business_cause') {
          item.flowName = ''
          item.settingName = ''
          item.value = ''
          item.settingList = []
        } else if (item.formType == 'map_address') {
          item.address = {
            state: '',
            city: '',
            area: ''
          }
        } else if ([
          'date',
          'datetime'
        ].includes(item.formType)) {
          item.value = ''
          item.range = []
        } else if (isArray(item.value)) {
          item.value = []
        } else if (isObject(item.value)) {
          item.value = {}
        } else {
          item.value = ''
        }
      })
    }
  }
}
