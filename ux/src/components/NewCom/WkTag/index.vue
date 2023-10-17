<template>
  <el-popover
    v-model="showPopover"
    :disabled="disabled"
    placement="bottom"
    width="350"
    popper-class="no-padding-popover"
    trigger="click">
    <tag
      v-if="!disabled"
      ref="tabview"
      :show.sync="showPopover"
      :options="options"
      :props="data.tagProps"
      :selected-data="dataValue"
      @changeCheckout="checkTag" />
    <flexbox
      v-if="!$slots.reference"
      slot="reference"
      :class="[disabled ? 'is_disabled' : 'is_valid', { 'is_focus': showPopover }]"
      wrap="wrap"
      class="tag-container xh-form-border"
      @click.native="focusClick">
      <div
        v-for="(item, index) in showDataValue"
        :key="index"
        :style="{'background-color':item.value}"
        class="tag-item text-one-line">{{ item.name }}
        <i
          class="delete-icon el-icon-close"
          @click.stop="deleteTag(index)" />
      </div>
      <i v-if="dataValue.length > max" class="el-icon-more" />
      <i
        :class="['el-icon-arrow-up']" />
      <div
        v-if="dataValue.length == 0"
        class="add-item text-one-line">{{ placeholder }}</div>
    </flexbox>
    <slot
      v-else
      slot="reference"
      name="reference" />
  </el-popover>

</template>
<script type="text/javascript">
import Tag from './Tag'

import Emitter from 'element-ui/lib/mixins/emitter'

export default {
  name: 'WKTag', // 新建 wk-tag
  components: {
    Tag
  },
  mixins: [Emitter],
  inheritAttrs: false,
  props: {
    value: null,
    data: { // 字段数据
      type: Object,
      default: () => {
        return {}
      }
    },
    options: { // 标签选项
      type: Array,
      default: () => {
        return []
      }
    },
    disabled: { // 是否禁用
      type: Boolean,
      default: false
    },
    placeholder: {
      type: String,
      default: '选择标签'
    },
    /** 展示值数量 */
    max: {
      type: Number,
      default: 3
    }
  },
  data() {
    return {
      showPopover: false, // 展示popover
      dataValue: [] // 选择数据
    }
  },
  computed: {
    showDataValue() {
      if (this.dataValue && this.dataValue.length > this.max) {
        return this.dataValue.slice(0, this.max)
      }
      return this.dataValue
    }
  },
  watch: {
    value: {
      handler(val) {
        this.dataValue = val || []
      },
      immediate: true
    }
  },
  mounted() {},
  methods: {
    /**
     * 选中
     */
    checkTag(data) {
      this.dataValue = data
      this.$nextTick(() => {
        this.dispatch('ElFormItem', 'el.form.change', this.dataValue)
      })
      this.$emit('input', data)
      this.$emit('change', {
        value: data
      })
      this.showPopover = false
    },

    /**
     * 删除
     */
    deleteTag(index) {
      if (this.disabled) {
        return
      }
      this.dataValue.splice(index, 1)
      this.$nextTick(() => {
        this.dispatch('ElFormItem', 'el.form.change', this.dataValue)
      })
      this.$emit('input', this.dataValue)
      this.$emit('change', {
        value: this.dataValue
      })
    },

    /**
     * 聚焦动作
     */
    focusClick() {
      this.$emit('focus')
    }
  }
}
</script>
<style lang="scss" scoped>
.tag-container {
  position: relative;
  min-height: $--input-height;
  max-height: 105px;
  padding: 0.5px;
  overflow-y: auto;
  font-size: 12px;
  line-height: 15px;
  cursor: pointer;
  background-color: $--input-background-color;
  border: $--border-medium;
  border-radius: $--border-radius-base;

  .tag-item {
    position: relative;
    max-width: 80px;
    padding: 5px 15px 5px 5px;
    margin: 3px;
    color: #fff;
    cursor: pointer;
    background-color: #f3f7ff;
    border-radius: $--border-radius-base;
  }

  .add-item {
    padding: 5px;
    font-size: $--font-size-base;
    color: $--input-placeholder-color;
    cursor: pointer;
  }

  .delete-icon {
    position: absolute;
    top: 8px;
    right: 2px;
    color: $--color-white;
    cursor: pointer;

    &:hover {
      color: $--color-primary;
    }
  }

  &:hover {
    color: $--form-field-hover-text-color;
    background-color: $--form-field-hover-bg-color;
    border-color: $--form-field-hover-border-color;
  }

  &:focus {
    color: $--color-text-regular;
    background-color: $--form-field-focus-bg-color;
    border-color: $--form-field-focus-border-color;
    outline: none;
  }
}

.tag-container.is_disabled {
  cursor: not-allowed;
  background-color: #f5f7fa;
  border-color: #e4e7ed;

  .tag-item {
    color: #c0c4cc;
    cursor: not-allowed;
    background-color: #f0f4f8ea;
  }

  .delete-icon {
    color: #c0c4cc;
    cursor: not-allowed;
  }

  .add-item {
    color: #c0c4cc;
    cursor: not-allowed;
  }
}

.tag-container.is_valid:hover {
  border-color: #c0c4cc;
}

.tag-container.is_focus {
  border-color: $--color-primary !important;
}

.el-icon-more {
  position: absolute;
  top: 5px;
  right: 20px;
  padding: 6px 10px;
  font-size: 12px;
  color: $--color-text-regular;
  background-color: #f3f7ff;
  border-radius: $--border-radius-base;

  &:hover {
    color: white;
    background-color: $--color-primary;
  }
}

.el-icon-arrow-up {
  position: absolute;
  top: calc(50% - 7px);
  right: 5px;
  font-size: 14px;
  font-weight: bold;
  color: $--input-icon-color;
  cursor: pointer;
  transition: transform 0.3s;
  transform: rotate(180deg);
}

// .el-icon-arrow-up.is-reverse {
//   transform: rotate(0deg);
// }
</style>
