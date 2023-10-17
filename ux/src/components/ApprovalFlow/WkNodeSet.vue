<template>
  <el-drawer
    :visible="visible"
    :with-header="false"
    size="500px"
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
      <div class="section">
        <el-radio-group
          v-model="editNode.examineType"
          @change="examineTypeChange">
          <el-radio
            v-for="(item, index) in examineTypeOptions"
            :key="index"
            :label="item.value"
            class="el-radio--type">{{ item.label }}</el-radio>
        </el-radio-group>
        <!-- 指定成员 -->
        <div v-if="editNode.examineType === 1" class="area-top">
          <div class="section-handle">
            <el-dropdown
              class="more-handle"
              @command="addCommandClick">
              <el-button type="primary">添加</el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="selectUser">选择员工</el-dropdown-item>
                <el-dropdown-item
                  v-if="props.examineNodeProps.addExternalUserShow"
                  command="external">选择外部联系人</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
            <span class="text-des">不能超过20人</span>
          </div>
          <div class="user-list-wrap">
            <el-tag
              v-for="(item, index) in editNode.memberUserList"
              :key="index"
              size="medium"
              disable-transitions
              closable
              type="info"
              class="user-item"
              @close="typeUserDelete(index)">
              {{ item.userId ? $getUserName(item) : item.outerUserEmail }}
            </el-tag>
          </div>
        </div>

        <!-- 上级 -->
        <div v-if="editNode.examineType === 2" class="area-top">
          <div class="section-handle">
            <span>发起人的</span>
            <el-select v-model="editNode.parentLevel">
              <el-option
                v-for="item in sendLevelOption"
                :key="item.value"
                :label="item.label"
                :value="item.value" />
            </el-select>
          </div>
          <div class="area-top wk-checkbox">
            <el-checkbox
              v-model="editNode.type"
              :true-label="1"
              :false-label="0" />
            <span class="wk-checkbox__label">找不到主管时，由上级主管代审批</span>
          </div>
        </div>

        <!-- 角色 -->
        <div v-if="editNode.examineType === 3" class="area-top">
          <role-employee-select
            ref="roleSelect"
            v-model="editNode.roleIdList"
            multiple
            :props="{
              onlyShowRole: true
            }"
            clearable
            style="width: 400px;"
            @change="roleSelectChange" />
        </div>

        <!-- 发起人自选 -->
        <div v-if="editNode.examineType === 4" class="area-top">
          <div class="section-handle">
            <el-select v-model="editNode.chooseType">
              <el-option :value="1" label="自选一人" />
              <el-option :value="2" label="自选多人" />
            </el-select>
          </div>
          <div class="area-top">
            <div class="section__title">选择范围</div>
            <div class="area-top">
              <el-select
                v-model="editNode.rangeType"
                @change="rangeTypeChange">
                <el-option :value="1" label="全公司" />
                <el-option :value="2" label="指定成员" />
                <el-option :value="3" label="指定角色" />
              </el-select>
              <template v-if="editNode.rangeType === 2">
                <el-button type="primary" @click="depUserViewDialogShow = true">添加员工</el-button>
              </template>
              <role-employee-select
                v-if="editNode.rangeType === 3"
                ref="roleSelect"
                v-model="editNode.roleIdList"
                multiple
                :props="{
                  onlyShowRole: true
                }"
                clearable
                @change="roleSelectChange" />
            </div>
            <div
              v-if="editNode.rangeType === 2 && editNode.userList.length > 0"
              class="user-list-wrap"
              style="margin-top: 8px;">
              <el-tag
                v-for="(item, index) in editNode.userList"
                :key="index"
                size="medium"
                disable-transitions
                closable
                type="info"
                class="user-item"
                @close="userDelete(index)">
                {{ $getUserName(item) }}
              </el-tag>
            </div>
          </div>
        </div>

        <!-- 连续多级上级 -->
        <div v-if="editNode.examineType === 5" class="area-top">
          <div class="section__title">审批终点</div>
          <el-radio-group
            v-model="editNode.type"
            class="el-radio-group--block"
            @change="endTypeChange">
            <el-radio :label="1" class="el-radio--block">
              <div>指定角色（连续多级上级须包含该角色）<role-employee-select
                ref="roleSelect"
                v-model="editNode.roleIdList"
                :props="{
                  onlyShowRole: true
                }"
                multiple
                clearable
                @change="roleSelectChange" /></div>
            </el-radio>
            <div
              class="area-top wk-checkbox"
              style=" margin-left: 20px;line-height: 34px;">
              <el-checkbox
                v-model="editNode.overType"
                :true-label="1"
                :false-label="0" />
              <span class="wk-checkbox__label" style="margin-right: 8px;font-size: 13px;">同时不超过发起人向上的</span>
              <el-select v-model="editNode.parentLevel">
                <el-option
                  v-for="item in levelOption"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value" />
              </el-select>
            </div>
            <el-radio :label="2" class="el-radio--block area-top"><span>组织架构中由上至下的</span><el-select
              v-model="editNode.tempParentLevel"
              style="margin-left: 5px;">
              <el-option
                v-for="item in topLevelOption"
                :key="item.value"
                :label="item.label"
                :value="item.value" />
            </el-select></el-radio>
          </el-radio-group>
        </div>

        <!-- 关联人员字段 -->
        <div v-if="editNode.examineType === 8" class="area-top">
          <div class="text-des">模板中必填的人员字段，可关联为审批人</div>
          <el-select
            v-model="editNode.fieldName"
            placeholder="请选择人员字段"
            style="margin-top: 5px;"
            @change="memberControlChange">
            <el-option
              v-for="(item, index) in memberControlList"
              :key="index"
              :label="item.name"
              :value="item.fieldName" />
          </el-select>
          <flexbox
            style="margin-top: 10px;">
            <el-checkbox v-model="editNode.parentLevelCheck" @change="parentLevelCheckClick">成员的</el-checkbox>
            <el-select
              v-model="editNode.parentLevel"
              style="margin-left: 3px;">
              <el-option
                v-for="item in sendLevelOption"
                :key="item.value"
                :label="item.label"
                :value="item.value" />
            </el-select>
          </flexbox>
        </div>
      </div>

      <!-- 审批方式-->
      <div
        v-if="waySectionShow"
        class="section">
        <div class="section__title">
          <span>多人审批时采用的审批方式</span>
        </div>

        <div class="section__content">
          <el-radio-group v-model="editNode.type">
            <el-radio
              v-if="editNode.examineType !== 3 && editNode.examineType !== 8"
              :label="1"
              class="el-radio--block">依次审批</el-radio>
            <el-radio :label="2" class="el-radio--block">会签（需所有审批人同意）</el-radio>
            <el-radio :label="3" class="el-radio--block">或签（一名审批人同意或拒绝即可）</el-radio>
          </el-radio-group>
        </div>
      </div>

      <!-- 审批为空 -->
      <!-- <div
        class="section">
        <div class="section__title">
          <span>审批人为空时</span><i
            class="wk wk-icon-fill-help wk-help-tips"
            data-type="26"
            data-id="235" />
        </div>

        <div class="section__content">
          <el-radio-group v-model="editNode.examineErrorHandling">
            <el-radio :label="1" class="el-radio--block">自动通过</el-radio>
            <el-radio :label="2" class="el-radio--block">自动转交管理员</el-radio>
          </el-radio-group>
        </div>
      </div> -->
    </div>

    <div
      v-if="activeTab === 'field' && editNode"
      v-loading="loading"
      class="drawer-body"
      :class="{'is-tab': tabsShow}">
      <field-custom-auth
        ref="fieldAuth"
        :auth-data="editNode.fieldAuthList"
        :field-list="fieldList" />
    </div>
    <div class="drawer-footer">
      <el-button
        v-debounce="handleConfirm"
        type="primary">保存</el-button>
      <el-button @click.native="close">取消</el-button>
    </div>

    <wk-dep-user-dialog
      v-if="depUserViewDialogShow"
      :user-value="selectUserList"
      :visible.sync="depUserViewDialogShow"
      :props="{
        showDisableUser: false,
        showDept: false,
      }"
      @change="selectUserChange"
    />

    <!-- 选择外部联系人 -->
    <el-dialog
      v-if="externalDialogShow"
      :visible.sync="externalDialogShow"
      :close-on-click-modal="false"
      :append-to-body="true"
      title="外部联系人"
      width="30%">
      <span>请输入外部联系人邮箱</span>
      <el-input
        v-model="outerUserEmail"
        style="margin-top: 10px;" />
      <span slot="footer" class="dialog-footer">
        <el-button @click="externalDialogShow = false">取消</el-button>
        <el-button type="primary" @click="verifyEmail">确定</el-button>
      </span>
    </el-dialog>
  </el-drawer>
</template>

<script>
import WkDepUserDialog from '@/components/NewCom/WkUserDialogSelect/Dialog'
import RoleEmployeeSelect from '@/views/admin/employeeDep/components/RoleEmployeeSelect'
import FieldCustomAuth from './FieldCustomAuth'

import { objDeepCopy } from '@/utils'
import NodeSetMixin from './NodeSet'
import { regexIsCRMEmail } from '@/utils'

export default {
  // 条件配置
  name: 'WkNodeSet',

  components: {
    WkDepUserDialog,
    RoleEmployeeSelect,
    FieldCustomAuth
  },

  mixins: [NodeSetMixin],

  inject: ['memberControlFun'],

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
      loading: false,
      editNode: null,
      isNameEdit: false,
      sendLevelOption: [],
      topLevelOption: [],
      levelOption: [], // sendLevelOption topLevelOption levelOption 仅第一级名称区别
      depUserViewDialogShow: false,
      activeTab: 'base',
      fieldList: [],
      selectUserList: [], // 已选员工数据
      // 外部联系人
      externalDialogShow: false,
      outerUserEmail: ''
    }
  },

  computed: {
    // 类型
    examineTypeOptions() {
      const defaultOptions = [{
        label: '指定成员',
        value: 1
      }, {
        label: '上级',
        value: 2
      }, {
        label: '角色',
        value: 3
      }, {
        label: '发起人自选',
        value: 4
      }, {
        label: '连续多级上级',
        value: 5
      }, {
        label: '关联人员字段',
        value: 8
      }]
      return this.props.examineTypeFilterFun ? this.props.examineTypeFilterFun(defaultOptions) : defaultOptions
    },
    // 审批方式
    waySectionShow() {
      return this.getWkWayShowStatus(this.editNode) || this.memberControlSectionShow
    },

    // 关联人员字段 人员字段是否展示多选方式
    memberControlSectionShow() {
      const { fieldName, examineType } = this.editNode
      if (examineType === 8 && fieldName) {
        const selectField = this.memberControlList.filter(item => item.fieldName == fieldName)[0]
        return selectField.isMulti === 1
      }
      return false
    },

    // 选择控件人员列表
    memberControlList() {
      return this.memberControlFun()
    },

    tabsShow() {
      return this.tabs.length > 1
    },

    tabs() {
      const tabs = [{ label: '基础设置', name: 'base' }]
      if (this.props.nodeFieldSetShow) {
        tabs.push({ label: '字段权限', name: 'field' })
      }
      return tabs
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
    memberControlChange() {
      if (this.memberControlSectionShow) {
        this.$set(this.editNode, 'type', 2)
      } else {
        this.$set(this.editNode, 'type', 1)
      }
    },

    /**
     * 输入失去焦点
     */
    nameInputBlur() {
      this.isNameEdit = false
      if (this.editNode.name === '') {
        this.editNode.name = '审批人'
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

      // eslint-disable-next-line no-unused-vars
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
     * 审批类型change
     */
    examineTypeChange() {
      this.editNode.parentLevel = 1
      this.$set(this.editNode, 'userList', [])
      this.$set(this.editNode, 'memberUserList', [])
      // 多人审批类型 1 依次审批 2 会签 3 或签
      // 当审批类型为主管时 找不到上级时，是否由上一级上级代审批 0 否 1 是
      // 当审批类型为连续多级主管时 1 指定角色 2 组织架构的最上级
      // 当审批类型为发起人自选时 1 全公司 2 指定成员 3 指定角色
      if (this.editNode.examineType === 1) { // 指定成员
        this.editNode.type = 1 // 多人审批类型
      } else if (this.editNode.examineType === 2) { // 上级
        this.editNode.type = 0 // 当审批类型为主管时
      } else if (this.editNode.examineType === 3) { // 角色
        this.editNode.type = 2 // 多人审批类型
      } else if (this.editNode.examineType === 4) { // 发起人自选
        if (!this.editNode.rangeType) {
          this.editNode.rangeType = 1
        }
        this.editNode.chooseType = 1
        this.editNode.type = 1 // 多人审批类型
      } else if (this.editNode.examineType === 5) { // 连续多级上级
        this.editNode.type = 1 // 指定角色
      }

      // this.editNode.examineErrorHandling = 2
      this.editNode.roleIdList = []
      this.editNode.chooseType = 1
    },

    /**
     * 选择员工change
     */
    selectUserChange(userIds, _, users) {
      // 指定成员
      if (this.editNode.examineType === 1) {
        const hasUserIds = []
        const hasEmail = []
        this.editNode.memberUserList.forEach(element => {
          if (element.userId) {
            hasUserIds.push(element.userId)
          } else {
            hasEmail.push(element.outerUserEmail)
          }
        })
        users.forEach(item => {
          // 不能超20
          if (!hasUserIds.includes(item.userId) && (hasUserIds.length + hasEmail.length) < 20) {
            this.editNode.memberUserList.push(item)
          }
        })
        this.editNode.type = 1 // 重置多人审批状态
      } else {
        if (users.length > 0) {
          this.editNode.userList = users.length > 20 ? users.slice(0, 20) : users
        } else {
          this.editNode.userList = []
        }
      }
    },

    /**
     * 用户删除
     */
    userDelete(index) {
      this.editNode.userList.splice(index, 1)
    },

    /**
     * @description: 指定成员删除
     * @param {*} index
     * @return {*}
     */
    typeUserDelete(index) {
      this.editNode.memberUserList.splice(index, 1)
    },

    /**
     * 发起人自选 范围change
     */
    rangeTypeChange() {
      this.editNode.userList = []
      this.editNode.roleIdList = []
      this.editNode.type = 1
    },

    /**
     * 审批终点 类型change
     */
    endTypeChange() {
      this.editNode.overType = 0
    },

    /**
     * 角色选择
     */
    roleSelectChange() {
      this.$nextTick(() => {
        const selecteds = this.$refs.roleSelect.select.selected
        this.$set(this.editNode, 'roleList', selecteds.map(item => {
          return {
            roleName: item.$props.label
          }
        }))
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
    },

    /**
     * @description: 添加员工
     * @return {*}
     */
    addCommandClick(command) {
      if (command === 'selectUser') {
        // 指定成员
        if (this.editNode.examineType === 1) {
          this.selectUserList = (this.editNode.memberUserList || []).filter(item => !!item.userId).map(item => item.userId)
        } else {
          this.selectUserList = (this.editNode.userList || []).map(item => item.userId)
        }
        this.depUserViewDialogShow = true
      } else if (command === 'external') {
        this.externalDialogShow = true
      }
    },

    /**
     * 验证邮箱
     */
    verifyEmail() {
      if (regexIsCRMEmail(this.outerUserEmail)) {
        const hasUserIds = []
        const hasEmail = []
        this.editNode.memberUserList.forEach(element => {
          if (element.userId) {
            hasUserIds.push(element.userId)
          } else {
            hasEmail.push(element.outerUserEmail)
          }
        })
        if (!hasEmail.includes(this.outerUserEmail) && (hasUserIds.length + hasEmail.length) < 20) {
          this.editNode.memberUserList.push({
            outerUserEmail: this.outerUserEmail
          })
        }
        this.outerUserEmail = null
        this.externalDialogShow = false
      } else {
        this.$message.error('邮箱格式有误')
      }
    },

    /**
     * @description: 关联人员层级
     * @return {*}
     */
    parentLevelCheckClick() {
      if (this.editNode.parentLevelCheck && !this.editNode.parentLevel) {
        this.editNode.parentLevel = this.sendLevelOption[0]?.value
      }
    }
  }
}
</script>

<style lang="scss">
.el-radio-group--block {
  display: block;
}

.el-radio--block {
  display: block;
  margin-right: 0;

  .el-radio__label {
    display: inline-block;
  }
}
</style>
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

      .text-des {
        margin-top: 10px;
        margin-left: 8px;
        color: $--color-text-secondary;
      }
    }
  }

  .el-radio--type {
    width: 25%;
    margin-bottom: 20px;
    font-size: 15px;
  }

  .el-radio--block {
    display: block;
    margin-bottom: 10px;
  }

  .area-top {
    margin-top: 8px;
  }

  .wk-checkbox {
    line-height: 25px;

    &__label {
      margin-left: 8px;
    }
  }

  .wk-help-tips {
    margin-left: 5px;
  }

  // 用户列表
  .user-list-wrap {
    .user-item + .user-item {
      margin-left: 8px;
    }
  }

  .mutil-line-tips {
    .tips-title {
      font-weight: bold;
    }

    .tips-line {
      margin-top: 3px;
    }
  }
}

.drawer-footer {
  padding: 15px;
  text-align: right;
}

</style>
