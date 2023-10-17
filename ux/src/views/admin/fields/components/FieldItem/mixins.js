import { getFieldAuth } from '../../utils'
import { isObject, isEmpty } from '@/utils/types'

export default {
  props: {
    field: { // 当前字段信息
      type: Object,
      required: true
    },
    fieldArr: { // 全部字段数组，为空时则禁止点击改变位置
      type: Array,
      default: () => []
    },
    point: { // 当前字段坐标
      type: Array
    },
    activePoint: { // 选中的字段坐标
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      specialFormType: [
        // 'detail_table' // 明细表格
      ]
    }
  },
  computed: {
    // 当前字段是否激活
    activate() {
      return this.point[0] === this.activePoint[0] &&
        this.point[1] === this.activePoint[1]
    },
    /** 只读 */
    disabled() {
      return !this.fieldAuth.defaultEdit
    },

    fieldAuth() {
      return getFieldAuth(this.field.operating)
    },
    // 向上操作按钮
    topFlag() {
      if (isEmpty(this.fieldArr)) return false
      // 第一行、上一行有4个、上一行为特殊字段类型不显示
      const row = this.point[0]
      if (row === 0) return false
      const prevRow = this.fieldArr[row - 1]
      if (prevRow.length === 4) return false
      if (this.specialFormType.includes(prevRow[0].formType)) return false
      return true
    },
    // 向下操作按钮
    bottomFlag() {
      if (isEmpty(this.fieldArr)) return false
      // 最后一行、当前行只有一个不显示
      const row = this.point[0]
      if (row === this.fieldArr.length - 1) return false
      // if (this.fieldArr[row].length <= 1) return false
      return true
    },
    // 左侧操作按钮
    leftFlag() {
      if (isEmpty(this.fieldArr)) return false
      // 第一列不显示
      const column = this.point[1]
      if (column === 0) return false
      return true
    },
    // 右侧操作按钮
    rightFlag() {
      if (isEmpty(this.fieldArr)) return false
      // 最后一列不显示
      const column = this.point[1]
      const row = this.point[0]
      if (column === this.fieldArr[row].length - 1) return false
      return true
    },
    // 复制按钮
    copyFlag() {
      if (isEmpty(this.fieldArr)) return false
      return ![
        'customer',
        'business',
        'contacts',
        'contract',
        'receivables_plan',
        'single_user'
      ].includes(this.field.formType)
    },
    controlFlag() {
      return {
        top: this.topFlag,
        bottom: this.bottomFlag,
        left: this.leftFlag,
        right: this.rightFlag,
        delete: this.fieldAuth.deleteEdit,
        copy: this.copyFlag
      }
    }
  },
  // watch: {
  //   field: {
  //     handler() {
  //       this.$nextTick(() => {
  //         this.$forceUpdate()
  //       })
  //     },
  //     deep: true,
  //     immediate: true
  //   }
  // },
  methods: {
    /**
     * click
     * @param evt
     */
    emitClick(evt) {
      this.$emit('click', evt)
    },

    /**
     * 点击删除
     * @param action
     * @param evt
     */
    handleAction(action, evt) {
      this.$emit('action', action, this.point, evt)
    },

    /**
     * 是对象
     */
    itemIsObject(item) {
      return isObject(item)
    }
  }
}
