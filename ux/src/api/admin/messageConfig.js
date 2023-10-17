import request from '@/utils/request'

/**
 * 查询列表
 * type 类型 1：crm 3：进销存
 * @param {*} data
 */
export function adminMessageConfigQueryAPI(type) {
  return request({
    url: `adminMessageConfig/queryList/${type}`,
    method: 'post'
  })
}

/**
 * 修改配置
 * @param {*} data
 */
export function adminMessageConfigUpdateAPI(data) {
  return request({
    url: 'adminMessageConfig/updateConfig',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
