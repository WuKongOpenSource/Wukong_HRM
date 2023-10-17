<template>
  <div class="wrapper">
    <div class="http404-container">
      <img
        class="pic"
        src="@/assets/404/1.png"
        alt="">
      <div class="title">抱歉，您没有访问权限</div>
      <div class="desc">请联系贵公司管理员对您的账号进行授权。如果已被授权但无法进入主页，请稍后尝试重新登录</div>
      <div class="btn">
        <el-button type="primary" @click="handleBack">返回登录页</el-button>
        <el-button
          v-if="addRouters && addRouters.length >0"
          type="primary"
          @click="handleHome">返回首页</el-button>
      </div>
    </div>

  </div>
</template>

<script>
import { mapGetters } from 'vuex'

import { removeAuth } from '@/utils/auth'

export default {
  name: 'PageNoAuth',
  computed: {
    ...mapGetters([
      'addRouters'
    ])
  },
  methods: {
    handleBack() {
      removeAuth().then(() => {
        this.$router.push('/login')
      })
    },

    handleHome() {
      this.$router.push('/')
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.wrapper {
  position: relative;
}

.http404-container {
  position: relative;
  top: 50%;
  text-align: center;
  transform: translateY(-80%);

  .pic {
    width: 200px;
  }

  .title {
    width: 100%;
    margin-top: 40px;
    font-size: 26px;
  }

  .desc {
    width: 100%;
    margin-top: 12px;
    font-size: 14px;
    color: $--color-text-secondary;
    text-align: center;
  }

  .el-button {
    margin-top: 24px;
  }
}
</style>
