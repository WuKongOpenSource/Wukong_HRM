<template>
  <xr-create
    :loading="loading"
    :title=" step=='prev'?title:'出勤日设置'"
    :show-confirm="showConfirm"
    :show-cancel="!showConfirm"
    @close="close"
    @save="saveClick">
    <el-form
      v-show="step=='prev'"
      ref="form"
      :model="fieldForm"
      :rules="fieldRules"
      :validate-on-rule-change="false"
      class="wk-form"
      label-position="top">
      <wk-form-items
        v-for="(children, index) in fieldList"
        :key="index"
        :field-from="fieldForm"
        :field-list="children"
        @change="formChange"
      >
        <template slot-scope="{ data }">
          <punch-card-field
            v-if="data && data.formType == 'punch_card'"
            ref="punchCard"
            v-model="fieldForm[data.fieldName]"
          />
          <wk-user-dep-select
            v-if="data && data.formType == 'structure_employee'"
            :dep-value.sync="fieldForm[data.fieldName]['deptIds']"
            :user-value.sync="fieldForm[data.fieldName]['employeeIds']"
            :props="{dataType:'hrm'}"
            style="width: 100%;"
          />
        </template>
      </wk-form-items>
    </el-form>
    <div v-show="step=='last'" class="last-step">

      <p>设置员工周一到周日的出勤日和休息日。若是大小周或单休，需要进行特殊日期设置。</p>
      <div class="last-step-firstSet">
        <span>快速设置班次：</span>
        <el-select v-model="quickSetting" size="mini" @change="quickSettingChange">
          <el-option
            v-for="item in options"
            :key="item.shiftId"
            :label="item.label"
            :value="item.shiftId" />
        </el-select>
      </div>
      <el-table
        ref="table"
        :data="workData"
        style="width: 100%;"
        @select="selectionChange"
        @select-all="selectionChange">
        <el-table-column
          type="selection"
          width="55" />
        <el-table-column
          prop="week"
          label="工作日"
          width="100" />
        <el-table-column
          prop="timeRange"
          label="当天班次"
          width="" />
        <el-table-column
          prop="prop"
          label="更改班次"
          width="120">
          <template slot-scope="{row}">
            <el-select v-model="row.shiftId" size="mini" @change="selectShiftChange(row)">
              <el-option
                v-for="(item,index) in options"
                :key="index"
                :label="item.label"
                :value="item.shiftId" />
            </el-select>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 10px;">
        <el-checkbox
          v-model="isRest"
          :true-label="1"
          :false-label="0"
          label="法定节假日休息"
        />
        <p>法定节假日将自动设为休息；若法定节假日有调班，请进行特殊日期设置。</p>
      </div>

      <span>特殊日期设置</span>
      <p>如大小周或单休、调休、调班，需要设置出勤日或休息日的具体日期。</p>

      <el-popover
        ref="popoverRef"
        placement="top"
        title="添加日期"
        width="310"
        trigger="click"
      >
        <el-date-picker
          v-model="selectedDate"
          value-format="yyyy-MM-dd"
          type="date"
          placeholder="选择日期" />
        <el-button type="primary" @click="addDate">确定</el-button>
        <el-button slot="reference" type="primary">+添加日期</el-button>
      </el-popover>

      <el-table
        :data="technicalDate"
        style="width: 100%;margin-top: 15px;">
        <el-table-column
          prop="date"
          label="日期"
          width="100" />
        <el-table-column
          prop="timeRange"
          label="当日班次"
          width="width" />
        <el-table-column
          prop="prop"
          label="更改班次"
          width="200">
          <template slot-scope="{row}">
            <el-select
              v-model="row.shiftId"
              size="mini"
              @change="selectShiftChange(row)">
              <el-option
                v-for="(item,index) in options"
                :key="index"
                :label="item.label"
                :value="item.shiftId" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column
          prop="prop"
          label="操作"
          width="120">
          <template slot-scope="{$index}">
            <el-button type="text" @click="deleteTechnicalDate($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-button
      slot="footer"
      class="handle-button"
      type="primary"
      @click="handleClick"
    >{{ buttonContent }}</el-button>
  </xr-create>
</template>

<script>

import { hrmVerifyAttendanceGroupNameAPI,
  hrmQueryAttendanceRulePageListAPI,
  hrmQueryAttendanceShiftPageListAPI,
  hrmAddAttendanceGroupAPI,
  hrmEditAttendanceGroupAPI
} from '@/api/admin/hrm'

import XrCreate from '@/components/XrCreate'
import WkFormItems from '@/components/NewCom/WkForm/WkFormItems'
import PunchCardField from './PunchCardField'
import WkUserDepSelect from '@/components/NewCom/WkUserDepSelect'

import fieldListLib from './fieldListLib'
import CustomFieldsMixin from '@/mixins/CustomFields'

export default {

  components: {
    XrCreate,
    WkFormItems,
    PunchCardField,
    WkUserDepSelect
  },
  mixins: [CustomFieldsMixin],
  props: {
    id: {
      type: [String, Number],
      default: ''
    },
    action: {
      type: Object,
      default: () => {
        return {
          id: '',
          type: 'save',
          data: {}
        }
      }
    }
  },
  data() {
    return {
      loading: false,
      step: 'prev',
      fieldList: [],
      fieldRules: {},
      fieldForm: {},
      visible: false,
      quickSetting: '',
      selectedDate: '', // 选择日期
      workData: [
        { week: '星期一', type: 'Monday', timeRange: '', shiftId: '', shiftType: '' },
        { week: '星期二', type: 'Tuesday', timeRange: '', shiftId: '', shiftType: '' },
        { week: '星期三', type: 'Wednesday', timeRange: '', shiftId: '', shiftType: '' },
        { week: '星期四', type: 'Thursday', timeRange: '', shiftId: '', shiftType: '' },
        { week: '星期五', type: 'Friday', timeRange: '', shiftId: '', shiftType: '' },
        { week: '星期六', type: 'Saturday', timeRange: '', shiftId: '', shiftType: '' },
        { week: '星期日', type: 'Sunday', timeRange: '', shiftId: '', shiftType: '' }
      ],
      shiftList: [],
      technicalDate: [],
      isRest: 1, // 是否开启假日

      selectList: []
    }
  },
  computed: {
    title() {
      return this.id ? '编辑考勤组' : '新建考勤组'
    },
    showConfirm() {
      return this.step != 'prev'
    },
    buttonContent() {
      return this.step == 'prev' ? '下一步' : '上一步'
    },
    options() {
      return this.shiftList.map(item => {
        return {
          label: item.shiftName,
          value: item.shiftType,
          shiftId: item.shiftId,
          timeRange: this.getTimeRange(item)
        }
      })
    }
  },
  watch: {
    step(val) {
      if (val == 'last') {
        this.workData.forEach(item => {
          if (item.shiftType != 0) {
            this.$refs.table.toggleRowSelection(item, true)
          } else {
            this.$refs.table.toggleRowSelection(item, false)
          }
        })
      }
    }
  },
  created() {
    this.getDeductionSetting()
  },
  methods: {
    /**
     * 员工部门改变
     */
    userDepChange(data, userDataValue, depDataValue, userDataSelects, depDataSelects) {
      this.$set(this.fieldForm, data.fieldName, { deptIds: userDataSelects, employeeIds: depDataSelects })
    },
    // 删除
    deleteTechnicalDate(index) {
      this.technicalDate.splice(index, 1)
    },
    // 添加日期
    addDate() {
      if (!this.selectedDate) return
      this.$refs.popoverRef.showPopper = false
      this.technicalDate.push({
        date: this.selectedDate,
        timeRange: '',
        shiftId: '',
        shiftType: '' })
      this.selectedDate = ''
    },
    quickSettingChange(shiftId) {
      const findRes = this.options.find(item => item.shiftId == shiftId)
      if (findRes) {
        if (!this.selectList.length && findRes.value != 0) {
          this.selectList = this.workData.filter(item => !['Saturday', 'Sunday'].includes(item.type))
        }

        const selectTypeList = this.selectList.map(item => item.type)
        this.workData.forEach(item => {
          if (selectTypeList.includes(item.type)) {
            item.timeRange = findRes.timeRange
            item.shiftType = findRes.value
            item.shiftId = findRes.shiftId
          }
        })

        if (findRes && findRes.value == 0) {
          this.selectList = []
        }
        this.workData.forEach(item => {
          if (item.shiftType != 0) {
            this.$refs.table.toggleRowSelection(item, true)
          } else {
            this.$refs.table.toggleRowSelection(item, false)
          }
        })
      }
    },
    selectShiftChange(row) {
      const findRes = this.options.find(item => item.shiftId == row.shiftId)
      if (findRes) {
        this.$set(row, 'timeRange', findRes.timeRange)
        this.$set(row, 'shiftType', findRes.value)
        if (findRes.value == 0) {
          this.$refs.table.toggleRowSelection(row, false)
        } else {
          this.$refs.table.toggleRowSelection(row, true)
        }
        this.selectList = this.workData.filter(item => item.shiftType != 0)
      }
    },
    getTimeRange(item) {
      const arr = []
      const { shiftType } = item
      for (let index = 1; index <= 3; index++) {
        if (item['start' + index]) {
          arr.push(str = item['start' + index] + '-' + item['end' + index])
        }
      }
      let str = ''
      if (item.restTimeStatus) {
        str = ` 休息：${item.restStartTime}-${item.restEndTime}`
      }
      return `${{ 0: '休息', 1: item.shiftName, 2: '分段打卡' }[shiftType]} ${arr.join('  ')} ${str}`
    },
    UniquePromise({ field, value }) {
      return hrmVerifyAttendanceGroupNameAPI({
        name: value,
        attendanceGroupId: this.id
      })
    },
    saveClick() {
      this.loading = true
      const instance = this.$refs.form
      instance.validate((valid) => {
        if (valid) {
          let params = this.getSubmiteParams([].concat.apply([], this.fieldList), this.fieldForm)
          params = this.getAttendanceSet(params)
          if (!params.isOpenPointCard && !params.isOpenWifiCard) {
            this.$message.error('请选择考勤打卡方式')
            this.loading = false
            return
          } else {
            if (params.isOpenPointCard && !params.pointList.length) {
              this.$message.error('请设置考勤打卡地址')
              this.loading = false

              return
            } else if (params.isOpenWifiCard && !params.wifiList.length) {
              this.$message.error('请设置考勤打卡wiff')
              this.loading = false
              return
            }
          }

          this.submitData(params)
        } else {
          this.loading = false
        }
      })
    },
    submitData(params) {
      const request = this.id ? hrmEditAttendanceGroupAPI : hrmAddAttendanceGroupAPI
      if (this.id) {
        params.attendanceGroupId = this.id
      }
      request(params).then(res => {
        this.loading = false
        this.$message.success(this.id ? '编辑成功，次日生效' : '添加成功')
        this.close()
        this.$emit('save-success')
      }).catch(() => { this.loading = false })
    },
    getSubmiteParams(array, data) {
      let params = {}
      for (let index = 0; index < array.length; index++) {
        const field = array[index]
        const dataValue = data[field.fieldName]
        if (field.formType == 'structure_employee') {
          const { deptIds = [], employeeIds = [] } = dataValue
          params = {
            // deptIds: deptIds.map(item => item.depId),
            deptIds,
            employeeIds,
            ...params
          }
        } else if (field.formType == 'punch_card') {
          // const data = this.$refs.punchCard[0]
          // const { checkGroup, punchCardAddressTable: pointList, punchCardWifiTable: wifiList } = data
          // wifiList.forEach((item, index) => {
          //   item.mac = item.mac.join(':') || ''
          // })
          // const {
          //   isOpenPointCard,
          //   isOpenWifiCard } = checkGroup
          // console.info(data, 'data', true)
          const { isOpenPointCard, isOpenWifiCard, pointList, wifiList } = dataValue
          params = {
            ...params,
            isOpenPointCard,
            isOpenWifiCard,
            pointList,
            wifiList
          }
        } else {
          params = {
            ...params,
            [field.fieldName]: dataValue
          }
        }
      }
      return params
    },
    /**
     * 出勤设置参数
     */
    getAttendanceSet(params) {
      return {
        ...params,
        shiftSetting: this.workData.map(item => item.shiftId),
        isRest: this.isRest,
        specialDateSetting: this.technicalDate.map(item => {
          return {
            shift: item.shiftId,
            date: item.date
          }
        })
      }
    },
    formChange() {},
    getField(deductionSetting) {
      const baseFields = []
      const fieldList = []
      const fieldRules = {}
      const fieldForm = {}
      fieldListLib.forEach(children => {
        const fields = []
        children.forEach(item => {
          if (item.fieldName == 'name') {
            if (this.id) {
              item.isUnique = 0
            } else {
              item.isUnique = 1
            }
          }
          if (item.fieldName == 'attendanceRuleId') {
            item.setting = deductionSetting
          }
          const temp = this.getFormItemDefaultProperty(item)

          if (temp.field == 'structure_employee') {
            fieldRules[temp.field] = [{
              validator: (rule, value, callback) => {
                const { structure_employee } = this.fieldForm
                if (!structure_employee.deptIds.length && !structure_employee.employeeIds.length) {
                  callback(new Error('请选择考核使用范围'))
                } else {
                  callback()
                }
              }
            }]
          } else {
            fieldRules[temp.field] = this.getRules(item)
          }
          fieldForm[temp.field] = this.getItemValue(item, this.action.data, this.action.type)
          fields.push(temp)
          baseFields.push(item)
        })
        fieldList.push(fields)
      })

      this.baseFields = baseFields
      this.fieldList = fieldList
      this.fieldForm = fieldForm
      this.fieldRules = fieldRules
      this.getShiftList()
      if (this.id) {
        this.isRest = this.action.data.isRest
      }
    },
    fullTechnicalDate() {
      const detailData = this.action.data
      if (!detailData) return
      const { specialDateSetting = [] } = this.action.data
      this.technicalDate = specialDateSetting.map(item => {
        const shiftObj = this.getShiftObjById(item.shift, this.shiftList)
        return {
          date: item.date,
          timeRange: shiftObj && shiftObj.timeRange,
          shiftId: item.shift
        }
      })
    },
    getItemValue(field, data = {}, type) {
      if (field.fieldName == 'structure_employee') {
        if (type == 'update') {
          const { deptList, employeeList } = data
          return {
            deptIds: deptList || [],
            employeeIds: employeeList || []
          }
        } else {
          return {}
        }
      }
      if (field.fieldName == 'attendanceRuleId') {
        if (type !== 'update') {
          const { setting } = field
          return setting[0].value
        } else {
          return data[field.fieldName]
        }
      }
      if (field.fieldName == 'punch_card') {
        if (type == 'update') {
          const { isAutoCard, isOpenPointCard, isOpenWifiCard, pointList, wifiList } = data
          return {
            isAutoCard,
            isOpenPointCard,
            isOpenWifiCard,
            pointList: pointList || [],
            wifiList: wifiList || []
          }
        } else {
          return {}
        }
      }
      return data[field.fieldName]
    },
    close() {
      this.$emit('close')
    },
    handleClick() {
      if (this.step == 'prev') {
        this.$refs.form.validate(valid => {
          if (valid) {
            const { punch_card } = this.fieldForm

            if (!punch_card.isOpenPointCard && !punch_card.isOpenWifiCard) {
              this.$message.error('请至少选择一种打卡方式')
              return
            }
            if (punch_card.isOpenPointCard) {
              const { pointList } = punch_card
              if (!pointList.length) {
                this.$message.error('请至少添加一个打卡点')
                return
              }
            }
            if (punch_card.isOpenWifiCard) {
              const { wifiList } = punch_card
              if (!wifiList.length) {
                this.$message.error('请至少添加一个打卡wifi')
                return
              }
            }

            this.step = 'last'
          } else {
            this.$message.error('请完善表单信息')
          }
        })
      } else {
        this.step = 'prev'
      }
    },
    getDeductionSetting() {
      hrmQueryAttendanceRulePageListAPI({
        pageType: 0
      }).then(res => {
        let setting = []
        setting = res.data.list.map(item => {
          return {
            label: item.attendanceRuleName,
            value: item.attendanceRuleId
          }
        })
        this.getField(setting)
      }).catch(() => {
      })
    },
    getShiftList() {
      hrmQueryAttendanceShiftPageListAPI({ pageType: 0 }).then(res => {
        this.shiftList = res.data.list
        const DefaultSetting = this.shiftList.find(item => item.isDefaultSetting == 1 && item.shiftType == 1)
        if (this.id) {
          const { shiftSetting } = this.action.data
          this.workData.forEach((item, index) => {
            const shiftObj = this.getShiftObjById(shiftSetting[index], this.shiftList)
            item.timeRange = shiftObj.timeRange
            item.shiftId = shiftObj.shiftId
            item.shiftType = shiftObj.shiftType
            if (item.shiftType != 0) {
              this.selectList.push(item)
            }
          })
        } else if (DefaultSetting) {
          this.workData.forEach((item) => {
            const shiftObj = this.getShiftObjById(DefaultSetting.shiftId, this.shiftList)
            if (!['Saturday', 'Sunday'].includes(item.type)) {
              item.timeRange = shiftObj.timeRange
              item.shiftId = shiftObj.shiftId
              item.shiftType = shiftObj.shiftType
              this.selectList.push(item)
            } else {
              const result = this.shiftList.filter(item => item.isDefaultSetting == 1 && item.shiftType == 0)[0]
              const shiftObj = this.getShiftObjById(result.shiftId, this.shiftList)
              item.timeRange = shiftObj.timeRange
              item.shiftId = shiftObj.shiftId
              item.shiftType = shiftObj.shiftType
            }
          })
        }
        this.fullTechnicalDate()
      })
    },
    getShiftObjById(shiftId, shiftList) {
      if (!shiftList.length) return {}
      const obj = shiftList.find(item => item.shiftId == shiftId)
      if (obj) {
        return {
          shiftId: obj.shiftId,
          shiftType: Number(obj.shiftType),
          timeRange: this.getTimeRange(obj)
        }
      }
      return {}
    },

    /**
     * @description: 勾选项发生变化
     * @param {*} selection
     * @return {*}
     */
    selectionChange(selection) {
      this.selectList = selection
      const result = this.shiftList.filter(item => item.isDefaultSetting == 1 && item.shiftType == 0)[0] // 休息日
      const shiftObj = this.getShiftObjById(result.shiftId, this.shiftList)
      const typeList = selection.map(o => o.type) // 选中的日期
      this.workData.forEach(item => {
        if (!typeList.includes(item.type)) {
          this.$set(item, 'shiftId', shiftObj.shiftId)
          this.$set(item, 'shiftType', shiftObj.shiftType)
          this.$set(item, 'timeRange', shiftObj.timeRange)
        } else if (item.shiftType == 0) {
          if (this.quickSetting) { // 快速设置班次有值
            const result = this.getShiftObjById(this.quickSetting, this.shiftList)
            this.$set(item, 'shiftId', result.shiftId)
            this.$set(item, 'shiftType', result.shiftType)
            this.$set(item, 'timeRange', result.timeRange)
          } else {
            const result = this.shiftList.filter(item => item.isDefaultSetting == 1 && item.shiftType == 1)[0] // 早晚打卡
            const shiftObj = this.getShiftObjById(result.shiftId, this.shiftList)
            this.$set(item, 'shiftId', shiftObj.shiftId)
            this.$set(item, 'shiftType', shiftObj.shiftType)
            this.$set(item, 'timeRange', shiftObj.timeRange)
          }
        }
      })
    }
  }
}
</script>

<style lang='scss' scoped>
.last-step {
  p {
    line-height: 40px;
    color: #ccc;
  }

  .last-step-firstSet {
    height: 40px;
    line-height: 40px;
  }
}
</style>
