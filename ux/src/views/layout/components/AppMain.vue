<template>
  <div class="app-main">
    <slot name="header" />
    <router-view :key="$route.path" class="app-main-content" />
    <slot />
  </div>
</template>

<script>
export default {
  name: 'AppMain',
  computed: {}
}
</script>

<style scoped>
.app-main {
  position: relative;
  height: 100%;
}
</style>
