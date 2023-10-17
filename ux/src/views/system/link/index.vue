<template>
  <components
    :is="currentComponent"
  />
</template>

<script>
import Examine from './examine/index.vue'
import VerifyEmail from './verifyEmail'

export default {
  name: 'LinkPage',
  components: {
    Examine,
    VerifyEmail
  },
  computed: {
    currentComponent() {
      return {
        email: 'VerifyEmail',
        examineEmail: 'Examine'
      }[this.$route.query?.type]
    }
  },
  created() {
    if (!this.$route.query?.type) {
      this.$router.replace('/')
    }
  }
}
</script>

<style lang="scss" scoped>
.wrapper {
  width: 100%;
  height: 100%;
}

</style>
