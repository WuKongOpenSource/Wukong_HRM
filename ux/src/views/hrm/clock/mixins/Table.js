import clockStatusLib from '../clockStatusLib'
import { isEmpty } from '@/utils/types'

export default {
  created() {
    this.getFieldList()
  },
  data() {
    return {
      loading: false, // 加载动画
      createType: '', // 创建类型
      tableHeight: document.documentElement.clientHeight - 260, // 表的高度
      list: [],
      fieldList: [],
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      otherParams: {},
      request: '',
      sortData: {},
      // 筛选宽
      popoverFilterWidth: 150,
      attendanceDetailDialog: false,
      attendanceDetail: {},
      statusMap: clockStatusLib,

      params: {}
    }
  },
  computed: {
    searchComputed() {
      return this.$parent.search || this.search
    },
    date() {
      return this.$parent.month || this.month
    },
    filterObjComputed() {
      return this.$parent.filterObj || this.filterObj
    },
    times() {
      return [
        this.$moment(this.date, 'YYYY-MM').startOf('month').format('YYYY-MM-DD'),
        this.$moment(this.date, 'YYYY-MM').endOf('month').format('YYYY-MM-DD')
      ]
    }
  },
  methods: {

    /**
     *
     * @param {*} column
     * @param {*} prop
     * @param {*} order
     */
    sortChange(column, prop, order) {
      this.currentPage = 1
      this.sortData = column || {}
      this.getList()
    },

    /**
     * 行点击
     */
    rowClick() {
      if (this.recordType == 'punching') {
        this.attendanceDetailDialog = true
        this.attendanceDetail = {}
      }
    },
    /**
     * 获取当月时间字段
     * @param {*} year
     * @param {*} month
     * @returns
     */
    getMonthField(year, month) {
      const tempArr = []
      const days = this.getCurrentMonthDay(year, month)
      for (let index = 1; index <= days; index++) {
        tempArr.push({
          fieldName: 'day' + index,
          name: index + '',
          width: 100
        })
      }
      return tempArr
    },
    /**
     * 获取当月天数
     */
    getCurrentMonthDay(year = new Date().getFullYear(), month = new Date().getMonth() + 1) {
      const d = new Date(year, month, 0)
      return d.getDate()
    },
    /**
     * 刷新
     */
    refreshList() {
      this.handleCurrentChange(1)
    },

    /**
     * 更改每页展示数量
     */
    handleSizeChange(val) {
      this.pageSize = val
      this.getList()
    },

    /**
     * 更改当前页数
     */
    handleCurrentChange(val) {
      this.currentPage = val
      this.getList()
    },
    /**
     * 格式化字段
     */
    fieldFormatter(row, column) {
      if (column.property == 'clockType') {
        return {
          1: '上班打卡',
          2: '下班打卡'
        }[row[column.property]] || '--'
      } else if (column.property == 'type') {
        return {
          1: '手机端打卡', 2: 'HR添加', 3: '自动打卡'
        }[row[column.property]] || '--'
      } else if (column.property == 'attendanceTime') {
        return row[column.property].split(' ').shift()
      } else {
        return isEmpty(row[column.property]) ? '--' : row[column.property]
      }
    },
    /**
     *
     * @param {Promise} request
     * @param {Object} otherParams
     */
    getList(cb) {
      if (this.$refs.originalTable) {
        this.$refs.originalTable.clearSelection()
      }

      this.loading = true
      const params = {
        page: this.currentPage,
        limit: this.pageSize,
        search: this.searchComputed,
        times: this.times
      }
      const request = this.request
      const otherParams = this.otherParams
      for (var key in this.filterObjComputed) {
        params[key] = this.filterObjComputed[key]
      }
      if (this.sortData.order) {
        params.sortField = this.sortData.prop
        params.order = this.sortData.order == 'ascending' ? 2 : 1
      }

      this.params = {
        ...params,
        ...otherParams
      }

      request({ ...params, ...otherParams }).then(res => {
        this.loading = false
        this.list = res.data.list

        console.log('this.list-', this.list)
        this.total = res.data.totalRow
        if (res.data.totalRow && Math.ceil(res.data.totalRow / this.pageSize) < this.currentPage && this.currentPage > 1) {
          this.currentPage = this.currentPage - 1
          this.getList()
        } else {
          this.total = res.data.totalRow
          this.loading = false
        }

        this.$nextTick(() => {
          const warpDom = document.querySelector('.el-table__body-wrapper')
          warpDom.scrollTop = 1
          if (warpDom.scrollLeft != 0) {
            warpDom.scrollLeft = warpDom.scrollLeft - 1
          }
        })
        cb && cb()
      }).catch(() => {
        this.loading = false
      })
    }

  }
}
