<template>
  <field-wrapper
    :activate="activate"
    :field="field"
    :control-flag="controlFlag"
    class="field-textarea"
    @click="emitClick"
    @action="handleAction">

    <div class="box">
      <div class="default-val">
        {{ typeof field.defaultValue == 'string' ? field.defaultValue : '' }}
      </div>
      <div class="max-tips">
        {{ (field.defaultValue || '').length+'/'+(field.maxLength || 800) }}
      </div>
    </div>

  </field-wrapper>
</template>

<script>
import FieldWrapper from './FieldWrapper'
import mixins from './mixins'

export default {
  name: 'FieldTextarea',
  components: {
    FieldWrapper
  },
  mixins: [mixins],
  methods: {}
}
</script>

<style scoped lang="scss">
.box {
  position: relative;
  width: 100%;
  height: 80px;
  padding: 10px 10px 15px;
  font-size: 14px;
  background: white;
  border: $--border-medium;
  border-radius: $--border-radius-base;

  .default-val {
    width: 100%;
    height: 48px;
    overflow: hidden;
    word-break: break-all;
  }

  .max-tips {
    width: 100%;
    overflow: hidden;
    font-size: 12px;
    color: $--color-text-secondary;
    text-align: right;
    word-break: break-all;
  }
}
</style>
