<template>
  <section
    class="img-cont">
    <flexbox wrap="wrap">
      <div
        v-for="(item, index) in data"
        :key="index"
        v-src:background-image="item.url || item.filePath"
        class="img-item"
        @mouseover="mouseImgOver(item, index)"
        @mouseleave="mouseImgLeave(item, index)">
        <el-button
          v-if="item.showDelete"
          class="img-delete"
          type="selected"
          icon="el-icon-close"
          @click="deleteItem(item, index)" />
      </div>
      <div class="el-upload el-upload--text">
        <div class="el-upload-dragger">
          <i class="el-icon-plus uploader-icon" />
          <input
            type="file"
            class="img-item-iput"
            accept="image/*"
            multiple
            @change="uploadFile">
        </div>
      </div>
    </flexbox>
    <el-button type="text" @click="deleteAll">全部删除</el-button>
  </section>
</template>

<script>

export default {
  // 添加图片展示
  name: 'AddImageList',
  components: {},
  props: {
    data: Array
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {
    /**
     *  鼠标移入和移除 图片区域
     */
    mouseImgOver(item, index) {
      item.showDelete = true
      this.$set(this.data, index, item)
    },
    mouseImgLeave(item, index) {
      item.showDelete = false
      this.$set(this.data, index, item)
    },

    deleteItem(item, index) {
      this.$emit('delete', item, index)
    },

    deleteAll() {
      this.$emit('delete-all')
    },

    uploadFile(event) {
      this.$emit('upload', event)
    }
  }
}
</script>

<style lang="scss" scoped>
/** 图片  */
.img-cont {
  margin-bottom: #{$--interval-base * 2};

  .img-item {
    position: relative;
    display: inline-block;
    width: 98px;
    height: 98px;
    margin: 0 $--interval-base $--interval-base 0;
    background-repeat: no-repeat;
    background-position: center;
    background-size: contain;
    border: 1px solid $--border-color-base;
    border-radius: $--border-radius-base;

    .img-delete {
      position: absolute;
      top: 0;
      right: 0;
      padding: 4px;
      cursor: pointer;
    }
  }

  .el-upload {
    margin: 0 8px 8px 0;

    .el-upload-dragger {
      width: 98px;
      height: 98px;
    }

    .img-item-iput {
      position: absolute;
      top: 0;
      right: 0;
      width: 98px;
      height: 98px;
      cursor: pointer;
      opacity: 0;
    }

    .uploader-icon {
      margin-top: 50%;
      font-size: 28px;
      color: #8c939d;
      transform: translateY(-50%);
    }
  }

  .el-button--text {
    padding: 0;
    font-size: 12px;
  }
}

</style>
