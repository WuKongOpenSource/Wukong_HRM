<template>
  <field-wrapper
    :activate="activate"
    :field="field"
    :control-flag="controlFlag"
    class="field-map-position"
    @click="emitClick"
    @action="handleAction">

    <flexbox class="box-select">
      <div :class="{placeholder: !Boolean(areaText)}">
        {{ areaText || '请选择' }}
      </div>
      <i class="el-icon-arrow-down el-icon--right" />
    </flexbox>
    <div
      v-if="field.precisions === 1"
      class="box-textarea">
      <div :class="{placeholder: !Boolean(detailAddress)}">
        {{ detailAddress || '详细地址' }}
      </div>
    </div>

  </field-wrapper>
</template>

<script>
import FieldWrapper from './FieldWrapper'
import mixins from './mixins'
import { isEmpty } from '@/utils/types'

export default {
  name: 'FieldPosition',
  components: {
    FieldWrapper
  },
  mixins: [mixins],
  computed: {
    areaText() {
      if (isEmpty(this.field.defaultValue)) return ''
      return this.field.defaultValue
        .filter(o => o.id !== 4)
        .map(o => o.name)
        .join('/')
    },
    detailAddress() {
      if (isEmpty(this.field.defaultValue)) return ''
      const findRes = this.field.defaultValue.find(o => o.id === 4)
      if (!findRes) return ''
      return findRes.name
    }
  },
  methods: {}
}
</script>

<style scoped lang="scss">
.placeholder {
  color: $--color-text-placeholder;
}

.box-select {
  width: 100%;
  padding: 10px;
  border: $--border-medium;
  border-radius: $--border-radius-base;

  div {
    flex: 1;
  }
}

.box-textarea {
  width: 100%;
  height: 100px;
  padding: 10px;
  margin-top: 10px;
  border: $--border-medium;
  border-radius: $--border-radius-base;
}
</style>
