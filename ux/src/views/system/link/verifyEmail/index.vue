<template>
  <div class="link-wrapper">
    <div class="container">
      <img class="logo" src="https://file.72crm.com/static/pc/images/logo-yun.png">
      <img
        class="pic"
        :src="img"
        alt="">
      <div class="title" :class="{ 'is-primary': isPrimary }">{{ title }}</div>
      <div v-if="showDesc" class="desc">{{ desc }}</div>
      <div v-if="showCountdown" class="desc"><span class="is-red">{{ count }}</span>S后自动返回系统</div>
      <el-button
        v-if="showButton"
        type="primary"
        @click="goClick">立即跳转</el-button>
    </div>
  </div>
</template>

<script>
import { adminCompanyManagerVerifyEmailAPI } from '@/api/adminCompanyManager'

import { getAuth } from '@/utils/auth'

export default {
  // 链接跳转 内置 验证成功 query  type email k 参数
  name: 'VerifyEmail',

  components: {
  },

  props: {},

  data() {
    return {
      title: '',
      isPrimary: false, // 标题变蓝
      desc: '',
      showDesc: false,
      count: 5, // 默认秒数
      showCountdown: false, // 展示倒计时
      intervalId: null, // 定时器
      showButton: true,
      img: require('./img/success.png')
    }
  },

  computed: {},

  watch: {},

  created() {},

  mounted() {
    const { query } = this.$route
    if (query.type === 'email' && query.k) {
      // 走邮箱验证逻辑
      this.verifyEmailInfo()
    }
  },

  beforeDestroy() {},

  methods: {
    /**
     * @description: 立即跳转
     * @param {*}
     * @return {*}
     */
    goClick() {
      getAuth().then(res => {
        this.$router.push('/')
      }).catch(() => {
        this.$router.push('/login')
      })
    },

    /**
     * 开启倒计时
     */
    startCountdown() {
      this.count = 5
      if (this.intervalId) {
        clearInterval(this.intervalId)
        this.intervalId = null
      }

      this.intervalId = setInterval(() => {
        this.count--
        if (this.count === 0) {
          clearInterval(this.intervalId)
          this.intervalId = null
          this.goClick()
        }
      }, 1000)
    },

    /**
     * @description: 验证邮箱信息  1 验证成功，2 已过期或不存在，3 验证失败
     * @param {*}
     * @return {*}
     */
    verifyEmailInfo() {
      const { k, edit } = this.$route.query
      adminCompanyManagerVerifyEmailAPI(k).then(res => {
        const resData = res.data || {}
        if (resData === 1) {
          this.title = edit === '1' ? '恭喜您，邮箱地址修改成功' : '恭喜您，账户验证成功'
          this.isPrimary = true
          this.showDesc = false
          this.showCountdown = true
          this.showButton = true
          this.startCountdown()
          this.img = require('./img/success.png')
        } else {
          this.title = edit === '1' ? '无效的邮箱修改链接，请确认后重新尝试' : '无效的账户验证链接，请确认后重新尝试'
          this.isPrimary = false
          this.showDesc = false
          this.showCountdown = false
          this.showButton = false
          this.img = require('./img/error.png')
        }
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.link-wrapper {
  position: relative;
  background-color: $--background-color-base;

  .container {
    position: absolute;
    top: 40%;
    left: 50%;
    width: 86%;
    max-width: 624px;
    padding: 40px;
    text-align: center;
    background-color: $--color-white;
    border: $--border-base;
    border-radius: $--border-radius-base;
    box-shadow: $--box-shadow-base;
    transform: translate(-50%, -50%);

    .logo {
      position: absolute;
      top: -56px;
      left: 50%;
      width: 136px;
      height: 42px;
      transform: translateX(-50%);
    }

    .pic {
      width: 114px;
      height: 114px;
    }

    .title {
      margin-top: 32px;
      font-size: 22px;

      &.is-primary {
        color: $--color-primary;
      }
    }

    .desc {
      margin-top: 5px;
      color: $--color-text-secondary;

      .is-red {
        margin: 0 4px;
        color: $--color-r400;
      }
    }

    .el-button {
      margin-top: 64px;
    }
  }
}
</style>
