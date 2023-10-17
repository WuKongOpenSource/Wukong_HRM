<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <div id="employee-main-container" class="performance-index">
    <xr-header
      ft-top="0"
      label="考核计划">
      <template slot="ft">
        <el-button
          v-if="createAuth"
          style="margin-right: 8px;"
          type="primary"
          @click="createClick">新建KPI计划</el-button>
      </template>
    </xr-header>
    <div class="main-body">
      <div
        v-empty="!viewListAuth"
        xs-empty-icon="nopermission"
        xs-empty-text="暂无权限"
        class="main-list">
        <div class="main-list__body">
          <wk-table-header
            :search.sync="search"
            :props="wkHeaderProps"
            :filter-header-props="wkHeaderProps.filterHeaderProps"
            @event-change="tableHeaderHandle">
            <!-- eslint-disable-next-line -->
            <template slot="custom" slot-scope="scope">
              <div class="custom-scene">
                <span class="label">显示：</span>
                <el-button
                  v-for="(item, index) in sceneOptions"
                  :key="index"
                  :title="item.label"
                  :type="item.value === activeTab ? 'selected' : null"
                  :icon="item.icon"
                  @click="tabsClick(item)">
                  {{ item.label }}
                  <span
                    v-if="item.num && item.num != '0'"
                    class="values-span"
                    :class="{ 'selected': item.value === activeTab }"
                  >
                    {{ item.num }}
                  </span>
                </el-button>
              </div>
            </template>
          </wk-table-header>
          <el-table
            id="crm-table"
            :row-height="50"
            :data="list"
            :height="tableHeight"
            :cell-class-name="cellClassName"
            use-virtual
            class="n-table--border el-table-header--white"
            stripe
            highlight-current-row
            style="width: 100%;margin-top: 15px;"
            @row-click="handleRowClick">
            <el-table-column
              v-for="(item, index) in fieldList"
              :key="index"
              :fixed="index == 0"
              :prop="item.field"
              :label="item.name"
              :min-width="item.width"
              :formatter="tableFieldFormatter"
              show-overflow-tooltip>
              <template slot-scope="{row, column}">
                <span v-if="item.field == 'appraisalCycle'">{{ row.quarter ? row.appraisalCycle + row.quarter : row.appraisalCycle }}</span>
                <span v-else-if="item.field == 'appraisalCycleType'">{{ appraisalCycleType[row[column.property]] }}</span>
                <span v-else> {{ row[column.property] ? row[column.property] : '--' }}</span>
              </template>
            </el-table-column>
            <!-- <el-table-column /> -->
            <el-table-column
              label="操作"
              fixed="right"
              width="150">
              <template slot-scope="{ row }">
                <div v-if="activeTab == 4">
                  <el-button
                    v-if="querySettingAuth"
                    type="text"
                    @click="handleTypeClick('check', row)">
                    查看考核设置
                  </el-button>
                  <el-button
                    v-if="delOfFileAuth"
                    type="text"
                    @click="handleDelClick(row)">
                    删除
                  </el-button>
                </div>
                <el-dropdown
                  v-else
                  trigger="click"
                  @command="handleTypeClick($event, row)">
                  <i
                    class="el-icon-more table-more" />
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item
                      v-for="(item, index) in getRowDropdownItems(row)"
                      :key="index"
                      :command="item.command">{{ item.label }}</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </template>
            </el-table-column>
          </el-table>
        </div>
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
    </div>

    <!-- <performance-create-view
      v-if="isCreate"
      :id="rowId"
      :status="rowStatus"
      @success="createSaveSuccess"
      @close="isCreate=false" /> -->
    <kpi-assessment-plan-create
      v-if="isCreate"
      :inspection-detial="inspectionDetial"
      @close="isCreate=false"
      @createSuccess="refreshList" />

    <employees-view
      :id="employeesViewData.appraisalId"
      :visible.sync="employeesViewShow"
      :status="employeesViewData.status"
    />

    <not-started-detail
      v-if="showDview"
      :id="rowID"
      :detail="rowData"
      @handle="detailHandle"
      @close="showDview=false"
      @success="refreshList" />

    <ongoing-detail
      v-if="showOngoingDview"
      :id="rowOngoingId"
      :detail="rowOngoingData"
      :is-result="isResult"
      :query-employee-appraisal-auth="queryEmployeeAppraisalAuth"
      @handle="detailHandle"
      @close="showOngoingDview=false"
      @success="refreshList" />
  </div>
</template>

<script>
import {
  hrmAppraisalPlanQueryListAPI,
  hrmAppraisalPlanDelAppraisalPlanAPI,
  hrmAppraisalPlanCheckAppraisalPlanAPI,
  hrmAppraisalPlanTerminationPlanAPI,
  hrmAppraisalPlanOpenScoringAPI,
  hrmAppraisalPlanToInterviewAPI,
  hrmAppraisalPlanPlaceOnFileAPI,
  hrmAppraisalPlanQueryAppraisalStatusNumAPI,
  hrmPerformanceDeleteAPI,
  hrmPerformanceStopAPI,
  hrmPerformanceUpdateStatusAPI,
  hrmAppraisalPlanQuerySetting,
  delAppraisalPlanOfFile
} from '@/api/hrm/performance'

import WkTableHeader from '@/components/Page/WkTableHeader'

import XrHeader from '@/components/XrHeader'
import EmployeesView from '../index/components/EmployeesView'
import KpiAssessmentPlanCreate from './Create'
import NotStartedDetail from './NotStartedDetail'
import OngoingDetail from './OngoingDetail'

import { timeToFormatTime } from '@/utils'
import { mapGetters } from 'vuex'
import { isEmpty } from '@/utils/types'

export default {
  name: 'KpiAssessmentIndex',
  components: {
    XrHeader,
    EmployeesView,
    KpiAssessmentPlanCreate,
    WkTableHeader,
    NotStartedDetail,
    OngoingDetail
  },

  provide() {
    return {
      'editAuth': this.editAuth
    }
  },

  beforeRouteUpdate(to, from, next) {
    if (to.query.tabType) {
      this.tabType = to.query.tabType
    }
    this.refreshList()
    next()
  },

  data() {
    return {
      loading: false, // 加载动画
      isCreate: false, // 是创建
      tabShowType: 'list',
      list: [],
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      //  0 未开启考核 1 目标填写/目标确认中 2 结果评定中 3 结果确认中 4 归档 5 进行中的考核
      tabType: '0',
      tabLeftList: [{
        label: '未开始考核',
        name: '0',
        num: 0
      }, {
        label: '进行中考核',
        name: '5',
        num: 0
      }, {
        label: '已归档',
        name: '4',
        num: 0
      }],
      tabRightList: [{
        label: '目标填写/目标确认中',
        name: '1',
        num: 0
      }, {
        label: '结果评定中',
        name: '2',
        num: 0
      }, {
        label: '结果确认中',
        name: '3',
        num: 0
      }],
      stepList: [
        '发起考核',
        '填写目标/确认目标',
        '评定结果',
        '确认结果',
        '考核归档'
      ],
      // 控制详情展示
      rowId: null, // 行id
      rowData: null, // 行信息
      rowStatus: null, // 行状态
      showDview: false,
      rowOngoingId: null, // 进行中和归档行id
      rowOngoingData: null, // 进行中和归档行信息
      isResult: false, // 是否是查看结果
      showOngoingDview: false,
      employeesViewData: {},
      employeesViewShow: false,

      search: '',
      activeTab: 2,
      sceneOptions: [
        { label: '未开始考核', value: 2, num: '0' },
        { label: '进行中', value: 3, num: '0' },
        { label: '已归档', value: 4, num: '0' }
      ],
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

      appraisalCycleType: {
        1: '月度',
        2: '季度',
        3: '上半年',
        4: '下半年',
        5: '全年',
        6: '其他'
      },

      // 表格
      tableHeight: document.documentElement.clientHeight - 260, // 表的高度
      fieldList: [],

      inspectionDetial: {}
    }
  },

  computed: {
    ...mapGetters([
      'hrm'
    ]),

    kpiAuth() {
      return this.hrm.appraisalPlan
    },

    // 查看列表权限
    viewListAuth() {
      return this.kpiAuth && this.kpiAuth.queryPageList
    },

    // 新建kpi考核计划
    createAuth() {
      return this.kpiAuth && this.kpiAuth.addAppraisalPlan
    },

    // 删除考核权限
    deleteAuth() {
      return this.kpiAuth && this.kpiAuth.delAppraisalPlan
    },

    // 删除归档权限
    delOfFileAuth() {
      return this.kpiAuth && this.kpiAuth.delAppraisalPlanOfFile
    },

    // 检查并开启考核设置权限
    checkAppraisalPlanAuth() {
      return this.kpiAuth && this.kpiAuth.checkAppraisalPlan
    },

    // 查看考核设置权限
    querySettingAuth() {
      return this.kpiAuth && this.kpiAuth.querySetting
    },

    // 终止考核权限
    terminationPlanAuth() {
      return this.kpiAuth && this.kpiAuth.terminationPlan
    },

    // 开启评分权限
    openScoringAuth() {
      return this.kpiAuth && this.kpiAuth.openScoring
    },

    // 发起绩效面谈权限
    toInterviewAuth() {
      return this.kpiAuth && this.kpiAuth.toInterview
    },

    // 归档权限
    placeOnFileAuth() {
      return this.kpiAuth && this.kpiAuth.placeOnFile
    },

    // 考核详情权限
    quotaInformationAuth() {
      return this.kpiAuth && this.kpiAuth.quotaInformation
    },

    queryEmployeeAppraisalAuth() {
      return this.kpiAuth && this.kpiAuth.queryAppraisalResultPageList
    }
  },
  watch: {
  },
  created() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - 260
    }
    if (this.$route.query.tabType) {
      this.tabType = this.$route.query.tabType
    }
    this.refreshList()
  },
  mounted() {
    // 路由配置事件
    this.initTask()
  },
  methods: {
    /**
     * 展示乐行change
     */
    showTypeChange(type) {
      this.tabShowType = type
    },

    /**
     * 新建
     */
    createClick() {
      this.inspectionDetial = {}
      this.isCreate = true
    },

    /**
     * 创建成功
     */
    createSaveSuccess() {
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
      this.getFieldList()
    },

    /**
     * 更改当前页数
     */
    handleCurrentChange(val) {
      this.currentPage = val
      this.getFieldList()
    },

    /**
     * 获取tab 数量
     */
    getTabsNum() {
      hrmAppraisalPlanQueryAppraisalStatusNumAPI()
        .then(res => {
          console.log(res.data)
          const data = res.data || {}
          this.sceneOptions.forEach(item => {
            item.num = data[item.value]
          })
        })
        .catch(() => {
        })
    },

    getFieldList() {
      if (this.activeTab == 2) {
        this.fieldList = [{
          field: 'appraisalPlanName',
          width: 120,
          name: '考核名称'
        }, {
          field: 'inspectionScopeName',
          width: 180,
          name: '考核范围'
        }, {
          field: 'appraisalCycleType',
          width: 120,
          name: '考核周期类型'
        }, {
          field: 'appraisalCycle',
          width: 120,
          name: '考核周期'
        }, {
          field: 'appraisalIllustrate',
          width: 120,
          name: '考核说明'
        }]
      } else {
        this.fieldList = [{
          field: 'appraisalPlanName',
          width: 120,
          name: '考核名称'
        }, {
          field: 'inspectionScopeName',
          width: 180,
          name: '考核范围'
        }, {
          field: 'appraisalCycleType',
          width: 120,
          name: '考核周期类型'
        }, {
          field: 'appraisalCycle',
          width: 120,
          name: '考核周期'
        }, {
          field: 'appraisalPersons',
          width: 120,
          name: '考核人数'
        }, {
          field: 'fillPersons',
          width: 120,
          name: '目标填写'
        }, {
          field: 'targetConfirmationPersons',
          width: 120,
          name: '目标待确认'
        }, {
          field: 'executingPersons',
          width: 120,
          name: '执行中'
        }, {
          field: 'selfScoringPersons',
          width: 120,
          name: '自评'
        }, {
          field: 'otherScoringPersons',
          width: 120,
          name: '他人评分'
        }, {
          field: 'resultAuditPersons',
          width: 120,
          name: '结果审核'
        }, {
          field: 'resultConfirmationPersons',
          width: 120,
          name: '结果确认'
        }, {
          field: 'appraisalIllustrate',
          width: 120,
          name: '考核说明'
        }]
      }

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
        pageType: 1, // 是否分页，0:不分页 1 分页
        status: this.activeTab
      }

      if (this.search) {
        params.appraisalPlanName = this.search
      }

      hrmAppraisalPlanQueryListAPI(params)
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
     * 表头搜索
     */
    tableHeaderHandle(type, data) {
      if (type == 'search') {
        this.refreshList()
      }
    },

    /**
     * 场景点击
     */
    tabsClick(item) {
      this.activeTab = item.value
      this.refreshList()
    },

    /**
     * 获取行操作
     */
    getRowDropdownItems(data) {
      const dropdownItems = []
      if (this.activeTab == 2) {
        if (this.deleteAuth) {
          dropdownItems.push({
            label: '删除考核',
            command: 'delete'
          })
        }
        if (this.querySettingAuth) {
          dropdownItems.push({
            label: '查看考核设置',
            command: 'check'
          })
        }
        if (this.checkAppraisalPlanAuth) {
          dropdownItems.push({
            label: '检查并开启考核',
            command: 'status'
          })
        }
      } else if (this.activeTab == 3) {
        if (this.querySettingAuth) {
          dropdownItems.push({
            label: '查看考核设置',
            command: 'check'
          })
        }
        if (this.terminationPlanAuth) {
          dropdownItems.push({
            label: '终止考核',
            command: 'termination'
          })
        }
        if (this.queryEmployeeAppraisalAuth) {
          dropdownItems.push({
            label: '考核结果',
            command: 'result'
          })
        }

        if (data.operationStage == 1 && this.openScoringAuth) {
          dropdownItems.unshift({
            label: '开始评分',
            command: 'score'
          })
        } else if (data.operationStage == 2 && this.toInterviewAuth) {
          dropdownItems.unshift({
            label: '发起绩效面谈',
            command: 'interview'
          })
        } else if (data.operationStage == 3 && this.placeOnFileAuth) {
          dropdownItems.unshift({
            label: '归档',
            command: 'archive'
          })
        }
      }

      return dropdownItems
    },

    handleTypeClick(command, data) {
      if (command == 'delete') {
        this.$confirm('确认要删除吗？确认后考核计划的设置、考核、结果记录、面谈记录将同时被删除', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            this.loading = true
            hrmAppraisalPlanDelAppraisalPlanAPI({
              appraisalPlanId: data.appraisalPlanId
            }).then(res => {
              this.loading = false
              this.$message.success('删除成功')
              this.refreshList()
            }).catch(() => {
              this.loading = false
            })
          })
          .catch(() => {
          })
      } else if (command == 'status') {
        this.loading = true
        hrmAppraisalPlanCheckAppraisalPlanAPI({
          appraisalPlanId: data.appraisalPlanId
        })
          .then(res => {
            this.loading = false
            this.rowData = data
            this.rowID = data.appraisalPlanId
            this.showDview = true
          })
          .catch(() => {
            this.loading = false
          })
      } else if (command == 'check') {
        this.loading = true
        hrmAppraisalPlanQuerySetting({ appraisalPlanId: data.appraisalPlanId })
          .then(res => {
            this.loading = false
            this.inspectionDetial = res.data
            this.isCreate = true
          })
          .catch(() => {
            this.loading = false
          })
      } else if (command == 'termination') {
        this.$confirm('确认要终止考核吗？确认后考核计划配置的流程将暂停', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            this.loading = true
            hrmAppraisalPlanTerminationPlanAPI({
              appraisalPlanId: data.appraisalPlanId
            }).then(res => {
              this.loading = false
              this.$message.success('终止考核成功')
              this.refreshList()
            }).catch(() => {
              this.loading = false
            })
          })
          .catch(() => {
          })
      } else if (command == 'score') {
        this.loading = true
        hrmAppraisalPlanOpenScoringAPI({
          appraisalPlanId: data.appraisalPlanId
        }).then(res => {
          this.loading = false
          this.$message.success('操作成功')
          this.refreshList()
        }).catch(() => {
          this.loading = false
        })
      } else if (command == 'interview') {
        this.loading = true
        hrmAppraisalPlanToInterviewAPI({
          appraisalPlanId: data.appraisalPlanId
        }).then(res => {
          this.loading = false
          this.$message.success('操作成功')
          this.refreshList()
        }).catch(() => {
          this.loading = false
        })
      } else if (command == 'archive') {
        this.loading = true
        hrmAppraisalPlanPlaceOnFileAPI({
          appraisalPlanId: data.appraisalPlanId
        }).then(res => {
          this.loading = false
          this.$message.success('操作成功')
          this.refreshList()
        }).catch(() => {
          this.loading = false
        })
      } else if (command == 'result') {
        this.rowOngoingData = data
        this.rowOngoingId = data.appraisalPlanId
        this.isResult = true
        this.showOngoingDview = true
      }
    },

    /**
     * 获取周期时间
     */
    getPeriodTime(data) {
      return `${timeToFormatTime(data.startTime, 'YYYY.MM.DD')}-${timeToFormatTime(data.endTime, 'YYYY.MM.DD')}`
    },

    /**
     * 获取状态操作按钮名字
     */
    getStatusHandleButtonName(item) {
      if (item.status == 4) {
        return ''
      }
      // 终止的考核  和 结果确认中 状态 可进行归档
      if (item.isStop == 1) {
        return '归档'
      }
      return {
        0: '开启考核',
        1: '开启评定',
        2: '开启结果确认',
        3: '归档'
      }[item.status]
    },

    /**
     * 操作禁用
     */
    isHandleDisabled(item) {
      return (item.status == 3 && item.isArchive == 0) // 是否可以归档 0 否 1 是
    },

    /**
     * 删除终止按钮
     */
    getIsDeleteHandle(item) {
      //  0 未开启考核 1 目标填写/目标确认中 2 结果评定中 3 结果确认中 4 归档 5 进行中的考核
      return item.status == 0 || item.status == 4 || item.isStop == 1
    },

    getDeleteButtonName(item) {
      if (item.status == 4) {
        return ''
      }
      if (this.getIsDeleteHandle(item)) {
        return '删除考核'
      }
      return '终止考核'
    },

    /**
     * 操作
     */
    rowClick(type, item, index) {
      if (type == 'delete') {
        // 删除操作
        if (this.getIsDeleteHandle(item)) {
          this.deleteItem(item)
        } else {
          this.stopItem(item)
        }
      } else if (type == 'status') {
        //  0 未开启考核 1 目标填写/目标确认中 2 结果评定中 3 结果确认中 4 归档 5 进行中的考核
        if (item.status == 3 || item.isStop == 1) {
          const message = item.isStop == 1 ? `${this.getStatusMessage(item.status, item.isStop)}，归档后不能修改，是否确认归档？` : `${this.getStatusMessage(item.status)}，归档后不能修改，是否确认归档？`
          this.updateItemStatus(message, item, '4')
        } else if (item.status == 0) {
          this.updateItemStatus(`${this.getStatusMessage(item.status)}，开启考核后只能修改结果评定人和结果确认人，是否确认开启？`, item, '1')
        } else if (item.status == 1) {
          let message = `${this.getStatusMessage(item.status)}，` + this.getStatusNumMessage(false, item.statistics)
          message = `${message}开启评定后只能修改结果确认人，是否确认开启？`
          this.updateItemStatus(message, item, '2')
        } else if (item.status == 2) {
          let message = `${this.getStatusMessage(item.status)}，` + this.getStatusNumMessage(true, item.statistics)
          message = `${message}开启结果确认后将无法修改结果确认人，是否确认开启？`

          this.updateItemStatus(message, item, '3')
        }
      } else if (type == 'check') {
        this.rowStatus = item.status
        this.rowId = item.appraisalId
        this.isCreate = true
      }
    },

    /**
     * @description: 删除归档
     * @param {*} row
     * @return {*}
     */
    handleDelClick(row) {
      this.$confirm('确定要删除吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delAppraisalPlanOfFile({ appraisalPlanId: row.appraisalPlanId })
          .then(res => {
            this.$message({
              type: 'success',
              message: '删除成功!'
            })
            this.refreshList()
          })
      }).catch(() => {
      })
    },

    /**
     * 值格式化
     */
    fieldFormatter(row, column, cellValue) {
      return `${cellValue}人`
    },

    /**
     * 数量颜色
     */
    numCellClassName({ row, column, rowIndex, columnIndex }) {
      if (columnIndex == 0) {
        return row.status === 0 ? '' : 'can-visit--underline'
      }
      return 'can-visit--underline'
    },

    /**
    * 卡片点击单元样式
     */
    itemCellStyle({ row, column }) {
      if (column.property == 0) {
        return row.status !== 0 ? { cursor: 'pointer' } : {}
      }
      return { cursor: 'pointer' }
    },

    /**
     * 数量查看
     */
    numRowClick(args, data) {
      const arr = Array.prototype.slice.call(args)
      console.log(arr)
      // if (arr[1].property != '0') {
      const status = arr[1].property
      if (!(data.status === 0 && status == '0')) {
        this.employeesViewData = {
          appraisalId: data.appraisalId,
          status: status == '0' ? '' : status
        }
        this.employeesViewShow = true
      }
    },

    /**
     * 删除
     */
    deleteItem(item) {
      this.$confirm('确定要删除考核?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          hrmPerformanceDeleteAPI(item.appraisalId).then(res => {
            this.$message.success('删除成功')
            this.refreshList()
          }).catch(() => {})
        })
        .catch(() => {})
    },

    /**
     *终止
     */
    stopItem(item) {
      let message = `${this.getStatusMessage(item.status)}，` + this.getStatusNumMessage(true, item.statistics)
      message = `${message}是否确认终止考核？`

      this.$confirm(message, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          hrmPerformanceStopAPI(item.appraisalId).then(res => {
            this.$message.success('终止成功')
            this.refreshList()
          }).catch(() => {})
        })
        .catch(() => {})
    },

    /**
     * 获取状态消息 无 已归档
     */
    getStatusMessage(status, isStop) {
      if (isStop === 1) {
        return '当前考核已终止'
      }
      return {
        0: '当前考核未开始',
        1: '当前考核在目标填写/目标确认中',
        2: '当前考核在结果评定中',
        3: '当前考核在结果确认中'
      }[status] || ''
    },

    /**
     * 获取num信息
     */
    getStatusNumMessage(evaluate = false, statistics) {
      const numObj = statistics || {}
      const writeNum = numObj[1]
      const sureNum = numObj[2]
      const evaluateNum = evaluate ? numObj[3] : 0
      let message = ''
      if (writeNum > 0 || sureNum > 0 || (evaluate && evaluateNum > 0)) {
        message = message + '还有'
        const mess = []
        if (writeNum > 0) {
          mess.push(`${writeNum}人目标待填写`)
        }
        if (sureNum > 0) {
          mess.push(`${sureNum}人目标待确认`)
        }
        if (evaluateNum > 0) {
          mess.push(`${evaluateNum}人结果待评定`)
        }
        message = message + mess.join('、') + '，'
      }
      return message
    },

    /**
     * 更新状态
     */
    updateItemStatus(message, item, status) {
      this.$confirm(`${message}`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          hrmPerformanceUpdateStatusAPI({
            appraisalId: item.appraisalId,
            status
          }).then(res => {
            this.$message.success('操作成功')
            this.refreshList()
          }).catch(() => {})
        })
        .catch(() => {})
    },

    // 表格
    /**
     * 通过回调控制class
     */
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if ((column.property === 'statistics.0' && row.status !== 0) ||
      ['appraisalName', 'statistics.1', 'statistics.2', 'statistics.3', 'statistics.4', 'appraisalPlanName'].includes(column.property)) {
        return 'can-visit--underline'
      } else {
        return ''
      }
    },

    /**
     * 列表点击
     */
    handleRowClick(row, column, event) {
      if (column.property === 'appraisalName') {
        this.rowClick('check', row, null)
      } else if (['statistics.0', 'statistics.1', 'statistics.2', 'statistics.3', 'statistics.4'].includes(column.property)) {
        const columnProperty = column.property.split('.')[1]
        this.numRowClick([null, { property: columnProperty }], row)
      } else if (column.property === 'appraisalPlanName' && row.status == 2) {
        if (this.querySettingAuth) {
          this.loading = true
          hrmAppraisalPlanQuerySetting({ appraisalPlanId: row.appraisalPlanId })
            .then(res => {
              this.loading = false
              this.inspectionDetial = res.data
              this.isCreate = true
            })
            .catch(() => {
              this.loading = false
            })
        } else {
          this.$message.error('暂无权限')
        }
      } else if (column.property === 'appraisalPlanName' && row.status != 2) {
        if (this.quotaInformationAuth) {
          this.rowOngoingData = row
          this.rowOngoingId = row.appraisalPlanId
          this.isResult = false
          this.showOngoingDview = true
        } else {
          this.$message.error('暂无权限')
        }
      }
      // else if (column.property === 'appraisalPlanName' && row.status == 4) {
      //   this.rowOngoingData = row
      //   this.rowOngoingId = row.appraisalPlanId
      //   this.isResult = false
      //   this.showOngoingDview = true
      // }
    },

    /**
     * 格式化字段
     */
    tableFieldFormatter(row, column, cellValue, index) {
      if (column.property === 'appraisalCycleType') {
        return {
          1: '月度',
          2: '季度',
          3: '上半年',
          4: '下半年',
          5: '全年',
          6: '其他'
        }[row.appraisalCycleType]
      }
      return isEmpty(cellValue) ? '--' : cellValue
    },

    /**
     * 获取阶段图标
     */
    getStepIcon(item, index) {
      // 如果有节点未完成
      if (index <= item.appraisalSteps) {
        return 'wk wk-success'
      } else if (index < item.activateSteps) {
        return 'wk wk-icon-datetime'
      } else if (index === item.activateSteps) {
        return ''
      } else if (index === 4 && item.status === 4) { // 最后一个归档阶段的状态
        return 'wk wk-success'
      }
      return ''
    },

    /**
     * 详情点击
     */
    detailClick() {
      this.rowID = 1
      this.showDview = true
    },

    detailHandle() {},

    /**
     * @description: 通过路由等路由携带的需要处理的事情
     * @return {*}
     */
    initTask() {
      const { query } = this.$route
      if (query.event === 'detail') {
        hrmAppraisalPlanQuerySetting({ appraisalPlanId: query.id })
          .then(res => {
            this.inspectionDetial = res.data
            this.isCreate = true
          })
          .catch(() => {
          })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.performance-index {
  height: 100%;

  .xr-header {
    height: 34px;
    margin-bottom: 20px;

    ::v-deep .xr-header__ft {
      top: 0;
      line-height: 34px;
    }
  }

  .el-dropdown-menu {
    .el-dropdown-title {
      padding: 0 20px 10px;
      color: $--color-text-secondary;
      border-bottom: 1px solid $--border-color-base;
    }

    .el-dropdown-menu__item {
      color: $--color-text-primary;
    }
  }

  .el-dropdown-link {
    cursor: pointer;
  }

  .wk-border-menu {
    margin-bottom: 8px;
  }

  // ::v-deep .el-tabs {
  //   .el-tabs__nav-wrap::after {
  //     display: none;
  //   }

  //   .el-tabs__item {
  //     font-weight: bold;
  //     color: $--color-text-regular;
  //   }
  // }

  .main-body {
    height: calc(100% - 50px);
  }

  .main-list {
    position: relative;
    height: calc(100% - 80px);
    padding: 8px;

    // &__body {
    //   height: calc(100% - 70px);
    //   overflow-y: auto;
    // }

    .border-unit {
      color: $--color-text-primary;
    }

    .custom-scene {
      .label {
        margin-right: 8px;
        margin-left: 24px;
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
    }
  }

  .main-card {
    position: relative;

    ::v-deep .el-card__body {
      padding: 10px 20px;
    }

    &__header {
      font-weight: bold;
      line-height: 45px;

      .header-handle {
        float: right;
      }
    }

    &__separate {
      position: relative;
      height: 80px;

      &::before {
        position: absolute;
        top: 20px;
        bottom: 20px;
        left: 50%;
        width: 1px;
        content: "";
        background-color: $--color-n30;
      }
    }

    &__stop {
      position: absolute;
      right: 60px;
      bottom: 0;
      opacity: 0.6;
    }

    ::v-deep.el-table {
      background: #f9fafc;
      border-radius: 4px;

      .el-table__header {
        margin-top: 8px;
      }

      .el-table__body {
        margin-top: -12px;
        margin-bottom: 5px;
      }

      th {
        background-color: transparent;
        border-bottom-color: transparent;

        .cell {
          font-weight: normal;
          color: $--color-text-regular;
        }
      }

      tr {
        background-color: transparent;
      }

      td {
        background-color: transparent;
        border-bottom-color: transparent;

        .cell {
          font-weight: bold;
        }
      }

      &::before {
        display: none;
      }
    }

    ::v-deep .el-steps {
      padding-left: 30px;
      margin-top: 15px;

      .el-step__head.is-process,
      .el-step__head.is-success {
        color: $--color-primary;
        border-color: $--color-primary;
      }

      .el-step__title {
        margin-left: 10px;
        font-size: 12px;
        text-align: center;
        transform: translateX(-50%);
      }

      .el-step__title.is-process {
        font-weight: normal;
        color: $--color-text-primary;
      }

      .el-step__title.is-finish,
      .el-step__title.is-success {
        color: $--color-text-primary;
      }

      .el-step__line {
        top: 8px;
        right: 7px;
        left: 30px;
        height: 1px;
        overflow: hidden;
      }

      .el-step__icon {
        width: 16px;
        height: 16px;
      }

      .el-step__head.is-success {
        .el-step__line {
          background-color: $--color-primary;
        }
      }

      div[class="el-step__icon-inner"] {
        display: none;
      }
    }
  }

  .main-card + .main-card {
    margin-top: 15px;
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
