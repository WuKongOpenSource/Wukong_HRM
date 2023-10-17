<template>
  <div>
    <!-- 此处的ref属性，可以很方便的在vue组件中通过 this.$refs.audio获取该dom元素 -->
    <audio
      ref="audio"
      :src="audioUrl"
      controls="controls"
      style="display: none;"
      @pause="onPause"
      @play="onPlay"
      @timeupdate="onTimeupdate"
      @loadedmetadata="onLoadedmetadata" />

    <!-- 音频播放控件 -->
    <div style="display: flex;">
      <div v-if="loading" class="slider-icon">
        <i class="el-icon-loading" />
      </div>
      <div v-else class="slider-icon">
        <i v-if="!audio.playing" class="iconfont" @click="startPlayOrPause">&#xe600;</i>
        <i v-else class="iconfont" @click="startPlayOrPause">&#xe6ad;</i>
      </div>
      <!--      <button @click="startPlayOrPause">{{ audio.playing | transPlayPause }}</button>-->
      <div class="slider" @mousedown="handleTouchStart">
        <div class="slider-track" />
        <div :style="'width:'+sliderTime+'%'" class="slider-fill" />
        <div :style="'left:'+sliderTime+'%'" class="slider-thumb" />
      </div>
      <div v-if="audio.maxTime > 0" style="width: 100px;" class="slider-time">
        <div v-if="defaultTime">{{ defaultTime }}</div>
        <div v-else-if="!audio.currentTime">{{ audio.maxTime | formatSecond }}</div>
        <div v-else>{{ audio.currentTime | formatSecond }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import merge from '@/utils/merge'

const DefaultAudios = {
  fileRequest: null, // 添加请求和参数
  fileParams: null
}

// 将整数转换成 时：分：秒的格式
function realFormatSecond(second) {
  var secondType = typeof second

  if (secondType === 'number' || secondType === 'string') {
    second = Math.ceil(second)

    var hours = Math.floor(second / 3600)
    second = second - hours * 3600
    var mimute = Math.floor(second / 60)
    second = second - mimute * 60

    return hours + ':' + ('0' + mimute).slice(-2) + ':' + ('0' + second).slice(-2)
  } else {
    return '0:00:00'
  }
}
export default {
  name: 'Audios',
  filters: {
    // 使用组件过滤器来动态改变按钮的显示
    transPlayPause(value) {
      return value ? '暂停' : '播放'
    },
    // 将整数转化成时分秒
    formatSecond(second = 0) {
      return realFormatSecond(second)
    }
  },
  props: {
    props: Object,
    duration: [String, Number] // 时长
  },
  data() {
    return {
      loading: false,
      sliderTime: 0,
      audio: {
        // 该字段是音频是否处于播放状态的属性
        playing: false,
        // 音频当前播放时长
        currentTime: 0,
        // 音频最大播放时长
        maxTime: 0,
        minTime: 0,
        step: 0.1
      },
      defaultTime: '',
      audioUrl: ''
    }
  },
  computed: {
    // 合并 props
    config() {
      return merge({ ...DefaultAudios }, this.props || {})
    }
  },
  watch: {
    duration() {
      this.audioUrl = ''
      if (this.duration == 0) {
        this.audio.maxTime = 0
      }
      this.defaultTime = realFormatSecond(this.duration)
    }
  },
  mounted() {
    this.defaultTime = realFormatSecond(this.duration)
  },
  methods: {
    /** 获取文件路径 */
    filePath() {
      if (!this.audioUrl) {
        this.loading = true
        this.config.fileRequest(this.config.fileParams).then(res => {
          this.audioUrl = URL.createObjectURL(res.data)
          this.defaultTime = ''
          this.$nextTick(() => {
            this.$refs.audio.play()
            this.loading = false
          })
        }).catch(() => {
          this.loading = false
        })
      } else {
        this.$refs.audio.play()
      }
    },
    // 控制音频的播放与暂停
    startPlayOrPause() {
      return this.audio.playing ? this.pause() : this.play()
    },
    // 播放音频
    play() {
      // duration 没传入时忽略0条件
      if ((this.duration !== undefined && this.duration !== null) &&
      this.defaultTime === '0:00:00') {
        return false
      }
      this.filePath()
    },
    // 暂停音频
    pause() {
      this.$refs.audio.pause()
    },
    // 当音频播放
    onPlay() {
      this.audio.playing = true
    },
    // 当音频暂停
    onPause() {
      this.audio.playing = false
    },
    handleFocus() {
      console.log('focues')
    },
    // 当加载语音流元数据完成后，会触发该事件的回调函数
    // 语音元数据主要是语音的长度之类的数据
    onLoadedmetadata(res) {
      // console.log(res)
      this.audio.maxTime = Math.ceil(res.target.duration)
    },
    // 当timeupdate事件大概每秒一次，用来更新音频流的当前播放时间
    // 当音频当前时间改变后，进度条也要改变
    onTimeupdate(res) {
      // console.log(res)
      this.audio.currentTime = res.target.currentTime
      this.sliderTime = Math.ceil(this.audio.currentTime / this.audio.maxTime * 100)
    },

    // 进度条格式化toolTip
    formatProcessToolTip(index = 0) {
      index = Math.ceil(this.audio.maxTime / 100 * index)
      return '进度条: ' + realFormatSecond(index)
    },

    handleTouchStart(e) {
      document.addEventListener('mousemove', this.handleTouchMove)
      document.addEventListener('mouseup', this.handleTouchEnd)
      document.addEventListener('mouseover', this.handleTouchEnd)
      document.addEventListener('mouseleave', this.handleTouchEnd)

      // e.preventDefault();
      // this.onDragStart(e);
    },
    handleTouchMove(e) {
      this.setValue(e)
    },
    handleTouchEnd(e) {
      this.setValue(e)
      document.removeEventListener('mousemove', this.handleTouchMove)
      document.removeEventListener('mouseup', this.handleTouchEnd)
      document.removeEventListener('mouseover', this.handleTouchEnd)
      document.removeEventListener('mouseleave', this.handleTouchEnd)
      // this.onDragStop(e);
    },
    // 从点击位置更新 value
    setValue(e) {
      const $el = this.$el
      const {
        maxTime,
        minTime,
        step
      } = this.audio
      let value = (e.screenX - $el.getBoundingClientRect().left - 36) / 154 * (maxTime - minTime)
      console.log(e.screenX, $el.getBoundingClientRect().left, value, 'value-==')
      value = Math.round(value / step) * step + minTime
      value = parseFloat(value.toFixed(5))

      if (value > maxTime) {
        value = maxTime
      } else if (value < minTime) {
        value = minTime
      }
      this.$refs.audio.currentTime = value
    },
    // 拖动进度条，改变当前时间，index是进度条改变时的回调函数的参数0-100之间，需要换算成实际时间
    changeCurrentTime(index) {
      // this.audio.playing && this.pause();
      // console.log('拖动了',index,this.sliderTime,this.audio.maxTime,parseInt(index / 100 * this.audio.maxTime))
      // !this.audio.playing && this.play();
      this.$refs.audio.currentTime = Math.floor(index / 100 * this.audio.maxTime)
    }
  }
}
</script>

<style lang="scss" scoped>
@font-face {
  font-family: iconfont;  /* project id 1336392 */
  src: url("//at.alicdn.com/t/font_1336392_gv2eli7nq6j.eot");
  src:
    url("//at.alicdn.com/t/font_1336392_gv2eli7nq6j.eot?#iefix") format("embedded-opentype"),
    url("//at.alicdn.com/t/font_1336392_gv2eli7nq6j.woff2") format("woff2"),
    url("//at.alicdn.com/t/font_1336392_gv2eli7nq6j.woff") format("woff"),
    url("//at.alicdn.com/t/font_1336392_gv2eli7nq6j.ttf") format("truetype"),
    url("//at.alicdn.com/t/font_1336392_gv2eli7nq6j.svg#iconfont") format("svg");
}

.iconfont {
  font-family: iconfont !important;
  font-size: 16px;
  font-style: normal;
  color: $--color-primary;
  -webkit-font-smoothing: antialiased;
  -webkit-text-stroke-width: 0.2px;
  -moz-osx-font-smoothing: grayscale;
}

.slider {
  position: relative;
  width: 100%;
  margin-left: 10px;
}

.slider-track {
  position: absolute;
  top: 50%;
  right: 0;
  left: 0;
  width: 160px;
  height: 2px;
  margin-top: -1px;
  background-color: #bec2c1;
}

.slider-fill {
  position: absolute;
  top: 50%;
  left: 0;
  width: 100%;
  height: 2px;
  margin-top: -1px;
  background-color: $--color-primary;
}

.slider-thumb {
  position: absolute;
  top: 50%;
  width: 12px;
  height: 12px;
  color: #e92e35;
  cursor: pointer;
  background-color: $--color-primary;
  border-radius: 50%;
  transform: translate(-50%, -50%);
}

.slider-time {
  margin-left: 20px;
}

.slider-icon {
  width: 30px;
  cursor: pointer;

  .el-icon-loading {
    color: $--color-primary;
  }
}
</style>
