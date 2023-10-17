import request from '@/utils/request'

/**
 * 分页查询请假记录列表
 * @param {*} data
 */
export function hrmQueryLeaveRecordPageListAPI(data) {
  return request({
    url: 'hrmEmployeeLeaveRecord/queryLeaveRecordPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 获取请假类型
 * @param {*} data
 */
export function hrmEmployeeLeaveRecordQueryLeaveTypeAPI(data) {
  return request({
    url: 'hrmEmployeeLeaveRecord/queryLeaveTypeList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 请假记录导出
 * @param {*} data
 */
export function hrmEmployeeLeaveRecordExportAPI(data) {
  return request({
    url: 'hrmEmployeeLeaveRecord/export',
    method: 'post',
    responseType: 'blob',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
