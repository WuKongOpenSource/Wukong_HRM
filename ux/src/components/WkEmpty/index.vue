<template>
  <div class="wk-empty">
    <div class="wk-empty__title">{{ config.emptyText || '暂无数据' }}</div>
    <el-button
      v-if="config.showButton"
      :icon="config.buttonIcon"
      style="margin-top: 8px;"
      type="primary"
      @click="btnClick">{{ config.buttonTitle || '新建' }}</el-button>
  </div>
</template>

<script>
import merge from '@/utils/merge'

const DefaultEmptyProps = {
  emptyText: '', //
  showButton: false,
  buttonIcon: '', //
  buttonTitle: '' //
}

export default {
  // 空数据
  name: 'WkEmpty',

  components: {},

  props: {
    props: Object
  },

  data() {
    return {
    }
  },

  computed: {
    config() {
      return merge({ ...DefaultEmptyProps }, this.props || {})
    }
  },

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    btnClick() {
      this.$emit('click')
    }
  }
}
</script>

<style lang="scss">
.wk-empty {
  color: $--color-text-secondary;
}
</style>
