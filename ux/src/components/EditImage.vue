<template>
  <el-dialog
    v-loading="loading"
    :title="title"
    :width="width"
    :append-to-body="true"
    :close-on-click-modal="false"
    :visible.sync="showDialog"
    @close="hiddenView">
    <flexbox class="content">
      <div class="cropper-box">
        <vueCropper
          ref="cropper"
          :can-move="true"
          :auto-crop="true"
          :fixed="true"
          :fixed-number="fixedNumber"
          :img="cropperImg"
          output-type="png"
          @realTime="realTime" />
      </div>
      <div class="preview">
        <div class="preview-name">预览</div>
        <img
          :style="{'width': previewWidth, 'height': previewHeight, 'border-radius': previewRadius}"
          :src="previewImg"
          class="preview-img">
      </div>
    </flexbox>
    <div
      slot="footer"
      class="dialog-footer">
      <el-button
        type="primary"
        @click="submiteImage()">{{ saveButtonTitle }}</el-button>
    </div>
  </el-dialog>
</template>
<script type="text/javascript">
import { VueCropper } from 'vue-cropper'

export default {
  name: 'EditImage', // 处理头像
  components: {
    VueCropper
  },
  props: {
    width: {
      type: String,
      default: '450px'
    },
    title: {
      type: String,
      default: '编辑头像'
    },
    saveButtonTitle: {
      type: String,
      default: '开始上传'
    },
    show: {
      type: Boolean,
      default: false
    },
    fixedNumber: {
      type: Array,
      default: () => {
        return [1, 1]
      }
    },
    previewWidth: {
      type: String,
      default: '70px'
    },
    previewHeight: {
      type: String,
      default: '70px'
    },
    previewRadius: {
      type: String,
      default: '35px'
    },
    file: [File],
    image: String
  },
  data() {
    return {
      loading: false,
      showDialog: false,
      cropperImg: '',
      previewImg: ''
    }
  },
  computed: {},
  watch: {
    show: {
      handler(val) {
        this.showDialog = val
      },
      deep: true,
      immediate: true
    },
    image: function(val) {
      this.cropperImg = val
    }
  },
  mounted() {
    this.cropperImg = this.image
  },
  methods: {
    realTime(data) {
      this.$refs.cropper.getCropData(cropperData => {
        this.previewImg = cropperData
      })
    },
    submiteImage() {
      // 获取截图的blob数据
      this.$refs.cropper.getCropBlob(data => {
        this.$emit('save', {
          blob: data,
          file: this.file,
          image: this.previewImg
        })
        this.hiddenView()
      })
    },
    hiddenView() {
      this.$emit('close')
    }
  }
}
</script>
<style lang="scss" scoped>
.cropper-box {
  width: 300px;
  height: 300px;
  margin-right: 15px;
}

.preview {
  position: absolute;
  right: 0;
  bottom: 0;

  .preview-name {
    margin-bottom: 8px;
    font-size: 13px;
    color: $--color-text-regular;
  }

  .preview-img {
    display: block;
  }
}

.content {
  position: relative;
  padding: 0 30px;
}
</style>
