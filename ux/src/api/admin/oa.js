import request from '@/utils/request'

/**
 * 审批类型列表
 * @param {*} data
 */
export function oaExamineCategoryListAPI(data) {
  return request({
    url: 'oaExamineCategory/queryExamineCategoryList',
    method: 'post',
    data: data
  })
}

/**
 * 审批类型的创建
 * @param {*} data
 */
export function oaExamineCategorySaveAPI(data) {
  return request({
    url: 'oaExamineCategory/setExamineCategory',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 审批删除
 * @param {*} data
 */
export function oaExamineCategoryDeleteAPI(data) {
  return request({
    url: 'oaExamineCategory/deleteExamineCategory',
    method: 'post',
    data: data
  })
}

/**
 * 审批状态（启用、停用）
 * @param {*} data
 */
export function oaExamineCategoryEnablesAPI(data) {
  return request({
    url: 'oaExamineCategory/updateStatus',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询 打印模板列表
 * @param {*} data
 */
export function oaExaminePrintTemplateListAPI(data) {
  return request({
    url: 'oaExaminePrintTemplate/queryList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除指定打印模板
 * templateId
 * @param {*} data
 */
export function oaExaminePrintDeleteTemplateAPI(data) {
  return request({
    url: 'oaExaminePrintTemplate/delete',
    method: 'post',
    data: data
  })
}

/**
 * 模板复制
 * type
 * @param {*} data
 */
export function oaExaminePrintCopyTemplateAPI(data) {
  return request({
    url: 'oaExaminePrintTemplate/copyTemplate',
    method: 'post',
    data: data
  })
}

/**
 * 新增 打印模板
 * @param {*} data
 */
export function oaExaminePrintTemplateAddAPI(data) {
  return request({
    url: 'oaExaminePrintTemplate/add',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询字段
 * @param {*} data
 */
export function oaExaminePrintTemplateFieldsAPI(data) {
  return request({
    url: 'oaExaminePrintTemplate/queryFields',
    method: 'post',
    data: data
  })
}

/**
 * 编辑 打印模板
 * @param {*} data
 */
export function oaExaminePrintTemplateUpdateAPI(data) {
  return request({
    url: 'oaExaminePrintTemplate/update',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 跟进ID查询打印模板
 * @param {*} data
 */
export function oaExaminePrintTemplateQueryByIdAPI(data) {
  return request({
    url: 'oaExaminePrintTemplate/queryById',
    method: 'post',
    data: data
  })
}

/**
 * 打印预览
 * type
 * @param {*} data
 */
export function oaExaminePrintTemplatePreviewAPI(data) {
  return request({
    url: 'oaExaminePrintTemplate/preview',
    method: 'post',
    data: data
  })
}

/**
 * 打印
 * type
 * @param {*} data
 */
export function oaExaminePrintTemplatePrintAPI(data) {
  return request({
    url: 'oaExaminePrintTemplate/print',
    method: 'post',
    data: data
  })
}

/** ****************日志***********************/
/**
 * 获取日志模板列表
 * @param {*} data
 */
export function oaLogTemplateListAPI(data) {
  return request({
    url: 'oaLogTemplate/queryTemplateList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 管理后台修改日志模板状态
 * @param {*} data
 * configId
 * status 状态 1 启用 2 停用 3 删除
 */
export function oaLogTemplateUpdateStatusAPI(data) {
  return request({
    url: `oaLogTemplate/updateLogTemplateStatus/${data.configId}`,
    method: 'post',
    data: data
  })
}

/**
 * 保存或者编辑日志模板
 * @param {*} data
 */
export function oaLogTemplateAddAPI(data) {
  return request({
    url: 'oaLogTemplate/addTemplate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 编辑时获取单个日志模板数据
 * @param {*} data
 * configId
 * status 状态 1 启用 2 停用 3 删除
 */
export function oaLogTemplateDetailAPI(configId) {
  return request({
    url: `oaLogTemplate/detail/${configId}`,
    method: 'post'
  })
}

/** ****************自定义模板***********************/

/**
 * 查询 打印模板列表
 * type 0查询管理员配置的，1查询个人的，2查询全部的
 * @param {*} data
 */
export function oaLogPrintTemplateListAPI(data) {
  return request({
    url: 'oaLogPrintTemplate/queryList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除指定打印模板
 * templateId
 * @param {*} data
 */
export function oaLogPrintDeleteTemplateAPI(data) {
  return request({
    url: 'oaLogPrintTemplate/delete',
    method: 'post',
    data: data
  })
}

/**
 * 模板复制
 * type
 * @param {*} data
 */
export function oaLogPrintCopyTemplateAPI(data) {
  return request({
    url: 'oaLogPrintTemplate/copyTemplate',
    method: 'post',
    data: data
  })
}

/**
 * 新增 打印模板
 * @param {*} data
 */
export function oaLogPrintTemplateAddAPI(data) {
  return request({
    url: 'oaLogPrintTemplate/add',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 编辑 打印模板
 * @param {*} data
 */
export function oaLogPrintTemplateUpdateAPI(data) {
  return request({
    url: 'oaLogPrintTemplate/update',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询字段
 * @param {*} data
 */
export function oaLogPrintTemplateFieldsAPI(data) {
  return request({
    url: 'oaLogPrintTemplate/queryFields',
    method: 'post',
    data: data
  })
}

/**
 * 跟进ID查询打印模板
 * @param {*} data
 */
export function oaLogPrintTemplateQueryByIdAPI(data) {
  return request({
    url: 'oaLogPrintTemplate/queryById',
    method: 'post',
    data: data
  })
}

/**
 * 打印
 * type
 * @param {*} data
 */
export function oaLogPrintTemplatePrintAPI(data) {
  return request({
    url: 'oaLogPrintTemplate/print',
    method: 'post',
    data: data
  })
}

/**
 * 打印预览
 * type
 * @param {*} data
 */
export function oaLogPrintTemplatePreviewAPI(data) {
  return request({
    url: 'oaLogPrintTemplate/preview',
    method: 'post',
    data: data
  })
}

/**
 * 预览下载
 * @param {*} data
 */
export function oaLogPrintTemplateDownAPI(data) {
  return request({
    url: 'oaLogPrintTemplate/down',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}
