<template>
  <div v-loading="loading" class="wrapper">
    <xr-header
      ft-top="0"
      label="发放记录">
      <template slot="ft">
        <el-date-picker
          v-model="yearMonth"
          :picker-options="pickerOptions"
          :clearable="false"
          style="width: 200px;margin-right: 15px;"
          type="month"
          format="yyyy-MM"
          value-format="yyyy-MM"
          placeholder="选择日期"
          @change="timeChange" />
      </template>
    </xr-header>
    <div
      v-empty="!list || list.length == 0"
      class="wrapper-main">
      <statistics-card
        v-for="(item, index) in list"
        :key="index"
        :title-show="false"
        :data="item"
        :field-list="fieldList"
        delete-button-show
        @detail-click="detailClick(item)"
        @delete-click="deleteCheckClick(item)"
      />
    </div>
  </div>
</template>

<script>
import {
  hrmSalarySlipRecordSendListAPI,
  hrmSalarySlipRecordDeleteAPI
} from '@/api/hrm/salary'
import {
  hrmSalaryConfigQueryInItConfigAPI
} from '@/api/hrm/salaryConfig'

import XrHeader from '@/components/XrHeader'
import StatisticsCard from '../../insuranceScheme/components/StatisticsCard'

import CheckStatusMixin from '@/mixins/CheckStatusMixin'

export default {
  name: 'SalarySlipHistory',
  components: {
    XrHeader,
    StatisticsCard
  },
  mixins: [CheckStatusMixin],
  beforeRouteUpdate(to, from, next) {
    if (to.query.time) {
      this.yearMonth = to.query.time
    }
    this.getList()
    next()
  },
  data() {
    return {
      loading: false,
      initDataConfig: null,
      yearMonth: '',
      pickerOptions: {
        disabledDate: (time) => {
          if (this.initDataConfig && this.initDataConfig.year) {
            if (time.getFullYear() == this.initDataConfig.year) {
              return time.getMonth() + 1 < this.initDataConfig.month
            }
            return time.getFullYear() < this.initDataConfig.year
          }
          return true
        }
      },
      list: [], // 列表
      fieldList: [{
        label: '创建人',
        prop: 'createUserName'
      }, {
        label: '发放时间',
        prop: 'createTime'
      }, {
        label: '工资表总人数 ',
        prop: 'salaryNum'
      }, {
        label: '发放人数',
        prop: 'payNum'
      }, {
        label: '已查看人数',
        prop: 'readNum'
      }]
    }
  },
  computed: {
  },
  watch: {},
  created() {
    if (this.$route.query.time) {
      this.yearMonth = this.$route.query.time
    } else {
      this.yearMonth = this.$moment().format('YYYY-MM')
    }
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
          if (this.initDataConfig.salaryStartMonth) {
            const dates = this.initDataConfig.salaryStartMonth.split('-')
            this.initDataConfig.year = parseInt(dates[0])
            this.initDataConfig.month = parseInt(dates[1])
          }

          // 路由有值时，说明是从详情返回，以路由为准
          if (this.initDataConfig.lastSalarySlipMonth && !this.$route.query.time) {
            this.yearMonth = this.initDataConfig.lastSalarySlipMonth
          }
          this.getList()

          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 时间筛选
     */
    timeChange() {
      this.yearMonth = this.yearMonth || this.$moment().format('YYYY-MM')
      this.$router.replace(`${this.$route.path}?time=${this.yearMonth}`)
    },

    /**
     * 获取年数据
     */
    getList() {
      this.loading = true
      const dates = this.yearMonth.split('-')
      hrmSalarySlipRecordSendListAPI({
        pageType: 0,
        year: parseInt(dates[0]),
        month: parseInt(dates[1])
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
        name: 'salarySlipHistoryDetail',
        params: {
          id: data.id,
          month: data.month
        }
      })
    },

    /**
     * 删除操作
     */
    deleteCheckClick(data) {
      this.$confirm('确定删除？此次发放的工资条将同时删除', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.loading = true
          hrmSalarySlipRecordDeleteAPI(data.id).then(res => {
            this.$message.success('删除成功')
            this.getList()
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
}
</style>
