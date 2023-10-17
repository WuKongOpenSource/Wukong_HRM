import request from '@/utils/request'

/**
 * 新建联系人
 * @param {*} data
 */
export function crmContactsSaveAPI(data) {
  var url = data.entity && data.entity.contactsId ? 'update' : 'add'
  return request({
    url: 'crmContacts/' + url,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 列表
 * @param {*} data
 */
export function crmContactsIndexAPI(data) {
  return request({
    url: 'crmContacts/queryPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除
 * @param {*} data
 */
export function crmContactsDeleteAPI(data) {
  return request({
    url: 'crmContacts/deleteByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 详情
 * @param {*} data
 */
export function crmContactsReadAPI(data) {
  return request({
    url: `crmContacts/queryById/${data.contactsId}`,
    method: 'post'
  })
}

/**
 * 联系人转移
 * @param {*} data
 */
export function crmContactsTransferAPI(data) {
  return request({
    url: 'crmContacts/changeOwnerUser',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 联系人下商机
 * @param {*} data
 */
export function crmContactsQueryBusinessAPI(data) {
  return request({
    url: 'crmContacts/queryBusiness',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 联系人导出
 * @param {*} data
 * Contacts_id 联系人ID
 */
export function crmContactsExcelExportAPI(data) {
  return request({
    url: 'crmContacts/batchExportExcel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    responseType: 'blob'
  })
}

export function crmContactsExcelAllExportAPI(data) {
  return request({
    url: 'crmContacts/allExportExcel',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 联系人导入
 * @param {*} data
 *
 */
export function crmContactsExcelImportAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'crmContacts/uploadExcel',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 联系人导入模板下载
 * @param {*} data
 *
 */
export const crmContactsExcelDownloadURL = process.env.VUE_APP_BASE_API + 'crmContacts/downloadExcel'
export function crmContactsDownloadExcelAPI(data) {
  return request({
    url: 'crmContacts/downloadExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

/**
 * 联系人关联商机
 * @param {*} data
 */
export function crmContactsRelateBusinessAPI(data) {
  return request({
    url: 'crmContacts/relateBusiness',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 联系人取消关联商机
 * @param {*} data
 */
export function crmContactsUnrelateBusinessAPI(data) {
  return request({
    url: 'crmContacts/unrelateBusiness',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 附件列表
 * @param {*} data
 *
 */
export function crmContactsFileListAPI(data) {
  return request({
    url: 'crmContacts/queryFileList',
    method: 'post',
    data: data
  })
}

/**
 * tab数量
 * @param {*} data
 *
 */
export function crmContactsNumAPI(data) {
  return request({
    url: 'crmContacts/num',
    method: 'post',
    data: data
  })
}

// 团队操作
/**
 * 团队成员创建
 * @param {*} data
 */
export function crmContactsSettingTeamSaveAPI(data) {
  return request({
    url: 'crmContacts/addMembers',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function crmContactsSettingTeamDeleteAPI(data) {
  return request({
    url: 'crmContacts/deleteMembers',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function crmContactsTeamMembersAPI(data) {
  return request({
    url: `crmContacts/getMembers/${data.id}`,
    method: 'post'
  })
}

export function crmContactsUpdateMembersAPI(data) {
  return request({
    url: 'crmContacts/updateMembers',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 退出团队
 * @param {*} data
 */
export function crmContactsExitTeamAPI(data) {
  return request({
    url: `crmContacts/exitTeam/${data.id}`,
    method: 'post'
  })
}

/**
 * 新增团队成员交叉产品信息
 */
export function crmContactsAddOrUpdateTeamProductAPI(data) {
  return request({
    url: `crmContacts/addOrUpdateTeamProduct/${data.id}`,
    method: 'post',
    data: data.entity,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除团队成员交叉产品信息
 */
export function crmContactsDeleteTeamProductAPI(id) {
  return request({
    url: `crmContacts/deleteTeamProductUser/${id}`,
    method: 'post'
  })
}

/**
 * 查询团队成员交叉产品信息
 */
export function crmContactsQueryTeamProductAPI(id) {
  return request({
    url: `crmContacts/queryTeamProductList/${id}`,
    method: 'post'
  })
}
