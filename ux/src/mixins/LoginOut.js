import { removeAuth } from '@/utils/auth'
import { isEmpty } from '@/utils/types'
import Lockr from 'lockr'
import NP from 'number-precision'
import moment from 'moment'
import { mapGetters } from 'vuex'
import { LOCAL_CLEAR_PAGE_TIME, LOCAL_FREE_TIME, LOCAL_ADMIN_TOKEN } from '@/utils/constants.js'

export default {
  data() {
    return {}
  },

  watch: {
    loginOutConfig: {
      handler() {
        if (!isEmpty(this.loginOutConfig)) {
          this.listenWindow()
          this.resetFreeTime()
        }
      },
      deep: true,
      immediate: true
    }
  },

  mounted() {
    window.addEventListener('beforeunload', () => {
      Lockr.rm(LOCAL_FREE_TIME)
      const token = Lockr.get(LOCAL_ADMIN_TOKEN)
      if (token) {
        Lockr.set(LOCAL_CLEAR_PAGE_TIME, +new Date())
      }
    })
  },

  computed: {
    ...mapGetters(['loginOutConfig', 'userInfo'])
  },

  methods: {
    /**
     * @description: 监听窗口事件
     * @return {*}
     */
    listenWindow() {
      this.$nextTick(() => {
        /**
         * maximumIdleTime 最大空闲时间
         * maximumIdleTimeUnit 最大空闲时间单位 1:天 2:小时 3:分钟
         */
        const { maximumIdleTime, maximumIdleTimeUnit } = this.loginOutConfig
        const timeObj = {
          1: NP.times(maximumIdleTime, 24, 60, 60, 1000),
          2: NP.times(maximumIdleTime, 60, 60, 1000),
          3: NP.times(maximumIdleTime, 60, 1000)
        }
        if (!Lockr.get(LOCAL_CLEAR_PAGE_TIME)) {
          return
        }
        const lastClearPageTime = Lockr.get(LOCAL_CLEAR_PAGE_TIME)
        if (NP.minus(+new Date(), lastClearPageTime) > timeObj[maximumIdleTimeUnit]) {
          this.loginOut()
        }

        Lockr.rm(LOCAL_CLEAR_PAGE_TIME)
      })

      window.addEventListener('keydown', () => this.resetFreeTime())
      window.addEventListener('mousemove', () => this.resetFreeTime())
      window.addEventListener('mousedown', () => this.resetFreeTime())

      // 监听页面滚动事件
      window.addEventListener('mousewheel', () => this.resetFreeTime())
    },

    /**
     * @description: 退出登录
     * @return {*}
     */
    loginOut() {
      removeAuth()
        .then(() => {
          const lastClearPageTime = Lockr.get(LOCAL_CLEAR_PAGE_TIME)
          const freeTime = Lockr.get(LOCAL_FREE_TIME)
          const time = lastClearPageTime ? moment(lastClearPageTime).format('YYYY-MM-DD HH:mm:ss') : moment(freeTime).format('YYYY-MM-DD HH:mm:ss')
          this.$confirm(`超出最大空闲时间，最后操作时间：${time}`, '提示', {
            confirmButtonText: '确定',
            showCancelButton: false,
            type: 'warning'
          }).then(() => {
            Lockr.rm(LOCAL_CLEAR_PAGE_TIME)
            Lockr.rm(LOCAL_FREE_TIME)
            location.reload()
          }).catch(() => {
            Lockr.rm(LOCAL_CLEAR_PAGE_TIME)
            Lockr.rm(LOCAL_FREE_TIME)
            location.reload()
          })
        })
    },

    /**
     * @description: 重置登录超时时间
     * @return {*}
     */
    resetFreeTime() {
      // 获取上次操作时间
      const freeTime = Lockr.get(LOCAL_FREE_TIME)
      if (!freeTime) {
        // 设置初始操作时间
        Lockr.set(LOCAL_FREE_TIME, +new Date())
        return
      }

      /**
       * maximumIdleTime 最大空闲时间
       * maximumIdleTimeUnit 最大空闲时间单位 1:天 2:小时 3:分钟
       */
      const { maximumIdleTime, maximumIdleTimeUnit } = this.loginOutConfig
      const timeObj = {
        1: NP.times(maximumIdleTime, 24, 60, 60, 1000),
        2: NP.times(maximumIdleTime, 60, 60, 1000),
        3: NP.times(maximumIdleTime, 60, 1000)
      }

      if (NP.minus(+new Date(), freeTime) > timeObj[maximumIdleTimeUnit]) {
        this.loginOut()
        return
      }

      Lockr.set(LOCAL_FREE_TIME, +new Date())
    }
  }
}
