<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <div>
    <div class="title">HRM工作台</div>
    <flexbox
      class="workbench"
      align="stretch">

      <div class="workbench__left">
        <wb-personnel />
        <wb-recruit />
        <wb-salary />
        <!-- <wb-performance /> -->
      </div>

      <div class="workbench__right">
        <wb-backlog />
        <wb-calendar />
      </div>
    </flexbox>
  </div>
</template>

<script>
import WbPersonnel from '../components/WbPersonnel'
import WbRecruit from '../components/WbRecruit'
import WbBacklog from '../components/WbBacklog'
import WbSalary from '../components/WbSalary'
// import WbPerformance from '../components/WbPerformance'

import WbCalendar from '../components/WbCalendar'

export default {
  // 工作台
  name: 'ManagerWorkbench',
  components: {
    WbPersonnel,
    WbRecruit,
    WbSalary,
    // WbPerformance,

    WbBacklog,
    WbCalendar
  },
  props: {},
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
.title {
  font-size: 24px;
  line-height: 28px;
  color: #182b4e;
}

.workbench {
  padding-bottom: 40px;
  margin-top: 16px;

  &__left {
    flex: 1;
    flex-shrink: 0;
    min-width: 700px;
  }

  &__right {
    flex-shrink: 0;
    width: 450px;
    margin-left: 15px;
  }

  .el-card + .el-card,
  .statistics-card + .statistics-card {
    margin-top: 15px;
    border-top: none;
  }

  ::v-deep .statistics-card {
    border: 0;
    box-shadow: $--box-shadow-bottom-light;
  }
}
</style>
