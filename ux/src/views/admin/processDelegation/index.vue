<template>
  <flexbox
    class="main"
    direction="column"
    align="stretch">
    <xr-header
      label="流程委托">
      <template slot="otherLabel">
        <el-tooltip
          effect="dark"
          placement="top"
          content="流程委托是指将某一时间段内某位员工需要处理的流程，委托给另一个员工处理。">
          <i class="wk wk-help wk-help-tips" />
        </el-tooltip>
      </template>
      <el-button
        v-if="addAuth"
        slot="ft"
        type="primary"
        @click="addClick">新建委托</el-button>
    </xr-header>
    <div class="main-body">
      <flexbox class="content-table-header">
        <el-select
          v-model="status"
          @change="refreshList">
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value" />
        </el-select>
      </flexbox>
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
          v-for="(item, index) in fieldList"
          :key="index"
          :formatter="fieldFormatter"
          :prop="item.prop"
          :min-width="item.width"
          :label="item.label"
          show-overflow-tooltip />
        <el-table-column
          v-if="editAuth || changeStateAuth"
          fixed="right"
          label="操作"
          width="250">
          <template slot-scope="scope">
            <el-button
              v-if="editAuth"
              :disabled="commissionTime(scope.row)"
              type="primary-text"
              style="padding: 0;"
              @click="handleClick('edit', scope)">编辑</el-button>
            <el-button
              v-if="changeStateAuth"
              type="primary-text"
              style="padding: 0;"
              @click="handleClick('state', scope)">{{ scope.row['state'] === 0 ? '启用' : '停用' }}</el-button>
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

    <!-- 新建委托 -->
    <process-delegation-create
      v-if="createViewVisible"
      :data="editData"
      @success="refreshList"
      @close="createViewVisible = false" />
  </flexbox>
</template>

<script>
import {
  examineDelegateQueryPageListAPI,
  examineDelegateSetDelegateStateAPI
} from '@/api/admin/processDelegation'

import XrHeader from '@/components/XrHeader'
import ProcessDelegationCreate from './Create'

import moment from 'moment'
import { mapGetters } from 'vuex'

export default {
  name: 'ProcessDelegation',
  components: {
    XrHeader,
    ProcessDelegationCreate
  },
  data() {
    return {
      loading: false, // 加载动画
      tableHeight: document.documentElement.clientHeight - 250, // 表的高度
      status: '',
      statusOptions: [{ label: '全部', value: '' }, { label: '启用', value: 1 }, { label: '停用', value: 0 }],
      list: [],
      fieldList: [
        {
          prop: 'delegateUser',
          label: '委托人',
          width: 150
        },
        {
          prop: 'number',
          label: '委托编号',
          width: 150
        },
        {
          prop: 'trusteeUser',
          label: '受委托人',
          width: 150
        },
        {
          prop: 'labels',
          label: '业务模块',
          width: 150
        },
        {
          prop: 'startTime',
          label: '委托开始时间',
          width: 150
        },
        {
          prop: 'endTime',
          label: '委托结束时间',
          width: 150
        },
        {
          prop: 'remarks',
          label: '备注',
          width: 150
        },
        {
          prop: 'state',
          label: '状态',
          width: 150
        },
        {
          prop: 'createUser',
          label: '创建人',
          width: 150
        },
        {
          prop: 'createTime',
          label: '创建时间',
          width: 150
        },
        {
          prop: 'updateTime',
          label: '最后修改时间',
          width: 150
        }
      ],
      currentPage: 1,
      pageSize: 10,
      pageSizes: [10, 20, 30, 40],
      total: 0,
      // 新建
      editData: null,
      createViewVisible: false,

      labels: {
        0: '办公审批',
        1: '合同',
        2: '回款',
        3: '发票',
        4: '薪资',
        5: '采购审核',
        6: '采购退货审核',
        7: '销售审核',
        8: '销售退货审核',
        9: '付款单审核',
        10: '回款单审核',
        11: '盘点审核',
        12: '调拨审核',
        20: '阶段审批'
      }
    }
  },

  computed: {
    ...mapGetters(['manage']),

    // 新建权限
    addAuth() {
      return this.manage.delegate && this.manage.delegate.addDelegate
    },
    // 编辑权限
    editAuth() {
      return this.manage.delegate && this.manage.delegate.updateDelegate
    },
    // 修改状态权限
    changeStateAuth() {
      return this.manage.delegate && this.manage.delegate.updateState
    },

    // 委托时间开始不能编辑 并且是开启状态
    commissionTime() {
      return (row) => {
        const currentTime = moment(new Date())
        return row.state == 1 && moment(row.startTime).isBefore(currentTime)
      }
    }
  },

  mounted() {
    // 控制table的高度
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - 250
    }

    this.getList()
  },

  methods: {
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

    refreshList() {
      this.handleCurrentChange(1)
    },

    /**
     * 数据获取
     */
    getList() {
      this.loading = true
      examineDelegateQueryPageListAPI({
        state: this.status,
        page: this.currentPage,
        limit: this.pageSize
      })
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
      if (column.property === 'labels') {
        const labelList = row[column.property].split(',')
        return labelList.map(item => this.labels[item]).join()
      } else if (column.property === 'state') {
        return row[column.property] == 1 ? '启用' : '停用'
      }
      return row[column.property] || '--'
    },

    /**
     * 新建
     */
    addClick() {
      this.editData = null
      this.createViewVisible = true
    },

    /**
     * @description: 列表操作
     * @param {*} type
     * @param {*} scope
     * @return {*}
     */
    handleClick(type, scope) {
      if (type === 'edit') {
        this.editData = scope.row
        this.createViewVisible = true
      } else if (type === 'state') {
        // 启用 停用
        this.$confirm(`您确定要${scope.row.state === 0 ? '启用' : '停用'}该流程委托?`,
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
          .then(() => {
            examineDelegateSetDelegateStateAPI({
              delegateId: scope.row.delegateId,
              state: scope.row.state === 0 ? 1 : 0
            })
              .then(res => {
                this.$message({
                  type: 'success',
                  message: '操作成功'
                })
                this.getList()
              })
              .catch(() => {})
          })
          .catch(() => {})
      }
    }
  }
}
</script>

  <style lang="scss" scoped>
  @import "../styles/index.scss";
  @import "../styles/table.scss";

  .main-body {
    margin-top: #{$--interval-base * 2};

    .content-table-header {
      padding-bottom: 16px;
    }
  }

  .el-button--small {
    padding: 0;
  }

  .wk-help-tips {
    margin-left: 4px;
  }
  </style>

