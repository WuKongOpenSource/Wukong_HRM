import request from '@/utils/request'

/**
 * 查询操作记录列表
 * type 1 员工
 * typeId 操作对象id
 * @param {*} data
 */
export function hrmActionRecordQueryAPI(data) {
  return request({
    url: 'hrmActionRecord/queryRecordList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
