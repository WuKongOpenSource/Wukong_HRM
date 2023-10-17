<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <div id="employee-main-container" class="scheme-index">
    <xr-header
      ft-top="0">
      <el-page-header slot="label" @back="goBack">
        <template slot="content">
          {{ topData.month ? `${topData.month}月社保详情` : '社保详情' }}
          <el-tooltip
            v-if="hrmUserType === 2"
            effect="dark"
            placement="top"
            style="margin-left: 8px;"
            content="仅展示您管理范围内的员工">
            <i class="wk wk-help wk-help-tips" />
          </el-tooltip>
        </template>
      </el-page-header>
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
        <el-button v-if="hasEditAuth" icon="el-icon-plus" type="primary" @click="addEmpClick">添加参保人员</el-button>
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
        @select="topCardSelect"
      />
      <xr-table-header
        v-if="selectionList.length > 0"
        :handles="tabelHandles"
        :selects="selectionList"
        @command="handleCommand" />
      <el-table
        id="crm-table"
        v-loading="loading"
        :row-height="40"
        :data="list"
        :height="tableHeight"
        :class="WKConfig.tableStyle.class"
        :stripe="WKConfig.tableStyle.stripe"
        use-virtual
        class="n-table--border el-table-header--white"
        highlight-current-row
        style="width: 100%;"
        @row-click="handleRowClick"
        @selection-change="handleSelectionChange">
        <el-table-column
          v-if="hasEditAuth"
          show-overflow-tooltip
          fixed
          type="selection"
          align="center"
          width="55" />
        <el-table-column
          v-for="(item, index) in fieldList"
          :key="index"
          :fixed="index == 0"
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

    <employ-insurance-scheme-detail
      v-if="employDetailShow"
      :id="rowData.iempRecordId"
      :can-edit="hasEditAuth"
      :employee-id="rowData.employeeId"
      @change="refreshCurrentPageData"
      @close="employDetailShow = false"
    />

    <employ-scheme-edit-dialog
      :selection-list="selectionList"
      :visible.sync="schemeEditDialogShow"
      @change="refreshPageData"
    />

    <employ-add-dialog
      :id="id"
      :visible.sync="employAddDialogShow"
      @change="refreshPageData"
    />
  </div>
</template>

<script>
import {
  hrmInsuranceMonthRecordQueryDetailAPI,
  hrmInsuranceMonthRecordQueryListAPI,
  hrmInsuranceMonthEmpRecordStopAPI
} from '@/api/hrm/insuranceScheme'
import {
  hrmConfigInsuranceSchemListAPI
} from '@/api/admin/hrm'

import XrHeader from '@/components/XrHeader'
import XrTableHeader from '@/components/XrTableHeader'
import WkPopoverFilter from '@/components/NewCom/WkPopoverFilter'
import StatisticsCard from './components/StatisticsCard'
import EmployInsuranceSchemeDetail from './components/EmployInsuranceSchemeDetail'
import EmploySchemeEditDialog from './components/EmploySchemeEditDialog'
import EmployAddDialog from './components/EmployAddDialog'

import { mapGetters } from 'vuex'

export default {
  name: 'InsuranceSchemeDetail',
  components: {
    XrHeader,
    XrTableHeader,
    WkPopoverFilter,
    StatisticsCard,
    EmployInsuranceSchemeDetail,
    EmploySchemeEditDialog,
    EmployAddDialog
  },
  data() {
    return {
      loading: false, // 加载动画
      tableHeight: document.documentElement.clientHeight - 340, // 表的高度
      list: [],
      status: 1, // 0 停保 1 参保
      fieldList: [],
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      // 筛选宽
      popoverFilterWidth: document.documentElement.clientWidth - 80,
      // 高级筛选
      filterObj: {}, // 筛选确定数据
      schemeList: [],
      selectionList: [], // 勾选数据 用于全局导出
      topFields: [{
        label: '参保人数',
        prop: 'num',
        visit: true
      }, {
        label: '个人社保',
        prop: 'personalInsuranceAmount'
      }, {
        label: '公司社保',
        prop: 'corporateInsuranceAmount'
      }, {
        label: '个人公积金',
        prop: 'personalProvidentFundAmount'
      }, {
        label: '公司公积金',
        prop: 'corporateProvidentFundAmount'
      }, {
        label: '本月停保人数',
        prop: 'stopNum',
        visit: true
      }],
      topData: {},
      // 行详情
      rowData: {},
      employDetailShow: false,
      // 批量编辑社保
      schemeEditDialogShow: false,
      // 添加员工展示
      employAddDialogShow: false
    }
  },
  computed: {
    ...mapGetters([
      'hrm',
      'hrmUserType'
    ]),

    // 社保权限
    insuranceAuth() {
      return this.hrm.insurance
    },

    // 管理权限
    manageAuth() {
      return this.insuranceAuth && this.insuranceAuth.manage
    },

    id() {
      return this.$route.params.id
    },

    // 是否能编辑和停用
    hasEditAuth() {
      if (this.manageAuth && this.topData) {
        // 未归档的可编辑 状态 0
        return this.topData.status === 0
      }
      return false
    },

    // 操作
    tabelHandles() {
      const temps = []
      if (this.manageAuth) {
        temps.push({
          label: '参保方案',
          command: 'security',
          icon: 'wk wk-approval-9'
        }, {
          label: '停止参保',
          command: 'stop',
          icon: 'wk wk-remove'
        })
      }
      return temps
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
    },

    filterList() {
      return [
        {
          name: '员工姓名',
          field: 'employeeName',
          formType: 'text',
          setting: []
        }, {
          name: '参保方案',
          field: 'schemeId',
          formType: 'select',
          setting: this.schemeList
        }, {
          name: '参保城市',
          field: 'city',
          formType: 'text',
          setting: []
        }
      ]
    }
  },
  watch: {},
  created() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - (this.selectionList.length > 0 ? 390 : 340)
    }
    this.fieldList = this.getFieldList()
    this.refreshPageData()
    this.getSchemList()
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    },

    /**
     * 刷新整个页面 顶部 和 列表的数据
     */
    refreshPageData() {
      this.refreshList()
      this.getDetail()
    },

    /**
     * 刷新整个页面 顶部 和 当前分页列表的数据
     */
    refreshCurrentPageData() {
      this.getList()
      this.getDetail()
    },

    /**
     * 获取参保方案
     */
    getSchemList() {
      hrmConfigInsuranceSchemListAPI({
        pageType: 0
      })
        .then(res => {
          this.schemeList = res.data.list.map(item => {
            return {
              label: item.schemeName,
              value: item.schemeId
            }
          })
        })
        .catch(() => {
        })
    },

    /**
     * 获取详情
     */
    getDetail() {
      this.loading = true
      hrmInsuranceMonthRecordQueryDetailAPI(this.id)
        .then(res => {
          this.topData = res.data || {}
          this.loading = false
        })
        .catch(() => {
          this.loading = false
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
     * 顶部card 切换在保 停保
     */
    topCardSelect(index) {
      this.status = index == 0 ? 1 : 0
      this.refreshList()
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
     * 列表数据
     */
    getList() {
      this.loading = true
      const params = {
        page: this.currentPage,
        limit: this.pageSize,
        irecordId: this.id,
        status: this.status// 0 停保 1 参保
      }

      for (var key in this.filterObj) {
        params[key] = this.filterObj[key]
      }

      hrmInsuranceMonthRecordQueryListAPI(params)
        .then(res => {
          this.list = res.data.list
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
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 格式化字段
     */
    fieldFormatter(row, column) {
      return row[column.property] || '--'
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
     * 列表操作
     */
    handleCommand(command) {
      if (command === 'stop') {
        this.$confirm('确定要停止勾选的员工参保吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            hrmInsuranceMonthEmpRecordStopAPI(this.selectionList.map(item => item.iempRecordId))
              .then(res => {
                this.$message({
                  type: 'success',
                  message: '操作成功'
                })
                this.getDetail()
                this.getList()
              })
              .catch(() => {})
          })
          .catch(() => {})
      } else if (command === 'security') {
        this.schemeEditDialogShow = true
      }
    },

    /**
     * 列表点击
     */
    handleRowClick(row, column, event) {
      if (column.property == 'employeeName') {
        this.rowData = row
        this.employDetailShow = true
      }
    },

    /**
     * 勾选操作
     */
    handleSelectionChange(val) {
      this.selectionList = val // 勾选的行
      this.tableHeight = document.documentElement.clientHeight - (this.selectionList.length > 0 ? 390 : 340)
    },

    /**
     * 表头字段
     */
    getFieldList() {
      return [{
        fieldName: 'employeeName',
        name: '姓名',
        width: 140
      }, {
        fieldName: 'jobNumber',
        name: '工号',
        width: 80
      }, {
        fieldName: 'deptName',
        name: '部门',
        width: 80
      }, {
        fieldName: 'entryTime',
        name: '入职日期',
        width: 180
      }, {
        fieldName: 'mobile',
        name: '手机号码',
        width: 140
      }, {
        fieldName: 'city',
        name: '参保城市',
        width: 80
      }, {
        fieldName: 'schemeName',
        name: '参保方案',
        width: 120
      }, {
        fieldName: 'personalInsuranceAmount',
        name: '个人社保费',
        width: 120
      }, {
        fieldName: 'corporateInsuranceAmount',
        name: '公司社保费',
        width: 120
      }, {
        fieldName: 'personalProvidentFundAmount',
        name: '个人公积金费',
        width: 120
      }, {
        fieldName: 'corporateProvidentFundAmount',
        name: '公司公积金费',
        width: 120
      }]
    },

    /**
     * 添加人员
     */
    addEmpClick() {
      this.employAddDialogShow = true
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
