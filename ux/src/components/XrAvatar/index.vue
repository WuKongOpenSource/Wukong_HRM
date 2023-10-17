<template>
  <el-popover
    v-if="id"
    v-model="popoverShow"
    :visible-arrow="false"
    :trigger="trigger"
    :disabled="popoverDisabled"
    class="xr-avatar"
    placement="bottom"
    width="250"
    popper-class="no-padding-popover">
    <xr-user-view
      v-loading="loading"
      :data="userData"
      :src="imageCache[src]" />
    <el-avatar
      slot="reference"
      :key="src"
      v-bind="$attrs"
      :src="imageCache[src]"
      :style="{ fontSize: fontSize, background: background }"
      :class="{ 'cursor-pointer': !disabled }"
      :size="size"
      fit="fill">{{ showName }}</el-avatar>
  </el-popover>

  <el-tooltip
    v-else
    class="xr-avatar"
    :content="name"
    effect="dark"
    placement="top">
    <el-avatar
      :key="src"
      v-bind="$attrs"
      :src="imageCache[src]"
      :style="{ fontSize: fontSize, background: background }"
      :class="{ 'cursor-pointer': !disabled }"
      :size="size"
      fit="fill">{{ showName }}</el-avatar>
  </el-tooltip>

</template>

<script>
import { adminUsersReadAPI } from '@/api/user/personCenter'
import { getImageData } from '@/utils'
import XRTheme from '@/styles/xr-theme.scss'
import store from '@/store'

export default {
  // Avatar 头像
  name: 'XrAvatar',
  components: {
    XrUserView: () => import('../XrUserView')
  },
  inheritAttrs: false,
  props: {
    name: String,
    id: [Number, String],
    size: {
      type: [Number, String],
      default: 38
    },
    src: String,
    disabled: {
      type: Boolean,
      default: false
    }, // 仅看文字
    trigger: {
      type: String,
      default: 'hover'
    },
    background: {
      type: String,
      default: XRTheme.colorPrimary
    }
  },
  data() {
    return {
      popoverShow: false,
      loading: false,
      userData: null,
      defaultHead: require('@/assets/img/head.png')
    }
  },
  computed: {
    imageCache() {
      return store.state.app.imageCache
    },
    fontSize() {
      if (this.size <= 30) {
        return '12px'
      }
      return '14px'
    },

    showName() {
      return this.name && this.name.length > 2 ? this.name.slice(-2) : this.name
    },

    popoverDisabled() {
      if (this.disabled) {
        return true
      }

      return !this.id
    }
  },
  watch: {
    popoverShow(val) {
      if (!this.userData) {
        this.getUserData()
      }
    },
    src: {
      handler() {
        this.handleImage()
      },
      immediate: true
    }
  },
  created() {},

  beforeDestroy() {},
  methods: {
    handleImage() {
      if (this.src) {
        if (!this.imageCache.hasOwnProperty(this.src)) {
          this.$set(this.imageCache, this.src, '')
          this.$store.commit('SET_IMAGECACHE', this.imageCache)
          if (this.src.startsWith('data:')) return
          getImageData(this.src)
            .then(data => {
              this.$set(this.imageCache, this.src, data.src)
              this.$store.commit('SET_IMAGECACHE', this.imageCache)
            })
            .catch(() => {
              delete this.imageCache[this.src]
              this.$store.commit('SET_IMAGECACHE', this.imageCache)
            })
        }
      }
    },

    getUserData() {
      adminUsersReadAPI({
        userId: this.id
      })
        .then(res => {
          this.userData = res.data
        })
        .catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.cursor-pointer {
  cursor: pointer;
}

.el-avatar {
  ::v-deep img {
    width: 100%;
    background: white !important;
  }
}
</style>
