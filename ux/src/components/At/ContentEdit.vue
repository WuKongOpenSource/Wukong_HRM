<template>
  <div :class="['edit-content-ctn',isFocus ? 'active-focus': '']">
    <div
      id="message-desc"
      ref="at"
      contenteditable="true"
      :data-text="showPlaceholder ? placeholder : ''"
      @keyup="handleKeyUp"
      @keydown="handleKeyDown"
      @focus="contendEditFocus"
      @blur="contendEditBlur"
      @click="contendEditClick"
      v-html="parseContendMsg"
    />

    <!-- 模块数据选择 -->
    <module-select
      v-if="moduleSelectShow"
      ref="moduleSelect"
      v-elclickoutside="clickOutSide"
      :style="moduleSelectStyle"
      :search="moduleSelectSearch"
      :props="{
        showUser: true
      }"
      @click.stop=""
      @close="moduleSelectShow = false"
      @change="moduleSelectChange"
    />

    <!-- 模块类型选择 -->
    <module-type-select
      v-if="moduleTypeSelectShow"
      ref="moduleTypeSelect"
      :style="moduleSelectStyle"
      :search="moduleSelectSearch"
      @click.stop=""
      @close="moduleTypeSelectShow = false"
      @change="moduleTypeSelectChange"
    />
  </div>
</template>

<script>
export default {
  name: 'EditByContenteditable',
  components: {
    ModuleSelect: () => import('@/views/oa/workLog/print/ModuleSelect'),
    ModuleTypeSelect: () => import('@/views/oa/workLog/print/ModuleTypeSelect')
  },
  props: {
    placeholder: {
      type: String,
      default: '输入@可选择需通知人员'
    },
    content: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      isFocus: false,
      showPlaceholder: true,

      // 关联模块展示
      moduleSelectShow: false,
      moduleSelectSearch: '', // 搜索
      moduleSelectStyle: {}, // 位置等
      moduleTypeSelectShow: false,
      oldSelection: null,
      currentRange: null, // 当前容器位置信息
      LINE_HEIGHT: 30
    }
  },
  computed: {
    parseContendMsg: function() {
      this.hidePlaceholder()
      return this.content
    }
  },
  created() {
    window.addEventListener('mousewheel', this.windowScrollListener)
  },
  destroyed() {
    // 离开页面时删除该监听
    window.removeEventListener('mousewheel', this.windowScrollListener)
  },
  mounted() {
    this.ObserverDOM()
  },
  methods: {
    ObserverDOM() {
      // 选定要观察变动的目标节点
      const targetNode = this.$refs.at

      // 观察器的配置（需要观察什么变动）
      const config = { attributes: true, childList: true, subtree: true }

      // 当观察到变动时执行的回调函数
      const callback = (mutationsList, observer) => {
      // 处理变动...
        if (mutationsList.length > 1) {
          this.showPlaceholder = false
        } else {
          const {
            type,
            target
          } = mutationsList[0]
          if (type === 'childList') {
            if (target.textContent) {
              this.showPlaceholder = false
            } else {
              this.showPlaceholder = true
            }
          }
        }
      }

      // 创建一个观察器实例并传入回调函数
      const observer = new MutationObserver(callback)

      // 用配置文件对目标节点进行观察
      observer.observe(targetNode, config)
    },

    hidePlaceholder() {
      this.showPlaceholder = !this.content
    },

    windowScrollListener() {
      if (this.currentRange) {
        const rect = this.currentRange.getClientRects()[0]
        this.moduleSelectStyle = {
          top: `${rect.y + this.LINE_HEIGHT}px`,
          left: `${rect.x}px`
        }
      }
    },

    /**
     * @description: 点击选人区域外隐藏选人组件`
     * @return {*}
     */
    clickOutSide() {
      this.showModuleSelect = false
    },

    /**
     * @description: 获取焦点操作
     * @return {*}
     */
    contendEditFocus() {
      this.isFocus = true
    },

    /**
     * @description: 失去焦点操作
     * @return {*}
     */
    contendEditBlur() {
      if (!this.moduleSelectShow) {
        this.isFocus = false
        this.moduleSelectShow = false
      }
    },

    /** ****************富文本逻辑***********************/
    /**
     * @description: 数据获取
     * @return {*}
     */
    moduleSelectChange(data, config) {
      // 多选值
      const allList = []
      if (config.multiple) {
        for (var key in data) {
          const list = data[key]
          if (list.length > 0) {
            allList.push(...list)
          }
        }
      } else {
        // 单条值
        allList.push(data)
      }

      const {
        preSlice,
        restSlice,
        parentNode,
        nextNode,
        node
      } = this.oldSelection
      if (node) {
        parentNode && parentNode.removeChild(node)
      }

      const previousTextNode = new Text(preSlice)

      const getAtButton = (item) => {
        const node = document.createElement('span')
        node.setAttribute('data-wk-type-id', item.id)
        node.setAttribute('data-wk-type', item.type)
        node.setAttribute('contenteditable', false)
        node.className = 'wk-at-tag'
        // \xa0 字符串形式增加空格
        node.textContent = `\xa0@${item.name}\xa0`
        return node
      }

      let nextTextNode = null
      if (nextNode) {
        parentNode.insertBefore(previousTextNode, nextNode)
        allList.forEach(item => {
          // 创建节点
          parentNode.insertBefore(getAtButton(item), nextNode)
        })
        nextTextNode = new Text(restSlice)
        parentNode.insertBefore(nextTextNode, nextNode)
      } else {
        parentNode.appendChild(previousTextNode)
        allList.forEach(item => {
          // 创建节点
          parentNode.appendChild(getAtButton(item))
        })
        nextTextNode = new Text(restSlice)
        parentNode.appendChild(nextTextNode)
      }

      parentNode.appendChild(document.createTextNode('\u200b'))

      if (navigator.userAgent.indexOf('Chrome') != -1 && nextTextNode) {
        const range = new Range()
        range.setStart(nextTextNode, 0)
        range.setEnd(nextTextNode, 0)
        const selection = window.getSelection()
        selection?.addRange(range)
      }

      this.oldSelection = null
    },

    /**
     * @description: 模块类型选择
     * @return {*}
     */
    moduleTypeSelectChange(data) {
      // this.relateDataChange(data)
    },

    /**
     * @description: 监听富文本按键输入
     * @return {*}
     */
    handleKeyDown(e) {
      if (this.moduleSelectShow || this.moduleTypeSelectShow) {
        if (
          e.code === 'ArrowUp' ||
          e.code === 'ArrowDown' ||
          e.code === 'Enter'
        ) {
          e.preventDefault()
          // e.stopPropagation()
          // e.stopImmediatePropagation()
          return false
        }
      }
    },

    /**
     * @description: 键盘抬起时的处理
     * @param {*} e
     * @return {*}
     */
    handleKeyUp(e) {
      const showAtInfo = this.showAt()
      this.showAtInfo = showAtInfo
      if (showAtInfo.value && showAtInfo.type === 'at') {
        const user = this.getAtUser(showAtInfo.type)
        this.moduleSelectSearch = this.replaceString(user || '', showAtInfo.type)
        if (this.moduleSelectShow) {
          // this.$refs.moduleSelect.keyDownHandler(e)
        } else {
          this.oldSelection = this.getOldSelection()
          const position = this.getRangeRect()
          this.moduleSelectStyle = {
            top: `${position.y}px`,
            left: `${position.x}px`
          }
          this.moduleSelectShow = true
        }
      } else if (showAtInfo.value && showAtInfo.type === 'line') {
        const user = this.getAtUser(showAtInfo.type)
        this.moduleSelectSearch = this.replaceString(user || '', showAtInfo.type)
        if (this.moduleTypeSelectShow) {
          // this.$refs.moduleTypeSelect.keyDownHandler(e)
        } else {
          const position = this.getRangeRect()
          this.moduleSelectStyle = {
            top: `${position.y}px`,
            left: `${position.x}px`
          }
          this.moduleTypeSelectShow = true
        }
      } else {
        this.moduleSelectShow = false
        this.moduleTypeSelectShow = false
      }

      if (this.moduleSelectShow || this.moduleTypeSelectShow) {
        if (
          e.code === 'ArrowUp' ||
          e.code === 'ArrowDown' ||
          e.code === 'Enter'
        ) {
          e.preventDefault()
          // e.stopPropagation()
          // e.stopImmediatePropagation()
          // return false
        }
      }
    },

    /**
     * @description: 获取@后的关键词
     * @param {*} raw
     * @return {*}
     */
    replaceString(raw, type = 'at') {
      const regx = type === 'at' ? /@([^@\s]*)$/ : /\/([^\/\s]*)$/
      return raw.replace(regx, '')
    },

    /**
     * @description: 获取光标位置
     * @return {*}
     */
    getCursorIndex() {
      const selection = window.getSelection()
      return selection?.focusOffset
    },

    /**
     * @description: 获取节点
     * @return {*}
     */
    getRangeNode() {
      const selection = window.getSelection()
      return selection?.focusNode
    },

    /**
     * @description: 获取 @ 用户
     * @return {*}
     */
    getAtUser(type) {
      const content = this.getRangeNode()?.textContent || ''
      const regx = type === 'at' ? /@([^@\s]*)$/ : /\/([^\/\s]*)$/
      const match = regx.exec(content.slice(0, this.getCursorIndex()))
      if (match && match.length === 2) {
        return match[1]
      }
      return undefined
    },

    /**
     * @description: 获取位置
     * @return {*}
     */
    getRangeRect() {
      const selection = window.getSelection()
      const range = selection?.getRangeAt(0)
      const rect = range.getClientRects()[0]

      this.currentRange = range
      return {
        x: rect.x,
        y: rect.y + this.LINE_HEIGHT
      }
    },

    /**
     * @description: 是否展示 @ 数据选择框
     * @return {*}
     */
    showAt() {
      const node = this.getRangeNode()
      if (!node || node.nodeType !== Node.TEXT_NODE) return false
      const content = node.textContent || ''
      const atRegx = /@([^@\s]*)$/
      const cursorIndex = this.getCursorIndex()
      const match = atRegx.exec(content.slice(0, cursorIndex))
      if (match && match.length === 2) {
        return {
          type: 'at',
          value: true
        }
      }

      return {
        type: '',
        value: false
      }

      // const lineRegx = /\/([^\/\s]*)$/
      // const lMatch = lineRegx.exec(content.slice(0, cursorIndex))
      // return {
      //   type: 'line',
      //   value: lMatch && lMatch.length === 2
      // }
    },

    /**
     * @description: 移除At输入的节点
     * @return {*}
     */
    getRemoveInputNode() {
      const node = this.getRangeNode()
      if (node) {
        const content = node?.textContent || ''
        const endIndex = this.getCursorIndex()
        const preSlice = this.replaceString(content.slice(0, endIndex), this.showAtInfo.type)
        const restSlice = content.slice(endIndex)

        const parentNode = node?.parentNode
        const nextNode = node?.nextSibling
        parentNode && parentNode.removeChild(node)

        return {
          preSlice,
          restSlice,
          parentNode,
          nextNode
        }
      }

      return {
        preSlice: '',
        restSlice: ''
      }
    },

    /**
     * @description: 获取就信心
     * @return {*}
     */
    getOldSelection() {
      const node = this.getRangeNode()
      if (node) {
        const content = node?.textContent || ''
        const endIndex = this.getCursorIndex()
        const preSlice = this.replaceString(content.slice(0, endIndex), this.showAtInfo.type)
        const restSlice = content.slice(endIndex)

        const parentNode = node?.parentNode
        const nextNode = node?.nextSibling
        // parentNode && parentNode.removeChild(node)

        return {
          preSlice,
          restSlice,
          parentNode,
          nextNode,
          node
        }
      }

      return {
        preSlice: '',
        restSlice: ''
      }
    },

    /**
     * @description: 编辑器点击
     * @return {*}
     */
    contendEditClick() {
      this.moduleSelectShow = false
      this.moduleTypeSelectShow = false
    }

  }
}
</script>
<style lang="scss" scoped>
.edit-content-ctn {
  position: relative;
  padding: 5px 6px;

  // 不设置字体的话 空格的宽度会很宽
  font-family: Avenir, Helvetica, Arial, sans-serif;
  font-size: 14px;
  line-height: 32px;
  -webkit-user-select: none; /* Chrome/Safari/Opera */
  -khtml-user-select: none; /* Konqueror */
  -moz-user-select: none; /* Firefox */
  -ms-user-select: none; /* Internet Explorer/Edge */
  user-select: none;
  background-color: #fafbfc;
  border: 2px solid #dfe1e6;
  border-radius: 3px;

  // 得加上这个 不然点击下拉li的时候 有bug
  -webkit-touch-callout: none; /* iOS Safari */

  &:hover {
    color: #172b4d;
    background-color: #ebecf0;
    border-color: #dfe1e6;
  }

  #message-desc {
    min-height: 77px;
    line-height: 1.5;
    -webkit-user-select: text;
    user-select: text;
    outline: none !important;

    ::v-deep .wk-at-tag {
      color: #0052cc;
      cursor: pointer;
    }

    // 模拟 placeholder
    &::before {
      position: absolute;
      width: 100%;
      overflow: hidden;
      color: #7a869a;
      text-overflow: ellipsis;
      white-space: nowrap;
      cursor: text;
      content: attr(data-text);
    }
  }

  .module-type-select-wrap,
  .module-select-wrap {
    position: fixed;
    z-index: 2089;
  }

  &.active-focus {
    color: #42526e;
    background-color: #fff !important;
    border-color: #4c9aff !important;
  }
}
</style>
