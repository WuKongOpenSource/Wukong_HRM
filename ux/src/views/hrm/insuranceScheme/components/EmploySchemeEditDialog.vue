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
    <div class="form-add-dialog-body">
      <el-form ref="schemeForm" :model="form" label-position="top" label-width="80px">
        <el-form-item
          :rules="{ required: true, message: '请选择', trigger: 'change' }"
          label="参保方案">
          <el-select
            v-model="form.schemeId"
            style="width: 100%;"
            @change="schemeChange">
            <el-option
              v-for="(item, index) in schemeList"
              :key="index"
              :label="item.label"
              :value="item.value " />
          </el-select>
        </el-form-item>
        <template v-if="schemeType == 1">
          <div style="margin-bottom: 8px;font-weight: bold;">编辑社保各项基数</div>
          <flexbox wrap="wrap">
            <el-form-item
              v-for="(item, index) in form.socialSecurityList"
              :key="`s${index}`"
              :prop="`socialSecurityList[${index}].defaultAmount`"
              :rules="{ required: true, message: '请输入', trigger: 'change' }"
              :label="getProjectName(item)">
              <el-input
                v-model="item.defaultAmount"
                v-wk-number="'positive3Float'"
                type="text" />
            </el-form-item>
          </flexbox>
          <template v-if="form.providentFundList && form.providentFundList.length > 0">
            <div style="margin-bottom: 8px;font-weight: bold;">编辑公积金各项基数</div>
            <flexbox wrap="wrap">
              <el-form-item
                v-for="(item, index) in form.providentFundList"
                :key="`p${index}`"
                :prop="`providentFundList[${index}].defaultAmount`"
                :rules="{ required: true, message: '请输入', trigger: 'change' }"
                :label="getProjectName(item)">
                <el-input
                  v-model="item.defaultAmount"
                  v-wk-number="'positive3Float'"
                  type="text" />
              </el-form-item>
            </flexbox>
          </template>
        </template>
        <template v-if="schemeType == 2">
          <div style="margin-bottom: 8px;font-weight: bold;">编辑社保金额</div>
          <flexbox
            v-for="(item, index) in form.socialSecurityList"
            :key="`s${index}`"
            wrap="wrap">
            <el-form-item
              :prop="`socialSecurityList[${index}].corporateAmount`"
              :rules="{ required: true, message: '请输入', trigger: 'change' }"
              :label="`${getProjectName(item)}公司金额`">
              <el-input
                v-model="item.corporateAmount"
                v-wk-number="'positive3Float'"
                type="text" />
            </el-form-item>
            <el-form-item
              :prop="`socialSecurityList[${index}].personalAmount`"
              :rules="{ required: true, message: '请输入', trigger: 'change' }"
              :label="`${getProjectName(item)}个人金额`">
              <el-input
                v-model="item.personalAmount"
                v-wk-number="'positive3Float'"
                type="text" />
            </el-form-item>
          </flexbox>
          <div style="margin-bottom: 8px;font-weight: bold;">编辑公积金金额</div>
          <flexbox
            v-for="(item, index) in form.providentFundList"
            :key="`p${index}`"
            wrap="wrap">
            <el-form-item
              :prop="`providentFundList[${index}].corporateAmount`"
              :rules="{ required: true, message: '请输入', trigger: 'change' }"
              :label="`${getProjectName(item)}公司金额`">
              <el-input
                v-model="item.corporateAmount"
                v-wk-number="'positive3Float'"
                type="text" />
            </el-form-item>
            <el-form-item
              :prop="`providentFundList[${index}].personalAmount`"
              :rules="{ required: true, message: '请输入', trigger: 'change' }"
              :label="`${getProjectName(item)}个人金额`">
              <el-input
                v-model="item.personalAmount"
                v-wk-number="'positive3Float'"
                type="text" />
            </el-form-item>
          </flexbox>
        </template>
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
  hrmInsuranceMonthEmpRecordUpdateProjectAPI,
  hrmInsuranceMonthEmpRecordBatchUpdateInsuranceAPI
} from '@/api/hrm/insuranceScheme'
import {
  hrmConfigInsuranceSchemListAPI,
  hrmConfigInsuranceSchemeDetailAPI
} from '@/api/admin/hrm'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import insuranceSchemeModel from '@/views/admin/hrm/insuranceScheme/model/insuranceScheme'

export default {
  // 修改社保方案
  name: 'UpdateSchemeDialog',
  components: {
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    selectionList: Array, // 多编辑详情
    detail: Object, // 单编辑详情
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      title: '编辑参保信息',
      schemeList: [],
      form: {}
    }
  },
  computed: {
    schemeType() {
      return this.form ? this.form.schemeType : ''
    }
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          if (this.schemeList.length == 0) {
            this.getSchemList()
          }

          let schemeId = ''
          if (this.detail) {
            schemeId = this.detail.schemeId
          } else if (this.selectionList && this.selectionList.length > 0) {
            schemeId = this.selectionList[0].schemeId
          }
          this.form = {
            schemeId: schemeId
          }
          this.getDetial()
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

    getProjectName(data) {
      if (data.type < 9) {
        return insuranceSchemeModel.providentFundTypeValue[data.type]
      } else if (data.type == 9 || data.type == 11) {
        return data.projectName
      }
      return insuranceSchemeModel.socialSecurityTypeValue[data.type]
    },

    /**
     * 切换项目
     */
    schemeChange(val) {
      this.form = {
        schemeId: val,
        schemeType: '',
        socialSecurityList: [],
        providentFundList: []
      }
      this.getDetial()
    },

    /**
     * 详情
     */
    getDetial() {
      if (this.form.schemeId) {
        this.loading = true
        hrmConfigInsuranceSchemeDetailAPI(this.form.schemeId)
          .then(res => {
            const data = res.data || {}

            this.form = {
              schemeId: data.schemeId,
              schemeType: data.schemeType,
              socialSecurityList: data.socialSecurityProjectList,
              providentFundList: data.providentFundProjectList
            }
            this.loading = false
          })
          .catch(() => {
            this.loading = false
          })
      }
    },

    /**
     * 点击确定
     */
    handleConfirm() {
      this.$refs.schemeForm.validate(valid => {
        if (valid) {
          this.loading = true
          const params = {
            schemeId: this.form.schemeId
          }

          if (this.detail) {
            params.iempRecordId = this.detail.iempRecordId
          } else if (this.selectionList) {
            params.iempRecordIds = this.selectionList.map(item => item.iempRecordId)
          }

          const projectList = this.form.socialSecurityList.concat(this.form.providentFundList)
          params.projectList = projectList.map(item => {
            const temp = {
              projectId: item.projectId
            }

            if (this.schemeType == 1) {
              temp.defaultAmount = item.defaultAmount
            } else if (this.schemeType == 2) {
              temp.corporateAmount = item.corporateAmount
              temp.personalAmount = item.personalAmount
            }

            return temp
          })

          let request = hrmInsuranceMonthEmpRecordUpdateProjectAPI
          if (this.selectionList) {
            request = hrmInsuranceMonthEmpRecordBatchUpdateInsuranceAPI
          }
          request(params)
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

  .el-form-item {
    flex: 0 0 47.5%;

    ::v-deep .el-form-item__label {
      padding-bottom: 0;
      line-height: 30px;
    }

    ::v-deep .el-form-item__content {
      line-height: 34px;
    }
  }

  .el-form-item:nth-child(even) {
    margin-left: 30px;
  }
}
</style>
