<template>
  <flexbox
    class="wk-form-items"
    align="flex-start"
    wrap="wrap"
    justify="flex-start">
    <template v-for="(item, index) in fieldList">
      <wk-form-item
        :key="index"
        :prop-prefix="propPrefix"
        :item="item"
        :index="index"
        :field-from="fieldFrom"
        :ignore-fields="ignoreFields"
        :disabled="item.disabled || disabled"
        @change="fieldChange"
      >
        <template slot-scope="slotProps">
          <slot :data="slotProps.data" :index="slotProps.index" />
        </template>
      </wk-form-item>
    </template>
  </flexbox>
</template>

<script>
import WkFormItem from './WkFormItem'

export default {
  // 多块形式的form-item 用于字段库
  name: 'WkFormItems',

  components: {
    WkFormItem
  },

  props: {
    // 表单验证前缀
    propPrefix: {
      type: String,
      default: ''
    },
    fieldFrom: {
      type: Object,
      default: () => {
        return {}
      }
    },
    fieldList: {
      type: Array,
      default: () => {
        return []
      }
    },
    // 忽略的字段直接输出
    ignoreFields: {
      type: Array,
      default: () => {
        return []
      }
    },
    disabled: Boolean
  },

  data() {
    return {
    }
  },

  computed: {},

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    fieldChange(item, index, value, valueList) {
      this.$emit('change', item, index, value, valueList)
    }
  }
}
</script>

<style lang="scss" scoped>
</style>
