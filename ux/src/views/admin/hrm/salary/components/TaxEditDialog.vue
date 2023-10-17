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
    width="700px"
    @close="handleCancel">
    <el-form ref="taxForm" :model="form" :rules="rules" class="form-add-dialog-body" label-position="top" label-width="80px">
      <create-sections title="基本信息">
        <flexbox wrap="wrap" style="padding: 0 15px;">
          <el-form-item
            label="方案名称">
            <div>{{ detail ? detail.ruleName : '' }}</div>
          </el-form-item>
          <el-form-item
            class="is-right"
            label="个税类型">
            <div>工资薪金所得税</div>
          </el-form-item>
          <el-form-item
            prop="cycleType">
            <template slot="label">
              计税周期<el-tooltip
                content="工资发放方式将影响计税周期和报税"
                effect="dark"
                placement="top">
                <i class="wk wk-help wk-help-tips" />
              </el-tooltip>
            </template>
            <el-radio-group
              v-model="form.cycleType">
              <el-radio
                v-for="(item, index) in cycleTypeOptions"
                :key="index"
                :label="item.value">
                {{ item.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item
            label="是否计税">
            <div>是</div>
          </el-form-item>
          <el-form-item
            class="is-right"
            label="起征点">
            <div>5000元/月</div>
          </el-form-item>
          <el-form-item
            label="个税结果">
            <div>保留2位小数</div>
          </el-form-item>
        </flexbox>
      </create-sections>
    </el-form>
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
  hrmSalaryTaxRuleUpdateAPI

} from '@/api/admin/hrm'

import CreateSections from '@/components/CreateSections'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import taxModel from '../model/tax'

export default {
  // 计税规则
  name: 'TaxEditDialog',
  components: {
    CreateSections
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
      rules: {
        cycleType: [
          { required: true, message: '请选择', trigger: 'blur' }
        ]
      },
      form: {},
      cycleTypeOptions: taxModel.getValueList(taxModel.cycleTypeValue)
    }
  },
  computed: {
    title() {
      return '编辑计税规则'
    }
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          const data = this.detail
          this.form = {
            cycleType: data.cycleType
          }
        }
      },
      immediate: true
    }
  },
  mounted() {},
  methods: {

    /**
     * 取消选择
     */
    handleCancel(goBack) {
      if (this.$refs.taxForm) {
        this.$refs.taxForm.clearValidate()
      }
      this.$emit('update:visible', false)
    },

    /**
     * 点击确定
     */
    handleConfirm() {
      this.$refs.taxForm.validate(valid => {
        if (valid) {
          this.loading = true

          this.form.ruleId = this.detail.ruleId

          hrmSalaryTaxRuleUpdateAPI(this.form)
            .then(res => {
              this.$message.success('操作成功')
              this.$emit('change')
              this.handleCancel(false)
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

  .el-form-item {
    flex: 0 0 47.5%;

    ::v-deep .el-form-item__label {
      padding-bottom: 0;
      line-height: 30px;
    }

    ::v-deep .el-form-item__content {
      line-height: 34px;
    }

    &.is-right {
      margin-left: 30px;
    }
  }

  ::v-deep .el-radio-group {
    .el-radio + .el-radio {
      margin-top: 5px;
    }
  }

  .wk-help-tips {
    margin-left: 3px;
  }
}
</style>
