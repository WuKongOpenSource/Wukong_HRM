<template>
  <video id="videoPlayer" :src="sourceData" :width="width" :height="height" controls />
</template>

<script>
import merge from '@/utils/merge'

const DefaultWkVideo = {
  fileRequest: null, // 添加请求和参数
  fileParams: null
}

export default {
  // 视频播放组件
  name: 'WkVideo',

  components: {},

  props: {
    props: Object,
    width: {
      type: [String, Number],
      default: '100%'
    },
    height: {
      type: [String, Number],
      default: '100%'
    }
  },

  data() {
    return {
      sourceData: ''
    }
  },

  computed: {
    // 合并 props
    config() {
      return merge({ ...DefaultWkVideo }, this.props || {})
    }
  },

  watch: {},

  created() {
    this.getSource()
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * @description: 获取资源
     * @return {*}
     */
    getSource() {
      this.config.fileRequest(this.config.fileParams).then(res => {
        this.sourceData = URL.createObjectURL(res.data)
        this.$refs.videoSource = this.sourceData
      }).catch(() => {
      })
    }
  }
}
</script>

  <style lang="scss" scoped>

  </style>

