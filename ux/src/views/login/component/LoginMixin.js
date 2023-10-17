export default {
  data() {
    return {
      redirect: undefined
    }
  },

  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },

  methods: {
    /**
     * 更新form状态
     */
    changeFormState() {
      return new Promise((resolve, reject) => {
        this.$nextTick(() => {
          const form = this.$refs.ruleForm
          form.$children.forEach(item => {
            if (item.prop) {
              this.ruleResult[item.prop].validate = item.validateState === 'success'
            }
          })
          resolve(this.ruleResult)
        })
      })
    }
  }
}
