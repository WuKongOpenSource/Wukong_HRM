<template>
  <field-wrapper
    :activate="activate"
    :field="field"
    :control-flag="controlFlag"
    class="field-star"
    @click="emitClick"
    @action="handleAction">
    <div class="box">
      <el-rate
        :value="field.defaultValue || 0"
        @change="change" />
    </div>

  </field-wrapper>
</template>

<script>
import FieldWrapper from './FieldWrapper'
import mixins from './mixins'

export default {
  name: 'FieldAttention',
  components: {
    FieldWrapper
  },
  mixins: [mixins],
  data() { return {} },
  methods: {
    change(val) {
      this.$set(this.field, 'defaultValue', val)
    }
  }
}
</script>

<style scoped lang="scss">
.box {
  width: 100%;
  height: $--input-height;
  padding: 0 10px;
  font-size: 14px;
}
</style>
