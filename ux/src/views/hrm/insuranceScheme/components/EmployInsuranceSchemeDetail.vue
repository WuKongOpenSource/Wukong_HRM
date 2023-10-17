<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <slide-view
    v-loading="loading"
    :listener-ids="listenerIDs"
    :no-listener-ids="noListenerIDs"
    :no-listener-class="noListenerClass"
    :body-style="{padding: 0, height: '100%', background: 'white'}"
    class="d-view"
    @afterEnter="afterEnter"
    @close="hideView">
    <flexbox
      v-if="detailData"
      direction="column"
      align="stretch"
      style="padding: 0 15px;"
      class="side-detail-main">
      <wk-detail-header
        :show-edit="canEdit"
        @edit="editClick">
        <div slot="body" class="employee-header">
          <div class="employee-header__top">
            <span class="name">{{ detailData.employeeName }}</span>
            <span class="post">{{ detailData.post }}</span>
          </div>
          <div class="employee-header__bottom">
            <el-tag
              v-if="detailData.sex === 1 || detailData.sex ===2"
              :class="{
                1: 'is-man',
                2: 'is-woman',
              }[detailData.sex]"
              disable-transitions
              class="xr-sex-tag"
              size="mini">
              <i
                :class="{
                  1: 'wk wk-man',
                  2: 'wk wk-woman',
                }[detailData.sex]" />
            </el-tag>
            <el-tag
              disable-transitions
              class="xr-tag"
              color="#ECEEF2"
              size="mini">
              <i class="wk wk-icon-work-card" />{{ detailData.jobNumber|| '--' }}
            </el-tag>
            <el-tag
              disable-transitions
              class="xr-tag"
              color="#ECEEF2"
              size="mini">
              <i class="wk wk-icon-layer" />{{ detailData.deptName || '--' }}
            </el-tag>
            <el-tag
              disable-transitions
              class="xr-tag"
              color="#ECEEF2"
              size="mini">
              <i class="wk wk-icon-time" />{{ getEmployeeStatusName(detailData.status)|| '--' }}
            </el-tag>
            <el-tag
              disable-transitions
              class="xr-tag"
              color="#ECEEF2"
              size="mini">
              <i class="wk wk-icon-status" />{{ detailData.entryTime|| '--' }}
            </el-tag>
            <el-tag
              disable-transitions
              class="xr-tag"
              color="#ECEEF2"
              size="mini">
              <i class="wk wk-icon-cake" />{{ detailData.age || '--' }}
            </el-tag>
          </div>
        </div>
      </wk-detail-header>
      <el-tabs
        class="side-detail__tabs--default">
        <el-tab-pane
          label="社保公积金"
        >
          <div class="tab-pane-content">
            <wk-base-detail-section
              :list="cardList"
            />

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
          </div>
        </el-tab-pane>
      </el-tabs>
    </flexbox>
    <employ-scheme-edit-dialog
      :detail="moneyData"
      :visible.sync="schemeEditDialogShow"
      @change="editChange"
    />

  </slide-view>
</template>

<script>
import { hrmEmployeeQueryByIdAPI } from '@/api/hrm/employee'
import {
  hrmInsuranceMonthEmpRecordQueryAPI
} from '@/api/hrm/insuranceScheme'

import SlideView from '@/components/SlideView'
import WkDetailHeader from '@/components/WkDetailHeader'
import WkBaseDetailSection from '@/components/WkBaseDetail/WkBaseDetailSection'
import EmploySchemeEditDialog from './EmploySchemeEditDialog'

import { employeeModel } from '../../employee/model/employee'
import { timeToFormatTime, floatAdd } from '@/utils'
import { isEmpty } from '@/utils/types'
import insuranceSchemeModel from '@/views/admin/hrm/insuranceScheme/model/insuranceScheme'

export default {
  // 员工社保详情
  name: 'EmployInsuranceSchemeDetail',
  components: {
    WkDetailHeader,
    SlideView,
    WkBaseDetailSection,
    EmploySchemeEditDialog
  },
  mixins: [],
  props: {
    // 有上层传入确定
    canEdit: Boolean,
    // 详情信息id
    id: [String, Number],
    employeeId: [String, Number],
    // 监听的dom 进行隐藏详情
    listenerIDs: {
      type: Array,
      default: () => {
        return ['crm-main-container']
      }
    },
    // 不监听
    noListenerIDs: {
      type: Array,
      default: () => {
        return []
      }
    },
    noListenerClass: {
      type: Array,
      default: () => {
        return ['el-table__body']
      }
    }
  },
  data() {
    return {
      loading: false, // 展示加载loading
      detailData: null,
      cardList: [],
      tabCurrentName: 'EmployeePostInfo',
      moneyData: null,
      moneyList: [],
      schemeEditDialogShow: false
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
  watch: {
    id: function(val) {
      this.getDetail()
    },
    employeeId: function(val) {
      this.detailData = null
      this.getEmployDetail()
    }
  },
  mounted() {},
  methods: {
    /**
     * 动画结束
     */
    afterEnter() {
      this.refreshDetail()
    },

    refreshDetail() {
      this.getDetail()
      this.getEmployDetail()
    },

    /**
     * 员工详情
     */
    getEmployDetail() {
      this.loading = true
      hrmEmployeeQueryByIdAPI(this.employeeId)
        .then(res => {
          this.loading = false
          const data = res.data || {}
          data.entryTime = timeToFormatTime(data.entryTime)

          this.detailData = data
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 社保详情
     */
    getDetail() {
      this.loading = true
      hrmInsuranceMonthEmpRecordQueryAPI(this.id)
        .then(res => {
          this.loading = false
          const data = res.data || {}
          data.iempRecordId = this.id
          this.moneyData = data

          this.cardList = [{
            label: '参保城市',
            value: data.city
          }, {
            label: '身份证号',
            value: data.idNumber
          }, {
            label: '个人社保号',
            value: data.socialSecurityNum
          }, {
            label: '个人公积金号',
            value: data.accumulationFundNum
          }, {
            label: '参保方案',
            value: data.schemeName
          }]

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
     * 员工状态展示值
     */
    getEmployeeStatusName(status) {
      return status ? employeeModel.statusValue[parseInt(status)] : ''
    },

    /**
     * 点击关闭按钮隐藏视图
     */
    hideView() {
      this.$emit('close')
    },

    /**
     * 编辑
     */
    editClick() {
      this.schemeEditDialogShow = true
    },

    /**
     * @description: 编辑完成
     * @return {*}
     */
    editChange() {
      this.$emit('change')
      this.getDetail()
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/side-detail.scss";

.employee-header {
  &__top {
    .name {
      font-size: 20px;
      font-weight: bold;
    }

    .post {
      margin-left: 8px;
      font-size: 12px;
      font-weight: bold;
    }
  }

  &__bottom {
    margin-top: 10px;

    .xr-tag {
      color: $--color-text-regular;
      border: none;

      ::v-deep i {
        margin-right: 5px;
        font-size: 12px;
      }
    }

    .xr-sex-tag {
      border: none;

      ::v-deep i {
        font-size: 12px;
        color: white;
      }

      &.is-man {
        background: #3875ff;
      }

      &.is-woman {
        background: #ff3838;
      }
    }
  }
}

.tab-pane-content {
  height: 100%;
  overflow-y: auto;
}

.side-detail__tabs--default {
  padding: 0 20px;
}
</style>
