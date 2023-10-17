<template>
  <field-wrapper
    :activate="activate"
    :field="field"
    :control-flag="controlFlag"
    class="field-current-position"
    @click="emitClick"
    @action="handleAction">

    <flexbox align="center" class="box">
      <flexbox-item class="default-val" />
      <span class="wk wk-icon-location" />
    </flexbox>

  </field-wrapper>
</template>

<script>
import FieldWrapper from './FieldWrapper'
import mixins from './mixins'

export default {
  name: 'FieldCurrentPosition',
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

  .wk-icon-location {
    color: $--color-text-secondary;
  }
}
</style>
