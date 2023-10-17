<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-27 16:08:02
 * @LastEditTime: 2020-07-07 11:02:28
 * @LastEditors: yang
-->
<template>
  <div class="relative-employee">
    <div class="relative-employee__header">
      <el-input
        v-model="search"
        placeholder="请输入员工姓名"
        style="width: 300px;"
        @input="debouncedGetList">
        <i slot="prepend" class="el-icon-search" />
      </el-input>
    </div>
    <el-table
      :data="list"
      :height="tableHeight"
      :cell-class-name="cellClassName"
      class="el-table-header--white"
      stripe
      @row-click="handleRowClick">
      <el-table-column
        v-for="(item, index) in fieldList"
        :key="index"
        :prop="item.prop"
        :label="item.label"
        :formatter="fieldFormatter"
        show-overflow-tooltip />
    </el-table>
    <el-pagination
      :page-size="pageSize"
      :current-page="currentPage"
      :total="pageTotal"
      style="text-align: right;"
      layout="prev, pager, next"
      @current-change="handleCurrentChange" />
  </div>
</template>

<script type="text/javascript">
import {
  hrmDeptQueryEmployeeAPI
} from '@/api/hrm/dept'

import { debounce } from 'throttle-debounce'
import { employeeModel } from '../../employee/model/employee'

export default {
  // 组织 相关员工
  name: 'RelativeEmployee',
  components: {
  },
  props: {
    id: [String, Number]
  },
  data() {
    return {
      list: [],
      search: '',
      currentPage: 1,
      pageSize: 15,
      pageTotal: 0,
      fieldList: [
        { prop: 'employeeName', width: '200', label: '姓名' },
        { prop: 'jobNumber', width: '200', label: '工号' },
        { prop: 'deptName', width: '200', label: '部门' },
        { prop: 'post', width: '200', label: '岗位' },
        { prop: 'employmentForms', width: '200', label: '聘用形式' },
        { prop: 'entryTime', width: '200', label: '入职时间' }
      ],
      tableHeight: '400px'
    }
  },
  computed: {},
  watch: {
    id: function(val) {
      this.list = []
      this.getList()
    }
  },
  created() {
    this.debouncedGetList = debounce(300, this.refreshList)
    this.refreshList()
  },
  activated: function() {},
  deactivated: function() {},
  methods: {
    refreshList() {
      this.handleCurrentChange(1)
    },

    handleCurrentChange(val) {
      this.currentPage = val
      this.getList()
    },

    /**
     * 获取数据
     */
    getList() {
      this.loading = true
      hrmDeptQueryEmployeeAPI({
        deptId: this.id,
        search: this.search,
        page: this.currentPage,
        limit: this.pageSize
      })
        .then(res => {
          this.loading = false
          this.list = res.data.list || []
          this.pageTotal = res.data.totalRow
        })
        .catch(data => {
          this.loading = false
        })
    },

    /**
     * 格式化字段
     */
    fieldFormatter(row, column) {
      const valueObj = employeeModel[`${column.property}Value`]
      if (valueObj) {
        return valueObj[row[column.property]] || '--'
      }
      return row[column.property] || '--'
    },

    /**
     * 列表点击 详情
     */
    handleRowClick(row, column, event) {
      console.log(row)
    },

    /**
     * 通过回调控制class
     */
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (column.property === 'name') {
        return 'can-visit--underline'
      } else {
        return ''
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.relative-employee {
  padding: 16px 0;

  &__header {
    margin-bottom: 10px;
  }
}

.el-pagination {
  margin-top: 10px;
  text-align: right;
}
</style>
