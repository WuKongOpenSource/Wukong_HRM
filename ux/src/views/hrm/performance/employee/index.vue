<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: chen
-->
<template>
  <div id="employee-main-container" class="employee-index">
    <xr-header
      ft-top="0"
      placeholder="请输入员工姓名"
      label="绩效档案"
      show-search
      @search="searchClick" />
    <div
      v-empty="!queryAppraisalAuth"
      v-loading="loading"
      xs-empty-icon="nopermission"
      xs-empty-text="暂无权限"
      class="crm-container">
      <xr-table-header
        v-if="selectionList.length > 0"
        :handles="tabelHandles()"
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
        @selection-change="handleSelectionChange"
        @row-click="handleRowClick">
        <el-table-column
          fixed
          type="selection"
          width="55" />
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
            <template v-else-if="scope.column.property === 'status'">
              {{ userStatus[scope.row[scope.column.property]] }}
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
    </div>
    <employee-performance-detail
      v-if="detailShow"
      :id="rowID"
      :query-appraisal-information="queryAppraisalInformation"
      @close="detailShow = false"
    />
  </div>
</template>

<script>
import {
  hrmEmployeeAchievementFileQueryAppraisalFileListAPI,
  delAppraisalFileRecordListOfAll
} from '@/api/hrm/performance'

import XrHeader from '@/components/XrHeader/Search'
// import WkPopoverFilter from '@/components/NewCom/WkPopoverFilter'
import XrTableHeader from '@/components/XrTableHeader'
import EmployeePerformanceDetail from './EmployeePerformanceDetail'

import { mapGetters } from 'vuex'
import { isEmpty } from '@/utils/types'
import { employeeModel } from '../../employee/model/employee'

export default {
  name: 'EmployeeIndex',
  components: {
    XrHeader,
    // WkPopoverFilter,
    EmployeePerformanceDetail,
    XrTableHeader
  },
  data() {
    return {
      loading: false, // 加载动画
      isCreate: false, // 是创建
      tableHeight: document.documentElement.clientHeight - 260, // 表的高度
      list: [],
      fieldList: [{
        fieldName: 'employeeName',
        name: '员工姓名',
        width: 140
      }, {
        fieldName: 'jobNumber',
        name: '工号',
        width: 140
      }, {
        fieldName: 'deptName',
        name: '部门',
        width: 80
      }, {
        fieldName: 'post',
        name: '岗位',
        width: 80
      }, {
        fieldName: 'mobile',
        name: '手机号',
        width: 80
      }, {
        fieldName: 'status',
        name: '员工状态',
        width: 80
      }, {
        fieldName: 'appraisalPlanName',
        name: '最近考核计划',
        width: 120
      }, {
        fieldName: 'recentlyScore',
        name: '最近绩效评分',
        width: 80
      }, {
        fieldName: 'recentlyLevel',
        name: '最近绩效等级',
        width: 80
      }, {
        fieldName: 'appraisalCount',
        name: '考核次数',
        width: 80
      }],
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      search: '', // 搜索内容
      // 筛选宽
      popoverFilterWidth: 150,
      /** 控制详情展示 */
      rowID: '', // 行信息
      detailShow: false,
      // 高级筛选
      filterObj: {}, // 筛选确定数据

      userStatus: {
        1: '正式',
        2: '试用',
        3: '实习',
        4: '兼职',
        5: '劳务',
        6: '顾问',
        7: '返聘',
        8: '外包'
      },
      selectionList: []
    }
  },
  computed: {
    ...mapGetters([
      'collapse', 'hrm'
    ]),

    // 绩效档案列表权限
    queryAppraisalAuth() {
      return this.hrm.appraisalArchives && this.hrm.appraisalArchives.queryAppraisalFileList
    },

    // 查看详情
    queryEmployeeAppraisalAuth() {
      return this.hrm.appraisalArchives && this.hrm.appraisalArchives.queryEmployeeAppraisalList
    },

    // 查看考核计划详情
    queryAppraisalInformation() {
      return this.hrm.appraisalArchives && this.hrm.appraisalArchives.queryAppraisalInformation
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
    collapse: {
      handler(val) {
        this.popoverFilterWidth = document.documentElement.clientWidth - (val ? 89 : 225)
      },
      immediate: true
    }
  },
  created() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - 260
    }
    this.refreshList()
  },
  methods: {
    /**
     * 高级筛选
     */
    resetFilter() {
      this.filterObj = {}
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
      this.handleCurrentChange(1)
    },

    // 操作
    tabelHandles() {
      const temps = []
      temps.push({
        label: '删除',
        command: 'delete',
        icon: 'wk wk-delete'
      })
      return temps
    },

    /**
     * 列表操作
     */
    handleCommand(command) {
      if (command === 'delete') {
        const params = {
          ids: this.selectionList.map(item => item.achievementFileId)
        }
        this.$confirm('确定要删除吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            delAppraisalFileRecordListOfAll(params)
              .then(res => {
                this.$message({
                  type: 'success',
                  message: '删除成功'
                })
                this.refreshList()
              })
              .catch(() => {})
          })
          .catch(() => {})
      }
    },

    /**
     * 勾选操作
     */
    handleSelectionChange(val) {
      this.selectionList = val // 勾选的行
      this.tableHeight = document.documentElement.clientHeight - (this.selectionList.length > 0 ? 395 : 345)
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

      if (this.search) {
        params.employeeName = this.search
      }

      // for (var key in this.filterObj) {
      //   params[key] = this.filterObj[key]
      // }
      hrmEmployeeAchievementFileQueryAppraisalFileListAPI(params)
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
     * 格式化字段
     */
    fieldFormatter(row, column) {
      if (column.property == 'employeeStatus') {
        return employeeModel.statusValue[row.employeeStatus] || '--'
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
        if (this.queryEmployeeAppraisalAuth) {
          this.rowID = row.employeeId
          this.detailShow = true
        } else {
          this.$message.error('暂无权限')
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.employee-index {
  .xr-header {
    margin-bottom: 20px;

    ::v-deep .xr-header__ft {
      top: 0;
      line-height: 34px;
    }
  }

  .search-button {
    border: none;

    &.is-select {
      color: white;
      background: $--color-primary;
    }
  }
}
</style>
