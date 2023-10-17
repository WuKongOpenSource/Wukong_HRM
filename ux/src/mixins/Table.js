
import WkEmpty from '@/components/WkEmpty'

export default {
  // 表格混入
  components: {
    WkEmpty
  },

  props: {},

  data() {
    return {
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,

      rowHeight: 44,

      otherTableHeight: 265, // 除表格其他的高度
      tableHeight: 200 // 表的高度
    }
  },

  computed: {},

  watch: {},

  created() {},

  mounted() {
    window.onresize = () => {
      this.updateTableHeight()
    }
  },

  beforeDestroy() {},

  methods: {
    /**
     * 更改每页展示数量
     * @param {*} val
     */
    handleSizeChange(val) {
      this.pageSize = val
      this.getList()
    },

    /**
     * 更改当前页数
     * @param {*} val
     */
    handleCurrentChange(val) {
      this.currentPage = val
      this.getList()
    },

    /**
     * 更新表高
     */
    updateTableHeight() {
      const maxTableHeight = document.documentElement.clientHeight - this.otherTableHeight

      const dataHeight = this.rowHeight * this.list.length + 41 // 头高度
      if (dataHeight > maxTableHeight) {
        this.tableHeight = maxTableHeight
      } else {
        this.tableHeight = this.list.length === 0 ? 200 : dataHeight
      }
    }
  }
}
