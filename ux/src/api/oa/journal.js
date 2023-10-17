import request from '@/utils/request'

/**
 * 日志列表
 * @param {*} data
 */
export function journalListAPI(data) {
  return request({
    url: 'oaLog/queryList',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 日志列表--已评论
 * @param {*} data
 */
export function journalHadCommentListAPI(data) {
  return request({
    url: 'biWork/queryOaComment',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 日志列表--未评论
 * @param {*} data
 */
export function journalNoCommentListAPI(data) {
  return request({
    url: 'biWork/queryOaNoComment',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 新建日志
 * @param {*} data
 */
export function journalAddAPI(data) {
  return request({
    url: 'oaLog/addOrUpdate',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data
  })
}

/**
 * 日志编辑
 * @param {*} data
 */
export function journalEditAPI(data) {
  return request({
    url: 'oaLog/addOrUpdate',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data
  })
}

/**
 * 日志删除
 * @param {*} data
 */
export function journalDeleteAPI(data) {
  return request({
    url: 'oaLog/deleteById',
    method: 'post',
    data
  })
}

/**
 * 日志标记已读
 * @param {*} data
 */
export function journalSetReadAPI(data) {
  return request({
    url: 'oaLog/readLog',
    method: 'post',
    data
  })
}

/**
 * 日志详情
 * @param {*} data
 */
export function journalQueryByIdAPI(data) {
  return request({
    url: 'oaLog/queryById',
    method: 'post',
    data
  })
}

/**
 * 详情页基本信息
 * @param {*} data
 */
export function journalInformationAPI(data) {
  return request({
    url: 'oaLog/information/' + data.id,
    method: 'post'
  })
}

/**
 * 日志概要
 * @param {*} data
 */
export function journalQueryBulletinAPI(data) {
  return request({
    url: 'oaLog/queryLogBulletin',
    method: 'post',
    data
  })
}

/**
 * 日志简报预览
 * @param {*} data
 */
export function journalQueryBulletinByTypeAPI(data) {
  return request({
    url: 'oaLog/queryLogBulletinByType',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 日志简报跟进记录预览
 * @param {*} data
 */
export function journalQueryRecordCountAPI(data) {
  return request({
    url: 'oaLog/queryLogRecordCount',
    method: 'post',
    data
  })
}

/**
 * 随机返回一条
 * @param {*} data
 */
export function journalGetLogWelcomeAPI(data) {
  return request({
    url: 'oaLog/getLogWelcomeSpeech',
    method: 'post',
    data
  })
}

/**
 * 日志导出
 * @param {*} data
 */
export function oaLogExportAPI(data) {
  return request({
    url: 'oaLog/export',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    responseType: 'blob'
  })
}

/**
 * 查询日志完成概要
 * @param {*} data
 */
export function oaLogCompleteStatsAPI(data) {
  return request({
    url: 'oaLog/queryCompleteStats',
    method: 'post',
    data
  })
}

/**
 * 查询日志完成情况
 * @param {*} data
 */
export function oaLogCompleteListAPI(data) {
  return request({
    url: 'oaLog/queryCompleteOaLogList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询日志未完成情况
 * @param {*} data
 */
export function oaLogUnCompleteListAPI(data) {
  return request({
    url: 'oaLog/queryIncompleteOaLogList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 点赞
 * @param {*} data
 */
export function oaLogFavourOrCancelAPI(data) {
  return request({
    url: 'oaLog/favourOrCancel',
    method: 'post',
    data: data
  })
}

/**
 * 查询正常日志模板列表
 * @param {*} data
 */
export function oaLogTemplatePartListAPI(data) {
  return request({
    url: 'oaLogTemplate/queryPartList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 保存个人日志模板的排序
 * @param {*} data
 */
export function oaLogTemplateSaveSortAPI(data) {
  return request({
    url: 'oaLogTemplate/saveTemplateSort',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询新增所需字段
 * @param {*} data configId
 */
export function oaLogTemplateFieldByCreate(data) {
  return request({
    url: 'oaLogTemplateField/field',
    method: 'post',
    data: data
  })
}

/**
 * 查询编辑所需字段
 * @param {*} logId
 */
export function oaLogTemplateFieldByLogId(data) {
  return request({
    url: `oaLogTemplateField/field/${data.logId}`,
    method: 'post',
    data
  })
}

/**
 * 导出用的字段
 * @param {*} logId
 */
export function oaLogTemplateFieldByExportAPI(data) {
  return request({
    url: `oaLogTemplateField/exportField/${data.configId}`,
    method: 'post'
  })
}

/**
 * 自定义字段验重
 * @param {*} data
 */
export function oaLogTemplateFieldVerifyAPI(data) {
  return request({
    url: 'oaLogTemplateField/verify',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
