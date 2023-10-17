<template>
  <el-cascader
    ref="elCascader"
    v-model="dataValue"
    v-bind="$attrs"
    :options="options"
    :show-all-levels="false"
    :props="defaultProps"
    style="width: 100%;"
    @change="valueChange" />
</template>

<script type="text/javascript">
import { productCategoryIndexAPI } from '@/api/admin/crm'
import { isEmpty } from '@/utils/types'

export default {
  name: 'ProductType', // 产品分类选择
  components: {},
  props: {
    item: Object,
    value: {
      type: [String, Number, Object, Array],
      default: null
    },
    type: {
      type: String,
      default: 'jxc'
    }
  },
  data() {
    return {
      options: [],
      dataValue: null,

      defaultProps: {},
      request: null
    }
  },
  watch: {
    value: {
      handler() {
        if (this.value) {
          if (!this.dataValue || !this.dataValue.includes(this.value)) {
            this.dataValue = this.value
          }
        } else {
          this.dataValue = null
        }
      },
      deep: true,
      immediate: true
    }
  },
  mounted() {
    this.setConfig()
    this.getProductCategoryIndex()
  },
  methods: {
    setConfig() {
      if (this.type === 'crm') {
        this.defaultProps = {
          children: 'children',
          label: 'label',
          value: 'categoryId',
          checkStrictly: true
        }
        this.request = productCategoryIndexAPI
      }
    },
    /** 获取产品分类数据 */
    getProductCategoryIndex() {
      this.request({
        type: 'tree'
      }).then(res => {
        this.options = this.formatOptions(res.data, this.defaultProps.children)
      }).catch(() => {})
    },

    formatOptions(list, key) {
      list.forEach(item => {
        if (item.hasOwnProperty(key)) {
          if (isEmpty(item[key])) {
            delete item[key]
          } else {
            item[key] = this.formatOptions(item[key], key)
          }
        }
      })
      return list
    },

    valueChange(val) {
      console.log('value change: ', val)
      this.$emit('input', val[val.length - 1] || '')
      this.$emit('change', {
        valArr: val,
        value: val,
        item: this.item,
        valueContent: val.length > 0 ? this.$refs.elCascader.getCheckedNodes()[0].label : ''
      })
    }
  }
}
</script>
<style lang="scss" scoped>
</style>
