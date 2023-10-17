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
    width="500px"
    @close="handleCancel">
    <div class="form-add-dialog-body">
      <el-form ref="schemeForm" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="参保方案">
          <el-select
            v-model="form.schemeId"
            style="width: 100%;">
            <el-option
              v-for="(item, index) in schemeList"
              :key="index"
              :label="item.label"
              :value="item.value " />
          </el-select>
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
  hrmEmployeeUpdateInsuranceSchemeAPI
} from '@/api/hrm/employeeSocialSecurity'
import {
  hrmConfigInsuranceSchemListAPI
} from '@/api/admin/hrm'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import { isArray } from '@/utils/types'

export default {
  // 修改社保方案
  name: 'UpdateSchemeDialog',
  components: {
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    id: [String, Number],
    list: Array,
    type: [String, Number], // 1 一个  2  多个
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      title: '修改社保方案',
      schemeList: [],
      rules: {
        schemeId: [
          { required: true, message: '请选择', trigger: 'blur' }
        ]
      },
      form: {}
    }
  },
  computed: {
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          if (this.schemeList.length == 0) {
            this.getSchemList()
          }

          if (isArray(this.list) && this.list.length === 1) {
            this.form = { schemeId: this.list[0].schemeId }
          } else {
            this.form = { schemeId: '' }
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
    handleCancel() {
      this.$emit('update:visible', false)
    },

    /**
     * 获取参保方案
     */
    getSchemList() {
      hrmConfigInsuranceSchemListAPI({
        pageType: 0
      })
        .then(res => {
          this.schemeList = res.data.list.map(item => {
            return {
              label: item.schemeName,
              value: item.schemeId
            }
          })
        })
        .catch(() => {
        })
    },

    /**
     * 点击确定
     */
    handleConfirm() {
      this.$refs.schemeForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.type == 1) {
            this.form.employeeIds = [this.id]
          } else {
            this.form.employeeIds = this.list.map(item => item.employeeId)
          }
          hrmEmployeeUpdateInsuranceSchemeAPI(this.form)
            .then(res => {
              this.$message.success('操作成功')
              this.$emit('change')
              this.handleCancel()
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
}
</style>
