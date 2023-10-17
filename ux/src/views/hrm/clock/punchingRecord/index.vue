<template>
  <div id="employee-main-container" class="clock-index">
    <wk-page-header
      title="月度汇总"
      :dropdowns="headerMoreHandle"
      @command="headerMoreHandleClick">
      <el-menu
        slot="title"
        :default-active="recordType"
        mode="horizontal"
        @select="menuSelect">
        <el-menu-item index="punching">打卡概况</el-menu-item>
        <el-menu-item index="original">打卡明细</el-menu-item>
      </el-menu>

      <el-button
        v-if="(recordType == 'punching' && synchronousClockInAuth) || (recordType == 'original' && createPunchInRecordAuth)"
        slot="right"
        type="primary"
        @click="addOrSync">
        {{ headerBtnContent }}
      </el-button>
    </wk-page-header>

    <wk-filter-header
      :props="{
        searchPlaceholder: '请输入员工姓名/工号',
      }"
      :selection-list="recordType === 'punching' ? [] : originalSelectionList"
      :operations="headerOptionList"
      @operations-click="selectionBarClick"
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
          @change="componentRefreshList" />
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
          @sure="componentRefreshList"
          @reset="resetFilter">
          <template slot-scope="{ data }">
            <el-select
              v-model="filterObj[data.field]"
              multiple
              collapse-tags
              style="margin-left: 20px;"
              placeholder="请选择"
              @change="typesChange">
              <el-option
                v-for="(item, index) in data.setting"
                :key="index"
                :label="item.label"
                :value="item.value" />
            </el-select>
          </template>
        </wk-popover-filter>
      </template>
    </wk-filter-header>

    <div class="crm-container">
      <component
        :is="componentName"
        ref="componentRef"
        @syncSuccess="$message.success('同步成功')"
        @selection-change="originalSelectionChange" />
    </div>

    <el-dialog
      v-if="showAddDialog"
      v-loading="loading"
      :visible.sync="showAddDialog"
      :before-close="handleClose"
      :title="dialogTitle"
      width="600px">
      <el-form
        ref="elForm"
        :model="fieldForm"
        :rules="fieldRules"
        :validate-on-rule-change="false"
        label-position="top">
        <wk-form-item
          v-for="(item, index) in formFieldList"
          :key="index"
          class="wk-form-item"
          :ignore-fields="['clockTime', 'attendanceTime']"
          :field-from="fieldForm"
          :item="item"
          @change="formChange">
          <template slot-scope="{ data }">
            <el-date-picker
              v-if="item.formType == 'date'"
              v-model="fieldForm[data.field]"
              :disabled="!fieldForm.clockEmployeeId"
              :picker-options="pickerOptions"
              style="width: 100%;"
              value-format="yyyy-MM-dd"
              type="date"
              placeholder="选择日期"
              @change="attendanceTimeChange" />
            <range-time-picker
              v-if="item.formType == 'datetime'"
              v-model="fieldForm[data.field]"
              class="range-time-picker"
              :disabled="!fieldForm.attendanceTime"
              :class="{'disabled': !fieldForm.attendanceTime}"
              :selectable-range="clockTimeRange"
            />
          </template>
        </wk-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="handleClose">取 消</el-button>
        <el-button type="primary" @click="handleSure">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  hrmAttendanceClockExcelExportAPI,
  hrmAttendanceClockAddAPI,
  hrmAttendanceClockUpdateAPI,
  hrmAttendanceClockQueryCurrentAttendShiftTimeAPI,
  hrmEmployeeQueryInspectionDeptEmployeeListAPI,
  excelEmpMonthDailyDetailExportAPI,
  hrmEmployeeQueryAttendanceAllEmployeeListAPI
} from '@/api/hrm/clock'
import {
  hrmDeptQueryTreeListAPI
} from '@/api/hrm/dept'

import WkPageHeader from '@/components/Page/WkPageHeader'
import WkFilterHeader from '@/components/NewCom/WkFilterHeader'
import WkPopoverFilter from '@/components/NewCom/WkPopoverFilter'
import WkFormItem from '@/components/NewCom/WkForm/WkFormItem'
import PunchingTable from './PunchingTable'
import OriginalTable from './OriginalTable'
import RangeTimePicker from '@/views/admin/hrm/schedule/classesSet/components/RangeTimePicker'

import { mapGetters } from 'vuex'
import { downloadExcelWithResData, objDeepCopy } from '@/utils'
import { isEmpty } from '@/utils/types'
import moment from 'moment'

export default {
  name: 'ClockIndex',
  components: {
    WkPageHeader,
    WkFilterHeader,
    WkPopoverFilter,
    WkFormItem,
    PunchingTable,
    OriginalTable,
    RangeTimePicker
  },
  data() {
    return {
      loading: false,
      recordType: 'punching', // 当前列表类型
      originalSelectionList: [], // 原始数据勾选
      month: moment().format('YYYY-MM'), // 月份
      search: '',
      // 高级筛选
      filterObj: {}, // 筛选确定数据
      componentName: 'PunchingTable', // 当前列表
      showAddDialog: false,
      fieldForm: {
        clockType: 1,
        clockStage: 1,
        clockTime: '',
        attendanceTime: '',
        type: 2
      }, // 新建打卡表单
      fieldRules: {
        clockEmployeeId: [{ required: true, message: '请输入员工姓名', trigger: 'blur' }],
        clockType: [{ required: true, message: '请输入打卡类型', trigger: 'blur' }],
        clockTime: [{ required: true, message: '请输入打卡时间', trigger: 'blur' }],
        clockStage: [{ required: true, message: '请输入打卡阶段', trigger: 'blur' }],
        attendanceTime: [{ required: true, message: '请选择打卡日期', trigger: 'blur' }]
      }, // 表单规则
      formFieldList: [ // 表单字段
        {
          name: '员工姓名',
          field: 'clockEmployeeId',
          formType: 'user',
          disabled: false,
          props: {
            label: 'employeeName',
            value: 'employeeId',
            request: hrmEmployeeQueryInspectionDeptEmployeeListAPI,
            searchRequest: hrmEmployeeQueryAttendanceAllEmployeeListAPI
          },
          redio: true
        },
        {
          name: '打卡类型',
          field: 'clockType',
          formType: 'select',
          redio: true,
          setting: [
            { label: '上班打卡', value: 1 },
            { label: '下班打卡', value: 2 }
          ]
        },
        {
          name: '打卡日期',
          field: 'attendanceTime',
          formType: 'date'
        },
        {
          name: '打卡时间',
          field: 'clockTime',
          formType: 'datetime'
        },
        {
          name: '备注',
          field: 'remark',
          formType: 'textarea',
          redio: true
        }
      ],
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now()
        }
      },
      dialogTitle: '新建打卡记录',
      clockTimeObj: {},
      createClockFlag: true,
      createMessage: ''
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

    // 同步打卡记录权限
    synchronousClockInAuth() {
      return this.attendanceAuth && this.attendanceAuth.punching
    },

    // 新建打卡记录权限
    createPunchInRecordAuth() {
      return this.attendanceAuth && this.attendanceAuth.addClock
    },

    // 导出打卡记录权限
    exportPunchInRecord() {
      return this.attendanceAuth && this.attendanceAuth.excelexport
    },

    /**
     * 头部更多操作
     */
    headerMoreHandle() {
      const temps = []
      if (this.exportPunchInRecord) {
        temps.push({ command: 'export', name: '导出', icon: 'wk wk-export' })
      }
      return temps
    },

    filterList() {
      const arr = [{
        name: '部门',
        field: 'deptIds',
        formType: 'structure',
        radio: false,
        props: {
          dataType: 'hrm'
        },
        request: hrmDeptQueryTreeListAPI,
        setting: []
      }]

      if (this.recordType == 'original') {
        arr.push(...[{
          name: '打卡类型',
          field: 'clockType',
          formType: 'select',
          setting: [{
            label: '上班打卡',
            value: 1
          }, {
            label: '下班打卡',
            value: 2
          }]
        }, {
          name: '打卡地点',
          field: 'address',
          formType: 'text'
        }, {
          name: '打卡来源',
          field: 'types',
          formType: 'types',
          setting: [{
            label: '手机端打卡',
            value: 1
          }, {
            label: 'HR添加',
            value: 2
          }]
        }])
      }

      return arr
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
    headerBtnContent() {
      return {
        punching: '同步打卡记录',
        original: '新建打卡记录'
      }[this.recordType]
    },

    headerOptionList() {
      const arr = [{
        name: '删除',
        type: 'delete',
        icon: 'wk wk-icon-delete-line'
      }]

      // const result = this.originalSelectionList.every(item => item.type == 2)
      // if (!result) {
      //   return []
      // }

      return arr
    },

    // 打卡时间可选择范围
    clockPickerOptions() {
      const that = this
      return {
        disabledDate(time) {
          if (!isEmpty(that.clockTimeObj)) {
            // 上班最早打卡时间,上班最晚打卡时间,下班最早打卡时间,下班最晚打卡时间
            const { advanceTime, lateTime, earlyTime, postponeTime } = that.clockTimeObj
            if (advanceTime && lateTime && earlyTime && postponeTime) {
              if (that.fieldForm.clockType == 1) { // 上班打卡
                // 选择范围  上班最早打卡时间 ~ 上班最晚打卡时间
                const startOfWork = moment(advanceTime).format('YYYY-MM-DD') // 上班开始
                const endOfWork = moment(lateTime).format('YYYY-MM-DD') // 上班结束
                return time.getTime() != moment(startOfWork) && time.getTime() != moment(endOfWork)
              } else if (that.fieldForm.clockType == 2) { // 下班打卡
                // 选择范围  下班最早打卡时间 ~ 下班最晚打卡时间
                const startOfWork = moment(earlyTime).format('YYYY-MM-DD') // 下班开始
                const endOfWork = moment(postponeTime).format('YYYY-MM-DD') // 下班结束
                return time.getTime() != moment(startOfWork) && time.getTime() != moment(endOfWork)
              }
            }

            return time.getTime() != moment(that.fieldForm.attendanceTime)
          }
          return time.getTime() != moment(that.fieldForm.attendanceTime)
        }
      }
    },

    // 打卡时分秒可选择范围
    clockTimeRange() {
      const { clockType } = this.fieldForm
      if (!isEmpty(this.clockTimeObj)) {
        const { advanceTime, lateTime, earlyTime, postponeTime } = this.clockTimeObj

        if (!advanceTime && !lateTime && !earlyTime && !postponeTime) {
          return ['00:00', '23:59']
        }

        if (clockType == 1) { // 上班
          const advanceDate = moment(advanceTime).format('YYYY-MM-DD')
          const lateDate = moment(lateTime).format('YYYY-MM-DD')
          if (moment(advanceDate).isSame(lateDate)) { // 同一天
            return [moment(advanceTime).format('HH:mm:ss'), moment(lateTime).format('HH:mm:ss')]
          } else {
            const endTimeH = Number(moment(lateTime).format('HH')) + 24
            const endTimem = moment(lateTime).format('mm')
            const endTimes = moment(lateTime).format('ss')
            return [moment(advanceTime).format('HH:mm:ss'), `${endTimeH}:${endTimem}:${endTimes}`]
          }
        } else if (clockType == 2) { // 下班
          const earlyDate = moment(earlyTime).format('YYYY-MM-DD')
          const postponeDate = moment(postponeTime).format('YYYY-MM-DD')
          if (moment(earlyDate).isSame(postponeDate)) { // 同一天
            return [moment(earlyTime).format('HH:mm:ss'), moment(postponeTime).format('HH:mm:ss')]
          } else {
            const endTimeH = Number(moment(postponeTime).format('HH')) + 24
            const endTimem = moment(postponeTime).format('mm')
            const endTimes = moment(postponeTime).format('ss')
            return [moment(earlyTime).format('HH:mm:ss'), `${endTimeH}:${endTimem}:${endTimes}`]
          }
        }
      }
      return ['00:00', '23:59']
    },

    // 是否为休息日打卡
    isRestDayClock() {
      return !isEmpty(this.clockTimeObj) && !this.clockTimeObj.start && !this.clockTimeObj.end
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

  },
  methods: {
    /**
     * 刷新
     */
    componentRefreshList() {
      this.$refs.componentRef.refreshList()
    },

    handleClose() {
      this.showAddDialog = false
      this.dialogTitle = '新建打卡记录'
      this.formFieldList.forEach(item => {
        if (item.formType == 'user') {
          this.$set(item, 'disabled', false)
        }
      })
      this.$refs.elForm.resetFields()
    },
    handleSure() {
      if (!this.createClockFlag) {
        this.$message.error(this.createMessage)
        return
      }

      this.loading = true
      const instance = this.$refs.elForm
      instance.validate((valid) => {
        if (valid) {
          const params = objDeepCopy(this.fieldForm)
          const { attendanceTime, clockTime, clockType } = this.fieldForm
          const { start, end } = this.clockTimeObj
          let newClockTime = ''
          const hour = clockTime.split(':')[0]
          if (hour > 23) {
            newClockTime = `${moment(attendanceTime).add(1, 'days').format('YYYY-MM-DD')} 0${Number(hour) - 24}:${clockTime.split(':')[1]}:00`
          } else {
            newClockTime = `${attendanceTime} ${clockTime}:00`
          }

          params.clockTime = newClockTime
          const startTime = moment(`2023-01-01 ${start || '00:00'}`).format('HH:mm:ss')
          const endTime = moment(`2023-01-01 ${end || '00:00'}`).format('HH:mm:ss')
          params.attendanceTime = `${params.attendanceTime} ${clockType == 1 ? startTime : endTime}`

          if (this.isRestDayClock) { // 休息日打卡
            params.clockStatus = 7
          }
          const flag = this.fieldForm.hasOwnProperty('isUpdate') && this.fieldForm.isUpdate
          const request = flag ? hrmAttendanceClockUpdateAPI : hrmAttendanceClockAddAPI
          request(params).then(res => {
            this.loading = false
            this.$message.success(flag ? '修改成功' : '添加成功')
            this.showAddDialog = false
            this.componentRefreshList()
          }).catch(() => { this.loading = false })
        } else {
          this.loading = false
          if (instance.fields) {
            for (
              let index = 0;
              index < instance.fields.length;
              index++
            ) {
              const ruleField = instance.fields[index]
              if (ruleField.validateState == 'error') {
                this.$message.error(ruleField.validateMessage)
                break
              }
            }
          }
        }
      })
    },
    formChange(data, index, value) {
      if (data.field == 'clockEmployeeId') {
        this.$refs.elForm.validateField('clockEmployeeId')
        if (value.length) { // 选择/更改员工
          this.getSelectTimeClockTime()
        } else { // 清除员工
          this.$set(this.fieldForm, 'attendanceTime', '')
          this.$set(this.fieldForm, 'clockTime', '')
        }
      }
    },

    /**
     * @description: 打卡日期发生变动
     * @return {*}
     */
    attendanceTimeChange(value) {
      this.$set(this.fieldForm, 'clockTime', '')
      if (value) {
        this.getSelectTimeClockTime()
      }
    },

    /**
     * @description: 根据选择时间获取打卡时间
     * @return {*}
     */
    getSelectTimeClockTime(time) {
      if (!this.fieldForm.clockEmployeeId || !this.fieldForm.attendanceTime) {
        return
      }

      const params = {
        employeeId: this.fieldForm.clockEmployeeId,
        currentDate: this.fieldForm.attendanceTime
      }
      hrmAttendanceClockQueryCurrentAttendShiftTimeAPI(params)
        .then(res => {
          this.clockTimeObj = res.data || {}
          const { status, end, start, advanceTime, earlyTime } = res.data
          if (!status) {
            this.createMessage = '该员工当天尚未创建，无法添加打卡记录'
            this.$message.error(this.createMessage)
            this.createClockFlag = false
          }

          if (!end && !start) {
            this.fieldForm.clockTime = '00:00'
          } else if (this.fieldForm.clockType == 1) { // 上班
            this.fieldForm.clockTime = moment(advanceTime).format('HH:mm')
          } else if (this.fieldForm.clockType == 2) { // 下班
            this.fieldForm.clockTime = moment(earlyTime).format('HH:mm')
          }

          if (time) {
            this.fieldForm.clockTime = time
          }
        })
    },

    /**
     * 添加或同步
     */
    addOrSync() {
      if (this.recordType == 'punching') {
        this.$refs.componentRef.syncData()
      } else {
        this.fieldForm = {
          clockType: 1,
          clockStage: 1,
          clockTime: '',
          attendanceTime: '',
          type: 2
        }
        this.clockTimeObj = {}

        this.showAddDialog = true
      }
    },
    menuSelect(val) {
      this.componentName = { original: 'OriginalTable', punching: 'PunchingTable' }[val]
      this.recordType = val
      // 切换重置勾选
      this.originalSelectionList = []
      this.search = ''
      this.filterObj = {}
    },
    /**
     * 高级筛选
     */
    resetFilter() {
      this.filterObj = {}
      this.componentRefreshList()
    },
    /**
     * 搜索
     */
    filterHeaderHandle(type, data) {
      if (type === 'search') {
        this.search = data
        this.componentRefreshList()
      }
    },

    typesChange() {
      this.$set(this.$refs.popoverFilter.$data.from, 'types', this.filterObj.types)
    },

    /**
     * 头部更多操作
     */
    headerMoreHandleClick(command) {
      if (command == 'export') {
        const params = objDeepCopy(this.$refs.componentRef.params)
        this.loading = true
        const request = this.recordType == 'original' ? hrmAttendanceClockExcelExportAPI : excelEmpMonthDailyDetailExportAPI
        request(params).then(res => {
          downloadExcelWithResData(res)
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
      }
    },

    /**
     * @description: 原始数据勾选
     * @param {*}
     * @return {*}
     */
    originalSelectionChange(val) {
      this.originalSelectionList = val
    },

    /**
     * @description: 原始数据勾选点击
     * @param {*}
     * @return {*}
     */
    selectionBarClick(commond) {
      if (commond === 'delete') {
        this.$refs.componentRef.delRecord()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/wk-form.scss";

.clock-index {
  ::v-deep .el-menu {
    .el-menu-item + .el-menu-item {
      margin-left: 16px;
    }

    .el-menu-item {
      padding: 0;
      font-size: 20px;
      color: #6b778c;
      border-bottom: unset;
    }

    .is-active {
      font-size: 24px;
      color: #172b4d;
    }
  }

  .wk-filter-header {
    margin-top: 30px;
    margin-bottom: 20px;
    line-height: 32px;
  }

  .el-date-editor.el-input {
    width: 130px;
  }

  .el-menu-item {
    height: auto;
    font-size: 24px;
    line-height: initial;
  }

  .el-menu.el-menu--horizontal {
    border-bottom: 0;
  }
}

.range-time-picker {
  width: 60%;

  &.disabled {
    color: #97a0af;
    cursor: not-allowed;
    background-color: #ebecf0;
    border-color: #dfe1e6;
  }
}
</style>
