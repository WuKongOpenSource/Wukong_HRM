<template>
  <wk-head-section
    v-if="examineFlowList && examineFlowList.length > 0"
    v-loading="loading"
    label="审批流信息">

    <el-popover
      slot="right"
      ref="popover"
      v-model="showFlowPopover"
      :placement="placement"
      width="300"
      trigger="click">
      <check-flow
        :id="recordId"
        ref="checkFlow"
        :type-id="id"
        :show="showFlowPopover"
        :examine-type="examineType"
        @close="showFlowPopover=false" />
      <el-button
        slot="reference"
        style="margin-right: 8px;"
        type="text"
        @click.stop="">查看历史审批流程</el-button>
    </el-popover>

    <!-- 固定 -->
    <el-timeline>
      <el-timeline-item
        v-for="(item, index) in examineFlowList"
        :key="index"
        hide-timestamp>
        <i
          v-if="item.examineType != 7"
          slot="dot"
          :class="getStatusIcon(item.examineStatus)"
          :style="{
            color: getStatusColor(item.examineStatus)
          }"
          class="examine-item-icon" />
        <i
          v-else
          slot="dot"
          style="color: #5e6c84;"
          class="examine-item-icon wk wk-source-line" />
        <div class="examine-item">
          <!-- 审批节点只有一人 -->
          <div class="examine-item__hd ei">
            <div>
              <span class="ei-status">{{ examineNodeName(item) }}</span>
              <el-tag
                v-if="examineCategory(item.type) && item.examineType != 7 && item.userList && item.userList.length > 1"
                :disable-transitions="true">{{ examineCategory(item.type) }}</el-tag>
              <flexbox
                v-if="singleNodeDisableUserShow(item)"
                style="margin-top: 10px;">
                <xr-avatar
                  :name="getDetailName(item)"
                  :size="20" />
                <span class="ei-name">
                  {{ getDetailName(item) }}
                  {{ item.examineType == 7 && item.userList[0].userStatus == 0 ? '(已停用)' : '' }}
                  <span v-if="item.userList[0].hasOwnProperty('seekUser') && item.userList[0].seekUser" class="other-people">
                    <i class="wk wk-new-employee" />
                    已征求他人意见</span>
                </span>
                <!-- examineStatus
                  11 转审  8 作废  13 终止
                  examineType
                  7 抄送
                -->
                <span
                  v-if="item.userList[0].examineStatus != 11 && item.examineType !=7 && item.userList[0].examineStatus != 13 && item.userList[0].examineStatus != 8"
                  class="ei-status">{{ getShowStatusName(item.userList[0].examineStatus, item.isAdminSkip) }}</span>
              </flexbox>
            </div>
            <div v-if="item.userList.length === 1" class="ei-time">
              <span>{{ item.userList[0].examineTime }}</span>
              <!-- 跳过 -->
              <div v-if="item.isAdminSkip" class="examine-error">{{ item.userList[0].remarks }}</div>
              <!-- 审批异常转交/自动同意 -->
              <div v-if="showOneUserExamine(item)" class="examine-error">
                <i class="el-icon-warning" />
                {{ showOneUserExamine(item) }}
              </div>

              <!-- 审批限时处理 -->
              <div
                v-if="(item.userList[0].duration || item.userList[0].passFlag)
                  && (examineAdvancedConfigVO.advancedLimitTimeVO && examineAdvancedConfigVO.advancedLimitTimeVO.handleType != 1)
                  && item.userList[0].userStatus != 0"
                class="ei-time-limit"
                :style="autoExamineText(item.userList[0].duration,item.userList[0].examineStatus,item.userList[0].passFlag,item.userList[0])['class']"
              >{{ autoExamineText(item.userList[0].duration,item.userList[0].examineStatus,item.userList[0].passFlag,item.userList[0])['text'] }}
              </div>

              <!-- 限时禁用处理 -->
              <div
                v-if="(item.userList[0].duration && item.userList[0].userStatus == 0) && timeLimitStyleShow"
                class="examine-error">
                <i class="el-icon-warning" />
                {{ limitDisableShow() }}
              </div>

              <!-- 抄送提示文本 -->
              <!-- <div
                v-if="carbonCopy(item.examineType,item.userList)"
                class="examine-error">
                <i class="el-icon-warning" />
                {{ carbonCopy(item.examineType,item.userList) }}
              </div> -->
            </div>
            <div v-if="item.userList.length === 1 && canCheckEditFieldData(item) && !item.isAdminSkip">
              <el-button type="primary-text" @click="checkEditFieldDataClick(item)">查看节点编辑信息</el-button>
            </div>
          </div>
          <!-- 审批节点有多人人 -->
          <div v-if="item.userList.length > 1" class="examine-item__bd">
            <div
              v-for="(subItem, subIndex) in item.userList"
              :key="subIndex"
              class="examine-item__hd ei">
              <!-- handlerType 1转交 3委托 -->
              <flexbox
                v-if="disableAvatarUserShow(subItem)"
                :class="subItem.hasOwnProperty('handlerType') && [1, 3].includes(subItem.handlerType) ? 'examine-care-of' : ''">
                <i
                  v-if="subItem.hasOwnProperty('handlerType') && [1, 3].includes(subItem.handlerType) "
                  class="wk wk-icon-reply"
                  :class="subItem.hasOwnProperty('handlerType') && [1, 3].includes(subItem.handlerType) ? 'care-of' : ''" />
                <xr-avatar
                  :name="$getUserName(subItem)"
                  :size="20" />
                <span class="ei-name">
                  {{ $getUserName(subItem) || subItem.outerUserEmail }}
                  {{ item.examineType == 7 && subItem.userStatus == 0 ? '(已停用)' : '' }}
                  <span v-if="subItem.hasOwnProperty('seekUser') && subItem.seekUser" class="other-people">
                    <i class="wk wk-new-employee" />
                    已征求他人意见</span>
                </span>

                <!-- examineStatus
                  11 转审  8 作废  13 终止 17 委托
                  examineType
                  7 抄送
                -->
                <span
                  v-if="subItem.examineStatus != 11 && subItem.examineStatus != 17 && item.examineType != 7 && subItem.examineStatus != 13 && subItem.examineStatus != 8"
                  class="ei-status">{{ getShowStatusName(subItem.examineStatus, item.isAdminSkip) }}</span>
                <span
                  v-else-if="item.examineType ==7 && [18, 19, 14, 15].includes(Number(subItem.examineStatus))"
                  class="ei-status">{{ getShowStatusName(subItem.examineStatus) }}</span>
                <!-- class="ei-status">{{ item.type != 3 ? getShowStatusName(subItem.examineStatus, item.isAdminSkip) : '' }}</span> -->
                <span
                  v-if="subItem.examineStatus == 11"
                  class="ei-status">已转审</span>
                <span
                  v-else-if="subItem.examineStatus == 17"
                  class="ei-status">已委托</span>
              </flexbox>
              <div v-if="subItem.userStatus != 0 && (subItem.passFlag != 102 && subItem.passFlag != 202)" class="ei-time">{{ subItem.examineTime }}</div>

              <!-- 限时 -->
              <div
                v-if="((subItem.duration || subItem.passFlag) && (examineAdvancedConfigVO.advancedLimitTimeVO && examineAdvancedConfigVO.advancedLimitTimeVO.handleType != 1) && subItem.userStatus != 0)"
                class="ei-time-limit"
                :style="autoExamineText(subItem.duration,subItem.examineStatus,subItem.passFlag,subItem)['class']">
                {{ autoExamineText(subItem.duration,subItem.examineStatus,subItem.passFlag,subItem)['text'] }}
              </div>

              <!-- 禁用账户异常展示 -->
              <div
                v-if="disableUserShow(subItem.passFlag, subItem.examineStatus,subItem)"
                class="examine-error">
                <i class="el-icon-warning" />
                {{ disableUserShow(subItem.passFlag, subItem.examineStatus,subItem) }}
              </div>
            </div>

            <!-- 终止异常展示 -->
            <div
              v-if="item.passFlag == 302"
              class="examine-error">
              <i class="el-icon-warning" />
              转交人不存在已自动终止
            </div>

            <!-- 节点异常展示 -->
            <div
              v-if="(showMultiplayerExamine(item.passFlag,item.userList, true) && item.examineType != 7)"
              class="examine-error">
              <i class="el-icon-warning" />
              {{ showMultiplayerExamine(item.passFlag,item.userList) }}
            </div>

            <!-- 抄送提示文本 -->
            <!-- <div
              v-if="carbonCopy(item.examineType,item.userList)"
              class="examine-error">
              <i class="el-icon-warning" />
              {{ carbonCopy(item.examineType,item.userList) }}
            </div> -->

            <div v-if="canCheckEditFieldData(item) && !item.isAdminSkip">
              <el-button type="primary-text" @click="checkEditFieldDataClick(item)">查看节点编辑信息</el-button>
            </div>
          </div>
        </div>
      </el-timeline-item>
    </el-timeline>

    <div class="examine-config"><span @click="checkExamineConfig">查看审批配置</span></div>
    <!-- CRM 进销存等带弹框的审批和else里仅按钮的审批 外部审核不包含表单 -->
    <template v-if="(isFieldAuthExamine) && !externalMailbox">
      <div
        v-if="(!isOptionalNode && (examineInfo.isCheck == 1
          || examineInfo.isRecheck == 1))
          || isOptionalNode
          || (!isOptionalNode && superRestoreHandleShow)
          || examineMoreHandle.length"
        class="examine-footer">
        <el-button
          v-if="!isOptionalNode && examineInfo.isCheck == 1"
          type="primary"
          @click="examineClick">审批</el-button>

        <el-button
          v-if="isOptionalNode"
          type="primary"
          @click="examineHandleClick('selectExamineUser')">{{ isOptionalNode.addBtnName }}</el-button>

        <el-button
          v-if="!isOptionalNode && superRestoreHandleShow"
          type="primary"
          style="margin-left: auto;"
          @click="examineHandleClick(19)">恢复</el-button>

        <el-dropdown
          v-if="examineMoreHandle.length"
          :style="isStageFlow ? 'margin-left: 10px;' : ''"
          class="more-handle"
          @command="moreHandlerClick">
          <el-button>更多操作</el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="(item,index) in examineMoreHandle"
              :key="index"
              :command="item.value"
              @click.stop>
              <template v-if="item.value =='forwardOthers'">
                <el-dropdown
                  v-if="!isStageFlow"
                  class="more-handle"
                  @command="moreHandlerClick">
                  <div>转他人处理</div>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item command="selectUser">选择员工</el-dropdown-item>
                    <el-dropdown-item command="external">选择外部联系人</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
                <span
                  v-else
                  @click.stop="moreHandlerClick('selectUser')">转他人处理</span>
              </template>
              <template v-else>
                {{ item.label }}
              </template>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </template>
    <examine-btn-bar
      v-else
      style="padding: 16px 0;"
      :examine-info="examineInfo"
      :examine-more-handle="examineMoreHandle"
      :is-optional-node="isOptionalNode"
      :is-stage-flow="isStageFlow"
      :super-handle-show="superRestoreHandleShow"
      @examineHandle="examineHandleClick"
      @moreHandler="moreHandlerClick"
    />

    <!-- 操作 -->
    <examine-handle
      :id="id"
      ref="examineHandleRef"
      :show="examineHandleShow"
      :record-id="recordId"
      :examine-type="examineType"
      :flow-id="flowId"
      :status="examineHandleInfo.status"
      :props="examineHandleProps"
      :get-post-fields="getAuthFieldsParams"
      @close="examineHandleShow = false"
      @emailExamine="emailExamineHandleClick"
      @save="examineHandleSuccess" />

    <!-- 转他人处理 -->
    <wk-dep-user-dialog
      v-if="userViewDialogShow"
      :props="selectUserProps"
      radio
      :visible.sync="userViewDialogShow"
      @change="selectUserChange"
    />

    <!-- 征求他人意见 -->
    <solicit-others
      v-if="solicitOthersShow"
      v-model="solicitOthersShow"
      @submitParams="solicitOthersHandler" />

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
        v-model="email"
        style="margin-top: 10px;" />
      <span slot="footer" class="dialog-footer">
        <el-button @click="externalDialogShow = false">取消</el-button>
        <el-button type="primary" @click="verifyEmail">确定</el-button>
      </span>
    </el-dialog>

    <!-- 自选选择审批人 -->
    <el-dialog
      v-if="examineOptionalShow"
      v-loading="dialogLoading"
      :visible.sync="examineOptionalShow"
      :close-on-click-modal="false"
      :append-to-body="true"
      :title="isOptionalNode.addBtnName"
      width="40%">
      <flexbox
        class="user-wrap"
        wrap="wrap">
        <el-tooltip
          v-for="(item, index) in optionalUserList"
          :key="index"
          class="item"
          effect="dark"
          :offset="10"
          :content="item.realname || item.email || item.outerUserEmail"
          placement="top">
          <div class="user">
            <xr-avatar
              :name="item.realname || item.email || item.outerUserEmail"
              :size="40"
              :src="item.img" />
            <div class="user__name">{{ item.realname || item.email || item.outerUserEmail }}</div>
            <i
              class="user__delete el-icon-close"
              @click="deleteUserClick(index)" />
          </div>
        </el-tooltip>
        <div
          class="user">

          <template v-if="isAddUser">
            <el-button
              v-if="isStageFlow"
              @click="addingMode('selectUser')">选择员工</el-button>
            <el-dropdown
              v-else
              trigger="click"
              placement="right"
              @command="addingMode">
              <span class="el-dropdown-link">
                <el-button
                  class="user__img"
                  icon="el-icon-plus"
                  style="margin-bottom: 8px;"
                  circle />
                <div class="user__name">选择人员</div>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="selectUser">选择员工</el-dropdown-item>
                <el-dropdown-item
                  command="external">选择外部联系人</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </div>
      </flexbox>
      <span slot="footer" class="dialog-footer">
        <el-button @click="selectExamineUserClose">取消</el-button>
        <el-button type="primary" @click="submitExamineUser">确定</el-button>
      </span>
    </el-dialog>

    <!-- 审批编辑详情 -->
    <examine-edit-data-detail
      v-if="editDetailVisible"
      :id="id"
      :visible.sync="editDetailVisible"
      :examine-info="examineInfo"
      :item-data="editDetailItem"
    />

    <!-- 审批配置 -->
    <xr-create
      v-if="configShow"
      :card-style="{
        width: 'auto',
        marginRight: '80px',
        marginLeft: '80px',
      }"
      :loading="loading"
      title="配置审批流"
      :show-cancel="false"
      :show-confirm="false"
      @close="configShow = false">
      <wk-approve-flow
        ref="wkApproveFlow"
        :props="configFlowProps"
        :list="configFlowList"
        :send-node="sendNode" />
    </xr-create>
  </wk-head-section>
</template>
<script type="text/javascript">
import {
  forwardOthersAPI,
  solicitOthersAPI,
  useExamineEmailSendAPI,
  actionExamineAPI,
  examineSuperExaminesSetNodeOptionalAPI
} from '@/api/oa/superExamine'
import {
  examinesQueryExamineFlowAPI
} from '@/api/examine'

import { examineSuperExaminesUseExamineEmailTokenForAuditExamineAPI } from '@/api/examine/superExamine.js'
import ExamineHandle from './ExamineHandle' // 审批操作理由
import CheckFlow from './CheckFlow' // 审批流程
import WkHeadSection from '@/components/NewCom/WkHeadSection'
import WkDepUserDialog from '@/components/NewCom/WkUserDialogSelect/Dialog'
import SolicitOthers from './SolicitOthers' // 征求他人意见
import ExamineBtnBar from './ExamineBtnBar' // 操作按钮

import ExamineEditDataDetail from './ExamineEditDataDetail'
import { WkApproveFlow } from '@/components/ApprovalFlow'
import XrCreate from '@/components/XrCreate'

import CheckStatusMixin from '@/mixins/CheckStatusMixin'
import { wayTypeObj } from '@/components/ApprovalFlow/nodeModel'
import { regexIsCRMEmail } from '@/utils'
import { isEmpty } from '@/utils/types'
import moment from 'moment'
import NP from 'number-precision'
import AuthFieldsMixin from './mixins/AuthFieldsMixin'
import { mapGetters } from 'vuex'
import ExamineInfoMinxin from '@/views/admin/examine/mixins/ExamineInfo'

export default {
  name: 'ExamineInfoSection', // 合同审核操作
  components: {
    ExamineHandle,
    CheckFlow,
    WkHeadSection,
    WkDepUserDialog,
    SolicitOthers,
    ExamineBtnBar,
    ExamineEditDataDetail,
    WkApproveFlow,
    XrCreate
  },
  filters: {},
  mixins: [CheckStatusMixin, AuthFieldsMixin, ExamineInfoMinxin],
  provide() {
    return {
      examineInfoSection: this
    }
  },
  props: {
    // 业务类型
    examineType: {
      type: String,
      default: ''
    },
    // 业务id
    id: [String, Number],
    // 审批流id
    recordId: [String, Number],

    // 以下为差异配置
    // 外部审批传入的审批详情
    examineRecord: {
      type: Object,
      default: () => {}
    },
    externalMailbox: {
      type: Boolean,
      default: false
    },
    isStageFlow: { // 是否为  阶段流程
      type: Boolean,
      default: false
    },
    placement: {
      type: String,
      default: 'bottom'
    },
    // 忽略的操作
    ignoreAction: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  data() {
    return {
      dialogLoading: false,
      loading: false,
      examineInfo: {}, // 审核信息
      showFlowPopover: false, // 展示历史
      examineHandleInfo: { status: 1 }, // 1 审核通过 2 审核拒绝 4 已撤回
      examineHandleProps: {
        setBackNodeShow: false
      }, // 审批操作配置项
      examineHandleShow: false, // 审核操作
      userViewDialogShow: false, // 转他人处理
      solicitOthersShow: false, // 征求他人意见
      externalDialogShow: false, // 选择外部联系人
      email: '', // 外部联系人邮箱
      flowId: '', // 当前审批节点flowId

      // 审批流高级配置
      examineAdvancedConfigVO: {},
      selectUserProps: {
        showUser: true,
        showDept: false,
        showDisableUser: false,
        disableUserLabel: '员工'
      },

      // 自选选择他人
      examineOptionalShow: false,
      optionalUserList: [], // 自选人员列表

      addUserType: 'default',

      // 字段授权审批
      createType: '', // 创建类型
      authFields: [], // 字段授权
      crmCreateShow: false, // 展示创建
      editDetailItem: null, // 查看详情的节点
      editDetailVisible: false, // 编辑详情查看

      // 配置查看
      configShow: false,
      configFlowList: [],
      configFlowProps: {
        conditionAddShow: false,
        examineAddShow: false,
        copyAddShow: false
      },
      sendNode: {
        name: '发起人',
        content: '具有新建权限的员工'
      }
    }
  },
  computed: {
    ...mapGetters([
      'userInfo'
    ]),
    // 上级数据
    sendLevelOption() {
      const sendLevelOption = []
      for (let index = 1; index <= 20; index++) {
        const label = `第${index}级上级`
        if (index === 1) {
          sendLevelOption.push({
            label: '直属上级',
            value: index
          })
        } else {
          sendLevelOption.push({
            label: label,
            value: index
          })
        }
      }
      return sendLevelOption
    },
    // 审批历史
    examineFlowList() {
      return this.examineInfo.examineFlowList ? this.examineInfo.examineFlowList : []
    },

    // 展示流程管理员操作  主要是恢复 阶段内不能操作恢复
    superRestoreHandleShow() {
      return this.examineInfo?.isRestore == 1 && !this.isStageFlow &&
      !this.ignoreAction.includes('recovery') // 忽略操作里不包含
    },

    // 跳过权限
    superSkipHandleShow() {
      return this.examineInfo?.isSkip == 1
    },

    // 字段授权审批  展示审批按钮
    isFieldAuthExamine() {
      return false
    },

    // OA 审批
    isOAExamine() {
      if (this.examineInfo) {
        const { label } = this.examineInfo
        // 1~3 CRM 5~12 JXC 0 为OA
        return label === 0
      }
      return false
    },

    // 更多操作项
    examineMoreHandle() {
      if (this.isOptionalNode) return []
      const isCheck = this.examineInfo.isCheck == 1 // 审批
      const isRecheck = this.examineInfo.isRecheck == 1 // 撤回
      const isSuperAdmin = this.examineInfo.isSuperAdmin == 1 // 撤回
      const createUser = this.examineInfo.createUser // 创建人
      const privilege = this.examineAdvancedConfigVO?.privilege // 终止 作废 归档
      const twoPrivilege = isNaN(privilege) ? '000' : privilege.toString(2).padStart(3, '0')
      const isTermination = twoPrivilege[0] == '1' // 终止
      const isCancellation = twoPrivilege[1] == '1' // 作废
      const isArchive = twoPrivilege[2] == '1' // 归档

      let moreActions = []

      if (this.externalMailbox) {
        if (isCheck && isTermination) {
          moreActions = [
            { label: '终止', value: 'termination' }
          ]
        } else {
          moreActions = []
        }
      } else {
        if (isCheck) {
          const isTransferable = this.examineAdvancedConfigVO?.isTransferable // 转交权限
          const isConsult = this.examineAdvancedConfigVO?.isConsult // 在审批中征询他人意见
          if (isTransferable) {
            moreActions.push({ label: '转他人处理', value: 'forwardOthers' })
          }
          if (isConsult) {
            moreActions.push({ label: '征求他人意见', value: 'solicitOthers' })
          }

          if (isTermination) {
            moreActions.push({ label: '终止', value: 'termination' })
          }
        }

        // 邮件催办 发起人  流程管理员  超管
        const { examineFlowList = [], examineStatus } = this.examineInfo

        // 以下操作具备权限的条件
        const nextOperateAuth = this.userInfo?.userId === createUser.userId ||
        isSuperAdmin || this.userInfo?.isAdmin == 1
        const canEmailReminder = nextOperateAuth && examineFlowList.some(item => item.examineStatus === 3) // 必须有审批中的状态，才能催办
        if (canEmailReminder) {
          moreActions.push(
            { label: '邮件催办', value: 'emailReminder' }
          )
        }

        // 发起人  流程管理员  超管 审批人 14 已归档  8  已作废 13 已终止
        if ((isCheck || nextOperateAuth) &&
        examineStatus !== 14 &&
        examineStatus !== 8 &&
        examineStatus !== 13) {
          if (isArchive) {
            moreActions.push(
              { label: '归档', value: 'archive' }
            )
          }
          if (isCancellation) {
            moreActions.push(
              { label: '作废', value: 'cancellation' }
            )
          }
        }

        // 撤回
        if (isRecheck) {
          moreActions.push(
            { label: '撤回', value: 'retract' }
          )
        }
      }

      // 流程管理员操作
      // 可操作的前提下，展示跳过
      if (this.superSkipHandleShow) {
        moreActions.push(
          // { label: '恢复', value: 'recovery' },
          { label: '跳过', value: 'skip' }
        )
      }

      // 人资去除操作
      if (this.examineType == 'hrm_salary') {
        const hrmHandler = []
        const hrmType = ['archive', 'cancellation']
        // 不能作废只有一个
        moreActions.forEach((item, index) => {
          if (!hrmType.includes(item.value)) {
            hrmHandler.push(item)
          }
        })
        moreActions = hrmHandler
      }

      // 阶段流程去除操作
      if (this.isStageFlow) {
        const stageFlowHandler = []
        const isStageFlowType = ['termination', 'emailReminder', 'archive', 'cancellation']
        moreActions.forEach(item => {
          if (!isStageFlowType.includes(item.value)) {
            stageFlowHandler.push(item)
          }
        })

        moreActions = stageFlowHandler
      }

      return moreActions.filter(action => !this.ignoreAction.includes(action.value))
    },

    examineCategory() {
      return function(type) {
        return wayTypeObj[type]
      }
    },

    // 审批节点名称
    examineNodeName() {
      return (item) => {
        const nodeName = item.name ? (item.examineStatus == 12 ? item.name : item.name + '：') : ''
        let nodeDes = ''
        if (item.examineType == 7 && (![18, 19, 14, 15].includes(Number(item.examineStatus)))) {
          // 不是 跳过 恢复 展示抄送
          nodeDes = '抄送'
        } else if (item.passFlag == 105) {
          nodeDes = '管理员干涉跳过'
        } else {
          nodeDes = this.getShowStatusName(item.examineStatus, item.isAdminSkip)
        }

        return `${nodeName}${nodeDes}`
      }
    },

    /**
     * 审批异常返回文本
     * flag
     * 102 审批人不存在自动同意
     * 103 超时自动同意
     * 104 审批人禁用自动同意
     * 202 审批人不存在自动转交
     * 203 超时自动转交
     * 204 审批人禁用自动转交
     *
     * examineStatus  // 审批状态
     * 1 已通过 3 审核中
     *
     */
    passFlagNumText() {
      return function(flag, examineStatus, userList = []) {
        console.log(flag, examineStatus, userList)
        const leftText = {
          1: '审批人不存在，',
          2: '审批人不存在，',
          102: '未找到审批人，',
          202: '未找到审批人，',
          104: '审批人不存在，',
          204: '审批人不存在，'
        }

        // const joinText = {
        //   1: '已',
        //   11: '已',
        //   3: '将'
        // }

        const rightText = {
          1: '自动同意',
          2: `自动转交给 ${this.examineAdvancedConfigVO.nodeHandleUser?.map(item => this.$getUserName(item)).join()}`,
          102: '自动同意',
          202: `自动转交给 ${this.examineAdvancedConfigVO.nodeHandleUser?.map(item => this.$getUserName(item)).join()}`,
          104: '自动同意',
          204: `自动转交给 ${this.examineAdvancedConfigVO.nodeHandleUser?.map(item => this.$getUserName(item)).join()}`
        }

        // return leftText[flag] + joinText[examineStatus] + rightText[flag]
        if (examineStatus != 0) {
          return leftText[flag] + '已' + rightText[flag]
        } else {
          return leftText[flag] + '将' + rightText[flag]
        }
      }
    },

    /**
     * 单人审批异常是否展示
     * flag
     * 101 手动同意
     * 102 审批人不存在自动同意
     * 103 超时自动同意
     * 104 审批人禁用自动同意
     * 201 手动转发
     * 202 审批人不存在自动转交
     * 204 审批人禁用自动转交
     * 203 超时自动转交
     * 204 审批人禁用自动转交
     * 205 审批委托转交
     * 402 审批人不存在
     */
    showOneUserExamine() {
      return function(item) {
        const { passFlag, userList, examineType } = item
        const user = userList[0]
        if (examineType == 7) return '' // isAdminSkip 管理员跳过的节点是null，产生的虚拟节点是true
        if ((!passFlag || [101, 201, 103, 203, 205].includes(passFlag)) && !user.hasOwnProperty('isAdvance')) return ''

        const isAdvance = user.isAdvance || null // 审批人未找到情况下提前预览审批流

        if ([104, 204].includes(passFlag)) {
          return this.passFlagNumText(passFlag, user.examineStatus)
        } else if (passFlag == 102 || (isAdvance && isAdvance == 1)) {
          return this.passFlagNumText(102, user.examineStatus)
        } else if (passFlag == 202 || (isAdvance && isAdvance == 2)) {
          return this.passFlagNumText(202, 3, userList)
        } else if ([402].includes(passFlag)) {
          return '审批人不存在'
        }
      }
    },

    /**
     * 多人审批异常是否展示
     *
     * flag
     * 102 审批人不存在自动同意
     * 201 手动转发
     * 202 审批人不存在自动转交
     * 104 审批人禁用自动同意
     * 204 审批人禁用自动转交
     * 205 审批委托转交
     * 401 被管理员操作跳过审批人 被跳过，不在审批历史流程显示
     *
     * examineType 多人审批类型
     * 1 依次审批 2 会签 3 或签
     *
     */
    showMultiplayerExamine() {
      return function(flag, userList) {
        const isAdvance = userList.map(item => item.isAdvance).includes(2) // 预览审批流程
        if ([101, 201, 103, 203, 205, 401].includes(flag) || (!flag && !isAdvance)) return ''
        const forbiddenShow = userList.map(item => item.passFlag).includes(104) || userList.map(item => item.passFlag).includes(204) // 禁用员工是否展示
        const timeLimitShow = userList.map(item => item.hasOwnProperty('duration')).includes(true) // 限时是否展示
        if (isAdvance) {
          return this.passFlagNumText(202, 1, userList)
        } else if (timeLimitShow || forbiddenShow) {
          return ''
        } else {
          return this.passFlagNumText(flag, 1, userList)
        }
      }
    },

    /**
     * 禁用账户是否展示
     */
    disableUserShow() {
      return function(passFlag, status, item) {
        if (passFlag != 104 && passFlag != 204) return ''

        if (passFlag == 204 && item.examineStatus == 11) {
          return this.passFlagNumText(passFlag, status)
        } else if (passFlag == 104 && status == 1) {
          return this.passFlagNumText(passFlag, status)
        }
      }
    },

    /**
     * 限时禁用展示样式
     */
    limitDisableShow() {
      return function() {
        const flag = this.examineAdvancedConfigVO.nodeHandleType
        return this.passFlagNumText(flag, 3, 0)
      }
    },

    /**
     * 限时自动提醒不展示样式
     */
    timeLimitStyleShow() {
      const advancedLimitTimeVO = this.examineAdvancedConfigVO.advancedLimitTimeVO || {}
      if (isEmpty(advancedLimitTimeVO)) {
        return false
      } else {
        // handleType 1.自动提醒  2.自动转交  3.自动同意
        const { handleType } = advancedLimitTimeVO
        if (handleType == 1) {
          return false
        } else {
          return true
        }
      }
    },

    /**
     * 当前节点是否为自选节点并可添加人员
     */
    isOptionalNode() {
      const { examineFlowList, createUser } = this.examineInfo

      // 创建人才能撤回  isRecheck 必须是1
      if (!this.userInfo || createUser.userId !== this.userInfo.userId) {
        return null
      }
      const currentNode = examineFlowList[examineFlowList.length - 1]
      const { examineType, isAdd, userList } = currentNode
      // 4 发起人自选   7 抄送节点
      if ((examineType == 7 && isAdd == 1 && !userList.length) || (examineType == 4 && !userList.length)) {
        return {
          examineType: examineType,
          addBtnName: {
            7: '选择抄送人',
            4: '选择审批人'
          }[examineType]
        }
      } else {
        return null
      }
    },

    /**
     * 判断当前节点是否能再添加人员
     */
    isAddUser() {
      const { examineFlowList } = this.examineInfo
      const currentNode = examineFlowList[examineFlowList.length - 1]
      const { chooseType } = currentNode
      // chooseType 1自选一人  2自选多人
      if ((chooseType == 1 && this.optionalUserList.length == 1) || (chooseType != 1 && this.optionalUserList.length == 20)) {
        return false
      } else {
        return true
      }
    },

    /**
     * @description: 可查看节点信息
     * @return {*}
     */
    canCheckEditFieldData() {
      return (item) => {
        // 不是抄送人 或者 新建 examineStatus = 6
        let can = this.isFieldAuthExamine &&
        item.examineType != 7 && !!item.examineType && item.examineStatus != 6
        // 判断是否异常
        if (can) {
          if (item.userList.length === 1) {
            can = !this.showOneUserExamine(item) //  返回空，是正常
          } else if (item.userList.length > 1) {
            can = !this.showMultiplayerExamine(item.passFlag, item.userList)
          }
        }

        // 判断是完成的状态
        if (can) {
          if (item.userList.length === 1) {
            can = item.examineStatus == 1 // 只通过的能查看
          } else if (item.userList.length > 1) {
            can = item.userList.some(user => user.examineStatus == 1)
          }
        }

        return can
      }
    }
  },
  watch: {
    // 传递loading事件
    loading(val) {
      this.$emit('on-loading', val)
    },
    // recordId 有值后，获取页面展示信息
    recordId: {
      handler(val) {
        if (val) {
          this.refreshData()
        }
      },
      deep: true,
      immediate: true
    },
    examineFlowList: {
      handler(val) {
        this.flowId = this.flowGetCurrentFlowId(val)
      },
      immediate: true,
      deep: true
    },
    examineRecord: {
      handler(val) {
        if (!isEmpty(val)) {
          this.updateExamineInfo(this.examineRecord)
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    /**
     * @description: 刷新数据
     * @return {*}
     */
    refreshData() {
      this.examineInfo = {}
      this.getFlowStepOAList()
      // 切换id后，避免缓存数据，重新获取审批流程
      if (this.$refs.checkFlow) {
        this.$refs.checkFlow.getDetail()
      }
    },

    /**
     * @description: 获取展示的审核状态
     * @param {*} examineStatus
     * @param {*} isAdminSkip true（虚拟节点）18 展示跳过 false 节点被跳过
     * @return {*}
     */
    getShowStatusName(examineStatus, isAdminSkip) {
      if (examineStatus === 18) {
        return isAdminSkip ? '跳过' : '节点被跳过'
      }
      return this.getStatusName(examineStatus)
    },

    /**
     * @description: 邮箱审批
     * @param {*} content 意见内容
     * @param {*} flag 是否符合上传标准
     * @return {*}
     */
    emailExamineHandleClick(content, flag) {
      if (flag) {
        this.$message.error('请输入审批意见（必填）')
        return
      }
      examineSuperExaminesUseExamineEmailTokenForAuditExamineAPI({
        emailToken: this.$route.query.k,
        status: this.examineHandleInfo.status,
        remarks: content
      }).then(res => {
        this.$message.success('操作成功')
        this.$refs.examineHandleRef.resetInfo()
        this.examineHandleShow = false
        this.$emit('success')
      }).catch(e => {})
    },

    /**
     * 详情
     */
    getFlowStepOAList() {
      this.loading = true
      this.flowGetFlowStepList(this.recordId)
        .then(res => {
          this.loading = false
          const resData = res.data || {}
          this.updateExamineInfo(resData)
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * @description: 更新审批信息
     * @return {*}
     */
    updateExamineInfo(resData) {
      this.examineAdvancedConfigVO = resData.examineAdvancedConfigVO
      this.examineInfo = resData
      // 暂时通过详情里的label信息，确定哪些模块开启字段授权
      // 获取员工字段
      this.getUserFieldList(this.examineInfo.label, this.examineAdvancedConfigVO.examineId)
    },

    /**
     * @description: 审批点击
     * @return {*}
     */
    async examineClick() {
    },

    /**
     * 更多操作
     */
    moreHandlerClick(command) {
      this.addUserType = 'default'
      if (command == 'selectUser') { // 转他人处理(选择员工)
        this.selectUserProps = {
          showUser: true,
          showDept: false,
          showDisableUser: false,
          disableUserLabel: '员工'
        }

        this.userViewDialogShow = true
      } else if (command == 'external') { // 转他人处理(外部联系人)
        this.externalDialogShow = true
      } else if (command == 'solicitOthers') { // 征求他人意见
        this.solicitOthersShow = true
      } else if (command == 'termination') { // 终止
        this.examineHandleClick(13)
      } else if (command == 'emailReminder') { // 邮件催办
        this.emailReminder()
      } else if (command == 'archive') { // 归档
        this.examineHandleClick(14)
      } else if (command == 'cancellation') { // 作废
        this.examineHandleClick(8)
      } else if (command == 'retract') { // 撤回
        this.examineHandleClick(4)
      } else if (command == 'skip') { // 跳过
        this.examineHandleClick(18)
      }
    },

    /**
     * 撤回审核 通过 拒绝 归档 作废
     */
    examineHandleClick(status) {
      if (status == 'selectExamineUser') {
        this.examineOptionalShow = true
        return
      }

      const { isRejectChooseFlow } = this.examineAdvancedConfigVO // 开启驳回节点配置
      this.examineHandleProps.setBackNodeShow = ((isRejectChooseFlow && status == 2) ||
      (this.superRestoreHandleShow && status == 19) ||
      (this.superSkipHandleShow && status == 18)) && !this.externalMailbox // 驳回 或  流程管理员恢复 跳过 展示 外部审批人 不展示
      this.examineHandleInfo.status = status
      this.examineHandleShow = true
    },

    /**
     * @description: 获取授权post字段
     * @return {*}
     */
    getAuthFieldsParams() {
      if (this.isFieldAuthExamine) {
        // 仅在 ExamineInfoSection 组件内，该变量有值
      }
      return null
    },

    /**
     * @description: 归档/作废
     * @param {*} status 3归档  4作废
     * @return {*}
     */
    archivedObsolete(status, type) {
      const params = {
        archiveOrVoidFlag: status,
        recordId: this.recordId
      }
      actionExamineAPI(params)
        .then(res => {
          this.$message.success('操作成功')
          this.getFlowStepOAList()
          this.$emit('on-handle', type)
        })
    },

    /**
     * @description: 邮件催办
     * @return {*}
     */
    emailReminder() {
      useExamineEmailSendAPI({ examineRecordId: this.recordId })
        .then(res => {
          this.$message.success('操作成功')
          this.getFlowStepOAList()
        })
    },

    /**
     * 审批操作成功后事件
     */
    examineHandleSuccess(data) {
      this.closeFieldView()

      this.getFlowStepOAList()
      if (this.$refs.checkFlow) {
        this.$refs.checkFlow.getDetail()
      }
      this.$emit('on-handle', data)
    },

    /**
     * 获取名称
     */
    getDetailName(data) {
      if (!data.userList || data.userList.length === 0) {
        return 'XX'
      } else if (data.userList.length === 1) {
        return this.$getUserName(data.userList[0]) ? this.$getUserName(data.userList[0]) : data.userList[0].outerUserEmail
      } else if (data.examineType === 5) {
        return `${data.userList.length}人${wayTypeObj[1]}`
      } else {
        return `${data.userList.length}人${wayTypeObj[data.type]}`
      }
    },

    /**
     * 转他人处理
     */
    selectUserChange(usersIds, _, users) {
      if (this.addUserType == 'optional') {
        if (!this.optionalUserList.map(item => item.userId).includes(users[0].userId)) {
          this.optionalUserList.push(...users)
        }
        return
      }

      const params = {
        examineFlowFinalUser: {
          userId: usersIds[0]
        },
        flowFinalId: this.flowId,
        recordId: this.recordId
      }

      forwardOthersAPI(params)
        .then(res => {
          this.closeFieldView()

          this.getFlowStepOAList()
        })
    },

    /**
     * 验证邮箱
     */
    verifyEmail() {
      if (regexIsCRMEmail(this.email)) {
        if (this.addUserType == 'optional') {
          if (!this.optionalUserList.map(item => item.email).includes(this.email)) {
            this.optionalUserList.push({ email: this.email })
          }
          this.email = null
          this.externalDialogShow = false
          return
        }

        const params = {
          examineFlowFinalUser: {
            email: this.email
          },
          flowFinalId: this.flowId,
          recordId: this.recordId
        }

        forwardOthersAPI(params)
          .then(res => {
            this.getFlowStepOAList()
          })
        this.email = null
        this.externalDialogShow = false
      } else {
        this.$message.error('邮箱格式有误')
      }
    },

    /**
     * 征求他人意见
     */
    solicitOthersHandler(data) {
      const params = {
        ...data,
        insertTarget: this.flowId,
        recordId: this.recordId
      }
      solicitOthersAPI(params)
        .then(() => {
          this.closeFieldView()
          this.getFlowStepOAList()
        })
    },

    /**
     * 自动审批提示文本
     * @param {*} millimeter 现在时间距离创建时间的毫秒数
     * @param {*} examineStatus 审核状态
     * @param {*} passFlag 103 限时自动同意   203 限时自动转交
     * @param {*} user
     */
    autoExamineText(millimeter, examineStatus, passFlag, user) {
      const time = Number(millimeter || 0)
      const limitTimeVO = this.examineAdvancedConfigVO?.advancedLimitTimeVO // 审批限时高级配置
      if (!limitTimeVO || user.userStatus == 0 || !time) return {}

      /**
       *  handleType 限时处理方式 2 自动同意  3 自动转交
       *  handleUserList 处理人
       *  limitTime 后台配置时间
       *  timeType 后台配置时间单位
       **/
      const { handleType, handleUserList, limitTime } = limitTimeVO
      const timeType = limitTimeVO.limitTimeUnit

      // 后台配置时间 - 距离当前时间 = 剩余时间
      const timeObj = {
        'minute': Number(NP.minus(NP.times(limitTime, 60, 1000), time)),
        'hour': Number(NP.minus(NP.times(limitTime, 60, 60, 1000), time)),
        'day': Number(NP.minus(NP.times(limitTime, 24, 60, 60, 1000), time))
      }

      const timeTypeObj = {
        'minute': '分钟',
        'hour': '小时',
        'day': '天'
      }

      if (passFlag == 103) {
        return {
          text: `【限时审批】流程在审批人处停留超过${limitTime}${timeTypeObj[timeType]}，系统已自动同意`,
          class: 'background-color: #EBECF0;color:#999'
        }
      } else if (passFlag == 203) {
        return {
          text: `【限时审批】流程在审批人处停留超过${limitTime}${timeTypeObj[timeType]}，系统已【自动转交】给${this.getHandleUserList(handleUserList)}`,
          class: 'background-color: #EBECF0;color:#999'
        }
      }

      if (timeObj[timeType] > 0) { // 限时处理
        const day = moment.duration(timeObj[timeType]).days() ? moment.duration(timeObj[timeType]).days() + '天' : '' // 剩余时间是否包含天
        const hour = moment.duration(timeObj[timeType]).hours() ? moment.duration(timeObj[timeType]).hours() + '小时' : '' // 剩余时间是否包含小时
        const minuter = moment.duration(timeObj[timeType]).minutes() ? moment.duration(timeObj[timeType]).minutes() + '分钟' : '' // 剩余时间是否包含分钟
        if (handleType == 3) {
          return {
            text: !day && !hour && !minuter ? '1分钟后自动同意' : day + hour + minuter + '后自动同意',
            class: 'color: red'
          }
        } else if (handleType == 2) {
          return {
            text: !day && !hour && !minuter ? '1分钟后自动转交给' + this.getHandleUserList(handleUserList) : day + hour + minuter + '后自动转交给' + this.getHandleUserList(handleUserList),
            class: 'color: red'
          }
        }
      } else { // 超时处理
        if (handleType == 2) {
          return {
            text: `【限时审批】流程在审批人处停留超过${limitTime}${timeTypeObj[timeType]}，系统${examineStatus == 1 ? '已' : '将'}【自动转交】给${this.getHandleUserList(handleUserList)}`,
            class: 'background-color: #EBECF0;color:#999'
          }
        } else if (handleType == 3) {
          return {
            text: `【限时审批】流程在审批人处停留超过${limitTime}${timeTypeObj[timeType]}，系统${examineStatus == 1 ? '已' : '将'}自动同意`,
            class: 'background-color: #EBECF0;color:#999'
          }
        }
      }
    },

    /**
     * @description: 获取自动转交信息
     * @param {*} handleUserList
     * @return {*}
     */
    getHandleUserList(handleUserList) {
      return handleUserList.map(bigItem => {
        const { type, dataList } = bigItem
        // 0指定用户 1指定上级2角色
        if (type == 1) {
          return dataList.map(item => this.sendLevelOption[item.parentLevel - 1]?.label).join()
        } else {
          return dataList.map(item => this.$getUserName(item)).join()
        }
      }).join()
    },

    // 单节点员工禁用是否展示
    singleNodeDisableUserShow(item) {
      const userList = item.userList
      if (userList?.length !== 1) return false
      const user = userList[0]
      if ((this.$getUserName(user) || user.outerUserEmail) &&
          (
            (!user.hasOwnProperty('userStatus') || user.userStatus != 0) ||
          item.examineType == 7 ||
          (![104, 204].includes(user.passFlag)) // 审批人停用 不用展示，抄送人停用 展示
          )) {
        return true
      }
    },

    /**
     * 多人禁用员工是否展示
     */
    disableAvatarUserShow(item) {
      const { passFlag, examineStatus } = item
      // 104 审批人禁用自动同意
      // 204 审批人禁用自动转交
      if (passFlag == 104 && examineStatus == 1) {
        return false
      } else if (passFlag == 204 && examineStatus == 11) {
        return false
      } else {
        return true
      }
    },

    /**
     * @description: 选择审批人
     * @return {*}
     */
    submitExamineUser() {
      if (this.optionalUserList.length == 0) return
      this.dialogLoading = true
      const { examineFlowList } = this.examineInfo
      const { flowId, examineType } = examineFlowList[examineFlowList.length - 1]

      const params = {
        flowId,
        examineType,
        recordId: this.recordId,
        userList: this.optionalUserList
      }

      examineSuperExaminesSetNodeOptionalAPI(params)
        .then(() => {
          this.dialogLoading = false
          this.selectExamineUserClose()
          this.getFlowStepOAList()
        }).catch(() => {
          this.dialogLoading = false
        })
    },

    /**
     * @description: 选择审批人关闭
     * @return {*}
     */
    selectExamineUserClose() {
      this.optionalUserList = []
      this.examineOptionalShow = false
    },

    /**
     * @description: 删除审批人
     * @param {*} index
     * @return {*}
     */
    deleteUserClick(index) {
      this.optionalUserList.splice(index, 1)
    },

    /**
     * @description: 选择添加方式
     * @param {*} item
     * @param {*} command
     * @return {*}
     */
    addingMode(command) {
      this.addUserType = 'optional'
      if (command == 'selectUser') { // 选择员工
        this.selectUserClick()
      } else if (command == 'external') { // 选择外部联系人
        this.externalDialogShow = true
        this.email = null
      }
    },

    /**
     * @description: 选择人员
     * @param {*} item
     * @return {*}
     */
    selectUserClick() {
      const { examineFlowList } = this.examineInfo
      const currentNode = examineFlowList[examineFlowList.length - 1]
      const { chooseUserList, rangeType, examineType } = currentNode
      let userOptions = null
      if (examineType == 4) {
        if (rangeType == 2 || rangeType == 3) {
          userOptions = chooseUserList
        }
      }

      this.selectUserProps = {
        showUser: !userOptions,
        showDept: false,
        showDisableUser: !!userOptions,
        disableUserList: userOptions,
        disableUserLabel: '员工'
      }
      this.userViewDialogShow = true
    },

    /**
     * @description: 查看编辑字段的信息
     * @return {*}
     */
    checkEditFieldDataClick(item) {
      this.editDetailItem = item
      this.editDetailVisible = true
    },

    /**
     * @description: 关闭表单字段查看
     * @return {*}
     */
    closeFieldView() {
    },

    /**
     * @description: 查看审批配置
     * @return {*}
     */
    checkExamineConfig() {
      this.configShow = true

      const doneFlowIds = this.examineFlowList.map(item => item.flowId) // 已经过或所在节点的flowId
      const lastFlowId = this.examineFlowList.length > 0 ? this.examineFlowList[this.examineFlowList.length - 1].flowId : null // 是否流程开始
      let passLastFlowId = null // 是否已过最后节点

      this.loading = true
      examinesQueryExamineFlowAPI({
        examineId: this.examineAdvancedConfigVO.examineId
      }).then(res => {
        const list = res.data || []
        const dataList = []
        // let modal = false
        this.getListInfo(list, dataList, {
          additionalInfoFun: (dataItem, orgData, orgIndex, parentList, parentIndex) => {
            dataItem.disabled = true // 后台返回数据禁止编辑
            if (dataItem.conditionName) {
              // 到最后一个生效节点之后，全部置灰
              console.log('lastFlowId', lastFlowId, passLastFlowId)
              if (!lastFlowId || passLastFlowId) {
                dataItem.modal = true
              } else {
                dataItem.modal = !orgData.examineDataList.some(eItem => doneFlowIds.includes(eItem.flowId))
              }
              if (orgData.flowId == lastFlowId) {
                passLastFlowId = orgData.flowId
              } // passLastFlowId 最后一个节点到了之后，之后的节点全部置灰
            } else {
              dataItem.modal = !doneFlowIds.includes(orgData.flowId)
            }
          },
          cycleFun: (cycType, data) => {
            if (cycType === 'conditionList') {
              // conditionWrap 原始数据  conditionWrapInfo 处理后的数据
              if (data.conditionWrap.conditionList.length - 1 === data.index) {
                // 如果所有条件节点都置灰，如果上层节点经过了和没有经过最后一个节点id，点亮第一个条件
                if (data.conditionWrapInfo.conditionList.every(item => item.modal)) {
                  // conditionIndex 条件所在index
                  if (data?.conditionIndex > 0) {
                    // 因为节点还未加入到
                    if (data.newList.length > 0) {
                      const preNode = data.newList[data.newList.length - 1]
                      if (!preNode.modal && !passLastFlowId) {
                        // 如果上个节点点亮，没有经过最后一个节点，点亮第一个 不包含审批的条件
                        for (let i = 0; i < data.conditionWrapInfo.conditionList.length; i++) {
                          const conditionItem = data.conditionWrapInfo.conditionList[i]
                          if (!conditionItem.examineDataList.length) {
                            conditionItem.modal = false
                            break
                          }
                        }
                      }
                    }
                  } else if (!passLastFlowId) {
                    // 如果条件是第一个节点，没有经过最后一个节点，点亮第一个条件
                    if (data.conditionWrapInfo?.conditionList.length > 0) {
                      data.conditionWrapInfo.conditionList[0].modal = false
                    }
                  }
                }
              }
            }
          }
        })
        this.configFlowList = dataList
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    }
  }
}
</script>
<style lang="scss" scoped>
::v-deep .el-timeline {
  .el-timeline-item {
    .el-timeline-item__dot {
      i {
        margin-top: -5px;
      }
    }
  }
}

.wk-head-section {
  ::v-deep {
    .section-body {
    }
  }
}

/** 审核流程 */
.check-items {
  overflow-x: auto;
  line-height: 2;
}

.check-item {
  flex-shrink: 0;
  width: auto;

  .check-item-name {
    margin-right: $--interval-base;
    font-size: 16px;
  }

  .check-item-status {
    color: $--color-text-secondary;
  }

  .check-item-arrow {
    margin: 0 #{$--interval-base * 2};
    font-size: 13px;
  }
}

// 固定审批流详情
.examine-item {
  &-icon {
    margin-left: -4px;
    font-size: 18px;
    background-color: white;
  }

  .ei {
    padding: 2px 0;

    &-name {
      font-size: 14px;
      color: $--color-black;
    }

    &-status {
      font-size: 14px;
      color: $--color-text-secondary;
    }

    &-time {
      margin-top: 4px;
      font-size: 12px;
      color: $--color-n70;
    }

    &-time-limit {
      padding: 5px 0;
      font-size: $--font-size-small;
    }

    span + span {
      margin-left: 4px;
    }
  }

  .ei + .ei {
    margin-top: 4px;
  }

  &__bd {
    margin-top: 4px;

    .examine-care-of {
      margin-left: 10px;
    }

    .care-of {
      margin-right: 5px;
      color: $--color-n200;
      transform: rotate(180deg);
    }

    .ei-name {
      font-size: 14px;
    }

    .ei-status {
      font-size: 12px;
    }
  }

  // .examine-error {
  //   margin-top: 4px;
  //   font-size: 12px;
  //   line-height: 1.5;
  //   color: $--color-text-secondary;
  // }

  .other-people {
    font-size: 14px;
    color: $--color-n200;

    i {
      margin-left: 4px;
      font-size: 14px;
      color: $--color-y300;
    }
  }

  .remarks {
    padding: 8px;
    margin: 10px 0;
    font-size: 12px;
    line-height: 18px;
    color: #6b778c;
    background-color: #f4f5f7;
    border-radius: 3px;
  }
}

.examine-error {
  padding: 5px;
  margin-top: 10px;
  font-size: 12px;
  color: #a5adba;
  background-color: $--color-n20;

  i {
    margin-right: 5px;
    color: #f56c6c;
  }
}

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

.examine-footer {
  margin-top: 8px;
  margin-bottom: 16px;
  text-align: right;
}

.examine-config {
  margin: 8px 0;
  color: $--color-text-secondary;
  text-align: right;

  span {
    cursor: pointer;
  }
}

::v-deep .wk-approve-flow-wrap {
  .el-button-group {
    position: fixed;
    top: 140px;
    right: 140px;
  }
}

::v-deep .config-view {
  .create-view-content {
    width: auto !important;
    margin-right: 80px;
    margin-left: 80px;
  }
}
</style>
