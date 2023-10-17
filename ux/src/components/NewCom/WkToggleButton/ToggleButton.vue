<template>
  <div class="wk-toggle-button">
    <div class="wk-toggle-button__bd">
      <slot />
    </div>
  </div>
</template>

<script>
import { getValueByPath, valueEquals } from 'element-ui/src/utils/util'

export default {
  // WkToggleButton
  name: 'WkToggleButton',

  components: {},

  provide() {
    return {
      'toggleButton': this
    }
  },

  props: {
    valueKey: {
      type: String,
      default: 'value'
    },
    value: {
      type: [Boolean, String, Number],
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },

  data() {
    return {
      cachedOptions: [],
      selectedLabel: '',
      selected: {}
    }
  },

  computed: {},

  watch: {
    value() {
      this.setSelected()
    }
  },

  created() {
    this.$on('handleOptionClick', this.handleOptionSelect)
    this.$on('setSelected', this.setSelected)
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    handleOptionSelect(option) {
      this.$emit('input', option.value)
      this.emitChange(option.value)
    },

    emitChange(val) {
      if (!valueEquals(this.value, val)) {
        this.$emit('change', val)
      }
    },

    setSelected() {
      const option = this.getOption(this.value)
      this.selectedLabel = option.currentLabel
      this.selected = option
    },

    getOption(value) {
      let option
      const isObject = Object.prototype.toString.call(value).toLowerCase() === '[object object]'
      const isNull = Object.prototype.toString.call(value).toLowerCase() === '[object null]'
      const isUndefined = Object.prototype.toString.call(value).toLowerCase() === '[object undefined]'

      for (let i = this.cachedOptions.length - 1; i >= 0; i--) {
        const cachedOption = this.cachedOptions[i]
        const isEqual = isObject
          ? getValueByPath(cachedOption.value, this.valueKey) === getValueByPath(value, this.valueKey)
          : cachedOption.value === value
        if (isEqual) {
          option = cachedOption
          break
        }
      }
      if (option) return option
      const label = (!isObject && !isNull && !isUndefined)
        ? String(value) : ''
      const newOption = {
        value: value,
        currentLabel: label
      }
      return newOption
    }

  }
}
</script>

<style lang="scss" scoped>
.wk-toggle-button {
  display: inline-block;

  &__bd {
    position: relative;
    display: flex;
    flex-wrap: wrap;
    width: max-content;
    max-width: 100%;
    padding: 4px;
    font-size: 12px;
    font-weight: 600;
    line-height: 1.3333;
    text-transform: uppercase;
    background-color: $--background-color-base;
    border-radius: 3px;
  }
}
</style>
