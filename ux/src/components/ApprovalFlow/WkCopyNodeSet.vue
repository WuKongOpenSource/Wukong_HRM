<template>
  <el-drawer
    :visible="visible"
    :with-header="false"
    size="500px"
    title="我是标题"
    append-to-body
    @close="close">
    <flexbox v-if="editNode" class="drawer-header">
      <div class="edit-title">
        <el-input
          v-if="isNameEdit"
          v-model="editNode.name"
          maxlength="20"
          @blur="nameInputBlur" />
        <template v-else>
          <span class="title" @click="isNameEdit = true">{{ editNode.name }}</span>
          <i class="wk wk-edit" @click="isNameEdit = true" />
        </template>
      </div>
      <i
        class="el-icon-close "
        @click="close" />
    </flexbox>
    <el-tabs
      v-if="tabsShow"
      v-model="activeTab"
      @tab-click="activeTabClick">
      <el-tab-pane
        v-for="(item, index) in tabs"
        :key="index"
        :label="item.label"
        :name="item.name" />
    </el-tabs>

    <div
      v-if="activeTab === 'base' && editNode"
      class="drawer-body"
      :class="{'is-tab': tabsShow}">
      <div class="set-copy">
        <div class="item">
          <div class="item-title">
            抄送人
          </div>
          <div class="item-body">
            <el-tag
              v-for="(user, index) in editNode.userList"
              :key="user.userId"
              size="medium"
              disable-transitions
              closable
              type="info"
              @close="handleRemoveUser(index)">
              {{ user.realname }}
            </el-tag>
            <el-button
              type="text"
              icon="el-icon-circle-plus"
              class="add-btn"
              @click="handleToChooseUser()">
              添加成员
            </el-button>

            <div class="section-handle">
              <p class="section-handle-title">指定上级</p>
              <el-select
                v-model="editNode.parentLevelList"
                multiple
                style="width: 400px;"
                @change="setContent">
                <el-option
                  v-for="item in sendLevelOption"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value" />
              </el-select>
            </div>

            <div class="section-handle">
              <p class="section-handle-title">指定角色</p>
              <role-employee-select
                ref="roleSelect"
                v-model="editNode.roleIdList"
                multiple
                :props="{
                  onlyShowRole: true,
                  showCustomRoles: true
                }"
                clearable
                style="width: 400px;"
                @change="roleSelectChange"
              />
            </div>

            <div class="checkbox-wrapper">
              <el-checkbox
                v-model="editNode.isSelf"
                :true-label="1"
                :false-label="0"
                @change="setContent">
                抄送给发起人本人
              </el-checkbox>
            </div>

            <div
              class="checkbox-wrapper">
              <el-checkbox
                v-model="editNode.isAdd"
                :true-label="1"
                :false-label="0"
                @change="setContent">
                允许发起人添加抄送人
              </el-checkbox>
            </div>
          </div>
        </div>

        <wk-dep-user-dialog
          v-if="userDialogSelectVisible"
          :visible.sync="userDialogSelectVisible"
          :user-value.sync="userList"
          :props="{
            showDept: false
          }"
          @change="selectUserChange" />
      </div>
    </div>

    <div
      v-if="activeTab === 'field' && editNode"
      v-loading="loading"
      class="drawer-body"
      :class="{'is-tab': tabsShow}">
      <field-custom-auth
        ref="fieldAuth"
        :auth-data="editNode.fieldAuthList"
        :field-list="fieldList"
        disabled-edit />
    </div>
    <div class="drawer-footer">
      <el-button
        v-debounce="handleConfirm"
        type="primary">保存</el-button>
      <el-button @click.native="close">取消</el-button>
    </div>
  </el-drawer>
</template>

<script>
import WkDepUserDialog from '@/components/NewCom/WkUserDialogSelect/Dialog'
import RoleEmployeeSelect from '@/views/admin/employeeDep/components/RoleEmployeeSelect'
import FieldCustomAuth from './FieldCustomAuth'

import { objDeepCopy } from '@/utils'
import NodeSetMixin from './NodeSet'

export default {
  // 条件配置
  name: 'WkCopyNodeSet',

  components: {
    WkDepUserDialog,
    RoleEmployeeSelect,
    FieldCustomAuth
  },

  mixins: [NodeSetMixin],

  inject: ['WkApproveFlow'],

  props: {
    visible: {
      type: Boolean,
      required: true,
      default: false
    },
    node: {
      type: Object,
      default() {
        return {}
      }
    },
    props: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },

  data() {
    return {
      editNode: null,
      isNameEdit: false,
      sendLevelOption: [],
      topLevelOption: [],
      levelOption: [], // sendLevelOption topLevelOption levelOption 仅第一级名称区别
      userDialogSelectVisible: false,
      userList: [],
      activeTab: 'base',
      fieldList: []
    }
  },

  computed: {
    tabsShow() {
      return this.tabs.length > 1
    },

    tabs() {
      // const tabs = [{ label: '基础设置', name: 'base' }]
      // if (this.props.nodeFieldSetShow) {
      //   tabs.push({ label: '字段权限', name: 'field' })
      // }
      return []
    }
  },

  watch: {
    visible(val) {
      if (val) {
        this.editNode = objDeepCopy(this.node)
      } else {
        this.editNode = null
      }
    }
  },

  created() {
    for (let index = 1; index <= 20; index++) {
      const label = `第${index}级上级`
      this.levelOption.push({
        label: label,
        value: index
      })
      if (index === 1) {
        this.sendLevelOption.push({
          label: '直属上级',
          value: index
        })
        this.topLevelOption.push({
          label: '最高级上级',
          value: index
        })
      } else {
        this.sendLevelOption.push({
          label: label,
          value: index
        })
        this.topLevelOption.push({
          label: label,
          value: index
        })
      }
    }
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 去选择员工
     */
    handleToChooseUser() {
      this.userList = this.editNode.userList?.map(user => user.userId)
      this.userDialogSelectVisible = true
    },

    setContent() {

    },
    /**
     * 输入失去焦点
     */
    nameInputBlur() {
      this.isNameEdit = false
      if (this.editNode.name === '') {
        this.editNode.name = '抄送节点'
      }
    },

    /**
     * 关闭
     */
    close() {
      this.$emit('update:visible', false)
    },

    /**
     * 保存
     */
    handleConfirm() {
      // 条件为0 报红
      this.editNode.isError = this.getWkNodeErrorStatus(this.editNode)
      for (const key in this.editNode) {
        if (key !== 'conditionList') {
          this.node[key] = this.editNode[key]
        }
      }

      const fieldAuthForm = this.$refs.fieldAuth
      if (fieldAuthForm) {
        const fieldAuthList = fieldAuthForm.getSaveForm()
        if (this.props.nodeFieldItemParamsFun) {
          fieldAuthList.forEach(item => {
            this.props.nodeFieldItemParamsFun(item)
          })
        }
        this.node.fieldAuthList = fieldAuthList
      }

      this.close()
    },

    /**
     * 选择员工
     * @param userIds
     * @param deptIds
     * @param userList
     * @param deptList
     */
    selectUserChange(userIds, deptIds, userList, deptList) {
      this.editNode.userList = userList
      this.setContent()
      this.userDialogSelectVisible = false
    },
    /**
     * 删除员工
     * @param index
     */
    handleRemoveUser(index) {
      this.editNode.userList.splice(index, 1)
      this.setContent()
      this.$forceUpdate()
    },

    /**
     * 角色选择
     */
    roleSelectChange() {
      this.$nextTick(() => {
        const arr = this.$refs.roleSelect.select.selected.map(o => {
          return { roleName: o.label }
        })
        this.$set(this.editNode, 'roleList', arr)
      })
    },

    /**
     * @description: tab点击
     * @return {*}
     */
    activeTabClick() {
      if (this.activeTab === 'field' && this.props.nodeFieldSetShow) {
        this.getFieldList()
      }
    },

    /**
     * @description: 获取字段
     * @return {*}
     */
    getFieldList() {
      this.loading = true
      this.props.nodeFieldSetRequestFun().then(res => {
        this.fieldList = res
        this.loading = false
      }).catch(() => {
        this.fieldList = []
        this.loading = false
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.drawer-header {
  height: 50px;
  padding: 0 15px;
  background: #f7f8fa;
  border-bottom: 1px solid $--border-color-base;

  .edit-title {
    width: 450px;
    margin-right: 10px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;

    .title {
      cursor: pointer;
    }

    .wk-edit {
      cursor: pointer;
    }
  }

  .el-icon-close {
    font-size: 24px;
    color: #909399;
    cursor: pointer;
  }

  .el-icon-close:hover {
    color: $--color-primary;
  }
}

::v-deep .el-tabs {
  padding: 0 8px;

  .el-tabs__header {
    margin-bottom: 0;
  }

  .el-tabs__nav-wrap::after {
    display: none;
  }
}

.drawer-body {
  height: calc(100% - 115px);
  overflow-y: auto;

  &.is-tab {
    height: calc(100% - 155px);
  }

  .item-body {
    margin-top: 8px;
  }

  .section {
    padding: 20px;
    border-bottom: 1px solid $--border-color-base;

    &__title {
      margin-bottom: 16px;
      font-weight: bold;
    }

    &-handle {
      position: relative;
      padding: 5px 0;
    }
  }
}

.drawer-footer {
  padding: 15px;
  text-align: right;
}

.set-copy {
  padding: 10px 20px 0;

  .el-tag {
    margin-right: 10px;
  }

  .checkbox-wrapper {
    margin-top: 10px;
  }

  .section-handle {
    margin-top: 10px;

    &-title {
      margin-bottom: 10px;
    }
  }

  .el-select {
    width: 220px;
  }
}
</style>
