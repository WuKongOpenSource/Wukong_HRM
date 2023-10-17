<template>
  <flexbox class="reminder-wrapper">
    <flexbox
      align="stretch"
      class="reminder-body">
      <i class="wk wk-warning reminder-icon" />
      <div
        :style="{'font-size': fontSize + 'px'}"
        class="reminder-content"
        v-html="content" />
      <slot />
      <i
        v-if="closeShow"
        class="el-icon-close close"
        @click="close" />
    </flexbox>
  </flexbox>
</template>

<script type="text/javascript">
// 警示信息

export default {
  name: 'Reminder',
  components: {},
  props: {
    closeShow: {
      type: Boolean,
      default: false
    },
    content: {
      type: String,
      default: '内容'
    },
    fontSize: {
      type: String,
      default: '14'
    }
  },
  data() {
    return {}
  },
  computed: {},
  mounted() {},
  destroyed() {},
  methods: {
    close() {
      this.$emit('close')
    }
  }
}
</script>
<style lang="scss" scoped>
.reminder-wrapper {
  .reminder-body {
    width: auto;
    padding: 4px 16px 4px 8px;
    line-height: 1.5;
    background-color: $--color-y50;
    border-radius: $--border-radius-base;

    .reminder-icon {
      margin-right: 8px;
      font-size: 16px;
      color: $--color-y500;
    }

    .reminder-content {
      word-break: break-all;
      word-wrap: break-word;
      white-space: pre-wrap;
    }

    .close {
      display: block;
      margin-left: 8px;
      font-size: 16px;
      line-height: 31px;
      color: #909399;
      cursor: pointer;
    }

    .close:hover {
      color: $--color-primary;
    }
  }
}
</style>
