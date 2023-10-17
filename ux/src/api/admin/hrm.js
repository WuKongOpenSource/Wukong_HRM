/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-06-02 11:26:03
 * @LastEditTime: 2023-08-07 16:54:25
 * @LastEditors: chenhaojie 1476192083@qq.com
 */
import request from '@/utils/request'

/**
 * 根据类型查询配置列表
 * @param {*} data
 */
export function hrmConfigQueryRecruitChannelAPI(data) {
  return request({
    url: 'hrmConfig/queryRecruitChannelList',
    method: 'post',
    data: data
  })
}

/**
 * 保存招聘渠道
 * @param {*} data
 */
export function hrmConfigSaveRecruitChannelAPI(data) {
  return request({
    url: 'hrmConfig/saveRecruitChannel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除招聘渠道
 * @param {*} data
 */
export function hrmConfigDeleteRecruitChannelAPI(data) {
  return request({
    url: 'hrmConfig/deleteRecruitChannel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询后台配置自定义字段列表
 * @param {*} data
 */
export function hrmConfigQueryFieldsAPI(data) {
  return request({
    url: 'hrmConfig/queryFields',
    method: 'post',
    data: data
  })
}

/**
 * 查询后台配置自定义字段列表
 * @param {*} data
 */
export function hrmConfigQueryFieldByLabelAPI(id) {
  return request({
    url: `hrmConfig/queryFieldByLabel/${id}`,
    method: 'post'
  })
}

/**
 * 保存后台自定义字段
 * @param {*} data
 */
export function hrmConfigSaveFieldAPI(data) {
  return request({
    url: 'hrmConfig/saveField',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询淘汰原因
 * @param {*} data
 */
export function hrmConfigQueryRecruitEliminateAPI(data) {
  return request({
    url: 'hrmConfig/queryRecruitEliminateList',
    method: 'post',
    data: data
  })
}

/**
 *
保存淘汰原因
 * @param {*} data
 */
export function hrmConfigSaveRecruitEliminateAPI(data) {
  return request({
    url: 'hrmConfig/saveRecruitEliminate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询考核模板列表
 * @param {*} data
 */
export function hrmConfigQueryAchievementListAPI(data) {
  return request({
    url: 'hrmConfig/queryAchievementTableList',
    method: 'post',
    data: data
  })
}

/**
 * 根据类型查询考核模板
 * @param {*} data
 */
export function hrmConfigQueryAchievementAPI(id) {
  return request({
    url: `hrmConfig/queryAchievementTableById/${id}`,
    method: 'post'
  })
}

/**
 * 添加或修改考核模板考核模板
 * @param {*} data
 */
export function hrmConfigSetAchievementAPI(data) {
  return request({
    url: 'hrmConfig/setAchievementTable',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询参保方案列表
 * @param {*} data
 */
export function hrmConfigInsuranceSchemListAPI(data) {
  return request({
    url: 'hrmConfig/queryInsuranceSchemePageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询参保方案详情
 * @param {*} data
 */
export function hrmConfigInsuranceSchemeDetailAPI(id) {
  return request({
    url: `hrmConfig/queryInsuranceSchemeById/${id}`,
    method: 'post'
  })
}

/**
 * 添加社保方案
 * @param {*} data
 */
export function hrmConfigAddInsuranceSchemAPI(data) {
  return request({
    url: 'hrmConfig/addInsuranceScheme',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除社保方案
 * @param {*} data
 */
export function hrmConfigDeleteInsuranceSchemeAPI(id) {
  return request({
    url: `hrmConfig/deleteInsuranceScheme/${id}`,
    method: 'post'
  })
}

/**
 * 添加薪资组
 * @param {*} data
 */
export function hrmSalaryGroupAddAPI(data) {
  return request({
    url: 'hrmSalaryGroup/addSalaryGroup',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询薪资组列表
 * @param {*} data
 */
export function hrmSalaryGroupListAPI(data) {
  return request({
    url: 'hrmSalaryGroup/querySalaryGroupPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改薪资组
 * @param {*} data
 */
export function hrmSalaryGroupUpdateAPI(data) {
  return request({
    url: 'hrmSalaryGroup/setSalaryGroup',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除薪资组
 * @param {*} data
 */
export function hrmSalaryGroupDeleteAPI(id) {
  return request({
    url: `hrmSalaryGroup/delete/${id}`,
    method: 'post'
  })
}

/**
 * 查询计税规则列表
 * @param {*} data
 */
export function hrmSalaryTaxRuleListAPI(data) {
  return request({
    url: 'hrmSalaryTaxRule/queryTaxRuleList',
    method: 'post',
    data: data
  })
}

/**
 * 修改计规则
 * @param {*} data
 */
export function hrmSalaryTaxRuleUpdateAPI(data) {
  return request({
    url: 'hrmSalaryTaxRule/setTaxRule',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询薪资项详情
 * @param {*} data
 */
export function hrmSalaryOptionDetailAPI(data) {
  return request({
    url: 'hrmSalaryOption/querySalaryOptionDetail',
    method: 'post',
    data: data
  })
}

/**
 * 修改薪资项
 * @param {*} data
 */
export function hrmSalaryOptionUpdateAPI(data) {
  return request({
    url: 'hrmSalaryOption/setSalaryOption',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询员工档案设置字段列表
 * @param {*} data
 */
export function hrmEmployeeArchivesQueryFieldAPI() {
  return request({
    url: 'hrmEmployeeArchives/queryEmployeeArchivesField',
    method: 'post'
  })
}

/**
 * 发送填写档案信息
 * @param {*} data
 */
export function hrmEmployeeArchivesSendAPI(data) {
  return request({
    url: 'hrmEmployeeArchives/sendWriteArchives',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改员工档案字段
 * @param {*} data
 */
export function hrmEmployeeArchivesSetFieldAPI(data) {
  return request({
    url: 'hrmEmployeeArchives/setEmployeeArchivesField',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询管理可设置员工字段列表
 * @param {*} data
 */
export function hrmEmployeeFieldManageQueryAPI(data) {
  return request({
    url: 'hrmEmployeeFieldManage/queryEmployeeManageField',
    method: 'post',
    data: data
  })
}

/**
 * 修改管理可以设置员工字段
 * @param {*} data
 */
export function hrmEmployeeFieldManageSetAPI(data) {
  return request({
    url: 'hrmEmployeeFieldManage/setEmployeeManageField',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询考勤班次列表
 */
export function hrmQueryAttendanceShiftPageListAPI(data) {
  return request({
    url: 'hrmAttendanceShift/queryAttendanceShiftPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 添加或修改考勤班次
 */
export function hrmAddOrSetAttendanceShiftAPI(data) {
  return request({
    url: `hrmAttendanceShift/${data.shiftId ? 'setAttendanceShift' : 'addAttendanceShift'}`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除考勤班次
 */
export function hrmAttendanceShiftDeleteAPI(data) {
  return request({
    url: `hrmAttendanceShift/delete/${data.attendanceShiftId}`,
    method: 'post',
    data: data
  })
}

/**
 * 校验考勤班次名称
 */
export function hrmVerifyAttendanceShiftNameAPI(data) {
  return request({
    url: 'hrmAttendanceShift/verifyAttendanceShiftName',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 添加考勤规则或设置考勤规则
 */
export function hrmAddOrSetAttendanceRuleAPI(data) {
  return request({
    url: `hrmAttendanceRule/${data.attendanceRuleId ? 'setAttendanceRule' : 'addAttendanceRule'}`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询考勤规则列表
 */
export function hrmQueryAttendanceRulePageListAPI(data) {
  return request({
    url: `hrmAttendanceRule/queryAttendanceRulePageList`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除考勤规则
 */
export function hrmAttendanceRuleDeleteAPI(data) {
  return request({
    url: `hrmAttendanceRule/delete/${data.attendanceRuleId}`,
    method: 'post',
    data: data
  })
}

/**
 * 校验考勤规则名称
 */
export function hrmVerifyAttendanceRuleNameAPI(data) {
  return request({
    url: `hrmAttendanceRule/verifyAttendanceRuleName`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
// 考勤组 ----------
/**
 * 查询考勤组列表
 */
export function hrmQueryAttendanceGroupPageListAPI(data) {
  return request({
    url: `hrmAttendanceGroup/queryAttendanceGroupPageList`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除考勤组
 */
export function hrmAttendanceGroupDeleteAPI(data) {
  return request({
    url: `hrmAttendanceGroup/delete/${data.attendanceGroupId}`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 校验考勤组名称
 */
export function hrmVerifyAttendanceGroupNameAPI(data) {
  return request({
    url: `hrmAttendanceGroup/verifyAttendanceGroupName`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 添加打卡地址
 */
export function hrmAddAttendancePointAPI(data) {
  return request({
    url: `hrmAttendancePoint/addAttendancePoint`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除打卡地址
 */
export function hrmDeleteAttendancePointAPI(data) {
  return request({
    url: `hrmAttendancePoint/delete/${data.attendancePointId}`,
    method: 'post',
    data: data
  })
}

/**
 * 查询打卡地址列表
 */
export function hrmQueryAttendancePointPageListAPI(data) {
  return request({
    url: `hrmAttendancePoint/queryAttendancePointPageList`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 添加打卡Wifi
 */
export function hrmAddAttendanceWifiAPI(data) {
  return request({
    url: `hrmAttendanceWifi/addAttendanceWifi`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除打卡Wifi
 */
export function hrmDeleteAttendanceWifiAPI(data) {
  return request({
    url: `hrmAttendanceWifi/delete/${data.attendanceWifiId}`,
    method: 'post',
    data: data
  })
}

/**
 * 查询打卡Wifi列表
 */
export function hrmQueryAttendanceWifiPageListAPI(data) {
  return request({
    url: `hrmAttendanceWifi/queryAttendanceWifiPageList`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 添加考勤组
 */
export function hrmAddAttendanceGroupAPI(data) {
  return request({
    url: `hrmAttendanceGroup/addAttendanceGroup`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 编辑考勤组
 */
export function hrmEditAttendanceGroupAPI(data) {
  return request({
    url: `hrmAttendanceGroup/setAttendanceGroup`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 获取考勤审批设置
 */
export function hrmAttendanceExamineQueryPageListAPI(data) {
  return request({
    url: 'hrmAttendanceExamine/queryHrmAttendanceExamine',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 保存审批设置
 */
export function hrmAttendanceExamineAddAPI(data) {
  return request({
    url: 'hrmAttendanceExamine/add',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 保存审批设置
 */
export function hrmAttendanceExamineUpdateAPI(data) {
  return request({
    url: 'hrmAttendanceExamine/update',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
