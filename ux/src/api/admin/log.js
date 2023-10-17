import request from '@/utils/request'

/**
 * 查询系统登录日志列表页接口
 * @param {*} data
 */
export function biLogQueryLoginLogsAPI(data) {
  return request({
    url: 'biLog/queryLoginLogs',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 登录日志导出
 * @param {*} data
 */
export function biLogExportLoginLogAPI(data) {
  return request({
    url: 'biLog/exportLoginLog',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    timeout: 60000
  })
}

/**
 * 查看用户操作日志接口
 * @param {*} data
 */
export function querySystemLogListAPI(data) {
  return request({
    url: '/biCustomer/queryLogsData',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 用户操作日志导出
 * @param {*} data
 */
export function systemLogExportAPI(data) {
  return request({
    url: '/biCustomer/exportLogsExcel',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    timeout: 60000
  })
}

// 用户操作日志
/**
 * @description: 查询应用
 * @return {*}
 */
export function biLogQueryApplyAPI(data) {
  return request({
    url: 'biLog/queryApply',
    method: 'post',
    data
  })
}

/**
 * @description: 查询操作行为
 * @return {*}
 */
export function biLogQueryBehaviorAPI() {
  return request({
    url: 'biLog/queryBehavior',
    method: 'post'
  })
}

/**
 * @description: 查询操作对象
 * @return {*}
 */
export function biLogQueryOperateObjectAPI(data) {
  return request({
    url: 'biLog/queryOperateObject',
    method: 'post',
    data
  })
}

/**
 * @description: 查询操作日志
 * @param {*} data
 * @return {*}
 */
export function biLogQueryOperationLogsAPI(data) {
  return request({
    url: 'biLog/queryOperationLogs',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

/**
 * 导出操作日志
 * @param {*} data
 */
export function biLogExportOperateLogAPI(data) {
  return request({
    url: 'biLog/exportOperateLog',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    timeout: 60000
  })
}

/**
 * @description: 查询数据导入日志
 * @param {*} data
 * @return {*}
 */
export function biLogQueryImportLogsAPI(data) {
  return request({
    url: 'biLog/queryImportLogs',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

/**
 * @description: 查询数据导出日志
 * @param {*} data
 * @return {*}
 */
export function biLogQueryExportLogsAPI(data) {
  return request({
    url: 'biLog/queryExportLogs',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}
