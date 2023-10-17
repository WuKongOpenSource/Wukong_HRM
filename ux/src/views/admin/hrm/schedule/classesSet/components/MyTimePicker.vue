<template>
  <div
    class="myTimePicker"
    :class="{disabled:disabled }">
    <el-time-picker
      v-model="value[0]"
      :show-trigger="false"
      value-format="HH:mm"
      format="HH:mm"
      placeholder="开始时间"
      :disabled="disabled"
      :picker-options="{
        selectableRange:selectableMinRange
      }"
      @input.native="pickerInput"
    />
    <span class="el-range-separator">至</span>
    <el-time-picker
      v-model="value[1]"
      :show-trigger="false"
      value-format="HH:mm"
      format="HH:mm"
      placeholder="结束时间"
      :disabled="disabled"
      :picker-options="{
        selectableRange:selectableMaxRange
      }"
      @input.native="pickerInput"
    />
  </div>
</template>

<script>
import { isEmpty } from '@/utils/types'

export default {
  model: {
    prop: 'value',
    event: 'change'
  },
  props: {
    value: {
      type: Array,
      default: () => ''
    },
    disabled: Boolean,
    minTime: String,
    maxTime: String
  },
  computed: {
    selectableMinRange() {
      const minTime = this.$moment(this.minTime, 'HH:mm').add(+1, 'm').format('HH:mm')
      const maxTime = this.$moment(this.maxTime, 'HH:mm').add(-1, 'm').format('HH:mm')

      // 是否有最大时间和最小时间
      if (this.value[1]) {
        // 是否选择了另一个值
        if (minTime < maxTime) {
          // 判断时间是否为次日，是的话valuw[1]必定小于maxTime
          return `${minTime}:00 - ${this.value[1]}:00`
        } else {
          if (this.value[1] < this.maxTime) {
            // 判断选择的另一个值是否为次日
            return [`${minTime}:00 - 23:59:00`, `00:00:00 - ${this.value[1]}:00`]
          } else {
            return `${minTime}:00 - ${this.value[1]}:00`
          }
        }
      } else {
        if (minTime < maxTime) {
          // 判断时间是否为次日
          return `${minTime}:00 - ${maxTime}:00`
        } else {
          return [`${minTime}:00 - 23:59:00`, `00:00:00 - ${maxTime}:00`]
        }
      }
    },
    selectableMaxRange() {
      const minTime = this.$moment(this.minTime, 'HH:mm').add(+1, 'm').format('HH:mm')
      const maxTime = this.$moment(this.maxTime, 'HH:mm').add(-1, 'm').format('HH:mm')

      if (this.value[0]) {
        // 是否选择了另一个值
        if (minTime < maxTime) {
          // 判断时间是否为次日，不是的话value[0]必定大于minTime
          return `${this.value[0]}:00 - ${maxTime}:00`
        } else {
          if (this.value[0] > this.minTime) {
            // 判断另一个值是否为次日
            return [`${this.value[0]}:00 - 23:59:00`, `00:00:00 - ${maxTime}:00`]
          } else {
            return `${this.value[0]}:00 - ${maxTime}:00`
          }
        }
      } else {
        if (minTime < maxTime) {
          // 判断时间是否为次日
          return `${minTime}:00 - ${maxTime}:00`
        } else {
          return [`${minTime}:00 - 23:59:00`, `00:00:00 - ${maxTime}:00`]
        }
      }
    }
  },
  methods: {
    pickerInput(val) {
      this.$emit('change', val)
    }
  }
}
</script>

<style lang="scss" scoped>
.myTimePicker {
  padding-right: 10px;
  width: 250px;
  height: 32px;
  display: inline-block;
  line-height:30px;
  border: 2px solid $--color-n40;
  border-radius: 3px;
  background: $--color-n10;
  position:relative;
  display:inline-flex;
  flex: 1;
}
.myTimePicker:hover {
  background: $--color-n30;
  ::v-deep .el-input__inner {
    background: $--color-n30;
  }
}
.disabled {
  background: $--background-color-base;
}
::v-deep .el-input__inner{
  border:none;
  padding-right: 0;
  padding-left: 30px;
  height: 20px;
}
::v-deep .el-input__inner:hover {
  background: none;
}
::v-deep .el-date-editor--time{
  width: 40%;
  height: 20px;
}
.el-range-separator{
  text-align: center;
}
</style>
