<template>
  <div class="condition-node">
    <div class="condition-node-wrap">
      <div :class="{'is-error': node.isError, 'is-disabled': disabled}" class="condition-wrap" @click="click">
        <div class="header">
          <el-input
            v-if="isEdit"
            ref="wkFlowInput"
            v-model="node.conditionName"
            type="text"
            size="mini"
            maxlength="20"
            @blur="nameInputBlur" />
          <span
            v-else
            class="title"
            @click.stop="titleEditClick">{{ node.conditionName }}</span>
          <span
            class="priority">优先级{{ index + 1 }}</span>
          <i
            v-if="!disabled"
            class="wk wk-copy"
            @click.stop="copyClick" />
          <i
            v-if="!disabled"
            class="el-icon-close close"
            @click.stop="deleteClick" />
        </div>
        <div class="content">
          <template v-if="node.conditionDataList && node.conditionDataList.length > 0">
            {{ getConditonContent() }}
          </template>
          <template v-else>
            无条件
          </template>
        </div>
        <div v-if="modal" class="wk-flow-modal" />
      </div>
      <add-node-btn @command="handleCommand" />
    </div>
  </div>
</template>

<script>
import AddNodeBtn from './AddNodeBtn'

import { examineModel, conditionListModel, copyModel } from './flowModel'
import { objDeepCopy } from '@/utils'
import {
  selectOptions,
  checkboxOptions,
  numberOptions,
  numberValueOptions,
  getOptionObj
} from './conditioModel'
import { isEmpty } from '@/utils/types'

export default {
  // 条件
  name: 'WkConditionNode',

  components: {
    AddNodeBtn
  },

  inject: ['WkApproveFlow'],

  props: {
    index: Number,
    disabled: {
      type: Boolean,
      default: false
    },
    modal: {
      type: Boolean,
      default: false
    }, // 是否需要遮罩层
    parent: Array,
    conditionParent: Array,
    node: {
      type: Object,
      default() {
        return {}
      }
    }
  },

  data() {
    return {
      selectOptionsObj: getOptionObj(selectOptions),
      checkboxOptionsObj: getOptionObj(checkboxOptions),
      numberOptionsObj: getOptionObj(numberOptions),
      numberValueOptionsObj: getOptionObj(numberValueOptions),
      isEdit: false
    }
  },

  computed: {},

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 输入失去焦点
     */
    nameInputBlur() {
      this.isEdit = false
      if (this.node.conditionName === '') {
        this.node.conditionName = '条件'
      }
    },

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
      this.$emit('delete')
    },

    /**
     * 添加
     */
    handleCommand(command) {
      if (command === 'approve') {
        this.parent.splice(0, 0, objDeepCopy(examineModel))
      } else if (command === 'condition') {
        const removeItems = this.parent.splice(0)
        const copyConditionListModel = objDeepCopy(conditionListModel)
        copyConditionListModel.conditionList[1].examineDataList = removeItems
        this.parent.push(copyConditionListModel)
      } else if (command === 'copy') {
        this.parent.splice(this.index + 1, 0, objDeepCopy(copyModel))
      } else if (command === 'paste') {
        const pasteNode = objDeepCopy(objDeepCopy(this.WkApproveFlow.eventObj.copy?.node || examineModel))
        pasteNode.name = `${pasteNode.name}（复制）`
        this.parent.unshift(pasteNode)
      }
    },

    /**
     * @description: 复制条件节点链
     * @return {*}
     */
    copyClick() {
      const copyNode = objDeepCopy(this.node)
      copyNode.conditionName = `${copyNode.conditionName}（复制）`
      this.conditionParent.splice(this.index + 1, 0, copyNode)
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
     * 获得条件内容
     */
    getConditonContent() {
      const conditionDataList = this.node.conditionDataList

      return conditionDataList.map(children => {
        return this.getSectionConditionDes(children)
      }).join(' 或 ')
    },

    /**
     * @description: 获取条件描述
     * @return {*}
     */
    getSectionConditionDes(children) {
      if (!Array.isArray(children)) return ''
      return children.map(item => {
        if (item.type === 3) {
          let values = item.values
          if (item.setting) {
            const showValues = []
            item.setting.forEach(setting => {
              if (setting.value != undefined && item.values.includes(setting.value)) {
                showValues.push(setting.label)
              }
            })
            if (showValues.length > 0) {
              values = showValues
            }
          }
          return `${item.name} ${this.selectOptionsObj[item.conditionType]} ${values.join('或')}`
        } else if (item.type === 9) {
          return `${item.name} ${this.checkboxOptionsObj[item.conditionType]} ${item.values.join('、')}`
        } else {
          if (item.conditionType === 6) {
            return `${isEmpty(item.leftValue) ? '' : item.leftValue} ${this.numberValueOptionsObj[item.leftCondition]} ${item.name} ${this.numberValueOptionsObj[item.rightCondition]} ${isEmpty(item.rightValue) ? '' : item.rightValue}`
          } else if (item.conditionType === 8) {
            return `${item.name}属于 ${item.deptList.map(item => $getDeptName(item)).join('或')}${item.deptList.length > 0 && item.userList.length > 0 ? '或' : ''}${item.userList.map(item => $getUserName(item)).join('或')}${item.userList.length > 0 && item.roleList.length > 0 ? '或' : ''}${item.roleList.map(item => item.roleName).join('或')}`
          } else {
            return `${item.name} ${this.numberOptionsObj[item.conditionType]} ${item.values}`
          }
        }
      }).join(' 并且 ')
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
