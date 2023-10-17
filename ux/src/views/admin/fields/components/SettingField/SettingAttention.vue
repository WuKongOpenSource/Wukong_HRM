<template>
  <div class="setting-attention">
    <el-checkbox
      v-model="isOpen"
      :disabled="!editAuth"
      @change="handleChange"
    >启用关注度自动变更规则</el-checkbox>
    <div class="rule">
      超过
      <el-input
        v-model="num"
        v-WkNumber="'positiveInt'"
        :disabled="!isOpen || !editAuth"
        @blur="handleChange" />天未跟进/未更新，
      自动减少
      <el-select
        v-model="value"
        :disabled="!isOpen || !editAuth"

        @change="handleChange">
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value" />
      </el-select>
      星，依次类推，降到0为止
    </div>
  </div>
</template>

<script>
import { getFieldAuth } from '../../utils'

export default {
  name: 'SettingAttention',
  props: {
    field: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      options: [
        { label: '一', value: 1 },
        { label: '二', value: 2 },
        { label: '三', value: 3 },
        { label: '四', value: 4 },
        { label: '五', value: 5 }
      ],
      isOpen: false, // 是否开启
      num: 1, // 超过天数
      value: '' // 减少数量
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
        if (this.field.setting && this.field.setting.length > 0) {
          this.isOpen = true
          this.num = this.field.setting[0].num
          this.value = this.field.setting[0].value
        } else {
          this.isOpen = false
          this.num = ''
          this.value = ''
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    handleChange() {
      if (this.isOpen) {
        if (!this.num || this.num <= 0) {
          this.num = 1
        }

        const setting = [{ num: this.num, value: this.value }]
        this.$set(this.field, 'setting', setting)
        this.$set(this.field, 'options', JSON.stringify(setting))
      } else {
        this.$set(this.field, 'setting', [])
        this.$set(this.field, 'options', JSON.stringify([]))
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.rule {
  padding: 10px 0;
  line-height: 40px;
  color: $--color-text-secondary;
}

.el-input,
.el-select {
  width: 100px;
}
</style>
