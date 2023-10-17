<template>
  <div class="attendance-detail">
    <div class="attendance-cycle">
      <h2>
        <i class="el-icon-arrow-left" />
        <span>{{ $moment(detailData.times[0],"YYYY-MM-DD").format("YYYY年MM月") }}</span>
        <i class="el-icon-arrow-right" />
      </h2>
      <span class="attendance-cycle-date">
        考勤周期（{{ $moment(detailData.times[0],"YYYY-MM-DD").format("MM月DD日")+'~'+$moment(detailData.times[1],"YYYY-MM-DD").format("MM月DD日") }}）
      </span>
    </div>
    <div class="attendance-group">
      <div class="attendance-group-item">
        <span class="attendance-group-label">考勤组</span>
        <span class="attendance-group-value">{{ detailData.attendanceGroupName || '' }}</span>
      </div>
      <div class="attendance-group-item">
        <span class="attendance-group-label">是否全勤</span>
        <span class="attendance-group-value">{{ detailData.isFullAttendance? '是':'否' }}</span>
      </div>
    </div>

    <el-button-group class="attendance-days">
      <el-button v-for="item in tabs" :key="item.label" @click="tabHandle(item)">
        {{ item.label+item.value+item.unit }}
      </el-button>
    </el-button-group>
    <div v-loading="loading" class="attendance-content">
      <div
        v-for="(tab, index) in tabs"
        v-if="showTable && activeTab == tab.fieldName"
        :key="index"
        class="content-table">
        <el-select
          v-if="showSelect"
          v-model="tab.selectdValue"
          :placeholder="tab.placeholder"
          @change="getListData(tab)">
          <el-option
            v-for="item in tab.option"
            :key="item.label || item"
            :label="item.label || item"
            :value="item.value || item" />
        </el-select>
        <el-table
          :data="tab.listData"
          style="width: 100%;">
          <el-table-column
            type="selection"
            align="center" />
          <el-table-column
            v-for="item in tabsMap[tab.fieldName]"
            :key="item.prop"
            :prop="item.prop"
            :label="item.label"
            :formatter="fieldFormatter" />
        </el-table>
      </div>
      <div v-if="!showTable" class="content-calendar">
        <simple-calendar v-bind="$attrs" :value="$moment(detailData.times[0]).toDate()" />
      </div>
    </div>
  </div>
</template>

<script>
import { hrmQueryOverTimeRecordPageListAPI, hrmQueryOutCardPageListAPI } from '@/api/hrm/clock'
import {
  hrmQueryLeaveRecordPageListAPI,
  hrmEmployeeLeaveRecordQueryLeaveTypeAPI
} from '@/api/hrm/leaveRecord'

import SimpleCalendar from './SimpleCalendar'
export default {
  components: { SimpleCalendar },
  inheritAttrs: false,
  props: {
    detailData: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      loading: false,
      tabs: [
        { fieldName: 'attendDays', label: '应出勤', unit: '天', value: '' },
        { fieldName: 'actualDays', label: '实际出勤', unit: '天', value: '', status: -1, interactive: true },
        { fieldName: 'lateCount', label: '迟到', unit: '次', value: '', status: 1, interactive: true },
        { fieldName: 'earlyCount', label: '早退', unit: '次', value: '', status: 2, interactive: true },
        { fieldName: 'absenteeismDays', label: '旷工', unit: '天', value: '', status: 4, interactive: true },
        { fieldName: 'misscardCount', label: '缺卡', unit: '次', value: '', status: 3, interactive: true },
        { fieldName: 'overTimeCount', label: '加班', unit: '次', value: '',
          interactive: true, placeholder: '请选择加班类型',
          option: [{ label: '工作日加班', value: 1 }, { label: '休息日加班', value: 2 }] },
        { fieldName: 'leaveDays', label: '请假', unit: '天', value: '',
          interactive: true, placeholder: '请选择请假类型',
          option: [] },
        { fieldName: 'outDays', label: '外勤', unit: '次', value: '', interactive: true }
      ],
      tabsMap: {
        overTimeCount: [{ label: '加班类型', prop: 'overTimeType' },
          { label: '加班开始时间', prop: 'overTimeStartTime' },
          { label: '加班结束时间', prop: 'overTimeEndTime' },
          { label: '加班时长', prop: 'overTimes' }],
        leaveDays: [{ label: '请假类型', prop: 'leaveType' },
          { label: '请假开始时间', prop: 'leaveStartTime' },
          { label: '请假结束时间', prop: 'leaveEndTime' },
          { label: '请假天数', prop: 'leaveDay' },
          { label: '请假事由', prop: 'leaveReason' }],
        outDays: [{ label: '日期', prop: 'attendanceTime' },
          { label: '外勤打卡时间', prop: 'clockTime' },
          { label: '外勤打卡地址', prop: 'address' }]
      },
      activeTab: 'attendance'
    }
  },
  computed: {
    showSelect() {
      return ['overTimeCount', 'leaveDays'].includes(this.activeTab)
    },
    showTable() {
      return ['overTimeCount', 'leaveDays', 'outDays'].includes(this.activeTab)
    }
  },
  created() {
    this.init()
    this.getLeaveTypeList()
  },
  methods: {
    init() {
      this.tabs.forEach(tab => {
        tab.value = this.detailData[tab.fieldName] || 0
      })
    },
    /**
      * tabs点击
    */
    tabHandle(item) {
      this.activeTab = item.fieldName
      if (item.hasOwnProperty('interactive') && item.interactive) {
        if (['overTimeCount', 'leaveDays', 'outDays'].includes(item.fieldName)) {
          this.getListData(item)
        } else {
          this.$emit('tabChange', item.status)
        }
      }
    },
    getListData(item) {
      const params = {
        pageType: 0,
        employeeId: this.detailData.employeeId,
        times: this.detailData.times
      }
      if (item.fieldName == 'overTimeCount') {
        params.overTimeType = item.selectdValue
      } else if (item.fieldName == 'leaveDays' && item.selectdValue) {
        params.leaveTypes = [item.selectdValue]
      }
      const request = {
        overTimeCount: hrmQueryOverTimeRecordPageListAPI,
        leaveDays: hrmQueryLeaveRecordPageListAPI,
        outDays: hrmQueryOutCardPageListAPI
      }[item.fieldName]
      this.loading = true
      request(params).then(res => {
        this.loading = false
        this.$set(item, 'listData', res.data.list)
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * @description: 获取请假类型
     * @return {*}
     */
    getLeaveTypeList() {
      hrmEmployeeLeaveRecordQueryLeaveTypeAPI()
        .then(res => {
          this.tabs.forEach(item => {
            if (item.fieldName == 'leaveDays') {
              this.$set(item, 'option', res.data)
            }
          })
        })
    },

    /**
     * @description: 字段格式化
     * @param {*} row
     * @param {*} column
     * @return {*}
     */
    fieldFormatter(row, column) {
      if (column.property == 'overTimeType') {
        return {
          1: '工作日加班',
          2: '休息日加班'
        }[row.overTimeType]
      }

      return row[column.property] || '--'
    }
  }
}
</script>

<style lang="scss" scoped>
.attendance-detail {
  padding-bottom: 16px;

  .attendance-cycle {
    display: flex;
    align-items: center;

    &-date {
      margin-left: 30px;
    }
  }

  .attendance-group {
    display: flex;
    line-height: 50px;

    &-item {
      flex: 1;

      .attendance-group-value {
        margin-left: 20px;
      }
    }
  }

  .attendance-days {
    .el-button {
      padding: 15px 10px;
    }
  }

  .attendance-content {
    margin-top: 15px;

    .content-table {
      width: 80%;
      margin-left: 30px;

      .el-select {
        margin-bottom: 20px;
      }
    }
  }
}

::v-deep .el-calendar__header {
  display: none;
}
</style>
