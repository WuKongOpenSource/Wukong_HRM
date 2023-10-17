import request from '@/utils/request'

/**
 * 查询客户管理导航栏设置接口
 * @param {*} data
 */
// export function configHeaderModelSortAPI(data) {
//   return request({
//     url: 'adminConfig/queryHeaderModelSort',
//     method: 'post',
//     data: data
//   })
// }

// /**
//  * 查询咨询活动
//  * @param {*} data
//  */
// export function configQueryMarketingAPI(data) {
//   return request({
//     url: 'adminConfig/queryMarketing',
//     method: 'post',
//     data: data
//   })
// }

/**
 * 设置咨询活动
 * @param {*} data
 */
export function configSetMarketingAPI(data) {
  return request({
    url: 'adminConfig/setMarketing',
    method: 'post',
    data: data
  })
}

/**
 * 查询已读状态
 * @param {*} data
 */
export function queryReadNoticeStatusAPI(data) {
  return request({
    url: 'adminConfig/queryReadNoticeStatus',
    method: 'post',
    data: data
  })
}

/**
 * 已读更新通知
 * @param {*} data
 */
export function readUpdateNoticeAPI(data) {
  return request({
    url: 'adminConfig/readNotice',
    method: 'post',
    data: data
  })
}
