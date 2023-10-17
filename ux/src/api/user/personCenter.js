import request from '@/utils/request'

/**
 * 个人详情
 * @param {*} data
 */
export function adminUsersReadAPI(data) {
  return request({
    url: 'adminUser/queryLoginUser',
    method: 'post',
    data: data
  })
}
