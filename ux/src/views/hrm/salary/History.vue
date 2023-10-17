<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <div v-loading="loading" class="wrapper">
    <xr-header
      ft-top="0"
      label="历史工资">
      <template slot="ft">
        <el-date-picker
          v-model="year"
          :picker-options="pickerOptions"
          :clearable="false"
          style="width: 200px;"
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
        :title="item.title"
        :icon-show="false"
        :data="item"
        :field-list="fieldList"
      >
        <template slot="handle">
          <span
            :style="{ 'color': getStatusColor(item.checkStatus) }"
            class="check-status">{{ getStatusName(item.checkStatus) }}</span>
          <el-button type="text" @click="detailClick(item)">查看详情</el-button>
        </template>
      </statistics-card>
    </div>
  </div>
</template>

<script>
import {
  hrmSalaryHistoryRecordListAPI
} from '@/api/hrm/salary'
import {
  hrmSalaryConfigQueryInItConfigAPI
} from '@/api/hrm/salaryConfig'

import XrHeader from '@/components/XrHeader'
import StatisticsCard from '../insuranceScheme/components/StatisticsCard'

import CheckStatusMixin from '@/mixins/CheckStatusMixin'

export default {
  name: 'SalaryHistory',
  components: {
    XrHeader,
    StatisticsCard
  },
  mixins: [CheckStatusMixin],
  data() {
    return {
      loading: false,
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
        label: '计薪人数',
        prop: 'num'
      }, {
        label: '应发工资',
        prop: 'expectedPaySalary'
      }, {
        label: '实发工资',
        prop: 'realPaySalary'
      }, {
        label: '个税总额',
        prop: 'personalTax'
      }]
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
          this.initDataConfig = res.data.otherInitConfig
          const setYear = this.initDataConfig.salaryStartMonth ? this.initDataConfig.salaryStartMonth.split('-')[0] : new Date().getFullYear().toString()
          this.year = this.initDataConfig.lastSalaryYear ? this.initDataConfig.lastSalaryYear.toString() : setYear
          this.initDataConfig.year = setYear
          this.getList()

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
      hrmSalaryHistoryRecordListAPI({
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
    detailClick(data) {
      this.$router.push({
        name: 'salaryHistoryDetail',
        params: {
          id: data.srecordId
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.wrapper {
  position: relative;
  width: 100%;
  height: calc(100% - 55px);

  .xr-header {
    height: 34px;
    margin-bottom: 20px;

    ::v-deep .xr-header__ft {
      top: 0;
      line-height: 34px;
    }
  }

  &-main {
    height: 100%;
    padding: 8px;
    overflow-y: auto;
  }

  .check-status {
    margin-right: 8px;
    font-size: 13px;
  }
}
</style>
