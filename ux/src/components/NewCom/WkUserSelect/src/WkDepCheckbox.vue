<template>
  <div v-if="onlyShow" class="wk-dep-checkbox">
    <div
      v-for="(item, i) in data"
      :key="i"
      :label="item.deptId"
      class="xh-user__item">
      <div class="dep-name text-one-line">{{ `${$getDeptName(item)}${item.currentNum > 0 ? `(${item.currentNum})` : ''}` }}</div>
      <el-button
        v-if="hasChildren(item)"
        type="text"
        icon="wk wk-icon-structure"
        @click="lowerLevelClick(item)">下级</el-button>
    </div>
  </div>
  <el-checkbox-group
    v-else
    ref="checkboxGroup"
    v-model="currentValue"
    :disabled="disabled"
    class="wk-dep-checkbox"
    @change="depChange">
    <el-checkbox
      v-for="(item, i) in data"
      :key="i"
      :label="item.deptId"
      class="xh-user__item">
      <div class="dep-name text-one-line">{{ `${$getDeptName(item)}${item.currentNum > 0 ? `(${item.currentNum})` : ''}` }}</div>
      <el-button
        v-if="hasChildren(item)"
        type="text"
        icon="wk wk-icon-structure"
        @click="lowerLevelClick(item)">下级</el-button>
    </el-checkbox>
  </el-checkbox-group>
</template>

<script>
import UserDepMixin from './UserDepMixin'

export default {
  // 部门布局
  name: 'WkDepCheckbox',

  components: {},

  mixins: [UserDepMixin],

  props: {
    onlyShow: {
      type: Boolean,
      default: false
    }
  },

  data() {
    return {
    }
  },

  computed: {},

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 下一级
     */
    lowerLevelClick(item) {
      this.$emit('next', item)
    },

    /**
     * 有
     */
    hasChildren(item) {
      if (item.hasOwnProperty('children')) {
        return item.children.length > 0
      } else if (item.hasOwnProperty('currentNum')) {
        if (item.currentNum > 0) {
          return true
        } else {
          if (item.hasOwnProperty('hasChildren')) {
            return item.hasChildren === 1
          }
          return false
        }
      }
      return false
    },

    depChange() {

    }
  }
}
</script>

<style lang="scss" scoped>
.wk-dep-checkbox {
  .el-checkbox {
    margin-right: 0;
  }

  .xh-user {
    &__item {
      display: flex;
      align-items: center;
      height: 40px;

      ::v-deep .el-checkbox__input {
        flex-shrink: 0;
      }

      ::v-deep .el-checkbox__label {
        display: flex;
        flex: 1;
      }

      .dep-name {
        flex: 1;
        padding-right: 8px;
      }

      .el-checkbox {
        margin-right: 8px;
      }

      .el-button--text {
        position: relative;
        flex-shrink: 0;
        padding: 0 12px;
        color: #ccc;

        ::v-deep i {
          margin-right: 3px;
          font-size: 13px;
        }

        &::before {
          position: absolute;
          top: 2px;
          bottom: 2px;
          left: 0;
          width: 1px;
          content: " ";
          background-color: $--border-color-base;
        }

        &:hover {
          color: $--color-primary;
        }

        &.is-disabled {
          color: #c0c4cc;
        }
      }

      .user {
        &-img {
          flex-shrink: 0;
        }

        &-info {
          flex: 1;
          padding: 0 8px;
        }

        &-name {
          max-width: 200px;
        }

        &-post {
          margin-top: 2px;
          font-size: 12px;
          color: $--color-text-secondary;
        }
      }
    }
  }
}
</style>
