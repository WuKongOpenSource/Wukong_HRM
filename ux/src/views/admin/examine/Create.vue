<template>
  <transition name="opacity-fade">
    <div class="business-approve-flow-create">
      <wk-backgroud-tabs
        v-model="tabIndex"
        :options="tabs"
      >
        <template slot="right">
          <el-button type="primary" @click="sendClick">发布</el-button>
          <i
            class="el-icon-close create-close"
            @click="closeClick" />
        </template>
      </wk-backgroud-tabs>
      <base-info-set
        v-show="tabIndex === 'base'"
        ref="baseInfoSet"
        :examine="true"
        :fields="fields"
        :fields-form="fieldsForm"
        :fields-rules="fieldsRules"
        :advanced-props="advancedProps"
        @change="formChange"
      />
      <wk-approve-flow
        v-show="tabIndex === 'flow'"
        ref="wkApproveFlow"
        :props="approveFlowConfig"
        :list="flowList"
        :send-node="sendNode" />
    </div>
  </transition>
</template>

<script type="text/javascript">
import {
  examinesQueryFieldAPI,
  examinesAddAPI,
  examinesQueryExamineFlowAPI
} from '@/api/examine'
import { crmInvoiceTypeListAPI } from '@/api/admin/crm'
import {
  jxcFieldQueryFieldListAPI,
  crmFieldQueryFieldAPI
} from '@/api/oa/superExamine'

import WkBackgroudTabs from './components/WkBackgroudTabs'
import BaseInfoSet from './components/BaseInfoSet'
import { WkApproveFlow } from '@/components/ApprovalFlow'

import { getMaxIndex } from '@/utils'
import { examineModel } from '@/components/ApprovalFlow'
import { objDeepCopy } from '@/utils'
import GenerateRulesMixin from '@/components/NewCom/WkForm/GenerateRules'
import ExamineInfoMinxin from './mixins/ExamineInfo'

export default {
  name: 'BusinessApproveFlowCreate',
  components: {
    WkBackgroudTabs,
    BaseInfoSet,
    WkApproveFlow
  },
  filters: {},
  mixins: [GenerateRulesMixin, ExamineInfoMinxin],
  props: {
    moduelType: String, // 暂时传入router name
    isCopy: {
      type: Boolean,
      default: false
    },
    labelList: Array, // 支持的类型
    detail: Object
  },
  data() {
    return {
      height: document.documentElement.clientHeight - 100,
      tabs: [{
        label: '1.配置基础信息',
        value: 'base'
      }, {
        label: '2.配置流程',
        value: 'flow'
      }],
      tabIndex: 'base',
      sendNode: {
        name: '发起人',
        content: '具有新建权限的员工'
      },
      fields: [],
      fieldsForm: {},
      fieldsRules: {},
      flowList: [], // 数据源
      conditionFields: null,
      invoiceTypeList: [] // 开票类型
    }
  },
  computed: {
    advancedProps() {
      return {
        privilegeOnlyTermination: this.moduelType === 'hrmExamine'
      }
    },

    approveFlowConfig() {
      const defaultConfig = {
        conditionSelectRequest: examinesQueryFieldAPI,
        conditionSelectParams: {
          label: this.fieldsForm.label
        }, // 3 为发票
        conditionSelectList: null,
        nodeFieldSetShow: true,
        nodeFieldSetRequestFun: null
      }

      if (!this.moduelType) {
        return defaultConfig
      }

      if (this.moduelType === 'customerExamine') {
        const labelId = this.fieldsForm.label
        const labelObj = {
          1: 6,
          2: 7,
          3: 18
        }
        const label = labelObj[labelId]
        defaultConfig.nodeFieldItemParamsFun = (item) => {
          item.label = label
          if (item.formType === 'detail_table' && item.fieldExtendList) {
            item.fieldExtendList.forEach(subItem => {
              subItem.label = label
            })
          }
        }
        defaultConfig.nodeFieldSetRequestFun = () => {
          return new Promise((resolve, reject) => {
            crmFieldQueryFieldAPI(label).then(res => {
              const fieldList = res.data
              if (label == 18) { // 限制发票
                const contractItem = fieldList.find(item => item.fieldName === 'contract_money' || item.fieldName === 'contractMoney')
                if (contractItem) {
                  this.$set(contractItem, 'hideEdit', true)
                }
              }
              resolve(fieldList)
            }).catch(() => {
              reject([])
            })
          })
        }
      } else if (this.moduelType === 'jxcExamine') {
        const label = this.fieldsForm.label
        defaultConfig.nodeFieldItemParamsFun = (item) => {
          item.label = label
          if (item.formType === 'detail_table' && item.fieldExtendList) {
            item.fieldExtendList.forEach(subItem => {
              subItem.label = label
            })
          }
        }

        defaultConfig.nodeFieldSetRequestFun = () => {
          return new Promise((resolve, reject) => {
            const jxcLabel = {
              5: 3,
              6: 4,
              7: 5,
              8: 6,
              9: 9,
              10: 10,
              11: 11,
              12: 12
            }[label]
            jxcFieldQueryFieldListAPI(jxcLabel).then(res => {
              resolve(res.data)
            }).catch(() => {
              reject([])
            })
          })
        }
      } else if (this.moduelType === 'hrmExamine') {
        defaultConfig.nodeFieldSetShow = false
        defaultConfig.examineTypeFilterFun = (list) => {
          return list.filter(item => item.value !== 8) // 关联人员字段 人资不使用
        }
      }

      return defaultConfig
    },

    examineId() {
      return this.detail ? this.detail.examineId : null
    }
  },
  created() {
    if (this.detail && this.detail.label === 3) {
      this.getInvoiceTypeList()
    }
    this.getBaseField()
    if (this.examineId) {
      this.getFlowList(this.examineId)
    } else {
      this.flowList = [objDeepCopy(examineModel)]
    }
  },
  mounted() {
    this.$el.style.zIndex = getMaxIndex()
    document.body.appendChild(this.$el)

    window.onresize = () => {
      this.height = document.documentElement.clientHeight - 100
    }
  },
  destroyed() {
    // remove DOM node after destroy
    if (this.$el && this.$el.parentNode) {
      this.$el.parentNode.removeChild(this.$el)
    }
  },
  methods: {
    getInvoiceTypeList() {
      crmInvoiceTypeListAPI().then(res => {
        this.invoiceTypeList = res.data || []
      })
    },
    /**
     * 获取流程详情
     */
    getFlowList(examineId) {
      examinesQueryExamineFlowAPI({ examineId }).then(res => {
        const list = res.data || []
        const dataList = []
        this.getListInfo(list, dataList)
        this.flowList = dataList
      }).catch(() => {})
    },

    /**
     * 验证数据
     */
    validateSetting(item) {
      this.requestConditionFields().then(fields => {
        const field = fields.find(fieldItem => fieldItem.fieldName === item.fieldName)
        item.setting = field ? field.setting : []
      })
    },

    requestConditionFields() {
      const { conditionSelectRequest, conditionSelectParams, conditionSelectList } = this.approveFlowConfig
      if (conditionSelectList) {
        return Promise.resolve(conditionSelectList)
      }

      if (this.conditionFields) {
        return Promise.resolve(this.conditionFields)
      }
      return new Promise((resolve, reject) => {
        conditionSelectRequest(conditionSelectParams).then(res => {
          const list = res.data || []
          this.conditionFields = list
          resolve(list)
        }).catch(() => {
          resolve([])
        })
      })
    },

    /**
     * 获取自定义字段
     */
    getBaseField() {
      const field = []

      field.push({
        field: 'examineName',
        formType: 'text',
        isNull: 1,
        name: '审批流名称',
        setting: [],
        inputTips: '',
        value: this.detail ? this.detail.examineName : ''
      })

      // 新建审批类型 默认为 oa_examine
      field.push({
        field: 'label',
        formType: 'select',
        isNull: 0,
        name: '关联对象',
        setting: this.labelList,
        value: this.detail ? this.detail.label : (this.labelList && this.labelList.length > 0 ? this.labelList[0].value : '')
      })

      // field.push({
      //   field: 'dept',
      //   formType: 'userDep',
      //   isNull: 0,
      //   name: '应用部门',
      //   setting: [],
      //   inputTips: '默认全公司',
      //   value: {
      //     users: this.detail ? this.detail.userList : [],
      //     strucs: this.detail ? this.detail.deptList : []
      //   }
      // })

      field.push({
        field: 'managerList',
        formType: 'user',
        // isNull: 1,
        name: '流程管理员',
        setting: [],
        radio: false,
        // tipType: 'tooltip',
        // inputTips: '<div>1、可以在"配置流程"设置当审批人为空，审批<br>自动转交给审批流管理员；当管理员也请假/离<br>职，审批将转交给超级管理员。</div><div>2、可指定多个管理员，审批方式为或签。</div>',
        value: this.detail ? this.detail.managerList : 1
      })

      field.push({
        field: 'remarks',
        formType: 'textarea',
        isNull: 0,
        name: '流程说明',
        maxlength: 200,
        setting: [],
        placeholder: '（请填写相关注意事项，方便员工在申请时查阅，限制输入200字）',
        // inputTips: '请填写相关注意事项，方便员工在申请时查阅，限制输入200字',
        value: this.detail ? this.detail.remarks : ''
      })

      this.handleFields(field)
    },

    /**
     * 调整字段
     */
    handleFields(list) {
      const fieldRules = {}
      const fieldForm = {}
      list.forEach(item => {
        if (item.formType === 'userDep') {
          fieldForm.userList = []
          fieldForm.deptList = []
        } else {
          fieldRules[item.field] = this.getRules(item)
          fieldForm[item.field] = item.value
        }
      })
      fieldForm.advancedConfigBO = this.detail && this.detail.advancedConfigVO || {}
      this.fields = objDeepCopy(list)
      this.fieldsForm = fieldForm
      this.fieldsRules = fieldRules

      // 刷新字段
      this.getUserFieldList(this.fieldsForm.label)
    },

    /**
     * change
     */
    formChange(item, index, value) {
      if (value === 3 && this.invoiceTypeList.length === 0) {
        this.getInvoiceTypeList()
      }
      if (item.field === 'label') {
        this.flowList = [objDeepCopy(examineModel)]
        // 刷新字段
        this.getUserFieldList(this.fieldsForm.label)
      }
    },

    /**
     * 发布点击
     */
    sendClick() {
      const baseInfoSet = this.$refs.baseInfoSet
      const advancedSettingFrom = baseInfoSet.$refs.advancedSetting.$refs.advancedSettingFrom
      this.$refs.baseInfoSet.validate().then(valid => {
        advancedSettingFrom.validate(sValid => {
          if (valid && sValid) {
            const flowParams = this.$refs.wkApproveFlow.getParams()
            if (flowParams.isError) {
              this.$message.error('请完善信息')
            } else {
              const params = {
                ...this.fieldsForm,
                dataList: flowParams.list
              }

              const advancedConfigBO = params.advancedConfigBO
              const advancedLimitTimeBO = params.advancedConfigBO.advancedLimitTimeBO
              const limitTimeFlag = advancedConfigBO.limitTimeStatus
              if ((advancedConfigBO.nodeHandleType == 2 && !advancedConfigBO.nodeHandleUser?.length) ||
                  (advancedLimitTimeBO.handleType == 2 && (
                    !advancedLimitTimeBO.handleUserList?.length &&
                    !advancedLimitTimeBO.nodeHandleParentLevel?.length &&
                    !advancedLimitTimeBO.nodeHandleRole?.length
                  ) && limitTimeFlag)) {
                this.$message.error('配置基础信息中的添加人员不可为空')
                return
              } else if ((advancedConfigBO.nodeHandleType == 2 && advancedConfigBO.nodeHandleUser?.length > 20) ||
                        (advancedLimitTimeBO.handleType == 1 && advancedLimitTimeBO.handleUserList?.length > 20 && limitTimeFlag) ||
                        (advancedLimitTimeBO.handleType == 2 && advancedLimitTimeBO.handleUserList?.length > 20 && limitTimeFlag)) {
                this.$message.error('人员不能超过20人')
                return
              }

              if (this.examineId && !this.isCopy) {
                params.examineId = this.examineId
              }

              this.submiteRequest(params)
            }
          } else {
            this.$message.error('请完善信息')
          }
        })
      })
    },

    /**
     * 发送请求
     */
    submiteRequest(params) {
      examinesAddAPI(params).then(res => {
        this.$message.success('创建成功')
        this.$emit('success')
        this.closeClick()
      }).catch(() => {})
    },

    /**
     * 关闭
     */
    closeClick() {
      this.$emit('close')
    }
  }
}
</script>
<style lang="scss" scoped>
.opacity-fade-enter-active,
.opacity-fade-leave-active {
  transition: all 0.2s;
}

.opacity-fade-enter,
.opacity-fade-leave-to {
  opacity: 0;
}

.business-approve-flow-create {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background-color: #f5f5f7;

  .create-close {
    display: block;
    padding: 10px;
    font-size: 24px;
    color: #909399;
    cursor: pointer;
  }

  .create-close:hover {
    color: $--color-primary;
  }

  .base-info-set {
    width: 900px;
    height: calc(100% - 100px);
    margin: 20px auto 0;
  }

  .wk-approve-flow-wrap {
    top: 60px;
  }
}

</style>
