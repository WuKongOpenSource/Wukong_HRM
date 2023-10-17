/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-06-24 09:47:30
 * @LastEditTime: 2020-06-24 11:36:26
 * @LastEditors: 悟空
 */
import request from '@/utils/request'

// ============================================
// === 推荐悟空云
// ============================================

export function getIntegralOrInvitationListAPI(data) {
  return request({
    url: 'adminIntegralRecord/queryPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function getInvitationCodeAPI(data) {
  return request({
    url: 'adminCompanyManager/queryByCompanyId',
    method: 'post',
    data: data
  })
}

export function getCurrentIntegralAPI(data) {
  return request({
    url: 'adminCompanyManager/queryAdminCompanyManagerByCompanyId',
    method: 'post',
    data: data
  })
}

// ============================================
// ===
// ============================================

export function crmGetCodeNumAPI(data) {
  return request({
    url: 'adminCompanyMarketing/getCode',
    method: 'post',
    data: data
  })
}

/**
 * 查询积分中心列表
 * @param {*} data
 */
export function crmGetProductListAPI(data) {
  return request({
    url: 'adminCompanyMarketing/queryProduct',
    method: 'post',
    data: data
  })
}

/**
 * 兑换
 */
export function crmChangeIntegralAPI(data) {
  return request({
    url: 'adminCompanyMarketing/exchange',
    method: 'post',
    data: data
  })
}

/**
 * 兑换历史列表
 */
export function crmExchangeHistoryAPI(data) {
  return request({
    url: 'adminCompanyMarketing/exchangeHistory',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 邀请记录
 */
export function crmPleaseRecordAPI(data) {
  return request({
    url: 'adminCompanyMarketing/history',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

