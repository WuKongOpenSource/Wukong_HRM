<template>
  <div class="wk-approve-flow-wrap">
    <el-button-group>
      <el-button
        class="group-btn"
        :disabled="scale <= 0.5"
        icon="el-icon-minus"
        @click="scaleClick('minus')" />
      <el-button
        class="group-btn"
        :disabled="scale > 3"
        icon="el-icon-plus"
        @click="scaleClick('plus')" />
    </el-button-group>
    <div
      :style="{
        transform: `scale(${scale})`
      }"
      class="wk-approve-flow">
      <wk-node
        v-if="sendNode"
        :visible-arrow="false"
        show-custom-content
        :node="sendNode"
        :parent="list"
        disabled
        header-color="#0052cc"
        header-icon="wk wk-visit-contract" />
      <template v-for="(item, index) in list">
        <wk-condition-wrap
          v-if="item.examineType === 0"
          :key="index"
          :index="index"
          :node="item"
          :parent="list"
          @node-click="nodeClick"
          @on-handle="nodeHandleClick" />
        <wk-node
          v-else
          :key="index"
          :index="index"
          :parent="list"
          :node="item"
          :disabled="item.disabled"
          :modal="item.modal"
          :header-color="getHeaderStyle(item).bgColor"
          :header-icon="getHeaderStyle(item).icon"
          @node-click="nodeClick"
          @on-handle="nodeHandleClick" />
      </template>
      <wk-end-node />
      <wk-condition-set
        :visible.sync="conditionSetVisible"
        :node="editNode"
        :condition-parent="conditionParent"
        :condition-parent-index="conditionParentIndex"
        :props="config"
      />
      <wk-node-set
        :visible.sync="nodeSetVisible"
        :node="editNode"
        :props="config"
      />
      <WkCopyNodeSet
        :visible.sync="copyNodeSetVisible"
        :node="editNode"
        :props="config"
      />
    </div>
  </div>
</template>

<script>
import WkNode from './WkNode'
import WkConditionWrap from './WkConditionWrap'
import WkEndNode from './WkEndNode'
import WkConditionSet from './WkConditionSet'
import WkNodeSet from './WkNodeSet'
import WkCopyNodeSet from './WkCopyNodeSet.vue'
import merge from '@/utils/merge'
import NodeSetMixin from './NodeSet'

const DefaultFlowProps = {
  // 条件选择数据参数
  conditionSelectRequest: null,
  conditionSelectParams: null,
  conditionSelectList: null, // 固定条件数据
  nodeFieldSetShow: false, // 节点字段设置展示
  nodeFieldItemParamsFun: null, // 节点字段每个需要追加的参数
  nodeFieldSetRequestFun: null, // 节点字段设置请求函数
  examineTypeFilterFun: null, // 审批类型过滤函数
  conditionAddShow: true, // 条件添加展示
  examineAddShow: true, // 审批人添加展示
  copyAddShow: true, // 抄送添加展示
  examineNodeProps: {
    addExternalUserShow: true // 默认支持添加外部联系人展示
  }
}

export default {
  name: 'WkApproveFlow',

  components: {
    WkNode,
    WkConditionWrap,
    WkEndNode,
    WkConditionSet,
    WkNodeSet,
    WkCopyNodeSet
  },

  mixins: [NodeSetMixin],

  inject: ['memberControlFun'],

  provide() {
    return {
      'WkApproveFlow': this
    }
  },

  props: {
    props: {
      type: Object,
      default: () => {
        return {}
      }
    },
    sendNode: Object,
    list: Array
  },

  data() {
    return {
      editNode: null,
      editNodeIndex: null,
      conditionParent: [],
      conditionParentIndex: 0,
      conditionSetVisible: false,
      nodeSetVisible: false,
      copyNodeSetVisible: false,
      errorList: [],
      scale: 1,

      // 记录操作事件的数据对象
      eventObj: {
        copy: null
      }
    }
  },

  computed: {
    config() {
      return merge({ ...DefaultFlowProps }, this.props || {})
    },

    // 选择控件人员列表
    memberControlList() {
      return this.memberControlFun()
    }
  },

  watch: {},

  created() {
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 查看详情
     */
    nodeClick(data) {
      const { node, index, conditionParent } = data
      this.editNode = node
      if (node.examineType > 0) {
        if (node.examineType == 7) {
          this.copyNodeSetVisible = true
        } else {
          this.nodeSetVisible = true
        }
      } else {
        this.conditionParent = conditionParent
        this.conditionParentIndex = index
        this.conditionSetVisible = true
      }
    },

    /**
     * @description: 节点操作click
     * @param {*} data
     * @return {*}
     */
    nodeHandleClick(data) {
      if (data.eventName === 'copy') {
        this.eventObj[data.eventName ] = data
        this.$message.success('复制成功')
      }
    },

    /**
     * 缩放
     */
    scaleClick(type) {
      if (type === 'minus') {
        this.scale = this.scale - 0.1
      } else if (type === 'plus') {
        this.scale = this.scale + 0.1
      }
    },

    /**
     * 获取参数
     */
    getParams(props = {}) {
      const DefaultParamsProps = {
        needUserFieldNamesParams: false // 需要审批节点关联人员字段用户名
      }
      const paramsConfig = merge({ ...DefaultParamsProps }, props)
      this.errorList = []
      const newList = []
      const paramsData = {
        userFieldNames: []
      } // 其他参数
      this.getListParams(this.list, newList, paramsConfig, paramsData)
      console.log(newList, this.errorList)
      if (this.errorList.length > 0) {
        return {
          isError: true,
          list: this.errorList,
          paramsData
        }
      } else {
        return {
          isError: false,
          list: newList,
          paramsData
        }
      }
    },

    getListParams(list, newList, paramsConfig, paramsData) {
      list.forEach(item => {
        // 条件
        if (item.examineType === 0) {
          newList.push(this.getConditonWrapParams(item, paramsConfig, paramsData))
        } else {
          newList.push(this.getNodeParams(item, paramsConfig, paramsData))
        }
      })
    },

    /**
     * 审批节点信息
     */
    getNodeParams(data, paramsConfig, paramsData) {
      data.isError = this.getWkNodeErrorStatus(data)

      if (data.isError) {
        this.errorList.push(data)
      }

      let dataParams = {
        examineType: data.examineType,
        name: data.name,
        deptList: data.deptList,
        // examineErrorHandling: data.examineErrorHandling,
        roleIdList: data.roleIdList,
        type: data.type,
        userList: data.userList.map(o => o.userId),
        chooseType: data.chooseType,
        rangeType: data.rangeType
      }

      // 指定成员
      if (data.examineType == 1) {
        dataParams.memberUserList = data.memberUserList
        dataParams.userList = []
      } else if (data.examineType === 5) {
        if (data.type === 1) {
          // overType 1 开启 0 不开启 parentLevel为0
          dataParams.parentLevel = data.overType === 1 ? data.parentLevel : 0
        } else if (data.type === 2) {
          dataParams.parentLevel = data.tempParentLevel
        }
      } else if (data.examineType === 7) {
        dataParams = {
          examineType: data.examineType,
          name: data.name,
          userList: data.userList.map(item => item.userId),
          isSelf: data.isSelf, // 是否通知本人
          isAdd: data.isAdd, // 是否允许添加
          roleIdList: data.roleIdList,
          parentLevelList: data.parentLevelList
        }
      } else if (data.examineType == 8) { // 关联人员字段
        const { parentLevelCheck = false, fieldName } = data
        dataParams.parentLevel = parentLevelCheck ? data.parentLevel : null
        dataParams.fieldName = fieldName
        if (fieldName) {
          const fieldId = this.memberControlList.filter(item => item.fieldName == fieldName)[0].fieldId
          dataParams.fieldId = fieldId

          // 获取关联人员的字段名
          if (paramsConfig.needUserFieldNamesParams) {
            paramsData.userFieldNames.push(fieldName)
          }
        }
      } else {
        dataParams.parentLevel = data.parentLevel
      }

      // 字段授权
      if (data.fieldAuthList) {
        dataParams.fieldAuthList = data.fieldAuthList
      }

      return dataParams
    },

    /**
     * 条件节点信息
     */
    getConditionNodeParams(data, index) {
      // data.isError = data.conditionDataList.length === 0
      data.isError = false
      if (data.isError) {
        this.errorList.push(data)
      }

      const dataParams = {
        conditionName: data.conditionName,
        sort: index + 1,
        conditionDataList: []
      }

      data.conditionDataList.forEach(children => {
        dataParams.conditionDataList.push(this.getSectionConditionParams(children))
      })

      return dataParams
    },

    /**
     * @description: 获取单个块的条件提交参数
     * @param {*} children
     * @return {*}
     */
    getSectionConditionParams(children) {
      return children.map(item => {
        const subItem = {
          name: item.name,
          fieldName: item.fieldName,
          type: item.type,
          fieldId: item.fieldId,
          conditionType: item.conditionType
        }

        if (item.type === 3 || item.type === 9) {
          subItem.values = item.values
        } else {
          if (item.conditionType === 6) {
            subItem.values = [item.leftValue, item.leftCondition, item.rightCondition, item.rightValue]
          } else if (item.conditionType === 8) {
            subItem.values = item.values
          } else {
            subItem.values = [item.values]
          }
        }
        return subItem
      })
    },

    getConditonWrapParams(conditionWrap, paramsConfig, paramsData) {
      const conditionWrapParams = {
        examineType: conditionWrap.examineType,
        name: conditionWrap.name,
        conditionList: []
      }
      conditionWrap.conditionList.forEach((item, index) => {
        // 条件
        const conditionNodeParams = this.getConditionNodeParams(item, index)
        conditionNodeParams.examineDataList = []

        conditionWrapParams.conditionList.push(conditionNodeParams)
        this.getListParams(item.examineDataList, conditionNodeParams.examineDataList, paramsConfig, paramsData)
      })

      return conditionWrapParams
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
      } else if (item.examineType === 8) {
        defaultStyle.bgColor = '#FF991F'
        defaultStyle.icon = 'wk wk-approve-line'
      }

      return defaultStyle
    }
  }
}
</script>

<style lang="scss">
@import "@/components/ApprovalFlow/flow.scss";
</style>
