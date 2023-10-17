/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: chen
 */
import request from '@/utils/request'

/**
 * 查询绩效考核列表
 * @param {*} data
 */
export function hrmPerformanceQueryListAPI(data) {
  return request({
    url: 'hrmAchievementAppraisal/queryAppraisalPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询每个绩效状态的数量
 * @param {*} data
 */
export function hrmPerformanceQueryStatusNumPI(data) {
  return request({
    url: 'hrmAchievementAppraisal/queryAppraisalStatusNum',
    method: 'post',
    data: data
  })
}

/**
 * 添加考核
 * @param {*} data
 */
export function hrmPerformanceAddAPI(data) {
  return request({
    url: 'hrmAchievementAppraisal/addAppraisal',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改考核
 * @param {*} data
 */
export function hrmPerformanceSetAPI(data) {
  return request({
    url: 'hrmAchievementAppraisal/setAppraisal',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改考核状态
 * @param {*} data
 */
export function hrmPerformanceUpdateStatusAPI(data) {
  return request({
    url: 'hrmAchievementAppraisal/updateAppraisalStatus',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除考核
 * @param {*} data
 */
export function hrmPerformanceDeleteAPI(id) {
  return request({
    url: `hrmAchievementAppraisal/delete/${id}`,
    method: 'post'
  })
}

/**
 * 删除归档考核
 * @param {*} data
 */
export function delAppraisalPlanOfFile(data) {
  return request({
    url: '/hrmAppraisalPlan/delAppraisalPlanOfFile',
    method: 'post',
    data: data
  })
}

/**
 * 终止考核
 * @param {*} data
 */
export function hrmPerformanceStopAPI(id) {
  return request({
    url: `hrmAchievementAppraisal/stopAppraisal/${id}`,
    method: 'post'
  })
}

/**
 * 查询考核详情
 * @param {*} data
 */
export function hrmPerformanceDetailAPI(id) {
  return request({
    url: `hrmAchievementAppraisal/queryAppraisalById/${id}`,
    method: 'post'
  })
}

/**
 * 修改考核状态
 * @param {*} data
 */
export function hrmPerformanceQuerylEmployeeListAPI(data) {
  return request({
    url: 'hrmAchievementAppraisal/queryAppraisalEmployeeList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询员工考核详情
 * @param {*} data
 */
export function hrmPerformanceEmployeeDetailAPI(id) {
  return request({
    url: `hrmAchievementAppraisal/queryEmployeeDetail/${id}`,
    method: 'post'
  })
}

/**
 * 绩效档案详情统计
 * @param {*} data
 */
export function hrmPerformanceEmployeeQueryAppraisalNumAPI(id) {
  return request({
    url: `hrmAchievementAppraisal/queryEmployeeAppraisalCount/${id}`,
    method: 'post'
  })
}

/**
 * 查询考核详情
 * @param {*} data
 */
export function hrmPerformanceAppraisalDetailAPI(id) {
  return request({
    url: `hrmAchievementAppraisal/queryEmployeeDetail/${id}`,
    method: 'post'
  })
}

/**
 * 绩效档案详情列表
 * @param {*} data
 */
export function hrmPerformanceQueryEmployeeAppraisalAPI(data) {
  return request({
    url: 'hrmAchievementAppraisal/queryEmployeeAppraisal',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 通过绩效id查询员工列表
 * @param {*} data
 */
export function hrmPerformanceQueryEmployeeByAppraisalIdAPI(data) {
  return request({
    url: 'hrmAchievementAppraisal/queryEmployeeListByAppraisalId',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询绩效考核列表
 * @param {*} data
 */
export function hrmAppraisalPlanQueryListAPI(data) {
  return request({
    url: 'hrmAppraisalPlan/queryPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询每个绩效状态的数量
 * @param {*} data
 */
export function hrmAppraisalPlanQueryAppraisalStatusNumAPI(data) {
  return request({
    url: '/hrmAppraisalPlan/queryAppraisalStatusNum',
    method: 'post',
    data: data
  })
}

/*
 * 新建kpi计划
 */
export function hrmAppraisalPlanAddAppraisalPlanAPI(data) {
  return request({
    url: 'hrmAppraisalPlan/addAppraisalPlan',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除考核计划
 * @param {*} data
 */
export function hrmAppraisalPlanDelAppraisalPlanAPI(data) {
  return request({
    url: '/hrmAppraisalPlan/delAppraisalPlan',
    method: 'post',
    data: data
  })
}

/**
 * 查看考核设置
 */
export function hrmAppraisalPlanQuerySetting(data) {
  return request({
    url: 'hrmAppraisalPlan/querySetting',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查看考核人员表头
 * @param {*} data
 */
export function hrmAppraisalPlanQueryEmployeeListHeadInfoAPI(data) {
  return request({
    url: '/hrmAppraisalPlan/queryEmployeeListHeadInfo',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查看考核人员列表
 * @param {*} data
 */
export function hrmAppraisalPlanQueryEmployeeListAPI(data) {
  return request({
    url: '/hrmAppraisalPlan/queryEmployeeList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询高级筛选字段
 * @param {*} data
 */
export function hrmAppraisalPlanQueryFieldAPI(data) {
  return request({
    url: '/hrmAppraisalPlan/queryField',
    method: 'post',
    data: data
  })
}

/**
 * 检查并考核计划
 * @param {*} data
 */
export function hrmAppraisalPlanCheckAppraisalPlanAPI(data) {
  return request({
    url: '/hrmAppraisalPlan/checkAppraisalPlan',
    method: 'post',
    data: data
  })
}

/**
 * 启动考核计划
 * @param {*} data
 */
export function hrmAppraisalPlanStartAppraisalPlanAPI(data) {
  return request({
    url: '/hrmAppraisalPlan/startAppraisalPlan',
    method: 'post',
    data: data
  })
}

/**
 * 添加考核人员
 * @param {*} data
 */
export function hrmAppraisalPlanAddEmployeeListAPI(data) {
  return request({
    url: '/hrmAppraisalPlan/addAppraisalEmployeeList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 移除考核人员
 * @param {*} data
 */
export function hrmAppraisalPlanDelEmployeeListAPI(data) {
  return request({
    url: '/hrmAppraisalPlan/delAppraisalEmployee',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 关闭考核计划
 * @param {*} data
 */
export function hrmAppraisalPlanTerminationPlanAPI(data) {
  return request({
    url: '/hrmAppraisalPlan/terminationPlan',
    method: 'post',
    data: data
  })
}

/**
 * 开启评分
 * @param {*} data
 */
export function hrmAppraisalPlanOpenScoringAPI(data) {
  return request({
    url: '/hrmAppraisalPlan/openScoring',
    method: 'post',
    data: data
  })
}

/**
 * 绩效面谈
 * @param {*} data
 */
export function hrmAppraisalPlanToInterviewAPI(data) {
  return request({
    url: '/hrmAppraisalPlan/toInterview',
    method: 'post',
    data: data
  })
}

/**
 * 绩效归档
 * @param {*} data
 */
export function hrmAppraisalPlanPlaceOnFileAPI(data) {
  return request({
    url: '/hrmAppraisalPlan/placeOnFile',
    method: 'post',
    data: data
  })
}

/**
 * 考核绩效列表
 * @param {*} data
 */
export function hrmAppraisalEmployeePlanQueryAppraisalListAPI(data) {
  return request({
    url: '/hrmAppraisalPlan/queryAppraisalResultPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 考核绩效考核人员阶段数据
 * @param {*} data
 */
export function hrmAppraisalEmployeeQueryStageEmployeeNumAPI(data) {
  return request({
    url: '/hrmAppraisalEmployee/queryStageEmployeeNum',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 考核绩效考核人员分数数据
 * @param {*} data
 */
export function hrmAppraisalEmployeeQueryScoreEmployeeNumAPI(data) {
  return request({
    url: '/hrmAppraisalEmployee/queryScoreEmployeeNum',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 考核计划结果设置等级数量列表
 * @param {*} data
 */
export function hrmAppraisalEmployeeQueryAppraisalPlanResultLevelNumAPI(data) {
  return request({
    url: '/hrmAppraisalPlan/queryAppraisalPlanResultLevelNum',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询绩效档案列表
 * @param {*} data
 */
export function hrmEmployeeAchievementFileQueryAppraisalFileListAPI(data) {
  return request({
    url: '/hrmEmployeeAchievementFile/queryAppraisalFileList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询员工考核绩效列表
 * @param {*} data
 */
export function hrmEmployeeAchievementFileQueryEmployeeAppraisalListAPI(data) {
  return request({
    url: '/hrmEmployeeAchievementFile/queryEmployeeAppraisalList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 根据权限获取人员和组织
 */
export function queryInspectionDeptEmployeeList(data) {
  return request({
    url: `/hrmEmployee/queryInspectionDeptEmployeeList/${data}`,
    method: 'post',
    data: data
  })
}

/**
 * 根据权限获取人员列表
 */
export function queryInspectionAllEmployeeList(data) {
  return request({
    url: 'hrmEmployee/queryInspectionAllEmployeeList',
    method: 'post',
    data: data
  })
}

/**
 * 绩效档案删除记录
 */
export function delAppraisalEmployee(data) {
  return request({
    url: '/hrmEmployeeAchievementFile/delAppraisalFileRecordList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 绩效档案多选删除
 */
export function delAppraisalFileRecordListOfAll(data) {
  return request({
    url: '/hrmEmployeeAchievementFile/delAppraisalFileRecordListOfAll',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
