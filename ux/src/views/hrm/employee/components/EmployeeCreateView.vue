<template>
  <xr-create
    :loading="loading"
    :show-cancel="false"
    :title="title"
    @close="close"
    @save="saveClick">
    <el-tabs
      v-if="isTabCreate"
      slot="title"
      v-model="tabType"
      @tab-click="tabChange">
      <el-tab-pane
        v-for="tab in tabList"
        :key="tab.name"
        :name="tab.name">
        <span slot="label">{{ tab.label }}</span>
      </el-tab-pane>
    </el-tabs>

    <el-button v-if="fieldsSetShow" slot="title" type="text" @click="createSetClick">新建员工字段设置</el-button>
    <create-sections title="个人信息">
      <el-form
        ref="baseForm"
        :key="tabType"
        :model="baseForm"
        :rules="baseRules"
        :validate-on-rule-change="false"
        class="wk-form"
        label-position="top"
        @change="wkBaseFormChange">
        <wk-form-item
          v-for="(item, index) in baseFields"
          :key="index"
          :index="index"
          :field-from="baseForm"
          :item="item"
          :disabled="item.disabled"
          style-percent="50%"
        >
          <template slot-scope="{ data }">
            <v-distpicker
              v-if="data && data.formType == 'city'"
              :province="baseForm[data.field].province"
              :city="baseForm[data.field].city"
              :disabled="data.disabled"
              hide-area
              @province="selectProvince($event, data, baseForm)"
              @city="selectCity($event, data, baseForm)" />
          </template>
        </wk-form-item>
      </el-form>
    </create-sections>
    <create-sections title="入职信息">
      <el-form
        ref="entryForm"
        :key="tabType"
        :model="entryForm"
        :rules="entryRules"
        :validate-on-rule-change="false"
        class="wk-form"
        label-position="top">
        <wk-form-item
          v-for="(item, index) in entryFields"
          :key="index"
          :index="index"
          :field-from="entryForm"
          :item="item"
          :disabled="item.disabled"
          style-percent="50%"
          @change="wkEntryFormChange"
        >
          <template slot-scope="{ data }">
            <v-distpicker
              v-if="data && data.formType == 'area'"
              :province="entryForm[data.field].province"
              :city="entryForm[data.field].city"
              :area="entryForm[data.field].area"
              :disabled="data.disabled"
              @province="selectProvince($event, data, entryForm)"
              @city="selectCity($event, data, entryForm)"
              @area="selectArea($event, data, entryForm)" />
          </template>
        </wk-form-item>
      </el-form>
    </create-sections>
    <el-button
      v-if="!detail"
      slot="footer"
      @click="saveContinueClick">保存并继续新建</el-button>
  </xr-create>
</template>

<script>
import {
  hrmEmployeeAddFieldsAPI,
  hrmEmployeeFieldsAddAPI,
  hrmEmployeeAgainOnboardingAPI,
  hrmEmployeeConfirmEntryAPI,
  hrmEmployeeFieldVerifyAPI
  // hrmEmployeeAllListAPI
} from '@/api/hrm/employee'
import {
  hrmRecruitChannelQueryAPI
} from '@/api/hrm/employeePost'
// import {
//   hrmDeptQueryTreeListAPI
// } from '@/api/hrm/dept'

import XrCreate from '@/components/XrCreate'
import CreateSections from '@/components/CreateSections'
import WkFormItem from '@/components/NewCom/WkForm/WkFormItem'
import VDistpicker from '@/components/VDistpicker'

import CustomFieldsMixin from '@/mixins/CustomFields'
import { objDeepCopy } from '@/utils'
import { isEmpty, isArray } from '@/utils/types'
import { mapGetters } from 'vuex'
import employField from '@/views/hrm/employee/model/employField'

export default {
  // 员工创建
  name: 'EmployeeCreateView',
  components: {
    XrCreate,
    CreateSections,
    WkFormItem,
    VDistpicker
  },
  filters: {},
  mixins: [CustomFieldsMixin],
  props: {
    createType: {
      type: String,
      default: 'new'
    }, // confirm 确认入职 new 新建员工 wait 待入职 again 再入职
    detail: Object
  },
  data() {
    return {
      loading: false,
      tabType: '1',
      // 1已入职 2待入职
      tabList: [{
        label: '新建在职员工',
        name: '1'
      }, {
        label: '新建待入职员工',
        name: '2'
      }],
      baseFields: [],
      baseRules: {},
      baseForm: {},

      // 入职
      entryFields: [],
      entryRules: {},
      entryForm: {},
      channelList: [] // 招聘渠道数据
    }
  },
  computed: {
    ...mapGetters(['manage']),
    // 是 tab 效果新建
    isTabCreate() {
      return this.createType == 'new' || this.createType == 'wait'
    },
    title() {
      return {
        again: '再入职',
        confirm: '确认入职'
      }[this.createType]
    },
    // 展示字段设置
    fieldsSetShow() {
      return this.isTabCreate && this.manage && this.manage.hrm && this.manage.hrm.archives
    },
    id() {
      return this.detail ? this.detail.employeeId : ''
    }
  },
  watch: {},
  created() {
    // 如果是待入职  默认展示 新建待入职员工
    if (this.createType === 'wait') {
      this.tabType = '2'
    }
  },
  mounted() {
    this.getAddFields()
  },

  beforeDestroy() {},
  methods: {
    /**
     * 获取添加字段
     */
    getAddFields() {
      // 除待入职 其他都传2
      this.loading = true
      hrmEmployeeAddFieldsAPI({
        entryStatus: this.tabType
      }).then(res => {
        const baseFields = []
        const baseRules = {}
        let baseForm = {}

        // 入职
        const entryFields = []
        const entryRules = {}
        let entryForm = {}

        const resData = res.data || []
        const assistIds = this.getFormAssistIds(resData)

        resData.forEach(item => {
          this.handleFieldExtendList(item)
          const temp = this.getFormItemDefaultProperty(item)
          temp.show = !assistIds.includes(item.formAssistId)

          // 特殊字段允许多选
          this.getItemRadio(item, temp)
          if ((item.formType === 'structure' && item.fieldName === 'dept_id') ||
          (item.formType === 'user' && item.fieldName === 'parent_id')) {
            temp.radio = true
          }

          temp.disabled = false

          employField.getCreateFieldDefalutData(temp)

          if (temp.formType === 'area') {
            temp.formType = 'address' // 和 address 类型功能一直
          }

          if (temp.label === 1) {
            baseFields.push(temp)

            // 籍贯
            if (temp.formType == 'city') {
              temp.defaultValue = {
                province: '',
                city: ''
              }
            }

            // 是否能编辑权限
            if (temp.show) {
              baseRules[temp.field] = this.getRules(item)
              baseForm[temp.field] = this.getItemValue(temp)
            }
          } else {
            entryFields.push(temp)

            if (temp.formType == 'recruit_channel') { // 'recruit_channel' 招聘渠道
              temp.formType = 'select'
              if (this.channelList && this.channelList.length > 0) {
                temp.setting = this.channelList
              } else {
                this.getChannelDetail(temp)
              }
            }

            if (temp.show) {
              entryRules[temp.field] = this.getRules(item)
              entryForm[temp.field] = this.getItemValue(temp)
            }
          }
        })

        // 填充值
        // confirm 确认入职  again 再入职  需要当前员工详情
        if (this.createType === 'again' || this.createType === 'confirm') {
          if (this.detail) {
            const detail = objDeepCopy(this.detail)
            const baseDetailForm = {}
            baseFields.forEach(item => {
              // humpField 用于取值
              const humpField = this.toHump(item.field)
              if (item.field == 'native_place') {
                // 籍贯
                const citys = detail[humpField]
                if (citys) {
                  const citys = citys.split(',')
                  baseDetailForm.native_place = {
                    province: citys[0] || '',
                    city: citys[1] || ''
                  }
                } else {
                  baseDetailForm.native_place = {}
                }
              } else {
                baseDetailForm[item.field] = detail[humpField]
              }
            })
            baseForm = { ...baseForm, ...baseDetailForm }

            const entryDetailForm = { employment_forms: detail.employment_forms }
            entryFields.forEach(item => {
              // humpField 用于取值
              const humpField = this.toHump(item.field)
              // 工作城市
              if (item.field == 'work_city') {
                const cityValue = detail[humpField]
                if (cityValue) {
                  const citys = cityValue.split(',')
                  entryDetailForm.work_city = {
                    province: citys[0] || '',
                    city: citys[1] || '',
                    area: citys[2] || ''
                  }
                } else {
                  entryDetailForm.work_city = {}
                }
              } else {
                entryDetailForm[item.field] = detail[humpField]
              }
            })
            // 超出范围的非正式状态 改为空值
            if (detail.employment_forms == 2 && (detail.status < 3 || detail.status > 8)) {
              entryDetailForm.status = ''
            }
            entryForm = { ...entryForm, ...entryDetailForm }
          }
        } else if (this.createType === 'wait') {
          // 候选人自动填充的字段：姓名、性别、手机号码、个人邮箱
          if (this.detail) {
            const detail = objDeepCopy(this.detail)
            baseForm = {
              ...baseForm,
              ...{
                id_type: 1,
                employee_name: detail.candidateName,
                mobile: detail.mobile,
                email: detail.email,
                sex: detail.sex
              }
            }
          }
        }

        this.baseFields = baseFields
        this.baseRules = baseRules
        this.baseForm = baseForm

        // 入职
        this.entryFields = entryFields
        this.entryRules = entryRules
        this.entryForm = entryForm

        this.handleProbationAndTypeDisabled(entryFields, entryForm)
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },

    // 下划线转换驼峰
    toHump(name) {
      return name.replace(/\_(\w)/g, function(all, letter) {
        return letter.toUpperCase()
      })
    },

    /**
     * 处理 试用期 非正式类型 disabled
     */
    handleProbationAndTypeDisabled(entryFields, entryForm) {
      const employment_forms = entryForm.employment_forms
      entryFields.forEach(item => {
        if (item.fieldName === 'probation') {
          item.disabled = !employment_forms || employment_forms != 1
          if (item.disabled) {
            entryForm.probation = ''
            this.entryRules[item.fieldName] = []
          } else {
            this.entryRules[item.fieldName] = [{
              validator: (rule, value, callback) => {
                if (isEmpty(value)) {
                  callback(new Error(`${item.name}不能为空`))
                } else {
                  callback()
                }
              },
              trigger: ['change']
            }]
          }
        } else if (item.fieldName === 'status') {
          item.disabled = !employment_forms || employment_forms != 2
          if (item.disabled) {
            entryForm.status = ''
            this.entryRules[item.fieldName] = []
          } else {
            this.entryRules[item.fieldName] = [{
              validator: (rule, value, callback) => {
                if (isEmpty(value)) {
                  callback(new Error(`${item.name}不能为空`))
                } else {
                  callback()
                }
              },
              trigger: ['change']
            }]
          }
        }
      })
    },

    /**
     * tab change
     */
    tabChange() {
      this.baseFields = []
      this.baseRules = {}
      this.baseForm = {}

      // 入职
      this.entryFields = []
      this.entryRules = {}
      this.entryForm = {}
      this.getAddFields()
    },

    /**
     * chage
     */
    wkEntryFormChange(item, index, value, valueList) {
      // 部门进行的选择
      if (item.field == 'deptId' || item.field == 'dept_id') {
        console.log('object', item, index, value, valueList)
        if (valueList && valueList.length) {
          const depObj = valueList[0]
          this.$set(this.entryForm, 'parent_id', depObj.mainEmployeeId)
        } else {
          this.$set(this.entryForm, 'parent_id', '')
        }
      } else if (item.field == 'employment_forms') {
        this.handleProbationAndTypeDisabled(this.entryFields, this.entryForm)
      } else if (item.field == 'entry_time') {
        const companyAge = this.entryFields.find(item => item.fieldName === 'company_age_start_time')
        if (companyAge) {
          this.entryForm.company_age_start_time = this.entryForm.entry_time
        }
      }

      if ([
        'select',
        'checkbox'
      ].includes(item.formType) &&
          item.remark === 'options_type' &&
          item.optionsData) {
        const { fieldForm, fieldRules } = this.getFormContentByOptionsChange(this.entryFields, this.entryForm)
        this.entryForm = fieldForm
        this.entryRules = fieldRules
      }
    },

    /**
     * 基础信息change
     */
    wkBaseFormChange(item, index, value, valueList) {
      if ([
        'select',
        'checkbox'
      ].includes(item.formType) &&
          item.remark === 'options_type' &&
          item.optionsData) {
        const { fieldForm, fieldRules } = this.getFormContentByOptionsChange(this.baseFields, this.baseForm)
        this.baseForm = fieldForm
        this.baseRules = fieldRules
      }
    },

    /**
     * 选择省市
     */
    selectProvince(data, item, form) {
      form[item.field].province = data.value
    },

    selectCity(data, item, form) {
      form[item.field].city = data.value
    },

    selectArea(data, item, form) {
      form[item.field].area = data.value
    },

    /**
     * 验证唯一
     */
    UniquePromise({ field, value }) {
      // this.action 详情
      return this.getUniquePromise(field, value, this.action)
    },

    /**
     * 验证唯一
     */
    getUniquePromise(field, value, detail) {
      return new Promise((resolve, reject) => {
        var validatesParams = {}
        validatesParams.fieldId = field.fieldId
        validatesParams.id = this.id
        validatesParams.value = this.getRealParams(field, value)

        hrmEmployeeFieldVerifyAPI(validatesParams).then(res => {
          // status 1 通过 0
          const resData = res.data || {}
          if (resData.status === 1) {
            resolve()
          } else {
            reject(`${field.name}已存在`)
          }
        }).catch(() => {
          reject()
        })
      })
    },

    close() {
      this.$emit('close')
    },

    saveClick(saveContinue = false) {
      this.$refs.baseForm.validate(valid => {
        if (valid) {
          this.$refs.entryForm.validate(valid => {
            if (valid) {
              this.uploadCreateData(saveContinue)
            } else {
              // 提示第一个error
              this.getFormErrorMessage(this.$refs.entryForm)
              return false
            }
          })
        } else {
          // 提示第一个error
          this.getFormErrorMessage(this.$refs.baseForm)
          return false
        }
      })
    },

    /**
     * 提交数据
     */
    uploadCreateData(saveContinue) {
      const entryForm = objDeepCopy(this.entryForm)
      const baseForm = objDeepCopy(this.baseForm)
      if (entryForm.employment_forms == 1) {
        delete entryForm['status']
      } else if (entryForm.employment_forms == 2) {
        delete entryForm['probation']
      }

      // 工作城市
      if (entryForm.work_city) {
        const keys = Object.keys(entryForm.work_city)
        let work_city = ''
        keys.forEach(key => {
          if (entryForm.work_city[key]) {
            if (!work_city) {
              work_city = entryForm.work_city[key]
            } else {
              work_city += `,${entryForm.work_city[key]}`
            }
          }
        })
        entryForm.work_city = work_city
      } else {
        entryForm.work_city = ''
      }

      // 籍贯
      if (baseForm.native_place) {
        const address = baseForm.native_place
        const addressList = []
        for (const key in address) {
          if (address[key]) {
            addressList.push(address[key])
          }
        }
        baseForm.native_place = addressList.join(',')
      } else {
        baseForm.native_place = ''
      }

      const entryFormParams = this.getSubmiteParams([].concat.apply([], this.entryFields), entryForm, ['isFixed', 'labelGroup'])
      const baseFormParams = this.getSubmiteParams([].concat.apply([], this.baseFields), baseForm, ['isFixed', 'labelGroup'])

      const baseDisabledFields = []
      this.baseFields.forEach(item => {
        if (item.disabled) {
          baseDisabledFields.push(item.fieldName)
        }
      })
      const employeeFieldList = baseFormParams.field.filter(item => !baseDisabledFields.includes(item.fieldName))
      employeeFieldList.forEach(item => {
        // 表格字段value 和 filed
        if (item.type === 45) {
          const tableValue = item.value
          if (isArray(tableValue)) {
            tableValue.forEach(children => {
              if (children) {
                children.forEach(child => {
                  if (child) {
                    child.fieldValue = child.value
                  }
                })
              }
            })
          }
        }
        item.fieldValue = item.value
        delete item.value
      })

      const entryDisabledFields = []
      this.entryFields.forEach(item => {
        if (item.disabled) {
          entryDisabledFields.push(item.fieldName)
        }
      })
      const postFieldList = entryFormParams.field.filter(item => !entryDisabledFields.includes(item.fieldName))
      postFieldList.forEach(item => {
        // 表格字段value 和 filed
        if (item.type === 45) {
          const tableValue = item.value
          if (isArray(tableValue)) {
            tableValue.forEach(children => {
              if (children) {
                children.forEach(child => {
                  if (child) {
                    child.fieldValue = child.value
                  }
                })
              }
            })
          }
        }
        item.fieldValue = item.value
        delete item.value
      })

      const params = { employeeFieldList, postFieldList }
      if (this.detail) {
        params.employeeId = this.detail.employeeId
      }

      // 如果是待入职 传候选人id
      if (this.createType == 'wait') {
        params.candidateId = this.detail.candidateId
      }

      // 加入工号
      params.jobNumber = entryForm.job_number

      this.loading = true
      let request = null
      if (this.createType == 'new' || this.createType == 'wait') {
        request = hrmEmployeeFieldsAddAPI

        // 在职 和 待入职 2
        params.entryStatus = this.tabType

        if (this.tabType == '2') {
        // 删掉工号信息
          delete params['jobNumber']
        }
      } else if (this.createType == 'again') {
        request = hrmEmployeeAgainOnboardingAPI
      } else if (this.createType == 'confirm') {
        request = hrmEmployeeConfirmEntryAPI
      }

      request(params).then(res => {
        if (saveContinue) {
          this.tabChange()
        } else {
          this.$emit('close')
        }

        this.$message.success('新建成功')
        this.$emit('success')
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * 获取字段值对象
     */
    getFieldValueObj(data, form) {
      console.log(data, form)
      return {
        fieldId: data.fieldId,
        fieldName: data.fieldName,
        fieldValue: form[data.fieldName],
        fieldValueDesc: '',
        isFixed: data.isFixed,
        labelGroup: data.labelGroup,
        name: data.name,
        oldFieldValueDesc: '',
        type: data.type
      }
    },

    /**
     * 保存并继续新建
     */
    saveContinueClick() {
      this.saveClick(true)
    },

    /**
     * 进入新建设置
     */
    createSetClick() {
      this.$router.push({
        name: 'employeeManageSet',
        query: {
          menuIndex: 'EmployeeFields',
          entryStatus: this.tabType, // 入职状态 1 在职 2 待入职
          redirect: '/hrm/employee'
        }
      })
    },

    /**
     * 处理表格数据块数据
     */
    handleFieldExtendList(item) {
      // 表格为了展示，提前处理为编辑数据
      if (item.formType === 'detail_table') {
        if (!isEmpty(item.fieldExtendList)) {
          const fieldExtendList = item.fieldExtendList
          if (isArray(fieldExtendList)) {
            fieldExtendList.forEach(child => {
              if (child) {
                employField.getCreateFieldDefalutData(child)
                child.value = child.fieldValue
              }
            })
          }
        }
      }
    },

    /**
     * 获取渠道信息
     */
    getChannelDetail(item) {
      return new Promise((resolve, reject) => {
        hrmRecruitChannelQueryAPI()
          .then(res => {
            const channelList = res.data || []
            channelList.forEach(item => {
              if (item.status == 1) {
                item.label = item.value
                item.value = item.channelId
              }
            })
            this.channelList = channelList
            this.$set(item, 'setting', this.channelList)
            resolve(this.channelList)
          })
          .catch(() => {
          })
      })
    }

  }
}
</script>

<style lang="scss" scoped>
::v-deep .el-tabs {
  width: 100%;

  .el-tabs__header {
    margin-bottom: 0;
  }

  .el-tabs__nav-wrap::after {
    display: none;
  }

  .el-tabs__item {
    font-weight: bold;
    color: $--color-text-primary;
  }
}

.wk-form {
  display: flex;
  flex-wrap: wrap;

  ::v-deep.is-detail_table {
    width: 100% !important;
  }
}
</style>
