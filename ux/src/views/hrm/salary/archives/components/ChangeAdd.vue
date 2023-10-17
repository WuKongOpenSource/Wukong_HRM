<template>
  <xr-create
    :loading="loading"
    :title="title"
    :show-cancel="!showDetail"
    :show-confirm="!showDetail"
    @close="closeClick"
    @save="saveClick">
    <div slot="title" class="create-title">
      <span>{{ title }}</span>
      <i
        v-if="helpObj"
        class="wk wk-icon-fill-help wk-help-tips"
        :data-type="helpObj.dataType"
        :data-id="helpObj.dataId" />
      <span :class="statusClass[detail.status]" class="mark">{{ statusObj[detail.status] }}</span>
    </div>

    <div class="content">
      <flexbox class="content__header">
        <div class="mark" />
        <span class="title">{{ userInfo.employeeName || '--' }}</span>
        <span class="des">{{ userInfo.deptName || '--' }}</span>
        <span class="des">{{ userInfo.post || '--' }}</span>
        <span class="des">{{ statusName }}</span>
        <div v-if="showDetail && !openEdit" class="handle">
          <el-button
            v-if="detail.isUpdate"
            class="head-handle-button xr-btn--green"
            icon="wk wk-circle-edit"
            type="primary"
            @click.native="handleClick('edit')">编辑</el-button>
          <el-dropdown
            v-if="moreTypes.length > 0"
            trigger="click"
            @command="handleClick">
            <el-button
              icon="el-icon-more" />
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                v-for="(item, index) in moreTypes"
                :key="index"
                :icon="item.icon"
                :command="item.command">{{ item.name }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </flexbox>
      <el-form
        ref="changeAddForm"
        :model="ruleForm"
        :rules="rules"
        label-width="100px"
        label-position="top">
        <el-form-item v-if="!showDetail && !openEdit" label="调薪模板" prop="templateId">
          <el-select
            v-model="ruleForm.templateId"
            class="item-width"
            @change="templateChange">
            <el-option
              v-for="item in templateList"
              :key="item.id"
              :label="item.templateName"
              :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="调薪细则" prop="value">
          <template v-if="userInfo && userInfo.status == 2">
            <div>试用期工资</div>
            <el-table
              :data="ruleForm.proValue"
              height="300"
              show-summary
              style="width: 100%;margin-bottom: 40px;">
              <el-table-column
                prop="name"
                label="调薪项"
                width="180" />
              <el-table-column
                prop="beforeValue"
                label="调薪前" />
              <el-table-column
                prop="afterValue"
                label="调薪后">
                <template slot-scope="scope">
                  <template v-if="showDetail">
                    {{ scope.row.afterValue }}
                  </template>
                  <el-input-number
                    v-else
                    v-model="scope.row.afterValue"
                    :precision="2"
                    :min="0"
                    :max="9999999.99"
                    :controls="false" />
                </template>
              </el-table-column>
            </el-table>
          </template>
          <div>转正后工资</div>
          <el-table
            :data="ruleForm.value"
            height="300"
            show-summary
            style="width: 100%;">
            <el-table-column
              prop="name"
              label="调薪项"
              width="180" />
            <el-table-column
              prop="beforeValue"
              label="调薪前" />
            <el-table-column
              prop="afterValue"
              label="调薪后">
              <template slot-scope="scope">
                <template v-if="showDetail">
                  {{ scope.row.afterValue }}
                </template>
                <el-input-number
                  v-else
                  v-model="scope.row.afterValue"
                  :precision="2"
                  :min="0"
                  :max="9999999.99"
                  :controls="false" />
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <flexbox
          align="stretch">
          <el-form-item label="调薪原因" style="flex: 1;" prop="changeReason">
            <template v-if="showDetail">
              {{ ruleForm.changeReason | getReasonName }}
            </template>
            <el-select
              v-else
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
            <template v-if="showDetail">
              {{ ruleForm.enableDate }}
            </template>
            <el-date-picker
              v-else
              v-model="ruleForm.enableDate"
              :picker-options="pickerOptions"
              style="width: 100%;"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="选择日期" />
          </el-form-item>
        </flexbox>
        <el-form-item label="备注" prop="remarks">
          <template v-if="showDetail">
            {{ ruleForm.remarks }}
          </template>
          <el-input
            v-else
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
import { hrmEmployeeQueryByIdAPI } from '@/api/hrm/employee'
import {
  hrmSalaryChangeTemplateQueryListAPI,
  hrmSalaryArchivesSetChangeAPI,
  hrmSalaryArchivesQueryChangeOptionByIdAPI,
  hrmSalaryArchivesQueryChangeRecordAPI,
  hrmSalaryArchivesCancelChangeSalaryAPI,
  hrmSalaryArchivesDeleteChangeSalaryAPI
} from '@/api/hrm/salary'
import {
  hrmSalaryConfigQueryInItConfigAPI
} from '@/api/hrm/salaryConfig'

import XrCreate from '@/components/XrCreate'

import { employeeModel } from '@/views/hrm/employee/model/employee'
import { floatAdd } from '@/utils'
import archivesModel from '../model/archives'
import { isEmpty } from '@/utils/types'
import { objDeepCopy } from '@/utils'

export default {
  // 单个调薪
  name: 'ChangeSalaryAdd',

  components: {
    XrCreate
  },

  filters: {
    getReasonName(value) {
      return archivesModel.changeReasonValue[value]
    }
  },

  mixins: [],

  props: {
    recordId: [String, Number], // 记录Id 用于编辑
    id: [String, Number], // 员工Id
    helpObj: Object
  },

  data() {
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
      statusObj: {
        0: '未生效',
        1: '已生效',
        2: '已取消'
      },
      statusClass: {
        0: 'is-cancel',
        1: 'is-success',
        2: 'is-cancel'
      },
      templateList: [],
      userInfo: {},
      changeReasonList: [],
      ruleForm: {
        templateId: '',
        remarks: '',
        value: [],
        proValue: [],
        changeReason: ''
      },
      openEdit: false,
      detail: {}
    }
  },

  computed: {
    title() {
      return this.showDetail ? '调薪记录' : '单个调薪'
    },

    showDetail() {
      return !!this.recordId && !this.openEdit
    },

    rules() {
      return this.showDetail ? {} : {
        templateId: [
          { required: true, message: '调薪模板不能为空', trigger: 'blur' }
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
    },

    moreTypes() {
      const temps = []
      if (this.detail.isDelete) {
        temps.push({
          name: '删除',
          icon: 'wk wk-delete',
          command: 'delete'
        })
      }

      if (this.detail.isCancel) {
        temps.push({
          name: '取消',
          icon: 'wk wk-icon-reply',
          command: 'cancel'
        })
      }

      return temps
    },

    statusName() {
      return employeeModel.statusValue[this.userInfo.status] || '--'
    }
  },

  watch: {},

  created() {
    this.changeReasonList = archivesModel.getValueList(archivesModel.changeReasonValue)
    this.ruleForm.changeReason = this.changeReasonList[0].value
    this.getEmployeeDetail()
    if (this.showDetail) {
      this.getDetail()
    } else {
      this.getTemplateList()
      this.getConfigList()
    }
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 获取详情数据
     */
    getDetail() {
      this.loading = true
      hrmSalaryArchivesQueryChangeRecordAPI(this.recordId)
        .then(res => {
          this.loading = false
          const resData = res.data || {}
          this.detail = resData
          this.detail = objDeepCopy(resData)
          this.ruleForm = this.getEditData(resData)
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 获取编辑数据
     */
    getEditData(data) {
      const ruleForm = {
        changeReason: data.changeReason,
        enableDate: data.enableDate,
        remarks: data.remarks
      }
      const proValue = []
      const proSalary = data.proSalary || {}
      const proNewSalary = proSalary.newSalary || []
      const proOldSalary = proSalary.oldSalary || []
      proOldSalary.forEach((item, index) => {
        proValue.push({
          code: item.code,
          name: item.name,
          beforeValue: item.value,
          afterValue: proNewSalary[index].value
        })
      })
      ruleForm.proValue = proValue

      const value = []
      const salary = data.salary || {}
      const newSalary = salary.newSalary || []
      const oldSalary = salary.oldSalary || []
      oldSalary.forEach((item, index) => {
        value.push({
          code: item.code,
          name: item.name,
          beforeValue: item.value,
          afterValue: newSalary[index].value
        })
      })

      ruleForm.value = value
      return ruleForm
    },

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
     * 员工详情
     */
    getEmployeeDetail() {
      this.loading = true
      hrmEmployeeQueryByIdAPI(this.id)
        .then(res => {
          this.loading = false
          this.userInfo = res.data || {}
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 获取模板数据
     */
    getTemplateList() {
      this.loading = true
      hrmSalaryChangeTemplateQueryListAPI()
        .then(res => {
          this.templateList = res.data || []
          if (this.templateList.length > 0) {
            const templateData = this.templateList[0]
            this.ruleForm.templateId = templateData.id
            this.getChangeOptionValue(templateData.id)
          }
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 查询调薪项的值
     */
    getChangeOptionValue(templateId) {
      this.loading = true
      hrmSalaryArchivesQueryChangeOptionByIdAPI({
        employeeId: this.id,
        templateId: templateId
      })
        .then(res => {
          const resData = res.data || {}
          this.ruleForm.proValue = this.getShowOption(resData.proSalary || [])
          this.ruleForm.value = this.getShowOption(resData.salary || [])
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
          beforeValue: isEmpty(item.value) ? 0.00 : item.value,
          afterValue: isEmpty(item.value) ? 0.00 : item.value
        })
      })
      return value
    },

    /**
     * 模板调整
     */
    templateChange() {
      const templateData = this.templateList.find(item => item.id === this.ruleForm.templateId)
      this.getChangeOptionValue(templateData.id)
    },

    /**
     * 保存
     */
    saveClick() {
      this.$refs.changeAddForm.validate((valid) => {
        if (valid) {
          this.loading = true

          const proSalaryObj = this.getOptionParams(this.ruleForm.proValue)
          const proSalary = {
            newSalary: proSalaryObj.newSalary,
            oldSalary: proSalaryObj.oldSalary
          }
          const proBeforeSum = proSalaryObj.beforeSum
          const proAfterSum = proSalaryObj.afterSum

          const salaryObj = this.getOptionParams(this.ruleForm.value)
          const salary = {
            newSalary: salaryObj.newSalary,
            oldSalary: salaryObj.oldSalary
          }
          const beforeSum = salaryObj.beforeSum
          const afterSum = salaryObj.afterSum

          const params = {
            employeeId: this.id,
            remarks: this.ruleForm.remarks,
            changeReason: this.ruleForm.changeReason,
            enableDate: this.ruleForm.enableDate,
            proSalary,
            proBeforeSum,
            proAfterSum,
            salary,
            beforeSum,
            afterSum
          }

          if (this.recordId) {
            params.id = this.recordId
          }

          hrmSalaryArchivesSetChangeAPI(params)
            .then(res => {
              this.$message.success('操作成功')
              this.$emit('change')
              this.loading = false
              this.$emit('close')
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
     * 获取项提交值
     */
    getOptionParams(list) {
      const newSalary = []
      const oldSalary = []
      let beforeSum = 0
      let afterSum = 0
      list.forEach(item => {
        oldSalary.push({
          code: item.code,
          name: item.name,
          value: item.beforeValue
        })
        beforeSum = floatAdd(beforeSum, item.beforeValue)

        newSalary.push({
          code: item.code,
          name: item.name,
          value: item.afterValue
        })
        afterSum = floatAdd(afterSum, item.afterValue)
      })
      return {
        newSalary,
        oldSalary,
        beforeSum,
        afterSum
      }
    },

    /**
     * 关闭
     */
    close() {
      if (this.showDetail) {
        this.$emit('close')
      } else {
        this.dialogVerify().then(() => {
          this.$emit('close')
        })
      }
    },

    /**
     * 弹窗验证
     */
    dialogVerify() {
      return new Promise((resolve) => {
        this.$confirm('确定离开？正在编辑的内容将会丢失', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            resolve()
          })
          .catch(() => {})
      })
    },

    /**
     * 关闭
     */
    closeClick() {
      if (this.openEdit) {
        this.dialogVerify().then(() => {
          this.ruleForm = this.getEditData(objDeepCopy(this.detail))
          this.openEdit = false
        })
      } else {
        this.close()
      }
    },

    /**
     * 操作
     */
    handleClick(type) {
      if (type === 'edit') {
        this.openEdit = true
      } else if (type === 'delete') {
        this.$confirm('确定删除此次调薪？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            this.loading = true
            hrmSalaryArchivesDeleteChangeSalaryAPI(this.recordId)
              .then(res => {
                this.loading = false
                this.$message.success('操作成功')
                this.$emit('change')
                this.loading = false
                this.$emit('close')
              })
              .catch(() => {
                this.loading = false
              })
          })
          .catch(() => {})
      } else if (type === 'cancel') {
        this.$confirm('确定取消此次调薪？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            this.loading = true
            hrmSalaryArchivesCancelChangeSalaryAPI(this.recordId)
              .then(res => {
                this.loading = false
                this.$message.success('操作成功')
                this.$emit('change')
                this.loading = false
                this.$emit('close')
              })
              .catch(() => {
                this.loading = false
              })
          })
          .catch(() => {})
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.create-title {
  flex: 1;
  font-size: 17px;
  font-weight: bold;
  color: $--color-text-primary;

  .mark {
    padding: 2px 8px;
    margin-left: 8px;
    font-size: 12px;
    font-weight: normal;
    border-radius: 4px;
  }

  .mark.is-success {
    color: white;
    background: #389e06;
  }

  .mark.is-cancel {
    color: #ccc;
    background: #f0f0f0;
  }
}

.content {
  padding: 0 15px;

  &__header {
    min-height: 31px;

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

    .handle {
      flex: 1;
      text-align: right;
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
.el-textarea {
  width: calc(50% - 40px);
}

.el-dropdown-menu {
  ::v-deep .wk-icon-reply {
    font-size: 12px;
  }
}
</style>
