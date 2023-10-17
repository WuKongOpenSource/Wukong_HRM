<template>
  <div id="employee-main-container" class="salary-index">
    <xr-header
      ft-top="0"
      label="薪资管理">
      <template slot="label">
        薪资管理
        <span v-if="startEndTimeValue" class="time-label">（{{ startEndTimeValue }}）</span>
      </template>
      <template slot="ft">
        <el-button
          type="text"
          @click="checkClick('progress')">查看审核进度</el-button>
      </template>
    </xr-header>
    <div class="crm-container">
      <xr-table-header>
        <el-tabs v-model="tabType" style="width: 100%;margin-bottom: -8px;" @tab-click="tabClick">
          <el-tab-pane
            v-for="tab in tabList"
            :key="tab.name"
            :name="tab.name">
            <span slot="label">{{ tab.label }}<span v-if="tab.num > 0" style="margin-left: 5px;" class="tab-pane-num">{{ tab.num }}</span></span>
          </el-tab-pane>
        </el-tabs>
      </xr-table-header>
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
          show-overflow-tooltip />
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
      :detail="examineData"
      :visible.sync="examineProDialogShow"
      @change="examineProgressChange"
      @success="getExamineData()"
    />
  </div>
</template>

<script>
import {
  useExamineEmailTokenForSalaryPageLis
} from '@/api/hrm/salary'
import {
  examineSuperExaminesUseExamineEmailTokenForExamineRecordListAPI
} from '@/api/examine/superExamine'

import XrHeader from '@/components/XrHeader'
import XrTableHeader from '@/components/XrTableHeader'
import ExamineProgressDialog from './ExamineProgressDialog'

import { isEmpty } from '@/utils/types'
import moment from 'moment'

export default {
  name: 'SalaryIndex',
  components: {
    XrHeader,
    XrTableHeader,
    ExamineProgressDialog
  },
  props: {
    emailToken: String
  },
  data() {
    return {
      loading: false, // 加载动画
      noNum: 0,
      tableHeight: 400, // 表的高度
      list: [],
      fieldList: [],
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      // 筛选宽
      popoverFilterWidth: document.documentElement.clientWidth - 80,
      tabType: '0',
      tabList: [{
        label: '计薪人数',
        name: '0',
        num: 0
      }, {
        label: '新入职',
        name: '1',
        num: 0
      }, {
        label: '离职',
        name: '2',
        num: 0
      }, {
        label: '转正',
        name: '3',
        num: 0
      }, {
        label: '调岗',
        name: '4',
        num: 0
      }],
      /** 控制详情展示 */
      // 行信息
      rowID: '',
      showDview: false,
      // 最新薪资的信息
      lastData: null,
      // 编辑数据
      editViewShow: false,
      // 总合计
      sumData: null,
      // 审批提交
      examineProDialogShow: false,
      examineData: null
    }
  },
  computed: {
    startEndTimeValue() {
      if (this.lastData) {
        return `计薪周期：${moment(this.lastData.startTime).format('MM月DD日')}-${moment(this.lastData.endTime).format('MM月DD日')}`
      }
      return ''
    }
  },
  watch: {},
  created() {
    this.getTableHeight()
    window.onresize = () => {
      this.getTableHeight()
    }

    this.getList()
    this.getExamineData()
  },
  methods: {
    /**
     * 获取table高
     */
    getTableHeight() {
      this.tableHeight = document.documentElement.clientHeight - 205 - 70
    },

    /**
     * 查看审批
     */
    checkClick(type) {
      this.examineProDialogShow = true
    },

    /**
     * 进行了审批
     */
    examineProgressChange() {
      this.getExamineData()
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
        type: this.tabType,
        emailToken: this.emailToken
      }
      useExamineEmailTokenForSalaryPageLis(params)
        .then(res => {
          const { salaryMonthRecord, salaryOptionHeadVOList, salaryPageListVO, employeeChangeNum } = res.data

          this.lastData = salaryMonthRecord || {}

          this.getTabNum(employeeChangeNum)

          // 设置表头
          const defaultTables = [
            { field: 'employeeName', name: '姓名', width: 100 },
            { field: 'jobNumber', name: '工号', width: 100 },
            { field: 'deptName', name: '部门', width: 100 },
            { field: 'post', name: '岗位', width: 100 }]

          const otherTables = salaryOptionHeadVOList.map(item => {
            return {
              field: item.code.toString(),
              name: item.name,
              isFixed: item.isFixed === 1,
              width: 100
            }
          })

          // 设置table数据
          const list = salaryPageListVO.list
          list.forEach(item => {
            const salary = item.salary || []
            salary.forEach(sItem => {
              item[sItem.code] = sItem.value
            })
          })

          this.list = list
          this.total = salaryPageListVO.totalRow

          const extraData = salaryPageListVO.extraData || {}
          const salaryOption = extraData.salaryOption

          const sumData = {}
          salaryOption.forEach(sItem => {
            sumData[sItem.code] = sItem.value
          })
          this.sumData = sumData

          this.fieldList = defaultTables.concat(otherTables)

          this.loading = false
        })
    },

    /**
     * 获取审批数据
     */
    getExamineData() {
      examineSuperExaminesUseExamineEmailTokenForExamineRecordListAPI({ emailToken: this.emailToken })
        .then((res) => {
          this.examineData = res.data
        })
    },

    /**
     * tab 数量
     */
    getTabNum(data) {
      this.tabList.forEach(item => {
        if (item.name != 'all') {
          item.num = data[parseInt(item.name)] || 0
        }
      })
    },

    tabClick() {
      this.handleCurrentChange(1)
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
      const value = row[column.property]
      return isEmpty(value) ? '--' : value
    }
  }
}
</script>

<style lang="scss" scoped>
.salary-index {
  padding: 24px 40px 0;

  .xr-header {
    height: 34px;
    margin-bottom: 20px;

    ::v-deep .xr-header__ft {
      top: 0;
      line-height: 34px;
    }
  }

  ::v-deep.reminder-wrapper {
    margin-bottom: 8px;

    .reminder-body {
      align-items: center !important;
    }
  }

  .time-label {
    font-size: 14px;
    font-weight: normal;
    color: $--color-text-secondary;
  }

  ::v-deep.xr-table-header {
    border-bottom: 1px solid $--border-color-base;

    .xr-table-header__body {
      padding: 0;
    }
  }

  ::v-deep .el-tabs {
    .el-tabs__header {
      margin-bottom: 0;
    }

    .el-tabs__nav-wrap::after {
      display: none;
    }

    .el-tabs__item {
      font-weight: bold;
      color: $--color-text-regular;
    }
  }

  .tab-pane-num {
    display: inline-block;
    height: 18px;
    padding: 0 6px;
    font-size: 12px;
    font-weight: bold;
    line-height: 18px;
    color: $--color-text-regular;
    text-align: center;
    white-space: nowrap;
    background-color: #dfe1e6;
    border: 1px solid #fff;
    border-radius: 10px;
  }
}
</style>
