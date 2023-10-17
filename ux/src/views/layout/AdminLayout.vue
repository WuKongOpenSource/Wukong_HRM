<template>
  <el-container>
    <el-header class="nav-container">
      <navbar
        title="系统设置"
        @nav-items-click="navClick" />
    </el-header>
    <wk-container
      :menu="showRouters"
      :header-obj="headerCellObj"
      side-type="normal">

      <el-main id="manager-main-container">
        <app-main />
      </el-main>
    </wk-container>
  </el-container>
</template>

<script>
import { mapGetters } from 'vuex'
import { Navbar, AppMain } from './components'
import { filterShowRouters } from '@/router/utils'
import WkContainer from './components/WkContainer'

export default {
  name: 'AdminLayout',
  components: {
    Navbar,
    WkContainer,
    AppMain
  },
  data() {
    return {
      routerItems: [],
      headerCellObj: {
        icon: 'wk wk-icon-all-solid',
        label: '企业管理后台',
        des: '管理配置信息'
      }
    }
  },

  computed: {
    ...mapGetters(['manage', 'manageRouters']),
    showRouters() {
      return filterShowRouters(this.manageRouters)
    }
  },

  mounted() {},

  methods: {
    navClick(index) {}
  }
}
</script>

<style lang="scss" scoped>
@import "./styles/common.scss";

.nav-container {
  min-width: 1200px;
  padding: 0;
  box-shadow: 0 1px 2px #dbdbdb;
}

.el-container {
  height: 100%;
  overflow: hidden;
}

.el-main {
  padding: 0;
}
</style>
