/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-30 16:06:01
 * @LastEditTime: 2020-06-05 17:53:53
 * @LastEditors: yang
 */
// 工资卡
const cardFields = [
  {
    name: '工资卡卡号',
    field: 'salaryCardNum',
    formType: 'text',
    setting: []
  },
  {
    name: '工资卡开户城市',
    field: 'accountOpeningCity',
    formType: 'text',
    setting: []
  },
  {
    name: '银行名称',
    field: 'bankName',
    formType: 'text',
    setting: []
  },
  {
    name: '工资卡开户行',
    field: 'openingBank',
    formType: 'text',
    setting: []
  }
]

// 工资卡验证规则
const cardRules = {
  salaryCardNum: [
    { required: true, message: '请选择', trigger: ['change', 'blur'] }
  ]
}

// 工资卡model
export default {
  fields: cardFields,
  rules: cardRules
}
