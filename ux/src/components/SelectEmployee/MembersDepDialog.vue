<template>
  <el-dialog
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    title="自定义展示数据"
    width="700px"
    custom-class="no-padding-dialog"
    @close="handleCancel">
    <div class="members-dep-dialog__content">
      <employee-department
        ref="employeeDepartment"
        v-bind="$attrs"
        show-preview
      />
    </div>
    <span
      slot="footer"
      class="dialog-footer">
      <el-button
        v-debounce="handleConfirm"
        type="primary">确定</el-button>
      <el-button @click="handleCancel">取消</el-button>
    </span>
  </el-dialog>

</template>
<script>
import EmployeeDepartment from './main'

export default {
  name: 'MembersDepDialog',
  components: {
    EmployeeDepartment
  },
  inheritAttrs: false,
  props: {
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
    }
  },
  computed: {
    employeeDepartment() {
      return this.$refs.employeeDepartment
    }
  },
  watch: {
  },
  methods: {
    // 提交按钮
    handleConfirm() {
      this.$emit(
        'confirm',
        this.employeeDepartment.userObjArray,
        this.employeeDepartment.depObjArray
      )

      this.handleCancel()
    },

    /**
     * 取消选择
     */
    handleCancel() {
      this.$emit('close',
        this.employeeDepartment.userObjArray,
        this.employeeDepartment.depObjArray
      )
      this.$emit('update:visible', false)
    }

  }
}
</script>

<style scoped lang="scss">
.members-dep-dialog__content {
  margin: 0 15px 10px;
  border: 1px solid $--border-color-base;
  border-radius: $--border-radius-base;
}
</style>

