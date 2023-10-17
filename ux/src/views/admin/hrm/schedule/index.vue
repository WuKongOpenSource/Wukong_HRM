<template>
  <div class="achivement-set">
    <xr-header
      ref="xrHeader"
      :show-search="![2, 3].includes(menuIndex)"
      search-icon-type="icon"
      placeholder="请输入考勤组名称/班次名称"
      label="考勤规则设置"
      @search="searchClick" />
    <div class="achivement-content">
      <!-- 客户管理导航 -->
      <div class="system-view-nav">
        <div class="nav-sections-wrap">
          <div
            v-for="(item, index) in menuList"
            :key="index"
            :class="{'is-select' : item.tableId == menuIndex}"
            class="menu-item"
            @click="menuClick(item.tableId)">
            {{ item.tableName }}
          </div>
        </div>
      </div>
      <keep-alive>
        <component
          :is="menuList[menuIndex].componentName"
          ref="componentRef"
          class="system-view-content"
        />
      </keep-alive>
    </div>
  </div>
</template>

<script>

import XrHeader from '@/components/XrHeader'
import GroupSet from './groupSet'
import ClassesSet from './classesSet'
import DeductionRules from '../achievementRule'
import ExamineSet from './examineSet'

import Lockr from 'lockr'
import { LOCAL_PAGE_SIZE } from '@/utils/constants.js'

export default {

  components: {
    XrHeader,
    GroupSet,
    ClassesSet,
    DeductionRules,
    ExamineSet
  },

  data() {
    return {
      currentPage: 1,
      pageSizes: [15, 30, 60, 100],
      pageSize: Lockr.get(LOCAL_PAGE_SIZE) || 15,
      total: 0,
      menuList: [{
        tableName: '考勤组设置',
        tableId: 0,
        componentName: 'GroupSet'
      }, {
        tableName: '班次设置',
        tableId: 1,
        componentName: 'ClassesSet'
      }, {
        tableName: '扣款规则',
        tableId: 2,
        componentName: 'DeductionRules'
      }, {
        tableName: '审批设置',
        tableId: 3,
        componentName: 'ExamineSet'
      }],
      menuIndex: 0,
      search: ''
    }
  },
  methods: {
    searchClick(val) {
      this.search = val
      this.$refs.componentRef.getList()
    },

    /**
     * @description: 菜单切换
     * @param {*} id
     * @return {*}
     */
    menuClick(id) {
      this.menuIndex = id
      this.$refs.xrHeader.$data.search = ''
      this.search = ''
      this.$nextTick(() => {
        this.$refs.componentRef.getList()
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.achivement-set {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 24px 40px;
}

.achivement-content {
  position: relative;
  display: flex;
  flex: 1;
  margin-top: 16px;
  overflow: hidden;
}

.system-view-nav {
  min-width: 224px;
  padding: 8px;
  margin-right: 10px;
  background: $--background-color-base;
  border-radius: $--border-radius-base;

  .nav-title {
    margin: 0 15px 8px;
  }
}

.system-view-content {
  display: flex;
  flex: 1;
  flex-direction: column;
  overflow-x: auto;
  background: #fff;
  border-radius: $--border-radius-base;
}

.nav-sections-wrap {
  padding: 8px;
  background: $--color-white;
}

// 菜单
.menu-item {
  position: relative;
  height: 32px;
  padding: 8px 12px;
  margin-bottom: 4px;
  font-size: 13px;
  color: #333;
  cursor: pointer;
  border: 1px solid $--color-white;

  .icon-close {
    position: absolute;
    top: 0;
    right: 8px;
    z-index: 1;
    display: none;
  }
}

.menu-item:hover,
.menu-item.is-select {
  background-color: $--color-b50;
  border: 1px solid $--color-b100;
  border-radius: 3px;
}

// .menu-item:hover::before,
// .menu-item.is-select::before {
//   content: ' ';
//   position: absolute;
//   top: 0;
//   left: 0;
//   bottom: 0;
//   width: 2px;
//   background-color: #5383ed;
// }
</style>
