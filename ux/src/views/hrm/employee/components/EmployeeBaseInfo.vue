<template>
  <div :key="updateKey" v-loading="loading">
    <wk-custom-base-detail
      :list="baseList">
      <template slot="data" slot-scope="{ data }">{{ data.value }}</template>
      <el-button v-if="editAuth" slot="more" type="text" @click="baseCommandSelect('edit')">编辑</el-button>
    </wk-custom-base-detail>

    <wk-custom-base-detail
      :dropdown-items="communicationDropdownItems"
      :list="communicationList"
      @top-command-select="communicationCommandSelect"
    />
    <create-sections
      title="教育经历">
      <wk-base-detail-section
        v-for="(item, index) in educationList"
        :key="index"
        :dropdown-items="dropdownItems"
        :list="item"
        @command-select="commandSelect($event, 'education', index)"
      />
      <el-button
        v-if="editAuth"
        class="base-add-button"
        plain
        @click="educationCreateClick">+ 添加教育经历</el-button>
    </create-sections>

    <create-sections
      title="工作经历">
      <wk-base-detail-section
        v-for="(item, index) in workList"
        :key="index"
        :dropdown-items="dropdownItems"
        :list="item"
        @command-select="commandSelect($event, 'work', index)"
      />
      <el-button
        v-if="editAuth"
        class="base-add-button"
        plain
        @click="workCreateClick">+ 添加工作经历</el-button>
    </create-sections>

    <create-sections
      title="证书/证件">
      <wk-base-detail-section
        v-for="(item, index) in certificateList"
        :key="index"
        :dropdown-items="dropdownItems"
        :list="item"
        @command-select="commandSelect($event, 'certificate', index)"
      />
      <el-button
        v-if="editAuth"
        class="base-add-button"
        plain
        @click="certificateCreateClick">+ 添加证书/证件</el-button>
    </create-sections>

    <create-sections
      title="培训经历">
      <wk-base-detail-section
        v-for="(item, index) in trainingList"
        :key="index"
        :dropdown-items="dropdownItems"
        :list="item"
        @command-select="commandSelect($event, 'training', index)"
      />
      <el-button
        v-if="editAuth"
        class="base-add-button"
        plain
        @click="trainingCreateClick">+ 添加培训经历</el-button>
    </create-sections>

    <create-sections
      title="联系人">
      <wk-custom-base-detail-section
        v-for="(item, index) in contactsList"
        :key="index"
        :dropdown-items="dropdownItems"
        :list="item"
        @command-select="commandSelect($event, 'contacts', index)"
      />
      <el-button
        v-if="editAuth"
        class="base-add-button"
        plain
        @click="contactsCreateClick">+ 联系人</el-button>
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
      :id="getCustomEditId()"
      ref="customFormAdddialog"
      :title="formAddTitle"
      :fields="customFormAddFields"
      :visible.sync="customFormAddDialogShow"
      @pass="customFormAddPass"
    />
  </div>
</template>

<script>
import {
  hrmEmployeePersonalInfoAPI,
  // 教育
  hrmEmployeeAddEduAPI,
  hrmEmployeeSetEduAPI,
  hrmEmployeeDeleteEduAPI,
  // 工作
  hrmEmployeeAddWorkAPI,
  hrmEmployeeSetWorkeAPI,
  hrmEmployeeDeleteWorkAPI,
  // 证书
  hrmEmployeeAddCertificateAPI,
  hrmEmployeeSetCertificateeAPI,
  hrmEmployeeDeleteCertificateAPI,
  // 培训
  hrmEmployeeAddTrainingAPI,
  hrmEmployeeSetTrainingAPI,
  hrmEmployeeDeleteTrainingAPI,
  // 联系人
  hrmEmployeeQueryContactsAddFieldAPI,
  hrmEmployeeAddContactsAPI,
  hrmEmployeeSetContactsAPI,
  hrmEmployeeDeleteContactsAPI,

  hrmEmployeeUpdateInformationAPI,
  hrmEmployeeUpdateCommunicationAPI,
  hrmEmployeeFieldVerifyAPI
} from '@/api/hrm/employee'

import WkCustomBaseDetail from '@/components/WkBaseDetail/CustomIndex'
import CreateSections from '@/components/CreateSections'
import WkBaseDetailSection from '@/components/WkBaseDetail/WkBaseDetailSection'
import WkCustomBaseDetailSection from '@/components/WkBaseDetail/WkCustomBaseDetailSection'
import FormAddDialog from './FormAddDialog'
import CustomFormAddDialog from './CustomFormAddDialog'

import CustomFieldsMixin from '@/mixins/CustomFields'
import { isEmpty, isArray } from '@/utils/types'
import {
  employeeModel,
  educationModel,
  workModel,
  certificateModel,
  trainingModel
} from '../model/employee'
import { objDeepCopy } from '@/utils'
import employField from '../model/employField'

export default {
  // 基本信息
  name: 'EmployeeBaseInfo',
  components: {
    CreateSections,
    WkBaseDetailSection,
    FormAddDialog,
    WkCustomBaseDetail,
    WkCustomBaseDetailSection,
    CustomFormAddDialog
  },
  filters: {},
  mixins: [CustomFieldsMixin],

  inject: ['editAuth'],
  props: {
    id: [String, Number]
  },
  data() {
    return {
      loading: false,
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
      formAddType: '', // 添加类型
      formAddForm: {},
      formAddRules: {},
      formAddFields: [],
      formAddDialogShow: false,
      // 新自定义字段弹窗
      customFormAddFields: [],
      customFormAddDialogShow: false,
      updateKey: Date.now()
    }
  },

  computed: {
    baseDropdownItems() {
      const temps = []
      if (this.editAuth) {
        temps.push({
          label: '编辑',
          command: 'edit',
          icon: ''
        })
      }
      return temps
    },

    communicationDropdownItems() {
      const temps = []
      if (this.editAuth) {
        temps.push({
          label: '编辑',
          command: 'edit',
          icon: ''
        })
      }
      return temps
    },

    // 事项操作
    dropdownItems() {
      return this.editAuth ? [{
        label: '编辑',
        command: 'edit'
      }, {
        label: '删除',
        command: 'delete'
      }] : []
    },

    // 新建标记弹框标题
    formAddTitle() {
      return {
        'create-education-experience': '新建教育经历',
        'update-education-experience': '编辑教育经历',
        'create-work-experience': '新建工作经历',
        'update-work-experience': '编辑工作经历',
        'create-certificate': '新建证书/证件',
        'update-certificate': '编辑证书/证件',
        'create-training-experience': '添加培训经历',
        'update-training-experience': '编辑培训经历',
        'create-contacts': '新建联系人',
        'update-contacts': '编辑联系人',
        'update-basic-info': '编辑基本信息',
        'update-contact-info': '编辑通讯信息'
      }[this.formAddType]
    }
  },
  watch: {
    id: function(val) {
      this.getDetail()
    }
  },
  mounted() {
    this.getDetail()
  },
  activated: function() {
    console.log(3)
  },
  deactivated: function() {
    console.log(4)
  },
  methods: {
    /**
     * 获取基础信息
     */
    getDetail() {
      this.loading = true
      hrmEmployeePersonalInfoAPI(this.id)
        .then(res => {
          this.loading = false
          const data = res.data || {}
          const copyData = objDeepCopy(data)
          this.baseList = [{
            name: '基本信息',
            list: this.getCustomFieldListValue(data.information || [])
          }]
          this.communicationList = [{
            name: '通讯信息',
            list: this.getCustomFieldListValue(data.communicationInformation || [])
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

          // 部分window 不能滚动的问题
          this.updateKey = Date.now()
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 自定义字段值
     */
    getCustomFieldListValue(list) {
      list.forEach(item => {
        if (['id_type', 'sex', 'birthday_type', 'highest_education'].includes(item.fieldName)) {
          item.value = item.fieldValueDesc
        } else {
          item.value = item.fieldValue
        }
      })

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

    /** ***** 教育 */
    educationCreateClick() {
      this.formAddType = 'create-education-experience'
      this.formAddForm = {
        isFirstDegree: 1,
        education: 8 // 默认本科8
      }
      this.formAddRules = educationModel.rules
      this.formAddFields = educationModel.fields
      this.formAddDialogShow = true
    },

    /**
     * 教育提交数据
     */
    uploadEmployeeRelativeData() {
      this.$refs.formAdddialog.loading = true
      this.formAddForm.employeeId = this.id
      let request = null
      if (this.formAddType === 'create-education-experience') {
        request = hrmEmployeeAddEduAPI
      } else if (this.formAddType === 'update-education-experience') {
        request = hrmEmployeeSetEduAPI
      } else if (this.formAddType === 'create-work-experience') {
        request = hrmEmployeeAddWorkAPI
      } else if (this.formAddType === 'update-work-experience') {
        request = hrmEmployeeSetWorkeAPI
      } else if (this.formAddType === 'create-certificate') {
        request = hrmEmployeeAddCertificateAPI
      } else if (this.formAddType === 'update-certificate') {
        request = hrmEmployeeSetCertificateeAPI
      } else if (this.formAddType === 'create-training-experience') {
        request = hrmEmployeeAddTrainingAPI
      } else if (this.formAddType === 'update-training-experience') {
        request = hrmEmployeeSetTrainingAPI
      } else if (this.formAddType === 'create-contacts') {
        request = hrmEmployeeAddContactsAPI
      } else if (this.formAddType === 'update-contacts') {
        request = hrmEmployeeSetContactsAPI
      }
      request(this.formAddForm).then(res => {
        this.$refs.formAdddialog.loading = false
        this.$message.success(`${this.formAddTitle}成功`)
        this.formAddDialogShow = false
        this.updateKey = Date.now()
        this.getDetail()
      }).catch(() => {
        this.$refs.formAdddialog.loading = false
      })
    },

    /** *** 工作经历 */
    workCreateClick() {
      this.formAddType = 'create-work-experience'
      this.formAddForm = {
        isFirstDegree: 1,
        education: 1
      }
      this.formAddRules = workModel.rules
      this.formAddFields = workModel.fields
      this.formAddDialogShow = true
    },

    /** *** 证书/证件 */
    certificateCreateClick() {
      this.formAddType = 'create-certificate'
      this.formAddForm = {}
      this.formAddRules = certificateModel.rules
      this.formAddFields = certificateModel.fields
      this.formAddDialogShow = true
    },

    /** *** 培训经历 */
    trainingCreateClick() {
      this.formAddType = 'create-training-experience'
      this.formAddForm = {}
      this.formAddRules = trainingModel.rules
      this.formAddFields = trainingModel.fields
      this.formAddDialogShow = true
    },

    /**
     * 联系人
     */
    contactsCreateClick() {
      hrmEmployeeQueryContactsAddFieldAPI().then(res => {
        const information = res.data || []
        const fields = []

        information.forEach(item => {
          this.handleFieldExtendList(item)
          employField.getCreateFieldDefalutData(item)
          item.value = item.fieldValue

          this.handleTableValue(item)

          fields.push(item)
        })

        this.formAddType = 'create-contacts'

        this.formAddForm = {}
        this.customFormAddFields = fields
        this.customFormAddDialogShow = true
      }).catch(() => {
      })
    },

    /**
     * 获取编辑id
     */
    getCustomEditId() {
      if (this.formAddType === 'create-contacts') {
        return ''
      }

      return this.formAddForm.contactsId || this.id
    },

    /**
     * 验证通过发送请求
     */
    formAddPass() {
      if (this.formAddType == 'update-basic-info' ||
      this.formAddType == 'update-contacts' ||
      this.formAddType == 'create-contacts' ||
      this.formAddType == 'update-contact-info') {
        this.uploadCustomRelativeData()
      } else {
        this.uploadEmployeeRelativeData()
      }
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
      params.employeeId = this.id
      params.dataList = dataList

      let request = null
      if (this.formAddType === 'update-basic-info') {
        request = hrmEmployeeUpdateInformationAPI
      } else if (this.formAddType === 'update-contacts') {
        request = hrmEmployeeSetContactsAPI
        params.contactsId = this.formAddForm.contactsId
      } else if (this.formAddType === 'create-contacts') {
        request = hrmEmployeeAddContactsAPI
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

    /** *** 编辑删除操作 */
    commandSelect(command, type, index) {
      if (type == 'education') {
        const data = this.educationBaseList[index]
        if (command == 'delete') {
          this.$confirm('确定删除?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          })
            .then(() => {
              hrmEmployeeDeleteEduAPI(data.educationId).then(res => {
                this.$message.success('删除成功')
                this.educationBaseList.splice(index, 1)
                this.educationList.splice(index, 1)
              })
            })
            .catch(() => {})
        } else if (command == 'edit') {
          this.formAddType = 'update-education-experience'
          this.formAddForm = {
            education: data.education,
            graduateSchool: data.graduateSchool,
            major: data.major,
            admissionTime: data.admissionTime,
            graduationTime: data.graduationTime,
            teachingMethods: data.teachingMethods,
            isFirstDegree: data.isFirstDegree,
            educationId: data.educationId
          }
          this.formAddRules = educationModel.rules
          this.formAddFields = educationModel.fields
          this.formAddDialogShow = true
        }
      } else if (type == 'work') {
        const data = this.workBaseList[index]
        if (command == 'delete') {
          this.$confirm('确定删除?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          })
            .then(() => {
              hrmEmployeeDeleteWorkAPI(data.workExpId).then(res => {
                this.$message.success('删除成功')
                this.workBaseList.splice(index, 1)
                this.workList.splice(index, 1)
              })
            })
            .catch(() => {})
        } else if (command == 'edit') {
          this.formAddType = 'update-work-experience'
          this.formAddForm = {
            workUnit: data.workUnit,
            post: data.post,
            workStartTime: data.workStartTime,
            workEndTime: data.workEndTime,
            leavingReason: data.leavingReason,
            witness: data.witness,
            witnessPhone: data.witnessPhone,
            workRemarks: data.workRemarks,
            workExpId: data.workExpId
          }
          this.formAddRules = workModel.rules
          this.formAddFields = workModel.fields
          this.formAddDialogShow = true
        }
      } else if (type == 'certificate') {
        const data = this.certificateBaseList[index]
        if (command == 'delete') {
          this.$confirm('确定删除?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          })
            .then(() => {
              hrmEmployeeDeleteCertificateAPI(data.certificateId).then(res => {
                this.$message.success('删除成功')
                this.certificateBaseList.splice(index, 1)
                this.certificateList.splice(index, 1)
              })
            })
            .catch(() => {})
        } else if (command == 'edit') {
          this.formAddType = 'update-certificate'
          this.formAddForm = {
            certificateName: data.certificateName,
            certificateLevel: data.certificateLevel,
            certificateNum: data.certificateNum,
            startTime: data.startTime,
            endTime: data.endTime,
            issuingAuthority: data.issuingAuthority,
            issuingTime: data.issuingTime,
            remarks: data.remarks,
            certificateId: data.certificateId
          }
          this.formAddRules = certificateModel.rules
          this.formAddFields = certificateModel.fields
          this.formAddDialogShow = true
        }
      } else if (type == 'training') {
        const data = this.trainingBaseList[index]
        if (command == 'delete') {
          this.$confirm('确定删除?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          })
            .then(() => {
              hrmEmployeeDeleteTrainingAPI(data.trainingId).then(res => {
                this.$message.success('删除成功')
                this.trainingBaseList.splice(index, 1)
                this.trainingList.splice(index, 1)
              })
            })
            .catch(() => {})
        } else if (command == 'edit') {
          this.formAddType = 'update-training-experience'
          this.formAddForm = {
            trainingCourse: data.trainingCourse,
            trainingOrganName: data.trainingOrganName,
            startTime: data.startTime,
            endTime: data.endTime,
            trainingDuration: data.trainingDuration,
            trainingResults: data.trainingResults,
            trainingCertificateName: data.trainingCertificateName,
            remarks: data.remarks,
            trainingId: data.trainingId
          }
          this.formAddRules = trainingModel.rules
          this.formAddFields = trainingModel.fields
          this.formAddDialogShow = true
        }
      } else if (type == 'contacts') {
        const data = objDeepCopy(this.contactsBaseList[index])
        if (command == 'delete') {
          this.$confirm('确定删除?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          })
            .then(() => {
              hrmEmployeeDeleteContactsAPI(data.contactsId).then(res => {
                this.$message.success('删除成功')
                this.contactsBaseList.splice(index, 1)
                this.contactsList.splice(index, 1)
              })
            })
            .catch(() => {})
        } else if (command == 'edit') {
          const information = objDeepCopy(data.information || [])
          const fields = []

          information.forEach(item => {
            this.handleFieldExtendList(item)
            employField.getCreateFieldDefalutData(item)
            item.value = item.fieldValue

            this.handleTableValue(item)

            fields.push(item)
          })

          this.formAddType = 'update-contacts'

          this.formAddForm = {
            contactsId: data.contactsId
          }
          this.customFormAddFields = fields
          this.customFormAddDialogShow = true
        }
      }
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
          id: this.formAddForm.contactsId || this.id, // 联系人下传联系人id 员工下 传员工id
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
        employeeId: this.id
      }

      this.formAddFields.forEach(item => {
        item.oldFieldValueDesc = item.fieldValueDesc
        if (item.formType == 'city') {
          const address = this.formAddForm[item.field] || {}
          const addressList = []
          for (const key in address) {
            if (address[key]) {
              addressList.push(address[key])
            }
          }
          item.fieldValue = addressList.join(',')
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
      } else if (this.formAddType === 'update-contacts') {
        request = hrmEmployeeSetContactsAPI
        params.contactsId = this.formAddForm.contactsId
      } else if (this.formAddType === 'create-contacts') {
        request = hrmEmployeeAddContactsAPI
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
.base-add-button {
  width: 100%;
  padding: 11px 12px;
  margin: 10px 0;
}

.wk-base-detail-section + .wk-base-detail-section {
  border-top: 1px solid $--border-color-base;
}
</style>
