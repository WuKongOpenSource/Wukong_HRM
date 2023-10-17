/**
 * Create by yxk at 2020/6/22
 */
import request from '@/utils/request'

/**
 * 进行审批
 */
export function crmExamineRecordAuditAPI(data) {
  return request({
    url: 'examineRecord/auditExamine',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 流程管理员进行审批
 */
export function crmExamineRecordAdminAuditAPI(data) {
  return request({
    url: 'examineRecord/adminAuditExamine',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  审批终止
 * @param {*} data
 */
export function examineEndAPI(data) {
  return request({
    url: '/examineSuperExamine/examine/end',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询审批记录
 */
export function crmExamineRecordLogListAPI(data) {
  return request({
    url: 'examineRecord/queryExamineRecordLog',
    method: 'post',
    data: data
  })
}

/**
 * 查询审批流程列表
 */
export function crmExamineRecordRecordListAPI(data) {
  return request({
    url: 'crmExamineRecord/queryExamineRecordList',
    method: 'post',
    data: data
  })
}

/**
 * 查询审批可供设置的字段
 * @param {*} data
 *
 */
export function examinesQueryFieldAPI(data) {
  return request({
    url: 'examines/queryField',
    method: 'post',
    data: data
  })
}

/**
 * 保存审批数据
 * @param {*} data
 */
export function examinesAddAPI(data) {
  return request({
    url: 'examines/addExamine',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询审批列表
 * @param {*} data
 */
export function examinesQueryListAPI(data) {
  return request({
    url: `examines/queryList/${data.label}`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改审批状态
 * @param {*} data
 */
export function examinesUpdateStatusAPI(data) {
  return request({
    url: 'examines/updateStatus',
    method: 'post',
    data: data
  })
}

/**
 * 审批详情
 * @param {*} data
 */
export function examinesQueryExamineFlowAPI(data) {
  return request({
    url: 'examines/queryExamineFlow',
    method: 'post',
    data: data
  })
}

/**
 * 获取审批流程有效字段
 * @param {*} data
 */
export function examinesPreviewFiledNameAPI(data) {
  return request({
    url: 'examines/previewFiledName',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 编辑获取审批流程
 * @param {*} data
 */
export function examinesDraftFiledNameAPI(data) {
  return request({
    url: 'oaExamine/getOaExamineDraft',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * 预览审批流程
 * @param {*} data
 */
export function examinesPreviewExamineFlowAPI(data) {
  return request({
    url: 'examines/previewExamineFlow',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询审批配置
 * @param {*} data
 */
export function examinesQueryAdvancedConfigAPI(data) {
  return request({
    url: 'examines/queryExamineAdvancedConfig',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 获取审批详情
 */
export function examineRecordQueryListAPI(data) {
  return request({
    url: 'examineRecord/queryExamineRecordList',
    method: 'post',
    data: data
  })
}

/**
 * 查询正常审批流列表
 * @param {*} data
 */
export function examinesQueryPartListAPI(data) {
  return request({
    url: 'examines/queryPartList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询全部审批流列表
 * @param {*} data
 */
export function examineWaitingQueryCrmExamineListAPI(data) {
  return request({
    url: 'examineWaiting/queryCrmExamineList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询全部审批流列表
 * @param {*} data
 */
export function examineWaitingQueryOaExamineListAPI(data) {
  return request({
    url: 'examineWaiting/queryOaExamineList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 进行审批(待办批量)
 * @param {*} data
 */
export function examineRecordBatchAuditAPI(data) {
  return request({
    url: 'examineRecord/batchAuditExamine',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  查询审批人员控件字段
 * @param {*} data
 */
export function examinesQueryExamineUserFieldAPI(data) {
  return request({
    url: `/examines/queryExamineUserField`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询审批人拒绝审批返回节点（拒绝节点）
 * @param {*} data
 */
export function examineRecordQueryRecordLogAPI(data) {
  return request({
    url: 'examineRecord/queryRecordLog',
    method: 'post',
    data: data
  })
}

/**
 * 管理员跳过预览审批流程节点
 * @param {*} data
 */
export function examineRecordQueryAdminRecordLogAPI(data) {
  return request({
    url: 'examineRecord/adminQueryExamineFlow',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  查询权限字段
 * @param {*} data
 */
export function examineRecordQueryAuthFieldAPI(data) {
  return request({
    url: `examineRecord/queryAuthField`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  查询更新数据日志
 * @param {*} data
 */
export function examineRecordQueryUpdateDataLogAPI(data) {
  return request({
    url: `examineRecord/queryExamineUpdateLog`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
