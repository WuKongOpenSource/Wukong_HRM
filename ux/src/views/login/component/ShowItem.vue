<template>
  <div class="item-show">
    <div class="item-show__inner">{{ content }}</div>
    <span class="item-show__suffix">
      <span class="item-show__suffix-inner">
        <i class="wk wk-icon-follow-up" />
      </span>
    </span>
  </div>
</template>

<script>
export default {
  // 展示正确的值
  name: 'ShowItem',

  components: {},

  props: {
    content: [String, Number]
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
.item-show {
  position: relative;
  cursor: pointer;

  &__inner {
    height: 40px;
    padding: 0 30px 0 8px;
    line-height: 40px;
    border: 2px solid transparent;
    border-radius: 3px;
    transition: background-color 0.2s ease-in-out 0s, border-color 0.2s ease-in-out 0s;

    &:hover {
      background: $--color-n10;
      border-color: $--background-color-base;
    }
  }

  &__suffix {
    position: absolute;
    top: 0;
    right: 5px;
    height: 100%;
    color: #c0c4cc;
    text-align: center;
    pointer-events: none;
    transition: all 0.3s;

    &-inner {
      pointer-events: all;

      .wk-icon-follow-up {
        margin-top: 8px;
        margin-right: 2px;
        font-size: 20px;
        color: $--color-black;
        cursor: pointer;
        user-select: none;
      }
    }
  }
}
</style>
