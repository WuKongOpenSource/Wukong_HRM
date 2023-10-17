<template>
  <div
    id="slideVerify"
    v-loading="showLoading"
    class="slide-verify"
    onselectstart="return false;"
    @click.stop>
    <div class="close-box">
      <span
        class="el-icon-close"
        @click="$emit('close')" />
    </div>
    <div class="verify-title">
      <div class="title">
        身份验证
      </div>
      <div class="desc">
        拖动滑块，使图片角度为正
      </div>
    </div>
    <div class="verify-info">
      <div
        :style="{
          backgroundImage: 'url(' + verifyImg + ')',
          transform: 'rotate(' + rotate +'deg)'
        }"
        class="verify-img" />
    </div>
    <!--<div
      class="slide-verify-refresh-icon"
      @click="refresh" />-->

    <!-- container -->
    <div
      :class="{
        'container-active': containerActive,
        'container-success': containerSuccess,
        'container-fail': containerFail
      }"
      class="slide-verify-slider">
      <div
        :style="{width: sliderMaskWidth}"
        class="slide-verify-slider-mask">
        <!-- slider -->
        <div
          :style="{left: sliderLeft}"
          class="slide-verify-slider-mask-item"
          @mousedown="sliderDown"
          @touchstart="touchStartEvent"
          @touchmove="touchMoveEvent"
          @touchend="touchEndEvent">
          <div class="slide-verify-slider-mask-item-icon" />
        </div>
      </div>
      <span class="slide-verify-slider-text">{{ sliderText }}</span>
    </div>
  </div>
</template>

<script>
import merge from '@/utils/merge'
/**
 * vue-monoplasty-slide-verify
 * @see https://github.com/monoplasty/vue-monoplasty-slide-verify
 */

const DefaultSlideVerify = {
  verifyRequest: null // 验证请求和参数
}

export default {
  name: 'SlideVerify',
  props: {
    phone: String,
    props: Object, // 配置对应 DefaultSlideVerify
    sliderText: {
      type: String,
      default: 'Slide filled right'
    }
  },
  data() {
    return {
      w: 300,

      // 拖拽条状态
      containerActive: false, // container active class
      containerSuccess: false, // container success class
      containerFail: false, // container fail class

      verifyImg: this.getRandomImg(),
      originX: undefined,

      // 鼠标按下
      isMouseDown: false,

      // 拖拽柄距离左的距离
      sliderLeft: 0, // block right offset

      rotate: 0,

      // 标记拖拽进度的宽
      sliderMaskWidth: 0, // mask width

      showLoading: false
    }
  },
  computed: {
    // 合并 props
    config() {
      return merge({ ...DefaultSlideVerify }, this.props || {})
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      this.bindEvents()
    },

    // 随机生成img src
    getRandomImg() {
      // this.showLoading = true
      // return (
      //   'https://picsum.photos/300/150/?image=' +
      //   this.getRandomNumberByRange(0, 1084)
      // )
      return `/api/captcha/${this.getRandomNumberByRange(1, 20)}`
    },

    getRandomNumberByRange(start, end) {
      return Math.round(Math.random() * (end - start) + start)
    },

    refresh() {
      this.reset()
      this.$emit('refresh')
    },
    sliderDown(event) {
      this.originX = event.clientX
      this.isMouseDown = true
    },
    touchStartEvent(e) {
      this.originX = e.changedTouches[0].pageX
      this.isMouseDown = true
    },
    bindEvents() {
      document.addEventListener('mousemove', e => {
        if (!this.isMouseDown) return false
        const moveX = e.clientX - this.originX

        if (moveX < 0 || moveX + 38 >= this.w) return false
        this.sliderLeft = moveX + 'px'
        if (moveX > 0) {
          const rotate = parseInt((moveX / 260) * 360)
          this.rotate = rotate > 360 ? 360 : rotate
        } else {
          this.rotate = 0
        }

        this.containerActive = true // add active
        this.sliderMaskWidth = moveX + 'px'
      })
      document.addEventListener('mouseup', e => {
        if (!this.isMouseDown) return false
        this.isMouseDown = false
        if (e.clientX === this.originX) return false
        this.containerActive = false // remove active
        this.verify()
      })
    },

    touchMoveEvent(e) {
      if (!this.isMouseDown) return false
      const moveX = e.pageX - this.originX

      if (moveX < 0 || moveX + 38 >= this.w) return false
      this.sliderLeft = moveX + 'px'
      if (moveX > 0) {
        const rotate = parseInt((moveX / 260) * 360)
        this.rotate = rotate > 360 ? 360 : rotate
      } else {
        this.rotate = 0
      }

      this.containerActive = true // add active
      this.sliderMaskWidth = moveX + 'px'
    },

    touchEndEvent(e) {
      if (!this.isMouseDown) return false
      this.isMouseDown = false
      if (e.pageX === this.originX) return false
      this.containerActive = false // remove active
      this.verify()
    },

    /**
     * 验证结果
     */
    verify() {
      this.showLoading = true
      this.config.verifyRequest({
        tel: this.phone,
        angle: this.rotate
      }).then(res => {
        this.showLoading = false
        if (res.data == 1) {
          this.containerSuccess = true
          this.$emit('success')
        } else {
          this.containerFail = true
          this.$emit('fail')
          setTimeout(() => {
            this.reset()
          }, 1000)
        }
      }).catch(() => {
        this.showLoading = false
        this.reset()
      })
    },

    reset() {
      this.containerActive = false
      this.containerSuccess = false
      this.containerFail = false
      this.sliderLeft = 0
      this.sliderMaskWidth = 0
      this.rotate = 0

      // generate img
      this.verifyImg = this.getRandomImg()
    }
  }
}
</script>

<style scoped lang="scss">
.slide-verify {
  position: relative;
  width: 100%;
  padding: 15px;
  background-color: white;
}

.slide-verify-block {
  position: absolute;
  top: 0;
  left: 0;
}

.close-box {
  text-align: right;

  .el-icon-close {
    font-size: 18px;
    cursor: pointer;
  }
}

.verify-title {
  margin-top: 5px;
  text-align: center;

  .title {
    color: $--color-text-regular;
  }

  .desc {
    margin-top: 5px;
    font-size: 18px;
    color: $--color-text-primary;
  }
}

.verify-info {
  position: relative;
  width: 150px;
  height: 150px;
  margin: 35px auto;
  overflow: hidden;
  background-size: 100% 100%;
}

.verify-img {
  position: relative;
  z-index: 2;
  width: 100%;
  height: 100%;
  pointer-events: none;
  background-repeat: no-repeat;
  background-size: 100% 100%;
  border-radius: 50%;
}

.slide-verify-refresh-icon {
  position: absolute;
  top: 0;
  right: 0;
  width: 34px;
  height: 34px;
  cursor: pointer;
  background: url("~@/assets/icon_light.png") 0 -437px;
  background-size: 34px 471px;
}

.slide-verify-slider {
  position: relative;
  width: 100%;
  height: 40px;
  line-height: 40px;
  color: #45494c;
  text-align: center;
  background: #f7f9fa;
  border: 1px solid #e4e7eb;
}

.slide-verify-slider-mask {
  position: absolute;
  top: 0;
  left: 0;
  height: 40px;
  background: #d1e9fe;
  border: 0 solid #1991fa;
}

.slide-verify-slider-mask-item {
  position: absolute;
  top: 0;
  left: 0;
  width: 40px;
  height: 38px;
  cursor: pointer;
  background: #fff;
  box-shadow: 0 0 3px rgba(0, 0, 0, 0.1);
  transition: background 0.2s linear;
}

.slide-verify-slider-mask-item:hover {
  background: #1991fa;
}

.slide-verify-slider-mask-item:hover .slide-verify-slider-mask-item-icon {
  background-position: 0 -13px;
}

.slide-verify-slider-mask-item-icon {
  position: absolute;
  top: 15px;
  left: 13px;
  width: 14px;
  height: 12px;
  background: url("~@/assets/icon_light.png") 0 -26px;
  background-size: 34px 471px;
}

.container-active .slide-verify-slider-mask-item {
  top: -1px;
  height: 38px;
  border: 1px solid #1991fa;
}

.container-active .slide-verify-slider-mask {
  height: 38px;
  border-width: 1px;
}

.container-success .slide-verify-slider-mask-item {
  top: -1px;
  height: 38px;
  background-color: #52ccba !important;
  border: 1px solid #52ccba;
}

.container-success .slide-verify-slider-mask {
  height: 38px;
  background-color: #d2f4ef;
  border: 1px solid #52ccba;
}

.container-success .slide-verify-slider-mask-item-icon {
  background-position: 0 0 !important;
}

.container-fail .slide-verify-slider-mask-item {
  top: -1px;
  height: 38px;
  background-color: #f57a7a !important;
  border: 1px solid #f57a7a;
}

.container-fail .slide-verify-slider-mask {
  height: 38px;
  background-color: #fce1e1;
  border: 1px solid #f57a7a;
}

.container-fail .slide-verify-slider-mask-item-icon {
  top: 14px;
  background-position: 0 -82px !important;
}

.container-active .slide-verify-slider-text,
.container-success .slide-verify-slider-text,
.container-fail .slide-verify-slider-text {
  display: none;
}
</style>
