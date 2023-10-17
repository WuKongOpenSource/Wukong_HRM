<template>
  <div class="tinymce" :class="{ 'is-print':isPrint }">
    <editor
      v-if="active"
      :id="id"
      ref="mceEditor"
      v-model="dataValue"
      v-bind="$attrs"
      :init="showInit"
      :plugins="showPlugins"
      :toolbar="showToolbar"
      tinymce-script-src="https://www.72crm.com/npm/tinymce/tinymce.min.js"
      v-on="$listeners"
      @onInit="onInit"
    />

    <print-size-dialog
      v-if="printSizeDialogVisible"
      :default-data="printSizeData"
      :visible.sync="printSizeDialogVisible"
      @confirm="sizeConfirm"
    />
  </div>
</template>

<script>
import { webFileSaveAPI } from '@/api/common'

import Editor from '@tinymce/tinymce-vue'
import PrintSizeDialog from './PrintSizeDialog'

import plugins from './plugins'
import toolbar from './toolbar'
import watermark from './watermark'

let unique = 0
const cmToPx = 96 / 2.54

export default {
  name: 'Tinymce',
  components: {
    Editor,
    PrintSizeDialog
  },
  inheritAttrs: false,
  props: {
    /**
     *  apiKey: String,
        cloudChannel: String,
        id: String,
        init: Object,
        initialValue: String,
        inline: Boolean,
        modelEvents: [String, Array],
        plugins: [String, Array],
        tagName: String,
        toolbar: [String, Array],
        value: String,
        disabled: Boolean,
        tinymceScriptSrc: String,
        outputFormat: {
          type: String,
          validator: (prop: string) => prop === 'html' || prop === 'text'
        }
     */
    value: String,
    init: Object,
    height: [String, Number],
    plugins: [String, Array],
    toolbar: [String, Array],
    menubar: [String, Array],
    isPrint: Boolean,
    printData: {
      type: Object,
      default: () => {
        return {
          top: 25.4, // 常规边距
          bottom: 25.4,
          left: 31.8,
          right: 31.8,
          width: 210.0, // A4 尺寸
          height: 297.0,
          header: '',
          footer: ''
        }
      }
    } // 包含 尺寸和页眉页脚信息
  },
  data() {
    return {
      // https://github.com/tinymce/tinymce-vue/issues/48
      active: false,
      id: this.uuid(),
      dataValue: '',
      languageTypeList: {
        'en': 'en',
        'zh': 'zh_CN',
        'es': 'es_MX',
        'ja': 'ja'
      },
      showInit: {},
      // 打印尺寸
      printSizeData: null,
      printSizeDialogVisible: false,
      oldIframeHeight: 0
    }
  },
  computed: {
    editor() {
      return window.tinymce.get(this.id)
    },

    showToolbar() {
      return this.toolbar != undefined ? this.toolbar : toolbar
    },

    showPlugins() {
      return this.plugins != undefined ? this.plugins : plugins
    }
  },
  watch: {
    value: {
      handler(val) {
        if (val != this.dataValue) {
          this.dataValue = val
        }
      },
      immediate: true,
      deep: true
    },
    dataValue() {
      this.$emit('input', this.dataValue)
    },
    init: {
      handler() {
        this.initTinymce()
      },
      immediate: true,
      deep: true
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.active = true
    })
  },
  methods: {
    initTinymce() {
      const initDefault = {
        skin: 'wukong',
        resize: false,
        branding: false,
        language: this.languageTypeList['zh'],
        menubar: this.menubar ? this.menubar : false,
        body_class: 'panel-body',
        image_advtab: true,
        end_container_on_empty_block: true,
        powerpaste_word_import: 'clean',
        content_css: ['./static/tinymce/css/tiny-wukong.css'],
        fontsize_formats: '12px 14px 16px 18px 24px 36px 48px 56px 72px',
        font_formats: 'Andale Mono=andale mono,times;Arial=arial,helvetica,sans-serif;Arial Black=arial black,avant garde;Book Antiqua=book antiqua,palatino;Comic Sans MS=comic sans ms,sans-serif;Courier New=courier new,courier;Georgia=georgia,palatino;Helvetica=helvetica;Impact=impact,chicago;Symbol=symbol;Tahoma=tahoma,arial,helvetica,sans-serif;Terminal=terminal,monaco;Times New Roman=times new roman,times;Trebuchet MS=trebuchet ms,geneva;Verdana=verdana,geneva;Webdings=webdings;Wingdings=wingdings,zapf dingbats;微软雅黑=Microsoft YaHei,Helvetica Neue,PingFang SC,sans-serif;苹果苹方=PingFang SC,Microsoft YaHei,sans-serif;宋体=simsun,serif',
        code_dialog_height: 450,
        code_dialog_width: 1000,
        advlist_bullet_styles: 'square',
        advlist_number_styles: 'default',
        imagetools_cors_hosts: ['72crm.com', '5kcrm.com'],
        imagetools_proxy: 'https://www.72crm.com/file.php',
        default_link_target: '_blank',
        link_assume_external_targets: true,
        link_title: false,
        target_list: false,
        quickbars_insert_toolbar: false,
        nonbreaking_force_tab: true,
        convert_urls: false,
        toolbar_mode: this.isPrint ? 'wrap' : 'sliding',
        paste_data_images: true,
        images_upload_handler: function(blobInfo, success, failure, progress) {
          progress(0)
          webFileSaveAPI({
            file: blobInfo.blob()
          }).then(res => {
            success(res.data.url)
            progress(100)
          }).catch(() => {
            failure('出现未知问题，刷新页面，或者联系管理员')
          })
        },
        watermark_click: () => {
          this.addWaterMark()
        },
        printsize_click: () => {
          this.printSizeDialogVisible = true
        },
        pageheader_click: () => {
          if (!this.dataValue.includes('wkPrintHd')) {
            const header = `<header id="wkPrintHd" class='wk-page-hdft page-hd-wrapper' contenteditable='false'>
            <div id="wkPrintHdContent" class='page-hdft-content' contenteditable='true'>在此输入内容</div>
            <div class='page-hd-line'>
              <div class='left-line'></div>
              <div class='right-line'></div>
            </div><span class='page-hdft-close'>关闭</span>
          </header>`
            this.dataValue = header + this.dataValue

            setTimeout(() => {
              this.addHeaderEvent()
            }, 500)
          }
        },
        pagefooter_click: () => {
          if (!this.dataValue.includes('wkPrintFt')) {
            const footer = `<footer id="wkPrintFt" class="wk-page-hdft page-ft-wrapper" contenteditable="false"><div id="wkPrintFtContent" class="page-hdft-content" contenteditable="true">在此输入内容</div><span class="page-hdft-close">关闭</span></footer>`
            this.dataValue = footer + this.dataValue

            setTimeout(() => {
              this.addFooterEvent()
            }, 500)
          }
        },
        ...this.init
      }

      /**
       * statusbar 隐藏底部状态栏
       */

      if (this.height != undefined) {
        initDefault.height = this.height
      }

      this.showInit = initDefault

      // 整合七牛上传
      // images_dataimg_filter(img) {
      //   setTimeout(() => {
      //     const $image = $(img);
      //     $image.removeAttr('width');
      //     $image.removeAttr('height');
      //     if ($image[0].height && $image[0].width) {
      //       $image.attr('data-wscntype', 'image');
      //       $image.attr('data-wscnh', $image[0].height);
      //       $image.attr('data-wscnw', $image[0].width);
      //       $image.addClass('wscnph');
      //     }
      //   }, 0);
      //   return img
      // },
      // images_upload_handler(blobInfo, success, failure, progress) {
      //   progress(0);
      //   const token = _this.$store.getters.token;
      //   getToken(token).then(response => {
      //     const url = response.data.qiniu_url;
      //     const formData = new FormData();
      //     formData.append('token', response.data.qiniu_token);
      //     formData.append('key', response.data.qiniu_key);
      //     formData.append('file', blobInfo.blob(), url);
      //     upload(formData).then(() => {
      //       success(url);
      //       progress(100);
      //     })
      //   }).catch(err => {
      //     failure('出现未知问题，刷新页面，或者联系程序员')
      //     console.log(err);
      //   });
      // },
    },
    uuid() {
      const time = Date.now()
      const random = Math.floor(Math.random() * 1000000000)

      unique++

      return 'wukong_' + random + unique + String(time)
    },

    /**
     * @description: 增加水印
     * @return {*}
     */
    addWaterMark() {
      this.$prompt('请输入水印文字', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        closeOnClickModal: false,
        inputPattern: /^.{1,20}$/
      }).then(({ value }) => {
        const iframeWindow = this.editor.contentWindow
        watermark.init({ watermark_txt: value,
          window: iframeWindow,
          document: iframeWindow.document })

        this.updateEditorContent()
      }).catch(() => {})
    },

    /**
     * @description: 更新编辑器值
     * @return {*}
     */
    updateEditorContent() {
      // 插入完成 触发input 事件
      this.$nextTick(() => {
        this.$emit('input', this.editor.getContent({ format: this.$refs.mceEditor.$props.outputFormat }))
      })
    },

    imageSuccessCBK(arr) {
      arr.forEach(v => {
        this.editor.insertContent(`<img class="wscnph" src="${v.url}" >`)
      })
    },

    /**
     * @description: 尺寸修改
     * @return {*}
     */
    sizeConfirm(styleData) {
      const iframe = this.editor.iframeElement
      if (iframe) {
        this.printSizeData = styleData

        const styleObj = {
          paddingTop: `${styleData.top / 10}cm`,
          paddingBottom: `${styleData.bottom / 10}cm`,
          paddingLeft: `${styleData.left / 10}cm`,
          paddingRight: `${styleData.right / 10}cm`,
          width: `${styleData.width / 10}cm`,
          height: `${this.editor.getBody().scrollHeight + ((styleData.top + styleData.bottom) / 10 * cmToPx) * 2 + 8}px`,
          minHeight: `${styleData.height / 10}cm`
        }

        // eslint-disable-next-line no-unused-vars
        for (const key in styleObj) {
          iframe.style[key] = styleObj[key]
        }

        // 更新body最小高度
        const body = this.editor.getBody()
        body.style.minHeight = `${(styleData.height - styleData.top - styleData.bottom) / 10}cm`

        // 更新 printData
        // eslint-disable-next-line no-unused-vars
        for (const key in styleData) {
          this.printData[key] = styleData[key]
        }

        // 更新iframe高度
        this.updateIframeHeight()
      }
    },

    /**
     * @description: 初始化完成 https://www.tiny.cloud/docs/advanced/events/
     * @return {*}
     */
    onInit() {
      if (this.isPrint) {
        this.editor.on('NodeChange SetContent keyup input FullscreenStateChanged ResizeContent', (e) => {
          this.updateIframeHeight()

          this.addHeaderEvent()
          this.addFooterEvent()
        })
      }
    },

    /**
     * @description: 增加头部事件
     * @param {*} body
     * @return {*}
     */
    addHeaderEvent() {
      const headerDom = this.editor.dom.get('wkPrintHd')
      const body = this.editor.getBody()
      const paddingTop = 64
      if (headerDom && body.style.paddingTop !== `${paddingTop}px`) {
        body.style.paddingTop = `${paddingTop}px`

        const closeBtns = this.editor.dom.select('.page-hd-wrapper .page-hdft-close')
        const closeBtn = closeBtns && closeBtns.length > 0 ? closeBtns[0] : null
        if (closeBtn) {
          this.editor.dom.bind(closeBtn, 'click', () => {
            const body = this.editor.getBody()
            headerDom.remove()
            this.editor.dom.remove('wkPrintHd')
            body.style.paddingTop = 0

            this.updateEditorContent()
            this.updateIframeHeight()
          })
        }

        this.updateEditorContent()
        this.updateIframeHeight()
      }
    },

    /**
     * @description: 增加尾部事件
     * @param {*} body
     * @return {*}
     */
    addFooterEvent() {
      const footerDom = this.editor.dom.get('wkPrintFt')
      const body = this.editor.getBody()
      const paddingBottom = 45
      if (footerDom && body.style.paddingBottom !== `${paddingBottom}px`) {
        body.style.paddingBottom = `${paddingBottom}px`

        const closeBtns = this.editor.dom.select('.page-ft-wrapper .page-hdft-close')
        const closeBtn = closeBtns && closeBtns.length > 0 ? closeBtns[0] : null
        if (closeBtn) {
          closeBtn.onclick = () => {
            const body = this.editor.getBody()
            footerDom.remove()
            this.editor.dom.remove('wkPrintFt')
            body.style.paddingBottom = 0

            this.updateEditorContent()
            this.updateIframeHeight()
          }
        }

        this.updateEditorContent()
        this.updateIframeHeight()
      }
    },

    /**
     * @description: 更新iframe高度
     * @return {*}
     */
    updateIframeHeight() {
      const body = this.editor.getBody()
      const bodyHeight = body.scrollHeight
      // 增加 防止出现滚动条
      var isSafari = /^((?!chrome|android).)*safari/i.test(navigator.userAgent)
      const otherHeight = isSafari ? 50 : 20
      const iframeHeight = bodyHeight + (this.printData.top + this.printData.bottom) / 10 * cmToPx + otherHeight
      if (this.oldIframeHeight !== iframeHeight) {
        this.oldIframeHeight = iframeHeight
        this.editor.iframeElement.style.height = `${iframeHeight}px`
      }
    }
  }
}
</script>

<style lang="scss">
.tinymce-container {
  position: relative;
  line-height: normal;
}

.tinymce-container >>> .mce-fullscreen {
  z-index: 10000;
}

.tinymce.is-print {
  .tox .tox-edit-area {
    justify-content: center;
    overflow-y: auto;
    background: rgb(237, 237, 237);
  }

  // .tox .tox-edit-area__iframe {
  //   background: rgb(237, 237, 237);
  // }

  .tox .tox-edit-area__iframe {
    width: 21cm;
    min-height: 29.7cm;
    padding: 96px 3.18cm;
    margin-top: 32px;
    background: #fff;
  }
}

.tinymce-textarea {
  z-index: -1;
  visibility: hidden;
}

.tox-tinymce-aux {
  z-index: 10001 !important;
}
</style>
