<template>
  <div class="system-customer main">
    <xr-header
      label="业务参数设置" />
    <div class="main-content-wrap">
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
import RecruitChannel from './components/RecruitChannel' // 跟进记录类型设置
import EliminateSet from './components/EliminateSet' // 淘汰原因设置
import XrHeader from '@/components/XrHeader'

export default {
  name: 'BizParam',

  components: {
    RecruitChannel,
    EliminateSet,
    XrHeader
  },

  data() {
    return {
      menuList: [
        { label: '招聘渠道设置', key: 'RecruitChannel' },
        { label: '淘汰原因设置', key: 'EliminateSet' }
      ],
      menuIndex: 'RecruitChannel'
    }
  },

  computed: { },

  methods: {
    /**
     * 菜单选择
     */
    menuSelect(i) {
      if (i == 'own' || i == 'lock') {
        this.types = {
          own: 1,
          lock: 2
        }[i]
      }
      this.menuIndex = i
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
@import "../../styles/index.scss";

.main-content-wrap {
  margin-top: #{$--interval-base * 2};
}

.nav-sections-wrap {
  padding: $--interval-base;
}
</style>
