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
      <wk-form
        ref="formAddForm"
        :model="editForm"
        :rules="rules"
        :field-from="editForm"
        :field-list="fields"
        :validate-on-rule-change="false"
        class="is-dialog"
        label-position="top"
        @change="wkFormChange"
      >
        <template slot-scope="scope">
          <!-- <slot :data="scope.data" /> -->
          <v-distpicker
            v-if="scope.data && scope.data.formType == 'city'"
            :province="editForm[scope.data.field].province"
            :city="editForm[scope.data.field].city"
            :disabled="scope.data.disabled"
            hide-area
            @province="selectProvince($event, scope.data)"
            @city="selectCity($event, scope.data)" />
        </template>
      </wk-form>
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
import WkForm from '@/components/NewCom/WkForm'
import VDistpicker from '@/components/VDistpicker'

import { objDeepCopy } from '@/utils'
import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {

  // 组织操作
  name: 'FormAddDialog',
  components: {
    WkForm,
    VDistpicker
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    title: String,
    form: Object,
    rules: Object,
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
      editForm: {}
    }
  },
  computed: {
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.$nextTick(() => {
            if (this.$refs.formAddForm) {
              this.editForm = objDeepCopy(this.form || {})
              this.$refs.formAddForm.$children[0].clearValidate()
            }
          })
        }
      },
      immediate: true
    },
    form: {
      handler(val) {
        this.editForm = objDeepCopy(this.form || {})
      },
      immediate: true
    }
  },
  mounted() {},
  methods: {

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
      this.$refs.formAddForm.$children[0].validate(valid => {
        if (valid) {
          this.$emit('update:form', this.editForm)
          this.$emit('pass')
        }
      })
    },

    /**
     * change 事件
     */
    wkFormChange(item, index, value, valueList) {
      this.$emit('change', item, index, value, valueList, this.editForm)
    },

    /**
     * 选择省市
     */
    selectProvince(data, item) {
      this.editForm[item.field].province = data.value
    },

    selectCity(data, item) {
      this.editForm[item.field].city = data.value
    }
  }
}
</script>
<style lang="scss" scoped>
.form-add-dialog-body {
  max-height: 55vh;
  overflow-x: hidden;
  overflow-y: auto;
}
</style>
