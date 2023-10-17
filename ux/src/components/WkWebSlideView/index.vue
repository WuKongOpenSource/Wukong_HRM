<template>
  <slide-view
    class="wk-web-slide-view"
    append-to-body
    :body-style="{padding: 0, height: '100%'}"
    @close="$emit('close')">
    <iframe
      :src="src"
      frameborder="0"
    />
  </slide-view>
</template>

<script>
import SlideView from '@/components/SlideView'

export default {
  // WkWebSlideView
  name: 'WkWebSlideView',

  components: {
    SlideView
  },

  props: {
    src: String
  },

  data() {
    return {
    }
  },

  computed: {},

  watch: {},

  created() {
  },

  mounted() {
  },

  beforeDestroy() {
  },

  methods: {}
}
</script>

<style lang="scss" scoped>
.wk-web-slide-view {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  width: $--detail-width-base;
  min-width: 950px;
}

iframe {
  width: 100%;
  height: 100%;
  background-color: #fff;
}
</style>

