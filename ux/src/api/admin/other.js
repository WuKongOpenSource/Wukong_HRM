import request from '@/utils/request'

/**
 * 设置日志欢迎语 oaCalendar/addOrUpdate
 * @param {*} data
 */
export function sysSetLogWelcomeAPI(data) {
  return request({
    url: 'adminConfig/setLogWelcomeSpeech',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 设置日志欢迎语
 * @param {*} data
 */
export function sysGetLogWelcomeListAPI(data) {
  return request({
    url: 'adminConfig/getLogWelcomeSpeechList',
    method: 'post',
    data: data
  })
}

// /**
//  * 根据id删除日志欢迎语
//  * @param {*} data
//  */
// export function sysDeleteConfigByIdAPI(data) {
//   return request({
//     url: 'sysConfig/deleteConfigById',
//     method: 'post',
//     data: data
//   })
// }

/**
 * 添加/修改日程类型
 * @param {*} data
 */
export function calendarAddOrUpdateAPI(data) {
  return request({
    url: 'oaCalendar/addOrUpdate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询日程类型
 * @param {*} data
 */
export function calendarQueryTypeListAPI(data) {
  return request({
    url: 'oaCalendar/queryTypeList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除日程类型
 * @param {*} typeId
 */
export function calendarDeleteAPI(typeId) {
  return request({
    url: `oaCalendar/delete/${typeId}`,
    method: 'post'
  })
}

/**
 * 查询日志规则接口
 * @param {*} data
 */
export function oaLogRuleQueryAPI(data) {
  return request({
    url: 'oaLog/queryOaLogRuleList',
    method: 'post',
    data: data
  })
}

/**
 * 设置日志规则接口
 * @param {*} data
 */
export function oaLogRuleSetAPI(data) {
  return request({
    url: 'oaLog/setOaLogRule',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data
  })
}
