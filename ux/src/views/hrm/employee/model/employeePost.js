/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-30 16:06:01
 * @LastEditTime: 2020-06-05 17:54:24
 * @LastEditors: yang
 */

const quitTypeValue = {
  1: '主动离职',
  2: '被动离职',
  3: '退休'
}

const quitReasonValue = {
  1: '家庭原因',
  2: '身体原因',
  3: '薪资原因',
  4: '交通不便',
  5: '工作压力',
  6: '管理问题',
  7: '无晋升机会',
  8: '职业规划',
  9: '合同到期放弃续签',
  10: '以其他个人原因离职',

  11: '试用期内辞退',
  12: '违反公司条例',
  13: '组织调整/裁员',
  14: '绩效不达标辞退',
  15: '合同到期不续签',
  16: '其他原因被动离职'
}

function getValueList(data) {
  const keys = Object.keys(data)
  return keys.map(key => {
    return {
      label: data[key],
      value: parseInt(key)
    }
  })
}

// 主动原因
function initiativeList() {
  const keys = Object.keys(quitReasonValue)
  const temps = []
  for (let index = 0; index < keys.length; index++) {
    const key = keys[index]
    if (key <= 10) {
      temps.push({
        label: quitReasonValue[key],
        value: parseInt(key)
      })
    }
  }
  return temps
}

// 被动原因
function passivityList() {
  const keys = Object.keys(quitReasonValue)
  const temps = []
  for (let index = 0; index < keys.length; index++) {
    const key = keys[index]
    if (key > 10) {
      temps.push({
        label: quitReasonValue[key],
        value: parseInt(key)
      })
    }
  }
  return temps
}

function getFields(data) {
  const tempFields = [{
    name: '计划离职日期',
    field: 'planQuitTime',
    formType: 'date',
    setting: []
  },
  {
    name: '离职申请日期',
    field: 'applyQuitTime',
    formType: 'date',
    setting: []
  },
  {
    name: '薪资结算日期',
    field: 'salarySettlementTime',
    formType: 'date',
    setting: []
  },
  {
    name: '离职类型',
    field: 'quitType',
    formType: 'select',
    setting: getValueList(quitTypeValue)
  }]

  if (!data || data.quitType != 3) {
    let quitReasonList = []
    if (data.quitType == 1) {
      quitReasonList = initiativeList()
    } else if (data.quitType == 2) {
      quitReasonList = passivityList()
    }

    tempFields.push({
      name: '离职原因',
      field: 'quitReason',
      formType: 'select',
      setting: quitReasonList
    })
  }

  tempFields.push({
    name: '备注',
    field: 'remarks',
    formType: 'textarea',
    setting: []
  })
  return tempFields
}

// 离职验证规则
function getRules(data) {
  const postRules = {
    planQuitTime: [
      { required: true, message: '请选择', trigger: ['blur', 'change'] }
    ],
    applyQuitTime: [
      { required: true, message: '请选择', trigger: ['blur', 'change'] }
    ],
    salarySettlementTime: [
      { required: true, message: '请选择', trigger: ['blur', 'change'] }
    ],
    quitType: [
      { required: true, message: '请选择', trigger: ['blur', 'change'] }
    ]
  }

  if (!data || data.quitType != 3) {
    postRules['quitReason'] = [
      { required: true, message: '请选择', trigger: ['blur', 'change'] }
    ]
  }

  return postRules
}

// 离职model
export default {
  getFields,
  getRules,
  quitTypeValue,
  quitReasonValue
}
