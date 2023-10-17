import request from '@/utils/request'

/**
 *  报表---导出
 * @param {*} data
 */
export function examineSuperExaminesGetExamineReporExportAPI(data) {
  return request({
    url: `/examineSuperExamines/getExamineReporExport`,
    method: 'post',
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 *  报表---统计头信息
 * @param {*} data
 */
export function examineSuperExaminesGetExamineReportInfoAPI(data) {
  return request({
    url: `/examineSuperExamines/getExamineReportInfo`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  报表---列表信息
 * @param {*} data
 */
export function examineSuperExaminesGetExamineReportListAPI(data) {
  return request({
    url: `/examineSuperExamines/getExamineReportList`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  报表---列表 数值列表
 * @param {*} data
 */
export function getExamineReportSubListAPI(data) {
  return request({
    url: '/examineSuperExamines/getExamineReportSubList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  首页搜索
 * @param {*} data
 */
export function superExamineByNameAPI(data) {
  return request({
    url: '/examineSuperExamines/querySuperExamineInfoByName',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  首页申请卡片列表
 * @param {*} data
 */
export function superExaminesRecentlyUsedListAPI(data) {
  return request({
    url: '/examineSuperExamines/recentlyUsedExamineList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 *  首页待办卡片列表
 * @param {*} data
 */
export function superExaminesBackLogAPI(data) {
  return request({
    url: '/examineSuperExamines/queryBackLogInfo',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 *  首页跟踪卡片列表
 * @param {*} data
 */
export function superExaminesTraceInfoAPI(data) {
  return request({
    url: '/examineSuperExamines/queryTraceInfo',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 *  申请列表
 * @param {*} data
 */
export function superExaminesApplyListAPI(data) {
  return request({
    url: '/examineSuperExamines/applyList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 *  申请列表搜索
 * @param {*} data
 */
export function superExaminesByNameAPI(data) {
  return request({
    url: '/examineSuperExamines/querySuperExamineByName',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  待办列表(抄送给我)
 * @param {*} data
 */
export function superExamineCopyMeAPI(data) {
  return request({
    url: '/examineSuperExamine/copy/me',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  待办列表(待我审批)
 * @param {*} data
 */
export function superExamineTodoMeAPI(data) {
  return request({
    url: '/examineSuperExamine/todo/me',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  归档列表(我的申请)
 * @param {*} data
 */
export function superExamineEndMeAPI(data) {
  return request({
    url: '/examineSuperExamine/end/me',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  归档列表(经我审批)
 * @param {*} data
 */
export function superExamineEndAuditAPI(data) {
  return request({
    url: '/examineSuperExamine/end/audit',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  归档列表(我的关注)
 * @param {*} data
 */
export function superExamineEndFollowAPI(data) {
  return request({
    url: '/examineSuperExamine/end/follow',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  归档列表(作废)
 * @param {*} data
 */
export function superExamineEndTovoidAPI(data) {
  return request({
    url: '/examineSuperExamine/end/tovoid',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  归档列表(抄送给我的)
 * @param {*} data
 */
export function superExamineEndCopyAPI(data) {
  return request({
    url: '/examineSuperExamine/end/copy',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  跟踪列表(我申请的)
 * @param {*} data
 */
export function superExamineTrackMeAPI(data) {
  return request({
    url: '/examineSuperExamine/track/me',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  跟踪列表(经我审批)
 * @param {*} data
 */
export function superExamineTrackDoAPI(data) {
  return request({
    url: '/examineSuperExamine/track/do',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  跟踪列表(抄送给我的)
 * @param {*} data
 */
export function superExamineTrackCopyAPI(data) {
  return request({
    url: '/examineSuperExamine/track/copy',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  草稿列表
 * @param {*} data
 */
export function superExamineDraftAPI(data) {
  return request({
    url: '/oaSuperExamine/draft',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  审批流列表
 * @param {*} data
 */
export function superExamineRecordAPI(data) {
  return request({
    url: '/examineSuperExamine/examine/record/list',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 *  审批流转他人处理
 * @param {*} data
 */
export function forwardOthersAPI(data) {
  return request({
    url: '/examineRecord/forwardOthers',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  审批流征求他人意见
 * @param {*} data
 */
export function solicitOthersAPI(data) {
  return request({
    url: '/examineRecord/solicitOthers',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  邮件催办-----进行发送邮件
 * @param {*} data
 */
export function useExamineEmailSendAPI(data) {
  return request({
    url: '/examineSuperExamines/useExamineEmailSend',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 *  审批归档/作废
 * @param {*} data
 */
export function actionExamineAPI(data) {
  return request({
    url: '/oaSuperExamine/actionExamine',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  修改关注状态
 * @param {*} data
 */
export function changeFollowAPI(data) {
  return request({
    url: '/examineFollow/change/follow',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  审批申请排序
 * @param {*} data
 */
export function saveOrUpdateOaExamineSortAPI(data) {
  return request({
    url: '/oaExamineCategory/group/saveOrUpdateOaExamineSort',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  代办待我审批导出
 * @param {*} data
 */
export function exportExamineDoMeInfoAPI(data) {
  return request({
    url: '/examineSuperExamine/exportExamineDoMeInfo',
    method: 'post',
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 *  代办抄送给我导出
 * @param {*} data
 */
export function exportExamineCopyMeInfoAPI(data) {
  return request({
    url: '/examineSuperExamine/exportExamineCopyMeInfo',
    method: 'post',
    responseType: 'blob',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 跟踪我的申请导出
 * @param {*} data
 */
export function exportExamineTrackMeInfoAPI(data) {
  return request({
    url: '/examineSuperExamine/exportExamineTrackMeInfo',
    method: 'post',
    responseType: 'blob',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 跟踪经我审批导出
 * @param {*} data
 */
export function exportExamineTrackDoInfoAPI(data) {
  return request({
    url: '/examineSuperExamine/exportExamineTrackDoInfo',
    method: 'post',
    responseType: 'blob',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 跟踪抄送我的导出
 * @param {*} data
 */
export function exportExamineTrackCopyInfoAPI(data) {
  return request({
    url: '/examineSuperExamine/exportExamineTrackCopyInfo',
    method: 'post',
    responseType: 'blob',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 归档我申请的导出
 * @param {*} data
 */
export function exportExamineEenMeInfoAPI(data) {
  return request({
    url: '/examineSuperExamine/exportExamineEenMeInfo',
    method: 'post',
    responseType: 'blob',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 归档经我审批导出
 * @param {*} data
 */
export function exportExamineEenAuditInfoAPI(data) {
  return request({
    url: '/examineSuperExamine/exportExamineEenAuditInfo',
    method: 'post',
    responseType: 'blob',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 归档我的关注导出
 * @param {*} data
 */
export function exportExamineEenFollowInfoAPI(data) {
  return request({
    url: '/examineSuperExamine/exportExamineEenFollowInfo',
    method: 'post',
    responseType: 'blob',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 归档作废导出
 * @param {*} data
 */
export function exportExamineEenTovoIdInfoAPI(data) {
  return request({
    url: '/examineSuperExamine/exportExamineEenTovoIdInfo',
    method: 'post',
    responseType: 'blob',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 归档抄送我的导出
 * @param {*} data
 */
export function exportExamineEenCopyInfoAPI(data) {
  return request({
    url: '/examineSuperExamine/exportExamineEenCopyInfo',
    method: 'post',
    responseType: 'blob',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 获取审批导出字段
 * @param {*} data
 */
export function getSuperExamineExportFieldAPI(data) {
  return request({
    url: `/examineSuperExamine/getExportField/${data.categoryId}`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

/**
 * 设置节点自选信息
 * @param {*} data
 */
export function examineSuperExaminesSetNodeOptionalAPI(data) {
  return request({
    url: `/examineSuperExamines/set/node/optional`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  查询人员控件
 * @param {*} data
 */
export function oaExamineFieldQueryUserFieldAPI(data) {
  return request({
    url: `/oaExamineField/queryUserField/${data.categoryId}`,
    method: 'post',
    data: data
  })
}

/**
 *  查询jxc人员控件
 * @param {*} data
 */
export function jxcFieldQueryUserFieldAPI(data) {
  return request({
    url: `/jxcField/queryUserField/${data.label}`,
    method: 'post',
    data: data
  })
}

/**
 *  查询OA 审批字段
 * @param {*} data
 */
export function oaExamineFieldQueryFieldAPI(categoryId) {
  return request({
    url: `/oaExamineField/queryFieldList/${categoryId}`,
    method: 'post'
  })
}

/**
 *  查询进销存审批字段
 * @param {*} data
 */
export function jxcFieldQueryFieldListAPI(label) {
  return request({
    url: `/jxcField/queryFieldList/${label}`,
    method: 'post'
  })
}

/**
 *  查询CRM审批字段信息
 * @param {*} data
 */
export function crmFieldQueryFieldAPI(label) {
  return request({
    url: `/crmField/queryFieldList/${label}`,
    method: 'post'
  })
}
