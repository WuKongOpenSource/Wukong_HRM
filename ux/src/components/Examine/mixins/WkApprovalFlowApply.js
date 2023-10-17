import {
  examinesPreviewFiledNameAPI,
  examinesQueryAdvancedConfigAPI,
  examinesQueryExamineUserFieldAPI
} from '@/api/examine'
import { isEmpty, isArray } from '@/utils/types'
import { objDeepCopy } from '@/utils'
import { debounce } from 'throttle-debounce'
import merge from '@/utils/merge'

/**
 * debouncedGetWkFlowList
 * initWkFlowData
 * validateWkFlowData
 */
export default {
  data() {
    /**
     * _wkFlowResolve 返回审批流结果
     * {
     *   list 审批未空 无审批流
     * }
     */
    return {
      // 审批信息
      _wkFlowSetFields: null, // 有值有审批流
      _wkFlowSetFieldNames: [], // 条件加关联员工的字段
      _wkFlowSetConditionFieldNames: [], // 条件内字段
      _wkFlowParams: {},
      _wkFlowListParams: {},
      _wkFlowResolve: null,
      _wkFieldForm: {}, // 自定字段值
      examineAdvancedSetting: {} // 审批高级设置
    }
  },

  created() {
    this.debouncedGetWkFlowList = debounce(500, this._getWkFlowList)
  },

  methods: {
    /**
     * 初始化审批流
     * data 包含 params 参数
     * fieldForm 自定义字段值
     */
    initWkFlowData(data, resultFun) {
      if (data.params) {
        this._wkFlowParams = data.params
      }
      if (data.fieldForm) {
        this._wkFieldForm = data.fieldForm
      }
      this._wkFlowResolve = resultFun
      this._getWkFlowSetFields()
    },

    /**
     * 获取可设置字段
     */
    _getWkFlowSetFields() {
      Promise.all([
        examinesPreviewFiledNameAPI(this._wkFlowParams),
        examinesQueryExamineUserFieldAPI(this._wkFlowParams)
      ])
        .then(res => {
          const [PreviewFiled, ExamineUserField] = res
          const data = (PreviewFiled.data || []).concat(ExamineUserField.data || [])
          this._wkFlowSetFields = data
          if (this._wkFlowSetFields) {
            // 条件字段，员工字段在有权限的情况下可编辑，需要固定的条件字段拼装完整的数据
            this._wkFlowSetConditionFieldNames = (PreviewFiled.data || []).map(item => item.fieldName)
            // 条件 加 员工的 所有字段
            this._wkFlowSetFieldNames = this._wkFlowSetFields.map(item => item.fieldName)
            this._getWkFlowList(null, this._wkFieldForm)
          } else {
            if (this._wkFlowResolve) {
              this._wkFlowResolve({
                list: null
              })
            }
          }
        })
      // examinesPreviewFiledNameAPI(this._wkFlowParams).then(res => {
      //   this._wkFlowSetFields = res.data
      //   if (this._wkFlowSetFields) {
      //     this._wkFlowSetFieldNames = this._wkFlowSetFields.map(item => item.fieldName)
      //     this._getWkFlowList(null, this._wkFieldForm)
      //   } else {
      //     if (this._wkFlowResolve) {
      //       this._wkFlowResolve({
      //         list: null
      //       })
      //     }
      //   }
      // }).catch(() => {})
    },

    /**
     * 获取审批流展示字段
     */
    _getWkFlowList(field, fieldForm) {
      // 不存在 不监测
      if (!this._wkFlowSetFields) {
        return
      }
      if (this._wkFlowSetFieldNames.includes(field) || !field) {
        this._wkFlowListParams = {
          ...this._wkFlowParams,
          dataMap: this.getWkFlowDataMap(fieldForm)
        }
        examinesQueryAdvancedConfigAPI(this._wkFlowListParams).then(res => {
          const resData = res.data || {}
          const wkFlowList = resData.examineId ? [] : null
          resData.label = this._wkFlowParams.label // 加入审批流的label
          this.examineAdvancedSetting = resData
          // wkFlowList.forEach(item => {
          //   item.values = []
          // })
          if (this._wkFlowResolve) {
            this._wkFlowResolve({
              list: wkFlowList,
              resData: resData
            })
          }
        }).catch(() => {})
      }
    },

    /**
     * @description: 根据条件字段和表单值获取有效条件对象
     * @param {*} fieldForm 数据表单对象
     * @param {*} userFieldNames 存储员工字段名的对象 相当于自定义
     * @return {*}
     */
    getWkFlowDataMap(fieldForm, userFieldNames) {
      const params = {}
      const validFieldNames = userFieldNames ? this._wkFlowSetConditionFieldNames.concat(userFieldNames) : (this._wkFlowSetFieldNames || [])
      validFieldNames.forEach(key => {
        const value = fieldForm ? fieldForm[key] : ''
        if (Array.isArray(value)) {
          params[key] = value.join(',')
        } else {
          params[key] = isEmpty(value) ? '' : value
        }
      })
      return params
    },

    /**
     * 验证数据是否完整，并反馈结果
     * props 配置项，默认为 forcePass 默认是false
     */
    validateWkFlowData(flowList, type, props) {
      const defaultConfig = {
        forcePass: false
      }
      const config = merge({ ...defaultConfig }, props || {})
      let pass = true
      const list = []

      if ((!flowList || (isArray(flowList) && flowList.length === 0)) && !config.forcePass) {
        return {
          pass,
          data: null
        }
      }
      flowList.forEach(item => {
        if (item.examineType === 4) {
          if (item.userList.length > 0) {
            const userList = []
            item.userList.forEach(sItem => {
              const data = {
                userId: sItem.userId,
                email: sItem.email || sItem.outerUserEmail
              }

              if (type == 'draft' || this.examineType == 'draft') data.realname = sItem.realname

              userList.push(data)
            })
            list.push({
              ...item,
              userList
            })
          } else {
            pass = false
          }
        } else {
          const flow = objDeepCopy(item)
          const userList = []
          flow.userList?.forEach(item => {
            if (item.hasOwnProperty('outerUserEmail') && item.outerUserEmail) {
              userList.push({ email: item.outerUserEmail })
            } else {
              userList.push(item)
            }
          })

          flow.userList = userList

          list.push(flow)
        }
      })

      return {
        pass: config.forcePass ? true : pass,
        data: {
          ...this._wkFlowListParams,
          examineFlowFinalBOList: list
        }
      }
    }
  }
}
