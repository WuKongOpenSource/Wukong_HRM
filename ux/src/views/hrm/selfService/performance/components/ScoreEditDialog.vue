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
    width="600px"
    @close="handleCancel">
    <div class="form-add-dialog-body">
      <el-form ref="scoreForm" :model="form" label-width="80px">
        <el-form-item label="当前为">
          <el-input
            v-model="detail.score"
            disabled>
            <template slot="append">分</template>
          </el-input>
          <el-select v-model="detail.levelId" disabled>
            <el-option
              v-for="item in levelOptions"
              :key="item.levelId"
              :label="item.levelName"
              :value="item.levelId" />
          </el-select>
        </el-form-item>
        <el-form-item label="调整为">
          <el-form-item
            :rules="getScoreRules()"
            prop="score"
            label="">
            <el-input
              v-model="form.score"
              v-wk-number>
              <template slot="append">分</template>
            </el-input>
          </el-form-item>
          <el-form-item
            :rules="{ required: true, message: '请选择', trigger: 'change' }"
            prop="levelId"
            label="">
            <el-select v-model="form.levelId">
              <el-option
                v-for="item in levelOptions"
                :key="item.levelId"
                :label="item.levelName"
                :value="item.levelId" />
            </el-select>
          </el-form-item>
        </el-form-item>
        <el-form-item :rules="{ required: true, message: '请输入', trigger: 'change' }" prop="reason" label="调整理由">
          <el-input
            v-model="form.reason"
            :autosize="{ minRows: 3}"
            :maxlength="800"
            type="textarea"
            resize="none" />
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
  hrmPerformanceEmployeeQueryLevelListAPI,
  hrmPerformanceEmployeeUpdateScoreLevelAPI
} from '@/api/hrm/selfService/performance'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  // 调整考核
  name: 'ScoreEditDialog',
  components: {
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    detail: {
      required: true,
      type: Object,
      default: () => {
        return {}
      }
    },
    fullScore: Number,
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      // 等级数据
      levelOptions: [],
      form: {}
    }
  },
  computed: {
    title() {
      const name = this.detail ? `(${this.detail.employeeName})` : ''
      return `调整考核${name}`
    }
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          if (this.levelOptions.length == 0) {
            this.getLevelList()
          }
        }
      },
      immediate: true
    }
  },
  mounted() {},
  methods: {
    /**
     * 获取层级信息
     */
    getLevelList() {
      hrmPerformanceEmployeeQueryLevelListAPI(this.detail.employeeAppraisalId)
        .then(res => {
          this.levelOptions = res.data || []
        })
        .catch(() => {
        })
    },

    /**
     * 获取分数规则
     */
    getScoreRules() {
      const validateScore = (rule, value, callback) => {
        if (value < 0 || parseInt(value) > this.fullScore || value === null || value === undefined || value === '') {
          callback(new Error(`范围在0-${this.fullScore}之间`))
        } else {
          callback()
        }
      }
      return { validator: validateScore, trigger: ['blur'] }
    },

    /**
     * 取消选择
     */
    handleCancel() {
      this.form = { score: '' }

      if (this.$refs.scoreForm) {
        this.$refs.scoreForm.clearValidate()
      }
      this.$emit('update:visible', false)
    },

    /**
     * 点击确定
     */
    handleConfirm() {
      this.$refs.scoreForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.form.employeeAppraisalId = this.detail.employeeAppraisalId
          hrmPerformanceEmployeeUpdateScoreLevelAPI(this.form)
            .then(res => {
              this.$message.success('操作成功')
              this.$emit('change', this.form.schedule)
              this.handleCancel()
              this.loading = false
            })
            .catch(() => {
              this.loading = false
            })
          this.$emit('change', this.form.score)
          this.handleCancel()
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

  ::v-deep .el-form {
    .el-form-item__content {
      display: flex;
      line-height: 34px;
    }
  }

  .el-input {
    width: 120px;
    margin-right: 20px;
  }

  .el-select {
    width: 200px;
  }
}
</style>
