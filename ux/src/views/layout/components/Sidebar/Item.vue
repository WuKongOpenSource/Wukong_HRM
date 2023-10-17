<template>
  <div
    :class="{ 'is-close': collapse, 'is-select': selectd }"
    class="menu-item-content">
    <i
      v-if="icon"
      :class="iconClass" />
    <span class="side-bar-label">{{ title }}</span>
    <span v-if="count">({{ count }})</span>
    <el-badge
      v-if="num && num > 0"
      :max="99"
      :value="num"
      type="primary" />
    <slot />
  </div>
</template>

<script>

export default {
  name: 'Item',
  components: {},
  props: {
    icon: String,
    title: String,
    num: [String, Number],
    collapse: Boolean,
    count: [String, Number],
    selectd: Boolean // 展示选择状态
  },
  data() {
    return {}
  },
  computed: {
    iconClass() {
      if (this.icon && (this.icon.startsWith('wk ') || this.icon.startsWith('el- ') || this.icon.startsWith('fm '))) {
        return this.icon
      }
      return `wk wk-${this.icon}`
    }
  },
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {}
}
</script>

<style lang="scss" scoped>
@import "./variables.scss";

.menu-item-content {
  position: relative;
  width: 100%;
  padding: 6px 32px 6px 10px;
  margin: 5px 0;
  overflow: hidden;
  line-height: 1.5;
  color: $menuText;
  text-overflow: ellipsis;
  cursor: pointer;
  border-radius: $--border-radius-base;

  [class^="wk wk-"] {
    font-size: 16px;
    color: $menuIcon;
  }

  &.is-select {
    background-color: $menuHover;

    [class^="wk wk-"] {
      color: $--color-primary;
    }

    [class^="fm wk-"] {
      color: $--color-primary;
    }

    .side-bar-label {
      color: $--color-primary;
    }
  }

  &:hover {
    background-color: $menuHover;
  }

  &:focus,
  &:active {
    [class^="wk wk-"] {
      color: $--color-primary;
    }

    .side-bar-label {
      color: $--color-primary;
    }
  }
}

.menu-item-content.is-close {
  .el-badge {
    position: absolute;
    top: 6px;
    right: auto;
    left: 50%;
    transform: translateX(-50%);
  }

  .side-bar-label {
    display: none;
    opacity: 0;
  }
}

.wk,
.fm {
  margin-right: 8px;
}

.side-bar-label {
  opacity: 1;
  transition: transform 0.3s;
}

// 消息数
.el-badge {
  position: absolute;
  top: 16px;
  right: 8px;
  line-height: normal;

  ::v-deep .el-badge__content {
    border-width: 0;
  }
}
</style>
