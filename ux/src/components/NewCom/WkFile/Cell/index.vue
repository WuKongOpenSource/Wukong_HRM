<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-30 14:56:51
 * @LastEditTime: 2023-02-23 15:56:59
 * @LastEditors: yang
-->
<template>
  <flexbox class="wk-file-cell">
    <img
      :src="fileIcon"
      class="wk-file-cell__header"
      alt>
    <div
      :class="{'cursor-pointer' :cursorPointer}"
      class="wk-file-cell__body text-one-line">
      {{ fileName.length > 20 ? fileName.substring(0, 20) + '...' : fileName }}
    </div>
    <div class="wk-file-cell__size">
      ({{ data.size | getFileSize }})
    </div>
    <div class="wk-file-cell__footer">
      <span
        v-if="previewShow"
        class="xr-text-btn primary"
        @click="previewClick">预览</span>
      <span
        class="xr-text-btn primary"
        @click="downloadClick">下载</span>
      <span
        v-if="deleteShow"
        class="xr-text-btn delete"
        @click="deleteClick">删除</span>
    </div>
  </flexbox>
</template>

<script type="text/javascript">
import { downloadFileAPI } from '@/api/common'

import { downloadFileWithBuffer, getFileIconWithSuffix, fileSize, canPreviewFile, wkPreviewFile } from '@/utils'

export default {
  name: 'WkFileCell',
  filters: {
    getFileSize(size) {
      return fileSize(size)
    }
  },
  props: {
    index: Number,
    data: Object,
    // 完整数据
    list: Array,
    showFoot: {
      type: Boolean,
      default: true
    },
    cursorPointer: {
      type: Boolean,
      default: false
    },
    previewShow: {
      type: Boolean,
      default: true
    },
    deleteShow: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {}
  }, // 附件展示效果
  computed: {
    fileName() {
      if (this.data) {
        return this.data.name || this.data.fileName
      }
      return ''
    },

    fileIcon() {
      const temps = this.fileName ? this.fileName.split('.') : []
      var ext = ''
      if (temps.length > 0) {
        ext = temps[temps.length - 1]
      } else {
        ext = ''
      }
      return getFileIconWithSuffix(ext)
    }
  },
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {
    /**
     * 下载
     */
    downloadClick() {
      downloadFileAPI(this.data.url).then(res => {
        downloadFileWithBuffer(res.data, this.data.name)
      }).catch(() => {})
    },

    /**
     * 附件预览
     */
    previewClick() {
      if (canPreviewFile(this.fileName)) {
        wkPreviewFile(this.data.url, this.fileName)
      } else {
        this.$wkPreviewFile.preview({
          index: this.index || 0,
          data: this.list.map(function(item) {
            return {
              url: item.url,
              name: item.name
            }
          })
        })
      }
    },

    /**
     * 删除
     */
    deleteClick() {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.$emit('delete', this.data, this.index)
        })
        .catch(() => {})
    }
  }
}
</script>
<style lang="scss" scoped>
.wk-file-cell {
  position: relative;
  height: 38px;
  padding: 8px;
  margin-bottom: 5px;
  line-height: 20px;
  background-color: #f8faff;
  border-radius: $--border-radius-base;

  &__header {
    display: block;
    width: 16px;
  }

  &__body {
    margin-left: 12px;
    font-size: 14px;
    color: $--color-primary;
  }

  &__size {
    flex-shrink: 0;
    margin-left: 8px;
    font-size: 12px;
    color: #ccc;
  }

  &__footer {
    display: none;
    flex-shrink: 0;
    margin-right: 8px;
    margin-left: 15px;
    cursor: pointer;

    i {
      padding: 0 2px;
      color: #ccc;
    }
  }

  &:hover {
    .wk-file-cell__footer {
      display: block;
    }
  }
}

.cursor-pointer {
  cursor: pointer;
}
</style>
