import request from '@/utils/request'

export function depDeleteAPI(data) {
  return request({
    url: 'adminDept/deleteDept/' + data.id,
    method: 'post'
  })
}

export function depEditAPI(data) {
  return request({
    url: 'adminDept/setDept',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function depSaveAPI(data) {
  return request({
    url: 'adminDept/addDept',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function userEditAPI(params) {
  return request({
    url: 'adminUser/setUser',
    method: 'post',
    data: params,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function userAddAPI(params) {
  return request({
    url: 'adminUser/addUser',
    method: 'post',
    data: params,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

// 角色列表
export function roleListAPI(data) {
  return request({
    url: 'adminRole/getAllRoleList',
    method: 'post',
    data: data
  })
}

// 查询系统默认角色
export function adminRoleQueryDefaultAPI() {
  return request({
    url: 'adminRole/queryDefaultRole',
    method: 'post'
  })
}

// 部分角色列表
export function adminRoleGetRoleListAPI(data) {
  return request({
    url: 'adminRole/getRoleList',
    method: 'post',
    data: data
  })
}

/**
 * 查询配置的角色范围
 * @param {*} data
 * @returns
 */
export function adminRoleQueryAuthRoleAPI(roleId) {
  return request({
    url: `adminRole/queryAuthRole/${roleId}`,
    method: 'post'
  })
}

/**
 * 更新配置的角色范围
 * @param {*} data
 * @returns
 */
export function adminRoleUpdateAuthRoleAPI(roleId, data) {
  return request({
    url: `adminRole/updateAuthRole/${roleId}`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 批量修改密码接口
 * @param {*} data
 * password
 * id 用户数组
 */
export function adminUsersUpdatePwdAPI(data) {
  return request({
    url: 'adminUser/resetPassword',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 编辑登录名
 * @param {*} data
 * username
 * password
 * id
 */
export function adminUsersUsernameEditAPI(data) {
  return request({
    url: 'adminUser/usernameEdit',
    method: 'post',
    data: data
  })
}

/**
 * 编辑登录名
 * @param {*} data
 * username
 * password
 * id
 * 可修改管理员
 */
export function adminUsersManagerUsernameEditAPI(data) {
  return request({
    url: 'adminUser/usernameEditByManager',
    method: 'post',
    data: data
  })
}

/**
 * 用户状态修改
 * @param {*} data
 */
export function usersEditStatusAPI(data) {
  return request({
    url: 'adminUser/setUserStatus',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 用户导入模板
 * @param {*} data
 */
export const userImportTemplateURL = 'https://www.72crm.com/npm/static/user_import.xlsx'
export function userImportTemplateAPI(data) {
  return request({
    url: 'adminUser/downloadExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

/**
 * 用户导入
 * @param {*} data
 */
export function userExcelImportAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'adminUser/excelImport',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 60000
  })
}

/**
 * 下载用户导入错误数据
 * @param {*} data
 *
 */
export function userErrorExcelDownAPI(data) {
  return request({
    url: 'adminUser/downExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

/**
 * 呼叫中心状态是否启用
 * @param {*} data
 */
export function crmCallAuthorizeAPI(data) {
  return request({
    url: 'crmCall/authorize',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 重置部门
 * @param {*} data
 */
export function adminUserSetUserDeptPI(data) {
  return request({
    url: 'adminUser/setUserDept',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工数量
 * @param {*} data
 */
export function adminUserCountNumOfUserAPI() {
  return request({
    url: 'adminUser/countNumOfUser',
    method: 'post'
  })
}

/**
 * 员工批量导出
 * @param {*} data
 *
 */
export function adminUserAllExportExcelAPI(data) {
  return request({
    url: 'adminUser/allExportExcel',
    method: 'post',
    responseType: 'blob'
  })
}

/**
 * 选择员工导出
 * @param {*} data
 *
 */
export function adminUserBatchExportExcelAPI(data) {
  return request({
    url: 'adminUser/batchExportExcel',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 设置部门排序
 * @param {*} data
 *
 */
export function adminDeptSetDeptSortAPI(data) {
  return request({
    url: 'adminDept/setDeptSort',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
