import request from '@/utils/request'

// export function roleListFun(data) {
//   return request({
//     url: 'adminRole/getAllRoleList',
//     method: 'post',
//     data: data
//   })
// }

// export function rulesList(data) {
//   return request({
//     url: 'adminMenu/getAllMenuList',
//     method: 'post',
//     data: data
//   })
// }

export function roleAddAPI(data) {
  return request({
    url: 'adminRole/add',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 项目管理添加角色
 * @param {*} data
 * @returns
 */
export function addProjectRoleAPI(data) {
  return request({
    url: 'adminRole/addProjectRole',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function roleDeleteAPI(data) {
  return request({
    url: 'adminRole/delete',
    method: 'post',
    data: data
  })
}

export function roleUpdateAPI(data) {
  return request({
    url: 'adminRole/update',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function updateRoleMenuAPI(data) {
  return request({
    url: 'adminRole/updateRoleMenu',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 角色复制
 * @param {*} data
 */
export function roleCopyAPI(data) {
  return request({
    url: 'adminRole/copy',
    method: 'post',
    data: data
  })
}

/**
 * 复制员工角色
 * @param {*} data
 */
export function adminRoleRelatedDeptUserAPI(data) {
  return request({
    url: 'adminRole/relatedDeptUser',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 添加编辑员工
 * @param {*} data
 */
export function adminRoleRelatedUserAPI(data) {
  return request({
    url: 'adminRole/relatedUser',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 删除员工
 * @param {*} data
 */
export function unbindingUserAPI(data) {
  return request({
    url: 'adminRole/unbindingUser',
    method: 'post',
    data: data
  })
}

/**
 * 角色分类列表
 * @param {*} data
 */
export function adminGroupsTypeListAPI(data) {
  return request({
    url: 'adminRole/getRoleTypeList',
    method: 'post',
    data: data
  })
}

/**
 * 参数 roleType   根据角色类型查询角色列表
 * @param {*} roleType
 */
export function systemRoleByTypeAPI(roleType) {
  return request({
    url: `adminRole/getRoleByType/${roleType}`,
    method: 'post'
  })
}

/**
 * @description: 角色列表排序
 * @param {*} data
 * @return {*}
 */
export function systemRoleListSortAPI(data) {
  return request({
    url: 'adminRole/setSorting',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 规则
 * @param {*} roleType
 */
export function systemRuleByTypeAPI(roleType) {
  return request({
    url: `adminMenu/getMenuListByType/${roleType}`,
    method: 'post'
  })
}

/**
 * 查询字段授权设置
 * @param {*} data
 */
export function systemRoleGetFieldAPI(data) {
  return request({
    url: 'crmField/queryRoleField',
    method: 'post',
    data: data
  })
}

/**
 * 模块设置字段授权
 * @param {*} data
 */
export function systemRoleSetFieldAPI(data) {
  return request({
    url: 'crmField/saveRoleField',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}
