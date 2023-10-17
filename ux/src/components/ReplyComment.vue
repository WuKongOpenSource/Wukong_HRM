<template>
  <div
    v-loading="commentLoading"
    class="reply-comment">
    <template v-if="!showNoFocus">
      <el-input
        v-if="config.contentType === 'text'"
        v-model="commentsTextarea"
        :rows="config.rows || 3"
        :maxlength="2000"
        show-word-limit
        type="textarea"
        placeholder="请输入评论内容"
        @blur="blurFun" />
      <tinymce
        v-else-if="config.contentType === 'rich'"
        ref="editor"
        v-model="commentsTextarea"
        :height="200"
        :init="tinymceInit"
        @onClick="tinyClick" />
      <div class="btn-group">
        <!-- <el-popover
          v-model="showEmoji"
          placement="top"
          width="400"
          trigger="click">
          // 表情
          <i
            slot="reference"
            class="wk wk-expression smiling-img" />
        </el-popover> -->
        <div class="btn-box">
          <el-button
            type="primary"
            @click="commentSubmit">评论</el-button>
          <el-button @click="closeComment">取消</el-button>
        </div>
      </div>
    </template>

    <div
      v-else
      class="no-focus"
      @click="toggleFocus()">
      请输入评论内容
    </div>

    <!-- 模块数据选择 -->
    <module-select
      v-if="moduleSelectShow"
      ref="moduleSelect"
      :style="moduleSelectStyle"
      :search="moduleSelectSearch"
      :props="{
        showUser: true
      }"
      @close="moduleSelectShow = false"
      @change="moduleSelectChange"
    />

    <!-- 模块类型选择 -->
    <module-type-select
      v-if="moduleTypeSelectShow"
      ref="moduleTypeSelect"
      :style="moduleSelectStyle"
      :search="moduleSelectSearch"
      @close="moduleTypeSelectShow = false"
      @change="moduleTypeSelectChange"
    />
  </div>
</template>

<script>
/**
 * 评论输入框
 * event:      close 关闭输入框
 *             reply 确认输入  参数： {String} 输入框值
 *             toggle 状态切换
 * public fn:  toggleFocus 切换输入框状态
 */
// import xss from 'xss'

import Tinymce from '@/components/Tinymce'
import ModuleSelect from '@/views/oa/workLog/print/ModuleSelect'
import ModuleTypeSelect from '@/views/oa/workLog/print/ModuleTypeSelect'

import merge from '@/utils/merge'

const DefaultReplyComment = {
  contentType: 'text' // text rich 文本或者富文本
}

export default {
  name: 'ReplyComment',
  components: {
    Tinymce,
    ModuleSelect,
    ModuleTypeSelect
  },
  props: {
    props: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      commentLoading: false, // 评论loading
      blurIndex: 0, // 评论表情插入位置
      commentsTextarea: '', // 评论内容
      showEmoji: false, // emoji选择标志
      showNoFocus: true,

      // 关联模块展示
      moduleSelectShow: false,
      moduleSelectSearch: '', // 搜索
      moduleSelectStyle: {}, // 位置等
      moduleTypeSelectShow: false
    }
  },
  computed: {
    // 合并 props
    config() {
      return merge({ ...DefaultReplyComment }, this.props || {})
    },

    // 富文本配置
    tinymceInit() {
      const self = this
      return {
        statusbar: false,
        extended_valid_elements: 'span[class|title|wktag|style|contenteditable]',
        content_style: `p { margin: 5px 0;} html { padding: 8px; }
              .wk-at-tag {
                color: #005af3;
              }
              `,
        content_css: ['./static/tinymce/css/tiny-wk-word.css'],
        setup: function(editor) {
          editor.on('init', function(e) {
            editor.focus()
          })

          editor.on('keydown', function(e) {
            self.tinyKeyDown(e)
          })

          editor.on('keyup', function(e) {
            self.tinyKeyUp(e)
          })
        }
      }
    }
  },
  methods: {
    /**
     * 输入框失去焦点
     */
    blurFun(eve) {
      this.blurIndex = eve.target.selectionEnd
    },
    /**
     * emoji 表情选择
     */
    // selectEmoji(val) {
    //   if (this.commentsTextarea && this.commentsTextarea.length + val.length <= 2000 || !this.commentsTextarea) {
    //     const list = this.commentsTextarea.split('')
    //     list.splice(this.blurIndex, 0, val)
    //     this.commentsTextarea = list.join('')
    //     this.showEmoji = false
    //   }
    // },
    /**
     * 提交评论评论
     */
    commentSubmit() {
      if (!this.commentsTextarea) {
        this.$message.error('评论内容不能为空')
        return
      }

      let atList = []
      // 是富文本
      if (this.config.contentType === 'rich') {
        const editor = this.$refs.editor.editor
        const tinyDoc = editor.getDoc()
        const atElements = Array.from(tinyDoc.querySelectorAll("[data-wk-type='user']"))
        atList = atElements.map(item => {
          let innerHTML = item.innerHTML || ''
          innerHTML = innerHTML.replace(/&nbsp;/g, '') // 去除空格的影响
          return {
            userId: item.dataset.wkTypeId,
            realname: innerHTML?.startsWith('@') ? innerHTML.replace('@', '') : innerHTML
          }
        })
      }

      this.$emit('reply', this.commentsTextarea, atList)
    },
    /**
     * 关闭评论评论框
     */
    closeComment() {
      // this.showEmoji = false
      this.$emit('close')
      this.toggleFocus()
    },

    /**
     * 切换输入框状态
     */
    toggleFocus(flag) {
      if (typeof flag === 'boolean') {
        this.showNoFocus = flag
      } else {
        this.showNoFocus = !this.showNoFocus
      }
      this.$emit('toggle', this.showNoFocus)
    },

    /** ****************富文本逻辑***********************/
    /**
     * @description: 数据获取
     * @return {*}
     */
    moduleSelectChange(data, config) {
      const nodeData = this.getRemoveInputNode()

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

      const editor = this.$refs.editor.editor
      let relativeContent = ''
      allList.forEach(item => {
        relativeContent += `<span data-wk-type="${item.type}" data-wk-type-id="${item.id}" class="wk-at-tag" contenteditable="false">\xa0@${item.name}\xa0</span>`
      })
      if (relativeContent) {
        editor.insertContent(nodeData.preSlice + relativeContent + nodeData.restSlice)
      }
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
    tinyKeyDown(e) {
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
     * @description: 监听富文本按键输入
     * @param {*} event
     * @return {*}
     */
    tinyKeyUp(e) {
      const showAtInfo = this.showAt()
      this.showAtInfo = showAtInfo
      if (showAtInfo.value && showAtInfo.type === 'at') {
        const user = this.getAtUser(showAtInfo.type)
        this.moduleSelectSearch = this.replaceString(user || '', showAtInfo.type)
        if (this.moduleSelectShow) {
          this.$refs.moduleSelect.keyDownHandler(e)
        } else {
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
          this.$refs.moduleTypeSelect.keyDownHandler(e)
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
      const selection = this.$refs.editor.editor.selection.getSel()
      return selection?.focusOffset
    },

    /**
     * @description: 获取节点
     * @return {*}
     */
    getRangeNode() {
      const selection = this.$refs.editor.editor.selection.getSel()
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
      const selection = this.$refs.editor.editor.selection.getSel()
      const range = selection?.getRangeAt(0)
      const rect = range.getClientRects()[0]
      const LINE_HEIGHT = 65
      return {
        x: rect.x - 12,
        y: rect.y + LINE_HEIGHT
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
     * @description: 编辑器点击
     * @return {*}
     */
    tinyClick(event) {
      if (event.target) {
        const target = event.target
        const { dataset } = target
        if (dataset && dataset.wkType && dataset.wkTypeId) {
          this.detailId = dataset.wkTypeId
          this.detailType = dataset.wkType
          this.detailShow = true
        }
      }
      this.moduleSelectShow = false
      this.moduleTypeSelectShow = false
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
        parentNode && parentNode.removeChild(node)

        return {
          preSlice,
          restSlice
        }
      }

      return {
        preSlice: '',
        restSlice: ''
      }
    }
  }
}
</script>

<style scoped lang="scss">
.reply-comment {
  position: relative;

  // overflow: hidden;
  border: 1px solid $--border-color-base;
  border-radius: $--border-radius-base;

  &::before {
    position: absolute;
    top: 5px;
    left: -12px;
    width: 0;
    height: 0;
    content: "";
    border: 6px solid $--border-color-light;
    border-color: transparent $--border-color-light transparent transparent;
  }

  &::after {
    position: absolute;
    top: 5px;
    left: -10px;
    width: 0;
    height: 0;
    content: "";
    border: 6px solid transparent;
    border-color: transparent $--color-white transparent transparent;
  }

  ::v-deep .tox-tinymce {
    border: none;
  }

  .btn-group {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    padding: 5px 10px;
    overflow: hidden;
    background-color: white;

    .btn-box {
      display: flex;
      flex: 1;
      align-items: center;
      justify-content: flex-end;
    }

    // .smiling-img {
    //   cursor: pointer;
    //   font-size: 17px;
    //   color: $--color-text-regular;
    // }
  }

  .el-textarea ::v-deep .el-textarea__inner {
    resize: none;
    background-color: white;
    border: 0;
  }

  .no-focus {
    width: 100%;
    padding: 9px 8px;
    font-size: 13px;
    color: #c0c4cc;
    cursor: pointer;
    background-color: white;
    border-radius: $--border-radius-base;
  }

  .module-type-select-wrap,
  .module-select-wrap {
    position: absolute;
    z-index: 2;
  }
}
</style>
