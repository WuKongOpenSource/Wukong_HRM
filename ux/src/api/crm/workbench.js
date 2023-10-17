import request from '@/utils/request'

/**
 * 销售简报
 * @param {*} data
 */
export function crmQueryBulletinAPI(data) {
  return request({
    url: 'biCrmInstrument/queryBulletin',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 数据汇总
 * @param {*} data
 */
export function queryDataInfo(data) {
  return request({
    url: 'biCrmInstrument/queryDataInfo',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 销售简报列表
 */
export function crmIndexIndexListAPI(data) {
  return request({
    url: 'crmInstrument/queryBulletinInfo',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 简报跟进记录数量
 * @param {*} data
 */
export function crmQueryRecordConuntAPI(data) {
  return request({
    url: 'crmInstrument/queryRecordCount',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 业绩指标
 * @param {*} data
 */
export function crmIndexAchievementDataAPI(data) {
  return request({
    url: 'biCrmInstrument/queryPerformance',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 销售漏斗
 * @param {*} data
 */
export function crmIndexFunnelAPI(data) {
  return request({
    url: 'biFunnel/sellFunnel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 销售漏斗详情
 * @param {*} data
 */
export function crmInstrumentSellFunnelBusinessListAPI(data) {
  return request({
    url: 'crmInstrument/sellFunnelBusinessList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 销售趋势 目标及完成情况
 * @param {*} data
 */
export function crmIndexSaletrendAPI(data) {
  return request({
    url: 'biCrmInstrument/salesTrend',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 获取简报 跟进记录信息
 * @param {*} data
 */
export function crmIndexGetRecordListAPI(data) {
  data.label = data.crmType
  return request({
    url: 'crmInstrument/queryRecordList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 遗忘提醒
 * @param {*} data
 */
export function crmIndexForgottenCustomerAPI(data) {
  return request({
    url: 'biCrmInstrument/forgottenCustomerCount',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 遗忘提醒详情
 * @param {*} data
 */
export function crmIndexForgottenCustomerListAPI(data) {
  return request({
    url: 'crmInstrument/forgottenCustomerPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 回款金额排行榜
 * @param {*} data
 */
export function biRankingReceivablesAPI(data) {
  return request({
    url: 'biRanking/receivablesRanKing',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 合同金额排行榜
 * @param {*} data
 */
export function biRankingContractAPI(data) {
  return request({
    url: 'biRanking/contractRanKing',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 合同数排行榜
 * @param {*} data
 */
export function biRankingContractCountAPI(data) {
  return request({
    url: 'biRanking/contractCountRanKing',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 新增客户数排行榜
 * @param {*} data
 */
export function biRankingCustomerCountAPI(data) {
  return request({
    url: 'biRanking/customerCountRanKing',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 新增联系人排行榜
 * @param {*} data
 */
export function biRankingContactsCountAPI(data) {
  return request({
    url: 'biRanking/contactsCountRanKing',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 新增跟进记录数排行榜
 * @param {*} data
 */
export function biRankingRecordCountAPI(data) {
  return request({
    url: 'biRanking/recordCountRanKing',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 仪表盘排序
 * @param {*} data
 */
export function crmIndexSortAPI(data) {
  return request({
    url: 'crmInstrument/queryModelSort',
    method: 'post',
    data: data
  })
}

/**
 * 仪表盘排序设置
 * @param {*} data
 */
export function crmIndexSetSortAPI(data) {
  return request({
    url: 'crmInstrument/setModelSort',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 逾期未联系客户接口
 * @param {*} data
 */
export function crmIndexUnContactCustomerAPI(data) {
  return request({
    url: 'crmInstrument/unContactCustomerPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 未跟进客户列表
 * @param {*} data
 */
export function crmIndexNoRecordCustomerListAPI(data) {
  return request({
    url: 'crmInstrument/queryNoRecordCustomerList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 跟进记录导出
 * @param {*} data
 */
export function crmActivityExportRecordListAPI(data) {
  return request({
    url: 'crmActivity/exportRecordList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    responseType: 'blob'
  })
}

/**
 * 跟进记录导入
 * @param {*} data
 */
export function crmActivityImportRecordListAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'crmActivity/importRecordList',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 跟进记录导入模板
 * @param {*} data
 */
export function crmActivityDownloadRecordExcelAPI(data) {
  return request({
    url: 'crmActivity/downloadRecordExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

/**
 * 跟进记录导出
 * @param {*} data
 */
export function crmActivityExportFieldAPI(data) {
  return request({
    url: `crmActivity/exportField/${data.activityType}`,
    method: 'post'
  })
}

/**
 * 筛选保存默认值
 * @param {*} data
 */
export function crmDefaultSaveAPI(data) {
  return request({
    url: 'crmSearchDefault/save',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
/**
 * 筛选获取默认值
 * @param {*} data
 */
export function crmDefaultQueryByTypeAPI(data) {
  return request({
    url: 'crmSearchDefault/queryByType',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
/**
 * 预计回款
 * @param {*} data
 */
export function crmPlanReturnMoneyAPI(data) {
  return request({
    url: 'crmBiSearch/searchReceivablesPlanPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 外勤签到导出
 * @param {*} data
 */
export function crmActivityExportFieldSignAPI(data) {
  return request({
    url: 'crmActivity/exportFieldSign',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    responseType: 'blob'
  })
}

/**
 * 外勤签到导入
 * @param {*} data
 */
export function crmActivityImportFieldSignAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'crmActivity/importFieldSign',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 外勤签到导入模板
 * @param {*} data
 */
export function crmActivityDownloadFieldSignExcelAPI() {
  return request({
    url: 'crmActivity/downloadFieldSignExcel',
    method: 'post',
    responseType: 'blob'
  })
}
