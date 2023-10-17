<template>
  <div v-if="data && data.length === 0" class="tips">审核人为空，审核将自动通过。</div>
  <el-timeline v-else>
    <el-timeline-item
      v-if="(!(!approverEditAuth&& !carbonCopyEditAuth)) && !isEdit"
      class="wk-approval-item">
      <template slot="dot">
        <el-dropdown
          placement="right-start"
          @command="addNode('first',$event)">
          <i class="wk wk-icon-create" />
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item v-if="approverEditAuth" command="examineNode">
              <i class="wk wk-approve1" />
              审批节点
            </el-dropdown-item>
            <el-dropdown-item v-if="carbonCopyEditAuth" command="carbonNode">
              <i class="wk wk-airplane" />
              抄送节点
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </template>
    </el-timeline-item>
    <el-timeline-item
      v-for="(flow, flowIndex) in data"
      :key="flowIndex"
      class="wk-approval-item">
      <template v-if="(!(!approverEditAuth && !carbonCopyEditAuth)) && !isEdit" slot="dot">
        <el-dropdown
          placement="right-start"
          @command="addNode(flowIndex,$event)">
          <i class="wk wk-icon-create" />
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item v-if="approverEditAuth" command="examineNode">
              <i class="wk wk-approve1" />
              审批节点
            </el-dropdown-item>
            <el-dropdown-item v-if="carbonCopyEditAuth" command="carbonNode">
              <i class="wk wk-airplane" />
              抄送节点
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </template>
      <div class="wk-approval-item__header">{{ flow.name }}
        <span>{{ getExamineTypeName(flow.examineType) }}{{ flow.chooseType != 1 ? flow.userList && flow.userList == 1 ? getWayTypeName(flow.type) : '' : '' }}</span>
      </div>
      <div class="wk-approval-item__body">
        <flexbox
          class="user-wrap"
          wrap="wrap">
          <el-tooltip
            v-for="(item, index) in flow.userList"
            :key="index"
            class="item"
            effect="dark"
            :offset="10"
            :content="$getUserName(item) || item.email || item.outerUserEmail"
            placement="top">
            <div class="user">
              <xr-avatar
                :name="$getUserName(item) || item.email || item.outerUserEmail"
                :size="40"
                :src="item.img" />
              <div class="user__name">{{ $getUserName(item) || item.email || item.outerUserEmail }}</div>
              <i
                v-if="(nodeUserDelAuth(flow) || item.isDel) && !isEdit"
                class="user__delete el-icon-close"
                @click="deleteUserClick(index, flow.userList,flowIndex)" />
            </div>
          </el-tooltip>
          <div
            v-if="nodeAddUserAuth(flow) && !isEdit"
            class="user">
            <el-dropdown
              placement="right"
              @command="addingMode(flow,$event)">
              <span class="el-dropdown-link">
                <el-button
                  class="user__img"
                  icon="el-icon-plus"
                  style="margin-bottom: 8px;"
                  circle />
                <div class="user__name">{{ getSelfSelectName(flow) }}</div>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="selectUser">选择员工</el-dropdown-item>
                <el-dropdown-item
                  v-if="!isStageFlow"
                  command="external">选择外部联系人</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </flexbox>

        <!-- 未找到审批人样式 -->
        <div
          v-if="showAnomalyText(flow)"
          class="examine-error">
          <i class="el-icon-warning" />
          {{ showAnomalyText(flow) }}
        </div>
      </div>
    </el-timeline-item>

    <wk-dep-user-dialog
      v-if="depUserViewDialogShow"
      :props="{
        showUser: !userOptions,
        showDept: false,
        showDisableUser: !!userOptions,
        disableUserList: userOptions,
        disableUserLabel: '员工'
      }"
      radio
      :visible.sync="depUserViewDialogShow"
      @change="selectUserChange"
    />

    <el-dialog
      v-if="externalDialogShow"
      :visible.sync="externalDialogShow"
      :close-on-click-modal="false"
      :append-to-body="true"
      title="外部联系人"
      width="400px">
      <span>请输入外部联系人邮箱</span>
      <el-input
        v-model="email"
        style="margin-top: 10px;" />
      <span slot="footer" class="dialog-footer">
        <el-button @click="externalDialogShow = false">取消</el-button>
        <el-button type="primary" @click="verifyEmail">确定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      v-if="addexamineNodeShow"
      :visible.sync="addexamineNodeShow"
      :close-on-click-modal="false"
      :append-to-body="true"
      :title="`增加${addexamineNodeTitle}节点`"
      width="500px">
      <el-form
        ref="examineForm"
        :model="examineNodeData"
        class="examine-node-form"
        label-position="top">
        <el-form-item
          :label="addexamineNodeTitle + '人'"
          :rules="{required:true}">
          <flexbox
            class="user-wrap"
            wrap="wrap">
            <div
              v-for="(item, index) in examineNodeData.userList"
              :key="index"
              class="user">
              <xr-avatar
                :name="$getUserName(item) || item.email || item.outerUserEmail"
                :size="40"
                :src="item.img"
              />
              <div class="user__name">{{ $getUserName(item) || item.email || item.outerUserEmail }}</div>
              <i class="user__delete el-icon-close" @click="deleteUserClick(index, examineNodeData.userList)" />
            </div>
            <div
              v-if="examineNodeData.userList.length === 0 || examineNodeData.userList.length < 20"
              class="user">
              <el-dropdown
                placement="right"
                @command="addingMode(examineNodeData,$event)">
                <span class="el-dropdown-link">
                  <el-button
                    class="user__img"
                    icon="el-icon-plus"
                    style="margin-bottom: 8px;"
                    circle />
                  <div class="user__name">{{ getSelfSelectName(examineNodeData.userList) }}</div>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="selectUser">选择员工</el-dropdown-item>
                  <el-dropdown-item command="external">选择外部联系人</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </flexbox>
        </el-form-item>
        <el-form-item
          v-if="examineNodeData.userList.length > 1 && examineNodeData.examineType != 7"
          prop="type"
          label="多人审批方式"
          :rules="{required:true,message:'请选择多人审批方式',trigger: 'blur'}">
          <el-radio v-model="examineNodeData.type" label="1">依次审批</el-radio>
          <el-radio v-model="examineNodeData.type" label="2">会签</el-radio>
          <el-radio v-model="examineNodeData.type" label="3">或签</el-radio>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addexamineNodeShow = false">取消</el-button>
        <el-button type="primary" @click="saveExamineNode">确定</el-button>
      </span>
    </el-dialog>
  </el-timeline>
</template>

<script>
import WkDepUserDialog from '@/components/NewCom/WkUserDialogSelect/Dialog'

import { examineTypeObj, wayTypeObj } from '@/components/ApprovalFlow/nodeModel'
import Nzhcn from 'nzh/cn'
import { regexIsCRMEmail, objDeepCopy } from '@/utils'

export default {
  // 审批流的应用参数
  name: 'WkApprovalFlowApply',

  components: {
    WkDepUserDialog
  },

  filters: {
    step: function(index) {
      return '第' + Nzhcn.encodeS(index) + '级'
    }
  },

  props: {
    data: Array,
    approverEditAuth: Boolean, // 固定审批人是否可修改
    carbonCopyEditAuth: Boolean, // 固定抄送人是否可修改
    examineAdvancedSetting: { // 审批高级配置
      type: Object,
      default: () => { return {} }
    },
    isStageFlow: { // 是否为阶段流程
      type: Boolean,
      default: false
    },
    isEdit: { // 是否可编辑
      type: Boolean,
      default: false
    }
  },

  data() {
    return {
      userOptions: [],
      editItem: null,
      depUserViewDialogShow: false,
      externalDialogShow: false, // 添加外部联系人
      addexamineNodeShow: false, // 添加节点
      addexamineNodeTitle: '审批',

      email: '',
      examineNodeData: {
        userList: []
      }, // 添加审批节点信息
      examineNodeIndex: null // 添加审批位置
    }
  },

  computed: {
    // 节点员工删除权限
    nodeUserDelAuth() {
      return function(flow) {
        /**
         * examineType 0 条件审批 1 指定成员 2 主管 3 角色 4 发起人自选 5 连续多级主管 6抄送审批节点 7抄送节点
         * carbonCopyEditAuth  固定抄送人是否可修改
         * approverEditAuth 固定审批人是否可修改
         */
        if (flow.examineType === 4 ||
            (flow.examineType === 7 && this.carbonCopyEditAuth) ||
            ((flow.examineType === 1 || flow.examineType === 2 || flow.examineType === 3 || flow.examineType === 5) &&
            this.approverEditAuth)) {
          return true
        } else {
          return false
        }
      }
    },

    // 节点添加员工权限
    nodeAddUserAuth() {
      return function(flow) {
        /**
         * examineType 0 条件审批 1 指定成员 2 主管 3 角色 4 发起人自选 5 连续多级主管 6抄送审批节点 7抄送节点
         * chooseType 只有发起人自选需要 1 自选一人 2 自选多人
         * isAdd 抄送人是否可添加
         * carbonCopyEditAuth  固定抄送人是否可修改
         * approverEditAuth 固定审批人是否可修改
         */
        if ((flow.chooseType === 1 && flow.userList.length === 0) ||
            (flow.examineType === 4 && flow.chooseType !== 1) ||
            (flow.chooseType === 2 && flow.userList.length < 20) ||
            ((flow.examineType === 7 && this.carbonCopyEditAuth) || (flow.examineType === 7 && flow.isAdd)) ||
            (flow.examineType === 5 && this.approverEditAuth) ||
            ((flow.examineType === 1 || flow.examineType === 2 || flow.examineType === 3 || flow.examineType === 5) && this.approverEditAuth)) {
          return true
        } else {
          return false
        }
      }
    }
  },

  watch: {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 获取审批类型名称
     */
    getExamineTypeName(examineType) {
      return examineTypeObj[examineType]
    },

    /**
     * 获取或并方式
     */
    getWayTypeName(type) {
      return wayTypeObj[type]
    },

    /**
     * 选择添加方式
     */
    addingMode(item, command) {
      if (command == 'selectUser') { // 选择员工
        this.selectUserClick(item)
      } else if (command == 'external') { // 选择外部联系人
        this.externalDialogShow = true
        this.email = null
        this.editItem = item
      }
    },

    /**
     * 选择人员
     */
    selectUserClick(item) {
      this.userOptions = item.rangeType === 1 ||
      !item.hasOwnProperty('rangeType') ||
      ((item.examineType === 1 || item.examineType === 2 || item.examineType === 3 || item.examineType === 5) && this.approverEditAuth) ||
      (item.examineType === 7 && this.carbonCopyEditAuth)
        ? null : item.finalUserList

      this.editItem = item
      this.depUserViewDialogShow = true
    },

    /**
     * 选择人员change
     */
    selectUserChange(usersIds, _, users) {
      const user = objDeepCopy(users[0])
      if (users.length) {
        const userIds = this.editItem.userList.map(item => item.userId)
        // const userIds = this.editItem.values.map(item => item.userId)
        if (!userIds.includes(user.userId)) {
          if (this.editItem.examineType == 7) {
            user.isDel = true
          }
          this.editItem.userList.push(user)
        }
      }
    },

    /**
     * 获取自选名称
     */
    getSelfSelectName(data) {
      if (data.chooseType === 1) {
        return '选择人员'
      } else if (data.chooseType === 2) {
        return {
          2: '多人会签',
          3: '多人或签'
        }[data.type] || '选择人员'
      }
    },

    /**
     * 删除user
     */
    deleteUserClick(index, list, dataIndex) {
      list.splice(index, 1)
      if (!this.data[dataIndex].hasOwnProperty('flowId') && !this.data[dataIndex].userList.length) {
        this.data.splice(dataIndex, 1)
      } else if (!this.data[dataIndex].hasOwnProperty('flowId') && this.data[dataIndex].userList.length == 1) {
        delete this.data[dataIndex].type
      }
    },

    /**
     * 验证邮箱
     */
    verifyEmail() {
      if (regexIsCRMEmail(this.email)) {
        const email = this.editItem.userList.map(item => item.email)
        if (!email.includes(this.email)) {
          const emailUser = {
            email: this.email
          }
          if (this.editItem.examineType == 7) {
            emailUser.isDel = true
          }

          this.editItem.userList.push(emailUser)
          this.email = null
          this.externalDialogShow = false
        } else {
          this.$message.error('请勿重复添加')
        }
      } else {
        this.$message.error('邮箱格式有误')
      }
    },

    /**
     * 添加审批节点
     */
    addNode(index, command) {
      this.examineNodeIndex = index
      this.examineNodeData = {
        userList: []
      }
      if (command == 'examineNode') { // 审批节点
        this.examineNodeData.examineType = 4
        this.addexamineNodeShow = true
        this.addexamineNodeTitle = '审批'
      } else if (command == 'carbonNode') { // 抄送节点
        this.examineNodeData.examineType = 7
        this.addexamineNodeShow = true
        this.addexamineNodeTitle = '抄送'
      }
    },

    /**
     * 添加节点保存
     */
    saveExamineNode() {
      this.$refs.examineForm.validate(valid => {
        if (this.examineNodeData.userList.length && valid) {
          this.examineNodeData.name = '自定义' + this.addexamineNodeTitle + '节点'
          if (this.examineNodeIndex == 'first') {
            this.data.unshift(this.examineNodeData)
          } else {
            this.data.splice(this.examineNodeIndex + 1, 0, this.examineNodeData)
          }
          this.addexamineNodeShow = false
        } else {
          this.$message.error('请完善基本信息')
        }
      })
    },

    // 展示异常处理
    showAnomalyText(flow) {
      /**
         * examineType 0 条件 1 指定成员 2 主管 3 角色 4 发起人自选 5 连续多级主管 6抄送审批节点 7抄送节点
         */
      if (!Object.keys(flow).length || flow.examineType == 4) return ''

      // if (flow.examineType == 7 && !flow.userList.length) {
      //   if (this.examineAdvancedSetting.nodeHandleType == 1) {
      //     return '未找到抄送人'
      //   } else if (this.examineAdvancedSetting.nodeHandleType == 2) {
      //     return '未找到抄送人，将自动抄送给' + this.examineAdvancedSetting.nodeHandleUser.map(item => this.$getUserName(item)).join(',')
      //   }
      // }

      // if (((flow.examineType == 2 &&
      //     !flow.userList.length) ||
      //     (flow.examineType == 3 &&
      //       !flow.userList.length) ||
      //       (flow.examineType == 5 &&
      //       !flow.userList.length))) {
      if (flow.examineType != 7 && !flow.userList.length) {
        if (this.examineAdvancedSetting.nodeHandleType == 1) {
          return '未找到审批人，将自动同意'
        } else if (this.examineAdvancedSetting.nodeHandleType == 2) {
          return '未找到审批人，将自动转交给' + this.examineAdvancedSetting.nodeHandleUser.map(item => this.$getUserName(item)).join(',')
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.tips {
  color: $--color-text-regular;
}

.wk-approval-item {
  &__header {
    span {
      display: inline-block;
      padding: 0 8px;
      font-size: 12px;
      line-height: 18px;
      color: white;
      background: $--color-danger;
      border-radius: 9px;
      transform: scale(0.8, 0.8);
    }
  }

  &__body {
    margin-top: $--interval-base;

    .user-wrap {
      padding: 0 $--interval-base;
      text-align: center;

      .user {
        position: relative;
        width: 65px;
        padding: $--interval-base #{$--interval-base * 3} $--interval-base 0;

        .user__img {
          width: 40px;
          height: 40px;
          border-radius: 20px;
        }

        .user__name {
          margin-top: 4px;
          margin-bottom: 4px;
          overflow: hidden;
          font-size: 12px;
          color: $--color-text-secondary;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        &__delete {
          position: absolute;
          top: 12px;
          right: 25px;
          font-size: 12px;
          color: white;
          cursor: pointer;
          visibility: hidden;
          background-color: #b2b2b2;
          border-radius: 6px;

          &:hover {
            background-color: $--color-primary;
          }
        }

        &:hover {
          .user__delete {
            visibility: visible;
          }
        }
      }
    }

    .examine-error {
      padding: 5px;
      background-color: $--color-n20;

      i {
        margin-right: 5px;
        color: #f56c6c;
      }
    }
  }

  ::v-deep .el-timeline-item__dot {
    top: -3px;
    left: -2px;
    z-index: 2;

    .wk.wk-icon-create {
      color: $--color-n100;
      cursor: pointer;
    }
  }

  ::v-deep .el-timeline-item__tail {
    z-index: 1;
  }
}

.examine-node-form {
  ::v-deep .el-form-item__content {
    line-height: unset;
  }

  .user-wrap {
    padding: 0 $--interval-base;
    text-align: center;

    .user {
      position: relative;
      padding: $--interval-base #{$--interval-base * 3} $--interval-base 0;

      .user__img {
        width: 40px;
        height: 40px;
        border-radius: 20px;
      }

      .user__name {
        margin-top: 4px;
        margin-bottom: 4px;
        overflow: hidden;
        font-size: 12px;
        color: $--color-text-secondary;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      &__delete {
        position: absolute;
        top: 12px;
        right: 25px;
        font-size: 12px;
        color: white;
        cursor: pointer;
        visibility: hidden;
        background-color: #b2b2b2;
        border-radius: 6px;

        &:hover {
          background-color: $--color-primary;
        }
      }

      &:hover {
        .user__delete {
          visibility: visible;
        }
      }
    }
  }
}
</style>
