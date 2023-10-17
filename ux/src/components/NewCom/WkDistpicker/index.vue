<template>
  <div class="wk-distpicker">
    <el-cascader
      v-model="dataValue"
      v-bind="$attrs"
      :options="options"
      :props="config"
      @change="handleChange" />
  </div>
</template>

<script>
import DISTRICTS from '../../VDistpicker/districts'
import { valueEquals } from 'element-ui/lib/utils/util'
import { isObject } from '@/utils/types'

export default {
  // WkDistpicker
  name: 'WkDistpicker',

  components: {},

  inheritAttrs: false,

  props: {
    hideArea: { type: Boolean, default: false },
    onlyProvince: { type: Boolean, default: false },
    value: Array,
    props: Object
  },

  data() {
    return {
      dataValue: []
    }
  },

  computed: {
    options() {
      return this.getOptions()
    },

    config() {
      const props = this.props || {}
      return {
        label: 'name',
        value: 'code',
        ...props
      }
    }
  },

  watch: {
    value: {
      handler() {
        this.validateValue()
      },
      immediate: true
    }
  },

  created() {

  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    getOptions() {
      const list = DISTRICTS
      if (!this.onlyProvince && !this.hideArea) {
        return list
      }

      const newList = []
      list.forEach(provinceItem => {
        const province = {
          code: provinceItem.code,
          name: provinceItem.name
        }
        if (!this.onlyProvince) {
          province.children = []
          provinceItem.children.forEach(cityItem => {
            const city = {
              code: cityItem.code,
              name: cityItem.name
            }
            province.children.push(city)
            if (!this.hideArea) {
              city.children = cityItem.children
            }
          })
        }

        newList.push(province)
      })

      return newList
    },

    validateValue() {
      if (this.value && this.value.length) {
        let dataValue = this.value
        if (isObject(this.value[0])) {
          dataValue = this.value.map(item => item[this.config.value])
        }
        if (!valueEquals(dataValue, this.dataValue)) {
          this.dataValue = dataValue
        }
      } else {
        this.dataValue = []
      }
    },

    handleChange() {
      const objValue = this.getCascaderValArr(this.dataValue, this.options)
      this.$emit('input', objValue)
      this.$emit('change', objValue)
    },

    getCascaderValArr(value, data) {
      const res = []
      if (value.length === 0) return res
      let index = 0
      do {
        const findRes = data.find(o => o.code === value[index])
        if (findRes) {
          data = findRes.children || []
          res.push({
            code: findRes.code,
            name: findRes.name,
            id: index + 1
          })
        }
        index++
      } while (index <= value.length)
      return res
    }
  }
}
</script>

<style lang="scss" scoped>
.el-cascader {
  width: 100%;
}
</style>
