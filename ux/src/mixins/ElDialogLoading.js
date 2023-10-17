export default {
  watch: {
    loading(v) {
      if (v) {
        const dialogPanel = this.$refs.wkDialog.$refs.dialog
        this.loadingInstance = this.$loading({
          target: dialogPanel
        })
      } else if (this.loadingInstance) {
        this.loadingInstance.close()
      }
    }
  }
}
