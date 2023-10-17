<template>
  <div class="dept-tree">
    <div class="tree-breadcrumb">
      <breadcrumb
        ref="breadcrumb"
        :data="breadcrumbs"
        @select="breadcrumbsClick" />
    </div>
    <div class="dept-tree__bd">
      <template v-if="currentBreadcrumb">
        <!-- 顶级展示全公司 相当于全选  单选不展示 -->
        <!-- <div v-if="breadcrumbs.length === 1 && !radio">
          <dept-check-cell
            :item="allItem"
            :label="allItem[config.value]"
            :disabled="disabled"
            @change="deptCheck(allItem)"
            @select="deptSelect" />
        </div> -->
        <div v-if="breadcrumbs.length > 1 && !radio && !disabled" class="all-check">
          <label
            class="el-checkbox"
            :class="[
              { 'is-checked': isAll }
            ]"
            @click.stop="allCheck"
          ><span
            class="el-checkbox__input"
            :class="{
              'is-checked': isAll
            }">
            <span class="el-checkbox__inner" />
          </span>
          </label><span class="label">全部</span>
        </div>
        <dept-check-cell
          v-for="(item, index) in currentBreadcrumb.children"
          :key="`dept${index}`"
          :item="item"
          :label="item[config.value]"
          :disabled="disabled"
          :props="config"
          @change="deptCheck(item, index)"
          @select="deptSelect" />
      </template>
    </div>
  </div>
</template>

<script>
import DeptCheckCell from './DeptCheckCell'
import Breadcrumb from './Breadcrumb'

import { objDeepCopy } from '@/utils'
import merge from '@/utils/merge'

const DeptTree = {
  value: 'deptId',
  label: 'name'
}

export default {
  // DeptTree
  name: 'DeptTree',

  components: {
    DeptCheckCell,
    Breadcrumb
  },

  inject: ['userDepDialog'],

  props: {
    tree: Array,
    disabled: Boolean,
    radio: Boolean,
    props: Object
  },

  data() {
    return {
      isAll: false,
      allItem: {
        deptId: '0',
        name: '全公司'
      },
      breadcrumbs: [],
      currentBreadcrumb: null
    }
  },

  computed: {
    // 合并 props
    config() {
      return merge({ ...DeptTree }, this.props || {})
    }
  },

  watch: {},

  created() {
    if (this.tree) {
      this.breadcrumbs.push({
        name: '全部',
        children: this.tree
      })
      this.currentBreadcrumb = {
        index: 0,
        children: this.tree
      }
    }
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 面包屑点击
     */
    breadcrumbsClick(item, index) {
      this.currentBreadcrumb = item
      this.breadcrumbs.splice(index + 1, this.breadcrumbs.length + 1 - index)

      // 刷新全选状态
      this.setAllStatus()
    },

    /**
     * 部门勾选
     */
    deptCheck(item) {
      // 刷新全选状态
      this.setAllStatus()

      this.$nextTick(() => {
        this.$emit('action', 'addDept', item)
      })
    },

    /**
     * 全选勾选
     */
    allCheck() {
      this.isAll = !this.isAll

      const deptValues = objDeepCopy(this.userDepDialog.currentDepValue)
      const deptIds = (this.currentBreadcrumb.children || []).map(item => item[this.config.value])
      // 添加
      if (this.isAll) {
        const addValues = []
        deptIds.forEach(deptId => {
        // 不包含在已选择数据的，需要添加
          if (!deptValues.includes(deptId)) {
            deptValues.push(deptId)
            addValues.push(deptId)
          }
        })
        this.$emit('action', 'add', addValues) // 动作历史 用于有规律调整选择 数据
        this.$emit('all-change', deptValues)
      } else {
        // 移除
        const newDeptValues = []
        const removeValues = []
        deptValues.forEach(deptId => {
          // 不包含的是需要保留
          if (!deptIds.includes(deptId)) {
            newDeptValues.push(deptId)
          } else {
            removeValues.push(deptId)
          }
        })
        this.$emit('action', 'remove', removeValues)
        this.$emit('all-change', newDeptValues)
      }
    },

    /**
     * 根据数据更新全部状态
     */
    setAllStatus() {
      this.$nextTick(() => {
        const { children } = this.currentBreadcrumb
        this.isAll = children ? children.every(item => item.isChecked) : false
      })
    },

    /**
     * 部门选择
     */
    deptSelect(item) {
      if (item.children && item.children.length > 0) {
        if (this.breadcrumbs[this.config.value] !== item[this.config.value]) {
          this.breadcrumbs.push(item)
          this.currentBreadcrumb = item

          // 更新面包屑位置
          this.$refs.breadcrumb.updatePostion()
          // 刷新全选状态
          this.setAllStatus()
        }
      }
    }

  }
}
</script>

<style lang="scss" scoped>
@import "./style";

.dept-tree {
  position: relative;
  height: 100%;
  font-size: $--font-size-base;

  &__bd {
    height: calc(100% - 30px);
    overflow-y: auto;
  }
}
</style>
