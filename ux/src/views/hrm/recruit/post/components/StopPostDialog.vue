<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-06-05 17:45:27
 * @LastEditTime: 2020-06-05 18:08:19
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
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="原因" prop="reason">
          <el-input v-model="form.reason" />
        </el-form-item>
      </el-form>
      <slot />
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
  hrmRecruitPostUpdateStatusAPI
} from '@/api/hrm/recruit/post'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {

  // 停止招聘
  name: 'StopPostDialog',
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
      form: {},
      title: '停止招聘',
      rules: {
        reason: [
          { required: true, message: '请输入', trigger: ['change', 'blur'] }
        ]
      }
    }
  },
  computed: {
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.form = {
            reason: ''
          }

          this.$nextTick(() => {
            if (this.$refs.form) {
              this.$refs.form.clearValidate()
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
      this.$emit('update:visible', false)
    },

    /**
     * 点击确定
     */
    handleConfirm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.loading = true
          hrmRecruitPostUpdateStatusAPI({
            status: 0,
            postId: this.id,
            reason: this.form.reason
          })
            .then(res => {
              this.$message.success('操作成功')
              this.loading = false
              this.$emit('success')
              this.handleCancel()
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
