<template>
  <el-table :data="formatData" :row-style="showRow" v-bind="$attrs" class="tree-table" v-on="$listeners">
    <el-table-column v-if="columns.length===0" show-overflow-tooltip width="150">
      <template slot-scope="scope">
        <span v-for="space in scope.row._level" :key="space" class="ms-tree-space" />
        <span v-if="iconShow(0,scope.row)" class="tree-ctrl" @click="toggleExpanded(scope.$index)">
          <i v-if="!scope.row._expanded" class="el-icon-caret-right" />
          <i v-else class="el-icon-caret-bottom" />
        </span>
        {{ scope.$index }}
      </template>
    </el-table-column>
    <el-table-column
      v-for="(columnObj, index) in columns"
      v-else
      :key="columnObj.value"
      :label="columnObj.text"
      :width="columnObj.width"
      show-overflow-tooltip>
      <template slot-scope="scope">
        <span v-for="space in scope.row._level" v-show="index === 0" :key="space" class="ms-tree-space" />
        <span v-if="iconShow(index,scope.row)" class="tree-ctrl" @click.stop="toggleExpanded(scope.$index)">
          <i v-if="!scope.row._expanded" class="el-icon-caret-right" />
          <i v-else class="el-icon-caret-bottom" />
        </span>
        {{ scope.row[columnObj.value] }}<template v-if="index === 0">
          <i v-if="scope.row.deptType == 1" class="wk wk-subordinate tree-icon" />
          <i v-if="scope.row.deptType == 2" class="wk wk-customer-solid tree-icon" />
        </template>
      </template>
      <template slot="header" slot-scope="{ column }">
        {{ column.label }}
        <template v-if="columnObj.helpType">
          <i
            class="wk wk-icon-fill-help wk-help-tips"
            :data-type="columnObj.helpType"
            :data-id="columnObj.helpId" />
        </template>

      </template>
    </el-table-column>
    <slot />
  </el-table>
</template>

<script>
import treeToArray from './eval'

export default {
  name: 'TreeTable',
  inheritAttrs: false,
  props: {
    /* eslint-disable */
    data: {
      type: [Array, Object],
      required: true
    },
    columns: {
      type: Array,
      default: () => []
    },
    evalFunc: Function,
    evalArgs: Array,
    expandAll: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    // 格式化数据源
    formatData: function() {
      let tmp
      if (!Array.isArray(this.data)) {
        tmp = [this.data]
      } else {
        tmp = this.data
      }
      const func = this.evalFunc || treeToArray
      const args = this.evalArgs ? [tmp, this.expandAll].concat(this.evalArgs) : [tmp, this.expandAll]
      return func.apply(null, args)
    }
  },
  methods: {
    showRow: function(row) {
      const show = (row.row.parent ? (row.row.parent._expanded && row.row.parent._show) : true)
      row.row._show = show
      return show ? {
        animation:'treeTableShow 1s',
        '-webkit-animation':'treeTableShow 1s'
      } : {display:'none'}
    },
    // 切换下级是否展开
    toggleExpanded: function(trIndex) {
      const record = this.formatData[trIndex]
      record._expanded = !record._expanded
    },
    // 图标显示
    iconShow(index, record) {
      return (index === 0 && record.children && record.children.length > 0)
    }
  }
}
</script>
<style rel="stylesheet/css">
  /* stylelint-disable-next-line keyframes-name-pattern */
  @keyframes treeTableShow {
    from { opacity: 0; }
    to { opacity: 1; }
  }

  /* stylelint-disable-next-line keyframes-name-pattern */
  @-webkit-keyframes treeTableShow {
    from { opacity: 0; }
    to { opacity: 1; }
  }
</style>

<style lang="scss" rel="stylesheet/scss" scoped>
  $color-blue: #2196f3;
  $space-width: 18px;

  .ms-tree-space {
    position: relative;
    top: 1px;
    display: inline-block;
    width: $space-width;
    height: 14px;
    font-style: normal;
    font-weight: 400;
    line-height: 1;

    &::before {
      content: "";
    }
  }

  .processContainer {
    width: 100%;
    height: 100%;
  }

  .tree-ctrl {
    position: relative;
    margin-left: -$space-width;
    color: #777;
    cursor: pointer;
  }

  .tree-icon {
    margin-left: 8px;
    font-size: 13px;
    color: $--color-text-regular;
  }
</style>

<style lang="scss">
  .tree-table::before {
    display: none;
  }

  .can-visit-default:hover {
    .tree-icon {
      color: #2362fb;
    }
  }

  .tree-table {
    tbody {
      td {
        border-bottom: 0 none !important;

        .control {
          display: none;
          padding-left: 10px;
          cursor: pointer;
        }

        &:hover {
          .control {
            display: inline-block;
          }
        }
      }
    }
  }
</style>
