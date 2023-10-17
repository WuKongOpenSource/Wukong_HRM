<template>
  <el-card
    style="padding: 0 20px; overflow: auto;"
    :body-style="{ height: '100%' }"
    class="base-info-set">
    <el-form
      ref="form"
      :model="flowForm"
      label-position="top">
      <create-sections title="考核范围及考核指标" />
      <el-form-item
        v-for="(item,index) in flowFormFieldList"
        :key="index"
        :label="item.label"
        :rules="item.rules">
        <el-radio
          v-if="item.fieldName == 'quotaSettingType'"
          v-model="flowForm[item.fieldName]"
          :disabled="disabled"
          :label="1"
          @change="radioChange">系统制定</el-radio>
        <el-radio
          v-if="item.fieldName == 'quotaSettingType'"
          v-model="flowForm[item.fieldName]"
          :disabled="disabled"
          :label="2">员工填写</el-radio>

        <div v-if="flowForm.quotaSettingType == 2 && item.fieldName == 'targetConfirmation'">
          <div style="display: flex;align-items: center;">
            <create-sections title="目标确认" style="margin-right: 10px;" />
            <el-switch
              v-model="flowForm[item.fieldName]"
              :disabled="disabled"
              active-color="#13ce66"
              inactive-color="#ff4949"
              @change="targetConfirmationChange" />
          </div>

          <el-form-item
            v-if="flowForm['targetConfirmation']"
            label="目标确认人"
            :rules="{ required: true }">
            <div style="display: flex;align-items: center;">
              <el-form-item
                prop="processSettingTargetConfirmationBO[confirmationType]"
                :rules="{ required: true, message: '请选择', trigger: 'change' }">
                <el-select
                  v-model="flowForm.processSettingTargetConfirmationBO.confirmationType"
                  :disabled="disabled"
                  @change="confirmationTypeChange">
                  <el-option
                    v-for="sItem in scorerOptions"
                    :key="sItem.value"
                    :label="sItem.label"
                    :value="sItem.value" />
                </el-select>
              </el-form-item>
              <el-form-item
                v-if="flowForm.processSettingTargetConfirmationBO.confirmationType == 1 || flowForm.processSettingTargetConfirmationBO.confirmationType == 2"
                prop="processSettingTargetConfirmationBO[confirmationLevel]"
                :rules="{ required: true, message: '请选择', trigger: 'change' }">
                <el-select
                  v-if="flowForm.processSettingTargetConfirmationBO.confirmationType == 1"
                  v-model="flowForm.processSettingTargetConfirmationBO.confirmationLevel"
                  :disabled="disabled">
                  <el-option
                    v-for="sItem in superiorOptions"
                    :key="sItem.value"
                    :label="sItem.label"
                    :value="sItem.value" />
                </el-select>

                <el-select
                  v-if="flowForm.processSettingTargetConfirmationBO.confirmationType == 2"
                  v-model="flowForm.processSettingTargetConfirmationBO.confirmationLevel"
                  :disabled="disabled">
                  <el-option
                    v-for="sItem in departmentOptions"
                    :key="sItem.value"
                    :label="sItem.label"
                    :value="sItem.value" />
                </el-select>
              </el-form-item>
              <el-form-item
                v-if="flowForm.processSettingTargetConfirmationBO.confirmationType == 3"
                prop="processSettingTargetConfirmationBO[identifyingPeople]"
                :rules="{ required: true, message: '请选择', trigger: 'change' }">
                <wk-user-select
                  v-model="flowForm.processSettingTargetConfirmationBO.identifyingPeople"
                  :disabled="disabled"
                  style="height: 32px;margin-left: 10px;"
                  :props="{dataType: 'hrm',label: 'employeeName',value:'employeeId',isKpi:true}"
                  :radio="true" />
              </el-form-item>
            </div>
          </el-form-item>
        </div>

        <div v-if="item.fieldName == 'processSettingScoringBOList'">
          <create-sections title="考核评分流程" />
          <grading-process
            ref="grading"
            :type="flowForm.quotaSettingType"
            :process-setting-scoring-b-o-list="flowForm.processSettingScoringBOList" />
        </div>

        <div v-if="item.fieldName == 'resultAudit'">
          <div style="display: flex;align-items: center;">
            <create-sections title="结果审核" style="margin-right: 10px;" />
            <el-switch
              v-model="flowForm[item.fieldName]"
              :disabled="disabled"
              active-color="#13ce66"
              inactive-color="#ff4949"
              @change="resultAuditChange" />
          </div>
          <div v-if="flowForm[item.fieldName]" style="padding-right: 73%;">
            <p style="color: #6b778c;">确认说明：确认驳回后重新提交后跳过已确认的层级，返回拒绝的层级</p>
            <div
              v-for="(sItem,sIndex) in flowForm['processSettingResultAuditBOList']"
              :key="sIndex">
              <div>
                <span>第{{ sIndex+1 == 1? '一' : sIndex+1 == 2? '二' : '三' }}级</span>
                <span class="level">选择员工<span style="color: #ff7330;">*</span></span>
              </div>
              <div class="results-level">
                <el-form-item
                  :prop="`processSettingResultAuditBOList[${sIndex}].auditType`"
                  :rules="{ required: true, message: '请选择', trigger: 'change' }">
                  <el-select
                    v-model="flowForm['processSettingResultAuditBOList'][sIndex].auditType"
                    :disabled="disabled"
                    @change="resultsLevelChange($event,sIndex)">
                    <el-option
                      v-for="(child,childIndex) in resultsAudit"
                      :key="childIndex"
                      :disabled="child.disabled"
                      :label="child.label"
                      :value="child.value" />
                  </el-select>
                </el-form-item>

                <el-form-item
                  v-if="flowForm['processSettingResultAuditBOList'][sIndex].auditType == 1 || flowForm['processSettingResultAuditBOList'][sIndex].auditType == 2"
                  :prop="`processSettingResultAuditBOList[${sIndex}].level`"
                  :rules="{ required: true, message: '请选择', trigger: 'change' }">
                  <el-select
                    v-if="flowForm['processSettingResultAuditBOList'][sIndex].auditType == 1"
                    v-model="flowForm['processSettingResultAuditBOList'][sIndex].level"
                    :disabled="disabled">
                    <el-option
                      v-for="(chidld,childIndex) in superiorOptions"
                      :key="childIndex"
                      :label="chidld.label"
                      :value="chidld.value" />
                  </el-select>

                  <el-select
                    v-if="flowForm['processSettingResultAuditBOList'][sIndex].auditType == 2"
                    v-model="flowForm['processSettingResultAuditBOList'][sIndex].level"
                    :disabled="disabled"
                  >
                    <el-option
                      v-for="(chidld,childIndex) in departmentOptions"
                      :key="childIndex"
                      :label="chidld.label"
                      :value="chidld.value" />
                  </el-select>
                </el-form-item>

                <el-form-item
                  v-if="flowForm['processSettingResultAuditBOList'][sIndex].auditType == 3"
                  :prop="`processSettingResultAuditBOList[${sIndex}].designatedPerson`"
                  :rules="{ required: true, message: '请选择', trigger: 'change' }">
                  <wk-user-select
                    v-model="flowForm['processSettingResultAuditBOList'][sIndex].designatedPerson"
                    :disabled="disabled"
                    style="height: 32px;"
                    :props="{dataType: 'hrm',label: 'employeeName',value:'employeeId',isKpi:true}"
                    :radio="true" />
                </el-form-item>

                <i
                  v-if="flowForm['processSettingResultAuditBOList'][sIndex].auditType && !disabled"
                  class="wk wk-close"
                  style="color: #97a0af;cursor: pointer;"
                  @click="handleClose(sIndex,'audit')" />
              </div>
            </div>
            <el-button
              :disabled="flowForm['processSettingResultAuditBOList'].length == 3 || disabled"
              type="text"
              style="margin-top: 10px;"
              @click="addResultsAudit">+新增审核节点</el-button>
          </div>
        </div>

        <div v-if="item.fieldName == 'resultConfirmation'">
          <div style="display: flex;align-items: center;">
            <create-sections title="结果确认" style="margin-right: 10px;" />
            <el-switch
              v-model="flowForm[item.fieldName]"
              :disabled="disabled"
              active-color="#13ce66"
              inactive-color="#ff4949"
              @change="appealListChange" />
          </div>
          <div v-if="flowForm[item.fieldName]" style="padding-right: 73%;">
            <p style="color: #6b778c;">确认说明：保证结果的公正性，员工可发起结果确认申诉，确认人为被考核人</p>

            <el-form-item
              style="margin-bottom: 10px;"
              label="超期未审核"
              :rules="{required:true}">
              <div style="display: flex;">
                <el-form-item
                  prop="overdueDays"
                  :rules="{required:true, message: '未审核天数不能为空', trigger: ['change','blur'] }">
                  <div style="display: flex;">
                    <span style="margin-right: 10px;">超</span>
                    <el-input-number
                      v-model="flowForm['overdueDays']"
                      :disabled="disabled"
                      style="width: 60px;"
                      :min="1"
                      :max="100"
                      :controls="false" />
                    <span>天</span>
                  </div>
                </el-form-item>
                <el-form-item
                  style="margin-left: 15px;"
                  prop="beOverdueType"
                  :rules="{required:true, message: '请选择', trigger: 'change' }">
                  <el-select
                    v-model="flowForm['beOverdueType']"
                    :disabled="disabled">
                    <el-option
                      v-for="sItem in [{ label:'未审批拒绝',value:1},{ label:'未审批通过',value:2}]"
                      :key="sItem.value"
                      :label="sItem.label"
                      :value="sItem.value" />
                  </el-select>
                </el-form-item>
              </div>
            </el-form-item>

            <div
              v-for="(sItem,sIndex) in flowForm['processSettingResultConfirmationBOList']"
              :key="sIndex">
              <div>
                <span>第{{ sIndex+1 == 1? '一' : sIndex+1 == 2? '二' : '三' }}级</span>
                <span class="level">选择员工<span style="color: #ff7330;">*</span></span>
              </div>
              <div class="results-level">
                <el-form-item
                  :prop="`processSettingResultConfirmationBOList[${sIndex}].confirmationType`"
                  :rules="{ required: true, message: '请选择', trigger: 'change' }">
                  <el-select
                    v-model="flowForm['processSettingResultConfirmationBOList'][sIndex].confirmationType"
                    :disabled="disabled"
                    @change="appealLevelChange($event,sIndex)">
                    <el-option
                      v-for="(child,childIndex) in appealList"
                      :key="childIndex"
                      :disabled="child.disabled"
                      :label="child.label"
                      :value="child.value" />
                  </el-select>
                </el-form-item>

                <el-form-item
                  v-if="flowForm['processSettingResultConfirmationBOList'][sIndex].confirmationType == 1 || flowForm['processSettingResultConfirmationBOList'][sIndex].confirmationType == 2"
                  :prop="`processSettingResultConfirmationBOList[${sIndex}].level`"
                  :rules="{ required: true, message: '请选择', trigger: 'change' }">
                  <el-select
                    v-if="flowForm['processSettingResultConfirmationBOList'][sIndex].confirmationType == 1"
                    v-model="flowForm['processSettingResultConfirmationBOList'][sIndex].level"
                    :disabled="disabled">
                    <el-option
                      v-for="(chidld,childIndex) in superiorOptions"
                      :key="childIndex"
                      :label="chidld.label"
                      :value="chidld.value" />
                  </el-select>

                  <el-select
                    v-if="flowForm['processSettingResultConfirmationBOList'][sIndex].confirmationType == 2"
                    v-model="flowForm['processSettingResultConfirmationBOList'][sIndex].level"
                    :disabled="disabled">
                    <el-option
                      v-for="(chidld,childIndex) in departmentOptions"
                      :key="childIndex"
                      :label="chidld.label"
                      :value="chidld.value" />
                  </el-select>
                </el-form-item>

                <el-form-item
                  v-if="flowForm['processSettingResultConfirmationBOList'][sIndex].confirmationType == 3"
                  :prop="`processSettingResultConfirmationBOList[${sIndex}].designatedUserId`"
                  :rules="{ required: true, message: '请选择', trigger: 'change' }">
                  <wk-user-select
                    v-model="flowForm['processSettingResultConfirmationBOList'][sIndex].designatedUserId"
                    style="height: 32px;"
                    :disabled="disabled"
                    :props="{dataType: 'hrm',label: 'employeeName',value:'employeeId',isKpi:true}"
                    :radio="true" />
                </el-form-item>

                <i
                  v-if="flowForm['processSettingResultConfirmationBOList'][sIndex].confirmationType && !disabled"
                  class="wk wk-close"
                  style="color: #97a0af;cursor: pointer;"
                  @click="handleClose(sIndex,'appeal')" />
              </div>
            </div>
            <el-button
              :disabled="flowForm['processSettingResultConfirmationBOList'].length == 3 || disabled"
              type="text"
              style="margin-top: 10px;"
              @click="addAppeal">+新增申诉节点</el-button>
          </div>
        </div>

      </el-form-item>
    </el-form>
  </el-card>
</template>

<script type="text/javascript">
import CreateSections from '@/components/CreateSections'
import GradingProcess from './GradingProcess'
import WkUserSelect from '@/components/NewCom/WkUserSelect'

export default {
  name: 'WorkflowSetting',
  components: {
    CreateSections,
    GradingProcess,
    WkUserSelect
  },
  inject: {
    disabled: {
      default: false
    }
  },
  props: {
    flowForm: Object,
    verify: Object
  },
  data() {
    return {
      flowFormFieldList: [
        { label: '指标制定', prop: 'quotaSettingType', fieldName: 'quotaSettingType', rules: { required: true, message: '请选择', trigger: 'change' }},
        { fieldName: 'targetConfirmation' },
        { fieldName: 'processSettingScoringBOList' },
        { fieldName: 'resultAudit' },
        { fieldName: 'resultConfirmation' }
      ],
      scorerOptions: [
        { label: '上级', value: 1 },
        { label: '部门负责人', value: 2 },
        { label: '指定评分人', value: 3 },
        { label: '被考核人', value: 4 }
      ],

      resultsAudit: [
        { label: '上级', value: 1 },
        { label: '部门负责人', value: 2 },
        { label: '指定评分人', value: 3 }
      ],

      appealList: [
        { label: '上级', value: 1 },
        { label: '部门负责人', value: 2 },
        { label: '指定评分人', value: 3 }
      ],
      superiorOptions: [
        { label: '直属上级', value: 1 },
        { label: '第二级上级', value: 2 },
        { label: '第三级上级', value: 3 },
        { label: '第四级上级', value: 4 },
        { label: '第五级上级', value: 5 },
        { label: '第六级上级', value: 6 },
        { label: '第七级上级', value: 7 },
        { label: '第八级上级', value: 8 },
        { label: '第九级上级', value: 9 },
        { label: '第十级上级', value: 10 }
      ],

      departmentOptions: [
        { label: '直属部门负责人', value: 1 },
        { label: '第二级部门负责人', value: 2 },
        { label: '第三级部门负责人', value: 3 },
        { label: '第四级部门负责人', value: 4 },
        { label: '第五级部门负责人', value: 5 },
        { label: '第六级部门负责人', value: 6 },
        { label: '第七级部门负责人', value: 7 },
        { label: '第八级部门负责人', value: 8 },
        { label: '第九级部门负责人', value: 9 },
        { label: '第十级部门负责人', value: 10 }
      ]
    }
  },

  watch: {
    'flowForm.processSettingResultAuditBOList': {
      handler(val) {
        val.forEach(item => {
          this.resultsAudit.forEach(sItem => {
            if (item.auditType == sItem.value) {
              sItem.disabled = true
            }
          })
        })
      },
      immediate: true,
      deep: true
    },
    'flowForm.resultAudit': {
      handler(val) {
        if (!val) {
          this.resultsAudit.forEach(sItem => {
            sItem.disabled = false
          })
        }
      },
      immediate: true,
      deep: true
    },
    'flowForm.resultConfirmation': {
      handler(val) {
        if (!val) {
          this.appealList.forEach(sItem => {
            sItem.disabled = false
          })
        }
      },
      immediate: true,
      deep: true
    },
    'flowForm.processSettingResultConfirmationBOList': {
      handler(val) {
        val.forEach(item => {
          this.appealList.forEach(sItem => {
            if (item.confirmationType == sItem.value) {
              sItem.disabled = true
            }
          })
        })
      },
      immediate: true,
      deep: true
    },
    verify: {
      handler(val) {
        if (val.tab == 'flow' && !val.pass) {
          this.$refs.form.validate((valid) => {
            if (this.flowForm.processSettingScoringBOList.length != 1) {
              this.$refs.grading[0].$refs.form.validate(sValid => {
                if (sValid && valid) {
                  var sum = 0
                  this.flowForm.processSettingScoringBOList.forEach(item => {
                    if (item.weight) sum += item.weight
                  })
                  if (sum == 100) {
                    this.$emit('pass', val.next, val.tab, val.type)
                  } else {
                    this.$message.error('评分总和需等于100%')
                  }
                } else {
                  this.$message.error('请完善信息')
                }
              })
            } else {
              this.$message.error('请至少设置一个评分人')
            }
          })
        }
      },
      deep: true
    }
  },

  methods: {
    radioChange(val) {
      if (val) {
        this.$set(this.flowForm.processSettingTargetConfirmationBO, 'confirmationType', '')
        this.$set(this.flowForm.processSettingTargetConfirmationBO, 'confirmationLevel', '')
        this.$set(this.flowForm.processSettingTargetConfirmationBO, 'identifyingPeople', '')
        this.$set(this.flowForm, 'targetConfirmation', false)
      }
    },
    /**
     * 考核范围及考核指标变化
     */
    targetConfirmationChange(val) {
      if (!val) {
        this.$set(this.flowForm.processSettingTargetConfirmationBO, 'confirmationType', '')
        this.$set(this.flowForm.processSettingTargetConfirmationBO, 'confirmationLevel', '')
        this.$set(this.flowForm.processSettingTargetConfirmationBO, 'identifyingPeople', '')
      }
    },

    /**
     * 目标确认人类型发生变化
     */
    confirmationTypeChange(val) {
      if (val == 1) {
        this.$set(this.flowForm.processSettingTargetConfirmationBO, 'confirmationLevel', 1)
        this.$set(this.flowForm.processSettingTargetConfirmationBO, 'identifyingPeople', '')
      } else if (val == 2) {
        this.$set(this.flowForm.processSettingTargetConfirmationBO, 'confirmationLevel', 1)
        this.$set(this.flowForm.processSettingTargetConfirmationBO, 'identifyingPeople', '')
      } else if (val == 3) {
        this.$set(this.flowForm.processSettingTargetConfirmationBO, 'identifyingPeople', '')
        this.$set(this.flowForm.processSettingTargetConfirmationBO, 'confirmationLevel', '')
      } else if (val == 4) {
        this.$set(this.flowForm.processSettingTargetConfirmationBO, 'identifyingPeople', '')
        this.$set(this.flowForm.processSettingTargetConfirmationBO, 'confirmationLevel', '')
      }
    },

    /**
     * 结果审核发生变化
     */
    resultAuditChange(val) {
      if (!val) {
        this.$set(this.flowForm, 'processSettingResultAuditBOList', [{ auditType: '' }])
      }
    },

    /**
     * 结果审核级别变化
     */
    resultsLevelChange(val, index) {
      if (val == 1) {
        this.$set(this.flowForm.processSettingResultAuditBOList[index], 'level', 1)
      } else if (val == 2) {
        this.$set(this.flowForm.processSettingResultAuditBOList[index], 'level', 1)
      } else if (val == 3) {
        this.$set(this.flowForm.processSettingResultAuditBOList[index], 'designatedPerson', '')
      }
      this.resultsAudit.forEach(item => {
        if (item.value == val) {
          item.disabled = true
        } else {
          item.disabled = false
        }
      })
    },

    /**
     * 添加结果审核
     */
    addResultsAudit() {
      this.flowForm.processSettingResultAuditBOList.push({ auditType: '' })
    },

    /**
     * 删除结果审核
     */
    handleClose(index, type) {
      if (type == 'audit') {
        if (this.flowForm.processSettingResultAuditBOList.length == 1) {
          this.$message.error('至少存在一个确认人')
        } else {
          this.flowForm.processSettingResultAuditBOList.splice(index, 1)
          this.flowForm.processSettingResultAuditBOList.forEach(item => {
            this.resultsAudit.forEach(sItem => {
              if (item.auditType != sItem.value) {
                sItem.disabled = false
              }
            })
          })
        }
      } else if (type == 'appeal') {
        if (this.flowForm.processSettingResultConfirmationBOList.length == 1) {
          this.$message.error('至少存在一个申诉节点')
        } else {
          this.flowForm.processSettingResultConfirmationBOList.splice(index, 1)
          this.flowForm.processSettingResultConfirmationBOList.forEach(item => {
            this.appealList.forEach(sItem => {
              if (item.confirmationType != sItem.value) {
                sItem.disabled = false
              }
            })
          })
        }
      }
    },

    /**
     * 新增申诉节点
     */
    addAppeal() {
      this.flowForm.processSettingResultConfirmationBOList.push({ confirmationType: '' })
    },

    /**
     * 结果确认发生变化
     */
    appealListChange(val) {
      if (!val) {
        this.$set(this.flowForm, 'processSettingResultConfirmationBOList', [{ confirmationType: '' }])
        this.$set(this.flowForm, 'overdueDays', '')
        this.$set(this.flowForm, 'beOverdueType', '')
      } else {
        this.$set(this.flowForm, 'overdueDays', 2)
        this.$set(this.flowForm, 'beOverdueType', 1)
      }
    },

    /**
     * 结果确认级别变化
     */
    appealLevelChange(val, index) {
      if (val == 1) {
        this.$set(this.flowForm.processSettingResultConfirmationBOList[index], 'level', 1)
      } else if (val == 2) {
        this.$set(this.flowForm.processSettingResultConfirmationBOList[index], 'level', 1)
      } else if (val == 3) {
        this.$set(this.flowForm.processSettingResultConfirmationBOList[index], 'designatedUserId', '')
      }
      this.appealList.forEach(item => {
        if (item.value == val) {
          item.disabled = true
        } else {
          item.disabled = false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.base-info-set {
  width: 100%;
  height: calc(100% - 60px);

  .el-form {
    ::v-deep .el-form-item {
      margin-top: 10px;

      .el-form-item__label {
        padding-bottom: 0;
        line-height: 30px;
      }

      .el-form-item__content {
        line-height: 30px;
      }

      .level {
        margin-left: 5px;
        font-size: 12px;
        color: #5e6c84;
      }

      .results-level {
        display: flex;
        justify-content: space-between;
        margin-left: 45px;

        .el-form-item {
          margin-top: 0 !important;

          &:nth-of-type(2) {
            margin-left: 10px;
          }
        }
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
