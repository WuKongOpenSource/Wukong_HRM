export default [
  [{
    isNull: 1,
    isUnique: 1,
    fieldName: 'attendanceRuleName',
    name: '规则名称',
    formType: 'text',
    stylePercent: 50
  }],
  [{
    defaultValue: 1,
    fieldName: 'lateRuleMethod',
    name: '迟到规则',
    inputTips: '扣款=迟到分钟*N元/分钟',
    formType: 'select',
    stylePercent: 50,
    setting: [
      { label: '迟到分钟扣款', value: 1 },
      { label: '迟到次数扣款', value: 2 },
      { label: '每月固定扣款', value: 3 }]
  }, {
    defaultValue: '0.00',
    isNull: 1,
    unit: '元/分',
    fieldName: 'lateDeductMoney',
    name: '计算方式',
    formType: 'computeMode',
    placeholder: '',
    stylePercent: 50
  }],
  [{
    defaultValue: 1,
    fieldName: 'earlyRuleMethod',
    name: '早退规则',
    inputTips: '扣款=早退分钟*N元/分钟',
    formType: 'select',
    stylePercent: 50,
    setting: [
      { label: '按早退分钟扣款', value: 1 },
      { label: '按早退次数扣款', value: 2 },
      { label: '每月固定扣款', value: 3 }]
  }, {
    defaultValue: '0.00',
    unit: '分/元',
    isNull: 1,
    fieldName: 'earlyDeductMoney',
    name: '计算方式',
    formType: 'computeMode',
    placeholder: '',
    stylePercent: 50
  }],
  [{
    defaultValue: 1,
    fieldName: 'absenteeismRuleMethod',
    name: '旷工规则',
    inputTips: '扣款=旷工天数*N元/天',
    formType: 'select',
    stylePercent: 50,
    setting: [{ label: '按旷工天数扣款', value: 1 }]
  }, {
    defaultValue: '0.00',
    unit: '元/天',
    isNull: 1,
    fieldName: 'absenteeismDeductMoney',
    name: '计算方式',
    formType: 'computeMode',
    placeholder: '',
    stylePercent: 50
  }],
  [{
    defaultValue: 1,
    fieldName: 'misscardRuleMethod',
    name: '缺卡规则',
    inputTips: '扣款=缺卡次数*N元/次',
    formType: 'select',
    stylePercent: 50,
    setting: [{ label: '按缺卡次数扣款', value: 1 }]
  }, {
    defaultValue: '0.00',
    unit: '元/次',
    isNull: 1,
    fieldName: 'misscardDeductMoney',
    name: '计算方式',
    formType: 'computeMode',
    placeholder: '',
    stylePercent: 50
  }],
  [{
    name: '',
    inputTips: '',
    fieldName: 'personalized',
    formType: 'personalized',
    defaultValue: 1,
    stylePercent: 100
  }]
]
