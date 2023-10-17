<template>
  <div
    :class="['xr-radio-menu-wrap', { 'is-show': popverVisible }]">
    <el-popover
      v-model="popverVisible"
      :width="width"
      placement="bottom-start"
      popper-class="no-padding-popover"
      trigger="click">
      <div class="xr-radio-menu">
        <div class="xr-radio-menu__content">
          <div
            v-for="(item, index) in options"
            :key="index"
            :class="{ 'selected' : selectValue == item.command}"
            class="xr-radio-menu-item"
            @click="selectClick(item)">
            <div class="mark" />{{ item.label }}
          </div>
          <div class="xr-radio-menu-default">
            <span>
              <el-checkbox
                v-show="showDefault"
                v-model="isChecked"
                :true-label="1"
                :false-label="0"
                @change="$emit('update:isDefault',isChecked)">保存为默认值</el-checkbox>
            </span>
            <el-button
              type="primary"
              @click="confirm">确定</el-button>
          </div>
        </div>
      </div>
      <slot
        slot="reference"
        name="reference" />
    </el-popover>
    <!-- 员工部门选择 -->
    <wk-dep-user-dialog
      :user-value.sync="users"
      :dep-value.sync="strucs"
      :visible.sync="membersDepVisible"
      :sub-dept-contain.sync="isNeedChild"
      :props="{
        showSubDeptContain: true
      }"
      @click.native.stop
      @close="membersDepCancel"
      @change="membersDepSelect"
    />
  </div>
</template>
<script type="text/javascript">

import { objDeepCopy } from '../../../utils'
export default {
  name: 'XrRadioMenu',
  components: {
    WkDepUserDialog: () => import('@/components/NewCom/WkUserDialogSelect/Dialog')
  },
  props: {
    width: {
      type: [String, Number],
      default: 200
    },
    showDefault: {
      type: Boolean,
      default: true
    },
    isDefault: {
      type: [String, Number],
      default: 0
    },
    options: Array, // {command label}
    value: [String, Number],
    // 编辑时 -- 用户默认勾选的数据
    userCheckedData: {
      type: Array,
      default: () => {
        return []
      }
    },
    // 编辑时 -- 部门默认勾选的数据
    depCheckedData: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  data() {
    return {
      popverVisible: false,
      selectItem: {},
      selectValue: '',
      isChecked: 0, // 是否为默认值
      // 自定义
      membersDepVisible: false,
      users: [], // 员工
      strucs: [], // 部门
      selectUsers: [], // 选择的员工  部门
      selectStrucs: [],
      isNeedChild: 0
    }
  },
  computed: {
  }, // 时间类型选择
  watch: {
    popverVisible(val) {
      if (val) {
        this.selectValue = this.value
        this.selectUsers = objDeepCopy(this.userCheckedData || [])
        this.selectStrucs = objDeepCopy(this.depCheckedData || [])
        this.users = objDeepCopy((this.userCheckedData || []).map(item => item.userId))
        this.strucs = objDeepCopy((this.depCheckedData || []).map(item => item.deptId))
        this.isChecked = this.isDefault
      }
    }
  },
  mounted() {
  },
  methods: {
    /**
     * 员工、部门选择取消
     */
    membersDepCancel() {
      if (this.selectValue != 'custom') {
        this.users = []
        this.strucs = []
      }
      this.popverVisible = true
    },
    /**
     * 员工、部门选择确定
     */
    membersDepSelect(userIds, deptIds, users, strucs) {
      if (!users.length && !strucs.length) {
        this.selectValue = 1
        this.users = []
        this.strucs = []
        this.selectUsers = []
        this.selectStrucs = []
      } else {
        this.selectValue = 'custom'
        this.selectUsers = users.map(item => { return { userId: item.userId, realname: this.$getUserName(item) } })
        this.selectStrucs = strucs.map(item => { return { name: this.$getDeptName(item), deptId: item.deptId } })
      }
    },
    // 类型选择
    selectClick(item) {
      this.isChecked = 0
      this.$emit('update:isDefault', this.isChecked)
      if (item.command == 'custom') {
        this.membersDepVisible = true
      } else {
        this.selectValue = item.command
      }
    },
    confirm() {
      this.$emit('input', this.selectValue)
      if (this.selectValue == 'custom') {
        this.$emit(
          'select',
          this.selectValue,
          {
            users: objDeepCopy(this.selectUsers),
            strucs: objDeepCopy(this.selectStrucs),
            isNeedChild: this.isNeedChild
          }
        )
      } else {
        this.$emit('select', this.selectValue)
      }
      this.popverVisible = false
    }
  }
}
</script>
<style lang="scss" scoped>
.xr-radio-menu {
  .xr-radio-menu__content {
    max-height: 250px;
    overflow-y: auto;

    .xr-radio-menu-item {
      position: relative;
      box-sizing: border-box;
      height: $--select-option-height;
      padding: 0 20px;
      overflow: hidden;
      font-size: $--select-font-size;
      line-height: $--select-option-height;
      color: $--select-option-color;
      text-overflow: ellipsis;
      white-space: nowrap;
      cursor: pointer;

      .el-input {
        margin-bottom: 5px;
      }
    }

    .xr-radio-menu-default {
      display: flex;
      justify-content: space-between;
      padding: 8px 16px;
      line-height: 32px;
      border-top: 1px solid $--border-color-base;
    }

    .selected {
      font-weight: bold;
      color: $--select-option-selected-font-color;
      background-color: $--select-option-selected-background;
      box-shadow: $--select-option-selected-box-shadow;
    }

    .xr-radio-menu-item:hover {
      background-color: $--select-option-hover-background;
    }
  }
}
</style>
