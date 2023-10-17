<template>
  <div v-loading="loading">
    <wk-custom-base-detail
      :list="postList">
      <template slot="data" slot-scope="{ data }">{{ data.value }}</template>
    </wk-custom-base-detail>
    <wk-base-detail
      v-if="leaveList.length > 0"
      :list="leaveList"
    />

  </div>
</template>

<script>
import {
  hrmEmployeeArchivesPostAPI
} from '@/api/hrm/selfService/myArchives'

import WkBaseDetail from '@/components/WkBaseDetail'
import WkCustomBaseDetail from '@/components/WkBaseDetail/CustomIndex'

import employeePost from '@/views/hrm/employee/model/employeePost'
import { objDeepCopy } from '@/utils'
import { isEmpty, isArray } from '@/utils/types'
import { employeeModel } from '@/views/hrm/employee/model/employee'
import CustomFieldsMixin from '@/mixins/CustomFields'

export default {
  // 岗位信息
  name: 'EmployeePostInfo',
  components: {
    WkBaseDetail,
    WkCustomBaseDetail
  },
  mixins: [CustomFieldsMixin],
  props: {},
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
      leaveList: []
    }
  },

  computed: {},
  watch: {},
  mounted() {
    this.getDetail()
  },
  methods: {
    /**
     * 获取基础信息
     */
    getDetail() {
      this.loading = true
      hrmEmployeeArchivesPostAPI()
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
    }
  }
}
</script>

<style lang="scss" scoped>
</style>
