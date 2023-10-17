<template>
  <wk-tags-view
    class="wk-user-dialog-select"
    :selected="selected"
    :visible="visible"
    :disabled="disabled"
    :placeholder="placeholder"
    :collapse-tags="collapseTags"
    value-key="realname"
    @remove-tag="removeTag"
    @click.native="click">
    <wk-dep-user-dialog
      :radio="radio"
      :disabled="disabled"
      :user-value.sync="dataValue"
      :visible.sync="visible"
      :props="{
        showDept: false,
        showDisableUser: config.showDisableUser,
        showUser: !config.disableUserList,
        disableUserList: config.disableUserList,
        disableUserLabel: config.disableUserLabel
      }"
      @click.native.stop
      @change="depUserDialogChange"
    />
  </wk-tags-view>
</template>

<script>
import WkDepUserDialog from './Dialog'
import WkTagsView from '@/components/NewCom/WkTagsView'

import { valueEquals } from 'element-ui/lib/utils/util'
import { objDeepCopy } from '@/utils'
import { mapGetters } from 'vuex'
import { isEmpty } from '@/utils/types'
import merge from '@/utils/merge'
import Emitter from 'element-ui/lib/mixins/emitter'

export default {
  // WkUserDialogSelect
  name: 'WkUserDialogSelect',

  components: {
    WkDepUserDialog,
    WkTagsView
  },

  mixins: [Emitter],

  props: {
    radio: Boolean,
    placeholder: {
      type: String,
      default() {
        return '请选择'
      }
    },
    collapseTags: Boolean,
    // 取值字段 如果继续新增 改为 props 方案
    props: {
      type: Object,
      default: () => {
        return {

        }
      }
    },
    // eslint-disable-next-line vue/require-prop-types
    value: {
      required: true
    },
    // 自定义请求和 参数
    // request: Function,
    // params: Object,
    // options: Array,
    disabled: {
      type: Boolean,
      default: false
    }
  },

  data() {
    return {
      visible: false,

      // 当前选择值
      dataValue: []
    }
  },

  computed: {
    ...mapGetters(['userDeptMap']),
    selected() {
      return this.dataValue.map(userId => {
        const data = this.userDeptMap[`user-${userId}`]
        if (data) {
          return {
            ...data,
            realname: this.$getUserName(data)
          }
        }
        return {
          userId: userId,
          realname: ''
        }
      })
    },

    // 合并 props
    config() {
      return merge({
        value: 'userId',
        label: 'realname',
        showDisableUser: false,
        disableUserList: null, // 用于单列固定数据的展示 会使 showUser false
        disableUserLabel: '停用员工'
      }, this.props || {})
    }
  },

  watch: {
    value() {
      if (!valueEquals(this.value, this.dataValue)) {
        this.verifyValue()
      }
    },

    /**
     * 更新值
     */
    dataValue() {
      if (this.radio) {
        this.$emit('input', this.dataValue && this.dataValue.length ? this.dataValue[0] : '')
      } else {
        this.$emit('input', this.dataValue)
      }
    }
  },

  created() {
    if (!valueEquals(this.value, this.dataValue)) {
      this.verifyValue()
    }
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
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
          if (!isEmpty(this.value)) {
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
     * 删除
     */
    removeTag(index) {
      this.dataValue.splice(index, 1)

      // 确定 和 移除  触发 change
      this.depUserDialogChange()
    },

    /**
     * 弹框确定
     */
    depUserDialogChange() {
      this.$nextTick(() => {
        if (this.radio) {
          this.dispatch('ElFormItem', 'el.form.change', this.dataValue && this.dataValue.length > 0 ? this.dataValue[0] : '')
        } else {
          this.dispatch('ElFormItem', 'el.form.change', this.dataValue)
        }
        this.$emit('change', this.dataValue, this.selected)
      })
    },

    /**
     * 点击
     */
    click() {
      if (this.disabled) return
      this.visible = true
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
