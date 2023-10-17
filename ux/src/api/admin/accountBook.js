import request from '@/utils/request'

// 查询账套管理列表页数据
export function queryPageList() {
  return request({
    url: 'financeAccountSet/queryPageList',
    method: 'post'
  })
}
// 查询账单授权员工根据id
export function getUserByaccountId(data) {
  return request({
    url: 'financeAccountSet/getUserByAccountId',
    method: 'post',
    data
  })
}
// 查询账单详情根据id
export function getAccountSetById(data) {
  return request({
    url: 'financeAccountSet/getAccountSetById',
    method: 'post',
    data
  })
}
// 保存数据
export function addAccount(data) {
  return request({
    url: 'financeAccountSet/addAccount',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
// 编辑数据保存
export function updateAccount(data) {
  return request({
    url: 'financeAccountSet/updateAccount',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}
// 删除账套授权员工
export function deleteAccountUser(data) {
  return request({
    url: 'financeAccountSet/deleteAccountUser',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

// 保存账套授权员工
export function saveAccountAuth(data) {
  return request({
    url: 'financeAccountSet/saveAccountAuth',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}
// 账套授权角色关联员工
export function accountUserRole(data) {
  return request({
    url: 'adminRole/accountUserRole',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

// 创建账套
export function saveAccountSet(data) {
  return request({
    url: 'financeAccountSet/saveAccountSet',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

/**
 * 获取帐套切换列表
 */
export function getAccountSetListAPI(data) {
  return request({
    url: '/financeAccountSet/getAccountSetList',
    method: 'post'
  })
}

/**
 * 切换帐套
 */
export function switchAccountSetAPI(data) {
  return request({
    url: '/financeAccountSet/switchAccountSet',
    method: 'post',
    data
  })
}

/**
 * 获取筛选本位币列表
 */
export function queryListByAccountId(data) {
  return request({
    url: '/financeCurrency/queryListByAccountId',
    method: 'post',
    data
  })
}

/**
 * 查询财务管理角色
 */
export function getFinanceRoleByTypeAPI(data) {
  return request({
    url: `/financeAccountSet/getFinanceRoleByType/${data.type}`,
    method: 'post'
  })
}
