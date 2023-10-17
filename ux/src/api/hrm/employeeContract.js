/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-14 13:58:52
 * @LastEditTime: 2020-07-01 10:14:41
 * @LastEditors: yang
 */
import request from '@/utils/request'

/**
 * 合同基本信息
 * @param {*} data
 */
export function hrmEmployeeContractInformationAPI(id) {
  return request({
    url: `hrmEmployeeContract/contractInformation/${id}`,
    method: 'post'
  })
}

/**
 * 添加合同
 * @param {*} data
 */
export function hrmEmployeeContractaddContractAPI(data) {
  return request({
    url: 'hrmEmployeeContract/addContract',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改合同
 * @param {*} data
 */
export function hrmEmployeeContractSetContractAPI(data) {
  return request({
    url: 'hrmEmployeeContract/setContract',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除合同
 * @param {*} data
 */
export function hrmEmployeeContractDeleteAPI(id) {
  return request({
    url: `hrmEmployeeContract/deleteContract/${id}`,
    method: 'post'
  })
}
