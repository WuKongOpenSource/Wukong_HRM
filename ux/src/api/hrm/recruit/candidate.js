/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-06-06 11:02:48
 * @LastEditTime: 2020-07-01 10:01:19
 * @LastEditors: yang
 */
import request from '@/utils/request'

/**
 * 详情
 * @param {*} data
 */
export function hrmRecruitCandidateQueryByIdAPI(id) {
  return request({
    url: `hrmRecruitCandidate/queryById/${id}`,
    method: 'post'
  })
}

/**
 * 删除
 * @param {*} data
 */
export function hrmRecruitCandidateDeleteByIdAPI(id) {
  return request({
    url: `hrmRecruitCandidate/deleteById/${id}`,
    method: 'post'
  })
}

/**
 * 删除
 * @param {*} data
 */
export function hrmRecruitCandidateDeleteByIdsAPI(data) {
  return request({
    url: 'hrmRecruitCandidate/deleteByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 数量
 * @param {*} data
 */
export function hrmRecruitCandidateQueryNumAPI(id) {
  return request({
    url: 'hrmRecruitCandidate/queryCandidateStatusNum',
    method: 'post'
  })
}

/**
 * 列表
 * @param {*} data
 */
export function hrmRecruitCandidateQueryListAPI(data) {
  return request({
    url: 'hrmRecruitCandidate/queryPageList',
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
export function hrmRecruitCandidateAddAPI(data) {
  return request({
    url: 'hrmRecruitCandidate/addCandidate',
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
export function hrmRecruitCandidateSetAPI(data) {
  return request({
    url: 'hrmRecruitCandidate/setCandidate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询一键清理候选人,查询完之后调用修改状态接口
 * @param {*} data
 */
export function hrmRecruitCandidateQueryCleanDataAPI(data) {
  return request({
    url: 'hrmRecruitCandidate/queryCleanCandidateIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 批量修改候选人职位
 * @param {*} data
 */
export function hrmRecruitCandidateUpdatePostAPI(data) {
  return request({
    url: 'hrmRecruitCandidate/updateCandidatePost',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 批量修改候选人招聘渠道
 * @param {*} data
 */
export function hrmRecruitCandidateUpdateChannelAPI(data) {
  return request({
    url: 'hrmRecruitCandidate/updateCandidateRecruitChannel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 批量修改候选人状态
 * @param {*} data
 */
export function hrmRecruitCandidateUpdateStatusAPI(data) {
  return request({
    url: 'hrmRecruitCandidate/updateCandidateStatus',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 淘汰/流失候选人
 * @param {*} data
 */
export function hrmRecruitCandidateEliminateAPI(data) {
  return request({
    url: 'hrmRecruitCandidate/eliminateCandidate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 安排面试
 * @param {*} data
 */
export function hrmRecruitInterviewAddAPI(data) {
  return request({
    url: 'hrmRecruitInterview/addInterview',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 安排面试
 * @param {*} data
 */
export function hrmRecruitInterviewAddBatchAPI(data) {
  return request({
    url: 'hrmRecruitInterview/addBatchInterview',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 填写面试结果
 * @param {*} data
 * result 面试情况 1面试未完成 2面试通过 3面试未通过 4 面试取消
 */
export function hrmRecruitInterviewSetResultAPI(data) {
  return request({
    url: 'hrmRecruitInterview/setInterviewResult',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询候选人附件
 * @param {*} data
 */
export function hrmRecruitCandidateQueryFileAPI(id) {
  return request({
    url: `hrmRecruitCandidate/queryFile/${id}`,
    method: 'post'
  })
}
