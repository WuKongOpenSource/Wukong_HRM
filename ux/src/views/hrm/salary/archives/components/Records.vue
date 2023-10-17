<template>
  <create-sections
    v-loading="loading"
    class="salary-archives-records"
    title="调薪记录">
    <el-table
      :data="list"
      :cell-class-name="cellClassName"
      height="400"
      style="width: 100%;"
      @row-click="handleRowClick">
      <el-table-column
        v-for="(item, index) in fieldList"
        :key="index"
        :prop="item.field"
        :label="item.name"
        show-overflow-tooltip />
      <el-table-column />
    </el-table>
    <change-salary-add
      v-if="changeAddShow"
      :id="id"
      :record-id="recordId"
      @change="getDetail"
      @close="changeAddShow = false"
    />

    <fix-salary-add
      v-if="fixAddShow"
      :id="id"
      :record-id="recordId"
      @change="getDetail"
      @close="fixAddShow = false"
    />
  </create-sections>
</template>

<script>
import {
  hrmSalaryArchivesQueryRecordAPI
} from '@/api/hrm/salary'

import CreateSections from '@/components/CreateSections'
import ChangeSalaryAdd from './ChangeAdd'
import FixSalaryAdd from './FixAdd'

import archivesModel from '../model/archives'

export default {
  // 调薪记录
  name: 'SalaryArchivesRecords',
  components: {
    CreateSections,
    ChangeSalaryAdd,
    FixSalaryAdd
  },
  props: {
    id: [String, Number]
  },
  data() {
    return {
      loading: false,
      total: 0,
      recordId: 0,
      list: [],
      fieldList: [{
        field: 'recordTypeName',
        name: '调整记录',
        width: 80
      }, {
        field: 'changeReasonName',
        name: '调整原因',
        width: 80
      }, {
        field: 'enableDate',
        name: '生效日期',
        width: 80
      }, {
        field: 'statusName',
        name: '生效状态',
        width: 80
      }, {
        field: 'beforeTotal',
        name: '调整前工资',
        width: 80
      }, {
        field: 'afterTotal',
        name: '调整后工资',
        width: 80
      }],
      fixAddShow: false,
      changeAddShow: false
    }
  },

  computed: {},
  watch: {
    id: {
      handler() {
        this.getDetail()
      },
      immediate: true
    }
  },
  mounted() {},
  activated: function() {
  },
  deactivated: function() {
  },
  methods: {
    /**
     * 获取基础信息
     */
    getDetail() {
      this.loading = true
      hrmSalaryArchivesQueryRecordAPI(this.id)
        .then(res => {
          const list = res.data || []
          list.forEach(item => {
            item.recordTypeName = archivesModel.typeValue[item.recordType] + '记录'
            item.statusName = archivesModel.statusValue[item.status]
            item.changeReasonName = archivesModel.changeReasonValue[item.changeReason]
          })
          this.list = list
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 通过回调控制class
     */
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (column.property == 'recordTypeName') {
        return 'can-visit--underline'
      } else {
        return ''
      }
    },

    /**
     * 列表点击
     */
    handleRowClick(row, column, event) {
      if (column.property == 'recordTypeName') {
        this.recordId = row.id
        if (row.recordType == 2) {
          this.changeAddShow = true
        } else {
          this.fixAddShow = true
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.el-table {
  margin-top: 16px;
}
</style>
