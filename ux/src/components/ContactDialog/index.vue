<template>
  <el-dialog
    :visible="visible"
    :title="title"
    v-bind="$attrs"
    width="600px"
    @close="close">
    <div class="dialog-content">{{ content }}</div>
    <div class="dialog-phone"><i class="wk wk-phone" />400-0812-558</div>
    <div slot="footer" class="dialog-footer">
      <span><slot /></span>
      <el-button type="primary" @click="close">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>

export default {
  // 联系我们
  name: 'ContactDialog',
  components: {},
  mixins: [],
  inheritAttrs: false,
  props: {
    title: {
      type: String,
      default: '联系我们'
    },
    content: {
      type: String,
      default: '您可以通过以下电话联系我们'
    },
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
    }
  },
  computed: {},
  watch: {},
  mounted() {
  },
  methods: {
    /**
     * 取消选择
     */
    close() {
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style lang="scss" scoped>
.dialog {
  &-content {
    font-size: 18px;
    color: $--color-n400;
  }

  &-phone {
    margin-top: 24px;
    font-size: 28px;
    color: $--color-primary;

    i {
      margin-right: 8px;
      font-size: 26px;
    }
  }

  &-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}
</style>
