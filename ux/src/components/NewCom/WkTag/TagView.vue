<template>
  <div
    class="tag-view"
    :style="{ 'flex-wrap': wrap}">
    <div
      v-for="(item, index) in showValue"
      :key="index"
      ref="tag"
      :style="{'background':colorRgb(item.value,0.1), 'margin-bottom': `${itemBottom}px`,'color':item.value}"
      class="item-label">{{ item.name }}</div>
    <el-tooltip
      v-if="hideValue.length > 0"
      placement="top"
      effect="light"
      popper-class="tooltip-change-border">
      <div
        slot="content"
        class="tooltip-box">
        <span
          v-for="(item, index) in value"
          v-show="index >= maxTagCount"
          :key="index"
          class="tooltip-item"
          :style="{'background':colorRgb(item.value,0.1), 'margin-bottom': `${itemBottom}px`,'color': item.value}">
          {{ item.name }}
        </span>
      </div>
      <el-button
        size="mini"
        style="height: 20px;"
        icon="el-icon-more" />
    </el-tooltip>

    <slot />
  </div>

</template>
<script>
import { convertHexByOpacity } from '@/utils'

export default {
  name: 'TagView',
  props: {
    value: null,
    // 已无效 自动计算
    // maxLength: {
    //   type: Number,
    //   default: Infinity
    // },
    wrap: {
      type: String,
      default: 'nowrap' // wrap 默认不展示更多按钮 nowrap 展示一行的效果，超出宽度展示更多
    },
    itemBottom: {
      type: Number,
      default: 4
    }
  },
  data() {
    return {
      maxTagCount: null
    }
  },
  computed: {
    showValue() {
      if (this.wrap === 'wrap') {
        return this.value
      }
      if (this.maxTagCount === null) {
        return this.value
      } else {
        return this.value.slice(0, this.maxTagCount)
      }
    },

    hideValue() {
      if (this.wrap === 'wrap') {
        return []
      }
      return this.value.slice(this.maxTagCount)
    }
  },
  mounted() {
    // 仅一行展示有效
    if (this.wrap === 'nowrap') {
      this.setMaxTagCount()
      window.addEventListener('resize', this.setMaxTagCount)
    }
  },
  beforeDestroy() {
    if (this.wrap === 'nowrap') {
      window.removeEventListener('resize', this.setMaxTagCount)
    }
  },
  methods: {
    /**
     * 转换颜色成rgb格式
     */
    colorRgb(hexCode, opacity) {
      return convertHexByOpacity(hexCode, opacity)
    },

    /**
     * @description: 设置最大标签数量
     * @return {*}
     */
    setMaxTagCount() {
      const containerWidth = this.$el.getBoundingClientRect().width
      const tagMargin = 4 // 标签间距
      let tagWidth = 0 // 标签宽度
      let tagCount = 0 // 标签数量
      this.$refs.tag?.forEach(tag => {
        tagWidth = tag.getBoundingClientRect().width
        if (containerWidth >= tagCount * (tagWidth + tagMargin) + tagWidth) {
          tagCount++
        }
      })
      this.maxTagCount = tagCount
    }
  }
}
</script>
<style lang='scss' scoped>
.tag-view {
  display: flex;

  .item-label {
    display: inline-block;
    height: 20px;
    padding: 0 10px;
    margin-right: 4px;
    margin-bottom: 4px;
    font-size: 12px;
    line-height: 20px;
    color: #fff;
    white-space: nowrap;
    border-radius: $--border-radius-base;
  }

  .color-label-more {
    position: relative;
    display: inline-block;
    width: 34px;
    height: 20px;
    font-size: inherit;
    font-weight: 700;
    line-height: 20px;
    text-align: center;
    vertical-align: middle;
    background: #eee;
    border-radius: $--border-radius-base;

    // margin-bottom: 6px;
    i {
      position: absolute;
      top: 0%;
      left: 50%;
      height: 20px;
      line-height: 36px;
      transform: translate(-50%, -50%);
    }
  }
}

.tooltip-box {
  margin: 8px 0 4px;

  .tooltip-item {
    display: inline-block;
    padding: 3px 10px;
    margin-right: 10px;
    margin-bottom: 4px;
    color: #fff;
    border-radius: 4px;
  }
}
</style>
