<template>
  <div
    ref="reference"
    class="el-date-editor el-range-editor el-input__inner"
  >
    <el-time-picker
      v-model="timeArr[0]"
      :picker-options="startPickerOptions"
      :clearable="false"
      value-format="HH:mm"
      format="HH:mm"
    />
    <span class="el-range-separator">-</span>
    <el-time-picker
      v-model="timeArr[1]"
      :picker-options="endPickerOptions"
      :clearable="false"
      value-format="HH:mm"
      format="HH:mm"
    />

  </div>
</template>

<script>
import { isEmpty } from '@/utils/types'

export default {
  props: {
    timeArr: {
      type: Array,
      default: () => []
    },
    pickerOptions: {
      type: Object,
      default: () => {}
    },
    type: String
  },
  data() {
    return {
    }
  },
  computed: {
    endPickerOptions() {
      if (!isEmpty(this.pickerOptions)) {
        const start = this.timeArr[0] ? this.timeArr[0] + ':00' : `00:00:00`
        const end = this.pickerOptions.selectableRange.split('-').pop().trim()
        return { selectableRange: `${start} - ${end}` }
      }
      return ``
    },
    startPickerOptions() {
      if (!isEmpty(this.pickerOptions)) {
        const end = this.timeArr[1] ? this.timeArr[1] + ':00' : this.pickerOptions.selectableRange.split('-').pop().trim()
        const start = this.type == 'workTime' ? `00:00:00` : this.pickerOptions.selectableRange.split('-')[0].trim()
        return { selectableRange: `${start} - ${end}` }
      }
      return ``
    }
  },
  methods: {
  }
}
</script>

<style lang="scss" scoped>
::v-deep .el-input__inner{
  border:none !important;
  height: 30px;
}
.el-range-separator{
  display: inline-block;
  line-height:34px;
  height: 34px !important;
}
</style>
