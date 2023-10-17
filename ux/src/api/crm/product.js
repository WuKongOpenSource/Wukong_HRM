import request from '@/utils/request'

/**
 * 新建产品
 * @param {*} data
 */
export function crmProductSaveAPI(data) {
  const url = data.entity && data.entity.productId ? 'update' : 'add'
  return request({
    url: 'crmProduct/' + url,
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
export function crmProductIndexAPI(data) {
  return request({
    url: 'crmProduct/queryPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 产品上架列表
 * @param {*} data
 */
export function crmProductSaleIndexAPI(data) {
  return request({
    url: 'crmProduct/querySaleProductPageList ',
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
export function crmProductReadAPI(data) {
  return request({
    url: `crmProduct/queryById/${data.productId}`,
    method: 'post'
  })
}

/**
 * 产品上架、下架
 * @param {*} data
 */
export function crmProductStatusAPI(data) {
  return request({
    url: 'crmProduct/updateStatus',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 产品导出
 * @param {*} data
 * product_id 产品ID
 */
export function crmProductExcelExportAPI(data) {
  return request({
    url: 'crmProduct/batchExportExcel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    responseType: 'blob'
  })
}

export function crmProductExcelAllExportAPI(data) {
  return request({
    url: 'crmProduct/allExportExcel',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 产品导入
 * @param {*} data
 *
 */
export function crmProductExcelImportAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'crmProduct/uploadExcel',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 产品导入模板下载
 * @param {*} data
 *
 */
export const crmProductExcelDownloadURL = process.env.VUE_APP_BASE_API + 'crmProduct/downloadExcel'
export function crmProductDownloadExcelAPI(data) {
  return request({
    url: 'crmProduct/downloadExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

/**
 * 附件列表
 * @param {*} data
 *
 */
export function crmProductFileListAPI(data) {
  return request({
    url: 'crmProduct/queryFileList',
    method: 'post',
    data: data
  })
}

/**
 * tab数量
 * @param {*} data
 *
 */
export function crmProductNumAPI(data) {
  return request({
    url: 'crmProduct/num',
    method: 'post',
    data: data
  })
}

/**
 * 产品转移
 * @param {*} data
 */
export function crmProductTransferAPI(data) {
  return request({
    url: 'crmProduct/changeOwnerUser',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 产品删除
 * @param {*} data
 */
export function crmProductDeleteAPI(data) {
  return request({
    url: 'crmProduct/deleteByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
