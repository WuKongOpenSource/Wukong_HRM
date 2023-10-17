<template>
  <field-wrapper
    :activate="activate"
    :field="field"
    :control-flag="controlFlag"
    hidden-title
    class="field-desc-text"
    @click="emitClick"
    @action="handleAction">

    <tinymce
      :value="field.defaultValue"
      disabled
      :toolbar="[]"
      :init="{
        menubar: false,
        toolbar_sticky: true,
        statusbar: false,
        placeholder: '描述文字内容',
        quickbars_selection_toolbar: false,
        contextmenu: '',
        content_style: ' * {color: #262626; margin: 0;} body { font-size: 14px; }',
        plugins: 'autoresize',
        autoresize_bottom_margin: 0
      }"
      class="rich-txt" />
    <div class="field-desc-text-cover" />
  </field-wrapper>
</template>

<script>
import FieldWrapper from './FieldWrapper'
import Tinymce from '@/components/Tinymce'
import mixins from './mixins'

export default {
  name: 'FieldDescText',
  components: {
    FieldWrapper,
    Tinymce
  },
  mixins: [mixins],
  methods: {}
}
</script>

<style lang="scss">
.field-desc-text {
  .tox-tinymce {
    border: none;
  }

  .tox .tox-edit-area__iframe {
    background-color: unset;
  }
}
</style>
<style scoped lang="scss">
.field-desc-text {
  padding: 15px 10px;

  &-cover {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    z-index: 1;
    cursor: move;
  }
}

.rich-txt {
  width: 100%;
  background-color: white;
  border: 0 none;
}
</style>
