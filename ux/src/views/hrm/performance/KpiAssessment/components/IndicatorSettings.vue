<template>
  <el-card
    v-loading="loading"
    style="padding: 0 20px;overflow: auto;"
    class="base-info-set">
    <el-form
      ref="baseForm"
      style="padding-right: 70%;"
      :model="tagetForm"
      label-position="top"
      :rules="fieldsRules">
      <wk-form-items
        :field-from="tagetForm"
        :disabled="disabled"
        :field-list="fields">
        <template slot-scope="{ data }">
          <el-radio
            v-if="data && data.formType == 'radioType'"
            v-model="tagetForm[data.fieldName]"
            :disabled="disabled"
            :label="1">
            加权计算
            <span style="font-size: 12px;color: #5e6c84;">总分=评分 X 维度权重 X 指标权重，再累加</span>
          </el-radio>
          <el-input
            v-if="data && data.formType == 'integer'"
            v-model="tagetForm[data.fieldName]"
            :disabled="disabled"
            placeholder=""
            @input="tagetForm[data.fieldName]=tagetForm[data.fieldName].replace(/[^\d]/g,'')"
          />
        </template>
      </wk-form-items>
    </el-form>
    <div>
      <div class="other">
        <span>
          当前维度权重合计:
          <span :style="dimensionWeightSum > 100 ? 'color:red' : 'color:#ffb55f'">
            {{ dimensionWeightSum }}<span v-if="dimensionWeightSum != '--'">%</span>
          </span>
        </span>
        <el-button
          type="primary"
          :disabled="disabled"
          @click="examineShow = true">
          新增考核维度
        </el-button>
      </div>
      <assessment-dimension
        v-if="assessmentDimensionList && assessmentDimensionList.length > 0"
        ref="assessmentForm"
        :assessment-dimension-list="assessmentDimensionList"
        :quota-item="quotaItem"
        @editAssess="editAssess" />
    </div>
    <el-dialog
      v-if="examineShow"
      width="900"
      :visible.sync="examineShow"
      append-to-body
      custom-class="drawer"
      :before-close="close">
      <el-form
        ref="examineForm"
        :model="examineFieldsForm"
        label-position="top"
        :rules="examineFieldsRules">
        <wk-form-items
          :field-from="examineFieldsForm"
          :field-list="examineFields">
          <template slot-scope="{ data }">
            <el-checkbox
              v-if="data && data.formType == 'check'"
              v-model="examineFieldsForm[data.fieldName]"
              :true-label="1"
              :false-label="0">允许员工填写&ensp;勾选后，员工在制定指标时可以为当前考核维度添加指标</el-checkbox>
          </template>
        </wk-form-items>
      </el-form>

      <span slot="footer" class="dialog-footer">

        <el-button @click="examineShow = false">取 消</el-button>
        <el-button type="primary" @click="examineCreate">确 定</el-button>
      </span>

    </el-dialog>
  </el-card>
</template>

<script type="text/javascript">
import { quoteTypeListAPI, informationAPI } from '@/api/hrm/achievementsAssessmentTemplate'

import WkFormItems from '@/components/NewCom/WkForm/WkFormItems'
import AssessmentDimension from '../../../components/AssessmentDimension'

import GenerateRulesMixin from '@/components/NewCom/WkForm/GenerateRules'
import CustomFieldsMixin from '@/mixins/CustomFields'

import { objDeepCopy } from '@/utils'
export default {
  name: 'IndicatorSettings',
  components: {
    WkFormItems,
    AssessmentDimension
  },
  mixins: [GenerateRulesMixin, CustomFieldsMixin],
  inject: {
    disabled: {
      default: false
    }
  },

  props: {
    tagetForm: Object,
    verify: Object,
    templateId: String,
    status: [String, Number]
  },
  data() {
    return {
      loading: false,
      height: document.documentElement.clientHeight - 100,

      fields: [],
      fieldsRules: {},

      // 考核维度
      examineFields: [],
      examineFieldsForm: {},
      examineFieldsRules: {},
      examineShow: false, // 新增考核维度
      quoteTypeList: [], // 指标类型

      assessmentDimensionList: [],

      quotaItem: {},
      examineType: 'create',
      examineIndex: undefined,

      flag: false
    }
  },

  computed: {
    dimensionWeightSum() {
      var sum = 0
      if (this.assessmentDimensionList && !this.assessmentDimensionList.length) {
        return '--'
      } else {
        this.assessmentDimensionList.forEach(item => {
          sum += item.dimensionWeight
        })
      }
      return sum
    }
  },

  watch: {
    /**
     * 重置新增考核维度表单
     */
    examineShow(val) {
      if (!val) {
        this.examineFieldsForm = {
          quotaType: 1
        }
      }
    },
    verify: {
      handler(val) {
        if (val.tab == 'target' && !val.pass) {
          var flag = true
          var tableFlag = true
          var weightSum = true
          var sValidFlag = true
          this.$refs.baseForm.validate((valid) => {
            if (this.$refs.assessmentForm && this.$refs.assessmentForm.$refs.form) {
              for (var key in this.$refs.assessmentForm.tableForm) {
                // 考核维度最少一项
                if (this.$refs.assessmentForm.tableForm[key].length == 1) {
                  tableFlag = false
                }
                this.assessmentDimensionList.forEach(childItem => {
                  if (!childItem.isAllowEdit) {
                    var weightNum = 0
                    // 考核维度百分比总数为100
                    this.$refs.assessmentForm.tableForm['table' + childItem.key].forEach(item => {
                      weightNum += item.quotaWeight || 0
                    })
                    console.log(weightNum, '合适的空间裂缝')
                    if (weightNum != 100) {
                      weightSum = false
                    }
                  }
                })
              }
              var quotaSettingDimensionQuotaBOList = []
              this.$refs.assessmentForm.$refs.form.forEach(item => {
                item.validate(sValid => {
                  if (!(sValid && valid && tableFlag && weightSum)) {
                    sValidFlag = false
                    if (flag) {
                      const message = !tableFlag ? '至少添加一条指标项' : !weightSum ? '指标权重总和需等于100%' : '请完善信息'
                      this.$message.error(message)
                      flag = false
                    }
                  }
                })
              })

              if (valid && tableFlag && weightSum && sValidFlag) {
                this.assessmentDimensionList.forEach((item, index) => {
                  quotaSettingDimensionQuotaBOList.push({
                    ...item,
                    quotaSettingDimensionQuotaBOList: this.$refs.assessmentForm.tableForm['table' + item.key].filter(item => Object.keys(item).length)
                  })
                })
                this.tagetForm.quotaSettingDimensionBOList = quotaSettingDimensionQuotaBOList

                this.$emit('pass', val.next, val.tab, val.type)
              }
            } else if (!this.assessmentDimensionList.length) {
              this.$message.error('至少添加一条考核维度')
            } else {
              this.$message.error('请完善信息')
            }
          })
        }
      },
      deep: true
    },
    templateId(val) {
      if (val && ((this.flag && this.status == 2 && this.status) || !this.status)) {
        this.setTemplate(val)
      }
    }
  },

  created() {
    this.getTypeList()
    this.getField()
  },

  methods: {
    getField() {
      this.fields = [
        { name: '总分计算', fieldName: 'scoreCalculation', field: 'scoreCalculation', formType: 'radioType', value: '加权计算', setting: ['加权计算'], isNull: 1, stylePercent: 100 },
        { name: '评分上限类型', fieldName: 'upperLimitType', field: 'upperLimitType', formType: 'select', setting: [{ label: '统一上限', value: 1 }], isNull: 1, value: 1, stylePercent: 100 },
        { name: '评分上限', fieldName: 'upperLimitScore', field: 'upperLimitScore', formType: 'integer', value: 100, min: 0, isNull: 1, stylePercent: 100 }
      ]

      this.examineFields = [
        { name: '维度名称', fieldName: 'dimensionName', field: 'dimensionName', formType: 'text', isNull: 1, stylePercent: 50 },
        { name: '指标类型', fieldName: 'quotaType', field: 'quotaType', formType: 'select', isNull: 1, setting: this.quoteTypeList, value: 1, stylePercent: 50 },
        { name: '维度权重', fieldName: 'dimensionWeight', field: 'dimensionWeight', formType: 'percent', isNull: 1, max: 100, min: 0, stylePercent: 50 },
        { name: '备注', fieldName: 'remarks', field: 'remarks', formType: 'textarea', maxlength: 200, stylePercent: 50 },
        { name: '', fieldName: 'isAllowEdit', field: 'isAllowEdit', formType: 'check' }
      ]

      this.examineFieldsForm = {
        quotaType: 1
      }
      if (this.tagetForm.quotaSettingDimensionBOList && this.tagetForm.quotaSettingDimensionBOList.length) {
        this.tagetForm.quotaSettingDimensionBOList.forEach((item, index) => {
          this.quotaItem['table' + (+new Date() - index)] = []
          this.assessmentDimensionList.push({
            dimensionName: item.dimensionName,
            dimensionWeight: item.dimensionWeight,
            isAllowEdit: item.isAllowEdit,
            quotaType: item.quotaType,
            remarks: item.remarks,
            key: +new Date() - index
          })
          this.quotaItem['table' + (+new Date() - index)].push(...item.quotaSettingDimensionQuotaBOList)
        })

        for (var sKey in this.quotaItem) {
          this.quotaItem[sKey].push({})
        }

        this.flag = true
      }

      this.fields.forEach(item => {
        this.fieldsRules[item.fieldName] = this.getRules(item)
      })

      this.examineFields.forEach(item => {
        this.examineFieldsRules[item.fieldName] = this.getRules(item)
      })
      this.examineFieldsRules.dimensionName.push({ max: 50, message: '最多输入50个汉字、英文、数字', trigger: ['blur', 'change'] })
    },

    /**
     * 根据基本设置选择模板设置指标模板
     */
    setTemplate(templateId) {
      this.assessmentDimensionList = []
      this.quotaItem = {}
      informationAPI(templateId)
        .then(res => {
          const data = objDeepCopy(res.data) || {}
          this.tagetForm.scoreCalculation = data.scoreCalculation
          this.tagetForm.upperLimitScore = data.upperLimitScore
          this.tagetForm.upperLimitType = data.upperLimitType

          data.dimensionVOList.forEach((item, index) => {
            this.quotaItem['table' + (item.templateId + index)] = []
            this.assessmentDimensionList.push({
              dimensionName: item.dimensionName,
              dimensionWeight: item.dimensionWeight,
              isAllowEdit: item.isAllowEdit,
              quotaType: item.quotaType,
              remarks: item.remarks,
              key: item.templateId + index
            })
            this.quotaItem['table' + (item.templateId + index)].push(...item.achievementsQuotaVOList)
          })
          for (var sKey in this.quotaItem) {
            this.quotaItem[sKey].push({})
          }
        })
    },

    /**
     * 获取指标类型
     */
    getTypeList() {
      quoteTypeListAPI()
        .then(res => {
          res.data.forEach(item => {
            this.quoteTypeList.push({ label: item.quoteTypeName, value: item.quoteType })
          })
        })
    },

    /**
     * 新增考核维度
     */
    examineCreate() {
      this.$refs.examineForm.validate(valid => {
        if (valid) {
          if (this.examineType == 'update') {
            this.assessmentDimensionList.splice(this.examineIndex, 1, JSON.parse(JSON.stringify(this.examineFieldsForm)))
            this.examineShow = false
            this.examineType = 'create'
          } else {
            this.examineFieldsForm.key = +new Date()
            this.assessmentDimensionList.push(this.examineFieldsForm)
            this.examineShow = false
          }
        } else {
          this.$message.error('请完善考核维度信息')
        }
      })
    },

    /**
     * 关闭新增考核维度
     */
    close() {
      this.examineShow = false
    },

    /**
     * 编辑考核维度
     */
    editAssess(index) {
      this.examineShow = true
      this.examineIndex = index
      this.examineType = 'update'
      this.examineFieldsForm = JSON.parse(JSON.stringify(this.assessmentDimensionList[index]))
    }
  }
}
</script>
<style lang="scss" scoped>
.base-info-set {
  width: 100%;
  height: calc(100% - 60px);

  .form-sections {
    padding: 0 70% 0 0;
  }

  .other {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 12px 0;
  }
}

.wk-approve-flow-wrap {
  top: 60px;
}
</style>
