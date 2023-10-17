<template>
  <div
    v-if="visible"
    id="vue-picture-viewer"
    ref="vuePictureViewer"
    :style="maskContainer"
    @mouseup="removeMove()">
    <!-- 头部 -->
    <div class="perview-header">
      <span>{{ title }}</span>
    </div>
    <!-- 图片容器 -->
    <div
      ref="imgContainer"
      class="imgContainer">
      <img
        v-if="bigShowType.isImage"
        ref="bigImg"
        v-src="bigImgUrl"
        :style="
          'transform: scale(' +
            imgScale +
            ') rotate(' +
            imgRotate +
            'deg);margin-top:' +
            imgTop +
            'px;margin-left:' +
            imgLeft +
            'px;' +
            'max-width:80%;max-height:80%;'
        "
        alt=""
        @click.stop=""
        @mousedown="addMove">
      <div
        v-else-if="isAudios(currentFile.name)"
        class="file-show is-audios">
        <i
          class="el-icon-close"
          @click="closeViewer" />

        <div class="content">
          <flexbox>
            <audios
              class="file-audios"
              :props="getAudiosProps(currentFile)" />
            <el-button
              type="primary"
              @click.native="downloadFile">下载</el-button>
          </flexbox>
        </div>
      </div>
      <div
        v-else-if="isVideo(currentFile.name)"
        class="file-show is-video">
        <i
          class="el-icon-close"
          @click="closeViewer" />

        <div class="content">
          <wk-video
            class="file-video"
            :props="getAudiosProps(currentFile)" />
          <div class="file-handle">
            <el-button
              type="primary"
              @click.native="downloadFile">下载</el-button>
          </div>
        </div>
      </div>
      <div
        v-else-if="!bigShowType.isImage"
        class="file-show">
        <div v-if="!showPreviewBtn" class="title">该附件格式不支持预览，请下载后查看</div>
        <i
          class="el-icon-close"
          @click="closeViewer" />
        <div class="content">
          <img
            :src="bigShowType.icon"
            class="file-icon">
          <div class="file-handle">
            <el-button
              v-if="showPreviewBtn"
              type="primary"
              plain
              @click.native="previewFile">预览</el-button>
            <el-button
              type="primary"
              @click.native="downloadFile">下载</el-button>
          </div>
        </div>
      </div>
      <!-- tips -->
      <transition name="fade">
        <div
          v-show="showTips"
          class="tips">{{ tipsText }}</div>
      </transition>
    </div>
    <div class="fixedHandle">
      <!-- 操作按钮 -->
      <flexbox
        v-if="bigShowType.isImage"
        class="handleContainer">
        <div class="handle-box">
          <i
            class="wk wk-zoom-in"
            @click="enlarge" />
          <i
            class="wk wk-zoom-on"
            @click="reduce" />
          <i
            class="wk wk-rotate"
            @click="rotate" />
          <i
            class="wk wk-download"
            @click="downloadFile" />
        </div>

        <div
          class="icon-btn"
          @click="closeViewer">
          <i class="el-icon-close" />
        </div>
      </flexbox>

      <!-- 缩略图容器 -->
      <div
        v-if="imgLength > 1"
        class="thumbnailContainer">
        <ul>
          <li
            v-for="(item, index) in imgData"
            ref="thumbnailItem"
            :key="index"
            @click="switchImgUrl(index, $event)">
            <img
              v-if="isShowImage(item.name)"
              v-src="item.url"
              alt="">
            <img
              v-else
              :src="getFileTypeIconWithSuffix(item.name)"
              alt="">
          </li>
        </ul>
      </div>
    </div>
    <!-- 左边箭头 -->
    <div
      class="leftArrowCon"
      @click="handlePrev"
      @mouseenter="enterLeft"
      @mouseout="outLeft">
      <div
        v-show="leftArrowShow"
        class="icon-btn leftArrow">
        <i class="el-icon-arrow-left" />
      </div>
    </div>
    <!-- 右边箭头 -->
    <div
      class="rightArrowCon"
      @click="handleNext"
      @mouseenter="enterRight"
      @mouseout="outRight">
      <div
        v-show="rightArrowShow"
        class="icon-btn rightArrow">
        <i class="el-icon-arrow-right" />
      </div>
    </div>
  </div>
</template>

<script>
import {
  getMaxIndex,
  getFileIconWithSuffix,
  downloadFileWithBuffer,
  canPreviewFile,
  wkPreviewFile
  // getImageData
} from '@/utils'
import { downloadFileAPI } from '@/api/common'

import Audios from '@/components/Audios'
import WkVideo from './WkVideo'

export default {
  name: 'WkPreviewFile',
  components: {
    Audios,
    WkVideo
  },
  props: {},
  data() {
    return {
      visible: false,
      imgData: [],
      background: 'rgba(0,0,0,0.4)',
      // 选择的索引
      selectIndex: -1,
      // 默认不显示左右切换箭头
      leftArrowShow: false,
      rightArrowShow: false,
      // 图片容器数据
      bigShowType: { isImage: true, icon: '' }, // 不是图片的时候 展示 icon
      bigImgName: '',
      imgLength: 0,
      imgIndex: 0,
      showTips: false,
      tipsText: '',
      maskContainer: {
        width: '100%',
        height: '100%',
        background: this.background,
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0
      },
      // 预览图片样式
      imgTop: 0,
      imgLeft: 0,
      imgScale: 1,
      imgRotate: 0,
      clientX: 0,
      clientY: 0
    }
  },
  computed: {
    title() {
      const fileName = this.bigImgName ? this.bigImgName.slice(0, this.bigImgName.indexOf('.')) : 'pic'

      return `${fileName} （${this.imgIndex + 1} / ${this.imgLength}）`
    },

    currentFile() {
      return this.imgData[this.imgIndex]
    },

    bigImgUrl() {
      return this.currentFile.url
    },

    showPreviewBtn() {
      return canPreviewFile(this.currentFile.name)
    }
  },
  methods: {
    /**
     * 预览
     */
    preview(data) {
      this.selectIndex = data.index
      this.imgData = data.data
      this.imgLength = this.imgData.length
      this.imgIndex = this.selectIndex
      // 如果附件非图片，但支持预览跳转预览页面
      if (this.showPreviewBtn) {
        wkPreviewFile(this.currentFile.url, this.currentFile.name)
        return
      }
      this.visible = true

      this.$nextTick(() => {
        const _dom = document.getElementById('vue-picture-viewer')
        _dom.onmousewheel = this.scrollFunc
        // 火狐浏览器没有onmousewheel事件，用DOMMouseScroll代替(滚轮事件)
        document.body.addEventListener('DOMMouseScroll', this.scrollFunc)
        // 禁止火狐浏览器下拖拽图片的默认事件
        document.ondragstart = function() {
          return false
        }

        document
          .getElementById('vue-picture-viewer')
          .addEventListener('click', e => {
            e.stopPropagation()
          })

        this.$nextTick(() => {
          this.bigImgName = this.imgData[this.imgIndex].name
          this.getShowTypeInfo(this.bigImgName)

          if (this.imgLength > 1) {
            // 大于1的时候才会展示缩略图
            var item = this.$refs.thumbnailItem
            item[this.imgIndex].className = 'borderActive'
          }

          this.init()
        })

        this.maskContainer['z-index'] = getMaxIndex()
      })
    },

    /**
     * init
     */
    init() {
      this.imgTop = 0
      this.imgLeft = 0
      this.imgScale = 1
      this.imgRotate = 0
      this.clientX = 0
      this.clientY = 0
    },

    /**
     * 放大
     */
    enlarge() {
      if (this.imgScale >= 5) return
      this.imgScale += 0.15
    },

    /**
     * 缩小
     */
    reduce() {
      if (this.imgScale <= 0.2) return

      this.imgScale -= 0.15
    },

    /**
     * 旋转
     */
    rotate() {
      this.imgRotate -= 90
    },

    /**
     * 点击缩略图切换图片
     */
    switchImgUrl(num, e) {
      var item = this.$refs.thumbnailItem
      item.forEach(function(i) {
        i.className = ''
      })
      this.imgIndex = num
      this.bigImgName = this.imgData[num].name
      this.getShowTypeInfo(this.bigImgName)
      e.currentTarget.className = 'borderActive'
      if (this.bigShowType.isImage) {
        this.init()
      }
    },

    /**
     * 切换到上一张
     */
    handlePrev() {
      if (this.imgIndex <= 0) {
        this.tips('已经是第一张了!')
        this.imgIndex = 0
      } else {
        this.imgIndex--
        this.bigImgName = this.imgData[this.imgIndex].name
        this.getShowTypeInfo(this.bigImgName)

        var item = this.$refs.thumbnailItem
        item.forEach(function(i) {
          i.className = ''
        })
        item[this.imgIndex].className = 'borderActive'
        if (this.bigShowType.isImage) {
          this.init()
        }
      }
    },

    /**
     * 切换到下一张
     */
    handleNext() {
      if (this.imgIndex + 1 >= this.imgData.length) {
        this.tips('已经是最后一张了!')
      } else {
        this.imgIndex++
        this.bigImgName = this.imgData[this.imgIndex].name
        this.getShowTypeInfo(this.bigImgName)

        var item = this.$refs.thumbnailItem
        item.forEach(function(i) {
          i.className = ''
        })
        item[this.imgIndex].className = 'borderActive'
        if (this.bigShowType.isImage) {
          this.init()
        }
      }
    },

    /**
     * 提示框
     */
    tips(msg) {
      this.showTips = true
      this.tipsText = msg
      const _this = this
      setTimeout(function() {
        _this.showTips = false
      }, 10000)
    },

    /**
     * 鼠标左移
     */
    enterLeft() {
      this.leftArrowShow = true
    },
    outLeft() {
      this.leftArrowShow = false
    },

    /**
     * 鼠标右移
     */
    enterRight() {
      this.rightArrowShow = true
    },
    outRight() {
      this.rightArrowShow = false
    },

    /**
     * 关闭查看器
     */
    closeViewer() {
      if (document.getElementById('vue-picture-viewer')) {
        document
          .getElementById('vue-picture-viewer')
          .removeEventListener('click', e => {
            e.stopPropagation()
          })
        // 移除火狐浏览器下的鼠标滚动事件
        document.body.removeEventListener('DOMMouseScroll', this.scrollFunc)
        // 恢复火狐及Safari浏览器下的图片拖拽
        document.ondragstart = null
      }

      this.visible = false
      this.imgData = []
      this.selectIndex = -1
      this.showTips = false
    },

    /**
     * 附件逻辑
     */
    downloadFile() {
      downloadFileAPI(this.currentFile.url).then(res => {
        downloadFileWithBuffer(res.data, this.currentFile.name)
      }).catch(() => {})
    },
    previewFile() {
      if (this.currentFile.url) {
        wkPreviewFile(this.currentFile.url, this.currentFile.name)
      }
    },
    getShowTypeInfo(name) {
      const temps = name ? name.split('.') : []
      var ext = ''
      if (temps.length > 0) {
        ext = temps[temps.length - 1]
      } else {
        ext = ''
      }

      var icon = getFileIconWithSuffix(ext)
      var isImage = ['jpg', 'png', 'gif', 'jpeg', 'bmp', 'ico'].includes(ext.toLowerCase())
      this.bigShowType = { isImage: isImage, icon: icon }
    },
    getFileTypeIconWithSuffix(name) {
      const temps = name ? name.split('.') : []
      var ext = ''
      if (temps.length > 0) {
        ext = temps[temps.length - 1]
      } else {
        ext = ''
      }
      return getFileIconWithSuffix(ext)
    },
    isShowImage(name) {
      const temps = name ? name.split('.') : []
      var ext = ''
      if (temps.length > 0) {
        ext = temps[temps.length - 1]
      } else {
        ext = ''
      }
      return ['jpg', 'png', 'gif', 'jpeg'].includes(ext.toLowerCase())
    },
    removeMove() {
      this.$refs.vuePictureViewer.onmousemove = null
    },

    /**
     * 鼠标按下
     */
    addMove(e) {
      e = e || window.event
      this.clientX = e.clientX
      this.clientY = e.clientY
      this.$refs.vuePictureViewer.onmousemove = this.moveFunc
    },

    /**
     * 鼠标拖动
     */
    moveFunc(e) {
      e = e || window.event
      e.preventDefault()
      const movementX = e.clientX - this.clientX
      const movementY = e.clientY - this.clientY
      // event.clientY;
      this.imgLeft += movementX * 2
      this.imgTop += movementY * 2
      this.clientX = e.clientX
      this.clientY = e.clientY
    },

    /**
     * 鼠标滚轮缩放
     */
    scrollFunc(e) {
      e = e || window.event
      // e.returnValue = false // ie
      // 火狐下没有wheelDelta，用detail代替，由于detail值的正负和wheelDelta相反，所以取反
      e.delta = e.wheelDelta || -e.detail

      e.preventDefault()
      if (e.delta > 0) {
        // 当滑轮向上滚动时
        this.enlarge()
      }
      if (e.delta < 0) {
        // 当滑轮向下滚动时
        this.reduce()
      }
    },

    /**
     * @description: 根据名称内是否包含mp3 或者 wav 判断是否因为
     * @param {*} name
     * @return {*}
     */
    isAudios(name) {
      if (name) {
        const lowerName = name.toLowerCase()
        return lowerName.includes('mp3') || lowerName.includes('wav')
      }
      return false
    },

    /**
     * @description: 根据名称内是否包含mp4
     * @param {*} name
     * @return {*}
     */
    isVideo(name) {
      if (name) {
        const lowerName = name.toLowerCase()
        return lowerName.includes('mp4')
      }
      return false
    },
    /**
     * @description: 获取录音文件的配置
     * @param {*}
     * @return {*}
     */
    getAudiosProps(item) {
      return {
        fileRequest: downloadFileAPI, // 添加请求和参数
        fileParams: item.url
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.perview-header {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 101;
  width: 100%;
  height: 40px;
  padding: 10px;
  padding: 0 20px;
  line-height: 40px;
  color: rgba(255, 255, 255, 0.8);
  text-align: center;
  cursor: pointer;
  background: rgba(0, 0, 0, 0.6);
}

.leftArrowCon {
  position: absolute;
  top: 40px;
  left: 0;
  z-index: 98;
  width: 30%;
  height: calc(100% - 40px);
  cursor: pointer;
  background: transparent;
}

.rightArrowCon {
  position: absolute;
  top: 40px;
  right: 0;
  z-index: 99;
  width: 30%;
  height: calc(100% - 40px);
  cursor: pointer;
  background: transparent;
}

.leftArrow {
  position: absolute;
  top: 50%;
  left: 25%;
  margin-top: -60px;
  pointer-events: none;
  transition: all 0.5s;
}

.rightArrow {
  position: absolute;
  top: 50%;
  right: 25%;
  margin-top: -60px;
  pointer-events: none;
  transition: all 0.5s;
}

.imgContainer {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  text-align: center;
  vertical-align: middle;
  background: rgba(0, 0, 0, 0.3);
}

.imgContainer .tips {
  position: absolute;
  top: 50%;
  left: 50%;
  min-width: 150px;
  padding: 4px;
  margin-top: -20px;
  margin-left: -60px;
  font-size: 14px;
  line-height: 40px;
  color: #fff;
  text-align: center;
  background: rgba(0, 0, 0, 0.7);
  border-radius: 6px;
}

.fixedHandle {
  position: fixed;
  bottom: 0;
  left: 50%;
  z-index: 100;
  width: 800px;
  height: 140px;
  margin-left: -400px;
  overflow: hidden;
}

.handleContainer {
  position: absolute;
  bottom: 100px;
  left: 50%;
  width: auto;
  margin-left: -150px;

  .handle-box {
    height: 40px;
    padding: 0 20px;
    margin-right: 30px;
    line-height: 40px;
    user-select: none;
    background: rgba(0, 0, 0, 0.6);
    border-radius: 20px;

    i {
      font-size: 20px;
      color: #fff;
      cursor: pointer;
    }

    i + i {
      margin-left: 15px;
    }
  }
}

// 图标按钮
.icon-btn {
  width: 40px;
  height: 40px;
  font-size: 20px;
  line-height: 40px;
  color: white;
  text-align: center;
  cursor: pointer;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 20px;

  i {
    font-weight: 600;
  }
}

.handleItem {
  width: 28px;
  height: 28px;
  color: white;
}

ul {
  padding: 0;
  margin: 0;
}

ul li {
  display: inline-block;
  width: 30px;
  height: 30px;
  margin-left: 20px;
  list-style: none;
  cursor: pointer;
}

.thumbnailContainer {
  position: absolute;
  bottom: 0;
  left: 50%;
  max-width: 800px;
  overflow-x: auto;
  overflow-y: hidden;
  background: rgba(255, 255, 255, 0.7);
  border-top-left-radius: 5px;
  border-top-right-radius: 5px;
  transform: translate(-50%, 0%);
}

.thumbnailContainer ul {
  padding-top: 10px;
  padding-bottom: 10px;
  text-align: center;
  white-space: nowrap;
}

.thumbnailContainer ul li {
  box-sizing: content-box;
  display: inline-block;
  width: 38px;
  height: 38px;
  margin-left: 10px;
  user-select: none;
}

.thumbnailContainer ul li:last-child {
  margin-right: 10px;
}

.thumbnailContainer ul li img {
  width: 100%;
  height: 100%;
  vertical-align: top;
  object-fit: contain;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 1s;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}

/* 添加border */
.borderActive {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.3);
}

/* 修改原生的滚动条 */
::-webkit-scrollbar {
  /* 血槽宽度 */
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-thumb {
  /* 拖动条 */
  background: rgba(0, 0, 0, 0.3);
  border-radius: 6px;
}

::-webkit-scrollbar-track {
  /* 背景槽 */
  background: #ddd;
  border-radius: 6px;
}

/** 文件展示 */
.file-show {
  position: absolute;
  top: 60%;
  left: 50%;
  width: 450px;
  height: 260px;
  padding: 15px;
  margin-top: -150px;
  margin-left: -225px;
  background-color: white;
  border-radius: 3px;

  &.is-audios {
    height: 120px;

    .file-audios {
      flex: 1;
      margin-right: 8px;
    }
  }

  &.is-video {
    top: 8vh;
    left: 10vw;
    z-index: 100;
    width: 80vw;
    height: 80vh;
    margin: 0;

    .content {
      height: calc(100% - 90px);
    }

    .file-handle {
      margin-top: 8px;
    }
  }

  .el-icon-close {
    position: absolute;
    top: 20px;
    right: 20px;
    font-size: 18px;
    font-weight: bold;
    color: #909399;
    cursor: pointer;
  }

  .el-icon-close:hover {
    color: $--color-primary;
  }

  .title {
    position: relative;
    padding-left: 6px;
    font-size: 13px;
    color: $--color-text-regular;
  }

  .title::before {
    position: absolute;
    top: 0;
    left: 0;
    color: red;
    content: "*";
  }

  .content {
    margin-top: 40px;
    text-align: center;

    .file-icon {
      width: 100px;
      width: 85px;
      vertical-align: middle;
    }

    .file-handle {
      display: inline-block;
      width: 100px;
      margin-right: 0;
      margin-left: 50px;
      vertical-align: middle;

      .el-button {
        height: 34px;
        margin-left: 0;
      }

      .el-button + .el-button {
        margin-top: 8px;
      }
    }
  }
}
</style>
