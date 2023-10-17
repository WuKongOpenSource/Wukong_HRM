<template>
  <div
    v-if="!showPreview && src"
    class="adapter-image img-wrapper"
    @click="handlePreview">
    <img :src="dataSrc" alt="" class="small-img">
  </div>

  <div
    v-else-if="showPreview && src"
    class="img-popover adapter-image">
    <div
      class="img-wrapper img-core"
      @click="handlePreview">
      <img :src="dataSrc" alt="" class="small-img">
    </div>
    <el-popover
      v-if="popoverFlag"
      v-model="popoverVisible"
      placement="right"
      width="200"
      trigger="hover">
      <div
        class="middle-img img-wrapper"
        @click.stop="handleClick">
        <img
          v-if="popoverVisible"
          :src="dataSrc"
          alt=""
          class="middle-img-core">
      </div>
      <div
        slot="reference"
        class="reference"
        @click="handlePreview" />
    </el-popover>
  </div>

  <div
    v-else
    style="background-color: unset;"
    class="adapter-image img-wrapper"
    @click.stop />
</template>

<script>
/**
 * Create by yxk at 2020/7/29 0029
 */
export default {
  name: 'PreviewImage',
  props: {
    src: {
      type: String,
      default: ''
    },
    showPreview: {
      type: Boolean,
      default: false
    },
    index: Number
  },
  data() {
    return {
      popoverVisible: false,
      popoverFlag: true,
      backgroundStyle: {}
    }
  },
  computed: {
    dataSrc() {
      if (!this.src) return ''
      return process.env.VUE_APP_BASE_API + this.src
    }
  },
  methods: {
    handlePreview() {
      this.$emit('preview', this.index)
    },

    handleClick(evt) {
      // 点击 popover 中的大图时，销毁popover弹出详情
      this.popoverFlag = false
      this.popoverVisible = false
      this.$nextTick(() => {
        this.popoverFlag = true
      })
    }
  }
}
</script>

<style scoped lang="scss">
.img-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  cursor: pointer;
  background-color: white;
}

.adapter-image {
  width: 35px;
  height: 25px;
  margin: 8px 8px 8px 0;
}

.small-img {
  max-width: 60px;
  max-height: 60px;
  vertical-align: middle;
}

// popover
.img-popover {
  position: relative;

  .el-popover {
    padding: 10px;
  }

  .img-core {
    width: 100%;
    height: 100%;
    margin: 0;
    pointer-events: none;
  }

  .reference {
    position: absolute;
    top: 0;
    left: 0;
    z-index: 0;
    width: 100%;
    height: 100%;
    cursor: pointer;
  }
}

.middle-img {
  width: 100%;
  height: 100%;

  .middle-img-core {
    width: 100%;
    object-fit: contain;
  }
}
</style>
