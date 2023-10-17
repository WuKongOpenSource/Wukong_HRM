<template>
  <div class="app-child-main">
    <router-view class="app-child-main-content" />
  </div>
</template>

<script>
export default {
  name: 'AppChildMain'
}
</script>

<style scoped>
.app-child-main {
  position: relative;
  height: 100%;
}
</style>
