import request from '@/utils/request'

/**
 * 人事概况
 * @param {*} data
 */
export function hrmDashboardEmployeeSurveyAPI() {
  return request({
    url: 'hrmDashboard/employeeSurvey',
    method: 'post'
  })
}

/**
 * 招聘动态
 * @param {*} data
 */
export function hrmDashboardRecruitSurveyAPI() {
  return request({
    url: 'hrmDashboard/recruitSurvey',
    method: 'post'
  })
}

/**
 * 上月薪资概况
 * @param {*} data
 */
export function hrmDashboardLastSalarySurveyAPI() {
  return request({
    url: 'hrmDashboard/lastSalarySurvey',
    method: 'post'
  })
}

/**
 * 绩效概况统计
 * @param {*} data
 */
export function hrmDashboardAppraisalCountSurveyAPI() {
  return request({
    url: 'hrmDashboard/appraisalCountSurvey',
    method: 'post'
  })
}

/**
 * 绩效概况
 * @param {*} data
 */
export function hrmDashboardAppraisalSurveyAPI(status) {
  return request({
    url: `hrmDashboard/appraisalSurvey/${status}`,
    method: 'post'
  })
}

/**
 * 查询时间段事项
 */
export function hrmDashboardQueryNotesStatusAPI(data) {
  return request({
    url: 'hrmDashboard/queryNotesStatus',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询当日事项
 */
export function hrmDashboardQueryNotesByTimeAPI(data) {
  return request({
    url: 'hrmDashboard/queryNotesByTime',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 添加备忘
 */
export function hrmDashboardAddNotesAPI(data) {
  return request({
    url: 'hrmDashboard/addNotes',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除备忘
 * @param {*} data
 */
export function hrmDashboardDeleteNotesAPI(notesId) {
  return request({
    url: `hrmDashboard/deleteNotes/${notesId}`,
    method: 'post'
  })
}

/**
 * 待办提醒
 * @param {*} data
 */
export function hrmDashboardToDoRemindAPI() {
  return request({
    url: 'hrmDashboard/toDoRemind',
    method: 'post'
  })
}

/**
 * 我的概况(员工端)
 * @param {*} data
 */
export function hrmDashboardMySurveyAPI() {
  return request({
    url: 'hrmDashboard/mySurvey',
    method: 'post'
  })
}

/**
 * 查询当日事项(员工端)
 */
export function hrmDashboardMyNotesByTimeAPI(data) {
  return request({
    url: 'hrmDashboard/myNotesByTime',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询当日事项(员工端)
 */
export function hrmDashboardMyNotesStatusAPI(data) {
  return request({
    url: 'hrmDashboard/myNotesStatus',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 我的团队(上级角色)
 * @param {*} data
 */
export function hrmDashboardMyTeamAPI() {
  return request({
    url: 'hrmDashboard/myTeam',
    method: 'post'
  })
}

/**
 * 团队概况(上级角色)
 * @param {*} data
 */
export function hrmDashboardTeamSurveyAPI() {
  return request({
    url: 'hrmDashboard/teamSurvey',
    method: 'post'
  })
}
