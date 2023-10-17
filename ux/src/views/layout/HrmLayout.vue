<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-14 13:58:54
 * @LastEditTime: 2022-10-31 17:25:02
 * @LastEditors: yang
-->
<template>
  <el-container direction="vertical">
    <el-header v-if="hrm && menus && menus.length > 0" class="nav-container">
      <navbar
        :menus="menus"
        title="HRM"
        nav-index="/hrm"
        @select="menuSelect" />
    </el-header>
    <wk-container
      :menu="sideMenus"
      :header-obj="headerCellObj"
    >
      <el-main style="padding: 0;">
        <app-main ref="hrmAppMain">
          <el-button
            v-if="tabShow"
            :icon="tabObj.icon"
            trigger="click"
            class="switch-btn"
            type="primary"
            @mousedown.native="mousedown($event)"
            @click="typeChange(tabType === 1 ? 2 :1 )">{{ `进入${tabObj.title}` }}</el-button>
        </app-main>
      </el-main>
    </wk-container>
    <employee-create-view
      v-if="isCreate"
      @success="createSaveSuccess"
      @close="isCreate=false" />
  </el-container>
</template>

<script>
import { mapGetters } from 'vuex'

import { Navbar, AppMain } from './components'
import WkContainer from './components/WkContainer'
import EmployeeCreateView from '@/views/hrm/employee/components/EmployeeCreateView'

import { getNavMenus } from './components/utils'
import path from 'path'

export default {
  name: 'HrmLayout',
  components: {
    Navbar,
    AppMain,
    EmployeeCreateView,
    WkContainer
  },
  data() {
    return {
      isCreate: false,
      sidebarPopoverVisible: false,
      tabType: 1, // manage 1 employee 2
      sideMenus: []
    }
  },
  computed: {
    ...mapGetters(['hrm', 'hrmRouters', 'collapse', 'hrmUserInfo', 'hrmShowType']),
    // 菜单
    menus() {
      return getNavMenus(this.menuRouters || [], '/hrm')
    },

    // 左侧菜单头部信息
    headerCellObj() {
      const { path } = this.$route
      if (path.includes('recruit')) {
        return {
          icon: 'wk wk-office',
          label: '招聘',
          des: '招聘管理'
        }
      } else if (path.includes('salary')) {
        return {
          icon: 'wk wk-payment',
          label: '薪资',
          des: '薪资管理'
        }
      } else if (path.includes('performance')) {
        return {
          icon: 'wk wk-results-solid',
          label: '绩效',
          des: '绩效管理'
        }
      } else if (path.includes('MyPerformanceArchives')) {
        return {
          icon: 'wk wk-results-solid',
          label: '绩效',
          des: '绩效管理'
        }
      } else if (path.includes('attendance')) {
        return {
          icon: 'wk wk-icon-task-line',
          label: '考勤',
          des: '考勤管理'
        }
      }
      return null
    },

    // 新建权限
    createName() {
      const permission = this.hrm.employee && this.hrm.employee.save
      return permission && this.tabType === 1 ? '快速创建' : ''
    },

    menuRouters() {
      if (!this.hrmUserInfo) {
        return []
      }
      return this.hrmRouters.filter(item => {
        if (item.isAll) {
          return true
        }
        return this.tabType === 1 ? !item.isEmployee : item.isEmployee
      })
    },

    tabObj() {
      // 要进入的类型 与实际值 相反
      return this.tabType === 2 ? {
        icon: 'wk wk-s-seas',
        title: '管理端'
      } : {
        icon: 'wk wk-user',
        title: '员工端'
      }
    },

    // 有管理或者上级角色
    tabShow() {
      return this.hrmUserInfo && this.hrmUserInfo.role !== null
    },

    // 当前人在人力资源内
    hasAddHrm() {
      return this.hrmUserInfo && !!this.hrmUserInfo.employeeId
    }
  },
  watch: {
    collapse() {
      this.sidebarPopoverVisible = false
    },

    tabType() {
      this.$store.commit('SET_HRMSHOWTYPE', this.tabType)
    },

    hrmUserInfo: {
      handler() {
        this.setShowType()
      },
      deep: true
    },

    $route: {
      handler(to) {
        if (to.fullPath) {
          if (to.fullPath.includes('self-server') && this.tabType !== 2) {
            this.tabType = 2
          } else if (!to.fullPath.includes('self-server') && this.tabType !== 1) {
            this.tabType = 1
          }

          if (this.hrmShowType !== this.tabType) {
            this.$store.commit('SET_HRMSHOWTYPE', this.tabType)
          }
        }
      },
      immediate: true
    }
  },
  created() {
    if (this.hrm) {
      if (!this.hrmUserInfo) {
        this.$store.dispatch('GetHrmUserInfo').then(() => {
          this.setShowType()
        }).catch(() => {
        })
      } else {
        this.setShowType()
      }
    }
  },
  methods: {
    /**
     * 菜单选择
     */
    menuSelect(menu) {
      const router = this.menuRouters[menu.index]
      let children = []

      if ((router && router.children && router.children.length > 1) ||
      (router && router.children && router.children.length === 1 && router.alwaysShow)) {
        children = router.children.filter(item => !item.hidden)
      }

      if (children.length > 1 || (router.alwaysShow && children.length > 0)) {
        this.sideMenus = this.getSideMenus(router.path, children)
      } else {
        this.sideMenus = []
      }
    },

    /**
     * 获取siderMenus
     */
    getSideMenus(mainPath, children) {
      const sideMenus = []
      children.forEach(item => {
        if (!item.hidden) {
          sideMenus.push({
            ...item,
            path: path.resolve(mainPath, item.path)
          })
        }
      })
      return sideMenus
    },

    /**
     * 设置展示类型
     */
    setShowType() {
      if (this.tabShow) {
        this.tabType = this.hrmShowType
      } else {
        this.tabType = this.tabShow ? 1 : 2
      }

      // 如果在员工下刷新 默认展示员工
      if (this.$route.fullPath.includes('self-server')) {
        this.tabType = 2
      }

      // 如果没有加入人资员工管理，当前需展示员工端，有管理权限跳转2，没管理权限跳首页
      if (!this.hasAddHrm && this.tabType === 2) {
        if (this.tabShow) {
          this.tabType = 1
        } else {
          this.$router.push('/')
        }
      }
    },

    /**
     * 快捷添加
     */
    quicklyCreate() {
      this.isCreate = true
    },

    /**
     * 创建成功刷新
     */
    createSaveSuccess() {
      const hrmAppMain = this.$refs.hrmAppMain
      if (hrmAppMain && hrmAppMain.$children && hrmAppMain.$children.length) {
        const appSubMain = hrmAppMain.$children[0]
        if (appSubMain.$options.name == 'EmployeeIndex') {
          appSubMain.createSaveSuccess()
        }
      }
    },

    /**
     * 类型切换
     */
    typeChange(type) {
      // 没有员工端权限，进行提示
      if (type == 2 && !this.hrmUserInfo.employeeId) {
        if (this.hrm && this.hrm.employee && this.hrm.employee.save) {
          this.$alert('您尚未开通员工端，请先将您本人添加至<strong>人力资源-员工管理</strong>', '提示', {
            dangerouslyUseHTMLString: true,
            showCancelButton: true,
            confirmButtonText: '添加员工',
            callback: action => {
              if (action === 'confirm') {
                this.$router.push({
                  name: 'hrmEmployee',
                  query: {
                    depAddEmplogyShow: true,
                    time: Date.now()
                  }
                })
              }
            }
          })
        } else {
          this.$alert(`您尚未开通员工端，请联系公司管理员将您添加至<strong>人力资源-员工管理</strong>。<br>
  （管理员可在员工管理点击“从组织架构中选择”添加员工）。`, '提示', {
            dangerouslyUseHTMLString: true,
            confirmButtonText: '确定'
          })
        }
      } else {
        this.sidebarPopoverVisible = false
        this.tabType = type
        const item = this.menuRouters[0]
        const firstPath = item.children[0].path
        this.$router.push(item.path + '/' + firstPath)
      }
    },

    mousedown(e) {
      const el = e.target
      const disx = e.pageX - el.offsetLeft
      const disy = e.pageY - el.offsetTop
      document.onmousemove = (e) => {
        el.style.left = e.pageX - disx + 'px'
        el.style.top = e.pageY - disy + 'px'
      }

      document.onmouseup = (e) => {
        document.onmousemove = null
        document.onmouseup = null
      }
    }
  }
}
</script>

<style lang="scss">
@import "./components/Sidebar/variables.scss";

.el-menu-item--bottom {
  height: auto;
  padding: 0;
  line-height: normal;
  background-color: transparent !important;

  .menu-item-content {
    text-overflow: unset;
    background-color: rgba($color: #fff, $alpha: 0.1) !important;

    &:hover {
      background-color: $--background-color-base !important;
    }
  }
}

.el-popove--sidebar {
  padding: 0;
  background-color: $menuBg;
  border-color: $--border-color-base;
}
</style>
<style lang="scss" scoped>
@import "./styles/common.scss";

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
  min-width: 1200px;
  padding: 0;
  box-shadow: 0 1px 2px #dbdbdb;
}

.el-main {
  background: white;
}

::v-deep .app-main {
  overflow: hidden;

  .app-main-content {
    padding: 24px 40px 0;
  }
}

.switch-btn {
  position: fixed;
  right: 24px;
  bottom: 64px;
  z-index: 9;
  height: 40px;
  border-radius: 20px;
}
</style>
