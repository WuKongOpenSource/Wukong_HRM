<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <el-dialog
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    title="编辑权重"
    width="400px"
    @close="handleCancel">
    <div class="form-add-dialog-body">
      <el-form ref="weightForm" :model="form" label-width="80px" @submit.native.prevent>
        <el-form-item label="权重">
          <el-input-number
            v-model="form.weight"
            :controls="false"
            :min="0"
            :max="100"
            style="margin-right: 8px;" />%
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

export default {
  // 编辑权限
  name: 'WeightEditDialog',
  components: {
  },
  mixins: [],
  props: {
    surplusWeight: [String, Number],
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
      form: {}
    }
  },
  computed: {
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.form = { weight: undefined }
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
      if (this.form.weight === undefined || this.form.weight === '' || this.form.weight === null || this.form.weight < 0 || this.form.weight > 100) {
        this.$message.error('范围在0-100之间')
      } else if (this.surplusWeight < this.form.weight) {
        this.$message.error('考核项权重累加，需等于100%')
      } else {
        this.$refs.weightForm.validate(valid => {
          if (valid) {
            this.$emit('change', this.form.weight)
            this.handleCancel()
          }
        })
      }
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
