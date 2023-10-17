<template>
  <div class="map-view">
    <div ref="mapView" class="main-map" />
    <i
      class="el-icon-close map-close"
      @click="hiddenView" />
  </div>
</template>

<script type="text/javascript">
import { getMaxIndex, getBaiduMap } from '@/utils'

export default {
  name: 'MapView', // 地图详情
  components: {},
  props: {
    /**
     * title 内容
     * lat lng 经纬度
     */
    title: {
      type: String,
      default: ''
    },
    lat: {
      type: [String, Number],
      default: 0
    },
    lng: {
      type: [String, Number],
      default: 0
    }
  },
  data() {
    return {}
  },
  computed: {},
  mounted() {
    this.$el.style.zIndex = getMaxIndex()
    document.body.appendChild(this.$el)

    getBaiduMap()
      .then(() => {
        var map = new BMap.Map(this.$refs.mapView, { enableMapClick: false })
        var point = new BMap.Point(parseFloat(this.lng), parseFloat(this.lat))
        map.centerAndZoom(point, 18)
        map.enableScrollWheelZoom()

        var marker = new BMap.Marker(point) // 创建标注
        map.addOverlay(marker) // 将标注添加到地图中
        var infoWindow = new BMap.InfoWindow(this.title) // 创建信息窗口对象
        marker.addEventListener('click', function() {
          map.openInfoWindow(infoWindow, point) // 开启信息窗口
        })
      })
  },
  destroyed() {
    // remove DOM node after destroy
    if (this.$el && this.$el.parentNode) {
      this.$el.parentNode.removeChild(this.$el)
    }
  },
  methods: {
    hiddenView() {
      this.$emit('hidden')
    }
  }
}
</script>
<style lang="scss" scoped>
.map-view {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #000;

  .main-map {
    position: absolute;
    top: 100px;
    right: 100px;
    bottom: 100px;
    left: 100px;
    overflow: hidden;
  }
}

.map-close {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 28px;
  color: #fff;
  cursor: pointer;
}
</style>
