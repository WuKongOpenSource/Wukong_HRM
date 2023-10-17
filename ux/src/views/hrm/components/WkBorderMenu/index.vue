<template>
  <flexbox
    :class="{ 'is-select': isSelect}"
    class="wk-border-menu">
    <div
      v-for="(tab, index) in list"
      :key="index"
      :class="{ 'is-select': tabType == tab.name}"
      class="wk-border-menu__item"
      @click="tabClick(tab, index)">
      <div class="label">{{ tab.label }}</div>
      <div class="num">{{ tab.num }}</div>
    </div>
  </flexbox>
</template>

<script>

export default {
  // border 菜单 员工头部筛选效果
  name: 'WkBorderMenu',
  components: {},
  props: {
    list: Array,
    value: [String, Number],
    isSelect: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      tabType: ''
    }
  },
  computed: {},
  watch: {
    value: {
      handler(val) {
        this.tabType = val
      },
      immediate: true
    }
  },
  mounted() {},

  beforeDestroy() {},
  methods: {
    tabClick(tab, index) {
      this.$emit('input', tab.name)
      this.$emit('select', tab, index)
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-border-menu {
  position: relative;
  overflow: hidden;

  /* border: 1px solid $--color-n30; */
  background-color: $--color-n10;
  border-radius: 4px;

  &__item {
    position: relative;
    flex: 1;
    flex-shrink: 0;
    min-width: 70px;
    height: 76px;
    padding-top: 15px;
    text-align: center;
    cursor: pointer;

    &:hover {
      background-color: #eceef2;
    }

    .num {
      margin-top: 8px;
      font-size: 18px;
      font-weight: bold;
    }

    &.is-select {
      /* background-color: #ECEEF2; */
      color: $--color-primary;
      border-bottom: 3px $--border-style-base $--color-primary;
    }

    &::before {
      position: absolute;
      top: 20px;
      bottom: 20px;
      left: 0;
      width: 1px;
      content: "";
      background-color: $--color-n30;
    }
  }

  &__item:first-child::before {
    display: none;
  }

  /* &::after {
    position: absolute;
    content: "";
    right: -4px;
    bottom: -4px;
    border: 6px solid transparent;
    border-left: 6px solid #E6E6E6;
    transform: rotate(45deg);
  } */

  &.is-select {
    /* background-color: white; */
    &::after {
      border-left-color: $--color-primary;
    }

    border-color: $--color-primary;
  }
}
</style>
