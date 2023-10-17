<template>
  <div class="container">
    <wk-page-header
      title="考勤报表"
      :dropdowns="headerMoreHandle"
      @command="headerMoreHandleClick"
    />

    <div class="main">
      <flexbox>
        <el-date-picker
          v-model="screenTime"
          :picker-options="pickerOptions"
          type="month"
          value-format="yyyy-MM-dd"
          placeholder="选择年月"
          @change="getList" />
        <span>考勤周期（{{ attendanceCycle }}）</span>
      </flexbox>
      <simple-calendar
        :key="screenTime"
        v-loading="loading"
        class="simple-calendar"
        :date-list="list"
        :value="$moment(screenTime).toDate()" />
    </div>
  </div>
</template>

<script>
import {
  hrmQueryAttendanceEmpMonthDailyDetailAPI,
  excelEmpMonthDailyDetailExportAPI
} from '@/api/hrm/clock'

import SimpleCalendar from '@/views/hrm/clock/components/SimpleCalendar'
import WkPageHeader from '@/components/Page/WkPageHeader'

import { downloadExcelWithResData } from '@/utils'
import moment from 'moment'
import { mapGetters } from 'vuex'

export default {
  name: 'AttendanceReport', // 考勤报表
  components: {
    WkPageHeader,
    SimpleCalendar
  },
  data() {
    return {
      loading: false,
      screenTime: '', // 筛选时间
      pickerOptions: { // 日期选择器配置
        disabledDate(time) {
          return time.getTime() > Date.now()
        }
      },
      list: []
    }
  },
  computed: {
    ...mapGetters(['hrmUserInfo']),

    // 头部更多操作
    headerMoreHandle() {
      const temps = []
      temps.push({ command: 'export', name: '导出', icon: 'wk wk-export' })
      return temps
    },

    // 考勤周期
    attendanceCycle() {
      return `${moment(this.screenTime).format('MM')}月01日~${moment(this.screenTime).endOf('month').format('MM')}月${moment(this.screenTime).endOf('month').format('DD')}日`
    }
  },
  mounted() {
    this.screenTime = moment().format('YYYY-MM-01')

    this.getList()
  },
  methods: {
    /**
     * @description: 获取表格数据
     * @return {*}
     */
    getList() {
      this.loading = true
      const params = {
        employeeId: this.hrmUserInfo.employeeId,
        status: -1,
        times: [this.screenTime, moment(this.screenTime).endOf('month').format('YYYY-MM-DD')]
      }

      hrmQueryAttendanceEmpMonthDailyDetailAPI(params)
        .then(res => {
          const result = res.data.dateList.map((item, index) => {
            return {
              ...item,
              key: index
            }
          })
          this.list = result
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * @description: 头部更多操作
     * @param {*} command
     * @return {*}
     */
    headerMoreHandleClick(command) {
      if (this.loading == true) return
      if (command == 'export') {
        this.loading = true
        const params = {
          employeeId: this.hrmUserInfo.employeeId,
          times: [this.screenTime, moment(this.screenTime).endOf('month').format('YYYY-MM-DD')]
        }

        excelEmpMonthDailyDetailExportAPI(params).then(res => {
          downloadExcelWithResData(res)
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  .wk-page-header {
    margin-bottom: 20px;
  }

  .main {
    .vux-flexbox {
      span {
        margin-left: 8px;
      }
    }

    ::v-deep .simple-calendar {
      margin-top: 10px;

      td {
        height: 110px;
      }
    }
  }
}
</style>
