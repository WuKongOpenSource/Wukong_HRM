<template>
  <div class="wk-select">
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
        @change="valueChange"
        @focus="selectFocus">
        <el-option
          v-for="(item, index) in filterOptions"
          :key="index"
          :label="!isEmptyValue(item.value) ? item.label || item.name : item "
          :title="!isEmptyValue(item.value) ? item.label || item.name : item "
          :value="getValue(item)" />
      </el-select>
      <el-radio-group
        v-else-if="showType === 'tiled'"
        v-model="dataValue.select"
        :disabled="disabled"
        @change="valueChange">
        <el-radio
          v-for="(item, index) in options"
          :key="index"
          :label="getValue(item)">
          {{ !isEmptyValue(item.value) ? item.label || item.name : item }}
        </el-radio>
      </el-radio-group>
      <el-input
        v-if="dataValue.select === LOGIC_OTHER && otherShowInput"
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
import Emitter from 'element-ui/lib/mixins/emitter'
import PinyinMatch from 'pinyin-match'

const LOGIC_OTHER = '其他'

export default {
  // 自定义字段库 单选
  name: 'WkSelect',

  components: {},

  mixins: [Emitter],

  props: {
    value: [String, Number],
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
        select: '',
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
    },
    options: {
      handler(newVal, oldVal) {
        this.validValue()
      },
      deep: true
    }
  },

  created() {
    // this.validValue()
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 验证值
     */
    validValue() {
      if (!isArray(this.options) || this.options.length === 0) {
        return
      }

      if (this.value !== this.dataValue.select && this.value !== this.dataValue.otherValue) {
        if (isEmpty(this.value)) {
          this.dataValue = {
            select: '',
            otherValue: ''
          }
        } else {
          if (this.otherShowInput) {
            const valueObj = this.options.find(item => this.getValue(item) === this.value)
            if (valueObj) {
              if (this.dataValue.select !== this.value) {
                this.dataValue = {
                  select: this.value,
                  otherValue: ''
                }
              }
            } else {
              if (this.dataValue.otherValue !== this.value) {
                this.dataValue = {
                  select: LOGIC_OTHER,
                  otherValue: this.value
                }
              }
            }
          } else {
            this.dataValue = {
              select: this.value,
              otherValue: ''
            }
          }
        }
      } else {
        if (isEmpty(this.value)) {
          if (this.dataValue.select !== LOGIC_OTHER) {
            this.dataValue = {
              select: '',
              otherValue: ''
            }
          } else {
            this.dataValue = {
              select: LOGIC_OTHER,
              otherValue: ''
            }
          }
        }
      }
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
     * 值更新
     */
    valueChange() {
      const value = this.dataValue.select === LOGIC_OTHER ? this.dataValue.otherValue.trim() : this.dataValue.select
      this.$emit('input', value)
      this.$emit('change', value)
      this.$nextTick(() => {
        this.refreshKey = Date.now()
      })
    },

    /**
     * 失去焦点
     */
    inputBlur() {
      // 暂未加input触发change逻辑
      const value = this.dataValue.otherValue
      const eIsObject = this.options.length > 0 && !this.isEmptyValue(this.options[0].value)
      const has = this.options.find(item => {
        if (eIsObject) {
          return item.value === value.trim()
        } else {
          return item === value.trim()
        }
      })
      if (has) {
        this.dataValue.otherValue = ''
      }

      this.$emit('input', this.dataValue.otherValue)
      this.$emit('change', this.dataValue.otherValue)
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
.wk-select {
  .el-input {
    margin-top: 5px;
  }

  .el-radio {
    margin: 5px 30px 5px 0;
  }
}
</style>
