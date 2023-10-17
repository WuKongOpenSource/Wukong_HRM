import request from '@/utils/request'

/**
 * 列表
 * @param {*} data
 */
export function systemRoleQueryProjectRoleListAPI(data) {
  return request({
    url: 'adminRole/queryProjectRoleList',
    method: 'post',
    data: data
  })
}

/**
 * 设置
 * @param {*} data
 */
export function systemRoleSetWorkRoleAPI(data) {
  return request({
    url: 'adminRole/setWorkRole',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 删除项目角色
 */
export function systemRoleDeleteWorkRoleAPI(data) {
  return request({
    url: 'adminRole/deleteWorkRole',
    method: 'post',
    data: data
  })
}

