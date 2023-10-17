<template>
  <div class="section create-sections">
    <div
      v-if="title && title.length > 0"
      class="section-header">
      <div
        :style="{ 'border-left-color': mColor }"
        class="section-mark" />
      <div class="section-title">{{ title }}</div>
      <el-dropdown
        v-if="dropdownItems && dropdownItems.length > 0"
        class="create-sections__more"
        trigger="click"
        @command="handleTypeClick">
        <i
          style="cursor: pointer;"
          class="el-icon-more" />
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item
            v-for="(item, index) in dropdownItems"
            :key="index"
            :icon="item.icon"
            :command="item.command">{{ item.label }}</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <slot name="header" />
    </div>
    <div class="content create-sections-content">
      <slot />
    </div>
  </div>
</template>
<script type="text/javascript">
export default {
  name: 'CreateSections',
  components: {},
  props: {
    title: {
      type: String,
      default: ''
    },
    mColor: {
      type: String,
      default: '#2362FB'
    },
    dropdownItems: Array
  },
  data() {
    return {}
  },
  computed: {},
  mounted() {},
  methods: {
    handleTypeClick(type) {
      this.$emit('command-select', type)
    }
  }
}
</script>
<style lang="scss" scoped>
.section {
  position: relative;
  margin-top: 8px;
  background-color: white;
}

.section:first-child {
  margin-top: 0;
}

.section-mark {
  height: 14px;
  border-left-style: solid;
  border-left-width: 4px;
  border-radius: 2px;
}

.section-header {
  display: flex;
  align-items: center;
  padding: 5px 0;
}

.section-title {
  flex-shrink: 0;
  margin-left: 8px;
  font-size: 16px;
  font-weight: 600;
}

.create-sections__more {
  position: absolute;
  right: 15px;
}

.content {
}
</style>
