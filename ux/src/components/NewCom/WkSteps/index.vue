<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <flexbox class="wk-steps" align="stretch">
    <slot />
  </flexbox>
</template>

<script>

export default {
  // 步骤说明
  name: 'WkSteps',
  components: {},
  props: {},
  data() {
    return {}
  },
  computed: {},
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {}
}
</script>

<style lang="scss" scoped>
.wk-steps {
  position: relative;
  text-align: center;
}
</style>
