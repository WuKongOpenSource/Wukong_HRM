<template>
  <div v-loading="loading" class="main">
    <div class="main-title">
      <span>
        审批设置
      </span>
      <el-button
        type="primary"
        @click="saveClick">
        保存
      </el-button>
    </div>
    <div class="main-body">
      <div class="tip">如果不设置审批，考勤数据将无法关联审批数据。</div>
      <div class="title">请假审批</div>
      <el-form
        ref="form"
        :model="fieldFrom"
        :rules="fieldRules">
        <el-form-item
          v-for="(item, index) in fieldList"
          :key="index"
          :label="item.name"
          :prop="item.field">
          <el-select
            v-model="fieldFrom[item.field]"
            placeholder="请选择"
            @change="selcetChange($event ,item.field)">
            <el-option
              v-for="(sItem, sIndex) in item.setting"
              :key="sIndex"
              :label="sItem.label"
              :value="sItem.value" />
          </el-select>
        </el-form-item>
      </el-form>

    </div>
  </div>
</template>

<script>
import {
  superExaminesApplyListAPI
} from '@/api/oa/superExamine'
import {
  oaExamineFieldListAPI
} from '@/api/admin/crm'
import {
  hrmAttendanceExamineQueryPageListAPI,
  hrmAttendanceExamineAddAPI
} from '@/api/admin/hrm'

import { isEmpty } from '@/utils/types'
import CustomFieldsMixin from '@/mixins/CustomFields'

export default {
  name: 'ExamineSet',

  components: {},

  mixins: [CustomFieldsMixin],

  data() {
    return {
      loading: false,
      fieldFrom: {},
      fieldRules: {},

      examineList: [],
      associatedFieldList: []
    }
  },

  computed: {
    // 字段列表
    fieldList() {
      return [{
        name: '关联审批',
        field: 'examineFieldId',
        formType: 'select',
        setting: this.examineList,
        stylePercent: 100,
        isNull: 1
      }, {
        name: '请假类型关联字段',
        field: 'typeFieldId',
        formType: 'select',
        setting: this.associatedFieldList,
        isNull: 1
      }, {
        name: '请假开始时间关联字段',
        field: 'startTimeFieldId',
        formType: 'select',
        setting: this.associatedFieldList,
        isNull: 1
      }, {
        name: '请假结束时间关联字段',
        field: 'endTimeFieldId',
        formType: 'select',
        setting: this.associatedFieldList,
        isNull: 1
      }, {
        name: '请假天数关联字段',
        field: 'durationFieldId',
        formType: 'select',
        setting: this.associatedFieldList,
        isNull: 1
      }, {
        name: '备注关联字段',
        field: 'remarkFieldId',
        formType: 'select',
        setting: this.associatedFieldList
      }]
    }
  },

  created() {
    this.getList()
    this.getExamineList()
  },

  methods: {
    /**
     * @description: 获取设置列表
     * @return {*}
     */
    getList() {
      this.loading = true
      this.getFieldRules()
      hrmAttendanceExamineQueryPageListAPI()
        .then(res => {
          const data = res.data || {}
          if (!isEmpty(data)) {
            const { examineFieldId } = data
            this.getExamineField(examineFieldId)
            this.fieldFrom = data
          }
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * @description: 获取所有审批列表
     * @return {*}
     */
    getExamineList() {
      superExaminesApplyListAPI()
        .then(res => {
          const list = res.data.filter(o => o.groupId != 0) || []
          this.examineList = list.map(item => {
            if (!isEmpty(item.superExamineVOList)) {
              return item.superExamineVOList.map(sItem => {
                return {
                  label: sItem.examineName,
                  value: sItem.examineId
                }
              })
            }
          }).flat()
        })
    },

    /**
     * @description: 设置表单校验
     * @return {*}
     */
    getFieldRules() {
      this.fieldList.forEach(item => {
        if (item.isNull) {
          this.fieldRules[item.field] = this.getRules(item)
        }
      })
    },

    /**
     * @description: 获取审批字段列表
     * @param {*} fieldId
     * @return {*}
     */
    getExamineField(fieldId) {
      const { examineFieldId } = this.fieldFrom
      const categoryId = fieldId || examineFieldId
      oaExamineFieldListAPI({ categoryId, type: 1 })
        .then(res => {
          const data = res.data.filter(o => o.fieldId) || []
          this.associatedFieldList = data.map(item => {
            return {
              label: item.name,
              value: item.fieldId
            }
          })
        })
    },

    /**
     * @description: 保存
     * @return {*}
     */
    saveClick() {
      this.loading = true
      this.fieldFrom.remarkFieldId = this.fieldFrom.remarkFieldId || ''
      this.$refs.form.validate(valid => {
        if (valid) {
          hrmAttendanceExamineAddAPI(this.fieldFrom)
            .then(res => {
              this.$message.success('保存成功')
              this.loading = false
              this.getList()
            })
            .catch(() => {
              this.loading = false
            })
        } else {
          this.loading = false
          this.$message.error('请完善请假审批设置')
        }
      })
    },

    /**
     * @description: 下拉框选择事件
     * @param {*} field
     * @param {*} value
     * @return {*}
     */
    selcetChange(value, field) {
      if (field == 'examineFieldId') {
        this.fieldFrom = {
          examineFieldId: value
        }
        this.getExamineField()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.main {
  padding: 10px 30px;

  .main-title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 20px;
  }

  .main-body {
    padding-top: 10px;

    .tip {
      margin-bottom: 20px;
      color: $--color-text-placeholder;
    }

    .title {
      font-weight: bold;
    }
  }
}
</style>
