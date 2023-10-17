<template>
  <div class="system-customer main">
    <xr-header
      label="业务参数设置" />
    <div class="main-content-wrap">
      <!-- 客户管理导航 -->
      <div class="main-nav">
        <div class="main-nav__content">
          <div class="nav-sections-wrap">
            <div
              v-for="(item, index) in menuList"
              :key="index"
              :class="{'is-select' : item.key == menuIndex}"
              class="menu-item"
              @click="menuSelect(item.key)">
              {{ item.label }}
            </div>
          </div>
        </div>
      </div>
      <keep-alive>
        <component
          :is="menuIndex"
          class="main-content" />
      </keep-alive>
    </div>
  </div>
</template>

<script>
import LogWelcome from './components/LogWelcome' // 跟进记录类型设置
import XrHeader from '@/components/XrHeader'
import CalendarType from './components/CalendarType'
import MarketingSet from './components/MarketingSet'
// import CallSet from './components/CallSet'

// import Lockr from 'lockr'

export default {
  name: 'OtherSystem',

  components: {
    LogWelcome,
    CalendarType,
    XrHeader,
    MarketingSet
    // CallSet
  },

  data() {
    return {
      // menuList: [
      //   { label: '日志欢迎语', key: 'LogWelcome' },
      //   { label: '日程类型设置', key: 'CalendarType' },
      //   { label: '活动资讯', key: 'MarketingSet' }
      // ],
      menuIndex: 'LogWelcome',
      types: '' // 区分拥有客户 和 锁定客户
    }
  },

  computed: {
    Show() {
      return this.$store.state.crm.isCall
      // return true
    },
    menuList() {
      const temps = [
        { label: '日志欢迎语', key: 'LogWelcome' },
        { label: '日历类型设置', key: 'CalendarType' }
        // { label: '活动资讯', key: 'MarketingSet' }
      ]
      // const callData = Lockr.get(LOCAL_CALL_AUTH_DATA)
      // hisUse 0 是默认硬呼单卡 1 是软乎 2是硬呼多卡
      // if (this.Show && callData && callData.hisUse == 2) {
      //   temps.push({ label: '话机设置', key: 'CallSet' })
      // }
      return temps
    }
  },

  methods: {
    /**
     * 菜单选择
     */
    menuSelect(i) {
      this.menuIndex = i
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
@import "../styles/index.scss";

.main-content-wrap {
  margin-top: #{$--interval-base * 2};
}

.nav-sections-wrap {
  padding: $--interval-base;
}
</style>
