<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: chen
-->
<template>
  <div id="employee-main-container" class="employee-index">
    <xr-header
      ft-top="0"
      placeholder="请输入考核方案名称"
      label="绩效档案"
      show-search
      @search="searchClick" />
    <div class="crm-container">
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
        @row-click="handleRowClick">
        <el-table-column
          v-for="(item, index) in fieldList"
          :key="index"
          :prop="item.fieldName"
          :formatter="fieldFormatter"
          :label="item.name"
          :min-width="item.width"
          show-overflow-tooltip />
        <el-table-column label="操作">
          <template slot-scope="{row}">
            <el-button
              type="text"
              @click="handlerDetail(row)">查看</el-button>
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
      :id="appraisalEmployeeId"
      active-tab="1"
      sub-tab-type="1"
      @hide-view="detailShow = false" />
  </div>
</template>

<script>
import {
  queryMyAppraisalFileList
} from '@/api/hrm/selfService/performance'

import EmployeeDetails from '@/views/hrm/components/EmployeeDetails'
import XrHeader from '@/components/XrHeader/Search'

import { mapGetters } from 'vuex'
import { isEmpty } from '@/utils/types'

export default {
  name: 'MyPerformanceArchives',
  components: {
    XrHeader,
    EmployeeDetails
  },
  data() {
    return {
      loading: false, // 加载动画
      isCreate: false, // 是创建
      tableHeight: document.documentElement.clientHeight - 260, // 表的高度
      list: [],
      fieldList: [{
        fieldName: 'appraisalPlanName',
        name: '考核方案名称',
        width: 140
      }, {
        fieldName: 'appraisalCycleType',
        name: '考核周期类型',
        width: 80
      }, {
        fieldName: 'appraisalCycle',
        name: '考核周期',
        width: 80
      }, {
        fieldName: 'stageStatus',
        name: '考核状态',
        width: 80
      }, {
        fieldName: 'score',
        name: '评分',
        width: 80
      }, {
        fieldName: 'level',
        name: '考核结果',
        width: 80
      }],
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      search: '', // 搜索内容
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

      // 查看详情
      appraisalEmployeeId: '',
      detailShow: false
    }
  },
  computed: {
    ...mapGetters([
      'collapse'
    ])
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
        appraisalPlanName: this.search
      }

      queryMyAppraisalFileList(params)
        .then(res => {
          console.log(res.data, 'hhhh')
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
      if (column.property == 'appraisalCycleType') {
        return this.appraisalCycleType[row[column.property]]
      } else if (column.property == 'stageStatus') {
        return this.inspectionStatus[row[column.property]]
      } else if (column.property == 'appraisalCycle') {
        return row['quarter'] ? row[column.property] + row['quarter'] : row[column.property]
      } else {
        const value = row[column.property]
        return isEmpty(value) ? '--' : value
      }
    },

    /**
     * 列表点击
     */
    handleRowClick(row, column, event) {
      if (column.property == 'employeeName') {
        this.detailShow = true
      }
    },

    handlerDetail(row) {
      this.appraisalEmployeeId = row.appraisalEmployeeId
      this.detailShow = true
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
