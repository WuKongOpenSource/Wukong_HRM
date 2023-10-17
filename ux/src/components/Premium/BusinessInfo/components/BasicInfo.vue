<template>
  <div v-loading="loading" class="basic-info">
    <div class="basic-info__header">
      <el-button
        v-for="(item, index) in tabs"
        :key="index"
        :title="item.label"
        :type="item.value === activeTab ? 'selected' : null"
        @click="tabsClick(item)">{{ item.label }}</el-button>
    </div>
    <div
      v-for="(item, index) in tabs"
      :key="index">
      <wk-head-section
        v-if="item.value === activeTab"
        class="basic-info__body"
        :label="item.label"
        :props="{
          headBgColor: '#FAFBFC',
          arrows: 'left',
          border: false,
          bodyPadding: '16px 0 0'
        }">
        <table v-if="activeTab === 'base'" class="base-table">
          <tbody>
            <tr
              v-for="(bItem, bIndex) in baseList"
              :key="bIndex">
              <td>{{ bItem.label }}</td>
              <td class="w-292">{{ getBaseInfoValue(bItem.field) }}</td>
              <td>{{ bItem.label1 }}</td>
              <td class="w-292">{{ bItem.field1 ? getBaseInfoValue(bItem.field1) : '' }}</td>
            </tr>
            <tr><td>注册地址</td><td colspan="3">{{ getBaseInfoValue('address') }}</td></tr>
            <tr><td>经营范围</td><td colspan="3">{{ getBaseInfoValue('scope') }}</td></tr>
          </tbody>
        </table>
        <el-table
          v-else
          :data="relativeObj[activeTab]"
          height="400"
          stripe
          highlight-current-row
          style="width: 100%;">
          <el-table-column
            label="序号"
            width="50"
            align="center"
            header-align="center"
            show-overflow-tooltip>
            <template slot-scope="scope">
              {{ scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column
            v-for="(rItem, rIndex) in relativeFieldObj[activeTab]"
            :key="rIndex"
            :prop="rItem.field"
            :label="rItem.label"
            :width="rItem.width"
            show-overflow-tooltip />
        </el-table>
      </wk-head-section>
    </div>
  </div>
</template>

<script>
import {
  crmEnterpriseEmployeesAPI, // 查询主要人员信息
  crmEnterprisePartnersAPI, // 查询股东信息
  crmEnterpriseBranchsAPI, // 查询分支机构信息
  crmEnterpriseInvestmentAPI, // 查询对外投资信息
  crmEnterpriseChangeRecordsAPI // 查询企业信息变更记录
} from '@/api/premium/businessInfo'

import WkHeadSection from '@/components/NewCom/WkHeadSection'

export default {
  // 基本信息
  name: 'BasicInfo',

  components: {
    WkHeadSection
  },

  props: {
    name: String,
    // 基本详情
    data: Object
  },

  data() {
    return {
      loading: false,
      activeTab: 'base',
      tabs: [{
        label: '工商信息',
        value: 'base'
      }, {
        label: '主要人员',
        value: 'employees'
      }, {
        label: '股东信息',
        value: 'partners'
      }, {
        label: '分支机构',
        value: 'branchs'
      }, {
        label: '对外投资',
        value: 'investment'
      }, {
        label: '变更记录',
        value: 'changeRecords'
      }],
      baseList: [],
      // 其他表格形式详情
      relativeObj: {
        employees: [],
        partners: [],
        branchs: [],
        investment: [],
        changeRecords: []
      },
      relativeFieldObj: {}
    }
  },

  computed: {},

  watch: {},

  created() {
    this.baseList = this.getBaseInfo()
    this.relativeFieldObj = this.getDetailFields()
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * @description: tabs 切换
     * @param {*} item tab 对象
     * @return {*}
     */
    tabsClick(item) {
      this.activeTab = item.value
      this.getDetail()
    },

    /**
     * @description: 基础信息展示字段
     * @param {*}
     * @return {*}
     */
    getBaseInfo() {
      const list = [{
        field: 'operName',
        label: '法定代表人'
      }, {
        field: 'newStatus',
        label: '经营状态'
      }, {
        field: 'registCapi',
        label: '注册资本'
      }, {
        field: 'actualCapi',
        label: '实缴资本'
      }, {
        field: 'creditNo',
        label: '统一社会信用代码'
      }, {
        field: 'regNo',
        label: '工商注册号'
      }, {
        field: 'orgNo',
        label: '组织机构代码'
      }, {
        field: 'startDate',
        label: '成立日期'
      }, {
        field: 'econKind',
        label: '企业类型'
      }, {
        field: 'term',
        label: '营业期限' // termStart termEnd
      }, {
        field: 'belongOrg',
        label: '登记机关'
      }, {
        field: 'checkDate',
        label: '核准日期'
      }]

      const trNum = Math.ceil(list.length / 2)
      const data = []
      for (let index = 0; index < trNum; index++) {
        const firstIndex = index * 2
        const nextIndex = firstIndex + 1
        const firstObj = list[firstIndex]
        const nextObj = list[nextIndex]
        data.push({
          ...firstObj,
          field1: nextObj ? nextObj.field : '',
          label1: nextObj ? nextObj.label : ''
        })
      }
      console.log(data)
      return data
    },

    /**
     * @description: 基础信息展示值
     * @param {*}
     * @return {*}
     */
    getBaseInfoValue(field) {
      const placeholder = '--'
      if (field === 'term') {
        const list = []
        const termStart = this.data.termStart ? this.data.termStart.split(' ')[0] : ''
        if (termStart) {
          list.push(termStart)
        }
        const termEnd = this.data.termEnd ? this.data.termEnd.split(' ')[0] : ''
        if (termEnd) {
          list.push(termEnd)
        }
        return list.join('-') || placeholder
      } else if (field === 'startDate' || field === 'checkDate') {
        return this.data[field] ? this.data[field].split(' ')[0] : placeholder
      }

      return this.data[field] || placeholder
    },

    /**
     * @description: 获取表格详情
     * @param {*}
     * @return {*}
     */
    getDetail() {
      const request = {
        employees: crmEnterpriseEmployeesAPI,
        partners: crmEnterprisePartnersAPI,
        branchs: crmEnterpriseBranchsAPI,
        investment: crmEnterpriseInvestmentAPI,
        changeRecords: crmEnterpriseChangeRecordsAPI
      }[this.activeTab]
      if (request) {
        const activeTab = this.activeTab
        this.loading = true
        request(this.name).then(res => {
          this.relativeObj[activeTab] = res.data || []
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
      }
    },

    /**
     * @description: 获取表格详情头部
     * @param {*}
     * @return {*}
     */
    getDetailFields() {
      const employees = [{
        label: '姓名',
        field: 'name'
      }, {
        label: '职务',
        field: 'title'
      }]
      const partners = [{
        label: '发起人/股东',
        field: 'name'
      }, {
        label: '持股比例',
        field: 'percent'
      }, {
        label: '股东类型',
        field: 'shType'
      }, {
        label: '投资金额(万元)',
        field: 'amount'
      }]
      const branchs = [{
        label: '公司名称',
        field: 'name'
      }, {
        label: '负责人',
        field: 'operName',
        width: '120px'
      }, {
        label: '成立时间',
        field: 'startDate',
        width: '120px'
      }, {
        label: '经营状态',
        field: 'status',
        width: '240px'
      }]
      const investment = [{
        label: '被投资企业',
        field: 'shortName'
      }, {
        label: '股东类型',
        field: 'shType'
      }, {
        label: '持股比例',
        field: 'percent',
        width: '120px'
      }, {
        label: '投资金额(万元)',
        field: 'amount',
        width: '120px'
      }]
      const changeRecords = [{
        label: '变更日期',
        field: 'changeDate',
        width: '120px'
      }, {
        label: '变更事项',
        field: 'changeItem',
        width: '220px'
      }, {
        label: '变更前',
        field: 'beforeContent'
      }, {
        label: '变更后',
        field: 'afterContent'
      }]

      return {
        employees,
        partners,
        branchs,
        investment,
        changeRecords
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.basic-info {
  position: relative;

  &__body {
    margin-top: 16px;
  }
}

.base-table {
  border-spacing: 0;
  border-collapse: collapse;
  border: $--border-base;

  tr {
    td:nth-child(odd) {
      width: 130px;
      background: $--background-color-base;
    }

    .w-292 {
      width: 292px;
    }
  }

  td {
    padding: 8px;
    font-weight: $--font-weight-primary;
    line-height: 1.57;
    word-break: break-all;
    vertical-align: middle;
    border: $--border-base;
  }
}
</style>
