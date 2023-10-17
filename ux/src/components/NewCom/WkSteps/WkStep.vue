<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <div class="wk-step">
    <flexbox>
      <div class="wk-step__line is-left" />
      <img :src="icon" class="wk-step__icon">
      <div class="wk-step__line is-right" />
    </flexbox>
    <div v-if="label" class="wk-step__label">{{ label }}</div>
    <div class="wk-step__des">
      <span v-if="desLeft">{{ desLeft }}</span>
      <el-button v-if="desCenter" type="link" @click="desClick">{{ desCenter }}</el-button>
      <span v-if="desRight">{{ desRight }}</span>
    </div>
    <div class="wk-step__btn">
      <el-button v-if="button" type="link" @click="bottomClick">{{ button }}</el-button>
    </div>
  </div>
</template>

<script>

export default {
  // 步骤
  name: 'WkStep',
  components: {},
  props: {
    icon: String,
    label: String,
    desLeft: String,
    desCenter: String,
    desRight: String,
    button: String
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {
    desClick() {
      this.$emit('step-click', 'des')
    },

    bottomClick() {
      this.$emit('step-click', 'button')
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-step {
  flex: 1;
  font-size: 16px;
  color: $--color-text-primary;

  &__line {
    width: 100%;
    height: 1px;
    border-top: 1px dashed #ebf2ff;
  }

  &__label {
    margin-top: 15px;
  }

  &__des {
    margin-top: 10px;
    font-size: 14px;

    .el-button {
      padding: 0;
      font-size: 14px;
    }
  }

  &__btn {
    margin-top: 30px;

    .el-button {
      font-size: 14px;
    }
  }

  &__icon {
    // font-size: 50px;
    // color: #2362FB;
    width: 50px;
    height: 50px;
  }

  &:first-child {
    .is-left {
      border-top-color: transparent;
    }
  }

  &:last-child {
    .is-right {
      border-top-color: transparent;
    }
  }
}
</style>
