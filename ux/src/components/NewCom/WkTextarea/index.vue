<template>
  <div class="wk-textarea">
    <el-input
      v-model="dataValue"
      v-bind="$attrs"
      @input="valueChange" />
    <el-popover
      :title="title"
      placement="bottom"
      width="600"
      trigger="click">
      <el-input
        v-model="dataValue"
        v-bind="$attrs"
        :autosize="{ minRows: 12}"
        @input="valueChange" />
      <i
        slot="reference"
        class="zoom el-icon-full-screen" />
    </el-popover>
  </div>
</template>

<script>
import Emitter from 'element-ui/lib/mixins/emitter'

export default {
  // 自定义字段库 文本域
  name: 'WkTextarea',

  components: {},

  mixins: [Emitter],

  props: {
    value: [String, Number],
    title: String,
    rows: Number
    // // 选择其他展示input输入框
    // disabled: Boolean,
    // rows: Number,
    // autosize: {
    //   type: Object,
    //   default: () => {
    //     return {}
    //   }
    // },
    // maxlength: [String, Number],
    // placeholder: String,
    // resize: {
    //   type: String,
    //   default: 'none'
    // }
  },

  data() {
    return {
      dataValue: ''
    }
  },

  computed: {},

  watch: {
    value: {
      handler(newVal, oldVal) {
        this.validValue()
      },
      immediate: true
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
      this.dataValue = this.value
    },

    /**
     * 值更新
     */
    valueChange() {
      this.$emit('input', this.dataValue)
      this.$emit('change', this.dataValue)
      this.dispatch('ElFormItem', 'el.form.change', this.dataValue)
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-textarea {
  position: relative;

  .zoom {
    position: absolute;
    top: 5px;
    right: 5px;
  }
}
</style>
