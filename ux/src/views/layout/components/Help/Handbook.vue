<template>
  <div class="main">
    <div class="main-title">
      {{ name }}
    </div>
    <div class="main-body">
      <div
        ref="preview"
        class="md-body"
        v-html="content"
      />
    </div>
  </div>
</template>

<script>
import request from './request'

import { marked } from 'marked'

export default {
  // Handbook
  name: 'Handbook',

  components: {},

  props: {
    type: [String, Number],
    id: [String, Number]
  },

  data() {
    return {
      name: '',
      content: '',
      imgs: []
    }
  },

  computed: {},

  watch: {
    id: {
      handler() {
        if (this.id) {
          this.name = ''
          this.content = ''
          this.getData()
        }
      },
      immediate: true
    }
  },

  created() {
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    getData() {
      request.post('/docInfo', {
        item: this.type,
        menu_id: this.id
      })
        .then((response) => {
          const res = response.data
          if (response.status === 200 && res.code === 200) {
            const resData = res.data || []
            this.name = resData.name
            this.content = marked.parse(resData.content)

            this.$nextTick(() => {
              this.loadImages()
            })
          }
        })
        .catch(() => {
        })
    },

    /**
     * 载入图片
     */
    loadImages() {
      const imgs = this.imgs
      if (imgs.length > 0) {
        for (let i = 0, len = imgs.length; i < len; i++) {
          imgs[i].onclick = null
        }
      }
      setTimeout(() => {
        this.imgs = this.$refs.preview.querySelectorAll('img')
        for (let i = 0, len = this.imgs.length; i < len; i++) {
          this.imgs[i].onclick = () => {
            const urls = Array.from(this.imgs).map(img => img.getAttribute('src'))
            this.$wkPreviewFile.preview({
              index: i,
              data: urls.map(src => {
                return {
                  url: src,
                  name: src
                }
              })
            })
          }
        }
      }, 600)
    }
  }
}
</script>
<style lang="scss">
.md-body {
  padding-left: 5px;

  img {
    max-width: 100%;
    height: auto;
    vertical-align: middle;
    box-shadow: $--box-shadow-dark;
  }

  .h1,
  .h2,
  .h3,
  .h4,
  .h5,
  .h6,
  h1,
  h2,
  h3,
  h4,
  h5,
  h6 {
    font-weight: bold;
    line-height: 1.1;
  }

  .h1,
  .h2,
  .h3,
  h1,
  h2,
  h3 {
    margin-top: 16px;
    margin-bottom: 8px;
  }

  .h4,
  .h5,
  .h6,
  h4,
  h5,
  h6 {
    margin-top: 8px;
    margin-bottom: 8px;
  }

  h1 {
    font-size: 24px;
  }

  h2 {
    font-size: 16px;
  }

  p {
    font-size: 14px;
    font-weight: normal;
    line-height: 2;
    white-space: pre-wrap;
  }
}
</style>
<style lang="scss" scoped>
@import "./style.scss";
</style>
