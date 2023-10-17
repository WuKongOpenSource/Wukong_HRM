<template>
  <field-wrapper
    :activate="activate"
    :field="field"
    :control-flag="controlFlag"
    hidden-title
    class="field-title"
    @click="emitClick"
    @action="handleAction">
    <div class="section-header">
      <div
        style="border-left-color: #2362fb;"
        class="section-mark" />
      <div class="section-title">{{ field.name }}</div>
      <span
        v-if="field.inputTips"
        class="section-tips">
        （{{ field.inputTips }}）
      </span>
    </div>
  </field-wrapper>
</template>

<script>
import FieldWrapper from './FieldWrapper'
import mixins from './mixins'

export default {
  name: 'FieldGroup',
  components: {
    FieldWrapper
  },
  mixins: [mixins],
  methods: {}
}
</script>

<style scoped lang="scss">
.section-header {
  display: flex;
  align-items: center;
  padding: 10px 0 8px;
}

.section-mark {
  height: 14px;
  border-left-style: solid;
  border-left-width: 4px;
  border-radius: 2px;
}

.section-title {
  flex-shrink: 0;
  margin-left: 8px;
  font-size: 14px;
  font-weight: 600;
  word-break: break-all;
  word-wrap: break-word;
}

.section-tips {
  color: $--color-text-secondary;
}

</style>
