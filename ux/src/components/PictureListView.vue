<template>
  <div class="picture-list-view">
    <div
      v-for="(imgItem, k) in list"
      :key="k"
      class="img-list-item"
      @click="imgZoom(list, k)">
      <img
        :key="imgItem.filePath || imgItem.url"
        v-src="imgItem.filePath || imgItem.url">
    </div>
  </div>
</template>

<script>
/**
 * 图片文件列表
 * @props list {Array} 图片列表数据
 */
export default {
  name: 'PictureListView',
  props: {
    list: {
      type: Array,
      required: true
    }
  },
  methods: {
    // 放大图片
    imgZoom(val, k) {
      this.$wkPreviewFile.preview({
        index: k,
        data: val.map(function(item, index, array) {
          return {
            url: item.filePath || item.url,
            name: item.name
          }
        })
      })
    }
  }
}
</script>

<style scoped lang="scss">
  .picture-list-view {
    max-width: 320px;

    .img-list-item {
      position: relative;
      display: inline-block;
      margin: 0 $--interval-base 4px 0;
      cursor: pointer;

      img {
        object-fit: fill;
        width: 98px;
        height: 98px;
        border-radius: $--border-radius-base;
      }
    }
  }
</style>
