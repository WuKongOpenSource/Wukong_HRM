<template>
  <input
    ref="wkFileInput"
    :name="data.name"
    :multiple="data.multiple"
    :accept="data.accept"
    style="display: none;"
    type="file"
    @change="handleChange">
</template>

<script>
import merge from '@/utils/merge'

const DefaultWkFileSelectData = {
  name: 'file',
  accept: '*.*',
  multiple: true
}

export default {
  // 附件选择
  name: 'WkFile',

  components: {},

  props: {},

  data() {
    return {
      data: {},
      resolve: null
    }
  },

  computed: {},

  watch: {},

  created() {},

  mounted() {
  },

  beforeDestroy() {},

  methods: {
    handleChange(ev) {
      this.resolve(ev)
    },

    select(data) {
      return new Promise((resolve, reject) => {
        this.data = merge({ ...DefaultWkFileSelectData }, data || {})
        this.$nextTick(() => {
          this.$refs.wkFileInput.value = null
          this.$refs.wkFileInput.click()
          this.resolve = resolve
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
