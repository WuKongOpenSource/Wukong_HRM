<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-30 15:16:52
 * @LastEditTime: 2020-05-30 15:31:56
 * @LastEditors: yang
-->
<template>
  <div class="wk-file-select" @click="handleClick">
    <slot />
  </div>
</template>

<script>

export default {
  // 用this.$wkFile.select()替代
  name: 'WkFileSelect',
  components: {},
  props: {
    name: {
      type: String,
      default: 'file'
    },
    disabled: Boolean,
    multiple: Boolean,
    accept: {
      type: String,
      default: '*.*' // *.* image/*
    }
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {
    handleChange(ev) {
      const files = ev.target.files

      if (!files) return
      this.$emit('change', files, ev)
    },

    handleClick() {
      if (!this.disabled) {
        this.$wkFile.select({
          name: this.name,
          multiple: this.multiple,
          accept: this.accept
        }).then(ev => {
          this.handleChange(ev)
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-file-select {
  &__input {
    display: none;
  }
}
</style>
