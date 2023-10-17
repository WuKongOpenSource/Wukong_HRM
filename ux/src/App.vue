<template>
  <div id="app">
    <router-view class="router-view" />
  </div>
</template>

<script>
import cache from '@/utils/cache'

export default {
  name: 'App',
  components: {
  },
  mixins: [],
  data() {
    return {}
  },
  watch: {
    $route(to, from) {
      const { meta, params/*, name*/ } = to
      let title = this.WKConfig.companyName
      if (meta.title) {
        title += ' - ' + meta.title
      } else if (params && params.title) {
        title += ' - ' + params.title
      }
      document.title = title

      this.$wkPreviewFile.closeViewer() // 切换页面隐藏图片预览
    }
  },
  mounted() {
    this.addDocumentVisibilityChange()
  },
  methods: {
    /**
     * @description: 当前标签再次显现进行的处理逻辑
     * @param {*}
     * @return {*}
     */
    addDocumentVisibilityChange() {
      // 网页当前状态判断
      // hidden,
      var state, visibilityChange
      if (typeof document.hidden !== 'undefined') {
        // hidden = 'hidden'
        visibilityChange = 'visibilitychange'
        state = 'visibilityState'
      } else if (typeof document.mozHidden !== 'undefined') {
        // hidden = 'mozHidden'
        visibilityChange = 'mozvisibilitychange'
        state = 'mozVisibilityState'
      } else if (typeof document.msHidden !== 'undefined') {
        // hidden = 'msHidden'
        visibilityChange = 'msvisibilitychange'
        state = 'msVisibilityState'
      } else if (typeof document.webkitHidden !== 'undefined') {
        // hidden = 'webkitHidden'
        visibilityChange = 'webkitvisibilitychange'
        state = 'webkitVisibilityState'
      }
      // 添加监听器，在title里显示状态变化
      document.addEventListener(visibilityChange, () => {
        if (document[state] == 'visible') {
          if (cache.updateAxiosHeaders() && this.$route.name === 'login') {
            window.location.reload()
          }
        }
        this.$bus.emit('document-visibility', document[state])
      }, false)
    }
  }
}
</script>

<style lang="scss">
#app {
  position: relative;
  display: flex;
  flex-direction: column;
  width: 100%;
  min-width: 1200px;
  height: 100%;
  min-height: 605px;
}
</style>
