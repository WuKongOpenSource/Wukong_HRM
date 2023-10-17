<template>
  <el-dialog
    :visible="visible"
    :before-close="handleClose"
    :title="title"
    width="50%">
    <el-table
      :data="moneyList"
      :summary-method="getSummaries"
      show-summary
      height="400"
      align="center">
      <el-table-column
        v-for="(item, index) in moneyTableFields"
        :key="index"
        :prop="item.prop"
        :label="item.label"
        :formatter="fieldFormatter" />
    </el-table>
  </el-dialog>
</template>

<script>
import {
  hrmInsuranceMonthEmpRecordMyDetailAPI
} from '@/api/hrm/insuranceScheme'

import { floatAdd } from '@/utils'
import { isEmpty } from '@/utils/types'
import insuranceSchemeModel from '@/views/admin/hrm/insuranceScheme/model/insuranceScheme'

export default {
  // 详情
  name: 'MyInsuranceSchemeDetail',

  components: {},

  props: {
    visible: {
      type: Boolean,
      default: false
    },
    title: String,
    id: [Number, String]
  },

  data() {
    return {
      loading: false,
      fieldList: [{
        prop: 'customerName',
        width: '200',
        label: '缴纳项目'
      }, {
        prop: 'customerName',
        width: '200',
        label: '个人缴纳'
      }, {
        prop: 'customerName',
        width: '200',
        label: '企业缴纳'
      }, {
        prop: 'customerName',
        width: '200',
        label: '合计缴纳'
      }],
      moneyData: null,
      moneyList: []
    }
  },

  computed: {
    moneyTableFields() {
      let tempList = [{
        label: '缴纳项目',
        prop: 'type'
      }, {
        label: '缴纳基数',
        prop: 'defaultAmount'
      }]
      if (this.moneyData && this.moneyData.schemeType == 1) {
        tempList = tempList.concat([{
          label: '企业比例',
          prop: 'corporateProportion'
        }, {
          label: '个人比例',
          prop: 'personalProportion'
        }])
      }

      return tempList.concat([{
        label: '个人缴纳',
        prop: 'personalAmount'
      }, {
        label: '企业缴纳',
        prop: 'corporateAmount'
      }, {
        label: '合计缴费',
        prop: 'allAmount'
      }])
    }
  },

  watch: {},

  created() {
    this.getDetail()
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 获取详情
     */
    getDetail() {
      this.loading = true
      hrmInsuranceMonthEmpRecordMyDetailAPI(this.id)
        .then(res => {
          this.loading = false
          const data = res.data || {}
          this.moneyData = data
          const moneyList = data.socialSecurityList.concat(data.providentFundList)
          moneyList.forEach(item => {
            item.allAmount = floatAdd(item.personalAmount, item.corporateAmount)
          })
          this.moneyList = moneyList
        })
        .catch(() => {
          this.loading = false
        })
    },

    getSummaries(param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = '缴费总价'
          return
        } else if (['personalProportion', 'corporateProportion', 'defaultAmount'].includes(column.property) || !column.property) {
          sums[index] = ''
          return
        }
        const values = data.map(item => Number(item[column.property]))
        if (!values.every(value => isNaN(value))) {
          sums[index] = values.reduce((prev, curr) => {
            const value = Number(curr)
            if (!isNaN(value)) {
              return floatAdd(prev, curr)
            } else {
              return prev
            }
          }, 0)
        } else {
          sums[index] = 'N/A'
        }
      })

      return sums
    },

    /**
     * 格式化字段
     */
    fieldFormatter(row, column) {
      const value = row[column.property]
      if (column.property == 'type') {
        if (value < 9) {
          return insuranceSchemeModel.providentFundTypeValue[value]
        } else if (value == 9 || value == 11) {
          return row.projectName
        }

        return insuranceSchemeModel.socialSecurityTypeValue[value]
      }

      return isEmpty(value) ? '--' : value
    },

    handleClose() {
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
