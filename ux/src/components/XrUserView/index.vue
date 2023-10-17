<template>
  <div class="xr-user-view">
    <div v-if="userData" class="xr-mian">
      <flexbox class="xr-mian__hd user">
        <div class="user-info">
          <div class="user-info__name">
            <span>{{ $getUserName(userData) }}</span>
            <i v-if="sexIcon" :class="sexIcon" />
          </div>
          <div class="user-info__company">
            {{ userData.companyName }}
          </div>
        </div>

        <xr-avatar
          :src="dataSrc"
          :name="$getUserName(userData)"
          :size="44"
          class="user-img" />
      </flexbox>
      <div class="xr-mian__bd">
        <flexbox class="info-cell">
          <i class="wk wk-department" />
          <div class="info-cell__label">部门</div>
          <div class="info-cell__value text-one-line">{{ $getDeptName(userData) }}</div>
        </flexbox>
        <flexbox class="info-cell">
          <i class="wk wk-tie" />
          <div class="info-cell__label">岗位</div>
          <div class="info-cell__value text-one-line">{{ userData.post }}</div>
        </flexbox>
        <flexbox class="info-cell">
          <i class="wk wk-b-mobile" />
          <div class="info-cell__label">手机</div>
          <div :title="userData.mobile" class="info-cell__value text-one-line">{{ userData.mobile }}</div>
        </flexbox>
        <flexbox class="info-cell">
          <i class="wk wk-email" />
          <div class="info-cell__label">邮箱</div>
          <div :title="userData.email" class="info-cell__value text-one-line">{{ userData.email }}</div>
        </flexbox>
      </div>
    </div>
  </div>
</template>

<script>
import { adminUsersReadAPI } from '@/api/user/personCenter'

export default {
  // 弹窗详情
  name: 'XrUserView',
  components: {},
  props: {
    id: [String, Number],
    data: Object,
    src: String
  },
  data() {
    return {
      userInfo: null
    }
  },
  computed: {
    sexIcon() {
      // 1 男 2 女
      if (this.userData.sex === 1) {
        return 'wk wk-man'
      } else if (this.userData.sex === 2) {
        return 'wk wk-woman'
      }
      return ''
    },

    dataSrc() {
      return this.userData ? this.$getUserImg(this.userData) : this.src
    },

    userData() {
      return this.userInfo || this.data
    }
  },
  watch: {
    id: {
      handler(val) {
        if (val) {
          this.userInfo = null
          this.getUserData()
        }
      },
      immediate: true
    }
  },
  mounted() {},

  beforeDestroy() {},
  methods: {
    getUserData() {
      adminUsersReadAPI({
        userId: this.id
      })
        .then(res => {
          this.userInfo = res.data
        })
        .catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.xr-user-view {
  min-height: 235px;
}

.xr-mian {
  min-height: 235px;

  &__hd {
    padding: 16px;
    background-color: $--background-color-base;
  }

  &__bd {
    padding: 20px 15px;
    padding-top: $--border-base;
  }
}

.user {
  &-info {
    flex: 1;

    &__name {
      font-size: 16px;
      font-weight: bold;

      i {
        margin-left: 8px;
        font-size: 14px;
      }

      .wk-woman {
        color: #ff3838;
      }

      .wk-man {
        color: #3875ff;
      }
    }

    &__company {
      margin-top: 8px;
      color: $--color-text-regular;
    }
  }

  &-img {
    flex-shrink: 0;
    margin-left: 15px;
  }
}

.info-cell {
  font-size: 14px;

  i {
    flex-shrink: 0;
    font-size: 14px;
    color: $--color-text-secondary;
  }

  &__label {
    flex-shrink: 0;
    width: 50px;
    margin-left: 5px;
    color: $--color-text-regular;
  }

  &__value {
    margin-left: 30px;
  }

  & + & {
    margin-top: 15px;
  }
}
</style>
