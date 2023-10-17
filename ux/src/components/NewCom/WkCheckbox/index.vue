<template>
  <div class="wk-checkbox">
    <template v-if="valueIsObject">
      <el-select
        v-if="showType === 'default'"
        :key="refreshKey"
        v-model="dataValue.select"
        filterable
        remote
        :remote-method="remoteMethod"
        :disabled="disabled"
        :clearable="clearable"
        :placeholder="placeholder"
        style="width: 100%;"
        multiple
        @change="valueChange"
        @focus="selectFocus">
        <el-option
          v-for="(item, index) in filterOptions"
          :key="index"
          :label="!isEmptyValue(item.value) ? item.label || item.name : item "
          :value="getValue(item)" />
      </el-select>
      <el-checkbox-group
        v-else-if="showType === 'tiled'"
        v-model="dataValue.select"
        :disabled="disabled"
        @change="valueChange">
        <el-checkbox
          v-for="(item, index) in options"
          :key="index"
          :label="getValue(item)">
          {{ !isEmptyValue(item.value) ? item.label || item.name : item }}
        </el-checkbox>
      </el-checkbox-group>
      <el-input
        v-if="dataValue.select.includes(LOGIC_OTHER) && otherShowInput"
        v-model="dataValue.otherValue"
        :disabled="disabled"
        :maxlength="100"
        placeholder="其他选项需填写，否则为无效选项"
        @blur="inputBlur" />
    </template>
  </div>
</template>

<script>
import { isObject, isEmpty, isArray } from '@/utils/types'
import { valueEquals } from 'element-ui/lib/utils/util'
import Emitter from 'element-ui/lib/mixins/emitter'
import PinyinMatch from 'pinyin-match'

const LOGIC_OTHER = '其他'

export default {
  // 自定义字段库 多选
  name: 'WkCheckbox',

  components: {},

  mixins: [Emitter],

  props: {
    // eslint-disable-next-line vue/require-prop-types
    value: {},
    // 选择其他展示input输入框
    otherShowInput: {
      type: Boolean,
      default: false
    },
    disabled: Boolean,
    clearable: Boolean,
    placeholder: String,
    options: {
      type: Array,
      default: () => {
        return []
      }
    },
    showType: {
      type: String,
      default: 'default' //  下拉 default 平铺 tiled
    }
  },

  data() {
    return {
      LOGIC_OTHER,
      dataValue: {
        select: [],
        otherValue: ''
      },
      search: '', // 搜索内容
      refreshKey: Date.now() // 频繁切换数据源，会导致options有数据但展示id的问题
    }
  },

  computed: {
    valueIsObject() {
      return isObject(this.dataValue)
    },
    filterOptions() {
      if (this.search) {
        return this.options.filter(item => PinyinMatch.match(!this.isEmptyValue(item.value) ? item.label || item.name : item, this.search))
      } else {
        return this.options
      }
    }
  },

  watch: {
    value: {
      handler(newVal, oldVal) {
        this.validValue()
      },
      immediate: true
    }
  },

  created() {
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 验证值
     */
    validValue() {
      if (isEmpty(this.value)) {
        this.dataValue = {
          select: [],
          otherValue: ''
        }
      } else {
        // 如果值是字符串，并且有值，尝试逗号分割处理
        let propValue = this.value
        if (typeof propValue === 'string' && propValue) {
          propValue = propValue.split(',')
        }

        let needChangeValue = false // 如果有无效值，该值将变成true

        if (this.otherShowInput) {
          const value = isArray(propValue) ? propValue : []
          const otherItem = value.filter((name) => !this.options.includes(name))
          if (otherItem.length > 0) {
            const newValue = value.filter((name) => !otherItem.includes(name))
            newValue.push(LOGIC_OTHER)
            const validValue = this.removeInvalidValue(newValue)
            this.dataValue = {
              select: validValue,
              otherValue: otherItem[otherItem.length - 1]
            }
            needChangeValue = validValue.length !== newValue.length
          } else {
            if (!valueEquals(value, this.dataValue.select)) {
              const validValue = this.removeInvalidValue(value)
              this.dataValue = {
                select: validValue,
                otherValue: ''
              }
              needChangeValue = validValue.length !== value.length
            }
          }
        } else {
          const validValue = this.removeInvalidValue(propValue)
          this.dataValue = {
            select: validValue,
            otherValue: ''
          }
          needChangeValue = validValue.length !== propValue.length
        }

        if (needChangeValue) {
          this.valueChange(false)
        }
      }
    },

    /**
     * @description: 去除无效值
     * @return {*}
     */
    removeInvalidValue(value) {
      if (this.options && this.options.length > 0 && isArray(value) && value.length > 0) {
        const values = this.options.map(o => this.getValue(o))
        return value.filter(o => values.includes(o))
      }
      return value
    },

    /**
     * 选项值
     */
    getValue(item) {
      return !this.isEmptyValue(item.value) ? item.value : item
    },

    /**
     * 判断是空值
     */
    isEmptyValue(value) {
      return value === null || value == undefined
    },

    /**
     * @description: 值更新
     * @param {*} sendEvent false 时 仅触发input
     * @return {*}
     */
    valueChange(sendEvent = true) {
      if (this.dataValue.select.includes(LOGIC_OTHER)) {
        const newValue = this.dataValue.select.filter(item => item !== LOGIC_OTHER)
        const otherValue = this.dataValue.otherValue.trim()
        // 有值才触发change
        if (otherValue) {
          newValue.push(otherValue)
          this.$emit('input', newValue)
          if (sendEvent !== false) {
            this.$emit('change', newValue)
          }
        }
      } else {
        this.dataValue.otherValue = ''
        this.$emit('input', this.dataValue.select)
        if (sendEvent !== false) {
          this.$emit('change', this.dataValue.select)
        }
      }

      this.$nextTick(() => {
        this.refreshKey = Date.now()
      })
    },

    /**
     * 失去焦点
     */
    inputBlur() {
      this.valueChange()
      // const value = this.dataValue.otherValue
      // const eIsObject = this.options.length > 0 && !this.isEmptyValue(this.options[0].value)
      // const has = this.options.find(item => {
      //   if (eIsObject) {
      //     return item.value === value.trim()
      //   } else {
      //     return item === value.trim()
      //   }
      // })
      // if (has) {
      //   this.dataValue.otherValue = ''
      // }
      // this.$emit('change', this.dataValue.select)
      // this.$emit('input', this.dataValue.select)
    },

    /**
     * @description: 单选搜索
     * @return {*}
     */
    remoteMethod(search) {
      this.search = search
    },

    /**
     * @description: 获取焦点重置搜索
     * @return {*}
     */
    selectFocus() {
      this.search = ''
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-checkbox {
  .el-input {
    margin-top: 5px;
  }

  .el-checkbox-group {
    line-height: 1.5;
  }
}
</style>
