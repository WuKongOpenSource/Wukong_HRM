<template>
  <div
    ref="reference"
    v-elclickoutside="handleClose"
    :class="[disabled ? 'is_disabled' : 'is_valid', { 'is_focus': visible }]"
    wrap="wrap"
    class="wk-dep-select xh-form-border"
    @click="containerClick">

    <div class="el-select__tags">
      <span
        v-for="(item, index) in showSelects"
        :key="index"
        class="user-item text-one-line">{{ $getDeptName(item) }}
        <i
          class="delete-icon el-icon-close"
          @click.stop="deleteDep(index)" />
      </span>
    </div>
    <i v-if="selects.length > max" class="el-icon-more" />
    <i
      :class="['el-icon-arrow-up']" />
    <div
      v-if="selects.length == 0"
      class="user-placeholder text-one-line">{{ placeholder }}</div>

    <transition
      name="el-zoom-in-top"
      @before-enter="handleMenuEnter"
      @after-leave="doDestroy">
      <wk-select-dropdown
        v-show="visible && !disabled"
        ref="popper"
        :append-to-body="popperAppendToBody">
        <el-scrollbar
          ref="scrollbar"
          tag="div">
          <wk-dep
            v-if="initView"
            v-model="dataValue"
            v-loading="loading"
            :disabled="disabled"
            :options="options"
            :props="config"
            :radio="radio"
            @change="wkDepChange"
            @close="visible = false" />
        </el-scrollbar>
      </wk-select-dropdown>
    </transition>
  </div>
</template>

<script>
import WkSelectDropdown from './src/SelectDropdown.vue'
import WkDep from './src/WkDep.vue'

import Emitter from 'element-ui/lib/mixins/emitter'
import { valueEquals } from 'element-ui/lib/utils/util'
import { objDeepCopy } from '@/utils'
import merge from '@/utils/merge'
import { mapGetters } from 'vuex'

const DefaultWkDepSelect = {
  value: 'deptId',
  label: 'name',
  // 主列表请求和参数 会和 props 内的合并（如果有值的情况下）
  request: null,
  params: null,
  dataType: 'manage', // 部门的 value label 一致，用 dataType 区分
  isList: false // 默认是树形接口，如果是列需设置为true
}

export default {
  name: 'WkDepSelect',

  components: {
    WkSelectDropdown,
    WkDep
  },

  mixins: [Emitter],

  props: {
    radio: Boolean,
    // 展示限制
    max: {
      type: Number,
      default: 2
    },
    props: {
      type: Object,
      default: () => {
        return {
          value: 'deptId',
          label: 'name'
        }
      }
    },
    placeholder: {
      type: String,
      default() {
        return '请选择'
      }
    },
    // eslint-disable-next-line vue/require-prop-types
    value: {
      required: true
    },
    // 自定义请求和 参数
    request: Function,
    params: Object,
    options: Array,
    disabled: {
      type: Boolean,
      default: false
    },
    popperAppendToBody: {
      type: Boolean,
      default: true
    }
  },

  data() {
    return {
      visible: false,
      dataValue: [], // 校准传入值
      loading: false,

      initView: false
    }
  },

  computed: {
    ...mapGetters(['deptList', 'hrmDeptList']),
    optionsList() {
      if (this.config.dataType === 'manage') {
        // 以缓存中的全部数据为id转对象的源
        return this.deptList
      } else if (this.config.dataType === 'hrm') {
        return this.hrmDeptList
      }
      return []
    },
    // 实际展示的数据
    showSelects() {
      if (this.selects && this.selects.length > this.max) {
        return this.selects.slice(0, this.max)
      }
      return this.selects
    },

    // 选择的数据
    selects() {
      if (!this.optionsList.length) {
        return []
      }

      return this.getSelectList()
    },

    // 合并 props
    config() {
      const props = merge({ ...DefaultWkDepSelect }, this.props || {})
      if (this.request) {
        props.request = this.request
      }

      if (this.params) {
        props.params = this.params
      }
      return props
    }
  },

  watch: {
    visible(val) {
      if (!val) {
        this.broadcast('WkSelectDropdown', 'destroyPopper')
      } else {
        this.broadcast('WkSelectDropdown', 'updatePopper')
      }
      this.$emit('visible-change', val)
    },

    value() {
      this.verifyValue()
    },

    props: {
      handler() {
        this.requestDepList()
      },
      immediate: true
    },

    /**
     * 更新值
     */
    dataValue(newValue, oldValue) {
      if (this.radio) {
        this.$emit('input', this.dataValue && this.dataValue.length ? this.dataValue[0] : '')
      } else {
        this.$emit('input', this.dataValue)
      }
    }
  },

  created() {
    this.verifyValue()
  },

  methods: {

    /**
     * 获取展示的数组
     */
    getSelectList() {
      const list = []
      this.recursionOptions(this.optionsList, this.dataValue, list)
      return list
    },

    recursionOptions(options, selects, list) {
      options.forEach(item => {
        if (selects.includes(item[this.config.value])) {
          list.push(item)
        }

        if (item.children) {
          this.recursionOptions(item.children, selects, list)
        }
      })
    },

    /**
     * 处理值逻辑
     */
    verifyValue() {
      // 多选的默认值 校准为数组  单选校准为number
      if (!this.radio && !Array.isArray(this.value)) {
        this.$emit('input', [])
      }

      if (this.radio && (Array.isArray(this.value) || this.value === null || this.value === undefined)) {
        this.$emit('input', '')
      }

      if (this.radio) {
        if (this.value !== this.dataValue) {
          if (this.value) {
            this.dataValue = [this.value]
          } else {
            this.dataValue = []
          }
        }
      } else {
        if (!valueEquals(this.value, this.dataValue)) {
          if (this.value && this.value.length) {
            const firstItem = this.value[0]
            if (firstItem[this.config.value]) {
              this.dataValue = this.value.map(item => item[this.config.value])
            } else {
              this.dataValue = objDeepCopy(this.value)
            }
          } else {
            this.dataValue = []
          }
        }
      }
    },

    /**
     * 获取请求
     */
    requestDepList() {
      if (this.config.dataType === 'manage') {
        // 以缓存中的全部数据为id转对象的源
        this.$store.dispatch('debounceGetDeptList')
      } else if (this.config.dataType === 'hrm') {
        this.$store.dispatch('debounceGetHrmDeptList')
      }
    },

    handleClose() {
      this.visible = false
    },

    handleMenuEnter() {
      // 处理聚焦到具体位置
    },

    doDestroy() {
      this.$refs.popper && this.$refs.popper.doDestroy()
    },

    /**
     * 删除
     */
    deleteDep(index) {
      if (!this.disabled) {
        // 直接 splice ,dataValue watch 的老旧值相同，复制之后新旧值不相同。暂时解决
        const dataValue = objDeepCopy(this.dataValue)
        dataValue.splice(index, 1)
        this.dataValue = dataValue
        this.wkDepChange()
      }
    },

    /**
     * 聚焦动作
     */
    focusClick() {
      this.$emit('focus')
    },

    /**
     * chang 事件
     */
    wkDepChange() {
      this.$nextTick(() => {
        if (this.radio) {
          const dataValue = this.dataValue && this.dataValue.length ? this.dataValue[0] : ''
          this.$emit('input', dataValue)
          this.dispatch('ElFormItem', 'el.form.change', dataValue)
        } else {
          this.$emit('input', this.dataValue)
          this.dispatch('ElFormItem', 'el.form.change', this.dataValue)
        }
        this.$emit('change', this.dataValue, this.selects)
      })
    },

    containerClick() {
      if (!this.disabled) {
        this.initView = true
        this.visible = !this.visible
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.wk-dep-select {
  position: relative;
  width: 180px;
  height: $--input-height;
  padding: 0 20px 0 5px;
  font-size: $--font-size-base;
  color: $--form-field-default-text-color;
  cursor: pointer;
  background-color: $--input-background-color;
  border: $--border-medium;
  border-radius: $--border-radius-base;

  .user-item {
    position: relative;
    max-width: 80px;
    padding: 2px 15px 2px 5px;
    margin: 3px;
    background-color: #f3f7ff;
    border-radius: $--border-radius-base;
  }

  .user-placeholder {
    font-weight: $--font-weight-primary;
    line-height: $--input-height;
    color: $--input-placeholder-color;
    cursor: pointer;
    user-select: none;
  }

  .delete-icon {
    position: absolute;
    top: 6px;
    right: 2px;
    color: $--color-text-secondary;
    cursor: pointer;

    &:hover {
      color: $--color-primary;
    }
  }

  &:hover {
    color: $--form-field-hover-text-color;
    background-color: $--form-field-hover-bg-color;
    border-color: $--form-field-hover-border-color;
  }
}

.wk-dep-select.is_disabled {
  cursor: not-allowed;
  background-color: #f5f7fa;
  border-color: #e4e7ed;

  .user-item {
    color: #c0c4cc;
    cursor: not-allowed;
    background-color: #f0f4f8ea;
  }

  .delete-icon {
    color: #c0c4cc;
    cursor: not-allowed;
  }

  .user-placeholder {
    color: #c0c4cc;
    cursor: not-allowed;
  }
}

.wk-dep-select.is_valid:hover {
  border-color: #c0c4cc;
}

.wk-dep-select.is_focus {
  border-color: $--color-primary !important;
}

.el-icon-more {
  position: absolute;
  top: 2px;
  right: 20px;
  padding: 6px 10px;
  font-size: 12px;
  color: $--color-text-regular;
  background-color: #f3f7ff;
  border-radius: $--border-radius-base;

  &:hover {
    color: white;
    background-color: $--color-primary;
  }
}

.el-icon-arrow-up {
  position: absolute;
  top: calc(50% - 7px);
  right: 5px;
  font-weight: bold;
  color: $--input-icon-color;
  cursor: pointer;
  transition: transform 0.3s;
  transform: rotate(180deg);
}

.el-icon-arrow-up.is-reverse {
  transform: rotate(0deg);
}
</style>
