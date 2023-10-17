<template>
  <div class="wk-approval-flow-config-apply">
    <el-button
      type="primary-text"
      @click="checkClick">{{ examineBtnName }}</el-button>

    <xr-create
      v-show="createShow"
      ref="configView"
      :loading="loading"
      title="查看审批配置"
      @close="createClose"
      @save="saveClick">
      <wk-approve-flow
        ref="wkApproveFlow"
        :props="approveFlowConfig"
        :list="flowList || []"
        :send-node="sendNode" />
    </xr-create>
  </div>
</template>

<script>
import {
  examinesQueryExamineFlowAPI
} from '@/api/examine'

import { WkApproveFlow } from '@/components/ApprovalFlow'
import XrCreate from '@/components/XrCreate'

import ExamineInfoMinxin from '@/views/admin/examine/mixins/ExamineInfo'
import { getMaxIndex } from '@/utils/index'

export default {
  // 审批流的应用参数
  name: 'WkAfNewApply',

  components: {
    WkApproveFlow,
    XrCreate
  },

  mixins: [ExamineInfoMinxin],

  props: {
    // data: Array, 根据详情id获取
    detail: { // 审批高级配置
      type: Object,
      default: () => { return {} }
    }
  },

  data() {
    return {
      flowList: null, // 数据源 如果是null 说明没有获取过流程数据，这时 userFieldNames 也返回null
      sendNode: {
        name: '发起人',
        content: '具有新建权限的员工'
      },
      loading: false,
      createShow: false
    }
  },

  computed: {
    // 审批添加
    examineAddShow() {
      return !this.detail?.modifyPermissionType ||
      !this.detail.modifyPermissionType.includes(1)
    },

    // 抄送添加
    copyAddShow() {
      return !this.detail?.modifyPermissionType ||
      !this.detail.modifyPermissionType.includes(2)
    },

    // 审批查看配置按钮
    examineBtnName() {
      // 1 修改固定审批人和审批节点 2 修改固定抄送人和抄送节点
      if (this.examineAddShow || this.copyAddShow) {
        return '查看并配置审批流>>'
      }
      return '查看审批流>>'
    },

    approveFlowConfig() {
      return {
        conditionAddShow: false,
        examineAddShow: this.examineAddShow,
        copyAddShow: this.copyAddShow
      }
    }
  },

  watch: {
    detail: {
      handler(val) {
        if (val && val.hasOwnProperty('label')) {
          // 获取可配置时需要的员工字段数据
          this.getUserFieldList(this.detail.label, this.detail.examineId, this.detail)
        }
      },
      immediate: true
    }
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * @description: 查看点击
     * @return {*}
     */
    checkClick() {
      // 重置层级到最上层
      this.$refs.configView.$refs.createView.zIndex = getMaxIndex()
      this.createShow = true

      if (!this.flowList || this.flowList.length === 0) {
        this.getFlowList(this.detail.examineId)
      }
    },

    /**
      * @description: 获取流程详情
      * @param {*} examineId
      * @return {*}
      */
    getFlowList(examineId) {
      this.loading = true
      examinesQueryExamineFlowAPI({ examineId }).then(res => {
        const list = res.data || []
        const dataList = []
        this.getListInfo(list, dataList, {
          additionalInfoFun: (dataItem, orgData, orgIndex) => {
            if (orgData.conditionDataList) { // 条件
              dataItem.disabled = true // 后台返回数据禁止编辑
            } else if (dataItem.examineType === 7) { // 抄送
              dataItem.disabled = !this.copyAddShow // 后台返回数据禁止编辑
            } else { // 审批
              dataItem.disabled = !this.examineAddShow // 后台返回数据禁止编辑
            }
          }
        })
        this.flowList = dataList || []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * @description: 创建关闭
     * @return {*}
     */
    createClose() {
      this.createShow = false
    },

    /**
     * @description: 编辑后的保存
     * @return {*}
     */
    saveClick() {
      const flowParams = this.$refs.wkApproveFlow.getParams()
      if (flowParams.isError) {
        this.$message.error('请完善信息')
      } else {
        // flowParams.list
        this.createClose()
      }
    },

    /**
     * @description: 获取参数
     * @return {*}
     */
    getParams() {
      const flowParams = this.$refs.wkApproveFlow.getParams({
        needUserFieldNamesParams: true
      })

      // 没有请求过审批流，userFieldNames改为null
      if (!this.flowList && flowParams.paramsData?.userFieldNames) {
        flowParams.paramsData.userFieldNames = null
      }

      if (flowParams.isError) {
        this.$message.error('请完善信息')
        return {
          pass: true,
          flowParams
        }
      } else {
        return {
          pass: true,
          flowParams
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
::v-deep .wk-approve-flow-wrap {
  .el-button-group {
    position: absolute;
    top: 40px;
  }
}
</style>
