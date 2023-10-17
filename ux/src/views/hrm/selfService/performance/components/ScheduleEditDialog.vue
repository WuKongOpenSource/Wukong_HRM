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
    title="编辑进度"
    width="500px"
    @close="handleCancel">
    <div class="form-add-dialog-body">
      <el-form
        ref="scheduleForm"
        :model="form"
        label-width="80px">
        <el-form-item label="进度">
          <el-slider v-model="form.schedule" />
        </el-form-item>
        <el-form-item prop="explainDesc" label="完成情况">
          <el-input
            v-model="form.explainDesc"
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
  hrmPerformanceEmployeeUpdateScheduleAPI
} from '@/api/hrm/selfService/performance'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  // 编辑进度
  name: 'ScheduleEditDialog',
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
      form: {}
    }
  },
  computed: {
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.form = {
            schedule: 0
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
      this.$emit('update:visible', false)
    },

    /**
     * 点击确定
     */
    handleConfirm() {
      this.loading = true
      this.form.segId = this.id
      hrmPerformanceEmployeeUpdateScheduleAPI(this.form)
        .then(res => {
          this.$message.success('操作成功')
          this.$emit('change', this.form.schedule)
          this.handleCancel()
          this.loading = false
        })
        .catch(() => {
          this.loading = false
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

  .el-slider {
    padding: 0 10px;
  }
}
</style>
