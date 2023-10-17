<template>
  <div :class="{ 'is-collapse': collapse }" class="sidebar-container">
    <slot v-if="!collapse" name="header" />
    <el-scrollbar
      :style="{
        'border-right-color': variables.menuBg,
        'padding-top': paddingTop,
        'height': menusHeight
      }"
      wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="collapse"
        :text-color="variables.menuText"
        :active-text-color="variables.menuActiveText"
        :style="{ paddingBottom: paddingBottom}"
        mode="vertical"
        class="el-menu-vertical"
        @select="handleSelect">
        <sidebar-item
          v-for="(route, index) in items"
          :key="`${route.path}${index}`"
          :item="route"
          :collapse="collapse"
          :base-path="route.path"
          :active-menu="activeMenu" />
      </el-menu>
    </el-scrollbar>
    <slot name="bottom" />

    <!-- 控制宽度条 -->
    <div
      ref="wkControlBar"
      :class="{'is-close': collapse}"
      class="wk-side-bar">
      <div class="wk-side-bar__shadow" />
      <div class="wk-side-bar__control">
        <el-button
          :class="{'is-close': collapse}"
          icon="el-icon-arrow-right"
          circle
          @click="toggleSideBarClick" />
      </div>
      <div class="wk-side-bar__control-line" />
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import SidebarItem from './SidebarItem'
import variables from './variables.scss'

export default {
  components: { SidebarItem },
  props: {
    items: {
      type: Array,
      default: () => {
        return []
      }
    },
    paddingTop: {
      type: String,
      default: '0'
    },
    // 防止菜单被底部布局遮住
    paddingBottom: {
      type: String,
      default: '48px'
    },
    menusHeight: {
      type: String,
      default: 'calc(100% - 70px)'
    }
  },
  data() {
    return {
      buttonCollapse: false
    }
  },
  computed: {
    ...mapGetters(['collapse']),
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    variables() {
      return variables
    }
  },
  watch: {
    collapse: function(val) {
      if (val) {
        this.buttonCollapse = val
      } else {
        setTimeout(() => {
          this.buttonCollapse = val
        }, 300)
      }
    }
  },
  mounted() {
    this.buttonCollapse = this.collapse
  },
  methods: {
    /**
     * 开关
     */
    collapseClick() {
      this.collapse = !this.collapse
    },

    toggleSideBarClick() {
      this.$store.commit('SET_COLLAPSE', !this.collapse)
    },

    // 快速创建
    quicklyCreate() {
      this.$emit('quicklyCreate')
    },

    handleSelect(key, keyPath) {
      this.$emit('select', key, keyPath)
    }
  }
}
</script>
<style lang="scss" scoped>
@import "./variables.scss";

.sidebar-container {
  position: relative;
  flex-shrink: 0;
  width: auto;
  height: 100%;
  background-color: $menuBg;
  border-right: 1px solid $--border-color-base;
  transition: width 0.28s;

  ::v-deep .el-scrollbar {
    .scrollbar-wrapper {
      overflow-x: hidden !important;
    }
  }

  a {
    display: inline-block;
    width: 100%;
    overflow: hidden;
  }

  &:hover {
    .wk-side-bar {
      .el-button {
        opacity: 1;
      }
    }
  }
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}

.el-menu-vertical {
  height: 100%;
  padding-bottom: 48px;
  overflow-x: hidden;
  overflow-y: auto;
  overflow-y: overlay;
  border-right-color: $menuBg;

  ::v-deep .el-submenu__title {
    &:hover {
      background-color: white;
    }
  }
}

.el-menu-vertical.el-menu--collapse {
  ::v-deep .el-submenu__icon-arrow {
    display: none;
  }

  ::v-deep .el-submenu__title {
    span {
      display: none;
    }
  }

  ::v-deep .menu-item-content {
    text-align: center;
    text-overflow: clip;

    [class^="wk wk-"] {
      margin-right: 0;
    }
  }
}

// 控制条
.wk-side {
  &-bar {
    position: absolute;
    top: 0;
    bottom: 0;
    left: calc(100% - 2px);
    z-index: 1;
    width: 24px;
    outline: none;

    // &:not(.is-close) {
    //   cursor: ew-resize;
    // }
    &:not(.is-close):hover {
      .wk-side-bar__control-line {
        background-color: $--color-b100;
      }
    }

    // &__shadow {
    /* stylelint-disable-next-line max-line-length */
    //   background: linear-gradient(to left, rgba(0,0,0,0.2) 0px, rgba(0,0,0,0.2) 1px, rgba(0,0,0,0.1) 1px, rgba(0,0,0,0) 100% );
    //   bottom: 0;
    //   top: 0;
    //   left: -1px;
    //   opacity: 0.5;
    //   pointer-events: none;
    //   position: absolute;
    //   transition-duration: 0.22s;
    //   transition-property: left,opacity,width;
    //   transition-timing-function: cubic-bezier(0.2,0,0,1);
    //   width: 3px;
    // }

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

