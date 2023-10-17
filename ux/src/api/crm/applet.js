import request from '@/utils/request'

/**
 * 查询小程序分页列表
 * @param {*} data
 */
export function crmAppletIndexAPI(data) {
  return request({
    url: 'CrmWeixinLeads/queryPage',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 批量删除小程序信息 changeLeads
 * @param {*} data
 */
export function crmWeixinDeleteAPI(data) {
  return request({
    url: 'CrmWeixinLeads/deleteByIds',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 转化为线索
 * @param {*} data
 */
export function crmWeixinChangeLeadsAPI(data) {
  return request({
    url: 'CrmWeixinLeads/changeLeads',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 导出勾选
 * @param {*} data
 */
export function crmWeixinLeadsExportLeadsAPI(data) {
  return request({
    url: 'CrmWeixinLeads/exportLeads',
    method: 'post',
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}
