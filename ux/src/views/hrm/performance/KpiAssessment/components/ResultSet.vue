<template>
  <el-card
    v-loading="loading"
    style="padding: 0 20px; overflow: auto;"
    :body-style="{ height: '100%' }"
    class="base-info-set">
    <el-form
      ref="resultForm"
      :model="resultForm"
      :rules="baseRules">
      <el-form-item>
        <el-select
          v-model="resultForm.resultTemplateId"

          :disabled="disabled"
          @change="getResultTemplateList">
          <el-option
            v-for="item in resultTemplateOption"
            :key="item.value"
            :label="item.label"
            :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <div style="display: flex; align-items: center;">
          <span class="moneny-title">同步到薪资</span>
          <el-switch
            v-model="resultForm.syncToSalary"
            :disabled="disabled"
            style="height: 30px;"
            active-color="#00875A"
            inactive-color="#7f7f7f" />
        </div>
      </el-form-item>

      <el-form-item
        v-if="resultForm.syncToSalary"
        label="参与计薪月份"
        prop="paidForMonth"
        :rules="{ required: true, message: '请选择', trigger: 'change' }">
        <el-date-picker
          v-model="resultForm['paidForMonth']"
          :disabled="disabled"
          type="month"
          value-format="yyyy-MM"
          placeholder="请选择" />
      </el-form-item>

      <div class="other-info-section">
        <el-form-item prop="appraisalPlanResultSettingLevelList">
          <flexbox
            v-for="(scoreLevel, sIndex) in resultForm.appraisalPlanResultSettingLevelList"
            :key="sIndex"
            class="weight-data-row">
            <div class="column">
              <div v-if="sIndex == 0" class="label">等级</div>
              <el-form-item
                :prop="`appraisalPlanResultSettingLevelList[${sIndex}].levelName`"
                :rules="{
                  required: true,
                  message: '请输入',
                  trigger: 'change',
                }"
                label="">
                <el-input
                  v-model="scoreLevel.levelName"
                  :disabled="disabled"
                  style="width: 200px;"
                  type="text" />
              </el-form-item>
            </div>
            <div class="column">
              <div v-if="sIndex == 0" class="label">分数范围</div>
              <flexbox class="flex-box">
                <el-form-item
                  :prop="`appraisalPlanResultSettingLevelList[${sIndex}].scoreLowerLimit`"
                  :rules="getScoreRules(scoreLevel, sIndex, 'min')"
                  label="">
                  <el-input
                    v-model="scoreLevel.scoreLowerLimit"
                    v-wk-number="'positiveInt'"
                    :disabled="disabled"
                    style="width: 200px; height: 34px;"
                    type="text"
                    @blur="inputBlurValidate('score', sIndex, 'min')">
                    <template slot="append">分</template>
                  </el-input>
                </el-form-item>
                <div class="range">~</div>
                <el-form-item
                  :prop="`appraisalPlanResultSettingLevelList[${sIndex}].scoreUpperLimit`"
                  :rules="getScoreRules(scoreLevel, sIndex, 'max')"
                  label="">
                  <el-input
                    v-model="scoreLevel.scoreUpperLimit"
                    v-wk-number="'positiveInt'"
                    :disabled="disabled"
                    style="width: 200px; height: 34px;"
                    type="text"
                    @blur="inputBlurValidate('score', sIndex, 'max')">
                    <template slot="append">分</template>
                  </el-input>
                </el-form-item>
              </flexbox>
            </div>
            <div v-if="resultForm.syncToSalary" class="column input-number">
              <div v-if="sIndex == 0" class="label">系数</div>
              <flexbox>
                <el-form-item
                  :prop="`appraisalPlanResultSettingLevelList[${sIndex}].coefficient`"
                  :rules="[
                    {
                      required: true,
                      message: '请输入',
                      trigger: 'blur',
                    },
                    {
                      pattern:
                        /(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/,
                      message: '请输入正确的格式,可保留两位小数',
                    },
                  ]"
                  label="">
                  <el-input
                    v-model="scoreLevel.coefficient"
                    style="width: 200px;"
                    :disabled="disabled"
                    type="number" />
                </el-form-item>
              </flexbox>
            </div>
            <i
              v-if="!disabled"
              :class="{ 'is-first': sIndex == 0 }"
              class="el-icon-remove"
              @click="deleteItemClick(resultForm.appraisalPlanResultSettingLevelList, sIndex)" />
          </flexbox>
          <div class="add-bar">
            <el-button
              icon="el-icon-plus"
              type="text"
              :disabled="disabled"
              @click="addScoreLevelClick">
              添加考核等级
            </el-button>
          </div>
        </el-form-item>
      </div>
    </el-form>
  </el-card>
</template>

<script type="text/javascript">
import {
  hrmAchievementsResultTemplateAPI,
  ResultTemplateInformation
} from '@/api/hrm/achievementsResultTemplate'

import NP from 'number-precision'

export default {
  name: 'ResultSet',
  components: {
  },

  inject: {
    disabled: {
      default: false
    }
  },
  props: {
    resultForm: Object,
    verify: Object,
    resultTemplateId: String,
    status: [String, Number]
  },
  data() {
    return {
      loading: false,

      resultTemplateOption: [],
      baseRules: {
        resultTemplateName: { required: true, message: '不能为空', trigger: ['blur', 'change'] },
        appraisalPlanResultSettingLevelList: { required: true, message: '请选择', trigger: ['blur', 'change'] }
      }
    }
  },

  watch: {
    'resultForm.salary': {
      handler(val) {
        if (!val) {
          this.$set(this.resultForm, 'PaidForMonth', '')
        }
      }
    },
    verify: {
      handler(val) {
        if (val.tab == 'result' && !val.pass) {
          this.$refs.resultForm.validate((valid) => {
            if (valid) {
              this.$emit('pass', val.next, val.tab, val.type)
            } else {
              this.$message.error('请完善表单信息')
            }
          })
        }
      },
      deep: true
    }
  },

  created() {
    this.getResultList()
  },

  methods: {
    /**
     * 获取考核结果设置列表
     */
    getResultList() {
      hrmAchievementsResultTemplateAPI()
        .then(res => {
          const data = res.data.list || []
          data.forEach(item => {
            this.resultTemplateOption.push({ label: item.resultTemplateName, value: item.resultTemplateId })
          })
          if (this.resultTemplateId && this.resultTemplateOption.some(o => o.value == this.resultTemplateId)) {
            this.$set(this.resultForm, 'resultTemplateId', this.resultTemplateOption[0].value)
          } else { 
            this.$set(this.resultForm, 'resultTemplateId', '')
          }

          if (!this.status && this.resultForm.resultTemplateId) this.getResultTemplateList(this.resultForm.resultTemplateId)
        })
    },

    /**
     * 获取结果模板对应的值
     */
    getResultTemplateList(resultTemplateId) {
      this.loading = true
      ResultTemplateInformation(resultTemplateId)
        .then(res => {
          this.$set(this.resultForm, 'appraisalPlanResultSettingLevelList', res.data.resultTemplateLevelBOList)
          this.loading = false
        })
    },

    // 删除评测
    deleteItemClick(list, index) {
      list.splice(index, 1)
    },

    /**
     * 增加评测
     */
    addScoreLevelClick() {
      this.resultForm.appraisalPlanResultSettingLevelList.push({
        levelName: ''
      })
    },

    /**
     * 获取比例规则
     */
    getProportionRules(data, index, type) {
      const validateWeight = (rule, value, callback) => {
        if (value < 0 || value > 100 || value === null || value === undefined || value === '') {
          callback(new Error('范围在0-100之间'))
        } else {
          if (parseInt(rule.data.maxNum || 0) < parseInt(rule.data.minNum || 0)) {
            callback(new Error('下限值不能大于上限值'))
          } else if (this.resultForm.appraisalPlanResultSettingLevelList.length) {
            let min = 0
            let max = 0
            this.resultForm.appraisalPlanResultSettingLevelList.forEach(item => {
              min = NP.plus(min, item.minNum)
              max = NP.plus(max, item.maxNum)
            })
            if (min > 100 && rule.type == 'min') {
              if (rule.index == this.resultForm.appraisalPlanResultSettingLevelList.length - 1) {
                callback(new Error('下限值之和不能大于100'))
              }
            } else if (max < 100 && rule.type == 'max') {
              if (rule.index == this.resultForm.appraisalPlanResultSettingLevelList.length - 1) {
                callback(new Error('上限值应大于等于100'))
              }
            } else {
              callback()
            }
          }
        }
      }
      return { validator: validateWeight, trigger: ['blur'], data, index, type }
    },

    /**
     * 获取分数规则
     */
    getScoreRules(data, index, type) {
      const validateWeight = (rule, value, callback) => {
        if (value < 0 || value > 100 || value === null || value === undefined || value === '') {
          callback(new Error(`范围在0-100之间`))
        } else {
          if (parseInt(rule.data.scoreUpperLimit || 0) < parseInt(rule.data.scoreLowerLimit || 0)) {
            callback(new Error('下限值不能大于上限值'))
          } else {
            if (rule.index > 0) {
              const preItem = this.resultForm.appraisalPlanResultSettingLevelList[rule.index - 1]
              if (parseInt(preItem.scoreLowerLimit || 0) <= parseInt(value || 0)) {
                callback(new Error('须小于上一等级下限值'))
                return
              }
            }
            callback()
          }
        }
      }
      return { validator: validateWeight, trigger: ['blur'], data, index, type }
    },

    /**
     * 分数 比例失去焦点 验证准确
     */
    inputBlurValidate(handleType, index, type) {
      if (handleType == 'num') {
        this.$refs.resultForm.validateField(`appraisalPlanResultSettingLevelList[${index}].${type == 'min' ? 'maxNum' : 'minNum'}`)
      } else if (handleType == 'score') {
        this.$refs.resultForm.validateField(`appraisalPlanResultSettingLevelList[${index}].${type == 'min' ? 'maxScore' : 'minScore'}`)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.base-info-set {
  width: 100%;
  height: calc(100% - 60px);

  .other {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 12px 0;
  }

  .moneny-title {
    margin-right: 10px;
    font-size: 22px;
  }

  .weight-data-row {
    padding-bottom: 20px;

    .input-number {
      ::v-deep input {
        &::-webkit-outer-spin-button,
        &::-webkit-inner-spin-button {
          -webkit-appearance: none;
        }

        &[type="number"] {
          -moz-appearance: textfield;
        }
      }
    }

    .column + .column {
      margin-left: 20px;
    }

    .flex-box {
      height: 34px;

      ::v-deep input {
        height: 34px;
      }
    }

    .range {
      margin: 0 8px 10px;
    }

    .el-input {
      width: 200px;
    }

    .el-icon-remove {
      display: none;

      // margin-bottom: 10px;
      margin-left: 8px;
      color: #ff6767;
      cursor: pointer;

      &.is-first {
        margin-top: 40px;
        margin-bottom: 0;
      }
    }

    &:hover {
      .el-icon-remove {
        display: inline-block;
      }
    }
  }

  .wk-help-tips {
    margin-left: 3px;
  }

  .add-bar {
    line-height: initial;

    .el-button {
      padding-top: 0;
    }
  }

  .wk-text-tips {
    font-size: 12px;
    color: #ccc;
  }

  .el-button--text {
    color: #0052cc;

    &.is-disabled {
      color: #a5adba;
    }
  }
}
</style>
