<template>
  <div id="employee-main-container" class="clock-index">
    <wk-page-header
      title="请假记录"
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
          ref="popoverFilter"
          class="margin-left-interval"
          :width="popoverFilterWidth"
          :field-from.sync="filterObj"
          :field-list="filterList"
          :has-content="hasFilterContent"
          placement="bottom"
          @sure="refreshList"
          @reset="resetFilter">
          <template slot-scope="{ data }">
            <el-select
              v-model="filterObj[data.field]"
              multiple
              collapse-tags
              style="margin-left: 20px;"
              placeholder="请选择"
              @change="leaveChange">
              <el-option
                v-for="(item, index) in data.setting"
                :key="index"
                :label="item"
                :value="item" />
            </el-select>
          </template>
        </wk-popover-filter>
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
        use-virtual
        stripe
        border
        highlight-current-row
        style="width: 100%;"
        @sort-change="sortChange">
        <el-table-column
          v-for="(item, index) in fieldList"
          :key="index"
          :fixed="index == 0"
          :prop="item.fieldName"
          :label="item.name"
          :min-width="item.width"
          :formatter="fieldFormatter"
          sortable
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
  </div>
</template>

<script>
import {
  hrmDeptQueryTreeListAPI
} from '@/api/hrm/dept'
import {
  hrmQueryLeaveRecordPageListAPI,
  hrmEmployeeLeaveRecordQueryLeaveTypeAPI,
  hrmEmployeeLeaveRecordExportAPI
} from '@/api/hrm/leaveRecord'

import WkPageHeader from '@/components/Page/WkPageHeader'
import WkFilterHeader from '@/components/NewCom/WkFilterHeader'
import WkPopoverFilter from '@/components/NewCom/WkPopoverFilter'

import moment from 'moment'
import { mapGetters } from 'vuex'

import { downloadExcelWithResData } from '@/utils'
import Table from '../mixins/Table'
export default {

  components: {
    WkPageHeader,
    WkFilterHeader,
    WkPopoverFilter
  },
  mixins: [Table],
  data() {
    return {
      request: hrmQueryLeaveRecordPageListAPI,
      month: moment().format('YYYY-MM'),
      search: '', // 搜索内容
      // 筛选宽
      popoverFilterWidth: 150,
      // 高级筛选
      filterObj: {}, // 筛选确定数据
      leaveTypeList: [], // 请假类型
      rowID: ''
    }
  },
  computed: {
    ...mapGetters([
      'collapse',
      'hrm'
    ]),

    // 考勤所有权限
    attendanceAuth() {
      return this.hrm && this.hrm.attendance
    },

    // 导出请假记录权限
    exportLeaveRecordAuth() {
      return this.attendanceAuth && this.attendanceAuth.exportLeaveRecord
    },

    /**
     * 头部更多操作
     */
    headerMoreHandle() {
      const temps = []
      if (this.exportLeaveRecordAuth) {
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
    },

    filterList() {
      return [{
        name: '部门',
        field: 'deptIds',
        formType: 'structure',
        radio: false,
        props: {
          dataType: 'hrm'
        },
        request: hrmDeptQueryTreeListAPI,
        setting: []
      }, {
        name: '请假类型',
        field: 'leaveTypes',
        formType: 'leaveTypes',
        clearable: true,
        setting: this.leaveTypeList
      }]
    }
  },
  watch: {
    collapse: {
      handler(val) {
        this.popoverFilterWidth = document.documentElement.clientWidth - (val ? 89 : 225)
      },
      immediate: true
    }
  },
  created() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - 190
    }
    this.getLeaveType()
  },
  methods: {
    /**
     * @description: 获取请假类型
     * @return {*}
     */
    getLeaveType() {
      hrmEmployeeLeaveRecordQueryLeaveTypeAPI()
        .then(res => {
          this.leaveTypeList = res.data || []
        })
    },

    /**
     * 审批回调
     */
    detailHandleCallBack() {

    },

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
        { fieldName: 'employeeName', name: '姓名', width: 100 },
        { fieldName: 'jobNumber', name: '工号', width: 100 },
        { fieldName: 'deptName', name: '部门', width: 100 },
        { fieldName: 'post', name: '岗位', width: 100 },
        { fieldName: 'leaveType', name: '请假类型' },
        { fieldName: 'leaveStartTime', name: '请假开始时间' },
        { fieldName: 'leaveEndTime', name: '请假结束时间' },
        { fieldName: 'leaveDay', name: '请假天数' },
        { fieldName: 'leaveReason', name: '请假事由' },
        { fieldName: 'remark', name: '备注' }
      ]
      this.getList()
    },

    leaveChange() {
      this.$set(this.$refs.popoverFilter.$data.from, 'leaveTypes', this.filterObj.leaveTypes)
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

        hrmEmployeeLeaveRecordExportAPI(this.params).then(res => {
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
      return ''
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
    top: 60px;
    right: 0;
    bottom: 0;
    width: 75%;
    min-width: 926px;
  }

  .el-date-editor.el-input {
    width: 130px;
  }
}
</style>
