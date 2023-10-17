:disabled="disabled"<template>
  <el-card
    v-loading="loading"
    style="padding: 0 20px;overflow: auto;"
    :body-style="{ height: '100%' }"
    class="base-info-set">

    <el-form
      ref="baseForm"
      :model="baseForm"
      label-position="top">
      <create-sections title="考核范围及考核指标" />
      <el-form-item label="考核范围" :rules="{required:true}">
        <div
          v-for="(item,index) in baseForm.inspectionScope"
          :key="index"
          style="display: flex;align-items: center;margin-bottom: 15px;">
          <el-form-item
            style="margin-right: 10px;"
            :prop="`inspectionScope[${index}].type`"
            :rules="{required:true, message: '请选择', trigger: 'change'}">
            <el-select
              v-model="item.type"

              style="width: 180px;"
              :disabled="disabled"
              class="el-select-active"
              @change="inspectionScopeChange($event,index)">
              <el-option
                v-for="sItem in inspectionScopeOptions"
                :key="sItem.value"
                :label="sItem.label"
                :value="sItem.value"
                :disabled="sItem.disabled" />
            </el-select>
          </el-form-item>

          <el-form-item
            v-if="item.type == 2"
            style="margin-right: 10px;"
            :prop="`inspectionScope[${index}].employType`"
            :rules="{ required: true, message: '请选择', trigger: 'change' }">
            <el-select
              v-model="item.employType"
              :disabled="disabled ? disabled : item.type == '' ? true : false"
              style="width: 180px;"

              @change="hireFormChange($event,index,item)">
              <el-option
                v-for="sItem in hireFormOptions"
                :key="sItem.value"
                :label="sItem.label"
                :disabled="sItem.disabled"
                :value="sItem.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item
            v-if="item.type == 2"
            :prop="`inspectionScope[${index}].employeeStatus`"
            :rules="{ required: true, message: '请选择', trigger: 'change' }">
            <el-select
              v-model="item.employeeStatus"
              :disabled="disabled ? disabled : item.type == '' ? true : false"
              multiple
              style="width: 180px;"
              >
              <el-option
                v-for="sItem in getEmploySubOptions(item)"
                :key="sItem.value"
                :label="sItem.label"
                :value="sItem.value" />
            </el-select>
          </el-form-item>

          <el-form-item
            v-if="item.type == 1 || item.type == ''"
            :prop="`inspectionScope[${index}]`"
            :rules="userDepRules">
            <wk-user-dep-select
              :props="{dataType:'hrm',isKpi:true}"
              :disabled="disabled ? disabled : item.type == '' ? true : false"
              :dep-value="baseForm.inspectionScope[index].deptIds"
              :user-value="baseForm.inspectionScope[index].employeeIds"
              @change="selectUserDepChange(arguments,index)" />
          </el-form-item>
          <i
            v-if="baseForm.inspectionScope.length>1 && !disabled"
            style="margin-left: 5px;color: red;"
            class="el-icon-error"
            @click="deleteItemClick(index)" />
        </div>
        <el-button
          type="text"
          :disabled="baseForm.inspectionScope.length == 3 || disabled"
          @click="addInspectionScope">+添加</el-button>
      </el-form-item>

      <el-form-item
        label="考核指标模板"
        prop="appraisalTemplateId"
        :rules="{required:true, message: '请选择', trigger: 'change'}">
        <div style="display: flex;">
          <el-button
            type="primary"
            :disabled="disabled"
            @click="selectTemplateShow = true">选择模板</el-button>
          <el-tooltip
            v-show="selectTemplateName"
            class="item"
            effect="dark"
            :content="selectTemplateName"
            placement="top-start">
            <div ref="templateName" class="template-tag">
              <span class="template-tag-overflow">{{ selectTemplateName }}</span>
              <i v-if="!disabled" class="wk wk-close" @click="handleClose" />
            </div>
          </el-tooltip>
        </div>
      </el-form-item>

      <create-sections title="基础信息" style="margin-top: 20px;" />
      <el-form-item
        v-for="(item,index) in baseOtherFieldList"
        :key="index"
        :label="item.name"
        :prop="item.fieldName"
        :rules="item.rules">
        <!-- eslint-disable-next-line -->
        <template slot-scope="scope">
          <el-input
            v-if="item.fieldName == 'appraisalPlanName'"
            v-model="baseForm[item.field]"
            :disabled="disabled"
            style="width: 400px;" />

          <el-select
            v-if="item.fieldName == 'appraisalCycleType'"
            v-model="baseForm[item.field]"
            :disabled="disabled"

            @change="appraisalCycleTypeChange">
            <el-option
              v-for="sItem in item.setting"
              :key="sItem.value"
              :label="sItem.label"
              :value="sItem.value" />
          </el-select>

          <div
            v-if="item.field == 'appraisalCycle'"
            style="display: flex;justify-content: space-between;width: 400px;">
            <el-form-item
              prop="appraisalCycle"
              :rules="{required:true, message: '请选择', trigger: 'change'}">
              <el-date-picker
                v-if="item.field == 'appraisalCycle'"
                v-model="baseForm[item.field]"
                :disabled="disabled"
                :style="baseForm.appraisalCycleType != 2 ? 'width:100%' : 'width:200px'"
                :value-format="datePickerTypeFormat[baseForm.appraisalCycleType]"
                :type="datePickerType[baseForm.appraisalCycleType]"
                :range-separator="baseForm.appraisalCycleType == 6 ? '至' : ''"
                :start-placeholder="baseForm.appraisalCycleType == 6 ? '开始日期' : ''"
                :end-placeholder="baseForm.appraisalCycleType == 6 ? '结束日期' : ''"
                placeholder="请选择" />
            </el-form-item>

            <el-form-item
              v-if="baseForm.appraisalCycleType == 2"
              prop="quarter"
              :rules="{required:true, message: '请选择', trigger: 'change'}">
              <el-select
                v-model="baseForm['quarter']"
                style="width: 180px;"
                :disabled="disabled"
                >
                <el-option
                  v-for="sItem in [
                    {label:'第一季度', value:'第一季度'},
                    {label:'第二季度', value:'第二季度'},
                    {label:'第三季度', value:'第三季度'},
                    {label:'第四季度', value:'第四季度'}
                  ]"
                  :key="sItem.value"
                  :label="sItem.label"
                  :value="sItem.value" />
              </el-select>
            </el-form-item>
          </div>
          <wk-textarea
            v-if="item.formType == 'textarea'"
            v-model="baseForm[item.fieldName]"
            style="width: 400px;"
            :rows="3"
            :disabled="disabled"
            :autosize="{ minRows: 3}"
            maxlength="200"
            resize="none"
            type="textarea" />
        </template>
      </el-form-item>
    </el-form>
    <!-- </create-sections> -->

    <select-template-dialog
      v-show="selectTemplateShow"
      :select-template-show="selectTemplateShow"
      :select-template-name="selectTemplateName"
      :status="status"
      @select="handelSelect"
      @close="selectTemplateShow=false" />
  </el-card>
</template>

<script type="text/javascript">
import { queryInspectionDeptEmployeeList } from '@/api/hrm/performance'

import CreateSections from '@/components/CreateSections'
import SelectTemplateDialog from './SelectTemplateDialog'
import WkTextarea from '@/components/NewCom/WkTextarea'
import WkUserDepSelect from '@/components/NewCom/WkUserDepSelect'

import GenerateRulesMixin from '@/components/NewCom/WkForm/GenerateRules'
import CustomFieldsMixin from '@/mixins/CustomFields'

export default {
  name: 'BaseInfoSet',
  components: {
    CreateSections,
    SelectTemplateDialog,
    WkTextarea,
    WkUserDepSelect
  },
  mixins: [GenerateRulesMixin, CustomFieldsMixin],
  inject: {
    disabled: {
      default: false
    }
  },

  props: {
    baseForm: Object,
    verify: Object,
    targetTemplateName: String,
    status: [String, Number]
  },

  data() {
    var validateMemberUsers = (rule, value, callback) => {
      if (value && ((value.employeeIds && value.employeeIds.length) || (value.deptIds && value.deptIds.length))) {
        callback()
      } else {
        callback(new Error('请选择'))
      }
    }

    return {
      loading: false,

      queryInspectionDeptEmployeeListFn: queryInspectionDeptEmployeeList,

      appraisalCycleTypeList: [
        { label: '月度', value: 1 },
        { label: '季度', value: 2 },
        { label: '上半年', value: 3 },
        { label: '下半年', value: 4 },
        { label: '全年', value: 5 },
        { label: '其他', value: 6 }
      ],

      baseOtherForm: {
        appraisalCycleType: 1
      },
      baseFormRules: {},
      baseOtherFieldList: [],

      inspectionScopeOptions: [
        { label: '员工部门', value: 1, disabled: false },
        { label: '聘用形式', value: 2, disabled: false }
      ],
      hireFormOptions: [
        { label: '正式', value: 1, disabled: false },
        { label: '非正式', value: 2, disabled: false }
      ],

      datePickerType: {
        1: 'month',
        2: 'year',
        3: 'year',
        4: 'year',
        5: 'year',
        6: 'daterange'
      },
      datePickerTypeFormat: {
        1: 'yyyy-MM',
        2: 'yyyy',
        3: 'yyyy',
        4: 'yyyy',
        5: 'yyyy',
        6: 'yyyy-MM-dd'
      },

      selectTemplateName: '',

      depUserViewDialogShow: false,
      selectTemplateShow: false,

      userDepRules: { required: true, validator: validateMemberUsers, trigger: 'change' }
    }
  },

  computed: {
    titleContent() {
      return Object.keys(this.resultsTemplate).length ? '编辑' : '新建'
    }
  },

  watch: {
    'baseForm.inspectionScope': {
      handler(val) {
        var sum = 0
        val.forEach(sItem => {
          if (sItem.type == 2) {
            sum += 1
          }
        })
        val.forEach((item, index) => {
          if (item.type == 1) {
            this.inspectionScopeOptions[item.type - 1 ].disabled = true
          } else if (item.type == 2) {
            if (item.employType) this.hireFormOptions[item.employType - 1 ].disabled = true
            if (sum == 2) {
              this.inspectionScopeOptions[item.type - 1 ].disabled = true
            }
          }
        })
      },
      immediate: true,
      deep: true
    },
    verify: {
      handler(val) {
        if (val.tab == 'base' && !val.pass) {
          this.$refs.baseForm.validate((valid) => {
            if (valid) {
              this.$emit('pass', val.next, val.tab, val.type)
            } else {
              this.$message.error('请完善表单信息')
            }
          })
        }
      },
      deep: true
    },
    targetTemplateName: {
      handler(val) {
        if (val) {
          this.selectTemplateName = val
        }
      },
      immediate: true
    }
  },

  created() {
    this.getField()
  },

  methods: {
    /**
     * 为表单添加验证规则
     */
    getField() {
      this.baseOtherFieldList = [
        { name: '考核计划名称',
          field: 'appraisalPlanName',
          fieldName: 'appraisalPlanName',
          formType: 'text',
          rules: [
            { required: true, message: '考核计划名称不能为空', trigger: ['change', 'blur'] },
            { max: 50, message: '最多输入50个汉字、英文、数字', trigger: ['change', 'blur'] }
          ]
        }, {
          name: '考核周期类型',
          field: 'appraisalCycleType',
          fieldName: 'appraisalCycleType',
          formType: 'select',
          setting: this.appraisalCycleTypeList,
          rules: { required: true, message: '考核周期类型不能为空', trigger: 'change' }
        }, {
          name: '考核周期',
          field: 'appraisalCycle',
          rules: { required: true }
        }, {
          name: '考核说明',
          field: 'appraisalIllustrate',
          fieldName: 'appraisalIllustrate',
          formType: 'textarea'
        }
      ]

      const rules = {}
      this.baseOtherFieldList.forEach(item => {
        if (item.formType == 'select') {
          rules[item.field] = { required: true, message: '请选择', trigger: 'change' }
        } else if (item.fieldName == 'appraisalCycle') {
          rules[item.field] = { required: true }
        } else {
          rules[item.field] = this.getRules(item)
        }
      })
      this.baseFormRules = rules
    },

    /**
     * 员工部门选择
     */
    selectUserDepChange(list, index) {
      this.$set(this.baseForm.inspectionScope[index], 'employeeIds', list[0])
      this.$set(this.baseForm.inspectionScope[index], 'deptIds', list[1])
      this.$refs.baseForm.validateField(`inspectionScope[${index}]`)
    },

    appraisalCycleTypeChange() {
      this.$set(this.baseForm, 'appraisalCycle', '')
      this.$set(this.baseForm, 'quarter', '')
    },

    getEmploySubOptions(item) {
      if (item.employType && item.employType == 1) {
        return [
          { label: '正式', value: 1 },
          { label: '试用', value: 2 }
        ]
      } else if (item.employType && item.employType == 2) {
        return [
          { label: '实习', value: 3 },
          { label: '兼职', value: 4 },
          { label: '劳务', value: 5 },
          { label: '顾问', value: 6 },
          { label: '返聘', value: 7 },
          { label: '外包', value: 8 }
        ]
      } else {
        return []
      }
    },

    /**
     * 添加考核范围
     */
    addInspectionScope() {
      this.baseForm.inspectionScope.push({ type: '', value: '' })
    },

    /**
     * 删除考核范围
     */
    deleteItemClick(index) {
      if (this.baseForm.inspectionScope[index].employType) {
        this.hireFormOptions[this.baseForm.inspectionScope[index].employType - 1 ].disabled = false
      }
      this.baseForm.inspectionScope.splice(index, 1)
      this.baseForm.inspectionScope.forEach(item => {
        this.inspectionScopeOptions.forEach(sItem => {
          if (item.type != sItem.value) {
            sItem.disabled = false
          }
        })
      })
    },

    /**
     * 选择考核指标模板
     */
    handelSelect(data) {
      this.$set(this.baseForm, 'appraisalTemplateId', data.templateId)
      this.selectTemplateName = data.templateName
      this.$refs.baseForm.validateField('appraisalTemplateId')
    },

    /**
     * 移除考核指标模板
     */
    handleClose() {
      this.$set(this.baseForm, 'appraisalTemplateId', '')
      this.selectTemplateName = ''
      this.$refs.baseForm.validateField('appraisalTemplateId')
    },

    /**
     * 考核范围发生变化
     */
    inspectionScopeChange(val, index) {
      var sum = 0
      this.baseForm.inspectionScope.forEach(item => {
        if (item.type == 2) sum += 1
      })

      this.baseForm.inspectionScope.forEach(item => {
        if (item.type == val && val == 1 && sum != 2) {
          this.inspectionScopeOptions[val].disabled = false
        } else if (item.type == val && val == 2) {
          this.inspectionScopeOptions[0].disabled = false
        }
      })
    },

    // 聘用形式发生变化
    hireFormChange(val, index, item) {
      this.hireFormOptions.forEach(item => {
        if (item.value == val) {
          item.disabled = true
        } else {
          item.disabled = false
        }
      })
      item.employeeStatus ? item.employeeStatus = [] : ''
    }

  }
}
</script>

<style lang="scss" scoped>
.base-info-set {
  width: 100%;
  height: 100%;

  &__header {
    height: 40px;
    padding: 0 10px;
    margin-bottom: 20px;
    font-size: 17px;
    font-weight: bold;
    color: $--color-text-primary;
  }

  .el-form {
    padding-right: 60%;

    .el-select-active {
      ::v-deep input {
        height: 32px !important;
      }
    }

    .el-form-item.is-required {
      margin-bottom: 0;

      ::v-deep .el-form-item__label {
        padding-bottom: 0;
      }
    }

    .template-tag-overflow {
      width: 100px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .template-tag {
      display: flex;
      justify-content: space-between;
      height: 32px;
      padding: 0 5px;
      margin-left: 5px;
      line-height: 32px;
      color: #42526e;
      background-color: #dfe1e6;
      border-radius: 3px;

      .wk-close {
        color: #42526e;
        cursor: pointer;
      }
    }
  }

  .el-button--text {
    color: #0052cc;

    &.is-disabled {
      color: #a5adba;
    }
  }
}
</style>
