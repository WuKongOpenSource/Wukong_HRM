<template>
  <div class="calendar">
    <div class="calendar-tools">

      <div class="calendar-info" @click.stop="changeYear">
        <!-- {{monthString}} -->
        <i class="el-icon-arrow-left calendar-prev" @click="prev" />
        <span class="time">{{ `${year}-${zeroPad(month + 1)}` }}</span>
        <i class="el-icon-arrow-right calendar-next" @click="next" />
      </div>
    </div>
    <table cellpadding="5">
      <thead>
        <tr>
          <td v-for="week in weeks" :key="week" class="week">{{ week }}</td>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="(dayItem,k1) in days"
          :key="k1"
          :style="{'animation-delay':(k1*30)+'ms'}">
          <td
            v-for="(child,k2) in dayItem"
            :key="k2"
            :class="{'selected':child.selected,'disabled':child.disabled,'circle': child.hasEvent,'today': child.isToday}"
            @click="select(k1,k2,$event)">
            <!-- <span :class="{'red':k2==0||k2==6||((child.isLunarFestival||child.isGregorianFestival) && lunar)}">{{ child.day }}</span> -->
            <span>{{ child.day }}</span>
            <!-- <div v-if="child.eventName!=undefined" class="text">{{ child.eventName }}</div> -->
            <div
              v-if="lunar"
              :class="{'isLunarFestival':child.isLunarFestival,'isGregorianFestival':child.isGregorianFestival}"
              class="text">{{ child.lunar }}</div>
          </td>
        </tr>
      </tbody>
    </table>

    <div :class="{'show':yearsShow}" class="calendar-years">
      <span v-for="y in years" :key="y" :class="{'active':y==year}" @click.stop="selectYear(y)">{{ y }}</span>
    </div>

  </div>
</template>

<script>
import calendar from './calendar.js'

export default {
  name: 'WkCalendar',
  props: {
    // 多选模式
    multi: {
      type: Boolean,
      default: false
    },
    // 范围模式
    range: {
      type: Boolean,
      default: false
    },
    // 默认日期
    value: {
      type: Array,
      default: function() {
        return []
      }
    },
    // 开始选择日期
    begin: {
      type: Array,
      default: function() {
        return []
      }
    },
    // 结束选择日期
    end: {
      type: Array,
      default: function() {
        return []
      }
    },

    // 是否小于10补零
    zero: {
      type: Boolean,
      default: false
    },
    // 屏蔽的日期
    disabled: {
      type: Array,
      default: function() {
        return []
      }
    },
    // 是否显示农历
    lunar: {
      type: Boolean,
      default: false
    },

    // 自定义星期名称
    weeks: {
      type: Array,
      default: function() {
        return ['日', '一', '二', '三', '四', '五', '六']
      }
    },
    // 自定义月份
    months: {
      type: Array,
      default: function() {
        return ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月']
      }
    },
    // 自定义事件
    events: {
      type: Array,
      default: function() {
        return []
      }
    }
  },
  data() {
    return {
      years: [],
      yearsShow: false,
      year: 0,
      month: 0,
      day: 0,
      days: [],
      multiDays: [],
      today: [],
      festival: {
        lunar: {
          '1-1': '春节',
          '1-15': '元宵节',
          '2-2': '龙头节',
          '5-5': '端午节',
          '7-7': '七夕节',
          '7-15': '中元节',
          '8-15': '中秋节',
          '9-9': '重阳节',
          '10-1': '寒衣节',
          '10-15': '下元节',
          '12-8': '腊八节',
          '12-23': '祭灶节'
        },
        gregorian: {
          '1-1': '元旦',
          '2-14': '情人节',
          '3-8': '妇女节',
          '3-12': '植树节',
          '4-5': '清明节',
          '5-1': '劳动节',
          '5-4': '青年节',
          '6-1': '儿童节',
          '7-1': '建党节',
          '8-1': '建军节',
          '9-10': '教师节',
          '10-1': '国庆节',
          '12-24': '平安夜',
          '12-25': '圣诞节'
        }
      },
      rangeBegin: [],
      rangeEnd: [],
      // 目前用于单选下，选中的记录
      selectValue: []
    }
  },
  watch: {
    events() {
      this.render(this.year, this.month)
    },
    value() {
      this.selectValue = this.value
      this.init()
    }
  },
  mounted() {
    this.selectValue = this.value
    this.init()
  },
  methods: {
    /**
     * 初始化
     */
    init() {
      const now = new Date()
      this.year = now.getFullYear()
      this.month = now.getMonth()
      this.day = now.getDate()
      if (this.value.length > 0) {
        if (this.range) { // 范围
          this.year = parseInt(this.value[0][0])
          this.month = parseInt(this.value[0][1]) - 1
          this.day = parseInt(this.value[0][2])

          const year2 = parseInt(this.value[1][0])
          const month2 = parseInt(this.value[1][1]) - 1
          const day2 = parseInt(this.value[1][2])

          this.rangeBegin = [this.year, this.month, this.day]
          this.rangeEnd = [year2, month2, day2]
        } else if (this.multi) { // 多选
          this.multiDays = this.value
          this.year = parseInt(this.value[0][0])
          this.month = parseInt(this.value[0][1]) - 1
          this.day = parseInt(this.value[0][2])
        } else { // 单选
          this.year = parseInt(this.value[0])
          this.month = parseInt(this.value[1]) - 1
          this.day = parseInt(this.value[2])
        }
      }
      this.render(this.year, this.month)
    },

    /**
     * 通过年月日 选择
     */
    selectDay(year, month, day) {
      this.year = year
      this.month = month - 1
      this.day = day
      this.selectValue = [this.year, month, day]
      this.renderWithValue(this.year, this.month, this.selectValue)
    },

    /**
     * 渲染日期
     */
    render(y, m) {
      this.renderWithValue(y, m, this.selectValue)
    },

    renderWithValue(y, m, selectValue) {
      const firstDayOfMonth = new Date(y, m, 1).getDay() // 当月第一天
      const lastDateOfMonth = new Date(y, m + 1, 0).getDate() // 当月最后一天
      const lastDayOfLastMonth = new Date(y, m, 0).getDate() // 最后一月的最后一天
      this.year = y
      const seletSplit = selectValue
      let i
      let line = 0
      const temp = []
      let nextMonthPushDays = 1
      for (i = 1; i <= lastDateOfMonth; i++) {
        const day = new Date(y, m, i).getDay() // 返回星期几（0～6）
        let k
        // 第一行
        if (day == 0) {
          temp[line] = []
        } else if (i == 1) {
          temp[line] = []
          k = lastDayOfLastMonth - firstDayOfMonth + 1
          for (let j = 0; j < firstDayOfMonth; j++) {
            // console.log("第一行",lunarYear,lunarMonth,lunarValue,lunarInfo)
            let year = y
            let month = m
            if (m === 0) {
              year = y - 1
              month = 12
            }
            temp[line].push(Object.assign(
              { day: k, disabled: true, year, month },
              this.getLunarInfo(this.computedPrevYear(), this.computedPrevMonth(true), k),
              this.getEvents(this.computedPrevYear(), this.computedPrevMonth(true), k)
            ))
            k++
          }
        }

        if (this.range) { // 范围
          // console.log("日期范围",this.getLunarInfo(this.year,this.month+1,i))
          const options = Object.assign(
            { day: i },
            this.getLunarInfo(this.year, this.month + 1, i),
            this.getEvents(this.year, this.month + 1, i)
          )
          if (this.rangeBegin.length > 0) {
            const beginTime = Number(new Date(this.rangeBegin[0], this.rangeBegin[1], this.rangeBegin[2]))
            const endTime = Number(new Date(this.rangeEnd[0], this.rangeEnd[1], this.rangeEnd[2]))
            const stepTime = Number(new Date(this.year, this.month, i))
            if (beginTime <= stepTime && endTime >= stepTime) {
              options.selected = true
            }
          }
          if (this.begin.length > 0) {
            const beginTime = Number(new Date(parseInt(this.begin[0]), parseInt(this.begin[1]) - 1, parseInt(this.begin[2])))
            if (beginTime > Number(new Date(this.year, this.month, i))) options.disabled = true
          }
          if (this.end.length > 0) {
            const endTime = Number(new Date(parseInt(this.end[0]), parseInt(this.end[1]) - 1, parseInt(this.end[2])))
            if (endTime < Number(new Date(this.year, this.month, i))) options.disabled = true
          }
          if (this.disabled.length > 0) {
            if (this.disabled.filter(v => { return this.year === v[0] && this.month === v[1] - 1 && i === v[2] }).length > 0) {
              options.disabled = true
            }
          }
          temp[line].push(options)
        } else if (this.multi) { // 多选
          let options
          // 判断是否选中
          if (selectValue.filter(v => { return this.year === v[0] && this.month === v[1] - 1 && i === v[2] }).length > 0) {
            options = Object.assign({ day: i, selected: true }, this.getLunarInfo(this.year, this.month + 1, i), this.getEvents(this.year, this.month + 1, i))
          } else {
            options = Object.assign({ day: i, selected: false }, this.getLunarInfo(this.year, this.month + 1, i), this.getEvents(this.year, this.month + 1, i))
            if (this.begin.length > 0) {
              const beginTime = Number(new Date(parseInt(this.begin[0]), parseInt(this.begin[1]) - 1, parseInt(this.begin[2])))
              if (beginTime > Number(new Date(this.year, this.month, i))) options.disabled = true
            }
            if (this.end.length > 0) {
              const endTime = Number(new Date(parseInt(this.end[0]), parseInt(this.end[1]) - 1, parseInt(this.end[2])))
              if (endTime < Number(new Date(this.year, this.month, i))) options.disabled = true
            }
            if (this.disabled.length > 0) {
              if (this.disabled.filter(v => { return this.year === v[0] && this.month === v[1] - 1 && i === v[2] }).length > 0) {
                options.disabled = true
              }
            }
          }

          temp[line].push(options)
        } else { // 单选
          // console.log(this.lunar(this.year,this.month,i));

          const chk = new Date()
          const chkY = chk.getFullYear()
          const chkM = chk.getMonth()
          const chkD = chk.getDate()
          // 匹配上次选中的日期
          if (parseInt(seletSplit[0]) == this.year && parseInt(seletSplit[1]) - 1 == this.month && parseInt(seletSplit[2]) == i) {
            // console.log("匹配上次选中的日期",lunarYear,lunarMonth,lunarValue,lunarInfo)
            temp[line].push(Object.assign(
              { day: i, selected: true, year: y, month: m + 1 },
              { isToday: this.year == chkY && this.month == chkM && chkD == i },
              this.getLunarInfo(this.year, this.month + 1, i),
              this.getEvents(this.year, this.month + 1, i)
            ))
            this.today = [line, temp[line].length - 1]
          } else if (chkY == this.year && chkM == this.month && i == this.day && selectValue == '') {
            // 没有默认值的时候显示选中今天日期
            // console.log("今天",lunarYear,lunarMonth,lunarValue,lunarInfo)
            temp[line].push(Object.assign(
              { day: i, selected: true, year: y, month: m + 1 },
              { isToday: this.year == chkY && this.month == chkM && chkD == i },
              this.getLunarInfo(this.year, this.month + 1, i),
              this.getEvents(this.year, this.month + 1, i)
            ))
            this.today = [line, temp[line].length - 1]
          } else {
            // 普通日期
            // console.log("设置可选范围",i,lunarYear,lunarMonth,lunarValue,lunarInfo)
            const options = Object.assign(
              { day: i, selected: false, year: y, month: m + 1 },
              { isToday: this.year == chkY && this.month == chkM && chkD == i },
              this.getLunarInfo(this.year, this.month + 1, i),
              this.getEvents(this.year, this.month + 1, i)
            )
            if (this.begin.length > 0) {
              const beginTime = Number(new Date(parseInt(this.begin[0]), parseInt(this.begin[1]) - 1, parseInt(this.begin[2])))
              if (beginTime > Number(new Date(this.year, this.month, i))) options.disabled = true
            }
            if (this.end.length > 0) {
              const endTime = Number(new Date(parseInt(this.end[0]), parseInt(this.end[1]) - 1, parseInt(this.end[2])))
              if (endTime < Number(new Date(this.year, this.month, i))) options.disabled = true
            }
            if (this.disabled.length > 0) {
              if (this.disabled.filter(v => { return this.year === v[0] && this.month === v[1] - 1 && i === v[2] }).length > 0) {
                options.disabled = true
              }
            }
            temp[line].push(options)
          }
        }
        // 到周六换行
        if (day == 6 && i < lastDateOfMonth) {
          line++
        } else if (i == lastDateOfMonth) {
          // line++
          let year = y
          let month = m + 2
          if (m === 11) {
            year = y + 1
            month = 1
          }
          let k = 1
          for (let d = day; d < 6; d++) {
            // console.log(this.computedNextYear()+"-"+this.computedNextMonth(true)+"-"+k)
            temp[line].push(Object.assign(
              { day: k, disabled: true, year, month },
              this.getLunarInfo(this.computedNextYear(), this.computedNextMonth(true), k),
              this.getEvents(this.computedNextYear(), this.computedNextMonth(true), k)
            ))
            k++
          }
          // 下个月除了补充的前几天开始的日期
          nextMonthPushDays = k
        }
      } // end for

      // console.log(this.year+"/"+this.month+"/"+this.day+":"+line)
      // 补充第六行让视觉稳定
      if (line <= 5 && nextMonthPushDays > 0) {
        // console.log({nextMonthPushDays:nextMonthPushDays,line:line})
        let year = y
        let month = m + 2
        if (m === 11) {
          year = y + 1
          month = 1
        }
        for (let i = line + 1; i <= 5; i++) {
          temp[i] = []
          const start = nextMonthPushDays + (i - line - 1) * 7
          for (let d = start; d <= start + 6; d++) {
            temp[i].push(Object.assign(
              { day: d, disabled: true, year, month },
              this.getLunarInfo(this.computedNextYear(), this.computedNextMonth(true), d),
              this.getEvents(this.computedNextYear(), this.computedNextMonth(true), d)
            ))
          }
        }
      }

      // 返回渲染时间段
      const firstDay = temp[0][0]
      const lastLine = temp[temp.length - 1]
      const lastDay = lastLine[lastLine.length - 1]
      this.$emit('render', firstDay, lastDay)
      this.days = temp
    },

    /**
     * 展示 上月
     */
    prev(e) {
      e && e.stopPropagation()
      if (this.month == 0) {
        this.month = 11
        this.year = parseInt(this.year) - 1
      } else {
        this.month = parseInt(this.month) - 1
      }
      this.render(this.year, this.month)
      this.$emit('selectMonth', this.month + 1, this.year)
      this.$emit('prev', this.month + 1, this.year)
    },

    /**
     * 展示 下月
     */
    next(e) {
      e && e.stopPropagation()
      if (this.month == 11) {
        this.month = 0
        this.year = parseInt(this.year) + 1
      } else {
        this.month = parseInt(this.month) + 1
      }
      this.render(this.year, this.month)
      this.$emit('selectMonth', this.month + 1, this.year)
      this.$emit('next', this.month + 1, this.year)
    },

    /**
     * 通过数组内位置 选中日期
     */
    select(k1, k2, e) {
      if (e != undefined) e.stopPropagation()
      // 日期范围
      if (this.range) {
        if (this.rangeBegin.length == 0 || this.rangeEndTemp != 0) {
          this.rangeBegin = [this.year, this.month, this.days[k1][k2].day]
          this.rangeBeginTemp = this.rangeBegin
          this.rangeEnd = [this.year, this.month, this.days[k1][k2].day]
          this.rangeEndTemp = 0
        } else {
          this.rangeEnd = [this.year, this.month, this.days[k1][k2].day]
          this.rangeEndTemp = 1
          // 判断结束日期小于开始日期则自动颠倒过来
          if (+new Date(this.rangeEnd[0], this.rangeEnd[1], this.rangeEnd[2]) < +new Date(this.rangeBegin[0], this.rangeBegin[1], this.rangeBegin[2])) {
            this.rangeBegin = this.rangeEnd
            this.rangeEnd = this.rangeBeginTemp
          }
          // 小于10左边打补丁
          let begin = []
          let end = []
          if (this.zero) {
            this.rangeBegin.forEach((v, k) => {
              if (k == 1)v = v + 1
              begin.push(this.zeroPad(v))
            })
            this.rangeEnd.forEach((v, k) => {
              if (k == 1)v = v + 1
              end.push(this.zeroPad(v))
            })
          } else {
            begin = this.rangeBegin
            end = this.rangeEnd
          }
          // console.log("选中日期",begin,end)
          this.$emit('select', begin, end)
        }
        this.render(this.year, this.month)
      } else if (this.multi) {
        // 如果已经选过则过滤掉
        const filterDay = this.multiDays.filter(v => {
          return this.year === v[0] && this.month === v[1] - 1 && this.days[k1][k2].day === v[2]
        })
        if (filterDay.length > 0) {
          this.multiDays = this.multiDays.filter(v => {
            return this.year !== v[0] || this.month !== v[1] - 1 || this.days[k1][k2].day !== v[2]
          })
        } else {
          this.multiDays.push([this.year, this.month + 1, this.days[k1][k2].day])
        }
        this.days[k1][k2].selected = !this.days[k1][k2].selected
        this.$emit('select', this.multiDays)
      } else {
        // 取消上次选中
        if (this.today.length > 0) {
          this.days.forEach(v => {
            v.forEach(vv => {
              vv.selected = false
            })
          })
        }
        // 设置当前选中天
        const datyItem = this.days[k1][k2]
        datyItem.selected = true
        this.day = this.days[k1][k2].day
        this.today = [k1, k2]
        this.$emit('select', this.year, this.zero ? this.zeroPad(datyItem.month) : datyItem.month, this.zero ? this.zeroPad(datyItem.day) : datyItem.day)
        if (datyItem.disabled) {
          if (k1 < 2) {
            const prevMonth = this.computedPrevMonth(true)
            const year = prevMonth == 12 ? this.year - 1 : this.year
            this.selectDay(year, prevMonth, datyItem.day)
          } else {
            const nextMonth = this.computedNextMonth(true)
            const year = nextMonth == 1 ? this.year + 1 : this.year
            this.selectDay(year, nextMonth, datyItem.day)
          }
        }
      }
    },

    /**
     * 展示年选择布局
     */
    changeYear() {
      if (this.yearsShow) {
        this.yearsShow = false
        return false
      }
      this.yearsShow = true
      this.years = []
      for (let i = ~~this.year - 10; i < ~~this.year + 10; i++) {
        this.years.push(i)
      }
    },

    /**
     * 选择年
     */
    selectYear(value) {
      this.yearsShow = false
      this.year = value
      this.render(this.year, this.month)
      this.$emit('selectYear', value)
    },

    /**
     * 返回今天
     */
    setToday() {
      const now = new Date()
      this.year = now.getFullYear()
      this.month = now.getMonth()
      this.day = now.getDate()
      this.render(this.year, this.month)
      // 遍历当前日找到选中
      this.days.forEach(v => {
        const day = v.find(vv => {
          return vv.day == this.day && !vv.disabled
        })
        if (day != undefined) {
          day.selected = true
        }
      })
    },

    /**
     * 获取自定义事件
     */
    getEvents(y, m, d) {
      if (this.events && this.events.length == 0) return false
      const dayName = `${y}-${this.zero ? this.zeroPad(m) : m}-${this.zero ? this.zeroPad(d) : d}`
      const data = {}
      data.hasEvent = this.events.includes(dayName)
      return data
    },

    /**
     * 获取农历信息
     */
    getLunarInfo(y, m, d) {
      const lunarInfo = calendar.solar2lunar(y, m, d)
      let lunarValue = lunarInfo.IDayCn
      // console.log(lunarInfo)
      let isLunarFestival = false
      let isGregorianFestival = false
      if (this.festival.lunar[lunarInfo.lMonth + '-' + lunarInfo.lDay] != undefined) {
        lunarValue = this.festival.lunar[lunarInfo.lMonth + '-' + lunarInfo.lDay]
        isLunarFestival = true
      } else if (this.festival.gregorian[m + '-' + d] != undefined) {
        lunarValue = this.festival.gregorian[m + '-' + d]
        isGregorianFestival = true
      }

      return {
        lunar: lunarValue,
        isLunarFestival: isLunarFestival,
        isGregorianFestival: isGregorianFestival
      }
    },

    /**
     * 计算年月
     */
    computedPrevYear() {
      let value = this.year
      if (this.month - 1 < 0) {
        value--
      }
      return value
    },
    computedPrevMonth(isString) {
      let value = this.month
      if (this.month - 1 < 0) {
        value = 11
      } else {
        value--
      }
      // 用于显示目的（一般月份是从0开始的）
      if (isString) {
        return value + 1
      }
      return value
    },
    computedNextYear() {
      let value = this.year
      if (this.month + 1 > 11) {
        value++
      }
      return value
    },
    computedNextMonth(isString) {
      let value = this.month
      if (this.month + 1 > 11) {
        value = 0
      } else {
        value++
      }
      // 用于显示目的（一般月份是从0开始的）
      if (isString) {
        return value + 1
      }
      return value
    },

    /**
     * 日期补零
     */
    zeroPad(n) {
      return String(n < 10 ? '0' + n : n)
    }
  }
}

</script>
<style lang="scss" scoped>
.calendar {
  position: relative;
  width: 100%;
  min-width: 300px;
  margin: auto;
  user-select: none;
  background: #fff;
}

.calendar-tools {
  height: 40px;
  font-size: 20px;
  line-height: 40px;

  i {
    cursor: pointer;
  }

  i:hover {
    color: #2362fb;
  }
}

// .calendar-prev{
//     width: 14.28571429%;
//     float:left;
//     text-align: center;
// }
.calendar-info {
  font-size: 16px;
  line-height: 1.3;
  text-align: center;

  .time {
    margin: 0 10px;
    font-weight: bold;
    cursor: pointer;
  }
}

// .calendar-info>div.month{
//     margin:auto;
//     height:20px;
//     width:100px;
//     text-align: center;
//     color:#2362FB;
//     overflow: hidden;
//     position: relative;
// }
// .calendar-info>div.month .month-inner{
//     position: absolute;
//     left:0;
//     top:0;
//     height:240px;
//     transition:top .5s cubic-bezier(0.075, 0.82, 0.165, 1);
// }
// .calendar-info>div.month .month-inner>span{
//     display: block;
//     font-size: 14px;
//     height:20px;
//     width:100px;
//     overflow: hidden;
//     text-align: center;
// }
// .calendar-info>div.year{
//    font-size:10px;
//    line-height: 1;
//    color:$--color-text-secondary;
// }
// .calendar-next{
//     width: 14.28571429%;
//     float:right;
//     text-align: center;
// }

.calendar table {
  width: 100%;
  margin-bottom: 10px;
  clear: both;
  color: #444;
  border-collapse: collapse;
}

.calendar td {
  position: relative;
  width: 14.2857%;
  height: 50px;
  padding-top: 8px;
  margin: 2px !important;
  font-size: 14px;
  line-height: 125%;
  text-align: center;
  vertical-align: middle;
  vertical-align: top;
  cursor: pointer;
}

.calendar td.week {
  font-size: 10px;
  pointer-events: none !important;
  cursor: default !important;
}

.calendar td.disabled {
  color: $--color-text-secondary;

  // pointer-events:none !important;
  // cursor: default !important;
}

.calendar td.today {
  color: $--color-primary;
}

.calendar td.disabled div {
  color: $--color-text-secondary;
}

.calendar td span {
  display: block;
  width: 24px;
  height: 24px;
  margin: 0 auto;
  font-size: 15px;
  line-height: 24px;
  border-radius: 12px;
}

.calendar td:not(.selected) span:not(.red):hover {
  color: #fff;
  background: #2362fb;
}

.calendar td:not(.selected) span.red:hover {
  background: #f9efef;
}

/* .calendar td:not(.disabled) span.red{
    color:#ea6151;
} */
.calendar td.selected span {
  color: #fff;
  background-color: #2362fb;
}

.calendar td .text {
  position: absolute;
  top: 30px;
  right: 0;
  left: 0;
  padding: 2px;
  font-size: 8px;
  line-height: 1.2;
  color: $--color-text-secondary;
  text-align: center;
}

.calendar td .isGregorianFestival,
.calendar td .isLunarFestival {
  color: $--color-text-secondary;
}

/* .calendar td.selected span.red{
    background-color: #ea6151;
    color: #fff;
}
.calendar td.selected span.red:hover{
    background-color: #ea6151;
    color: #fff;
} */
.calendar thead td {
  height: 30px;
  text-transform: uppercase;
  vertical-align: middle;
}

.calendar-button {
  text-align: center;
}

.calendar-button span {
  display: inline-block;
  min-width: 5em;
  min-height: 1em;
  padding: 0.6em 2em;
  margin: 0 0.25em 0 0;
  font-size: 1em;
  line-height: 1em;
  color: #fff;
  text-align: center;
  vertical-align: baseline;
  cursor: pointer;
  background: #2362fb;
  border-radius: 0.3em;
}

.calendar .circle {
  position: relative;
}

.calendar .circle::after {
  position: absolute;
  top: 14px;
  right: 13px;
  display: block;
  width: 5px;
  height: 5px;
  content: "";
  background: #2362fb;
  border: 1px solid white;
  border-radius: 50%;
}

.calendar-button span.cancel {
  color: $--color-text-regular;
  background: #efefef;
}

.calendar-years {
  position: absolute;
  top: 40px;
  right: 0;
  bottom: 0;
  left: 0;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  overflow: auto;
  pointer-events: none;
  background: #fff;
  opacity: 0;
  transition: all 0.5s cubic-bezier(0.075, 0.82, 0.165, 1);
  transform: translateY(-10px);
}

.calendar-years.show {
  pointer-events: auto;
  opacity: 1;
  transform: translateY(0);
}

.calendar-years > span {
  display: inline-block;
  width: 60px;
  margin: 1px 5px;
  line-height: 30px;
  color: $--color-text-secondary;
  text-align: center;
  cursor: pointer;
  border: 1px solid #fbfbfb;
  border-radius: 20px;
}

.calendar-years > span.active {
  color: #fff;
  background-color: #2362fb;
  border: 1px solid #2362fb;
}
</style>
