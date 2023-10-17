<template>
  <div class="punching-table">
    <el-table
      id="crm-table"
      ref="Table"
      v-loading="loading"
      :row-height="40"
      :cell-class-name="cellClassName"
      :data="list"
      :height="tableHeight"
      class=""
      stripe
      border
      highlight-current-row
      style="width: 100%;">
      <el-table-column
        v-for="(item, index) in fieldList"
        :key="item.fieldName"
        :fixed="index == 0"
        :prop="item.fieldName"
        :label="item.name"
        :min-width="item.width"
        align="center"
        show-overflow-tooltip>
        <template slot="header" slot-scope="{column}">
          <div v-if="index>3">
            <p class="day">{{ column.label }}</p>
            <p class="week">{{ getWeek(column.label) }}</p>
          </div>
          <template v-else>{{ item.name }}</template>
        </template>
        <template slot-scope="{row, column}">
          <div v-if="index>3" @click="handleDetail(row, column)">
            <span
              v-for="(t,tIndex) in getTimeAndStatus(row.dateList[Number(column.label)-1]['time'])"
              :key="tIndex"
              :style="{color: statusMap[t.status].color}">
              {{ t.time }}
            </span>
          </div>
          <template v-else>{{ row[column.property] || "--" }}</template>
        </template>
      </el-table-column>
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

    <el-dialog
      :visible.sync="attendanceDetailDialog"
      :title="`${attendanceDetail.employeeName} ${attendanceDetail.currentDate}`"
      width="650px">
      <p>班次：{{ attendanceDetail.shiftStr }}</p>
      <p v-if="attendanceDetail.leaveStr">请假：{{ attendanceDetail.leaveStr }}</p>
      <el-table
        :data="attendanceDetail.attendanceEmpDailyRecordList"
        style="width: 100%;">
        <el-table-column
          v-for="(item, index) in detailFieldList"
          :key="index"
          :prop="item.prop"
          :label="item.label"
          width="width">
          <template slot-scope="{row, column}">
            {{ fieldFormatter(row, column) }}
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import {
  hrmQueryAttendanceEmpMonthDailyDetailPageListAPI,
  hrmQueryAttendanceDailyDetailAPI
} from '@/api/hrm/clock'

import { isEmpty } from '@/utils/types'
import Table from '../mixins/Table'
import moment from 'moment'
export default {
  mixins: [Table],
  data() {
    return {
      attendanceDetailDialog: false,
      recordType: 'punching',
      request: hrmQueryAttendanceEmpMonthDailyDetailPageListAPI,
      detailFieldList: [
        { label: '打卡时间', prop: 'clockTime' },
        { label: '打卡类型', prop: 'clockType' },
        { label: '打卡地点', prop: 'address' },
        { label: '打卡来源', prop: 'type' }
      ]
    }
  },
  methods: {
    getTimeAndStatus(_time) {
      if (!_time.length) {
        return [{ time: '--', status: 0 }]
      }
      return _time.map(item => {
        const [time, status] = item.split('-')
        if ([3, 4, 6, 9].includes(Number(status))) {
          return {
            time: this.statusMap[status].label,
            status: status
          }
        } else {
          return {
            time: time || '--',
            status: status || 0
          }
        }
      })
    },
    handleDetail(row, column) {
      console.log(row, column)
      if (column.property.startsWith('day')) {
        const { employeeName, employeeId } = row
        const currentDate = row.dateList[Number(column.label) - 1]['date']
        hrmQueryAttendanceDailyDetailAPI({ employeeId, currentDate }).then(res => {
          this.attendanceDetailDialog = true
          const { hrmAttendanceShiftVO,
            hrmEmployeeLeaveRecord,
            attendanceEmpDailyRecordList = [] } = res.data
          const shiftStr = this.getTimeRange(hrmAttendanceShiftVO)
          const leaveStr = (
            hrmEmployeeLeaveRecord &&
            hrmEmployeeLeaveRecord.leaveType + ' ' +
            hrmEmployeeLeaveRecord.leaveStartTime + '～' +
            hrmEmployeeLeaveRecord.leaveEndTime)
          this.attendanceDetail = {
            employeeName,
            currentDate,
            shiftStr,
            leaveStr,
            attendanceEmpDailyRecordList
          }
        }).catch(() => {})
      }
    },
    getTimeRange(item) {
      const arr = []
      const { shiftType } = item
      for (let index = 1; index <= 3; index++) {
        if (item['start' + index]) {
          arr.push(item['start' + index] + '-' + item['end' + index])
        }
      }
      return `${{ 0: '休息', 1: '早晚打卡', 2: '分段打卡' }[shiftType]} ${arr.join('  ')} `
    },

    /**
     * 获取表头展示字段
     */
    getFieldList() {
      const arr = [
        { fieldName: 'employeeName', name: '姓名', sortable: '', width: 100 },
        { fieldName: 'jobNumber', name: '工号', sortable: '', width: 100 },
        { fieldName: 'deptName', name: '部门', sortable: '', width: 100 },
        { fieldName: 'post', name: '岗位', sortable: '', width: 100 }
      ]
      this.fieldList = arr.concat(this.getMonthField())
      this.getList()
    },
    getWeek(day) {
      const y = moment(this.times[0], 'YYYY-MM-DD').year()
      const m = moment(this.times[0], 'YYYY-MM-DD').month()
      const week = moment([y, m, day]).day()
      return { 1: '一', 2: '二', 3: '三', 4: '四', 5: '五', 6: '六', 0: '日' }[week]
    },
    syncData() {
      this.getList(() => {
        this.$message.success('同步成功')
      })
    },
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (columnIndex > 3) {
        return 'can-visit--underline'
      } else {
        return ''
      }
    },

    /**
     * 格式化字段
     */
    fieldFormatter(row, column) {
      if (column.property == 'clockType') {
        return {
          1: '上班打卡',
          2: '下班打卡'
        }[row[column.property]] || '--'
      } else if (column.property == 'type') {
        return {
          1: '手机端打卡', 2: 'HR添加', 3: '自动打卡'
        }[row[column.property]] || '--'
      } else if (column.property == 'attendanceTime') {
        return row[column.property].split(' ').shift()
      } else if (column.property == 'address') {
        return isEmpty(row[column.property]) ? '--' : row.isOutCard ? row[column.property] + '(外勤打卡)' : row[column.property]
      } else {
        return isEmpty(row[column.property]) ? '--' : row[column.property]
      }
    }
  }
}
</script>

<style scoped lang="scss">
::v-deep .el-dialog__header {
  border-bottom: 1px solid #ccc;
}

::v-deep .el-dialog__body {
  padding-top: 10px;
}

.el-dialog {
  p {
    line-height: 20px;
  }
}
</style>
