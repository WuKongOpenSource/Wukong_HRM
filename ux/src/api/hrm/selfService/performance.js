/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: chen
 */
import request from '@/utils/request'

/**
 * 查询绩效档案数量
 * @param {*} data
 */
export function hrmPerformanceEmployeeQueryNumAPI() {
  return request({
    url: 'hrmAppraisalEmployee/queryPendingStageNum',
    method: 'post'
  })
}

/**
 * 查询我的绩效
 * @param {*} data
 */
export function hrmPerformanceEmployeeMyListAPI(data) {
  return request({
    url: 'hrmAchievementEmployeeAppraisal/queryMyAppraisal',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询结果确认列表
 * @param {*} data
 */
export function hrmPerformanceEmployeeResultListAPI(data) {
  return request({
    url: 'hrmAchievementEmployeeAppraisal/queryResultConfirmList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询目标确认列表
 * @param {*} data
 */
export function hrmPerformanceEmployeeTargetListAPI(data) {
  return request({
    url: 'hrmAchievementEmployeeAppraisal/queryTargetConfirmList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询结果评定列表
 * @param {*} data
 */
export function hrmPerformanceEmployeeEvaluatoListAPI(data) {
  return request({
    url: 'hrmAchievementEmployeeAppraisal/queryEvaluatoList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询考核详情
 * @param {*} data
 */
export function hrmPerformanceEmployeeAppraisalDetailAPI(id) {
  return request({
    url: `hrmAchievementEmployeeAppraisal/queryEmployeeAppraisalDetail/${id}`,
    method: 'post'
  })
}

/**
 * 填写绩效
 * @param {*} data
 */
export function hrmPerformanceEmployeeWriteAPI(data) {
  return request({
    url: 'hrmAchievementEmployeeAppraisal/writeAppraisal',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改目标进度
 * @param {*} data
 */
export function hrmPerformanceEmployeeUpdateScheduleAPI(data) {
  return request({
    url: 'hrmAchievementEmployeeAppraisal/updateSchedule',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 目标确认
 * @param {*} data
 */
export function hrmPerformanceEmployeeTargetConfirmAPI(data) {
  return request({
    url: 'hrmAchievementEmployeeAppraisal/targetConfirm',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 结果评定
 * @param {*} data
 */
export function hrmPerformanceEmployeeResultEvaluatoAPI(data) {
  return request({
    url: 'hrmAchievementEmployeeAppraisal/resultEvaluato',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改考评分数
 * @param {*} data
 */
export function hrmPerformanceEmployeeUpdateScoreLevelAPI(data) {
  return request({
    url: 'hrmAchievementEmployeeAppraisal/updateScoreLevel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询分数配置列表 绩效档案id
 * @param {*} data
 */
export function hrmPerformanceEmployeeQueryLevelListAPI(id) {
  return request({
    url: `hrmAchievementAppraisalScoreLevel/queryScoreLevelList/${id}`,
    method: 'post'
  })
}

/**
 * 查询分数配置列表 绩效id
 * @param {*} data
 */
export function hrmPerformanceEmployeeQueryLevelByAppraisalIdAPI(id) {
  return request({
    url: `hrmAchievementAppraisalScoreLevel/queryScoreLevelListByAppraisalId/${id}`,
    method: 'post'
  })
}

/**
 * 查询绩效结果确认
 * @param {*} data
 */
export function hrmPerformanceEmployeeQueryResultConfirmAPI(id) {
  return request({
    url: `hrmAchievementEmployeeAppraisal/queryResultConfirmByAppraisalId/${id}`,
    method: 'post'
  })
}

/**
 * 绩效结果确认
 * @param {*} data
 */
export function hrmPerformanceEmployeeResultConfirmAPI(id) {
  return request({
    url: `hrmAchievementEmployeeAppraisal/resultConfirm/${id}`,
    method: 'post'
  })
}

/**
 * 查询目标确认列表的绩效筛选条件
 * @param {*} data
 */
export function hrmPEQueryTargetConfirmScreenAPI() {
  return request({
    url: 'hrmAchievementEmployeeAppraisal/queryTargetConfirmScreen',
    method: 'post'
  })
}

/**
 * 查询结果评定列表的绩效筛选条件
 * @param {*} data
 */
export function hrmPEQueryEvaluatoScreenAPI(data) {
  return request({
    url: 'hrmAchievementEmployeeAppraisal/queryEvaluatoScreen',
    method: 'post',
    data: data
  })
}

/**
 * 根据得分获取等级
 * @param {*} data
 */
export function hrmPerformanceEmployeeQueryLevelAPI(data) {
  return request({
    url: 'hrmAchievementEmployeeAppraisal/queryLevelIdByScore',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端获取绩效列表
 * @param {*} data
 */
export function queryStageAppraisalListAPI(data) {
  return request({
    url: 'hrmAppraisalEmployee/queryStageAppraisalList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端详情头部
 * @param {*} data
 */
export function queryStageListAPI(data) {
  return request({
    url: 'hrmAppraisalEmployee/queryStageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端填写列表
 * @param {*} data
 */
export function quotaInformationAPI(data) {
  return request({
    url: 'hrmAppraisalEmployee/quotaInformation',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端头部信息
 * @param {*} data
 */
export function queryEmployeeAppraisalBaseInfo(data) {
  return request({
    url: 'hrmAppraisalEmployee/queryEmployeeAppraisalBaseInfo',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端指标评分保存
 * @param {*} data
 */
export function quotaScoreAPI(data) {
  return request({
    url: 'hrmAppraisalEmployee/quotaScore',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端填写指标
 * @param {*} data
 */
export function fillInQuotaAPI(data) {
  return request({
    url: 'hrmAppraisalEmployee/fillInQuota',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端绩效记录
 * @param {*} data
 */
export function queryRecordListAPI(data) {
  return request({
    url: 'hrmAppraisalEmployee/queryRecordList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端绩效管理我的绩效档案
 * @param {*} data
 */
export function queryMyAppraisalFileList(data) {
  return request({
    url: 'hrmEmployeeAchievementFile/queryMyAppraisalFileList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端绩效管理指标确认
 * @param {*} data
 */
export function targetConfirmationPass(data) {
  return request({
    url: 'hrmAppraisalEmployee/targetConfirmationPass',
    method: 'post',
    data: data
  })
}

/**
 * 员工端绩效管理指标填写驳回
 * @param {*} data
 */
export function targetConfirmationReject(data) {
  return request({
    url: 'hrmAppraisalEmployee/targetConfirmationReject',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端绩效管理指标评分驳回
 * @param {*} data
 */
export function rejectScore(data) {
  return request({
    url: '/hrmAppraisalEmployee/rejectScore',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端绩效管理驳回选择节点
 * @param {*} data
 */
export function queryScoringPoint(data) {
  return request({
    url: 'hrmAppraisalEmployee/queryScoringPoint',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端绩效管理结果审核驳回
 * @param {*} data
 */
export function resultAuditReject(data) {
  return request({
    url: 'hrmAppraisalEmployee/resultAuditReject',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端绩效管理结果审核通过
 * @param {*} data
 */
export function resultAuditPass(data) {
  return request({
    url: 'hrmAppraisalEmployee/resultAuditPass',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端绩效管理结果申诉
 * @param {*} data
 */
export function resultAppeal(data) {
  return request({
    url: 'hrmAppraisalEmployee/resultAppeal',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端绩效管理结果确定
 * @param {*} data
 */
export function resultConfirmation(data) {
  return request({
    url: 'hrmAppraisalEmployee/resultConfirmationPass',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端绩效管理申诉确认驳回
 * @param {*} data
 */
export function resultAppealReject(data) {
  return request({
    url: 'hrmAppraisalEmployee/resultAppealReject',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端绩效管理申诉确认
 * @param {*} data
 */
export function resultAppealPass(data) {
  return request({
    url: 'hrmAppraisalEmployee/resultAppealPass',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工端实时评分
 * @param {*} data
 */
export function preCalculationQuotaScore(data) {
  return request({
    url: '/hrmAppraisalEmployee/preCalculationQuotaScore',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
