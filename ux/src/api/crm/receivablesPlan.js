import request from '@/utils/request'

/**
 * 新建 编辑
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

/**
 * 批量保存数据
 * @param {*} data
 */
export function crmReceivablesPlanBatchSaveAPI(data) {
  return request({
    url: 'crmReceivablesPlan/batchSave',
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
export function crmReceivablesPlanIndexAPI(data) {
  return request({
    url: 'crmReceivablesPlan/queryPageList',
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
 * 详情
 * @param {*} id
 */
export function crmReceivablesPlanReadAPI(id) {
  return request({
    url: `crmReceivablesPlan/queryById/${id}`,
    method: 'post'
  })
}

/**
 * 回款计划导出
 * @param {*} data
 *
 */
export function crmReceivablesPlanExcelExportAPI(data) {
  return request({
    url: 'crmReceivablesPlan/batchExportExcel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    responseType: 'blob'
  })
}

/**
 * 回款计划全部导出
 * @param {*} data
 */
export function crmReceivablesPlanExcelAllExportAPI(data) {
  return request({
    url: 'crmReceivablesPlan/allExportExcel',
    method: 'post',
    data: data,
    responseType: 'blob',
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
export function crmReceivablesPlanFileListAPI(data) {
  return request({
    url: 'crmReceivablesPlan/queryFileList',
    method: 'post',
    data: data
  })
}

/**
 * tab数量
 * @param {*} data
 *
 */
export function crmReceivablesPlanNumAPI(data) {
  return request({
    url: 'crmReceivablesPlan/num',
    method: 'post',
    data: data
  })
}

/**
 * 转移
 * @param {*} data
 */
export function crmReceivablesPlanTransferAPI(data) {
  return request({
    url: 'crmReceivablesPlan/changeOwnerUser',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 导入
 * @param {*} data
 *
 */
export function crmReceivablesPlanExcelImportAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'crmReceivablesPlan/uploadExcel',
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
export const crmReceivablesPlanExcelDownloadURL = process.env.VUE_APP_BASE_API + 'crmReceivablesPlan/downloadExcel'
export function crmReceivablesPlanDownloadExcelAPI(data) {
  return request({
    url: 'crmReceivablesPlan/downloadExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}
