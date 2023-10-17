import request from '@/utils/request'

/**
 * 新建
 * @param {*} data
 */
export function crmReturnVisitSaveAPI(data) {
  const url = data.entity && data.entity.visitId ? 'update' : 'add'
  return request({
    url: 'crmReturnVisit/' + url,
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
export function crmReturnVisitIndexAPI(data) {
  return request({
    url: 'crmReturnVisit/queryPageList',
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
export function crmReturnVisitDeleteAPI(data) {
  return request({
    url: 'crmReturnVisit/deleteByIds',
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
export function crmReturnVisitFileAPI(data) {
  return request({
    url: 'crmReturnVisit/queryFileList',
    method: 'post',
    data: data
  })
}

/**
 * 详情
 * @param {*} visitId
 */
export function crmReturnVisitReadAPI(visitId) {
  return request({
    url: `crmReturnVisit/queryById/${visitId}`,
    method: 'post'
  })
}

// 团队操作
/**
 * 团队成员创建
 * @param {*} data
 */
export function crmReturnVisitSettingTeamSaveAPI(data) {
  return request({
    url: 'crmReturnVisit/addMembers',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function crmReturnVisitSettingTeamDeleteAPI(data) {
  return request({
    url: 'crmReturnVisit/deleteMembers',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function crmReturnVisitTeamMembersAPI(data) {
  return request({
    url: `crmReturnVisit/getMembers/${data.id}`,
    method: 'post'
  })
}

export function crmReturnVisitUpdateMembersAPI(data) {
  return request({
    url: 'crmReturnVisit/updateMembers',
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
export function crmReturnVisitExitTeamAPI(data) {
  return request({
    url: `crmReturnVisit/exitTeam/${data.id}`,
    method: 'post'
  })
}

/**
 * 新增团队成员交叉产品信息
 */
export function crmReturnVisitAddOrUpdateTeamProductAPI(data) {
  return request({
    url: `crmReturnVisit/addOrUpdateTeamProduct/${data.id}`,
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
export function crmReturnVisitDeleteTeamProductAPI(id) {
  return request({
    url: `crmReturnVisit/deleteTeamProductUser/${id}`,
    method: 'post'
  })
}

/**
 * 查询团队成员交叉产品信息
 */
export function crmReturnVisitQueryTeamProductAPI(id) {
  return request({
    url: `crmReturnVisit/queryTeamProductList/${id}`,
    method: 'post'
  })
}

/**
 * 回访导入
 * @param {*} data
 *
 */
export function crmReturnVisitExcelImportAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'crmReturnVisit/uploadExcel',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 回访导入模板下载
 * @param {*} data
 *
 */
export const crmReturnVisitExcelDownloadURL = process.env.VUE_APP_BASE_API + 'crmReturnVisit/downloadExcel'
export function crmReturnVisitDownloadExcelAPI(data) {
  return request({
    url: 'crmReturnVisit/downloadExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

/**
 * 勾选导出
 * @param {*} data
 */
export function crmReturnVisitExcelExportAPI(data) {
  return request({
    url: 'crmReturnVisit/batchExportExcel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    responseType: 'blob'
  })
}

/**
 * @description: 批量导出
 * @param {*} data
 * @return {*}
 */
export function crmReturnVisitExcelAllExportAPI(data) {
  return request({
    url: 'crmReturnVisit/allExportExcel',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
