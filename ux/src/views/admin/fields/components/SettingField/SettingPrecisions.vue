<template>
  <div class="setting-precisions">
    <el-select
      v-model="field.precisions"
      :disabled="!editAuth"
      >
      <el-option
        v-for="item in options"
        :key="item.value"
        :label="item.label"
        :value="item.value" />
    </el-select>
  </div>
</template>

<script>
import { getFieldAuth } from '../../utils'

export default {
  name: 'SettingPrecisions',
  props: {
    field: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      options: []
    }
  },
  computed: {
    // 选项不能配置
    editAuth() {
      return getFieldAuth(this.field.operating).defaultEdit
    }
  },
  watch: {
    field: {
      handler() {
        if (![
          'date_interval',
          'position',
          'select',
          'checkbox'
        ].includes(this.field.formType)) return
        if (this.field.formType === 'date_interval') {
          this.options = [
            { label: '日期', value: 1 },
            { label: '日期时间', value: 2 }
          ]
        } else if (this.field.formType === 'position') {
          this.options = [
            { label: '省/地区、市、区/县、详细地址', value: 1 },
            { label: '省/地区、市、区/县', value: 2 },
            { label: '省/地区、市', value: 3 },
            { label: '省/地区', value: 4 }
          ]
        } else {
          this.options = [
            { label: '平铺', value: 1 },
            { label: '下拉', value: 2 }
          ]
          if (!this.field.precisions) {
            this.$set(this.field, 'precisions', this.field.formType === 'checkbox' ? 1 : 2)
          }
        }
        if (!this.field.precisions) {
          this.$set(this.field, 'precisions', 1)
        }
      },
      deep: true,
      immediate: true
    }
  }
}
</script>

<style scoped>
.el-select {
  width: 100%;
}
</style>
