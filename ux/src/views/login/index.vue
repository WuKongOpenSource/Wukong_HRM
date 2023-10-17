<template>
  <div class="login">
    <div class="login__content">
      <div class="login-title">
        {{ companyInfo.name !== null && companyInfo.name !== '' ? companyInfo.name : '悟空软件' }}
      </div>

      <login-by-pwd
        v-if="loginType === 'loginPwd'"
        :username.sync="username"
      />

    </div>

    <div class="footer">
      <img v-if="companyInfo.logo" :src="companyInfo.logo" class="company-logo">
      <div v-else class="footer-title"><img src="@/assets/img/logo.png" class="logo"><span>悟空软件</span></div>
      <div class="footer-des">一个账户可以访问 悟空CRM、悟空FS、悟空HRM 以及其他产品</div>
    </div>
  </div>
</template>

<script>
import { adminSystemIndexAPI } from '@/api/admin/config'

import LoginByPwd from './component/LoginByPwd'

import Lockr from 'lockr'
import { updateNavLinkName } from '@/utils'
import { LOCAL_LOGIN_LOGO_NAME } from '@/utils/constants.js'

export default {
  // 登录
  name: 'Login',

  components: {
    LoginByPwd
  },

  props: {},

  data() {
    return {
      loginType: 'loginPwd', // loginPwd loginCode foregetPwd multiple
      username: '', // 登录账号
      companyList: [],
      companyInfo: {
        name: '',
        logo: ''
      },
      loginParams: null // 登录参数，多企业二次登录需要
    }
  },

  computed: {},

  watch: {
  },

  created() {
    this.handleLoginCache('get')
    this.getLogoAndName()
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 获取logo 和 名称
     */
    getLogoAndName() {
      adminSystemIndexAPI().then(res => {
        const resData = res.data
        if (resData) {
          this.handleLoginCache('set', resData)
        }
      }).catch(() => {})
    },

    /**
     * @description: 操作登录缓存
     * @param {*} type get  set
     * @return {*}
     */
    handleLoginCache(type, data) {
      const hostname = window.location.hostname
      if (type === 'get') {
        // 读取缓存
        updateNavLinkName()

        const logCacheInfo = Lockr.get(LOCAL_LOGIN_LOGO_NAME)
        if (logCacheInfo && logCacheInfo[hostname]) {
          const domainData = logCacheInfo[hostname]
          this.companyInfo = {
            name: domainData.companyName,
            logo: domainData.companyLoginLogo
          }

          // 更新导航菜单头信息
          updateNavLinkName(domainData)
        }
      } else if (type === 'set') {
        const logCacheInfo = Lockr.get(LOCAL_LOGIN_LOGO_NAME) || {}
        logCacheInfo[hostname] = data
        Lockr.set(LOCAL_LOGIN_LOGO_NAME, logCacheInfo)
        this.companyInfo.logo = data.companyLoginLogo
        this.companyInfo.name = data.companyName

        // 更新导航菜单头信息
        updateNavLinkName(data)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@media screen and (max-width: 1200px) {
  .login {
    background-image: none !important;
  }
}

.login {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 100vh;
  background-image: url("./img/left.png"), url("./img/right.png");
  background-repeat: no-repeat, no-repeat;
  background-attachment: fixed, fixed;
  background-position: left bottom, right bottom;
  background-size: 360px, 480px;
  -webkit-box-pack: justify;

  &__content {
    width: 400px;
    margin: 0 auto;
    text-align: center;
  }

  &-title {
    padding: 40px 0;
    font-size: 25px;
    font-weight: bold;
    line-height: 40px;
    color: $--color-primary;
  }

  .footer {
    width: 400px;
    padding: 24px 0;
    margin: 0 auto;
    font-size: 12px;
    color: $--color-text-secondary;
    text-align: center;

    &-title {
      font-size: 28px;
      font-weight: bold;
      color: $--color-text-regular;

      .logo {
        width: 32px;
        margin-right: $--interval-base;
      }

      .logo,
      span {
        vertical-align: middle;
      }
    }

    > .company-logo {
      width: 160px;
    }

    &-des {
      margin-top: 16px;
      font-size: 12px;
      color: $--color-text-secondary;
    }
  }

  .statement {
    margin-top: 24px;
    text-align: center;

    span {
      padding: 0 8px;
    }
  }
}
</style>
