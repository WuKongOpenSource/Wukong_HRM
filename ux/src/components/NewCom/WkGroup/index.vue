<template>
  <div class="wk-group">
    <div class="wk-group-header">
      <div class="wk-group-mark" />
      <div class="wk-group-title">{{ data.name }}</div>
      <template v-if="showTips">
        <el-tooltip
          v-if="data.tipType == 'tooltip'"
          effect="dark"
          placement="top">
          <div slot="content" v-html="getTips(data)" />
          <i class="wk wk-help wk-help-tips" />
        </el-tooltip>
        <span v-else style="color: #6b778c;">{{ getTips(data) }}</span>
      </template>
    </div>
  </div>
</template>

<script>
import { isEmpty } from '@/utils/types'
export default {
  name: 'WkGroup',
  props: {
    data: {
      type: Object,
      default: () => {
        return {}
      }
    },
    // 默认展示
    showTips: {
      type: Boolean,
      default: true
    }
  },
  methods: {
    /**
     * 获取提示语
     */
    getTips(data) {
      const tips = data.tips || data.inputTips
      if (data.tipType == 'tooltip') {
        return isEmpty(tips) ? '' : tips
      }
      return isEmpty(tips) ? '' : `（${tips}）`
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-group-header {
  display: flex;
  align-items: center;
}

.wk-group-mark {
  height: 14px;

  // border-left-color: $--color-primary;
  // border-left-style: solid;
  // border-left-width: 4px;
  border-left: 4px solid #2362fb;
  border-radius: 2px;
}

.wk-group-title {
  margin-left: 8px;
  font-size: 16px;
  font-weight: 600;
  color: $--color-black;
}
</style>
