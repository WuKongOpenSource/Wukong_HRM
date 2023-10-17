/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-30 14:36:57
 * @LastEditTime: 2020-06-05 17:54:17
 * @LastEditors: yang
 */

function getFields(data) {
  const tempFields = [
    {
      name: '合同编号',
      field: 'contractNum',
      formType: 'text',
      setting: []
    },
    {
      name: '合同类型',
      field: 'contractType',
      formType: 'select',
      setting: [{
        label: '固定期限劳动合同',
        value: 1
      }, {
        label: '无固定期限劳动合同',
        value: 2
      }, {
        label: '以完成一定工作任务为期限的劳动合同',
        value: 3
      }, {
        label: '实习协议',
        value: 4
      }, {
        label: '劳务合同',
        value: 5
      }, {
        label: '返聘协议',
        value: 6
      }, {
        label: '劳务派遣合同',
        value: 7
      }, {
        label: '借调合同',
        value: 8
      }, {
        label: '其他',
        value: 9
      }]
    },
    {
      name: '合同开始日期',
      field: 'startTime',
      formType: 'date',
      setting: []
    },
    {
      name: '合同结束日期',
      field: 'endTime',
      formType: 'date',
      setting: []
    }
  ]

  if (!data || data.contractType != 2) {
    tempFields.push({
      name: '合同期限',
      field: 'term',
      formType: 'select',
      setting: [
        { label: '1年', value: 1 },
        { label: '2年', value: 2 },
        { label: '3年', value: 3 },
        { label: '4年', value: 4 },
        { label: '5年', value: 5 },
        { label: '6年', value: 6 },
        { label: '7年', value: 7 },
        { label: '8年', value: 8 },
        { label: '9年', value: 9 },
        { label: '10年', value: 10 }
      ]
    })
  }

  return tempFields.concat([{
    name: '合同状态',
    field: 'status',
    formType: 'select',
    setting: [{
      label: '未执行',
      value: 0
    }, {
      label: '执行中',
      value: 1
    }, {
      label: '已到期',
      value: 2
    }]
  },
  {
    name: '签约公司',
    field: 'signCompany',
    formType: 'text',
    setting: []
  },
  {
    name: '合同签订日期',
    field: 'signTime',
    formType: 'date',
    setting: []
  },
  {
    name: '合同备注',
    field: 'remarks',
    formType: 'textarea',
    setting: []
  }])
}

// 合同验证规则 合同类型、合同开始时间、合同结束时间、合同签订时间、合同期限、合同状态

function getRules(data) {
  const tempRules = {
    contractType: [
      { required: true, message: '请选择', trigger: ['blur', 'change'] }
    ],
    startTime: [
      { required: true, message: '请选择', trigger: ['blur', 'change'] }
    ],
    endTime: [
      { required: true, message: '请选择', trigger: ['blur', 'change'] }
    ],
    signTime: [
      { required: true, message: '请选择', trigger: ['blur', 'change'] }
    ],
    status: [
      { required: true, message: '请选择', trigger: ['blur', 'change'] }
    ]
  }

  if (!data || data.contractType != 2) {
    tempRules['term'] = [
      { required: true, message: '请选择', trigger: ['blur', 'change'] }
    ]
  }

  return tempRules
}

const contractTypeValue = {
  1: '固定期限劳动合同',
  2: '无固定期限劳动合同',
  3: '以完成一定工作任务为期限的劳动合同',
  4: '实习协议',
  5: '劳务合同',
  6: '返聘协议',
  7: '劳务派遣合同',
  8: '借调合同',
  9: '其他'
}

const termValue = {
  1: '1年',
  2: '2年',
  3: '3年',
  4: '4年',
  5: '5年',
  6: '6年',
  7: '7年',
  8: '8年',
  9: '9年',
  10: '10年'
}

const statusValue = {
  0: '未执行',
  1: '执行中',
  2: '已到期'
}

// 合同model
export default {
  getFields,
  getRules,
  contractTypeValue,
  termValue,
  statusValue
}
