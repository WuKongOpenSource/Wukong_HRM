<template>
  <el-popover
    v-model="showTypePopover"
    :width="width"
    :placement="placement"
    class="time-type-select"
    popper-class="no-padding-popover"
    trigger="click">
    <div class="type-popper">
      <div class="type-content">
        <div
          v-for="(item, index) in options"
          :key="index"
          :class="{ 'selected' : selectType.value == item.value && !showCustomContent}"
          class="type-content-item"
          @click="typeSelectClick(item)">
          <div class="mark" />{{ item.label }}
        </div>
        <div
          :class="{ 'selected' : showCustomContent}"
          class="type-content-item"
          @click="showCustomContent = true">
          <div class="mark" />自定义
        </div>
      </div>
      <div
        v-if="showCustomContent"
        class="type-content-custom">
        <el-date-picker
          v-model="startTime"
          type="date"
          value-format="yyyy.MM.dd"
          placeholder="选择日期" />
        <el-date-picker
          v-model="endTime"
          type="date"
          value-format="yyyy.MM.dd"
          placeholder="选择日期" />
        <el-button
          type="primary"
          @click="customSureClick">确定</el-button>
      </div>
    </div>
    <el-input
      slot="reference"
      v-model="typeShowValue"
      :readonly="true"
      :style="{width: isNaN(width) ? width : `${width}px`}"
      class="type-select">
      <i
        slot="suffix"
        :class="['el-icon-arrow-up']" />
    </el-input>
  </el-popover>
</template>

<script type="text/javascript">
import { isObject } from '@/utils/types'

export default {
  name: 'TimeTypeSelect',
  props: {
    defaultType: [String, Object],
    // 容器宽度，默认200px
    width: {
      type: [String, Number],
      default: 200
    },
    placement: {
      type: String,
      default: 'bottom'
    },
    options: {
      type: Array,
      default: () => {
        return [
          { label: '今天', value: 'today' },
          { label: '昨天', value: 'yesterday' },
          { label: '本周', value: 'week' },
          { label: '上周', value: 'lastWeek' },
          { label: '本月', value: 'month' },
          { label: '上月', value: 'lastMonth' },
          { label: '本季度', value: 'quarter' },
          { label: '上季度', value: 'lastQuarter' },
          { label: '本年', value: 'year' },
          { label: '去年', value: 'lastYear' }
        ]
      }
    } // 数据源 如果存在 替换 默认
  },
  data() {
    return {
      selectType: { label: '本年', value: 'year' },
      showTypePopover: false,
      showCustomContent: false, // 展示自定义时间内容
      sureCustomContent: false, // 确定

      startTime: '',
      endTime: ''
    }
  },
  computed: {
    typeShowValue() {
      if (this.sureCustomContent) {
        if (this.startTime || this.endTime) {
          return (this.startTime || '') + '-' + (this.endTime || '')
        }
        return ''
      } else {
        return this.selectType.label
      }
    }
  }, // 时间类型选择
  watch: {
    defaultType() {
      if (typeof this.defaultType === 'string') {
        if (this.selectType.value != this.defaultType) {
          this.selectType = this.getDefaultTypeValue(this.defaultType)
        }
      } else if (isObject(this.defaultType)) {
        if (this.defaultType.type == 'default') {
          this.selectType = this.getDefaultTypeValue(this.defaultType.value)
        } else if (this.defaultType.type == 'custom') {
          this.sureCustomContent = true
          this.showCustomContent = true
          this.startTime = this.defaultType.startTime
          this.endTime = this.defaultType.endTime
        }
      }
    }
  },
  mounted() {
    if (this.defaultType !== undefined) {
      if (typeof this.defaultType === 'string') {
        this.selectType = this.getDefaultTypeValue(this.defaultType)
      } else if (isObject(this.defaultType)) {
        if (this.defaultType.label) {
          this.selectType = this.defaultType
        } else if (this.defaultType.type == 'default') {
          this.selectType = this.getDefaultTypeValue(this.defaultType.value)
        } else if (this.defaultType.type == 'custom') {
          this.sureCustomContent = true
          this.showCustomContent = true
          this.startTime = this.defaultType.startTime
          this.endTime = this.defaultType.endTime
        }
      }
    } else {
      this.$emit('change', { type: 'default', value: this.selectType.value })
    }
  },
  methods: {
    getDefaultTypeValue(type) {
      for (let index = 0; index < this.options.length; index++) {
        const element = this.options[index]
        if (element.value == type) {
          return element
        }
      }

      return { label: '本年', value: 'year' }
    },

    // 类型选择
    typeSelectClick(item) {
      this.showTypePopover = false
      this.sureCustomContent = false
      this.showCustomContent = false
      this.selectType = item
      this.$emit('change', { type: 'default', value: this.selectType.value, label: this.selectType.label })
    },
    // 选择自定义时间 确定
    customSureClick() {
      if (this.startTime && this.endTime) {
        if (this.$moment(this.endTime).isBefore(this.startTime)) {
          this.$message.error('开始时间必须小于结束时间')
          return
        }

        this.sureCustomContent = true
        this.showTypePopover = false

        this.$emit('change', {
          type: 'custom',
          startTime: this.startTime,
          endTime: this.endTime
        })
      }
    }
  }
}
</script>
<style lang="scss" scoped>
// 时间选择
.type-popper {
  .type-content {
    max-height: 250px;
    overflow-y: auto;

    .type-content-item {
      position: relative;
      box-sizing: border-box;
      height: $--select-option-height;
      padding: 0 20px;
      overflow: hidden;
      font-size: $--select-font-size;
      line-height: $--select-option-height;
      color: $--select-option-color;
      text-overflow: ellipsis;
      white-space: nowrap;
      cursor: pointer;

      .el-input {
        margin-bottom: 5px;
      }
    }

    .selected {
      font-weight: bold;
      color: $--select-option-selected-font-color;
      background-color: $--select-option-selected-background;
      box-shadow: $--select-option-selected-box-shadow;
    }

    .type-content-item:hover {
      background-color: $--select-option-hover-background;
    }
  }

  .type-content-custom {
    position: relative;
    padding: 5px 20px 10px;
    overflow: hidden;

    .el-date-editor {
      width: 100%;
      margin-bottom: 8px;
    }

    .el-button {
      float: right;
      margin-top: 5px;
    }
  }
}

.el-icon-arrow-up {
  position: absolute;
  top: 12px;
  right: 5px;
  font-size: $--input-font-size;
  font-weight: bold;
  color: $--input-icon-color;
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
</style>
