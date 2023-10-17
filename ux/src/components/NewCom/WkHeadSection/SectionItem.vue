<template>
  <div class="wk-section-item">
    <div class="item-label">{{ label }}<slot name="otherLabel" /></div>
    <div class="item-content">
      <slot />
    </div>
  </div>
</template>

<script>
export default {
  // 简单的行效果
  name: 'WkSectionItem',

  components: {},

  props: {
    label: String
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
// 详情form
.wk-section-item {
  display: flex;
  flex-wrap: wrap;
  line-height: 1.5;
  word-break: break-word;

  > .item-label {
    position: relative;
    width: 80px;
    color: $--color-text-secondary;
  }

  > .item-content {
    flex: 1;
    margin-left: 16px;
  }
}

.wk-section-item + .wk-section-item {
  margin-top: 16px;
}
</style>
