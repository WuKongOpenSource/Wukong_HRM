/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @LastEditors: yang
 */
import request from '@/utils/request'

/**
 * 创建下月薪资表
 * @param {*} data
 */
export function hrmSalaryMonthRecordAddNextAPI() {
  return request({
    url: 'hrmSalaryMonthRecord/addNextMonthSalary',
    method: 'post'
  })
}

/**
 * 核算薪资数据
 * @param {*} data
 */
export function hrmSalaryMonthRecordComputeAPI(data) {
  const param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'hrmSalaryMonthRecord/computeSalaryData',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 查询计薪人员数量 0 未计薪 1 计薪
 * @param {*} data
 */
export function hrmSalaryMonthRecordNumAPI(type) {
  return request({
    url: `hrmSalaryMonthRecord/queryPaySalaryNumByType/${type}`,
    method: 'post'
  })
}

/**
 * 查询未计薪人员列表
 * @param {*} data
 */
export function hrmSalaryMonthRecordQueryNoEmployeeAPI() {
  return request({
    url: 'hrmSalaryMonthRecord/queryNoPaySalaryEmployee',
    method: 'post'
  })
}

/**
 * 查询计薪列表员工异动数量
 * @param {*} data
 */
export function hrmSalaryMonthRecordChangeNumAPI() {
  return request({
    url: 'hrmSalaryMonthRecord/queryEmployeeChangeNum',
    method: 'post'
  })
}

/**
 * 查询薪资项表头
 * @param {*} data
 */
export function hrmSalaryMonthRecordHeadAPI() {
  return request({
    url: 'hrmSalaryMonthRecord/querySalaryOptionHead',
    method: 'post'
  })
}

/**
 * 查询薪资列表
 * @param {*} data
 */
export function hrmSalaryMonthRecordListAPI(data) {
  return request({
    url: 'hrmSalaryMonthRecord/querySalaryPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询最新的薪资记录
 * @param {*} data
 */
export function hrmSalaryMonthLastRecordAPI() {
  return request({
    url: 'hrmSalaryMonthRecord/queryLastSalaryMonthRecord',
    method: 'post'
  })
}

/**
 * 查询薪资项合计
 * @param {*} data
 */
export function hrmSalaryMonthRecordSumOptionAPI(srecordId) {
  return request({
    url: `hrmSalaryMonthRecord/querySalaryOptionCount/${srecordId}`,
    method: 'post'
  })
}

/**
 * 查询历史薪资列表
 * @param {*} data
 */
export function hrmSalaryHistoryRecordListAPI(data) {
  return request({
    url: 'hrmSalaryHistoryRecord/queryHistorySalaryList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询历史薪资详情
 * @param {*} data
 */
export function hrmSalaryHistoryRecordDetailAPI(data) {
  return request({
    url: 'hrmSalaryHistoryRecord/queryHistorySalaryDetail',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 在线修改薪资
 * @param {*} data
 */
export function hrmSalaryMonthRecordUpdateAPI(data) {
  return request({
    url: 'hrmSalaryMonthRecord/updateSalary',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 考勤模板
 */
export function hrmSalaryDownloadAttendanceTempAPI() {
  return request({
    url: 'hrmSalaryMonthRecord/downloadAttendanceTemp',
    method: 'post',
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 下载上月个税累计导入模板
 */
export function hrmSalaryDownCumulativeOfLastMonthTempAPI() {
  return request({
    url: 'hrmSalaryMonthRecord/downCumulativeTaxOfLastMonthTemp',
    method: 'post',
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 下载个税专项附加扣除累计导入模板
 */
export function hrmSalaryDownAdditionalTempAPI() {
  return request({
    url: 'hrmSalaryMonthRecord/downloadAdditionalDeductionTemp',
    method: 'post',
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 提交审核
 */
export function hrmSalaryMonthRecordSubmitExamineAPI(data) {
  return request({
    url: 'hrmSalaryMonthRecord/submitExamine',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除薪资记录
 */
export function hrmSalaryMonthRecordDeleteAPI(srecordId) {
  return request({
    url: `hrmSalaryMonthRecord/deleteSalary/${srecordId}`,
    method: 'post'
  })
}

/** 工资条 */
/**
 * 查询工资条模板列表
 */
export function hrmSalarySlipTemplateListAPI() {
  return request({
    url: 'hrmSalarySlipTemplate/querySlipTemplateList',
    method: 'post'
  })
}

/**
 * 查询工资模板项
 */
export function hrmSalarySlipTemplateDetailAPI(id) {
  return request({
    url: `hrmSalarySlipTemplateOption/queryTemplateOptionByTemplateId/${id}`,
    method: 'post'
  })
}

/**
 * 工资模板删除
 */
export function hrmSalarySlipTemplateDeleteAPI(id) {
  return request({
    url: `hrmSalarySlipTemplate/deleteSlipTemplate/${id}`,
    method: 'post'
  })
}

/**
 * 添加工资条模板
 */
export function hrmSalarySlipTemplateAddAPI(data) {
  return request({
    url: 'hrmSalarySlipTemplate/addSlipTemplate',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询工资条选择发送员工列表
 */
export function hrmSalarySlipRecordQueryEmployeeAPI(data) {
  return request({
    url: 'hrmSalarySlipRecord/querySlipEmployeePageList',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 发工资条
 */
export function hrmSalarySlipRecordSendAPI(data) {
  return request({
    url: 'hrmSalarySlipRecord/sendSalarySlip',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询发放工资条记录列表
 */
export function hrmSalarySlipRecordSendListAPI(data) {
  return request({
    url: 'hrmSalarySlipRecord/querySendRecordList',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询发放工资条记录详情列表
 */
export function hrmSalarySlipRecordSendDetailListAPI(data) {
  return request({
    url: 'hrmSalarySlipRecord/querySendDetailList',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 添加修改工资条备注
 */
export function hrmSalarySlipRecordSetRemarksAPI(data) {
  return request({
    url: 'hrmSalarySlipRecord/setSlipRemarks',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询工资条明细
 */
export function hrmSalarySlipRecordQueryDetailAPI(id) {
  return request({
    url: `hrmSalarySlipRecord/querySlipDetail/${id}`,
    method: 'post'
  })
}

/**
 * 删除工资条明细
 */
export function hrmSalarySlipRecordDeleteAPI(id) {
  return request({
    url: `hrmSalarySlipRecord/deleteSendRecord/${id}`,
    method: 'post'
  })
}

/** 薪资档案 */

/**
 * 查询薪资档案列表
 */
export function hrmSalaryArchivesQueryListAPI(data) {
  return request({
    url: 'hrmSalaryArchives/querySalaryArchivesList',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/** 调薪模板 */

/**
 * 删除模板
 */
export function hrmSalaryChangeTemplateDeleteAPI(id) {
  return request({
    url: `hrmSalaryChangeTemplate/deleteChangeTemplate/${id}`,
    method: 'post'
  })
}

/**
 * 查询调薪默认项
 */
export function hrmSalaryChangeTemplateQueryOptionAPI() {
  return request({
    url: 'hrmSalaryChangeTemplate/queryChangeSalaryOption',
    method: 'post'
  })
}

/**
 * 查询模板列表
 */
export function hrmSalaryChangeTemplateQueryListAPI() {
  return request({
    url: 'hrmSalaryChangeTemplate/queryChangeTemplateList',
    method: 'post'
  })
}

/**
 * 设置定薪模板
 */
export function hrmSalaryArchivesSetAPI(data) {
  return request({
    url: 'hrmSalaryChangeTemplate/setChangeTemplate',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 单个定薪
 */
export function hrmSalaryArchivesSetFixAPI(data) {
  return request({
    url: 'hrmSalaryArchives/setFixSalaryRecord',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 单个调薪
 */
export function hrmSalaryArchivesSetChangeAPI(data) {
  return request({
    url: 'hrmSalaryArchives/setChangeSalaryRecord',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询调薪项的值(单个调薪使用)
 */
export function hrmSalaryArchivesQueryChangeOptionByIdAPI(data) {
  return request({
    url: 'hrmSalaryArchives/queryChangeOptionByTemplateId',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询批量调薪项
 */
export function hrmSalaryArchivesQueryBatchChangeOptionAPI() {
  return request({
    url: 'hrmSalaryArchives/queryBatchChangeOption',
    method: 'post'
  })
}

/**
 * 批量调薪
 */
export function hrmSalaryArchivesBatchSetChangeAPI(data) {
  return request({
    url: 'hrmSalaryArchives/batchChangeSalaryRecord',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询薪资档案信息
 */
export function hrmSalaryArchivesQueryByIdAPI(id) {
  return request({
    url: `hrmSalaryArchives/querySalaryArchivesById/${id}`,
    method: 'post'
  })
}

/**
 * 查询调薪记录列表
 */
export function hrmSalaryArchivesQueryRecordAPI(id) {
  return request({
    url: `hrmSalaryArchives/queryChangeRecordList/${id}`,
    method: 'post'
  })
}

/**
 * 查询定薪记录详情
 */
export function hrmSalaryArchivesQueryFixRecordAPI(id) {
  return request({
    url: `hrmSalaryArchives/queryFixSalaryRecordById/${id}`,
    method: 'post'
  })
}

/**
 * 查询调薪记录详情
 */
export function hrmSalaryArchivesQueryChangeRecordAPI(id) {
  return request({
    url: `hrmSalaryArchives/queryChangeSalaryRecordById/${id}`,
    method: 'post'
  })
}

/**
 * 查询调薪记录详情
 */
export function hrmSalaryArchivesCancelChangeSalaryAPI(id) {
  return request({
    url: `hrmSalaryArchives/cancelChangeSalary/${id}`,
    method: 'post'
  })
}

/**
 * 删除调薪
 */
export function hrmSalaryArchivesDeleteChangeSalaryAPI(id) {
  return request({
    url: `hrmSalaryArchives/deleteChangeSalary/${id}`,
    method: 'post'
  })
}

/**
 * 下载调薪模板
 * @param {*} data
 */
export function hrmSalaryArchivesDownLoadChangeTemplateAPI() {
  return request({
    url: 'hrmSalaryArchives/downLoadChangeTemplate',
    method: 'post',
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 下载定薪模板
 * @param {*} data
 */
export function hrmSalaryArchivesDownLoadFixTemplateAPI() {
  return request({
    url: 'hrmSalaryArchives/downLoadFixTemplate',
    method: 'post',
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 导入调薪
 * @param {*} data
 */
export function hrmSalaryArchivesChangeRecordExportAPI(data) {
  const param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'hrmSalaryArchives/exportChangeSalaryRecord',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 导入定薪
 * @param {*} data
 */
export function hrmSalaryArchivesFixRecordExportAPI(data) {
  const param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'hrmSalaryArchives/exportFixSalaryRecord',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 邮箱审批查看薪资列表
 * @param {*} data
 */
export function useExamineEmailTokenForSalaryPageLis(data) {
  return request({
    url: 'hrmSalaryMonthRecord/useExamineEmailTokenForSalaryPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    customConfig: {
      removeToken: true
    }
  })
}
