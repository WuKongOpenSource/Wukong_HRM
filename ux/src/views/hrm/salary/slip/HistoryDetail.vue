<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <div id="employee-main-container" class="scheme-index">
    <xr-header
      ft-top="0">
      <el-page-header slot="label" :content="`${titleMonth}月发放详情`" @back="goBack" />
      <template slot="ft">
        <el-button v-if="hasFilterContent" type="text" @click="resetFilter">清除筛选</el-button>
        <wk-popover-filter
          :width="popoverFilterWidth"
          :field-from.sync="filterObj"
          :field-list="filterList"
          :has-content="hasFilterContent"
          style="margin-right: 5px;"
          placement="bottom-end"
          @sure="refreshList"
          @reset="resetFilter"
        />
      </template>
    </xr-header>
    <div class="crm-container">
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
        use-virtual
        class="n-table--border el-table-header--white"
        stripe
        highlight-current-row
        style="width: 100%;"
        @selection-change="handleSelectionChange">
        <el-table-column
          v-if="sendAuth"
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
            <template v-if="scope.column.property === 'readStatus'">
              {{ getReadStatus(scope.row[scope.column.property]) }}
            </template>
            <template v-else-if="scope.column.property === 'remarks'">
              <template v-if="scope.row[scope.column.property]">
                {{ scope.row[scope.column.property] }}
              </template>
              <el-button v-else-if="sendAuth" type="text" @click="handleCommand('add', scope.row)">添加</el-button>
            </template>
            <template v-else>
              {{ fieldFormatter(scope.row, scope.column) }}
            </template>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          label="操作"
          width="100">
          <template slot-scope="scope">
            <el-button
              type="text"
              @click.native.prevent="checkDetail(scope.row)">
              查看明细
            </el-button>
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

    <slip-detail-dialog
      :id="rowData.id"
      :visible.sync="detailDialogShow"
    />
  </div>
</template>

<script>
import {
  hrmSalarySlipRecordSendDetailListAPI,
  hrmSalarySlipRecordSetRemarksAPI
} from '@/api/hrm/salary'
import {
  hrmDeptQueryTreeListAPI
} from '@/api/hrm/dept'

import XrHeader from '@/components/XrHeader'
import XrTableHeader from '@/components/XrTableHeader'
import WkPopoverFilter from '@/components/NewCom/WkPopoverFilter'
import SlipDetailDialog from './DetailDialog'

import { mapGetters } from 'vuex'

export default {
  name: 'SalarySlipDetail',
  components: {
    XrHeader,
    XrTableHeader,
    WkPopoverFilter,
    SlipDetailDialog
  },
  data() {
    return {
      loading: false, // 加载动画
      tableHeight: document.documentElement.clientHeight - 210, // 表的高度
      list: [],
      fieldList: [],
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,
      popoverFilterWidth: document.documentElement.clientWidth - 80,
      filterObj: {}, // 筛选确定数据
      selectionList: [], // 勾选数据 用于全局导出
      rowData: {}, // 行详情
      detailDialogShow: false
    }
  },
  computed: {
    ...mapGetters([
      'hrm'
    ]),

    // 权限
    salaryAuth() {
      return this.hrm.salary
    },

    // 发工资条
    sendAuth() {
      return this.salaryAuth && this.salaryAuth.sendSlip
    },

    id() {
      return this.$route.params.id
    },

    titleMonth() {
      return this.$route.params.month
    },

    // 操作
    tabelHandles() {
      const temps = []
      if (this.sendAuth) {
        temps.push({
          label: '编辑备注',
          command: 'edit',
          icon: 'wk wk-circle-edit'
        }, {
          label: '删除备注',
          command: 'delete',
          icon: 'wk wk-delete'
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
          name: '员工姓名/工号',
          field: 'search',
          formType: 'text',
          setting: []
        }, {
          name: '部门',
          field: 'deptId',
          formType: 'structure',
          props: {
            dataType: 'hrm'
          },
          request: hrmDeptQueryTreeListAPI,
          setting: []
        }, {
          name: '查看状态',
          field: 'readStatus',
          formType: 'select',
          setting: [{
            label: '未查看',
            value: 0
          }, {
            label: '已查看',
            value: 1
          }]
        }, {
          name: '备注',
          field: 'remarks',
          formType: 'text',
          setting: []
        }
      ]
    }
  },
  watch: { },
  created() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - (this.selectionList.length > 0 ? 260 : 210)
    }
    this.fieldList = this.getFieldList()
    this.refreshPageData()
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
    },

    /**
     * 高级筛选
     */
    resetFilter() {
      this.filterObj = {}
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
        id: this.id
      }

      for (var key in this.filterObj) {
        params[key] = this.filterObj[key]
      }

      hrmSalarySlipRecordSendDetailListAPI(params)
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
    handleCommand(command, data) {
      if (command === 'delete') {
        this.$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            this.updateRemarks({
              ids: this.selectionList.map(item => item.id),
              remarks: ''
            })
          })
          .catch(() => {
          })
      } else if (command === 'edit') {
        let inputValue = ''
        if (this.selectionList.length === 1) {
          inputValue = this.selectionList[0].remarks
        }
        this.$prompt('', '备注', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputType: 'text',
          inputValue: inputValue,
          inputPlaceholder: '请输入内容',
          closeOnClickModal: false
        }).then(({ value }) => {
          this.updateRemarks({
            ids: this.selectionList.map(item => item.id),
            remarks: value
          })
        }).catch(() => {})
      } else if (command === 'add') {
        this.$prompt('', '备注', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputType: 'text',
          inputValue: '',
          inputPlaceholder: '请输入内容',
          closeOnClickModal: false
        }).then(({ value }) => {
          this.updateRemarks({
            ids: [data.id],
            remarks: value
          })
        }).catch(() => {})
      }
    },

    /**
     * 更新备注
     */
    updateRemarks(data) {
      hrmSalarySlipRecordSetRemarksAPI(data)
        .then(res => {
          this.$message({
            type: 'success',
            message: '操作成功'
          })
          this.getList()
        })
        .catch(() => {})
    },

    /**
     * 勾选操作
     */
    handleSelectionChange(val) {
      this.selectionList = val // 勾选的行
      this.tableHeight = document.documentElement.clientHeight - (this.selectionList.length > 0 ? 260 : 210)
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
        fieldName: 'post',
        name: '岗位',
        width: 180
      }, {
        fieldName: 'mobile',
        name: '手机号码',
        width: 140
      }, {
        fieldName: 'readStatus',
        name: '查看状态',
        width: 80
      }, {
        fieldName: 'remarks',
        name: '备注',
        width: 120
      }]
    },

    /**
     * 查看详情
     */
    checkDetail(data) {
      this.rowData = data
      this.detailDialogShow = true
    },

    /**
     * @description: 获取已读状态
     * @param {*} status
     * @return {*}
     */
    getReadStatus(status) {
      return {
        0: '未查看',
        1: '已查看'
      }[status]
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

::v-deep.el-table {
  .el-table__body .el-table__row td:first-child .cell {
    padding-left: 10px !important;
  }
}
</style>
