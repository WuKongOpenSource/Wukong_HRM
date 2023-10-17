<template>
  <div class="detail-header">
    <flexbox
      align="center"
      justify="flex-start"
      class="header-control">
      <flexbox-item
        class="info">
        <div class="type-name">
          {{ typeName }}
        </div>
        <div class="mini-title">
          {{ label }}
        </div>
      </flexbox-item>
      <template v-if="btnGroup && btnGroup.length > 0">
        <el-button
          v-for="(btn, index) in btnGroup"
          :key="index"
          :icon="btn.icon || ''"
          :type="btn.type || ''"
          :class="btn.btnClass"
          @click="handleBtnClick(btn.command)">
          {{ btn.btnText }}
        </el-button>
      </template>

      <el-dropdown
        v-if="moreBtnGroup && moreBtnGroup.length > 0"
        trigger="click"
        @command="handleBtnClick">
        <el-button
          icon="el-icon-more"
          class="t-more" />
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item
            v-for="(item, index) in moreBtnGroup"
            :key="index"
            :icon="item.icon | wkIconPre"
            :command="item.command">
            {{ item.label }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </flexbox>

    <div class="abstract">
      <flexbox
        align="stretch">
        <div
          v-for="(item, index) in baseInfo"
          :key="index"
          class="abstract-item"
          span="200">
          <div class="abstract-title">
            {{ item.title }}
          </div>
          <div
            :style="{ color: item.color }"
            class="abstract-value">
            {{ item.value }}
          </div>
        </div>
      </flexbox>
    </div>
  </div>
</template>

<script>
import { separator } from '@/filters/vueNumeralFilter/filters'
import { isEmpty } from '@/utils/types'

export default {
  name: 'DetailHeader',
  props: {
    typeName: String, // 类型名称
    label: String, // 小标题
    icon: String, // 图标
    iconColor: String, // 图标颜色
    btnGroup: { // 操作按钮组
      type: Array,
      default: () => []
    },
    moreBtnGroup: { // 更多按钮组
      type: Array,
      default: () => []
    },
    detailData: { // 详情数据
      type: Object,
      default: () => {}
    },
    fields: { // 头部首要信息字段
      type: Array,
      default: () => []
    }
  },
  data() {
    return {}
  },
  computed: {
    /**
     * 详情头部信息字段格式化
     * @return {[]|*[]}
     */
    baseInfo() {
      const res = []
      /**
       * {
       *   fieldName: String, 字段名
       *   field: String, 字段
       *   color: String, 颜色
       *   colorFormat: Function|null, // 颜色格式化函数
       *   valueFormat: Function|Null // 字段值格式化函数
       *   isMoneyField: false
       * }
       */
      this.fields.forEach(item => {
        let value = this.detailData[item.field]
        let color = item.color || ''
        if (item.colorFormat && typeof item.colorFormat === 'function') {
          color = item.colorFormat({
            field: item.field,
            value: value
          })
        }
        if (item.isMoneyField) {
          value = separator(value || 0)
        } else if (item.valueFormat && typeof item.valueFormat === 'function') {
          value = item.valueFormat({
            field: item.field,
            value: value
          })
        }

        res.push({
          title: item.fieldName,
          value: isEmpty(value) ? '--' : value,
          color: color
        })
      })
      return res || []
    }
  },
  methods: {
    handleBtnClick(command) {
      this.$emit('command', command)
    }
  }
}
</script>

<style scoped lang="scss">
  .detail-header {
    width: 100%;
    background-color: white;
    .header-control {
      width: 100%;
      padding: 32px 32px 0;

      .info {
        .type-name {
          color: $--color-text-secondary;
          font-size: $--font-size-base;
          margin-bottom: 2px;
        }
        .mini-title {
          font-size: $--font-size-xxlarge;
          font-weight: $--font-weight-bold;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 1;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
      }

      .el-dropdown {
        margin-left: 8px;
      }
    }

    .abstract{
      position: relative;
      margin-top: #{$--interval-base * 2};
      padding: 0 32px;

      .vux-flexbox {
        padding: #{$--interval-base * 2};
        background-color: $--color-n20;
        border-radius: $--border-radius-base;
      }

      &-item{
        flex: 0 0 20%;
        overflow: hidden;
        & + & {
          padding-left: $--interval-base;
        }
      }

      &-title{
        color: $--color-text-secondary;
      }
      &-value{
        min-height: 14px;
        margin-top: #{$--interval-base};
      }
    }
  }
</style>
