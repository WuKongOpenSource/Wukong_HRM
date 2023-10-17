<template>
  <xr-create
    :loading="loading"
    :title="title"
    @close="close"
    @save="saveClick">
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="80px"
      label-position="top">
      <el-form-item
        class="wk-form-item"
        label="班次名称"
        prop="shiftName">
        <el-input v-model="form.shiftName" style="width: 325px;" placeholder="请输入" />
      </el-form-item>
      <el-form-item
        class="wk-form-item"
        label="打卡类型">
        <el-radio-group v-model="form.shiftType">
          <el-radio :label="1" checked>早晚打卡 <span class="input_tips">(午休无需打卡)</span> </el-radio>
          <!-- <el-radio :label="2">分段打卡 <span class="input_tips">(一天多次上下班打卡)</span> </el-radio> -->
        </el-radio-group>
      </el-form-item>
      <div class="time-set-item-wrap">
        <div class="time-set-item">
          <el-row type="flex">
            <el-col :span="10">
              <el-form-item
                label="上班时间"
                prop="workTime"
                class="small-label">
                <range-time-picker
                  v-model="form.workTime"
                  :selectable-range="workTimeRange"
                  @change="workTimeChange($event, 'workTime')"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col :span="18">
              <el-form-item
                class="time-range-el-form-item"
                label="上班打卡时间段"
                prop="workTimeRange">
                <flexbox class="time-range">
                  <range-time-picker
                    v-model="form.workTimeStart"
                    :selectable-range="workTimeStartRange"
                  />
                  <span class="division">至</span>
                  <range-time-picker
                    v-model="form.workTimeEnd"
                    :selectable-range="workTimeEndRange"
                  />
                </flexbox>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col :span="10">
              <el-form-item
                label="下班时间"
                prop="offWorkTime"
                class="small-label">
                <range-time-picker
                  v-model="form.offWorkTime"
                  :selectable-range="offWorkTimeRange"
                  @change="workTimeChange($event, 'offWorkTime')"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col :span="18">
              <el-form-item
                class="time-range-el-form-item time"
                label="下班打卡时间段"
                prop="offWorkRange">
                <flexbox class="time-range">
                  <range-time-picker
                    v-model="form.offWorkTimeStart"
                    :selectable-range="offWorkTimeStartRange"
                  />
                  <span class="division">至</span>
                  <range-time-picker
                    v-model="form.offWorkTimeEnd"
                    :selectable-range="offWorkTimeEndRange"
                  />
                </flexbox>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col :span="14">
              <el-form-item
                label="休息时间"
                class="small-label">
                <flexbox class="time-range">
                  <range-time-picker
                    v-model="form.restStartTime"
                    :selectable-range="restTimeStartRange"
                  />
                  <span class="division">至</span>
                  <range-time-picker
                    v-model="form.restEndTime"
                    :selectable-range="restTimeEndRange"
                  />
                </flexbox>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <el-form-item
                class="small-label restTimeStatusCheckbox">
                <el-checkbox
                  v-model="form.restTimeStatus"
                  :true-label="1"
                  :false-label="0"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <span style="color: #999;">未选中参与工作时长计算，选中不参与工作时长计算</span>
        </div>
      </div>
      <p class="total">合计工作时长： <span style="color: #2362fb;">{{ shiftHoursShow }}</span></p>
    </el-form>
  </xr-create>
</template>

<script>
import { hrmAddOrSetAttendanceShiftAPI, hrmVerifyAttendanceShiftNameAPI } from '@/api/admin/hrm'
import RangeTimePicker from './components/RangeTimePicker'
import XrCreate from '@/components/XrCreate'

import { isEmpty } from '@/utils/types'
import moment from 'moment'

export default {

  components: {
    XrCreate,
    RangeTimePicker
  },
  props: {
    id: {
      type: [String, Number],
      default: ''
    },
    dataValue: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {

      loading: false,

      form: {
        shiftType: 1, // 班次类型
        shiftName: '',
        workTime: '9:00', // 工作时间
        offWorkTime: '18:00',
        workTimeStart: '05:00',
        workTimeEnd: '17:59',
        offWorkTimeStart: '09:01',
        offWorkTimeEnd: '28:59', // 大于23是次日
        restStartTime: '12:00',
        restEndTime: '18:00',
        restTimeStatus: 0
      },
      rules: {
        // 验证班次信息
        shiftName: [
          { required: true, message: '请输入班次名称', trigger: 'blur' },
          { validator: (rule, value, callback) => {
            if (isEmpty(value)) {
              callback()
            } else {
              this.verifyAttendanceShiftName({
                value
              }).then(() => {
                callback()
              }).catch(msg => {
                callback(new Error('班次名称验证出错'))
              })
            }
          }, trigger: ['blur'] }
        ]
      }
    }
  },
  computed: {
    title() {
      return this.id ? '编辑班次' : '新建班次'
    },

    // 工作时间可选区间  最大时间不能大于24小时
    workTimeRange() {
      const endTime = this.timeAddSubtractMinute(this.form.offWorkTime, 'subtract')
      const endTimeNum = parseFloat(endTime.replace(/:/g, ''))
      // 开始时间不能选择大于24点的值
      return ['00:00', endTimeNum >= 2400 ? '23:59' : endTime]
    },

    // 工作开始可选区间 上班时间-8小时+1分钟 ~ 上班时间减1
    workTimeStartRange() {
      const workTime = this.timeAddSubtractMinute(this.form.workTime, 'add')
      const start = this.timeAddSubtractHour(workTime, 'subtract', 8)
      return [start, this.timeAddSubtractMinute(this.form.workTime, 'subtract')]
    },

    // 工作开始可选区间 上班时间加1 ~ 下班时间减1
    workTimeEndRange() {
      return [this.timeAddSubtractMinute(this.form.workTime, 'add'), this.timeAddSubtractMinute(this.form.offWorkTime, 'subtract')]
    },

    // 工作结束可选区间 上班时间加1 ~ 下班时间减1
    offWorkTimeStartRange() {
      return [this.timeAddSubtractMinute(this.form.workTime, 'add'), this.timeAddSubtractMinute(this.form.offWorkTime, 'subtract')]
    },

    // 工作结束可选区间 下班班时间加1 ~ 上班开始时间-1（本身是次日）
    offWorkTimeEndRange() {
      const end = this.timeAddSubtractMinute(this.form.workTimeStart, 'subtract')
      const workTimeEnd = this.processingTime(end)
      workTimeEnd[0] += 24
      return [this.timeAddSubtractMinute(this.form.offWorkTime, 'add'), workTimeEnd.join(':')]
    },

    // 下班时间可选区间 上班时间加1
    offWorkTimeRange() {
      return [this.timeAddSubtractMinute(this.form.workTime, 'add', 2), this.getOffWorkEndTime()]
    },

    // 休息时间区间开始
    restTimeStartRange() {
      const restEndTime = this.$moment(`2022/3/16 ${this.form.restEndTime}`).subtract(1, 'm').format('HH:mm')
      return [this.form.workTime, restEndTime]
    },

    // 休息时间区间结束
    restTimeEndRange() {
      const date = `2022/3/16 ${this.form.restStartTime}`
      const startTime = this.$moment(date).add(1, 'm').format('HH:mm')
      return [startTime, this.form.offWorkTime]
    },

    // 工作时间总长计算
    shiftHours() {
      let totalMinute = 0
      let restMinute = 0
      // const hour = 0
      if (!this.form.restTimeStatus) {
        restMinute = 0
      } else if (this.form.shiftType == 2) {
        restMinute = 0
      } else if (!this.form.restStartTime || !this.form.restEndTime) {
        restMinute = 0
      } else {
        const flag = this.processingTime(this.form.restEndTime)[0] > 23
        const restStartTime = this.form.restStartTime
        const restEndTime = this.form.restEndTime
        if (flag) {
          restMinute += this.difference(restStartTime, '23:59')
          restMinute += this.difference('00:00', restEndTime)
        } else {
          restMinute = this.difference(restStartTime, restEndTime)
        }
      }
      const flag = this.processingTime(this.form.offWorkTime)[0] > 23
      const startTime = this.form.workTime
      const endTime = this.form.offWorkTime
      if (flag) {
        // 计算夜班的工作时间
        totalMinute += this.difference(startTime, '23:59')
        totalMinute += this.difference('00:00', endTime)
      } else {
        totalMinute += this.difference(startTime, endTime)
      }

      totalMinute = Math.abs(totalMinute - restMinute)
      // if (totalMinute % 60 > 15 && totalMinute % 60 < 46) {
      //   hour = Math.floor(totalMinute / 60) + 0.5
      // } else if (totalMinute % 60 > 45) {
      //   hour = Math.floor(totalMinute / 60) + 1
      // } else if (totalMinute % 60 <= 15) {
      //   hour = Math.floor(totalMinute / 60)
      // }
      return totalMinute
      // const minute = totalMinute % 60 > 9 ? totalMinute % 60 : '0' + totalMinute % 60
      // return Math.floor(totalMinute / 60) + '小时' + minute + '分钟'
    },
    shiftHoursShow() {
      const totalMinute = this.shiftHours
      const minute = totalMinute % 60 > 9 ? totalMinute % 60 : '0' + totalMinute % 60
      return Math.floor(totalMinute / 60) + '小时' + minute + '分钟'
    }
  },
  watch: {
  },
  created() {
    this.init()
  },
  methods: {
    /**
     * @description: 初始化数据
     * @param {*}
     * @return {*}
     */
    init() {
      if (this.id) {
        const {
          shiftName,
          shiftType,
          start1,
          end1,
          restEndTime,
          restStartTime,
          lateCard1, // 上班最晚
          advanceCard1, // 上班最早
          postponeCard1, // 下班最晚
          earlyCard1, // 下班最早
          restTimeStatus
        } = this.dataValue
        this.form.shiftName = shiftName
        this.form.shiftType = shiftType
        this.form.workTime = start1
        this.form.offWorkTime = end1

        this.form.workTimeStart = advanceCard1
        this.form.workTimeEnd = lateCard1

        this.form.offWorkTimeStart = earlyCard1
        this.form.offWorkTimeEnd = postponeCard1
        this.form.restTimeStatus = restTimeStatus

        this.form.restStartTime = restStartTime
        this.form.restEndTime = restEndTime
      }
    },

    /**
     * @description: 验证班次名称
     * @param {*} value
     * @return {*}
     */
    verifyAttendanceShiftName({ value }) {
      return hrmVerifyAttendanceShiftNameAPI({
        'shiftId': this.id,
        'shiftName': value
      })
    },

    /**
     * @description: 关闭
     * @param {*}
     * @return {*}
     */
    close() {
      this.$emit('close')
    },

    /**
     * @description: 保存
     * @param {*}
     * @return {*}
     */
    saveClick() {
      this.loading = true
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          const params = this.getParams()
          hrmAddOrSetAttendanceShiftAPI(params).then(res => {
            this.loading = false
            const message = this.id ? '班次次日生效,且关联班次的考勤组次日生效' : '班次管理列表刷新数据'
            this.$message.success(message)
            this.close()
            this.$emit('save-success')
          }).catch(() => { this.loading = false })
        } else {
          this.loading = false
        }
      })
    },

    /**
     * @description: 获取参数
     * @param {*}
     * @return {*}
     */
    getParams() {
      const obj = {
        shiftType: this.form.shiftType, // 1早晚打卡 2 分段打卡
        shiftName: this.form.shiftName,
        shiftHours: this.shiftHours,
        restStartTime: this.form.restStartTime,
        restEndTime: this.form.restEndTime
      }

      if (this.id) {
        obj['shiftId'] = this.id
      }
      obj.start1 = this.form.workTime
      obj.end1 = this.form.offWorkTime
      obj.advanceCard1 = this.form.workTimeStart
      obj.lateCard1 = this.form.workTimeEnd
      obj.earlyCard1 = this.form.offWorkTimeStart
      obj.postponeCard1 = this.form.offWorkTimeEnd
      obj.restTimeStatus = this.form.restTimeStatus
      return obj
    },

    /**
     * 计算时间差
     */
    difference(startTime, endTime) {
      if (!startTime || !endTime) return 0
      const startArr = this.processingTime(startTime)
      const endArr = this.processingTime(endTime)
      if (startArr[0] > 23) {
        startArr[0] = startArr[0] - 24
        startTime = startArr.join(':')
      }
      if (endArr[0] > 23) {
        endArr[0] = endArr[0] - 24
        endTime = endArr.join(':')
      }
      const start = this.$moment(startTime, 'HH:mm')
      const end = this.$moment(endTime, 'HH:mm')
      return start.diff(end, 'minute')
    },

    /**
     * @description: 时间加减分
     * @param {*} time 时间
     * @param {*} type add subtract
     * @param {*} minute 默认加减 1 不大于 59
     * @return {*}
     */
    timeAddSubtractMinute(time, type, value = 1) {
      const times = time.split(':')
      let hour = parseInt(times[0])
      let minute = parseInt(times[1])
      if (type === 'add') {
        minute += value
        if (minute > 59) {
          hour++
          minute = minute - 60
        }
      } else if (type === 'subtract') {
        minute -= value
        if (minute < 0) {
          hour--
          minute = minute + 60
        }
      }

      return `${this.fillZero(hour)}:${this.fillZero(minute)}`
    },
    /**
     * @description: 时间加减小时
     * @param {*} time 时间
     * @param {*} type add subtract
     * @param {*} minute 默认加减 1
     * @return {*}
     */
    timeAddSubtractHour(time, type, value = 1) {
      const times = time.split(':')
      let hour = parseInt(times[0])
      const minute = parseInt(times[1])
      if (type === 'add') {
        hour += value
      } else if (type === 'subtract') {
        hour -= value
      }

      return `${this.fillZero(hour)}:${this.fillZero(minute)}`
    },
    /**
     * @description: 补充0
     * @param {*}
     * @return {*}
     */
    fillZero(index) {
      let item
      if (index < 0) {
        const val = Math.abs(index)
        if (index < 10) {
          item = `-0${val}`
        } else {
          item = `-${val}`
        }
      } else {
        if (index < 10) {
          item = `0${index}`
        } else {
          item = index
        }
      }
      return item
    },

    /**
     * @description: 获取下班可打卡时间
     * @param {*}
     * @return {*}
     */
    getOffWorkTimeEnd() {
      if (this.form.workTime) {
        // const workValue = parseInt .replace(/:/g, '-')
        const times = this.form.workTime.split(':')
        const hour = parseInt(times[0])
        const minute = parseInt(times[1])
        // 大于0 说明下班可打卡结束时间 到次日
        let endHour = hour + 24
        let endMinute = minute
        if (hour > 0 || minute > 0) {
          // 如果截止时间大于24 小时数需用48小时 减
          if (minute === 0) {
            endHour = endHour - 1
            endMinute = 59
          } else {
            endMinute = endMinute - 1
          }
          return `${this.fillZero(endHour)}:${this.fillZero(endMinute)}`
        } else {
          return `23:59` // 00:00 减 1  次日23:59
        }
      }
      return '23:59'
    },

    /**
     * @description: 获取选择下班最晚时间
     * @param {*}
     * @return {*}
     */
    getOffWorkEndTime() {
      const times = this.form.workTimeStart.split(':')
      let hour = parseInt(times[0])
      let minute = parseInt(times[1])
      if (minute < 2) {
        hour = hour - 1
        minute = 60 + minute - 2
      } else {
        minute = minute - 2
      }
      return `${this.fillZero(hour + 24)}:${this.fillZero(minute)}`
    },
    processingTime(time) {
      return time.split(':').map((item, index) => {
        return Number(item)
      })
    },

    workTimeChange(value, type) {
      const { restStartTime, restEndTime, workTime, offWorkTime } = this.form

      const closingTime = moment(value, 'HH:mm') // /上班/下班时间
      const restBeginsTime = moment(restStartTime, 'HH:mm') // 休息开始时间
      const restBreakTime = moment(restEndTime, 'HH:mm') // 休息结束时间
      const result = closingTime.isBefore(restBeginsTime)
      const result1 = closingTime.isBefore(restBreakTime)
      if (type == 'offWorkTime') { // 调整下班时间
        if (result) { // 下班时间小于休息开始时间
          this.$nextTick(() => {
            this.$set(this.form, 'restStartTime', workTime)
            setTimeout(() => {
              this.$set(this.form, 'restEndTime', offWorkTime)
            }, 0)
          })
        }
        if (result1) { // 下班时间小于休息结束时间
          this.$nextTick(() => {
            this.$set(this.form, 'restEndTime', value)
          })
        }
      } else if (type == 'workTime') { // 调整上班时间
        console.log(result, result1)
        if (!result) { // 上班时间大于休息开始时间
          this.$nextTick(() => {
            this.$set(this.form, 'restStartTime', value)
            setTimeout(() => {
              this.$set(this.form, 'restEndTime', offWorkTime)
            }, 0)
          })
        }
      }
    }
  }

}
</script>

<style lang='scss' scoped>
@import "@/styles/wk-form.scss";

::v-deep.restTimeStatusCheckbox .el-checkbox {
  margin-top: 4px;
}

.el-row {
  ::v-deep .el-form-item {
    display: flex;

    .el-form-item__label {
      flex-shrink: 0;
      width: 110px;
      padding: 0;
    }

    .el-checkbox {
      margin-top: unset;
      margin-left: 10px;
    }

    &.small-label {
      align-items: center;

      .el-form-item__label {
        width: 80px;
      }
    }
  }
}

.time-set-item {
  margin-top: 20px;
  border-bottom: 1px solid #ccc;

  .el-row--flex {
    justify-content: space-between;
  }
}

.time-set-item:last-of-type {
  border-bottom: none;
}

.time-set-item-wrap {
  margin-top: -20px;
}

.range-time-picker {
  // width: 250px;
}

.time-range-el-form-item {
  ::v-deep .el-form-item__content {
    flex: 1;
  }
}

.time-range {
  .range-time-picker {
    flex: 1;
  }

  .division {
    margin: 0 8px;
  }
}

.total {
  margin-top: 30px;
}

::v-deep .el-form-item__content {
  height: 40px;
}
</style>
