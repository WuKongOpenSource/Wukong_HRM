import request from '@/utils/request'

/**
 * 报表统计
 * @param {*} data
 */
export function hrmAttendanceClockReportAPI(data) {
  return request({
    url: 'hrm/attendance/clockReport',
    method: 'post',
    data: data
  })
}

/**
 * 报表详情
 * @param {*} data
 */
export function hrmAttendanceDetailsReportAPI(data) {
  return request({
    url: 'hrm/attendance/detailsReport',
    method: 'post',
    data: data
  })
}

/**
 * 打卡统计
 * @param {*} data
 */
export function hrmAttendanceClockCardingAPI(data) {
  return request({
    url: 'hrm/attendance/clockCarding',
    method: 'post',
    data: data
  })
}

/**
 * 打卡详情/打卡日历
 * @param {*} data
 */
export function hrmAttendanceClockDetailsAPI(data) {
  return request({
    url: 'hrm/attendance/clockDetails',
    method: 'post',
    data: data
  })
}
