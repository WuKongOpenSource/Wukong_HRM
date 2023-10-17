<template>
  <div class="el-input__inner range-time-picker">
    <i class="el-input__icon el-icon-time" />
    <el-select
      v-model="hourNum"
      :disabled="disabled"
      placeholder="请选择"
      @change="hourChange">
      <el-option
        v-for="item in hourOptions"
        :key="item.value"
        :label="item.label"
        :value="item.value" />
    </el-select>
    <span class="division">:</span>
    <el-select
      v-model="minuteNum"
      placeholder="请选择"
      :disabled="disabled"
      @change="minuteChange">
      <el-option
        v-for="item in minuteOptions"
        :key="item.value"
        :label="item.label"
        :value="item.value" />
    </el-select>
  </div>
</template>

<script>
export default {
  // RangeTimePicker
  name: 'RangeTimePicker',

  components: {},

  props: {
    value: String,
    selectableRange: {
      type: Array,
      default: () => {
        return ['00:00', '23:59']
      }
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },

  data() {
    return {
      hourNum: 0,
      hourOptions: [],
      minuteNum: 0,
      minuteOptions: []
    }
  },

  computed: {
    rangeConfig() {
      const minS = (this.selectableRange[0] || '00:00').split(':')
      const maxS = (this.selectableRange[1] || '23:59').split(':')
      return {
        minHour: isNaN(parseInt(minS[0])) ? 0 : parseInt(minS[0]),
        maxHour: isNaN(parseInt(maxS[0])) ? 0 : parseInt(maxS[0]),
        minMinute: isNaN(parseInt(minS[1])) ? 0 : parseInt(minS[1]),
        maxMinute: isNaN(parseInt(maxS[1])) ? 0 : parseInt(maxS[1])
      }
    },

    // 目前值
    currentValue() {
      return `${this.fillZero(this.hourNum)}:${this.fillZero(this.minuteNum)}`
    }
  },

  watch: {
    selectableRange: {
      handler() {
        // 根据区间配置创建十分选项，在下一次更新区间配置时，会校准当前值
        this.initHourOptions()
        // 验证值，如果不在区间内，进行校准
        if (!this.verifyValue(this.hourNum, this.minuteNum)) {
          // 当前值不符合标准，覆盖为最小区间值
          this.hourNum = this.rangeConfig.minHour
          this.minuteNum = this.rangeConfig.minMinute

          // 更新 value
          this.updateValue()
        } else {
          // if 小时改变会刷新 分区间，这里保证每次range改变。分区间数据刷新
          this.initMinuteOptions()
        }
      },
      deep: true
    },

    value: {
      handler() {
        this.compareValue()
      },
      immediate: true
    },

    // 时数据改变，刷新分数据区间
    hourNum: {
      handler() {
        this.initMinuteOptions()
      },
      immediate: true
    }
  },

  created() {
    // 根据区间配置创建十分选项，在下一次更新区间配置时，会校准当前值
    this.initHourOptions()
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * @description: 比较值 如果符合要求 value 值填充 当前值
     * @param {*}
     * @return {*} Boolean 通过验证或未通过
     */
    compareValue() {
      if (this.value) {
        const times = this.value.split(':')
        const hour = parseInt(times[0])
        const minute = parseInt(times[1])

        // 验证传入值是否在区间内，如果不在用区间值重置
        if (this.verifyValue(hour, minute)) {
          // 通过验证的值，在比较是否不同
          if (this.currentValue !== this.value) {
            this.hourNum = hour
            this.minuteNum = minute
          }
        } else {
          // 如果传入值不符合，验证当前值后，更新传入值
          if (!this.verifyValue(this.hourNum, this.minuteNum)) {
            // 如果当前值也不符合，重置为区间最小值，并更新value
            this.hourNum = this.rangeConfig.minHour
            this.minuteNum = this.rangeConfig.minMinute
          }
          // 更新 value
          this.updateValue()
        }
      }
    },

    /**
     * @description: 验证值
     * @param {*} hour
     * @param {*} minute
     * @return {*}
     */
    verifyValue(hour, minute) {
      const value = parseInt(`${this.fillZero(hour + 24)}${this.fillZero(minute)}`)
      const min = parseInt(`${this.fillZero(this.rangeConfig.minHour + 24)}${this.fillZero(this.rangeConfig.minMinute)}`)
      const max = parseInt(`${this.fillZero(this.rangeConfig.maxHour + 24)}${this.fillZero(this.rangeConfig.maxMinute)}`)
      return value >= min && value <= max
    },

    /** ****************事件部分*********************/
    /**
     * @description: 时事件
     * @param {*}
     * @return {*}
     */
    hourChange() {
      // 刷新分数据 统一 在watch 里面刷新了

      // 验证分数据是否再区间内
      if (this.hourNum === this.rangeConfig.minHour) {
        const isSameHour = this.rangeConfig.minHour === this.rangeConfig.maxHour
        const endMinute = isSameHour ? this.rangeConfig.maxMinute : 59

        // 如果分不在可选范围，进行校准
        if (this.minuteNum < this.rangeConfig.minMinute || this.minuteNum > endMinute) {
          this.minuteNum = this.rangeConfig.minMinute
        }
      } else if (this.hourNum === this.rangeConfig.maxHour) {
        // 如果分不在可选范围，进行校准
        if (this.minuteNum < 0 || this.minuteNum > this.rangeConfig.maxMinute) {
          this.minuteNum = 0
        }
      }

      this.updateValue()
      this.$emit('change', this.currentValue)
    },

    /**
     * @description: 分事件
     * @param {*}
     * @return {*}
     */
    minuteChange() {
      this.updateValue()
      this.$emit('change', this.currentValue)
    },

    /**
     * @description: 更新value
     * @param {*}
     * @return {*}
     */
    updateValue() {
      this.$emit('input', this.currentValue)
    },

    /** ****************数据部分*********************/
    /**
       * @description: 时区间可选数据
       * @param {*}
       * @return {*}
       */
    initHourOptions() {
      const hourOptions = []
      for (let index = this.rangeConfig.minHour; index <= this.rangeConfig.maxHour; index++) {
        hourOptions.push(this.getNumItem(index))
      }
      this.hourOptions = hourOptions
    },

    /**
     * @description: 分区间可选数据 根据 时值的改变刷新
     * @param {*}
     * @return {*}
     */
    initMinuteOptions() {
      const minuteOptions = []
      // 最小时 和 最大 时，详细控制分数据
      if (this.hourNum === this.rangeConfig.minHour) {
        const isSameHour = this.rangeConfig.minHour === this.rangeConfig.maxHour
        const endMinute = isSameHour ? this.rangeConfig.maxMinute : 59
        for (let index = this.rangeConfig.minMinute; index <= endMinute; index++) {
          minuteOptions.push(this.getNumItem(index, false))
        }
      } else if (this.hourNum === this.rangeConfig.maxHour) {
        for (let index = 0; index <= this.rangeConfig.maxMinute; index++) {
          minuteOptions.push(this.getNumItem(index, false))
        }
      } else {
        // 其他情况可看到0~59
        for (let index = 0; index <= 59; index++) {
          minuteOptions.push(this.getNumItem(index, false))
        }
      }
      this.minuteOptions = minuteOptions
    },

    /**
     * @description: 获取数量项
     * @param {*} isHour 大于等于24 增加次日
     * @return {*}
     */
    getNumItem(index, isHour = true) {
      if (!isHour) {
        if (index < 10) {
          return {
            label: `0${index}`,
            value: index
          }
        } else {
          return {
            label: `${index}`,
            value: index
          }
        }
      } else {
        let label
        const value = index
        let data = index
        if (index < 0) {
          data = 24 + index
          if (data < 10) {
            label = `昨日0${data}`
          } else {
            label = `昨日${data}`
          }
        } else {
          if (index < 10) {
            label = `0${data}`
          } else if (index > 9 && index <= 23) {
            label = `${data}`
          } else if (index > 23) {
            const showIndex = (index - 24)
            label = showIndex < 10 ? `次日0${showIndex}` : `次日${showIndex}`
          }
        }
        // 大于 23 的叫次日，但index值按照实际展示

        return {
          label,
          value
        }
      }
    },

    /**
     * @description: 补充0
     * @param {*}
     * @return {*}
     */
    fillZero(index) {
      // return index < 10 ? `0${index}` : index
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
    }

  }
}
</script>

<style lang="scss" scoped>
.range-time-picker {
  display: flex;
  padding-left: 0;
  line-height: 30px;

  .division {
    flex-shrink: 0;
    margin: 0 8px;
  }

  .el-input__icon {
    flex-shrink: 0;
    margin-right: 4px;
    line-height: 30px;
    color: $--color-n100;
  }

  ::v-deep .el-select {
    flex: 1;

    .el-input__inner {
      height: 28px;
      padding: 0;
      line-height: 28px;
      text-align: center;
      background-color: transparent;
      border: none;
    }

    .el-input__suffix {
      display: none;
    }
  }
}
</style>
