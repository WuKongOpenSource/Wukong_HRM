import request from '@/utils/request'

/**
 * 查询工资条列表
 */
export function hrmSalarySlipQueryMyListAPI(data) {
  return request({
    url: 'hrmSalarySlip/querySalarySlipList',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
