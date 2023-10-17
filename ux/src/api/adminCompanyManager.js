import request from '@/utils/request'

/**
 * 域名配置
 * @param {*} data
 */
export function adminCompanyManagerSetDomainAPI(data) {
  return request({
    url: 'adminCompanyManager/setCompanyDoMain',
    method: 'post',
    data: data
  })
}

/**
 * 根据域名查询logo和名称
 * @param {*} data
 */
export function adminCompanyManagerQueryLogoAPI(data) {
  return request({
    url: 'adminCompanyManager/queryCompanyLogoByDomain',
    method: 'post',
    data: data
  })
}

/**
 * @description: 发送邮件进行验证
 * @param {*} email
 * @return {*}
 */
export function adminCompanyManagerSetEmailAPI(email) {
  return request({
    url: 'adminCompanyManager/setEmailInfo',
    method: 'post',
    data: { email }
  })
}

/**
 * @description: 验证邮箱信息
 * @param {*} k 邮箱跳转进入时携带的参数
 * @return {*}
 */
export function adminCompanyManagerVerifyEmailAPI(k) {
  return request({
    url: `adminCompanyManager/verifyEmailInfo/?k=${k}`,
    method: 'get'
  })
}
