<template>
  <flexbox class="dept-check-cell dept-cell" @click.native="cellClick">
    <el-checkbox
      ref="checkbox"
      v-bind="$attrs"
      @click.native.stop=""
      v-on="$listeners" /><div class="dept-cell__hd text-one-line" :title="$getDeptName(item, config.label)">{{ $getDeptName(item, config.label) }}</div>
    <i v-if="hasChildren && showNext" class="el-icon-arrow-right" />
  </flexbox>
</template>

<script>
import Flexbox from '@/components/Flexbox/Flexbox.vue'
import merge from '@/utils/merge'

const DeptCheckCell = {
  value: 'deptId',
  label: 'name'
}

export default {
  // DeptCheckCell 部门行
  name: 'DeptCheckCell',

  components: { Flexbox },

  inject: ['userDepDialog'],

  inheritAttrs: false,

  props: {
    item: Object,
    showNext: {
      type: Boolean,
      default: true
    },
    props: Object
  },

  data() {
    return {
    }
  },

  computed: {
    isChecked() {
      return this.userDepDialog.currentDepValue.includes(this.item.deptId)
    },

    // 有子部门
    hasChildren() {
      return this.item.children && this.item.children.length > 0
    },

    // 合并 props
    config() {
      return merge({ ...DeptCheckCell }, this.props || {})
    }
  },

  watch: {
    isChecked: {
      handler() {
        this.$set(this.item, 'isChecked', this.isChecked)
      },
      deep: true,
      immediate: true
    }
  },

  created() {},

  mounted() {
  },

  beforeDestroy() {},

  methods: {
    /**
     * 行点击
     */
    cellClick() {
      if (this.hasChildren) {
        this.$emit('select', this.item)
      } else {
        this.$refs.checkbox.$el.click()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "./style";

.dept-check-cell {
  ::v-deep .el-checkbox {
    .el-checkbox__label {
      display: none;
    }
  }
}
</style>
