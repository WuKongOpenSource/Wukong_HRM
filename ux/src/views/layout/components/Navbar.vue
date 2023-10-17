<template>
  <div class="navbar">
    <flexbox style="width: auto;">
      <el-popover
        v-if="dropMenus && dropMenus.length > 0"
        placement="bottom"
        width="320"
        trigger="click"
        popper-class="no-padding-popover">
        <div class="drop-wrap">
          <div class="drop-wrap__section">
            <div class="drop-wrap__title">切换到</div>
            <div class="drop-wrap__content">
              <flexbox
                v-for="(item, index) in dropMenus"
                :key="index"
                class="drop-cell is-interval"
                :class="[currentModule === item.module ? 'is-select' : '']"
                @click.native="dropClick(item)">
                <flexbox class="drop-cell__hd" justify="center">
                  <i :class="item.icon" />
                </flexbox>
                <div class="drop-cell__bd">
                  <div>{{ item.title }}</div>
                  <div class="des">{{ item.des }}</div>
                </div>
              </flexbox>
            </div>
          </div>
        </div>
        <el-button slot="reference" class="more" type="bg-icon" circle>
          <i class="wk wk-grid" />
        </el-button>
      </el-popover>
      <img
        :key="logo"
        v-src="logo"
        class="logo"
        @click="enterMainPage">
      <div v-if="title" class="title">{{ title }}</div>
      <div class="menus">
        <el-tabs
          v-model="menuTab"
          class="header-menu"
          @tab-click="navItemsClick">
          <el-tab-pane
            v-for="(item, index) in menus"
            :key="index"
            :index="item.path"
            :name="item.path"
            :label="item.label">
            <!-- <i :style="{ fontSize: item.fontSize }" :class="item.icon" /> -->
            <template slot="label">
              <router-link
                class="router-link"
                :to="{ path: item.fullPath }">
                <span
                  class="label">{{ item.label }}</span>
              </router-link><span v-if="item.num" type="primary" class="el-badge__content el-badge__content--undefined">{{ item.num }}</span>
            </template>
          </el-tab-pane>
        </el-tabs>
      </div>
      <slot name="left" />
    </flexbox>

    <flexbox style="width: auto;">

      <!-- <el-badge
        :value="unreadNums.announceCount"
        :hidden="!unreadNums.announceCount || unreadNums.announceCount == 0"
        :max="99"
        type="primary">
        <el-button type="bg-icon" circle @click="checkMessageDetail(true)">
          <i class="wk wk-announcement" />
        </el-button>
      </el-badge> -->
      <!-- <el-tooltip
        content="通知/待办"
        effect="dark"
        placement="bottom">
        <el-badge
          class="nav-badge"
          :value="messageCount"
          :hidden="messageCount === 0"
          :max="99"
          type="primary">
          <el-button type="bg-icon" circle @click="checkMessageDetail(false)">
            <i class="wk wk-bell" />
          </el-button>
        </el-badge>
      </el-tooltip> -->

      <!-- <el-tooltip
        content="帮助"
        effect="dark"
        placement="bottom">
        <el-button type="bg-icon" circle @click="enterHelp">
          <i class="wk wk-help" />
        </el-button>
      </el-tooltip> -->

      <el-tooltip
        content="系统设置"
        effect="dark"
        placement="bottom">
        <el-button v-if="manage" type="bg-icon" style="margin-left: 0;" circle @click="enterSystemSet">
          <i class="wk wk-manage" />
        </el-button>
      </el-tooltip>

      <!-- <system-message
        :visible.sync="sysMessageShow"
        :unread-nums="unreadNums"
        :only-announcement="mesOnlyAnnouncement"
        @update-count="sendSystemUnreadNum" /> -->
      <!-- <messages
        v-if="sysMessageShow"
        :unread-nums="unreadNums"
        @close="sysMessageShow = false"
        @update-unread="sendSystemUnreadNum" /> -->

      <xr-avatar
        v-if="!isEmpty(userInfo)"
        style="margin-left: 8px;"
        disabled
        :name="$getUserName(userInfo)"
        :size="24"
        :src="$getUserImg(userInfo)" />
    </flexbox>
    <!-- 帮助中心 -->
    <help-side
      v-if="helpSideShow"
      :props="helpSideProps"
      @close="helpSideShow = false"
    />
  </div>
</template>

<script>
import { systemMessageUnreadCountAPI } from '@/api/common'

// import Messages from './Messages'
import HelpSide from './Help/HelpSide'

// mapState
import { mapGetters } from 'vuex'
import { Loading } from 'element-ui'
import { on, off } from '@/utils/dom'
import { isEmpty } from '@/utils/types'

export default {
  filters: {
    langName: function(value) {
      if (value) {
        return { cn: '中文', en: 'English' }[value]
      } else {
        return '中文'
      }
    }
  },
  components: {
    // Messages,
    HelpSide
  },
  props: {
    title: String,
    menus: Array // 中间菜单数据
  },
  data() {
    return {
      unreadNums: {
        allCount: 0,
        announceCount: 0,
        crmCount: 0,
        examineCount: 0,
        eventCount: 0,
        logCount: 0,
        taskCount: 0
      },
      mesOnlyAnnouncement: false,
      sysMessageShow: false,
      intervalId: null,
      menuTab: '',
      helpSideShow: false // 帮助中心
    }
  },
  computed: {
    ...mapGetters([
      'userInfo',
      'lang',
      'logo',
      'crm',
      'bi',
      'manage',
      'oa',
      'hrm',
      'collapse',
      'app',
      'hrmUserInfo',
      'oaRouters',
      'moduleAuth',
      'moduleData'
    ]),
    // 通知 + 代办事项总数量
    messageCount() {
      return this.unreadNums.allCount || 0
    },
    moreMenu() {
      const temps = [{
        command: 'baseInfo',
        divided: false,
        label: '基本信息',
        icon: 'wk wk-user'
      }]

      return temps.concat([{
        command: 'logOut',
        divided: false,
        label: '退出登录',
        icon: 'wk wk-logout'
      }, {
        command: 'version',
        divided: false,
        label: `版本 V${WKConfig.version}`,
        icon: 'wk wk-version',
        disabled: true
      }])
    },
    //  左侧下拉数据
    dropMenus() {
      const allList = []
      // eslint-disable-next-line
      for (const key in this.allItemsObj) {
        allList.push(this.allItemsObj[key])
      }
      return allList
    },
    allItemsObj() {
      var tempsItems = {}

      if (
        this.moduleAuth &&
        this.moduleAuth.hrm
      ) {
        tempsItems.hrm = {
          title: 'HRM',
          des: '人力资源管理系统',
          type: 11,
          module: 'hrm',
          path: '/hrm',
          icon: 'wk wk-employees'
        }
      }
      return tempsItems
    },

    // 获取当前模块关键字
    currentModule() {
      const { path, query } = this.$route
      let module = query.module
      if (!module) {
        const paths = path.split('/')
        paths.forEach(o => {
          if (o && !module) {
            module = o
          }
        })
      }
      module = module ? module.toLowerCase() : ''
      const vilidModules = this.dropMenus.map(item => item.module)
      if (!vilidModules.includes(module)) {
        module = ''
      }
      return module
    }
  },
  watch: {
    $route: {
      handler(val) {
        const { path, query, meta } = this.$route
        // 子菜单逻辑 解决这部分之前的 当做菜单
        if (path.includes('/subs/')) {
          this.menuTab = path.split('/subs/')[0]
        } else {
          this.menuTab = path
        }

        // 有些多个地方对应一个位置 query 里传入 navActiveMenu
        if (query && query.navActiveMenu) {
          this.menuTab = query.navActiveMenu
        } else if (meta && meta.activeMenu) {
          this.menuTab = meta.activeMenu
        }

        if (this.menus) {
          const menu = this.menus.find(item => item.path === this.menuTab)
          menu && this.$emit('select', menu)
        }
      },
      immediate: true
    }
  },
  created() {
    // this.menuTab = this.menus[0].path
    // this.$store.dispatch('QueryMarketing')
  },

  mounted() {
    on(document, 'mousedown', this.handleDocumentClick)

    // 消息数
    // this.getSystemUnreadNum('visible')

    // this.$bus.on('document-visibility', (state) => {
    //   this.getSystemUnreadNum(state)
    // })
  },
  beforeDestroy() {
    this.$bus.off('document-visibility')
    if (this.intervalId) {
      clearInterval(this.intervalId)
      this.intervalId = null
    }
    off(document, 'mousedown', this.handleDocumentClick)
  },
  methods: {
    isEmpty(data) {
      return isEmpty(data)
    },

    navItemsClick(tab) {
      const menu = this.menus[parseInt(tab.index)]
      this.$router.push(menu.fullPath)
      this.$emit('change', menu)
    },

    /**
     * 验证页面进入信息
     */
    validateEnterData(type) {
      return new Promise((resolve) => {
        if (type.includes('hrm')) {
          if (this.hrm) {
            if (this.hrmUserInfo) {
              resolve(true)
            } else {
              this.$store.commit('CLEAR_CANCELTOKENARR') // 取消之前的请求，保证切换的流畅

              this.$store.dispatch('GetHrmUserInfo').then(() => {
                resolve(true)
              }).catch(() => {
                resolve(false)
              })
            }
          } else {
            this.$router.push({
              name: 'hrmTips'
            })
            resolve(false)
          }
        } else {
          resolve(true)
        }
      })
    },

    enterSystemSet() {
      this.$router.push({
        name: 'manage'
      })
    },

    switchLang(item) {
      this.$store.commit('SET_LANG', item.lang)
      this.langName = item.name
    },

    /**
     * 获取系统未读消息数
     */
    getSystemUnreadNum(state) {
      if (this.intervalId) {
        clearInterval(this.intervalId)
        this.intervalId = null
      }
      if (state == 'visible') {
        this.sendSystemUnreadNum()
        this.intervalId = setInterval(() => {
          this.sendSystemUnreadNum()
        }, 600000)
      }
    },

    sendSystemUnreadNum() {
      systemMessageUnreadCountAPI()
        .then(res => {
          this.unreadNums = res.data
        })
        .catch(() => {
          if (this.intervalId) {
            clearInterval(this.intervalId)
            this.intervalId = null
          }
        })
    },

    /**
     * 有客户权限点击logo 进入仪表盘
     */
    enterMainPage() {
      this.$router.push('/')
    },

    moreMenuClick(command) {
      if (command == 'baseInfo') {
        const time = Date.parse(new Date())
        this.$router.push({
          name: 'person',
          query: {
            selectedIndex: 0,
            time
          }
        })
      } else if (command == 'logOut') {
        this.$confirm('退出登录？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            var loading = Loading.service({
              target: document.getElementById('#app')
            })
            this.$store
              .dispatch('LogOut')
              .then(() => {
                loading.close()
                location.reload()
              })
              .catch(() => {
                loading.close()
                location.reload()
              })
          })
          .catch(() => {})
      }
    },

    /**
     * 控制导航管理隐藏
     */
    handleDocumentClick(e) {
      if (typeof e.target.className !== 'string') return

      if (e.target.className.includes('wk-icon-fill-help')) {
        const type = e.target.dataset.type
        const id = e.target.dataset.id
        if (type && id) {
          this.helpSideProps = {
            menuIndex: 'Question',
            type: parseInt(type),
            id: parseInt(id)
          }
          this.helpSideShow = true
        }
      }
    },

    /**
     * 查看消息详情
     */
    checkMessageDetail(onlyAnnouncement) {
      this.mesOnlyAnnouncement = onlyAnnouncement
      this.sysMessageShow = true
    },

    /**
     * 模块切换
     */
    dropClick(item) {
      this.validateEnterData(item.path).then(result => {
        if (result) {
          this.$router.push(item.path)
        }
      })
    },

    /**
     * 日志点击
     */
    logClick(item) {
      let module = ''
      const { path, query } = this.$route
      // 获取path中的模块关键词 一般是第一个
      if (path) {
        const paths = path.split('/')
        paths.forEach(o => {
          if (o && !module) {
            module = o
          }
        })
      }

      if (module === 'oa') {
        module = query.module ? query.module : this.getAuthModule()
      } else if (!module) {
        module = this.getAuthModule()
      }

      let nextPath = `${item.path}?module=${module}`
      if (module === 'hrm') { // 区分是管理还是员工
        // 1 管理 2 员工
        const role = query.module === 'hrm' && query.role ? query.role : (this.$route.fullPath.includes('self-server') ? '2' : '1')
        nextPath = `${nextPath}&role=${['1', '2'].includes(role) ? role : '1'}`
      }

      // 进入审批获取审批待办数量
      if (item.type == 'oa/examine') {
        this.$store.dispatch('GetBacklogNum')
      }

      this.$router.push(nextPath)
    },

    /**
     * 获取有权限的模块
     */
    getAuthModule() {
      if (this.crm) {
        return 'crm'
      } else if (
        this.moduleAuth &&
        this.moduleAuth.hrm
      ) {
        return 'hrm'
      } else if (this.oa) {
        return 'oa'
      }
      return ''
    },

    /**
     * 展示help
     */
    enterHelp() {
      this.helpSideProps = null
      this.helpSideShow = true
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
.header-menu {
  .el-tabs__header {
    margin-bottom: 0;
  }

  .el-tabs__nav-wrap::after {
    display: none;
  }

  .el-tabs__item {
    height: 56px;
    padding: 0 12px !important;
    line-height: 56px;

    .router-link {
      display: inline-block;
      user-select: none;
    }

    .label {
      position: relative;

      &:hover {
        color: $--color-primary;
      }
    }

    .label:hover::before {
      position: absolute;
      top: -4px;
      right: -4px;
      bottom: -4px;
      left: -4px;
      z-index: -1;
      content: "";
      background-color: $--color-b50;
      border-radius: $--border-radius-base;
    }
  }

  .el-tabs__active-bar {
    height: 3px;
  }

  .el-button--bg-text {
    padding-right: 4px;
    padding-left: 4px;
    font-weight: bold;

    [class^="el-icon-"] {
      margin-left: 2px;
      font-size: 12px;
      font-weight: bold;
      color: #97a0ae;
    }
  }
}
</style>

<style rel="stylesheet/scss" lang="scss" scoped>
@import "style";

.navbar {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 12px;
  background-color: white;

  .more + .logo {
    margin-left: 8px;
  }

  .logo {
    display: block;
    flex-shrink: 0;
    width: 24px;
    height: 24px;
    margin-left: $--interval-base;
    cursor: pointer;
    background-color: white;
  }

  .title {
    margin-right: 20px;
    margin-left: 8px;
    font-size: 18px;
    color: $--color-n600;
  }

  .menus {
  }

  // 模块切换图标
  .wk-grid {
    font-size: 16px;
  }

  .xr-avatar {
    cursor: pointer;
  }

  .xr-avatar + .mark {
    margin-left: 6px;
  }
}

// 右侧操作
.handel-box {
  padding: 0 15px;
  padding-top: 10px;
  border-top: 1px solid #ebeef5;

  .handel-button {
    width: 100%;
    background-color: #2362fb;
    border-color: #2362fb;
    border-radius: $--border-radius-base;
  }
}

.hr-top {
  padding-top: 3px;
  margin-top: 8px;
  border-top: 1px solid #f4f4f4;
}

// 系统消息
.wk-icon-gift,
.wk-announcement,
.wk-bell {
  font-size: 20px;
}

.el-badge {
  margin-right: 4px;
}

.wk-announcement:hover,
.wk-bell:hover {
  color: $--color-primary;
}

.el-dropdown-menu {
  ::v-deep .popper__arrow {
    display: none;
  }
}

::v-deep .nav-badge {
  .el-badge__content.is-fixed {
    top: 8px;
    right: 18px;
  }
}

.el-button.is-circle {
  padding: 6px;
  border: none;

  i {
    font-size: 20px;
  }

  &:hover {
    background-color: rgba($color:$--color-b50, $alpha: 0.9);
  }
}

.log-menus {
  position: relative;
  flex-shrink: 0;
  padding-right: $--interval-base;
  margin-right: $--interval-base;

  &::after {
    position: absolute;
    top: 6px;
    right: 0;
    bottom: 6px;
    width: 1px;
    content: " ";
    background-color: $--border-color-base;
  }
}

.el-button.is-current {
  color: $--color-primary;
  background-color: rgba(222, 235, 255, 0.9);
}

// .top-btn {
//   display: flex;
//   flex-direction: row;
//   justify-content: flex-start;
//   align-items: center;
//   color: $--color-primary;
//   .gift {
//     color: $--color-primary;
//     margin-right: 5px;
//   }
// }
</style>

