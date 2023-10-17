/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-05-14 13:58:52
 * @LastEditTime: 2023-04-07 17:25:32
 * @LastEditors: yang
 */
import request from '@/utils/request'

/**
 * 查询所用员工(表单选择使用)
 * status 1 在职 2 离职 3 删除
 * @param {*} data
 */
export function hrmEmployeeAllListAPI(data) {
  return request({
    url: 'hrmEmployee/queryAllEmployeeList',
    method: 'post',
    data: data
  })
}

/**
 * 分页查询员工列表
 * @param {*} data
 */
export function hrmEmployeeQueryPageListAPI(data) {
  return request({
    url: 'hrmEmployee/queryPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询在职员工(表单选择使用)
 * @param {*} data
 */
export function hrmEmployeeQueryInAPI(data) {
  return request({
    url: 'hrmEmployee/queryInEmployeeList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询部门用户列表
 */
export function hrmEmployeeQueryByUserDeptAPI(data) {
  return request({
    url: 'hrmEmployee/queryDeptEmpListByUser',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询员工表字段
 * @param {*} data
 */
export function hrmEmployeeFieldListHeadsAPI(data) {
  return request({
    url: 'hrmEmployeeField/queryListHeads',
    method: 'post',
    data: data
  })
}

/**
 * 批量修改字段表头配置
 * @param {*} data
 */
export function hrmEmployeeFieldUpdateConfigAPI(data) {
  return request({
    url: 'hrmEmployeeField/updateFieldConfig',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改字段宽度
 * @param {*} data
 */
export function hrmEmployeeFieldUpdateWidthAPI(data) {
  return request({
    url: 'hrmEmployeeField/updateFieldWidth',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 验证唯一
 * @param {*} data
 */
export function hrmEmployeeFieldVerifyAPI(data) {
  return request({
    url: 'hrmEmployeeField/verify',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 新建员工
 * @param {*} data
 */
export function hrmEmployeeAddEmployeeAPI(data) {
  return request({
    url: 'hrmEmployee/addEmployee',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 获取各个状态数量
 * @param {*} data
 */
export function hrmEmployeeQueryStatusNumAPI(data) {
  return request({
    url: 'hrmEmployee/queryEmployeeStatusNum',
    method: 'post',
    data: data
  })
}

/**
 * 查询员工详情
 * @param {*} data
 */
export function hrmEmployeeQueryByIdAPI(id) {
  return request({
    url: `hrmEmployee/queryById/${id}`,
    method: 'post'
  })
}

/**
 * 删除员工
 * @param {*} data
 */
export function hrmEmployeeDeleteByIdsAPI(data) {
  return request({
    url: 'hrmEmployee/deleteByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 个人基本信息
 * @param {*} data
 */
export function hrmEmployeePersonalInfoAPI(id) {
  return request({
    url: `hrmEmployee/personalInformation/${id}`,
    method: 'post'
  })
}

/** *** 教育 */
/**
 * 添加教育经历
 * @param {*} data
 */
export function hrmEmployeeAddEduAPI(data) {
  return request({
    url: 'hrmEmployee/addExperience',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改教育经历
 * @param {*} data
 */
export function hrmEmployeeSetEduAPI(data) {
  return request({
    url: 'hrmEmployee/setEduExperience',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除教育经历
 * @param {*} data
 */
export function hrmEmployeeDeleteEduAPI(id) {
  return request({
    url: `hrmEmployee/deleteEduExperience/${id}`,
    method: 'post'
  })
}

/** *** 工作 */
/**
 * 添加工作经历
 * @param {*} data
 */
export function hrmEmployeeAddWorkAPI(data) {
  return request({
    url: 'hrmEmployee/addWorkExperience',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改工作经历
 * @param {*} data
 */
export function hrmEmployeeSetWorkeAPI(data) {
  return request({
    url: 'hrmEmployee/setWorkExperience',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除工作经历
 * @param {*} data
 */
export function hrmEmployeeDeleteWorkAPI(id) {
  return request({
    url: `hrmEmployee/deleteWorkExperience/${id}`,
    method: 'post'
  })
}

/** *** 证书 */
/**
 * 添加证书
 * @param {*} data
 */
export function hrmEmployeeAddCertificateAPI(data) {
  return request({
    url: 'hrmEmployee/addCertificate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改证书
 * @param {*} data
 */
export function hrmEmployeeSetCertificateeAPI(data) {
  return request({
    url: 'hrmEmployee/setCertificate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除证书
 * @param {*} data
 */
export function hrmEmployeeDeleteCertificateAPI(id) {
  return request({
    url: `hrmEmployee/deleteCertificate/${id}`,
    method: 'post'
  })
}

/** *** 培训 */
/**
 * 添加培训经历
 * @param {*} data
 */
export function hrmEmployeeAddTrainingAPI(data) {
  return request({
    url: 'hrmEmployee/addTrainingExperience',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改培训经历
 * @param {*} data
 */
export function hrmEmployeeSetTrainingAPI(data) {
  return request({
    url: 'hrmEmployee/setTrainingExperience',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除培训经历
 * @param {*} data
 */
export function hrmEmployeeDeleteTrainingAPI(id) {
  return request({
    url: `hrmEmployee/deleteTrainingExperience/${id}`,
    method: 'post'
  })
}

/** *** 联系人 */

/**
 * 查询联系人添加字段
 * @param {*} data
 */
export function hrmEmployeeQueryContactsAddFieldAPI() {
  return request({
    url: 'hrmEmployee/queryContactsAddField',
    method: 'post'
  })
}

/**
 * 添加联系人经历
 * @param {*} data
 */
export function hrmEmployeeAddContactsAPI(data) {
  return request({
    url: 'hrmEmployee/addContacts',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改联系人经历
 * @param {*} data
 */
export function hrmEmployeeSetContactsAPI(data) {
  return request({
    url: 'hrmEmployee/setContacts',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除联系人经历
 * @param {*} data
 */
export function hrmEmployeeDeleteContactsAPI(id) {
  return request({
    url: `hrmEmployee/deleteContacts/${id}`,
    method: 'post'
  })
}

/**
 * 修改员工状态(转正/调整部门/岗位/晋升/降级) *失效* 拆成以下三个接口
 * @param {*} data
 */
export function hrmEmployeeSetChangeAPI(data) {
  return request({
    url: 'hrmEmployee/change',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 转正
 * @param {*} data
 */
export function hrmEmployeeSetBecomeAPI(data) {
  return request({
    url: 'hrmEmployee/become',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 调整部门/岗位
 * @param {*} data
 */
export function hrmEmployeeSetChangePostAPI(data) {
  return request({
    url: 'hrmEmployee/changePost',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 晋升/降级
 * @param {*} data
 */
export function hrmEmployeeSetPromotionAPI(data) {
  return request({
    url: 'hrmEmployee/promotion',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 再入职
 * @param {*} data
 */
export function hrmEmployeeAgainOnboardingAPI(data) {
  return request({
    url: 'hrmEmployee/againOnboarding',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 确认入职
 * @param {*} data
 */
export function hrmEmployeeConfirmEntryAPI(data) {
  return request({
    url: 'hrmEmployee/confirmEntry',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改员工基本信息
 * @param {*} data
 */
export function hrmEmployeeUpdateInformationAPI(data) {
  return request({
    url: 'hrmEmployee/updateInformation',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改通讯信息
 * @param {*} data
 */
export function hrmEmployeeUpdateCommunicationAPI(data) {
  return request({
    url: 'hrmEmployee/updateCommunication',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/** *** 附件 */
/**
 * 添加附件
 * @param {*} data
 */
export function hrmEmployeeFileAddAPI(data) {
  return request({
    url: 'hrmEmployeeFile/addFile',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除附件
 * @param {*} data
 */
export function hrmEmployeeFileDeleteAPI(id) {
  return request({
    url: `hrmEmployeeFile/deleteFile/${id}`,
    method: 'post'
  })
}

/**
 * 查询员工总体附件
 * @param {*} data
 */
export function hrmEmployeeFileQueryNumAPI(id) {
  return request({
    url: `hrmEmployeeFile/queryFileNum/${id}`,
    method: 'post'
  })
}

/**
 * 根据附件类型查询附件详情
 * @param {*} data
 */
export function hrmEmployeeFileQueryBySubTypeAPI(data) {
  return request({
    url: 'hrmEmployeeFile/queryFileBySubType',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询部门员工列表
 * @param {*} data
 */
export function hrmEmployeeQueryByDeptAPI(deptId) {
  return request({
    url: `hrmEmployee/queryDeptEmployeeList/${deptId}`,
    method: 'post'
  })
}

/**
 * 从系统用户添加员工
 * @param {*} data
 */
export function hrmEmployeeAdminAddAPI(data) {
  return request({
    url: 'hrmEmployee/adminAddEmployee',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 导出员工
 * @param {*} data
 */
export function hrmEmployeeExportAPI(data) {
  return request({
    url: 'hrmEmployee/export',
    method: 'post',
    responseType: 'blob',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 获取导入模板
 * @param {*} data
 */
export function hrmEmployeeDownloadExportAPI() {
  return request({
    url: 'hrmEmployee/downloadExcel',
    method: 'post',
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 员工导入
 * @param {*} data
 */
export function hrmEmployeeUploadExportAPI(data) {
  const param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'hrmEmployee/uploadExcel',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 当前员工信息
 * @param {*} data
 */
export function hrmEmployeeQueryLoginEmployeeAPI() {
  return request({
    url: 'hrmEmployee/queryLoginEmployee',
    method: 'post'
  })
}

/**
 * 新建员工字段获取
 * @param {*} data
 */
export function hrmEmployeeAddFieldsAPI(data) {
  return request({
    url: 'hrmEmployee/field',
    method: 'post',
    data
  })
}

/**
 * 新建自定义字段员工
 * @param {*} data
 */
export function hrmEmployeeFieldsAddAPI(data) {
  return request({
    url: 'hrmEmployee/addEmployeeField',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
