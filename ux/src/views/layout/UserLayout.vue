<template>
  <el-container>
    <el-header class="nav-container">
      <navbar
        title="基本信息"
        @nav-items-click="navClick" />
    </el-header>
    <el-container>
      <el-main
        id="crm-main-container"
        style="padding: 15px;">
        <app-main />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { Navbar, AppMain } from './components'

export default {
  name: 'UserLayout',
  components: {
    Navbar,
    AppMain
  },
  data() {
    return {}
  },
  computed: {
  },
  methods: {
    navClick(index) {}
  }
}
</script>

<style lang="scss" scoped>
.el-container {
  height: 100%;
  min-height: 0;
}

.aside-container {
  position: relative;
  box-sizing: border-box;
  background-color: #2d3037;
}

.nav-container {
  z-index: 100;
  min-width: 1200px;
  padding: 0;
  box-shadow: 0 1px 2px #dbdbdb;
}
</style>
