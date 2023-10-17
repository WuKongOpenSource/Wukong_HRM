<template>
  <flexbox align="stretch">
    <flexbox-item style="margin-right: 50px;">
      <div class="area-title">定位<i class="wk wk-icon-fill-help wk-help-tips" data-type="8" data-id="94" /></div>
      <el-autocomplete
        v-model="searchInput"
        :fetch-suggestions="querySearchAsync"
        style="width: 100%;"
        placeholder="请输入详细位置名称"
        @blur="inputBlur"
        @focus="inputFocus"
        @select="handleSelect">
        <template slot-scope="{ item: searchItem }">
          <div
            class="name"
            :title="(searchItem.address || '') + (searchItem.title || '')"
          >{{ (searchItem.address || '') + (searchItem.title || '') }}</div>
        </template>
      </el-autocomplete>
      <div
        ref="addressMap"
        class="map" />
      <div class="area-title">详细地址</div>
      <el-input
        v-model="detailAddress"
        placeholder=""
        @blur="valueChange" />
    </flexbox-item>
    <flexbox-item>
      <div class="area-title">省/市/区</div>
      <v-distpicker
        :province="addressSelect.province"
        :city="addressSelect.city"
        :area="addressSelect.area"
        @province="selectProvince"
        @city="selectCity"
        @area="selectArea" />
    </flexbox-item>
  </flexbox>
</template>
<script type="text/javascript">
import VDistpicker from '@/components/VDistpicker'

import { getBaiduMap } from '@/utils'
import { valueEquals } from 'element-ui/lib/utils/util'
import { isObject, isArray } from '@/utils/types'
import { objDeepCopy } from '@/utils'

export default {
  name: 'XhCustomerAddress', // 新建 客户位置
  components: {
    VDistpicker
  },
  props: {
    value: {
      type: Object,
      default: () => {
        return {}
      }
    },
    index: Number, // 索引值 用于更新数据
    item: Object // 包含数据源
  },
  data() {
    return {
      map: null,
      // 搜索地图输入框
      searchInput: '',
      searchCopyInput: '', // 避免修改
      // 完整地址输入框
      detailAddress: '',
      pointAddress: null, // 经纬度点
      // 区域选择
      addressSelect: {
        province: '',
        city: '',
        area: ''
      },
      // 防止联动情况
      canExecute: true,
      currentValue: null // 验重
    }
  },
  computed: {},
  watch: {
    value(newValue) {
      if (newValue && JSON.stringify(newValue) !== '{}' && !this.valueObjEquals(newValue, this.currentValue)) {
        this.initInfo(this.value)
      }
    }
  },
  mounted() {
    getBaiduMap()
      .then(() => {
        var map = new BMap.Map(this.$refs.addressMap, { enableMapClick: false })
        map.centerAndZoom(new BMap.Point(116.404, 39.915), 14)
        // map.disableDragging() //禁止拖拽
        // map.disableDoubleClickZoom()
        // map.disableScrollWheelZoom()
        // map.disableContinuousZoom()
        map.enableScrollWheelZoom()
        this.map = map
        if (this.value && JSON.stringify(this.value) !== '{}') {
          this.initInfo(this.value)
        } else {
          // 定位逻辑
          // var geolocation = new BMap.Geolocation()
          // var self = this
          // geolocation.getCurrentPosition(
          //   function(r) {
          //     if (this.getStatus() == BMAP_STATUS_SUCCESS) {
          //       self.addMarkerLabel(r.point)
          //     }
          //   },
          //   { enableHighAccuracy: true }
          // )
        }
      })
  },
  methods: {
    /**
     * @description: 载入数据
     * @param {*} val
     * @return {*}
     */
    initInfo(val) {
      this.searchInput = val.location
      this.detailAddress = val.detailAddress
      if (Object.prototype.toString.call(val.address) == '[object Array]') {
        var address = {}
        for (let index = 0; index < val.address.length; index++) {
          index === 0 ? (address['province'] = val.address[0]) : ''
          index === 1 ? (address['city'] = val.address[1]) : ''
          index === 2 ? (address['area'] = val.address[2]) : ''
        }
        this.addressSelect = address
      }
      if (val.lng != 0 && val.lat != 0) {
        this.pointAddress = new BMap.Point(val.lng, val.lat)
        this.addMarkerLabel(this.pointAddress)
      }
    },

    /**
     * @description: 关键词查询
     * @param {*} queryString
     * @param {*} cb
     * @return {*}
     */
    querySearchAsync(queryString, cb) {
      if (queryString) {
        var options = {
          onSearchComplete: function(results) {
            if (local.getStatus() == BMAP_STATUS_SUCCESS) {
              var address = []
              for (var i = 0; i < results.getCurrentNumPois(); i++) {
                address.push(results.getPoi(i))
              }
              cb(address)
            } else {
              cb([])
            }
          },
          pageCapacity: 20
        }
        var local = new BMap.LocalSearch(this.map, options)
        local.search(queryString)
      } else {
        cb([])
      }
    },

    /**
     * @description: 选择了查询结果
     * @param {*} item
     * @return {*}
     */
    handleSelect(item) {
      this.searchInput = (item.address || '') + (item.title || '')
      this.searchCopyInput = this.searchInput // 只能通过这种方式修改

      this.detailAddress = this.searchInput
      this.addMarkerLabel(item.point)
      this.pointAddress = item.point
      this.mapSelectArea(item)
      this.valueChange()
    },

    /**
     * @description: Input 失去焦点  searchInput 只能通过选择更改
     * @param {*}
     * @return {*}
     */
    inputBlur() {
      if (this.searchCopyInput !== this.searchInput) {
        this.searchInput = this.searchCopyInput
        this.valueChange()
      }
    },

    /**
     * @description: 聚焦
     * @param {*}
     * @return {*}
     */
    inputFocus() {
      this.searchCopyInput = this.searchInput
    },

    /**
     * @description: 创建标注
     * @param {*} point
     * @return {*}
     */
    addMarkerLabel(point) {
      this.map.clearOverlays()
      this.map.centerAndZoom(point, 14)
      this.map.addOverlay(new BMap.Marker(point))
    },

    /**
     * @description: 区域选择
     * @param {*} value
     * @return {*}
     */
    selectProvince(value) {
      this.addressSelect.province = value.value
      this.valueChange()
    },
    selectCity(value) {
      this.addressSelect.city = value.value
      this.valueChange()
    },
    selectArea(value) {
      this.addressSelect.area = value.value
      this.valueChange()
    },

    /**
     * @description: 地图选择区域
     * @param {*} data
     * @return {*}
     */
    mapSelectArea(data) {
      if (this.canExecute) {
        this.canExecute = false
        // this.addressSelect.province = data.province ? data.province : "";
        // this.addressSelect.city = data.city ? data.city : "";
        /** 因为poi里面不包含区域 所以需要逆地址解析 */
        var myGeo = new BMap.Geocoder()
        // 根据坐标得到地址描述
        myGeo.getLocation(
          new BMap.Point(data.point.lng, data.point.lat),
          (result) => {
            if (result) {
              // 获取经纬度点
              this.pointAddress = result.point
              this.addressSelect.province = result.addressComponents.province
                ? result.addressComponents.province
                : ''
              this.addressSelect.city = result.addressComponents.city
                ? result.addressComponents.city
                : ''
              this.addressSelect.area = result.addressComponents.district
                ? result.addressComponents.district
                : ''

              this.valueChange()
            }
          }
        )

        setTimeout(() => {
          this.canExecute = true
        }, 500)
      }
    },

    /**
     * @description: 值更新
     * @param {*}
     * @return {*}
     */
    valueChange() {
      const value = {
        address: [
          this.addressSelect.province,
          this.addressSelect.city,
          this.addressSelect.area
        ],
        location: this.searchInput,
        detailAddress: this.detailAddress,
        lat: this.pointAddress ? this.pointAddress.lat : '',
        lng: this.pointAddress ? this.pointAddress.lng : ''
      }
      this.currentValue = objDeepCopy(value)
      this.$emit('value-change', {
        index: this.index,
        value: value
      })
    },

    /**
     * @description: 判断新旧值是否相同
     * @param {*} newVal
     * @param {*} oldVal
     * @return {*}
     */
    valueObjEquals(a, b) {
      if (a === b) return true
      if (!isObject(a)) return false
      if (!isObject(b)) return false
      if (Object.keys(a).length !== Object.keys(b).length) return false
      const keys = Object.keys(a)
      for (let index = 0; index < keys.length; index++) {
        const key = keys[index]
        const aValue = a[key]
        const bValue = b[key]
        if (isArray(aValue)) {
          if (!isArray(bValue)) return false
          if (!valueEquals(aValue, bValue)) return false
        } else {
          if (aValue !== bValue) return false
        }
      }
      return true
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.map {
  width: 100%;
  height: 150px;
  margin-top: 5px;
  overflow: hidden;
}

.area-title {
  color: $--color-text-secondary;
}
</style>
