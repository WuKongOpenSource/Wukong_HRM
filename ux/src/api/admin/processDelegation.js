import request from '@/utils/request'

/**
 *  查询流程委托列表
 * @param {*} data
 */
export function examineDelegateQueryPageListAPI(data) {
  return request({
    url: `/examineDelegate/queryPageList`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  保存流程委托
 * @param {*} data
 */
export function examineDelegateSaveAPI(data) {
  return request({
    url: `/examineDelegate/save`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  修改流程委托
 * @param {*} data
 */
export function examineDelegateUpdateAPI(data) {
  return request({
    url: `/examineDelegate/update`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *  修改流程委托状态
 * @param {*} data
 */
export function examineDelegateSetDelegateStateAPI(data) {
  return request({
    url: `/examineDelegate/setDelegateState/${data.delegateId}/${data.state}`,
    method: 'post',
    data: data
  })
}
