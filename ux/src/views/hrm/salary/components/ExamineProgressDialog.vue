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
    width="550px">
    <div class="form-add-dialog-body">
      <examine-info-section
        :id="id"
        ref="examineInfo"
        :record-id="recordId"
        :ignore-action="ignoreAction"
        examine-type="hrm_salary"
        class="examine-info"
        @info="handleExamineInfo"
        @on-handle="handleBack"
        @on-loading="loading = $event" />
    </div>
    <span
      slot="footer"
      class="dialog-footer">
      <el-button @click.native="handleCancel">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import ExamineInfoSection from '@/components/Examine/ExamineInfoSection'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  // 审批进度详情
  name: 'ExamineProgressDialog',
  components: {
    ExamineInfoSection
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    detail: Object,
    ignoreAction: {
      type: Array,
      default: () => {
        return []
      }
    },
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      title: '详情'
    }
  },
  computed: {
    id() {
      return this.detail ? this.detail.srecordId : ''
    },

    recordId() {
      return this.detail ? this.detail.examineRecordId : ''
    },

    createUserId() {
      return this.detail ? this.detail.createUserId : ''
    }
  },
  watch: {
    visible: {
      handler(val) {
        // 触发内部更新
        if (val) {
          if (this.recordId) {
            if (this.$refs.examineInfo) {
              this.$refs.examineInfo.refreshData()
            }
          }
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

    handleExamineInfo(data) {
    },

    handleBack(data) {
      this.close()
      this.$emit('change')
    }
  }
}
</script>
<style lang="scss" scoped>
.form-add-dialog-body {
  overflow-x: hidden;
  overflow-y: hidden;

  .approval-flow {
    padding-top: 0;
  }

  .examine-info {
    ::v-deep .section-body {
      max-height: 42vh;
      overflow-y: auto;

      .handle {
        justify-content: flex-end;

        .el-button {
          margin-left: 10px;
        }
      }
    }
  }
}
</style>
