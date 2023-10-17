<template>
  <div class="wrapper">
    <div class="http404-container">
      <img
        class="pic"
        src="@/assets/404/2.png"
        alt="">
      <div class="title">抱歉，您要访问的页面丢失了</div>
      <div class="desc">请检查您输入的网址是否正确，请点击以下按钮返回首页</div>
      <el-button
        type="primary"
        @click="handleBack">
        返回首页
      </el-button>
    </div>

  </div>
</template>

<script>
export default {
  name: 'Page404',
  methods: {
    handleBack() {
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
    width: 185px;
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
