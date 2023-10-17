<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <div id="employee-main-container" class="performance-self">
    <xr-header
      :placeholder="searchPlaceholder"
      :show-search="searchShow"
      :content.sync="search"
      style="margin-bottom: 16px;"
      ft-top="0"
      @search="searchClick">
      <template slot="label">
        <el-tabs v-model="tabType" @tab-click="tabClick">
          <el-tab-pane
            v-for="tab in tabList"
            :key="tab.name"
            :name="tab.name">
            <span slot="label">{{ tab.label }}<span v-if="tab.num > 0" style="margin-left: 5px;" class="tab-pane-num">{{ tab.num }}</span></span>
          </el-tab-pane>
        </el-tabs>
        <!-- <el-button v-if="hasFilterContent" type="text" @click="resetFilter">清除筛选</el-button>
        <wk-popover-filter
          :width="popoverFilterWidth"
          :field-from.sync="filterObj"
          :field-list="filterList"
          :has-content="hasFilterContent"
          style="margin-right: 5px;"
          placement="bottom"
          @sure="refreshList"
          @reset="resetFilter"
        /> -->
      </template>
      <el-select
        v-if="appraisaSelectShow"
        slot="ft"
        v-model="appraisalId"

        @change="handleCurrentChange(1)">
        <el-option
          v-for="item in appraisaOptions"
          :key="item.value"
          :label="item.appraisalName"
          :value="item.appraisalId" />
      </el-select>
    </xr-header>
    <div class="crm-container">
      <xr-table-header v-if="subTabShow" style="margin-top: -20px;">
        <el-tabs v-model="subTabType" style="width: 100%;margin-bottom: -8px;" @tab-click="subTabClick">
          <el-tab-pane
            v-for="tab in subTabList"
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
        :cell-class-name="cellClassName"
        :class="WKConfig.tableStyle.class"
        :stripe="WKConfig.tableStyle.stripe"
        use-virtual
        class="n-table--border el-table-header--white"
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
          :formatter="fieldFormatter"
          show-overflow-tooltip />
        <el-table-column />
        <el-table-column
          v-if="tabType != 'result'"
          label="操作"
          fixed="right"
          width="100">
          <template slot-scope="scope">
            <el-button style="padding: 0;" type="text" @click="handleClick(scope.row, scope.$index)">{{ getRowButtonName(scope.row) }}</el-button>
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
    </div>

    <employee-edit-performance
      v-if="detailShow"
      :id="rowId"
      :handle-type="editHandleType"
      @change="refreshList"
      @close="detailShow = false"
    />

    <employee-confirm-performance
      v-if="confirmDetailShow"
      :id="rowId"
      :handle-type="confirmHandleType"
      :next-list="list"
      :next-index="rowIndex"
      @nextChange="nextChange"
      @change="refreshList"
      @close="confirmDetailShow = false"
    />

    <confirm-result-view
      v-if="resultConfirmShow"
      :id="rowId"
      @change="refreshList"
      @close="resultConfirmShow = false"
    />
  </div>
</template>

<script>
import {
  hrmPerformanceEmployeeMyListAPI,
  hrmPerformanceEmployeeResultListAPI,
  hrmPerformanceEmployeeTargetListAPI,
  hrmPerformanceEmployeeEvaluatoListAPI,
  hrmPerformanceEmployeeQueryNumAPI,
  hrmPEQueryTargetConfirmScreenAPI,
  hrmPEQueryEvaluatoScreenAPI
} from '@/api/hrm/selfService/performance'

import XrHeader from '@/components/XrHeader'
import XrTableHeader from '@/components/XrTableHeader'
// import WkPopoverFilter from '@/components/NewCom/WkPopoverFilter'

import { mapGetters } from 'vuex'
import { isEmpty } from '@/utils/types'
import performanceModel from './model/performance'
import EmployeeEditPerformance from './components/EmployeeEditPerformance'
import EmployeeConfirmPerformance from './components/EmployeeConfirmPerformance'
import ConfirmResultView from './components/ConfirmResultView'

export default {
  name: 'PerformanceSelf',
  components: {
    XrHeader,
    XrTableHeader,
    // WkPopoverFilter,
    EmployeeEditPerformance,
    EmployeeConfirmPerformance,
    ConfirmResultView
  },
  beforeRouteUpdate(to, from, next) {
    this.handleRouterData(to)
    next()
  },
  data() {
    return {
      loading: false, // 加载动画
      tableHeight: document.documentElement.clientHeight - 255, // 表的高度
      list: [],
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      search: '', // 搜索内容
      // 筛选宽
      popoverFilterWidth: document.documentElement.clientWidth - 80,
      tabType: 'my',
      tabList: [{
        label: '我的绩效',
        key: 1,
        name: 'my',
        num: 0
      }, {
        label: '目标待确认',
        key: 2,
        name: 'target',
        num: 0
      }, {
        label: '结果评定',
        key: 3,
        name: 'evaluato',
        num: 0
      }, {
        label: '结果待确认',
        key: 4,
        name: 'result',
        num: 0
      }],
      mySubTabList: [{
        label: '待评定',
        name: '0',
        key: 5,
        num: 0
      }, {
        label: '已评定',
        name: '1',
        key: 6,
        num: 0
      }],
      evaluatoSubTabList: [{
        label: '待填写',
        name: '0',
        key: 7,
        num: 0
      }, {
        label: '已填写',
        name: '1',
        key: 8,
        num: 0
      }],
      // 结果评定/我的 子tab
      subTabType: '0',
      // 控制详情展示
      rowId: '', // 行信息
      rowIndex: '', // 行索引
      editHandleType: '',
      detailShow: false,
      confirmHandleType: '',
      // 目标确定 和 评定 评定预览
      confirmDetailShow: false,
      // 结果确定视图控制
      resultConfirmShow: false,
      // 高级筛选
      filterObj: {}, // 筛选确定数据
      filterList: [],
      // 考核筛选
      appraisalId: '',
      appraisaOptions: []
    }
  },
  computed: {
    ...mapGetters([
    ]),
    // 操作
    searchShow() {
      return this.tabType != 'my'
    },
    // evaluato 结果评定 my 我的绩效
    subTabShow() {
      return this.tabType == 'evaluato' || this.tabType == 'my'
    },
    subTabList() {
      return this.tabType == 'evaluato' ? this.mySubTabList : this.evaluatoSubTabList
    },
    searchPlaceholder() {
      if (this.tabType == 'result') {
        return '请输入考核名称'
      }
      return '请输入姓名/手机号/考核名称'
    },

    // 考核筛选展示
    appraisaSelectShow() {
      return ['target', 'evaluato'].includes(this.tabType)
    },

    // 表头
    fieldList() {
      return this.getTableFileList()
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
  watch: {},
  created() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - (this.subTabShow ? 245 : 215)
    }
    this.handleRouterData(this.$route)
  },
  methods: {
    /**
     * 处理路由时间
     */
    handleRouterData(to) {
      const query = to.query
      if (query && query.tabType) {
        this.tabType = query.tabType
        this.subTabType = query.subTabType

        if (query.id && this.subTabType == 1) { // 已填写
          this.rowId = query.id
          // 预览结果
          this.confirmHandleType = 'evaluato-view'
          this.confirmDetailShow = true
        }
      }

      this.refreshList()
    },

    /**
     * 获取tab 数量 1 我的绩效 2目标确认 3 结果评定 4结果确认
     */
    getTabsNum() {
      hrmPerformanceEmployeeQueryNumAPI()
        .then(res => {
          const data = res.data || {}
          this.tabList.forEach((item, index) => {
            item.num = data[item.key] || 0
          })

          // 结果评定/我的 子项数量
          this.mySubTabList.forEach((item, index) => {
            item.num = data[item.key] || 0
          })

          this.evaluatoSubTabList.forEach((item, index) => {
            item.num = data[item.key] || 0
          })
        })
        .catch(() => {
        })
    },

    /**
     * 高级筛选
     */
    resetFilter() {
      this.filterObj = {}
      this.refreshList()
    },

    /**
     * 创建成功
     */
    tabClick() {
      this.$nextTick(() => {
        this.tableHeight = document.documentElement.clientHeight - (this.subTabShow ? 245 : 215)
        console.log('subTabShow---', this.subTabShow, this.tableHeight)
      })

      // 大类tab切换，重置小类tab值
      this.subTabType = '0'
      this.search = ''
      this.list = []

      if (this.appraisaSelectShow) {
        this.getPerformanceList()
      }

      this.refreshList()
    },

    /**
     * 评定tab切换
     */
    subTabClick() {
      if (this.appraisaSelectShow) {
        this.getPerformanceList()
      }

      this.refreshList()
    },

    /**
     * 获取有效考核
     */
    getPerformanceList() {
      const request = {
        target: hrmPEQueryTargetConfirmScreenAPI,
        evaluato: hrmPEQueryEvaluatoScreenAPI
      }[this.tabType]
      this.appraisaOptions = []
      this.appraisalId = ''
      if (request) {
        const params = {}
        if (this.subTabShow) {
          params.status = this.subTabType
        }
        request(params).then(res => {
          const resData = res.data || []
          this.appraisaOptions = [{
            appraisalId: '',
            appraisalName: '全部考核'
          }].concat(resData)
        }).catch(() => {

        })
      }
    },

    /**
     * 搜索
     */
    searchClick(search) {
      this.search = search
      this.refreshList()
    },

    /**
     * 刷新
     */
    refreshList() {
      this.getTabsNum()
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
     * 列表数据
     */
    getList() {
      this.loading = true
      const params = {
        page: this.currentPage,
        limit: this.pageSize
      }

      if (this.searchShow) {
        params.search = this.search
      }

      if (this.subTabShow) {
        params.status = this.subTabType
      }

      if (this.appraisalId) {
        params.appraisalId = this.appraisalId
      }

      // for (var key in this.filterObj) {
      //   params[key] = this.filterObj[key]
      // }

      const request = {
        my: hrmPerformanceEmployeeMyListAPI,
        target: hrmPerformanceEmployeeTargetListAPI,
        evaluato: hrmPerformanceEmployeeEvaluatoListAPI,
        result: hrmPerformanceEmployeeResultListAPI
      }[this.tabType]
      request(params)
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
     * 获取表头
     */
    getTableFileList() {
      if (this.tabType == 'my') {
        return [{
          fieldName: 'appraisalName',
          name: '考核名称',
          width: 80
        }, {
          fieldName: 'appraisalTime',
          name: '考核时间',
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
          width: 80
        }]
      } else if (this.tabType == 'target' || this.tabType == 'evaluato') {
        const tempList = [{
          fieldName: 'employeeName',
          name: '姓名',
          width: 80
        }, {
          fieldName: 'mobile',
          name: '手机号',
          width: 80
        }, {
          fieldName: 'deptName',
          name: '部门',
          width: 80
        }, {
          fieldName: 'appraisalName',
          name: '考核名称',
          width: 80
        }, {
          fieldName: 'status', // 0 待确认 1 已确认  0 待评定 1 已评定
          name: '考核状态',
          width: 80
        }]
        return tempList
      } else if (this.tabType == 'result') {
        return [{
          fieldName: 'appraisalName',
          name: '考核名称',
          width: 80
        }, {
          fieldName: 'appraisalTime',
          name: '考核时间',
          width: 80
        }, {
          fieldName: 'totalNum',
          name: '待确认/总人数',
          width: 80
        }, {
          fieldName: 'scoreLevels',
          name: '等级分布',
          width: 80
        }]
      }
      return []
    },

    /**
     * 格式化字段
     */
    fieldFormatter(row, column) {
      if (this.tabType == 'target') {
        if (column.property == 'status') {
          return '待确认'
        } else {
          const value = row[column.property]
          return isEmpty(value) ? '--' : value
        }
      } else if (this.tabType == 'evaluato') {
        if (column.property == 'status') {
          return {
            1: '已评定',
            0: '待评定'
          }[row[column.property] ] || '--'
        } else {
          const value = row[column.property]
          return isEmpty(value) ? '--' : value
        }
      } else if (this.tabType == 'result') {
        if (column.property == 'totalNum') {
          return `${row.waitingNum}/${row.totalNum}`
        } else if (column.property == 'scoreLevels') {
          return row.scoreLevels ? row.scoreLevels.map(item => `${item.levelName} ${item.num}人`).join('，') : '--'
        } else {
          const value = row[column.property]
          return isEmpty(value) ? '--' : value
        }
      } else if (this.tabType == 'my') {
        if (column.property == 'status') {
          return performanceModel.statusValue[row.status] || '--'
        } else {
          const value = row[column.property]
          return isEmpty(value) ? '--' : value
        }
      }

      const value = row[column.property]
      return isEmpty(value) ? '--' : value
    },

    /**
     * 通过回调控制class
     */
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (this.tabType === 'result' && column.property === 'appraisalName') {
        return 'can-visit--underline'
      } else {
        return ''
      }
    },
    /**
     * 行信息
     */
    getRowButtonName(data) {
      // 目标待填写 是草稿，展示编辑草稿
      if (data.isDraft === 1 && data.status === 1) {
        return '编辑草稿'
      }
      if (this.tabType == 'my') {
        return data.status == 1 ? '填写' : '查看详情'
      } else if (this.tabType == 'target') {
        return '确认目标'
      } else if (this.tabType == 'evaluato') {
        return data.status == 0 ? '评定' : '查看详情'
      }
      return ''
    },

    /**
     * 列表点击
     */
    handleClick(row, index) {
      this.rowId = row.employeeAppraisalId
      this.rowIndex = index
      if (this.tabType == 'target') {
        this.confirmHandleType = 'confirm'
        this.confirmDetailShow = true
      } else if (this.tabType == 'evaluato') {
        this.confirmHandleType = row.status == 0 ? 'evaluato' : 'evaluato-view'
        this.confirmDetailShow = true
      } else {
        /**
         *  1: '目标待填写',
            2: '目标待确认',
            3: '结果待评定',
            4: '结果待确认',
            5: '考核终止',
            6: '考核完成'
         */
        // 1 2 3 可编辑
        if (row.status == 1 || row.status == 2) {
          this.editHandleType = ''
          this.detailShow = true
        } else if (row.status == 3) {
          this.editHandleType = 'schedule'
          this.detailShow = true
        } else {
          // 预览结果
          this.confirmHandleType = 'evaluato-view'
          this.confirmDetailShow = true
        }
      }
    },

    /**
     * 待确认/待评定 点击下一个
     */
    nextChange(index) {
      this.rowId = this.list[index].employeeAppraisalId
      this.rowIndex = index
    },

    handleRowClick(row, column) {
      if (this.tabType == 'result' && column.property == 'appraisalName') {
        this.rowId = row.appraisalId
        this.resultConfirmShow = true
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.performance-self {
  .xr-header {
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

  .search-button {
    border: none;

    &.is-select {
      color: white;
      background: $--color-primary;
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

  ::v-deep .el-table__fixed-body-wrapper {
    td:first-child .cell {
      padding-left: 10px !important;
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

  .table-more {
    cursor: pointer;

    &:hover {
      color: $--color-primary;
    }
  }
}
</style>
