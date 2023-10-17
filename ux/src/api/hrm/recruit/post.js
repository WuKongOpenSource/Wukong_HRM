/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-06-04 10:40:32
 * @LastEditTime: 2020-07-01 10:02:16
 * @LastEditors: yang
 */
import request from '@/utils/request'

/**
 * 详情
 * @param {*} data
 */
export function hrmRecruitPostQueryByIdAPI(id) {
  return request({
    url: `hrmRecruitPost/queryById/${id}`,
    method: 'post'
  })
}

/**
 * 查询所有职位列表(表单使用)
 * @param {*} data
 */
export function hrmRecruitPostQuerAllListAPI() {
  return request({
    url: 'hrmRecruitPost/queryAllRecruitPostList',
    method: 'post'
  })
}

/**
 * 数量
 * @param {*} data
 */
export function hrmRecruitPostQueryNumAPI(id) {
  return request({
    url: 'hrmRecruitPost/queryPostStatusNum',
    method: 'post'
  })
}

/**
 * 列表
 * @param {*} data
 */
export function hrmRecruitPostQueryListAPI(data) {
  return request({
    url: 'hrmRecruitPost/queryRecruitPostPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 添加
 * @param {*} data
 */
export function hrmRecruitPostAddAPI(data) {
  return request({
    url: 'hrmRecruitPost/addRecruitPost',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改
 * @param {*} data
 */
export function hrmRecruitPostSetAPI(data) {
  return request({
    url: 'hrmRecruitPost/setRecruitPost',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 状态
 * @param {*} data
 */
export function hrmRecruitPostUpdateStatusAPI(data) {
  return request({
    url: 'hrmRecruitPost/updateRecruitPostStatus',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询职位类型
 * @param {*} data
 */
export function hrmRecruitPostTypeQueryAPI() {
  return request({
    url: 'hrmRecruitPostType/queryPostType',
    method: 'post'
  })
}
