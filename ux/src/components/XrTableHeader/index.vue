<template>
  <div class="xr-table-header">
    <flexbox
      v-show="!handleShow"
      class="xr-table-header__body"
      align="center">
      <slot />
    </flexbox>
    <flexbox
      v-show="handleShow"
      class="xr-table-header__handle">
      <div class="selected—title">已选中 <span class="selected—count">{{ selects.length }}</span> 项</div>
      <flexbox class="selection-items-box">
        <slot name="prefix" />
        <el-button
          v-for="(item, index) in handles"
          :key="index"
          :icon="item.icon | wkIconPre"
          size="medium"
          @click="selectionBarClick(item.command)">{{ item.label }}</el-button>
        <slot name="suffix" />
      </flexbox>
    </flexbox>
    <slot name="append" />
  </div>
</template>

<script>

export default {
  // 表头
  name: 'XrTableHeader',
  components: {
  },
  props: {
    selects: {
      type: Array,
      default: () => {
        return []
      }
    },
    handles: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  data() {
    return {}
  },
  computed: {
    // 操作展示
    handleShow() {
      return this.selects && this.selects.length > 0
    }
  },
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {
    selectionBarClick(command) {
      this.$emit('command', command)
    }
  }
}
</script>

<style lang="scss" scoped>
.xr-table-header {
  background: white;

  &__handle {
    height: 50px;

    .selected—title {
      flex-shrink: 0;
      padding-right: 20px;
      font-weight: $--font-weight-semibold;

      .selected—count {
        color: $--color-primary;
      }
    }
  }

  .selection-items-box {
    padding: 0 15px;
    overflow-x: auto;
    overflow-y: hidden;
  }

  &__body {
    height: 50px;
    padding: 0 20px;
    font-size: 13px;
  }
}
</style>
