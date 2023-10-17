/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-14 13:58:52
 * @LastEditTime: 2020-07-01 10:02:41
 * @LastEditors: yang
 */
import request from '@/utils/request'

/**
 * 部门列表
 * @param {*} data
 */
export function hrmDeptQueryTreeListAPI(data) {
  return request({
    url: 'hrmDept/queryTreeList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 部门新建
 * @param {*} data
 */
export function hrmDeptAddDeptAPI(data) {
  return request({
    url: 'hrmDept/addDept',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 部门部门
 * @param {*} data
 */
export function hrmDeptSetDeptAPI(data) {
  return request({
    url: 'hrmDept/setDept',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询部门详情
 * @param {*} data
 */
export function hrmDeptQueryByIdAPI(id) {
  return request({
    url: `hrmDept/queryById/${id}`,
    method: 'post'
  })
}

/**
 * 查询部门删除
 * @param {*} data
 */
export function hrmDeptDeleteByIdAPI(id) {
  return request({
    url: `hrmDept/deleteDeptById/${id}`,
    method: 'post'
  })
}

/**
 * 通过部门id查询员工列表
 * @param {*} data
 */
export function hrmDeptQueryEmployeeAPI(data) {
  return request({
    url: 'hrmDept/queryEmployeeByDeptId',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
