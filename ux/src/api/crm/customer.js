import request from '@/utils/request'

/**
 * 新建编辑
 * @param {*} data
 */
export function crmCustomerSaveAPI(data) {
  const url = data.entity && data.entity.customerId ? 'update' : 'add'
  return request({
    url: 'crmCustomer/' + url,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 列表
 * @param {*} data
 */
export function crmCustomerIndexAPI(data) {
  return request({
    url: 'crmCustomer/queryPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除
 * @param {*} data
 */
export function crmCustomerDeleteAPI(data) {
  return request({
    url: 'crmCustomer/deleteByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 公海列表
 * @param {*} data
 */
export function crmCustomerPoolListAPI(data) {
  return request({
    url: 'crmCustomerPool/queryPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除
 * @param {*} data
 */
export function crmCustomerPoolDeleteAPI(data) {
  return request({
    url: 'crmCustomerPool/deleteByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 设置公海
 * @param {*} data
 */
export function crmCustomerPoolSetAPI(data) {
  return request({
    url: 'crmCustomerPool/setCustomerPool',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 公海设置列表
 * @param {*} data
 */
export function crmCustomerPoolSetListAPI(data) {
  return request({
    url: 'crmCustomerPool/queryPoolSettingList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 公海列表排序编辑
 * @param {*} data
 */
export function crmCustomerPoolFieldConfigAPI(data) {
  return request({
    url: 'crmCustomerPool/poolFieldConfig',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 公海设置删除
 */
export function crmCustomerPoolSetDeleteAPI(data) {
  return request({
    url: 'crmCustomerPool/deleteCustomerPool',
    method: 'post',
    data: data
  })
}

/**
 * 公海设置详情
 * @param {*} data
 */
export function crmCustomerPoolSetDetailAPI(data) {
  return request({
    url: 'crmCustomerPool/queryPoolById',
    method: 'post',
    data: data
  })
}

/**
 * 公海启停
 * @param {*} data
 */
export function crmCustomerPoolSetChangeStatusAPI(data) {
  return request({
    url: 'crmCustomerPool/changeStatus',
    method: 'post',
    data: data
  })
}

/**
 * 公海客户转移
 * prePoolId 转出公海id
 * postPoolId 转入公海id
 * @param {*} data
 */
export function crmCustomerPoolSetTransferAPI(data) {
  return request({
    url: 'crmCustomerPool/transfer',
    method: 'post',
    data: data
  })
}

/**
 * 转移数据源
 * @param {*} data
 */
export function crmCustomerPoolSetNameListAPI(data) {
  return request({
    url: 'crmCustomerPool/queryPoolNameList',
    method: 'post',
    data: data
  })
}

/**
 * 客户管理用到的公海列表
 * @param {*} data
 */
export function crmCustomerPoolNameListAPI(data) {
  return request({
    url: 'crmCustomerPool/queryPoolNameListByAuth',
    method: 'post',
    data: data
  })
}

/**
 * 获取客户级别选项接口
 * @param {*} data
 */
export function crmCustomerPoolQueryLevelAPI(data) {
  return request({
    url: 'crmCustomerPool/queryCustomerLevel',
    method: 'post',
    data: data
  })
}

/**
 * 详情
 */
export function crmCustomerReadAPI(data) {
  return request({
    url: `crmCustomer/queryById/${data.customerId}`,
    method: 'post',
    data: data
  })
}

// 操作
/**
 * 客户锁定，解锁
 * @param {*} data
 */
export function crmCustomerLockAPI(data) {
  return request({
    url: 'crmCustomer/lock',
    method: 'post',
    data: data
  })
}

/**
 * 客户放入公海
 * @param {*} data
 * customer_id 	客户数组
 */
export function crmCustomerPutInPoolAPI(data) {
  return request({
    url: 'crmCustomer/updateCustomerByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 客户转移
 * @param {*} data
 */
export function crmCustomerTransferAPI(data) {
  return request({
    url: 'crmCustomer/changeOwnerUser',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 客户导出
 * @param {*} data
 * customer_id 客户ID
 */
export function crmCustomerExcelExportAPI(data) {
  return request({
    url: 'crmCustomer/batchExportExcel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    responseType: 'blob'
  })
}

/**
 * 全部导出
 * @param {*} data
 */
export function crmCustomerExcelAllExportAPI(data) {
  return request({
    url: 'crmCustomer/allExportExcel',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 客户导入
 * @param {*} data
 * customer_id 客户ID
 */
export function crmCustomerExcelImportAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'crmCustomer/uploadExcel',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 公海客户导入
 * @param {*} data
 * customer_id 客户ID
 */
export function crmCustomerPoolExcelImportAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'crmCustomerPool/uploadExcel',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 客户导入模板下载
 * @param {*} data
 *
 */
export const crmCustomerExcelDownloadURL = process.env.VUE_APP_BASE_API + 'crmCustomer/downloadExcel'
export function crmCustomerDownloadExcelAPI(data) {
  return request({
    url: 'crmCustomer/downloadExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

/**
 * 公海模板
 * @param {*} data
 * @returns
 */
export function crmCustomerPoolDownloadExcelAPI(data) {
  return request({
    url: 'crmCustomerPool/downloadExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

/**
 * 公海导出
 * @param {*} data
 */
export function crmCustomerPoolExcelExportAPI(data) {
  return request({
    url: 'crmCustomerPool/batchExportExcel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    responseType: 'blob',
    timeout: 60000
  })
}

/**
 * 公海全部导出
 * @param {*} data
 */
export function crmCustomerPoolExcelAllExport(data) {
  return request({
    url: 'crmCustomerPool/allExportExcel',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 公海字段查询
 * @param {*} data
 */
export function crmCustomerPoolQueryPoolFieldAPI(data) {
  return request({
    url: 'crmCustomerPool/queryPoolField',
    method: 'post',
    data: data
  })
}

/**
 * 获取公海池 操作权限
 * @param {*} data
 */
export function crmCustomerPoolQueryAuthAPI(data) {
  return request({
    url: 'crmCustomerPool/queryAuthByPoolId',
    method: 'post',
    data: data
  })
}

/**
 * 保存公海外漏高级筛选条件
 * @param {*} data
 */
export function crmCustomerPoolSaveSearchExpertAPI(data) {
  return request({
    url: 'crmCustomerPool/saveSearchExpert',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 设置锁定字段
 * @param {*} data
 */
export function crmCustomerPoolSetIsLockAPI(data) {
  return request({
    url: 'crmCustomerPool/setIsLock',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 客户分配
 * @param {*} data
 */
export function crmCustomerDistributeAPI(data) {
  return request({
    url: 'crmCustomer/distributeByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 客户领取
 * @param {*} data
 */
export function crmCustomerReceiveAPI(data) {
  return request({
    url: 'crmCustomer/receiveByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

// 团队操作
/**
 * 团队成员创建
 * @param {*} data
 * types crm_leads
 * typesId 分类ID
 */
export function crmCustomerSettingTeamSaveAPI(data) {
  return request({
    url: 'crmCustomer/addMembers',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function crmCustomerSettingTeamDeleteAPI(data) {
  return request({
    url: 'crmCustomer/deleteMembers',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function crmCustomerTeamMembersAPI(data) {
  return request({
    url: `crmCustomer/getMembers/${data.id}`,
    method: 'post'
  })
}

export function crmCustomerUpdateMembersAPI(data) {
  return request({
    url: 'crmCustomer/updateMembers',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 退出团队
 * @param {*} data
 */
export function crmCustomerExitTeamAPI(data) {
  return request({
    url: `crmCustomer/exitTeam/${data.id}`,
    method: 'post'
  })
}

/**
 * 客户标记跟进
 * @param {*} data
 * id 客户IDs
 */
export function crmCustomerSetFollowAPI(data) {
  return request({
    url: 'crmBackLog/setCustomerFollowup',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 客户成交状态修改
 * @param {*} data
 * id 客户IDs
 */
export function crmCustomerDealStatusAPI(data) {
  return request({
    url: 'crmCustomer/setDealStatus',
    method: 'post',
    data: data
  })
}

/**
 * 设置首要联系人
 * @param {*} data
 *
 */
export function crmCustomerSetContactsAPI(data) {
  return request({
    url: 'crmCustomer/setContacts',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 数据查重
 * @param {*} data
 *
 */
export function crmCustomerDataCheckAPI(data) {
  return request({
    url: 'crmCustomer/dataCheck',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 附件列表
 * @param {*} data
 *
 */
export function crmCustomerFileListAPI(data) {
  return request({
    url: 'crmCustomer/queryFileList',
    method: 'post',
    data: data
  })
}

/**
 * tab数量
 * @param {*} data
 *
 */
export function crmCustomerNumAPI(data) {
  return request({
    url: 'crmCustomer/num',
    method: 'post',
    data: data
  })
}

/**
 * 呼叫中心客服的权限
 * @param {*} data
 * id 客户IDs
 */
export function crmCallCheckAuthAPI(data) {
  return request({
    url: 'crmCall/checkAuth',
    method: 'post',
    data: data
  })
}

/**
 * 呼叫中心客服的权限
 * @param {*} data
 * id 客户IDs
 */
export function crmCallSaveAPI(data) {
  return request({
    url: 'crmCall/save',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 发票信息
 * @param {*} data
 */
export function crmCustomerInvoiceAPI(data) {
  return request({
    url: 'crmCustomer/queryInvoice',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除发票信息
 * @param {*} data
 */
export function crmCustomerInvoiceInfoAPI(data) {
  return request({
    url: 'crmCustomer/queryInvoiceInfo',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 通话记录
 * @param {*} data
 */
export function crmCustomerCallRecordAPI(data) {
  return request({
    url: 'crmCustomer/queryCallRecord',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询相关联系人视图结构
 */
export function crmCustomerQueryContactsByIdAPI(data) {
  return request({
    url: `crmCustomer/queryContactsById/${data.customerId}`,
    method: 'post',
    data: data
  })
}

/**
 * 查询相关客户地址
 */
export function crmCustomerQueryAddressByIdAPI(data) {
  return request({
    url: `crmCustomer/queryAddressById/${data.customerId}`,
    method: 'post',
    data: data
  })
}

/**
 * 新建相关客户地址
 */
export function crmCustomerSaveAddressAPI(data) {
  return request({
    url: 'crmCustomer/saveAddress',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
/**
 * 编辑相关客户地址
 */
export function crmCustomerUpdateAddressAPI(data) {
  return request({
    url: 'crmCustomer/updateAddress',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 设为主要地址
 */
export function crmCustomerSetIsMainAPI(data) {
  return request({
    url: 'crmCustomer/setIsMain',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 作废客户地址
 */
export function crmCustomerCancellationAddressAPI(data) {
  return request({
    url: 'crmCustomer/cancellationAddress',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询相关费用
 */
export function crmCustomerQueryApplyExamineAPI(data) {
  return request({
    url: 'crmCustomer/queryApplyExamine',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询客户上级以及下级
 */
export function crmCustomerQuerySuperiorByIdAPI(data) {
  return request({
    url: `crmCustomer/querySuperiorById/${data.customerId}`,
    method: 'post',
    data: data
  })
}
/**
 * 关联上级客户
 */
export function crmCustomerSaveSuperiorAPI(data) {
  return request({
    url: 'crmCustomer/saveSuperior',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
/**
 * 关联下级客户
 */
export function crmCustomerSaveSubordinateAPI(data) {
  return request({
    url: 'crmCustomer/saveSubordinate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 取消关联上级或下级客户
 */
export function crmCustomerDeleteSuperiorAPI(data) {
  return request({
    url: 'crmCustomer/deleteSuperior',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询客户上级以及下级的id
 */
export function crmCustomerQuerySuperiorIdsAPI(customerId) {
  return request({
    url: `crmCustomer/querySuperiorIds/${customerId}`,
    method: 'post'
  })
}

/**
 * 新增团队成员交叉产品信息
 */
export function crmCustomerAddOrUpdateTeamProductAPI(data) {
  return request({
    url: `crmCustomer/addOrUpdateTeamProduct/${data.id}`,
    method: 'post',
    data: data.entity,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除团队成员交叉产品信息
 */
export function crmCustomerDeleteTeamProductAPI(customerId) {
  return request({
    url: `crmCustomer/deleteTeamProductUser/${customerId}`,
    method: 'post'
  })
}

/**
 * 查询团队成员交叉产品信息
 */
export function crmCustomerQueryTeamProductAPI(customerId) {
  return request({
    url: `crmCustomer/queryTeamProductList/${customerId}`,
    method: 'post'
  })
}

/**
 * 关系图谱列表
 * @param {*} data
 */
export function crmCustomerQueryCustomerRelationstListAPI(data) {
  return request({
    url: 'crmCustomer/queryCustomerRelationstList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

/**
 * 关联企业
 * @param {*} data
 */
export function crmCustomerAffiliateCustomerAPI(data) {
  return request({
    url: `crmCustomer/affiliateCustomer/${data.id}`,
    method: 'post',
    data: data.list,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

/**
 * 客户图谱列表
 * @param {*} data
 */
export function crmCustomerQueryCustomerAtlastListAPI(data) {
  return request({
    url: 'crmCustomer/queryCustomerAtlastList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

/**
 * 公海 列表宽度设置
 * @param {*} data
 */
export function crmPoolFieldColumnWidthAPI(data) {
  return request({
    url: 'crmField/setPoolFieldStyle',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
