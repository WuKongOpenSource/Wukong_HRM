<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <div id="employee-main-container" class="performance-self">
    <xr-header
      ft-top="0"
      label="KPI考核" />
    <div class="crm-container">
      <wk-table-header
        style="margin-top: 30px;"
        :search.sync="search"
        :props="wkHeaderProps"
        :filter-header-props="wkHeaderProps.filterHeaderProps">
        <!-- eslint-disable-next-line -->
            <template slot="custom" slot-scope="scope">
          <div class="custom-scene">
            <span>显示：</span>
            <el-button
              v-for="(item, index) in tabList"
              :key="index"
              :title="item.label"
              :type="item.value === activeTab ? 'selected' : null"
              :icon="item.icon"
              @click="tabsClick(item)">
              {{ item.label }}
              <span
                v-if="item.num && item.num != '0'"
                class="values-span"
                :class="{ 'selected': item.value === activeTab }">
                {{ item.num }}
              </span>
            </el-button>
          </div>
        </template>
      </wk-table-header>
      <xr-table-header>
        <el-tabs v-model="subTabType" style="width: 100%;margin-bottom: -8px;" @tab-click="subTabClick">
          <el-tab-pane
            v-for="tab in evaluatoSubTabList"
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
        :class="WKConfig.tableStyle.class"
        :stripe="WKConfig.tableStyle.stripe"
        use-virtual
        class="n-table--border el-table-header--white"
        highlight-current-row
        style="width: 100%;">
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
          label="操作"
          fixed="right"
          width="100">
          <template slot-scope="scope">
            <el-button style="padding: 0;" type="text" @click="handleClick(scope.row, scope.$index)">{{ handlerOperate(scope.row) }}</el-button>
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

    <employee-details
      v-if="detailShow"
      :id="id"
      :active-tab="activeTab"
      :appraisal-plan-name="appraisalPlanName"
      :sub-tab-type="subTabType"
      @hide-view="detailShow = false"
      @success="success" />
  </div>
</template>

<script>
import {
  queryStageAppraisalListAPI,
  hrmPerformanceEmployeeQueryNumAPI
} from '@/api/hrm/selfService/performance'

import XrHeader from '@/components/XrHeader'
import XrTableHeader from '@/components/XrTableHeader'
import EmployeeDetails from '@/views/hrm/components/EmployeeDetails'
import WkTableHeader from '@/components/Page/WkTableHeader'

export default {
  name: 'PerformanceSelf',
  components: {
    XrHeader,
    XrTableHeader,
    // WkPopoverFilter,
    EmployeeDetails,
    WkTableHeader
  },
  beforeRouteUpdate(to, from, next) {
    this.handleTab(to)
    next()
  },
  data() {
    return {
      loading: false, // 加载动画
      tableHeight: document.documentElement.clientHeight - 305, // 表的高度
      list: [],
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      search: '', // 搜索内容
      // 筛选宽
      popoverFilterWidth: document.documentElement.clientWidth - 80,
      activeTab: '1',
      tabList: [{
        label: '指标填写',
        value: '1',
        type: 'FILL',
        num: 0
      }, {
        label: '指标确认',
        value: '2',
        type: 'TARGET_CONFIRMATION',
        num: 0
      }, {
        label: '指标评分',
        value: '4',
        type: 'SCORE',
        num: 0
      }, {
        label: '结果审核',
        value: '5',
        type: 'RESULT_AUDIT',
        num: 0
      }, {
        label: '结果确认',
        value: '6',
        type: 'RESULT_CONFIRMATION',
        num: 0
      }, {
        label: '申诉确认',
        value: '7',
        type: 'APPEAL_CONFIRMATION',
        num: 0
      }],
      wkHeaderProps: {
        showFilterView: false,
        filterHeaderProps: {
          searchPlaceholder: '请输入考核指标名称',
          tabSetShow: false
        },
        filterFormProps: {
          showSaveScene: false
        }
      },
      // 结果评定/我的 子tab
      subTabType: '2',
      // 控制详情展示
      detailShow: false,

      id: '',

      appraisalCycleType: {
        1: '月度',
        2: '季度',
        3: '上半年',
        4: '下半年',
        5: '全年'
      },

      appraisalPlanName: '' // 绩效名称

    }
  },
  computed: {
    // 表头
    fieldList() {
      return this.getTableFileList()
    },

    // 分类
    evaluatoSubTabList() {
      if (this.activeTab == 1) {
        return [{ label: '待填写', name: '2' }, { label: '已填写', name: '1' }]
      } else if (this.activeTab == 2) {
        return [{ label: '待确认', name: '2' }, { label: '已确认', name: '1' }]
      } else if (this.activeTab == 4) {
        return [{ label: '待评分', name: '2' }, { label: '已评分', name: '1' }]
      } else if (this.activeTab == 5) {
        return [{ label: '待审核', name: '2' }, { label: '已审核', name: '1' }]
      } else if (this.activeTab == 6) {
        return [{ label: '待确认结果', name: '2' }, { label: '已确认', name: '1' }, { label: '已申诉', name: '5' }]
      } else if (this.activeTab == 7) {
        return [{ label: '待确认', name: '2' }, { label: '已确认', name: '1' }]
      } else {
        return []
      }
    }
  },
  watch: {},
  created() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - 305
    }
    this.handleTab(this.$route)
  },
  methods: {
    // 消息跳转处理
    handleTab(to) {
      this.activeTab = to.query.tabType ? to.query.tabType : '1'
      this.subTabType = to.query.subTabType ? to.query.subTabType : '2'
      this.refreshList()
    },

    /**
     * 评定tab切换
     */
    subTabClick() {
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

    getTabsNum() {
      hrmPerformanceEmployeeQueryNumAPI()
        .then(res => {
          const data = res.data || {}
          this.tabList.forEach(item => {
            item.num = data[item.type]
          })
        })
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

      params.search = this.search
      params.stage = this.activeTab
      params.status = this.subTabType

      queryStageAppraisalListAPI(params)
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
      return [{
        fieldName: 'appraisalPlanName',
        name: '考核方案名称'
      }, {
        fieldName: 'employeeName',
        name: '姓名',
        width: 80
      }, {
        fieldName: 'appraisalCycleType',
        name: '考核周期类型',
        width: 80
      },
      {
        fieldName: 'appraisalCycle',
        name: '考核周期',
        width: 80
      }, {
        fieldName: 'score',
        name: '评分',
        width: 80
      }, {
        fieldName: 'level',
        name: '考核结果',
        width: 80
      }]
    },

    /**
     * 保存成功
     */
    success() {
      this.refreshList(this.currentPage)
    },

    /**
     * 列表格式化
     */
    fieldFormatter(row, column) {
      // 如果需要格式化
      if (column.property == 'appraisalCycleType') {
        return this.appraisalCycleType[row[column.property]] ? this.appraisalCycleType[row[column.property]] : row[column.property]
      } else if (column.property == 'appraisalCycle') {
        return row['quarter'] ? row[column.property] + row['quarter'] : row[column.property]
      }
      return row[column.property] ? row[column.property] : '--'
    },

    /**
     * 操作
     */
    handlerOperate(row) {
      /**
       *  row.appraisalStatus/this.activeTab 当前场景
       *  1.指标填写 2.指标确认 4.指标评分 5.结果审核 6.结果确认 7.申诉确认
       *  this.subTabType 状态
       *  2.未处理 1.已处理
       *  */
      if (row.stageType == 1 && this.activeTab == 1 && this.subTabType == 2) {
        return '填写'
      } else if (row.stageType == 1 && this.activeTab == 1 && this.subTabType == 1) {
        return '查看详情'
      } else if (row.stageType == 2 && this.activeTab == 2 && this.subTabType == 2) {
        return '确认'
      } else if (row.stageType == 2 && this.activeTab == 2 && this.subTabType == 1) {
        return '查看详情'
      } else if (row.stageType == 3 && this.activeTab == 4 && this.subTabType == 2) {
        return '自评'
      } else if (row.stageType == 4 && this.activeTab == 4 && this.subTabType == 2) {
        return '评分'
      } else if ((row.stageType == 4 || row.stageType == 3) && this.activeTab == 4 && this.subTabType == 1) {
        return '查看详情'
      } else if (row.stageType == 5 && this.activeTab == 5 && this.subTabType == 2) {
        return '审核'
      } else if (row.stageType == 5 && this.activeTab == 5 && this.subTabType == 1) {
        return '查看详情'
      } else if (row.stageType == 6 && this.activeTab == 6 && this.subTabType == 2) {
        return '确认'
      } else if (row.stageType == 6 && this.activeTab == 6 && (this.subTabType == 1 || this.subTabType == 5)) {
        return '查看详情'
      } else if (row.stageType == 7 && this.activeTab == 7 && this.subTabType == 2) {
        return '确认'
      } else if (row.stageType == 7 && this.activeTab == 7 && this.subTabType == 1) {
        return '查看详情'
      } else {
        return ''
      }
    },

    /**
     * 列表点击
     */
    handleClick(row, index) {
      this.appraisalPlanName = row.appraisalPlanName
      this.detailShow = true
      this.id = row.appraisalEmployeeId
    },

    tabsClick(item) {
      this.activeTab = item.value
      this.refreshList()
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

  .values-span {
    padding: 0 7px;
    margin-left: 2px;
    color: $--color-n800;
    background-color: $--color-n40;
    border-radius: 10px;
  }

  .values-span.selected {
    color: $--color-n0;
    background-color: $--color-n500;
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
