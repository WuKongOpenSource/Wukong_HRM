<template>
  <div class="crm-relative-table">
    <flexbox v-if="config.showHeader" class="header" justify="center">
      <el-input
        v-model="search"
        :placeholder="config.placeholder"
        @keyup.enter.native="searchInput">
        <el-button
          slot="append"
          type="primary"
          @click.native="searchInput">搜索</el-button>
      </el-input>
    </flexbox>
    <div class="body">
      <el-table
        id="crm-table"
        v-loading="loading"
        :row-height="40"
        :data="list"
        :height="tableHeight + reduceHeaderHeight"
        :row-key="config.rowKey"
        use-virtual
        stripe
        highlight-current-row
        style="width: 100%;"
        @selection-change="handleSelectionChange">
        <el-table-column
          show-overflow-tooltip
          reserve-selection
          type="selection"
          align="center"
          width="55" />
        <el-table-column
          v-for="(item, index) in fieldList"
          :key="index"
          :fixed="index==0"
          :prop="item.fieldName"
          :label="item.name"
          :formatter="config.tableFormatter"
          show-overflow-tooltip />
      </el-table>
      <div class="p-contianer">
        <el-pagination
          :current-page="currentPage"
          :page-sizes="pageSizes"
          :page-size.sync="pageSize"
          :total="total"
          :pager-count="5"
          class="p-bar"
          background
          layout="prev, pager, next, sizes, total, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </div>
  </div>
</template>

<script>
import merge from '@/utils/merge'
import { isArray } from '@/utils/types'

const DefaultCrmRelativeTable = {
  // 表头
  fieldList: null,
  request: null,
  params: null,
  placeholder: '', // 搜索placeholder
  isPaging: true, // 是否分页
  showHeader: true, //
  tableFormatter: null,
  rowKey: '' // ElTable的rowKey
}

export default {
  // 通用table
  name: 'CrmRelativeTable',

  components: {},

  props: {
    props: Object
  },

  data() {
    return {
      search: '',
      copySearch: '',
      tableHeight: document.documentElement.clientHeight - 290,
      loading: false, // 加载动画
      list: [],
      fieldList: [],
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      selectionList: [],
      ignoreSelectedChange: false
    }
  },

  computed: {
    config() {
      return merge({ ...DefaultCrmRelativeTable }, this.props || {})
    },

    reduceHeaderHeight() {
      return this.config.showHeader ? 0 : 32
    }
  },

  watch: {
    'config.params': {
      handler() {
        this.getList()
      }
    }
  },

  created() {
    if (this.config.fieldList) {
      this.fieldList = this.config.fieldList
    }
    this.getList()
  },

  mounted() {
    this.updateTableHeight()
    window.onresize = () => {
      this.updateTableHeight()
    }
  },

  beforeDestroy() {},

  methods: {
    /**
     * 进行搜索操作
     */
    searchInput() {
      this.copySearch = this.search
      this.handleCurrentChange(1)
    },

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
     * 获取列表
     */
    getList() {
      const params = this.config.params || {}
      if (this.config.isPaging) {
        params.page = this.currentPage
        params.limit = this.pageSize
      }
      this.config.request(params).then(res => {
        const resData = res.data
        if (isArray(resData)) {
          this.total = resData.length
          this.list = resData
        } else {
          this.list = resData.list
          this.total = resData.totalRow
        }
      }).catch(() => {

      })
    },

    /**
     * 更新表高
     */
    updateTableHeight() {
      this.tableHeight = document.documentElement.clientHeight - 290
    },

    /**
     * 获取table
     */
    getMainTable() {
      let table = null
      this.$children.forEach(item => {
        if (item.$options && item.$options.name === 'ElTable') {
          table = item
        }
      })
      return table
    },

    /**
     * 勾选操作
     * @param {*} val
     */
    handleSelectionChange(val) {
      if (this.ignoreSelectedChange) {
        return
      }

      // 单选操作
      if (this.config.radio && val.length > 1) {
        const mainTable = this.getMainTable()
        const lastObj = val[val.length - 1]
        this.ignoreSelectedChange = true
        mainTable.clearSelection()
        this.$nextTick(() => {
          this.ignoreSelectedChange = false
          mainTable.toggleRowSelection(lastObj)
        })
        return
      } else {
        this.selectionList = val
      }
      this.$emit('selection-change', val)
    }
  }
}
</script>

<style lang="scss" scoped>
.crm-relative-table {
  .header {
    ::v-deep .el-input {
      width: 300px;
    }

    ::v-deep .el-input-group__append {
      color: white;
      background-color: $--color-primary;
      border-color: $--color-primary;
    }
  }

  .body {
    padding: 0 40px 16px;
  }
}
</style>
