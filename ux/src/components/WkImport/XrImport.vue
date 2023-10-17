<template>
  <flexbox class="xr-import">
    <div
      v-if="loading"
      v-loading="loading"
      class="xr-import__icon" />
    <i
      v-else
      class="wk wk-success xr-import__icon" />
    <p
      :class="{ 'is-loading': loading }"
      class="xr-import__label">{{ processLabel }}</p>
  </flexbox>
</template>

<script>
export default {
  // 导入缩小框子
  name: 'XrImport',
  components: {},
  props: {
    // wait / process / finish / error / success
    processStatus: {
      type: String,
      default: 'wait'
    }
  },
  data() {
    return {
    }
  },
  computed: {
    loading() {
      return this.processStatus == 'process'
    },

    processLabel() {
      return this.loading ? '导入中' : '导入完成'
    }
  },
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {}
}
</script>

<style lang="scss" scoped>
.xr-import {
  position: fixed;
  right: 50px;
  bottom: 150px;
  z-index: 999999;
  width: 130px;
  height: 50px;
  padding: 10px;
  cursor: pointer;
  background-color: white;
  border-radius: 25px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

  &__icon {
    margin-right: 5px;
    font-size: 30px;
  }

  &__label {
    font-size: 14px;
    color: $--color-text-primary;
  }

  &__label.is-loading {
    margin-left: 35px;
  }

  ::v-deep .el-loading-spinner .circular {
    width: 30px;
    height: 30px;
  }

  ::v-deep .el-loading-spinner {
    margin-top: -15px;
  }
}

.wk-success {
  color: $--color-primary;
}
</style>
