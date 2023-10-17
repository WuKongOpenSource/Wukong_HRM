import request from '@/utils/request'

/**
 * 公告添加
 * @param {*} data
 */
export function noticeListAPI(data) {
  return request({
    url: 'oaAnnouncement/queryList',
    method: 'post',
    data: data
  })
}

/**
 * 公告添加或编辑
 * @param {*} data
 */
export function noticeAddOrUpateAPI(data) {
  var url = data.announcementId ? 'setAnnouncement' : 'addAnnouncement'
  return request({
    url: 'oaAnnouncement/' + url,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 公告删除
 * @param {*} data
 */
export function noticeDeleteAPI(id) {
  return request({
    url: `oaAnnouncement/delete/${id}`,
    method: 'post'
  })
}

/**
 * 公告详情
 * @param {*} data
 */
export function noticeQueryByIdAPI(id) {
  return request({
    url: `oaAnnouncement/queryById/${id}`,
    method: 'post'
  })
}

/**
 * 公告设为已读
 * @param {*} data
 */
export function noticeIsReadAPI(data) {
  return request({
    url: 'oaAnnouncement/readAnnouncement',
    method: 'post',
    data: data
  })
}
