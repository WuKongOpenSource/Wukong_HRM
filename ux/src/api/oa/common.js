import request from '@/utils/request'

/**
 * 评论添加
 * @param {*} data
 */
export function setCommentAPI(data) {
  return request({
    url: 'workTaskComment/setComment',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function deleteCommentAPI(data) {
  return request({
    url: 'workTaskComment/deleteComment',
    method: 'post',
    data: data
  })
}

export function queryCommentListAPI(data) {
  return request({
    url: 'workTaskComment/queryCommentList',
    method: 'post',
    data
  })
}
