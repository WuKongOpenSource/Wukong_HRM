<template>
  <transition name="opacity-fade">
    <div
      v-loading="loading"
      class="business-approve-flow-create">
      <wk-backgroud-tabs
        v-model="tabIndex"
        :options="tabs"
        @tabClick="tabClick">
        <template slot="right">
          <el-button
            type="primary"
            :disabled="disableStatus"
            @click="sendClick">保存</el-button>
          <i
            class="el-icon-close create-close"
            @click="closeClick" />
        </template>
      </wk-backgroud-tabs>
      <base-info-set
        v-show="tabIndex === 'base'"
        ref="baseInfoSet"
        :verify="verify"
        :status="status"
        :base-form="fieldsForm.baseForm"
        :target-template-name="targetTemplateName"
        @pass="tabPass" />
      <indicator-settings
        v-show="tabIndex === 'target'"
        :taget-form="fieldsForm.appraisalPlanQuotaSettingBO"
        :template-id="fieldsForm.baseForm.appraisalTemplateId"
        :verify="verify"
        :status="status"
        @pass="tabPass" />
      <workflow-setting
        v-show="tabIndex === 'flow'"
        :verify="verify"
        :status="status"
        :flow-form="fieldsForm.appraisalPlanProcessSettingBO"
        @pass="tabPass" />

      <result-set
        v-show="tabIndex === 'result'"
        :verify="verify"
        :status="status"
        :result-form="fieldsForm.appraisalEmployeeResultSettingBO"
        :result-template-id="resultTemplateId"
        @pass="tabPass" />
    </div>
  </transition>
</template>

<script type="text/javascript">
import { hrmAppraisalPlanAddAppraisalPlanAPI } from '@/api/hrm/performance'

import WkBackgroudTabs from './components/WkBackgroudTabs'
import BaseInfoSet from './components/BaseInfoSet'
import IndicatorSettings from './components/IndicatorSettings'
import WorkflowSetting from './components/WorkflowSetting'
import ResultSet from './components/ResultSet'

import { getMaxIndex, objDeepCopy } from '@/utils'

export default {
  name: 'BusinessApproveFlowCreate',
  components: {
    WkBackgroudTabs,
    BaseInfoSet,
    IndicatorSettings,
    WorkflowSetting,
    ResultSet
  },
  filters: {},
  provide() {
    return {
      'disabled': this.disableStatus
    }
  },
  props: {
    inspectionDetial: Object
  },
  data() {
    return {
      loading: false,
      height: document.documentElement.clientHeight - 100,
      tabs: [{
        label: '1.基础设置',
        value: 'base'
      }, {
        label: '2.指标设置',
        value: 'target'
      }, {
        label: '3.流程设置',
        value: 'flow'
      }, {
        label: '4.结果设置',
        value: 'result'
      }],
      tabIndex: 'base',
      verify: {},
      fieldsForm: {
        baseForm: {
          inspectionScope: [
            { type: '' }
          ],
          appraisalCycleType: 1
        },
        appraisalPlanQuotaSettingBO: {
          scoreCalculation: 1,
          upperLimitType: 1,
          upperLimitScore: 100,
          quotaSettingDimensionBOList: []
        },
        appraisalPlanProcessSettingBO: {
          quotaSettingType: 1,
          processSettingScoringBOList: [
            {}
          ],
          processSettingResultAuditBOList: [
            { auditType: '' }
          ],
          processSettingResultConfirmationBOList: [
            { confirmationType: '' }
          ],
          processSettingTargetConfirmationBO: {
            confirmationLevel: '',
            confirmationType: '',
            identifyingPeople: ''
          }
        },
        appraisalEmployeeResultSettingBO: {
          resultTemplateId: '',
          syncToSalary: false,
          paidForMonth: '',
          appraisalPlanResultSettingLevelList: [{
            levelName: 'S',
            scoreLowerLimit: '85',
            scoreUpperLimit: '100',
            minNum: '10',
            maxNum: '15',
            coefficient: ''
          }, {
            levelName: 'A',
            scoreLowerLimit: '75',
            scoreUpperLimit: '84',
            minNum: '50',
            maxNum: '55',
            coefficient: ''
          }, {
            levelName: 'B',
            scoreLowerLimit: '60',
            scoreUpperLimit: '74',
            minNum: '30',
            maxNum: '35',
            coefficient: ''
          }, {
            levelName: 'C',
            scoreLowerLimit: '0',
            scoreUpperLimit: '59',
            minNum: '10',
            maxNum: '15',
            coefficient: ''
          }]
        }
      },
      pass: [],

      status: null,
      targetTemplateName: '',
      resultTemplateId: ''
    }
  },
  computed: {
    disableStatus() {
      return this.status == 3 || this.status == 4
    }
  },
  watch: {
    fieldsForm: {
      handler() {
        this.pass.forEach((item, index) => {
          if (item == this.tabIndex) {
            this.pass.splice(index, 1)
          }
        })
      },
      deep: true
    },
    inspectionDetial: {
      handler(val) {
        if (Object.keys(val).length) {
          const fieldsForm = objDeepCopy(val)
          this.status = fieldsForm.status
          this.targetTemplateName = fieldsForm.appraisalTemplateName
          this.resultTemplateId = fieldsForm.resultTemplateId
          // 基础设置
          const baseForm = {
            appraisalCycleType: fieldsForm.appraisalCycleType,
            appraisalCycle: fieldsForm.appraisalCycle.split(',').length == 2 ? fieldsForm.appraisalCycle.split(',') : fieldsForm.appraisalCycle,
            appraisalIllustrate: fieldsForm.appraisalIllustrate,
            appraisalPlanId: fieldsForm.appraisalPlanId,
            appraisalPlanName: fieldsForm.appraisalPlanName,
            appraisalTemplateId: fieldsForm.appraisalTemplateId,
            inspectionScope: fieldsForm.inspectionScope,
            quarter: fieldsForm.quarter
          }

          // 指标设置
          const targetForm = { ...fieldsForm.appraisalPlanQuotaSettingVO }
          targetForm.quotaSettingDimensionVOList.forEach(item => {
            item.quotaSettingDimensionQuotaBOList = item.quotaSettingDimensionQuotaVOList
            delete item.quotaSettingDimensionQuotaVOList
          })
          targetForm.quotaSettingDimensionBOList = targetForm.quotaSettingDimensionVOList
          delete targetForm.quotaSettingDimensionVOList

          // 流程设置
          const flowForm = { ...fieldsForm.appraisalPlanProcessSettingVO }
          flowForm.processSettingScoringVOList.push({})
          flowForm.processSettingScoringBOList = flowForm.processSettingScoringVOList
          flowForm.processSettingResultAuditBOList = flowForm.processSettingResultAuditVOList
          flowForm.processSettingResultConfirmationBOList = flowForm.processSettingResultConfirmationVOList
          flowForm.processSettingTargetConfirmationBO = flowForm.processSettingTargetConfirmationVO
          delete flowForm.processSettingScoringVOList
          delete flowForm.processSettingResultAuditVOList
          delete flowForm.processSettingResultConfirmationVOList
          delete flowForm.processSettingTargetConfirmationVO

          // 结果设置
          const resultForm = { ...fieldsForm.appraisalPlanResultSettingVO }
          this.fieldsForm.baseForm = baseForm
          this.fieldsForm.appraisalEmployeeResultSettingBO = resultForm
          this.fieldsForm.appraisalPlanQuotaSettingBO = targetForm
          this.fieldsForm.appraisalPlanProcessSettingBO = flowForm
          this.pass.push('base', 'target', 'flow', 'result')
        }
      },
      immediate: true,
      deep: true
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
     * 发布点击
     */
    sendClick() {
      for (var i = 0; i < this.tabs.length; i++) {
        if (!this.pass.includes(this.tabs[i].value)) {
          if (i == this.tabs.length - 1) {
            this.tabClick(this.tabs[i].value, 'submit')
          } else {
            this.tabClick(this.tabs[i + 1].value, 'submit')
            break
          }
        }
      }
      if (this.pass.length == 4) {
        this.tabClick(this.tabIndex, 'submit')
      }
    },

    /**
     * 发送请求
     */
    submiteRequest() {
      this.loading = true
      const fieldsForm = objDeepCopy(this.fieldsForm)
      delete fieldsForm.baseForm
      const params = { ...this.fieldsForm.baseForm, ...fieldsForm }
      params.appraisalPlanProcessSettingBO.processSettingScoringBOList.pop()
      if (Array.isArray(params.appraisalCycle))params.appraisalCycle = params.appraisalCycle.toString()
      const message = params.hasOwnProperty('appraisalPlanId') ? '编辑成功' : '新建成功'

      hrmAppraisalPlanAddAppraisalPlanAPI(params)
        .then(res => {
          this.closeClick()
          this.$emit('createSuccess')
          this.$message.success(message)
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 关闭
     */
    closeClick() {
      this.$emit('close')
    },

    tabClick(value, type) {
      this.tabs.forEach(item => {
        if (item.value == value) {
          this.verify = { tab: this.tabIndex, pass: false, next: value, type }
        }
      })
    },

    tabPass(val, tab, type) {
      if (!this.pass.includes(tab)) this.pass.push(tab)
      if (this.pass.length == 4 && type) {
        this.submiteRequest()
      } else {
        this.tabIndex = val
      }
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

  .wk-approve-flow-wrap {
    top: 60px;
  }
}

</style>
