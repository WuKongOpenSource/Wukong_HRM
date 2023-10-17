<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang

-->
<template>
  <div
    :style="{
      width: `${width}px`,
      height: `${width}px`,
      borderRadius: `${width/2}px`,
      borderColor: bold ? '#2362FB' : '#CCD4DC'
    }"
    class="wk-circle-tag">
    <flexbox
      :style="{
        borderColor: bold ? '#2362FB' : '#CCD4DC'
      }"
      class="wk-circle-tag__content"
      orient="vertical"
      align="center"
      justify="center">
      <div
        :class="{'is-bold':bold}"
        :style="{
          color: bold ? '#2362FB' : '#6B778C',
          fontSize: bold ? '16px' : '12px',
        }"
        class="label text-one-line">{{ label }}</div>
      <div v-if="des" class="des text-one-line">{{ des }}</div>
    </flexbox>
  </div>
</template>

<script>
import FitText from '@/directives/fitText'

export default {
  name: 'WkCircleTag',
  directives: {
    FitText
  },
  components: {},
  props: {
    width: {
      type: [String, Number],
      default: 90
    },
    bold: {
      type: Boolean,
      default: true
    },
    label: [String, Number],
    des: [String, Number]
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {}
}
</script>

<style lang="scss" scoped>
.wk-circle-tag {
  position: relative;
  padding: 3px;
  border: 1px solid #ccd4dc;

  &__content {
    height: 100%;
    padding: 0 8px;
    overflow: hidden;
    text-align: center;
    border: 2px solid #ccd4dc;
    border-radius: 50%;

    .label {
      width: 100%;
    }

    .label.is-bold {
      font-weight: bold;
    }

    .des {
      width: 100%;
      margin-top: 3px;
    }
  }
}
</style>

