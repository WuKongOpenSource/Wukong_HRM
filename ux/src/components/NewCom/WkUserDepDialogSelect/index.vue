<template>
  <wk-tags-view
    class="wk-user-dep-dialog-select"
    :selected="selected"
    :visible="visible"
    :disabled="disabled"
    :placeholder="placeholder"
    :collapse-tags="collapseTags"
    :value-key="['nickname', 'realname', 'name']"
    @remove-tag="removeTag"
    @click.native="click">
    <wk-dep-user-dialog
      ref="depUserDialog"
      v-bind="$attrs"
      :user-value="selectUserValue"
      :dep-value="selectDepValue"
      :sub-dept-contain="selectSubDeptContain"
      :props="props"
      :visible.sync="visible"
      :radio="radio"
      :disabled="disabled"
      @change="depUserDialogChange"
    />
  </wk-tags-view>
</template>

<script>
import WkDepUserDialog from '../WkUserDialogSelect/Dialog'
import WkTagsView from '@/components/NewCom/WkTagsView'

import { mapGetters } from 'vuex'
import merge from '@/utils/merge'
import Emitter from 'element-ui/lib/mixins/emitter'
import { objDeepCopy } from '@/utils'

const WkDeptDialogSelect = {
  value: 'deptId',
  label: 'name'
}

export default {
  // WkUserDepDialogSelect
  name: 'WkUserDepDialogSelect',

  components: {
    WkDepUserDialog,
    WkTagsView
  },

  mixins: [Emitter],

  props: {
    radio: Boolean,
    userValue: {
      type: Array,
      default: () => {
        return []
      }
    },
    depValue: {
      type: Array,
      default: () => {
        return []
      }
    },
    subDeptContain: [Number, Boolean],
    placeholder: {
      type: String,
      default() {
        return '请选择'
      }
    },
    collapseTags: Boolean,
    disabled: {
      type: Boolean,
      default: false
    },
    // 控制项
    props: {
      type: Object,
      default: () => {
        return {
          showDisableUser: false
        }
      }
    }
  },

  data() {
    return {
      visible: false,
      selectUserValue: [],
      selectDepValue: [],
      selectSubDeptContain: false,

      // 当前选择值
      dataValue: []
    }
  },

  computed: {
    ...mapGetters(['userDeptMap']),
    // 与id数组对应的，对象数组
    userSelectData() {
      const userSelectData = []
      this.selectUserValue.forEach(userId => {
        const data = this.userDeptMap[`user-${userId}`]
        if (data) {
          userSelectData.push(data)
        }
      })
      return userSelectData
    },

    deptSelectData() {
      const deptSelectData = []
      this.selectDepValue.forEach(deptId => {
        const data = this.userDeptMap[`dept-${deptId}`]
        if (data) {
          deptSelectData.push(data)
        }
      })
      return deptSelectData
    },

    // 展示的值
    selected() {
      return this.userSelectData.concat(this.deptSelectData)
    },

    // 合并 props
    config() {
      return merge({ ...WkDeptDialogSelect }, this.props || {})
    }
  },

  watch: {
    userValue: {
      handler() {
        this.selectUserValue = objDeepCopy(this.userValue)
      },
      immediate: true
    },

    depValue: {
      handler() {
        this.selectDepValue = objDeepCopy(this.depValue)
      },
      immediate: true
    },

    subDeptContain: {
      handler() {
        this.selectSubDeptContain = this.subDeptContain
      },
      immediate: true
    }
  },

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 删除
     */
    removeTag(index) {
      if (index < this.selectUserValue.length) {
        this.selectUserValue.splice(index, 1)
      } else {
        this.selectDepValue.splice(index - this.selectUserValue.length, 1)
      }

      this.depUserDialogChange(this.selectUserValue, this.selectDepValue)
    },

    /**
     * 弹框确定
     */
    depUserDialogChange(currentUserValue, currentDepValue, userSelectData, deptSelectData, currentSubDeptContain) {
      this.selectUserValue = currentUserValue
      this.selectDepValue = currentDepValue
      this.selectSubDeptContain = currentSubDeptContain

      this.$emit('update:userValue', this.selectUserValue)
      this.$emit('update:depValue', this.selectDepValue)
      this.$emit('update:subDeptContain', this.selectSubDeptContain)

      this.$nextTick(() => {
        this.dispatch('ElFormItem', 'el.form.change', currentUserValue.concat(currentDepValue))
        this.$emit('change', currentUserValue, currentDepValue, this.userSelectData, this.deptSelectData, this.selectSubDeptContain)
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
