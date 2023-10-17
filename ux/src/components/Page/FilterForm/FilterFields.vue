<template>
  <div
    ref="filterFieldsForm"
    class="filter-fields">
    <el-row
      v-for="(formItem, index) in form"
      :key="index"
      type="flex">
      <el-col :span="6">
        <wk-search-select
          v-model="formItem.fieldName"
          :disabled="isExport"
          placeholder="请选择要筛选的字段名"
          :options="fieldListOptions"
          @change="fieldChange(formItem)"
          @focus="fieldFocus" />
      </el-col>

      <template v-if="showCalCondition(formItem.formType, formItem.fieldName, formItem)">
        <el-col
          class="interval-base"
          :span="1" />
        <el-col
          :span="4">
          <el-select
            v-model="formItem.condition"
            placeholder="请选择范围"
            @change="selectChange($event,formItem)">
            <el-option
              v-for="item in getAdvancedFilterOptions(conditionTypeFun ? conditionTypeFun(formItem) : formItem.formType, formItem)"
              :key="item.value"
              :label="item.label"
              :value="item.value" />
          </el-select>
        </el-col>
      </template>

      <!-- 商机组 -->
      <el-col
        v-if="formItem.formType == 'business_cause'"
        class="interval-base"
        :span="1" />
      <el-col
        v-if="formItem.formType == 'business_cause'"
        :span="4">
        <el-select
          v-model="formItem.flowName"
          @change="typeOptionsChange(formItem)">
          <el-option
            v-for="item in formItem.typeOption"
            :key="item.flowId"
            :label="item.flowName"
            :value="item[config.businessCauseValueKey]" />
        </el-select>
      </el-col>

      <el-col class="interval-base" :span="1" />
      <el-col :span="getValueSpan(formItem.formType, formItem.fieldName, formItem) ? (isExport || !showExport ? 11 : 9) : (isExport ? 16 : 14)" style="position: relative;">
        <template
          v-if="formItem.condition === 'isNull' ||
            formItem.condition === 'isNotNull'" />
        <template v-else-if="updateFlag">
          <wk-search-select
            v-if="formItem.formType === 'checkStatus'
              || formItem.formType === 'dealStatus'
              || formItem.formType === 'invoiceStatus'
              || formItem.formType === 'receivedStatus'
              || formItem.formType === 'status'
              || (formItem.formType === 'select' && getSettingValueType(formItem.setting) != 'string')"
            v-model="formItem.value"
            placeholder="请选择筛选条件"
            :options="formItem.setting" />
          <wk-search-select
            v-else-if="formItem.formType === 'checkbox' && getSettingValueType(formItem.setting) != 'string'"
            v-model="formItem.value"
            :multiple="formItem.formType !== 'field_tag'"
            placeholder="请选择筛选条件"
            :options="formItem.setting">
            <el-option
              v-for="item in formItem.setting"
              :key="item.value"
              :label="(item.name || item.label)"
              :value="item.value" />
          </wk-search-select>
          <wk-search-select
            v-else-if="['select', 'checkbox'].includes(formItem.formType)"
            v-model="formItem.value"
            :multiple="formItem.formType !== 'field_tag'"
            placeholder="请选择筛选条件"
            :options="getObjOptions(formItem.setting)" />
          <el-select
            v-else-if="['field_tag'].includes(formItem.formType)"
            v-model="formItem.value"
            filterable
            multiple
            placeholder="请选择筛选条件"
            popper-class="select-popper-class">
            <el-option-group
              v-for="group in formItem.groupList"
              :key="group.label"
              :label="group.label">
              <el-option
                v-for="item in group.labelList"
                :key="`${group.label + item}`"
                :label="item"
                :value="item" />
            </el-option-group>
          </el-select>
          <el-select
            v-else-if="formItem.formType === 'field_attention'"
            v-model="formItem.value"
            multiple
            placeholder="请选择筛选条件">
            <el-option
              v-for="item in formItem.setting"
              :key="item.value"
              :label="item.name"
              :value="item.value" />
          </el-select>
          <template
            v-else-if="formItem.formType == 'number' ||
              formItem.formType == 'floatnumber' ||
              formItem.formType == 'percent'">
            <div v-if="formItem.type === 14" class="date-range-value">
              <el-input-number
                v-model="formItem.min"
                :controls="false"
                class="small" />
              <span>-</span>
              <el-input-number
                v-model="formItem.max"
                :controls="false"
                class="small" />
            </div>
            <el-input-number
              v-else
              v-model="formItem.value"
              :controls="false"
              style="width: 100%;"
              class="small" />
          </template>
          <template v-else-if="formItem.formType === 'date' || formItem.formType === 'datetime'">
            <el-date-picker
              v-show="formItem.type === 14"
              :ref="`wkDatePicker${index}`"
              v-model="formItem.range"
              :picker-options="getPickerOptions(formItem, index)"
              :type="formItem.formType === 'date' ? 'daterange' : 'datetimerange'"
              :value-format="formItem.formType === 'date' ? 'yyyy-MM-dd' : 'yyyy-MM-dd HH:mm:ss'"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              align="right"
              @change="datePickerChange(formItem)" />
            <div
              v-if="formItem.timeType"
              class="date-range-content"
              @click="dateRangeSelect(formItem, index)">{{ formItem.timeTypeName }}</div>
            <el-date-picker
              v-show="formItem.type !== 14"
              v-model="formItem.value"
              :value-format="formItem.formType === 'date' ? 'yyyy-MM-dd' : 'yyyy-MM-dd HH:mm:ss'"
              :type="formItem.formType"
              placeholder="选择日期" />
          </template>
          <el-select
            v-else-if="formItem.formType === 'business_cause'"
            v-model="formItem.settingName">
            <el-option
              v-for="item in formItem.settingList"
              :key="item.settingId"
              :label="item.settingName"
              :value="item.settingName" />
          </el-select>
          <wk-user-dialog-select
            v-else-if="formItem.formType === 'user' || formItem.formType === 'single_user'"
            v-model="formItem.value"
            :radio="false"
            style="width: 100%;"
            @change="userDepChange(formItem, arguments[0], arguments[1])" />
          <wk-dept-dialog-select
            v-else-if="formItem.formType === 'structure'"
            v-model="formItem.value"
            :radio="false"
            style="width: 100%;"
            @change="userDepChange(formItem, arguments[0], arguments[1])" />
          <el-switch
            v-else-if="formItem.formType == 'boolean_value'"
            v-model="formItem.value"
            active-value="1"
            inactive-value="0" />
          <wk-product-category
            v-else-if="formItem.fieldName === 'productTypeId'"
            :item="formItem"
            :value="formItem.value"
            type="jxc"
            @change="arrayValueChange" />
          <xh-prouct-cate
            v-else-if="formItem.formType === 'category'"
            :item="formItem"
            :value="formItem.value"
            @value-change="arrayValueChange" />
          <v-distpicker
            v-else-if="formItem.formType === 'map_address'"
            :province="formItem.address.state"
            :city="formItem.address.city"
            :area="formItem.address.area"
            @province="selectProvince($event,formItem)"
            @city="selectCity($event,formItem)"
            @area="selectArea($event,formItem)" />
          <wk-position
            v-else-if="formItem.formType == 'position'"
            v-model="formItem.value"
            :show-detail="false"
            :props="{ checkStrictly: true }" />
          <el-input
            v-else
            v-model="formItem.value"
            placeholder="多个条件请用；隔开" />
        </template>
      </el-col>
      <el-col
        v-if="!isExport"
        :span="1"
        class="delete">
        <i
          class="el-icon-error delete-btn"
          @click="handleDelete(index)" />
      </el-col>

      <el-col v-if="!isExport && showExport" :span="2">
        <el-checkbox
          v-model="formItem.isOut"
          :true-label="1"
          :false-label="0"
          @change="exportChange">外露</el-checkbox>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { XhProuctCate } from '@/components/CreateCom'

import WkUserDialogSelect from '@/components/NewCom/WkUserDialogSelect'
import WkDeptDialogSelect from '@/components/NewCom/WkDeptDialogSelect'
import WkProductCategory from '@/components/NewCom/WkProductCategory'
import VDistpicker from '@/components/VDistpicker'
import WkPosition from '@/components/NewCom/WkPosition'
import WkSearchSelect from './WkSearchSelect'

import AdvancedFilterMixin from '@/mixins/AdvancedFilter'
import merge from '@/utils/merge'

const DefaultFilterFields = {
  /** 个人标签相关 */
  showPersonTag: false,
  request: null,
  params: null,
  /** 个人标签相关 */
  businessCauseValueKey: 'flowName' // 阶段的取值key 商机 是 flowId 其他是 flowName
}

export default {
  // 筛选字段
  name: 'FilterFields',

  components: {
    WkUserDialogSelect,
    XhProuctCate,
    WkProductCategory,
    VDistpicker,
    WkDeptDialogSelect,
    WkPosition,
    WkSearchSelect
  },

  mixins: [AdvancedFilterMixin],

  props: {
    conditionTypeFun: Function, // 根据filed对象中的数据，校准条件。例如产品下的状态是select类型，需要更正为status
    form: Array,
    fieldList: Array,
    disabled: { // 暂时区分是高级筛选 还是 外露 true
      type: Boolean,
      default: false
    },
    // 控制是否展示外露按钮
    showExport: {
      type: Boolean,
      default: true
    },
    // 是外漏展示 启用禁用
    isExport: {
      type: Boolean,
      default: false
    },
    props: Object
  },

  data() {
    return {
      updateFlag: true
    }
  },

  computed: {
    // 合并 props
    config() {
      return merge({ ...DefaultFilterFields }, this.props || {})
    },

    fieldListOptions() {
      return this.fieldList.map(item => {
        return {
          name: item.name,
          value: item.fieldName
        }
      })
    }
  },

  watch: {
    form() {
      this.$nextTick(() => {
        this.$el.scrollTop = this.$el.scrollHeight
      })
    }
  },

  created() {
    if (this.form?.length > 0) {
      // 有自定义签名发请求
      this.form.forEach(item => {
        if (item.formType == 'field_tag') {
          this.getPersonLabel(item)
        }
      })
    }
  },

  mounted() {},

  beforeDestroy() {},

  methods: {

    /**
     * 是否展示条件
     */
    showCalCondition(formType, fieldName, formItem) {
      return this.getAdvancedFilterOptions(this.conditionTypeFun ? this.conditionTypeFun(formItem) : formType, fieldName).length > 0
    },

    /**
     * 值span
     */
    getValueSpan(formType, fieldName, formItem) {
      if (formType == 'business_cause') {
        return 8
      }
      return this.showCalCondition(formType, fieldName, formItem) ? 8 : 13
    },

    /**
     * 商机组状态
     */
    typeOptionsChange(formItem) {
      if (formItem.flowName) {
        const obj = formItem.typeOption.find(item => {
          return item[this.config.businessCauseValueKey] === formItem.flowName
        })
        formItem.settingList = obj.settingList || []
      } else {
        formItem.settingList = []
      }
      formItem.settingName = ''
    },

    /**
     * 获取setting数据类型
     */
    getSettingValueType(setting) {
      if (setting && setting.length > 0) {
        const value = setting[0]
        return typeof value
      }
      return []
    },

    /**
     * 获取个人标签
     */
    getPersonLabel(formItem) {
      this.$nextTick(() => {
        const enterpriseTag = Array.from(new Set(formItem.setting))
        const isCategory = !!(enterpriseTag.length > 0 && enterpriseTag[0].labelList)
        let categoryLabelList = null
        if (isCategory) {
          categoryLabelList = enterpriseTag.map(item => {
            return {
              label: item.name,
              labelList: item.labelList?.map(subItem => subItem.name)
            }
          })
        }
        const options = [{
          label: '企业标签',
          labelList: isCategory ? [] : enterpriseTag
        }]

        if (isCategory) {
          options.push(...categoryLabelList)
        }

        const { showPersonTag, request, params } = this.config
        if (showPersonTag) {
          request(params).then(res => {
            const newTag = []
            // eslint-disable-next-line
            for (const item of (res.data || [])) {
              newTag.push(item.name)
            }
            const personTag = Array.from(new Set(newTag))
            const item = {
              label: '个人标签',
              labelList: personTag
            }
            options.push(item)
            this.$set(formItem, 'groupList', options)
          })
        } else {
          this.$set(formItem, 'groupList', options)
        }
      })
    },

    /**
     * 位置更改
     */
    selectProvince(data, formItem) {
      formItem.address.state = data.value
    },
    selectCity(data, formItem) {
      formItem.address.city = data.value
    },
    selectArea(data, formItem) {
      formItem.address.area = data.value
    },

    /**
     * 用户创建人
     * 产品类别
     */
    arrayValueChange(data) {
      if (data.value.length > 0) {
        data.item.value = data.value
        data.item.valueContent = data.valueContent
      } else {
        data.item.value = []
      }
    },

    /**
     * 用户部门change
     */
    userDepChange(formItem, dataIds, data) {
      // 用于部门员工的展示
      formItem.valueContent = data
    },

    /**
     * 连接条件的变更
     */
    selectChange(event, formItem) {
      this.getAdvancedFilterOptions(this.conditionTypeFun ? this.conditionTypeFun(formItem) : formItem.formType, formItem.fieldName).forEach(item => {
        if (item.value === event) {
          formItem.type = item.type
        }
      })
    },

    /**
     * 聚焦
     */
    fieldFocus() {
      this.$el.click()
    },

    /**
     * 当前选择的字段名改变，判断是否有重复
     * @param formItem
     */
    fieldChange(formItem) {
      const obj = this.fieldList.find(item => {
        return item.fieldName === formItem.fieldName
      })
      if (obj) {
        formItem.formType = obj.formType
        formItem.name = obj.name

        if (formItem.formType == 'field_tag') {
          this.getPersonLabel(formItem)
        }

        this.getAdvancedFilterDefaultItemByFormType(formItem, obj, this.conditionTypeFun)
      }

      this.$emit('on-field-change')
      this.updateFlag = false
      this.$nextTick(() => {
        this.updateFlag = true
      })
      // this.getError()
    },

    /**
     * 聚焦datepicker
     */
    dateRangeSelect(formItem, index) {
      const datePicker = this.$refs[`wkDatePicker${index}`][0]
      this.$nextTick(() => {
        datePicker.focus()
        datePicker.pickerVisible = true
      })
    },

    /**
     * datepicker change
     */
    datePickerChange(formItem) {
      formItem.timeTypeName = ''
      formItem.timeType = ''
    },

    /**
     * 时间筛选配置
     */
    getPickerOptions(item, itemIndex) {
      const types = [
        { text: '本年度', value: 'year' },
        { text: '上一年度', value: 'lastYear' },
        { text: '下一年度', value: 'nextYear' },
        { text: '上半年', value: 'firstHalfYear' },
        { text: '下半年', value: 'nextHalfYear' },
        { text: '本季度', value: 'quarter' },
        { text: '上一季度', value: 'lastQuarter' },
        { text: '下一季度', value: 'nextQuarter' },
        { text: '本月', value: 'month' },
        { text: '上月', value: 'lastMonth' },
        { text: '下月', value: 'nextMonth' },
        { text: '本周', value: 'week' },
        { text: '上周', value: 'lastWeek' },
        { text: '下周', value: 'nextWeek' },
        { text: '今天', value: 'today' },
        { text: '昨天', value: 'yesterday' },
        { text: '明天', value: 'tomorrow' },
        { text: '过去7天', value: 'previous7day' },
        { text: '过去30天', value: 'previous30day' },
        { text: '未来7天', value: 'future7day' },
        { text: '未来30天', value: 'future30day' }
      ]
      const shortcuts = []
      for (let index = 0; index < types.length; index++) {
        const element = types[index]
        shortcuts.push({
          text: element.text,
          onClick: (picker) => {
            picker.$emit('pick', [], false)
            this.$nextTick(() => {
              item.timeTypeName = element.text
              item.timeType = element.value
            })
          }
        })
      }

      return {
        shortcuts: shortcuts
      }
    },

    /**
     * 删除筛选条件
     * @param index
     */
    handleDelete(index) {
      this.$emit('on-field-delete', index)
    },

    /**
     * 外露change事件
     */
    exportChange() {
      this.$emit('on-field-export')
    },

    /**
     * @description: 字符串数组转成name value 对象数组
     * @return {*}
     */
    getObjOptions(list) {
      return list.map(item => {
        return {
          name: item,
          value: item
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.el-row {
  margin-bottom: 8px;
  overflow: hidden;
  line-height: $--input-height;

  .delete-btn {
    margin-left: 15px;
    font-weight: normal;
    color: #bbb;
    cursor: pointer;
  }

  .el-select,
  .el-date-editor {
    width: 100%;

    ::v-deep .el-range__icon {
      line-height: 26px;
    }
  }

  .el-input-number {
    width: 100%;

    ::v-deep input {
      padding: 0 8px;
      text-align: left;
    }
  }

  .date-range-content {
    position: absolute;
    top: 2px;
    right: 30px;
    left: 30px;
    height: $--input-height - 4;
    line-height: $--input-height - 4;
    cursor: pointer;
    background-color: $--input-background-color;
  }

  .date-range-value {
    display: flex;
    align-content: space-around;

    .el-input-number {
      flex: 1;
      width: auto;
    }

    span {
      flex-shrink: 0;
      padding: 0 4px;
    }
  }

  .interval-base {
    width: $--interval-base;
  }
}

</style>
