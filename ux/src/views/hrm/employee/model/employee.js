
function getValueList(data) {
  const keys = Object.keys(data)
  return keys.map(key => {
    return {
      label: data[key],
      value: parseInt(key)
    }
  })
}

// 教育字段
const educationFields = [
  {
    name: '学历',
    field: 'education',
    formType: 'select',
    setting: [{
      label: '小学',
      value: 1
    }, {
      label: '初中',
      value: 2
    }, {
      label: '中专',
      value: 3
    }, {
      label: '中职',
      value: 4
    }, {
      label: '中技',
      value: 5
    }, {
      label: '高中',
      value: 6
    }, {
      label: '大专',
      value: 7
    }, {
      label: '本科',
      value: 8
    }, {
      label: '硕士',
      value: 9
    }, {
      label: '博士',
      value: 10
    }, {
      label: '博士后',
      value: 11
    }, {
      label: '其他',
      value: 12
    }]
  },
  {
    name: '毕业院校',
    field: 'graduateSchool',
    formType: 'text',
    setting: []
  },
  {
    name: '专业',
    field: 'major',
    formType: 'text',
    setting: []
  },
  {
    name: '入学时间',
    field: 'admissionTime',
    formType: 'date',
    setting: []
  },
  {
    name: '毕业时间',
    field: 'graduationTime',
    formType: 'date',
    setting: []
  },
  {
    name: '教学方式',
    field: 'teachingMethods',
    formType: 'select',
    setting: [{
      label: '全日制',
      value: 1
    }, {
      label: '成人教育',
      value: 2
    }, {
      label: '远程教育',
      value: 3
    }, {
      label: '自学考试',
      value: 4
    }, {
      label: '其他',
      value: 5
    }]
  },
  {
    name: '是否第一学历',
    field: 'isFirstDegree',
    formType: 'radio',
    setting: [{
      label: '否',
      value: 0
    }, {
      label: '是',
      value: 1
    }]
  }
]

const educationValue = {
  1: '小学',
  2: '初中',
  3: '中专',
  4: '中职',
  5: '中技',
  6: '高中',
  7: '大专',
  8: '本科',
  9: '硕士',
  10: '博士',
  11: '博士后',
  12: '其他'
}

const teachingMethodsValue = {
  1: '全日制',
  2: '成人教育',
  3: '远程教育',
  4: '自学考试',
  5: '其他'
}

const isFirstDegreeValue = {
  1: '是',
  0: '否'
}

// 教育验证规则
const educationRules = {
  education: [
    { required: true, message: '请选择', trigger: ['blur', 'change'] }
  ],
  graduateSchool: [
    { required: true, message: '请输入', trigger: ['blur', 'change'] }
  ],
  major: [
    { required: true, message: '请输入', trigger: ['blur', 'change'] }
  ],
  isFirstDegree: [
    { required: true, message: '请选择', trigger: ['blur', 'change'] }
  ]
}

// 教育model
export const educationModel = {
  fields: educationFields,
  rules: educationRules,
  educationValue,
  teachingMethodsValue,
  isFirstDegreeValue
}

// 工作字段
const workFields = [
  {
    name: '工作单位',
    field: 'workUnit',
    formType: 'text',
    setting: []
  },
  {
    name: '职务',
    field: 'post',
    formType: 'text',
    setting: []
  },
  {
    name: '工作开始时间',
    field: 'workStartTime',
    formType: 'date',
    setting: []
  },
  {
    name: '工作结束时间',
    field: 'workEndTime',
    formType: 'date',
    setting: []
  },
  {
    name: '离职原因',
    field: 'leavingReason',
    formType: 'text',
    setting: []
  },
  {
    name: '证明人',
    field: 'witness',
    formType: 'text',
    setting: []
  },
  {
    name: '证明人电话',
    field: 'witnessPhone',
    formType: 'mobile',
    setting: []
  },
  {
    name: '工作备注',
    field: 'workRemarks',
    formType: 'text',
    setting: []
  }
]

import {
  regexIsCRMMobile
} from '@/utils'
import { isEmpty } from '@/utils/types'
const validateCRMMobile = (rule, value, callback) => {
  if (isEmpty(value) || regexIsCRMMobile(value)) {
    callback()
  } else {
    callback(new Error('手机格式有误'))
  }
}

// 工作验证规则
const workRules = {
  workUnit: [
    { required: true, message: '请输入', trigger: ['blur', 'change'] }
  ],
  post: [
    { required: true, message: '请输入', trigger: ['blur', 'change'] }
  ],
  witnessPhone: [
    {
      validator: validateCRMMobile,
      trigger: ['blur', 'change']
    }
  ]
}

// 工作model
export const workModel = {
  fields: workFields,
  rules: workRules
}

// 证书字段
const certificateFields = [
  {
    name: '证书名称',
    field: 'certificateName',
    formType: 'text',
    setting: []
  },
  {
    name: '证书级别',
    field: 'certificateLevel',
    formType: 'text',
    setting: []
  },
  {
    name: '证书/证件编号',
    field: 'certificateNum',
    formType: 'text',
    setting: []
  },
  {
    name: '有效期起始日期',
    field: 'startTime',
    formType: 'date',
    setting: []
  },
  {
    name: '有效期到期日',
    field: 'endTime',
    formType: 'date',
    setting: []
  },
  {
    name: '发证机构',
    field: 'issuingAuthority',
    formType: 'text',
    setting: []
  },
  {
    name: '发证日期',
    field: 'issuingTime',
    formType: 'date',
    setting: []
  },
  {
    name: '证件备注',
    field: 'remarks',
    formType: 'text',
    setting: []
  }
]
// 证书验证规则
const certificateRules = {
  certificateName: [
    { required: true, message: '请输入', trigger: ['blur', 'change'] }
  ]
}

// 证书model
export const certificateModel = {
  fields: certificateFields,
  rules: certificateRules
}

/** *** */
// 培训字段
const trainingFields = [
  {
    name: '培训课程',
    field: 'trainingCourse',
    formType: 'text',
    setting: []
  },
  {
    name: '培训机构名称',
    field: 'trainingOrganName',
    formType: 'text',
    setting: []
  },
  {
    name: '培训开始时间',
    field: 'startTime',
    formType: 'date',
    setting: []
  },
  {
    name: '培训结束时间',
    field: 'endTime',
    formType: 'date',
    setting: []
  },
  {
    name: '培训时长',
    field: 'trainingDuration',
    formType: 'number',
    setting: []
  },
  {
    name: '培训成绩',
    field: 'trainingResults',
    formType: 'text',
    setting: []
  },
  {
    name: '培训证书名称',
    field: 'trainingCertificateName',
    formType: 'text',
    setting: []
  },
  {
    name: '培训备注',
    field: 'remarks',
    formType: 'textarea',
    setting: []
  }
]
// 培训验证规则
const trainingRules = {
  trainingCourse: [
    { required: true, message: '请输入', trigger: ['blur', 'change'] }
  ]
}

// 培训model
export const trainingModel = {
  fields: trainingFields,
  rules: trainingRules
}

/** *** */
// 联系人
const contactsFields = [
  {
    name: '联系人姓名',
    field: 'contactsName',
    formType: 'text',
    setting: []
  },
  {
    name: '关系',
    field: 'contactsOrganName',
    formType: 'text',
    setting: []
  },
  {
    name: '联系人电话',
    field: 'startTime',
    formType: 'text',
    setting: []
  },
  {
    name: '联系人工作单位',
    field: 'endTime',
    formType: 'text',
    setting: []
  },
  {
    name: '联系人职务',
    field: 'contactsDuration',
    formType: 'text',
    setting: []
  },
  {
    name: '联系人地址',
    field: 'contactsResults',
    formType: 'text',
    setting: []
  }
]
// 联系人验证规则
const contactsRules = {
  contactsName: [
    { required: true, message: '请输入', trigger: ['blur', 'change'] }
  ]
}

// 联系人model
export const contactsModel = {
  fields: contactsFields,
  rules: contactsRules
}

/** *** */
// 员工model

// 性别 值
const sexValue = {
  1: '男',
  2: '女'
}

// 生日类型 值
const birthdayTypeValue = {
  1: '阳历',
  2: '农历'
}

const sexSetting = [{
  label: '男',
  value: 1
}, {
  label: '女',
  value: 2
}]

// 证件类型
const idTypeValue = {
  1: '身份证',
  2: '港澳通行证',
  3: '台湾通行证',
  4: '护照',
  5: '其他'
}

const idTypeSetting = [{
  label: '身份证',
  value: 1
}, {
  label: '港澳通行证',
  value: 2
}, {
  label: '台湾通行证',
  value: 3
}, {
  label: '护照',
  value: 4
}, {
  label: '其他',
  value: 5
}]

// 聘用形式
const employmentFormsValue = {
  1: '正式',
  2: '非正式'
}

const employmentFormsSetting = [{
  label: '正式',
  value: 1
}, {
  label: '非正式',
  value: 2
}]

// 试用期
const probationValue = {
  0: '无试用期',
  1: '1个月',
  2: '2个月',
  3: '3个月',
  4: '4个月',
  5: '5个月',
  6: '6个月'
}

const probationSetting = [{
  label: '无试用期',
  value: 0
}, {
  label: '1个月',
  value: 1
}, {
  label: '2个月',
  value: 2
}, {
  label: '3个月',
  value: 3
}, {
  label: '4个月',
  value: 4
}, {
  label: '5个月',
  value: 5
}, {
  label: '6个月',
  value: 6
}]

const statusSetting = [{
  label: '实习',
  value: 3
}, {
  label: '兼职',
  value: 4
}, {
  label: '劳务',
  value: 5
}, {
  label: '顾问',
  value: 6
}, {
  label: '返聘',
  value: 7
}, {
  label: '外包',
  value: 8
}]

// 非正式类型 特殊 和状态一致
const statusValue = {
  1: '正式',
  2: '试用',
  3: '实习',
  4: '兼职',
  5: '劳务',
  6: '顾问',
  7: '返聘',
  8: '外包',
  // 9: '待离职',
  // 10: '已离职',
  11: '在职',
  12: '全职',
  13: '待入职',
  14: '待离职',
  15: '已离职'
}

const validStatusValue = {
  1: '正式',
  2: '试用',
  3: '实习',
  4: '兼职',
  5: '劳务',
  6: '顾问',
  7: '返聘',
  8: '外包'
}

// 员工model
export const employeeModel = {
  sexValue,
  birthdayTypeValue,
  idTypeValue,
  employmentFormsValue,
  probationValue,
  statusValue,
  validStatusValue,

  getValueList,

  sexSetting,
  idTypeSetting,
  employmentFormsSetting,
  probationSetting,
  statusSetting
}

/** *** */
// 转正
import {
  hrmEmployeeQueryInAPI
} from '@/api/hrm/employee'
import {
  hrmDeptQueryTreeListAPI
} from '@/api/hrm/dept'
const officialFields = [
  {
    name: '部门',
    field: 'newDept',
    formType: 'structure',
    props: {
      dataType: 'hrm'
    },
    request: hrmDeptQueryTreeListAPI,
    setting: []
  },
  {
    name: '岗位',
    field: 'newPost',
    formType: 'text',
    setting: []
  },
  {
    name: '直属上级',
    field: 'newParentId',
    formType: 'user',
    props: { label: 'employeeName', value: 'employeeId', isList: true },
    request: hrmEmployeeQueryInAPI,
    setting: []
  },
  {
    name: '计划转正日期',
    field: 'becomeTime',
    formType: 'text',
    disabled: true,
    setting: []
  },
  {
    name: '实际转正日期',
    field: 'effectTime',
    formType: 'date',
    setting: []
  },
  {
    name: '备注',
    field: 'remarks',
    formType: 'textarea',
    setting: []
  }
]
// 转正验证规则
const officialRules = {
  effectTime: [
    { required: true, message: '请选择', trigger: ['blur', 'change'] }
  ]
}

// 转正model
export const officialModel = {
  fields: officialFields,
  rules: officialRules
}

/** 调岗字段 */
// 1、 只有员工的“工作性质”为“实习、兼职”时，才可以“转全职员工” 3: '实习' 4: '兼职',
// 变动类型 4 转正 5调岗 6晋升 7降级 8转为全职员工
function getChangePostFields(changeType, status) {
  const changeTypeSetting = [{
    label: '调岗',
    value: 5
  }, {
    label: '晋升',
    value: 6
  }, {
    label: '降级',
    value: 7
  }]

  //  说明 1
  if (status == 3 || status == 4) {
    changeTypeSetting.push({
      label: '转为全职员工',
      value: 8
    })
  }

  // 异动原因 7 降级 与其他原因不一样
  let changeReasonList = []
  if (changeType == 7) {
    changeReasonList = [{
      label: '违规违纪',
      value: 4
    }, {
      label: '绩效不达标',
      value: 5
    }, {
      label: '个人身体原因',
      value: 6
    }, {
      label: '不适应当前岗位',
      value: 7
    }]
  } else {
    changeReasonList = [{
      label: '组织架构调整',
      value: 1
    }, {
      label: '个人申请',
      value: 2
    }, {
      label: '工作安排',
      value: 3
    }]
  }

  let temps = [{
    name: '异动类型',
    field: 'changeType',
    formType: 'select',
    setting: changeTypeSetting
  }, {
    name: '异动原因',
    field: 'changeReason',
    formType: 'select',
    setting: changeReasonList
  }]

  if (changeType == 8) {
    temps = temps.concat([{
      name: '入职日期',
      field: 'entryTime',
      formType: 'date',
      disabled: true,
      setting: []
    }, {
      name: '试用期',
      field: 'probation',
      formType: 'select',
      setting: [{
        label: '无试用期',
        value: 0
      }, {
        label: '1个月',
        value: 1
      }, {
        label: '2个月',
        value: 2
      }, {
        label: '3个月',
        value: 3
      }, {
        label: '4个月',
        value: 4
      }, {
        label: '5个月',
        value: 5
      }, {
        label: '6个月',
        value: 6
      }]
    }])
  }

  return temps.concat([{
    name: '原部门',
    field: 'oldDept',
    formType: 'structure',
    disabled: true,
    props: {
      dataType: 'hrm'
    },
    request: hrmDeptQueryTreeListAPI,
    setting: []
  }, {
    name: '变更为',
    field: 'newDept',
    formType: 'structure',
    props: {
      dataType: 'hrm'
    },
    request: hrmDeptQueryTreeListAPI,
    setting: []
  }, {
    name: '原岗位',
    field: 'oldPost',
    formType: 'text',
    disabled: true,
    setting: []
  }, {
    name: '变更为',
    field: 'newPost',
    formType: 'text',
    setting: []
  }, {
    name: '原岗位职级',
    field: 'oldPostLevel',
    disabled: true,
    formType: 'text',
    setting: []
  }, {
    name: '变更为',
    field: 'newPostLevel',
    formType: 'text',
    setting: []
  }, {
    name: '原工作地点',
    field: 'oldWorkAddress',
    formType: 'text',
    disabled: true,
    setting: []
  }, {
    name: '变更为',
    field: 'newWorkAddress',
    formType: 'text',
    setting: []
  }, {
    name: '生效日期',
    field: 'effectTime',
    formType: 'date',
    setting: []
  }, {
    name: '备注',
    field: 'remarks',
    formType: 'textarea',
    setting: []
  }])
}

// 调岗验证规则
const changePostRules = {
  effectTime: [
    { required: true, message: '请选择', trigger: ['blur', 'change'] }
  ]
}

// 调岗model
export const changePostModel = {
  fieldsFunc: getChangePostFields,
  rules: changePostRules
}

