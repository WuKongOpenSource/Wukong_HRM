<template>
  <div class="original-table">
    <el-table
      id="crm-table"
      ref="originalTable"
      v-loading="loading"
      :row-height="40"
      :data="list"
      :height="tableHeight"
      row-key="clockId"
      stripe
      border
      highlight-current-row
      style="width: 100%;"
      @selection-change="handleSelectionChange"
      @row-click="rowClick">
      <el-table-column
        key="selection"
        reserve-selection
        type="selection"
        align="center"
      />
      <el-table-column
        v-for="(item, index) in fieldList"
        :key="item.fieldName"
        :fixed="index == 0"
        :prop="item.fieldName"
        :label="item.name"
        :min-width="item.width"
        :formatter="fieldFormatter"
        show-overflow-tooltip />
      <el-table-column
        v-if="recordType==='original'"
        label="操作"
      >
        <template slot-scope="{row}">
          <el-button
            :disabled="row.type != 2"
            type="text"
            @click="updateHandler(row)">修改</el-button>
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
</template>

<script>
import {
  hrmQueryAttendanceClockPageListAPI,
  hrmAttendanceClockDeleteAPI
} from '@/api/hrm/clock'

import Table from '../mixins/Table'
import moment from 'moment'

export default {
  mixins: [Table],
  data() {
    return {
      tableHeight: document.documentElement.clientHeight - 260, // 表的高度
      recordType: 'original',
      request: hrmQueryAttendanceClockPageListAPI,
      disabled: true,
      multipleSelection: []
    }
  },
  methods: {
    /**
     * 选择数据
     */
    handleSelectionChange(val) {
      this.multipleSelection = val
      if (this.multipleSelection.length) {
        this.disabled = this.multipleSelection.some(item => {
          return item.type != 2
        })
      } else {
        this.disabled = true
      }

      this.$emit('selection-change', val)
    },
    /**
     * 修改
     */
    updateHandler(row) {
      this.$parent.showAddDialog = true
      const {
        clockEmployeeId,
        clockType,
        clockTime,
        type,
        attendanceTime,
        clockStage,
        remark,
        clockId } = row
      this.$parent.dialogTitle = '修改打卡记录'
      this.$parent.formFieldList.forEach(item => {
        if (item.formType == 'user') {
          item.disabled = true
        }
      })
      this.$nextTick(() => {
        this.$parent.fieldForm = {
          clockEmployeeId,
          clockType,
          clockTime: moment(clockTime).format('HH:mm'),
          type,
          attendanceTime: moment(attendanceTime).format('YYYY-MM-DD'),
          clockStage,
          remark,
          clockId,
          isUpdate: true
        }
      })
      this.$parent.getSelectTimeClockTime(moment(clockTime).format('HH:mm'))
    },
    /**
     * 删除打卡记录
     */
    delRecord() {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.loading = true
          const clockIds = this.multipleSelection
            .map(item => item.clockId)
          hrmAttendanceClockDeleteAPI(clockIds).then(() => {
            this.loading = false
            this.$message.success('操作成功')
            this.refreshList()
          }).catch(() => {
            this.loading = false
            this.message.error('操作失败')
          })
        })
        .catch(() => {})
    },
    /**
     * 获取表头展示字段
     */
    getFieldList() {
      this.fieldList = [
        { fieldName: 'employeeName', name: '姓名', sortable: '', width: 100 },
        { fieldName: 'jobNumber', name: '工号', sortable: '', width: 100 },
        { fieldName: 'deptName', name: '部门', sortable: '', width: 100 },
        { fieldName: 'post', name: '岗位', sortable: '', width: 100 },
        { fieldName: 'attendanceTime', name: '上班日期', sortable: '', width: 100 },
        { fieldName: 'clockType', name: '打卡类型', sortable: '', width: 100 },
        { fieldName: 'clockTime', name: '打卡时间', sortable: '', width: 100 },
        { fieldName: 'address', name: '打卡地点', sortable: '', width: 100 },
        { fieldName: 'type', name: '打卡来源', sortable: '', width: 100 },
        { fieldName: 'remark', name: '备注', sortable: '', width: 100 }
      ]
      this.getList()
    }
  }
}
</script>

<style>

</style>
