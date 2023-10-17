<template>
  <xr-create
    :loading="loading"
    :title="title"
    @close="close"
    @save="saveClick">
    <el-form
      ref="form"
      :model="fieldForm"
      :rules="fieldRules"
      :validate-on-rule-change="false"
      class="wk-form"
      label-position="top">
      <wk-form-items
        v-for="(children, index) in fieldList"
        :key="index"
        :field-from="fieldForm"
        :field-list="children"
        @change="formChange"
      >
        <template slot-scope="{ data }">
          <div
            v-if="data && data.formType == 'personalized'"
            :value="data"
            class="personalized">
            <div>个性设置
              <el-switch
                v-model="fieldForm[data.fieldName].isPersonalization"
                :disabled="personalitySetting"
                :active-value="1"
                :inactive-value="0" />
            </div>
            <div
              v-if="fieldForm[data.fieldName].isPersonalization"
              class="interval-top">每月累计迟到
              <el-input v-model.number="fieldForm[data.fieldName].lateMinutesOrCounts" type="number">
                <span slot="suffix">{{ fieldForm.lateRuleMethod == 1 ? '分钟' : '次' }}</span>
              </el-input> 后不扣款 </div>
            <div
              v-if="fieldForm[data.fieldName].isPersonalization"
              class="interval-top">每月累计早退
              <el-input v-model.number="fieldForm[data.fieldName].earlyMinutesOrCounts" type="number">
                <span slot="suffix">{{ fieldForm.earlyRuleMethod == 1 ? '分钟' : '次' }}</span>
              </el-input> 后不扣款 </div>
          </div>
          <div v-else-if="data && data.formType=='computeMode'">
            <el-input
              v-model.number="fieldForm[data.field]"
              :placeholder="data.placeholder"
              clear="no-number"
              :precision="2"
              type="number">
              <span slot="suffix">{{ data.unit }}</span>
            </el-input>
          </div>
        </template>
      </wk-form-items>
    </el-form>
  </xr-create>
</template>

<script>
import { hrmAddOrSetAttendanceRuleAPI, hrmVerifyAttendanceRuleNameAPI } from '@/api/admin/hrm'
import XrCreate from '@/components/XrCreate'
import WkFormItems from '@/components/NewCom/WkForm/WkFormItems'
import CustomFieldsMixin from '@/mixins/CustomFields'
import fieldListLib from './fieldListLib'
export default {

  components: {
    XrCreate,
    WkFormItems
  },
  mixins: [CustomFieldsMixin],
  props: {
    id: {
      type: [String, Number],
      default: ''
    },
    action: {
      type: Object,
      default: () => {
        return {
          id: '',
          type: 'save',
          data: {}
        }
      }
    }
  },
  data() {
    return {
      loading: false,
      fieldList: [],
      fieldForm: {},
      fieldRules: {},
      baseFields: [],
      personalized: {},
      setectValueMap: {
        lateRuleMethod: {
          1: { inputTips: '扣款=迟到分钟*N元/分钟', unit: '元/分' },
          2: { inputTips: '扣款=迟到次数*N元/次', unit: '元/次' },
          3: { inputTips: '当月出现迟到情况即按N元/月扣款', unit: '元/月' },
          computeMode: 'lateDeductMoney'
        },
        earlyRuleMethod: {
          1: { inputTips: '扣款=早退分钟*N元/分钟', unit: '元/分' },
          2: { inputTips: '扣款=早退次数*N元/次', unit: '元/次' },
          3: { inputTips: '当月出现早退情况即按N元/月扣款', unit: '元/月' },
          computeMode: 'earlyDeductMoney'
        }
      }
    }
  },
  computed: {
    title() {
      return this.id ? '编辑扣款规则' : '新建扣款规则'
    },

    // 个性设置
    personalitySetting() {
      const { lateRuleMethod, earlyRuleMethod } = this.fieldForm
      if (lateRuleMethod == 3 || earlyRuleMethod == 3) {
        return true
      } else {
        return false
      }
    }
  },
  watch: {
    personalized: {
      handler(val, old) {
        this.fieldForm.personalized = val
      },
      immediate: true,
      deep: true
    },

    personalitySetting(val) {
      if (val) {
        this.$set(this.fieldForm, 'personalized', {
          isPersonalization: 0,
          lateMinutesOrCounts: 0,
          earlyMinutesOrCounts: 0
        })
      }
    }
  },
  created() {
    this.getField()
  },
  methods: {
    getField() {
      const baseFields = []
      const fieldList = []
      const fieldRules = {}
      const fieldForm = {}
      fieldListLib.forEach(children => {
        const fields = []
        children.forEach(item => {
          const temp = this.getFormItemDefaultProperty(item)
          if (['absenteeismDeductMoney', 'earlyDeductMoney', 'lateDeductMoney', 'misscardDeductMoney'].includes(temp.field)) {
            fieldRules[temp.field] = [
              { required: true, trigger: 'change', message: '请输入金额' },
              { pattern: /(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/, message: '最多保留两位小数' }
            ]
          } else {
            fieldRules[temp.field] = this.getRules(item)
          }
          fieldForm[temp.field] = this.getItemValue(item, this.action.data, this.action.type)
          if (['lateRuleMethod', 'earlyRuleMethod'].includes(temp.fieldName)) {
            setTimeout(() => {
              this.setInputTipsAndUnit(temp, temp.fieldName, fieldForm[temp.field])
            })
          }
          fields.push(temp)

          baseFields.push(item)
        })
        fieldList.push(fields)
      })
      this.baseFields = baseFields
      this.fieldList = fieldList
      this.fieldForm = fieldForm
      this.fieldRules = fieldRules
    },
    getItemValue(item, detail, type) {
      if (item.formType == 'personalized') {
        if (type === 'update') {
          const { isPersonalization, lateMinutesOrCounts, earlyMinutesOrCounts } = detail
          return {
            isPersonalization,
            lateMinutesOrCounts,
            earlyMinutesOrCounts
          }
        } else {
          return {
            isPersonalization: 0,
            lateMinutesOrCounts: '',
            earlyMinutesOrCounts: ''
          }
        }
      } else {
        if (type === 'update') {
          return detail[item.fieldName]
        } else {
          return item.defaultValue === null || item.defaultValue === undefined ? '' : item.defaultValue
        }
      }
    },
    UniquePromise({ field, value }) {
      return hrmVerifyAttendanceRuleNameAPI({
        attendanceRuleId: this.id,
        attendanceRuleName: value
      })
    },
    close() {
      this.$emit('close')
    },
    saveClick() {
      this.loading = true
      this.$refs.form.validate((valid) => {
        if (valid) {
          const params = this.getSubmiteParams([].concat.apply([], this.fieldList), this.fieldForm)
          if (this.id) {
            params.attendanceRuleId = this.id
          }
          if (params.isPersonalization && (!params.lateMinutesOrCounts || !params.earlyMinutesOrCounts)) {
            this.$message.error('请补充完整个性设置')
            this.loading = false
            return
          }
          hrmAddOrSetAttendanceRuleAPI(params).then(res => {
            this.$message.success('操作成功')
            this.close()
            this.$emit('save-success')
          }).catch(() => { this.loading = false })
        } else {
          this.loading = false
        }
      })
    },
    getSubmiteParams(array, data) {
      const params = {}
      array.forEach(field => {
        let dataValue = null
        dataValue = data[field.fieldName]
        if (field.formType == 'computeMode') {
          params[field.fieldName] = Number(dataValue)
        } else if (field.formType == 'personalized') {
          for (const key in dataValue) {
            if (dataValue[key] || dataValue[key] === 0) {
              params[key] = dataValue[key]
            }
          }
        } else {
          params[field.fieldName] = dataValue
        }
      })
      return params
    },
    formChange(item, index, value, valueList) {
      if (item.fieldName == 'lateRuleMethod' || item.fieldName == 'earlyRuleMethod') {
        this.setInputTipsAndUnit(item, item.fieldName, value)
      }
    },
    setInputTipsAndUnit(item, fieldName, value) {
      item.inputTips = this.setectValueMap[fieldName][value].inputTips
      this.fieldList.forEach(children => {
        children.forEach(field => {
          if (field.fieldName == this.setectValueMap[fieldName]['computeMode']) {
            this.$set(field, 'unit', this.setectValueMap[fieldName][value].unit)
          }
        })
      })
      this.$forceUpdate()
    }
  }

}
</script>

<style lang='scss'>
.personalized {
  .el-input {
    width: 100px !important;
  }
}

input[type="number"] {
  -moz-appearance: textfield;
}

input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.interval-top {
  margin-top: 10px;
}
</style>

