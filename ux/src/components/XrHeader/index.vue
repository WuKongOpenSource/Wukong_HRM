<template>
  <flexbox class="xr-header">
    <div
      v-if="!!iconClass"
      :style="{ backgroundColor: iconColor }"
      class="xr-header__icon">
      <i :class="iconClass" />
    </div>

    <div class="xr-header__label">
      <slot v-if="$slots.label" name="label" />
      <template v-else>{{ label }}<slot name="otherLabel" /></template>
    </div>

    <el-input
      v-if="showSearch"
      v-model="search"
      :placeholder="placeholder"
      v-bind="inputAttr"
      :style="{'margin-top': ftTop}"
      class="xr-header__search"
      @input="inputChange"
      @keyup.enter.native="searchClick">
      <el-button
        slot="suffix"
        type="icon"
        icon="wk wk-sousuo"
        @click="searchClick" />
    </el-input>

    <div :style="{ top: ftTop }" class="xr-header__ft">
      <slot name="ft" />
    </div>
  </flexbox>
</template>

<script>

export default {
  // 公共列表搜索头部
  name: 'XrHeader',
  components: {},
  // inheritAttrs: false,
  props: {
    iconClass: [String, Array],
    iconColor: String,
    label: String,
    // value: String,

    showSearch: {
      type: Boolean,
      default: false
    },
    placeholder: {
      type: String,
      default: '请输入内容'
    },
    ftTop: {
      type: String,
      default: '0'
    },

    content: [String, Number],

    inputAttr: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      search: ''
    }
  },
  computed: {},
  watch: {
    content: {
      handler() {
        if (this.search != this.content) {
          this.search = this.content
        }
      },
      immediate: true
    }
  },
  mounted() {
  },

  beforeDestroy() {},
  methods: {
    inputChange() {
      this.$emit('update:content', this.search)
    },

    searchClick() {
      this.$emit('search', this.search)
    }
  }
}
</script>

<style lang="scss" scoped>
.xr-header {
  position: relative;

  &__icon {
    width: 30px;
    height: 30px;
    margin-right: 10px;
    line-height: 30px;
    text-align: center;
    border-radius: $--border-radius-base;

    .wk {
      font-size: 18px;
      color: white;
    }
  }

  &__label {
    font-size: $--font-size-xxlarge;
    font-weight: $--font-weight-primary;
    color: $--color-text-primary;
  }

  &__search {
    position: absolute;
    top: 0;
    left: 50%;
    width: 300px;
    margin: 0 0 0 -150px;
  }

  &__search.is-text {
    ::v-deep .el-input-group__append {
      color: white;
      background-color: $--color-primary;
      border-color: $--color-primary;
    }
  }

  &__ft {
    position: absolute;
    right: 0;
  }
}
</style>
