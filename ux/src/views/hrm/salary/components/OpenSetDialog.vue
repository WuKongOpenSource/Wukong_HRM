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
    :show-close="false"
    width="500px">
    <div class="form-add-dialog-body">
      <reminder
        style="margin-bottom: 10px;"
        content="计薪周期设置后将不可修改" />
      <el-form ref="openForm" :model="form" label-width="120px">
        <el-form-item label="计薪周期">
          <span>当月</span>
          <el-select
            v-model="form.salaryCycleStartDay">
            <el-option
              v-for="(item, index) in cycleOptions"
              :key="index"
              :label="item"
              :value="item " />
          </el-select>
          <span>{{ getSalaryCycleEndDayDes() }}</span>
          <el-tooltip
            content="计薪周期设定后将不可修改"
            effect="dark"
            placement="top">
            <i class="wk wk-help wk-help-tips" />
          </el-tooltip>
        </el-form-item>
        <el-form-item label="对应社保自然月">
          <el-select
            v-model="form.socialSecurityMonthType"
            style="margin-left: 0;">
            <el-option
              v-for="(item, index) in monthTypeOptions"
              :key="index"
              :label="item.label"
              :value="item.value" />
          </el-select>
          <el-tooltip
            content="请根据你工资报表月份对应的社保报表月份进行选择，例如工资报表为6月，如果要对应5月的社保报表，则选择上月"
            effect="dark"
            placement="top">
            <i class="wk wk-help wk-help-tips" />
          </el-tooltip>
        </el-form-item>
      </el-form>
    </div>
    <span
      slot="footer"
      class="dialog-footer">
      <el-button
        v-debounce="handleConfirm"
        type="primary">保存</el-button>
      <el-button @click.native="handleCancel">取消</el-button>
    </span>
  </el-dialog>
</template>

<script>
import {
  hrmSalaryConfigSaveInItConfigAPI
} from '@/api/hrm/salaryConfig'

import Reminder from '@/components/Reminder'
import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  // 开启工资设置
  name: 'OpenSetDialog',
  components: {
    Reminder
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    detail: Object,
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      title: '开启工资设置',
      form: {},
      cycleOptions: [],
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
  computed: {
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          if (this.detail) {
            this.form = {
              salaryCycleStartDay: this.detail.salaryCycleStartDay || 1,
              socialSecurityMonthType: this.detail.socialSecurityMonthType || 0
            }
          } else {
            this.form = {
              salaryCycleStartDay: 1,
              socialSecurityMonthType: 0
            }
          }
        }
      },
      immediate: true
    }
  },
  created() {
    for (let index = 1; index <= 31; index++) {
      this.cycleOptions.push(index)
    }
  },
  methods: {

    /**
     * 取消选择
     */
    handleCancel() {
      this.$router.go(-1)
      this.close()
    },

    close() {
      this.$emit('update:visible', false)
    },

    /**
     * 结束时间
     */
    getSalaryCycleEndDay() {
      return this.form.salaryCycleStartDay == 1 ? 31 : this.form.salaryCycleStartDay - 1
    },

    getSalaryCycleEndDayDes() {
      const day = this.getSalaryCycleEndDay()
      return `至${day === 31 ? '当月' : '次月'}${day}号`
    },

    /**
     * 点击确定
     */
    handleConfirm() {
      this.$refs.openForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.form.salaryCycleEndDay = this.getSalaryCycleEndDay()
          hrmSalaryConfigSaveInItConfigAPI(this.form)
            .then(res => {
              // this.$message.success('操作成功')

              this.$emit('change', this.form)
              this.close()
              this.loading = false
            })
            .catch(() => {
              this.loading = false
            })
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.form-add-dialog-body {
  max-height: 55vh;
  overflow-x: hidden;
  overflow-y: auto;

  .el-select {
    width: 80px;
    margin: 0 8px;
  }
}
</style>
