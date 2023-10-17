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
    :title="title"
    width="500px"
    @close="handleCancel">
    <div class="form-add-dialog-body">
      <div v-if="des" class="form-des">{{ des }}</div>
      <el-form
        ref="rejectForm"
        :rules="rules"
        :model="form"
        label-width="80px">
        <el-form-item prop="rejectReason" label="驳回原因">
          <el-input
            v-model="form.rejectReason"
            :autosize="{ minRows: 3}"
            :maxlength="800"
            type="textarea"
            resize="none" />
        </el-form-item>
      </el-form>
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
import {
  hrmPerformanceEmployeeTargetConfirmAPI,
  hrmPerformanceEmployeeResultEvaluatoAPI
} from '@/api/hrm/selfService/performance'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  // 驳回进度
  name: 'RejectDialog',
  components: {
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    handleType: String,
    id: [String, Number],
    des: String,
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      form: {},
      rules: {
        rejectReason: [
          { required: true, message: '请输入', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    title() {
      // 驳回评定
      if (this.handleType == 'evaluato') {
        return '驳回评定'
      }
      return '驳回目标'
    }
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.form = {
            rejectReason: ''
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
      this.form = {
        rejectReason: ''
      }
      if (this.$refs.rejectForm) {
        this.$refs.rejectForm.clearValidate()
      }
      this.$emit('update:visible', false)
    },

    /**
     * 点击确定
     */
    handleConfirm() {
      this.$refs.rejectForm.validate(valid => {
        if (valid) {
          this.loading = true
          let request = hrmPerformanceEmployeeTargetConfirmAPI
          const params = {
            employeeAppraisalId: this.id,
            rejectReason: this.form.rejectReason
          }
          if (this.handleType == 'target' || this.handleType == 'evaluato') {
            request = hrmPerformanceEmployeeResultEvaluatoAPI
            // status: 2, // 状态 1 提交 2 驳回目标 3 驳回评定
            params.status = {
              target: 2,
              evaluato: 3
            }[this.handleType]
          } else {
            params.status = 2 // 2 驳回
          }

          request(params)
            .then(res => {
              this.$message.success('操作成功')
              this.$emit('change')
              this.handleCancel()
              this.loading = false
            })
            .catch(() => {
              this.loading = false
            })
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.form-add-dialog-body {
  max-height: 55vh;
  overflow-x: hidden;
  overflow-y: auto;
}

.form-des {
  margin-bottom: 8px;
}
</style>
