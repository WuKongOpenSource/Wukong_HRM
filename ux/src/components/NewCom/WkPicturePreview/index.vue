<template>
  <div class="wk-picture-preview" :class="[wrap ? 'wrap' : 'nowrap']">
    <template
      v-if="showMorePicture">
      <preview-image
        v-for="(valObj, valIndex) in getPictureArray"
        :key="valIndex"
        class="preview"
        :src="valObj.url"
        :index="valIndex"
        show-preview
        @preview="handlePreview" />
    </template>
    <template v-else>
      <preview-image
        :class="[wrap ? 'is-wrap' : 'is-nowrap']"
        :src="getOnePicture"
        :index="1"
        show-preview
        @preview="handlePreview" />
    </template>
  </div>
</template>

<script>
import PreviewImage from './PreviewImage'
import { isArray, isString } from '@/utils/types'
import { getSmallPicUrl } from '@/utils'

export default {
  name: 'WkPicturePreview',
  components: {
    PreviewImage
  },
  props: {
    wrap: {
      type: Boolean,
      default: false
    },
    pictureUrl: [Array, String]
  },
  data() {
    return {

    }
  },
  computed: {
    /**
     * @description: 是否显示多图片
     * @return {*}
     */
    showMorePicture() {
      if (
        isArray(this.pictureUrl) ||
        isString(this.pictureUrl) && (this.pictureUrl.includes(',') || this.pictureUrl.includes('&'))
      ) {
        return true
      }
      return false
    },

    /**
     * @description: 解析url为数组
     * @return {*}
     */
    getPictureArray() {
      const value = this.pictureUrl
      if (isArray(value)) {
        return value.map((item, index) => {
          return { url: getSmallPicUrl(item.url), name: `preview${index + 1}.png` }
        })
      } else if (isString(value)) {
        return value.split(',').map((item, index) => {
          return { url: getSmallPicUrl(item), name: `preview${index + 1}.png` }
        })
      } else {
        return []
      }
    },

    getOnePicture() {
      return getSmallPicUrl(this.pictureUrl)
    }
  },
  methods: {
    handlePreview(index) {
      this.$wkPreviewFile.preview({
        index: index,
        data: this.getBigPicInfo()
      })
    },

    getBigPicInfo() {
      const value = this.pictureUrl
      if (isArray(value)) {
        return value.map((item, index) => {
          item.name = `preview${index + 1}.png`
          return item
        })
      } else if (isString(value)) {
        if (value) {
          return value.split(',').map((item, index) => {
            if (item.includes('&')) {
              const [bigUrl] = item.split('&')
              return { url: bigUrl, name: `preview${index + 1}.png` }
            } else {
              return { url: item, name: `preview${index + 1}.png` }
            }
          })
        } else {
          return []
        }
      } else {
        return []
      }
    }
  }
}
</script>
<style lang='scss' scoped>
.wk-picture-preview {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.wrap {
  flex-wrap: wrap;
}

.nowrap {
  ::v-deep .preview {
    margin-top: 0;
    margin-bottom: 0;
  }
}

.is-nowrap {
  margin: 0 8px 0 0;
}
</style>
