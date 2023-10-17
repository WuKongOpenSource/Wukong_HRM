<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <el-dialog
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    title="详情"
    top="10vh"
    width="80%"
    @close="handleCancel">
    <div class="filter-div">
      <el-button v-if="hasFilterContent" type="text" @click="resetFilter">清除筛选</el-button>
      <wk-popover-filter
        :field-from.sync="filterObj"
        :field-list="filterList"
        :has-content="hasFilterContent"
        width="80%"
        style="margin-right: 5px;"
        placement="bottom"
        @sure="refreshList"
        @reset="resetFilter"
      />
    </div>
    <el-table
      id="crm-table"
      v-loading="loading"
      :row-height="40"
      :data="list"
      :height="tableHeight"
      use-virtual
      class="n-table--border el-table-header--white"
      stripe
      highlight-current-row
      style="width: 100%;"
      @row-click="handleRowClick">
      <el-table-column
        prop="index"
        label="序号"
        width="80"
        show-overflow-tooltip>
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column
        v-for="(item, index) in fieldList"
        :key="index"
        :prop="item.fieldName"
        :label="item.name"
        :min-width="item.width"
        show-overflow-tooltip>
        <template slot-scope="scope">
          <template v-if="scope.column.property === 'employeeName'">
            <span class="can-visit--underline">{{ fieldFormatter(scope.row, scope.column) }}</span><span v-if="scope.row.isDel == 1" style="color: #6b778c;">（已删除）</span>
          </template>
          <template v-else>
            {{ fieldFormatter(scope.row, scope.column) }}
          </template>
        </template>
      </el-table-column>
    </el-table>
    <div class="p-contianer">
      <el-pagination
        :current-page="currentPage"
        :page-sizes="pageSizes"
        :page-size.sync="pageSize"
        :total="total"
        class="p-bar"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
    </div>
    <employee-confirm-performance
      v-if="detailShow"
      :id="rowId"
      handle-type="evaluato-view"
      @close="detailShow = false"
    />
  </el-dialog>
</template>

<script>
import {
  hrmPerformanceQueryEmployeeByAppraisalIdAPI
} from '@/api/hrm/performance'
import {
  hrmPerformanceEmployeeQueryLevelByAppraisalIdAPI
} from '@/api/hrm/selfService/performance'
import {
  hrmDeptQueryTreeListAPI
} from '@/api/hrm/dept'

import WkPopoverFilter from '@/components/NewCom/WkPopoverFilter'
import EmployeeConfirmPerformance from '../../../selfService/performance/components/EmployeeConfirmPerformance'

import { isEmpty } from '@/utils/types'
import { employeeModel } from '../../../employee/model/employee'
import performanceModel from '../../../selfService/performance/model/performance'

export default {
  // 绩效内员工
  name: 'EmployeesView',
  components: {
    WkPopoverFilter,
    EmployeeConfirmPerformance
  },
  mixins: [],
  props: {
    status: [String, Number],
    id: [String, Number],
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
      loading: false, // 加载动画
      // 高级筛选
      filterObj: {}, // 筛选确定数据
      tableHeight: this.getTableHeight(), // 表的高度
      list: [],
      fieldList: [{
        fieldName: 'employeeName',
        name: '姓名',
        width: 130
      }, {
        fieldName: 'mobile',
        name: '手机号',
        width: 80
      }, {
        fieldName: 'jobNumber',
        name: '工号',
        width: 80
      }, {
        fieldName: 'deptName',
        name: '部门',
        width: 80
      }, {
        fieldName: 'employeeStatus',
        name: '员工状态',
        width: 80
      }, {
        fieldName: 'status',
        name: '考核状态',
        width: 80
      }, {
        fieldName: 'followUpEmployeeName',
        name: '跟进人',
        width: 80
      }, {
        fieldName: 'score',
        name: '评分',
        width: 80
      }, {
        fieldName: 'levelName',
        name: '考核结果',
        width: 120
      }],
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      // 查看详情
      rowId: '',
      detailShow: false,
      // 等级数据
      levelOptions: []
    }
  },
  computed: {
    filterList() {
      return [
        {
          name: '姓名',
          field: 'employeeName',
          formType: 'text',
          setting: []
        }, {
          name: '部门',
          field: 'deptId',
          formType: 'structure',
          props: {
            dataType: 'hrm'
          },
          request: hrmDeptQueryTreeListAPI,
          setting: []
        },
        {
          name: '等级',
          field: 'levelId',
          formType: 'select',
          setting: this.levelOptions
        }, {
          name: '考核状态',
          field: 'status',
          formType: 'select',
          setting: performanceModel.getValueList(performanceModel.statusValue)
        }]
    },

    // 有筛选内容
    hasFilterContent() {
      if (this.filterObj) {
        let hasContent = false
        const keys = Object.keys(this.filterObj)
        for (let index = 0; index < keys.length; index++) {
          const key = keys[index]
          if (this.filterObj[key] != '' &&
           this.filterObj[key] != [] &&
           this.filterObj[key] != null &&
           this.filterObj[key] != undefined) {
            hasContent = true
          }
        }
        return hasContent
      }
      return false
    }
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.filterObj = {
            status: isEmpty(this.status) || isNaN(this.status) ? '' : parseInt(this.status)
          }
          // 每次获取对应计划id
          this.getLevelList()
          this.refreshList()
        }
      },
      immediate: true
    }
  },
  mounted() {},
  methods: {
    /**
     * 刷新
     */
    refreshList() {
      this.handleCurrentChange(1)
    },

    /**
     * 高级筛选
     */
    resetFilter() {
      this.filterObj = {}
      this.refreshList()
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
     * 列表数据
     */
    getList() {
      this.loading = true
      const params = {
        page: this.currentPage,
        limit: this.pageSize,
        status: this.status,
        appraisalId: this.id
      }

      for (var key in this.filterObj) {
        params[key] = this.filterObj[key]
      }

      hrmPerformanceQueryEmployeeByAppraisalIdAPI(params)
        .then(res => {
          this.list = res.data.list
          this.total = res.data.totalRow
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 获取表高
     */
    getTableHeight() {
      const clientHeight = document.documentElement.clientHeight
      const paddingHieght = clientHeight * 0.2

      return clientHeight - paddingHieght - 240
    },

    /**
     * 格式化字段
     */
    fieldFormatter(row, column) {
      if (column.property == 'employeeStatus') {
        return employeeModel.statusValue[row.employeeStatus] || '--'
      } else if (column.property == 'status') {
        return performanceModel.statusValue[row.status] || '--'
      } else {
        const value = row[column.property]
        return isEmpty(value) ? '--' : value
      }
    },

    // /**
    //  * 通过回调控制class
    //  */
    // cellClassName({ row, column, rowIndex, columnIndex }) {
    //   if (column.property == 'employeeName') {
    //     return 'can-visit--underline'
    //   } else {
    //     return ''
    //   }
    // },

    /**
     * 列表点击
     */
    handleRowClick(row, column, event) {
      if (column.property == 'employeeName') {
        this.rowId = row.employeeAppraisalId
        this.detailShow = true
      }
    },

    /**
     * 取消选择
     */
    handleCancel() {
      this.detailShow = false
      this.$emit('update:visible', false)
    },

    /**
     * 获取层级信息
     */
    getLevelList() {
      hrmPerformanceEmployeeQueryLevelByAppraisalIdAPI(this.id)
        .then(res => {
          const levelOptions = res.data || []
          levelOptions.forEach(item => {
            item.label = item.levelName
            item.value = item.levelId
          })
          this.levelOptions = levelOptions
        })
        .catch(() => {
        })
    }
  }
}
</script>
<style lang="scss" scoped>
.filter-div {
  position: absolute;
  top: 35px;
  right: 40px;
  height: 34px;
  line-height: 34px;
  text-align: right;
}
</style>
