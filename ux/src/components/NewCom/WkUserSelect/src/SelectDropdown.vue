<template>
  <div
    :class="[{ 'is-multiple': $parent.multiple }, popperClass]"
    :style="{ minWidth: minWidth }"
    class="el-select-dropdown el-popper">
    <slot />
  </div>
</template>

<script type="text/babel">
import Popper from 'element-ui/lib/utils/vue-popper'

export default {
  name: 'WkSelectDropdown',

  componentName: 'WkSelectDropdown',

  mixins: [Popper],

  props: {
    placement: {
      type: String,
      default: 'bottom-start'
    },

    boundariesPadding: {
      type: Number,
      default: 0
    },

    popperOptions: {
      type: Object,
      default() {
        return {
          gpuAcceleration: false
        }
      }
    },

    visibleArrow: {
      type: Boolean,
      default: false
    },

    appendToBody: {
      type: Boolean,
      default: true
    }
  },

  data() {
    return {
      minWidth: '300px'
    }
  },

  computed: {
    popperClass() {
      return this.$parent.popperClass
    }
  },

  watch: {
    // '$parent.inputWidth'() {
    //   this.minWidth = this.$parent.$el.getBoundingClientRect().width + 'px'
    // }
  },

  mounted() {
    this.referenceElm = this.$parent.$el
    this.$parent.popperElm = this.popperElm = this.$el
    this.$on('updatePopper', () => {
      if (this.$parent.visible) this.updatePopper()
    })
    this.$on('destroyPopper', this.destroyPopper)
  }
}
</script>
