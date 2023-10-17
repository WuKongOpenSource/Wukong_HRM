<template>
  <div class="main">
    <xr-header
      label="业务审批流">
      <el-button
        slot="ft"
        type="primary"
        @click="addExamine">新建审批流程</el-button>
    </xr-header>
    <div class="main-body">
      <el-table
        id="examine-table"
        v-loading="loading"
        :data="list"
        :class="WKConfig.tableStyle.class"
        :stripe="WKConfig.tableStyle.stripe"
        :height="tableHeight"
        class="main-table"
        highlight-current-row
        style="width: 100%;">
        <el-table-column
          width="100"
          label="审批流图标">
          <template slot-scope="scope">
            <div
              class="table-icon">
              <i :class="getLableIcon(scope.row.label) " />
            </div>
          </template>
        </el-table-column>
        <el-table-column
          v-for="(item, index) in fieldList"
          :key="index"
          :formatter="fieldFormatter"
          :prop="item.prop"
          :min-width="item.width"
          :label="item.label"
          show-overflow-tooltip />

        <el-table-column
          fixed="right"
          label="操作"
          width="250">
          <template slot-scope="scope">
            <el-button
              type="primary-text"
              style="padding: 0;"
              @click="handleClick('edit', scope)">编辑</el-button>
            <el-button
              type="primary-text"
              style="padding: 0;"
              @click="handleClick('delete', scope)">删除</el-button>
            <el-button
              type="primary-text"
              style="padding: 0;"
              @click="handleClick('change', scope)">{{ scope.row['status'] === 2 ? '启用' : '停用' }}</el-button>
            <el-button
              type="primary-text"
              style="padding: 0;"
              @click="handleClick('copy', scope)">复制并新建</el-button>
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
          background
          layout="prev, pager, next, sizes, total, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </div>
    <business-approve-flow-create
      v-if="createShow"
      ref="create"
      :moduel-type="currentRouteName"
      :detail="rowDetail"
      :is-copy="isCopyCreate"
      :label-list="labelList"
      @success="getList"
      @close="createShow = false"
    />
  </div>
</template>

<script>
import {
  examinesQueryListAPI,
  examinesUpdateStatusAPI
} from '@/api/examine'

import BusinessApproveFlowCreate from './Create'
import XrHeader from '@/components/XrHeader'

export default {
  // 系统管理 的 审核管理
  name: 'SystemExamine',
  components: {
    XrHeader,
    BusinessApproveFlowCreate
  },
  mixins: [],
  data() {
    return {
      loading: false, // 加载动画
      tableHeight: document.documentElement.clientHeight - 220, // 表的高度
      list: [],
      fieldList: [
        {
          prop: 'examineName',
          label: '审批流名称',
          width: 150
        },
        {
          prop: 'label',
          label: '关联对象',
          width: 150
        },
        {
          prop: 'advancedConfigVO',
          label: '审批被拒后重新提交/审批',
          width: 150
        },
        {
          prop: 'updateUserName',
          label: '最后修改人',
          width: 150
        },
        {
          prop: 'updateTime',
          label: '最后修改时间',
          width: 150
        },
        {
          prop: 'status',
          label: '状态',
          width: 100
        }
      ],
      currentPage: 1,
      pageSize: 20,
      pageSizes: [10, 20, 30, 40],
      currentRoute: null,
      total: 0,
      rowDetail: null,
      isCopyCreate: false,
      createShow: false
    }
  },
  computed: {
    currentRouteName() {
      return this.currentRoute ? this.currentRoute.name : ''
    },

    labelList() {
      if (!this.currentRouteName) {
        return []
      }
      return {
        customerExamine: [
          { name: '合同', value: 1 },
          { name: '回款', value: 2 },
          { name: '发票', value: 3 }
        ],
        hrmExamine: [{ name: '薪资', value: 4 }],
        jxcExamine: [
          { name: '采购审批', value: 5 },
          { name: '采购退货审批', value: 6 },
          { name: '销售审批', value: 7 },
          { name: '销售退货审批', value: 8 },
          { name: '付款审批', value: 9 },
          { name: '回款审批', value: 10 },
          { name: '盘点审批', value: 11 },
          { name: '调拨审批', value: 12 }
        ]
      }[this.currentRouteName] || []
    }
  },
  watch: {
    '$route.name': {
      handler() {
        this.currentRoute = this.$route
        this.getList()
      },
      immediate: true
    }
  },
  mounted() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - 220
    }
  },
  methods: {
    /**
     * 获取列表数据
     * customerExamine  hrmExamine  jxcExamine
     * 0是办公审批，1是crm，5是人资，6是进销存
     */
    getList() {
      this.loading = true
      examinesQueryListAPI({
        label: {
          customerExamine: 1,
          hrmExamine: 5,
          jxcExamine: 6
        }[this.currentRouteName],
        page: this.currentPage,
        limit: this.pageSize
      }).then(res => {
        this.list = res.data.list

        this.total = res.data.totalRow
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * 格式化字段
     */
    fieldFormatter(row, column) {
      // 如果需要格式化
      // 1 固定审批 2 授权审批
      if (column.property === 'label') {
        const findRes = this.labelList.find(o => o.value === row.label)
        return findRes ? findRes.name : '--'
      } else if (column.property === 'userList') {
        const structures = row['deptList'] || []
        let strName = structures
          .map(item => {
            return item.name
          })
          .join('、')

        const users = row['userList'] || []
        const userName = users
          .map(item => {
            return item.realname
          })
          .join('、')

        if (strName && userName) {
          strName += '、'
        }
        const name = strName + userName
        return name || '全公司'
        // status    1 正常 2 停用 3 删除
      } else if (column.property === 'status') {
        if (row[column.property] === 2) {
          return '停用'
        }
        return '启用'
      } else if (column.property === 'advancedConfigVO') {
        return {
          2: '从拒绝时指定节点依次处理' // 返回审批流初始层级
        }[row[column.property].rejectHandleType]
      }
      return row[column.property]
    },

    /**
     *  添加审批流
     */
    addExamine() {
      this.isCopyCreate = false
      this.rowDetail = null
      this.createShow = true
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

    handleClick(type, scope) {
      if (type === 'edit') {
        this.isCopyCreate = false
        this.rowDetail = scope.row
        this.createShow = true
      } else if (type === 'delete') {
        // 启用停用
        this.$confirm('您确定要删除该审批流?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            examinesUpdateStatusAPI({
              examineId: scope.row['examineId'],
              status: 3
            }).then(res => {
              this.list.splice(scope.$index, 1)
              if (this.list.length === 0) {
                this.currentPage = this.currentPage - 1 > 0 ? this.currentPage - 1 : 1
              }
              this.getList()
              this.$message({
                type: 'success',
                message: '操作成功'
              })
            }).catch(() => {})
          }).catch(() => {
          })
      } else if (type === 'change') {
        // 启用停用
        this.$confirm(
          '您确定要' +
            (scope.row['status'] === 2 ? '启用' : '停用') +
            '该审批流?',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
          .then(() => {
            examinesUpdateStatusAPI({
              examineId: scope.row['examineId'],
              status: scope.row['status'] === 2 ? 1 : 2
            }).then(res => {
              this.$message({
                type: 'success',
                message: '操作成功'
              })
              this.getList()
            }).catch(() => {})
          }).catch(() => {})
      } else if (type === 'copy') {
        this.isCopyCreate = true
        this.rowDetail = scope.row
        this.createShow = true
      }
    },

    /**
     * 获取label 图标
     */
    getLableIcon(label) {
      return {
        1: 'wk wk-contract',
        2: 'wk wk-receivables',
        3: 'wk wk-invoice',
        4: 'wk wk-icon-pay-solid',
        5: 'wk wk-icon-shop-cart',
        6: 'wk wk-drafts',
        7: 'wk wk-record',
        8: 'wk wk-approval-13',
        9: 'wk wk-icon-category-note',
        10: 'wk wk-l-record',
        11: 'wk wk-icon-search-note',
        12: 'wk wk-approval-11'
      }[label]
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../styles/index.scss";
@import "../styles/table.scss";

.main-body {
  margin-top: #{$--interval-base * 2};
}

.el-button--small {
  padding: 0;
}
</style>
