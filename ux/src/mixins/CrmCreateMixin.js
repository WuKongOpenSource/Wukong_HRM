import merge from '@/utils/merge'

const DefaultCRMCreate = {
  title: '',
  showConfirm: true,
  showCancel: true,
  getFields: null
}

export default {
  props: {
    props: Object
  },
  data() {
    return {
      config: DefaultCRMCreate
    }
  },
  watch: {
    props: {
      handler() {
        this.config = merge({ ...DefaultCRMCreate }, this.props || {})
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    /**
     * @description: 获取自定义提交参数
     * @return {*}
     */
    getCustomFieldPostParams() {
      return new Promise((resolve, reject) => {
        const crmForm = this.$refs.crmForm
        crmForm.validate(valid => {
          if (valid) {
            const params = this.getSubmiteParams(this.baseFields, this.fieldForm)
            // 如果外层有 product，说明有类似 合同 商机 关联产品， 将关键信息 拼入 entity
            if (params.hasOwnProperty('product')) {
              params.entity.product = {
                discountRate: params.entity.discountRate,
                totalPrice: params.entity.totalPrice,
                product: params.product
              }
            }
            resolve(params)
          } else {
            // 提示第一个error
            this.getFormErrorMessage(crmForm)
            reject()
            return false
          }
        })
      })
    },

    /**
     * @description: 一维数组转二维数据
     * @param {*} fields 一维数组
     * @return {*}
     */
    convertToTwoFields(fields) {
      const twoFields = []
      let childFields = []

      fields.forEach((element, elementIndex) => {
        if (!element.stylePercent) element.stylePercent = 50
        if (childFields.length > 0) {
          const linePercent = childFields.reduce((accumulator, current) => {
            return accumulator + current.stylePercent
          }, 0)
          if (linePercent + element.stylePercent > 100) {
            twoFields.push(childFields)
            childFields = []
            childFields.push(element)
          } else {
            childFields.push(element)
          }
          if (fields.length - 1 === elementIndex) {
            twoFields.push(childFields)
          }
        } else {
          childFields.push(element)
          if (fields.length - 1 === elementIndex) {
            twoFields.push(childFields)
          }
        }
      })

      return twoFields
    }
  }
}
