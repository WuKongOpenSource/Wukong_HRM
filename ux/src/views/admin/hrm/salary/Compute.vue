<template>
  <div v-loading="loading" class="main">
    <xr-header
      label="计薪设置">
      <el-button
        slot="ft"
        type="primary"
        @click="saveClick">保存</el-button>
    </xr-header>
    <div class="main-body">
      <div class="main-item">
        <span class="label">计薪周期：</span>当月1日至当月31日<i
          class="wk wk-icon-fill-help wk-help-tips"
          data-type="28"
          data-id="275" />
      </div>
      <div class="main-item">
        <span class="label">对应社保自然月：</span><i
          class="wk wk-icon-fill-help wk-help-tips"
          data-type="28"
          data-id="274" />
      </div>
      <div class="main-item">
        <el-select
          v-model="form.socialSecurityMonthType"
          style="margin-left: 0;">
          <el-option
            v-for="(item, index) in monthTypeOptions"
            :key="index"
            :label="item.label"
            :value="item.value" />
        </el-select>
      </div>
    </div>
  </div>
</template>

<script>
import {
  hrmSalaryConfigQueryConfigAPI,
  hrmSalaryConfigUpdateConfigAPI
} from '@/api/hrm/salaryConfig'

import XrHeader from '@/components/XrHeader'

export default {
  // 计薪设置
  name: 'SalaryCompute',

  components: {
    XrHeader
  },

  props: {},

  data() {
    return {
      loading: false,
      form: {},
      monthTypeOptions: [{
        label: '上月',
        value: 0
      }, {
        label: '当月',
        value: 1
      }, {
        label: '次月',
        value: 2
      }]
    }
  },

  computed: {},

  watch: {},

  created() {
    this.getDetail()
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 详情
     */
    getDetail() {
      this.loading = true
      hrmSalaryConfigQueryConfigAPI()
        .then(res => {
          this.loading = false
          this.form = res.data || { socialSecurityMonthType: 0 }
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 保存
     */
    saveClick() {
      if (this.form.socialSecurityMonthType == null) {
        return this.$message.error('请选择')
      }
      this.loading = true
      hrmSalaryConfigUpdateConfigAPI({
        socialSecurityMonthType: this.form.socialSecurityMonthType
      })
        .then(res => {
          this.loading = false
          this.$message.success('操作成功')
          this.getDetail()
        })
        .catch(() => {
          this.loading = false
        })
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../../styles/index.scss";

.main-body {
  margin-top: #{$--interval-base * 2};

  .main-item {
    margin-bottom: #{$--interval-base * 2};

    .label {
      color: $--color-text-regular;
    }
  }
}
</style>
