<template>
  <div class="navbar">
    <flexbox style="width: auto;">
      <img
        :key="logo"
        v-src="logo"
        class="logo"
        @click="enterMainPage">
      <div class="nav-title">
        系统设置
      </div>
    </flexbox>
    <flexbox style="width: auto;">
      <el-button
        type="primary"
        @click="enterHome">返回首页</el-button>
      <el-button
        @click="enterLogin">退出系统</el-button>
    </flexbox>
  </div>
</template>

<script>
import { Loading } from 'element-ui'
import { mapGetters } from 'vuex'

export default {
  components: {},
  props: {
    navIndex: String
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['logo', 'crm'])
  },
  mounted() {},
  methods: {
    enterHome() {
      this.$router.replace({
        path: '/'
      })
    },
    enterLogin() {
      this.$confirm('退出登录？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          var loading = Loading.service({
            target: document.getElementById('#app')
          })
          this.$store
            .dispatch('LogOut')
            .then(() => {
              loading.close()
              location.reload()
            })
            .catch(() => {
              loading.close()
              location.reload()
            })
        })
        .catch(() => {})
    },

    /**
     * 有客户权限点击logo 进入仪表盘
     */
    enterMainPage() {
      this.$router.push('/')
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.navbar {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 12px;
  background-color: white;

  .logo {
    display: block;
    flex-shrink: 0;
    width: 24px;
    height: 24px;
    margin-left: 48px;
    cursor: pointer;
    background-color: white;
  }

  .nav-title {
    margin-left: 8px;
    font-size: 18px;
    color: $--color-n600;
  }
}

</style>

