<template>
  <div class="setting-rich-text">
    <tinymce
      ref="createTinymce"
      v-model="field.defaultValue"
      :init="getEditConfig()"
      :height="200"
      :disabled="!editAuth"
      toolbar="bold italic underline strikethrough | fontselect | forecolor backcolor | fontsizeselect | numlist bullist | alignleft aligncenter alignright | image link | removeformat"
      class="rich-txt"
      @input="debouncedEditorInput" />
  </div>
</template>

<script>
import Tinymce from '@/components/Tinymce'
import { debounce } from 'throttle-debounce'
import { getFieldAuth } from '../../utils'

export default {
  name: 'SettingRichText',
  components: {
    Tinymce
  },
  props: {
    field: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      debouncedEditorInput: null
    }
  },
  computed: {
    // 编辑权限
    editAuth() {
      return getFieldAuth(this.field.operating).defaultEdit
    }
  },
  created() {
    this.debouncedEditorInput = debounce(300, this.editInputChange)
  },
  methods: {
    getEditConfig() {
      return {
        menubar: false,
        statusbar: false,
        paste_data_images: true, // 允许粘贴图片
        paste_enable_default_filters: false,
        placeholder: '描述文字内容',
        content_style: ' * {color: #262626; margin: 0;} body { margin: 8px; font-size: 14px; }',
        paste_retain_style_properties: 'border', // 粘贴内容时要保留的样式
        toolbar_mode: 'scrolling',
        paste_preprocess: function(plugin, args) {
          // 删除部分标签
          var delTag = ['b', 'strong', 'i', 'em']
          delTag.forEach(tag => {
            var reg = new RegExp(`(<${tag}>)|(</${tag}>)]`, 'g')
            args.content = args.content.replace(reg, '')
          })
          // 替换部分标签
          var replaceTag = ['h1', 'h2', 'h3', 'h4', 'h5', 'h6']
          replaceTag.forEach(tag => {
            var reg1 = new RegExp(`<${tag}>`, 'g')
            var reg2 = new RegExp(`</${tag}>`, 'g')
            args.content = args.content.replace(reg1, '<p>')
            args.content = args.content.replace(reg2, '</p>')
          })
          // 删除所有font标签
          args.content = args.content.replace(/<\/font>/ig, '').replace(/<font[^>]+>/ig, '')
          console.log(args.content)
        },
        paste_postprocess: function(plugin, args) {
          var doms = Array.from(args.node.querySelectorAll('*'))
          // 删除字体样式
          doms.forEach(dom => {
            dom.style.color = ''
            dom.style.fontWeight = ''
            dom.style.fontFamily = ''
            dom.style.fontSize = ''
            dom.style.background = ''
            console.log(dom)
          })
        }
      }
    },
    editInputChange() {}
  }
}
</script>

<style scoped>

</style>
