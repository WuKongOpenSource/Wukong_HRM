<template>
  <div class="verify-button">
    <el-button
      :disabled="disabled || time !== second"
      type="primary"
      @click="getSmsCode">
      <template v-if="getSmsShow">
        <span>获取验证码</span>
      </template>
      <template v-else>
        <span>重新发送({{ second }}s)</span>
      </template>
    </el-button>

    <Verify
      ref="verify"
      captcha-type="blockPuzzle"
      :img-size="{ width: '330px', height: '155px' }"
      @success="verifySuccess"
    />
  </div>
</template>

<script>
import Verify from '@/components/Verify'

export default {
  // 滑动验证加倒计时按钮
  name: 'VerifyButton',

  components: {
    Verify
  },

  props: {
    disabled: Boolean
  },

  data() {
    return {
      timer: null,
      time: 60,
      second: 60
    }
  },

  computed: {
    // 获取验证码展示
    getSmsShow() {
      return this.time === this.second
    }
  },

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {
    if (this.timer) {
      clearTimeout(this.timer)
      this.second = this.time
    }
  },

  methods: {
    /**
     * 获取验证码
     */
    getSmsCode() {
      if (this.getSmsShow) {
        this.$refs.verify.show()
      }
    },

    /**
     * @description: 验证成功
     * @param {*} params
     * @return {*}
     */
    verifySuccess(params) {
      this.$emit('success', params)
    },

    /**
     * @description: 重置
     * @return {*}
     */
    resetTimer() {
      if (this.timer) {
        clearTimeout(this.timer)
        this.timer = null
        this.second = this.time
      }
    },

    /**
     * 发送短信倒计时
     */
    startTimer() {
      if (this.second === this.time) {
        this.second--
      }
      this.timer = setTimeout(() => {
        this.second--
        if (this.second >= 0) {
          this.startTimer()
        } else {
          clearTimeout(this.timer)
          this.timer = null
          this.second = this.time
        }
      }, 1000)
    }
  }
}
</script>

<style lang="scss" scoped>
.verify-button {
  display: inline-block;

  .el-button {
    &.is-disabled,
    &.is-disabled:hover {
      color: $--color-n80;
      background-color: $--color-n30;
      border-color: $--color-n30;
    }
  }
}
</style>
