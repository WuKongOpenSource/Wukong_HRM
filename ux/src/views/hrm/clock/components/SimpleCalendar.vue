<template>
  <table border="1">
    <tr>
      <th v-for="j in 7" :key="`_` + j">{{ weekDays[j - 1] }}</th>
    </tr>
    <tr v-for="i in 6" :key="i">
      <td v-for="j in 7" :key="j">
        <div
          :class="{notCurrentMonth: !isCurrentMonth(visibleDays[(i - 1) * 7 + (j - 1)])}"
          class="cell-row">
          <span>{{ getLunarCalendar(visibleDays[(i - 1) * 7 + (j - 1)]) }}</span>
          <span :class="{today:isToday(visibleDays[(i - 1) * 7 + (j - 1)])}">{{ visibleDays[(i - 1) * 7 + (j - 1)].getDate() }}</span>
        </div>
        <calendar-cell :item="getCalendarCell(visibleDays[(i - 1) * 7 + (j - 1)])" />
      </td>
    </tr>
  </table>
</template>
<script>
import calendar from '@/views/hrm/workbench/components/calendar'
import CalendarCell from './CalendarCell'
export default {

  components: {
    CalendarCell
  },
  props: {
    value: {
      type: Date,
      default: () => new Date()
    },
    dateList: Array
  },
  data() {
    const { year, month } = this.getYearMonthDay(this.value)
    return {
      weekDays: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      time: { year, month },
      dayTimes: 60 * 60 * 1000 * 24,
      lunarCalendar: calendar.solar2lunar
    }
  },
  computed: {
    visibleDays() {
      const { year, month } = this.getYearMonthDay(
        this.getDate(this.time.year, this.time.month, 1)
      )
      const currentFirstDay = this.getDate(year, month, 1)
      let week = currentFirstDay.getDay()
      if (week == 0) {
        week = 7
      }
      const startDay = currentFirstDay - (week - 1) * this.dayTimes
      const arr = []
      for (let i = 0; i < 42; i++) {
        arr.push(new Date(startDay + i * this.dayTimes))
      }
      return arr
    }
  },
  methods: {
    getCalendarCell(date) {
      const _date = this.$moment(date).format('YYYY-MM-DD')
      const findResult = this.dateList.find(item => item[_date])
      if (findResult) {
        return findResult[_date]
      }
      return {}
    },
    getLunarCalendar(date) {
      const { year, month, day } = this.getYearMonthDay(date)
      return calendar.solar2lunar(year, month + 1, day).IDayCn
    },

    getYearMonthDay(date) {
      const year = date.getFullYear()
      const month = date.getMonth()
      const day = date.getDate()
      return { year, month, day }
    },
    getDate(year, month, day) {
      return new Date(year, month, day)
    },

    isCurrentMonth(date) {
      const { year, month } = this.getYearMonthDay(
        this.getDate(this.time.year, this.time.month, 1)
      )
      const { year: y, month: m } = this.getYearMonthDay(date)
      return year === y && month === m
    },
    // 是否是今天
    isToday(date) {
      const { year, month, day } = this.getYearMonthDay(new Date())
      const { year: y, month: m, day: d } = this.getYearMonthDay(date)
      return year === y && month === m && day === d
    }
  }
}
</script>
<style scoped lang="scss">
table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #e6e6e6;
}

th {
  height: 50px;
  text-align: center;
}

td {
  width: calc(100% / 7);
  height: 150px;
  padding: 6px;
  line-height: 20px;
  vertical-align: top;
}

.cell-row {
  display: flex;
  justify-content: space-between;
}

.notCurrentMonth {
  color: gray;
}

.today {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  color: white;
  background-color: #2362fb;
  border: 1px solid #2362fb;
  border-radius: 50%;
}
</style>
