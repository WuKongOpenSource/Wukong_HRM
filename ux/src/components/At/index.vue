<template>
  <ContendEdit
    ref="contendEdit"
    :content.sync="content" />
</template>

<script>
import ContendEdit from '@/components/At/ContentEdit'
export default {
  name: 'At',
  components: {
    ContendEdit
  },
  props: {
    commonWords: String,
    detailContent: String
  },
  data() {
    return {
      content: '',
      selectDataList: {}
    }
  },
  watch: {
    commonWords(val) {
      if (val) {
        this.content = val
      }
    },
    detailContent: {
      handler(val) {
        if (val) {
          this.content = val
        }
      },
      immediate: true
    }
  },
  methods: {
    getAtData() {
      const { innerHTML, textContent: value } = this.$refs.contendEdit.$refs.at
      const atList = []
      const reg = /data-wk-type-id="([^"]*)"/g
      const nodeList = innerHTML.match(reg) || []
      for (const item of nodeList) {
        const userId = item.split('=')[1].slice(1, -1)
        atList.push(userId)
      }
      const data = {
        atList,
        content: innerHTML,
        value
      }
      return data
    }
  }
}
</script>

<style>

</style>
