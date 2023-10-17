/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
 */

// 社保
import request from '@/utils/request'

/**
 * 查询社保统计数据列表
 * @param {*} data
 */
export function hrmInsuranceMonthRecordQueryAPI(data) {
  return request({
    url: 'hrmInsuranceMonthRecord/queryInsuranceRecordList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询社保数据列表
 * @param {*} data
 */
export function hrmInsuranceMonthRecordQueryListAPI(data) {
  return request({
    url: 'hrmInsuranceMonthRecord/queryInsurancePageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 核算社保数据
 * @param {*} data
 */
export function hrmInsuranceMonthRecordComputeDataAPI() {
  return request({
    url: 'hrmInsuranceMonthRecord/computeInsuranceData',
    method: 'post'
  })
}

/**
 * 查询社保详情统计数据(详情统计)
 * @param {*} data
 */
export function hrmInsuranceMonthRecordQueryDetailAPI(id) {
  return request({
    url: `hrmInsuranceMonthRecord/queryInsuranceRecordList/${id}`,
    method: 'post'
  })
}

/**
 * 停止参保
 * @param {*} data
 */
export function hrmInsuranceMonthEmpRecordStopAPI(data) {
  return request({
    url: 'hrmInsuranceMonthEmpRecord/stop',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询员工社保详情
 * @param {*} data
 */
export function hrmInsuranceMonthEmpRecordQueryAPI(id) {
  return request({
    url: `hrmInsuranceMonthEmpRecord/queryById/${id}`,
    method: 'post'
  })
}

/**
 * 修改参保方案项目
 * @param {*} data
 */
export function hrmInsuranceMonthEmpRecordUpdateProjectAPI(data) {
  return request({
    url: 'hrmInsuranceMonthEmpRecord/updateInsuranceProject',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 添加参保人员
 * @param {*} data
 */
export function hrmInsuranceMonthEmpRecordAddInsuranceEmpAPI(data) {
  return request({
    url: 'hrmInsuranceMonthEmpRecord/addInsuranceEmp',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 批量修改参保方案项目
 * @param {*} data
 */
export function hrmInsuranceMonthEmpRecordBatchUpdateInsuranceAPI(data) {
  return request({
    url: 'hrmInsuranceMonthEmpRecord/batchUpdateInsuranceProject',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询本月没有参保人员
 * @param {*} data
 */
export function hrmInsuranceMonthEmpRecordQueryNoInsuranceEmpAPI(data) {
  return request({
    url: `hrmInsuranceMonthEmpRecord/queryNoInsuranceEmp/${data.irecordId}`,
    method: 'post'
  })
}

/**
 * 删除社保记录
 * @param {*} data
 */
export function hrmInsuranceMonthRecordDeleteAPI(irecordId) {
  return request({
    url: `hrmInsuranceMonthRecord/deleteInsurance/${irecordId}`,
    method: 'post'
  })
}

/**
 * 查询社保比例(调用社保100)
 * @param {*} data
 */
export function hrmInsuranceSchemeScaleAPI(data) {
  return request({
    url: 'hrmInsuranceScheme/queryInsuranceScale',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询社保类型(调用社保100)
 * @param {*} data
 */
export function hrmInsuranceSchemeTypeAPI(data) {
  return request({
    url: 'hrmInsuranceScheme/queryInsuranceType',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 我的社保详情
 * @param {*} data
 */
export function hrmInsuranceMonthEmpRecordMyDetailAPI(iempRecordId) {
  return request({
    url: `hrmInsuranceMonthEmpRecord/myInsuranceDetail/${iempRecordId}`,
    method: 'post'
  })
}

/**
 * 我的社保
 * @param {*} data
 */
export function hrmInsuranceMonthEmpRecordMyAPI(data) {
  return request({
    url: 'hrmInsuranceMonthEmpRecord/myInsurance',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
