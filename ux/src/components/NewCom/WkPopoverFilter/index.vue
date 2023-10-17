<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-28 17:23:39
 * @LastEditTime: 2023-08-18 15:27:13
 * @LastEditors: chenhaojie 1476192083@qq.com
-->
<template>
  <el-popover
    v-model="popverShow"
    v-bind="$attrs"
    class="wk-popover-filter"
    v-on="$listeners"
    @hide="hide">
    <div style="padding: 3px 12px;">
      <wk-form
        :field-from="from"
        :field-list="fieldList"
        class="wk-filter-form"
        custom-class=""
      >
        <template slot-scope="{ data }">
          <div
            v-if="data && data.formType == 'range'"
            class="wk-range-select">
            <flexbox class="wk-range-select__top">
              <el-input-number
                v-model="from[data.field][0]"
                :precision="data.hasOwnProperty('precision') ? data.precision : undefined"
                maxlength="100"
                :controls="false" />
              <span class="range-separator">至</span>
              <el-input-number
                v-model="from[data.field][1]"
                :precision="data.hasOwnProperty('precision') ? data.precision : undefined"
                maxlength="100"
                :controls="false" />
            </flexbox>
          </div>
          <slot :data="data" />
        </template>
      </wk-form>
      <div>
        <el-button type="primary" @click="sureClick">确定</el-button>
        <el-button @click="resetClick">重置</el-button>
      </div>
    </div>
    <!-- <i
      slot="reference"
      :class="{'is-select': hasContent || popverShow}"
      class="wk wk-screening" /> -->
    <el-button
      slot="reference"
      :type="popverShow ? 'selected' : ''"
      class="filter-button"
      icon="wk wk-screening"
    >高级筛选</el-button>
  </el-popover>
</template>

<script>
import WkForm from '@/components/NewCom/WkForm'

import { objDeepCopy } from '@/utils'

export default {
  // Popover 形式的筛选
  name: 'WkPopoverFilter',
  components: {
    WkForm
  },
  inheritAttrs: false,
  props: {
    hasContent: {
      type: Boolean,
      default: false
    },
    fieldFrom: Object,
    fieldList: Array
  },
  data() {
    return {
      popverShow: false,
      from: {}
    }
  },
  computed: {},
  watch: {
    popverShow(val) {
      this.$emit(val ? 'show' : 'hide')
    },
    fieldFrom: {
      handler() {
        const from = objDeepCopy(this.fieldFrom)
        this.fieldList.forEach(item => {
          if (item.formType == 'range') {
            from[item.field] = from[item.field] ? from[item.field] : ['', '']
          }
        })
        this.from = from
      },
      immediate: true
    }
  },
  mounted() {},

  beforeDestroy() {},
  methods: {
    sureClick() {
      this.$emit('update:fieldFrom', this.from)
      this.$emit('sure', objDeepCopy(this.from))
      this.popverShow = false
    },

    resetClick() {
      this.$emit('reset')
    },

    hide() {
      const from = objDeepCopy(this.fieldFrom)
      this.fieldList.forEach(item => {
        if (item.formType == 'range') {
          from[item.field] = from[item.field] ? from[item.field] : ['', '']
        }
      })
      this.from = from
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-popover-filter {
  vertical-align: middle;
}

.wk-filter-form {
  display: flex;
  flex-wrap: wrap;

  ::v-deep.el-form-item {
    display: flex;
    flex-shrink: 0;
    margin-right: 30px;
    margin-bottom: 15px;

    .el-form-item__content {
      display: flex;
      align-items: center;
    }

    .el-select {
      .el-input__inner {
        height: 32px !important;
      }
    }

    .wk-user-select,
    .wk-dep-select,
    .el-input {
      width: 200px !important;
    }

    ::v-deep .el-input-number {
      .el-input__inner {
        padding: 0 6px !important;
        text-align: left;
      }
    }
  }
}

.wk-range-select {
  &__top {
    .range-separator {
      margin: 0 10px;
    }

    .range-unit {
      flex-shrink: 0;
      width: 75px;
      margin-left: 10px;
    }
  }
}
</style>
