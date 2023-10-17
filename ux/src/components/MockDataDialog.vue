<template>
  <el-dialog
    :visible.sync="dialogVisible"
    width="530px"
    :before-close="handleClose"
    append-to-body>
    <div class="dialog-content">
      <div class="title">悟空模拟数据</div>
      <div class="article">
        为了帮助您更好地了解和使用悟空系统，我们在系统的各个版块为您模拟了相应的虚拟数据，您可以通过这些数据获得更好的系统使用体验。
      </div>
      <div class="tips">注：模拟数据清除后方可添加您的真实数据。</div>
      <el-button type="primary" @click="handleClose">
        立即体验模拟数据
      </el-button>
      <div class="tips">
        <span>关于模拟数据的清除：点击导航栏右侧设置按钮</span>
        <span class="wk wk-icon-setting icon-box" />
        <span>进入企业管理后台，点击【初始化数据】选择要清除的各个应用的模拟数据。</span>
      </div>
    </div>

    <div slot="footer" class="dialog-footer">
      <!--<el-button
        type="text"
        class="btn-cancel"
        @click="handleClose">
        <flexbox align="center">
          <span class="smile">☺</span>
          <span>不了谢谢，我对系统已经熟悉</span>
        </flexbox>
      </el-button>-->
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'MockDataDialog',
  data() {
    return {
      dialogVisible: true
    }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
      this.$emit('close')
    },

    handleConfirm() {}
  }
}
</script>

<style scoped lang="scss">
::v-deep .el-dialog {
  background: url("~@/assets/img/mock/bottom_bg.png") no-repeat bottom right white;
  background-size: 198px;

  .el-dialog__header {
    height: 0;
    padding: 0;
  }

  .el-dialog__headerbtn {
    position: absolute;
    top: -32px;
    right: -32px;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 28px;
    height: 28px;
    color: white;
    border: 2px solid white;
    border-radius: 50%;

    .el-dialog__close {
      color: inherit;
    }
  }

  .el-dialog__body {
    padding: 0 24px;
    background: url("~@/assets/img/mock/top_bg.png") no-repeat top left;
    background-size: 116px;
  }

  .el-dialog__footer {
    padding: 30px 24px;
  }
}

.dialog-content {
  line-height: 1.5;

  .title {
    padding: 30px 0;
    font-size: 24px;
    text-align: center;
  }

  .article {
    margin-bottom: 5px;
  }

  .tips {
    font-size: $--font-size-small;
    color: $--color-n90;
  }

  .el-button {
    padding: 10px 36px;
    margin: 25px 50%;
    transform: translateX(-50%);
  }

  .icon-box {
    padding: 6px;
    margin: 0 3px;
    font-size: inherit;
    color: $--color-primary;
    background-color: rgba($--color-primary, 0.1);
    border-radius: 50%;
  }
}

.dialog-footer {
  .btn-cancel {
    font-size: $--font-size-small;
    color: $--color-n90;
  }

  .smile {
    margin-right: 5px;
    font-size: 18px;
    line-height: 1;
    vertical-align: middle;
  }
}
</style>
