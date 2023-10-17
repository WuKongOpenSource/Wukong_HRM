<template>
  <flexbox align="flex-start" class="person-center">
    <div class="left">
      <flexbox class="user-box">
        <xr-avatar
          :name="userInfo.realname"
          :size="50"
          :src="userInfo.img"
          class="user-img" />
        <span class="username">
          {{ userInfo.realname }}
        </span>
      </flexbox>
      <ul class="nav-list">
        <li
          v-for="(item, index) in navList"
          :key="index"
          :class="{active: selectedIndex == item.index}"
          class="nav-list-item"
          @click="selectedIndex = item.index">
          <span :class="item.icon" class="wk icon" />
          <span class="text">
            {{ item.label }}
          </span>
        </li>
      </ul>
    </div>
    <div class="right">
      <edit-user-info
        v-if="selectedIndex == 0"
        @change="getDetail" />
      <edit-pwd v-if="selectedIndex == 1" />
    </div>
  </flexbox>
</template>

<script>
import { mapGetters } from 'vuex'
import EditUserInfo from './components/EditUserInfo'
import EditPwd from './components/EditPwd'

export default {
  name: 'PersonCenter',
  components: {
    EditUserInfo,
    EditPwd
  },
  data() {
    return {
      selectedIndex: 0 // 0 个人信息 1 账号密码 2 订单
    }
  },
  computed: {
    ...mapGetters([
      'userInfo'
    ]),
    navList() {
      const navs = [
        { label: '个人信息', icon: 'wk-user', index: 0 },
        { label: '账号密码', icon: 'wk-circle-password', index: 1 }
      ]
      return navs
    }
  },
  watch: {
    $route: {
      handler: function(val, oldVal) {
        if (this.userInfo.isAdmin && val.query && (val.query.selectedIndex || val.query.selectedIndex === 0)) {
          this.selectedIndex = val.query.selectedIndex
        }
      },
      deep: true
    }
  },
  created() {
    this.getDetail()
  },
  mounted() {
    if (this.userInfo && this.userInfo.isAdmin && this.$route.query && this.$route.query.selectedIndex) {
      this.selectedIndex = this.$route.query.selectedIndex
    }
  },
  methods: {
    getDetail() {
      this.loading = true
      this.$store.dispatch('GetUserInfo').then(() => {
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    }
  }
}
</script>

<style scoped lang="scss">
  .person-center {
    width: 1460px;
    margin: 0 auto;

    .left {
      width: 300px;
      padding-bottom: 100px;
      margin-right: 20px;
      background-color: white;

      .user-box {
        padding: 15px 20px 20px;

        .user-img {
          margin-right: 22px;
        }

        .username {
          flex: 1;
          font-size: 14px;
          color: $--color-text-regular;
        }
      }

      .nav-list {
        width: 100%;

        &-item {
          display: flex;
          align-items: center;
          justify-content: flex-start;
          height: 50px;
          padding: 0 20px;
          color: $--color-text-regular;
          cursor: pointer;

          .icon {
            margin-right: 10px;
          }

          &:hover,
          &.active {
            background-color: #f1f5f8;
          }
        }
      }
    }

    .right {
      flex: 1;
      overflow: hidden;
    }
  }
</style>
