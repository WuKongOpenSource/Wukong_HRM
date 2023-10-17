<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: Please set LastEditors
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
    <el-form ref="groupForm" :model="form" :rules="rules" class="form-add-dialog-body" label-position="top" label-width="80px">
      <create-sections title="基本信息">
        <flexbox wrap="wrap" style="padding: 0 15px;">
          <el-form-item
            prop="groupName"
            label="薪资组名称">
            <el-input
              v-model="form.groupName"
              :maxlength="100" />
          </el-form-item>
          <el-form-item
            prop="employeeIds">
            <template slot="label">
              适用范围<el-tooltip
                content="选择部门，即所选部门整体人员均适用本薪资组，新增员工自动关联；选择员工，即按个人为单位"
                effect="dark"
                placement="top">
                <i class="wk wk-help wk-help-tips" />
              </el-tooltip>
            </template>
            <wk-user-dep-select
              :user-value.sync="form.employeeIds"
              :dep-value.sync="form.deptIds"
              :props="{
                dataType: 'hrm'
              }"
              style="width: 100%;"
            />
          </el-form-item>
        </flexbox>
      </create-sections>
      <create-sections title="设置计薪规则">
        <flexbox wrap="wrap" style="padding: 0 15px;">
          <el-form-item>
            <template slot="label">
              月计薪标准<el-tooltip
                content="月应出勤天数：员工所在考勤组的出勤日总和"
                effect="dark"
                placement="top">
                <i class="wk wk-help wk-help-tips" />
              </el-tooltip>
            </template>
            <div>21.75</div>
          </el-form-item>
          <el-form-item
            label="转正、调薪月规则">
            <div>按转正/调薪前后的工资混合计算</div>
          </el-form-item>
          <el-form-item
            prop="ruleId"
            label="计税规则">
            <el-select
              v-model="form.ruleId"
              style="width: 100%;">
              <el-option
                v-for="(item, index) in taxList"
                :key="index"
                :label="item.ruleName"
                :value="item.ruleId " />
            </el-select>
          </el-form-item>
        </flexbox>
      </create-sections>
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
  hrmSalaryGroupAddAPI,
  hrmSalaryGroupUpdateAPI,
  hrmSalaryTaxRuleListAPI
} from '@/api/admin/hrm'

import CreateSections from '@/components/CreateSections'
import WkUserDepSelect from '@/components/NewCom/WkUserDepSelect'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  // 薪资组操作
  name: 'GroupEditDialog',
  components: {
    CreateSections,
    WkUserDepSelect
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    detail: Object,
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    const validateEmployeeIds = (rule, value, callback) => {
      if ((this.form.employeeIds && this.form.employeeIds.length > 0) ||
        (this.form.deptIds && this.form.deptIds.length > 0)) {
        callback()
      } else {
        callback(new Error('请选择'))
      }
    }
    return {
      loading: false,
      rules: {
        groupName: [
          { required: true, message: '请输入', trigger: 'blur' }
        ],
        ruleId: [
          { required: true, message: '请选择', trigger: 'change' }
        ],
        employeeIds: [
          { validator: validateEmployeeIds, trigger: '' }
        ]
      },
      form: {},
      taxList: [],
      defaultProps: { isHrm: true }
    }
  },
  computed: {
    title() {
      return this.detail ? '编辑薪资组' : '新建薪资组'
    }
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.form = { groupName: '' }
          if (this.detail) {
            const data = this.detail
            this.form = {
              groupName: data.groupName,
              employeeIds: data.employeeList ? data.employeeList.map(item => item.employeeId) : [],
              deptIds: data.deptList ? data.deptList.map(item => item.deptId) : [],
              ruleId: data.ruleId
            }
          }

          this.$nextTick(() => {
            if (this.$refs.groupForm) {
              this.$refs.groupForm.clearValidate()
            }
          })
          this.getTaxList()
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
    handleCancel(goBack) {
      this.$emit('update:visible', false)
    },

    /**
     * 获取计税规则列表数据
     */
    getTaxList() {
      hrmSalaryTaxRuleListAPI()
        .then(res => {
          this.taxList = res.data
        })
        .catch(() => {
        })
    },

    /**
     * 点击确定
     */
    handleConfirm() {
      this.$refs.groupForm.validate(valid => {
        if (valid) {
          this.loading = true
          const request = this.detail ? hrmSalaryGroupUpdateAPI : hrmSalaryGroupAddAPI
          if (this.detail) {
            this.form.groupId = this.detail.groupId
          }
          request(this.form)
            .then(res => {
              this.$message.success('操作成功')
              this.$emit('change')
              this.handleCancel(false)
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

  .el-form-item {
    flex: 0 0 47.5%;

    ::v-deep .el-form-item__label {
      padding-bottom: 0;
      line-height: 30px;
    }

    ::v-deep .el-form-item__content {
      line-height: 34px;
    }
  }

  .el-form-item:nth-child(even) {
    margin-left: 30px;
  }

  .wk-help-tips {
    margin-left: 3px;
  }
}
</style>
