<template>
  <el-popover
    :disabled="!hasContent"
    placement="bottom"
    width="200"
    trigger="hover">
    <el-image
      :src="url"
      :style="styleObj"
      style="width: 100%; height: 100%;"
      fit="contain" />
    <div slot="reference">
      <el-image
        v-if="hasContent"
        :src="url"
        :style="{height: height, ...styleObj}"
        fit="contain" />
      <template v-else>--</template>
    </div>
  </el-popover>
</template>

<script>
import { adminFileQueryOneByBatchIdAPI } from '@/api/admin/file'

import axios from '@/utils/request'
import { LOCAL_ADMIN_TOKEN } from '@/utils/constants.js'

export default {
  // WkSignatureImage
  name: 'WkSignatureImage',

  components: {},

  props: {
    height: {
      type: String,
      default: '100%'
    },
    src: String // batchId 交互
  },

  data() {
    return {
      url: '',
      styleObj: {}
    }
  },

  computed: {
    hasContent() {
      return !!this.url
    }
  },

  watch: {
    src: {
      handler(val) {
        if (val) {
          this.getData()
        } else {
          this.url = ''
        }
      },
      immediate: true,
      deep: true
    }
  },

  created() {
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    getData() {
      adminFileQueryOneByBatchIdAPI(this.src).then(res => {
        const resData = res.data
        if (resData) {
          const token = axios.defaults.headers.common[LOCAL_ADMIN_TOKEN]
          let url = process.env.VUE_APP_BASE_API + resData.url + `?c=${token}`
          url = url.replace('//', '/')
          if (this.url !== url) {
            this.url = url
          }
        } else {
          this.url = ''
        }
      }).catch(() => {

      })
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
