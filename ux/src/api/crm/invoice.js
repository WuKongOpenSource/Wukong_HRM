import request from '@/utils/request'

/**
 * 新建编辑
 * @param {*} data
 */
export function crmInvoiceSaveAPI(data) {
  const url = data.entity && data.entity.invoiceId ? 'update' : 'add'
  return request({
    url: 'crmInvoice/' + url,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 编辑
 * @param {*} data
 */
export function crmInvoiceUpdateAPI(data) {
  return request({
    url: 'crmInvoice/update',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 标注已开发票
 * @param {*} data
 */
export function crmInvoiceStatusUpdateAPI(data) {
  return request({
    url: 'crmInvoice/updateInvoiceStatus',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 标注已开发票
 * @param {*} data
 */
export function crmInvoiceStatusResetAPI(data) {
  return request({
    url: 'crmInvoice/resetInvoiceStatus',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 分页查询发票
 * @param {*} data
 */
export function crmInvoiceIndexAPI(data) {
  return request({
    url: 'crmInvoice/queryPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

// 发票详情
export function crmInvoiceReadAPI(invoiceId) {
  return request({
    url: `crmInvoice/queryById/${invoiceId}`,
    method: 'post'
  })
}

/**
 * 附件列表
 * @param {*} data
 *
 */
export function crmInvoiceFileListAPI(data) {
  return request({
    url: 'crmInvoice/queryFileList',
    method: 'post',
    data: data
  })
}

/**
 * 转移
 * @param {*} data
 */
export function crmInvoiceTransferAPI(data) {
  return request({
    url: 'crmInvoice/changeOwnerUser',
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
export function crmInvoiceDeleteIdsAPI(data) {
  return request({
    url: 'crmInvoice/deleteByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 发票规则信息
 * @param {*} data
 */
export function crmInvoiceNumberConfigAPI(data) {
  return request({
    url: 'crmNumberSetting/queryInvoiceNumberSetting',
    method: 'post',
    data: data
  })
}

/**
 * 新建发票信息
 * @param {*} data
 */
export function crmInvoiceSaveInvoiceInfoAPI(data) {
  return request({
    url: 'crmInvoice/saveInvoiceInfo',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 编辑发票信息
 * @param {*} data
 */
export function crmInvoiceUpdateInvoiceInfoAPI(data) {
  return request({
    url: 'crmInvoice/updateInvoiceInfo',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除发票信息
 * @param {*} data
 */
export function crmInvoiceDeleteInvoiceInfoAPI(data) {
  return request({
    url: 'crmInvoice/deleteInvoiceInfo',
    method: 'post',
    data: data
  })
}

/**
 * 全部导出
 * @param {*} data
 */
export function crmInvoiceExcelAllExportAPI(data) {
  return request({
    url: 'crmInvoice/allExportExcel',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 勾选导出
 * @param {*} data
 */
export function crmInvoiceBatchExcelExportAPI(data) {
  return request({
    url: 'crmInvoice/batchExportExcel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    responseType: 'blob'
  })
}

/**
 * 导入
 * @param {*} data
 *
 */
export function crmInvoiceExcelImportAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'crmInvoice/uploadExcel',
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
export const crmInvoiceExcelDownloadURL = process.env.VUE_APP_BASE_API + 'crmInvoice/downloadExcel'
export function crmInvoiceDownloadExcelAPI(data) {
  return request({
    url: 'crmInvoice/downloadExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}
