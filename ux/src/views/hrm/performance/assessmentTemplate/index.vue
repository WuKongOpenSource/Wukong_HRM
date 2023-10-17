<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: mt mt@5kcrm.com
-->
<template>
  <div id="employee-main-container" class="employee-index">
    <xr-header
      ft-top="0"
      placeholder="请输入模板名称"
      label="考核指标模板"
      show-search
      @search="searchClick">
      <template slot="ft">
        <el-button
          v-if="saveTemplateAuth"
          type="primary"
          @click="createTemplate"
        >新建模板</el-button>
      </template>
    </xr-header>
    <div
      v-empty="!queryPageListAuth"
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
            <template v-else>
              {{ fieldFormatter(scope.row, scope.column) }}
            </template>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              v-if="updateTemplateAuth"
              type="text"
              style="color: #0052cc;"
              @click="editKpiTemplate(scope.row.templateId)">编辑</el-button>
            <el-button
              v-if="delTemplateAuth"
              type="text"
              style="color: #0052cc;"
              @click="deleteKpiTemplate(scope.row.templateId)">删除</el-button>
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

      <kpi-template-create
        v-if="isCreate"
        :indicator-template="indicatorTemplate"
        @createEditSuccess="createEditSuccess"
        @close="isCreate = false" />
    </div>
  </div>
</template>

<script>
import {
  hrmAchievementsAssessmentTemplateAPI,
  delTemplateAPI,
  informationAPI
} from '@/api/hrm/achievementsAssessmentTemplate'

import XrHeader from '@/components/XrHeader/Search'
import KpiTemplateCreate from './Create'
import XrTableHeader from '@/components/XrTableHeader'

import { mapGetters } from 'vuex'
import { isEmpty } from '@/utils/types'
import { employeeModel } from '../../employee/model/employee'

export default {
  name: 'EmployeeIndex',
  components: {
    XrHeader,
    KpiTemplateCreate,
    XrTableHeader
  },
  data() {
    return {
      loading: false, // 加载动画
      isCreate: false, // 是创建
      tableHeight: document.documentElement.clientHeight - 260, // 表的高度
      list: [],
      fieldList: [{
        fieldName: 'templateName',
        name: '模板名称',
        width: 140
      }, {
        fieldName: 'templateIllustrate',
        name: '描述',
        width: 120
      }, {
        fieldName: 'dimensionNum',
        name: '考核维度',
        width: 80
      }, {
        fieldName: 'quotaNum',
        name: '考核指标',
        width: 80
      }, {
        fieldName: 'upperLimitScore',
        name: '总分',
        width: 80
      }, {
        fieldName: 'createUserName',
        name: '创建人',
        width: 80
      }, {
        fieldName: 'updateTime',
        name: '最近更新时间',
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

      // 指标模板详情
      indicatorTemplate: {},
      selectionList: []

    }
  },
  computed: {
    ...mapGetters([
      'collapse', 'hrm'
    ]),
    assessmentTemplateAuth() {
      return this.hrm.appraisalSetting
    },

    // 查看列表权限
    queryPageListAuth() {
      return this.assessmentTemplateAuth && this.assessmentTemplateAuth.queryPageList
    },

    // 新建模板权限
    saveTemplateAuth() {
      return this.assessmentTemplateAuth && this.assessmentTemplateAuth.saveTemplate
    },

    // 编辑权限
    updateTemplateAuth() {
      return this.assessmentTemplateAuth && this.assessmentTemplateAuth.updateTemplate
    },

    // 删除权限
    delTemplateAuth() {
      return this.assessmentTemplateAuth && this.assessmentTemplateAuth.delTemplate
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
    createTemplate() {
      this.indicatorTemplate = {}
      this.isCreate = true
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
        templateName: this.search
      }

      hrmAchievementsAssessmentTemplateAPI(params)
        .then(res => {
          this.list = res.data.list
          this.total = res.data.totalRow
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    // 操作
    tabelHandles() {
      const temps = []
      if (this.delTemplateAuth) {
        temps.push({
          label: '删除',
          command: 'delete',
          icon: 'wk wk-delete'
        })
      }
      return temps
    },

    /**
     * 列表操作
     */
    handleCommand(command) {
      if (command === 'delete') {
        const params = {
          ids: this.selectionList.map(item => item.templateId)
        }
        this.$confirm('确定要删除吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            delTemplateAPI(params)
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
     * 编辑KPI考核模板
     */
    editKpiTemplate(templateId) {
      informationAPI(templateId)
        .then(res => {
          this.indicatorTemplate = res.data
          this.isCreate = true
        })
    },

    /**
     * 删除KPI考核模板
     */
    deleteKpiTemplate(templateId) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          delTemplateAPI({ ids: [templateId] })
            .then(res => {
              this.$message.success('删除成功')
              this.getList()
            })
        })
        .catch(() => {
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
     * 新建或编辑完成
     */
    createEditSuccess() {
      this.getList()
    },

    /**
     * 列表点击
     */
    handleRowClick(row, column, event) {
      if (column.property == 'employeeName') {
        this.rowID = row.employeeId
        this.detailShow = true
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
