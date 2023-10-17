<template>
  <div
    class="crm-relative-cell el-input__inner"
    :class="{ 'is-focus': isFocus }">
    <el-select
      :key="refreshKey"
      v-model="dataValue"
      filterable
      remote
      :allow-create="allowCreate"
      :default-first-option="allowCreate"
      :remote-method="remoteMethod"
      :disabled="disabled"
      :clearable="clearable"
      :placeholder="disabled ? '' : placeholder"
      :collapse-tags="collapseTags"
      :multiple="!radio"
      style="width: 100%;"
      @clear="removeTag"
      @change="selectChange"
      @blur="selectBlur"
      @focus="selectFocus">
      <el-option
        v-for="(item, index) in options"
        :key="index"
        :label="getOptionName(item)"
        :title="getOptionName(item)"
        :value="item.value" />
    </el-select>
    <el-button
      class="more-btn"
      icon="el-icon-plus"
      :disabled="disabled"
      @click="contentClick" />
    <crm-relative
      v-if="!disabled&&viewLoaded"
      ref="crmrelative"
      :visible.sync="showSelectView"
      :data-type="dataType"
      :radio="radio"
      :props="props"
      :selected-data="selectedData"
      @changeCheckout="checkInfos">
      <template slot="footer">
        <slot name="footer" />
      </template>
    </crm-relative>
  </div>
</template>
<script type="text/javascript">
import ArrayMixin from './ArrayMixin'
import { isArray, isObject } from '@/utils/types'
import { objDeepCopy } from '@/utils'
import merge from '@/utils/merge'

const DefaulCrmRelativeCell = {
  optionNameFun: null // 自定义option 名称
}

export default {
  name: 'CrmRelativeCell', // 相关模块CRMCell 单类型 自定义字段用
  components: {
    CrmRelative: () =>
      import('./CrmRelative')
  },
  mixins: [ArrayMixin],
  props: {
    dataType: String,
    props: Object, // 主要包含 CrmRelative 组件的 params
    mainProps: Object, // 当前
    // 多选框 只能选一个
    radio: {
      type: Boolean,
      default: true
    },
    placeholder: {
      type: String,
      default() {
        return '请选择'
      }
    },
    allowCreate: Boolean,
    allowCreateFun: Function, // 是否允许创建
    allowCreateItemFun: Function, // 操作添加对象
    collapseTags: Boolean,
    clearable: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      isFocus: false, // 是否聚焦
      showSelectView: false, // 内容
      viewLoaded: false,
      search: '', // 搜索内容
      options: [],
      defaultOptions: [], // 已选择options
      refreshKey: Date.now() // 频繁切换数据源，会导致options有数据但展示id的问题
    }
  },
  computed: {
    config() {
      return merge({ ...DefaulCrmRelativeCell }, this.mainProps || {})
    },

    selectedData() {
      const crmObj = {}
      crmObj[this.dataType] = this.defaultOptions
      return crmObj
    },

    valueKey() {
      return this.getValueKey()
    },
    nameKey() {
      return this.getNameKey()
    }
  },
  watch: {},
  mounted() {},
  methods: {
    /**
     * @description: 获取有效值
     * @return {*}
     */
    getValidValue(value) {
      // 数据只能是对象数组 要不不能正常展示
      if (isArray(value) && value.length > 0) {
        if (isObject(value[0])) {
          value.forEach(item => {
            item.name = this.getShowName(item)
            item.value = item[this.valueKey]
          })
          this.defaultOptions = objDeepCopy(value)
          this.options = objDeepCopy(value)
          this.$nextTick(() => {
            this.refreshKey = Date.now()
          })
          if (this.radio) {
            return value[0][this.valueKey]
          } else {
            return value.map(item => item[this.valueKey])
          }
        }
      } else {
        this.defaultOptions = []
        this.options = []
        this.$nextTick(() => {
          this.refreshKey = Date.now()
        })
      }
      return this.radio ? '' : []
    },

    /**
     * 选中
     */
    checkInfos(data) {
      const dataList = data.data ? data.data : []
      this.defaultOptions = dataList.map(item => {
        return {
          ...item,
          name: this.getShowName(item),
          value: item[this.valueKey]
        }
      })
      this.options = this.defaultOptions
      this.dataValue = this.options.map(item => item[this.valueKey])
      this.$emit('value-change', {
        index: this.index,
        value: objDeepCopy(this.defaultOptions)
      })
    },

    /**
     * 仅支持单选
     */
    removeTag() {
      if (this.disabled) return
      if (this.radio && this.$refs.crmrelative) {
        // 如果单选告知删除
        this.$refs.crmrelative.setSelections([])
      }

      this.dataValue = []
      this.defaultOptions = []

      this.$emit('value-change', {
        index: this.index,
        value: objDeepCopy(this.defaultOptions)
      })
    },

    contentClick() {
      if (this.disabled) return
      this.viewLoaded = true
      this.showSelectView = true
    },

    getNameKey() {
      return {
        leads: 'leadsName',
        customer: 'customerName',
        contacts: ['name', 'contactsName'],
        business: 'businessName',
        contract: ['num', 'contractNum'],
        product: 'name',
        receivables: 'number',
        visit: 'visitNumber',
        invoice: 'invoiceApplyNumber',
        receivablesPlan: 'num'
      }[this.dataType]
    },

    /**
     * @description: 获取值key
     * @return {*}
     */
    getValueKey() {
      return {
        leads: 'leadsId',
        customer: 'customerId',
        contacts: 'contactsId',
        business: 'businessId',
        contract: 'contractId',
        product: 'productId',
        receivables: 'receivablesId',
        visit: 'visitId',
        invoice: 'invoiceId',
        receivablesPlan: 'receivablesPlanId'
      }[this.dataType]
    },

    /**
     * @description: 单选搜索
     * @return {*}
     */
    remoteMethod(search) {
      this.search = search
      const crmIndexRequest = this.getIndexRequest()
      const params = this.getBaseParams()
      params.page = 1
      params.limit = 10
      crmIndexRequest(params).then(res => {
        const list = res.data.list || []
        const options = []

        list.forEach(item => {
          options.push({
            ...item,
            name: this.getShowName(item),
            value: item[this.valueKey]
          })
        })

        const validIds = list.map(item => item[this.valueKey])
        this.defaultOptions.forEach(item => {
          const valueId = item[this.valueKey]
          if (!validIds.includes(valueId)) {
            options.push({
              ...item,
              name: this.getShowName(item),
              value: valueId
            })
          }
        })

        this.options = options
      })
    },

    /**
     * @description: 单选修改
     * @return {*}
     */
    selectChange() {
      const defaultOptions = []
      this.defaultOptions = this.options.filter(item => this.dataValue.includes(item[this.valueKey]))
      const dataValue = []
      if (typeof this.dataValue === 'string' && this.dataValue) {
        dataValue.push(this.dataValue)
      } else if (this.dataValue.length > 0) {
        dataValue.push(...this.dataValue)
      }
      dataValue.forEach(value => {
        const option = this.options.find(item => item[this.valueKey] === value)
        if (option) {
          defaultOptions.push(option)
        } else if (this.allowCreate && this.allowCreateFun) {
          try {
            if (this.allowCreateFun(value)) {
              const itemData = {}
              itemData[this.valueKey] = value
              const nameKey = isArray(this.nameKey) ? this.nameKey[0] : this.nameKey
              itemData[nameKey] = value
              itemData.value = value // 为了外层方便操作
              itemData.name = value
              if (this.allowCreateItemFun) {
                this.allowCreateItemFun(itemData)
              }
              defaultOptions.push(itemData)
            }
          } catch (error) {
            console.log(error)
          }
        }
      })

      this.defaultOptions = defaultOptions
      this.valueChange(this.defaultOptions)
    },

    /**
     * 获取列表请求
     */
    getIndexRequest() {
      return {
      }[this.dataType]
    },
    /**
     * 获取基础参数
     * @returns
     */
    getBaseParams() {
      const params = {
        search: this.search,
        ...(this.props?.params || {})
      }
      return params
    },

    /**
     * @description: 下拉失去焦点
     * @return {*}
     */
    selectBlur() {
      this.search = ''
      this.isFocus = false
    },

    /**
     * @description: 下拉获取焦点
     * @return {*}
     */
    selectFocus() {
      this.isFocus = true
      this.remoteMethod(this.search)
    },

    getShowName(item) {
      if (isArray(this.nameKey)) {
        const validKey = this.nameKey.find(keyItem => !!item[keyItem])
        return validKey ? item[validKey] : ''
      }

      return item[this.nameKey]
    },

    /**
     * @description: 获取选项展示名称 默认展示name
     * @param {*} item
     * @return {*}
     */
    getOptionName(item) {
      if (this.config.optionNameFun) {
        return this.config.optionNameFun(item)
      }
      return item.name
    }
  }
}
</script>
<style lang="scss" scoped>
.crm-relative-cell {
  display: flex;
  width: 100%;
  overflow: hidden;
  border: $--input-border;
  border-radius: $--border-radius-base;

  &.el-input__inner {
    height: auto;
    padding: 0;
    line-height: normal;
  }

  &:hover {
    border-color: $--select-disabled-border;
  }

  &.is-focus {
    border-color: $--select-input-focus-border-color;
  }

  ::v-deep .el-input {
    .el-input__inner {
      height: 28px;
      line-height: 28px;
      border: none;
      border-radius: 0;
    }
  }

  .el-select {
    flex: 1;
  }

  .more-btn {
    flex-shrink: 0;
    padding: 6px;
    border-left: $--input-border;
    border-radius: 0;
  }
}
</style>
