<template>
  <el-select
    :key="refreshKey"
    v-model="selectedValue"
    v-bind="$attrs"
    filterable
    remote
    :remote-method="remoteMethod"
    v-on="$listeners"
    @change="valueChange"
    @focus="selectFocus"
  >
    <el-option
      v-for="item in filterOptions"
      :key="item.value"
      :label="(item.name || item.label)"
      :value="item.value" />
  </el-select>
</template>

<script>
import PinyinMatch from 'pinyin-match'

export default {
  // WkSearchSelect
  name: 'WkSearchSelect',

  inheritAttrs: false,

  props: {
    // eslint-disable-next-line vue/require-prop-types
    value: {
      required: true
    },
    options: Array
  },

  data() {
    return {
      selectedValue: null,
      search: '', // 搜索内容
      refreshKey: Date.now() // 频繁切换数据源，会导致options有数据但展示id的问题
    }
  },

  computed: {
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
      handler() {
        if (this.selectedValue !== this.value) {
          this.selectedValue = this.value
        }
      },
      immediate: true
    }
  },

  methods: {
    /**
     * @description: 单选搜索
     * @return {*}
     */
    remoteMethod(search) {
      console.log('remoteMethod---', search)
      this.search = search
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
    valueChange(e) {
      this.$emit('input', e)
      this.$nextTick(() => {
        this.refreshKey = Date.now()
      })
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

</style>
