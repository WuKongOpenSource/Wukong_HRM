import { superExamineRecordAPI } from '@/api/oa/superExamine'
import { examineRecordQueryAuthFieldAPI } from '@/api/examine'
import { oaExamineReadAPI } from '@/api/oa/examine'

import CustomFieldsMixin from '@/mixins/CustomFields'
import { objDeepCopy } from '@/utils'

export default {
  methods: {
    /**
     * @description: 获取提交信息
     * @param {*} mainLabel
     * @param {*} authFields
     * @param {*} createView
     * @return {*}
     */
    flowGetAuthPostFields(mainLabel, authFields, createView, detailData) {
      return new Promise((resolve, reject) => {
        if (createView) {
          createView.getCustomFieldPostParams().then(res => {
            const { entity, field: postFields } = res
            const batchId = detailData.batchId // 业务 batchId // 业务详情
            const flowFieldUpdateLogs = []
            authFields.forEach((item, itemIndex) => {
              // 只提交可编辑 权限 为 3 的数据
              if (item.authLevel === 3) {
                let newValue = null
                if (item.fieldType == 1) {
                  newValue = entity ? entity[item.fieldName] : null
                } else {
                  const fieldItem = postFields.find(fieldObj => fieldObj.fieldName === item.fieldName)
                  newValue = fieldItem ? fieldItem.value : null
                }
                let oldValue = null
                if (['detail_table'].includes(item.formType)) { // 明细表格item里的value 就是oldValue
                  if (Array.isArray(item.value)) {
                    const oldCopyValue = objDeepCopy(item.value)
                    oldCopyValue.forEach(tableItems => {
                      tableItems.forEach(tableItem => {
                        tableItem.value = CustomFieldsMixin.methods.getRealParams(tableItem, tableItem.value)
                      })
                    })
                    oldValue = oldCopyValue
                  } else {
                    oldValue = item.value
                  }
                } else {
                  oldValue = CustomFieldsMixin.methods.getRealParams(item, item.value) // 特殊数据校准为上传数据 例如 客户 员工等
                }
                flowFieldUpdateLogs.push({
                  batchId: batchId,
                  label: mainLabel,
                  fieldName: item.fieldName,
                  fieldId: item.fieldId,
                  type: item.type,
                  fieldType: item.fieldType,
                  name: item.name,
                  newValue: newValue && typeof newValue === 'object' ? JSON.stringify(newValue) : newValue,
                  oldValue: oldValue && typeof oldValue === 'object' ? JSON.stringify(oldValue) : oldValue
                })
              }
            })
            resolve(flowFieldUpdateLogs)
          }).catch(err => {
            reject(err)
          })
        }
      })
    },

    /**
     * @description: 获取审批流程信息
     * @param {*} recordId
     * @return {*}
     */
    flowGetFlowStepList(recordId) {
      return new Promise((resolve, reject) => {
        superExamineRecordAPI({ recordId }).then(res => {
          resolve(res)
        }).catch(err => {
          reject(err)
        })
      })
    },

    /**
     * @description: 获取授权字段
     * @return {*}
     */
    flowGetAuthFields(params) {
      return new Promise((resolve, reject) => {
        examineRecordQueryAuthFieldAPI(params).then(res => {
          resolve(res)
        }).catch(err => {
          reject(err)
        })
      })
    },

    /**
     * @description: 根据流程信息获取审批id
     * @return {*}
     */
    flowGetCurrentFlowId(examineFlowList) {
      let flowId = ''
      examineFlowList.forEach(item => {
        if (item.examineStatus == 3) {
          flowId = item.flowId
        }
      })
      return flowId
    },

    /**
     * @description: 获取详情请求方法
     * @return {*}
     */
    flowGetCRMDetailRequestFun(createType) {
      return {
      }[createType]
    },

    /**
     * @description: 获取详情请求方法
     * @return {*}
     */
    flowGetOADetailRequestFun() {
      return oaExamineReadAPI
    }
  }
}
