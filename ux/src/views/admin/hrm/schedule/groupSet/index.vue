<template>
  <div v-loading="loading">
    <div class="content-title">
      <span>考勤组设置</span>
      <el-button
        type="primary"
        class="rt"
        @click="addHandleClick">新建考勤组</el-button>
    </div>
    <div class="content-body">
      <el-table
        :data="list"
        :height="tableHeight"
        style="width: 100%;">
        <el-table-column
          v-for="(item,index) in fieldList"
          :key="index"
          :prop="item.prop"
          :label="item.label"
          :width="item.width">
          <template slot-scope="{row,column}">
            <template v-if="column.property=='attendanceDate'">
              <attendanceDate
                :attendance-value="row.attendanceDate" />
            </template>
            <template v-else>
              {{ formatter(row,column) }}
            </template>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="{ row }">
            <el-button
              type="primary-text"
              @click="updateHandler(row)">修改</el-button>
            <el-button
              v-if="delShow(row)"
              type="primary-text"
              @click="deleteHandler(row.attendanceGroupId)">删除</el-button>
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
    <!-- 创建 -->
    <create
      v-if="createShow"
      :id="id"
      :action="createAction"
      @close="createShow = false"
      @save-success="saveSuccess"
    />
  </div>
</template>

<script>
import { hrmQueryAttendanceGroupPageListAPI, hrmAttendanceGroupDeleteAPI } from '@/api/admin/hrm'

import Create from './Create'
import attendanceDate from './components/attendanceDate'

import Lockr from 'lockr'
import { LOCAL_PAGE_SIZE } from '@/utils/constants.js'

export default {

  components: {
    Create,
    attendanceDate
  },
  props: {
    search: String
  },

  data() {
    return {
      createAction: {},
      currentPage: 1,
      pageSizes: [15, 30, 60, 100],
      pageSize: Lockr.get(LOCAL_PAGE_SIZE) || 15,
      total: 0,
      list: [],
      loading: false,
      fieldList: [
        { label: '考勤组名称', prop: 'name' },
        // { label: '考勤类型', prop: '' },
        { label: '出勤时间', prop: 'attendanceDate', width: 300 },
        { label: '考勤规则', prop: 'attendanceRuleName' },
        { label: '适用范围', prop: 'applyScope' }
      ],
      id: '',
      createShow: false,
      tableHeight: document.documentElement.clientHeight - 275// 表的高度

    }
  },

  created() {
    this.getList()
  },

  methods: {
    formatter(row, column) {
      if (column.property == 'applyScope') {
        const { employeeList = [], deptList = [] } = row
        const depEmployeeList = [...employeeList, ...deptList]
        return depEmployeeList.map(item => item.deptName || item.employeeName).join('、')
      } else {
        return row[column.property] || '--'
      }
    },
    getList() {
      this.loading = true
      hrmQueryAttendanceGroupPageListAPI({
        limit: this.pageSize,
        page: this.currentPage,
        name: this.$parent.search
      }).then(res => {
        this.loading = false
        res.data.list.sort((first, second) => {
          if (first.isDefaultSetting === 1 && second.isDefaultSetting === 0) {
            return -1 // a排在b前面
          } else if (first.isDefaultSetting === 0 && second.isDefaultSetting === 1) {
            return 1 // b排在a前面
          } else {
            return 0 // 保持原有顺序
          }
        })

        this.list = res.data.list
        this.total = res.data.totalRow
      }).catch(() => { this.loading = false })
    },
    /**
     * 创建
     */
    addHandleClick() {
      this.id = ''
      this.createAction = {}
      this.createShow = true
    },
    /**
      * 创建回调
     */
    saveSuccess() {
      this.handleRefresh()
    },
    /**
     * 更改每页展示数量
     * @param {*} val
     */
    handleSizeChange(val) {
      Lockr.set(LOCAL_PAGE_SIZE, val)
      this.pageSize = val
      this.getList()
    },
    /**
     * 更改当前页数
     * @param {*} val
     */
    handleCurrentChange(val) {
      this.currentPage = val
      this.getList()
    },
    deleteHandler(attendanceGroupId) {
      this.$confirm('确定删除', '提示', {
        cancelButtonText: '取消',
        confirmButtonText: '确定',
        type: 'warning'
      }).then(res => {
        hrmAttendanceGroupDeleteAPI({
          attendanceGroupId
        }).then(() => {
          this.$message.success('操作成功')
          this.handleRefresh()
        }).catch(() => {})
      }).catch(() => {
        this.$message.info('取消操作')
      })
    },

    /**
     * 修改考勤组
     */
    updateHandler(row) {
      this.createAction = {
        id: row.attendanceGroupId,
        type: 'update',
        data: row
      }
      this.createShow = true
      this.id = row.attendanceGroupId
    },
    /**
     * 刷新
     */
    handleRefresh() {
      this.currentPage = 1
      this.getList()
    },
    delShow(row) {
      if (row.isDefaultSetting) return false
      return true
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.content-title {
  padding: 10px;
  font-size: 20px;
}

.content-title > span {
  display: inline-block;
  height: 36px;
  margin-left: 20px;
  line-height: 36px;
}

.content-body {
  height: calc(100% - 37px);
  padding: 10px 30px;
  overflow-y: auto;
}

.section {
  &__title {
    span {
      color: #999;
    }

    margin-bottom: 10px;
  }

  &__row {
    margin-bottom: 10px;
  }

  .item-section {
    .sub-input::before {
      position: absolute;
      top: 8px;
      left: -25px;
      width: 10px;
      height: 10px;
      content: "";
      border-bottom: 1px solid #e6e6e6;
      border-left: 1px solid #e6e6e6;
    }
  }

  & + & {
    padding-top: 15px;
    margin-top: 15px;
    border-top: 1px solid $--border-color-light;
  }
}

.el-form {
  ::v-deep .el-form-item__content {
    line-height: 34px;
  }
}

.el-col.is-close {
  line-height: 34px;
  text-align: center;

  .el-icon-close {
    font-size: 20px;
    color: #ccc;
    cursor: pointer;

    &:hover {
      color: $--color-primary;
    }
  }
}
</style>
