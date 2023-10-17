<template>
  <div class="salary-archives">
    <xr-header
      ft-top="0"
      placeholder="请输入员工姓名/工号"
      show-search
      @search="searchClick">
      <template slot="label">
        薪资档案<el-tooltip
          v-if="hrmUserType === 2"
          effect="dark"
          placement="top"
          style="margin-left: 8px;"
          content="仅展示您管理范围内的员工">
          <i class="wk wk-help wk-help-tips" />
        </el-tooltip>
      </template>
      <template slot="bottom-ft">
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
      </template>
      <template slot="ft">
        <el-button v-if="hrmUserType === 1" @click="templateManage">管理定薪模板</el-button>
        <el-button
          v-if="updateAuth"
          type="primary"
          @click="headerMoreHandleClick('batchEdit')">批量调薪</el-button>
        <el-dropdown
          v-if="headerMoreHandle.length > 0"
          style="margin-left: 10px;"
          trigger="click"
          @command="headerMoreHandleClick">
          <el-button icon="el-icon-more" />
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="(item, index) in headerMoreHandle"
              :key="index"
              :icon="item.icon"
              :command="item.type">{{ item.name }}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </template>
    </xr-header>
    <div class="crm-container">
      <flexbox class="filter">
        <wk-border-menu
          v-model="tabType"
          :list="tabLeftList"
          :is-select="!!tabLeftList.find(item => item.name == tabType)"
          style="flex: 6;"
          @select="tabClick"
        />
        <wk-border-menu
          v-model="tabType"
          :list="tabCenterList"
          :is-select="!!tabCenterList.find(item => item.name == tabType)"
          style="flex: 2;"
          @select="tabClick"
        />
        <div style="flex: 3;" />
      </flexbox>
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
        :cell-class-name="cellClassName"
        :class="WKConfig.tableStyle.class"
        :stripe="WKConfig.tableStyle.stripe"
        use-virtual
        class="n-table--border el-table-header--white"
        highlight-current-row
        style="width: 100%;"
        @row-click="handleRowClick"
        @selection-change="handleSelectionChange">
        <el-table-column
          v-if="updateAuth"
          show-overflow-tooltip
          fixed
          type="selection"
          align="center"
          width="55" />
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
        <el-table-column
          v-if="updateAuth"
          label="操作"
          fixed="right"
          width="100">
          <template slot-scope="scope">
            <el-button v-if="scope.row.changeType === 0" type="text" @click="rowHandleClick('fix', scope.row)">定薪</el-button>
            <el-button v-else type="text" @click="rowHandleClick('change', scope.row)">调薪</el-button>
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
    <salary-archives-detail
      v-if="showDview"
      :id="rowID"
      @handle="detailHandle"
      @close="showDview=false" />

    <template-manage-dialog
      :visible.sync="templateManageDialogShow"
    />

    <fix-salary-add
      v-if="fixAddShow"
      :id="rowData.employeeId"
      :help-obj="helpObj.fix"
      @change="getList"
      @close="fixAddShow = false"
    />

    <change-salary-add
      v-if="changeAddShow"
      :id="rowData.employeeId"
      :help-obj="helpObj.change"
      @change="getList"
      @close="changeAddShow = false"
    />

    <batch-change-salary-add
      v-if="batchChangeAddShow"
      :user-list="isBatchChange ? [] : selectionList"
      @close="batchChangeAddShow = false"
    />
  </div>
</template>

<script>
import {
  hrmEmployeeQueryStatusNumAPI
} from '@/api/hrm/employee'
import {
  hrmSalaryArchivesQueryListAPI,
  hrmSalaryArchivesDownLoadChangeTemplateAPI,
  hrmSalaryArchivesChangeRecordExportAPI,
  hrmSalaryArchivesDownLoadFixTemplateAPI,
  hrmSalaryArchivesFixRecordExportAPI
} from '@/api/hrm/salary'
import {
  hrmDeptQueryTreeListAPI
} from '@/api/hrm/dept'

import XrHeader from '@/components/XrHeader/Search'
import XrTableHeader from '@/components/XrTableHeader'
import WkPopoverFilter from '@/components/NewCom/WkPopoverFilter'
import SalaryArchivesDetail from './Detail'
import WkBorderMenu from '../../components/WkBorderMenu'
import TemplateManageDialog from './components/TemplateManageDialog'
import FixSalaryAdd from './components/FixAdd'
import ChangeSalaryAdd from './components/ChangeAdd'
import BatchChangeSalaryAdd from './components/BatchChangeAdd'

import { mapGetters } from 'vuex'
import { employeeModel } from '@/views/hrm/employee/model/employee'
import archivesModel from './model/archives'
import { isEmpty } from '@/utils/types'

export default {
  // 薪资档案
  name: 'SalaryArchives',
  components: {
    XrHeader,
    XrTableHeader,
    SalaryArchivesDetail,
    WkPopoverFilter,
    WkBorderMenu,
    TemplateManageDialog,
    FixSalaryAdd,
    ChangeSalaryAdd,
    BatchChangeSalaryAdd
  },
  data() {
    return {
      loading: false, // 加载动画
      helpObj: {
        fix: {
          dataType: 33,
          dataId: 298
        },
        change: {
          dataType: 33,
          dataId: 339
        }
      },
      tableHeight: document.documentElement.clientHeight - 350, // 表的高度
      list: [],
      fieldList: [{
        field: 'employeeName',
        name: '姓名'
      }, {
        field: 'jobNumber',
        name: '工号'
      }, {
        field: 'deptName',
        name: '部门'
      }, {
        field: 'post',
        name: '岗位'
      }, {
        field: 'status',
        name: '员工状态'
      }, {
        field: 'entryTime',
        name: '入职日期'
      }, {
        field: 'becomeTime',
        name: '转正日期'
      }, {
        field: 'changeData',
        name: '最近调整日期'
      }, {
        field: 'changeReason',
        name: '调薪原因'
      }, {
        field: 'total',
        name: '工资合计'
      }],
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      search: '', // 搜索内容
      popoverFilterWidth: document.documentElement.clientWidth - 80,
      tabType: '12',
      // 员工状态 1正式 2试用 3实习 4兼职 5劳务 6顾问 7返聘 8外包 13待离职 14待入职  15已离职
      tabLeftList: [{
        label: '在职',
        name: '11',
        num: 0
      }, {
        label: '全职',
        name: '12',
        num: 0
      }, {
        label: '实习',
        name: '3',
        num: 0
      }, {
        label: '劳务',
        name: '5',
        num: 0
      }, {
        label: '顾问',
        name: '6',
        num: 0
      }, {
        label: '返聘',
        name: '7',
        num: 0
      }, {
        label: '外包',
        name: '8',
        num: 0
      }, {
        label: '兼职',
        name: '4',
        num: 0
      }],
      tabCenterList: [{
        label: '试用',
        name: '2',
        num: 0
      }, {
        label: '正式',
        name: '1',
        num: 0
      }],
      rowID: '', // 行信息
      showDview: false,
      // 高级筛选
      filterObj: {}, // 筛选确定数据
      filterList: [
        {
          name: '部门',
          field: 'deptId',
          formType: 'structure',
          props: {
            dataType: 'hrm'
          },
          request: hrmDeptQueryTreeListAPI,
          setting: []
        }, {
          name: '岗位',
          field: 'post',
          formType: 'text',
          setting: []
        }, {
          name: '调薪状态',
          field: 'changeType',
          formType: 'select',
          setting: [{
            label: '未定薪',
            value: 0
          }, {
            label: '已定薪',
            value: 1
          }, {
            label: '已调薪',
            value: 2
          }]
        }],
      selectionList: [], // 勾选数据 用于全局导出
      rowData: null,
      fixAddShow: false,
      changeAddShow: false,
      isBatchChange: false,
      batchChangeAddShow: false,
      templateManageDialogShow: false // 模板管理
    }
  },
  computed: {
    ...mapGetters([
      'hrm',
      'hrmUserType',
      'userInfo'
    ]),

    // 薪资管理
    salaryAuth() {
      return this.hrm.salary
    },

    // 更新权限
    updateAuth() {
      return this.salaryAuth && this.salaryAuth.updateArchives
    },

    /**
     * 头部更多操作
     */
    headerMoreHandle() {
      const temps = []
      if (this.updateAuth) {
        temps.push({ type: 'importFix', name: '导入定薪', icon: 'wk wk-import' })
        temps.push({ type: 'importChange', name: '导入调薪', icon: 'wk wk-import' })
      }
      return temps
    },

    // 操作
    tabelHandles() {
      const temps = []
      temps.push({
        label: '批量调薪',
        command: 'edit',
        icon: 'wk wk-edit'
      })
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
    }
  },
  watch: {},
  created() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - (this.selectionList.length > 0 ? 400 : 350)
    }

    // 监听导入
    this.$bus.on('import-crm-done-bus', () => {
      this.refreshPageData()
    })

    this.refreshList()
    this.getTabsNum()
  },
  mounted() {
    // 路由配置事件
    this.initTask()
  },
  beforeDestroy() {
    this.$bus.off('import-crm-done-bus')
  },
  methods: {
    /**
     * 刷新当前页面信息
     */
    refreshPageData() {
      this.getList()
      this.getTabsNum()
    },

    /**
     * 获取tab 数量
     */
    getTabsNum() {
      hrmEmployeeQueryStatusNumAPI()
        .then(res => {
          const data = res.data || {}
          this.tabLeftList.forEach(item => {
            item.num = data[parseInt(item.name)] || 0
          })
          this.tabCenterList.forEach(item => {
            item.num = data[parseInt(item.name)] || 0
          })
          this.tabRightList.forEach(item => {
            item.num = data[parseInt(item.name)] || 0
          })
        })
        .catch(() => {
        })
    },

    /**
     * tab点击
     */
    tabClick(tab) {
      this.refreshList()
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
    createSaveSuccess() {
      this.getTabsNum()
      this.refreshList()
    },

    /**
     * 搜索
     */
    searchClick(search) {
      this.search = search
      this.refreshList()
    },

    /**
     * 详情操作
     */
    detailHandle(data) {
      this.getTabsNum()
      this.getList()
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
        search: this.search,
        status: this.tabType
      }

      for (var key in this.filterObj) {
        params[key] = this.filterObj[key]
      }

      hrmSalaryArchivesQueryListAPI(params)
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
      const rowValue = row[column.property]
      if (column.property == 'status') {
        return employeeModel.statusValue[rowValue] || '--'
      } else if (column.property == 'changeReason') {
        return archivesModel.changeReasonValue[rowValue] || '--'
      } else {
        return isEmpty(rowValue) ? '--' : rowValue
      }
    },

    /**
     * 通过回调控制class
     */
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (column.property == 'employeeName') {
        return 'can-visit--underline'
      } else {
        return ''
      }
    },

    /**
     * 列表操作
     */
    handleCommand(command) {
      if (command === 'edit') {
        this.isBatchChange = false
        this.batchChangeAddShow = true
      }
    },

    /**
     * 头部更多操作
     */
    headerMoreHandleClick(command) {
      if (command == 'importFix') {
        this.$wkImport.import('hrmSalaryFix', {
          typeName: '定薪',
          ownerSelectShow: false,
          repeatHandleShow: false,
          moduleType: 81,
          helpObj: {
            dataType: 33,
            dataId: 295
          },
          importRequest: hrmSalaryArchivesFixRecordExportAPI, // 导入请求
          templateRequest: hrmSalaryArchivesDownLoadFixTemplateAPI, // 模板请求
          userInfo: this.userInfo
        })
      } else if (command == 'importChange') {
        this.$wkImport.import('hrmSalaryChange', {
          typeName: '调薪',
          ownerSelectShow: false,
          repeatHandleShow: false,
          moduleType: 82,
          helpObj: {
            dataType: 33,
            dataId: 296
          },
          importRequest: hrmSalaryArchivesChangeRecordExportAPI, // 导入请求
          templateRequest: hrmSalaryArchivesDownLoadChangeTemplateAPI, // 模板请求
          userInfo: this.userInfo
        })
      } else if (command == 'batchEdit') {
        this.isBatchChange = true
        this.batchChangeAddShow = true
      }
    },

    /**
     * 列表点击
     */
    handleRowClick(row, column, event) {
      if (column.property == 'employeeName') {
        this.rowID = row.employeeId
        this.showDview = true
      }
    },

    /**
     * 勾选操作
     */
    handleSelectionChange(val) {
      this.selectionList = val // 勾选的行
      this.tableHeight = document.documentElement.clientHeight - (this.selectionList.length > 0 ? 400 : 350)
    },

    /**
     * 模板管理
     */
    templateManage() {
      this.templateManageDialogShow = true
    },

    /**
     * 定薪和调薪
     */
    rowHandleClick(type, data) {
      this.rowData = data
      if (type === 'fix') {
        this.fixAddShow = true
      } else {
        this.changeAddShow = true
      }
    },

    /**
     * @description: 通过路由等路由携带的需要处理的事情
     * @return {*}
     */
    initTask() {
      const { query } = this.$route
      if (query.event === 'detail') {
        this.rowID = query.id
        this.showDview = true
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.salary-archives {
  .xr-header {
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

  .table-more {
    cursor: pointer;

    &:hover {
      color: $--color-primary;
    }
  }

  .filter {
    margin-bottom: 8px;
  }

  // 列表操作按钮
  .el-button--text {
    padding: 0;
  }
}
</style>
