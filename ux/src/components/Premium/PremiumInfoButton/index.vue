<template>
  <el-button
    type="text"
    class="wk-premium-info-btn"
    :data-type="info">
    <i class="wk wk-icon-lightning-solid wk-premium-info-icon" :data-type="info" />
    <span class="wk-premium-info-label" :data-type="info">{{ label }}</span>
  </el-button>
</template>

<script>
export default {
  // 查看增值按钮
  name: 'WkPremiumInfoButton',

  components: {},

  props: {
    // 按钮类型
    type: {
      type: String,
      default: 'default'
    },
    // 按钮文案
    label: String,
    // 增值类型
    info: String
  },

  data() {
    return {
    }
  },

  computed: {},

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {}
}
</script>

<style lang="scss" scoped>

</style>
