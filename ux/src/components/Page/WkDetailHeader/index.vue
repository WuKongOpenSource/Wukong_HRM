<template>
  <div class="wk-detail-header">
    <div v-if="subtitle || $slots.subtitle" class="wk-detail-header-subtitle">
      <slot name="subtitle">{{ subtitle }}</slot>
    </div>
    <flexbox class="wk-detail-header-body" justify="space-between">
      <div class="wk-detail-header-left">
        <slot name="title" />
        <el-tooltip
          v-if="title && !$slots.title"
          :disabled="!title"
          :content="title"
          effect="dark"
          placement="top-start">
          <span class="wk-detail-header-title">{{ title }}</span>
        </el-tooltip>
        <slot name="left" />
        <el-button-group v-if="pageList && pageList.length > 1" class="wk-detail-header-page-switch">
          <el-button type="subtle" icon="el-icon-arrow-left" @click="$emit('pageChange', 'left')" />
          <el-button type="subtle" icon="el-icon-arrow-right" @click="$emit('pageChange', 'right')" />
        </el-button-group>
      </div>

      <div class="wk-detail-header-right">
        <slot name="right" />
        <el-dropdown
          v-if="dropdowns && dropdowns.length > 0"
          trigger="click"
          :class="{ 'margin-left-interval': $slots.right }"
          @command="dropdownCommand">
          <el-button class="dropdown-btn" icon="el-icon-more" />
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="(item, index) in dropdowns"
              :key="index"
              :icon="item.icon"
              :command="item.command">{{ item.name }}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </flexbox>
  </div>
</template>

<script>
export default {
  // 详情头部
  name: 'WkDetailHeader',

  components: {},

  props: {
    subtitle: String,
    title: String,
    dropdowns: Array, // 更多
    pageList: Array // 切换详情
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
     * @description: 更多菜单事件
     * @param {*} command
     * @return {*}
     */
    dropdownCommand(command) {
      this.$emit('command', command)
    }
  }
}
</script>

<style lang="scss">
.wk-detail-header-page-switch {
  flex-shrink: 0;
  margin-left: #{$--interval-base * 3};

  .el-button + .el-button {
    margin-left: $--interval-base !important;
  }

  .el-button {
    padding: 6px;

    i {
      font-weight: bold;
    }
  }
}
</style>

<style lang="scss" scoped>
.wk-detail-header {
  &-subtitle {
    margin-bottom: 2px;
    font-size: $--font-size-base;
    color: $--color-text-secondary;
  }

  &-left,
  &-right {
    display: flex;
    align-items: center;
  }

  &-title {
    display: flex;
    overflow: hidden;
    font-size: $--font-size-xxlarge;
    font-weight: $--font-weight-bold;
    text-overflow: ellipsis;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
  }

  .dropdown-btn {
    padding: 8px;
  }
}
</style>
