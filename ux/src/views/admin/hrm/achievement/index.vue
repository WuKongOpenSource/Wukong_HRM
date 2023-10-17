<template>
  <div class="achivement-set main">
    <xr-header
      label="考核模板设置" />
    <div class="main-content-wrap">
      <div class="main-nav">
        <div class="main-nav__content">
          <div class="nav-sections-wrap">
            <div class="nav-section">
              <div class="nav-section__title">模板类型</div>
              <div class="nav-section__content is-padding">
                <div
                  v-for="(item, index) in menuList"
                  :key="index"
                  :class="{'is-select' : item.tableId == menuIndex}"
                  class="menu-item"
                  @click="menuSelect(item.tableId)">
                  {{ item.tableName }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <keep-alive>
        <achievement-set
          :id="menuIndex"
          class="main-content"
          @change="achievementSetChange" />
      </keep-alive>
    </div>
  </div>
</template>

<script>
import {
  hrmConfigQueryAchievementListAPI
} from '@/api/admin/hrm'

import AchievementSet from './components/AchievementSet' // 考核设置
import XrHeader from '@/components/XrHeader'

export default {
  name: 'AchievementMain',

  components: {
    AchievementSet,
    XrHeader
  },

  data() {
    return {
      menuList: [],
      menuIndex: ''
    }
  },

  computed: { },

  created() {
    this.getList()
  },

  methods: {
    /**
     * 获取详情
     */
    getList() {
      this.loading = true
      hrmConfigQueryAchievementListAPI()
        .then(res => {
          this.loading = false
          this.menuList = res.data || []
          if (this.menuList.length) {
            this.menuIndex = this.menuList[0].tableId
          }
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 菜单选择
     */
    menuSelect(i) {
      this.menuIndex = i
    },

    /**
     * 详情设置
     */
    achievementSetChange(data) {
      let currentIndex = -1
      for (let index = 0; index < this.menuList.length; index++) {
        const element = this.menuList[index]
        if (element.tableId == this.menuIndex) {
          currentIndex = index
          break
        }
      }

      if (currentIndex >= 0) {
        this.menuList.splice(currentIndex, 1, data)
        this.menuIndex = data.tableId
      }
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
@import "../../styles/index.scss";

.main-content-wrap {
  margin-top: #{$--interval-base * 2};
}

</style>
