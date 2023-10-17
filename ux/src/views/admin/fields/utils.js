export function getFieldAuth(operating) {
  const binaryStr = operating.toString(2).padStart(9, '0')
  // console.log('binaryStr: ', operating, binaryStr)
  return {
    nameEdit: Boolean(Number(binaryStr.charAt(0))), // 是否可编辑字段名/提示文字
    deleteEdit: Boolean(Number(binaryStr.charAt(1))), // 是否可删除字段
    defaultEdit: Boolean(Number(binaryStr.charAt(2))), // 可编辑
    percentEdit: Boolean(Number(binaryStr.charAt(3))), // 是否允许修改字段占比
    nullEdit: Boolean(Number(binaryStr.charAt(4))), // 是否可设置为必填
    uniqueEdit: Boolean(Number(binaryStr.charAt(5))), // 是否可设置为唯一
    hiddenEdit: Boolean(Number(binaryStr.charAt(6))), // 是否可隐藏字段
    optionsEdit: Boolean(Number(binaryStr.charAt(7))), // 是否允许编辑选项
    radioEdit: Boolean(Number(binaryStr.charAt(8))) // 是否允许编辑员工部门单多选
  }
}

/**
 * @description: 根据长度校准默认权限
 * @param {*} decimalVal
 * @param {*} length
 * @return {*}
 */
export function getDefaultOperating(decimalVal, length = 8) {
  if (length == 9) {
    return decimalVal * 2 + 1 // 最后一位默认有值
  }
  return decimalVal
}

export function typeToComponent(item) {
  if (item.type === 0) return 'FieldInput'
  if ([
    'text',
    'number',
    'floatnumber',
    'mobile',
    'email',
    'date',
    'datetime',
    'user',
    'structure',
    'contacts',
    'customer',
    'contract',
    'business',
    'single_user',
    'website'
  ].includes(item.formType)) {
    return 'FieldInput'
  }
  switch (item.formType) {
    case 'textarea': // 多行文本
      return 'FieldTextarea'
    case 'select': // 选项
      return 'FieldSelect'
    case 'checkbox': // 多选
      return 'FieldCheckbox'
    case 'file': // 附件
      return 'FieldFile'
    case 'boolean_value': // 布尔
      return 'FieldBoolean'
    case 'percent': // 百分数
      return 'FieldPercent'
    case 'position': // 地区定位
      return 'FieldPosition'
    case 'location': // 定位
      return 'FieldLocation'
    case 'detail_table': // 明细表格
      return 'FieldDetailTable'
    case 'handwriting_sign': // 手写签名
      return 'FieldWritingSign'
    case 'date_interval': // 日期区间
      return 'FieldDateInterval'
    case 'desc_text': // 描述文字
      return 'FieldDescText'
    case 'pic': // 图片
      return 'FieldFile'
    case 'serial_number': // 自定义编号
      return 'FieldSerialNumber'
    case 'field_tag': // 自定义标签
      return 'FieldTag'
    case 'field_attention': // 关注度
      return 'FieldAttention'
    case 'field_group': // 分组标题
      return 'FieldGroup'
    default:
      return 'FieldInput'
  }
}
