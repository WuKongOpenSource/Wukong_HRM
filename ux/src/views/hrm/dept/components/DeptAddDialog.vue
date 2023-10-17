<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-26 15:23:33
 * @LastEditTime: 2020-06-29 19:12:07
 * @LastEditors: yang
-->
<template>
  <el-dialog
    ref="wkDialog"
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    :title="title"
    width="700px"
    @close="handleCancel">
    <el-form
      ref="deptAddForm"
      :model="deptAddForm"
      :rules="rules"
      :validate-on-rule-change="false"
      label-position="top">
      <el-form-item
        v-for="(item, index) in addFields"
        :key="index"
        :prop="item.field">
        <span slot="label">
          {{ item.name }}
        </span>
        <el-input
          v-if="item.formType == 'text'"
          v-model="deptAddForm[item.field]"
          :maxlength="item.maxlength || 100"
          placeholder="请输入内容" />
        <el-radio-group
          v-else-if="item.formType == 'radio'"
          v-model="deptAddForm[item.field]">
          <el-radio
            v-for="set in item.setting"
            :key="set.value"
            :label="set.value">{{ set.label }}</el-radio>
        </el-radio-group>
        <wk-dep-select
          v-else-if="item.formType == 'structure'"
          v-model="deptAddForm[item.field]"
          :props="{dataType: 'hrm'}"
          :params="structureParams"
          style="width: 100%;"
          radio
        />
        <wk-user-select
          v-else-if="item.formType == 'user'"
          v-model="deptAddForm[item.field]"
          :request="item.request"
          :props="{label: 'employeeName', value: 'employeeId'}"
          style="width: 100%;"
          radio
        />
      </el-form-item>
    </el-form>
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
  hrmEmployeeQueryByDeptAPI
} from '@/api/hrm/employee'
import {
  hrmDeptAddDeptAPI,
  hrmDeptSetDeptAPI
} from '@/api/hrm/dept'

import WkUserSelect from '@/components/NewCom/WkUserSelect'
import WkDepSelect from '@/components/NewCom/WkDepSelect'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {

  // 组织操作
  name: 'DeptAddDialog',
  components: {
    WkUserSelect,
    WkDepSelect
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    data: Object,
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      deptAddForm: {
        deptType: 1
      }
    }
  },
  computed: {
    title() {
      return this.data ? '编辑组织部门' : '新建组织部门'
    },
    addFields() {
      const tempList = [{
        name: '组织部门名称',
        field: 'name',
        formType: 'text',
        maxlength: 60,
        setting: []
      }, {
        name: '组织编码',
        field: 'code',
        maxlength: 60,
        formType: 'text',
        setting: []
      }, {
        name: '组织类型',
        field: 'deptType',
        formType: 'radio',
        setting: [{
          label: '公司',
          value: 1
        }, {
          label: '部门',
          value: 2
        }]
      }]

      if (!this.data || this.data.parentId !== 0) {
        tempList.push({
          name: '上级组织',
          field: 'parentId',
          formType: 'structure',
          setting: []
        })
      }

      return tempList.concat([{
        name: '组织负责人',
        field: 'mainEmployeeId',
        formType: 'user',
        request: hrmEmployeeQueryByDeptAPI,
        setting: []
      }, {
        name: '分管领导',
        field: 'leaderEmployeeId',
        formType: 'user',
        request: hrmEmployeeQueryByDeptAPI,
        setting: []
      }])
    },
    rules() {
      const tempRules = {
        name: { required: true, message: '请输入', trigger: ['blur', 'change'] },
        code: { required: true, message: '请输入', trigger: ['blur', 'change'] },
        deptType: { required: true, message: '请选择', trigger: ['blur', 'change'] }
        // mainEmployeeId: { required: true, message: '请选择', trigger: ['blur', 'change'] },
        // leaderEmployeeId: { required: true, message: '请选择', trigger: ['blur', 'change'] }
      }

      if (!this.data || this.data.parentId !== 0) {
        tempRules['parentId'] = { required: true, message: '请选择', trigger: ['blur', 'change'] }
      }

      return tempRules
    },
    structureParams() {
      return this.data ? { type: 'update', id: this.data.deptId } : { type: 'tree' }
    }
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          if (this.data) {
            this.deptAddForm = {
              deptType: this.data.deptType,
              code: this.data.code,
              name: this.data.name,
              mainEmployeeId: this.data.mainEmployeeId,
              leaderEmployeeId: this.data.leaderEmployeeId,
              parentId: this.data.parentId
            }
          } else {
            // 重置状态
            this.deptAddForm = {
              deptType: 1
            }
          }

          this.$nextTick(() => {
            if (this.$refs.deptAddForm) {
              this.$refs.deptAddForm.clearValidate()
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
      this.$refs.deptAddForm.validate(valid => {
        if (valid) {
          const params = {}
          params.name = this.deptAddForm.name
          params.code = this.deptAddForm.code
          params.deptType = this.deptAddForm.deptType
          params.parentId = this.deptAddForm.parentId
          params.mainEmployeeId = this.deptAddForm.mainEmployeeId
          params.leaderEmployeeId = this.deptAddForm.leaderEmployeeId
          this.loading = true

          if (this.data) {
            params.deptId = this.data.deptId
          }
          const request = this.data ? hrmDeptSetDeptAPI : hrmDeptAddDeptAPI
          request(params).then(res => {
            this.loading = false
            this.$message.success(`${this.title}成功`)
            this.$store.dispatch('GetHrmDeptList')
            this.$emit('success')
            this.handleCancel()
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.el-form {
  display: flex;
  flex-wrap: wrap;
  margin: 0 -23px;

  ::v-deep .el-form-item.is-required .el-form-item__label::before {
    margin-right: 0;
  }
}

.el-form-item {
  flex: 0 0 50%;
  flex-shrink: 0;
  padding: 0 40px;
  padding-bottom: 10px;
  margin-bottom: 4px;

  ::v-deep .el-form-item__label {
    padding: 5px 0;
    line-height: inherit;
    color: $--color-text-primary;
  }

  ::v-deep .el-form-item__error {
    padding-top: 2px;
  }
}
</style>
