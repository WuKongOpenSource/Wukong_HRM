<template>
  <el-popover
    v-model="showTypePopover"
    :width="350"
    placement="bottom-start"
    class="wk-time-type-select"
    popper-class="no-padding-popover"
    trigger="click">
    <div class="type-popper">
      <flexbox class="type-popper-common">
        <flexbox
          direction="column"
          justify="space-around"
          class="type-sort">
          <div
            v-for="(item,index) in sortList"
            :key="index"
            class="type-sort-item">{{ item.name }}</div>
        </flexbox>

        <flexbox
          direction="column"
          justify="space-around"
          class="type-content">
          <flexbox
            v-for="(item,index) in sortList"
            :key="index"
            class="type-content-box"
            wrap="wrap">
            <template v-for="(subItem, subIndex) in options">
              <div
                v-if="item.list.includes(subItem.value)"
                :key="subIndex"
                :class="{ 'selected' : selectItem.value == subItem.value && !showCustomContent}"
                class="type-content-item text-one-line"
                @click="typeSelectClick(subItem)">
                {{ subItem.label }}
              </div>
            </template>
          </flexbox>
        </flexbox>
      </flexbox>
      <div>
        <div
          class="type-content-item custom">
          自定义
        </div>
        <flexbox
          justify="space-between"
          class="type-content-custom">
          <el-date-picker
            v-model="editCustomTimes"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy.MM.dd"
            @change="updateDefaultStatus(0)" />
          <!-- <el-date-picker
            v-model="startTime"
            type="date"
            value-format="yyyy.MM.dd"
            placeholder="选择日期"
            @change="updateDefaultStatus(0)" />
          <el-date-picker
            v-model="endTime"
            type="date"
            value-format="yyyy.MM.dd"
            placeholder="选择日期"
            @change="updateDefaultStatus(0)" /> -->
        </flexbox>
        <div class="confirm">
          <div>
            <div v-show="showDefault">
              <el-checkbox
                v-model="isChecked"
                :true-label="1"
                :false-label="0"
                @change="$emit('update:isDefault',isChecked)"
              >保存为默认值</el-checkbox><i class="wk wk-icon-fill-help wk-help-tips" data-type="4" data-id="6" />
            </div>
          </div>
          <el-button
            type="primary"
            @click="sureClick">确定</el-button>
        </div>
      </div>
    </div>
    <el-input
      slot="reference"
      v-model="typeShowValue"
      :readonly="true"
      :style="{width: width + 'px'}"
      :class="[mode ? 'type-select--' + mode : '', { 'is-show': showTypePopover }]"
      class="type-select">
      <i
        slot="suffix"
        :class="['el-icon-arrow-up']" />
    </el-input>
  </el-popover>
</template>

<script type="text/javascript">
import { isObject, isArray } from '@/utils/types'
import moment from 'moment'

export default {
  name: 'WkTimeTypeSelect',
  props: {
    defaultType: [String, Object],
    // 容器宽度，默认200px
    width: {
      type: [String, Number],
      default: 200
    },
    isDefault: {
      type: [String, Number],
      default: 0
    },
    showDefault: {
      type: Boolean,
      default: true
    },
    mode: String, // 选择框展示
    options: {
      type: Array,
      default: () => {
        return [
          { label: '今天', value: 'today' },
          { label: '昨天', value: 'yesterday' },
          { label: '明天', value: 'tomorrow' },
          { label: '本周', value: 'week' },
          { label: '上周', value: 'lastWeek' },
          { label: '下周', value: 'nextWeek' },
          { label: '本月', value: 'month' },
          { label: '上月', value: 'lastMonth' },
          { label: '下月', value: 'nextMonth' },
          { label: '本季度', value: 'quarter' },
          { label: '上一季度', value: 'lastQuarter' },
          { label: '下一季度', value: 'nextQuarter' },
          { label: '本年度', value: 'year' },
          { label: '上一年度', value: 'lastYear' },
          { label: '下一年度', value: 'nextYear' }
        ]
      }
    } // 数据源 如果存在 替换 默认
  },
  data() {
    return {
      selectType: { label: '本月', value: 'month' },
      selectItem: {},
      showTypePopover: false,
      sureCustomContent: false, // 确定
      isChecked: 0, // 是否为默认值
      customTimes: [], // 自定义时间
      editCustomTimes: [],

      sortList: [
        { name: '天', list: ['today', 'yesterday', 'tomorrow'] },
        { name: '周', list: ['week', 'lastWeek', 'nextWeek'] },
        { name: '月', list: ['month', 'lastMonth', 'nextMonth'] },
        { name: '季度', list: ['quarter', 'lastQuarter', 'nextQuarter'] },
        { name: '年度', list: ['year', 'lastYear', 'nextYear'] }
      ]
    }
  },
  computed: {
    typeShowValue() {
      if (this.sureCustomContent) {
        return this.customTimes.join('-')
      } else {
        return this.selectType.label
      }
    },

    // 展示自定义时间内容
    showCustomContent() {
      return isArray(this.editCustomTimes) && this.editCustomTimes.length > 0
    }
  }, // 时间类型选择
  watch: {
    defaultType: {
      handler() {
        // if (this.selectType.value != this.defaultType) {
        // this.selectType = this.getDefaultTypeValue(this.defaultType)
        // this.selectItem = this.getDefaultTypeValue(this.defaultType)
        if (this.defaultType !== undefined) {
          if (typeof this.defaultType === 'string') {
            this.selectType = this.getDefaultTypeValue(this.defaultType)
            this.selectItem = this.getDefaultTypeValue(this.defaultType)
          } else if (isObject(this.defaultType)) {
            if (this.defaultType.label) {
              this.selectType = this.defaultType
              this.selectItem = this.defaultType
            } else if (this.defaultType.type == 'default') {
              this.selectType = this.getDefaultTypeValue(this.defaultType.value)
              this.selectItem = this.getDefaultTypeValue(this.defaultType.value)
            } else if (this.defaultType.type == 'custom') {
              this.sureCustomContent = true
              this.customTimes = [this.defaultType.startTime, this.defaultType.endTime]
              this.editCustomTimes = [this.defaultType.startTime, this.defaultType.endTime]
            }
          }
        }
        // }
      },
      immediate: true

    },
    showTypePopover(val) {
      if (val) {
        this.isChecked = this.isDefault
      }
    }
  },
  mounted() {
    // if (this.defaultType !== undefined) {
    //   if (typeof this.defaultType === 'string') {
    //     this.selectType = this.getDefaultTypeValue(this.defaultType)
    //     this.selectItem = this.getDefaultTypeValue(this.defaultType)
    //   } else if (isObject(this.defaultType)) {
    //     if (this.defaultType.label) {
    //       this.selectType = this.defaultType
    //       this.selectItem = this.defaultType
    //     } else if (this.defaultType.type == 'default') {
    //       this.selectType = this.getDefaultTypeValue(this.defaultType.value)
    //       this.selectItem = this.getDefaultTypeValue(this.defaultType.value)
    //     } else if (this.defaultType.type == 'custom') {
    //       this.sureCustomContent = true
    //       this.showCustomContent = true
    //       this.startTime = this.defaultType.startTime
    //       this.endTime = this.defaultType.endTime
    //     }
    //   }
    // } else {
    //   this.$emit('change', { type: 'default', value: this.selectType.value })
    // }
  },
  methods: {
    getDefaultTypeValue(type) {
      for (let index = 0; index < this.options.length; index++) {
        const element = this.options[index]
        if (element.value == type) {
          return element
        }
      }

      return { label: '本月', value: 'month' }
    },

    updateDefaultStatus(status = 0) {
      this.isChecked = status
      this.$emit('update:isDefault', this.isChecked)
    },

    // 类型选择
    typeSelectClick(item) {
      this.editCustomTimes = []
      this.updateDefaultStatus()
      this.selectItem = item
    },
    //  确定
    sureClick() {
      if (this.showCustomContent) { // 自定义时间
        const days = moment(this.editCustomTimes[1].replace(/\./g, '-')).diff(moment(this.editCustomTimes[0].replace(/\./g, '-')), 'days', true)
        if (days > 90) {
          this.$message.error('筛选天数不能大于90天')
          return
        }

        this.customTimes = [this.editCustomTimes[0], this.editCustomTimes[1]]
        this.sureCustomContent = true
        this.showTypePopover = false
        this.$emit('change', {
          type: 'custom',
          startTime: this.editCustomTimes[0],
          endTime: this.editCustomTimes[1]
        })
      } else {
        this.selectType = this.selectItem
        this.sureCustomContent = false
        this.showTypePopover = false
        this.$emit('change',
          { type: 'default', value: this.selectType.value, label: this.selectType.label })
      }
    }
  }
}
</script>
<style lang="scss" scoped>
// 时间选择
.type-popper {
  .type-sort {
    flex-shrink: 0;
    width: 80px;
    height: 200px;
    font-size: 12px;
    color: $--color-n300;

    .type-sort-item {
      height: 34px;
      padding: 0 10px;
      line-height: 34px;
    }
  }

  &-common {
    padding-bottom: $--interval-base;
    border-bottom: $--border-medium;
  }

  .type-content {
    width: 300px;
    height: 200px;
    padding: 0 8px;
    color: $--color-black;

    .type-content-box {
      justify-content: space-around;
      height: 34px;

      .type-content-item {
        position: relative;
        width: 30%;
        height: 34px;
        line-height: 34px;
        text-align: center;
        cursor: pointer;
        border-radius: $--border-radius-base;

        .mark {
          display: inline-block;
          width: 6px;
          height: 6px;
          margin-right: 8px;
          background-color: transparent;
          border-radius: 3px;
        }
      }
    }

    .type-content-item:hover {
      background-color: #f5f7fa;
    }
  }

  .selected {
    font-weight: bold;
    color: #fff !important;
    background: $--button-default-selected-bg-color !important;

    .mark {
      background-color: $--button-default-selected-bg-color;
    }
  }

  .custom {
    width: 80px;
    height: 32px;
    font-size: 12px;
    line-height: 32px;
    color: $--color-n300;
    text-align: center;
    cursor: pointer;
  }

  .type-content-custom {
    position: relative;
    padding: 5px 20px 10px;
    overflow: hidden;

    .el-date-editor {
      margin-bottom: 8px;
    }

    .el-button {
      float: right;
    }
  }

  .confirm {
    display: flex;
    justify-content: space-between;
    padding: 8px 16px;
    line-height: 32px;
    border-top: $--border-medium;
  }
}

.el-icon-arrow-up {
  position: absolute;
  top: 10px;
  right: 5px;
  font-size: 14px;
  color: $--color-black;
  cursor: pointer;
  transition: transform 0.3s;
  transform: rotate(180deg);
}

// .el-icon-arrow-up.is-reverse {
//   transform: rotate(0deg);
// }
::v-deep .type-select {
  .el-input__inner {
    font-weight: $--font-weight-primary;
  }
}

::v-deep .type-select--no-border {
  .el-input__inner {
    background: $--button-default-background-color;
    border: none;
  }

  &.is-show {
    .el-input__inner {
      color: $--color-white;
      background-color: $--button-default-selected-bg-color !important;
      border-color: $--button-default-selected-bg-color;

      &::placeholder {
        color: $--color-white;
      }
    }

    .el-icon-arrow-up {
      color: $--color-white;
    }
  }
}
</style>
