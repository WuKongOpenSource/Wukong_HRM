<template>
  <div
    :class="{'is-select':select}"
    class="xr-menu-item">
    <i
      v-if="iconClass"
      :style="{backgroundColor: select ? iconColor || '#2362FB' : '#edf2f6'}"
      :class="['xr-menu-item__icon', iconClass]" />
    <span class="xr-menu-item__label">{{ label }}</span>
    <el-badge
      v-if="num > 0"
      :max="99"
      :value="num" />
  </div>
</template>

<script>
export default {
  // 菜单
  name: 'XrMenuItem',
  components: {},
  props: {
    iconClass: String,
    iconColor: String,
    label: String,
    // 关键字
    name: String,
    num: [String, Number],
    select: Boolean
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {}
}
</script>

<style lang="scss" scoped>
.xr-menu-item {
  position: relative;
  padding: 12px 20px;
  cursor: pointer;

  &__icon {
    display: inline-block;
    width: 25px;
    height: 25px;
    padding: 6px;
    font-size: 12px;
    color: #8a94a6;
    background-color: #edf2f6;
    border-radius: $--border-radius-base;
  }

  &__label {
    margin-left: 10px;
    font-size: 13px;
    color: $--color-text-primary;
  }

  &.is-select {
    .xr-menu-item__icon {
      color: white;
      background-color: $--color-primary;
    }
  }
}

.xr-menu-item.is-select,
.xr-menu-item:hover {
  background-color: $--background-color-base;
}

.xr-menu-item::before {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  width: 2px;
  content: " ";
  background-color: $--color-primary;
  opacity: 0;
}

.xr-menu-item.is-select::before {
  opacity: 1;
}

.el-badge ::v-deep .el-badge__content {
  top: 0;
  border: none;
}

.el-badge {
  position: absolute;
  top: 15px;
  right: 10px;
}
</style>
