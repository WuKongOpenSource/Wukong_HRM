
import request from '@/utils/request'

/**
 * 分页查询打卡记录列表
 * @param {*} data
 */
export function hrmClockQueryPageListAPI(data) {
  return request({
    url: 'hrmAttendanceClock/queryPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 导出月度总汇
 * @param {*} data
 */
export function hrmAttendanceClockExportAPI(data) {
  return request({
    url: 'hrmAttendanceEmpMonthRecord/excelEmpMonthRecordExport',
    method: 'post',
    responseType: 'blob',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询打卡列表
 * @param {*} data
 */
export function hrmQueryAttendanceClockPageListAPI(data) {
  return request({
    url: 'hrmAttendanceClock/queryPageList',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 添加打卡
 * @param {*} data
 */
export function hrmAttendanceClockAddAPI(data) {
  return request({
    url: 'hrmAttendanceClock/add',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改打卡
 * @param {*} data
 */
export function hrmAttendanceClockUpdateAPI(data) {
  return request({
    url: 'hrmAttendanceClock/update',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除打卡
 * @param {*} data
 */
export function hrmAttendanceClockDeleteAPI(data) {
  return request({
    url: `hrmAttendanceClock/deleteByIds`,
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除打卡
 * @param {*} data
 */
export function hrmQueryAttendanceEmpMonthRecordPageListAPI(data) {
  return request({
    url: `hrmAttendanceEmpMonthRecord/queryAttendanceEmpMonthRecordPageList`,
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询单一员工月度明细
 * @param {*} data
 */
export function hrmQueryAttendanceEmpMonthDailyDetailAPI(data) {
  return request({
    url: `hrmAttendanceEmpMonthRecord/queryAttendanceEmpMonthDailyDetail`,
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询员工月度明细列表
 * @param {*} data
 */
export function hrmQueryAttendanceEmpMonthDailyDetailPageListAPI(data) {
  return request({
    url: `hrmAttendanceEmpMonthRecord/queryAttendanceEmpMonthDailyDetailPageList`,
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询员工每日打卡明细
 * @param {*} data
 */
export function hrmQueryAttendanceDailyDetailAPI(data) {
  return request({
    url: `hrmAttendanceClock/queryAttendanceDailyDetail`,
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询加班记录列表
 * @param {*} data
 */
export function hrmQueryOverTimeRecordPageListAPI(data) {
  return request({
    url: `hrmEmployeeOverTimeRecord/queryOverTimeRecordPageList`,
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询外勤打卡列表
 * @param {*} data
 */
export function hrmQueryOutCardPageListAPI(data) {
  return request({
    url: `hrmAttendanceClock/queryOutCardPageList`,
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 导出打卡记录原始记录
 * @param {*} data
 */
export function hrmAttendanceClockExcelExportAPI(data) {
  return request({
    url: 'hrmAttendanceClock/excelExport',
    method: 'post',
    responseType: 'blob',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端导出个人月度明细
 * @param {*} data
 */
export function excelEmpMonthDailyDetailExportAPI(data) {
  return request({
    url: 'hrmAttendanceEmpMonthRecord/excelEmpMonthDailyDetailExport',
    method: 'post',
    responseType: 'blob',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 获取当日打卡详情
 * @param {*} data
 */
export function hrmAttendanceClockQueryCurrentAttendShiftTimeAPI(data) {
  return request({
    url: 'hrmAttendanceClock/queryCurrentAttendShiftTime',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 根据权限获取人员部门列表
 * @param {*} data
 */
export function hrmEmployeeQueryInspectionDeptEmployeeListAPI(data) {
  return request({
    url: `/hrmEmployee/queryInspectionDeptEmployeeList/${data}`,
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 根据权限获取人员部门列表搜索数据
 * @param {*} data
 */
export function hrmEmployeeQueryAttendanceAllEmployeeListAPI(data) {
  return request({
    url: `hrmEmployee/queryAttendanceAllEmployeeList`,
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
