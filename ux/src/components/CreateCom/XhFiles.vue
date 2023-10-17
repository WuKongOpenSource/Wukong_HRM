<template>
  <div
    :class="[disabled ? 'is_disabled' : 'is_valid']"
    class="xh-files-cont xh-form-border">
    <flexbox
      :class="[disabled ? 'is_disabled' : 'is_valid']"
      class="f-header"
      @click.native="selectImage">
      <i
        v-if="!disabled"
        class="wukong wukong-file f-logo" />

      <div class="f-name">附件</div>
      <input
        ref="fileInput"
        type="file"
        class="bar-iput"
        accept="*.*"
        :multiple="props.multiple"
        @change="xhUploadFile">
    </flexbox>
    <div class="f-body">
      <flexbox
        v-for="(item, index) in dataValue"
        :key="index"
        class="f-item">
        <i class="wukong wukong-file f-img" />
        <div class="f-name" @click="perviewClick(item, index)">{{ item | fileName }}</div>
        <div
          v-if="!disabled"
          class="close-button"
          @click.stop="deleteFile(item, index)">×</div>
      </flexbox>
    </div>
  </div>
</template>
<script type="text/javascript">
import { crmFileDeleteAPI } from '@/api/common'

import { fileSize, canPreviewFile, wkPreviewFile } from '@/utils'
import ArrayMixin from './ArrayMixin'

export default {
  name: 'XhFiles', // 新建 file  以数组的形式上传
  components: {},
  filters: {
    fileName(file) {
      const name = file.name && file.name.length > 10 ? (file.name.substring(0, 10) + '...') : file.name
      return name + '(' + fileSize(file.size) + ')'
    }
  },
  mixins: [ArrayMixin],
  props: {
    props: {
      type: Object,
      default: () => {
        return {
          multiple: true
        }
      }
    }
  },
  data() {
    return {
      batchId: '' // 批次ID
    }
  },
  computed: {},
  watch: {
    dataValue: {
      handler() {
        if (!this.batchId && this.dataValue && this.dataValue.length) {
          this.batchId = this.dataValue[0].batchId
        }
      },
      immediate: true
    }
  },
  mounted() {},
  methods: {
    selectImage() {
      if (!this.disabled) {
        this.$refs.fileInput.click()
      }
    },

    /**
     * 图片选择出发
     */
    xhUploadFile(event) {
      var files = event.target.files
      var firstFile = files[0]
      this.sendFileRequest(firstFile, () => {
        for (let index = 1; index < files.length; index++) {
          const file = files[index]
          this.sendFileRequest(file)
        }
        event.target.value = ''
      })
    },

    /**
     * @description: 发送请求
     * @param {*} file
     * @param {*} result
     * @return {*}
     */
    sendFileRequest(file, result) {
      this.$wkUploadFile.upload({
        file: file,
        params: {
          batchId: this.batchId
        }
      }).then(({ res }) => {
        const data = res.data || {}
        if (this.batchId == '') {
          this.batchId = data.batchId
        }
        data.size = fileSize(data.size)
        this.dataValue.push(data)
        this.$emit('value-change', {
          index: this.index,
          value: this.dataValue
        })
        if (result) {
          result()
        }
      }).catch(() => {})
    },

    /**
     * @description: 删除图片
     * @param {*} item
     * @param {*} index
     * @return {*}
     */
    deleteFile(item, index) {
      this.$confirm('您确定要删除该文件吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          crmFileDeleteAPI({
            id: item.fileId
          })
            .then(res => {
              this.dataValue.splice(index, 1)
              this.$emit('value-change', {
                index: this.index,
                value: this.dataValue
              })
              this.$message.success('操作成功')
            })
            .catch(() => {})
        })
        .catch(() => {})
    },

    /**
     * @description: 预览附件
     * @param {*}
     * @return {*}
     */
    perviewClick(data, index) {
      if (canPreviewFile(data.name)) {
        wkPreviewFile(data.url, data.name)
      } else {
        this.$wkPreviewFile.preview({
          index: index,
          data: this.dataValue
        })
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.xh-files-cont {
  position: relative;
  padding: 0 20px 0 6px;
  font-size: $--font-size-base;
  line-height: $--input-height;
  color: $--form-field-default-text-color;
  background-color: $--input-background-color;
  border: $--border-medium;
  border-radius: $--border-radius-base;

  &:hover {
    color: $--form-field-hover-text-color;
    background-color: $--form-field-hover-bg-color;
    border-color: $--form-field-hover-border-color;
  }
}

.xh-files-cont.is_disabled {
  color: $--input-disabled-color;
  cursor: not-allowed;
  background-color: $--input-disabled-fill;
  border-color: $--input-disabled-border;

  .f-name {
    cursor: not-allowed;
  }
}

.xh-files-cont.is_valid:hover {
  border-color: $--input-disabled-color;
}

.f-header {
  cursor: pointer;

  // padding: 5px 0 5px;
  .f-logo {
    margin-right: 8px;
    color: $--input-placeholder-color;
  }

  .f-name {
    color: $--input-placeholder-color;
  }
}

.f-header.is_disabled {
  cursor: not-allowed;
}

.f-body {
  .f-item {
    height: 25px;
    padding: 3px 0;

    .f-img {
      position: block;
      margin-right: 8px;
      color: $--color-primary;
    }

    .f-name {
      cursor: pointer;
    }

    .close-button {
      cursor: pointer;
    }
  }
}

.bar-iput {
  position: absolute;
  top: 0;
  left: 0;
  width: 0;
  height: 0;
  opacity: 0;
}
</style>
