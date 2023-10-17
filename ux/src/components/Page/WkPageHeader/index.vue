<template>
  <flexbox
    class="wk-page-header"
    justify="space-between">
    <!-- 左侧内容 -->
    <div class="wk-page-header__left">
      <span v-if="title || $slots.title" class="wk-page-header-title">
        <slot name="title">{{ title }}</slot>
      </span>
      <i v-if="help" class="wk wk-icon-fill-help wk-help-tips" :data-type="help.type" :data-id="help.id" />
      <slot name="left" />
    </div>

    <!-- 中部内容 -->
    <div class="wk-page-header__center"><slot name="center" /></div>

    <!-- 右侧内容 -->
    <div class="wk-page-header__right">
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
</template>

<script>
export default {
  // index 主列表 头部
  name: 'WkPageHeader',

  components: {},

  props: {
    title: String,
    dropdowns: Array, // 更多
    help: Object // 帮助提示
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

<style lang="scss" scoped>
.wk-page-header {
  position: relative;

  &__left,
  &__right {
    display: flex;
    align-items: center;
  }

  &-title {
    font-size: $--font-size-xxlarge;
    font-weight: $--font-weight-primary;
    color: $--color-text-primary;
  }

  .dropdown-btn {
    padding: 8px;
  }

  &__center {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
  }
}
</style>
