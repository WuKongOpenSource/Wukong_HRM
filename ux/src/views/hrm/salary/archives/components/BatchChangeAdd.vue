<template>
  <xr-create
    :loading="loading"
    :title="title"
    @close="close"
    @save="saveClick">
    <template slot="title-append">
      <i
        class="wk wk-icon-fill-help wk-help-tips"
        data-type="33"
        data-id="294" />
    </template>
    <div class="content">
      <el-form
        ref="changeAddForm"
        :model="ruleForm"
        :rules="rules"
        label-width="100px"
        label-position="top">
        <el-form-item prop="range">
          <template slot="label">调薪员工<span style="color: #6b778c;">（所选对象调薪细则、原因、生效日期将相同）</span>
          </template>
          <wk-user-dep-select
            :user-value.sync="ruleForm.range.employeeIds"
            :dep-value.sync="ruleForm.range.deptIds"
            :props="{dataType: 'hrm'}"
          />
        </el-form-item>
        <el-form-item prop="value">
          <template slot="label">
            调薪细则<el-tooltip
              effect="dark"
              placement="top">
              <div slot="content">1、工资表中只有基本工资类下的薪资项可以批量调薪，如需批量调整其他薪资项，请在后台工资表设置-基本工资类下添加自定义项<br>
                2、试用员工只对试用期工资进行调薪<br>
                3、调薪计算示例：<br>
                <span style="margin-left: 18px;">按比例调薪：基本工资=2000，调薪比例=10%，调薪后=2000+2000X10%=2200</span><br>
                <span style="margin-left: 18px;">按金额调薪：基本工资=2000，调薪金额=100，调薪后=2000+100=2100</span></div>
              <i class="wk wk-help wk-help-tips" style="margin-left: 3px;" />
            </el-tooltip>
          </template>
          <el-radio-group v-model="ruleForm.type">
            <el-radio :label="1">按比例调薪</el-radio>
            <el-radio :label="2">按金额调薪</el-radio>
          </el-radio-group>
          <el-table
            :data="ruleForm.value"
            height="300"
            style="width: 100%;">
            <el-table-column
              prop="name"
              label="调薪项" />
            <el-table-column
              :label="ruleForm.type === 1 ? '调薪比例' : '调薪金额'"
              prop="value">
              <template slot-scope="scope">
                <el-input-number
                  v-if="ruleForm.type === 1"
                  v-model="scope.row.percent"
                  :precision="2"
                  :max="9999.99"
                  :controls="false" />
                <el-input-number
                  v-else
                  v-model="scope.row.value"
                  :precision="2"
                  :max="9999999.99"
                  :controls="false" />
                {{ ruleForm.type === 1 ? '%' : '元' }}
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <flexbox
          align="stretch">
          <el-form-item label="调薪原因" style="flex: 1;" prop="changeReason">
            <el-select
              v-model="ruleForm.changeReason"
              style="width: 100%;">
              <el-option
                v-for="item in changeReasonList"
                :key="item.value"
                :label="item.label"
                :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="调薪生效日期" style="flex: 1;margin-left: 80px;" prop="enableDate">
            <el-date-picker
              v-model="ruleForm.enableDate"
              :picker-options="pickerOptions"
              style="width: 100%;"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="选择日期" />
          </el-form-item>
        </flexbox>
        <el-form-item label="备注" prop="remarks">
          <el-input
            v-model="ruleForm.remarks"
            :rows="3"
            :autosize="{ minRows: 3}"
            :maxlength="800"
            type="textarea"
            resize="none" />
        </el-form-item>
      </el-form>
    </div>
  </xr-create>
</template>

<script>
import {
  hrmSalaryArchivesBatchSetChangeAPI,
  hrmSalaryArchivesQueryBatchChangeOptionAPI
} from '@/api/hrm/salary'
import {
  hrmSalaryConfigQueryInItConfigAPI
} from '@/api/hrm/salaryConfig'

import XrCreate from '@/components/XrCreate'
import WkUserDepSelect from '@/components/NewCom/WkUserDepSelect'

import archivesModel from '../model/archives'

export default {
  // 批量调薪
  name: 'BatchChangeSalaryAdd',

  components: {
    XrCreate,
    WkUserDepSelect
  },

  mixins: [],

  props: {
    userList: Array
  },

  data() {
    var validateRange = (rule, value, callback) => {
      if (value.employeeIds.length === 0 && value.deptIds.length === 0) {
        callback(new Error('调薪员工不能为空'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      initDataConfig: null,
      pickerOptions: {
        disabledDate: (time) => {
          if (this.initDataConfig && this.initDataConfig.lastSalaryYear) {
            if (time.getFullYear() == this.initDataConfig.lastSalaryYear) {
              return time.getMonth() + 1 < this.initDataConfig.lastSalaryMonth
            }
            return time.getFullYear() < this.initDataConfig.lastSalaryYear
          }
          return false
        }
      },
      changeReasonList: [],
      ruleForm: {
        range: {
          employeeIds: [],
          deptIds: []
        },
        remarks: '',
        value: [],
        type: 1,
        changeReason: ''
      },
      rules: {
        range: [
          { required: true, message: '调薪员工不能为空', trigger: 'blur' },
          { validator: validateRange, trigger: 'change' }
        ],
        value: [
          { required: true, message: '调薪细则不能为空', trigger: 'blur' }
        ],
        changeReason: [
          { required: true, message: '调薪原因不能为空', trigger: 'blur' }
        ],
        enableDate: [
          { required: true, message: '调薪生效日期不能为空', trigger: 'blur' }
        ]
      }
    }
  },

  computed: {
    title() {
      return '批量调薪'
    }
  },

  watch: {},

  created() {
    this.changeReasonList = archivesModel.getValueList(archivesModel.changeReasonValue)
    this.ruleForm.changeReason = this.changeReasonList[0].value
    if (this.userList) {
      this.ruleForm.range.employeeIds = this.userList.map(item => item.employeeId)
    }
    this.getConfigList()
    this.getChangeOptionValue()
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 获取配置信息
     */
    getConfigList() {
      this.loading = true
      hrmSalaryConfigQueryInItConfigAPI()
        .then(res => {
          this.initDataConfig = res.data.otherInitConfig
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 查询调薪项的值
     */
    getChangeOptionValue() {
      this.loading = true
      hrmSalaryArchivesQueryBatchChangeOptionAPI()
        .then(res => {
          this.ruleForm.value = this.getShowOption(res.data || [])
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 获取调薪项
     */
    getShowOption(list) {
      const value = []
      list.forEach(item => {
        value.push({
          code: item.code,
          name: item.name,
          value: 0.00,
          percent: 0.00
        })
      })
      return value
    },

    /**
     * 保存
     */
    saveClick() {
      this.$refs.changeAddForm.validate((valid) => {
        if (valid) {
          this.loading = true

          const options = this.ruleForm.value || []
          const salaryOptions = []
          options.forEach(item => {
            salaryOptions.push({
              code: item.code,
              name: item.name,
              value: this.ruleForm.type === 1 ? item.percent : item.value
            })
          })

          hrmSalaryArchivesBatchSetChangeAPI({
            deptIds: this.ruleForm.range.deptIds,
            employeeIds: this.ruleForm.range.employeeIds,
            remarks: this.ruleForm.remarks,
            changeReason: this.ruleForm.changeReason,
            enableDate: this.ruleForm.enableDate,
            type: this.ruleForm.type,
            salaryOptions
          })
            .then(res => {
              this.loading = false
              const resData = res.data
              const hasError = resData.errorNum > 0
              let message = `<span style="color:#2362FB">${resData.successNum}</span>个员工调薪成功`
              if (hasError) {
                message = message + `<br /><span style="color:red">${resData.errorNum}</span>个员工存在未生效的调薪或未定薪，无法调薪`
              }
              this.$confirm(message, '提示', {
                confirmButtonText: '关闭',
                dangerouslyUseHTMLString: true,
                showCancelButton: false,
                type: hasError ? 'warning' : 'success'
              }).then(() => {
                this.$emit('change')

                this.$emit('close')
              }).catch(() => {
              })
            })
            .catch(() => {
              this.loading = false
            })
        } else {
          return false
        }
      })
    },

    /**
     * 关闭
     */
    close() {
      this.$confirm('确定离开？正在编辑的内容将会丢失', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.$emit('close')
        })
        .catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.content {
  padding: 0 15px;

  &__header {
    .mark {
      width: 4px;
      height: 14px;
      background-color: $--color-primary;
      border-radius: 2px;
    }

    .title {
      margin: 0 8px;
      font-size: 16px;
      font-weight: bold;
    }

    .des {
      position: relative;
      padding: 0 10px;
      color: $--color-text-regular;

      &::before {
        position: absolute;
        top: 2px;
        left: 0;
        width: 1px;
        height: 12px;
        content: " ";
        background-color: $--color-text-regular;
      }
    }
  }
}

.el-form {
  margin-top: 10px;

  ::v-deep .el-form-item__label {
    padding-bottom: 0;
    line-height: 30px;
  }
}

.el-table {
  line-height: initial;
}

.item-width,
.wk-user-dep-select,
.el-textarea {
  width: calc(50% - 40px);
}
</style>
