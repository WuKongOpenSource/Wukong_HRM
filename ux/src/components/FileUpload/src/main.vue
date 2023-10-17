<template>
  <transition
    name="slide-fade">
    <div v-if="visible" class="file-upload">
      <flexbox class="file-upload__header">
        <div class="title">附件上传</div>
        <div>
          <el-button :icon="unfoldIcon" class="unfold-button" @click="unfoldClick" />
          <el-button class="close-button" icon="el-icon-close" @click="close" />
        </div>
      </flexbox>
      <div v-show="isUnfold" class="file-upload__body">
        <wk-upload-cell
          v-for="(item, index) in uploadList"
          :key="index"
          :index="index"
          :file="item.file"
          :request="item.request"
          :params="item.params"
          :complete="item.complete"
          @completed="uploadCompleted"
          @delete="uploadDelete"
          @cancelTokenChange="cancelTokenChange"
        />
      </div>
    </div>
  </transition>
</template>

<script>
import WkUploadCell from './WkUploadCell'

export default {
  // 文件上传
  name: 'FileUpload',
  components: {
    WkUploadCell
  },
  props: {},
  data() {
    return {
      visible: false,
      isUnfold: true,
      uploadList: []
    }
  },
  computed: {
    unfoldIcon() {
      return this.isUnfold ? 'el-icon-minus' : 'el-icon-plus'
    }
  },
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {
    /**
     * data 必须包含  file
     * 可选 request  params
     * data 辅助信息
     */
    upload(data) {
      this.visible = true
      return new Promise((resolve, reject) => { // eslint-disable-line
        this.uploadList.push({
          ...data,
          complete: false,
          resolve: resolve,
          reject: reject
        })
      })
    },

    close() {
      let allComplete = true
      for (let index = 0; index < this.uploadList.length; index++) {
        const element = this.uploadList[index]
        if (!element.complete) {
          allComplete = false
          break
        }
      }

      if (allComplete) {
        this.uploadList = []
        this.visible = false
      } else {
        this.$confirm('关闭窗口，将取消上传任务，是否继续?', '提示', {
          confirmButtonText: '继续',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            for (let index = 0; index < this.uploadList.length; index++) {
              const element = this.uploadList[index]
              if (element.cancelToken) {
                element.cancelToken()
              }
            }

            this.uploadList = []
            this.visible = false
          })
          .catch(() => {
            this.isUnfold = false
          })
      }
    },

    unfoldClick() {
      this.isUnfold = !this.isUnfold
    },

    /**
     * 上传完成
     */
    uploadCompleted(index, res) {
      const data = this.uploadList[index]
      data.res = res
      data.complete = true
      if (data.resolve) {
        data.resolve(data)
      }
    },

    uploadDelete(index) {
      this.uploadList.splice(index, 1)
    },

    cancelTokenChange(cancelToken, index) {
      const data = this.uploadList[index]
      this.$set(data, 'cancelToken', cancelToken)
    }
  }
}
</script>

<style lang="scss" scoped>
.slide-fade-enter-active,
.slide-fade-leave-active {
  will-change: transform;
  transition: all 0.35s ease;
}

.slide-fade-enter,
.slide-fade-leave-to {
  transform: translateY(100%);
}

.file-upload {
  position: fixed;
  right: 0;
  bottom: 0;
  z-index: 1000000;
  width: 380px;
  font-size: 14px;
  background-color: white;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

  &__header {
    height: 40px;
    padding: 5px 15px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

    .title {
      flex: 1;
      font-weight: bold;
    }

    .el-button {
      padding: 6px;
      margin-left: 0;
      font-weight: bold;
      background: transparent;
      border: none;
      outline: none;

      ::v-deep i {
        font-size: 15px;
        font-weight: bold;
      }
    }
  }

  &__body {
    height: 350px;
    padding: 10px;
    overflow-y: auto;

    .wk-upload-cell + .wk-upload-cell {
      margin-top: 10px;
    }
  }
}
</style>
