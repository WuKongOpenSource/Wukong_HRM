<template>
  <field-wrapper
    :activate="activate"
    :field="field"
    :control-flag="controlFlag"
    class="field-boolean"
    @click="emitClick"
    @action="handleAction">
    <el-switch
      v-model="field.defaultValue"
      active-value="1"
      inactive-value="0" />
  </field-wrapper>
</template>

<script>
import FieldWrapper from './FieldWrapper'
import mixins from './mixins'

export default {
  name: 'FieldBoolean',
  components: {
    FieldWrapper
  },
  mixins: [mixins],
  methods: {}
}
</script>

<style scoped lang="scss">
.box {
  height: $--input-height;
  font-size: 14px;
  text-align: center;
  background: white;
  border: $--border-medium;
  border-radius: $--border-radius-base;
}
</style>
