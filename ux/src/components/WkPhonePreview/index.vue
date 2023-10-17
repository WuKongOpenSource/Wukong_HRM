<template>
  <el-drawer
    v-bind="$attrs"
    :visible="visible"
    :with-header="false"
    size="405px"
    append-to-body
    @close="close">
    <div class="wk-phone-preview">
      <iframe
        :src="src"
        frameborder="0"
      />
    </div>
  </el-drawer>
</template>

<script>
export default {
  // 数据预览网址
  name: 'WkPhonePreview',

  components: {},

  props: {
    visible: Boolean,
    src: String
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

  methods: {
    close() {
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-phone-preview {
  box-sizing: border-box;
  width: 365px;
  height: calc(100% - 20px);
  padding: 100px 16px;
  margin: 20px 20px 0;
  background-image: url(./img/phone.png);
  background-repeat: no-repeat;
  background-size: 100%;

  iframe {
    width: 100%;
    height: 580px;
    background-color: #fff;
  }
}
</style>
