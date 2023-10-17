<template>
  <div class="wk-node">
    <div
      :class="{ 'is-first': !visibleArrow, 'is-error': node.isError, 'is-disabled': disabled }"
      class="wk-node-wrap"
      @click="click">
      <div
        :style="{
          backgroundColor: headerColor
        }"
        class="header">
        <i class="icon" :class="headerIcon" />
        <el-input
          v-if="isEdit"
          ref="wkFlowInput"
          v-model="node.name"
          type="text"
          size="mini"
          @blur="isEdit = false" />
        <span
          v-else
          class="title"
          @click.stop="titleEditClick">{{ node.name }}</span>
        <i
          v-if="!disabled"
          class="wk wk-copy"
          @click.stop="copyClick" />
        <i v-if="!disabled" class="el-icon-close close" @click.stop="deleteClick" />
      </div>
      <div class="body">
        <div v-if="showCustomContent" class="content">{{ node.content }}</div>
        <div v-else class="content">{{ getContent() }}</div>
        <i v-if="!disabled" class="el-icon-arrow-right" />
      </div>
      <div v-if="modal" class="wk-flow-modal" />
    </div>
    <div class="add-node-btn-wrap">
      <add-node-btn @command="handleCommand" />
    </div>
  </div>
</template>

<script>
import AddNodeBtn from './AddNodeBtn'

import { examineModel, conditionListModel, copyModel } from './flowModel'
import { examineTypeObj, wayTypeObj } from './nodeModel'
import { objDeepCopy } from '@/utils'

export default {
  // 审批人 发起人
  name: 'WkNode',

  components: {
    AddNodeBtn
  },

  mixins: [],

  inject: ['memberControlFun', 'WkApproveFlow'],

  props: {
    visibleArrow: {
      type: Boolean,
      default: true
    },
    index: Number,
    disabled: {
      type: Boolean,
      default: false
    },
    modal: {
      type: Boolean,
      default: false
    }, // 是否需要遮罩层
    headerColor: {
      type: String,
      default: '#FF991F'
    },
    headerIcon: String,
    parent: Array,
    showCustomContent: {
      type: Boolean,
      default: false
    }, // 展示自定义内容 node.content 内的
    node: {
      type: Object,
      default() {
        return {}
      }
    }
  },

  data() {
    return {
      isEdit: false,
      examineTypeObj: examineTypeObj,
      sendLevelObj: {},
      topLevelObj: {},
      levelObj: {}
    }
  },

  computed: {
    // 选择控件人员列表
    memberControlList() {
      return this.memberControlFun()
    }
  },

  watch: {},

  created() {
    for (let index = 1; index <= 20; index++) {
      const label = `第${index}级上级`
      this.levelObj[index] = label
      if (index === 1) {
        this.sendLevelObj[index] = '直属上级'
        this.topLevelObj[index] = '最高级上级'
      } else {
        this.sendLevelObj[index] = label
        this.topLevelObj[index] = label
      }
    }
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 编辑名称
     */
    titleEditClick() {
      if (!this.disabled) {
        this.isEdit = true
        this.$nextTick(() => {
          this.$refs.wkFlowInput.focus()
        })
      }
    },

    /**
     * 删除
     */
    deleteClick() {
      this.parent.splice(this.index, 1)
    },

    /**
     * 添加
     */
    handleCommand(command) {
      if (command === 'approve') {
        this.parent.splice(this.index + 1, 0, objDeepCopy(examineModel))
      } else if (command === 'condition') {
        const removeItems = this.parent.splice(this.index + 1)
        const copyConditionListModel = objDeepCopy(conditionListModel)
        copyConditionListModel.conditionList[1].examineDataList = removeItems
        this.parent.push(copyConditionListModel)
      } else if (command === 'copy') {
        this.parent.splice(this.index + 1, 0, objDeepCopy(copyModel))
      } else if (command === 'paste') {
        const pasteNode = objDeepCopy(objDeepCopy(this.WkApproveFlow.eventObj.copy?.node || examineModel))
        pasteNode.name = `${pasteNode.name}（复制）`
        this.parent.splice(this.index + 1, 0, pasteNode)
      }
    },

    /**
     * 获取内容
     * 指定成员：1.依次  展示员工名  2.或签/会签  展示N人或签/会签
上级：展示  第N级上级
角色：展示  角色名或签/会签
发起人自选：只展示类型名  发起人自选
连续多级主管：展示  从 直属上级 到 发起人向上的/组织架构中由上至下的第N级上级；当指定角色未勾选不超过发起人向上，展示 从 直属上级到 角色名
     */
    getContent() {
      const { examineType, type, userList, memberUserList, parentLevel, parentLevelList, roleIdList, roleList = [], overType, tempParentLevel, isAdd, isSelf, fieldName, parentLevelCheck = false } = this.node
      const examineTypeName = examineTypeObj[examineType]
      if (examineType === 1) {
        if (memberUserList?.length === 1) {
          return `${examineTypeName} 1人`
        } else if (memberUserList?.length > 1) {
          return `${memberUserList?.length}人${wayTypeObj[type]}`
        } else {
          return '请选择审批人'
        }
      } else if (examineType === 2) {
        return `${this.sendLevelObj[parentLevel]}`
      } else if (examineType === 3) {
        if (roleIdList && roleIdList.length > 0) {
          return `${roleList.map(role => role.roleName).join('，')}${wayTypeObj[type]}`
        } else {
          return '请选择审批人'
        }
      } else if (examineType === 4) {
        if (roleIdList && roleIdList.length > 0) {
          return `发起人从${roleList.map(role => role.roleName).join('，')}中自选`
        } else {
          return examineTypeName
        }
      } else if (examineType === 5) {
        if (type === 1) {
          if (!roleIdList && roleIdList.length > 0) {
            return '请选择审批人'
          } else {
            if (overType === 0) {
              return `从 直属上级 到 ${roleList.map(role => role.roleName).join('，')}`
            } else if (overType === 1) {
              return `从 直属上级 到 发起人向上的${this.levelObj[parentLevel]}`
            } else {
              return ''
            }
          }
        } else {
          return `从 直属上级 到 组织架构中由上至下的${this.topLevelObj[tempParentLevel]}`
        }
      } else if (examineType === 7) {
        let content = ''
        if (userList.length) {
          content += `指定抄送人${userList.length}人`
          // userList.forEach((o, index) => {
          //   content += `${o.realname}${index === userList.length - 1 ? '' : '，'}`
          // })
        }
        if (roleIdList && roleIdList.length) {
          content += content ? ' 指定角色：' : '指定角色：'
          roleList.forEach((o, index) => {
            content += `${o.roleName}${index === roleIdList.length - 1 ? '' : '，'}`
          })
        }
        if (parentLevelList && parentLevelList.length) {
          content += content ? ' 指定上级：' : '指定上级：'
          parentLevelList.forEach((o, index) => {
            content += `${o === 1 ? '直属上级' : `第${o}级上级`}${index === parentLevelList.length - 1 ? '' : '，'}`
          })
        }
        if (isAdd) {
          content += content ? ' 发起人自选' : '发起人自选'
        }
        if (isSelf) {
          content += content ? ' 抄送给发起人自己' : '抄送给发起人自己'
        }
        return content || '请选择抄送人'
      } else if (examineType === 8) {
        let content = ''
        let memberControlName = ''
        if (fieldName) {
          memberControlName = this.memberControlList.filter(item => item.fieldName == fieldName)[0].name

          content += memberControlName
        }

        if (parentLevelCheck) {
          content += parentLevel === 1 ? '直属上级' : `第${parentLevel}级上级`
        }

        if (type != 1) {
          content += wayTypeObj[type]
        }

        return content || '请选择审批人'
      }

      return ''
    },

    /**
     * 点击
     */
    click() {
      if (!this.disabled) {
        this.$emit('node-click', this.$props)
      }
    },

    /**
     * @description: 复制条件节点链
     * @return {*}
     */
    copyClick() {
      this.$emit('on-handle', {
        eventName: 'copy',
        ...this.$props
      })
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
