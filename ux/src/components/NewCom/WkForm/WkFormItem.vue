<template>
  <el-form-item
    v-if="getShowValue(item)"
    :key="index"
    :prop="`${propPrefix || ''}${item.field}`"
    :rules="item.rules"
    :class="[item.className || '', `is-${item.formType}`]"
    :style="{width: stylePercent ? stylePercent : (item.stylePercent ? `${item.stylePercent}%` : 'auto')}"
    class="wk-form-item">
    <template v-if="item.formType !== 'field_group'" slot="label">{{ item.name }}<el-tooltip
      v-if="item.tipType == 'tooltip'"
      effect="dark"
      placement="top">
      <div slot="content" v-html="getTips(item)" />
      <i class="wk wk-icon-fill-help wk-help-tips" />
    </el-tooltip>
      <i v-else-if="item.tipType == 'system-help' || item.helpType" class="wk wk-icon-fill-help wk-help-tips" :data-type="item.helpType" :data-id="item.helpId" />
      <span v-else class="label-tips">
        {{ getTips(item) }}
      </span>
    </template>
    <wk-field
      :item="item"
      :index="index"
      :field-from="fieldFrom"
      :ignore-fields="ignoreFields"
      :disabled="item.disabled || disabled"
      @change="fieldChange"
    >
      <template slot-scope="slotProps">
        <slot :data="slotProps.data" :index="slotProps.index" />
      </template>
    </wk-field>
  </el-form-item>
</template>

<script>
import WkField from './WkField'

import Mixin from './Mixin'

export default {
  // item
  name: 'WkFormItem',

  components: {
    WkField
  },

  mixins: [Mixin],

  props: {
    // 表单验证前缀
    propPrefix: {
      type: String,
      default: ''
    },
    item: Object,
    index: Number,
    fieldFrom: {
      type: Object,
      default: () => {
        return {}
      }
    },
    // 忽略的字段直接输出
    ignoreFields: {
      type: Array,
      default: () => {
        return []
      }
    },
    stylePercent: String,
    disabled: Boolean
  },

  data() {
    return {
    }
  },

  computed: {},

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    fieldChange(item, index, value, valueList) {
      this.$emit('change', item, index, value, valueList)
    }
  }
}
</script>

<style lang="scss">
.wk-form-item {
  .el-form-item__label {
    padding-bottom: 4px !important;
    line-height: 1.5;
    word-break: break-all;
    word-wrap: break-word;
  }

  .el-form-item__content {
    line-height: $--input-height;
  }

  .el-form-item__error {
    position: relative;
    top: auto;
    left: auto;
  }

  .el-form-item.is-desc_text {
    .el-form-item__label {
      display: none;
    }
  }
}
</style>

<style lang="scss" scoped>
.wk-form-item {
  padding: 12px 12px 0;
  margin-bottom: 0;
}

.el-form-item.is-user {
  display: flex;
  flex-direction: column;
}

.el-form-item.is-structure {
  display: flex;
  flex-direction: column;
}

.label-tips {
  color: $--color-text-placeholder;
}
</style>
