<template>
  <div v-show="showBox" :class="mode=='pop'?'mask':''">
    <div :class="mode=='pop'?'verifybox':''" :style="{'max-width':parseInt(imgSize.width)+32+'px'}">
      <div v-if="mode=='pop'" class="verifybox-top">
        请完成安全验证
        <span class="verifybox-close" @click="closeBox">
          <i class="el-icon-close" />
        </span>
      </div>
      <div class="verifybox-bottom" :style="{padding:mode=='pop'?'16px':'0'}">
        <!-- 验证码容器 -->
        <components
          :is="componentType"
          v-if="componentType"
          ref="instance"
          :captcha-type="captchaType"
          :type="verifyType"
          :figure="figure"
          :arith="arith"
          :mode="mode"
          :v-space="vSpace"
          :explain="explain"
          :img-size="imgSize"
          :block-size="blockSize"
          :bar-size="barSize"
          :default-img="defaultImg"
        />
      </div>
    </div>
  </div>
</template>
<script type="text/babel">
/**
     * Verify 验证码组件
     * @description 分发验证码使用
     * */
import VerifySlide from './Verify/VerifySlide'
import VerifyPoints from './Verify/VerifyPoints'

import { LOCAL_VERIFY_SLIDER, LOCAL_VERIFY_POINT } from '@/utils/constants.js'
import Lockr from 'lockr'

export default {
  name: 'Vue2Verify',
  components: {
    VerifySlide,
    VerifyPoints
  },
  props: {
    // 双语化
    locale: {
      require: false,
      type: String,
      default() {
        // 默认语言不输入为浏览器语言
        if (navigator.language) {
          return navigator.language
        } else {
          return navigator.browserLanguage
        }
      }
    },
    captchaType: {
      type: String,
      required: true
    },
    figure: {
      type: Number
    },
    arith: {
      type: Number
    },
    mode: {
      type: String,
      default: 'pop'
    },
    vSpace: {
      type: Number
    },
    explain: {
      type: String
    },
    imgSize: {
      type: Object,
      default() {
        return {
          width: '310px',
          height: '155px'
        }
      }
    },
    blockSize: {
      type: Object
    },
    barSize: {
      type: Object
    }
  },
  data() {
    return {
      // showBox:true,
      clickShow: false,
      // 内部类型
      verifyType: undefined,
      // 所用组件类型
      componentType: undefined,
      // 默认图片
      defaultImg: require('./assets/image/default.jpg')
    }
  },
  computed: {
    instance() {
      return this.$refs.instance || {}
    },
    showBox() {
      if (this.mode == 'pop') {
        return this.clickShow
      } else {
        return true
      }
    }
  },
  watch: {
    captchaType: {
      immediate: true,
      handler(captchaType) {
        switch (captchaType.toString()) {
          case 'blockPuzzle':
            this.verifyType = '2'
            this.componentType = 'VerifySlide'
            break
          case 'clickWord':
            this.verifyType = ''
            this.componentType = 'VerifyPoints'
            break
        }
      }
    }
  },
  mounted() {
    this.uuid()
  },
  methods: {
    // 生成 uuid
    uuid() {
      var s = []
      var hexDigits = '0123456789abcdef'
      for (var i = 0; i < 36; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1)
      }
      s[14] = '4' // bits 12-15 of the time_hi_and_version field to 0010
      s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1) // bits 6-7 of the clock_seq_hi_and_reserved to 01
      s[8] = s[13] = s[18] = s[23] = '-'

      var slider = LOCAL_VERIFY_SLIDER + '-' + s.join('')
      var point = LOCAL_VERIFY_POINT + '-' + s.join('')
      // 判断下是否存在 slider
      console.log(Lockr.get(LOCAL_VERIFY_SLIDER))
      if (!Lockr.get(LOCAL_VERIFY_SLIDER)) {
        Lockr.set(LOCAL_VERIFY_SLIDER, slider)
      }
      if (!Lockr.get(LOCAL_VERIFY_POINT)) {
        Lockr.set(LOCAL_VERIFY_POINT, point)
      }
    },
    /**
             * i18n
             * @description 兼容vue-i18n 调用$t来转换ok
             * @param {String} text-被转换的目标
             * @return {String} i18n的结果
             * */
    i18n(text) {
      if (this.$t) {
        return this.$t(text)
      } else {
        // 兼容不存在的语言
        const i18n = this.$options.i18n.messages[this.locale] || this.$options.i18n.messages['en-US']
        return i18n[text]
      }
    },
    /**
             * refresh
             * @description 刷新
             * */
    refresh() {
      if (this.instance.refresh) {
        this.instance.refresh()
      }
    },
    closeBox() {
      this.clickShow = false
      this.refresh()
    },
    show() {
      if (this.mode == 'pop') {
        this.clickShow = true
      }
    }
  }
}
</script>
<style lang="scss">
  .verifybox {
    position: relative;
    top: 50%;
    left: 50%;
    box-sizing: border-box;
    line-height: initial;
    background-color: #fff;
    border-radius: 2px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
    transform: translate(-50%, -50%);
  }

  .verifybox-top {
    box-sizing: border-box;
    padding: 16px 16px 0;
    font-size: 18px;
    color: $--color-text-primary;
    text-align: left;
  }

  .verifybox-bottom {
    box-sizing: border-box;
    padding: 15px;
  }

  .verifybox-close {
    position: absolute;
    top: 18px;
    right: 12px;
    width: 24px;
    height: 24px;
    line-height: initial;
    color: $--color-n90;
    text-align: center;
    cursor: pointer;
  }

  .mask {
    position: fixed;
    top: 0;
    left: 0;
    z-index: 1001;
    width: 100%;
    height: 100vh;
    background: rgba(23, 43, 77, 0.5);

    /* display: none; */
    transition: all 0.5s;
  }

  .verify-tips {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 30px;
    line-height: 30px;
    color: #fff;
  }

  .suc-bg {
    background-color: rgba(92, 184, 92, 0.5);
    filter: progid:dximagetransform.microsoft.gradient(startcolorstr=#7f5CB85C, endcolorstr=#7f5CB85C);
  }

  .err-bg {
    background-color: rgba(217, 83, 79, 0.5);
    filter: progid:dximagetransform.microsoft.gradient(startcolorstr=#7fD9534F, endcolorstr=#7fD9534F);
  }

  .tips-enter,
  .tips-leave-to {
    bottom: -30px;
  }

  .tips-enter-active,
  .tips-leave-active {
    transition: bottom 0.5s;
  }

  /* ---------------------------- */

  /* 常规验证码 */
  .verify-code {
    margin-bottom: 5px;
    font-size: 20px;
    text-align: center;
    cursor: pointer;
    border: 1px solid #ddd;
  }

  .cerify-code-panel {
    height: 100%;
    overflow: hidden;
  }

  .verify-code-area {
    float: left;
  }

  .verify-input-area {
    float: left;
    width: 60%;
    padding-right: 10px;
  }

  .verify-change-area {
    float: left;
    line-height: 30px;
  }

  .varify-input-code {
    display: inline-block;
    width: 100%;
    height: 25px;
  }

  .verify-change-code {
    color: #337ab7;
    cursor: pointer;
  }

  .verify-btn {
    width: 200px;
    height: 30px;
    margin-top: 10px;
    color: #fff;
    background-color: #337ab7;
    border: none;
  }

  /* 滑动验证码 */
  .verify-bar-area {
    position: relative;
    box-sizing: content-box;
    text-align: center;
    background: #fff;
    border: $--border-base;
    border-radius: $--border-radius-base;
  }

  .verify-bar-area .verify-move-block {
    position: absolute;
    top: 0;
    left: 0;
    cursor: pointer;
    background: #fff;
    border-radius: $--border-radius-base;
    transition: background 0.2s linear;
  }

  .verify-bar-area .verify-move-block:hover {
    background: $--color-primary;

    .verify-icon {
      color: #fff !important;
    }
  }

  .verify-bar-area .verify-left-bar {
    position: absolute;
    top: -1px;
    left: -1px;
    box-sizing: content-box;
    cursor: pointer;
    border: $--border-base;
    border-radius: $--border-radius-base;
  }

  .verify-img-panel {
    position: relative;
    box-sizing: content-box;
    overflow: hidden;
    border-radius: $--border-radius-base;
  }

  .verify-img-panel .verify-refresh {
    position: absolute;
    top: 0;
    right: 0;
    z-index: 2;
    padding: 4px;
    text-align: center;
    cursor: pointer;
    background-color: rgba(0, 0, 0, 0.12);
    transform: rotateY(180deg);
  }

  .verify-img-panel .icon-refresh {
    font-size: 16px;
    color: #fff;
  }

  .verify-img-panel .verify-gap {
    position: relative;
    z-index: 2;
    background-color: #fff;
    border: 1px solid #fff;
  }

  .verify-bar-area .verify-move-block .verify-sub-block {
    position: absolute;
    z-index: 3;
    text-align: center;

    /* border: 1px solid #fff; */
  }

  .verify-bar-area .verify-move-block .verify-icon {
    font-size: 18px;
  }

  .verify-bar-area .verify-msg {
    z-index: 3;
    font-size: 14px;
    color: $--color-text-regular;
  }
</style>
