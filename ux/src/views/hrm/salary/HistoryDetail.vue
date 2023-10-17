<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <div id="employee-main-container" class="scheme-index">
    <xr-header
      ft-top="0">
      <el-page-header slot="label" :content="topData.month ? `${topData.month}月薪资详情` : '薪资详情'" @back="goBack" />
      <template slot="ft">
        <el-button v-if="hasFilterContent" type="text" @click="resetFilter">清除筛选</el-button>
        <wk-popover-filter
          :width="popoverFilterWidth"
          :field-from.sync="filterObj"
          :field-list="filterList"
          :has-content="hasFilterContent"
          style="margin-right: 5px;"
          placement="bottom"
          @sure="refreshList"
          @reset="resetFilter"
        />
        <el-button
          v-if="examineShow"
          type="text"
          @click="checkClick">查看审核</el-button>
      </template>
    </xr-header>
    <div class="crm-container">
      <statistics-card
        :data="topData"
        :icon-show="false"
        :button-show="false"
        :field-list="topFields"
        title="合计"
        class="filter"
      />
      <el-table
        id="crm-table"
        v-loading="loading"
        :row-height="40"
        :data="list"
        :height="tableHeight"
        :summary-method="getSummaries"
        :class="WKConfig.tableStyle.class"
        :stripe="WKConfig.tableStyle.stripe"
        show-summary
        use-virtual
        class="n-table--border el-table-header--white"
        highlight-current-row
        style="width: 100%;">
        <el-table-column
          v-for="(item, index) in fieldList"
          :key="index"
          :fixed="index == 0"
          :prop="item.field"
          :label="item.name"
          :min-width="item.width"
          :formatter="fieldFormatter"
          show-overflow-tooltip>
          <template slot-scope="scope">
            <template v-if="scope.column.property === 'employeeName'">
              {{ fieldFormatter(scope.row, scope.column) }}<span v-if="scope.row.isDel == 1" style="color: #6b778c;">（已删除）</span>
            </template>
            <template v-else>
              {{ fieldFormatter(scope.row, scope.column) }}
            </template>
          </template>
        </el-table-column>
        <el-table-column />
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
    </div>

    <examine-progress-dialog
      :detail="topData"
      :ignore-action="['recovery']"
      :visible.sync="examineProDialogShow"
    />
  </div>
</template>

<script>
import {
  hrmSalaryHistoryRecordDetailAPI,
  hrmSalaryMonthRecordSumOptionAPI
} from '@/api/hrm/salary'
import {
  hrmDeptQueryTreeListAPI
} from '@/api/hrm/dept'

import XrHeader from '@/components/XrHeader'
import StatisticsCard from '../insuranceScheme/components/StatisticsCard'
import ExamineProgressDialog from './components/ExamineProgressDialog'
import WkPopoverFilter from '@/components/NewCom/WkPopoverFilter'

import { mapGetters } from 'vuex'

export default {
  name: 'HistoryDetail',
  components: {
    XrHeader,
    StatisticsCard,
    ExamineProgressDialog,
    WkPopoverFilter
  },
  data() {
    return {
      loading: false, // 加载动画
      tableHeight: document.documentElement.clientHeight - 340, // 表的高度
      list: [],
      fieldList: [],
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      // 筛选宽
      popoverFilterWidth: document.documentElement.clientWidth - 80,
      // 高级筛选
      filterObj: {}, // 筛选确定数据
      filterList: [
        {
          name: '员工姓名',
          field: 'employeeName',
          formType: 'text',
          setting: []
        }, {
          name: '工号',
          field: 'jobNumber',
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
        }],
      topFields: [{
        label: '本月计薪人数',
        prop: 'num'
      }, {
        label: '预计应发工资',
        prop: 'expectedPaySalary'
      }, {
        label: '个人社保',
        prop: 'personalInsuranceAmount'
      }, {
        label: '个人公积金',
        prop: 'personalProvidentFundAmount'
      }, {
        label: '个人所得税',
        prop: 'personalTax'
      }, {
        label: '预计实发工资',
        prop: 'realPaySalary'
      }, {
        label: '社保-企业',
        prop: 'corporateInsuranceAmount'
      }, {
        label: '公积金-企业',
        prop: 'corporateProvidentFundAmount'
      }],
      topData: {},
      // 总合计
      sumData: null,
      examineProDialogShow: false
    }
  },
  computed: {
    ...mapGetters([
    ]),

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
    },

    id() {
      return this.$route.params.id
    },

    // 是否展示审批
    examineShow() {
      return this.topData && this.topData.examineRecordId
    }
  },
  watch: {},
  created() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - 340
    }
    this.refreshList()
  },
  methods: {
    checkClick() {
      this.examineProDialogShow = true
    },

    goBack() {
      this.$router.go(-1)
    },

    /**
     * 刷新
     */
    refreshList() {
      this.getSumData()
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
     * 高级筛选
     */
    resetFilter() {
      this.filterObj = {}
      this.refreshList()
    },

    /**
     * 列表数据
     */
    getList() {
      this.loading = true
      const params = {
        page: this.currentPage,
        limit: this.pageSize,
        srecordId: this.id
      }

      for (var key in this.filterObj) {
        params[key] = this.filterObj[key]
      }

      hrmSalaryHistoryRecordDetailAPI(params)
        .then(res => {
          const data = res.data || {}
          this.topData = data

          const pageData = data.pageData || {}
          const list = pageData.list
          list.forEach(item => {
            const salary = item.salary || []
            salary.forEach(sItem => {
              item[sItem.code] = sItem.value
            })
          })
          this.list = list

          this.total = pageData.totalRow
          if (pageData.totalRow && Math.ceil(pageData.totalRow / this.pageSize) < this.currentPage && this.currentPage > 1) {
            this.currentPage = this.currentPage - 1
            this.getList()
          } else {
            this.total = pageData.totalRow
            this.loading = false
          }

          // 表头
          if (this.fieldList.length == 0) {
            const defaultTables = [
              { field: 'employeeName', name: '姓名', width: 150 },
              { field: 'jobNumber', name: '工号', width: 100 },
              { field: 'deptName', name: '部门', width: 100 },
              { field: 'post', name: '岗位', width: 100 }]

            const salaryOptionHeadList = data.salaryOptionHeadList || []

            const otherTables = salaryOptionHeadList.map(item => {
              return {
                field: item.code.toString(),
                name: item.name,
                width: 100
              }
            })
            this.fieldList = defaultTables.concat(otherTables)
          }

          this.$nextTick(() => {
            const warpDom = document.querySelector('.el-table__body-wrapper')
            warpDom.scrollTop = 1
            if (warpDom.scrollLeft != 0) {
              warpDom.scrollLeft = warpDom.scrollLeft - 1
            }
          })
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    getSumData() {
      hrmSalaryMonthRecordSumOptionAPI(this.id)
        .then(res => {
          const dataList = res.data || []
          const sumData = {}
          dataList.forEach(sItem => {
            sumData[sItem.code] = sItem.value
          })
          this.sumData = sumData
        })
        .catch(() => {
        })
    },

    getSummaries(param) {
      const { columns } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = '总合计'
        } else if (index <= 5) {
          sums[index] = ''
        } else {
          sums[index] = this.sumData ? this.sumData[column.property] : ''
        }
      })

      return sums
    },

    /**
     * 格式化字段
     */
    fieldFormatter(row, column) {
      return row[column.property] || '--'
    }
  }
}
</script>

<style lang="scss" scoped>
.scheme-index {
  .xr-header {
    height: 34px;
    margin-bottom: 20px;

    ::v-deep .xr-header__ft {
      top: 0;
      line-height: 34px;
    }
  }

  ::v-deep.xr-table-header {
    border-bottom: 1px solid $--border-color-base;

    .xr-table-header__body {
      padding: 0;
    }
  }

  ::v-deep .el-page-header {
    .el-page-header__content {
      font-size: 15px;
    }
  }

  .filter {
    margin-bottom: 15px;
  }
}
</style>
