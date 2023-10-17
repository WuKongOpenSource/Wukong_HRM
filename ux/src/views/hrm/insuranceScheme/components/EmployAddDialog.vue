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
    width="500px">
    <span slot="title" class="el-dialog__title">
      {{ title }}
      <i
        class="wk wk-icon-fill-help wk-help-tips"
        data-type="32"
        data-id="290" />
    </span>
    <div class="form-add-dialog-body">
      <el-form ref="addEmployForm" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="参保人员" prop="employeeIds">
          <wk-user-select
            v-if="visible && id"
            v-model="form.employeeIds"
            :props="userProps"
            :params="{
              irecordId: id
            }"
            style="width: 100%;"
          />
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
  hrmInsuranceMonthEmpRecordQueryNoInsuranceEmpAPI,
  hrmInsuranceMonthEmpRecordAddInsuranceEmpAPI
} from '@/api/hrm/insuranceScheme'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import WkUserSelect from '@/components/NewCom/WkUserSelect'

export default {
  // 添加员工
  name: 'EmployAddDialog',
  components: {
    WkUserSelect
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
      title: '添加参保人员',
      rules: {
        employeeIds: [
          { required: true, message: '请选择', trigger: 'blur' }
        ]
      },
      userProps: { label: 'employeeName', value: 'employeeId', request: hrmInsuranceMonthEmpRecordQueryNoInsuranceEmpAPI, isList: true },
      form: {}
    }
  },
  computed: {
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.form = { employeeIds: [] }
          this.$nextTick(() => {
            if (this.$refs.addEmployForm) {
              this.$refs.addEmployForm.clearValidate()
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
      this.$refs.addEmployForm.validate(valid => {
        if (valid) {
          this.loading = true

          hrmInsuranceMonthEmpRecordAddInsuranceEmpAPI({
            ...this.form,
            irecordId: this.id
          })
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
