/** 自定义组件 共同逻辑 */
export default {
  data() {
    return {
      dataValue: {}
    }
  },
  watch: {
    value: function(val) {
      this.dataValue = val
    }
  },
  props: {
    value: {
      type: Object,
      default: () => {
        return {}
      }
    },
    /** 索引值 用于更新数据 */
    index: Number,
    /** 包含数据源 */
    item: Object,
    disabled: {
      type: Boolean,
      default: false
    }
  },

  created() {
    /** 如果有值以传入值为主 如果无值 已默认值为主 */
    this.dataValue = this.value
  },

  methods: {
    // 输入的值
    valueChange(val) {
      this.$emit('value-change', {
        index: this.index,
        value: val
      })
    }
  }

}
