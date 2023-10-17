<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <el-dialog
    ref="wkDialog"
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    :title="title"
    width="700px"
    @close="close">
    <el-table
      :data="list"
      height="400px"
      class="el-table-header--white"
      stripe>
      <el-table-column
        v-for="(item, index) in fieldList"
        :key="index"
        :prop="item.prop"
        :label="item.label"
        :formatter="fieldFormatter"
        show-overflow-tooltip />
    </el-table>
  </el-dialog>
</template>

<script>
import {
  hrmSalaryMonthRecordQueryNoEmployeeAPI
} from '@/api/hrm/salary'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import { employeeModel } from '../../employee/model/employee'

export default {
  // 不计薪资的员工列表
  name: 'NoEmployeeDialog',
  components: {
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      title: '无薪资组员工列表',
      list: [],
      fieldList: [
        { prop: 'employeeName', width: '200', label: '姓名' },
        { prop: 'jobNumber', width: '200', label: '工号' },
        { prop: 'deptName', width: '200', label: '部门' },
        { prop: 'post', width: '200', label: '岗位' },
        { prop: 'status', width: '200', label: '员工状态' },
        { prop: 'entryTime', width: '200', label: '入职时间' }
      ]
    }
  },
  computed: {
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.getDetail()
        }
      },
      immediate: true
    }
  },
  mounted() {},
  methods: {

    /**
     * 关闭
     */
    close() {
      this.$emit('update:visible', false)
    },

    /**
     * 格式化字段
     */
    fieldFormatter(row, column) {
      const valueObj = employeeModel[`${column.property}Value`]
      if (valueObj) {
        return valueObj[row[column.property]] || '--'
      } else {
        return row[column.property] || '--'
      }
    },

    /**
     * 获取信息
     */
    getDetail() {
      this.loading = true

      hrmSalaryMonthRecordQueryNoEmployeeAPI()
        .then(res => {
          this.list = res.data || []
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    }
  }
}
</script>
<style lang="scss" scoped>
</style>
