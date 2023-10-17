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
    @close="close">
    <div class="form-add-dialog-body">
      <el-form ref="memoForm" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="备忘事件" prop="content">
          <el-input
            v-model="form.content"
            :maxlength="100"
            type="textarea" />
        </el-form-item>
        <el-form-item label="提醒日期" prop="reminderTime">
          <el-date-picker
            v-model="form.reminderTime"
            clearable
            style="width: 100%;"
            type="date"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            placeholder="选择日期" />
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
  hrmDashboardAddNotesAPI
} from '@/api/hrm/workbench'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  // 添加备忘
  name: 'MemoAddDialog',
  components: {
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      title: '添加备忘事件',
      rules: {
        reminderTime: [
          { required: true, message: '请输入', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请选择', trigger: 'blur' }
        ]
      },
      form: {}
    }
  },
  computed: {
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.form = { content: '', reminderTime: '' }
          this.$nextTick(() => {
            if (this.$refs.memoForm) {
              this.$refs.memoForm.clearValidate()
            }
          })
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
      this.$emit('change')
      this.$refs.memoForm.validate(valid => {
        if (valid) {
          this.loading = true
          hrmDashboardAddNotesAPI(this.form)
            .then(res => {
              this.$message.success('操作成功')
              this.$emit('change')
              this.close()
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
