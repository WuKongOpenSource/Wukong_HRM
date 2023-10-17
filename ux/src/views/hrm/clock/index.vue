<template>
  <div id="employee-main-container" class="clock-index">
    <wk-page-header
      title="月度汇总"
      :dropdowns="headerMoreHandle"
      @command="headerMoreHandleClick" />

    <wk-filter-header
      :props="{
        searchPlaceholder: '请输入员工姓名/工号',
      }"
      @event-change="filterHeaderHandle"
    >
      <template slot="left-start">
        <el-date-picker
          v-model="month"
          class="margin-left-interval"
          :clearable="false"
          value-format="yyyy-MM"
          format=""
          type="month"
          placeholder="选择月"
          @change="refreshList" />
      </template>
      <template slot="right">
        <el-button v-if="hasFilterContent" type="text" @click="resetFilter">清除筛选</el-button>
        <wk-popover-filter
          class="margin-left-interval"
          :width="popoverFilterWidth"
          :field-from.sync="filterObj"
          :field-list="filterList"
          :has-content="hasFilterContent"
          placement="bottom"
          @sure="refreshList"
          @reset="resetFilter"
        />
      </template>
    </wk-filter-header>

    <div class="crm-container">
      <el-table
        id="table"
        v-loading="loading"
        :cell-class-name="cellClassName"
        :row-height="40"
        :data="list"
        :height="tableHeight"
        :class="WKConfig.tableStyle.class"
        :stripe="WKConfig.tableStyle.stripe"
        use-virtual
        border
        highlight-current-row
        style="width: 100%;"
        @sort-change="sortChange"
        @row-click="handleRowClick">
        <el-table-column
          v-for="(item, index) in fieldList"
          :key="index"
          :fixed="index == 0"
          :prop="item.fieldName"
          :label="item.name"
          :min-width="item.width"
          :formatter="fieldFormatter"
          :sortable="item.sortable"
          show-overflow-tooltip />
        <el-table-column />
      </el-table>
      <div class="p-contianer">
        <el-pagination
          :current-page="currentPage"
          :page-sizes="pageSizes"
          :page-size.sync="pageSize"
          :total="total"
          class="p-bar"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </div>
    <!-- 详情页面 -->
    <detail
      v-if="showDview"
      :id.sync="rowID"
      :times="times"
      :detail-data="detailData"
      class="d-view"
      @hide-view="showDview=false" />
  </div>
</template>

<script>
import {
  hrmQueryAttendanceEmpMonthRecordPageListAPI,
  hrmAttendanceClockExportAPI
} from '@/api/hrm/clock'
import {
  hrmDeptQueryTreeListAPI
} from '@/api/hrm/dept'

import WkPageHeader from '@/components/Page/WkPageHeader'
import WkFilterHeader from '@/components/NewCom/WkFilterHeader'
import WkPopoverFilter from '@/components/NewCom/WkPopoverFilter'

import Detail from './Detail'
import moment from 'moment'
import { mapGetters } from 'vuex'
// import { isEmpty } from '@/utils/types'
import { downloadExcelWithResData } from '@/utils'
import Table from './mixins/Table'

export default {
  name: 'ClockIndex',
  components: {
    WkPageHeader,
    WkFilterHeader,
    WkPopoverFilter,
    Detail
  },
  mixins: [Table],
  data() {
    return {
      search: '', // 搜索内容
      // // 筛选宽
      request: hrmQueryAttendanceEmpMonthRecordPageListAPI,
      // 筛选宽
      popoverFilterWidth: document.documentElement.clientWidth - 80,
      // 高级筛选
      filterObj: {}, // 筛选确定数据
      filterList: [
        // {
        //   name: '时间',
        //   field: 'date',
        //   formType: 'date',
        //   setting: []
        // },
        {
          name: '部门',
          field: 'deptIds',
          formType: 'structure',
          radio: false,
          props: {
            dataType: 'hrm'
          },
          request: hrmDeptQueryTreeListAPI,
          setting: []
        },
        {
          name: '是否全勤',
          field: 'isFullAttendance',
          formType: 'select',
          setting: [{
            label: '是',
            value: 1
          }, {
            label: '否',
            value: 2
          }]
        }
      ],
      rowID: '',
      detailData: {},
      showDview: false,
      month: moment().format('YYYY-MM')
      // startTime: ''
    }
  },
  computed: {
    ...mapGetters([
      'hrm'
    ]),

    // 考勤所有权限
    attendanceAuth() {
      return this.hrm && this.hrm.attendance
    },

    // 导出月度汇总权限
    exportMonthRecord() {
      return this.attendanceAuth && this.attendanceAuth.exportMonthRecord
    },

    /**
     * 头部更多操作
     */
    headerMoreHandle() {
      const temps = []
      if (this.exportMonthRecord) {
        temps.push({ command: 'export', name: '导出', icon: 'wk wk-export' })
      }
      return temps
    },

    // 有筛选内容
    hasFilterContent() {
      if (this.filterObj) {
        let hasContent = false
        const keys = Object.keys(this.filterObj)
        for (let index = 0; index < keys.length; index++) {
          const key = keys[index]
          if (this.filterObj[key] != '' &&
           this.filterObj[key] != [] &&
           this.filterObj[key] != null &&
           this.filterObj[key] != undefined) {
            hasContent = true
          }
        }
        return hasContent
      }
      return false
    }
  },
  watch: {},
  created() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - 260
    }
  },
  methods: {
    /**
     * 高级筛选
     */
    resetFilter() {
      this.filterObj = {}
      this.refreshList()
    },
    /**
     * 搜索
     */
    filterHeaderHandle(type, data) {
      if (type === 'search') {
        this.search = data
        this.refreshList()
      }
    },

    /**
     * 获取表头展示字段
     */
    getFieldList() {
      this.fieldList = [
        { fieldName: 'employeeName', name: '姓名', sortable: true, width: 100 },
        { fieldName: 'jobNumber', name: '工号', width: 100 },
        { fieldName: 'deptName', name: '部门', width: 100 },
        { fieldName: 'post', name: '岗位', width: 100 },
        { fieldName: 'attendDays', name: '应出勤天数' },
        { fieldName: 'actualDays', name: '实际出勤天数' },
        { fieldName: 'lateMinute', name: '迟到时长（分钟）' },
        { fieldName: 'lateCount', name: '迟到次数' },
        { fieldName: 'earlyMinute', name: '早退时长（分钟）' },
        { fieldName: 'earlyCount', name: '早退次数' },
        { fieldName: 'absenteeismDays', name: '旷工天数' },
        { fieldName: 'misscardCount', name: '缺卡次数' },
        { fieldName: 'overTimeCount', name: '加班次数' },
        { fieldName: 'leaveDays', name: '请假天数' },
        { fieldName: 'outDays', name: '外勤次数' }
      ]
      this.refreshList()
    },

    /**
     * 刷新
     */
    refreshList() {
      this.handleCurrentChange(1)
    },

    /**
     * 更改每页展示数量
     */
    handleSizeChange(val) {
      this.pageSize = val
      this.getList()
    },

    /**
     * 更改当前页数
     */
    handleCurrentChange(val) {
      this.currentPage = val
      this.getList()
    },

    /**
     * 头部更多操作
     */
    headerMoreHandleClick(command) {
      if (command == 'export') {
        this.loading = true
        const params = {
          search: this.search,
          times: this.times
        }

        for (var key in this.filterObj) {
          params[key] = this.filterObj[key]
        }

        hrmAttendanceClockExportAPI(params).then(res => {
          downloadExcelWithResData(res)
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
      }
    },
    /**
     * 通过回调控制class
     */
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (column.property === 'employeeName') {
        return 'can-visit--underline'
      } else {
        return ''
      }
    },
    /**
     * 列表操作
     * @param {*} row
     * @param {*} column
     * @param {*} event
     */
    handleRowClick(row, column, event) {
      if (column.property === 'employeeName') {
        this.rowID = row.employeeId
        this.detailData = { ...row, times: this.times }
        this.showDview = true
      } else {
        this.showDview = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.clock-index {
  .wk-filter-header {
    margin-top: 30px;
    margin-bottom: 20px;
    line-height: 32px;
  }

  .d-view {
    position: fixed;
    top: $--detail-view-top;
    right: 0;
    bottom: 0;
    width: $--detail-width-base;
    min-width: 926px;
  }

  .el-date-editor.el-input {
    width: 130px;
  }
}
</style>
