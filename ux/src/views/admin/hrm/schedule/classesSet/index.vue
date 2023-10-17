<template>
  <div v-loading="loading">
    <div class="content-title">
      <span>班次设置</span>
      <el-button
        type="primary"
        class="rt"
        @click="addClasses">新建班次</el-button>
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
        >
          <template slot-scope="{row ,column}">
            <template v-if="item.prop=='shiftType' ">
              <span>{{ row[item.prop] | shiftType }}</span>
            </template>
            <template v-else-if="item.prop=='shiftTime'">
              <p v-for=" range in getShiftTime(row).timeRange" :key="range">
                {{ range }}
              </p>
              <p v-if="row.restTimeStatus">休息:{{ getShiftTime(row).restTime }}</p>
            </template>
            <template v-else>
              <span>{{ formatter(row ,column ,row[column.property]) }}</span>
            </template>
          </template>
        </el-table-column>
        <el-table-column
          label="操作">
          <template slot-scope="{row}">
            <el-button
              v-if="row.shiftName!=='休息'"
              type="primary-text"
              @click="updateHandler(row)">修改</el-button>
            <el-button
              v-if="isDelShow(row)"
              type="primary-text"
              @click="deleteHandler(row.shiftId)"
            >删除</el-button>
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
      :data-value="dataValue"
      @close="createShow = false"
      @save-success="saveSuccess"
    />
  </div>
</template>

<script>

import {
  hrmQueryAttendanceShiftPageListAPI,
  hrmAttendanceShiftDeleteAPI
} from '@/api/admin/hrm'

import Create from './Create'

import Lockr from 'lockr'
import { LOCAL_PAGE_SIZE } from '@/utils/constants.js'

export default {
  filters: {
    shiftType(val) {
      return { 1: '早晚打卡', 2: '分段打卡' }[val]
    }
  },
  components: {
    Create
  },

  data() {
    return {
      currentPage: 1,
      pageSizes: [15, 30, 60, 100],
      pageSize: Lockr.get(LOCAL_PAGE_SIZE) || 15,
      total: 0,
      list: [],
      otherParams: {}, // 查询附加条件
      loading: false,
      id: '',
      dataValue: {},
      fieldList: [
        { label: '班次名称', prop: 'shiftName' },
        { label: '打卡类型', prop: 'shiftType' },
        { label: '出勤时段', prop: 'shiftTime' },
        { label: '出勤时长', prop: 'shiftHours' }
      ],
      createShow: false,
      tableHeight: document.documentElement.clientHeight - 275// 表的高度
    }
  },

  created() {
    this.getList()
  },

  methods: {
    formatter(row, column, cellValue) {
      // 如果需要格式化
      if (column.property === 'shiftHours') {
        if (!row[column.property]) return '--'
        const totalMinute = row[column.property]
        const minute = totalMinute % 60 > 9 ? totalMinute % 60 : '0' + totalMinute % 60
        return Math.floor(totalMinute / 60) + '小时' + minute + '分钟'
      } else {
        return row[column.property] || '--'
      }
    },
    addClasses() {
      this.id = ''
      this.createShow = true
    },
    deleteHandler(id) {
      this.$confirm('确定删除!', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(res => {
        hrmAttendanceShiftDeleteAPI({
          attendanceShiftId: id
        }).then(result => {
          this.$message.success('删除成功')
          this.handleRefresh()
        })
      }).catch(() => {
        this.$message.info('取消操作')
      })
    },
    saveSuccess() {
      this.handleRefresh()
    },
    getShiftTime(row) {
      const obj = {}
      if (row.restEndTime && row.restEndTime != null) {
        obj.restTime = `${row.restStartTime}-${row.restEndTime}`
      }
      if (row.shiftType == 1) {
        // 早晚打卡
        obj.timeRange = [`${row.start1}-${row.end1}`]
      } else {
        // 分段打卡
        const arr = []
        for (let index = 1; index <= 3; index++) {
          if (row['start' + index] && row['start' + index] != null) {
            arr.push(`${row['start' + index]}-${row['end' + index]}`)
          }
        }
        obj.timeRange = arr
      }
      return obj
    },
    updateHandler(row) {
      this.id = row.shiftId
      this.dataValue = row
      this.createShow = true
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
    /**
     * 刷新
     */
    handleRefresh() {
      this.currentPage = 1
      this.getList()
    },
    /**
     * 获取列表数据
     */
    getList() {
      this.loading = true
      hrmQueryAttendanceShiftPageListAPI({
        limit: this.pageSize,
        page: this.currentPage,
        shiftName: this.$parent.search
      }).then(res => {
        this.loading = false
        console.log(res.data)
        this.list = res.data.list.filter(item => ![0].includes(item.shiftType))
        this.total = res.data.totalRow
      }).catch(() => {
        this.loading = false
      })
    },
    isDelShow(row) {
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
