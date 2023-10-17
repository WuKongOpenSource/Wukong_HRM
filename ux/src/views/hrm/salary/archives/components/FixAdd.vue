<template>
  <xr-create
    :loading="loading"
    :title="title"
    :show-cancel="!showDetail"
    :show-confirm="!showDetail"
    @close="closeClick"
    @save="saveClick">
    <template slot="title-append">
      <i
        v-if="helpObj"
        class="wk wk-icon-fill-help wk-help-tips"
        :data-type="helpObj.dataType"
        :data-id="helpObj.dataId" />
    </template>
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
        </div>
      </flexbox>
      <el-form
        ref="addForm"
        :model="ruleForm"
        :rules="rules"
        label-width="100px"
        label-position="top">
        <el-form-item v-if="!showDetail && !openEdit" label="定薪模板" prop="templateId">
          <el-select
            v-model="ruleForm.templateId"
            @change="templateChange">
            <el-option
              v-for="item in templateList"
              :key="item.id"
              :label="item.templateName"
              :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="定薪细则" prop="value">
          <el-table
            :data="ruleForm.value"
            height="300"
            show-summary
            style="width: 100%;">
            <el-table-column
              prop="name"
              label="定薪项"
              width="180" />
            <el-table-column
              prop="probationValue"
              label="试用期工资">
              <template slot-scope="scope">
                <template v-if="showDetail">
                  {{ scope.row.probationValue }}
                </template>
                <template v-else>
                  <el-input-number
                    v-if="userInfo && userInfo.status == 2"
                    v-model="scope.row.probationValue"
                    :precision="2"
                    :min="0"
                    :max="9999999.99"
                    :controls="false" />
                  <template v-else>{{ scope.row.probationValue }}</template>
                </template>
              </template>
            </el-table-column>
            <el-table-column
              prop="permanentValue"
              label="转正后工资">
              <template slot-scope="scope">
                <template v-if="showDetail">
                  {{ scope.row.permanentValue }}
                </template>
                <el-input-number
                  v-else
                  v-model="scope.row.permanentValue"
                  :precision="2"
                  :min="0"
                  :max="9999999.99"
                  :controls="false" />
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
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
  hrmSalaryArchivesSetFixAPI,
  hrmSalaryArchivesQueryFixRecordAPI
} from '@/api/hrm/salary'

import XrCreate from '@/components/XrCreate'

import { employeeModel } from '@/views/hrm/employee/model/employee'
import { floatAdd } from '@/utils'
import { objDeepCopy } from '@/utils'

export default {
  // 单个定薪
  name: 'FixSalaryAdd',

  components: {
    XrCreate
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
      templateList: [],
      userInfo: {},
      ruleForm: {
        templateId: '',
        remarks: '',
        value: []
      },
      openEdit: false,
      detail: {}
    }
  },

  computed: {
    title() {
      return this.showDetail ? '定薪记录' : '单个定薪'
    },

    showDetail() {
      return !!this.recordId && !this.openEdit
    },

    rules() {
      return this.showDetail ? {} : {
        templateId: [
          { required: true, message: '定薪模板不能为空', trigger: 'blur' }
        ],
        value: [
          { required: true, message: '定薪细则不能为空', trigger: 'blur' }
        ]
      }
    },

    statusName() {
      return employeeModel.statusValue[this.userInfo.status] || '--'
    }
  },

  watch: {},

  created() {
    this.getEmployeeDetail()
    if (this.showDetail) {
      this.getDetail()
    } else {
      this.getTemplateList()
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
      hrmSalaryArchivesQueryFixRecordAPI(this.recordId)
        .then(res => {
          this.loading = false
          const resData = res.data || {}
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
      const proSalary = data.proSalary || []
      const proSalaryObj = {}
      proSalary.forEach(item => {
        proSalaryObj[item.code] = item.value
      })

      const salary = data.salary || []
      const value = []
      salary.forEach(item => {
        value.push({
          code: item.code,
          name: item.name,
          probationValue: proSalaryObj[item.code] || 0.00,
          permanentValue: item.value
        })
      })
      return {
        remarks: data.remarks,
        value
      }
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
            this.getTemplateOption(templateData.value)
          }
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 获取调薪项
     */
    getTemplateOption(list) {
      const value = []
      list.forEach(item => {
        value.push({
          ...item,
          probationValue: 0.00,
          permanentValue: 0.00
        })
      })
      this.ruleForm.value = value
    },

    /**
     * 模板调整
     */
    templateChange() {
      const templateData = this.templateList.find(item => item.id === this.ruleForm.templateId)
      this.getTemplateOption(templateData.value)
    },

    /**
     * 保存
     */
    saveClick() {
      this.$refs.addForm.validate((valid) => {
        if (valid) {
          this.loading = true

          const value = this.ruleForm.value
          const proSalary = []
          let proSum = 0.00
          const salary = []
          let sum = 0.00
          value.forEach(item => {
            proSalary.push({
              code: item.code,
              name: item.name,
              value: item.probationValue
            })
            proSum = floatAdd(proSum, item.probationValue)

            salary.push({
              code: item.code,
              name: item.name,
              value: item.permanentValue
            })
            sum = floatAdd(sum, item.permanentValue)
          })

          const params = {
            employeeId: this.id,
            remarks: this.ruleForm.remarks,
            proSalary,
            proSum,
            salary,
            sum
          }

          if (this.recordId) {
            params.id = this.recordId
          }

          hrmSalaryArchivesSetFixAPI(params)
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
      }
    }
  }
}
</script>

<style lang="scss" scoped>
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

.el-textarea,
.el-select {
  width: calc(50% - 40px);
}
</style>
