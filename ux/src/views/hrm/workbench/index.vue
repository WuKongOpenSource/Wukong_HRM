<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <employee-workbench v-if="componentName === 'EmployeeWorkbench'" />
  <manager-workbench v-else-if="componentName === 'ManagerWorkbench'" />
  <team-workbench v-else-if="componentName === 'TeamWorkbench'" />
</template>

<script>
import EmployeeWorkbench from './employee'
import ManagerWorkbench from './manager'
import TeamWorkbench from './team'

import { mapGetters } from 'vuex'

export default {
  // 工作台
  name: 'Workbench',
  components: {
    EmployeeWorkbench,
    ManagerWorkbench,
    TeamWorkbench
  },
  props: {},
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['hrmUserInfo', 'hrmShowType']), // hrmShowType 1 管理 2 员工
    componentName() {
      if (this.hrmUserInfo) {
        if (this.hrmUserInfo.role) {
          if (this.hrmUserInfo.role.label == 91) { // 管理员
            return {
              1: 'ManagerWorkbench',
              2: 'EmployeeWorkbench'
            }[this.hrmShowType]
          } else if (this.hrmUserInfo.role.label == 92) { // 上级
            return {
              1: 'TeamWorkbench',
              2: 'EmployeeWorkbench'
            }[this.hrmShowType]
          }
        }
        return 'EmployeeWorkbench'
      }

      return ''
    }
  },
  created() {
    if (this.hrmUserInfo && !this.hrmUserInfo.role && !this.$route.path.includes('self-server')) {
      this.$router.replace('/hrm/self-server/workbench')
    }
  },

  beforeDestroy() {},
  methods: {}
}
</script>

<style lang="scss" scoped>
.app-main-content {
  height: 100%;
  overflow-y: auto;
}
</style>
