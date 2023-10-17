/**
 * Created by yxk at 2020/6/5
 */
import request from '@/utils/request'

/**
 * 从人力资源添加员工
 */
export function hrmAddUserAPI(data) {
  return request({
    url: 'adminUser/hrmAddUser',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询部门用户列表
 */
export function adminUserQueryDeptUserByHrmAPI(data) {
  return request({
    url: 'adminUser/queryDeptUserListByHrm',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询部门员工列表
 * @param {*} data
 */
export function adminUserQueryByDeptAPI(deptId) {
  return request({
    url: `adminUser/queryDeptUserList/${deptId}`,
    method: 'post'
  })
}
