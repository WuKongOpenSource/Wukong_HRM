import {
  adminUserQueryByDeptAPI
} from '@/api/admin/user'
import {
  hrmEmployeeQueryInAPI,
  hrmEmployeeQueryByDeptAPI
} from '@/api/hrm/employee'
import { userListAPI } from '@/api/common'

/**
 * 根据请求获取搜索请求
 * @param {*} request
 * @returns
 */
export function getSearchRequestWithRequest(request) {
  const requestName = request ? request.name : null
  if (requestName) {
    return {
      hrmEmployeeQueryByDeptAPI: hrmEmployeeQueryInAPI,
      adminUserQueryByDeptAPI: userListAPI
    }[requestName]
  }
  return null
}

/**
 * 根据搜索请求获取请求
 * @param {*} request
 * @returns
 */
export function getRequestWithSearchRequest(searchRequest) {
  const requestName = searchRequest ? searchRequest.name : null
  if (requestName) {
    return {
      hrmEmployeeQueryInAPI: hrmEmployeeQueryByDeptAPI,
      userListAPI: adminUserQueryByDeptAPI
    }[requestName]
  }
  return null
}
