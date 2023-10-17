<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <div v-loading="loading" class="wrapper">
    <xr-header
      ft-top="0"
      label="社保管理">
      <template slot="ft">
        <el-date-picker
          v-model="year"
          :picker-options="pickerOptions"
          :clearable="false"
          style="width: 100px;"
          type="year"
          format="yyyy"
          value-format="yyyy"
          placeholder="选择日期"
          @change="getList" />
      </template>
    </xr-header>
    <div
      v-empty="!list || list.length == 0"
      class="wrapper-main">
      <statistics-card
        v-for="(item, index) in list"
        :key="index"
        :delete-button-show="false"
        :title="`${item.month}月社保表`"
        :data="item"
        :field-list="fieldList"
        button-show
        @detail-click="detailCheckClick"
      />
    </div>

    <my-insurance-scheme-detail
      v-if="detailVisible"
      :id="rowDetail.iempRecordId"
      :title="`${rowDetail.month}月社保表`"
      :visible.sync="detailVisible"
    />
  </div>
</template>

<script>
import {
  hrmInsuranceMonthEmpRecordMyAPI
} from '@/api/hrm/insuranceScheme'
import {
  hrmSalaryConfigQueryInItConfigAPI
} from '@/api/hrm/salaryConfig'

import XrHeader from '@/components/XrHeader'
import StatisticsCard from '../../insuranceScheme/components/StatisticsCard'
import MyInsuranceSchemeDetail from './components/Detail'

export default {
  name: 'MyInsuranceSchemeIndex',
  components: {
    XrHeader,
    StatisticsCard,
    MyInsuranceSchemeDetail
  },
  data() {
    return {
      loading: false,
      initStatusConfig: null,
      initDataConfig: null,
      year: '',
      pickerOptions: {
        disabledDate: (time) => {
          if (this.initDataConfig && this.initDataConfig.year) {
            return time.getFullYear() < this.initDataConfig.year
          }
          return true
        }
      },
      list: [], // 列表
      fieldList: [{
        label: '个人社保',
        prop: 'personalInsuranceAmount'
      }, {
        label: '公司社保',
        prop: 'corporateInsuranceAmount'
      }, {
        label: '个人公积金',
        prop: 'personalProvidentFundAmount'
      }, {
        label: '公司公积金',
        prop: 'corporateProvidentFundAmount'
      }],
      // 最新的年
      newYear: '',
      rowDetail: null,
      detailVisible: false
    }
  },
  computed: {
  },
  watch: {},
  created() {
    this.getConfigList()
  },
  mounted() {
  },
  methods: {
    /**
     * 获取配置信息
     */
    getConfigList() {
      this.loading = true
      // 2 薪资初始化配置1 3 薪资初始化配置2 4 社保初始化配置1 5 社保初始化配置2
      hrmSalaryConfigQueryInItConfigAPI()
        .then(res => {
          const statusInitConfig = res.data.statusInitConfig
          const firstConfig = statusInitConfig[4] == 1
          const secondConfig = statusInitConfig[5] == 1
          this.initStatusConfig = { firstConfig, secondConfig }

          this.initDataConfig = res.data.otherInitConfig
          if (!firstConfig) {
            // 初始化未完成
          } else if (!secondConfig) {
            // 初始化未完成
          } else {
            const setYear = this.initDataConfig.socialSecurityStartMonth ? this.initDataConfig.socialSecurityStartMonth.split('-')[0] : new Date().getFullYear().toString()
            const year = this.initDataConfig.lastSocialSecurityYear ? this.initDataConfig.lastSocialSecurityYear.toString() : setYear
            this.year = year.toString()
            this.newYear = this.year
            this.initDataConfig.year = setYear
            this.getList()
          }

          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 获取年数据
     */
    getList() {
      this.loading = true
      hrmInsuranceMonthEmpRecordMyAPI({
        pageType: 0,
        year: this.year
      })
        .then(res => {
          this.list = res.data.list || []
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 详情查看
     */
    detailCheckClick(data) {
      this.rowDetail = data
      this.detailVisible = true
    }
  }
}
</script>

<style scoped lang="scss">
.wrapper {
  position: relative;
  width: 100%;
  height: 100%;

  .xr-header {
    height: 34px;
    margin-bottom: 20px;

    ::v-deep .xr-header__ft {
      top: 0;
      line-height: 34px;
    }
  }

  &-main {
    height: calc(100% - 50px);
    padding: 8px;
    overflow-y: auto;
  }
}
</style>
