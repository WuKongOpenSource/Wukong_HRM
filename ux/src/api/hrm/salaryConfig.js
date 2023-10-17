import request from '@/utils/request'

/**
 * 查询薪资/社保初始化配置是否存在
 * @param {*} data
 */
export function hrmSalaryConfigQueryInItConfigAPI() {
  return request({
    url: 'hrmSalaryConfig/queryInItConfig',
    method: 'post'
  })
}

/**
 * 保存初始化配置
 * @param {*} data
 */
export function hrmSalaryConfigSaveInItConfigAPI(data) {
  return request({
    url: 'hrmSalaryConfig/saveInitConfig',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改初始化配置状态为已配置
 * @param {*} data
 */
export function hrmSalaryConfigUpdateInItConfigAPI(type) {
  return request({
    url: `/hrmSalaryConfig/updateInitStatus/${type}`,
    method: 'post'
  })
}

/**
 * 查询薪资设置
 * @param {*} data
 */
export function hrmSalaryConfigQueryConfigAPI() {
  return request({
    url: '/hrmSalaryConfig/querySalaryConfig',
    method: 'post'
  })
}

/**
 * 修改薪资对应社保月设置
 * @param {*} data
 */
export function hrmSalaryConfigUpdateConfigAPI(data) {
  return request({
    url: 'hrmSalaryConfig/updateSalaryConfig',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
