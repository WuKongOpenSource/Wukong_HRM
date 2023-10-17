import request from '@/utils/request'

/**
 * 新建 编辑
 * @param {*} data
 */
export function crmLeadsSaveAPI(data) {
  const url = data.entity && data.entity.leadsId ? 'update' : 'add'
  return request({
    url: 'crmLeads/' + url,
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
export function crmLeadsIndexAPI(data) {
  return request({
    url: 'crmLeads/queryPageList',
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
export function crmLeadsPoolIndexAPI(data) {
  return request({
    url: 'crmLeadsPool/queryPageList',
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
export function crmLeadsDeleteAPI(data) {
  return request({
    url: 'crmLeads/deleteByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 详情
 * @param {*} id
 */
export function crmLeadsReadAPI(data) {
  return request({
    url: `crmLeads/queryById/${data.leadsId}`,
    method: 'post',
    data: data
  })
}

/**
 * 线索转移
 * @param {*} data
 */
export function crmLeadsTransferAPI(data) {
  return request({
    url: 'crmLeads/changeOwnerUser',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 线索转换为客户
 * @param {*} data
 */
export function crmLeadsTransformAPI(data) {
  return request({
    url: 'crmLeads/transfer',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 线索导出
 * @param {*} data
 *
 */
export function crmLeadsExcelExportAPI(data) {
  return request({
    url: 'crmLeads/batchExportExcel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    responseType: 'blob'
  })
}

/**
 * 线索全部导出
 * @param {*} data
 */
export function crmLeadsExcelAllExportAPI(data) {
  return request({
    url: 'crmLeads/allExportExcel',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 线索导入
 * @param {*} data
 *
 */
export function crmLeadsExcelImportAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'crmLeads/uploadExcel',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 线索导入模板下载
 * @param {*} data
 *
 */
export const crmLeadsExcelDownloadURL = process.env.VUE_APP_BASE_API + 'crmLeads/downloadExcel'
export function crmLeadsDownloadExcelAPI(data) {
  return request({
    url: 'crmLeads/downloadExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

/**
 * 线索池导入模板下载
 * @param {*} data
 *
 */
export function crmLeadsPoolDownloadExcelAPI(data) {
  return request({
    url: 'crmLeadsPool/downloadExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

/**
 * 线索标记跟进
 * @param {*} data
 * id 客户IDs
 */
export function crmLeadsSetFollowAPI(data) {
  return request({
    url: 'crmBackLog/setLeadsFollowup',
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
export function crmLeadsFileListAPI(data) {
  return request({
    url: 'crmLeads/queryFileList',
    method: 'post',
    data: data
  })
}

/**
 * 保存线索池外漏高级筛选条件
 * @param {*} data
 */
export function crmLeadsPoolSaveSearchExpertAPI(data) {
  return request({
    url: 'crmLeadsPool/saveSearchExpert',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * tab数量
 * @param {*} data
 *
 */
export function crmLeadsNumAPI(data) {
  return request({
    url: 'crmLeads/num',
    method: 'post',
    data: data
  })
}

/**
 * 标星
 * @param {*} data
 *
 */
export function crmLeadsStarAPI(data) {
  return request({
    url: `crmLeads/star/${data.id}`,
    method: 'post',
    data: data
  })
}

/**
 * 公海列表字段排序数据
 * @param {*} data
 */
export function crmLeadsPoolFieldConfigIndexAPI(data) {
  return request({
    url: 'crmLeadsPool/queryPoolFieldConfig',
    method: 'post',
    data: data
  })
}

/**
 * 公海列表排序编辑
 * @param {*} data
 */
export function crmLeadsPoolFieldConfigAPI(data) {
  return request({
    url: 'crmLeadsPool/poolFieldConfig',
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
export function crmLeadsPoolSetListAPI(data) {
  return request({
    url: 'crmLeadsPool/queryPoolSettingList',
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
export function crmLeadsPoolSetDeleteAPI(data) {
  return request({
    url: 'crmLeadsPool/deleteLeadsPool',
    method: 'post',
    data: data
  })
}

/**
 * 公海启停
 * @param {*} data
 */
export function crmLeadsPoolSetChangeStatusAPI(data) {
  return request({
    url: 'crmLeadsPool/changeStatus',
    method: 'post',
    data: data
  })
}

/**
 * 公海字段查询
 * @param {*} data
 */
export function crmLeadsPoolQueryPoolFieldAPI(data) {
  return request({
    url: 'crmLeadsPool/queryPoolField',
    method: 'post',
    data: data
  })
}

/**
 * 设置公海
 * @param {*} data
 */
export function crmLeadsPoolSetAPI(data) {
  return request({
    url: 'crmLeadsPool/setLeadsPool',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 公海设置详情
 * @param {*} data
 */
export function crmLeadsPoolSetDetailAPI(data) {
  return request({
    url: 'crmLeadsPool/queryPoolById',
    method: 'post',
    data: data
  })
}

/**
 * 获取客户级别选项接口
 * @param {*} data
 */
export function crmLeadsPoolQueryLevelAPI(data) {
  return request({
    url: 'crmLeadsPool/queryLeadsLevel',
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
export function crmLeadsPoolSetTransferAPI(data) {
  return request({
    url: 'crmLeadsPool/transfer',
    method: 'post',
    data: data
  })
}

/**
 * 转移数据源
 * @param {*} data
 */
export function crmLeadsPoolSetNameListAPI(data) {
  return request({
    url: 'crmLeadsPool/queryPoolNameList',
    method: 'post',
    data: data
  })
}

/**
 * 线索领取
 * @param {*} data
 */
export function crmLeadsReceiveAPI(data) {
  return request({
    url: 'crmLeads/receiveByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 线索管理用到的公海列表
 * @param {*} data
 */
export function crmLeadsPoolNameListAPI(data) {
  return request({
    url: 'crmLeadsPool/queryPoolNameListByAuth',
    method: 'post',
    data: data
  })
}

/**
 * 获取公海池 操作权限
 * @param {*} data
 */
export function crmLeadsPoolQueryAuthAPI(data) {
  return request({
    url: 'crmLeadsPool/queryAuthByPoolId',
    method: 'post',
    data: data
  })
}

/**
 * 公海导出
 * @param {*} data
 */
export function crmLeadsPoolExcelExportAPI(data) {
  return request({
    url: 'crmLeadsPool/batchExportExcel',
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
 * 删除
 * @param {*} data
 */
export function crmLeadsPoolDeleteAPI(data) {
  return request({
    url: 'crmLeadsPool/deleteByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 线索放入公海
 * @param {*} data
 *  	客户数组
 */
export function crmLeadsPutInPoolAPI(data) {
  return request({
    url: 'crmLeads/updateLeadsByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 线索分配
 * @param {*} data
 */
export function crmLeadsDistributeAPI(data) {
  return request({
    url: 'crmLeads/distributeByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 公海全部导出
 * @param {*} data
 */
export function crmLeadsPoolExcelAllExport(data) {
  return request({
    url: 'crmLeadsPool/allExportExcel',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 公海线上导入
 * @param {*} data
 * leads 线上ID
 */
export function crmLeadsPoolExcelImportAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'crmLeadsPool/uploadExcel',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 公海 列表宽度设置
 * @param {*} data
 */
export function crmLeadsPoolFieldColumnWidthAPI(data) {
  return request({
    url: 'crmField/setLeadsPoolFieldStyle',
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
export function crmLeadsPoolSetIsLockAPI(data) {
  return request({
    url: 'crmLeadsPool/setIsLock',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
