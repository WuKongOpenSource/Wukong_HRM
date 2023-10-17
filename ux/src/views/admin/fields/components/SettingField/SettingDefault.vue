<template>
  <div class="setting-default">
    <el-input
      v-if="type === 'text'"
      v-model="field.defaultValue"
      :maxlength="field.maxLength || 100"
      :disabled="disabled"
      @blur="inputBlur" />

    <el-input
      v-else-if="type === 'textarea'"
      v-model="field.defaultValue"
      :maxlength="field.maxLength || 800"
      :disabled="disabled"
      @blur="inputBlur" />

    <el-date-picker
      v-else-if="type === 'datePicker'"
      v-model="field.defaultValue"
      :disabled="disabled"
      :type="field.formType === 'date' ? 'date' : 'datetime'"
      :value-format="field.formType === 'date' ? 'yyyy-MM-dd' : 'yyyy-MM-dd HH:mm:ss'"
      placeholder="请选择" />

    <el-date-picker
      v-else-if="type === 'date_interval'"
      v-model="field.defaultValue"
      :type="field.precisions === 1 ? 'daterange' : 'datetimerange'"
      :value-format="field.precisions === 1 ? 'yyyy-MM-dd' : 'yyyy-MM-dd HH:mm:ss'"
      :disabled="disabled"
      start-placeholder="开始日期"
      end-placeholder="结束日期" />

    <el-select
      v-else-if="type === 'select'"
      :key="field.formType"
      v-model="field.defaultValue"
      :clearable="canClearable"
      :multiple="field.formType === 'checkbox'"
      :disabled="disabled"
      >
      <el-option
        v-for="item in options"
        :key="item.value"
        :label="item.label || item.name"
        :value="item.value" />
    </el-select>

    <template v-else-if="type === 'number'">
      <el-input
        v-model="field.defaultValue"
        :disabled="disabled"
        @blur="inputBlur">
        <div
          v-if="field.formType === 'percent'"
          slot="suffix"
          class="el-input__icon">%</div>
      </el-input>
      <div class="input-tips">
        <span>*</span>
        数字的位数必须少于{{ field.formType === 'percent' ? 10 : 15 }}位
      </div>
    </template>

    <template v-else-if="type === 'position'">
      <wk-distpicker
        v-model="selectedMapValue"
        :hide-area="field.precisions >= 3"
        :only-province="field.precisions === 4"
        :disabled="disabled"
        clearable
        @change="handleCascaderChange" />
      <el-input
        v-if="field.precisions === 1"
        v-model="detailAddress"
        :rows="3"
        :maxlength="100"
        :disabled="disabled"
        type="textarea"
        style="margin-top: 5px;"
        @change="inputPositionChange" />
    </template>
  </div>
</template>

<script>
import WkDistpicker from '@/components/NewCom/WkDistpicker'

import { isObject, isEmpty, isArray } from '@/utils/types'
import { regexIsCRMMobile, regexIsCRMEmail, objDeepCopy } from '@/utils'
import { getFieldAuth } from '../../utils'

export default {
  name: 'SettingDefault',
  components: {
    WkDistpicker
  },
  props: {
    field: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      selectedMapValue: [],
      detailAddress: '',
      oldPrecisions: null,

      areaData: []
    }
  },
  computed: {
    // 只读
    disabled() {
      return !getFieldAuth(this.field.operating).defaultEdit
    },
    // 是否允许清除默认选项
    canClearable() {
      const formType = this.field.formType
      if ([
        'boolean_value'
      ].includes(formType)) return false
      return true
    },
    // 类型
    type() {
      const formType = this.field.formType
      if ([
        'date',
        'datetime'
      ].includes(formType)) return 'datePicker'
      if ([
        'number',
        'floatnumber',
        'percent'
      ].includes(formType)) return 'number'
      if ([
        'select',
        'checkbox',
        'boolean_value',
        'field_attention'
      ].includes(formType)) return 'select'
      switch (this.field.formType) {
        case 'date_interval':
          return 'date_interval'
        case 'position':
          return 'position'
        case 'textarea':
          return 'textarea'
        default:
          return 'text'
      }
    },
    // 选项
    options() {
      if (this.type !== 'select') return []
      const formType = this.field.formType
      if ([
        'select',
        'checkbox'
      ].includes(formType)) {
        if (this.field.setting && this.field.setting.length > 0 && this.itemIsObject(this.field.setting[0])) {
          return this.field.setting
        } else {
          return this.field.setting.map(o => {
            return { label: o, value: o }
          })
        }
      }
      switch (formType) {
        case 'boolean_value':
          return [
            { label: '选中', value: '1' },
            { label: '不选中', value: '0' }
          ]
        case 'field_attention':
          return [
            { label: '一星', value: 1 },
            { label: '二星', value: 2 },
            { label: '三星', value: 3 },
            { label: '四星', value: 4 },
            { label: '五星', value: 5 }
          ]
        default:
          return []
      }
    }
  },
  watch: {
    field: {
      handler() {
        if (this.field.formType === 'boolean_value') {
          // 布尔值
          this.field.defaultValue = isEmpty(this.field.defaultValue) ? '0' : this.field.defaultValue
          return
        }
        // 关注度 值
        if (this.field.formType === 'field_attention') {
          this.field.defaultValue = isEmpty(this.field.defaultValue) ? null : Number(this.field.defaultValue)
          return
        }
        if (
          this.type === 'select' &&
          isEmpty(this.field.setting) &&
          !isEmpty(this.field.options)
        ) {
          this.$set(this.field, 'setting', this.field.options.split(','))
        }
        if (this.type === 'position') {
          this.resetDefaultValue()
          // 地址默认值
          if (isEmpty(this.field.defaultValue)) {
            this.selectedMapValue = []
            if (!isArray(this.field.defaultValue)) {
              this.field.defaultValue = []
            }
            this.detailAddress = ''
          } else {
            this.selectedMapValue = this.field.defaultValue.filter(o => o.id !== 4)
            if (this.field.precisions === 1) {
              const findRes = this.field.defaultValue.find(o => o.id === 4)
              if (findRes) {
                this.detailAddress = findRes.name
              } else {
                this.detailAddress = ''
              }
            }
          }
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    inputBlur() {
      // this.$emit('input', this.value)
      if (!this.field.defaultValue) return
      if (this.field.formType === 'mobile') {
        // 校验手机号
        if (!regexIsCRMMobile(this.field.defaultValue)) {
          this.$message.error('输入的手机格式有误')
          this.field.defaultValue = ''
        }
      } else if (this.field.formType === 'email') {
        // 校验邮箱
        if (!regexIsCRMEmail(this.field.defaultValue)) {
          this.$message.error('输入的邮箱格式有误')
          this.field.defaultValue = ''
        }
      } else if (this.type === 'number') {
        // 校验数字类型
        const num = Number(this.field.defaultValue) // 去0
        if (isNaN(num)) {
          this.field.defaultValue = null
          return
        }
        this.field.defaultValue = String(num)
        const arr = String(num).split('.')

        const len = String(num)
          .replace('.', '')
          .replace('-', '')
          .length
        const maxlength = this.field.formType === 'percent' ? 10 : 15
        if (len > maxlength) {
          this.$message.error(`最多支持${maxlength}位数字（包含小数位）`)
          this.field.defaultValue = null
          return
        }

        const min = isEmpty(this.field.minNumRestrict) ? -Infinity : Number(this.field.minNumRestrict || -Infinity)
        const max = isEmpty(this.field.maxNumRestrict) ? Infinity : Number(this.field.maxNumRestrict || Infinity)
        if (num < min) {
          this.$message.error('默认值不能小于最小值')
          this.field.defaultValue = null
          return
        }
        if (num > max) {
          this.$message.error('默认值不能大于最大值')
          this.field.defaultValue = null
          return
        }

        // null 不支持小数  0 不限制小数位
        if (isEmpty(this.field.precisions)) {
          this.field.defaultValue = arr[0]
          return
        }
        if (this.field.precisions === 0) return
        if (arr.length > 1 && arr[1].length > Number(this.field.precisions)) {
          this.$message.error(`默认值的小数位不能大于${this.field.precisions}`)
          this.field.defaultValue = null
        }
      }
    },

    /**
     * 修改精度重置默认值
     */
    resetDefaultValue() {
      if (
        !this.oldPrecisions ||
        (this.oldPrecisions === this.field.precisions)
      ) {
        this.oldPrecisions = this.field.precisions
        return
      }
      this.oldPrecisions = this.field.precisions
      this.selectedMapValue = []
      if (!isEmpty(this.field.defaultValue)) {
        this.field.defaultValue = []
      }
    },

    /**
     * 修改详细地址
     */
    inputPositionChange() {
      if (this.field.precisions !== 1) {
        const findIndex = this.field.defaultValue.findIndex(o => o.id === 4)
        if (findIndex === -1) return
        this.field.defaultValue.splice(findIndex, 1)
      } else {
        const findRes = this.field.defaultValue.find(o => o.id === 4)
        if (findRes) {
          findRes.name = this.detailAddress
        } else {
          this.field.defaultValue.push({
            code: '',
            name: this.detailAddress,
            id: 4
          })
        }
      }
    },

    /**
     * 选择省市区
     */
    handleCascaderChange() {
      this.field.defaultValue = this.selectedMapValue
      this.inputPositionChange()
    },

    getCascaderValArr(data, value) {
      const res = []
      if (value.length === 0) return res
      let index = 0
      let _data = objDeepCopy(data)
      do {
        const findRes = _data.find(o => o.code === value[index])
        if (findRes) {
          _data = findRes.children || []
          res.push({
            code: findRes.code,
            name: findRes.name,
            id: index + 1
          })
        }
        index++
      } while (index <= value.length)
      return res
    },

    /**
     * 是对象
     */
    itemIsObject(item) {
      return isObject(item)
    }
  }
}
</script>

<style scoped lang="scss">
.el-date-editor {
  width: 100%;
}

.el-select {
  width: 100%;
}

.el-cascader {
  width: 100%;
}

.el-input__icon {
}

.input-tips {
  margin-top: 10px;
  font-size: 12px;
  color: $--color-text-secondary;

  span {
    color: red;
  }
}
</style>
