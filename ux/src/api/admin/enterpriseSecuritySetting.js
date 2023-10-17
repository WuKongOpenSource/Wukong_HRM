import request from '@/utils/request'

/**
 * 配置查询 规则类型(1.密码规则,2.登出规则）
 * @param {*} data
 */
export function enterpriseSecurityConfigQueryAPI(data) {
  return request({
    url: `admin/adminCompanySecuritySetting/queryByConfigType/${data.type}`,
    method: 'post',
    data: data
  })
}

/**
 * 配置查询 规则类型(1.密码规则,2.登出规则）未登录时查询
 * @param {*} data
 */
export function adminCompanySecuritySettingQuerySecuritySettingAPI(data) {
  return request({
    url: `admin/adminCompanySecuritySetting/querySecuritySetting/${data.type}/${data.companyId}`,
    method: 'post',
    data: data
  })
}

/**
 * 配置保存
 * @param {*} data
 */
export function enterpriseSecurityConfigSaveAPI(data) {
  return request({
    url: 'admin/adminCompanySecuritySetting/update',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 水印配置查询
 * @param {*} data
 */
export function enterpriseSecurityWaterMarkConfigQueryAPI(data) {
  return request({
    url: 'admin/adminCompanySecuritySetting/queryWaterMarkConfig',
    method: 'post',
    data: data
  })
}

/**
 * 水印配置设置
 * @param {*} data
 */
export function enterpriseSecurityWaterMarkConfigSettingAPI(data) {
  return request({
    url: 'admin/adminCompanySecuritySetting/updateWaterMarkConfig',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 密码到期重置通知
 * @param {*} data
 */
export function adminUserResetPwdNoticeAPI(data) {
  return request({
    url: 'admin/adminUser/resetPwdNotice',
    method: 'post',
    data: data
  })
}

/**
 * 强制修改登录密码
 * @param {*} data
 */
export function adminUserforceResetPwdNoticeAPI(data) {
  return request({
    url: 'admin/adminUser/forceResetPwdNotice',
    method: 'post',
    data: data
  })
}

