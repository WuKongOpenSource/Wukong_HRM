import request from '@/utils/request'

/**
 * 登录二维码
 * @param {*} data
 */
export function authorizationLoginQrCodeAPI() {
  return request({
    url: 'authorization/getLoginQrCode',
    method: 'post'
  })
}

/**
 * 查询扫描结果
 * @param {*} data
 */
export function authorizationLoginQrInfoAPI(token) {
  return request({
    url: `authorization/getLoginQrInfo/${token}`,
    method: 'post'
  })
}
