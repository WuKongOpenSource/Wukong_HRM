import request from '@/utils/request'

/**
 * 通讯录列表
 * @param {*} data
 */
export function addressListAPI(data) {
  return request({
    url: 'adminUser/queryListName',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

// /**
//  * 通讯录部门列表
//  * @param {*} data
//  */
// export function queryListNameByDept(data) {
//   return request({
//     url: 'adminUser/queryListNameByDept',
//     method: 'post',
//     data: data,
//     headers: {
//       'Content-Type': 'application/json;charset=UTF-8'
//     }
//   })
// }

/**
 * 通讯录关注状态切换
 * @param {*} data
 */
export function toggleAttentionAPI(data) {
  return request({
    url: 'adminUser/attention',
    method: 'post',
    data: data
  })
}
