import request from '@/utils/request'

/**
 * 考核模板列表
 * @param {*} data
 */
export function hrmAchievementsAssessmentTemplateAPI(data) {
  return request({
    url: 'hrmAchievementsAssessmentTemplate/queryPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 新建考核模板
 * @param {*} data
 */
export function createAssessmentTemplateAPI(data) {
  return request({
    url: 'hrmAchievementsAssessmentTemplate/saveTemplate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 编辑考核模板
 * @param {*} data
 */
export function updateTemplateAPI(data) {
  return request({
    url: '/hrmAchievementsAssessmentTemplate/updateTemplate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除考核模板
 * @param {*} data
 */
export function delTemplateAPI(data) {
  return request({
    url: '/hrmAchievementsAssessmentTemplate/delTemplate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 获取指标类型
 * @param {*} data
 */
export function quoteTypeListAPI(data) {
  return request({
    url: 'hrmAchievementsAssessmentTemplate/quoteTypeList',
    method: 'post',
    data: data
  })
}

/**
 * 根据id获取详情
 */
export function informationAPI(data) {
  return request({
    url: `/hrmAchievementsAssessmentTemplate/information/${data}`,
    method: 'post',
    data: data
  })
}

