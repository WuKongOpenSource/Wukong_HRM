<template>
  <el-dialog
    :visible="visible"
    :close-on-click-modal="false"
    append-to-body
    title="选择位置"
    width="500px"
    @close="close">
    <flexbox align="stretch">
      <flexbox-item>
        <div class="area-title">定位</div>
        <el-autocomplete
          v-model="searchInput"
          :fetch-suggestions="querySearchAsync"
          style="width: 100%;"
          placeholder="请输入详细位置名称"
          @blur="inputBlur"
          @focus="inputFocus"
          @select="handleSelect">
          <template slot-scope="{ item }">
            <div
              class="name"
              :title="item.address + item.title"
            >{{ item.address + item.title }}</div>
          </template>
        </el-autocomplete>
        <div
          ref="myMap"
          class="map" />
      </flexbox-item>
    </flexbox>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="selectSure">确定</el-button>
      <el-button @click="close">取消</el-button>
    </span>
  </el-dialog>
</template>
<script type="text/javascript">
import { getBaiduMap } from '@/utils'
import { isEmpty } from '@/utils/types'

export default {
  name: 'WkLocationPointDialog',
  components: { },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    value: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  data() {
    return {
      map: null,
      searchInput: '', // 搜索
      searchCopyInput: '', // 避免修改
      pointAddress: null // 经纬度点
    }
  },
  computed: {},
  watch: {
    visible(val) {
      if (val) {
        this.$nextTick(() => {
          getBaiduMap()
            .then(() => {
              const map = new BMap.Map(this.$refs.myMap, { enableMapClick: true })
              let point = new BMap.Point(116.404, 39.915)
              if (this.value) {
                if (!isEmpty(this.value.lat) && !isEmpty(this.value.lng)) {
                  point = new BMap.Point(this.value.lng, this.value.lat)
                }

                if (!isEmpty(this.value.address)) {
                  this.searchInput = this.value.address
                  this.searchCopyInput = this.searchInput
                }
              }

              if (!this.value || (
                this.value && isEmpty(this.value.lat) && isEmpty(this.value.lng) &&
                 isEmpty(this.value.address)
              )) {
                const self = this
                // 定位逻辑
                var geolocation = new BMap.Geolocation()
                geolocation.getCurrentPosition(function(r) {
                  if (this.getStatus() == BMAP_STATUS_SUCCESS) {
                    var myGeo = new BMap.Geocoder({ extensions_town: true })
                    // 根据坐标得到地址描述
                    myGeo.getLocation(r.point, function(result) {
                      if (result) {
                        self.searchInput = result.address
                        self.searchCopyInput = result.address
                        self.addMarkerLabel(result.point)
                        result.title = ''
                        self.pointAddress = result
                      }
                    })
                  }
                })
              }
              map.centerAndZoom(point, 14)
              map.enableScrollWheelZoom()
              this.map = map
              this.addMarkerLabel(point)
            })
        })
      }
    }
  },
  mounted() {

  },
  methods: {
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
     * 搜索结果选择
     **/
    handleSelect(item) {
      this.searchInput = item.address + item.title
      this.searchCopyInput = this.searchInput // 只能通过这种方式修改

      this.addMarkerLabel(item.point)
      this.pointAddress = item
    },
    /**
     * Input 失去焦点  searchInput 只能通过选择更改
     **/
    inputBlur() {
      if (this.searchCopyInput !== this.searchInput) {
        this.searchInput = this.searchCopyInput
      }
    },
    inputFocus() {
      this.searchCopyInput = this.searchInput
    },
    /**
     * 创建标注
     */
    addMarkerLabel(point) {
      this.map.clearOverlays()
      this.map.centerAndZoom(point, 14)
      this.map.addOverlay(new BMap.Marker(point))
    },
    /**
     * 关闭
     */
    close() {
      this.$emit('update:visible', false)
    },
    /**
     * 确定选择
     */
    selectSure() {
      this.$emit('select', this.pointAddress)
      this.close()
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
  padding-bottom: 4px;
  font-size: 14px;
  color: $--color-text-secondary;
}

.distpicker-address-wrapper ::v-deep select {
  height: 34px;
  font-size: 12px;
  border-radius: 0.1rem;
}

::v-deep .el-dialog__body {
  padding: 10px 20px 20px;
}
</style>
