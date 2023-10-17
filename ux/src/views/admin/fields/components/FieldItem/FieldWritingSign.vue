<template>
  <field-wrapper
    :activate="activate"
    :field="field"
    :control-flag="controlFlag"
    class="field-writing-sign"
    @click="emitClick"
    @action="handleAction">

    <div class="box" />

  </field-wrapper>
</template>

<script>
import FieldWrapper from './FieldWrapper'
import mixins from './mixins'

export default {
  name: 'FieldWritingSign',
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
  height: 150px;
  padding: 10px 0;
  background: white;
  border: $--border-medium;
  border-radius: $--border-radius-base;
}
</style>
