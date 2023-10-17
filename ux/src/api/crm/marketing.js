import request from '@/utils/request'

/**
 * 活动列表
 * @param {*} data
 */
export function crmMarketingIndexAPI(data) {
  return request({
    url: 'crmMarketing/queryPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 活动创建
 * @param {*} data
 */
export function crmMarketingSaveAPI(data) {
  return request({
    url: 'crmMarketing/add',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 活动编辑
 * @param {*} data
 */
export function crmMarketingUpdateAPI(data) {
  return request({
    url: 'crmMarketing/update',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 活动删除
 * @param {*} data
 */
export function crmMarketingDeleteAPI(data) {
  return request({
    url: 'crmMarketing/deleteByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除推广信息
 * @param {*} data
 */
export function crmMarketingDeleteDataAPI(data) {
  return request({
    url: 'crmMarketing/deleteData',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 活动详情
 * @param {*} data
 */
export function crmMarketingReadAPI(data) {
  return request({
    url: 'crmMarketing/queryById',
    method: 'post',
    data: data
  })
}

/**
 * 活动统计
 * @param {*} data
 */
export function crmMarketingCensusAPI(data) {
  return request({
    url: 'crmMarketing/census',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 活动启用/禁用
 * @param {*} data
 */
export function crmMarketingIsEnableAPI(data) {
  return request({
    url: 'crmMarketing/updateStatus',
    method: 'post',
    data: data
  })
}

/**
 * 同步数据
 * @param {*} data
 */
export function crmMarketingSynchroAPI(data) {
  return request({
    url: 'crmMarketing/syncData',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 导出
 * @param {*} data
 */
export function crmMarketingExcelExportAPI(data) {
  return request({
    url: 'crmMarketing/customerExportExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}
/**
 * 详情
 * @param {*} data
 */
export function crmMarketingInformationAPI(data) {
  return request({
    url: 'crmMarketing/information',
    method: 'post',
    data: data
  })
}

/**
 * 导入
 * @param {*} data
 *
 */
export function crmMarketingExcelImportAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'crmMarketing/uploadExcel',
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
export const crmMarketingExcelDownloadURL = process.env.VUE_APP_BASE_API + 'crmMarketing/downloadExcel'
export function crmMarketingDownloadExcelAPI(data) {
  return request({
    url: 'crmMarketing/downloadExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

/**
 * @description: 批量导出
 * @param {*} data
 * @return {*}
 */
export function crmMarketingExcelAllExportAPI(data) {
  return request({
    url: 'crmMarketing/allExportExcel',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
