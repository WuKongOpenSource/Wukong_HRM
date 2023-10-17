import request from '@/utils/request'

/**
 *  新建分组
 * @param {*} data
 */
export function examineSuperExaminesAddExamineGroupAPI(data) {
  return request({
    url: `/examineSuperExamines/addExamineGroup`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  查询分组
 * @param {*} data
 */
export function examineSuperExaminesQueryExamineGroupAPI(data) {
  return request({
    url: `/examineSuperExamines/queryExamineGroup`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  查询全部审批流列表不带分页
 * @param {*} data
 */
export function examinesQueryAllListAPI(data) {
  return request({
    url: `/examines/queryAllList/${data.type}`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  删除分组
 * @param {*} data
 */
export function examineSuperExaminesDelExamineGroupAPI(data) {
  return request({
    url: `examineSuperExamines/delExamineGroup`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  审批列表 邮件链接🔗审批
 * @param {*} data
 */
export function examineSuperExaminesUseExamineEmailTokenForExamineRecordListAPI(data) {
  return request({
    url: `examineSuperExamines/useExamineEmailTokenForExamineRecordList`,
    method: 'post',
    data: data,
    customConfig: {
      removeToken: true
    }
  })
}

/**
 *  审批事件 邮件链接🔗审批
 * @param {*} data
 */
export function examineSuperExaminesUseExamineEmailTokenForAuditExamineAPI(data) {
  return request({
    url: `examineSuperExamines/useExamineEmailTokenForAuditExamine`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    customConfig: {
      removeToken: true
    }
  })
}

/**
 *  详情上部分 邮件链接🔗审批
 * @param {*} data
 */
export function oaExamineUseExamineEmailTokenForOaExamineInfoAPI(data) {
  return request({
    url: `oaExamine/useExamineEmailTokenForOaExamineInfo`,
    method: 'post',
    data: data,
    customConfig: {
      removeToken: true
    }
  })
}

/**
 *  详情 邮件链接🔗审批
 * @param {*} data
 */
export function oaExamineUseExamineEmailTokenForOaExaminePositionInfoAPI(data) {
  return request({
    url: `oaExamine/useExamineEmailTokenForOaExaminePositionInfo`,
    method: 'post',
    data: data,
    customConfig: {
      removeToken: true
    }
  })
}

/**
 *  审批记录 邮件链接🔗审批
 * @param {*} data
 */
export function examineSuperExaminesUseExamineEmailTokenForExamineRecordLogAPI(data) {
  return request({
    url: `examineSuperExamines/useExamineEmailTokenForExamineRecordLog`,
    method: 'post',
    data: data,
    customConfig: {
      removeToken: true
    }
  })
}

/**
 *  审批记录 crm邮件链接🔗审批列表
 * @param {*} data
 */
export function ExamineEmailTokenForExaminePositionInfoAPI(data) {
  return request({
    url: `/crmField/useExamineEmailTokenForExaminePositionInfo`,
    method: 'post',
    data: data,
    customConfig: {
      removeToken: true
    }
  })
}

/**
 *  审批记录 crm邮件链接🔗审批详情
 * @param {*} data
 */
export function ExamineEmailTokenForExamineInfoAPI(data) {
  return request({
    url: `/crmField/useExamineEmailTokenForExamineInfo`,
    method: 'post',
    data: data,
    customConfig: {
      removeToken: true
    }
  })
}

/**
 *  jxc邮件审批 详情
 * @param {*} data
 */
export function useExamineEmailTokenByIdAPI(data) {
  return request({
    url: `/jxcEmail/useExamineEmailTokenById`,
    method: 'post',
    data: data,
    customConfig: {
      removeToken: true
    }
  })
}

/**
 *  jxc邮件审批 基本信息
 * @param {*} data
 */
export function useExamineEmailTokenForInformationAPI(data) {
  return request({
    url: `/jxcEmail/useExamineEmailTokenForInformation`,
    method: 'post',
    data: data,
    customConfig: {
      removeToken: true
    }
  })
}
