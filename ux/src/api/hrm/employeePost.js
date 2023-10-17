/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-14 13:58:52
 * @LastEditTime: 2020-07-16 14:41:12
 * @LastEditors: yang
 */
import request from '@/utils/request'

/**
 * 岗位信息
 * @param {*} data
 */
export function hrmEmployeePostQueryByIdAPI(id) {
  return request({
    url: `hrmEmployeePost/postInformation/${id}`,
    method: 'post'
  })
}

/**
 * 办理离职
 * @param {*} data
 */
export function hrmEmployeePostAddLeaveAPI(data) {
  return request({
    url: 'hrmEmployeePost/addLeaveInformation',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改离职信息
 * @param {*} data
 */
export function hrmEmployeePostSetLeaveAPI(data) {
  return request({
    url: 'hrmEmployeePost/setLeaveInformation',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改岗位信息
 * @param {*} data
 */
export function hrmEmployeePostUpdatePostAPI(data) {
  return request({
    url: 'hrmEmployeePost/updatePostInformation',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 岗位信息
 * @param {*} data
 */
export function hrmEmployeePostDeleteLeaveAPI(data) {
  return request({
    url: 'hrmEmployeePost/deleteLeaveInformation',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询招聘渠道列表
 * @param {*} data
 */
export function hrmRecruitChannelQueryAPI() {
  return request({
    url: 'hrmRecruitChannel/queryRecruitChannelList',
    method: 'post'
  })
}
