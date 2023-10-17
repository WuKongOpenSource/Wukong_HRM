<template>
  <div v-loading="loading" class="employee-base-info">
    <wk-reminder :content="reminderContent" />
    <wk-custom-base-detail
      :list="baseList">
      <template slot="data" slot-scope="{ data }">{{ data.value }}</template>
      <el-button v-if="baseEditAuth" slot="more" type="text" @click="baseCommandSelect('edit')">编辑</el-button>
    </wk-custom-base-detail>

    <wk-custom-base-detail
      :dropdown-items="communicationDropdownItems"
      :list="communicationList"
      @top-command-select="communicationCommandSelect"
    />
    <create-sections
      title="教育经历">
      <div v-if="educationList.length === 0" class="empty-text">暂无数据</div>
      <wk-base-detail-section
        v-for="(item, index) in educationList"
        v-else
        :key="index"
        :list="item"
      />
    </create-sections>

    <create-sections
      title="工作经历">
      <div v-if="workList.length === 0" class="empty-text">暂无数据</div>
      <wk-base-detail-section
        v-for="(item, index) in workList"
        v-else
        :key="index"
        :list="item"
      />
    </create-sections>

    <create-sections
      title="证书/证件">
      <div v-if="certificateList.length === 0" class="empty-text">暂无数据</div>
      <wk-base-detail-section
        v-for="(item, index) in certificateList"
        v-else
        :key="index"
        :list="item"
      />
    </create-sections>

    <create-sections
      title="培训经历">
      <div v-if="trainingList.length === 0" class="empty-text">暂无数据</div>
      <wk-base-detail-section
        v-for="(item, index) in trainingList"
        v-else
        :key="index"
        :list="item"
      />
    </create-sections>

    <create-sections
      title="联系人">
      <div v-if="contactsList.length === 0" class="empty-text">暂无数据</div>
      <template v-else>
        <wk-custom-base-detail-section
          v-for="(item, index) in contactsList"
          :key="index"
          :list="item"
        />
      </template>
    </create-sections>

    <form-add-dialog
      ref="formAdddialog"
      :title="formAddTitle"
      :form.sync="formAddForm"
      :rules="formAddRules"
      :fields="formAddFields"
      :visible.sync="formAddDialogShow"
      @pass="formAddPass"
      @change="formAddChange"
    />

    <custom-form-add-dialog
      v-if="customFormAddDialogShow"
      :id="employeeId"
      ref="customFormAdddialog"
      :title="formAddTitle"
      :fields="customFormAddFields"
      :visible.sync="customFormAddDialogShow"
      :help-obj="helpObj"
      @pass="customFormAddPass"
    />
  </div>
</template>

<script>
import {
  hrmEmployeeUpdateInformationAPI,
  hrmEmployeeUpdateCommunicationAPI,
  hrmEmployeeFieldVerifyAPI
} from '@/api/hrm/employee'
import {
  hrmEmployeeArchivesPersonalAPI
} from '@/api/hrm/selfService/myArchives'

import WkCustomBaseDetail from '@/components/WkBaseDetail/CustomIndex'
import CreateSections from '@/components/CreateSections'
import WkBaseDetailSection from '@/components/WkBaseDetail/WkBaseDetailSection'
import WkCustomBaseDetailSection from '@/components/WkBaseDetail/WkCustomBaseDetailSection'
import FormAddDialog from '@/views/hrm/employee/components/FormAddDialog'
import CustomFormAddDialog from '@/views/hrm/employee/components/CustomFormAddDialog'
import WkReminder from '@/components/Reminder'

import CustomFieldsMixin from '@/mixins/CustomFields'
import { isEmpty, isArray } from '@/utils/types'
import {
  employeeModel,
  educationModel,
  workModel,
  certificateModel,
  trainingModel
} from '@/views/hrm/employee/model/employee'
import { objDeepCopy } from '@/utils'
import employField from '@/views/hrm/employee/model/employField'
import { mapGetters } from 'vuex'

export default {
  // 基本信息
  name: 'EmployeeBaseInfo',
  components: {
    CreateSections,
    WkBaseDetailSection,
    FormAddDialog,
    WkReminder,
    WkCustomBaseDetail,
    WkCustomBaseDetailSection,
    CustomFormAddDialog
  },
  filters: {},
  mixins: [CustomFieldsMixin],
  props: {},
  data() {
    return {
      loading: false,
      baseEditAuth: false,
      communicationEditAuth: false,
      baseEditList: [],
      baseList: [],
      communicationEditList: [],
      communicationList: [],
      // 教育
      educationBaseList: [], // 基础数组
      educationList: [],
      // 工作
      workBaseList: [],
      workList: [],
      // 证书
      certificateBaseList: [],
      certificateList: [],
      // 培训
      trainingBaseList: [],
      trainingList: [],
      // 联系人
      contactsBaseList: [],
      contactsFieldList: [], // 联系人添加字段
      contactsAddRules: {},
      contactsList: [],
      // 弹窗添加
      formAddType: '',
      formAddForm: {},
      formAddRules: {},
      formAddFields: [],
      formAddDialogShow: false,
      // 新自定义字段弹窗
      customFormAddFields: [],
      customFormAddDialogShow: false,
      updateKey: Date.now(),
      helpObj: {
        dataType: 35,
        dataId: 299
      }
    }
  },

  computed: {
    ...mapGetters(['hrmUserInfo']),
    reminderContent() {
      if (this.baseEditAuth || this.communicationEditAuth) {
        return '可编辑的信息由公司管理员设置，如有问题，请联系公司管理员。'
      }
      return '您的编辑权限已被管理员关闭，如有问题，请联系公司管理员。'
    },

    communicationDropdownItems() {
      const temps = []
      if (this.communicationEditAuth) {
        temps.push({
          label: '编辑',
          command: 'edit',
          icon: ''
        })
      }
      return temps
    },

    employeeId() {
      return this.hrmUserInfo ? this.hrmUserInfo.employeeId : ''
    },

    // 新建标记弹框标题
    formAddTitle() {
      return {
        'update-basic-info': '编辑基本信息',
        'update-contact-info': '编辑通讯信息'
      }[this.formAddType]
    }
  },
  watch: {},
  mounted() {
    this.$store.dispatch('GetHrmUserInfo')
    this.getDetail()
  },
  methods: {
    /**
     * 获取基础信息
     */
    getDetail() {
      this.loading = true
      hrmEmployeeArchivesPersonalAPI()
        .then(res => {
          this.loading = false
          const data = res.data || {}
          const copyData = objDeepCopy(data)
          this.baseList = [{
            name: '基本信息',
            list: this.getCustomFieldListValue(data.information || [], 'base')
          }]
          this.communicationList = [{
            name: '通讯信息',
            list: this.getCustomFieldListValue(data.communicationInformation || [], 'communication')
          }]

          this.baseEditList = copyData.information || []
          this.communicationEditList = copyData.communicationInformation || []

          // 教育数据
          const educationExperienceList = data.educationExperienceList || []
          const educationList = []
          this.educationBaseList = educationExperienceList
          educationExperienceList.forEach(item => {
            const list = []
            educationModel.fields.forEach(field => {
              const temp = {}
              temp.label = field.name
              if (field.field === 'education') {
                temp.value = educationModel.educationValue[item[field.field]]
              } else if (field.field === 'teachingMethods') {
                temp.value = educationModel.teachingMethodsValue[item[field.field]]
              } else if (field.field === 'isFirstDegree') {
                temp.value = educationModel.isFirstDegreeValue[item[field.field]]
              } else {
                temp.value = item[field.field]
              }
              list.push(temp)
            })
            educationList.push(list)
          })
          this.educationList = educationList

          // 工作数据
          const workExperienceList = data.workExperienceList || []
          this.workBaseList = workExperienceList
          this.workList = this.getCommonFieldListValue(workExperienceList, workModel.fields)

          // 证书/证件数据
          const cList = data.certificateList || []
          this.certificateBaseList = cList
          this.certificateList = this.getCommonFieldListValue(cList, certificateModel.fields)

          // 培训数据
          const trainingExperienceList = data.trainingExperienceList || []
          this.trainingBaseList = trainingExperienceList
          this.trainingList = this.getCommonFieldListValue(trainingExperienceList, trainingModel.fields)

          // 联系人
          const contactsList = data.hrmEmployeeContacts || []
          this.contactsBaseList = copyData.hrmEmployeeContacts || []
          this.contactsList = contactsList.map(item => {
            return this.getCustomFieldListValue(item.information || [])
          })
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 自定义字段值
     */
    getCustomFieldListValue(list, type) {
      let editAuth = false

      list.forEach(item => {
        if (!editAuth && item.isEmployeeUpdate === 1) {
          editAuth = true
        }

        if (['id_type', 'sex', 'birthday_type', 'highest_education'].includes(item.fieldName)) {
          item.value = item.fieldValueDesc
        } else {
          item.value = item.fieldValue
        }
      })

      if (type === 'base') {
        this.baseEditAuth = editAuth
      } else if (type === 'communication') {
        this.communicationEditAuth = editAuth
      }

      // 逻辑表单逻辑
      const assistIds = this.getFormAssistIds([list])
      const showPostList = []
      list.forEach(item => {
        this.getFormItemDefaultProperty(item, false)
        item.show = !assistIds.includes(item.formAssistId)
        // 表格为了展示，提前处理为编辑数据
        if (item.formType === 'detail_table') {
          if (!isEmpty(item.value)) {
            const tableValue = item.value
            if (isArray(tableValue)) {
              tableValue.forEach(children => {
                if (children) {
                  children.forEach(child => {
                    if (child) {
                      child.value = child.fieldValue
                    }
                  })
                }
              })
            }
            item.value = this.getItemValue(item, null, 'update')
          }

          if (!isArray(item.value)) {
            item.value = []
          }
        }

        if (item.show) {
          showPostList.push(item)
        }
      })
      return showPostList
    },

    /**
     *常规字段值
     */
    getCommonFieldListValue(dataList, fieldList) {
      const resultList = []
      dataList.forEach(item => {
        const list = []
        fieldList.forEach(field => {
          const temp = {}
          temp.label = field.name
          temp.value = item[field.field]
          list.push(temp)
        })
        resultList.push(list)
      })
      return resultList
    },

    /**
     * 验证通过发送请求
     */
    formAddPass() {
      this.uploadCustomRelativeData()
    },

    formAddChange(item, index, value, valueList) {
      if (valueList) {
        item.valueList = valueList
      }
    },

    /**
     * 自定义字段保存
     */
    customFormAddPass(data) {
      const dataList = objDeepCopy(data.field)
      dataList.forEach(item => {
        if (item.fieldName == 'native_place') {
          if (item.value) {
            const keys = Object.keys(item.value)
            let address = ''
            keys.forEach(key => {
              if (item.value[key]) {
                if (!address) {
                  address = item.value[key]
                } else {
                  address += `,${item.value[key]}`
                }
              }
            })
            item.value = address
          } else {
            item.value = ''
          }
        }

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

        delete item.fieldType
        item.fieldValue = item.value
        delete item.value
      })

      const dialog = this.$refs.customFormAdddialog
      dialog.loading = true
      console.log(dataList)
      const params = {}
      params.employeeId = this.employeeId
      params.dataList = dataList

      let request = null
      if (this.formAddType === 'update-basic-info') {
        request = hrmEmployeeUpdateInformationAPI
      } else if (this.formAddType === 'update-contact-info') {
        request = hrmEmployeeUpdateCommunicationAPI
      }

      request(params).then(res => {
        dialog.loading = false
        this.$message.success(`${this.formAddTitle}成功`)
        this.customFormAddDialogShow = false
        this.getDetail()
        this.updateKey = Date.now()
        this.$emit('handle', { type: 'update' })
      }).catch(() => {
        dialog.loading = false
      })
    },

    /**
     * 编辑基本信息
     */
    baseCommandSelect(command) {
      if (command == 'edit') {
        const baseEditList = objDeepCopy(this.baseEditList)
        const fields = []
        baseEditList.forEach(item => {
          if (item.fieldName != 'age') {
            this.handleFieldExtendList(item)
            employField.getCreateFieldDefalutData(item)
            if (item.hasOwnProperty('isEmployeeUpdate')) {
              item.disabled = item.isEmployeeUpdate != 1
            }
            item.value = item.fieldValue

            this.handleTableValue(item)

            if (item.formType == 'city') {
              if (item.value) {
                const citys = item.value.split(',')
                item.value = {
                  province: citys[0] || '',
                  city: citys[1] || ''
                }
              } else {
                item.value = {}
              }
            }

            fields.push(item)
          }
        })

        this.formAddType = 'update-basic-info'
        this.customFormAddFields = fields
        this.customFormAddDialogShow = true
      }
    },

    /**
     * 处理表格数据 为了转换 value 和 fieldValue
     */
    handleTableValue(item) {
      // 表格为了展示，提前处理为编辑数据
      if (item.formType === 'detail_table') {
        if (!isEmpty(item.value)) {
          const tableValue = item.value
          if (isArray(tableValue)) {
            tableValue.forEach(children => {
              if (children) {
                children.forEach(child => {
                  if (child) {
                    employField.getCreateFieldDefalutData(child)
                    if (item.hasOwnProperty('isEmployeeUpdate')) {
                      child.disabled = item.isEmployeeUpdate != 1
                    }
                    child.value = child.fieldValue
                  }
                })
              }
            })
          }
        }
        if (!isArray(item.value)) {
          item.value = []
        }
      }
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
                if (item.hasOwnProperty('isEmployeeUpdate')) {
                  child.disabled = item.isEmployeeUpdate != 1
                }
                child.value = child.fieldValue
              }
            })
          }
        }
      }
    },

    /**
     * 编辑通讯信息
     */
    communicationCommandSelect(command) {
      if (command == 'edit') {
        const communicationEditList = objDeepCopy(this.communicationEditList)
        const fields = []
        communicationEditList.forEach(item => {
          this.handleFieldExtendList(item)
          employField.getCreateFieldDefalutData(item)
          if (item.hasOwnProperty('isEmployeeUpdate')) {
            item.disabled = item.isEmployeeUpdate != 1
          }
          item.value = item.fieldValue

          this.handleTableValue(item)

          fields.push(item)
        })

        this.formAddType = 'update-contact-info'
        this.customFormAddFields = fields
        this.customFormAddDialogShow = true
      }
    },

    /**
     * 验证唯一
     */
    UniquePromise({ field, value }) {
      return new Promise((resolve, reject) => {
        hrmEmployeeFieldVerifyAPI({
          fieldId: field.fieldId,
          id: this.employeeId, // 员工下 传员工id
          value
        }).then(response => {
          resolve()
        }).catch(() => {
          reject()
        })
      })
    },

    /**
     * 提交自定义数据
     */
    uploadCustomRelativeData() {
      this.$refs.formAdddialog.loading = true
      const params = {
        employeeId: this.employeeId
      }

      this.formAddFields.forEach(item => {
        item.oldFieldValueDesc = item.fieldValueDesc
        if (item.formType == 'city') {
          const address = this.formAddForm[item.field] || {}
          item.fieldValue = address.province ? address.province + ',' + address.city : ''
        } else {
          item.fieldValue = this.formAddForm[item.field]
        }

        if (item.formType == 'user') {
          if (item.valueList) {
            item.fieldValueDesc = item.valueList.map(item => item.employeeName).join('，')
          } else if (item.fieldValue && !item.valueList) {
            // item.fieldValueDesc = item.fieldValueDesc
          }
        } else if (item.formType == 'structure') {
          if (item.valueList) {
            item.fieldValueDesc = item.valueList.map(item => item.name).join('，')
          } else if (item.fieldValue && !item.valueList) {
            // item.fieldValueDesc = item.fieldValueDesc
          }
        } else if (item.field == 'id_type') {
          item.fieldValueDesc = employeeModel.idTypeValue[item.fieldValue]
        } else if (item.field == 'sex') {
          item.fieldValueDesc = employeeModel.sexValue[item.fieldValue]
        } else if (item.field == 'birthday_type') {
          item.fieldValueDesc = employeeModel.birthdayTypeValue[item.fieldValue]
        } else if (item.field == 'highest_education') {
          item.fieldValueDesc = educationModel.educationValue[item.fieldValue]
        } else {
          item.fieldValueDesc = item.fieldValue
        }
      })
      params.dataList = this.formAddFields

      let request = null
      if (this.formAddType === 'update-basic-info') {
        request = hrmEmployeeUpdateInformationAPI
      } else if (this.formAddType === 'update-contact-info') {
        request = hrmEmployeeUpdateCommunicationAPI
      }

      request(params).then(res => {
        this.$refs.formAdddialog.loading = false
        this.$message.success(`${this.formAddTitle}成功`)
        this.formAddDialogShow = false
        this.getDetail()
      }).catch(() => {
        this.$refs.formAdddialog.loading = false
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.employee-base-info {
  position: relative;
}

.base-add-button {
  width: 100%;
  padding: 11px 12px;
  margin: 10px 0;
}

.wk-base-detail-section + .wk-base-detail-section {
  border-top: 1px solid $--border-color-base;
}

.create-sections {
  .empty-text {
    line-height: 60px;
    color: $--color-text-secondary;
    text-align: center;
  }
}
</style>
