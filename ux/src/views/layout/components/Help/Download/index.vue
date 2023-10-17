<template>
  <div class="xr-download">
    <div class="xr-download__header">
      <img src="./download.png" class="download-img">
    </div>
    <div class="xr-download__title">悟空移动办公平台</div>
    <div class="down-des">
      您可以通过悟空移动端，实现销售管理CRM、人资管理HRM、财务管理FS。全面支持安卓、IOS，无缝接入企业微信、钉钉。
    </div>
    <flexbox
      justify="center"
      class="xr-download__body">
      <div v-for="(item, index) in list" :key="index" class="down-item">
        <img :src="item.icon">
        <div>{{ item.label }}</div>
      </div>
    </flexbox>

    <div class="xr-download__footer">
      <div class="logo"><i class="wk wk-icon-android" /><i class="wk wk-icon-ios" /></div>
      <div>扫描二维码，下载Android、iPhone客户端</div>
    </div>
  </div>
</template>

<script>

export default {
  name: 'Download',
  components: {
  },
  props: {},
  data() {
    return {
      list: [{
        icon: require('./crm.png'),
        label: '客户管理CRM',
        url: WKConfig.androidUrl
      }, {
        icon: require('./hrm.png'),
        label: '人资管理HRM',
        url: WKConfig.iOSUrl
      }, {
        icon: require('./fs.png'),
        label: '财务管理FS',
        url: WKConfig.winUrl
      }]
    }
  },
  computed: {},
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {}
}
</script>

<style lang="scss" scoped>
.xr-download {
  &__header {
    position: relative;
    height: 36vh;
    text-align: center;
    background: linear-gradient(to bottom, #0064fe, #0052cc);

    .download-img {
      height: 32vh;
      margin: 4vh;
    }

    .el-icon-close {
      position: absolute;
      top: 8px;
      right: 8px;
      font-size: 24px;
      color: white;
      cursor: pointer;
    }
  }

  &__title {
    margin-top: $--interval-base * 3;
    font-size: 37px;
    text-align: center;
  }

  &__body {
    margin-top: 20px;
  }

  &__footer {
    margin-top: 32px;
    font-size: $--font-size-small;
    color: $--color-text-regular;
    text-align: center;

    .logo {
      margin-bottom: 8px;

      i {
        font-size: 18px;
      }

      i + i {
        margin-left: 8px;
      }
    }
  }

  .down-item {
    text-align: center;

    > img {
      width: 108px;
      height: 108px;
    }

    > div {
      margin-top: 8px;
      color: $--color-n500;
    }
  }

  .down-item + .down-item {
    margin-left: 56px;
  }

  .down-des {
    padding: 24px 16%;
    font-size: $--font-size-large;
    line-height: 1.5;
    color: $--color-text-regular;
    text-align: center;
  }
}
</style>
