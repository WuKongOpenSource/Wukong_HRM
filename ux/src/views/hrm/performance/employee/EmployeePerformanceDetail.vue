<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: mt mt@5kcrm.com
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
      direction="column"
      align="stretch"
      style="padding: 0 20px 20px;"
      class="side-detail-main">

      <div class="d-header">
        <div class="header-user">
          <div class="header-user-ft">
            <xr-avatar
              :name="userName"
              :size="40" />
            <div class="user-info">
              <span class="user-info-label">姓名</span>
              <span class="user-info-name">{{ userName }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="d-container-body">
        <detail-head-base :list="headDetails" />
        <div style="padding-top: 15px;">
          <flexbox class="filter-wrap">
            <el-input
              v-model.trim="search"
              placeholder="请输入考核方案"
              class="search-input"
              @keyup.enter.native="searchInput">
              <el-button
                slot="suffix"
                type="icon"
                icon="wk wk-sousuo"
                @click="searchInput" />
            </el-input>
          </flexbox>
          <xr-table-header
            v-if="selectionList.length > 0"
            :handles="tabelHandles()"
            :selects="selectionList"
            @command="handleCommand" />
          <el-table
            id="crm-table"
            :data="list"
            :cell-class-name="cellClassName"
            :class="WKConfig.tableStyle.class"
            :stripe="WKConfig.tableStyle.stripe"
            :height="tableHeight"
            class="n-table--border el-table-header--white"
            highlight-current-row
            style="width: 100%; margin-top: 15px;"
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
              :formatter="fieldFormatter"
              show-overflow-tooltip />
            <el-table-column v-if="delAuth" label="操作">
              <template slot-scope="{row}">
                <el-button
                  type="text"
                  @click="delExaminationRecord(row)">删除</el-button>
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
      </div>
    </flexbox>

    <employee-details
      v-if="detailShow"
      :id="rowId"
      :active-tab="5"
      :appraisal-plan-name="rowAppraisalPlanName"
      :sub-tab-type="1"
      @hide-view="detailShow = false" />
  </slide-view>
</template>

<script>
import {
  hrmEmployeeAchievementFileQueryEmployeeAppraisalListAPI,
  delAppraisalEmployee
} from '@/api/hrm/performance'
import { hrmEmployeeQueryByIdAPI } from '@/api/hrm/employee'

import XrTableHeader from '@/components/XrTableHeader'
import SlideView from '@/components/SlideView'
import DetailHeadBase from '../../components/DetailHeadBase'
import EmployeeDetails from '@/views/hrm/components/EmployeeDetails'

import { isEmpty } from '@/utils/types'
import { mapGetters } from 'vuex'

export default {
  // 绩效档案详情
  name: 'EmployeePerformanceDetail',
  components: {
    SlideView,
    DetailHeadBase,
    EmployeeDetails,
    XrTableHeader
  },
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
        return ['el-table__body']
      }
    },
    queryAppraisalInformation: Boolean // 查看详情权限
  },
  data() {
    return {
      loading: false,
      carouselList: [],
      search: '',
      list: [],
      fieldList: [{
        fieldName: 'appraisalPlanName',
        name: '考核方案名称'
      }, {
        fieldName: 'appraisalCycleType',
        name: '考核周期类型'
      }, {
        fieldName: 'appraisalCycle',
        name: '考核周期'
      }, {
        fieldName: 'stageStatus',
        name: '考核状态'
      }, {
        fieldName: 'score',
        name: '评分'
      }, {
        fieldName: 'level',
        name: '考核结果'
      }],
      tableHeight: '600px',
      headDetails: [],

      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,

      // 查看详情
      rowId: '',
      rowAppraisalPlanName: '',
      detailShow: false,
      userName: '',

      // 考核周期
      appraisalCycleType: {
        1: '月度',
        2: '季度',
        3: '上半年',
        4: '下半年',
        5: '全年'
      },

      // 考核状态
      inspectionStatus: {
        0: '未开始',
        1: '指标填写',
        2: '指标确定',
        3: '自评',
        4: '他评',
        5: '结果审核',
        6: '结果确认',
        7: '申诉确认',
        8: '归档',
        9: '执行中',
        10: '结束'
      },

      selectionList: []
    }
  },
  computed: {
    ...mapGetters(['hrm']),
    delAuth() {
      return this.hrm.appraisalArchives && this.hrm.appraisalArchives.delAppraisalFileRecordList
    }
  },
  watch: {
    id() {
      this.getDetialHeader()
    }
  },
  mounted() {},

  beforeDestroy() {},
  methods: {
    viewAfterEnter() {
      this.getDetialHeader()
    },

    /**
     * 获取头部信息
     */
    getDetialHeader() {
      this.loading = true
      hrmEmployeeQueryByIdAPI(this.id)
        .then(res => {
          console.log(res.data, '哈哈哈哈哈哈')
          const data = res.data || {}
          this.userName = data.employeeName
          const headerDetails = [
            { title: '部门', field: 'deptName', value: '' },
            { title: '岗位', field: 'post', value: '' },
            { title: '聘用形式', field: 'employmentForms', value: '' }
          ]
          headerDetails.forEach(item => {
            if (item.field == 'employmentForms') {
              item.value = data[item.field] == 1 ? '正式' : '非正式'
            } else {
              item.value = data[item.field]
            }
          })

          this.headDetails = headerDetails

          this.getPerformanceList()
        })
    },

    /**
     * 详情
     */
    getPerformanceList() {
      const params = {
        employeeId: this.id,
        page: this.currentPage,
        limit: this.pageSize
      }

      if (this.search) {
        params.appraisalPlanName = this.search
      }

      hrmEmployeeAchievementFileQueryEmployeeAppraisalListAPI(params)
        .then(res => {
          const list = res.data.list || []
          this.list = list
          this.total = res.data.totalRow

          this.loading = false
        })
        .catch(() => {
          this.loading = false
          this.hideView()
        })
    },

    /**
     * 列表操作
     */
    handleCommand(command) {
      if (command === 'delete') {
        const params = {
          ids: this.selectionList.map(item => item.appraisalEmployeeId),
          employeeId: this.selectionList[0].employeeId
        }
        this.$confirm('确定要删除吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            delAppraisalEmployee(params)
              .then(res => {
                this.$message({
                  type: 'success',
                  message: '删除成功'
                })
                this.getPerformanceList()
              })
              .catch(() => {})
          })
          .catch(() => {})
      }
    },

    // 操作
    tabelHandles() {
      const temps = []
      if (this.delAuth) {
        temps.push({
          label: '删除',
          command: 'delete',
          icon: 'wk wk-delete'
        })
      }
      return temps
    },

    /**
     * 更改每页展示数量
     */
    handleSizeChange(val) {
      this.pageSize = val
      this.getPerformanceList()
    },

    /**
     * 勾选操作
     */
    handleSelectionChange(val) {
      this.selectionList = val // 勾选的行
      this.tableHeight = document.documentElement.clientHeight - (this.selectionList.length > 0 ? 395 : 345)
    },

    /**
     * 更改当前页数
     */
    handleCurrentChange(val) {
      this.currentPage = val
      this.getPerformanceList()
    },

    /**
     * 搜索
     */
    searchInput() {
      this.getPerformanceList()
    },

    /**
     * 格式化字段
     */
    fieldFormatter(row, column) {
      if (column.property == 'appraisalCycleType') {
        return this.appraisalCycleType[row[column.property]] ? this.appraisalCycleType[row[column.property]] : '--'
      } else if (column.property == 'stageStatus') {
        return this.inspectionStatus[row[column.property]] ? this.inspectionStatus[row[column.property]] : '--'
      } else {
        const value = row[column.property]
        return isEmpty(value) ? '--' : value
      }
    },

    /**
     * 通过回调控制class
     */
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (column.property == 'appraisalPlanName') {
        return 'can-visit--underline'
      } else {
        return ''
      }
    },

    /**
     * 列表点击
     */
    handleRowClick(row, column, event) {
      if (column.property == 'appraisalPlanName') {
        if (this.queryAppraisalInformation) {
          this.rowId = row.appraisalEmployeeId
          this.rowAppraisalPlanName = row.appraisalPlanName
          this.detailShow = true
        } else {
          this.$message.error('暂无权限')
        }
      }
    },

    /**
     * @description: 删除用户考核记录
     * @param {*} row
     * @return {*}
     */
    delExaminationRecord(row) {
      this.$confirm('确定要删除吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const params = {
          ids: [row.appraisalEmployeeId],
          employeeId: row.employeeId
        }
        delAppraisalEmployee(params)
          .then(res => {
            this.getPerformanceList()
            this.$message({
              type: 'success',
              message: '删除成功!'
            })
          })
      }).catch(() => {
      })
    },

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

.d-header {
  margin-bottom: 10px;

  .header-title {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .title {
      font-size: 24px;
      font-weight: 700;
      text-overflow: ellipsis;
    }
  }

  .header-user {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 32px;

    .header-user-ft {
      display: flex;
      justify-content: space-between;

      .user-info {
        display: flex;
        flex-direction: column;
        margin-right: 16px;

        .user-info-label {
          font-size: 14px;
          color: #6b778c;
        }

        .user-info-name {
          font-size: 20px;
          font-weight: 700;
        }
      }
    }

    .header-user-rt {
      display: flex;
      flex-direction: column;
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

.times-section {
  height: 100%;
  padding: 0 55px;
  text-align: center;

  &__item {
    flex: 1;

    .item-label {
      margin-top: 15px;
      color: $--color-text-regular;
    }

    .item-des {
      margin-top: 8px;
      font-size: 12px;
      color: $--color-text-regular;
    }
  }
}

.times {
  &__line {
    width: 100%;
    height: 1px;
    background-color: #e6e6e6;
  }

  .wk-circle-tag {
    flex-shrink: 0;
  }
}

.table-des {
  padding: 20px;
}
</style>
