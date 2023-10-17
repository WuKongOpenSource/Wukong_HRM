<template>
  <div
    v-if="typeObj"
    v-clickoutside="clickOutSide"
    class="field-setting">
    <div class="setting-title">
      {{ typeObj.name }}<i
        v-if="titleHelpObj"
        slot="label"
        class="wk wk-icon-fill-help wk-help-tips"
        :data-type="titleHelpObj.type"
        :data-id="titleHelpObj.id"
        @click.stop="" />
    </div>

    <div class="setting-body">
      <template v-if="!isDescText">
        <div class="item-section">
          <div class="name"><span>*</span>标识名<i
            class="wk wk-icon-fill-help wk-help-tips"
            data-type="27"
            data-id="240" /></div>
          <el-input
            v-model="field.name"
            :maxlength="250"
            :disabled="!fieldAuth.nameEdit" />
          <div class="input-tips">标识名不能为空</div>
        </div>

        <div class="item-section">
          <div class="name">提示文字<i
            class="wk wk-icon-fill-help wk-help-tips"
            data-type="27"
            data-id="241" /></div>
          <el-input
            v-model="field.inputTips"
            :rows="3"
            :maxlength="60"
            type="textarea"
            resize="none" />
          <div class="input-tips">显示在标识名右侧的说明文字</div>
        </div>

        <div
          v-if="['user', 'structure'].includes(field.formType)"
          class="item-section">
          <div class="name">选择方式</div>
          <el-radio-group
            v-model="field.isMulti"
            :disabled="!fieldAuth.radioEdit">
            <el-radio
              v-for="(item, index) in [{ label: '单选', value: 0 }, { label: '多选', value: 1 }]"
              :key="index"
              :label="item.value">{{ item.label }}</el-radio>
          </el-radio-group>
        </div>

        <setting-detail-table
          v-if="field.formType === 'detail_table'"
          :field="field"
          @child-edit="emitChildEdit" />

        <template v-if="canOptions">
          <div class="item-section">
            <div class="name"><span>*</span>选项内容</div>
            <div class="input-tips">修改选项后该项设置的逻辑表单会失效</div>
            <setting-options
              :field="field"
              :is-table-child="isTableChild" />
          </div>

          <div v-if="!isTableChild && showLogic" class="item-section">
            <div class="name">逻辑表单<i
              class="wk wk-icon-fill-help wk-help-tips"
              data-type="27"
              data-id="248" /></div>
            <setting-logic-form
              :field="field"
              :point="point"
              :field-arr="fieldArr" />
          </div>
        </template>

        <div v-if="canBusinessRelationship" class="item-section">
          <div class="name">关系类型配置</div>
          <setting-relation-type />
        </div>

        <div v-if="canPrecisions" class="item-section">
          <div class="name">
            {{ precisionsTitle }}
          </div>
          <setting-precisions :field="field" />
        </div>

        <!-- 关注度 -->
        <div v-if="isAttention" class="item-section">
          <setting-attention :field="field" />
        </div>

        <div v-if="canDefault" class="item-section">
          <div class="name">默认值</div>
          <setting-default :field="field" />
        </div>

        <div v-if="canNumber" class="item-section">
          <setting-number :field="field" />
        </div>

        <!-- 编号规则 -->
        <div v-if="isSerialNumber" class="item-section">
          <div class="name">编号规则</div>
          <setting-serial-number
            :field="field"
            :point="point"
            :field-arr="fieldArr" />
        </div>
        <!-- 标签管理 -->
        <div v-if="isTag" class="item-section">
          <div class="name">标签管理<i
            class="wk wk-icon-fill-help wk-help-tips"
            data-type="27"
            data-id="253" /></div>
          <setting-tag :field="field" />
        </div>

      </template>

      <div v-if="isDescText" class="item-section">
        <div class="name">内容</div>
        <setting-desc-text :field="field" />
      </div>

      <div v-if="isPic" class="item-section">
        <div class="name">图片上传数量设置</div>
        <div class="item-check-section">
          <span>
            <span class="require">*</span>最大
          </span>
          <el-select
            v-model="field.maxNumRestrict"
            @change="handleSelectNum($event, 'max')">
            <el-option
              v-for="item in 20"
              :key="item"
              :label="item"
              :value="item" />
          </el-select>
        </div>
        <!-- <div class="item-check-section">
          <span>&nbsp;最小</span>
          <el-select
            v-model="field.minNumRestrict"
            clearable
            @change="handleSelectNum($event, 'min')">
            <el-option
              v-for="item in 3"
              :key="item"
              :label="item"
              :value="item" />
          </el-select>
        </div> -->
        <div class="item-check-section">
          <el-checkbox
            v-model="field.isPhotograph"
            :true-label="1"
            :false-label="0">只允许拍照上传</el-checkbox>
          <div class="input-tips add-style">该功能仅对APP/微信小程序/企业微信/飞书移动客户端有效</div>
          <div class="input-tips">提示：单张图片不超过20M</div>
        </div>
      </div>

      <div v-if="fieldAuth.percentEdit" class="item-section">
        <div class="name">
          字段占比 %<i
            class="wk wk-icon-fill-help wk-help-tips"
            data-type="27"
            data-id="242" />
        </div>
        <el-radio-group
          v-model="field.stylePercent"
          size="medium"
          @change="emitUpdateWidth">
          <el-radio-button
            v-for="item in widthOptions"
            :key="item.value"
            :label="item.value">{{ item.value }}</el-radio-button>
        </el-radio-group>
      </div>

      <div v-if="canTransform && transformData && transformData[field.formType]" class="item-section">
        <div class="name">转化客户字段</div>
        <el-select
          v-model="field.relevant"
          clearable>
          <el-option
            v-for="item in transformData[field.formType]"
            :key="item.value"
            :label="item.label"
            :value="item.value" />
        </el-select>
      </div>

      <div
        v-if="!isDescText && (fieldAuth.nullEdit
          || fieldAuth.uniqueEdit
          || fieldAuth.hiddenEdit)"
        class="item-section">
        <div
          v-if="fieldAuth.nullEdit"
          class="item-check-section">
          <el-checkbox
            v-model="field.isNull"
            :true-label="1"
            :false-label="0">设为必填</el-checkbox><i
              class="wk wk-icon-fill-help wk-help-tips"
              data-type="27"
              data-id="243" />
        </div>

        <div
          v-if="fieldAuth.uniqueEdit"
          class="item-check-section">
          <el-checkbox
            v-model="field.isUnique"
            :true-label="1"
            :false-label="0">设为唯一</el-checkbox><i
              class="wk wk-icon-fill-help wk-help-tips"
              data-type="27"
              data-id="244" />
        </div>

        <div
          v-if="fieldAuth.hiddenEdit"
          class="item-check-section">
          <el-checkbox
            v-model="field.isHidden"
            :true-label="1"
            :false-label="0">隐藏字段</el-checkbox><i
              class="wk wk-icon-fill-help wk-help-tips"
              data-type="27"
              data-id="245" />
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import SettingDefault from './SettingDefault'
import SettingOptions from './SettingOptions'
import SettingNumber from './SettingNumber'
import SettingPrecisions from './SettingPrecisions'
import SettingDescText from './SettingDescText'
import SettingDetailTable from './SettingDetailTable'
import SettingLogicForm from './SettingLogicForm'
import SettingSerialNumber from './SettingSerialNumber'
import SettingTag from './SettingTag'
import SettingAttention from './SettingAttention'
import SettingRelationType from './SettingRelationType'

import FieldTypeLib from '../../fieldTypeLib'
import { getFieldAuth } from '../../utils'

export default {
  name: 'FieldSetting',
  components: {
    SettingDefault,
    SettingOptions,
    SettingNumber,
    SettingPrecisions,
    SettingDescText,
    SettingDetailTable,
    SettingLogicForm,
    SettingSerialNumber,
    SettingTag,
    SettingAttention,
    SettingRelationType
  },
  props: {
    // 是否开启转移  转移对应数据
    canTransform: Boolean,
    transformData: Object,
    field: { // 要编辑的字段信息
      type: Object,
      required: true
    },
    fieldArr: { // 所有字段
      type: Array,
      required: true
    },
    point: { // 被选中的字段坐标
      type: Array,
      required: true
    },
    showLogic: { // 是否使用逻辑表单
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      widthOptions: [
        { value: 25 },
        { value: 50 },
        { value: 75 },
        { value: 100 }
      ],
      stylePercentValue: []
    }
  },
  computed: {
    typeObj() {
      const field = FieldTypeLib.find(o => o.formType === this.field.formType)
      return field || this.field
    },
    fieldAuth() {
      return getFieldAuth(this.field.operating)
    },
    // 是否允许设置字段默认值
    canDefault() {
      return ![
        'user',
        'structure',
        'file',
        'location',
        'handwriting_sign',
        'detail_table',
        'serial_number',
        'field_tag',
        'field_group',
        'pic'
      ].includes(this.field.formType)
    },
    // 是否允许设置选项内容
    canOptions() {
      return [
        'select',
        'checkbox'
      ].includes(this.field.formType)
    },
    // 是否允许设置小数
    canNumber() {
      return [
        'number',
        'floatnumber',
        'percent'
      ].includes(this.field.formType)
    },
    // 精度
    canPrecisions() {
      return [
        'date_interval',
        'position',
        'select',
        'checkbox'
      ].includes(this.field.formType)
    },
    // 精度标题
    precisionsTitle() {
      if (!this.canPrecisions) return ''
      switch (this.field.formType) {
        case 'date_interval':
          return '日期类型'
        case 'position':
          return '地址精度'
        case 'select':
          return '展示方式'
        case 'checkbox':
          return '展示方式'
        default:
          return '精度'
      }
    },
    // 是否为描述文字类型
    isDescText() {
      return this.field.formType === 'desc_text'
    },

    // 是否为自定义编号类型
    isSerialNumber() {
      return this.field.formType === 'serial_number'
    },
    // 是否为自定义标签类型
    isTag() {
      return this.field.formType === 'field_tag'
    },
    // 是否为关注度类型
    isAttention() {
      return this.field.formType === 'field_attention'
    },

    // 是否为明细表格内部字段
    isTableChild() {
      const fatherField = this.fieldArr[this.point[0]][this.point[1]]
      return fatherField.formType === 'detail_table'
    },
    // 帮助信息
    titleHelpObj() {
      const id = {
        boolean_value: '246',
        detail_table: '250',
        field_group: '255'
      }[this.field.formType]

      return id ? {
        type: '27',
        id
      } : null
    },
    // 是否为客户关系
    canBusinessRelationship() {
      return this.field.formType === 'customer_relations'
    },
    isPic() {
      return this.field.formType === 'pic'
    }
  },
  watch: {
    field: {
      handler() {
        this.stylePercentValue = [Number(this.field.stylePercent) || 100]
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    emitUpdateWidth() {
      this.$emit('update-width')
    },
    emitChildEdit(field = null) {
      this.$emit('child-edit', field)
    },
    clickOutSide() {
      this.emitChildEdit()
    },
    handleSelectNum() {
      const { maxNumRestrict, minNumRestrict } = this.field
      if (
        maxNumRestrict &&
        minNumRestrict &&
        maxNumRestrict < minNumRestrict
      ) {
        this.field.minNumRestrict = this.field.maxNumRestrict
      }
    }
  }
}
</script>

<style scoped lang="scss">
.el-checkbox ::v-deep .el-checkbox__label {
  font-size: 13px;
}

.field-setting {
  display: flex;
  flex-direction: column;

  .setting-title {
    font-size: 18px;
    font-weight: bold;
    line-height: 18px;
  }

  .setting-body {
    flex: 1;
    padding: 24px 16px;
    margin-top: 16px;
    overflow-y: auto;
    background-color: white;
    border: $--border-base;
    border-radius: $--border-radius-base;

    .input-tips {
      margin-top: 8px;
      font-size: 12px;
      color: $--color-text-placeholder;
    }

    .add-style {
      margin-top: 0;
      margin-left: 20px;
    }

    .item-section {
      margin-top: 16px;

      .name {
        margin-bottom: 8px;
        font-size: 14px;
        font-weight: 500;

        span {
          color: $--color-r300;
        }
      }

      &:first-child {
        margin-top: 0;
      }
    }

    .item-check-section + .item-check-section {
      margin-top: 8px;
    }
  }
}

::v-deep .el-radio-button {
  .el-radio-button__inner {
    border-width: $--border-width-medium;
  }
}

.setting-options {
  margin-top: 4px;
}

.require {
  color: #ff5630;
}
</style>
