<template>
  <transition name="opacity-fade">
    <div class="business-approve-flow-create">
      <wk-backgroud-tabs>
        <template slot="left">
          <span>{{ title }}KPI指标模板</span>
        </template>
        <template slot="right">
          <el-button type="primary" @click="sendClick">保存</el-button>
          <i class="el-icon-close create-close" @click="closeClick" />
        </template>
      </wk-backgroud-tabs>

      <el-card
        v-loading="loading"
        style="padding: 0 20px;overflow: auto;"
        class="base-info-set">
        <create-sections title="基本信息" class="form-sections">
          <el-form
            ref="baseInfoSet"
            :model="fieldsForm"
            label-position="top"
            :rules="fieldsRules">
            <wk-form-items
              :field-from="fieldsForm"
              :field-list="fields">
              <template slot-scope="{ data }">
                <el-radio
                  v-if="data && data.formType == 'radioType'"
                  v-model="fieldsForm[data.fieldName]"
                  :label="1">
                  加权计算
                  <span style="font-size: 12px;color: #5e6c84;">总分=评分 X 维度权重 X 指标权重，再累加</span>
                </el-radio>
                <el-input
                  v-if="data && data.formType == 'integer'"
                  v-model="fieldsForm[data.fieldName]"
                  placeholder=""
                  @input="fieldsForm[data.fieldName]=fieldsForm[data.fieldName].replace(/[^\d]/g,'')"
                />
              </template>
            </wk-form-items>
          </el-form>
        </create-sections>

        <create-sections title="考核维度">
          <div class="other">
            <span>
              当前维度权重合计:
              <span :style="dimensionWeightSum > 100 ? 'color:red' : 'color:#ffb55f'">
                {{ dimensionWeightSum }}<span v-if="dimensionWeightSum != '--'">%</span>
              </span>
            </span>
            <el-button
              type="primary"
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
        </create-sections>

      </el-card>

      <el-dialog
        v-if="examineShow"
        :title="examineType == 'create'? '新建考核维度' : '编辑考核维度'"
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
    </div>
  </transition>
</template>

<script type="text/javascript">
import {
  createAssessmentTemplateAPI,
  updateTemplateAPI,
  quoteTypeListAPI } from '@/api/hrm/achievementsAssessmentTemplate'

import WkFormItems from '@/components/NewCom/WkForm/WkFormItems'
import CreateSections from '@/components/CreateSections'
import WkBackgroudTabs from '@/views/admin/examine/components/WkBackgroudTabs'
import AssessmentDimension from '../../components/AssessmentDimension'

import GenerateRulesMixin from '@/components/NewCom/WkForm/GenerateRules'
import CustomFieldsMixin from '@/mixins/CustomFields'

export default {
  name: 'KpiTemplateCreate',
  components: {
    WkBackgroudTabs,
    WkFormItems,
    AssessmentDimension,
    CreateSections
  },
  mixins: [GenerateRulesMixin, CustomFieldsMixin],

  props: {
    indicatorTemplate: Object
  },
  data() {
    return {
      loading: false,
      height: document.documentElement.clientHeight - 100,

      fields: [],
      fieldsForm: {},
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
      examineIndex: undefined
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
    },
    title() {
      return this.fieldsForm.templateId ? '编辑' : '新建'
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
    }
  },

  created() {
    this.getTypeList()
    this.getField()
  },

  methods: {
    getField() {
      this.fields = [
        { name: '考核模板名称', fieldName: 'templateName', field: 'templateName', formType: 'text', isNull: 1, stylePercent: 100 },
        { name: '考核指标说明', fieldName: 'templateIllustrate', field: 'templateIllustrate', formType: 'textarea', stylePercent: 100 },
        { name: '总分计算', fieldName: 'scoreCalculation', field: 'scoreCalculation', formType: 'radioType', value: '1', setting: ['加权计算'], isNull: 1, stylePercent: 100 },
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

      if (Object.keys(this.indicatorTemplate).length) {
        this.loading = true
        // 编辑赋值
        const fieldsFormList = {}
        for (var key in this.indicatorTemplate) {
          this.fields.forEach(item => {
            if (key == item.fieldName) {
              fieldsFormList[item.fieldName] = this.indicatorTemplate[key]
              fieldsFormList['templateId'] = this.indicatorTemplate.templateId
            }
          })
        }
        this.fieldsForm = fieldsFormList

        this.indicatorTemplate['dimensionVOList'].forEach((item, index) => {
          this.quotaItem['table' + item.templateId + index ] = []
          this.assessmentDimensionList.push({
            dimensionName: item.dimensionName,
            quotaType: item.quotaType,
            dimensionWeight: item.dimensionWeight,
            remarks: item.remarks,
            isAllowEdit: item.isAllowEdit,
            key: item.templateId + index
          })

          this.quotaItem['table' + item.templateId + index].push(...item.achievementsQuotaVOList)
        })

        for (var sKey in this.quotaItem) {
          this.quotaItem[sKey].push({})
        }
        this.loading = false
      } else {
        this.fieldsForm = {
          scoreCalculation: 1,
          upperLimitType: 1,
          upperLimitScore: 100
        }

        this.examineFieldsForm = {
          quotaType: 1
        }
      }

      this.fields.forEach(item => {
        this.fieldsRules[item.fieldName] = this.getRules(item)
      })

      this.examineFields.forEach(item => {
        this.examineFieldsRules[item.fieldName] = this.getRules(item)
      })
      this.fieldsRules.templateIllustrate.push({ max: 200, message: '最多输入200个字符', trigger: 'change' })
      this.fieldsRules.templateName.push({ max: 50, message: '最多输入50个汉字、英文、数字', trigger: ['blur', 'change'] })
      this.examineFieldsRules.dimensionName.push({ max: 50, message: '最多输入50个汉字、英文、数字', trigger: ['blur', 'change'] })
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
            // if (item.key == this.examineFieldsForm.key) {
            //   this.examineFieldsForm.type = 'update'
            //   this.assessmentDimensionList.splice(index, 1, JSON.parse(JSON.stringify(this.examineFieldsForm)))
            //   this.examineShow = false
            // } else {
            //   this.examineFieldsForm.key = +new Date()
            //   this.assessmentDimensionList.push(this.examineFieldsForm)
            //   this.examineShow = false
            // }
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
    },

    /**
     * 发布点击
     */
    sendClick() {
      var flag = true
      var tableFlag = true
      var weightSum = true
      var sValidFlag = true

      this.$refs.baseInfoSet.validate(valid => {
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
          var achievementsDimensionBOList = []
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
              achievementsDimensionBOList.push({
                ...item,
                achievementsPointBOList: this.$refs.assessmentForm.tableForm['table' + item.key].filter(item => Object.keys(item).length)
              })
            })

            this.submiteRequest({ ...this.fieldsForm, ...{ achievementsDimensionBOList }})
          }
        } else {
          if (valid) {
            this.submiteRequest({ ...this.fieldsForm })
          } else {
            this.$message.error('请完善信息')
          }
        }
      })
    },

    /**
     * 发送请求
     */
    submiteRequest(params) {
      this.loading = true
      const request = params.templateId ? updateTemplateAPI : createAssessmentTemplateAPI
      const message = params.templateId ? '修改成功' : '创建成功'
      request(params)
        .then(res => {
          this.loading = false
          this.$message.success(message)
          this.closeClick()
          this.$emit('createEditSuccess')
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 关闭
     */
    closeClick() {
      this.$emit('close')
    }
  }
}
</script>
<style lang="scss" scoped>
::v-deep .drawer {
  .el-drawer__header {
    height: 60px;
    padding: 0 15px;
    font-size: 18px;
    font-weight: bold;
    background: #f7f8fa;
    border-bottom: 1px solid $--border-color-base;

    .el-icon-close {
      font-size: 24px;
      color: #909399;
      cursor: pointer;
    }

    .el-icon-close:hover {
      color: $--color-primary;
    }
  }

  .drawer__footer {
    position: absolute;
    bottom: 40px;
    padding: 12px 12px 0;
  }
}

.opacity-fade-enter-active,
.opacity-fade-leave-active {
  transition: all 0.2s;
}

.opacity-fade-enter,
.opacity-fade-leave-to {
  opacity: 0;
}

.business-approve-flow-create {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 10;
  background-color: #f5f5f7;

  .create-close {
    display: block;
    padding: 10px;
    font-size: 24px;
    color: #909399;
    cursor: pointer;
  }

  .create-close:hover {
    color: $--color-primary;
  }

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
}
</style>
