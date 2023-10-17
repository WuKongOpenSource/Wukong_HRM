<template>
  <div class="ad-set">
    <el-form
      ref="advancedSettingFrom"
      :model="advancedLimitTimeBO">
      <div v-for="item in fieldList" :key="item.name" class="ad-set-item">
        <div class="set-name-wrap">
          <div class="set-name">{{ item.name }}</div>
          <el-switch
            v-if="item.field=='nodeLimit'"
            v-model="form.limitTimeStatus"
            :active-value="1"
            :inactive-value="0"
            @change="nodeLimitChange" />
        </div>

        <div v-if="item.explain" class="set-explain">{{ item.explain }}</div>
        <!-- 单选框处理 -->
        <el-radio-group
          v-if="item.type=='radio' && (item.field == 'nodeLimit' ? form.limitTimeStatus : true)"
          v-model="form[item.field]"
          @change="radioChange($event,item.field)"
        >
          <el-radio
            v-for="subItem in item.forms"
            :key="subItem.value"
            :label="subItem.value">
            {{ subItem.label }}
          </el-radio>

          <el-form-item
            v-if="item.field == 'nodeLimit'"
            prop="limitTime"
            :rules="{required: true, message: '节点时间不能为空', trigger: 'blur' }">
            <div>
              <div>
                <span>流程到达该节点超过</span>
                <div style="display: inline-block;width: 200px;">
                  <el-input
                    v-model="advancedLimitTimeBO.limitTime"
                    placeholder="请输入内容"
                    @input="timeChange">
                    <el-select
                      slot="append"
                      v-model="advancedLimitTimeBO.limitTimeUnit"

                      :style="{width:'90px'}"
                      @change="timeTypeChange">
                      <el-option label="分钟" value="minute" />
                      <el-option label="小时" value="hour" />
                      <el-option label="天" value="day" />
                    </el-select>
                  </el-input>
                </div>
                <span>处理人未处理时{{ form.nodeLimit== 3 ? ',自动同意' : '' }}</span>
              </div>
              <span v-if="form.nodeLimit == 1" style="font-weight: bold;">自动提醒被提醒人</span>
              <span v-if="form.nodeLimit == 2" style="font-weight: bold;">自动转交</span>
            </div>
          </el-form-item>

          <div
            v-if="(item.field =='nodeHandleType' && form.nodeHandleType==2)||
              (item.field =='nodeLimit' && [1,2].includes(form.nodeLimit))"
            style="margin-top: 10px;">
            <el-button type="primary" @click="addUserHandler(item.field)">添加人员</el-button>
            <span>不能超过20人</span>
            <div class="user-list-wrap">
              <span v-if="form.nodeLimit==1 && item.field == 'nodeLimit'" class="user-item">当前处理人</span>
              <!-- eslint-disable vue/no-use-v-if-with-v-for -->
              <el-tag
                v-for="(user, index) in form.nodeHandleUser"
                v-if="item.field =='nodeHandleType'"
                :key="index"
                size="medium"
                disable-transitions
                closable
                type="info"
                class="user-item"
                @close="userDelete(index,item.field)">
                {{ user.realname }}
              </el-tag>
              <el-tag
                v-for="(user, index) in advancedLimitTimeBO.handleUserList"
                v-if="item.field =='nodeLimit'"
                :key="index"
                size="medium"
                disable-transitions
                closable
                type="info"
                class="user-item"
                @close="userDelete(index,item.field)">
                {{ user.realname }}
              </el-tag>
            </div>
          </div>

          <div v-if="item.field == 'nodeLimit' && [1, 2].includes(form.nodeLimit)">
            <div style="margin-top: 10px;">
              <div class="small-title">指定上级</div>
              <el-select v-model="advancedLimitTimeBO.nodeHandleParentLevel" multiple>
                <el-option
                  v-for="sitem in sendLevelOption"
                  :key="sitem.value"
                  :label="sitem.label"
                  :value="sitem.value" />
              </el-select>
            </div>

            <div style="margin-top: 10px;">
              <div class="small-title">指定角色</div>
              <role-employee-select
                ref="roleSelect"
                v-model="advancedLimitTimeBO.nodeHandleRole"
                multiple
                :props="{
                  onlyShowRole: true
                }"
                clearable />
            </div>

            <div v-if="form.nodeLimit === 2" style="margin-top: 10px;">
              <div class="small-title">转交给多人时采用的审批方式</div>
              <el-radio-group v-model="advancedLimitTimeBO.examineType">
                <el-radio
                  v-for="(eItem,eIndex) in examineType"
                  :key="eIndex"
                  :label="eItem.value">
                  {{ eItem.label }}
                </el-radio>
              </el-radio-group>
            </div>
          </div>

        </el-radio-group>
        <!-- 复选框处理 -->
        <el-checkbox-group
          v-if="item.type=='checkbox'"
          v-model="form[item.field]"
          :class="item.class"
        >
          <el-checkbox
            v-for="subItem in item.forms"
            :key="subItem.value"
            :label="subItem.value">
            {{ subItem.label }}
          </el-checkbox>
        </el-checkbox-group>
      </div>
    </el-form>

    <!-- user选择弹窗 -->
    <wk-dep-user-dialog
      v-if="userDialogVisible"
      :user-value="userDialogValue"
      :visible.sync="userDialogVisible"
      :props="{
        showDisableUser: false,
        showDept: false,
      }"
      @change="selectUserChange"
    />
  </div>
</template>
<script>

import WkDepUserDialog from '@/components/NewCom/WkUserDialogSelect/Dialog'
import RoleEmployeeSelect from '@/views/admin/employeeDep/components/RoleEmployeeSelect'
import { objDeepCopy } from '@/utils/index'

import NP from 'number-precision'
import merge from '@/utils/merge'

const DefaultAdvancedSetting = {
  privilegeOnlyTermination: false // 权限仅展示终止
}

export default {
  name: 'AdvancedSetting',

  components: {
    WkDepUserDialog,
    RoleEmployeeSelect
  },

  props: {
    value: {
      type: Object,
      default: () => {}
    },
    isStageFlow: { // 是否为阶段流程审批
      type: Boolean,
      default: false
    },
    props: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      cpValue: objDeepCopy(this.value),
      examineType: [
        { label: '依次审批', value: 1 },
        { label: '会签（需所有转交人同意）', value: 2 },
        { label: '或签（一名转交人同意或拒绝即可）', value: 3 }
      ],

      advancedLimitTimeBO: {},
      form: {},
      userDialogVisible: false,
      selectedUserList: [],
      currentAddType: '',

      sendLevelOption: []
    }
  },
  computed: {
    // 合并 props
    config() {
      return merge({ ...DefaultAdvancedSetting }, this.props || {})
    },

    fieldList() {
      const fieldList = [
        { field: 'nodeHandleType', name: '异常处理', explain: '审批节点内成员离职,为空等情况的处理方式', type: 'radio',
          forms: [
            { type: 'radio', label: '自动同意', value: 1 },
            { type: 'radio', label: '转交给指定人员处理', value: 2 }
          ] },
        { field: 'repeatHandleType', name: '审批人重复', explain: '当同一个审批人重复审批同一单据时', type: 'radio',
          forms: [
            { type: 'radio', label: '仅首个节点需审批，其余自动同意', value: 1 },
            { type: 'radio', label: '仅连续审批时自动同意', value: 2 },
            { type: 'radio', label: '每个节点都需要审批', value: 3 }
          ] },
        { field: 'rejectHandleType', name: '审批被拒后重新提交/审批', explain: '', type: 'radio',
          forms: [
            { type: 'radio', label: '从拒绝时指定节点依次处理', value: 2 } // 返回审批流初始层级
          ] }
      ]

      const privilegeForms = [{ type: 'checkbox', label: '终止', value: 1 }]
      if (!this.config.privilegeOnlyTermination) {
        privilegeForms.push({ type: 'checkbox', label: '作废', value: 2 },
          { type: 'checkbox', label: '归档', value: 3 })
      }

      const fieldOhterList = [
        { field: 'modifyPermissionType', name: '修改权限', explain: '', type: 'checkbox',
          forms: [
            { type: 'checkbox', label: '提交申请时，员工不可修改固定审批人和审批节点', value: 1 },
            { type: 'checkbox', label: '提交申请时，员工不可修改固定抄送人和抄送节点', value: 2 }
          ]
        },
        { field: 'isTransferable', name: '转交权限', explain: '', type: 'checkbox',
          forms: [
            { type: 'checkbox', label: '允许员工将流程转交给他人处理', value: 1 }
          ]
        },
        { field: 'isConsult', name: '征求他人意见权限', explain: '', type: 'checkbox',
          forms: [
            { type: 'checkbox', label: '允许员工征求他人意见权限', value: 1 }
          ]
        },
        { field: 'isRejectChooseFlow', name: '拒绝时驳回到任一节点权限（如果不允许，则拒绝时默认驳回到发起人）', explain: '', type: 'checkbox',
          forms: [
            { type: 'checkbox', label: '允许员工拒绝时选择驳回到哪一节点', value: 1 }
          ]
        },
        { field: 'isCreateUserWithdraw', name: '发起人撤回权限', explain: '', type: 'checkbox',
          forms: [
            { type: 'checkbox', label: '允许发起人撤回审批', value: 1 }
          ]
        },
        { field: 'nodeLimit', name: '节点限时处理', explain: '', type: 'radio',
          forms: [
            { type: 'radio', label: '自动提醒', value: 1 },
            { type: 'radio', label: '自动转交', value: 2 },
            { type: 'radio', label: '自动同意', value: 3 }
          ]
        },
        { field: 'privilege', name: '发起人及审核人操作权限', explain: '', type: 'checkbox',
          class: 'is-flex',
          forms: privilegeForms
        }
      ]

      if (!this.isStageFlow) {
        fieldList.push(...fieldOhterList)
      } else {
        fieldList.push(...[
          { field: 'isTransferable', name: '转交权限', explain: '', type: 'checkbox',
            forms: [
              { type: 'checkbox', label: '允许员工将流程转交给他人处理', value: 1 }
            ]
          },
          { field: 'isConsult', name: '征求他人意见权限', explain: '', type: 'checkbox',
            forms: [
              { type: 'checkbox', label: '允许员工征求他人意见权限', value: 1 }
            ]
          },
          { field: 'isRejectChooseFlow', name: '拒绝时驳回到任一节点权限（如果不允许，则拒绝时默认驳回到发起人）', explain: '', type: 'checkbox',
            forms: [
              { type: 'checkbox', label: '允许员工拒绝时选择驳回到哪一节点', value: 1 }
            ]
          },
          { field: 'isCreateUserWithdraw', name: '发起人撤回权限', explain: '', type: 'checkbox',
            forms: [
              { type: 'checkbox', label: '允许发起人撤回审批', value: 1 }
            ]
          }])
      }

      return fieldList
    },

    userDialogValue() {
      return (this.selectedUserList || []).map(item => item.userId)
    }
  },
  watch: {
    form: {
      handler() {
        this.emitEvent()
      },
      deep: true
    },
    advancedLimitTimeBO: {
      handler() {
        this.emitEvent()
      },
      deep: true
    }
  },
  created() {
    const {
      nodeHandleType = 1,
      repeatHandleType = 3,
      rejectHandleType = 2,
      limitTimeStatus = 0,
      modifyPermissionType = [],
      nodeHandleUser = [],
      advancedLimitTimeVO = {},
      isTransferable = false,
      isConsult = false,
      isRejectChooseFlow = false,
      isCreateUserWithdraw = false,
      privilege
    } = this.cpValue
    const twoPrivilege = isNaN(privilege) ? '000' : privilege.toString(2).padStart(3, '0')
    const privileges = []
    for (let index = 0; index < twoPrivilege.length; index++) {
      if (twoPrivilege[index] == '1') {
        privileges.push(index + 1)
      }
    }

    this.form = {
      nodeHandleType,
      repeatHandleType,
      rejectHandleType,
      limitTimeStatus,
      nodeLimit: advancedLimitTimeVO?.handleType || 3,
      modifyPermissionType: modifyPermissionType || [],
      nodeHandleUser: nodeHandleUser || [],
      isTransferable: typeof isTransferable === 'boolean' ? isTransferable : false,
      isConsult: typeof isConsult === 'boolean' ? isConsult : false,
      isRejectChooseFlow: typeof isRejectChooseFlow === 'boolean' ? isRejectChooseFlow : false,
      isCreateUserWithdraw: typeof isCreateUserWithdraw === 'boolean' ? isCreateUserWithdraw : false,
      privilege: privileges
    }

    // rejectHandleType 只保留了2逻辑，强制重置为2
    if (this.form.rejectHandleType != 2) {
      this.form.rejectHandleType = 2
    }

    if (advancedLimitTimeVO && Object.keys(advancedLimitTimeVO).length) {
      const {
        handleType,
        examineType,
        limitTimeUnit = 'minute',
        limitTime,
        // 编辑时该字段包含：人员、上级、角色
        handleUserList
      } = advancedLimitTimeVO

      let userList = [] // 人员列表
      let nodeHandleParentLevel = [] // 指定上级
      let nodeHandleRole = [] // 指定角色
      for (const { type, dataList } of handleUserList) {
        if (type === 0) {
          userList = dataList
        } else if (type === 1) {
          nodeHandleParentLevel = dataList.map(item => item.parentLevel)
        } else if (type === 2) {
          nodeHandleRole = dataList.map(item => item.roleId)
        }
      }

      this.advancedLimitTimeBO = {
        nodeHandleParentLevel,
        nodeHandleRole,
        handleType,
        examineType,
        limitTimeUnit,
        limitTime,
        handleUserList: userList
      }
    }

    this.getSuperLevel()
  },
  methods: {

    /**
     * @description: 上级下拉
     * @return {*}
     */
    getSuperLevel() {
      for (let index = 1; index <= 20; index++) {
        const label = `第${index}级上级`
        if (index === 1) {
          this.sendLevelOption.push({
            label: '直属上级',
            value: index
          })
        } else {
          this.sendLevelOption.push({
            label: label,
            value: index
          })
        }
      }
    },

    /**
     * @description: 节点限时处理开启关闭
     * @param {*} val
     * @return {*}
     */
    nodeLimitChange(val) {
      if (val) {
        this.form.nodeLimit = 3
        this.advancedLimitTimeBO.handleType = 3
        this.$set(this.advancedLimitTimeBO, 'examineType', 1)
        this.$set(this.advancedLimitTimeBO, 'limitTimeUnit', 'minute')
      }
    },

    /**
     * @description: 选择员工change
     * @param {*} userIds id数组
     * @param {*} _
     * @param {*} users 员工数组
     * @return {*}
     */
    selectUserChange(userIds, _, users) {
      if (users.length > 0) {
        if (this.currentAddType == 'nodeHandleType') {
          this.form['nodeHandleUser'] = users
        } else if (this.currentAddType == 'nodeLimit') {
          this.$set(this.advancedLimitTimeBO, 'handleUserList', users)
        }
      } else {
        this.selectedUserList = []
      }
    },

    /**
     * @description: 选择员工删除
     * @param {*} index
     * @param {*} type
     * @return {*}
     */
    userDelete(index, type) {
      if (type == 'nodeHandleType') {
        this.form.nodeHandleUser.splice(index, 1)
      } else if (type == 'nodeLimit') {
        this.advancedLimitTimeBO.handleUserList.splice(index, 1)
      }
      this.$forceUpdate()
    },

    /**
     * @description: 添加员工
     * @param {*} type 添加字段名称
     * @return {*}
     */
    addUserHandler(type) {
      console.log(type)
      this.currentAddType = type
      if (type == 'nodeHandleType') {
        this.selectedUserList = this.form.nodeHandleUser
      } else if (type == 'nodeLimit') {
        this.selectedUserList = this.advancedLimitTimeBO.handleUserList
      }
      this.userDialogVisible = true
    },

    /**
     * @description: 所有单选
     * @param {*} val 值
     * @param {*} field 字段名称
     * @return {*}
     */
    radioChange(val, field) {
      if (val == 2) {
        this.$set(this.advancedLimitTimeBO, 'examineType', 1)
      }
      if (field == 'nodeLimit') {
        this.$set(this.advancedLimitTimeBO, 'handleType', val)
      }
    },

    /**
     * @description: 更新值
     * @return {*}
     */
    emitEvent() {
      const copyForm = objDeepCopy(this.form)
      const copyConfigBO = objDeepCopy(this.advancedLimitTimeBO)
      if (!this.isStageFlow) {
        copyForm.nodeHandleUser = copyForm.nodeHandleUser?.map(item => item.userId)
      }
      copyConfigBO.handleUserList = copyConfigBO.handleUserList?.map(item => item.userId)
      copyForm.advancedLimitTimeBO = copyConfigBO
      if (this.form.privilege?.length > 0) {
        let twoPrivilege = ''
        for (let index = 0; index <= 3; index++) {
          if (this.form.privilege.includes(index)) {
            twoPrivilege += '1'
          } else {
            twoPrivilege += '0'
          }
        }
        copyForm.privilege = parseInt(twoPrivilege, 2)
      } else {
        copyForm.privilege = 0
      }
      delete copyForm.nodeLimit
      this.$emit('input', copyForm)
    },

    /**
     * @description: 超过时间调整
     * @param {*} val 时间
     * @return {*}
     */
    timeChange(val) {
      const timeType = this.advancedLimitTimeBO.limitTimeUnit
      if (isNaN(val) || val.includes('.')) {
        this.$set(this.advancedLimitTimeBO, 'limitTime', 0)
      } else if (timeType == 'minute' && val > NP.times(60, 24, 30)) {
        this.$set(this.advancedLimitTimeBO, 'limitTime', NP.times(60, 24, 30))
      } else if (timeType == 'hour' && val > NP.times(24, 30)) {
        this.$set(this.advancedLimitTimeBO, 'limitTime', NP.times(24, 30))
      } else if (timeType == 'day' && val > 30) {
        this.$set(this.advancedLimitTimeBO, 'limitTime', 30)
      }
    },

    /**
     * @description:超时时间单位调整
     * @param {*} val 单位
     * @return {*}
     */
    timeTypeChange(val) {
      const time = Number(this.advancedLimitTimeBO.limitTime)
      if (val == 'minute' && time > NP.times(60, 24, 30)) {
        this.$set(this.advancedLimitTimeBO, 'limitTime', NP.times(60, 24, 30))
      } else if (val == 'hour' && time > NP.times(24, 30)) {
        this.$set(this.advancedLimitTimeBO, 'limitTime', NP.times(24, 30))
      } else if (val == 'day' && time > 30) {
        this.$set(this.advancedLimitTimeBO, 'limitTime', 30)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.ad-set {
  padding: 0 10px;
  overflow: hidden;

  .ad-set-item {
    margin-top: 10px;

    .set-name-wrap {
      display: flex;

      .set-name {
        margin-right: 10px;
      }
    }

    .set-explain {
      margin-top: 10px;
      color: #5e6c84;
    }

    // 用户列表
    .user-list-wrap {
      margin-top: 10px;

      .user-item + .user-item {
        margin-left: 8px;
      }
    }

    .input-with-select {

    }
  }
}

.el-form-item {
  margin-bottom: 0;
}

.el-radio,
.el-checkbox {
  display: block;
  margin-top: 10px;
}

.is-flex {
  display: flex;
}

.el-radio-group {
  font-size: unset;
}

.small-title {
  margin-bottom: 5px;
}

</style>
