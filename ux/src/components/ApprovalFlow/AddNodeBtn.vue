<template>
  <div class="add-node-btn">
    <el-popover
      v-if="showAdd"
      v-model="popoverShow"
      :visible-arrow="false"
      placement="right"
      trigger="click"
      popper-class="no-padding-popover">
      <div class="add-node-approve">
        <div v-if="WkApproveFlow.config.examineAddShow" class="add-node-approve-btn" @click="selectClick('approve')">
          <i class="wk wk-associate is-orange" />审批人
        </div>
        <div v-if="WkApproveFlow.config.conditionAddShow" class="add-node-approve-btn" @click="selectClick('condition')">
          <i class="wk wk-approve is-green" />条件分支
        </div>
        <div v-if="WkApproveFlow.config.copyAddShow" class="add-node-approve-btn" @click="selectClick('copy')">
          <i class="wk wk-source-line" />抄送节点
        </div>
        <div v-if="WkApproveFlow.eventObj.copy" class="add-node-approve-btn" @click="selectClick('paste')">
          <i class="wk wk-icon-transfer2" />粘贴
        </div>
      </div>
      <el-button slot="reference" type="primary" icon="el-icon-plus" circle />
    </el-popover>
  </div>
</template>

<script>
export default {
  // 添加
  name: 'AddNodeBtn',

  components: {},

  inject: ['WkApproveFlow'],

  props: {},

  data() {
    return {
      popoverShow: false
    }
  },

  computed: {
    showAdd() {
      return this.WkApproveFlow.config.examineAddShow ||
      this.WkApproveFlow.config.conditionAddShow ||
      this.WkApproveFlow.config.copyAddShow
    }
  },

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 选择
     */
    selectClick(command) {
      this.popoverShow = false
      this.$emit('command', command)
    }
  }
}
</script>
