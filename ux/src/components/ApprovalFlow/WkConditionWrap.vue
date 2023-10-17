<template>
  <div class="wk-conditon-node">
    <div
      class="wk-conditon-node-wrap">
      <div
        class="conditon-wrap-body"
        :class="{
          'hide-condition-add': !WkApproveFlow.config.conditionAddShow
        }">
        <el-button
          v-if="WkApproveFlow.config.conditionAddShow"
          class="add-btn"
          plain
          @click="addClick">添加条件</el-button>
        <div
          v-for="(item, itemIndex) in node.conditionList"
          :key="itemIndex"
          class="condition">
          <template v-if="itemIndex === 0">
            <div class="cover-line is-top-left" />
            <div class="cover-line is-bottom-left" />
          </template>
          <template v-if="itemIndex === node.conditionList.length - 1">
            <div class="cover-line is-top-right" />
            <div class="cover-line is-bottom-right" />
          </template>
          <wk-condition-node
            :index="itemIndex"
            :parent="item.examineDataList"
            :condition-parent="node.conditionList"
            :node="item"
            :disabled="item.disabled"
            :modal="item.modal"
            @delete="conditionDelete(itemIndex)"
            @node-click="nodeClick"
          />
          <template v-if="item.examineDataList && item.examineDataList.length > 0">
            <template
              v-for="(subItem, subIndex) in item.examineDataList">
              <wk-condition-wrap
                v-if="subItem.examineType === 0"
                :key="subIndex"
                :level="level + 1"
                :index="subIndex"
                :node="subItem"
                :parent="item.examineDataList" />
              <wk-node
                v-else
                :key="subIndex"
                :index="subIndex"
                :node="subItem"
                :disabled="subItem.disabled"
                :modal="subItem.modal"
                :header-color="getHeaderStyle(subItem).bgColor"
                :header-icon="getHeaderStyle(subItem).icon"
                :parent="item.examineDataList"
                @node-click="nodeClick"
                @on-handle="nodeHandleClick" />
            </template>
          </template>
        </div>
      </div>
      <div class="add-node-btn-wrap">
        <add-node-btn
          @command="handleCommand" />
      </div>
    </div>
  </div>
</template>

<script>
import AddNodeBtn from './AddNodeBtn'
import WkNode from './WkNode'
import WkConditionNode from './WkConditionNode'

import { conditionModel, examineModel, conditionListModel, copyModel } from './flowModel'
import { objDeepCopy } from '@/utils'

export default {
  // 条件node
  name: 'WkConditionWrap',

  components: {
    AddNodeBtn,
    WkNode,
    WkConditionNode
  },

  inject: ['WkApproveFlow'],

  props: {
    level: {
      type: Number,
      default: 0
    },
    index: Number,
    parent: Array,
    node: {
      type: Object,
      default() {
        return {}
      }
    }
  },

  data() {
    return {
      tree: null
    }
  },

  computed: {
    wrapAddHide() {
      // 为方式最后出现两个添加按钮，当条件只有一个时，隐藏加好
      return this.parent.length === this.index + 1 && this.node.conditionList.length === 1
    }
  },

  watch: {},

  created() {
    if (this.$parent.$options.name === 'WkConditionWrap') {
      this.tree = this.$parent.tree
    } else {
      this.tree = this
    }
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 条件添加
     */
    addClick() {
      this.node.conditionList.push(objDeepCopy(conditionModel))
    },

    /**
     * 条件删除
     */
    conditionDelete(index) {
      // 两个以上删除整条
      if (this.node.conditionList.length > 2) {
        this.node.conditionList.splice(index, 1)
      } else {
        // 只有两条的时候，把另外一条除第一个条件之后的有效节点，追加到当前节点位置
        // this.parent.splice(this.index, 1)
        const anotherCondition = index == 0 ? 1 : 0
        this.parent.splice(this.index, 1, ...this.node.conditionList[anotherCondition].examineDataList)
      }
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
     * 点击
     */
    nodeClick(data) {
      this.tree.$emit('node-click', data)
    },

    /**
     * 点击
     */
    nodeHandleClick(data) {
      this.tree.$emit('on-handle', data)
    },

    /**
     * @description: 通过审批类型 获取对应头部信息
     * @param {*} item
     * @return {*}
     */
    getHeaderStyle(item) {
      const defaultStyle = {
        bgColor: '#0052cc',
        icon: ''
      }

      // examineType 1 审批人 7 抄送
      // 发起人：#0052cc（蓝色）
      // 审批节点：#FF991F（橙色）
      // 抄送节点：#6B778C（灰色）
      // 填写节点：#0052cc（蓝色）
      // 添加数据：#36B37E（绿色）
      // 更新数据：#00B8D9（浅蓝色）
      if (item.examineType >= 1 && item.examineType <= 5) {
        defaultStyle.bgColor = '#FF991F'
        defaultStyle.icon = 'wk wk-approve-line'
      } else if (item.examineType === 7) {
        defaultStyle.bgColor = '#6B778C'
        defaultStyle.icon = 'wk wk-source-line'
      }

      return defaultStyle
    }
  }
}
</script>

<style lang="scss">
.hide-condition-add {
  margin-top: 0 !important;
}
</style>
