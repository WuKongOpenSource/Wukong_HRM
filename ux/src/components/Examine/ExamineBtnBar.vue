<template>
  <div
    class="examine-btn-bar"
    :style="!examineMoreHandle.length || isStageFlow ? 'justify-content: start;' : ''">
    <el-button
      v-if="examineInfo.isCheck == 1"
      type="success"
      icon="wk wk-success"
      @click="examineHandle(1)">通过</el-button>
    <el-button
      v-if="examineInfo.isCheck == 1"
      type="danger"
      :style="!examineMoreHandle.length || isStageFlow ? 'margin-left: 10px;' : ''"
      icon="wk wk-close"
      @click="examineHandle(2)">拒绝</el-button>

    <el-button
      v-if="isOptionalNode"
      type="primary"
      @click="examineHandle('selectExamineUser')">{{ isOptionalNode.addBtnName }}</el-button>

    <el-button
      v-if="!isOptionalNode && superHandleShow"
      type="primary"
      style="margin-left: auto;"
      @click="examineHandle(19)">恢复</el-button>

    <el-dropdown
      v-if="examineMoreHandle.length && !isOptionalNode"
      :style="isStageFlow ? 'margin-left: 10px;' : ''"
      class="more-handle"
      @command="moreHandler">
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
              @command="moreHandler">
              <div>转他人处理</div>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="selectUser">选择员工</el-dropdown-item>
                <el-dropdown-item command="external">选择外部联系人</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
            <span
              v-else
              @click.stop="moreHandler('selectUser')">转他人处理</span>
          </template>
          <template v-else>
            {{ item.label }}
          </template>
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script type="text/javascript">

export default {
  name: 'ExamineBtnBar',
  props: {
    examineInfo: Object, // 审批信息
    examineMoreHandle: Array, // 更多操作按钮
    // 限制展示
    isOptionalNode: Object, // 当前节点是否为自选节点并可添加人员  null 不是
    isStageFlow: Boolean, // 是否为  阶段流程
    superHandleShow: Boolean // 是否展示超级管理员操作
  },
  methods: {
    /**
     * @description: 审批操作
     * @return {*}
     */
    examineHandle(status) {
      this.$emit('examineHandle', status)
    },

    /**
     * @description: 更多操作
     * @param {*} command
     * @return {*}
     */
    moreHandler(command) {
      this.$emit('moreHandler', command)
    }
  }
}
</script>

<style lang="scss" scoped>
.examine-btn-bar {
  text-align: right;
  background-color: $--color-n0;

  .el-button + .el-button {
    margin-left: unset;
  }

  .el-button.el-button--danger {
    margin-left: unset;
  }

  .xr-btn--green,
  .xr-btn--red {
    color: white;
  }
}
</style>
