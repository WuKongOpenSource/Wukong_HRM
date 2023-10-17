<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in levelList" :key="item.path">
        <span v-if="item.redirect==='noRedirect'||index==levelList.length-1" class="no-redirect">{{ item.meta.title }}</span>
        <a v-else :class="{ 'is-disabled': disabledIndexs && disabledIndexs.includes(index) }" @click.prevent="handleLink(item, index)">{{ item.meta.title }}</a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script>
import pathToRegexp from 'path-to-regexp'

export default {
  props: {
    disabledIndexs: Array,
    defaultLevel: Object
  },
  data() {
    return {
      levelList: null // 例如 { path: '/dashboard', meta: { title: 'Dashboard' }}
    }
  },
  watch: {
    $route() {
      this.getBreadcrumb()
    }
  },
  created() {
    this.getBreadcrumb()
  },
  methods: {
    getBreadcrumb() {
      // only show routes with meta.title
      let matched = this.$route.matched.filter(item => item.meta && item.meta.title)

      if (this.defaultLevel) {
        if (matched.length === 1) {
          matched = [this.defaultLevel].concat(matched)
        } else if (matched.length > 1) {
          const firstItem = matched[0]
          if (firstItem.path === this.defaultLevel.path) {
            matched[0] = this.defaultLevel
          }
        }
      }

      this.levelList = matched.filter(item => item.meta && item.meta.title && item.meta.breadcrumb !== false)
    },
    pathCompile(path) {
      // To solve this problem https://github.com/PanJiaChen/vue-element-admin/issues/561
      const { params } = this.$route
      var toPath = pathToRegexp.compile(path)
      return toPath(params)
    },
    handleLink(item, index) {
      if (this.disabledIndexs && this.disabledIndexs.includes(index)) return
      const { redirect, path } = item
      if (redirect) {
        this.$router.push(redirect)
        return
      }
      this.$router.push(this.pathCompile(path))
    }
  }
}
</script>

<style lang="scss" scoped>
.app-breadcrumb.el-breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: 24px;

  .no-redirect {
    color: $--color-n200;
    cursor: text;
  }

  a.is-disabled {
    pointer-events: none;
    opacity: 0.5;
  }
}
</style>
