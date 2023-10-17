<template>
  <div
    v-if="!item.hidden"
    class="menu-wrapper">
    <template v-if="hasOneShowingChild(item.children,item) && (!onlyOneChild.children||onlyOneChild.noShowingChildren)&&!item.alwaysShow">
      <el-tooltip
        :disabled="!collapse"
        :content="onlyOneChild.meta.title"
        effect="dark"
        placement="right">
        <app-link
          v-if="onlyOneChild.meta"
          :to="resolvePath(onlyOneChild.path)">
          <el-menu-item
            :index="resolvePath(onlyOneChild.path)">
            <item
              :class="{ 'is-select': getItemSelected(onlyOneChild)}"
              :icon="onlyOneChild.meta.icon||(item.meta&&item.meta.icon)"
              :title="onlyOneChild.meta.title"
              :num="onlyOneChild.meta.num"
              :count="onlyOneChild.count"
              :collapse="collapse" />
          </el-menu-item>
        </app-link>
      </el-tooltip>
    </template>

    <el-submenu
      v-else
      ref="subMenu"
      :index="resolvePath(item.path)"
      popper-append-to-body>
      <template slot="title">
        <item
          v-if="item.meta"
          :class="{ 'is-select': getItemSelected(item)}"
          :icon="item.meta && item.meta.icon"
          :title="item.meta.title"
          :num="item.meta.num" />
      </template>
      <sidebar-item
        v-for="child in item.children"
        :key="child.path"
        :is-nest="true"
        :item="child"
        :base-path="resolvePath(child.path)"
        :active-menu="activeMenu"
        class="nest-menu" />
    </el-submenu>
  </div>
</template>

<script>
import path from 'path'
import { isExternal } from '@/utils/validate'
import Item from './Item'
import AppLink from './Link'
import FixiOSBug from './FixiOSBug'
import variables from './variables.scss'

export default {
  name: 'SidebarItem',
  components: { Item, AppLink },
  mixins: [FixiOSBug],
  props: {
    // route object
    item: {
      type: Object,
      required: true
    },
    basePath: {
      type: String,
      default: ''
    },
    activeMenu: String,
    collapse: Boolean
  },
  data() {
    // To fix https://github.com/PanJiaChen/vue-admin-template/issues/237
    // TODO: refactor with render function
    this.onlyOneChild = null
    return {}
  },
  computed: {
    variables() {
      return variables
    }
  },
  methods: {
    hasOneShowingChild(children = [], parent) {
      const showingChildren = children.filter(item => {
        if (item.hidden) {
          return false
        } else {
          // Temp set(will be used if only has one showing child)
          this.onlyOneChild = item
          return true
        }
      })

      // When there is only one child router, the child router is displayed by default
      if (showingChildren.length === 1) {
        return true
      }

      // Show parent if there are no child router to display
      if (showingChildren.length === 0) {
        this.onlyOneChild = { ...parent, path: '', noShowingChildren: true }
        return true
      }

      return false
    },
    resolvePath(routePath) {
      if (isExternal(routePath)) {
        return routePath
      }
      if (isExternal(this.basePath)) {
        return this.basePath
      }
      return path.resolve(this.basePath, routePath)
    },

    /**
     * @description: 菜单选中状态
     * @param {*} item
     * @return {*}
     */
    getItemSelected(item) {
      const itemMenu = this.resolvePath(item.path)
      const selected = this.activeMenu === itemMenu
      if (!selected) {
        // 去掉query 影响的比较
        return this.activeMenu.split('?')[0] === itemMenu.split('?')[0]
      }
      return selected
    }
  }
}
</script>
<style lang="scss" scoped>
@import "./variables.scss";

.menu-wrapper {
  ::v-deep .el-submenu__title {
    height: auto;
    line-height: normal;
  }
}

::v-deep.el-menu-item {
  height: auto !important;
  line-height: normal !important;

  // padding: 0 8px !important;
  background-color: $menuBg !important;
}

// ::v-deep.el-submenu {
//   [class^="wk wk-"] {
//     color: $menuIcon !important;
//     font-size: 16px;
//   }
// }

// element自带的有问题 is-active 换成 is-select
// ::v-deep.el-menu-item.is-select {
//   .menu-item-content {
//     background-color: $menuHover !important;
//     [class^="wk wk-"] {
//       color: $--color-primary !important;
//     }
//     .side-bar-label {
//       color: $--color-primary !important;
//     }
//   }
// }

// .el-menu-item:hover {
//   .menu-item-content {
//     background-color: $menuHover;
//   }
// }
</style>
