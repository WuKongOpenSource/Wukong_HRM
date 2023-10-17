<template>
  <div v-loading="loading" class="punch-card-field">
    <div class="relation">
      <el-checkbox
        v-model="checkGroup.isOpenPointCard"
        :true-label="1"
        :false-label="0"
      >关联打卡地址</el-checkbox>
      <div class="relation__table">
        <el-button @click="addPunchAddress">+新建打卡地址</el-button>
        <el-table
          :data="punchCardAddressTable"
          style="width: 100%;">
          <el-table-column
            prop="name"
            label="地址名称"
            width="100" />
          <el-table-column
            prop="address"
            label="打卡地址"
            width="width">
            <template slot-scope="{row}">
              {{ row.address+ " | "+ `有效范围${row.pointRange}米` }}
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            align="center"
            width="80">
            <template slot-scope="{row , $index}">
              <el-button type="text" @click="deleteAddress(row,$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <div class="relation">
      <el-checkbox
        v-model="checkGroup.isOpenWifiCard"
        :true-label="1"
        :false-label="0"
      >关联打卡WIFI</el-checkbox>
      <div class="relation__table">
        <el-button @click="addPunchWifi">+新建打卡WIFI</el-button>
        <el-table
          :data="punchCardWifiTable"
          style="width: 100%;">
          <el-table-column
            prop="ssid"
            label="WIFI名称"
            width="100" />
          <el-table-column
            prop="mac"
            label="MAC地址"
            width="width" />
          <el-table-column
            label="操作"
            align="center"
            width="80">
            <template slot-scope="{row ,$index}">
              <el-button type="text" @click="deleteWifi(row,$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <!-- <p>
      <span style="margin-left:10px">自动打卡</span>
      <el-checkbox
        v-model="checkGroup.isAutoCard"
        :true-label="1"
        :false-label="0"
        style="margin-left:10px">
        下班前打过卡，下班将自动打卡
      </el-checkbox>
    </p> -->
    <!-- 新建打卡地址 -->
    <el-dialog
      :visible.sync="addressVisible"
      title="新建打卡地址"
      append-to-body
      width="600px">
      <div class="map-search">
        <div class="search-input">
          <!-- <el-input size="mini">
            <el-button slot="append" icon="el-icon-search"/>
          </el-input> -->
          <el-autocomplete
            v-model="searchInput"
            popper-class="clock-adress-search"
            :fetch-suggestions="querySearchAsync"
            size="mini"
            style="width: 100%;"
            placeholder="请输入详细位置名称"
            @blur="inputBlur"
            @focus="inputFocus"
            @select="handleSelect">
            <template slot-scope="{ item }">
              {{ item.address + item.title }}
            </template>
          </el-autocomplete>
          <el-select v-model="punchCardAddress.radius" size="mini" @change="radiusChange">
            <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value" />
          </el-select>
        </div>

        <div v-if="searchCopyInput" class="search-info">
          {{ searchCopyInput }}
        </div>
      </div>
      <div
        ref="myMap"
        class="map">地图</div>
      <p>打卡地址 : {{ punchCardAddress.address || '' }}</p>
      <p>地址名称 : <el-input
        v-model="punchCardAddress.addressName"
        style="width: 200px;"
        :maxlength="25"
      /> </p>
      <div slot="footer">
        <el-button @click="addressVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmAddress">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 新建wifi打卡 -->
    <el-dialog
      :visible.sync="wifiVisible"
      title="新建打卡Wifi"
      append-to-body
      width="450px">
      <el-form ref="form" :model="wifiForm" label-width="80px">
        <el-form-item label="WiFi名称">
          <el-input v-model="wifiForm.ssid" placeholder="建议与WiFi名一致，避免员工连错WiFi" />
        </el-form-item>
        <el-form-item label="MAC地址">
          <div class="input-group">
            <!-- <el-input v-model="wifiForm.mac[0]" placeholder="" /> :
            <el-input v-model="wifiForm.mac[1]" placeholder="" /> :
            <el-input v-model="wifiForm.mac[2]" placeholder="" /> :
            <el-input v-model="wifiForm.mac[3]" placeholder="" /> :
            <el-input v-model="wifiForm.mac[4]" placeholder="" /> :
            <el-input v-model="wifiForm.mac[5]" placeholder="" /> -->
            <template
              v-for="(item,index) in 6">
              <el-input
                ref="mac"
                :key="index"
                v-model="wifiForm.mac[index]"
                placeholder=""
                @input="macChange(index)"
              />
              {{ index===5?'':':' }}
            </template>

          </div>
          <div>请前往手机端关于悟空查看可连接WiFiMac地址</div>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="wifiVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmWifi">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>

import {
// hrmAddAttendancePointAPI,
// hrmQueryAttendancePointPageListAPI,
// hrmDeleteAttendancePointAPI,
// hrmAddAttendanceWifiAPI,
// hrmDeleteAttendanceWifiAPI
} from '@/api/admin/hrm'

import { getBaiduMap, objDeepCopy } from '@/utils'

export default {
  props: {
    value: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      loading: false,
      addressVisible: false,
      wifiVisible: false,
      wifiForm: {
        mac: [],
        ssid: ''
      },
      punchCardWifiTable: [],
      punchCardAddress: {
        radius: 300,
        address: '',
        addressName: ''
      },
      punchCardAddressTable: [],
      checkGroup: {
        isOpenPointCard: 0,
        isOpenWifiCard: 0
        // isAutoCard: 0
      },

      map: null,
      searchInput: '', // 搜索
      searchCopyInput: '', // 避免修改
      pointAddress: null, // 经纬度点
      options: [
        { label: '100', value: 100 },
        { label: '200', value: 200 },
        { label: '300', value: 300 },
        { label: '400', value: 400 },
        { label: '500', value: 500 },
        { label: '600', value: 600 },
        { label: '700', value: 700 },
        { label: '800', value: 800 },
        { label: '900', value: 900 },
        { label: '1000', value: 1000 },
        { label: '2000', value: 2000 },
        { label: '3000', value: 3000 }
      ],
      circle: null,
      // 定位图标
      centerPoint: {}
    }
  },
  watch: {
    addressVisible(val) {
      if (val) {
        this.$nextTick(() => {
          getBaiduMap()
            .then(() => {
              const map = new BMap.Map(this.$refs.myMap, { enableMapClick: true })
              const point = new BMap.Point(116.404, 39.915)
              map.centerAndZoom(point, 14)
              map.enableScrollWheelZoom()
              this.map = map
              this.addMarkerLabel(point)
              this.centerPoint = point
            })
        })
      } else {
        this.punchCardAddress = {
          radius: 300,
          address: '',
          addressName: ''
        }
        this.pointAddress = null
        this.searchInput = ''
      }
    },
    wifiVisible(val) {
      if (!val) {
        this.wifiForm = {
          mac: [],
          ssid: ''
        }
      }
    },
    checkGroup: {
      handler() {
        this.emitEvent()
      },
      deep: true,
      immediate: true
    },
    punchCardAddressTable() {
      this.emitEvent()
    },
    punchCardWifiTable() {
      this.emitEvent()
    }
  },
  created() {
  },
  mounted() {
    if (Object.keys(this.value).length) {
      const val = objDeepCopy(this.value)
      this.punchCardAddressTable = val.pointList
      this.punchCardWifiTable = val.wifiList
      // const { isOpenPointCard, isOpenWifiCard, isAutoCard } = val
      const { isOpenPointCard, isOpenWifiCard } = val
      this.checkGroup = {
        isOpenPointCard,
        isOpenWifiCard
      }
    }
  },
  methods: {
    radiusChange() {
      this.setCircle()
    },

    emitEvent() {
      const { isOpenPointCard, isOpenWifiCard, isAutoCard } = this.checkGroup
      this.$emit('input', {
        isOpenPointCard,
        isOpenWifiCard,
        isAutoCard,
        wifiList: this.punchCardWifiTable,
        pointList: this.punchCardAddressTable
      })
    },

    /**
     * 删除打卡地址
     */
    deleteWifi(row, index) {
      this.punchCardWifiTable.splice(index, 1)
    },
    /**
     * 删除打卡地址
     */
    deleteAddress(row, index) {
      this.punchCardAddressTable.splice(index, 1)
    },
    /**
     * 添加WiFi
     */
    confirmWifi() {
      const rsg = /^((([a-f0-9]{2}:){5})|(([a-f0-9]{2}-){5}))[a-f0-9]{2}$/i
      const ssid = this.wifiForm.ssid
      const mac = this.wifiForm.mac.join(':') || ''
      if (!ssid) {
        this.$message.error('请输入wifi名字')
      } else if (!mac) {
        this.$message.error('请输入mac地址')
      } else if (!rsg.test(mac)) {
        this.$message.error('mac地址输入有误')
      } else {
        this.punchCardWifiTable.push({ ssid, mac })
        this.wifiVisible = false
        this.wifiForm = {
          mac: [],
          ssid: ''
        }
      }
    },
    confirmAddress() {
      if (!this.punchCardAddress.address) {
        this.$message.error('请选择打卡地址')
        return
      }
      if (!this.punchCardAddress.addressName) {
        this.$message.error('请输入地址名称')
        return
      }
      const params = this.getAddressParams()
      this.punchCardAddressTable.push(params)
      this.punchCardAddress = {
        radius: 300,
        address: '',
        addressName: ''
      }
      this.searchInput = ''
      this.addressVisible = false
    },

    // getAddressList() {
    //   hrmQueryAttendancePointPageListAPI({ pageType: 0 }).then(res => {
    //     this.punchCardWifiTable = res.data.list
    //   }).catch(() => {})
    // },

    getAddressParams() {
      const name = this.punchCardAddress.addressName
      const { address, point } = this.pointAddress
      const { lat, lng } = point
      const pointRange = this.punchCardAddress.radius
      return {
        address,
        lat,
        lng,
        name,
        pointRange
      }
    },
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
      this.centerPoint = item.point
      this.addMarkerLabel(item.point)
      this.pointAddress = item
      this.punchCardAddress.address = item.address
      if (item.title.length > 25) {
        this.punchCardAddress.addressName = item.title.subString(0, 25)
      } else {
        this.punchCardAddress.addressName = item.title
      }
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
      const Circle = new BMap.Circle(point, this.punchCardAddress.radius, {
        strokeColor: '#2362FB',
        fillColor: '#2362FB',
        strokeWeight: 2,
        fillOpacity: 0.05,
        strokeOpacity: 0.5,
        strokeStyle: 'solid'
      })
      this.circle = Circle
      this.map.addOverlay(Circle)
    },
    /**
     * 获取地图信息
     *  lng 经度
        lat 纬度
        type 用来确定模块，这个不确定，先传空
        radius 半径距离
        ownerUserId 负责人
     */
    getMapInfo() {
      this.loading = true
      this.map.clearOverlays()
      this.addMarkerLabel()
      this.setCircle()
      this.loading = false
    },
    /**
     * 添加地址打卡
     */
    addPunchAddress() {
      this.addressVisible = true
    },
    /**
     * 添加wifi打卡
     */
    addPunchWifi() {
      this.wifiVisible = true
    },
    /**
     * 添加圆形覆盖物
    */
    setCircle() {
      if (this.circle) {
        this.removeOverlay(this.circle)
      }
      const Circle = new BMap.Circle(this.centerPoint, this.punchCardAddress.radius, {
        strokeColor: '#2362FB',
        fillColor: '#2362FB',
        strokeWeight: 2,
        fillOpacity: 0.05,
        strokeOpacity: 0.5,
        strokeStyle: 'solid'
      })
      Circle.setCenter(this.centerPoint)
      this.circle = Circle
      this.circle.type = 'circle'
      this.map.addOverlay(Circle)
      this.map.setCenter(this.centerPoint)
      this.map.panTo(this.centerPoint)
      this.map.setViewport(this.markerArr)
    },
    /**
     * 删除指定的覆盖物
    */
    removeOverlay(item) {
      // 获取所有的覆盖物
      var allOverlay = this.map.getOverlays()
      for (let i = 0; i < allOverlay.length; i++) {
        if (allOverlay[i].type == item.type) {
          this.map.removeOverlay(allOverlay[i])
        }
      }
    },
    macChange(index) {
      const mac = this.$refs['mac']
      console.info(mac, 'mac', true)
      if (this.wifiForm.mac[index].length == 2) {
        mac[index].blur()
        if (index < 5) {
          mac[index + 1].focus()
        }
      }
    }
  }
}
</script>

<style lang='scss' scoped>
.relation {
  display: flex;
  margin-left: 20px;

  &__table {
    padding-left: 10px;

    & > .el-button {
      margin-left: 20px;
    }

    .el-table {
      margin-top: 10px;
    }
  }
}

.relation + .relation {
  margin-top: 8px;
}

.input-group {
  display: flex;
}

.el-form-item {
  .input-group,
  .el-input {
    width: 300px;
  }
}

.el-form-item.is-wifi {
  ::v-deep .el-form-item__content {
    display: flex;
    align-content: center;
    align-items: center;
  }
}

.map {
  width: 100%;
  height: 400px;
  margin-top: 5px;
  overflow: hidden;
}

.el-dialog {
  position: relative;

  .map-search {
    position: absolute;
    top: 90px;
    left: 5;
    z-index: 1;

    .search-input {
      display: flex;
      width: 400px;

      ::v-deep .el-autocomplete-suggestion {
        width: auto !important;
      }
    }

    .search-info {
      width: 200px;
      height: auto;
      padding: 5px;
      margin-top: 5px;
      margin-left: 2px;
      background-color: #fff;
    }
  }

  p {
    line-height: 40px;
  }
}

.el-autocomplete {
  ::v-deep .el-autocomplete-suggestion {
    width: auto !important;
  }
}
</style>

<style>
.clock-adress-search {
  width: 500px !important;
}
</style>
