<template>
  <div :class="wrapper">
    <el-select v-model="currentProvince" :disabled="disabled || provinceDisabled" placeholder="省" @change="getCities">
      <el-option
        v-for="(item, index) in provinces"
        :key="index"
        :title="item.name"
        :value="item.name"
        :label="item.name" />
    </el-select>
    <template v-if="!onlyProvince">
      <el-select v-model="currentCity" :disabled="disabled || cityDisabled" placeholder="市" @change="getAreas">
        <el-option
          v-for="(item, index) in cities"
          :key="index"
          :title="item.name"
          :value="item.name"
          :label="item.name" />
      </el-select>
      <el-select v-if="!hideArea" v-model="currentArea" :disabled="disabled || areaDisabled" placeholder="区">
        <el-option
          v-for="(item, index) in areas "
          :key="index"
          :title="item.name"
          :value="item.name"
          :label="item.name" />
      </el-select>
    </template>
  </div>
</template>

<script>
import DISTRICTS from './districts'

export default {
  name: 'VDistpicker',
  props: {
    province: { type: [String, Number], default: '' },
    city: { type: [String, Number], default: '' },
    area: { type: [String, Number], default: '' },
    hideArea: { type: Boolean, default: false },
    onlyProvince: { type: Boolean, default: false },
    disabled: { type: Boolean, default: false },
    provinceDisabled: { type: Boolean, default: false },
    cityDisabled: { type: Boolean, default: false },
    areaDisabled: { type: Boolean, default: false },
    wrapper: { type: String, default: 'distpicker-address-wrapper' }
  },
  data() {
    return {
      provinces: [],
      cities: [],
      areas: [],
      currentProvince: '',
      currentCity: '',
      currentArea: ''
    }
  },
  watch: {
    currentProvince(value) {
      this.$emit('province', this.setData(value, this.provinces))
      if (this.onlyProvince) this.emit('selected')
    },
    currentCity(value) {
      this.$emit('city', this.setData(value, this.cities))
      if (this.hideArea) this.emit('selected')
    },
    currentArea(value) {
      this.$emit('area', this.setData(value, this.areas))
      this.emit('selected')
    },
    province(value) {
      this.currentProvince = this.province
      this.cities = this.getDataList(this.currentProvince, this.provinces)
    },
    city(value) {
      this.currentCity = this.city
      this.areas = this.getDataList(this.currentCity, this.cities)
    },
    area(value) {
      this.currentArea = this.area
    }
  },
  created() {
    this.provinces = DISTRICTS
    this.currentProvince = this.getNameValue(this.province, this.provinces)
    this.cities = this.province ? this.getDataList(this.province, this.provinces) : []
    this.currentCity = this.getNameValue(this.city, this.cities)
    this.areas = this.city ? this.getDataList(this.city, this.cities) : []
    this.currentArea = this.getNameValue(this.area, this.areas)
  },
  methods: {
    setData(value, list) {
      const valueObj = list ? list.find(item => item.name === value) : null
      return valueObj ? {
        code: valueObj.code,
        value: valueObj.name
      } : {
        code: '',
        value: ''
      }
    },

    emit(name) {
      const data = {
        province: this.setData(this.currentProvince, this.provinces)
      }

      if (!this.onlyProvince) {
        this.$set(data, 'city', this.setData(this.currentCity, this.cities))
      }

      if (!this.onlyProvince || this.hideArea) {
        this.$set(data, 'area', this.setData(this.currentArea, this.areas))
      }

      this.$emit(name, data)
    },

    getCities() {
      this.currentCity = ''
      this.currentArea = ''
      this.cities = this.getDataList(this.currentProvince, this.provinces)
      this.cleanList('areas')
      if (this.cities.length === 0) {
        this.emit('selected')
      }
    },

    getAreas() {
      this.currentArea = ''
      this.areas = this.getDataList(this.currentCity, this.cities)
      if (this.areas.length === 0) {
        this.emit('selected')
      }
    },

    /**
     * 获取数据信息
     * value code/name
     * list 上一级数据
     */
    getDataList(value, list) {
      const isCode = typeof value === 'number'
      for (let index = 0; index < list.length; index++) {
        const item = list[index]
        if ((isCode && item.code === value) ||
        !isCode && item.name === value) {
          return item.children
        }
      }
    },

    /**
     * 获取名称值
     * value code/name
     * list 当前类型数据
     */
    getNameValue(value, list) {
      if (typeof value === 'number') {
        for (let index = 0; index < list.length; index++) {
          const item = list[index]
          if (item.code === value) {
            return item.name
          }
        }
      }
      return value
    },

    cleanList(name) {
      this[name] = []
    }
  }
}
</script>

<style lang="scss">
.distpicker-address-wrapper {
  color: #9caebf;

  .el-select {
    width: 30%;
  }
}
</style>
