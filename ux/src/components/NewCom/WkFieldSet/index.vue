<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-06-03 20:50:42
 * @LastEditTime: 2020-06-03 21:25:50
 * @LastEditors: yang
-->
<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-06-03 20:32:18
 * @LastEditTime: 2020-06-03 20:49:11
 * @LastEditors: yang
-->
<template>
  <el-popover
    v-model="show"
    popper-class="no-padding-popover"
    placement="bottom"
    width="240"
    trigger="click">
    <div v-loading="loading" class="wk-field-set">
      <el-input
        v-model="search"
        class="wk-field-set__search"
        placeholder="搜索字段"
        @input="searchClick" />

      <div class="wk-field-set__content">
        <draggable
          v-model="copyfields"
          :options="{ dragClass: 'sortable-drag', forceFallback: false }">
          <div
            v-for="(item, index) in copyfields"
            :key="index"
            class="wk-field-set__content--item text-one-line">
            <el-switch v-model="item.check" />
            <span v-if="item.center">{{ item.left }}<span class="input-word">{{ item.center }}</span>{{ item.right }}</span>
            <span v-else>{{ item.name }}</span>
          </div>
        </draggable>
      </div>

      <div class="wk-field-set__ft">
        <el-button type="text" @click="reSet">重置</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </div>
    </div>
    <slot v-if="$slots.reference" slot="reference" @click="show = !show" />
    <!-- <i
      v-else
      slot="reference"
      class="wk wk-icon-config-line"
      @click="show = !show" /> -->
    <el-button
      v-else
      slot="reference"
      icon="wk wk-icon-config-line"
      class="set-btn"
      @click="show = !show" />
  </el-popover>
</template>

<script>
import { objDeepCopy } from '@/utils'
import Draggable from 'vuedraggable'

export default {
  /** 字段管理 */
  name: 'WkFieldSet',
  components: {
    Draggable
  },
  props: {
    loading: Boolean,
    fields: Array
  },
  data() {
    return {
      show: false,
      // 用于重置
      copyfields: [],
      search: ''
    }
  },
  computed: {},
  watch: {
    fields: {
      handler() {
        this.copyfields = objDeepCopy(this.fields)
      },
      immediate: true
    }
  },
  mounted() {},

  beforeDestroy() {},
  methods: {
    /**
     * 搜索操作
     */
    searchClick() {
      this.fields = this.fields.map(item => {
        const index = item.name.indexOf(this.search)
        if (index != -1) {
          item.left = item.name.substr(0, index)
          item.center = item.name.substr(index, this.search.length)
          const rightIndex = index + this.search.length
          item.right = item.name.substr(rightIndex, item.name.length - rightIndex)
        } else {
          item.left = ''
          item.center = ''
          item.right = ''
        }
        return item
      })
    },

    /**
     * 保存更改
     */
    save() {
      const noHideIds = this.copyfields.filter(item => {
        return item.check
      })
      if (noHideIds.length < 2) {
        this.$message.error('至少要显示两列')
      } else {
        this.$emit('save', this.copyfields)
      }
    },

    /**
     * 重置
     */
    reSet() {
      this.copyfields = objDeepCopy(this.fields)
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-field-set {
  &__search {
    padding: 8px 10px 0;
  }

  &__content {
    min-height: 100px;
    max-height: 250px;
    overflow: auto;

    &--item {
      padding: 5px 10px;
      font-size: 12px;
      cursor: move;

      .el-switch {
        margin-right: 3px;
        transform: scale(0.8);

        ::v-deep .el-switch__core {
          width: 33px !important;
        }
      }
    }

    &--item:hover {
      background-color: #e7edf4;
    }
  }

  &__ft {
    padding: 8px 0;
    margin-top: 10px;
    overflow: hidden;
    overflow-y: overlay;
    background-color: $--background-color-base;

    .el-button {
      float: right;
      padding: 6px 12px;
      font-size: 12px;
    }
  }
}

// 设置
.table-set {
  width: 15px;
  margin-top: 5px;
  color: #9da9c2;
  cursor: pointer;

  &:hover {
    color: $--color-primary;
  }
}

// 搜索到的字
.input-word {
  color: white;
  background-color: $--color-primary;
}

.sortable-drag {
  cursor: move;
}

.set-btn {
  padding: 11px 8px;
  font-size: 16px;
}
</style>
