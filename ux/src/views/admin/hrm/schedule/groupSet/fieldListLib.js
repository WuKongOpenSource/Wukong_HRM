export default [
  [
    {
      name: '考勤组名称',
      formType: 'text',
      fieldName: 'name',
      placeholder: '请输入',
      isNull: 1,
      isUnique: 1,
      stylePercent: 50,
      maxlength: 50
    }, {
      tipType: 'tooltip',
      inputTips: '用于折算迟到、早退、旷工时长，方便计算应出勤天数',
      name: '考勤标注时长',
      formType: 'number',
      fieldName: 'dailyTime',
      placeholder: '请输入',
      isNull: 1,
      precisions: 2,
      maxNumRestrict: 24,
      minNumRestrict: 0.01,
      stylePercent: 50
    }
  ],
  [
    {
      name: '考勤使用范围',
      formType: 'structure_employee',
      fieldName: 'structure_employee',
      placeholder: '请输入',
      isNull: 1,
      stylePercent: 50,
      defaultValue: {}
    }
  ],
  [
    {
      name: '选择考勤打卡方式',
      formType: 'punch_card',
      fieldName: 'punch_card',
      isNull: 1,
      stylePercent: 100,
      defaultValue: {}
    }
  ],
  [
    {
      name: '扣款规则',
      formType: 'select',
      fieldName: 'attendanceRuleId',
      isNull: 1,
      stylePercent: 50,
      setting: [
      ]
    }
  ]
]
