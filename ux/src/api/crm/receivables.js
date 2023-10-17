import request from '@/utils/request'

/**
 * 新建回款
 * @param {*} data
 */
export function crmReceivablesSaveAPI(data) {
  const url = data.entity && data.entity.receivablesId ? 'update' : 'add'
  return request({
    url: 'crmReceivables/' + url,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 回款列表
 * @param {*} data
 */
export function crmReceivablesIndexAPI(data) {
  return request({
    url: 'crmReceivables/queryPageList',
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
 *
 */
export function crmReceivablesDeleteAPI(data) {
  return request({
    url: 'crmReceivables/deleteByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 回款详情
 * @param {*} data
 */
export function crmReceivablesReadAPI(id) {
  return request({
    url: `crmReceivables/queryById/${id}`,
    method: 'post'
  })
}

/**
 * 回款计划删除
 * @param {*} data
 */
export function crmReceivablesPlanDeleteAPI(data) {
  return request({
    url: 'crmReceivablesPlan/deleteByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 回款列表
 * @param {*} data
 *
 */
export function crmReceivablesFileListAPI(data) {
  return request({
    url: 'crmReceivables/queryFileList',
    method: 'post',
    data: data
  })
}

/**
 * tab数量
 * @param {*} data
 *
 */
export function crmReceivablesNumAPI(data) {
  return request({
    url: 'crmReceivables/num',
    method: 'post',
    data: data
  })
}

/**
 * 导出
 * @param {*} data
 */
export function crmReceivablesExcelExportAPI(data) {
  return request({
    url: 'crmReceivables/batchExportExcel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    responseType: 'blob',
    timeout: 60000
  })
}

export function crmReceivablesExcelAllExportAPI(data) {
  return request({
    url: 'crmReceivables/allExportExcel',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 转移
 * @param {*} data
 */
export function crmReceivablesTransferAPI(data) {
  return request({
    url: 'crmReceivables/changeOwnerUser',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 回款计划创建
 * @param {*} data
 */
export function crmReceivablesPlanSaveAPI(data) {
  const url = data.entity && data.entity.receivablesPlanId ? 'update' : 'add'
  return request({
    url: 'crmReceivablesPlan/' + url,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
// 团队操作
/**
 * 团队成员创建
 * @param {*} data
 */
export function crmReceivablesSettingTeamSaveAPI(data) {
  return request({
    url: 'crmReceivables/addMembers',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function crmReceivablesSettingTeamDeleteAPI(data) {
  return request({
    url: 'crmReceivables/deleteMembers',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function crmReceivablesTeamMembersAPI(data) {
  return request({
    url: `crmReceivables/getMembers/${data.id}`,
    method: 'post'
  })
}

export function crmReceivablesUpdateMembersAPI(data) {
  return request({
    url: 'crmReceivables/updateMembers',
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
export function crmReceivablesExitTeamAPI(data) {
  return request({
    url: `crmReceivables/exitTeam/${data.id}`,
    method: 'post'
  })
}

/**
 * 新增团队成员交叉产品信息
 */
export function crmReceivablesAddOrUpdateTeamProductAPI(data) {
  return request({
    url: `crmReceivables/addOrUpdateTeamProduct/${data.id}`,
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
export function crmReceivablesDeleteTeamProductAPI(id) {
  return request({
    url: `crmReceivables/deleteTeamProductUser/${id}`,
    method: 'post'
  })
}

/**
 * 查询团队成员交叉产品信息
 */
export function crmReceivablesQueryTeamProductAPI(id) {
  return request({
    url: `crmReceivables/queryTeamProductList/${id}`,
    method: 'post'
  })
}

/**
 * 导入
 * @param {*} data
 *
 */
export function crmReceivablesExcelImportAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'crmReceivables/uploadExcel',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 导入模板下载
 * @param {*} data
 *
 */
export const crmReceivablesExcelDownloadURL = process.env.VUE_APP_BASE_API + 'crmReceivables/downloadExcel'
export function crmReceivablesDownloadExcelAPI(data) {
  return request({
    url: 'crmReceivables/downloadExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}
