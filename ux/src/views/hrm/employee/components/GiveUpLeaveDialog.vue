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
      <el-form ref="giveupForm" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="原因">
          <el-input
            v-model="form.remarks"
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
  hrmEmployeePostDeleteLeaveAPI
} from '@/api/hrm/employeePost'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  // 放弃离职
  name: 'GiveUpLeaveDialog',
  components: {
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    id: [String, Number],
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      title: '放弃离职',
      rules: {},
      form: {}
    }
  },
  computed: {
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.form = { remarks: '' }
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
      this.$emit('update:visible', false)
    },

    /**
     * 点击确定
     */
    handleConfirm() {
      this.$refs.giveupForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.form.employeeId = this.id
          hrmEmployeePostDeleteLeaveAPI(this.form)
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
</style>
