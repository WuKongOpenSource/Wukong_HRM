<template>
  <field-wrapper
    :activate="activate"
    :field="field"
    :control-flag="controlFlag"
    class="field-custom-number"
    @click="emitClick"
    @action="handleAction">

    <flexbox align="center" class="box">
      <span class="default-val">
        {{ typeof field.defaultValue == 'string' ? field.defaultValue : '' }}
      </span>
    </flexbox>

  </field-wrapper>
</template>

<script>
import FieldWrapper from './FieldWrapper'
import mixins from './mixins'

export default {
  name: 'FieldSerialNumber',
  components: {
    FieldWrapper
  },
  mixins: [mixins],
  methods: {}
}
</script>

<style scoped lang="scss">
.box {
  width: 100%;
  height: 32px;
  padding: 0 10px;
  font-size: 14px;
  background: white;
  border: $--border-medium;
  border-radius: $--border-radius-base;

  .default-val {
  }
}
</style>
