<template>
  <div
    class="wk-toggle-item"
    :class="{
      'selected': itemSelected,
      'is-disabled': disabled
    }"
    @click.stop="selectOptionClick">
    <slot>
      <span>{{ currentLabel }}</span>
    </slot>
  </div>
</template>

<script>
import { getValueByPath } from 'element-ui/src/utils/util'

export default {
  // WkToggleItem
  name: 'WkToggleItem',

  components: {},

  inject: ['toggleButton'],

  props: {
    // eslint-disable-next-line vue/require-prop-types
    value: {
      required: true
    },
    label: [String, Number],
    disabled: {
      type: Boolean,
      default: false
    }
  },

  data() {
    return {
    }
  },

  computed: {
    isObject() {
      return Object.prototype.toString.call(this.value).toLowerCase() === '[object object]'
    },

    currentLabel() {
      return this.label || (this.isObject ? '' : this.value)
    },

    itemSelected() {
      return this.isEqual(this.value, this.toggleButton.value)
    }
  },

  watch: {
    currentLabel() {
      this.dispatch('WkToggleButton', 'setSelected')
    },
    value(val, oldVal) {
      const { valueKey } = this.toggleButton
      if (valueKey && typeof val === 'object' && typeof oldVal === 'object' && val[valueKey] === oldVal[valueKey]) {
        return
      }
      this.dispatch('WkToggleButton', 'setSelected')
    }
  },

  created() {
    this.toggleButton.cachedOptions.push(this)
  },

  mounted() {},

  beforeDestroy() {
    const { selected } = this.toggleButton
    const selectedOptions = [selected]
    const index = this.toggleButton.cachedOptions.indexOf(this)
    const selectedIndex = selectedOptions.indexOf(this)

    // if option is not selected, remove it from cache
    if (index > -1 && selectedIndex < 0) {
      this.toggleButton.cachedOptions.splice(index, 1)
    }
  },

  methods: {
    isEqual(a, b) {
      if (!this.isObject) {
        return a === b
      } else {
        const valueKey = this.select.valueKey
        return getValueByPath(a, valueKey) === getValueByPath(b, valueKey)
      }
    },

    selectOptionClick() {
      if (this.disabled !== true) {
        this.dispatch('WkToggleButton', 'handleOptionClick', [this, true])
      }
    },

    dispatch(componentName, eventName, params) {
      var parent = this.$parent || this.$root
      var name = parent.$options.name
      while (parent && (!name || name !== componentName)) {
        parent = parent.$parent

        if (parent) {
          name = parent.$options.name
        }
      }
      if (parent) {
        parent.$emit.apply(parent, [eventName].concat(params))
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-toggle-item {
  display: block;
  padding: 0 5px;
  line-height: 24px;
  cursor: pointer;
  background-color: $--color-n40;
  border-radius: $--border-radius-base;

  &.selected {
    background-color: white;
    border-radius: $--border-radius-base;
    box-shadow: rgba(13, 20, 36, 0.18) 0 3px 5px, rgba(13, 20, 36, 0.29) 0 0 1px !important;
  }

  &:not(.selected):hover {
    background-color: $--color-n40;
    box-shadow: rgba(13, 20, 36, 0.18) 0 3px 5px, rgba(13, 20, 36, 0.29) 0 0 2px !important;
  }
}

.wk-toggle-item + .wk-toggle-item {
  margin-left: 4px;
}
</style>
