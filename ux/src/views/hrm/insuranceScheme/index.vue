<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
-->
<template>
  <div v-loading="loading" class="wrapper">
    <template v-if="listShow">
      <xr-header
        ft-top="0"
        label="社保管理">
        <template slot="otherLabel">
          <i
            class="wk wk-icon-fill-help wk-help-tips"
            data-type="32"
            data-id="287" />
        </template>
        <template slot="ft">
          <el-date-picker
            v-model="year"
            :picker-options="pickerOptions"
            :clearable="false"
            style="width: 100px;margin-right: 15px;"
            type="year"
            format="yyyy"
            value-format="yyyy"
            placeholder="选择日期"
            @change="getList" />
        </template>
        <template slot="ft">
          <el-button
            v-if="manageAuth"
            type="primary"
            @click="createClick">新建次月报表</el-button>
        </template>
      </xr-header>
      <div
        v-empty="!list || list.length == 0"
        class="wrapper-main">
        <statistics-card
          v-for="(item, index) in list"
          :key="index"
          :delete-button-show="index === 0 && newYear == year && manageAuth"
          :title="`${item.month}月社保表`"
          :data="item"
          :field-list="fieldList"
          :button-show="detailAuth"
          @detail-click="detailCheckClick"
          @delete-click="deleteCheckClick"
        />
      </div>
    </template>

    <wk-steps-explain-view
      v-if="stepsExplainShow"
      :title="explainTitle"
      :steps="explainSteps"
      @step-click="stepClick" />

    <month-set-dialog
      :visible.sync="monthDialogShow"
      @change="monthSetChange"
    />
  </div>
</template>

<script>
import {
  hrmInsuranceMonthRecordQueryAPI,
  hrmInsuranceMonthRecordComputeDataAPI,
  hrmInsuranceMonthRecordDeleteAPI
} from '@/api/hrm/insuranceScheme'
import {
  hrmSalaryConfigQueryInItConfigAPI,
  hrmSalaryConfigUpdateInItConfigAPI
} from '@/api/hrm/salaryConfig'

import XrHeader from '@/components/XrHeader'
import WkStepsExplainView from '../components/WkStepsExplainView'
import MonthSetDialog from './components/MonthSetDialog'
import StatisticsCard from './components/StatisticsCard'

import { mapGetters } from 'vuex'

export default {
  name: 'InsuranceSchemeIndex',
  components: {
    XrHeader,
    WkStepsExplainView,
    MonthSetDialog,
    StatisticsCard
  },
  data() {
    return {
      loading: false,
      // 首次
      explainTitle: '首次创建社保前，请先配置参保信息',
      explainSteps: [{
        icon: require('@/assets/img/resource/setting.png'),
        label: '第一步：',
        desLeft: '去企业后台',
        desCenter: '【配置自主参保方案】',
        desRight: ''
      }, {
        icon: require('@/assets/img/resource/people.png'),
        label: '第二步：',
        desLeft: '去员工管理配置员工所属参保方案',
        desCenter: '',
        desRight: '',
        button: '已配置完成，去生成社保'
      }, {
        icon: require('@/assets/img/resource/cycle.png'),
        label: '第三步：',
        desLeft: '选择社保月份，生成社保',
        desCenter: '',
        desRight: ''
      }],
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
        label: '参保人数',
        prop: 'num'
      }, {
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
      }, {
        label: '本月停保人数',
        prop: 'stopNum'
      }],
      stepsExplainShow: false,
      // 月份设置
      monthDialogShow: false,
      // 最新的年
      newYear: ''
    }
  },
  computed: {
    ...mapGetters([
      'hrm'
    ]),

    // 社保权限
    insuranceAuth() {
      return this.hrm.insurance
    },

    // 详情权限
    detailAuth() {
      return this.insuranceAuth && this.insuranceAuth.read
    },

    // 管理权限
    manageAuth() {
      return this.insuranceAuth && this.insuranceAuth.manage
    },

    listShow() {
      if (!this.initStatusConfig) {
        return false
      }
      return this.initStatusConfig.firstConfig && this.initStatusConfig.secondConfig
    }
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
            this.stepsExplainShow = true
          } else if (!secondConfig) {
            this.monthDialogShow = true
          } else {
            this.stepsExplainShow = false
            this.monthDialogShow = false
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
     * 步骤点击
     */
    stepClick(type, index, step) {
      if (index == 0) {
        this.$router.push({
          name: 'insuranceSchemeSet'
        })
      } else if (index == 1) {
        this.updateInItConfig(4)
      }
    },

    /**
     * 更新状态
     */
    updateInItConfig(type) {
      this.loading = true
      // 2 薪资初始化配置1 3 薪资初始化配置2 4 社保初始化配置1 5 社保初始化配置2
      hrmSalaryConfigUpdateInItConfigAPI(type)
        .then(res => {
          if (this.initStatusConfig.secondConfig) {
            // 详情
            this.getList()
          } else {
            this.monthDialogShow = true
            // 去设置月份
          }

          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 月份设置change
     */
    monthSetChange(year, month) {
      this.getConfigList()
    },

    /**
     * 获取年数据
     */
    getList() {
      this.loading = true
      hrmInsuranceMonthRecordQueryAPI({
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
     * 新建
     */
    createClick() {
      this.$confirm('新建次月社保，本月数据将不可修改。请确认要新建次月社保吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.loading = true
          hrmInsuranceMonthRecordComputeDataAPI()
            .then(res => {
              this.loading = false
              this.getConfigList()
            })
            .catch(() => {
              this.loading = false
            })
        })
        .catch(() => {})
    },

    /**
     * 详情查看
     */
    detailCheckClick(data) {
      this.$router.push(`insurance-scheme/detail/${data.irecordId}`)
    },

    /**
     * 删除操作
     */
    deleteCheckClick(data) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.loading = true
          hrmInsuranceMonthRecordDeleteAPI(data.irecordId).then(res => {
            this.$message.success('删除成功')
            this.getConfigList()
            this.loading = false
          }).catch(() => {
            this.loading = false
          })
        })
        .catch(() => {})
    }
  }
}
</script>

<style scoped lang="scss">
.wrapper {
  position: relative;
  width: 100%;

  /* height: calc(100% - 55px); */
  height: 100%;

  .xr-header {
    margin-bottom: 20px;

    ::v-deep .xr-header__ft {
      top: 0;
      line-height: 34px;
    }
  }

  &-main {
    height: calc(100% - 60px);
    padding: 8px;
    overflow-y: auto;
  }
}
</style>
