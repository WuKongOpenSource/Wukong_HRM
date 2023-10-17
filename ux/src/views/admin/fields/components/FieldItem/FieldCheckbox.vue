<template>
  <field-wrapper
    :activate="activate"
    :field="field"
    :control-flag="controlFlag"
    class="field-checkbox"
    @click="emitClick"
    @action="handleAction">

    <el-checkbox-group
      v-if="field.precisions === 1"
      v-model="field.defaultValue"
      :disabled="disabled">
      <el-checkbox
        v-for="(item, index) in field.setting"
        :key="index"
        :label="itemIsObject(item) ? item.label || item.name : item"
        class="checkbox" />
    </el-checkbox-group>

    <div
      v-else
      class="select-content">
      <el-select
        v-model="field.defaultValue"
        multiple
        >
        <el-option
          v-for="(item, index) in field.setting"
          :key="index"
          :label="itemIsObject(item) ? item.label || item.name : item"
          :value="itemIsObject(item) ? item.value : item" />
      </el-select>
      <div class="mask" />
    </div>

  </field-wrapper>
</template>

<script>
import FieldWrapper from './FieldWrapper'
import mixins from './mixins'

export default {
  name: 'FieldCheckbox',
  components: {
    FieldWrapper
  },
  mixins: [mixins],
  watch: {
    field: {
      handler() {
        // 兼容老字段，确保状态不变
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

<style scoped lang="scss">
.select-content {
  position: relative;
  width: 100%;

  .mask {
    position: absolute;
    top: 0;
    left: 0;
    z-index: 100;
    display: block;
    width: 100%;
    height: 100%;
    background-color: transparent;
  }

  .el-select {
    width: 100%;
  }
}
</style>
