<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-27 13:37:31
 * @LastEditTime: 2022-07-06 09:38:23
 * @LastEditors: chen
-->
<template>
  <slide-view
    v-loading="loading"
    :listener-ids="listenerIDs"
    :no-listener-ids="noListenerIDs"
    :no-listener-class="noListenerClass"
    :body-style="{padding: 0, height: '100%', background: 'white'}"
    class="d-view"
    @afterEnter="viewAfterEnter"
    @close="hideView">
    <flexbox
      v-if="detailData"
      direction="column"
      align="stretch"
      style="padding: 0 15px;"
      class="side-detail-main">
      <wk-detail-header
        subtitle="绩效详情"
        :title="detailData.appraisalName"
        @edit="editClick"
        @command-select="commandSelect"
      >
        <template slot="left">
          <template v-if="detail">
            <span v-if="detail.status == 2" class="tip-btn-start">未开始</span>
            <span v-if="detail.status == 3" class="tip-btn-ing">进行中</span>
            <span v-if="detail.status == 4" class="tip-btn-end">已归档</span>
          </template>
        </template>
      </wk-detail-header>
      <div class="d-container-body">
        <detail-head-base :list="headDetails" />
        <div class="top-padding">
          <wk-table-header
            :search.sync="search"
            :props="wkHeaderProps"
            :filter-header-props="wkHeaderProps.filterHeaderProps"
            :filter-form-props="wkHeaderProps.filterFormProps"
            :fields="getFilterFields"
            @event-change="tableHeaderHandle"
            @filter-change="handleFilter">
            <!-- eslint-disable-next-line -->
            <template slot="custom" slot-scope="scope">
              <div class="custom-scene">
                <span>显示：</span>
                <el-button
                  v-for="(item, index) in customScene"
                  :key="index"
                  :title="item.label"
                  :type="item.value === activeScene ? 'selected' : null"
                  :icon="item.icon"
                  @click="sceneClick(item)">{{ item.label }}</el-button>
              </div>
            </template>
          </wk-table-header>
          <el-tabs
            v-model="tabCurrent"
            style="margin-top: 15px;"
            nav-mode="more"
            @tab-click="tabClick"
          >
            <el-tab-pane
              v-for="(tabItem, tabIndex) in tabs"
              :key="tabIndex"
              :label="tabItem.label"
              :name="tabItem.name"
              lazy>
              <template slot="label">
                <el-badge
                  :value="tabItem.num"
                  :hidden="tabItem.num <= 0"
                  type="undefined">
                  {{ tabItem.label }}
                </el-badge>
              </template>
            </el-tab-pane>
          </el-tabs>
          <el-table
            id="crm-table"
            :data="list"
            :height="tableHeight"
            :cell-class-name="cellClassName"
            stripe
            style="width: 100%;"
            @row-click="handleRowClick">
            <el-table-column
              v-for="(item, index) in fieldList"
              :key="index"
              :prop="item.field"
              :label="item.name"
              :min-width="item.width"
              :formatter="fieldFormatter"
              show-overflow-tooltip />
            <!-- <el-table-column /> -->
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
      </div>

      <indicators-detail
        v-if="detailShow"
        :id="rowId"
        :active-tab="3"
        :appraisal-plan-name="rowAppraisalPlanName"
        :sub-tab-type="1"
        @hide-view="detailShow = false" />

    </flexbox>
  </slide-view>
</template>

<script>
import {
  hrmAppraisalEmployeePlanQueryAppraisalListAPI,
  hrmAppraisalEmployeeQueryStageEmployeeNumAPI,
  hrmAppraisalEmployeeQueryScoreEmployeeNumAPI,
  hrmAppraisalEmployeeQueryAppraisalPlanResultLevelNumAPI,
  hrmAppraisalPlanQueryFieldAPI
} from '@/api/hrm/performance'

import SlideView from '@/components/SlideView'
import WkDetailHeader from '@/components/Page/WkDetailHeader'
import DetailHeadBase from '../../components/DetailHeadBase'
import WkTableHeader from '@/components/Page/WkTableHeader'
import IndicatorsDetail from './components/IndicatorsDetail'

import { objDeepCopy } from '@/utils'

export default {
  // 进行中详情
  name: 'OngoingDetail',
  components: {
    SlideView,
    WkDetailHeader,
    DetailHeadBase,
    WkTableHeader,
    IndicatorsDetail
  },

  inject: ['editAuth'],
  props: {
    // 详情信息id
    id: [String, Number],
    // 监听的dom 进行隐藏详情
    listenerIDs: {
      type: Array,
      default: () => {
        return ['crm-main-container']
      }
    },
    // 不监听
    noListenerIDs: {
      type: Array,
      default: () => {
        return []
      }
    },
    noListenerClass: {
      type: Array,
      default: () => {
        return ['el-table__body', 'org-tree-node-label']
      }
    },
    detail: {
      type: Object,
      default: () => {
        return null
      }
    },
    isResult: {
      type: Boolean,
      default: () => {
        return false
      }
    },

    queryEmployeeAppraisalAuth: Boolean
  },
  data() {
    return {
      loading: false,
      detailData: null,
      headDetails: [],
      tabCurrent: 'all',
      // tabs Number
      tabsNumber: {},
      levelTabsData: [],

      // 搜索 场景 高级筛选
      search: '',
      activeScene: 1,
      wkHeaderProps: {
        showFilterView: true,
        filterHeaderProps: {
          searchPlaceholder: '请输入姓名',
          tabSetShow: false
        },
        filterFormProps: {
          showExport: false,
          showSaveScene: false
        }
      },

      // 列表
      list: [],
      fieldList: [{
        field: 'employeeName',
        name: '姓名'
      }, {
        field: 'mobile',
        name: '手机号'
      }, {
        field: 'jobNumber',
        name: '工号'
      }, {
        field: 'deptName',
        name: '部门'
      }, {
        field: 'employmentForms',
        name: '聘用形式'
      }, {
        field: 'stageStatusName',
        name: '考核状态'
      }, {
        field: 'stageUsersName',
        name: '待处理人'
      }, {
        field: 'score',
        name: '评分'
      }, {
        field: 'level',
        name: '结果'
      }],
      tableHeight: '600px',
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      filterObj: { form: [] }, // 筛选确定数据

      // 详情
      detailShow: false,
      rowId: null,
      rowAppraisalPlanName: ''
    }
  },

  computed: {
    // tabs
    tabs() {
      var tempsTabs = []

      if (this.activeScene == 1) {
        tempsTabs = [
          { label: '全部', name: 'all', num: this.tabsNumber.TOTAL },
          // { label: '未开始', name: '0', num: this.tabsNumber.NO_START },
          { label: '目标填写', name: '1', num: this.tabsNumber.FILL },
          { label: '目标确认', name: '2', num: this.tabsNumber.TARGET_CONFIRMATION },
          { label: '执行中', name: '9', num: this.tabsNumber.EXECUTING },
          { label: '自评中', name: '3', num: this.tabsNumber.SELF_SCORE },
          { label: '他人评价', name: '4', num: this.tabsNumber.OTHER_SCORE },
          { label: '结果审核', name: '5', num: this.tabsNumber.RESULT_AUDIT },
          { label: '结果确认', name: '6,7', num: this.tabsNumber.RESULT_CONFIRMATION },
          { label: '已完成', name: '10', num: this.tabsNumber.END }
        ]
      } else if (this.activeScene == 2) {
        tempsTabs = [
          { label: '全部', name: 'all', num: this.tabsNumber.TOTAL }
        ]

        if (this.levelTabsData.length) {
          this.levelTabsData.forEach(item => {
            tempsTabs.push({ label: item.levelName, name: item.levelName, num: item.count })
          })
        }

        tempsTabs.push(
          { label: '未定级', name: 'no', num: this.tabsNumber.UNCLASSIFIED }
        )
      }

      return tempsTabs
    },

    customScene() {
      const arr = [{ label: '考核人员', value: 1 }]
      if (this.queryEmployeeAppraisalAuth) {
        arr.push({ label: '考核结果', value: 2 })
      }
      return arr
    }
  },
  watch: {
    id() {
      this.detailData = null
      this.getDetial()
      this.refreshList()
    }
  },
  mounted() {
    if (this.isResult) {
      this.activeScene = 2
      this.getLevelTabs()
    }
    this.getDetial()
    this.refreshList()
  },

  beforeDestroy() {},
  methods: {
    viewAfterEnter() {
      this.getDetial()
    },

    /**
     * 详情
     */
    getDetial() {
      this.detailData = {
        appraisalName: this.detail && this.detail.appraisalPlanName
      }

      const headDetails = [
        { title: '考核范围', value: '' },
        { title: '考核周期类型', value: '' },
        { title: '考核周期', value: '' },
        { title: '考核人数', value: '' }
      ]
      headDetails[0].value = this.detail && this.detail.inspectionScopeName
      headDetails[1].value = this.detail && {
        1: '月度',
        2: '季度',
        3: '上半年',
        4: '下半年',
        5: '全年',
        6: '其他'
      }[this.detail.appraisalCycleType]
      headDetails[2].value = this.detail && this.detail.appraisalCycle
      headDetails[3].value = this.detail && this.detail.appraisalPersons.toString()

      this.headDetails = headDetails
    },

    /**
     * 获取等级tab及其数量
     */
    getLevelTabs() {
      hrmAppraisalEmployeeQueryAppraisalPlanResultLevelNumAPI({
        id: this.id
      })
        .then(res => {
          this.levelTabsData = res.data || []
        })
        .catch(() => {
        })
    },

    /**
     * 获取tab数量
     */
    getTabsNum() {
      const request = {
        1: hrmAppraisalEmployeeQueryStageEmployeeNumAPI,
        2: hrmAppraisalEmployeeQueryScoreEmployeeNumAPI
      }[this.activeScene]

      if (!request) {
        return
      }

      const params = {}
      params['id'] = this.id

      request(params)
        .then(res => {
          this.tabsNumber = res.data || {}
        })
        .catch(() => {
        })
    },

    /**
     * @description: wk-table-header 筛选字段的获取
     * @param {*}
     * @return {*}
     */
    getFilterFields() {
      return new Promise((resolve) => {
        hrmAppraisalPlanQueryFieldAPI({
          appraisalPlanId: this.id
        }).then(res => {
          const resData = res.data || []
          this.filterFieldList = resData
          resolve(this.filterFieldList)
        }).catch(() => {})
      })
    },

    /**
     * 考核绩效列表
     */
    getAppraisalList() {
      const params = {
        page: this.currentPage,
        limit: this.pageSize,
        searchList: []
      }

      if (this.filterObj && this.filterObj.length > 0) {
        // 滤掉无效值 5 6 为空 不为空 不需要提供values
        const filterObj = this.filterObj ? this.filterObj.filter(item => (item.values && item.values.length > 0) || ([5, 6].includes(item.type))) : []
        if (filterObj.length > 0) {
          params.searchList = objDeepCopy(this.filterObj)
        }
      }

      params.searchList.push({
        type: 1,
        values: [
          this.id
        ],
        formType: 'text',
        name: 'appraisalPlanId'
      })

      if (this.activeScene == 1) {
        if (this.tabCurrent && !['all', '0'].includes(this.tabCurrent)) {
          params.searchList.push({
            type: 1,
            values: this.tabCurrent == '6,7' ? this.tabCurrent.split(',') : [this.tabCurrent],
            formType: 'text',
            name: 'stageStatus'
          })
        }
      } else if (this.activeScene == 2) {
        if (this.tabCurrent && !['all', 'no'].includes(this.tabCurrent)) {
          params.searchList.push({
            type: 1,
            values: [
              this.tabCurrent
            ],
            formType: 'text',
            name: 'level'
          })
        } else if (this.tabCurrent && this.tabCurrent == 'no') {
          params.searchList.push({
            type: 5,
            values: [],
            formType: 'text',
            name: 'level'
          })
        }
      }

      if (this.search) {
        params.employeeName = this.search
      }
      this.loading = true
      hrmAppraisalEmployeePlanQueryAppraisalListAPI(params)
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
     * 更改每页展示数量
     */
    handleSizeChange(val) {
      this.pageSize = val
      this.getAppraisalList()
    },

    /**
     * 更改当前页数
     */
    handleCurrentChange(val) {
      this.currentPage = val
      this.getAppraisalList()
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
     * 筛选操作
     * @param {*} data 高级筛选数据
     */
    handleFilter(data) {
      this.filterObj = data

      this.refreshList()
    },

    /**
     * 场景点击
     */
    sceneClick(item) {
      this.tabCurrent = 'all'
      this.activeScene = item.value
      if (this.activeScene == 2) {
        this.getLevelTabs()
      }
      this.refreshList()
    },

    /**
     * tab点击
     */
    tabClick() {
      this.refreshList()
    },

    /**
     * 刷新
     */
    refreshList() {
      this.tabsNumber = {}
      this.getTabsNum()
      this.handleCurrentChange(1)
    },

    /**
     * 列表格式化
     */
    fieldFormatter(row, column) {
      // console.log(row)
      // 如果需要格式化
      if (column.property == 'employmentForms') {
        return ['', '正式', '非正式'][row.employmentForms]
      }
      return row[column.property] || '—'
    },

    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (column.property === 'employeeName') {
        return 'can-visit--underline'
      } else {
        return ''
      }
    },

    handleRowClick(row, column, event) {
      if (column.property === 'employeeName') {
        this.rowId = row.appraisalEmployeeId
        this.rowAppraisalPlanName = row.employeeName
        this.detailShow = true
      }
    },

    /**
     * 编辑
     */
    editClick() {},

    editSuccess() {
      this.getDetial()
      this.$emit('edit-success')
      this.$emit('handle', { type: 'edit' })
    },

    /**
     * 更多操作
     */
    commandSelect(command) {},

    /**
     * 关闭
     */
    hideView() {
      this.$emit('close')
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/side-detail.scss";

.tip-btn-start,
.tip-btn-ing,
.tip-btn-end {
  width: 42px;
  height: 20px;
  margin-left: 10px;
  font-size: 12px;
  line-height: 20px;
  text-align: center;
  border-radius: 3px;
}

.tip-btn-start {
  color: #fff;
  background-color: #c1c7d0;
}

.tip-btn-ing {
  color: #ffab00;
  background-color: #fffae6;
}

.tip-btn-end {
  color: #f8fcfd;
  background-color: #c3c5d2;
}

.side-detail__tabs--default {
  padding: 0 20px;
}

.wk-detail-header {
  padding: 32px 32px 0;
}

// 详情
.d-container-body {
  flex: 1;
  height: 0;
  padding: 0 32px;
  overflow: scroll;

  .top-padding {
    padding-top: 16px;
  }

  .custom-scene {
    span {
      margin-right: 8px;
      margin-left: 24px;
    }
  }

  .left-right-wrap {
    margin-top: #{$--interval-base * 2};

    &.is-hidden-right {
      >.right {
        display: none;
      }

      >.left {
        padding-right: 0;
      }
    }

    > .left {
      flex: 1;
      padding-right: 40px;
      overflow: hidden;
    }

    > .right {
      flex-shrink: 0;
      width: 280px;
    }
  }

  // ::v-deep .el-tabs__content {
  //   position: relative;
  //   padding: 0;
  //   overflow: hidden;

  //   .el-tab-pane {
  //     overflow: hidden;
  //   }
  // }

  ::v-deep .el-badge {
    .el-badge__content {
      top: 50%;
      right: -4px;
    }
  }
}

// 搜索
::v-deep .search-input {
  width: 220px;

  .el-input__inner {
    padding-right: 36px;
  }
}

.delete-btn {
  padding: 0;
  color: $--button-default-font-color;

  &:hover {
    color: $--color-primary;
  }
}
</style>
