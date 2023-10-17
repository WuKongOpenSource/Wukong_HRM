import request from '@/utils/request'

/**
 * 薪资字段列表
 * @param {*} data
 */
export function hrmParrollFieldListAPI(data) {
  return request({
    url: 'hrm/parroll/fieldList',
    method: 'post',
    data: data
  })
}

/**
 * 薪资列表表头展示内容
 * @param {*} data
 */
export function hrmParrollReadTitleAPI(data) {
  return request({
    url: 'hrm/parroll/readTitle',
    method: 'post',
    data: data
  })
}

/**
 * 薪资字段添加
 * @param {*} data
 */
export function hrmParrollFieldSaveAPI(data) {
  return request({
    url: 'hrm/parroll/fieldSave',
    method: 'post',
    data: data
  })
}

/**
 * 薪资设置系统事项状态获取
 * @param {*} data
 */
export function hrmParrollSystemFieldAPI(data) {
  return request({
    url: 'hrm/parroll/systemField',
    method: 'post',
    data: data
  })
}

/**
 * 薪资设置系统事项设置
 * @param {*} data
 */
export function hrmParrollSystemFieldSaveAPI(data) {
  return request({
    url: 'hrm/parroll/systemFieldSave',
    method: 'post',
    data: data
  })
}

/**
 * 员工薪资设置保存
 * @param {*} data
 */
export function hrmParrollSaveAPI(data) {
  return request({
    url: 'hrm/parroll/save',
    method: 'post',
    data: data
  })
}

/**
 * 员工薪资 薪资列表 编辑
 * @param {*} data
 */
export function hrmParrollUpdateAPI(data) {
  return request({
    url: 'hrm/parroll/update',
    method: 'post',
    data: data
  })
}

/**
 * 固定工资 编辑
 * @param {*} data
 */
export function hrmParrollSaveForlistAPI(data) {
  return request({
    url: 'hrm/parroll/saveForlist',
    method: 'post',
    data: data
  })
}

/**
 * 员工薪资删除
 * @param {*} data
 */
export function hrmParrollDeleteAPI(data) {
  return request({
    url: 'hrm/parroll/delete',
    method: 'post',
    data: data
  })
}

/**
 * 薪资列表审核
 * @param {*} data
 */
export function hrmParrollCheckParrollAPI(data) {
  return request({
    url: 'hrm/parroll/checkParroll',
    method: 'post',
    data: data
  })
}

/**
 * 薪资列表
 * @param {*} data
 */
export function hrmParrollIndexAPI(data) {
  return request({
    url: 'hrm/parroll/index',
    method: 'post',
    data: data
  })
}

/**
 * 固定薪资设置列表
 * @param {*} data
 */
export function hrmParrollStopfieldListAPI(data) {
  return request({
    url: 'hrm/parroll/stopfieldList',
    method: 'post',
    data: data
  })
}
