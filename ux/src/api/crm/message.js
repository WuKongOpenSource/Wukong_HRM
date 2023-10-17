import request from '@/utils/request'

/**
 * 待审核合同
 * @param {*} data
 */
export function crmMessageCheckContractAPI(data) {
  return request({
    url: 'crmBackLog/checkContract',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 待审核回款
 * @param {*} data
 */
export function crmMessageCheckReceivablesAPI(data) {
  return request({
    url: 'crmBackLog/checkReceivables',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 今日需联系客户
 * @param {*} data
 */
export function crmMessageTodayCustomerAPI(data) {
  return request({
    url: 'crmBackLog/todayCustomer',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 今日需联系线索
 * @param {*} data
 */
export function crmMessageTodayLeadsAPI(data) {
  return request({
    url: 'crmBackLog/todayLeads',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 今日需联系商机
 * @param {*} data
 */
export function crmMessageTodayBusinessAPI(data) {
  return request({
    url: 'crmBackLog/todayBusiness',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 待跟进线索
 * @param {*} data
 */
export function crmMessageFollowLeadsAPI(data) {
  return request({
    url: 'crmBackLog/followLeads',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 待跟进客户
 * @param {*} data
 */
export function crmMessageFollowCustomerAPI(data) {
  return request({
    url: 'crmBackLog/followCustomer',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 即将到期合同
 * @param {*} data
 */
export function crmMessagEndContractAPI(data) {
  return request({
    url: 'crmBackLog/endContract',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 待回款合同
 * @param {*} data
 */
export function crmMessagRemindreceivablesplanAPI(data) {
  return request({
    url: 'crmBackLog/remindReceivables',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 待办消息数
 * @param {*} data
 */
export function crmMessagNumAPI(data) {
  return request({
    url: 'crmBackLog/num',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 待进入客户池
 * @param {*} data
 */
export function crmMessagRemindCustomerAPI(data) {
  return request({
    url: 'crmBackLog/putInPoolRemind',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 待回访合同
 * @param {*} data
 */
export function crmMessagVisitRemindAPI(data) {
  return request({
    url: 'crmBackLog/returnVisitRemind',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 待审核发票
 * @param {*} data
 */
export function crmMessageCheckInvoiceAPI(data) {
  return request({
    url: 'crmBackLog/checkInvoice',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 全部标为已处理接口
 * @param {*} data
 */
export function crmMessagAllDealAPI(data) {
  return request({
    url: 'crmBackLog/allDeal',
    method: 'post',
    data: data
  })
}

/**
 * 标为已处理接口
 * @param {*} data
 */
export function crmMessagzealByIdAPI(data) {
  return request({
    url: 'crmBackLog/dealById',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 待审核办公审批
 * @param {*} data
 */
export function crmMessagzealCheckOaAPI(data) {
  return request({
    url: 'crmBackLog/checkOa',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 待进入线索池
 * @param {*} data
 */
export function crmMessagRemindPutInLeadsPoolAPI(data) {
  return request({
    url: 'crmBackLog/putInLeadsPoolRemind',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
