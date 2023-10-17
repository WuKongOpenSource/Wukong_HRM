<template>
  <el-container :class="className">
    <template v-if="menu && menu.length > 0">
      <wk-side-menu
        v-if="sideType === 'simple'"
        :menus="menu"
      >
        <flexbox v-if="headerObj" slot="header" class="header-cell">
          <flexbox justify="center" class="header-cell__hd">
            <i :class="headerObj.icon" />
          </flexbox>
          <div class="header-cell__bd">
            <div class="header-cell--label">{{ headerObj.label }}</div>
            <div class="header-cell--des">{{ headerObj.des }}</div>
          </div>
        </flexbox>
      </wk-side-menu>

      <sidebar
        v-else-if="sideType === 'normal'"
        :items="menu">
        <flexbox v-if="headerObj" slot="header" class="header-cell is-normal">
          <flexbox justify="center" class="header-cell__hd">
            <i :class="headerObj.icon" />
          </flexbox>
          <div class="header-cell__bd">
            <div class="header-cell--label text-one-line">{{ headerObj.label }}</div>
            <div class="header-cell--des text-one-line">{{ headerObj.des }}</div>
          </div>
        </flexbox>
      </sidebar>
    </template>

    <slot />
    <slot name="right" />
  </el-container>
</template>

<script>
import WkSideMenu from './Sidebar/Menu'
import Sidebar from './Sidebar/index'

export default {
  // 容器
  name: 'WkContainer',

  components: {
    WkSideMenu,
    Sidebar
  },

  props: {
    sideType: {
      type: String,
      default: 'simple' // simple 一级简单效果  normal 正常的左侧菜单
    },
    menu: Array,
    headerObj: Object,
    className: String
  },

  data() {
    return {
    }
  },

  computed: {
    showGuideBtn() {
      const { path } = this.$route
      if (path.includes('crm/')) {
        return true
      }
      return false
    }
  },

  watch: {},

  created() {
  },

  mounted() {
  },

  beforeDestroy() {
  },

  methods: {
  }
}
</script>

<style lang="scss" scoped>
@import "style";
</style>
