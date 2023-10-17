import request from '@/utils/request'

/**
 * 员工
 * @param {*} data
 */
export function userListAPI(data) {
  return request({
    url: 'adminUser/queryUserList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 部门
 * @param {*} data
 */
export function depListAPI(data) {
  return request({
    url: 'adminUser/queryDeptTree',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 通过员工id，部门id，角色id数组查询基本信息
 */
export function queryUserDeptOrRoleInfoAPI(data) {
  return request({
    url: 'adminUser/queryUserDeptOrRoleInfo',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 附件
 * @param {*} data
 */
export function crmFileSaveAPI(data, config = {}) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'commonFile/upload',
    method: 'post',
    data: param,
    ...config,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function crmFileSingleSaveAPI(data, config = {}) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  param.append('overwrite', true)
  return request({
    url: 'commonFile/upload',
    method: 'post',
    data: param,
    ...config,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * @description: table附件上传
 * @param {*} data
 * @param {*} config
 * @return {*}
 */
export function tableAttachmentUploadAPI(data, config = {}) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'adminFile/attachments/upload',
    method: 'post',
    data: param,
    ...config,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * @description: table附件删除
 * @param {*} data
 * @return {*}
 */
export function tableAttachmentDeleteAPI(data) {
  return request({
    url: 'adminFile/attachments/del',
    method: 'post',
    data: data
  })
}

/**
 * @description: table附件下载
 * @param {*} data
 * @return {*}
 */
export function tableAttachmentDownAPI(data) {
  return request({
    url: 'adminFile/attachments/down',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

/**
 * 公共web图片上传
 * @param data
 */
export function webPicSaveAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
    param.append('isPublic', '1')
  })
  return request({
    url: 'adminFile/uploadImage',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 公共web文件上传
 * @param data
 */
export function webFileSaveAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
    param.append('isPublic', '1')
  })
  return request({
    url: 'commonFile/upload',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/** 暂时客户管理里面也有 */
/**
 * 附件删除接口
 * @param {*} data
 */
export function crmFileDeleteAPI(data) {
  return request({
    url: `commonFile/deleteById/${data.id}`,
    method: 'post',
    data: data
  })
}

/**
 * 根据批次ID删除文件
 * @param {*} data
 */
export function crmFileRemoveByBatchIdAPI(data) {
  return request({
    url: 'commonFile/deleteByBatchId',
    method: 'post',
    data: data
  })
}

/**
 * 权限数据返回
 * @param {*} data
 */
export function adminIndexAuthListAPI(data) {
  return request({
    url: 'adminRole/auth',
    method: 'post',
    data: data
  })
}

/**
 * 系统消息列表
 * label 1 任务 2 日志 3 oa审批 4公告 5 日程 6 客户管理
 * isRead 0 未读 1 已读
 * @param {*} data
 */
export function systemMessageListAPI(data) {
  return request({
    url: 'adminMessage/queryList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 读消息
 * @param {*} data
 */
export function systemMessageReadAPI(data) {
  return request({
    url: 'adminMessage/readMessage',
    method: 'post',
    data: data
  })
}

/**
 * 读全部消息
 * @param {*} data
 */
export function systemMessageReadAllAPI(data) {
  return request({
    url: 'adminMessage/readAllMessage',
    method: 'post',
    data: data
  })
}

/**
 * 用户信息
 * @param {*} data
 */
export function systemUserInfoAPI(data) {
  return request({
    url: 'adminUser/queryUserInfo',
    method: 'post',
    data: data
  })
}

/**
 * 系统消息按类别删除
 * @param {*} data
 */
export function systemMessageClearAPI(data) {
  return request({
    url: 'adminMessage/clear',
    method: 'post',
    data: data
  })
}

/**
 * 系统消息删除
 * @param {*} data
 */
export function systemMessageDeleteByIdAPI(id) {
  return request({
    url: `adminMessage/deleteById/${id}`,
    method: 'post'
  })
}

export function downloadFileAPI(url) {
  return request({
    url: url,
    data: {},
    responseType: 'blob'
  })
}

/**
 * 查询完整组织架构信息
 * @param {*} data
 */
export function adminUserQueryOrgInfoAPI(data) {
  return request({
    url: 'adminUser/queryOrganizationInfo',
    method: 'post',
    data: data
  })
}

/**
 * 金额单位设置查询
 * @param {*} data
 */
export function adminMoneyUnitGetAPI() {
  return request({
    url: 'adminConfig/getMonetaryUnit',
    method: 'post'
  })
}

/**
 * 金额单位设置
 * @param {*} data
 */
export function adminMoneyUnitSetAPI(data) {
  return request({
    url: 'adminConfig/setMonetaryUnit',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

