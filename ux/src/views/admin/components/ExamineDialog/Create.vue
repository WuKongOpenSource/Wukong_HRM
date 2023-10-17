<template>
  <transition name="opacity-fade">
    <div class="business-approve-flow-create">
      <wk-backgroud-tabs
        v-model="tabIndex"
        :options="tabs"
      >
        <template slot="right">
          <el-button type="primary" @click="sendClick">保存</el-button>
          <i
            class="el-icon-close create-close"
            @click="closeClick" />
        </template>
      </wk-backgroud-tabs>
      <base-info-set
        v-show="tabIndex === 'base'"
        ref="baseInfoSet"
        :examine="examine"
        :is-stage-flow="isStageFlow"
        :fields="fields"
        :fields-form="fieldsForm"
        :fields-rules="fieldsRules"
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
import WkBackgroudTabs from '@/views/admin/examine/components/WkBackgroudTabs'
import BaseInfoSet from '@/views/admin/examine/components/BaseInfoSet'
import { WkApproveFlow } from '@/components/ApprovalFlow'

import { getMaxIndex } from '@/utils'
import { examineModel } from '@/components/ApprovalFlow'
import { objDeepCopy } from '@/utils'
import GenerateRulesMixin from '@/components/NewCom/WkForm/GenerateRules'
import ExamineInfoMinxin from '@/views/admin/examine/mixins/ExamineInfo'
import fieldTypeLib from '../../fields/fieldTypeLib'

export default {
  name: 'ExamineDialog',
  components: {
    WkBackgroudTabs,
    BaseInfoSet,
    WkApproveFlow
  },
  filters: {},
  mixins: [GenerateRulesMixin, ExamineInfoMinxin],
  props: {
    detail: Object,
    moduleType: String,
    examine: { // 是否开启高级配置
      type: Boolean,
      default: false
    },
    isStageFlow: { // 是否阶段审批
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: false,
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
      conditionFields: null
    }
  },
  computed: {
    approveFlowConfig() {
      const list = (this.detail.formList || [])
        .flat()
        .filter(field =>
          field.isNull &&
          [
            'select',
            'checkbox',
            'number',
            'floatnumber'
          ].includes(field.formType)
        )
        .map(field => {
          const findRes = fieldTypeLib.find(o => o.formType === field.formType) || {}
          return {
            type: findRes.type,
            ...field
          }
        })

      return {
        conditionSelectList: list,
        examineNodeProps: {
          addExternalUserShow: false
        }
      }
    }
  },
  watch: {
    detail: {
      handler(val) {
        // 加入获取员工字段逻辑
        const formList = (this.detail.formList || []).flat()
        const memberControlList = []
        formList.forEach(item => {
          if (item.formType === 'user' && item.isNull === 1) {
            // 新拖入的字段没有fieldName 过滤掉
            if (item.fieldName) {
              memberControlList.push({
                ...item,
                fieldName: item.fieldName
              })
            }
          }
        })

        this.memberControlList = memberControlList
      },
      deep: true,
      immediate: true
    }
  },
  created() {
    this.getBaseField()
    if (
      this.detail.examineSaveBO &&
      Object.keys(this.detail.examineSaveBO).length > 0 &&
      this.detail.examineSaveBO.dataList &&
      this.detail.examineSaveBO.dataList.length > 0
    ) {
      this.getFlowList()
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
    /**
     * 获取流程详情
     */
    getFlowList(examineId = null) {
      const list = this.detail.examineSaveBO.dataList || []
      const dataList = []
      this.getListInfo(list, dataList)
      this.flowList = dataList
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
        field: 'managerList',
        formType: 'user',
        // isNull: 1,
        name: '审批流管理员',
        setting: [],
        radio: false,
        // tipType: 'tooltip',
        // inputTips: '<div>1、可以在"配置流程"设置当审批人为空，审批<br>自动转交给审批流管理员；当管理员也请假/离<br>职，审批将转交给超级管理员。</div><div>2、可指定多个管理员，审批方式为或签。</div>',
        value: this.detail.examineSaveBO ? this.detail.examineSaveBO.managerList : 1
      })

      field.push({
        field: 'remarks',
        formType: 'textarea',
        isNull: 0,
        name: '流程说明',
        maxlength: 200,
        setting: [],
        placeholder: '(请填写相关注意事项，方便员工在申请时查阅，限制输入200字)',
        // inputTips: '请填写相关注意事项，方便员工在申请时查阅，限制输入200字',
        value: this.detail.examineSaveBO ? this.detail.examineSaveBO.remarks : ''
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

      const examineSaveBO = this.detail.examineSaveBO

      if (examineSaveBO) {
        fieldForm.advancedConfigBO = examineSaveBO.examineAdvancedConfigVO || examineSaveBO.advancedConfigBO
      } else {
        fieldForm.advancedConfigBO = {}
      }

      this.fields = objDeepCopy(list)
      this.fieldsForm = fieldForm
      this.fieldsRules = fieldRules
    },

    /**
     * change
     */
    formChange(item, index, value) {
      if (item.field === 'label') {
        this.flowList = [objDeepCopy(examineModel)]
      }
    },

    /**
     * 发布点击
     */
    sendClick() {
      this.$refs.baseInfoSet.validate().then(valid => {
        if (valid) {
          const flowParams = this.$refs.wkApproveFlow.getParams()
          if (flowParams.isError) {
            this.$message.error('请完善信息')
          } else {
            const params = {
              ...this.fieldsForm,
              dataList: flowParams.list
            }
            // console.log('confirm: ', params)
            this.$emit('change', params)
          }
        }
      })
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
