<template>
  <div class="user-tree">
    <div class="tree-breadcrumb">
      <breadcrumb
        ref="breadcrumb"
        :data="breadcrumbs"
        @select="breadcrumbsClick" />
    </div>
    <div class="user-tree__bd">
      <template v-if="currentBreadcrumb">
        <!-- 顶级没有全部 -->
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
        <!-- 部门 -->
        <flexbox
          v-for="(item, index) in currentBreadcrumb.children"
          :key="`dept${index}`"
          class="dept-cell"
          @click.native="deptClick(item, index)">
          <label
            class="el-checkbox"
            :class="[
              {
                'is-disabled': radio || disabled,
                'is-checked': item.isChecked
              }
            ]"
            @click.stop="deptCheck(item, index)"
          ><span
            class="el-checkbox__input"
            :class="{
              'is-disabled': radio || disabled,
              'is-checked': item.isChecked,
              'is-indeterminate': item.indeterminate
            }">
            <span class="el-checkbox__inner" />
          </span>
          </label><div class="dept-cell__hd text-one-line" :title="$getDeptName(item, deptConfig.label)">{{ $getDeptName(item, deptConfig.label) }}</div>
          <i v-if="((item.children && item.children.length > 0) || (item.userList && item.userList.length > 0))" class="el-icon-arrow-right" />
        </flexbox>
        <!-- 员工 -->
        <check-cell
          v-for="(item, index) in currentBreadcrumb.userList"
          :key="`user${index}`"
          :item="item"
          :label="item[userConfig.value]"
          :disabled="disabled"
          :props="userConfig"
          @change="userCheck(item, index)" />
      </template>
    </div>
  </div>
</template>

<script>
import CheckCell from './CheckCell'
import Breadcrumb from './Breadcrumb'

import { objDeepCopy } from '@/utils'
import merge from '@/utils/merge'

const UserTreeUser = {
  value: 'userId',
  label: 'realname'
}

const UserTreeDept = {
  value: 'deptId',
  label: 'name'
}

export default {
  // UserTree
  name: 'UserTree',

  components: {
    CheckCell,
    Breadcrumb
  },

  inject: ['userDepDialog'],

  props: {
    visisble: Boolean, // 外出标记当前组件是否隐藏
    tree: Array, // 数据源
    radio: Boolean,
    disabled: Boolean,
    userProps: Object, // 员工部门控制项
    deptProps: Object
  },

  data() {
    return {
      isAll: false,
      breadcrumbs: [],
      currentBreadcrumb: null,
      isEdit: false // 编辑的时候 退出刷新上层书
    }
  },

  computed: {
    // 合并 props
    userConfig() {
      return merge({ ...UserTreeUser }, this.userProps || {})
    },

    deptConfig() {
      return merge({ ...UserTreeDept }, this.deptProps || {})
    }
  },

  watch: {
    'userDepDialog.currentUserValue': {
      handler() {
        this.isEdit = true
      }
    },
    visisble() {
      if (this.visisble && this.isEdit) {
        // 更改整个树的状态
        this.refreshData()
        // 刷新全选状态
        this.setAllStatus()
      }
    }
  },

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

  mounted() {
    this.refreshData()
  },

  beforeDestroy() {},

  methods: {
    /**
     * 刷新数据
     */
    refreshData() {
      const list = this.tree
      const values = this.userDepDialog.currentUserValue
      this.loopChildren(list, values)
    },

    /**
     * index 0 第一层
     */
    loopChildren(list, values) {
      // 当前item 里面的 userList children 任一勾选
      list.forEach(item => {
        let hasCheck = false
        let userCount = 0 // 勾选的员工数量
        if (item.userList) {
          item.userList.forEach(user => {
            if (values.includes(user[this.userConfig.value])) {
              userCount++
              hasCheck = true
              // 勾选
              this.$set(user, 'isChecked', true)
            } else {
              this.$set(user, 'isChecked', false)
            }
          })
        }

        // 上层Item
        let checkDeptCount = 0 // 勾选的部门数量
        if (item.children) {
          this.loopChildren(item.children, values)
          item.children.forEach(dept => {
            if (dept.isChecked) {
              checkDeptCount++
              hasCheck = true
            }
            if (dept.indeterminate) {
              hasCheck = true
            }
          })
        }

        // 刷新当前item状态
        if (hasCheck) {
          const deptCount = checkDeptCount
          const checkCount = deptCount + userCount
          const allCount = (item.userList ? item.userList.length : 0) + (item.children ? item.children.length : 0)

          if (checkCount > 0) {
            if (checkCount === allCount) {
              this.$set(item, 'isChecked', true)
              this.$set(item, 'indeterminate', false)
            } else {
              this.$set(item, 'isChecked', false)
              this.$set(item, 'indeterminate', true)
            }
          } else {
            this.$set(item, 'isChecked', false)
            this.$set(item, 'indeterminate', true) // hasCheck 有勾选
          }
        } else {
          this.$set(item, 'isChecked', false)
          this.$set(item, 'indeterminate', false)
        }
      })
    },

    /**
     * 面包屑点击
     */
    breadcrumbsClick(item, index) {
      // 刷新上层状态
      if (this.isEdit) {
        this.isEdit = false
        this.findAllParentInfo(this.tree, this.currentBreadcrumb[this.deptConfig.value])
      }

      this.currentBreadcrumb = item
      this.breadcrumbs.splice(index + 1, this.breadcrumbs.length + 1 - index)

      // 刷新全选状态
      this.setAllStatus()
    },

    /**
     * 部门点击
     */
    deptClick(item) {
      if ((item.children && item.children.length > 0) ||
      (item.userList && item.userList.length)) {
        if (this.breadcrumbs[this.deptConfig.value] !== item[this.deptConfig.value]) {
          this.breadcrumbs.push(item)
          this.currentBreadcrumb = item

          // 更新面包屑位置
          this.$refs.breadcrumb.updatePostion()
          // 刷新全选状态
          this.setAllStatus()
        }
      }
    },

    /**
     * 根据数据更新全部状态
     * 点击部门 和 面包屑的时候 触发
     * 勾选 员工 部门 触发
     */
    setAllStatus() {
      this.$nextTick(() => {
        const { userList, children } = this.currentBreadcrumb
        let isChecked = children ? children.every(item => item.isChecked) : true
        if (isChecked) {
          isChecked = userList ? userList.every(item => item.isChecked) : true
        }
        this.isAll = isChecked
      })
    },

    /**
     * 用全部状态更新部门数据，树形结构之后的数据
     */
    updateChildrenItemsByAllStatus() {
      // userList 的勾选 受 value的控制  仅处理children 部门
      this.loopChildrenStatus(this.currentBreadcrumb, this.isAll)
    },

    /**
     * 获取子部门
     */
    loopChildrenStatus(item, isChecked) {
      if (item.children) {
        item.children.forEach(item => {
          this.$set(item, 'isChecked', isChecked)
          this.$set(item, 'indeterminate', false) // 因为是全部勾选或取消。将部分变false
          if (item.children) {
            this.loopChildrenStatus(item, isChecked)
          }
        })
      }
    },

    /**
     * 部门勾选
     */
    deptCheck(item) {
      // 单选不能选择
      if (this.radio || this.disabled) {
        return
      }
      const isChecked = !item.isChecked
      this.$set(item, 'isChecked', isChecked)
      this.$set(item, 'indeterminate', false) // 因为是全部勾选或取消。将部分变false

      // 更新value 也就是更新userList的勾选
      this.updateValueByItemAndIsChecked(item, isChecked)
      // 更新children 的勾选状态
      this.loopChildrenStatus(item, isChecked)
      // 刷新全选状态
      this.setAllStatus()
    },

    /**
     * 根据isChecked 以 item及子层级的 所有userIds 更新 value
     */
    updateValueByItemAndIsChecked(item, isChecked) {
      const userIds = []
      this.getUserIds(item, userIds) // 选定id填充到userIds

      const userValues = objDeepCopy(this.userDepDialog.currentUserValue)
      // 添加
      if (isChecked) {
        const addValues = []
        userIds.forEach(id => {
        // 不包含在已选择数据的，需要添加
          if (!userValues.includes(id)) {
            userValues.push(id)
            addValues.push(id)
          }
        })
        this.$emit('change', userValues)
        this.$emit('action', 'add', addValues) // 动作历史 用于有规律调整选择 数据
      } else {
        // 移除
        const newUserValues = []
        const removeValues = []
        userValues.forEach(id => {
          // 不包含的是需要保留
          if (!userIds.includes(id)) {
            newUserValues.push(id)
          } else {
            removeValues.push(id)
          }
        })
        this.$emit('change', newUserValues)
        this.$emit('action', 'remove', removeValues)
      }
    },

    /**
     * 根据部门下的所有员工ids
     */
    getUserIds(item, userIds) {
      if (item.userList) {
        item.userList.forEach(item => {
          userIds.push(item[this.userConfig.value])
        })
      }

      if (item.children) {
        item.children.forEach(element => {
          if (element.userList) {
            element.userList.forEach(item => {
              userIds.push(item[this.userConfig.value])
            })
          }
          if (element.children) {
            this.getUserIds(element, userIds)
          }
        })
      }
    },

    /**
     * 员工选择
     */
    userCheck(item) {
      // 刷新全选状态
      this.setAllStatus()

      this.$nextTick(() => {
        this.$emit('action', 'addUser', item)
      })
    },

    /**
     * 全选勾选
     */
    allCheck() {
      this.isAll = !this.isAll
      this.updateChildrenItemsByAllStatus()
      this.updateValueByItemAndIsChecked(this.currentBreadcrumb, this.isAll)
    },

    /**
     * 获取所有部门父节点
     */
    findAllParentInfo(tree, deptId) {
      const forFn = (arr, deptId) => {
        arr.map(item => {
          if (item[this.deptConfig.value] === deptId) {
            // 更新所有部门勾选状态
            const { userList, children } = item
            const depts = children || []
            const users = userList || []

            const deptCount = depts.filter(item => item.isChecked).length
            const userCount = users.filter(item => item.isChecked).length
            const checkCount = deptCount + userCount
            const allCount = depts.length + users.length
            if (checkCount > 0) {
              if (checkCount === allCount) {
                this.$set(item, 'isChecked', true)
                this.$set(item, 'indeterminate', false)
              } else {
                this.$set(item, 'isChecked', false)
                this.$set(item, 'indeterminate', true)
              }
            } else {
              this.$set(item, 'isChecked', false)
              this.$set(item, 'indeterminate', false)
            }
            forFn(tree, item.parentId)
          } else if (item.children && item.children.length > 0) {
            forFn(item.children, deptId)
          }
        })
      }
      forFn(tree, deptId)
    }

  }
}
</script>

<style lang="scss" scoped>
@import "./style";

.user-tree {
  position: relative;
  height: 100%;
  font-size: $--font-size-base;

  &__hd {
    line-height: 30px;
  }

  &__bd {
    height: calc(100% - 30px);
    overflow-y: auto;
  }
}

::v-deep .breadcrumb {
  .breadcrumb-item {
    .label {
      max-width: 200px;
    }
  }
}
</style>
