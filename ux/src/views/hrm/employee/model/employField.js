/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-06-01 18:51:07
 * @LastEditTime: 2023-08-25 18:19:20
 * @LastEditors: yang
 */
// componentType 0 无需关联 1 员工 2 部门 3 志威 4 系统给用户 5 系统各部门 6 招聘渠道 7 籍贯
// 1 text 2 textarea 3 select 4 date 5 number 6 floatnumber 7 mobile 8 file 9 checkbox
// 10 datetime 11 email 12 address
function getFormTypeWithType(type, componentType) {
  if (type == 1) {
    return 'text'
  } else if (type == 2) {
    return 'textarea'
  } else if (type == 3) {
    if (componentType == 1) {
      return 'user'
    } else if (componentType == 2) {
      return 'structure'
    } else if (componentType == 7) {
      return 'city'
    }
    return 'select'
  } else if (type == 4) {
    return 'date'
  } else if (type == 5) {
    return 'number'
  } else if (type == 6) {
    return 'floatnumber'
  } else if (type == 7) {
    return 'mobile'
  } else if (type == 8) {
    return 'file'
  } else if (type == 9) {
    return 'checkbox'
  } else if (type == 13) {
    return 'datetime'
  } else if (type == 14) {
    return 'email'
  } else if (type == 40) {
    return 'address'
  }

  return 'text'
}

import {
  hrmEmployeeQueryInAPI
} from '@/api/hrm/employee'
import {
  hrmDeptQueryTreeListAPI
} from '@/api/hrm/dept'

function getCreateFieldDefalutData(item) {
  item.field = item.fieldName
  if (item.formType == 'user') {
    item.request = hrmEmployeeQueryInAPI
    item.props = { dataType: 'hrm', label: 'employeeName', value: 'employeeId', isList: true }
  } else if (item.formType == 'structure') {
    item.request = hrmDeptQueryTreeListAPI
    item.props = { dataType: 'hrm', label: 'name', value: 'deptId' }
  }
}

export default {
  getFormTypeWithType,
  // 获取字段默认值
  getCreateFieldDefalutData
}
