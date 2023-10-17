<template>
  <div
    :class="{
      'vux-flex-col': orient === 'vertical',
      'vux-flex-row': orient === 'horizontal'
    }"
    :style="styles"
    class="vux-flexbox">
    <slot />
  </div>
</template>

<script>
export default {
  name: 'Flexbox',
  props: {
    gutter: {
      type: Number,
      default: 8
    },
    orient: {
      type: String,
      default: 'horizontal'
    },
    justify: String,
    align: String,
    wrap: String,
    direction: String
  },
  computed: {
    styles() {
      const styles = {
        'justify-content': this.justify,
        '-webkit-justify-content': this.justify,
        'align-items': this.align,
        '-webkit-align-items': this.align,
        'flex-wrap': this.wrap,
        '-webkit-flex-wrap': this.wrap,
        'flex-direction': this.direction,
        '-webkit-flex-direction': this.direction
      }
      return styles
    }
  }
}
</script>

<style lang="scss">
.vux-flexbox {
  display: flex;
  align-items: center;
  width: 100%;
  text-align: left;

  .vux-flexbox-item {
    -webkit-flex: 1;
    flex: 1;
    width: 0%;
    min-width: 20px;

    &:first-child {
      margin-top: 0 !important;
      margin-left: 0 !important;
    }
  }
}

.vux-flex-row {
  flex-direction: row;
}

.vux-flex-col {
  flex-direction: column;

  > .vux-flexbox-item {
    width: 100%;
  }
}
</style>
