<template>
  <div v-loading="loading" class="wk-pic">
    <div class="content">
      <flexbox class="images">
        <template v-if="imageList.length">
          <div
            v-for="(item, index) in imageList"
            :key="index"
            class="img-box">
            <img
              :key="item.url"
              v-src="item.url"
              class="img">
            <div
              v-if="!disabled"
              class="img-model">
              <i
                class="el-icon-zoom-in set-img-zoom"
                @click.stop="previewImage(index)" />
              <i
                class="el-icon-delete set-img-delete"
                @click.stop="deleteImg(index)" />
            </div>
          </div>
        </template>
        <!-- 上传 -->
        <el-upload
          action=""
          :class="{'hide-upload': hideUpload, 'hide-upload-list': true }"
          :file-list="[]"
          :http-request="upLoad"
          :limit="max"
          :disabled="disabled"
          accept="image/*"
          list-type="picture-card"
          drag>
          <template slot="default">
            <i class="el-icon-plus" />
            <div class="upload-text">点击或拖拽上传</div>
          </template>
        </el-upload>
      </flexbox>
    </div>

    <div class="tips">支持jpg、gif、png等格式的图片，最多可添加{{ max }}张</div>
  </div>
</template>

<script>
import { crmFileSaveByBatchIdAPI } from '@/api/common'
import { adminFileDeleteByIdAPI } from '@/api/admin/file'

import { toArrayObjectByPicUrl, checkFileSize } from '@/utils'
import Compressor from 'compressorjs'

export default {
  // 自定义字段库 图片上传
  name: 'WkPicture',

  components: {},

  props: {
    value: { // 绑定的 url
      type: String,
      default: ''
    },
    defaultValue: { // 默认图片
      type: String,
      default: ''
    },
    max: {
      type: Number,
      default: 1
    },
    disabled: Boolean
  },

  data() {
    return {
      loading: false,
      imageList: []
    }
  },

  computed: {
    hideUpload() {
      return this.imageList.length >= this.max
    }
  },

  watch: {
    defaultValue: {
      handler(val) {
        if (val.length > 0) {
          console.log(val, 'defaultValue')
          this.imageList = toArrayObjectByPicUrl(val || '')
          this.$nextTick(() => {
            this.updateVa()
          })
        }
      },
      deep: true,
      immediate: true
    },
    value: {
      handler(val) {
        this.imageList = toArrayObjectByPicUrl(val || '')
      },
      immediate: true
    }
  },

  created() {

  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * @description: 预览图片
     * @param {*} index
     * @return {*}
     */
    previewImage(index) {
      this.$wkPreviewFile.preview({
        index: index,
        data: this.imageList.map((item, itemIndex) => ({ url: item.url || item.thumb?.url, name: `${itemIndex + 1}.png` }))
      })
    },

    /**
     * @description: 删除图片
     * @param {*} index
     * @return {*}
     */
    deleteImg(index) {
      const params = {}
      params.id = this.imageList[index].url.slice(16)

      this.$confirm('此操作将永久删除该图片, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        adminFileDeleteByIdAPI(params.id).then(res => {
          this.$message.success('删除成功')
          this.imageList.splice(index, 1)
          this.updateValue()
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },

    /**
     * @description: 上传到服务器
     * @param {*} event
     * @return {*}
     */
    upLoad(event) {
      const file = event.file
      if (!checkFileSize(file.size, 20)) {
        this.$message.error('单张图片不能超过20M')
        return
      }

      this.loading = true
      new Compressor(file, {
        quality: 0.1,
        convertSize: 10000,
        success: (thumbFile) => {
          Promise.all([
            crmFileSaveByBatchIdAPI({
              file: event.file
            }),
            crmFileSaveByBatchIdAPI({
              file: thumbFile
            })
          ]).then(resArr => {
            this.loading = false
            const fileData = resArr[0].data || {}
            const thumbData = resArr[1].data || {}

            this.imageList.push({
              ...fileData,
              thumb: thumbData
            })
            this.updateValue()
          }).catch(() => {
            this.loading = false
          })
        },
        error: (err) => {
          this.loading = false
          console.log(err.message)
          this.$message.error('图片压缩失败')
        }
      })
    },

    /**
     * @description: 更新值
     * @return {*}
     */
    updateValue() {
      const allUrl = []
      for (const item of this.imageList) {
        if (item.thumb) {
          allUrl.push(`${item.url}&${item.thumb.url}`)
        } else {
          allUrl.push(item.url)
        }
      }
      const value = allUrl.join()
      this.$emit('input', value)
      this.$emit('change', value)
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-pic {
  .content {
    .images {
      display: flex;
      flex-wrap: wrap;
      align-items: flex-start;
      justify-content: flex-start;
      margin: 0;
      margin-bottom: 5px;
      overflow-x: auto;

      .img-box {
        position: relative;
        flex-shrink: 0;
        height: 80px;
        margin-right: 10px;
        margin-bottom: 5px;
        overflow: hidden;
        cursor: pointer;

        &:hover {
          .img-model {
            visibility: visible;
          }
        }

        .img {
          width: 100px;
          height: 80px;
          border-radius: $--border-radius-base;
        }

        .img-model {
          position: absolute;
          top: 0;
          right: 0;

          // bottom: 0;
          left: 0;
          z-index: 10;
          line-height: 80px;
          visibility: hidden;
          background-color: #2d3037;
          border-radius: 6px;
          opacity: 0.8;

          .set-img-zoom {
            padding-left: 15px;
            font-size: 20px;
            color: white;
            cursor: pointer;
          }

          .set-img-delete {
            padding-left: calc(50% - 35px);
            font-size: 20px;
            color: white;
            cursor: pointer;
          }
        }
      }

      .img-upload-box {
        position: relative;
        display: flex;
        flex-shrink: 0;
        width: 100px;
        height: 80px;
        margin-bottom: 5px;
        text-align: center;
        cursor: pointer;
        border: 1px #c0ccda dashed;
        border-radius: 6px;

        .file-input {
          width: 0;
          height: 0;
          visibility: hidden;
        }

        .cross {
          margin-left: calc(50% - 10px);
          font-size: 20px;
          color: $--color-text-primary;
        }
      }
    }
  }

  ::v-deep.el-icon-zoom-in {
    margin-right: 10px;
  }

  .hide-upload {
    ::v-deep .el-upload--picture-card {
      display: none;
    }
  }

  .hide-upload-list {
    ::v-deep .el-upload-list {
      display: none;
    }
  }

  ::v-deep .el-upload-list--picture-card .el-upload-list__item {
    width: 100px;
    height: 80px;
    margin: 0 8px 0 0;
  }

  ::v-deep .el-upload--picture-card {
    width: 100px;
    height: 80px;
    line-height: 80px;
    border: none;

    .el-upload-dragger {
      position: relative;
      width: 100px;
      height: 80px;

      .el-icon-plus {
        position: absolute;
        top: 18px;
        left: calc(50% - 10px);
        font-size: 20px;
        color: $--color-text-primary;
      }

      .upload-text {
        margin-top: 12px;
        font-size: 12px;
        color: $--color-text-placeholder;
        text-align: center;
      }
    }
  }

  .tips {
    font-size: 12px;
    line-height: 24px;
    color: $--color-text-placeholder;
  }
}
</style>
