<template>
  <div :style="{width: contentWidth}" class="wk-side-menu wk-side-wrap">
    <div ref="sideContent" :class="{'is-close': collapse}" class="wk-side-content">
      <div v-if="$slots.header" :style="{'min-width': width}" class="wk-side__header">
        <slot name="header" />
      </div>
      <div :style="{'min-width': width}" class="wk-side__body">
        <transition
          name="menu-fade">
          <el-scrollbar
            :style="{'border-right-color': variables.menuBg}"
            wrap-class="scrollbar-wrapper">
            <el-menu
              :default-active="activeMenu"
              :text-color="variables.menuText"
              :active-text-color="variables.menuActiveText"
              mode="vertical"
              class="el-menu-vertical"
              @select="handleSelect">
              <sidebar-item
                v-for="(route, index) in menus"
                :key="`${route.path}${index}`"
                :item="route"
                :base-path="route.path"
                :active-menu="activeMenu" />
            </el-menu>
          </el-scrollbar>
        </transition>
      </div>
      <div v-if="$slots.footer" class="wk-side__footer">
        <slot name="footer" />
      </div>
    </div>

    <div
      ref="wkControlBar"
      :class="{'is-close': collapse}"
      class="wk-side-bar"
      @mousedown="mousedown">
      <div class="wk-side-bar__shadow" />
      <div class="wk-side-bar__control">
        <el-button
          :class="{'is-close': collapse}"
          icon="el-icon-arrow-right"
          circle
          @click="collapseClick" />
      </div>
      <div class="wk-side-bar__control-line" />
    </div>
  </div>
</template>

<script>
import SidebarItem from './SidebarItem'
import variables from './variables.scss'

export default {
  // 边菜单
  name: 'WkSideMenu',

  components: {
    SidebarItem
  },

  props: {
    width: {
      type: String,
      default: '240px'
    },
    menus: Array
  },

  data() {
    return {
      collapse: false,
      initLeft: 0,
      dragWidth: 0
    }
  },

  computed: {
    variables() {
      return variables
    },
    activeMenu() {
      const route = this.$route
      const { meta, fullPath } = route
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return fullPath
    },
    contentWidth() {
      return this.collapse ? '20px' : (this.dragWidth > 20 ? `${this.dragWidth}px` : this.width)
    }
  },

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 开关
     */
    collapseClick() {
      this.collapse = !this.collapse
    },

    handleSelect(key, keyPath) {
      this.$emit('select', key, keyPath)
    },

    /**
     * 鼠标拖动
     */
    mousedown(e) {
      const sideContent = this.$refs.sideContent
      if (!Array.from(sideContent.classList || []).includes('is-close')) {
        const odiv = e.target
        const disX = e.clientX - odiv.offsetLeft
        e.preventDefault()
        const maxWidth = document.body.clientWidth / 2

        document.onmousemove = (e) => {
          this.dragWidth = e.clientX - disX
        }
        document.onmouseup = (e) => {
          const left = e.clientX - disX
          this.dragWidth = maxWidth < left ? maxWidth : left
          document.onmousemove = null
          document.onmouseup = null
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "./variables.scss";

.wk-side {
  &-wrap {
    position: relative;
    z-index: 1;
    flex-shrink: 0;
    height: 100%;
    background-color: $menuBg;
    transition: width 300ms cubic-bezier(0.2, 0, 0, 1) 0s;
    transition: width 0.28s;

    .el-menu {
      border-right: none;
    }

    &:hover {
      .wk-side-bar {
        .el-button {
          opacity: 1;
        }
      }
    }
  }

  &-content {
    display: flex;
    flex-direction: column;
    height: 100%;
    overflow-x: hidden;
    opacity: 1;
    transition: opacity 350ms cubic-bezier(0.2, 0, 0, 1);

    &.is-close {
      opacity: 0;
    }
  }

  &__header {
    position: relative;
    padding: 24px 8px 8px;

    &::after {
      position: absolute;
      right: 0;
      bottom: 0;
      left: 0;
      display: block;
      height: 2px;
      content: "";
      background-color: $--border-color-base;
      border-radius: 1px;
    }
  }

  &__body {
    position: relative;
    height: 100%;

    ::v-deep .el-submenu__title {
      &:hover {
        background-color: white;
      }
    }
  }

  &__footer {
    position: relative;
    height: 100%;

    &::before {
      position: absolute;
      top: 0;
      right: 0;
      left: 0;
      display: block;
      height: 2px;
      content: "";
      background-color: #fafbfc;
      border-radius: 1px;
    }
  }

  &-bar {
    position: absolute;
    top: 0;
    bottom: 0;
    left: calc(100% - 2px);
    width: 24px;
    outline: none;

    &:not(.is-close) {
      cursor: ew-resize;
    }

    &:not(.is-close):hover {
      .wk-side-bar__control-line {
        background-color: $--color-b100;
      }
    }

    &__shadow {
      position: absolute;
      top: 0;
      bottom: 0;
      left: -1px;
      width: 3px;
      pointer-events: none;
      background:
        linear-gradient(
          to left,
          rgba(0, 0, 0, 0.2) 0,
          rgba(0, 0, 0, 0.2) 1px,
          rgba(0, 0, 0, 0.1) 1px,
          rgba(0, 0, 0, 0) 100%
        );
      opacity: 0.5;
      transition-timing-function: cubic-bezier(0.2, 0, 0, 1);
      transition-duration: 0.22s;
      transition-property: left, opacity, width;
    }

    &__control {
      position: absolute;
      top: 50px;
      left: -11px;

      ::v-deep.el-button {
        width: 24px;
        height: 24px;
        padding: 4px;
        font-weight: bold;
        color: $--color-n200;
        background-color: $--color-white;
        box-shadow: 0 0 0 1px $--color-n30A, 0 2px 4px 1px $--color-n30A;
        opacity: 0;
        transition: background-color 100ms linear, color 100ms linear, opacity 350ms cubic-bezier(0.2, 0, 0, 1);
        transform: rotate(180deg);

        .el-icon-arrow-right {
          font-size: 12px;
        }

        &.is-close {
          transform: rotate(0deg);
        }

        &:hover {
          color: $--color-white;
          background-color: $--color-b100;
          opacity: 1;
        }
      }
    }

    &__control-line {
      width: 2px;
      height: 100%;
      background-color: transparent;
      transition: background-color 200ms;
    }

    &:hover {
      .el-button {
        opacity: 1;
      }
    }
  }
}
</style>
