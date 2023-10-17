<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <el-dialog
    ref="wkDialog"
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    :show-close="false"
    width="500px">
    <span slot="title" class="el-dialog__title">{{ title }}<el-tooltip
      v-if="remarks"
      :content="remarks"
      effect="dark"
      placement="bottom">
      <i
        class="wk wk-icon-fill-help wk-help-tips"
        data-type="33"
        data-id="292" />
    </el-tooltip></span>
    <div class="form-add-dialog-body">
      <!-- <wk-approval-flow-apply
        v-if="list"
        :data="list"
        :is-edit="isEdit"
        :approver-edit-auth="approverEditAuth"
        :carbon-copy-edit-auth="carbonCopyEditAuth"
        :examine-advanced-setting="examineAdvancedSetting"
        style="padding-top: 2px;padding-left: 5px;"
      /> -->
      <wk-af-new-apply
        ref="afNewApply"
        :data="list"
        :approver-edit-auth="approverEditAuth"
        :detail="examineAdvancedSetting" />
    </div>
    <span
      slot="footer"
      class="dialog-footer">
      <el-button
        v-debounce="handleConfirm"
        type="primary">保存</el-button>
      <el-button @click.native="handleCancel">取消</el-button>
    </span>
  </el-dialog>
</template>

<script>
// import WkApprovalFlowApply from '@/components/Examine/WkApprovalFlowApply'
import WkAfNewApply from '@/components/Examine/WkAfNewApply'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  // 发起审批
  name: 'ExamineSubmitDialog',
  components: {
    // WkApprovalFlowApply,
    WkAfNewApply
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    visible: {
      type: Boolean,
      required: true,
      default: false
    },
    data: Object,
    examineAdvancedSetting: {
      type: Object,
      default: () => { return {} }
    },
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      list: null,
      title: '提交审核',

      approverEditAuth: false,
      carbonCopyEditAuth: false
    }
  },
  computed: {
    remarks() {
      return this.data ? this.data.remarks : ''
    }
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          const modifyPermissionType = this.examineAdvancedSetting?.modifyPermissionType
          if (!modifyPermissionType) {
            this.approverEditAuth = true
            this.carbonCopyEditAuth = true
          } else {
            this.approverEditAuth = !modifyPermissionType.includes(1)
            this.carbonCopyEditAuth = !modifyPermissionType.includes(2)
          }
          this.list = this.data ? this.data.examineFlowList : null // 参数已无效
        }
      },
      immediate: true
    }
  },
  mounted() {},
  methods: {

    /**
     * 取消选择
     */
    handleCancel() {
      this.close()
    },

    close() {
      this.$emit('update:visible', false)
    },

    /**
     * 点击确定
     */
    handleConfirm() {
      // 如果 afNewApply 说明没有开始审批流
      const wkFlowResult = this.$refs.afNewApply ? this.$refs.afNewApply.getParams() : {
        pass: true,
        flowParams: { list: [] }
      }

      this.$emit('save', wkFlowResult)
    }
  }
}
</script>
<style lang="scss" scoped>
.form-add-dialog-body {
  min-height: 20px;
  max-height: 55vh;
  overflow-x: hidden;
  overflow-y: auto;
}

.wk-help-tips {
  margin-left: 4px;
}
</style>
