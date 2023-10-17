<template>
  <div ref="wkSignaturePad" class="wk-signature-pad">
    <wk-signature-image
      v-if="disabled"
      :src="value"
      :height="height"
    />
    <template v-else>
      <vue-signature-pad
        ref="signaturePad"
        :key="height"
        :options="options"
        :height="height"
        width="100%"
      />
      <div class="wk-signature-pad__handle">
        <el-button type="text" icon="wk wk-icon-reply" @click="handleClick('undo')">撤回</el-button>
        <el-button type="text" icon="wk wk-icon-bin" @click="handleClick('clear')">清空</el-button>
      </div>
    </template>
  </div>
</template>

<script>
import { adminFileQueryOneByBatchIdAPI, adminFileDeleteByBatchIdAPI } from '@/api/admin/file'
import { crmFileSingleSaveAPI } from '@/api/common'

import VueSignaturePad from './VueSignaturePad'
import WkSignatureImage from './Image'

import { valueEquals } from 'element-ui/lib/utils/util'
import { getImageData } from '@/utils'
import Emitter from 'element-ui/lib/mixins/emitter'

export default {
  // 签名
  name: 'WkSignaturePad',

  components: {
    VueSignaturePad,
    WkSignatureImage
  },

  mixins: [Emitter],

  props: {
    value: String, // batchId 交互
    data: String, // 同步数据源
    disabled: Boolean
  },

  data() {
    return {
      options: {
        backgroundColor: '#fff',
        minWidth: 1,
        maxWidth: 3
      },
      height: '150px'
    }
  },

  computed: {
  },

  watch: {
    data(newVal, oldVal) {
      if (!valueEquals(newVal, oldVal)) {
        this.$refs.signaturePad.fromDataURL(newVal)
      }
    }
  },

  created() {},

  mounted() {
    if (this.value) {
      this.getData()
    }
    // else {
    //   this.$emit('input', guid())
    // }

    this.height = `${parseInt(this.$refs.wkSignaturePad.clientWidth / 2.6)}px`

    // 非禁用,拿到签名增加事件
    if (!this.disabled) {
      this.$nextTick(() => {
        this.$refs.signaturePad.signaturePad.addEventListener('endStroke', () => {
          this.endStroke()
        })
      })
    }
  },

  beforeDestroy() {},

  methods: {
    getData() {
      adminFileQueryOneByBatchIdAPI(this.value).then(res => {
        const resData = res.data
        if (resData) {
          getImageData(resData.url)
            .then(data => {
              var img = new Image()
              img.onload = () => {
                const canvasWidth = this.$refs.signaturePad.canvas.width
                const width = img.width
                const ratio = canvasWidth / width
                console.log(width, canvasWidth, ratio)
                this.options.minWidth = this.options.minWidth * ratio
                this.options.maxWidth = this.options.maxWidth * ratio

                this.$nextTick(() => {
                  this.$refs.signaturePad.fromDataURL(data.src)
                })
              }
              img.src = data.src
            })
            .catch(() => {
            })
        }
      }).catch(() => {

      })
    },

    endStroke() {
      const { isEmpty, data } = this.$refs.signaturePad.saveSignature()
      this.$emit('update:data', data)
      if (!isEmpty) {
        this.$refs.signaturePad.toBlob((blob) => {
          this.uploadFile(blob)
        })
      }
    },

    uploadFile(blob) {
      crmFileSingleSaveAPI({
        file: blob,
        batchId: this.value
      }).then(res => {
        const { batchId } = res.data || {}
        if (batchId && this.value !== batchId) {
          this.$emit('input', batchId)
          this.dispatch('ElFormItem', 'el.form.change', batchId)
        }
      }).catch(() => {})
    },

    handleClick(type) {
      if (type === 'clear') {
        this.$refs.signaturePad.clearSignature()
        if (this.value) {
          this.deleteByBatchId(this.value)
        }
        this.$emit('input', '')
      } else if (type === 'undo') {
        this.$refs.signaturePad.undoSignature()
        this.$nextTick(() => {
          const { isEmpty } = this.$refs.signaturePad.saveSignature()
          if (isEmpty) {
            if (this.value) {
              this.deleteByBatchId(this.value)
            }
            this.$emit('input', '')
          }
          this.endStroke()
        })
      }
    },

    /**
     * 删除附件
     */
    deleteByBatchId(batchId) {
      adminFileDeleteByBatchIdAPI({ batchId }).then(res => {}).catch(() => {})
    }
  }
}
</script>
<style lang="scss">
.el-form-item.is-error {
  .wk-signature-pad {
    border-color: #f56c6c;
  }
}
</style>

<style lang="scss" scoped>
.wk-signature-pad {
  position: relative;
  overflow: hidden;
  font-size: $--font-size-base;
  color: $--form-field-default-text-color;
  background-color: $--input-background-color;
  border: $--border-medium;
  border-radius: $--border-radius-base;

  &__handle {
    position: absolute;
    right: 15px;
    bottom: 0;

    .el-button--text {
      color: $--color-text-secondary;

      &:hover {
        color: $--color-primary;
      }
    }
  }
}
</style>

