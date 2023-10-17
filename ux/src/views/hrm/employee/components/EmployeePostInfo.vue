<template>
  <div :key="updateKey" v-loading="loading">
    <wk-custom-base-detail
      :list="postList">
      <template slot="data" slot-scope="{ data }">{{ data.value }}</template>
      <el-button v-if="editAuth" slot="more" type="text" @click="postCommandSelect('edit')">编辑</el-button>
    </wk-custom-base-detail>
    <wk-base-detail
      v-if="leaveList.length > 0"
      :dropdown-items="dropdownItems"
      :list="leaveList"
      @top-command-select="topCommandSelect"
    />

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
      :id="id"
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
  hrmEmployeePostQueryByIdAPI,
  hrmEmployeePostSetLeaveAPI,
  hrmEmployeePostUpdatePostAPI,
  hrmRecruitChannelQueryAPI
} from '@/api/hrm/employeePost'
import {
  hrmEmployeeFieldVerifyAPI
} from '@/api/hrm/employee'

import WkBaseDetail from '@/components/WkBaseDetail'
import WkCustomBaseDetail from '@/components/WkBaseDetail/CustomIndex'
import FormAddDialog from './FormAddDialog'
import CustomFormAddDialog from './CustomFormAddDialog'

import employeePost from '../model/employeePost'
import employField from '../model/employField'
import { objDeepCopy } from '@/utils'
import { isEmpty, isArray } from '@/utils/types'
import { employeeModel } from '../model/employee'
import CustomFieldsMixin from '@/mixins/CustomFields'

export default {
  // 岗位信息
  name: 'EmployeePostInfo',
  components: {
    WkBaseDetail,
    WkCustomBaseDetail,
    FormAddDialog,
    CustomFormAddDialog
  },
  mixins: [CustomFieldsMixin],

  inject: ['editAuth'],
  props: {
    id: [String, Number],
    detail: Object
  },
  data() {
    return {
      loading: false,
      leaveDetail: null,
      postEditList: [],
      postList: [],
      leaveFields: [{
        label: '计划离职日期',
        field: 'planQuitTime',
        value: ''
      }, {
        label: '申请离职日期',
        field: 'applyQuitTime',
        value: ''
      }, {
        label: '薪资结算日期',
        field: 'salarySettlementTime',
        value: ''
      }, {
        label: '离职类型',
        field: 'quitType',
        value: ''
      }, {
        label: '离职原因',
        field: 'quitReason',
        value: ''
      }, {
        label: '备注',
        field: 'remarks',
        value: ''
      }],
      leaveList: [],
      // 弹窗添加
      formAddCommond: '',
      formAddType: '',
      formAddForm: {},
      formAddRules: {},
      formAddFields: [],
      formAddDialogShow: false,
      channelList: [], // 招聘渠道数据
      // 新自定义字段弹窗
      customFormAddFields: [],
      customFormAddDialogShow: false,
      updateKey: Date.now()
    }
  },

  computed: {
    dropdownItems() {
      const temps = []
      // 入职状态 1 在职 2 待入职 3 待离职 4 离职
      if (this.editAuth && this.detail && this.detail.entryStatus == 3) {
        temps.push({
          label: '调整离职信息',
          command: 'change-leave',
          icon: ''
        })
      }
      return temps
    },

    postDropdownItems() {
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

    // 新建编辑标题
    formAddTitle() {
      return {
        'change-leave': '调整离职信息',
        'update-post-info': '编辑岗位信息'
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
      hrmEmployeePostQueryByIdAPI(this.id)
        .then(res => {
          this.loading = false
          const pList = res.data.information || []
          this.postEditList = objDeepCopy(pList)
          const postList = []
          pList.forEach(item => {
            item.value = item.fieldValue
            if (item.fieldName == 'employment_forms') {
              item.value = employeeModel.employmentFormsValue[item.value]
            } else if (item.fieldName == 'probation') {
              item.value = employeeModel.probationValue[item.value]
            } else if (item.fieldName == 'status') {
              item.value = employeeModel.statusValue[item.value]
            } else if (item.formType == 'user' ||
            item.formType == 'structure' ||
            item.formType == 'recruit_channel' ||
            item.formType == 'area') {
              item.value = item.fieldValueDesc
            } else if (item.fieldName == 'company_age') {
              item.value = item.fieldValueDesc // 天数格式化的展示值再这个字段
            }
            postList.push(item)
          })

          // 逻辑表单逻辑
          const assistIds = this.getFormAssistIds([postList])
          const showPostList = []
          postList.forEach(item => {
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

          this.postList = [{
            name: '岗位信息',
            list: showPostList
          }]

          const leaveData = res.data.employeeQuitInfo
          this.leaveDetail = leaveData

          if (leaveData) {
            const leaveFields = []
            employeePost.getFields(leaveData).forEach(item => {
              const temp = {
                label: item.name
              }
              if (item.field == 'quitType') {
                temp.value = employeePost.quitTypeValue[leaveData[item.field]]
              } else if (item.field == 'quitReason') {
                temp.value = employeePost.quitReasonValue[leaveData[item.field]]
              } else {
                temp.value = leaveData[item.field]
              }

              leaveFields.push(temp)
            })
            this.leaveList = [{
              name: '离职信息',
              list: leaveFields
            }]
          } else {
            this.leaveList = []
          }
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 编辑操作
     */
    topCommandSelect(command) {
      this.formAddCommond = command
      if (command == 'change-leave') {
        this.formAddType = 'change-leave'
        this.formAddForm = this.leaveDetail ? {
          planQuitTime: this.leaveDetail.planQuitTime,
          applyQuitTime: this.leaveDetail.applyQuitTime,
          salarySettlementTime: this.leaveDetail.salarySettlementTime,
          quitType: this.leaveDetail.quitType,
          quitReason: this.leaveDetail.quitReason,
          remarks: this.leaveDetail.remarks,
          quitInfoId: this.leaveDetail.quitInfoId
        } : {
          quitType: 1,
          quitReason: 1
        }
        this.formAddRules = employeePost.getRules(this.formAddForm)
        this.formAddFields = employeePost.getFields(this.formAddForm)
        this.formAddDialogShow = true
      }
    },

    formAddPass() {
      this.$refs.formAdddialog.loading = true
      this.formAddForm.employeeId = this.id
      hrmEmployeePostSetLeaveAPI(this.formAddForm).then(res => {
        this.$refs.formAdddialog.loading = false
        this.$message.success(`${this.formAddTitle}成功`)
        this.formAddDialogShow = false
        this.getDetail()
        this.updateKey = Date.now()
        this.$emit('handle', { type: this.formAddCommond })
      }).catch(() => {
        this.$refs.formAdddialog.loading = false
      })
    },

    /**
     * 自定义字段保存
     */
    customFormAddPass(data) {
      const dataList = objDeepCopy(data.field)
      dataList.forEach(item => {
        if (item.fieldName == 'work_city') {
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

      hrmEmployeePostUpdatePostAPI(params).then(res => {
        dialog.loading = false
        this.$message.success(`${this.formAddTitle}成功`)
        this.customFormAddDialogShow = false
        this.getDetail()
        this.updateKey = Date.now()
        this.$emit('handle', { type: this.formAddCommond })
      }).catch(() => {
        dialog.loading = false
      })
    },

    /**
     * 岗位信息编辑
     */
    postCommandSelect(command) {
      if (command == 'edit') {
        const copyList = objDeepCopy(this.postEditList)
        const fields = []
        copyList.forEach(item => {
          // 忽略掉员工状态和司龄
          if (item.fieldName != 'company_age' &&
            item.fieldName != 'become_time') {
            this.handleFieldExtendList(item)
            employField.getCreateFieldDefalutData(item)
            item.value = item.fieldValue

            this.handleTableValue(item)

            // 为和crm统一，将work_city的 formType 改为 从 area 改为 address
            if (item.fieldName === 'work_city') {
              item.formType = 'address'
            }
            // 处理部门商机为单选
            if (item.fieldName === 'dept_id' || item.fieldName === 'parent_id') {
              item.radio = true
            }
            // 1 正式  2 非正式
            if (item.fieldName == 'employment_forms') {
              item.disabled = true
              item.setting = employeeModel.employmentFormsSetting
            } else if (item.fieldName == 'probation') {
              item.setting = employeeModel.probationSetting
            } else if (item.formType == 'recruit_channel') { // 'recruit_channel' 招聘渠道
              item.formType = 'select'
              if (this.channelList && this.channelList.length > 0) {
                item.setting = this.channelList
              } else {
                this.getChannelDetail(item)
              }
            } else if (item.fieldName == 'status') {
              item.setting = employeeModel.statusSetting
            }

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
              }
              if (!isArray(item.value)) {
                item.value = []
              }
            }

            // 聘用形式 是非正式 读取所有值  是正式 忽略状态的值
            if (this.detail.employmentForms == 2 ||
            (this.detail.employmentForms == 1 &&
            item.fieldName != 'status')) {
              if (item.formType == 'address') {
                if (item.value) {
                  const citys = item.value.split(',')
                  item.value = {
                    province: citys[0] || '',
                    city: citys[1] || '',
                    area: citys[2] || ''
                  }
                } else {
                  item.value = {}
                }
              }
              fields.push(item)
            }
          }
        })

        this.formAddType = 'update-post-info'
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
          id: this.id,
          value
        }).then(response => {
          resolve()
        }).catch(() => {
          reject()
        })
      })
    },

    formAddChange(item, index, value, valueList, editForm) {
      if (valueList) {
        item.valueList = valueList
      }
      if (item.field == 'entry_time') {
        const companyAge = this.formAddFields.find(item => item.fieldName === 'company_age_start_time')
        if (companyAge) {
          this.$set(editForm, companyAge.fieldName, editForm.entry_time)
        }
      }

      if (this.formAddType === 'change-leave' && item.field == 'quitType') {
        if (value == 1) {
          editForm.quitReason = 1
        } else if (value == 2) {
          editForm.quitReason = 11
        } else if (value == 3) {
          editForm.quitReason = ''
        }

        this.formAddRules = employeePost.getRules(editForm)
        this.formAddFields = employeePost.getFields(editForm)
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
    }
  }
}
</script>

<style lang="scss" scoped>
</style>
