/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-06-04 11:29:14
 * @LastEditTime: 2023-08-25 18:19:57
 * @LastEditors: yang
 */
import {
  hrmDeptQueryTreeListAPI
} from '@/api/hrm/dept'
import {
  hrmEmployeeQueryInAPI
} from '@/api/hrm/employee'

function getValueList(data) {
  const keys = Object.keys(data)
  return keys.map(key => {
    return {
      label: data[key],
      value: parseInt(key)
    }
  })
}

const jobNatureValue = {
  1: '全职',
  2: '实习',
  3: '兼职'
}

const workTimeValue = {
  1: '不限',
  2: '一年以内',
  3: '一至三年',
  4: '三至五年',
  5: '五至十年',
  6: '十年以上'
}

const educationRequireValue = {
  1: '不限',
  2: '高中及以上',
  3: '大专及以上',
  4: '本科及以上',
  5: '硕士及以上',
  6: '博士'
}

const emergencyLevelValue = {
  1: '紧急',
  2: '一般'
}

const salaryUnitValue = {
  1: '元/月',
  2: '元/年'
}

// 职位字段
const fields = [
  {
    name: '职位名称',
    field: 'postName',
    formType: 'text',
    width: 150,
    isTable: true,
    setting: []
  }, {
    name: '用人部门',
    field: 'deptId',
    formType: 'structure',
    isTable: true,
    props: {
      dataType: 'hrm'
    },
    request: hrmDeptQueryTreeListAPI,
    setting: []
  },
  {
    name: '工作性质',
    field: 'jobNature',
    formType: 'select',
    isTable: true,
    setting: getValueList(jobNatureValue)
  },
  {
    name: '工作城市',
    field: 'city',
    formType: 'address',
    isTable: true,
    setting: []
  },
  {
    name: '招聘人数',
    field: 'recruitNum',
    formType: 'number',
    max: 999999999,
    isTable: true,
    setting: []
  },
  {
    name: '已入职人数',
    field: 'hasEntryNum',
    formType: 'number',
    isTable: true,
    width: 100,
    setting: []
  },
  {
    name: '招聘原因',
    field: 'reason',
    formType: 'text',
    setting: []
  },
  {
    name: '工作经验',
    field: 'workTime',
    formType: 'select',
    isTable: true,
    setting: getValueList(workTimeValue)
  },
  {
    name: '学历要求',
    field: 'educationRequire',
    formType: 'select',
    isTable: true,
    width: 100,
    setting: getValueList(educationRequireValue)
  },
  {
    name: '薪资范围',
    field: 'salary',
    formType: 'salary',
    isTable: true,
    width: 150,
    setting: []
  },
  {
    name: '最迟到岗时间',
    field: 'latestEntryTime',
    formType: 'date',
    setting: []
  },
  {
    name: '年龄要求',
    field: 'age',
    formType: 'age',
    isTable: true,
    width: 100,
    setting: []
  },
  {
    name: '紧急程度',
    field: 'emergencyLevel',
    formType: 'radio',
    isTable: true,
    setting: getValueList(emergencyLevelValue)
  },
  {
    name: '招聘负责人',
    field: 'ownerEmployeeId',
    formType: 'user',
    isTable: true,
    width: 120,
    props: { label: 'employeeName', value: 'employeeId', isList: true },
    request: hrmEmployeeQueryInAPI,
    setting: []
  },
  {
    name: '职业类型',
    field: 'postTypeId',
    formType: 'postType',
    isTable: true,
    setting: []
  },
  {
    name: '面试官',
    field: 'interviewEmployeeIds',
    formType: 'user',
    radio: false,
    isTable: true,
    width: 120,
    props: { label: 'employeeName', value: 'employeeId', isList: true },
    request: hrmEmployeeQueryInAPI,
    setting: []
  },
  {
    name: '职位描述',
    field: 'description',
    formType: 'textarea',
    setting: []
  }
]

// 职位验证规则
const validatePostType = (rule, value, callback) => {
  if (value && value.length == 1) {
    callback(new Error('职位类型需至少选到第二级'))
  } else {
    callback()
  }
}
const rules = {
  postName: [
    { required: true, message: '请输入', trigger: ['change'] }
  ],
  jobNature: [
    { required: true, message: '请选择', trigger: ['change'] }
  ],
  postType: [
    { validator: validatePostType, trigger: ['change'] }
  ]
}

// 职位model
export default {
  fields: fields,
  rules: rules,
  jobNatureValue,
  workTimeValue,
  educationRequireValue,
  emergencyLevelValue,
  getValueList,
  salaryUnitValue
}
