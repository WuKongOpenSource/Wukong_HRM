<template>
  <field-wrapper
    :activate="activate"
    :field="field"
    :control-flag="controlFlag"
    class="field-date-interval"
    @click="emitClick"
    @action="handleAction">

    <flexbox class="range-box">
      <i class="el-icon-date icon" />
      <flexbox-item :class="{ placeholder: !isHasValue }">
        {{ isHasValue ? field.defaultValue[0] : '开始时间' }}
      </flexbox-item>
      <span>至</span>
      <flexbox-item :class="{ placeholder: !isHasValue }">
        {{ isHasValue ? field.defaultValue[1] : '结束时间' }}
      </flexbox-item>
    </flexbox>

  </field-wrapper>
</template>

<script>
import FieldWrapper from './FieldWrapper'
import mixins from './mixins'

export default {
  name: 'FieldDateInterval',
  components: {
    FieldWrapper
  },
  mixins: [mixins],
  computed: {
    // 判断是否有值
    isHasValue() {
      return Array.isArray(this.field.defaultValue) && this.field.defaultValue.length === 2
    }
  },
  methods: {}
}
</script>

<style scoped lang="scss">
.range-box {
  width: 100%;
  height: $--input-height;
  padding: 0 10px;
  font-size: 14px;
  text-align: center;
  background: white;
  border: $--border-medium;
  border-radius: 3px;

  .icon {
    color: $--color-n100;
  }

  .placeholder {
    flex: 1;
    color: $--color-text-placeholder;
  }
}
</style>
