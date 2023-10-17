import request from '@/utils/request'

/**
 * 查看考核结果模板列表
 * @param {*} data
 */
export function hrmAchievementsResultTemplateAPI(data) {
  return request({
    url: 'hrmAchievementsResultTemplate/queryPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 添加考核结果模板
 * @param {*} data
 */
export function addAchievementsResultTemplate(data) {
  return request({
    url: 'hrmAchievementsResultTemplate/addResultTemplate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除考核结果模板
 * @param {*} data
 */
export function delTemplateAPI(data) {
  return request({
    url: '/hrmAchievementsResultTemplate/delTemplate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  根据Id获取考核结果详情
 * @param {*} data
 */
export function ResultTemplateInformation(data) {
  return request({
    url: `/hrmAchievementsResultTemplate/information/${data}`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  编辑考核结果详情
 * @param {*} data
 */
export function updateResultTemplate(data) {
  return request({
    url: '/hrmAchievementsResultTemplate/updateResultTemplate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
