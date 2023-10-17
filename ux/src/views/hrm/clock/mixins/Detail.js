
export default {
  data() {
    return {
      // detailData: null
    }
  },

  computed: {
    canShowDetail() {
      return true
    }
  },

  watch: {
    id: function() {
      this.getDetail()
    },
    detailData() {
      if (this.$refs.crmDetailMain && this.$refs.crmDetailMain.style.background == 'white') {
        this.$refs.crmDetailMain.style.background = 'inherit'
      }
    }
  },

  mounted() {
    this.$refs.crmDetailMain.style.background = 'white'
  },

  beforeDestroy() {
  },

  methods: {
    viewAfterEnter() {
      if (this.canShowDetail) {
        this.getDetail()
      }
    }
  }

}
