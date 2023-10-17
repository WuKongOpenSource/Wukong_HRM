/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-14 13:58:52
 * @LastEditTime: 2020-07-04 17:45:54
 * @LastEditors: yang
 */
import request from '@/utils/request'

/**
 * 工资社保基本信息
 * @param {*} data
 */
export function hrmEmployeeSocialSecurityQueryByIdAPI(id) {
  return request({
    url: `hrmEmployee/SocialSecurity/salarySocialSecurityInformation/${id}`,
    method: 'post'
  })
}

/**
 * 添加工资卡
 * @param {*} data
 */
export function hrmEmployeeSocialSecurityAddSalaryCardAPI(data) {
  return request({
    url: 'hrmEmployee/SocialSecurity/addSalaryCard',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 添加社保信息
 * @param {*} data
 */
export function hrmEmployeeSocialSecurityAddSocialAPI(data) {
  return request({
    url: 'hrmEmployee/SocialSecurity/addSocialSecurity',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除工资卡
 * @param {*} data
 */
export function hrmEmployeeSocialSecurityDeleteSalaryCardAPI(id) {
  return request({
    url: `hrmEmployee/SocialSecurity/deleteSalaryCard/${id}`,
    method: 'post'
  })
}

/**
 * 删除社保信息
 * @param {*} data
 */
export function hrmEmployeeSocialSecuritDeleteSocialAPI(id) {
  return request({
    url: `hrmEmployee/SocialSecurity/deleteSocialSecurity/${id}`,
    method: 'post'
  })
}

/**
 * 修改工资卡
 * @param {*} data
 */
export function hrmEmployeeSocialSecuritySetSalaryCardAPI(data) {
  return request({
    url: 'hrmEmployee/SocialSecurity/setSalaryCard',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改社保信息
 * @param {*} data
 */
export function hrmEmployeeSocialSecuritySetSocialAPI(data) {
  return request({
    url: 'hrmEmployee/SocialSecurity/setSocialSecurity',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改社保信息
 * @param {*} data
 */
export function hrmEmployeeUpdateInsuranceSchemeAPI(data) {
  return request({
    url: 'hrmEmployee/updateInsuranceScheme',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询薪资列表
 * @param {*} data
 */
export function hrmEmployeeSocialSecuritySalaryListAPI(data) {
  return request({
    url: 'hrmEmployee/SocialSecurity/querySalaryList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询薪资详情
 * @param {*} data
 */
export function hrmEmployeeSocialSecuritySalaryDetailAPI(id) {
  return request({
    url: `hrmEmployee/SocialSecurity/querySalaryDetail/${id}`,
    method: 'post'
  })
}
