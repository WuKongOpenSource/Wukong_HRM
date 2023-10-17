<template>
  <div v-if="ignoreFields.includes(item.field)">
    <slot :data="item" :index="index" />
  </div>
  <el-input
    v-else-if="item.formType == 'text'"
    v-model="fieldFrom[item.field]"
    :disabled="item.disabled || disabled"
    :maxlength="item.maxlength ||100"
    :placeholder="(item.disabled || disabled) ? '' : item.placeholder"
    :type="item.formType"
    @input="commonChange(item, index, $event)" />
  <el-input
    v-else-if="isTrimInput(item.formType, item.disabled || disabled)"
    v-model.trim="fieldFrom[item.field]"
    :disabled="item.disabled || disabled"
    :prefix-icon="getInputIcon(item.formType)"
    :maxlength="getInputMaxlength(item.formType)"
    :placeholder="(item.disabled || disabled) ? '' : item.placeholder"
    type="text"
    @input="commonChange(item, index, $event)" />
  <el-input-number
    v-else-if="item.formType == 'number'"
    v-model="fieldFrom[item.field]"
    :placeholder="(item.disabled || disabled) ? '' : item.placeholder"
    :disabled="item.disabled || disabled"
    :controls="false"
    @input="commonChange(item, index, $event)" />
  <el-input-number
    v-else-if="item.formType == 'floatnumber'"
    v-model="fieldFrom[item.field]"
    :placeholder="(item.disabled || disabled) ? '' : item.placeholder"
    :disabled="item.disabled || disabled"
    :controls="false"
    @change="commonChange(item, index, $event)" />
  <wk-percent-input
    v-else-if="item.formType == 'percent'"
    v-model="fieldFrom[item.field]"
    :max="item.max"
    :min="item.min"
    :placeholder="(item.disabled || disabled) ? '' : item.placeholder"
    :disabled="item.disabled || disabled"
    :controls="false"
    @change="commonChange(item, index, $event)" />
  <!-- <el-input
    v-else-if="item.formType == 'textarea'"
    v-model="fieldFrom[item.field]"
    :disabled="item.disabled || disabled"
    :rows="3"
    :autosize="{ minRows: 3}"
    :maxlength="item.maxlength || 800"
    :placeholder="(item.disabled || disabled) ? '' : item.placeholder"
    :type="item.formType"
    resize="none"
    @input="commonChange(item, index, $event)" /> -->
  <wk-textarea
    v-else-if="item.formType == 'textarea'"
    v-model="fieldFrom[item.field]"
    :disabled="item.disabled || disabled"
    :rows="3"
    :autosize="{ minRows: 3}"
    :maxlength="item.maxlength || 800"
    :placeholder="(item.disabled || disabled) ? '' : item.placeholder"
    :title="item.name"
    resize="none"
    type="textarea"
    @change="commonChange(item, index, $event)" />
  <wk-select
    v-else-if="['select'].includes(item.formType)"
    v-model="fieldFrom[item.field]"
    :disabled="item.disabled || disabled"
    :clearable="item.clearable"
    :placeholder="(item.disabled || disabled) ? '' : item.placeholder"
    :options="item.setting"
    :show-type="item.precisions === 1 ? 'tiled' : 'default'"
    :other-show-input="item.setting.includes(LOGIC_OTHER)"
    @change="commonChange(item, index, $event)" />

  <wk-checkbox
    v-else-if="['checkbox'].includes(item.formType)"
    v-model="fieldFrom[item.field]"
    :disabled="item.disabled || disabled"
    :clearable="item.clearable"
    :placeholder="(item.disabled || disabled) ? '' : item.placeholder"
    :options="item.setting"
    :show-type="item.precisions === 1 ? 'tiled' : 'default'"
    :other-show-input="item.setting.includes(LOGIC_OTHER)"
    @change="commonChange(item, index, $event)" />

  <!-- <el-select
        v-else-if="['checkbox', 'select'].includes(item.formType)"
        v-model="fieldFrom[item.field]"
        :disabled="item.disabled || disabled"
        :clearable="item.clearable"
        :placeholder="(item.disabled || disabled) ? '' : item.placeholder"
        :multiple="item.formType === 'checkbox'"
        style="width: 100%;"
        @change="commonChange(item, index, $event)">
        <el-option
          v-for="(item, index) in item.setting"
          :key="index"
          :label="!isEmptyValue(item.value) ? item.label || item.name : item "
          :value="!isEmptyValue(item.value) ? item.value : item"/>
      </el-select> -->
  <!-- <el-select
        v-else-if="item.formType == 'checkbox'"
        v-model="fieldFrom[item.field]"
        :disabled="item.disabled || disabled"
        :clearable="item.clearable"
        :placeholder="(item.disabled || disabled) ? '' : item.placeholder"
        multiple
        style="width: 100%;"
        @change="commonChange(item, index, $event)">
        <el-option
          v-for="(item, index) in item.setting"
          :key="index"
          :label="!isEmptyValue(item.value) ? item.label || item.name : item "
          :value="!isEmptyValue(item.value) ? item.value : item"/>
      </el-select> -->
  <el-date-picker
    v-else-if="item.formType == 'date'"
    v-model="fieldFrom[item.field]"
    :disabled="item.disabled || disabled"
    clearable
    style="width: 100%;"
    type="date"
    value-format="yyyy-MM-dd"
    :placeholder="(item.disabled || disabled) ? '' : '选择日期'"
    @change="commonChange(item, index, $event)" />
  <el-date-picker
    v-else-if="item.formType == 'dateRange'"
    v-model="fieldFrom[item.field]"
    :disabled="item.disabled || disabled"
    :type="item.dateType || 'daterange'"
    :value-format="item.dateValueFormat || 'yyyy-MM-dd'"
    clearable
    style="width: 100%;vertical-align: middle;"
    :start-placeholder="(item.disabled || disabled) ? '' : '开始日期'"
    :end-placeholder="(item.disabled || disabled) ? '' : '结束日期'"
    @change="commonChange(item, index, $event)" />
  <el-date-picker
    v-else-if="item.formType == 'datetime'"
    v-model="fieldFrom[item.field]"
    :disabled="item.disabled || disabled"
    clearable
    style="width: 100%;"
    type="datetime"
    value-format="yyyy-MM-dd HH:mm:ss"
    :placeholder="(item.disabled || disabled) ? '' : '选择日期'"
    @change="commonChange(item, index, $event)" />
  <wk-dep-select
    v-else-if="item.formType == 'structure' && (item.request || item.props || item.params)"
    v-model="fieldFrom[item.field]"
    :request="item.request"
    :props="item.props"
    :params="item.params"
    :disabled="item.disabled || disabled"
    :radio="!isEmptyValue(item.radio) ? item.radio : true"
    style="width: 100%;"
    @change="depOrUserChange(item, index, arguments[0], arguments[1])"
  />
  <wk-dept-dialog-select
    v-else-if="item.formType == 'structure'"
    v-model="fieldFrom[item.field]"
    :disabled="item.disabled || disabled"
    :radio="!isEmptyValue(item.radio) ? item.radio : true"
    :placeholder="(item.disabled || disabled) ? '' : undefined"
    style="width: 100%;"
    @change="depOrUserChange(item, index, arguments[0], arguments[1])"
  />
  <wk-user-select
    v-else-if="['single_user', 'user'].includes(item.formType) && (item.request || item.props || item.params)"
    v-model="fieldFrom[item.field]"
    :request="item.request"
    :props="item.props"
    :params="item.params"
    :disabled="item.disabled || disabled"
    :radio="!isEmptyValue(item.radio) ? item.radio : true"
    :placeholder="(item.disabled || disabled) ? '' : undefined"
    style="width: 100%;"
    @change="depOrUserChange(item, index, arguments[0], arguments[1])"
  />
  <wk-user-dialog-select
    v-else-if="['single_user', 'user'].includes(item.formType)"
    v-model="fieldFrom[item.field]"
    :disabled="item.disabled || disabled"
    :radio="!isEmptyValue(item.radio) ? item.radio : true"
    :placeholder="(item.disabled || disabled) ? '' : undefined"
    style="width: 100%;"
    @change="depOrUserChange(item, index, arguments[0], arguments[1])"
  />
  <el-radio-group
    v-else-if="item.formType == 'radio'"
    v-model="fieldFrom[item.field]"
    :disabled="item.disabled || disabled"
    :placeholder="(item.disabled || disabled) ? '' : item.placeholder"
    @change="commonChange(item, index, $event)">
    <el-radio
      v-for="(set, setIndex) in item.setting"
      :key="setIndex"
      :label="!isEmptyValue(set.value) ? set.value : set">
      {{ !isEmptyValue(set.value) ? set.label || set.name : set }}
    </el-radio>
  </el-radio-group>
  <el-switch
    v-else-if="item.formType == 'boolean_value'"
    v-model="fieldFrom[item.field]"
    :disabled="item.disabled || disabled"
    active-value="1"
    inactive-value="0"
    @change="commonChange(item, index, $event)" />
  <wk-position
    v-else-if="item.formType == 'position'"
    v-model="fieldFrom[item.field]"
    :hide-area="item.hideArea"
    :only-province="item.onlyProvince"
    :show-detail="item.showDetail"
    :disabled="item.disabled || disabled"
    @change="commonChange(item, index, $event)" />
  <wk-location
    v-else-if="item.formType == 'location'"
    v-model="fieldFrom[item.field]"
    :disabled="item.disabled || disabled"
    @change="commonChange(item, index, $event)" />
  <wk-signature-pad
    v-else-if="item.formType == 'handwriting_sign'"
    v-model="fieldFrom[item.field]"
    :disabled="item.disabled || disabled" />
  <wk-desc-text
    v-else-if="item.formType == 'desc_text'"
    :value="fieldFrom[item.field]" />
  <el-date-picker
    v-else-if="item.formType === 'date_interval'"
    v-model="fieldFrom[item.field]"
    :type="item.dateType || 'daterange'"
    :value-format="item.dateValueFormat || 'yyyy-MM-dd'"
    :disabled="item.disabled || disabled"
    style="width: 100%;vertical-align: middle;"
    clearable
    :start-placeholder="(item.disabled || disabled) ? '' : '开始日期'"
    :end-placeholder="(item.disabled || disabled) ? '' : '结束日期'"
    @change="commonChange(item, index, $event)" />
  <v-distpicker
    v-else-if="item.formType == 'address'"
    :province="fieldFrom[item.field].province"
    :city="fieldFrom[item.field].city"
    :area="fieldFrom[item.field].area"
    @province="selectProvince($event, item, index)"
    @city="selectCity($event, item, index)"
    @area="selectArea($event, item, index)" />
  <xh-files
    v-else-if="item.formType == 'file'"
    :value="fieldFrom[item.field]"
    :disabled="item.disabled || disabled"
    @value-change="oldChange($event, item, index)"
  />
  <wk-detail-table
    v-else-if="item.formType == 'detail_table'"
    :show-type="item.precisions === 2 ? 'table' : 'default'"
    :title="item.name"
    :prop-prefix="item.field"
    :btn-name="item.remark"
    :add-field-list="item.fieldExtendList"
    :add-field-form="item.fieldForm"
    :field-form="fieldFrom[item.field]"
    :field-list="item.fieldList"
    :disabled="item.disabled || disabled"
    :add-disabled="getTableDisabled(item).addDisabled"
    :del-disabled="getTableDisabled(item).delDisabled" />
  <!-- 编号规则 -->
  <el-input
    v-else-if="item.formType == 'serial_number'"
    v-model="fieldFrom[item.field]"
    :maxlength="100"
    :type="item.formType"
    placeholder="根据编号规则自动生成"
    :disabled="getSerialNumberDisabled(item)"
    @input="commonChange(item, index, $event)" />
  <!-- 关注度 -->
  <el-rate
    v-else-if="item.formType == 'field_attention'"
    v-model="fieldFrom[item.field]"
    :disabled="item.disabled || disabled"
    @change="commonChange(item, index, $event)" />
  <!-- 分组标题 -->
  <wk-group
    v-else-if="item.formType == 'field_group'"
    :data="item" />
  <!-- 自定义标签 -->
  <wk-tag
    v-else-if="item.formType=='field_tag'"
    v-model="fieldFrom[item.field]"
    :disabled="item.disabled || disabled"
    :placeholder="(item.disabled || disabled) ? '' : item.placeholder"
    :data="item"
    :options="item.setting"
    @change="commonChange(item, index, $event)" />
  <!-- 富文本 -->
  <tinymce
    v-else-if="item.formType == 'rich_text_format'"
    v-model="fieldFrom[item.field]"
    :height="200"
    :disabled="item.disabled || disabled"
    :init="{
      menubar: false,
      toolbar_sticky: true,
      statusbar: false,
      contextmenu: '',
      content_style: 'p { margin: 5px 0; line-height: 1.5;}',
    }"
    class="rich-txt"
    @onChange="commonChange(item, index, $event)" />
  <!-- 图片 -->
  <div
    v-else-if="item.formType === 'pic' && item.isPhotograph === 1"
    class="field-tips">仅能通过移动端（手机）拍照上传</div>
  <wk-picture
    v-else-if="item.formType === 'pic' && item.isPhotograph !== 1"
    v-model="fieldFrom[item.field]"
    :disabled="item.disabled || disabled"
    :max="Number(item.maxNumRestrict)" />
  <div v-else>
    <slot :data="item" :index="index" />
  </div>
</template>

<script>
import WkUserSelect from '@/components/NewCom/WkUserSelect'
import WkDepSelect from '@/components/NewCom/WkDepSelect'
import WkUserDialogSelect from '@/components/NewCom/WkUserDialogSelect'
import WkDeptDialogSelect from '@/components/NewCom/WkDeptDialogSelect'
import WkPosition from '@/components/NewCom/WkPosition'
import WkLocation from '@/components/NewCom/WkLocation'
import WkSignaturePad from '@/components/NewCom/WkSignaturePad'
import WkDescText from '@/components/NewCom/WkDescText'
import WkPercentInput from '@/components/NewCom/WkPercentInput'
import WkSelect from '@/components/NewCom/WkSelect'
import WkCheckbox from '@/components/NewCom/WkCheckbox'
import WkDetailTable from '@/components/NewCom/WkDetailTable'
import VDistpicker from '@/components/VDistpicker'
import WkTextarea from '@/components/NewCom/WkTextarea'
import { XhFiles } from '@/components/CreateCom'
import WkGroup from '@/components/NewCom/WkGroup'
import WkTag from '@/components/NewCom/WkTag'
import Tinymce from '@/components/Tinymce'
import WkPicture from '@/components/NewCom/WkPicture'

import Mixin from './Mixin'
const LOGIC_OTHER = '其他'

export default {
  // 字段
  name: 'WkField',

  components: {
    WkUserSelect,
    WkDepSelect,
    WkUserDialogSelect,
    WkDeptDialogSelect,
    WkPosition,
    WkLocation,
    WkSignaturePad,
    WkDescText,
    WkPercentInput,
    WkSelect,
    WkCheckbox,
    WkDetailTable,
    VDistpicker,
    XhFiles,
    WkTextarea,
    WkGroup,
    WkTag,
    Tinymce,
    WkPicture
  },

  mixins: [Mixin],

  props: {
    item: Object,
    index: Number,
    fieldFrom: {
      type: Object,
      default: () => {
        return {}
      }
    },
    // 忽略的字段直接输出
    ignoreFields: {
      type: Array,
      default: () => {
        return []
      }
    },
    disabled: Boolean
  },

  data() {
    return {
      LOGIC_OTHER
    }
  },

  computed: {},

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * @description: 获取自定义编号的编辑状态
     * @param {*}
     * @return {*}
     */
    getSerialNumberDisabled(field) {
      const item = field.setting.find(item => item.type === 1)
      if (item) {
        // 优先读取字段里是否允许输入配置
        const allowedInput = item.allowedInput == 1
        if (allowedInput) {
          return false
        } else {
          if (this.disabled) return true
          return !allowedInput
        }
      }
      return this.disabled
    },

    /**
     * @description: 获取明细表格内部项的添加删除权限  根据 tableLevel  判断
     * @param {*} field
     * @return {*}
     */
    getTableDisabled(field) {
      if (field.hasOwnProperty('tableLevel')) {
        const binaryStr = field.tableLevel.toString(2).padStart(3, '0')
        // Boolean(Number(binaryStr.charAt(0))) // 行添加
        // Boolean(Number(binaryStr.charAt(1))) // 行编辑
        // Boolean(Number(binaryStr.charAt(2))) // 行删除
        return {
          addDisabled: !Number(binaryStr.charAt(0)),
          delDisabled: !Number(binaryStr.charAt(2))
        }
      }
      return {
        addDisabled: false,
        delDisabled: false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.el-input-number {
  width: 100%;

  ::v-deep .el-input__inner {
    padding: 0 6px !important;
    text-align: left;
  }
}

.field-tips {
  color: $--color-text-regular;
}
</style>
