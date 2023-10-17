<!--
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-06-04 14:52:43
 * @LastEditTime: 2023-02-23 14:48:53
 * @LastEditors: yang
-->
<template>
  <xr-create
    v-loading="loading"
    :title="titleContent"
    @close="close"
    @save="saveClick">
    <create-sections title="基本信息">
      <wk-form
        ref="baseForm"
        :model="baseForm"
        :rules="baseRules"
        :field-from="baseForm"
        :field-list="baseFields"
        label-position="top"
      >
        <template slot-scope="{data}">
          <!-- <slot :data="data" /> -->
          <el-cascader
            v-if="data && data.formType == 'postType'"
            ref="elCascader"
            v-model="baseForm[data.field]"
            :show-all-levels="false"
            :props="defaultProps"
            :options="options"
            style="width: 100%;"
            change-on-select />
          <div
            v-else-if="data && data.formType == 'salary'"
            class="wk-range-select">
            <flexbox class="wk-range-select__top">
              <el-input-number
                v-model="baseForm.minSalary"
                :disabled="baseForm.noLimitsalary"
                :max="100000000"
                :min="0"
                :controls="false"
                :precision="2" />
              <span class="range-separator">至</span>
              <el-input-number
                v-model="baseForm.maxSalary"
                :disabled="baseForm.noLimitsalary"
                :max="100000000"
                :min="0"
                :controls="false"
                :precision="2" />
              <el-select
                v-model="baseForm.salaryUnit"
                :disabled="baseForm.noLimitsalary"
                class="range-unit">
                <el-option
                  :value="1"
                  label="元/月" />
                <el-option
                  :value="2"
                  label="元/年" />
              </el-select>
            </flexbox>
            <el-checkbox
              v-model="baseForm.noLimitsalary"
              @change="salaryCheckBoxChange">面议</el-checkbox>
          </div>
          <div
            v-else-if="data && data.formType == 'age'"
            class="wk-range-select">
            <flexbox class="wk-range-select__top">
              <el-input-number
                v-model="baseForm.minAge"
                :disabled="baseForm.noLimitAge"
                :max="99"
                :min="0"
                :controls="false"
                :precision="0" />
              <span class="range-separator">至</span>
              <el-input-number
                v-model="baseForm.maxAge"
                :disabled="baseForm.noLimitAge"
                :max="99"
                :min="0"
                :controls="false"
                :precision="0" />
            </flexbox>
            <el-checkbox
              v-model="baseForm.noLimitAge"
              @change="ageCheckBoxChange">不限</el-checkbox>
          </div>
        </template>
      </wk-form>
    </create-sections>
  </xr-create>
</template>

<script>
import {
  hrmRecruitPostAddAPI,
  hrmRecruitPostSetAPI,
  hrmRecruitPostTypeQueryAPI
} from '@/api/hrm/recruit/post'

import XrCreate from '@/components/XrCreate'
import WkForm from '@/components/NewCom/WkForm'
import CreateSections from '@/components/CreateSections'
import postModel from '../model/post'

import { objDeepCopy } from '@/utils'
import { isArray, isEmpty } from '@/utils/types'
import { getTreeValue } from '@/utils'

export default {
  // 岗位创建
  name: 'PostCreateView',
  components: {
    XrCreate,
    CreateSections,
    WkForm
  },
  filters: {},
  props: {
    detail: Object
  },
  data() {
    return {
      loading: false,
      baseFields: postModel.fields.filter(item => item.field !== 'hasEntryNum'),
      baseRules: postModel.rules,
      baseForm: {},
      options: [],
      defaultProps: {
        children: 'children',
        label: 'name',
        value: 'id'
      }
    }
  },
  computed: {
    titleContent() {
      return this.detail ? '编辑招聘职位' : '新建招聘职位'
    }
  },
  watch: {},
  created() {
    this.getOptions()

    if (this.detail) {
      const baseForm = {}
      this.baseFields.forEach(item => {
        if (item.formType == 'salary') {
          if (this.detail.salaryUnit == -1) {
            baseForm.noLimitsalary = true
            baseForm.salaryUnit = 1
          } else {
            baseForm.minSalary = isEmpty(this.detail.minSalary) ? undefined : this.detail.minSalary
            baseForm.maxSalary = isEmpty(this.detail.maxSalary) ? undefined : this.detail.maxSalary
            baseForm.salaryUnit = this.detail.salaryUnit
          }
        } else if (item.formType == 'age') {
          if (this.detail.minAge == -1 && this.detail.maxAge == -1) {
            baseForm.noLimitAge = true
          } else {
            baseForm.minAge = isEmpty(this.detail.minAge) ? undefined : this.detail.minAge
            baseForm.maxAge = isEmpty(this.detail.maxAge) ? undefined : this.detail.maxAge
          }
        } else if (item.field == 'interviewEmployeeIds') {
          baseForm.interviewEmployeeIds = this.detail.interviewEmployeeIds ? this.detail.interviewEmployeeIds.split(',') : []
        } else if (item.field == 'city') {
          const cityValue = this.detail[item.field]
          if (cityValue) {
            const citys = cityValue.split(',')
            baseForm.city = {
              province: citys[0] || '',
              city: citys[1] || '',
              area: citys[2] || ''
            }
          } else {
            baseForm.city = {}
          }
        } else {
          if (item.formType === 'number') {
            baseForm[item.field] = isEmpty(this.detail[item.field]) ? undefined : this.detail[item.field]
          } else {
            baseForm[item.field] = this.detail[item.field]
          }
        }
      })
      this.baseForm = baseForm
    } else {
      this.baseForm = {
        jobNature: 1,
        salaryUnit: 1,
        emergencyLevel: 1,
        city: {
          province: '',
          city: '',
          area: ''
        }
      }
    }
  },
  mounted() {
  },

  beforeDestroy() {},
  methods: {
    getOptions() {
      hrmRecruitPostTypeQueryAPI().then(res => {
        this.options = res.data || []
        if (this.detail) {
          const postTypeId = this.detail.postTypeId ? getTreeValue(this.detail.postTypeId, this.options) : []
          this.$set(this.baseForm, 'postTypeId', postTypeId)
        }
      }).catch(() => {})
    },

    /**
     * 选择省市
     */
    selectProvince(data, item) {
      this.baseForm[item.field] = data.value
    },

    close() {
      this.$emit('close')
    },

    saveClick() {
      this.$refs.baseForm.$children[0].validate(valid => {
        if (valid) {
          this.uploadCreateData()
        }
      })
    },

    /**
     * 切换 薪资 或者 年龄
     */
    salaryCheckBoxChange(val) {
      if (!val && (this.baseForm.salaryUnit != 1 || this.baseForm.salaryUnit != 2)) {
        this.baseForm.salaryUnit = 1
      }
      this.baseForm.minSalary = undefined
      this.baseForm.maxSalary = undefined
    },

    ageCheckBoxChange() {
      this.baseForm.minAge = undefined
      this.baseForm.maxAge = undefined
    },

    /**
     * 提交数据
     */
    uploadCreateData() {
      const params = objDeepCopy(this.baseForm)
      if (params.noLimitsalary) {
        params.minSalary = -1
        params.maxSalary = -1
        params.salaryUnit = -1
      }
      delete params['noLimitsalary']
      if (params.interviewEmployeeIds && isArray(params.interviewEmployeeIds)) {
        params.interviewEmployeeIds = params.interviewEmployeeIds.join(',')
      }

      if (params.postTypeId && params.postTypeId.length) {
        params.postTypeId = params.postTypeId[params.postTypeId.length - 1]
      } else {
        params.postTypeId = ''
      }

      if (params.city) {
        const keys = Object.keys(params.city)
        let city = ''
        keys.forEach(key => {
          if (params.city[key]) {
            if (!city) {
              city = params.city[key]
            } else {
              city += `,${params.city[key]}`
            }
          }
        })
        params.city = city
      } else {
        params.city = ''
      }

      if (params.noLimitAge) {
        params.minAge = -1
        params.maxAge = -1
      }
      delete params['noLimitAge']

      for (const key in params) {
        if (params[key] === undefined) {
          params[key] = ''
        }
      }

      if (this.detail) {
        params.postId = this.detail.postId
      }
      this.loading = true
      const request = this.detail ? hrmRecruitPostSetAPI : hrmRecruitPostAddAPI
      request(params).then(res => {
        this.$emit('close')
        this.$message.success(`${this.titleContent}成功`)
        this.$emit('success')
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    }

  }
}
</script>

<style lang="scss" scoped>
.distpicker-address-wrapper {
  ::v-deep .el-select {
    width: 100%;
  }
}

.wk-range-select {
  &__top {
    .range-separator {
      margin: 0 10px;
    }

    .range-unit {
      flex-shrink: 0;
      width: 75px;
      margin-left: 10px;
    }

    .el-input-number {
      width: 110px;
    }
  }
}
</style>
