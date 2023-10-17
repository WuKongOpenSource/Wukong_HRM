<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <div v-loading="loading">
    <div class="content-header">
      <span>活动资讯</span>
    </div>
    <div class="content-body">
      是否启用悟空活动资讯<el-switch
        v-model="value"
        style="margin-left: 8px;"
        @change="changeStatus" />
    </div>
  </div>
</template>

<script>
import { configSetMarketingAPI } from '@/api/config'

export default {
  name: 'MarketingSet',
  components: {},
  props: {},
  data() {
    return {
      loading: false,
      value: false
    }
  },
  computed: {},
  watch: {},
  created() {
    this.$store.dispatch('QueryMarketing').then(res => {
      this.value = res.data === 1
    })
  },

  beforeDestroy() {},
  methods: {
    changeStatus() {
      this.loading = true
      configSetMarketingAPI({
        status: this.value ? 1 : 0
      }).then(res => {
        this.$message.success('操作成功')
        this.$store.dispatch('QueryMarketing')
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import "./index.scss";
</style>
