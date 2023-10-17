<template>
  <el-autocomplete
    ref="autoComplete"
    v-model.trim="content"
    v-bind="$attrs"
    :fetch-suggestions="queryCustomerName"
    hide-loading
    @select="select"
    v-on="$listeners"
    @keyup.enter.native="enterSearch">
    <el-button
      slot="suffix"
      type="icon"
      icon="wk wk-sousuo"
      :loading="loading"
      @click="searchInput" />
  </el-autocomplete>
</template>

<script>
import { crmEnterpriseListAPI } from '@/api/premium/businessInfo'

export default {
  // 工商信息输入框
  name: 'WkBusinessInfoInput',

  components: {},

  inheritAttrs: false,

  props: {
    value: String
  },

  data() {
    return {
      content: '',
      search: '', // 搜索是传的字符串
      businessList: [],
      loading: false,
      enterActive: false, // enter 搜索处于激活状态，fetchSuggestions执行时进行查询请求
      fetchSuggestions: null
    }
  },

  computed: {},

  watch: {
    value: {
      handler() {
        if (this.value !== this.content) {
          this.content = this.value
        }
      },
      immediate: true
    },
    content() {
      if (this.value !== this.content) {
        this.$emit('input', this.content)
      }
    }
  },

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * @description: 客户名称搜索
     * @param {*} queryString 搜索字符串
     * @param {*} cb 结果回调方法
     * @return {*}
     */
    queryCustomerName(queryString, cb) {
      this.fetchSuggestions = { queryString, cb }
      if (this.enterActive) {
        this.enterActive = false
        this.searchInput()
      } else {
        cb(this.businessList)
      }
    },

    /**
     * @description: 开启搜索
     * @return {*}
     */
    searchInput() {
      if (!this.fetchSuggestions) return
      const { queryString, cb } = this.fetchSuggestions
      if (queryString !== '') {
        if (this.search !== queryString) {
          this.search = queryString
          this.loading = true
          crmEnterpriseListAPI(queryString).then(res => {
            const reaData = res.data || []
            this.businessList = reaData
            this.loading = false
            cb(this.businessList)
          }).catch(() => {
            this.loading = false
            cb([])
          })
        } else {
          cb(this.businessList)
        }
      } else {
        cb([])
      }
    },

    /**
     * @description: 回车触发搜索
     * @return {*}
     */
    enterSearch() {
      this.enterActive = true
      this.$refs.autoComplete.getData(this.content)
    },

    /**
     * @description: 下拉选择
     * @param {*} item
     * @return {*}
     */
    select(item) {
      this.search = this.content // 选择之后，不再重复发送请求，校准为一致
      this.$emit('select', item)
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
