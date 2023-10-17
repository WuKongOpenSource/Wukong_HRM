import { valueEquals } from 'element-ui/lib/utils/util'
import { objDeepCopy } from '@/utils'
import merge from '@/utils/merge'

const DefaultProps = {
  value: '',
  label: '',
  // 搜索
  // 请求和参数
  searchRequest: null,
  searchParams: null
}

export default {
  props: {
    radio: Boolean,
    data: Array, // 相当于 currentList
    value: Array,
    // 取值字段
    props: {
      type: Object,
      default: () => {
        return {}
      }
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      dataValue: [],
      otherValue: [],
      currentValue: [],

      // 点击下一页时，忽略change
      ignoreCurrentChange: false,
      ignoreOtherChange: false
    }
  },
  computed: {
    config() {
      return merge({ ...DefaultProps }, this.props || {})
    }
  },
  watch: {
    value() {
      if (!valueEquals(this.value, this.dataValue)) {
        this.dataValue = objDeepCopy(this.value)
        this.getCurrentAndOtherValue(false)
      }
    },

    data() {
      if (!this.data) {
        // 没有数据初始化当前值
        this.currentValue = []
        return
      }
      this.$nextTick(() => {
        this.getCurrentAndOtherValue()
      })
    },

    currentValue(newVal, oldVal) {
      if (!this.data) return
      if (this.radio) {
        if (this.otherValue.length > 0) {
          // 阻挡触发change事件
          this.ignoreOtherChange = true
          this.otherValue = []
          this.$nextTick(() => {
            this.ignoreOtherChange = false
          })
        }

        // change
        if (this.currentValue.length > 1) {
          this.currentValue = this.currentValue.length ? [this.currentValue[this.currentValue.length - 1]] : []
          return
        }
      }

      // 触发change  和 input 事件
      this.$emit('all-select', this.data.length > 0 && this.data.length === newVal.length)
      if (!this.ignoreCurrentChange) {
        if (!valueEquals(newVal, oldVal)) {
          // change
          this.checkboxChange([...this.currentValue, ...this.otherValue])
          this.$nextTick(() => {
            this.$emit('change', objDeepCopy(this.dataValue))
          })
        }
      }
    },

    otherValue(newVal, oldVal) {
      if (!this.data) return
      if (this.radio) {
        const needChange = (this.currentValue.length + this.otherValue.length) > 1
        if (needChange) {
          if (this.currentValue.length > 0) {
            // 阻挡触发change事件
            this.ignoreCurrentChange = true
            this.currentValue = []
            this.$nextTick(() => {
              this.ignoreCurrentChange = false
            })
          }

          if (this.otherValue.length > 1) {
            this.otherValue = [this.otherValue[this.otherValue.length - 1]]
          }
          return
        }
      }

      // 触发change  和 input 事件
      if (!valueEquals(newVal, oldVal) && !this.ignoreOtherChange) {
        this.checkboxChange([...this.currentValue, ...this.otherValue])
        this.$nextTick(() => {
          this.$emit('change', objDeepCopy(this.dataValue))
        })
      }
    }
  },
  mounted() {
    this.dataValue = objDeepCopy(this.value || [])
    this.getCurrentAndOtherValue(true)
  },
  methods: {

    /**
     * 搜索选择
     */
    searchSelect(item) {
      const id = item[this.config.value]
      const listIds = this.data.map(item => item[this.config.value])
      if (listIds.includes(id)) {
        if (!this.currentValue.includes(id)) {
          this.currentValue = [...this.currentValue, id]
        }
      } else {
        if (!this.otherValue.includes(id)) {
          this.otherValue = [...this.otherValue, id]
        }
      }
    },

    /**
     * 勾选
     */
    checkboxChange(val) {
      this.dataValue = val
      this.$emit('input', val)
    },

    /**
     * 全部勾选
     */
    handleCheckAllChange(val) {
      if (this.data && this.data.length > 0) {
        if (val) {
          const currentValue = this.data.map(item => item[this.config.value])
          if (!valueEquals(this.currentValue, currentValue)) {
            this.currentValue = currentValue
          }
        } else {
          this.currentValue = []
        }
      }
    },

    /**
     * 清空全部
     */
    clearAll() {
      this.currentValue = []
    },

    /**
     * 获取当前值和其他值
     */
    getCurrentAndOtherValue(ignoreChange = true) {
      if (!this.data) {
        return
      }
      this.ignoreCurrentChange = ignoreChange
      this.ignoreOtherChange = ignoreChange
      // 解析当前列的值
      const otherValue = []
      const currentValue = []
      const listIds = this.data.map(item => item[this.config.value])
      this.dataValue.forEach(id => {
        if (listIds.includes(id)) {
          currentValue.push(id)
        } else {
          otherValue.push(id)
        }
      })

      if (!valueEquals(this.otherValue, otherValue)) {
        this.otherValue = otherValue
      }
      if (!valueEquals(this.currentValue, currentValue)) {
        this.currentValue = currentValue
      }

      this.$nextTick(() => {
        this.ignoreCurrentChange = false
        this.ignoreOtherChange = false
      })
    }
  }
}
