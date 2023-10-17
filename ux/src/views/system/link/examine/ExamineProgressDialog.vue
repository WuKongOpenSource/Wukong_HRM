<template>
  <el-dialog
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    :show-close="false"
    width="550px">
    <div class="form-add-dialog-body">
      <examine-info-section
        ref="examineInfo"
        :examine-record="detail"
        class="examine-info"
        :external-mailbox="true"
        @success="examineSuccess"
        @on-handle="handleBack" />
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

export default {
  // 审批进度详情
  name: 'ExamineProgressDialog',
  components: {
    ExamineInfoSection
  },
  mixins: [],
  props: {
    detail: {
      type: Object,
      default: () => {}
    },
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
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
  mounted() {
  },
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

    handleBack(data) {
      this.close()
      this.$emit('change')
    },

    examineSuccess() {
      this.$emit('success')
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
