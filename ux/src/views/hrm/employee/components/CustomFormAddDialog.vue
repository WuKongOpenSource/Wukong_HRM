<template>
  <el-dialog
    ref="wkDialog"
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    width="700px"
    @close="handleCancel">
    <span slot="title" class="el-dialog__title">
      {{ title }}
      <i
        v-if="helpObj"
        class="wk wk-icon-fill-help wk-help-tips"
        :data-type="helpObj.dataType"
        :data-id="helpObj.dataId" />
    </span>
    <div class="form-add-dialog-body">
      <el-form
        ref="crmForm"
        :model="fieldForm"
        :rules="fieldRules"
        :validate-on-rule-change="false"
        class="wk-form"
        label-position="top">
        <wk-form-item
          v-for="(item, index) in fieldList"
          :key="index"
          :index="index"
          :field-from="fieldForm"
          :item="item"
          :disabled="item.disabled"
          style-percent="50%"
          @change="formChange"
        >
          <template slot-scope="{ data }">
            <v-distpicker
              v-if="data && data.formType == 'area' || data.formType == 'city'"
              :province="fieldForm[data.field].province"
              :city="fieldForm[data.field].city"
              :disabled="data.disabled"
              hide-area
              @province="selectProvince($event, data)"
              @city="selectCity($event, data)" />
          </template>
        </wk-form-item>
      </el-form>
      <slot />
    </div>
    <span
      slot="footer"
      class="dialog-footer">
      <el-button
        v-debounce="handleConfirm"
        type="primary">保存</el-button>
      <el-button @click.native="handleCancel">取消</el-button>
    </span>
  </el-dialog>
</template>

<script>
import {
  hrmEmployeeFieldVerifyAPI
} from '@/api/hrm/employee'

import WkFormItem from '@/components/NewCom/WkForm/WkFormItem'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import CustomFieldsMixin from '@/mixins/CustomFields'
import VDistpicker from '@/components/VDistpicker'

export default {

  // 自定义字段弹窗新建
  name: 'CustomFormAddDialog',
  components: {
    WkFormItem,
    VDistpicker
  },
  mixins: [ElDialogLoadingMixin, CustomFieldsMixin],
  props: {
    id: [String, Number],
    title: String,
    fields: Array,
    helpObj: Object,
    visible: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      fieldList: [],
      fieldForm: {},
      fieldRules: {}
    }
  },
  computed: {
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.getField()
        }
      },
      immediate: true
    }
  },
  mounted() {},
  methods: {
    /**
     * 获取数据
     */
    getField() {
      const list = this.fields
      const assistIds = this.getFormAssistIds(list)

      const fieldList = []
      const fieldRules = {}
      const fieldForm = {}
      list.forEach(item => {
        const temp = this.getFormItemDefaultProperty(item, false)
        temp.show = !assistIds.includes(item.formAssistId)

        // 是否能编辑权限
        if (temp.show) {
          fieldRules[temp.field] = this.getRules(item)
        }

        // 是否可编辑
        if (!temp.hasOwnProperty('disabled')) {
          temp.disabled = false
        }

        // 特殊字段允许多选
        if (typeof temp.radio !== 'boolean') {
          this.getItemRadio(item, temp)
        }

        // 获取默认值
        if (temp.show) {
          fieldForm[temp.field] = this.getItemValue(item, null, this.id ? 'update' : 'save')
        }
        fieldList.push(temp)
      })

      this.fieldList = fieldList
      this.fieldForm = fieldForm
      this.fieldRules = fieldRules
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

    /**
     * 取消选择
     */
    handleCancel() {
      this.$emit('update:visible', false)
    },

    /**
     * 点击确定
     */
    handleConfirm() {
      const crmForm = this.$refs.crmForm
      crmForm.validate(valid => {
        if (valid) {
          const params = this.getSubmiteParams([].concat.apply([], this.fieldList), this.fieldForm, ['isFixed'])
          this.$emit('pass', params)
        } else {
          // 提示第一个error
          this.getFormErrorMessage(crmForm)
          return false
        }
      })
    },

    /**
     * change 事件
     */
    formChange(field, index, value, valueList) {
      if ([
        'select',
        'checkbox'
      ].includes(field.formType) &&
          field.remark === 'options_type' &&
          field.optionsData) {
        const { fieldForm, fieldRules } = this.getFormContentByOptionsChange(this.fieldList, this.fieldForm)
        this.fieldForm = fieldForm
        this.fieldRules = fieldRules
      }
    },

    /**
     * 选择省市
     */
    selectProvince(data, item) {
      this.fieldForm[item.field].province = data.value
    },

    selectCity(data, item) {
      this.fieldForm[item.field].city = data.value
    }
  }
}
</script>
<style lang="scss" scoped>
.form-add-dialog-body {
  max-height: 55vh;
  overflow-x: hidden;
  overflow-y: auto;

  .wk-form {
    display: flex;
    flex-wrap: wrap;

    ::v-deep.is-detail_table {
      width: 100% !important;
    }
  }
}
</style>
