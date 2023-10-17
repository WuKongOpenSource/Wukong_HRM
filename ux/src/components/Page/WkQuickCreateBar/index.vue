<template>
  <div class="wk-quick-create-bar">
    <el-button
      v-for="(item, index) in list"
      :key="index"
      :icon="item.icon"
      @click="handleClick(item.command)">{{ item.label }}</el-button>
  </div>
</template>

<script>
export default {
  // WkQuickCreateBar
  // 快捷创建条
  name: 'WkQuickCreateBar',

  components: {
  },

  props: {
    list: Array
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

  methods: {
    /**
     * 操作点击
     */
    handleClick(command) {
      this.$emit('command', command)
    }
  }
}
</script>

<style lang="scss" scoped>
.el-button {
  display: inline-flex;
  align-items: center;
}
</style>
