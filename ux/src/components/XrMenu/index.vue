<template>
  <div>
    <xr-menu-item
      v-for="(item, index) in list"
      :key="index"
      :label="item.label"
      :name="item.name"
      :icon-color="item.iconColor"
      :icon-class="item.iconClass"
      @click.native="menuClick(index)" />
  </div>
</template>

<script>
import XrMenuItem from './XrMenuItem'

export default {
  // 菜单
  name: 'XrMenu',
  components: {
    XrMenuItem
  },
  props: {
    value: {
      type: [String, Number],
      default: 0
    },
    list: Array
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {
    menuClick(index) {
      this.$emit('input', index)
      this.$emit('select', index)
    }
  }
}
</script>

<style lang="scss" scoped>
</style>
