export default class Field {
  constructor(obj) {
    this.fieldType = 0 // 新增字段默认加入0 1是系统字段 2 客户行业 级别 来源 等 3 特殊
    this.fieldId = obj.fieldId || '' //  字段id  1
    this.name = obj.name || '' //  标识名  1
    this.formType = obj.formType || '' // 字段类型  1
    this.isUnique = obj.isUnique || 0 // 是否唯一
    this.isNull = obj.isNull || 0 // 是否必填
    this.isHidden = obj.isHidden || 0 // 是否隐藏字段
    this.inputTips = obj.inputTips || '' // 输入提示
    if (this.formType === 'textarea') {
      this.maxLength = obj.maxLength || 800 // textarea 多行文本有最大数量
    }

    if (['user', 'structure'].includes(this.formType)) { // 人员和部门限制选择方式
      this.isMulti = obj.isMulti || 1
    }

    if (this.formType === 'checkbox') {
      this.defaultValue = obj.defaultValue || []
    } else {
      this.defaultValue = obj.defaultValue || ''
    }

    if (this.formType === 'pic') {
      // 默认1
      this.maxNumRestrict = obj.maxNumRestrict || 1
    } else {
      this.maxNumRestrict = null
    }

    // 表格的特殊处理
    if (this.formType === 'form') {
      this.formValue = obj.formValue || [] // 内部布局
    }

    this.setting = obj.setting || [] // 单选选项
    // this.showSetting = obj.showSetting || [] // 单选选项
    // this.componentName = '' // 组件名字
    this.isDeleted = 0 // 是删除标示这个字段是无效的 1是无效的
  }

  // 校验数据
  check() {
    if (this.name === '') {
      return '字段名称不能为空'
    }
    return ''
  }
}
