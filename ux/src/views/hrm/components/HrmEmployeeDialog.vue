<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: majiaxing ziyuechuwu@163.com
-->
<template>
  <el-dialog
    ref="wkDialog"
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    :title="title"
    :show-close="false"
    width="700px">
    <div class="form-add-dialog-body">
      <wk-dep-user-view
        :props="{
          value: 'employeeId',
          label: 'employeeName',
          canSelectDep: false,
          isHrm: true
        }"
        @change="selectChange"
      />
    </div>
    <span
      slot="footer"
      class="dialog-footer">
      <el-button
        v-debounce="handleConfirm"
        type="primary">确定</el-button>
      <el-button @click.native="close">取消</el-button>
    </span>
  </el-dialog>
</template>

<script>

import WkDepUserView from '@/components/NewCom/WkUserSelect/Dep'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  // 人资员工
  name: 'HrmEmployeeDialog',
  components: {
    WkDepUserView
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
      title: '选择员工',
      users: [],
      deps: []
    }
  },
  computed: {
  },
  watch: {
    visible: {
      handler(val) {
      },
      immediate: true
    }
  },
  mounted() {},
  methods: {
    close() {
      this.$emit('update:visible', false)
    },

    /**
     * 第一步
     */
    selectChange(users, deps) {
      this.users = users || []
      this.deps = deps || []
    },

    /**
     * 点击确定
     */
    handleConfirm() {
      this.$emit('change', this.users, this.deps)
    }
  }
}
</script>
<style lang="scss" scoped>
@import "@/styles/wk-form.scss";

.form-add-dialog-body {
  max-height: 55vh;
  overflow-x: hidden;
  overflow-y: auto;
}
</style>
