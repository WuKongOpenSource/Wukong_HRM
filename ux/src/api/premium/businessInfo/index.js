import request from '@/utils/request'

/**
 * @description: 查询工商照面信息
 * @param {*} name 公司名称
 * @return {*}
 */
export function crmEnterpriseBasicInfoAPI(name) {
  return request({
    url: 'crmEnterprise/queryBasicInfo',
    method: 'post',
    data: { name },
    requestProp: {
      disabledMessage: true
    }
  })
}

/**
 * @description: 查询分支机构信息
 * @param {*} name 公司名称
 * @return {*}
 */
export function crmEnterpriseBranchsAPI(name) {
  return request({
    url: 'crmEnterprise/queryBranchs',
    method: 'post',
    data: { name }
  })
}

/**
 * @description: 查询企业联系方式信息
 * @param {*} name 公司名称
 * @return {*}
 */
export function crmEnterpriseContactInfoAPI(name) {
  return request({
    url: 'crmEnterprise/queryContactInfo',
    method: 'post',
    data: { name },
    requestProp: {
      disabledMessage: true
    }
  })
}

/**
 * @description: 查询企业信息变更记录
 * @param {*} name 公司名称
 * @return {*}
 */
export function crmEnterpriseChangeRecordsAPI(name) {
  return request({
    url: 'crmEnterprise/queryChangeRecords',
    method: 'post',
    data: { name }
  })
}

/**
 * @description: 查询主要人员信息
 * @param {*} name 公司名称
 * @return {*}
 */
export function crmEnterpriseEmployeesAPI(name) {
  return request({
    url: 'crmEnterprise/queryEmployees',
    method: 'post',
    data: { name }
  })
}

/**
 * @description: 查询对外投资信息
 * @param {*} name 公司名称
 * @return {*}
 */
export function crmEnterpriseInvestmentAPI(name) {
  return request({
    url: 'crmEnterprise/queryInvestment',
    method: 'post',
    data: { name }
  })
}

/**
 * @description: 模糊查询工商信息列表
 * @param {*} name 公司名称
 * @return {*}
 */
export function crmEnterpriseListAPI(name) {
  return request({
    url: 'crmEnterprise/queryList',
    method: 'post',
    data: { name }
  })
}

/**
 * @description: 查询股东信息
 * @param {*} name 公司名称
 * @return {*}
 */
export function crmEnterprisePartnersAPI(name) {
  return request({
    url: 'crmEnterprise/queryPartners',
    method: 'post',
    data: { name }
  })
}

/**
 * @description: 清空缓存
 * @param {*} name 公司名称
 * @return {*}
 */
export function crmEnterpriseCleanCacheAPI(name) {
  return request({
    url: 'crmEnterprise/cleanCache',
    method: 'post',
    data: { name }
  })
}
