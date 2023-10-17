<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <wb-section :move="false" class="el-card wb-perfor">
    <template slot="title-left">
      绩效考核 {{ des }}
    </template>
    <div class="body">
      <el-tabs v-model="tabType" style="width: 100%;" @tab-click="refreshList">
        <el-tab-pane
          v-for="tab in tabList"
          :key="tab.name"
          :name="tab.name">
          <span slot="label">{{ tab.label }}<span v-if="tab.num > 0" style="margin-left: 5px;" class="tab-pane-num">{{ tab.num }}</span></span>
        </el-tab-pane>
      </el-tabs>

      <div class="tabs__content">
        <el-table
          v-for="(item, index) in list"
          :key="index"
          :data="[item]"
          :cell-class-name="numCellClassName"
          align="center"
          header-align="center"
          style="width: 100%;"
          @row-click="numRowClick(arguments, item)">
          <el-table-column
            prop="time"
            show-overflow-tooltip
            label="考核时间">
            <template slot-scope="scope">
              {{ getPeriodTime(scope.row) }}
            </template>
          </el-table-column>
          <el-table-column
            prop="appraisalName"
            show-overflow-tooltip
            label="考核名称" />
          <el-table-column
            prop="tableName"
            show-overflow-tooltip
            label="考核模板" />
          <el-table-column
            prop="scoreLevels"
            show-overflow-tooltip
            label="考核结果">
            <template slot-scope="scope">
              <span
                v-for="(level, lIndex) in scope.row.scoreLevels"
                :key="lIndex"
                style="font-weight: bold;">
                {{ `${level.levelName}（${level.num}人）` }}
              </span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <employees-view
      :id="appraisalId"
      :visible.sync="employeesViewShow"
    />
  </wb-section>
</template>

<script>
import {
  hrmDashboardAppraisalCountSurveyAPI,
  hrmDashboardAppraisalSurveyAPI
} from '@/api/hrm/workbench'

import WbSection from './WbSection'
import EmployeesView from '../../performance/index/components/EmployeesView'

import { timeToFormatTime } from '@/utils'
import moment from 'moment'

export default {
  // 绩效考核
  name: 'WbPerformance',
  components: {
    WbSection,
    EmployeesView
  },
  props: {},
  data() {
    return {
      des: '',
      tabType: '2',
      tabList: [{
        label: '已完成考核',
        name: '2',
        num: 0
      }, {
        label: '进行中考核',
        name: '1',
        num: 0
      }],
      list: [],
      appraisalId: '',
      employeesViewShow: false
    }
  },
  computed: {},
  watch: {},
  created() {
    const startTime = moment().add(-1, 'y').format('YYYY.MM.DD')
    const endTime = moment().format('YYYY.MM.DD')
    this.des = `（${startTime}-${endTime}）`
    this.getCountDetail()
    this.getDetail()
  },

  beforeDestroy() {},
  methods: {
    getCountDetail() {
      hrmDashboardAppraisalCountSurveyAPI().then(res => {
        const data = res.data || {}
        this.tabList.forEach(item => {
          item.num = data[parseInt(item.name)] || 0
        })
      }).catch(() => {})
    },

    getDetail() {
      hrmDashboardAppraisalSurveyAPI(this.tabType).then(res => {
        this.list = res.data || []
      }).catch(() => {})
    },

    /**
     * 获取周期时间
     */
    getPeriodTime(data) {
      return `${timeToFormatTime(data.startTime, 'YYYY.MM.DD')}-${timeToFormatTime(data.endTime, 'YYYY.MM.DD')}`
    },

    refreshList() {
      this.list = []
      this.getDetail()
    },

    /**
     * 数量颜色
     */
    numCellClassName({ row, column, rowIndex, columnIndex }) {
      if (column.property == 'scoreLevels') {
        return 'can-visit--underline'
      }
      return ''
    },

    /**
     * 数量查看
     */
    numRowClick(args, data) {
      const arr = Array.prototype.slice.call(args)
      const property = arr[1].property
      if (property == 'scoreLevels') {
        this.appraisalId = data.appraisalId
        this.employeesViewShow = true
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.wb-perfor {
  .body {
    padding-top: 15px;
    padding-left: 15px;
  }

  .tabs__content {
    min-height: 40px;
    max-height: 243px;
    overflow-y: auto;
  }

  ::v-deep.el-table {
    border-bottom: 1px solid #e6e6e6;

    th {
      background-color: transparent;
      border-bottom-color: transparent;
    }

    tr {
      background-color: transparent;
    }

    td {
      background-color: transparent;
      border-bottom-color: transparent;
    }

    &::before {
      display: none;
    }
  }

  ::v-deep .el-tabs {
    .el-tabs__nav-wrap::after {
      display: none;
    }

    .el-tabs__item {
      font-weight: bold;
    }
  }

  .tab-pane-num {
    display: inline-block;
    height: 18px;
    padding: 0 6px;
    font-size: 12px;
    font-weight: bold;
    line-height: 18px;
    color: $--color-text-regular;
    text-align: center;
    white-space: nowrap;
    background-color: #dfe1e6;
    border: 1px solid #fff;
    border-radius: 10px;
  }
}
</style>
