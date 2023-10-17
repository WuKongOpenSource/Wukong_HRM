<template>
  <create-view
    ref="createView"
    :body-style="{ height: '100%'}"
    :card-style="cardStyle"
    @afterEnter="afterEnter">
    <flexbox
      v-loading="loading"
      direction="column"
      align="stretch"
      class="xr-create">
      <flexbox class="xr-create__header">
        <div v-if="!$slots.title" class="title">{{ title }}<slot name="title-append" /></div>
        <slot name="title" />
        <i
          class="el-icon-close close"
          @click="close" />
      </flexbox>
      <div class="xr-create__body">
        <slot />
      </div>
      <div class="xr-create__footer">
        <slot name="footer-left" />
        <el-button
          v-if="showConfirm"
          :disabled="confirmDisabled"
          type="primary"
          @click.native="debouncedSaveField(false)">{{ confirmButtonText }}</el-button>
        <slot name="footer" />
        <el-button
          v-if="showCancel"
          @click.native="close">取消</el-button>
      </div>
    </flexbox>
  </create-view>
</template>

<script>
import CreateView from '@/components/CreateView'
import { debounce } from 'throttle-debounce'

export default {
  // 创建页面
  name: 'XrCreate',
  components: {
    CreateView
  },
  props: {
    title: String,
    loading: Boolean,
    cardStyle: Object,
    appendToBody: {
      type: Boolean,
      default: true
    },
    showCancel: {
      type: Boolean,
      default: true
    },
    showConfirm: {
      type: Boolean,
      default: true
    },
    confirmDisabled: {
      type: Boolean,
      default: false
    },
    confirmButtonText: {
      type: String,
      default: '保存'
    }
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  created() {
    this.debouncedSaveField = debounce(300, this.save)
  },
  mounted() {
    if (this.appendToBody) {
      document.body.appendChild(this.$el)
    }
  },

  destroyed() {
    if (this.appendToBody) {
      if (this.$el && this.$el.parentNode) {
        this.$el.parentNode.removeChild(this.$el)
      }
    }
  },
  methods: {
    afterEnter() {
      this.$emit('afterEnter')
    },

    save() {
      this.$emit('save')
    },

    close() {
      this.$emit('close')
    }
  }
}
</script>

<style lang="scss" scoped>
.xr-create {
  position: relative;
  height: 100%;

  &__header {
    flex-shrink: 0;
    height: 40px;
    margin-bottom: 20px;

    .title {
      flex: 1;
      font-size: 20px;
      font-weight: bold;
    }

    .close {
      display: block;
      padding: 10px;
      margin-right: -10px;
      font-size: 24px;
      color: $--color-n500;
      cursor: pointer;
    }

    .close:hover {
      color: $--color-primary;
    }
  }

  &__body {
    position: relative;
    flex: 1;
    overflow-x: hidden;
    overflow-y: auto;
  }

  &__footer {
    position: relative;
    padding: 20px 20px 0;
    text-align: right;
  }
}
</style>
