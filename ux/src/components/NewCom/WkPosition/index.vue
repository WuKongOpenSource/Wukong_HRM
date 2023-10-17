<template>
  <div class="wk-position">
    <wk-distpicker
      v-model="distpickerValue"
      :hide-area="hideArea"
      :only-province="onlyProvince"
      :disabled="disabled"
      v-bind="$attrs"
      clearable
      @change="valueChange" />
    <el-input
      v-if="showDetail"
      v-model="detailAddress"
      :rows="2"
      :disabled="disabled"
      :maxlength="100"
      type="textarea"
      placeholder="详细地址"
      @change="valueChange" />
  </div>
</template>

<script>
import WkDistpicker from '../WkDistpicker'

import { isArray, isEmpty } from '@/utils/types'
import Emitter from 'element-ui/lib/mixins/emitter'

export default {
  // 地址
  name: 'WkPosition',

  components: {
    WkDistpicker
  },

  mixins: [Emitter],

  inheritAttrs: false,

  props: {
    hideArea: { type: Boolean, default: false },
    onlyProvince: { type: Boolean, default: false },
    showDetail: { type: Boolean, default: true },
    // eslint-disable-next-line vue/require-prop-types
    value: {
      required: true
    },
    disabled: Boolean
  },

  data() {
    return {
      distpickerValue: [],
      detailAddress: '',
      dataValue: []
    }
  },

  computed: {},

  watch: {
    value: {
      handler(val) {
        if (!this.valueEquals(val, this.dataValue)) {
          this.getDefaultValue()
        }
      },
      immediate: true
    }
  },

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    getDefaultValue() {
      const value = isArray(this.value) ? this.value : []

      const distpickerValue = []
      let hasAddress = false
      value.forEach(item => {
        if (item?.id === 4) {
          hasAddress = true
          this.detailAddress = item.name
        } else {
          if (item) {
            distpickerValue.push(item)
          }
        }
      })

      if (!hasAddress) {
        this.detailAddress = ''
      }

      this.distpickerValue = distpickerValue
    },

    /**
     * 值更新
     */
    valueChange() {
      const dataValue = (this.showDetail && isEmpty(this.detailAddress)) || !this.showDetail ? this.distpickerValue : this.distpickerValue.concat([{
        code: '',
        name: this.detailAddress,
        id: 4
      }])
      this.dataValue = dataValue

      this.$emit('input', this.dataValue)
      this.$emit('change', this.dataValue)
      this.dispatch('ElFormItem', 'el.form.change', this.dataValue)
    },

    valueEquals(a, b) {
      if (a === b) return true
      if (!(a instanceof Array)) return false
      if (!(b instanceof Array)) return false
      if (a.length !== b.length) return false
      for (let i = 0; i !== a.length; ++i) {
        const aValue = a[i]
        const bValue = b[i]
        if (aValue.id !== bValue.id || aValue.name !== bValue.name) return false
      }
      return true
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-position {
  .el-textarea {
    margin-top: 15px;
  }
}
</style>
