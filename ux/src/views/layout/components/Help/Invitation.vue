<template>
  <el-dialog
    :visible.sync="isShow"
    :show-close="false"
    class="feedback-dialog"
    append-to-body
    title="邀请同事"
    width="506px">
    <div class="dialog-content">
      <div class="text">
        通过链接邀请的同事接受邀约后需管理员审核，<br> 通过后即可登录企业平台。
      </div>
      <div class="field-item">
        <el-input
          v-model="text"
          :rows="6"
          type="textarea" />
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button
        v-clipboard:copy="text"
        v-clipboard:success="clipboardSuccess"
        type="primary">复制链接</el-button>
      <el-button @click="visibleChange(false)">取消</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { mapGetters } from 'vuex'
import clipboard from '@/directives/clipboard/index.js' // use clipboard by v-directive

export default {
  name: 'Invitation',
  directives: {
    clipboard
  },
  components: {},
  props: {
    value: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      text: ''
    }
  },
  computed: {
    ...mapGetters([
      'name',
      'userInfo'
    ]),
    isShow: {
      get() {
        return this.value
      },
      set(val) {
        this.visibleChange(val)
      }
    }
  },
  watch: {},
  created() {
    this.text = `【${this.name}】企业管理员${this.$getUserName(this.userInfo)}给你发来链接 https://www.baidu.com ，点击进入悟空云一起协作工作吧！链接24小时内有效，为确保企业信息安全，切勿随意传播该条信息，客户服务热线4000812558【悟空云】`
  },
  mounted() {

  },
  methods: {
    visibleChange(isShow) {
      this.$emit('input', isShow)
    },

    /**
     * 复制地址
     */
    clipboardSuccess() {
      this.$message.success('复制成功')
    }
  }
}
</script>

<style lang="scss" scoped>
.feedback-dialog {
  .el-dialog__header {
    padding: 25px 32px 10px;

    .el-dialog__title {
      font-size: 20px;
      line-height: 24px;
    }
  }

  .el-dialog__body {
    padding: 6px 32px 10px;

    .text {
      margin-bottom: 8px;
      font-size: 14px;
      line-height: 20px;
    }

    .field-item {
      margin-top: 16px;
    }
  }

  .el-dialog__footer {
    background-color: #fff;
    border-top: none;
  }
}
</style>
