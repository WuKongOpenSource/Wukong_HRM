<template>
  <transition name="opacity-fade" @after-enter="afterEnter">
    <div
      :style="{ 'background-color': backgroundColor, 'padding': padding+' 0', 'z-index': zIndex }"
      class="c-view create-view">
      <el-card
        v-loading="loading"
        :style="{ 'width': width, ...cardStyle}"
        :body-style="bodyStyle"
        class="create-view-content">
        <slot name="header" />
        <slot />
      </el-card>
    </div>
  </transition>
</template>
<script type="text/javascript">
import { getMaxIndex } from '@/utils/index'

export default {
  name: 'CreateView', // 所有新建效果的view
  components: {},
  props: {
    bodyStyle: {
      type: Object,
      default: () => {
        return {}
      }
    },
    cardStyle: {
      type: Object,
      default: () => {
        return {}
      }
    },
    loading: {
      type: Boolean,
      default: false
    },
    // 更改背景颜色颜色
    backgroundColor: {
      type: String,
      default: 'rgba(23,43,77, 0.5)' // rgba(0, 0, 0, 0.6) 黑色半透明
    },
    /** 展示内容的宽 */
    width: {
      type: String,
      default: '900px'
    },
    /** 展示内容的上下padding */
    padding: {
      type: String,
      default: '40px'
    }
  },
  data() {
    return {
      zIndex: getMaxIndex(),
      loadingObj: null
    }
  },
  computed: {},
  watch: {},
  mounted() {},
  methods: {
    afterEnter() {
      this.$emit('afterEnter')
    }
  }
}
</script>
<style lang="scss" scoped>
.opacity-fade-enter-active,
.opacity-fade-leave-active {
  transition: all 0.2s;
}

.opacity-fade-enter,
.opacity-fade-leave-to {
  opacity: 0;
}

/** 容器布局 */
.c-view {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
}

.create-view-content {
  position: relative;
  height: 100%;
  margin: 0 auto;

  ::v-deep .el-card__body {
    padding: 20px 30px;
  }
}
</style>
