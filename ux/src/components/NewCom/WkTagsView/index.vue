<template>
  <div
    class="wk-tags-view el-select"
    :class="[
      size ? 'el-select--' + size : '',
      mode ? 'el-select--' + mode : ''
    ]">
    <div
      ref="tags"
      class="el-select__tags"
      :style="{ 'max-width': inputWidth - 32 + 'px', width: '100%' }">
      <span v-if="collapseTags && selected.length">
        <el-tag
          :closable="!disabled"
          :size="collapseTagSize"
          type="info"
          disable-transitions
          @close="deleteTag($event, 0)">
          <span class="el-select__tags-text">{{ getLabelValue(selected[0]) }}</span>
        </el-tag>
        <el-tag
          v-if="selected.length > 1"
          :closable="false"
          :size="collapseTagSize"
          type="info"
          disable-transitions>
          <span class="el-select__tags-text">+ {{ selected.length - 1 }}</span>
        </el-tag>
      </span>
      <transition-group v-if="!collapseTags" @after-leave="resetInputHeight">
        <el-tag
          v-for="(item, index) in selected"
          :key="index"
          :closable="!disabled"
          :size="collapseTagSize"
          type="info"
          disable-transitions
          @close="deleteTag($event, index)">
          <span class="el-select__tags-text">{{ getLabelValue(item) }}</span>
        </el-tag>
      </transition-group>
    </div>

    <el-input
      :id="id"
      ref="reference"
      v-model="selectedLabel"
      type="text"
      :placeholder="currentPlaceholder"
      :name="name"
      :autocomplete="autocomplete"
      :size="size"
      :disabled="disabled"
      readonly
      :validate-event="false"
      :class="{ 'is-focus': visible }"
      tabindex="-1"
      @mouseenter.native="inputHovering = true"
      @mouseleave.native="inputHovering = false">
      <template v-if="$slots.prefix" slot="prefix">
        <slot name="prefix" />
      </template>
      <template slot="suffix">
        <i :class="['el-select__caret', 'el-input__icon', 'el-icon-' + iconClass]" />
      </template>
    </el-input>

    <slot />
  </div>
</template>

<script>
import { addResizeListener, removeResizeListener } from 'element-ui/src/utils/resize-event'

export default {
  // WkTagsView
  name: 'WkTagsView',

  components: {},

  props: {
    id: String,
    valueKey: { // 支持 'realname'/ ['realname', 'name']
      type: [String, Array],
      default: 'label'
    },
    name: String,
    autocomplete: {
      type: String,
      default: 'off'
    },
    collapseTags: Boolean,
    selected: {
      type: Array,
      default: () => {
        return []
      }
    },
    disabled: Boolean,
    visible: Boolean,
    size: String,
    mode: String,
    placeholder: {
      type: String,
      required: false
    }
  },

  data() {
    return {
      inputWidth: 0,
      initialInputHeight: 0,
      inputHovering: false,
      selectedLabel: ''
    }
  },

  computed: {
    collapseTagSize() {
      return ['small', 'mini'].indexOf(this.size) > -1
        ? 'mini'
        : 'small'
    },

    iconClass() {
      return (this.visible ? 'arrow-up is-reverse' : 'arrow-up')
    },

    currentPlaceholder() {
      return this.selected && this.selected.length > 0 ? '' : this.placeholder
    }
  },

  watch: {
    selected() {
      this.$nextTick(() => {
        this.resetInputHeight()
      })
    }
  },

  created() {},

  mounted() {
    addResizeListener(this.$el, this.handleResize)

    const reference = this.$refs.reference
    if (reference && reference.$el) {
      const sizeMap = {
        medium: 36,
        small: 32,
        mini: 28
      }
      const input = reference.$el.querySelector('input')
      this.initialInputHeight = input.getBoundingClientRect().height || sizeMap[this.size]
    }

    this.resetInputHeight()

    this.$nextTick(() => {
      if (reference && reference.$el) {
        this.inputWidth = reference.$el.getBoundingClientRect().width
      }
    })
  },

  beforeDestroy() {
    if (this.$el && this.handleResize) removeResizeListener(this.$el, this.handleResize)
  },

  methods: {
    resetInputWidth() {
      this.inputWidth = this.$refs.reference.$el.getBoundingClientRect().width
    },

    handleResize() {
      this.resetInputWidth()
      this.resetInputHeight()
    },

    resetInputHeight() {
      if (this.collapseTags) return
      this.$nextTick(() => {
        if (!this.$refs.reference) return
        const inputChildNodes = this.$refs.reference.$el.childNodes
        const input = [].filter.call(inputChildNodes, item => item.tagName === 'INPUT')[0]
        const tags = this.$refs.tags
        const tagsHeight = tags ? Math.round(tags.getBoundingClientRect().height) : 0
        const sizeInMap = this.initialInputHeight || 32
        input.style.height = this.selected.length === 0
          ? sizeInMap + 'px'
          : Math.max(
            tags ? (tagsHeight + (tagsHeight > sizeInMap ? 6 : 0)) : 0,
            sizeInMap
          ) + 'px'
      })
    },

    deleteTag(event, index) {
      this.$emit('remove-tag', index)
      event.stopPropagation()
    },

    /**
     * 获取展示值
     */
    getLabelValue(data) {
      if (Array.isArray(this.valueKey)) {
        const valueKey = this.valueKey.find(key => data[key])
        return valueKey ? data[valueKey] : ''
      }
      return data[this.valueKey]
    }
  }
}
</script>

<style lang="scss" scoped>
.el-select__tags {
  max-height: 110px;
  overflow-x: hidden;
  overflow-y: auto;
}
</style>
