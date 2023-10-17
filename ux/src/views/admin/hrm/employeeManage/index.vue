<template>
  <div class="system-customer main">
    <xr-header
      label="员工管理设置" />
    <div class="main-content-wrap">
      <!-- 导航 -->
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
import EmployeeArchives from './components/EmployeeArchives' // 员工档案设置
import EmployeeFields from './components/EmployeeFields' // 员工档案设置
import XrHeader from '@/components/XrHeader'

export default {
  name: 'EmployeeManage',

  components: {
    EmployeeArchives,
    EmployeeFields,
    XrHeader
  },

  data() {
    return {
      menuList: [
        { label: '新建员工字段设置', key: 'EmployeeFields' },
        { label: '员工档案设置', key: 'EmployeeArchives' }
      ],
      menuIndex: 'EmployeeFields'
    }
  },

  computed: { },

  created() {
    const { menuIndex } = this.$route.query
    if (menuIndex) {
      this.menuIndex = menuIndex
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
@import "../../styles/index.scss";

.main-content-wrap {
  margin-top: #{$--interval-base * 2};
}

.nav-sections-wrap {
  padding: $--interval-base;
}
</style>
