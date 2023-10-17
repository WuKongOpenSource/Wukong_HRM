/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-30 16:06:01
 * @LastEditTime: 2020-07-15 18:07:53
 * @LastEditors: yang
 */
// 社保

// 社保验证规则
const securityRules = {
  isFirstSocialSecurity: [
    { required: true, message: '请选择', trigger: ['blur', 'change'] }
  ],
  isFirstAccumulationFund: [
    { required: true, message: '请选择', trigger: ['blur', 'change'] }
  ]
}

function getFields(setting, auth) {
  return [
    {
      name: '是否本地首次缴纳社保',
      field: 'isFirstSocialSecurity',
      formType: 'radio',
      setting: [{
        label: '否',
        value: 0
      }, {
        label: '是',
        value: 1
      }]
    },
    {
      name: '是否本地首次缴纳公积金',
      field: 'isFirstAccumulationFund',
      formType: 'radio',
      setting: [{
        label: '否',
        value: 0
      }, {
        label: '是',
        value: 1
      }]
    },
    {
      name: '个人社保账号',
      field: 'socialSecurityNum',
      formType: 'text',
      setting: []
    },
    {
      name: '个人公积金账号',
      field: 'accumulationFundNum',
      formType: 'text',
      setting: []
    },
    {
      name: '参保起始月份',
      field: 'socialSecurityStartMonth',
      formType: 'date',
      disabled: true,
      setting: []
    },
    {
      name: '参保方案',
      field: 'schemeId',
      formType: 'select',
      setting: setting || [],
      disabled: auth ? !auth.setInsured : true
    }
  ]
}

// 社保model
export default {
  getFields,
  rules: securityRules
}
