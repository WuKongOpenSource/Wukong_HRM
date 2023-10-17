<template>
  <div
    class="wk-head-section wrap"
    :class="{ 'is-no-border': !config.border }">
    <div
      :class="{ 'is-unfold': unfold }"
      class="section-head"
      :style="{ 'background-color': config.headBgColor }"
      @click="unfold = !unfold">
      <i v-if="config.arrows === 'left'" class="el-icon-arrow-down" />
      <div class="section-title text-one-line">{{ label }}</div>
      <slot name="otherLabel" />
      <div class="section-right">
        <slot name="right" />
        <i v-if="config.arrows === 'right'" class="el-icon-arrow-down" />
      </div>
    </div>

    <div
      v-show="unfold"
      class="section-body"
      :style="{
        padding: config.bodyPadding
      }">
      <slot />
    </div>
  </div>
</template>

<script>
import merge from '@/utils/merge'

const DefaultWkHeadSection = {
  headBgColor: 'transparent',
  arrows: 'right', // left right
  border: true, // 带边框
  bodyPadding: '16px'
}

export default {
  // 带头部的section
  name: 'WkHeadSection',

  components: {},

  props: {
    props: Object,
    label: [String, Number]
  },

  data() {
    return {
      unfold: true // 展开
    }
  },

  computed: {
    // 合并 props
    config() {
      return merge({ ...DefaultWkHeadSection }, this.props || {})
    }
  },

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {}
}
</script>

<style lang="scss" scoped>
.wrap {
  position: relative;

  .section-head {
    position: relative;
    display: flex;
    align-items: center;
    height: 48px;
    padding: 16px;
    cursor: pointer;
    user-select: none;
    border: 1px solid $--border-color-base;
    border-radius: 4px;

    &.is-unfold {
      border-radius: 4px 4px 0 0;
    }

    &:hover {
      background-color: $--color-n20;
    }

    > .section-title {
      flex-shrink: 0;
      max-width: 60%;
      padding-right: 4px;
      font-size: 14px;
      font-weight: $--font-weight-semibold;
    }

    .el-icon-arrow-down + .section-title {
      margin-left: $--interval-base;
    }

    > .section-right {
      display: flex;
      align-items: center;
      margin-left: auto;
    }
  }

  > .section-body {
    box-sizing: border-box;
    padding-bottom: unset !important;
    border-width: 0 1px 1px;
    border-top-color: initial;
    border-top-style: initial;
    border-right-color: $--border-color-base;
    border-right-style: solid;
    border-bottom-color: $--border-color-base;
    border-bottom-style: solid;
    /* stylelint-disable-next-line declaration-block-no-redundant-longhand-properties */
    border-left-color: $--border-color-base;
    /* stylelint-disable-next-line declaration-block-no-redundant-longhand-properties */
    border-left-style: solid;
    border-bottom-right-radius: 4px;
    border-bottom-left-radius: 4px;
    border-image: initial;
  }

  &.is-no-border {
    .section-head,
    .section-body {
      border: none;
    }
  }
}
</style>
