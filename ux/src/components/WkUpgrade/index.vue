<template>
  <el-dialog
    :visible="visible"
    width="600px"
    title=""
    :close-on-press-escape="false"
    :close-on-click-modal="false"
    class="upgrade-dialog"
    @close="close">
    <div class="header">
      <img src="./bg.png">
    </div>
    <div class="main">{{ WKConfig.updateContent }}</div>

    <span
      slot="footer">
      <el-button
        type="primary"
        @click="close">我知道了</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { readUpdateNoticeAPI } from '@/api/config'

export default {
  props: {
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
      message: ''
    }
  },
  methods: {
    /**
     * @description: 公告内容
     * @param {*}
     * @return {*}
     */
    getContent() {
      this.message = WKConfig.updateContent
    },

    /**
     * @description: 关闭
     * @param {*}
     * @return {*}
     */
    close() {
      readUpdateNoticeAPI().then(res => {
      }).catch(() => {})
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style lang="scss" scoped>
.upgrade-dialog {
  ::v-deep .el-dialog__header {
    display: none;
  }

  ::v-deep .el-dialog__body {
    position: relative;
    padding: 0;
  }

  .header {
    position: relative;
    height: 188px;
    text-align: center;
    background-repeat: no-repeat;
    background-size: cover;

    img {
      position: absolute;
      top: -26px;
      left: 0;
      width: 100%;
      height: 196px;
    }
  }

  .main {
    max-height: 300px;
    padding: 0 46px 20px;
    overflow-y: auto;
    line-height: 1.5;
    word-break: break-all;
    word-wrap: break-word;
    white-space: pre-wrap;
  }
}

</style>

