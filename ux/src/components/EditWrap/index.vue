<template>
  <span class="wk-edit-wrap">
    <slot />
    <div class="handle-bar">
      <el-button icon="el-icon-check" @click="click('save')" />
      <el-button icon="el-icon-close" @click="click('cancel')" />
    </div>
  </span>
</template>

<script>
export default {
  // EditWrap
  name: 'WkEditWrap',

  components: {},

  props: {},

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
     * 点击
     */
    click(type) {
      this.$emit(type)
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-edit-wrap {
  position: relative;

  .handle-bar {
    position: absolute;
    top: 100%;
    right: 0;
    display: flex;
    flex-shrink: 0;
    margin-top: 6px;

    ::v-deep .el-button {
      padding: 8px;
      box-shadow: $--box-shadow-dark;

      i {
        font-weight: bold;
      }
    }
  }
}
</style>
